package hr.wozai.service.user.server.component;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.thirdparty.client.bean.BatchEmail;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.client.enums.MessageTemplate;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.thirdparty.client.utils.EmailTemplateHelper;
import hr.wozai.service.thirdparty.client.utils.SqsProducer;
import hr.wozai.service.user.client.okr.dto.DirectorDTO;
import hr.wozai.service.user.client.okr.dto.ObjectiveDTO;
import hr.wozai.service.user.client.okr.dto.ObjectiveListDTO;
import hr.wozai.service.user.client.okr.enums.DirectorType;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.client.okr.enums.PeriodTimeSpan;
import hr.wozai.service.user.client.okr.enums.RegularRemindType;
import hr.wozai.service.user.client.okr.facade.OkrFacade;
import hr.wozai.service.user.client.userorg.dto.OrgDTO;
import hr.wozai.service.user.server.dao.okr.ObjectiveDao;
import hr.wozai.service.user.server.dao.okr.ObjectivePeriodDao;
import hr.wozai.service.user.server.dao.okr.OkrRemindSettingDao;
import hr.wozai.service.user.server.dao.userorg.OrgDao;
import hr.wozai.service.user.server.enums.OkrRemindType;
import hr.wozai.service.user.server.helper.CalcProgressHelper;
import hr.wozai.service.user.server.model.okr.*;
import hr.wozai.service.user.server.model.survey.SurveyActivity;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/10/9
 */
@Component
public class SurveyTimerTask {
  private static final Logger LOGGER = LoggerFactory.getLogger(SurveyTimerTask.class);

  private static final long ONE_DAY = 3600 * 24 * 1000;

  @Autowired
  SurveyService surveyService;

  @Autowired
  OrgDao orgDao;

  @Autowired
  UserService userService;

  @Autowired
  UserEmploymentService userEmploymentService;

  @Autowired
  UserProfileService userProfileService;

  @Autowired
  EmailTemplateHelper emailTemplateHelper;

  @Autowired
  @Qualifier("messageCenterFacadeProxy")
  private ThriftClientProxy messageCenterFacadeProxy;

  private MessageCenterFacade messageCenterFacade;

  @Value("${okr.url.host}")
  private String host;

  @PostConstruct
  public void init() throws Exception {
    messageCenterFacade = (MessageCenterFacade) messageCenterFacadeProxy.getObject();
  }

  /**
   * 每周五19:00跑
   */
  @Scheduled(cron="0 01 19 ? * FRI")
  public void initSurveyActivityForEveryOrg() {
    System.out.println(("initSurveyActivityForEveryOrg begin"));
    long curTs = System.currentTimeMillis();
    List<Org> orgList = orgDao.listAllOrgs();
    for (Org org : orgList) {
      List<Long> userIds = userService.listAllUsersByOrgId(org.getOrgId());
      List<Long> activeUsers = userEmploymentService.sublistUserIdByUserStatus(org.getOrgId(), userIds,
              UserStatus.ACTIVE.getCode());
      SurveyActivity surveyActivity = new SurveyActivity();
      surveyActivity.setOrgId(org.getOrgId());
      surveyActivity.setCreatedTime(curTs);
      surveyActivity.setCreatedUserId(-1L);
      boolean result = surveyService.initSurveyActivity(org.getOrgId(), surveyActivity, userIds);
      if (result) {
        List<CoreUserProfile> coreUserProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(
                org.getOrgId(), activeUsers);

        sendMessage(org, coreUserProfiles);
        batchSendEmail(org, coreUserProfiles);
      }
    }
    LOGGER.info("sendObjectiveDeadlineNotification finish");
  }

  private void batchSendEmail(Org org, List<CoreUserProfile> coreUserProfiles) {
    String urlCode = "";
    try {
      urlCode = URLEncoder.encode("/#/survey/activity", "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(e.toString());
    }
    String url = host + "/survey/activity?urlCode=" + urlCode;

    BatchEmail batchEmail = new BatchEmail();
    Map<String, String> fixedParamsMap = new HashMap<>();
    fixedParamsMap.put("orgShortName", org.getShortName());
    fixedParamsMap.put("url", url);

    List<String> dynamicParamSeq = new ArrayList<>();
    dynamicParamSeq.add("userName");
    dynamicParamSeq.add("dstEmailAddress");
    List<List<String>> dynamicParams = new ArrayList<>();
    for (CoreUserProfile coreUserProfile : coreUserProfiles) {
      List<String> params = new ArrayList<>();
      params.add(coreUserProfile.getFullName());
      params.add(coreUserProfile.getEmailAddress());
      dynamicParams.add(params);
    }
    batchEmail.setFixedParamsMap(fixedParamsMap);
    batchEmail.setDynamicParamSeq(dynamicParamSeq);
    batchEmail.setDynamicParams(dynamicParams);
    batchEmail.setEmailTemplate(EmailTemplate.SURVEY_PUSH);
    emailTemplateHelper.preBatchSendEmail(batchEmail);
  }

  private void sendMessage(Org org, List<CoreUserProfile> coreUserProfiles) {
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(org.getOrgId());
    messageDTO.setSenders(new ArrayList<>());
    messageDTO.setTemplateId(MessageTemplate.SURVEY_PUSH.getCode());
    messageDTO.setObjectId(1L);
    messageDTO.setObjectContent("");
    LongDTO voidDTO = messageCenterFacade.addSystemMessage(messageDTO);
    if (ServiceStatus.COMMON_OK.getCode() != voidDTO.getServiceStatusDTO().getCode()) {
      LOGGER.error("messageDTO:" + messageDTO);
      LOGGER.error("sendMessageForOkrUpdate-error():" + voidDTO);
    }
  }

  private List<Long> getUserIdsFromCoreUserProfileList(List<CoreUserProfile> coreUserProfiles) {
    List<Long> result = new ArrayList<>();

    for (CoreUserProfile coreUserProfile : coreUserProfiles) {
      result.add(coreUserProfile.getUserId());
    }
    return result;
  }
}
