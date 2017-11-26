package hr.wozai.service.user.server.component;

import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.thirdparty.client.utils.EmailParamCons;
import hr.wozai.service.thirdparty.client.utils.RabbitMQProducer;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.JobTransferResponseDTO;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.StatusUpdate;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.client.enums.MessageTemplate;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.thirdparty.client.utils.SqsProducer;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * @author Zhe Chen
 * @version 1.0
 * @created 16/5/19
 */
@Component("employeeManagementNotifier")
public class EmployeeManagementNotifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingFlowNotifier.class);

  private static final String HTTP_ENDPOINT_PREFIX_OF_AUTH = "u?uuid=";
  private static final String HTTP_ENDPOINT_SURFIX_OF_INIT_PASSWORD = "#init-password";
  private static final String HTTP_ENDPOINT_SURFIX_OF_ONBOARDING_FLOW_REVIEW = "#/team/staffProfile/";
  private static final String HTTP_ENDPOINT_SURFIX_OF_USER_PROFILE = "#/user/";
  private static final String HTTP_ENDPOINT_SURFIX_OF_LOGIN = "u";

 /* private static final String PARAM_DST_EMAIL_ADDRESS = "dstEmailAddress";
  private static final String PARAM_ORG_SHORT_NAME = "orgShortName";
  private static final String PARAM_USER_NAME = "userName";
  private static final String PARAM_NAME = "name";
  private static final String PARAM_USER_EMAIL = "userEmail";
  private static final String PARAM_BEFORE_TEAM_NAME = "beforeTeamName";
  private static final String PARAM_AFTER_TEAM_NAME = "afterTeamName";
  private static final String PARAM_BEFORE_REPORTER_NAME = "beforeReporterName";
  private static final String PARAM_AFTER_REPORTER_NAME = "afterReporterName";
  private static final String PARAM_BEFORE_JOB_TITLE = "beforeJobTitle";
  private static final String PARAM_AFTER_JOB_TITLE = "afterJobTitle";
  private static final String PARAM_BEFORE_JOB_LEVEL = "beforeJobLevel";
  private static final String PARAM_AFTER_JOB_LEVEL = "afterJobLevel";
  private static final String PARAM_TRANSFER_TYPE = "transferType";
  private static final String PARAM_TRANSFER_DATE = "transferDate";
  private static final String PARAM_TRANSFER_DESCRIPTION = "transferDescription";
  private static final String PARAM_TEAM_NAME = "teamName";
  private static final String PARAM_USER_JOB_TITLE = "userJobTitle";
  private static final String PARAM_PASS_PROBATION_TYPE = "passProbationType";
  private static final String PARAM_PASS_PROBATION_DATE = "passProbationDate";
  private static final String PARAM_PASS_PROBATION_DESCRIPTION = "passProbationDescription";
  private static final String PARAM_RESIGN_TYPE = "resignType";
  private static final String PARAM_RESIGN_DATE = "resignDate";
  private static final String PARAM_RESIGN_DESCRIPTION = "resignDescription";*/

  /*  @Autowired
    SqsProducer sqsProducer;*/
  @Autowired
  RabbitMQProducer rabbitMQProducer;

  @Autowired
  @Qualifier("messageCenterFacadeProxy")
  private ThriftClientProxy messageCenterFacadeProxy;

  private MessageCenterFacade messageCenterFacade;

  @Value("${url.host}")
  private String host;

  @PostConstruct
  public void init() throws Exception {
    messageCenterFacade = (MessageCenterFacade) messageCenterFacadeProxy.getObject();
  }

  /*********************** For JobTransfer ***********************/

  /**
   * Steps:
   *  1) send email to the staff
   *  2) send email to toNotifyUsers
   *  3) send message to the staff
   *  4) send message to toNotifyUsers
   *
   * @param jobTransferResponseDTO
   * @param toNotifyCUPs
   */
  @LogAround
  public void sendEmailAndMessageAfterJobTransfer(
      Org org, JobTransferResponseDTO jobTransferResponseDTO,
      CoreUserProfile hrCUP, CoreUserProfile staffCUP, List<CoreUserProfile> toNotifyCUPs) {

    // 1)
    sendEmailToStaffAfterJobTransfer(org.getShortName(), jobTransferResponseDTO, staffCUP);

    // 2)
    long jobTransferId = jobTransferResponseDTO.getJobTransferId();
    sendMessageToStaffAfterJobTransfer(staffCUP, jobTransferId, hrCUP.getUserId());

    if (!CollectionUtils.isEmpty(toNotifyCUPs)) {

      // 3)
      for (CoreUserProfile toNotifyCUP: toNotifyCUPs) {
        sendEmailToNotifyUserAfterJobTransfer(org.getShortName(), jobTransferResponseDTO, staffCUP, toNotifyCUP);
      }

      // 4)
      sendMessageToNotifyUserAfterJobTransfer(staffCUP, toNotifyCUPs, jobTransferId, hrCUP.getUserId());
    }


  }

  private void sendEmailToStaffAfterJobTransfer(
          String orgShortName, JobTransferResponseDTO jobTransferResponseDTO, CoreUserProfile staffCUP) {

    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, staffCUP.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);
    emailParams.put(EmailParamCons.PARAM_USER_NAME, staffCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_NAME, staffCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_USER_EMAIL, staffCUP.getEmailAddress());
    if (null != jobTransferResponseDTO.getBeforeTeamDTO()) {
      emailParams.put(EmailParamCons.PARAM_BEFORE_TEAM_NAME, jobTransferResponseDTO.getBeforeTeamDTO().getTeamName());
    }else{
      emailParams.put(EmailParamCons.PARAM_BEFORE_TEAM_NAME, "");
    }

    if (null != jobTransferResponseDTO.getAfterTeamDTO()) {
      emailParams.put(EmailParamCons.PARAM_AFTER_TEAM_NAME, jobTransferResponseDTO.getAfterTeamDTO().getTeamName());
    } else {
      emailParams.put(EmailParamCons.PARAM_AFTER_TEAM_NAME, "");
    }

    if (null != jobTransferResponseDTO.getBeforeReporterSimpleUserProfileDTO()) {
      emailParams.put(EmailParamCons.PARAM_BEFORE_REPORTER_NAME,
              jobTransferResponseDTO.getBeforeReporterSimpleUserProfileDTO().getFullName());
    } else {
      emailParams.put(EmailParamCons.PARAM_BEFORE_REPORTER_NAME, "");
    }

    if (null != jobTransferResponseDTO.getAfterReporterSimpleUserProfileDTO()) {
      emailParams.put(EmailParamCons.PARAM_AFTER_REPORTER_NAME,
              jobTransferResponseDTO.getAfterReporterSimpleUserProfileDTO().getFullName());
    } else {
      emailParams.put(EmailParamCons.PARAM_AFTER_REPORTER_NAME, "");
    }

    if (null != jobTransferResponseDTO.getBeforeJobTitleOrgPickOptionDTO()) {
      emailParams.put(EmailParamCons.PARAM_BEFORE_JOB_TITLE,
              jobTransferResponseDTO.getBeforeJobTitleOrgPickOptionDTO().getOptionValue());
    } else {
      emailParams.put(EmailParamCons.PARAM_BEFORE_JOB_TITLE, "");
    }

    if (null != jobTransferResponseDTO.getAfterJobTitleOrgPickOptionDTO()) {
      emailParams.put(EmailParamCons.PARAM_AFTER_JOB_TITLE,
              jobTransferResponseDTO.getAfterJobTitleOrgPickOptionDTO().getOptionValue());
    } else {
      emailParams.put(EmailParamCons.PARAM_AFTER_JOB_TITLE, "");
    }

    if (null != jobTransferResponseDTO.getBeforeJobLevelOrgPickOptionDTO()) {
      emailParams.put(EmailParamCons.PARAM_BEFORE_JOB_LEVEL,
              jobTransferResponseDTO.getBeforeJobLevelOrgPickOptionDTO().getOptionValue());
    } else {
      emailParams.put(EmailParamCons.PARAM_BEFORE_JOB_LEVEL, "");
    }

    if (null != jobTransferResponseDTO.getAfterJobLevelOrgPickOptionDTO()) {
      emailParams.put(EmailParamCons.PARAM_AFTER_JOB_LEVEL,
              jobTransferResponseDTO.getAfterJobLevelOrgPickOptionDTO().getOptionValue());
    } else {
      emailParams.put(EmailParamCons.PARAM_AFTER_JOB_LEVEL, "");
    }

    emailParams.put(EmailParamCons.PARAM_TRANSFER_TYPE, jobTransferResponseDTO.getTransferType());
    emailParams.put(EmailParamCons.PARAM_TRANSFER_DATE, TimeUtils.formatDateWithTimeZone(jobTransferResponseDTO.getTransferDate(),
            TimeUtils.BEIJING));
    emailParams.put(EmailParamCons.PARAM_TRANSFER_DESCRIPTION, jobTransferResponseDTO.getDescription());

    LOGGER.info("emailParams:" + emailParams);
    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.TRANSFER_NOTIFICATION, emailParams);

  }

  private void sendMessageToStaffAfterJobTransfer(CoreUserProfile staffCUP, long jobTransferId, long hrUserId) {

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(staffCUP.getOrgId());
    messageDTO.setSenders(Arrays.asList(hrUserId));
    messageDTO.setTemplateId(MessageTemplate.TRANSFER_NOTIFICATION.getCode());
    messageDTO.setObjectId(jobTransferId);

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(staffCUP.getUserId()));

  }

  private void sendEmailToNotifyUserAfterJobTransfer(
      String orgShortName, JobTransferResponseDTO jobTransferResponseDTO,
      CoreUserProfile staffCUP, CoreUserProfile toNotifyUserCUP) {

    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, toNotifyUserCUP.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);
    emailParams.put(EmailParamCons.PARAM_USER_NAME, staffCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_NAME, toNotifyUserCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_USER_EMAIL, staffCUP.getEmailAddress());
    if (null != jobTransferResponseDTO.getBeforeTeamDTO()) {
      emailParams.put(EmailParamCons.PARAM_BEFORE_TEAM_NAME, jobTransferResponseDTO.getBeforeTeamDTO().getTeamName());
    } else {
      emailParams.put(EmailParamCons.PARAM_BEFORE_TEAM_NAME, "");
    }

    if (null != jobTransferResponseDTO.getAfterTeamDTO()) {
      emailParams.put(EmailParamCons.PARAM_AFTER_TEAM_NAME, jobTransferResponseDTO.getAfterTeamDTO().getTeamName());
    } else {
      emailParams.put(EmailParamCons.PARAM_AFTER_TEAM_NAME, "");
    }

    if (null != jobTransferResponseDTO.getBeforeReporterSimpleUserProfileDTO()) {
      emailParams.put(EmailParamCons.PARAM_BEFORE_REPORTER_NAME,
              jobTransferResponseDTO.getBeforeReporterSimpleUserProfileDTO().getFullName());
    } else {
      emailParams.put(EmailParamCons.PARAM_BEFORE_REPORTER_NAME, "");
    }

    if (null != jobTransferResponseDTO.getAfterReporterSimpleUserProfileDTO()) {
      emailParams.put(EmailParamCons.PARAM_AFTER_REPORTER_NAME,
              jobTransferResponseDTO.getAfterReporterSimpleUserProfileDTO().getFullName());
    } else {
      emailParams.put(EmailParamCons.PARAM_AFTER_REPORTER_NAME, "");
    }

    if (null != jobTransferResponseDTO.getBeforeJobTitleOrgPickOptionDTO()) {
      emailParams.put(EmailParamCons.PARAM_BEFORE_JOB_TITLE,
              jobTransferResponseDTO.getBeforeJobTitleOrgPickOptionDTO().getOptionValue());
    } else {
      emailParams.put(EmailParamCons.PARAM_BEFORE_JOB_TITLE, "");
    }

    if (null != jobTransferResponseDTO.getAfterJobTitleOrgPickOptionDTO()) {
      emailParams.put(EmailParamCons.PARAM_AFTER_JOB_TITLE,
              jobTransferResponseDTO.getAfterJobTitleOrgPickOptionDTO().getOptionValue());
    } else {
      emailParams.put(EmailParamCons.PARAM_AFTER_JOB_TITLE, "");
    }
    if (null != jobTransferResponseDTO.getBeforeJobLevelOrgPickOptionDTO()) {
      emailParams.put(EmailParamCons.PARAM_BEFORE_JOB_LEVEL,
              jobTransferResponseDTO.getBeforeJobLevelOrgPickOptionDTO().getOptionValue());
    } else {
      emailParams.put(EmailParamCons.PARAM_BEFORE_JOB_LEVEL, "");
    }

    if (null != jobTransferResponseDTO.getAfterJobLevelOrgPickOptionDTO()) {
      emailParams.put(EmailParamCons.PARAM_AFTER_JOB_LEVEL,
              jobTransferResponseDTO.getAfterJobLevelOrgPickOptionDTO().getOptionValue());
    } else {
      emailParams.put(EmailParamCons.PARAM_AFTER_JOB_LEVEL, "");
    }

    emailParams.put(EmailParamCons.PARAM_TRANSFER_TYPE, jobTransferResponseDTO.getTransferType());
    emailParams.put(EmailParamCons.PARAM_TRANSFER_DATE, TimeUtils.formatDateWithTimeZone(jobTransferResponseDTO.getTransferDate(),
            TimeUtils.BEIJING));
    emailParams.put(EmailParamCons.PARAM_TRANSFER_DESCRIPTION, jobTransferResponseDTO.getDescription());

    LOGGER.info("emailParams:" + emailParams);
    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.TRANSFER_NOTIFICATION, emailParams);

  }

  private void sendMessageToNotifyUserAfterJobTransfer(
      CoreUserProfile staffCUP, List<CoreUserProfile> toNotifyUserCUPs, long jobTransferId, long hrUserId) {

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(staffCUP.getOrgId());
    messageDTO.setSenders(Arrays.asList(hrUserId));
    messageDTO.setTemplateId(MessageTemplate.TRANSFER_NOTIFICATION.getCode());
    messageDTO.setObjectId(jobTransferId);

    List<Long> toNotifyUserIds = new ArrayList<>();
    for (CoreUserProfile coreUserProfile : toNotifyUserCUPs) {
      toNotifyUserIds.add(coreUserProfile.getUserId());
    }

    messageCenterFacade.addPersonalMessage(messageDTO, toNotifyUserIds);

  }

  /*********************** For PassProbation ***********************/

  /**
   * Steps:
   *  1) send email to staff
   *  2) send message to staff
   *  3) send email to toNotifyUsers
   *  4) send message to toNotifyUsers
   *
   */
  @LogAround
  public void sendEmailAndMessageAfterPassProbation(
      Org org, StatusUpdate statusUpdate, CoreUserProfileDTO staffCUPDTO,
      List<CoreUserProfile> toNotifyUserCUPs, long hrUserId) {

    // 1)
    sendEmailToStaffAfterPassProbation(org.getShortName(), statusUpdate, staffCUPDTO, hrUserId);

    // 2)
    long statusUpdateId = statusUpdate.getStatusUpdateId();
    sendMessageToStaffAfterPassProbation(staffCUPDTO, statusUpdateId, hrUserId);

    if (!CollectionUtils.isEmpty(toNotifyUserCUPs)) {

      // 3)
      for (CoreUserProfile toNotifyUserCUP: toNotifyUserCUPs) {
        sendEmailToNotifyUserAfterPassProbation(
                org.getShortName(), statusUpdate, staffCUPDTO, toNotifyUserCUP, hrUserId);
      }

      // 4)
      sendMessageToNotifyUserAfterPassProbation(staffCUPDTO, toNotifyUserCUPs, statusUpdateId, hrUserId);

    }

  }

  private void sendEmailToStaffAfterPassProbation(
          String orgShortName, StatusUpdate statusUpdate, CoreUserProfileDTO staffCUPDTO, long hrUserId) {

    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, staffCUPDTO.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);
    emailParams.put(EmailParamCons.PARAM_USER_NAME, staffCUPDTO.getFullName());
    emailParams.put(EmailParamCons.PARAM_NAME, staffCUPDTO.getFullName());
    emailParams.put(EmailParamCons.PARAM_USER_EMAIL, staffCUPDTO.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_TEAM_NAME, staffCUPDTO.getTeamMemberDTO().getTeamName());
    emailParams.put(EmailParamCons.PARAM_USER_JOB_TITLE, staffCUPDTO.getJobTitleName());
    emailParams.put(EmailParamCons.PARAM_PASS_PROBATION_TYPE, statusUpdate.getUpdateType());
    emailParams.put(EmailParamCons.PARAM_PASS_PROBATION_DATE, TimeUtils.formatDateWithTimeZone(statusUpdate.getUpdateDate(),
            TimeUtils.BEIJING));
    emailParams.put(EmailParamCons.PARAM_PASS_PROBATION_DESCRIPTION, statusUpdate.getDescription());
    LOGGER.info("emailParams:" + emailParams);
    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.PASS_PROBATION_NOTIFICATION, emailParams);

  }

  private void sendMessageToStaffAfterPassProbation(
          CoreUserProfileDTO staffCUPDTO, long statusUpdateId, long hrUserId) {

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(staffCUPDTO.getOrgId());
    messageDTO.setSenders(Arrays.asList(hrUserId));
    messageDTO.setTemplateId(MessageTemplate.PASS_PROBATION_TO_STAFF.getCode());
    messageDTO.setObjectId(statusUpdateId);

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(staffCUPDTO.getUserId()));

  }

  private void sendEmailToNotifyUserAfterPassProbation(
      String orgShortName, StatusUpdate statusUpdate,
      CoreUserProfileDTO staffCUPDTO, CoreUserProfile toNotifyCUP, long hrUserId) {

    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, toNotifyCUP.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);
    emailParams.put(EmailParamCons.PARAM_USER_NAME, staffCUPDTO.getFullName());
    emailParams.put(EmailParamCons.PARAM_NAME, toNotifyCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_USER_EMAIL, staffCUPDTO.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_TEAM_NAME, staffCUPDTO.getTeamMemberDTO().getTeamName());
    emailParams.put(EmailParamCons.PARAM_USER_JOB_TITLE, staffCUPDTO.getJobTitleName());
    emailParams.put(EmailParamCons.PARAM_PASS_PROBATION_TYPE, statusUpdate.getUpdateType());
    emailParams.put(EmailParamCons.PARAM_PASS_PROBATION_DATE, TimeUtils.formatDateWithTimeZone(statusUpdate.getUpdateDate(),
            TimeUtils.BEIJING));
    emailParams.put(EmailParamCons.PARAM_PASS_PROBATION_DESCRIPTION, statusUpdate.getDescription());

    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.PASS_PROBATION_NOTIFICATION, emailParams);

  }

  private void sendMessageToNotifyUserAfterPassProbation(
      CoreUserProfileDTO staffCUPDTO, List<CoreUserProfile> toNotifyCUPs, long statusUpdateId, long hrUserId) {

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(staffCUPDTO.getOrgId());
    messageDTO.setSenders(Arrays.asList(hrUserId));
    messageDTO.setTemplateId(MessageTemplate.PASS_PROBATION_TO_DIRECTOR.getCode());
    messageDTO.setObjectId(statusUpdateId);
    List<Long> toNotifyUserIds = new ArrayList<>();
    for (CoreUserProfile toNotifyUserCUP: toNotifyCUPs) {
      toNotifyUserIds.add(toNotifyUserCUP.getUserId());
    }

    messageCenterFacade.addPersonalMessage(messageDTO, toNotifyUserIds);

  }

  /*********************** For Resign ***********************/

  /**
   * Steps:
   *  1) send email to toNotifyUsers
   *  2) send message to toNotifyUsers
   *
   */
  @LogAround
  public void sendEmailAndMessageAfterResign(
      Org org, StatusUpdate statusUpdate, CoreUserProfileDTO staffCUPDTO,
      List<CoreUserProfile> toNotifyUserCUPs, long hrUserId) {

    if (!CollectionUtils.isEmpty(toNotifyUserCUPs)) {

      // 1)
      for (CoreUserProfile toNotifyUserCUP: toNotifyUserCUPs) {
        sendEmailToNotifyUserAfterResign(
                org.getShortName(), statusUpdate, staffCUPDTO, toNotifyUserCUP);
      }

      // 2
      long statusUpdateId = statusUpdate.getStatusUpdateId();
      sendMessageToNotifyUserAfterResign(staffCUPDTO, toNotifyUserCUPs, statusUpdateId, hrUserId);

    }

  }

  private void sendEmailToNotifyUserAfterResign(
          String orgShortName, StatusUpdate statusUpdate, CoreUserProfileDTO staffCUPDTO, CoreUserProfile toNotifyUserCUP) {

    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, toNotifyUserCUP.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);
    emailParams.put(EmailParamCons.PARAM_USER_NAME, staffCUPDTO.getFullName());
    emailParams.put(EmailParamCons.PARAM_NAME, toNotifyUserCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_USER_EMAIL, staffCUPDTO.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_TEAM_NAME, staffCUPDTO.getTeamMemberDTO().getTeamName());
    emailParams.put(EmailParamCons.PARAM_USER_JOB_TITLE, staffCUPDTO.getJobTitleName());
    emailParams.put(EmailParamCons.PARAM_RESIGN_TYPE, statusUpdate.getUpdateType());
    emailParams.put(EmailParamCons.PARAM_RESIGN_DATE, TimeUtils.formatDateWithTimeZone(statusUpdate.getUpdateDate(),
            TimeUtils.BEIJING));
    emailParams.put(EmailParamCons.PARAM_RESIGN_DESCRIPTION, statusUpdate.getDescription());

    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.RESIGN_NOTIFICATION, emailParams);

  }

  private void sendMessageToNotifyUserAfterResign(
      CoreUserProfileDTO staffCUPDTO, List<CoreUserProfile> toNotifyCUPs, long statusUpdateId, long hrUserId) {

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(staffCUPDTO.getOrgId());
    messageDTO.setSenders(Arrays.asList(hrUserId));
    messageDTO.setTemplateId(MessageTemplate.RESIGN_NOTIFICATION.getCode());
    messageDTO.setObjectId(statusUpdateId);
    List<Long> toNotifyUserIds = new ArrayList<>();
    for (CoreUserProfile toNotifyUserCUP: toNotifyCUPs) {
      toNotifyUserIds.add(toNotifyUserCUP.getUserId());
    }

    messageCenterFacade.addPersonalMessage(messageDTO, toNotifyUserIds);

  }

}
