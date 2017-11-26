package hr.wozai.service.user.server.factory;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.client.utils.EmailTemplateHelper;
import hr.wozai.service.thirdparty.client.utils.RabbitMQProducer;
import hr.wozai.service.user.client.common.enums.RemindType;
import hr.wozai.service.user.client.okr.enums.DirectorType;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.server.model.common.RemindSetting;
import hr.wozai.service.user.server.model.okr.Director;
import hr.wozai.service.user.server.model.okr.KeyResult;
import hr.wozai.service.user.server.model.okr.Objective;
import hr.wozai.service.user.server.model.okr.OkrComment;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.service.*;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.client.enums.MessageTemplate;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.thirdparty.client.utils.SqsProducer;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/19
 */
@Component
public class OkrFacadeFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(OkrFacadeFactory.class);

  private static final String HASH_PREFIX = "/#";
  private static final String OKR_URL_PREFIX = "/okr/objectives/";
  private static final String OKR_URL_SUFFIX = "?type=2&teamId=";

  @Autowired
  private OkrService okrService;

  @Autowired
  private TeamService teamService;

  @Autowired
  private UserProfileService userProfileService;

  @Autowired
  private NameIndexService nameIndexService;

  @Autowired
  private OrgService orgService;

  @Autowired
  private RemindSettingService remindSettingService;

  @Autowired
  @Qualifier("messageCenterFacadeProxy")
  private ThriftClientProxy messageCenterFacadeProxy;

  private MessageCenterFacade messageCenterFacade;

