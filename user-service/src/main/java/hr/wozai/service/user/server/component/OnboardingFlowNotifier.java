package hr.wozai.service.user.server.component;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.client.utils.EmailParamCons;
import hr.wozai.service.thirdparty.client.utils.RabbitMQProducer;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.client.userorg.util.ExternalUrlUtils;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.client.enums.MessageTemplate;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.thirdparty.client.utils.SqsProducer;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
@Component("onboardingFlowNotifier")
public class OnboardingFlowNotifier {

  private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingFlowNotifier.class);

  private static final String HTTP_ENDPOINT_PREFIX_OF_AUTH = "u?uuid=";
  private static final String HTTP_ENDPOINT_SURFIX_OF_INIT_PASSWORD = "#init-password";
  private static final String HTTP_ENDPOINT_SURFIX_OF_ONBOARDING_FLOW_REVIEW = "#/team/staffProfile/";
  private static final String HTTP_ENDPOINT_SURFIX_OF_USER_PROFILE = "#/user/";
  private static final String HTTP_ENDPOINT_SURFIX_OF_LOGIN = "u";

//  private static final String SQIAN_SERVICE_EMAIL = "service@sqian.com";
  private static final String SQIAN_SERVICE_EMAIL = "service@sqian.com";

/*  private static final String PARAM_DST_EMAIL_ADDRESS = "dstEmailAddress";
  private static final String PARAM_ORG_SHORT_NAME = "orgShortName";
  private static final String PARAM_USER_FULL_NAME = "userFullName";
  private static final String PARAM_PROFILE_URL = "profileUrl";
  private static final String PARAM_JOB_TITLE_NAME = "jobTitleName";
  private static final String PARAM_USER_NAME = "userName";
  private static final String PARAM_NAME = "name";
  private static final String PARAM_INVITATION_URL = "invitationUrl";
  private static final String PARAM_STAFF_FULL_NAME = "staffFullName";
  private static final String PARAM_HR_FULL_NAME = "hrFullName";
  private static final String PARAM_ONBOARDING_REVIEW_URL = "onboardingReviewUrl";
  private static final String PARAM_ONBOARDING_INVITATION_URL = "onboardingInvitationUrl";
  private static final String PARAM_LOGIN_URL = "loginUrl";
  private static final String PARAM_URL = "url";*/


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

  @LogAround
  public void sendOpenAccountEmailToFirstStaff(
      String orgShortName, CoreUserProfile coreUserProfile, String invitationUUID) {

    String invitationUrl = generateInvitationUrlOfInitPassword(invitationUUID);

    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, coreUserProfile.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);
    emailParams.put(EmailParamCons.PARAM_USER_NAME, coreUserProfile.getFullName());
    emailParams.put(EmailParamCons.PARAM_URL, invitationUrl);

    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.OPEN_ACCOUNT_SUCCESS, emailParams);
  }

  @LogAround
  public void sendOpenAccountEmailToAdmin(String orgShortName, String adminEmail) {
    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);
    emailParams.put(EmailParamCons.PARAM_ADMIN_EMAIL, adminEmail);
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, adminEmail);
    emailParams.put(EmailParamCons.PARAM_URL, host);

    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.OPEN_ACCOUNT_SUCCESS_TOADMIN, emailParams);
  }


  @LogAround
  public void resendInvitationActivationEmailToImportedStaff(
      String orgShortName, CoreUserProfile coreUserProfile, String invitationUUID) {
    String invitationUrl = generateInvitationUrlOfInitPassword(invitationUUID);
    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, coreUserProfile.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);
    emailParams.put(EmailParamCons.PARAM_USER_FULL_NAME, coreUserProfile.getFullName());
    emailParams.put(EmailParamCons.PARAM_INVITATION_URL, invitationUrl);
    LOGGER.info("resendInvitationActivationEmailToImportedStaff(): invitationUrl=" + invitationUrl);
    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.INVITE_ACTIVATION, emailParams);
  }

  @LogAround
  public void sendManualOperationCSVFileEmailToActor(
      String submitterEmail, String csvUrl) {

    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, SQIAN_SERVICE_EMAIL);
    emailParams.put(EmailParamCons.PARAM_SUBMITTER_EMAIL, submitterEmail);
    emailParams.put(EmailParamCons.PARAM_URL, csvUrl);

    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.WARRANT_MANUAL_OPERATION, emailParams);

  }


  @LogAround
  public void batchSendInvitationEmailToImportedStaff(
      String orgShortName, List<CoreUserProfile> coreUserProfiles, Map<Long, String> userIdUUIDMap) {

    List<Long> userIds = new ArrayList<>();
    for (Long userId: userIdUUIDMap.keySet()) {
      if (null != userId) {
        userIds.add(userId);
      }
    }
    Map<Long, CoreUserProfile> coreUserProfileMap = new HashMap<>();
    for (CoreUserProfile coreUserProfile : coreUserProfiles) {
      coreUserProfileMap.put(coreUserProfile.getUserId(), coreUserProfile);
    }

    for (Long userId: userIds) {
      CoreUserProfile coreUserProfile = coreUserProfileMap.get(userId);
      String uuid = userIdUUIDMap.get(userId);
      String invitationUrl = generateInvitationUrlOfInitPassword(uuid);
      Map<String, String> emailParams = new HashMap<>();
      emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, coreUserProfile.getEmailAddress());
      emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);
      emailParams.put(EmailParamCons.PARAM_USER_FULL_NAME, coreUserProfile.getFullName());
      emailParams.put(EmailParamCons.PARAM_INVITATION_URL, invitationUrl);
      LOGGER.info("invitationUrl=" + invitationUrl);
      rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.INVITE_ACTIVATION, emailParams);
    }

  }

  @LogAround
  public void individuallySendInvitationEmailToImportedStaff(
      String orgShortName, CoreUserProfile coreUserProfile, String uuid) {
      String invitationUrl = generateInvitationUrlOfInitPassword(uuid);
      Map<String, String> emailParams = new HashMap<>();
      emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, coreUserProfile.getEmailAddress());
      emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);
      emailParams.put(EmailParamCons.PARAM_USER_FULL_NAME, coreUserProfile.getFullName());
      emailParams.put(EmailParamCons.PARAM_INVITATION_URL, invitationUrl);
      LOGGER.info("invitationUrl=" + invitationUrl);
      rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.INVITE_ACTIVATION, emailParams);
  }

  @LogAround
  public void sendInvitationEmailToOnboardingStaff(String orgShorName, CoreUserProfile coreUserProfile, String uuid) {

    String invitationUrl = ExternalUrlUtils.generateInvitationUrlOfOnboardingFlowForStaff(host, uuid);

    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, coreUserProfile.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShorName);
    emailParams.put(EmailParamCons.PARAM_NAME, coreUserProfile.getFullName());
    emailParams.put(EmailParamCons.PARAM_URL, invitationUrl);
    LOGGER.info("invitationUrl=" + invitationUrl);

    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.INVITE_ONBOARDING, emailParams);
  }

  @LogAround
  public void sendEmailAndMessageToHrAfterStaffSubmitOnboardingFlow(
      Org org, CoreUserProfile staffCUP, CoreUserProfile hrCUP) {
    sendEmailToHrAfterStaffSubmitOnboardingFlow(org, staffCUP, hrCUP);
    sendMessageToHrAfterStaffSubmitOnboardingFlow(org, staffCUP, hrCUP);
  }

  @LogAround
  public void sendEmailAndMessageToStaffAfterHrRejectOnboardingFlow(
      Org org, CoreUserProfile staffCUP, CoreUserProfile hrCUP, String uuid) {
    sendEmailToStaffAfterHrRejectOnboardingFlow(org.getShortName(), staffCUP, hrCUP, uuid);
    sendMessageToStaffAfterHrRejectOnboardingFlow(org, staffCUP, hrCUP);
  }

  @LogAround
  public void sendEmailAndMessageToStaffAfterHrApproveOnboardingFlow(
      Org org, CoreUserProfile staffCUP, CoreUserProfile hrCUP) {
    sendEmailToStaffAfterHrApproveOnboardingFlow(org, staffCUP, hrCUP);
    sendMessageToStaffAfterHrApproveOnboardingFlow(org, staffCUP, hrCUP);
  }

  @LogAround
  public void broadcastEmailToStaffAfterHrApproveOnboardingFlow(
      String orgShortName, CoreUserProfile newStaffCUP, String jobTitleName, List<CoreUserProfile> dstCUPs) {

    String staffUserIdEnctypted = null;
    try {
      staffUserIdEnctypted = EncryptUtils.symmetricEncrypt(String.valueOf(newStaffCUP.getUserId()));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    String profileUrl = generateUserProfileUrl(staffUserIdEnctypted);

    for (CoreUserProfile dstCUP: dstCUPs) {
      sendEmailToSingleStaffAfterHrApproveOnboardingFlow(orgShortName, newStaffCUP, jobTitleName, dstCUP);
    }

  }



  private void sendEmailToHrAfterStaffSubmitOnboardingFlow(
      Org org, CoreUserProfile staffCUP, CoreUserProfile hrCUP) {

    String staffUserIdEnctypted = null;
    try {
      staffUserIdEnctypted = EncryptUtils.symmetricEncrypt(String.valueOf(staffCUP.getUserId()));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    String reviewUrl = generateInvitationUrlOfOnboardingFlowReview(staffUserIdEnctypted);
    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, hrCUP.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, org.getShortName());
    emailParams.put(EmailParamCons.PARAM_STAFF_FULL_NAME, staffCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_HR_FULL_NAME, hrCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_ONBOARDING_REVIEW_URL, reviewUrl);
    LOGGER.info("onboardingReviewUrl=" + reviewUrl);

    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.SUBMIT_ONBOARDING, emailParams);
  }

  private void sendMessageToHrAfterStaffSubmitOnboardingFlow(Org org, CoreUserProfile staffCUP, CoreUserProfile hrCUP) {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(org.getOrgId());
    messageDTO.setSenders(Arrays.asList(staffCUP.getUserId()));
    messageDTO.setTemplateId(MessageTemplate.ONBOARDING_SUBMIT.getCode());
    messageDTO.setObjectId(staffCUP.getUserId());

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(hrCUP.getUserId()));
  }


  private void sendEmailToStaffAfterHrRejectOnboardingFlow(
      String orgShortName, CoreUserProfile staffCUP, CoreUserProfile hrCUP, String uuid) {

    String invitationUrl = ExternalUrlUtils.generateInvitationUrlOfOnboardingFlowForStaff(host, uuid);
    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, staffCUP.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);
    emailParams.put(EmailParamCons.PARAM_STAFF_FULL_NAME, staffCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_HR_FULL_NAME, hrCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_ONBOARDING_INVITATION_URL, invitationUrl);
    LOGGER.info("invitationUrl=" + invitationUrl);

    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.REJECT_ONBOARDING, emailParams);
  }

  private void sendMessageToStaffAfterHrRejectOnboardingFlow(
      Org org, CoreUserProfile staffCUP, CoreUserProfile hrCUP) {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(org.getOrgId());
    messageDTO.setSenders(Arrays.asList(hrCUP.getUserId().longValue()));
    messageDTO.setTemplateId(MessageTemplate.ONBOARDING_REJECT.getCode());
    messageDTO.setObjectId(staffCUP.getUserId());

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(staffCUP.getUserId()));
  }

  private void sendEmailToStaffAfterHrApproveOnboardingFlow(Org org, CoreUserProfile staffCUP, CoreUserProfile hrCUP) {

    String loginUrl = generateLoginUrl();

    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, staffCUP.getEmailAddress());
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, org.getShortName());
    emailParams.put(EmailParamCons.PARAM_STAFF_FULL_NAME, staffCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_HR_FULL_NAME, hrCUP.getFullName());
    emailParams.put(EmailParamCons.PARAM_LOGIN_URL, loginUrl);
    LOGGER.info("loginUrl=" + loginUrl);

    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.APPROVE_ONBOARDING, emailParams);
  }

  private void sendMessageToStaffAfterHrApproveOnboardingFlow(Org org, CoreUserProfile staffCUP, CoreUserProfile hrCUP) {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(org.getOrgId());
    messageDTO.setSenders(Arrays.asList(hrCUP.getUserId().longValue()));
    messageDTO.setTemplateId(MessageTemplate.ONBOARDING_APPROVE.getCode());
    messageDTO.setObjectId(staffCUP.getUserId());

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(staffCUP.getUserId()));
  }

  private void sendEmailToSingleStaffAfterHrApproveOnboardingFlow(
      String orgShortName, CoreUserProfile newStaffCUP, String jobTitleName, CoreUserProfile dstStaffCUP) {

    if (null == jobTitleName || jobTitleName == "") {
      jobTitleName = " ";
    }

    String dstEmailAddress = dstStaffCUP.getEmailAddress();
    String userName = newStaffCUP.getFullName();
    String staffUserIdEnctypted = null;
    try {
      staffUserIdEnctypted = EncryptUtils.symmetricEncrypt(String.valueOf(newStaffCUP.getUserId()));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    String profileUrl = generateUserProfileUrl(staffUserIdEnctypted);
    Map<String, String> emailParams = new HashMap<>();
    emailParams.put(EmailParamCons.PARAM_DST_EMAIL_ADDRESS, dstEmailAddress);
    emailParams.put(EmailParamCons.PARAM_USER_NAME, userName);
    emailParams.put(EmailParamCons.PARAM_JOB_TITLE_NAME, jobTitleName);
    emailParams.put(EmailParamCons.PARAM_PROFILE_URL, profileUrl);
    emailParams.put(EmailParamCons.PARAM_ORG_SHORT_NAME, orgShortName);

    // TEST
    LOGGER.info("profileUrl={}", profileUrl);

    rabbitMQProducer.sendMessageWithoutSurroundingPercentSign(EmailTemplate.ENROLL_BROADCAST, emailParams);
  }

  private String generateInvitationUrlOfInitPassword(String uuid) {
    return host + HTTP_ENDPOINT_PREFIX_OF_AUTH + uuid + HTTP_ENDPOINT_SURFIX_OF_INIT_PASSWORD;
  }

  private String generateInvitationUrlOfOnboardingFlowReview(String encryptedUserId) {
    String urlSuffix = HTTP_ENDPOINT_SURFIX_OF_ONBOARDING_FLOW_REVIEW + encryptedUserId;

    String urlCode = null;
    try {
      urlCode = URLEncoder.encode("/" + urlSuffix, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(e.toString());
    }
    String url = host + urlSuffix + "?urlCode=" + urlCode;

    return url;
  }

  private String generateLoginUrl() {
    return host + HTTP_ENDPOINT_SURFIX_OF_LOGIN;
  }

  private String generateUserProfileUrl(String encryptedUserId) {
    String urlSuffix = HTTP_ENDPOINT_SURFIX_OF_USER_PROFILE + encryptedUserId;

    String urlCode = null;
    try {
      urlCode = URLEncoder.encode("/" + urlSuffix, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(e.toString());
    }
    String url = host + urlSuffix + "?urlCode=" + urlCode;

    return url;
  }
}