/*
  @Autowired
  SqsProducer sqsProducer;
*/

  @Autowired
  RabbitMQProducer rabbitMQProducer;

  @Autowired
  EmailTemplateHelper emailTemplateHelper;

  @Value("${okr.url.host}")
  private String host;

  @PostConstruct
  public void init() throws Exception {
    messageCenterFacade = (MessageCenterFacade) messageCenterFacadeProxy.getObject();
  }

  public void sendMessageAndEmailWhenCreateObjective(Objective objective, List<Director> directors, long actorUserId) {
    if (checkIfNeedSendEmail(objective)) return;
    if (!CollectionUtils.isEmpty(directors)) {
      Long orgId = objective.getOrgId();
      List<Long> receivers = getUserIdsFromDirectors(directors);
      sendMessageForOkrUpdate(orgId, objective, receivers, actorUserId);

      List<CoreUserProfile> userProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId,
              receivers);
      String updateContent = "目标的负责人设置为 " + getNameString(userProfiles);
      sendEmailForOkrUpdate(orgId, receivers, objective, updateContent, actorUserId);
    }
  }

  public void sendMessageAndEmailWhenUpdateObjective(Objective oldObjective, Objective newObjective,
                                                     List<Director> oldDirectors,
                                                     List<Director> newDirectors, long actorUserId) {
    if (checkIfNeedSendEmail(oldObjective)) return;
    Long orgId = oldObjective.getOrgId();

    StringBuffer updateContent = new StringBuffer();
    // todo: 完成度重写
    String before;
    String after;
    if (oldObjective.getIsAutoCalc() == 0 && newObjective.getIsAutoCalc() == 1) {
      if (oldObjective.getProgressMetricType() == 1) {
        before = oldObjective.getCurrentAmount().stripTrailingZeros().toPlainString() + oldObjective.getUnit();
      } else {
        before = oldObjective.getCurrentAmount().stripTrailingZeros().toPlainString()  + "/"
                + oldObjective.getGoalAmount().stripTrailingZeros().toPlainString()
                + " " + oldObjective.getUnit() + "（初始值："
                + oldObjective.getStartingAmount().stripTrailingZeros().toPlainString()  + "）";
      }
      after = "自动计算";
      updateContent.append("目标完成度由 ").append(before).append(" 更新为 ").append(after);
    } else if (oldObjective.getIsAutoCalc() == 0 && newObjective.getIsAutoCalc() == 0) {
      BigDecimal oldStartAmount = oldObjective.getStartingAmount();
      BigDecimal oldGoalAmount = oldObjective.getGoalAmount();
      BigDecimal oldCurrentAmount = oldObjective.getCurrentAmount();
      BigDecimal newStartAmount = newObjective.getStartingAmount();
      BigDecimal newGoalAmount = newObjective.getGoalAmount();
      BigDecimal newCurrentAmount = newObjective.getCurrentAmount();
      if (oldStartAmount.compareTo(newStartAmount) != 0 || oldGoalAmount.compareTo(newGoalAmount) != 0
              || oldCurrentAmount.compareTo(newCurrentAmount) != 0) {
        if (oldObjective.getProgressMetricType() == 1) {
          before = oldCurrentAmount.stripTrailingZeros().toPlainString() + oldObjective.getUnit();
        } else {
          before = oldCurrentAmount.stripTrailingZeros().toPlainString() + "/"
                  + oldGoalAmount.stripTrailingZeros().toPlainString()
                  + " " + oldObjective.getUnit() + "（初始值："
                  + oldStartAmount.stripTrailingZeros().toPlainString() + "）";
        }

        if (newObjective.getProgressMetricType() == 1) {
          after = newCurrentAmount.stripTrailingZeros().toPlainString() + newObjective.getUnit();
        } else {
          after = newCurrentAmount.stripTrailingZeros().toPlainString() + "/"
                  + newGoalAmount.stripTrailingZeros().toPlainString()
                  + " " + newObjective.getUnit() + "（初始值："
                  + newStartAmount.stripTrailingZeros().toPlainString() + "）";
        }
        updateContent.append("目标完成度由 ").append(before).append(" 更新为 ").append(after);
      }

    } else if (oldObjective.getIsAutoCalc() == 1 && newObjective.getIsAutoCalc() == 0) {
      before = "自动计算";
      if (newObjective.getProgressMetricType() == 1) {
        after = newObjective.getCurrentAmount().stripTrailingZeros().toPlainString() + newObjective.getUnit();
      } else {
        after = newObjective.getCurrentAmount().stripTrailingZeros().toPlainString() + "/"
                + newObjective.getGoalAmount().stripTrailingZeros().toPlainString()
                + " " + newObjective.getUnit() + "（初始值："
                + newObjective.getStartingAmount().stripTrailingZeros().toPlainString() + "）";
      }
      updateContent.append("目标完成度由 ").append(before).append(" 更新为 ").append(after);
    } else {

    }


    List<Long> oldUserIds = getUserIdsFromDirectors(oldDirectors);
    List<Long> newUserIds = getUserIdsFromDirectors(newDirectors);
    List<Long> receivers = (List) CollectionUtils.union(oldUserIds, newUserIds);
    List<CoreUserProfile> oldUserProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(
            orgId, oldUserIds);
    List<CoreUserProfile> userProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(
            orgId, newUserIds);
    if (!CollectionUtils.isEqualCollection(oldUserIds, newUserIds)) {
      if (updateContent.length() != 0) {
        updateContent.append("<br/>");
      }
      if (!CollectionUtils.isEmpty(oldUserProfiles)) {
        updateContent.append("目标负责人由 ").append(getNameString(oldUserProfiles)).append(" 更新为 ")
                .append(getNameString(userProfiles));
      } else {
        updateContent.append("目标负责人设置为 " + getNameString(userProfiles));
      }
    }

    if (!CollectionUtils.isEmpty(receivers) && updateContent.length() != 0) {
      sendMessageForOkrUpdate(orgId, oldObjective, receivers, actorUserId);
      sendEmailForOkrUpdate(orgId, receivers, oldObjective, updateContent.toString(), actorUserId);
    }
  }

  private boolean checkIfNeedSendEmail(Objective oldObjective) {
    if (oldObjective.getType().intValue() != OkrType.TEAM.getCode().intValue()
            && oldObjective.getType().intValue() != OkrType.PROJECT_TEAM.getCode().intValue()) {
      return true;
    }
    return false;
  }

  public void sendMessageAndEmailWhenCreateKR(KeyResult keyResult, List<Director> directors, long actorUserId) {
    Long orgId = keyResult.getOrgId();
    Long objectiveId = keyResult.getObjectiveId();
    Objective objective = okrService.getObjective(orgId, objectiveId);
    if (checkIfNeedSendEmail(objective)) return;

    List<Director> objectiveDirectors = okrService.listDirector(orgId, DirectorType.OBJECTIVE.getCode(), objectiveId);

    // 获取消息和email的接收人
    List<Long> objUsers = getUserIdsFromDirectors(objectiveDirectors);
    List<Long> krUsers = getUserIdsFromDirectors(directors);
    List<Long> receivers = (List) CollectionUtils.union(objUsers, krUsers);

    if (!CollectionUtils.isEmpty(krUsers)) {
      sendMessageForOkrUpdate(orgId, objective, receivers, actorUserId);

      List<CoreUserProfile> userProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId, krUsers);
      StringBuffer updateContent = new StringBuffer();
      updateContent.append("关键结果【").append(keyResult.getContent()).append("】的负责人设置为 ");
      updateContent.append(getNameString(userProfiles));
      sendEmailForOkrUpdate(orgId, receivers, objective, updateContent.toString(), actorUserId);
    }

  }

  public void sendMessageAndEmailWhenUpdateKR(KeyResult oldKR, KeyResult newKR, List<Director> oldDirectors,
                                              List<Director> newDirectors, long actorUserId) {
    long orgId = oldKR.getOrgId();
    long objectiveId = oldKR.getObjectiveId();
    Objective objective = okrService.getObjective(orgId, objectiveId);
    if (checkIfNeedSendEmail(objective)) return;
    List<Director> objectiveDirectors = okrService.listDirector(orgId, DirectorType.OBJECTIVE.getCode(), objectiveId);

    List<Long> objUsers = getUserIdsFromDirectors(objectiveDirectors);
    List<Long> oldKrUsers = getUserIdsFromDirectors(oldDirectors);
    List<Long> newKrUsers = getUserIdsFromDirectors(newDirectors);
    List<Long> receivers = (List) CollectionUtils.union(objUsers, CollectionUtils.union(oldKrUsers, newKrUsers));

    StringBuffer updateContent = new StringBuffer();
    // todo: 完成度重写
    String before;
    String after;
    BigDecimal oldStartAmount = oldKR.getStartingAmount();
    BigDecimal oldGoalAmount = oldKR.getGoalAmount();
    BigDecimal oldCurrentAmount = oldKR.getCurrentAmount();
    BigDecimal newStartAmount = newKR.getStartingAmount();
    BigDecimal newGoalAmount = newKR.getGoalAmount();
    BigDecimal newCurrentAmount = newKR.getCurrentAmount();
    if (oldStartAmount.compareTo(newStartAmount) != 0 || oldGoalAmount.compareTo(newGoalAmount) != 0
            || oldCurrentAmount.compareTo(newCurrentAmount) != 0) {
      if (oldKR.getProgressMetricType() == 1) {
        before = oldCurrentAmount.stripTrailingZeros().toPlainString() + oldKR.getUnit();
      } else {
        before = oldCurrentAmount.stripTrailingZeros().toPlainString() + "/"
                + oldGoalAmount.stripTrailingZeros().toPlainString()
                + " " + oldKR.getUnit() + "（初始值："
                + oldStartAmount.stripTrailingZeros().toPlainString() + "）";
      }

      if (newKR.getProgressMetricType() == 1) {
        after = newCurrentAmount.stripTrailingZeros().toPlainString() + newKR.getUnit();
      } else {
        after = newCurrentAmount.stripTrailingZeros().toPlainString() + "/"
                + newGoalAmount.stripTrailingZeros().toPlainString()
                + " " + newKR.getUnit() + "（初始值："
                + newStartAmount.stripTrailingZeros().toPlainString() + "）";
      }
      updateContent.append("关键结果【").append(oldKR.getContent()).append("】的完成度由 ")
              .append(before).append(" 更新为 ").append(after);
    }


    List<CoreUserProfile> oldUserProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId,
            oldKrUsers);
    List<CoreUserProfile> userProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId,
            newKrUsers);

    if (!CollectionUtils.isEqualCollection(oldKrUsers, newKrUsers)) {
      if (updateContent.length() != 0) {
        updateContent.append("<br/>");
      }
      if (!CollectionUtils.isEmpty(oldUserProfiles)) {
        updateContent.append("关键结果【").append(oldKR.getContent()).append("】的负责人由 ")
                .append(getNameString(oldUserProfiles)).append(" 更新为 ")
                .append(getNameString(userProfiles));
      } else {
        updateContent.append("关键结果【").append(oldKR.getContent()).append("】的负责人设置为 ")
                .append(getNameString(userProfiles));
      }
    }

    if (!CollectionUtils.isEmpty(receivers) && updateContent.length() != 0) {
      sendMessageForOkrUpdate(orgId, objective, receivers, actorUserId);
      sendEmailForOkrUpdate(orgId, receivers, objective, updateContent.toString(), actorUserId);
    }
  }

  public void sendMessageAndEmailWhenAddOkrComment(long orgId, OkrComment okrComment, long actorUserId) {
    long objectiveId = okrComment.getObjectiveId();
    long keyResult = okrComment.getKeyResultId();
    Objective objective = okrService.getObjective(orgId, objectiveId);

    Set<Long> receivers = new HashSet<>();
    if (keyResult != 0) {
      List<Director> keyResultDirectors = okrService.listDirector(orgId, DirectorType.KEYRESULT.getCode(), keyResult);
      receivers.addAll(getUserIdsFromDirectors(keyResultDirectors));
    }
    List<Director> objectiveDirectors = okrService.listDirector(orgId, DirectorType.OBJECTIVE.getCode(), objectiveId);
    receivers.addAll(getUserIdsFromDirectors(objectiveDirectors));

    if (!CollectionUtils.isEmpty(receivers)) {
      sendMessageForOkrUpdate(orgId, objective, new ArrayList<>(receivers), actorUserId);
      sendEmailForAddOkrComment(orgId, new ArrayList<>(receivers), objective, okrComment, actorUserId);
    }
  }

  private void sendMessageForOkrUpdate(long orgId, Objective objective, List<Long> receivers, long actorUserId) {
    receivers.remove(actorUserId);

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(new ArrayList<>());
    messageDTO.setTemplateId(MessageTemplate.OKR_UPDATE.getCode());
    messageDTO.setObjectId(objective.getObjectiveId());
    messageDTO.setObjectContent(JSONObject.toJSONString(objective));
    VoidDTO voidDTO = messageCenterFacade.addPersonalMessage(messageDTO, receivers);
    if (ServiceStatus.COMMON_OK.getCode() != voidDTO.getServiceStatusDTO().getCode()) {
      LOGGER.error("messageDTO:" + messageDTO);
      LOGGER.error("sendMessageForOkrUpdate-error():" + voidDTO);
    }
  }

  private void sendEmailForOkrUpdate(
          long orgId, List<Long> receivers, Objective objective, String updateContent, long actorUserId) {
    Org org = orgService.getOrg(orgId);
    for (Long userId : receivers) {
      if (userId == actorUserId) {
        continue;
      }
      RemindSetting remindSetting = remindSettingService.getRemindSettingByUserIdAndRemindType(orgId,
              userId, RemindType.TEAM_OKR_UPDATE.getCode());
      if (remindSetting.getStatus() == 0) {
        return;
      }
      CoreUserProfile user = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
      String objectiveIdString;
      String ownerIdString;
      try {
        objectiveIdString = EncryptUtils.symmetricEncrypt(Long.toString(objective.getObjectiveId()));
        ownerIdString = EncryptUtils.symmetricEncrypt(Long.toString(objective.getOwnerId()));
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }

      String okrSuffix = "?type=2&teamId=";
      if (objective.getType() == OkrType.PROJECT_TEAM.getCode()) {
        okrSuffix = "?type=4&projectTeamId=";
      }

      String urlCode = null;
      try {
        urlCode = URLEncoder.encode(
                HASH_PREFIX + OKR_URL_PREFIX + objectiveIdString + okrSuffix + ownerIdString, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        LOGGER.error(e.toString());
      }
      String messageJson = EmailTemplate.getOKRUpdateEmailContent(
              EmailTemplate.OKR_UPDATE, user.getFullName(),
              objective.getContent(), updateContent,
              host + OKR_URL_PREFIX + objectiveIdString + okrSuffix + ownerIdString + "&urlCode=" + urlCode,
              org.getShortName(), user.getEmailAddress());
      rabbitMQProducer.sendMessage(messageJson);
    }
  }

  private void sendEmailForAddOkrComment(
          long orgId, List<Long> receivers, Objective objective, OkrComment okrComment, long actorUserId) {
    Org org = orgService.getOrg(orgId);
    CoreUserProfile actor = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, actorUserId);
    for (Long userId : receivers) {
      if (userId == actorUserId) {
        continue;
      }
      RemindSetting remindSetting = remindSettingService.getRemindSettingByUserIdAndRemindType(orgId,
              userId, RemindType.TEAM_OKR_UPDATE.getCode());
      if (remindSetting.getStatus() == 0) {
        return;
      }
      CoreUserProfile user = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
      String objectiveIdString = getEncryptString(Long.toString(objective.getObjectiveId()));
      String ownerIdString = getEncryptString(Long.toString(objective.getOwnerId()));

      if (objectiveIdString == null || ownerIdString == null) {
        continue;
      }

      String urlSuffix = OKR_URL_PREFIX + objectiveIdString + "?type=" + objective.getType();
      if (objective.getType().intValue() == OkrType.ORG.getCode()) {
      } else if (objective.getType().intValue() == OkrType.TEAM.getCode()) {
        urlSuffix = urlSuffix + "&teamId=" + ownerIdString;
      } else if (objective.getType().intValue() == OkrType.PERSON.getCode()) {
        urlSuffix = urlSuffix + "&userId=" + ownerIdString;
      } else if (objective.getType().intValue() == OkrType.PROJECT_TEAM.getCode()) {
        urlSuffix = urlSuffix + "&projectTeamId=" + ownerIdString;
      }

      String urlCode = null;
      try {
        urlCode = URLEncoder.encode("/#" + urlSuffix, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        LOGGER.error(e.toString());
      }
      String url = host + urlSuffix + "&urlCode=" + urlCode;

      emailTemplateHelper.preSendOkrAddNote(EmailTemplate.OKR_ADD_NOTE, actor.getFullName(),
              getOkrType(objective.getType()), objective.getContent(),
              user.getFullName(), okrComment.getContent(), url,
              org.getShortName(), user.getEmailAddress());
    }
  }

  private String getOkrType(int type) {
    if (type == OkrType.ORG.getCode()) {
      return "公司";
    } else if (type == OkrType.TEAM.getCode()) {
      return "团队";
    } else if (type == OkrType.PROJECT_TEAM.getCode()) {
      return "个人";
    } else {
      return "项目组";
    }
  }

  private String getEncryptString(String source) {
    String result = null;
    try {
      result = EncryptUtils.symmetricEncrypt(source);
    } catch (Exception e) {
      LOGGER.error("OkrNotificationTask-getEncryptString-error():" + e);
    }

    if (result == null) {
      return result;
    } else {
      return result.toUpperCase();
    }
  }

  private List<Long> getUserIdsFromDirectors(List<Director> directors) {
    List<Long> result = new ArrayList<>();
    for (Director director : directors) {
      result.add(director.getUserId());
    }
    return result;
  }

  private String getNameString(List<CoreUserProfile> userProfiles) {
    StringBuffer s = new StringBuffer();
    if (CollectionUtils.isEmpty(userProfiles)) {
      s.append("无");
    } else {
      for (CoreUserProfile coreUserProfile : userProfiles) {
        s.append(coreUserProfile.getFullName());
        s.append(",");
      }
      s.deleteCharAt(s.length() - 1);
    }
    return s.toString();
  }
}
