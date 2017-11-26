package hr.wozai.service.user.server.component;

import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.client.utils.EmailTemplateHelper;
import hr.wozai.service.thirdparty.client.utils.RabbitMQProducer;
import hr.wozai.service.thirdparty.client.utils.SqsProducer;
import hr.wozai.service.user.client.okr.dto.DirectorDTO;
import hr.wozai.service.user.client.okr.dto.ObjectiveDTO;
import hr.wozai.service.user.client.okr.dto.ObjectiveListDTO;
import hr.wozai.service.user.client.okr.enums.DirectorType;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.client.okr.enums.PeriodTimeSpan;
import hr.wozai.service.user.client.okr.enums.RegularRemindType;
import hr.wozai.service.user.client.okr.facade.OkrFacade;
import hr.wozai.service.user.server.dao.okr.ObjectiveDao;
import hr.wozai.service.user.server.dao.okr.ObjectivePeriodDao;
import hr.wozai.service.user.server.dao.okr.OkrRemindSettingDao;
import hr.wozai.service.user.server.dao.userorg.OrgDao;
import hr.wozai.service.user.server.enums.OkrRemindType;
import hr.wozai.service.user.server.helper.CalcProgressHelper;
import hr.wozai.service.user.server.model.okr.*;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.service.OkrService;
import hr.wozai.service.user.server.service.SecurityModelService;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.service.UserProfileService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
public class OkrNotificationTask {
  private static final Logger LOGGER = LoggerFactory.getLogger(OkrNotificationTask.class);

  private static final long ONE_DAY = 3600 * 24 * 1000;
  private static final String OKR_URL_PREFIX = "/okr/objectives/";
  private static final String OKR_URL_SUFFIX = "?type=";

  private static final String OKR_PERIOD_URL_PREFIX = "/okr/";
  private static final String OKR_PERIOD_URL_SUFFIX = "/objectives?";

  private static final String HASH_PRFIX = "/#";

  @Autowired
  OkrFacade okrFacade;

  @Autowired
  OrgDao orgDao;

  @Autowired
  OkrRemindSettingDao okrRemindSettingDao;

  @Autowired
  ObjectivePeriodDao objectivePeriodDao;

  @Autowired
  ObjectiveDao objectiveDao;

/*  @Autowired
  SqsProducer sqsProducer;*/

  @Autowired
  RabbitMQProducer rabbitMQProducer;

  @Autowired
  SecurityModelService securityModelService;

  @Autowired
  UserProfileService userProfileService;

  @Autowired
  TeamService teamService;

  @Autowired
  OkrService okrService;

  @Autowired
  EmailTemplateHelper emailTemplateHelper;

  @Value("${okr.url.host}")
  private String host;

  /**
   * 目标截止日提醒
   */
  @Scheduled(cron="0 01 13 * * ? ")
  public void sendObjectiveDeadlineNotification() {
    LOGGER.info("sendObjectiveDeadlineNotification begin");
    List<Org> orgList = orgDao.listAllOrgs();
    OkrRemindSetting okrRemindSetting;
    for (Org org : orgList) {
      okrRemindSetting = okrRemindSettingDao.getOkrRemindSettingByOrgIdAndRemindType(
              org.getOrgId(), OkrRemindType.OBJECTIVE_DEADLINE.getCode());
      int daysBeforeObjectiveDeadline = (okrRemindSetting != null)?okrRemindSetting.getFrequency():
              OkrRemindType.OBJECTIVE_DEADLINE.getDefaultFrequency();
      long today = TimeUtils.getTimestampOfZeroOclockToday(TimeUtils.BEIJING);
      long startDeadline = today + daysBeforeObjectiveDeadline * ONE_DAY;
      long endDeadline = today + (daysBeforeObjectiveDeadline + 1) * ONE_DAY;

      ObjectiveListDTO objectiveListDTO = okrFacade.listObjectivesByStartAndEndDeadline(
              org.getOrgId(), startDeadline, endDeadline);
      if (!CollectionUtils.isEmpty(objectiveListDTO.getObjectiveDTOList())) {
        for (ObjectiveDTO objectiveDTO : objectiveListDTO.getObjectiveDTOList()) {
          // send email
          List<DirectorDTO> directorDTOs = objectiveDTO.getDirectorDTOList();
          if (!CollectionUtils.isEmpty(directorDTOs)) {
            String objectiveIdString = getEncryptString(Long.toString(objectiveDTO.getObjectiveId()));
            String ownerIdString = getEncryptString(Long.toString(objectiveDTO.getOwnerId()));

            if (objectiveIdString == null || ownerIdString == null) {
              continue;
            }

            String urlSuffix = OKR_URL_PREFIX + objectiveIdString + OKR_URL_SUFFIX + objectiveDTO.getType();
            if (objectiveDTO.getType().intValue() == OkrType.ORG.getCode()) {
            } else if (objectiveDTO.getType().intValue() == OkrType.TEAM.getCode()) {
              urlSuffix = urlSuffix + "&teamId=" + ownerIdString;
            } else if (objectiveDTO.getType().intValue() == OkrType.PERSON.getCode()) {
              urlSuffix = urlSuffix + "&userId=" + ownerIdString;
            } else if (objectiveDTO.getType().intValue() == OkrType.PROJECT_TEAM.getCode()) {
              urlSuffix = urlSuffix + "&projectTeamId=" + ownerIdString;
            }

            String urlCode = null;
            try {
              urlCode = URLEncoder.encode(HASH_PRFIX + urlSuffix, "UTF-8");
            } catch (UnsupportedEncodingException e) {
              LOGGER.error(e.toString());
            }
            String url = host + urlSuffix + "&urlCode=" + urlCode;
            for(DirectorDTO directorDTO : directorDTOs) {
              String messageJson = EmailTemplate.getOKRDeadlineReminderEmailContent(
                      EmailTemplate.OKR_OBJECTIVE_DEADLINE_REMINDER, directorDTO.getCoreUserProfileDTO().getFullName(),
                      objectiveDTO.getContent(), transferProgress(objectiveDTO.getProgress()),
                      String.valueOf(daysBeforeObjectiveDeadline),
                      TimeUtils.formatDateWithTimeZone(objectiveDTO.getDeadline(), TimeUtils.BEIJING), url, org.getShortName(),
                      directorDTO.getCoreUserProfileDTO().getEmailAddress());
              LOGGER.info("sendObjectiveDeadlineNotification send email:" + messageJson);
              rabbitMQProducer.sendMessage(messageJson);
            }
          }
        }
      }
    }
    LOGGER.info("sendObjectiveDeadlineNotification finish");
  }

  /**
   * 关键结果截止日提醒
   */
  @Scheduled(cron="0 50 14 * * ? ")
  public void sendKeyResultDeadlineNotification() {
    LOGGER.info("sendKeyResultDeadlineNotification begin");
    List<Org> orgList = orgDao.listAllOrgs();
    OkrRemindSetting okrRemindSetting;
    for (Org org : orgList) {
      okrRemindSetting = okrRemindSettingDao.getOkrRemindSettingByOrgIdAndRemindType(
              org.getOrgId(), OkrRemindType.KEY_RESULT_DEADLINE.getCode());
      int daysBeforeObjectiveDeadline = (okrRemindSetting != null)?okrRemindSetting.getFrequency():
              OkrRemindType.KEY_RESULT_DEADLINE.getDefaultFrequency();
      long today = TimeUtils.getTimestampOfZeroOclockToday(TimeUtils.BEIJING);
      long startDeadline = today + daysBeforeObjectiveDeadline * ONE_DAY;
      long endDeadline = today + (daysBeforeObjectiveDeadline + 1) * ONE_DAY;

      List<KeyResult> keyResults = okrService.listKeyResultsByStartAndEndDeadline(
              org.getOrgId(), startDeadline, endDeadline);

      if (!CollectionUtils.isEmpty(keyResults)) {
        List<Long> keyResultIds = new ArrayList<>();
        List<Long> objectiveIds = new ArrayList<>();
        for (KeyResult keyResult : keyResults) {
          keyResultIds.add(keyResult.getKeyResultId());
          objectiveIds.add(keyResult.getObjectiveId());
        }
        List<Director> directors = okrService.listDirectorsByObjectiveIds(
                org.getOrgId(), DirectorType.KEYRESULT.getCode(), keyResultIds);
        List<Objective> objectives = okrService.listObjectivesByObjectiveIds(org.getOrgId(), objectiveIds);
        Map<Long, List<Director>> directorMap = transferDirectorListToMap(directors);
        Map<Long, CoreUserProfile> coreUserProfileMap = transferDirectorListToCoreUserProfileMap(org.getOrgId(), directors);
        Map<Long, Objective> objectiveMap = transferObjectiveListToMap(objectives);

        for (KeyResult keyResult : keyResults) {
          Objective objective;
          if (objectiveMap.containsKey(keyResult.getObjectiveId())) {
            objective = objectiveMap.get(keyResult.getObjectiveId());
          } else {
            continue;
          }

          if (directorMap.containsKey(keyResult.getKeyResultId())) {
            String objectiveIdString = getEncryptString(Long.toString(objective.getObjectiveId()));
            String ownerIdString = getEncryptString(Long.toString(objective.getOwnerId()));

            if (objectiveIdString == null || ownerIdString == null) {
              continue;
            }

            String urlSuffix = OKR_URL_PREFIX + objectiveIdString + OKR_URL_SUFFIX + objective.getType();

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
              urlCode = URLEncoder.encode(HASH_PRFIX + urlSuffix, "UTF-8");
            } catch (UnsupportedEncodingException e) {
              LOGGER.error(e.toString());
            }
            String url = host + urlSuffix + "&urlCode=" + urlCode;
            directors = directorMap.get(keyResult.getKeyResultId());
            for (Director director : directors) {
              if (coreUserProfileMap.containsKey(director.getUserId())) {
                CoreUserProfile coreUserProfile = coreUserProfileMap.get(director.getUserId());
                String progress = CalcProgressHelper.calcProgressByDifferentAmount(
                      String.valueOf(keyResult.getStartingAmount()),
                      String.valueOf(keyResult.getGoalAmount()),
                      String.valueOf(keyResult.getCurrentAmount()));
                // send email
                LOGGER.info("sendKeyResultDeadlineNotification send email:" + keyResult);
                emailTemplateHelper.preSendKeyResultDeadlineReminder(
                        EmailTemplate.OKR_KEYRESULT_DEADLINE_REMINDER, keyResult.getContent(),
                        String.valueOf(daysBeforeObjectiveDeadline),
                        coreUserProfile.getFullName(),
                        objective.getContent(),
                        TimeUtils.formatDateWithTimeZone(keyResult.getDeadline(), TimeUtils.BEIJING),
                        transferProgress(progress),
                        url, org.getShortName(),
                        coreUserProfile.getEmailAddress());
              }
            }
          }
        }
      }
    }
    LOGGER.info("sendKeyResultDeadlineNotification finish");
  }

  private Map<Long, Objective> transferObjectiveListToMap(List<Objective> objectives) {
    Map<Long, Objective> result = new HashMap<>();
    for (Objective objective : objectives) {
      result.put(objective.getObjectiveId(), objective);
    }
    return result;
  }

  private Map<Long, List<Director>> transferDirectorListToMap(List<Director> directors) {
    Map<Long, List<Director>> result = new HashMap<>();

    for (Director director : directors) {
      long keyResultId = director.getObjectId();
      if (result.containsKey(keyResultId)) {
        List<Director> inMap = result.get(keyResultId);
        inMap.add(director);
        result.put(keyResultId, inMap);
      } else {
        List<Director> newOne = new ArrayList<>();
        newOne.add(director);
        result.put(keyResultId, newOne);
      }
    }
    return result;
  }

  private Map<Long, CoreUserProfile> transferDirectorListToCoreUserProfileMap(long orgId, List<Director> directors) {
    Map<Long, CoreUserProfile> result = new HashMap<>();
    List<Long> userIds = new ArrayList<>();
    for (Director director : directors) {
      userIds.add(director.getUserId());
    }
    List<CoreUserProfile> coreUserProfiles = new ArrayList<>();
    if (!CollectionUtils.isEmpty(userIds)) {
      coreUserProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId, userIds);
    }
    for (CoreUserProfile coreUserProfile : coreUserProfiles) {
      result.put(coreUserProfile.getUserId(), coreUserProfile);
    }
    return result;
  }

  @Scheduled(cron="0 01 14 * * ? ")
  public void sendPeriodDeadlineNotification() {
    LOGGER.info("sendPeriodDeadlineNotification begin");
    List<Org> orgList = orgDao.listAllOrgs();
    OkrRemindSetting okrRemindSetting;

    for (Org org : orgList) {
      Map<Long, Long> userIdTeamIdMap = new HashMap<>();
      List<CoreUserProfile> orgAdmins = getOrgAdminsByOrgId(org.getOrgId(), userIdTeamIdMap);
      Map<Long, CoreUserProfile> userProfileMap = new HashMap<>();
      Map<Long, List<CoreUserProfile>> teamMemberMap = new HashMap<>();
      okrRemindSetting = okrRemindSettingDao.getOkrRemindSettingByOrgIdAndRemindType(
              org.getOrgId(), OkrRemindType.OBJECTIVE_PERIOD_DEADLINE.getCode());
      int daysBeforeObjectivePeriodDeadline = (okrRemindSetting != null) ? okrRemindSetting.getFrequency() :
              OkrRemindType.OBJECTIVE_PERIOD_DEADLINE.getDefaultFrequency();

      long timestamp = TimeUtils.getTimestampOfZeroOclockToday(TimeUtils.BEIJING) +
              daysBeforeObjectivePeriodDeadline * ONE_DAY; // 到期的时间戳
      int year = TimeUtils.getYearFromTimestamp(timestamp, TimeUtils.BEIJING); // 到期的年份
      int month = TimeUtils.getMonthFromTimestamp(timestamp, TimeUtils.BEIJING); // 到期的月份
      // 如果是当月最后一天
      if (isLastDayOfMonth(timestamp)) {
        List<Integer> currentPeriodSpanIds = PeriodTimeSpan.getPeriodTimeSpanIdListByMonth(month);
        List<ObjectivePeriod> periods = objectivePeriodDao.listObjectivePeriodsByOrgIdAndYearAndPeriodSpanIds(
                org.getOrgId(), year, currentPeriodSpanIds);
        for (ObjectivePeriod objectivePeriod : periods) {
          int newYear = PeriodTimeSpan.getYearByCurrentYearAndPeriodTimeSpan(
                  objectivePeriod.getYear(), PeriodTimeSpan.getEnumByCode(objectivePeriod.getPeriodTimeSpanId()));
          PeriodTimeSpan nextSpan = PeriodTimeSpan.getNextPeriodTimeSpan(
                  PeriodTimeSpan.getEnumByCode(objectivePeriod.getPeriodTimeSpanId()));
          String name = PeriodTimeSpan.getNameByYearAndPeriodTimeSpan(newYear, nextSpan);
          ObjectivePeriod nextPeriod = objectivePeriodDao.findObjectivePeriodByName(
                  org.getOrgId(), name, objectivePeriod.getType(), objectivePeriod.getOwnerId());


          if (nextPeriod == null) {
            // send email
            int type = objectivePeriod.getType();
            if (type == OkrType.ORG.getCode()) {
              // 发给公司管理员
              sendPeriodDeadlineEmailToOrgAdmins(
                      org, orgAdmins, userIdTeamIdMap, daysBeforeObjectivePeriodDeadline, objectivePeriod);
            } else if (type == OkrType.TEAM.getCode()) {
              // 发给team成员
              sendPeriodDeadlineEmailToTeamMember(
                      org, userProfileMap, teamMemberMap, userIdTeamIdMap,
                      daysBeforeObjectivePeriodDeadline, objectivePeriod);
            } else if (type == OkrType.PERSON.getCode()) {
              // 发给个人
              sendPeriodDeadlineEmailToOneUser(
                      org, userProfileMap, userIdTeamIdMap, daysBeforeObjectivePeriodDeadline, objectivePeriod);
            } else if (type == OkrType.PROJECT_TEAM.getCode()) {
              // TODO: 发给项目组成员
              sendPeriodDeadlineEmailToProjectTeamMember(
                      org, userProfileMap, daysBeforeObjectivePeriodDeadline, objectivePeriod);
            }
          }
        }
      }
    }
    LOGGER.info("sendPeriodDeadlineNotification finish");
  }

  private void sendPeriodDeadlineEmailToTeamMember(
          Org org, Map<Long, CoreUserProfile> userProfileMap, Map<Long,
          List<CoreUserProfile>> teamMemberMap, Map<Long, Long> userIdTeamIdMap,
          int daysBeforeObjectivePeriodDeadline, ObjectivePeriod objectivePeriod) {
    long teamId = objectivePeriod.getOwnerId();
    List<Long> userIds = teamService.getUserIdsByOrgIdAndTeamIds(
            org.getOrgId(), Arrays.asList(teamId), 1, Integer.MAX_VALUE);
    List<CoreUserProfile> coreUserProfiles;
    if (teamMemberMap.containsKey(teamId)) {
      coreUserProfiles = teamMemberMap.get(teamId);
    } else {
      coreUserProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(org.getOrgId(), userIds);
      teamMemberMap.put(teamId, coreUserProfiles);
    }
    String periodIdString = getEncryptString(Long.toString(objectivePeriod.getObjectivePeriodId()));
    String encryptTeamId = this.getEncryptString(Long.toString(teamId));
    String url;
    if (encryptTeamId != null) {
      String urlSuffix = OKR_PERIOD_URL_PREFIX + periodIdString + OKR_PERIOD_URL_SUFFIX + "teamId=" + encryptTeamId
              + "&type=2";
      String urlCode = null;
      try {
        urlCode = URLEncoder.encode(HASH_PRFIX + urlSuffix, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        LOGGER.error(e.toString());
      }
      url = host + urlSuffix + "&urlCode=" + urlCode;
    } else {
      return;
    }
    for (CoreUserProfile coreUserProfile : coreUserProfiles) {
      userProfileMap.put(coreUserProfile.getUserId(), coreUserProfile);
      userIdTeamIdMap.put(coreUserProfile.getUserId(), teamId);
      String messageJson = EmailTemplate.getOKRPeriodDeadlineReminderEmailContent(
              EmailTemplate.OKR_PERIOD_DEADLINE_REMINDER, coreUserProfile.getFullName(), "团队",
              objectivePeriod.getName(), String.valueOf(daysBeforeObjectivePeriodDeadline),
              url, org.getShortName(), coreUserProfile.getEmailAddress());
      LOGGER.info("sendPeriodDeadlineNotification send email:" + messageJson);
      rabbitMQProducer.sendMessage(messageJson);
    }
  }

  private void sendPeriodDeadlineEmailToProjectTeamMember(
          Org org, Map<Long, CoreUserProfile> userProfileMap,
          int daysBeforeObjectivePeriodDeadline, ObjectivePeriod objectivePeriod) {
    long projectTeamId = objectivePeriod.getOwnerId();
    List<Long> userIds = teamService.listUserIdsByOrgIdAndProjectTeamId(org.getOrgId(), projectTeamId);
    List<CoreUserProfile> coreUserProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(org.getOrgId(), userIds);

    String periodIdString = getEncryptString(Long.toString(objectivePeriod.getObjectivePeriodId()));
    String encryptProjectTeamId = this.getEncryptString(Long.toString(projectTeamId));
    String url;
    if (encryptProjectTeamId != null) {
      String urlSuffix = OKR_PERIOD_URL_PREFIX + periodIdString + OKR_PERIOD_URL_SUFFIX + "projectTeamId=" + encryptProjectTeamId
              + "&type=4";
      String urlCode = null;
      try {
        urlCode = URLEncoder.encode(HASH_PRFIX + urlSuffix, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        LOGGER.error(e.toString());
      }
      url = host + urlSuffix + "&urlCode=" + urlCode;
    } else {
      return;
    }
    for (CoreUserProfile coreUserProfile : coreUserProfiles) {
      userProfileMap.put(coreUserProfile.getUserId(), coreUserProfile);
      String messageJson = EmailTemplate.getOKRPeriodDeadlineReminderEmailContent(
              EmailTemplate.OKR_PERIOD_DEADLINE_REMINDER, coreUserProfile.getFullName(), "项目组",
              objectivePeriod.getName(), String.valueOf(daysBeforeObjectivePeriodDeadline),
              url, org.getShortName(), coreUserProfile.getEmailAddress());
      LOGGER.info("sendPeriodDeadlineNotification send email:" + messageJson);
      rabbitMQProducer.sendMessage(messageJson);
    }
  }

  private void sendPeriodDeadlineEmailToOneUser(
          Org org, Map<Long, CoreUserProfile> userProfileMap, Map<Long, Long> userIdTeamIdMap,
          int daysBeforeObjectivePeriodDeadline, ObjectivePeriod objectivePeriod) {
    long userId = objectivePeriod.getOwnerId();
    CoreUserProfile coreUserProfile;
    if (userProfileMap.containsKey(userId)) {
      coreUserProfile = userProfileMap.get(userId);
    } else {
      coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(org.getOrgId(), userId);
      userProfileMap.put(userId, coreUserProfile);
    }
    long teamId;
    if (userIdTeamIdMap.containsKey(userId)) {
      teamId = userIdTeamIdMap.get(userId);
    } else {
      teamId = teamService.getTeamMemberByUserIdAndOrgId(org.getOrgId(), userId).getTeamId();
      userIdTeamIdMap.put(userId, teamId);
    }
    String periodIdString = getEncryptString(Long.toString(objectivePeriod.getObjectivePeriodId()));
    String encryptTeamId = getEncryptString(Long.toString(teamId));
    String encryptUserId = getEncryptString(Long.toString(userId));
    String url;
    if (periodIdString != null && encryptTeamId != null) {
      String urlSuffix = OKR_PERIOD_URL_PREFIX + periodIdString + OKR_PERIOD_URL_SUFFIX + "teamId=" + encryptTeamId +
              "&userId=" + encryptUserId + "&type=3";
      String urlCode = null;
      try {
        urlCode = URLEncoder.encode(HASH_PRFIX + urlSuffix, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        LOGGER.error(e.toString());
      }
      url = host + urlSuffix + "&urlCode=" + urlCode;
    } else {
      return;
    }
    String messageJson = EmailTemplate.getOKRPeriodDeadlineReminderEmailContent(
            EmailTemplate.OKR_PERIOD_DEADLINE_REMINDER, coreUserProfile.getFullName(), "个人",
            objectivePeriod.getName(), String.valueOf(daysBeforeObjectivePeriodDeadline),
            url, org.getShortName(), coreUserProfile.getEmailAddress());
    LOGGER.info("sendPeriodDeadlineNotification send email:" + messageJson);
    rabbitMQProducer.sendMessage(messageJson);
  }

  private void sendPeriodDeadlineEmailToOrgAdmins(
          Org org, List<CoreUserProfile> orgAdmins, Map<Long, Long> userIdTeamIdMap,
          int daysBeforeObjectivePeriodDeadline, ObjectivePeriod objectivePeriod) {
    String periodIdString = getEncryptString(Long.toString(objectivePeriod.getObjectivePeriodId()));
    for (CoreUserProfile coreUserProfile : orgAdmins) {
      String teamId;
      if (userIdTeamIdMap.containsKey(coreUserProfile.getUserId())) {
        teamId = getEncryptString(Long.toString(userIdTeamIdMap.get(coreUserProfile.getUserId())));
      } else {
        continue;
      }
      String url;
      if (periodIdString != null && teamId != null) {
        String urlSuffix = OKR_PERIOD_URL_PREFIX + periodIdString + OKR_PERIOD_URL_SUFFIX + "teamId=" + teamId + "&type=1";
        String urlCode = null;
        try {
          urlCode = URLEncoder.encode(HASH_PRFIX + urlSuffix, "UTF-8");
        } catch (UnsupportedEncodingException e) {
          LOGGER.error(e.toString());
        }
        url = host + urlSuffix + "&urlCode=" + urlCode;
      } else {
        continue;
      }
      String messageJson = EmailTemplate.getOKRPeriodDeadlineReminderEmailContent(
              EmailTemplate.OKR_PERIOD_DEADLINE_REMINDER, coreUserProfile.getFullName(), "公司",
              objectivePeriod.getName(), String.valueOf(daysBeforeObjectivePeriodDeadline),
              url, org.getShortName(), coreUserProfile.getEmailAddress());
      LOGGER.info("sendPeriodDeadlineNotification send email:" + messageJson);
      rabbitMQProducer.sendMessage(messageJson);
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

  private List<CoreUserProfile> getOrgAdminsByOrgId(
          long orgId, Map<Long, Long> userIdTeamIdMap) {
    List<Long> orgAdminUserIds = securityModelService.listOrgAdminUserIdByOrgId(orgId);
    List<CoreUserProfile> orgAdmins = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId, orgAdminUserIds);
    for (Long userId : orgAdminUserIds) {
      Long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId).getTeamId();
      userIdTeamIdMap.put(userId, teamId);
    }
    return orgAdmins;
  }

  private boolean isLastDayOfMonth(long timestamp) {
    int year = TimeUtils.getYearFromTimestamp(timestamp, TimeUtils.BEIJING);
    int month = TimeUtils.getMonthFromTimestamp(timestamp, TimeUtils.BEIJING);

    long lastDayTs = TimeUtils.getLastDayOfMonth(year, month, TimeUtils.BEIJING) - ONE_DAY;
    return timestamp == lastDayTs;
  }

  @Scheduled(cron="0 01 15 * * ? ")
  public void sendObjectiveUpdateNotification() {
    LOGGER.info("sendObjectiveUpdateNotification begin");
    List<Org> orgList = orgDao.listAllOrgs();
    for (Org org : orgList) {
      Map<Long, CoreUserProfile> userIdMap = new HashMap<>();
      Map<Long, ObjectivePeriod> periodMap = new HashMap<>();
      List<Objective> objectives = objectiveDao.listObjectivesWithRegularRemindType(org.getOrgId());
      List<Objective> needRemind = new ArrayList<>();
      for (Objective objective : objectives) {
        long today = TimeUtils.getTimestampOfZeroOclockToday(TimeUtils.BEIJING);
        long lastUpdateTime = TimeUtils.getTimestampOfZeroOclockTodayOfInputTimestamp(
                objective.getLastModifiedTime(), TimeUtils.BEIJING);
        RegularRemindType regularRemindType = RegularRemindType.getEnumByCode(objective.getRegularRemindType());
        if ((today - lastUpdateTime) % (ONE_DAY * regularRemindType.getDays()) == 0) {
          needRemind.add(objective);
        }
      }
      for (Objective objective : needRemind) {
        ObjectivePeriod objectivePeriod;
        if (periodMap.containsKey(objective.getObjectivePeriodId())) {
          objectivePeriod = periodMap.get(objective.getObjectivePeriodId());
        } else {
          objectivePeriod = okrService.getObjectivePeriod(org.getOrgId(), objective.getObjectivePeriodId());
          periodMap.put(objective.getObjectivePeriodId(), objectivePeriod);
        }
        long daysBeforePeriodDeadline = getPeriodDeadlineDays(objectivePeriod);
        if (daysBeforePeriodDeadline < 0) {
          continue;
        }
        List<Director> directors = okrService.listDirector(
                org.getOrgId(), DirectorType.OBJECTIVE.getCode(), objective.getObjectiveId());
        if (!CollectionUtils.isEmpty(directors)) {
          String url = getObjectiveUrl(objective);
          if (url == null) {
            continue;
          }
          for (Director director : directors) {
            CoreUserProfile coreUserProfile;
            if (userIdMap.containsKey(director.getUserId())) {
              coreUserProfile = userIdMap.get(director.getUserId());
            } else {
              coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(
                      org.getOrgId(), director.getUserId());
              userIdMap.put(director.getUserId(), coreUserProfile);
            }
            String content = getUpdateContent(objective, objectivePeriod);
            String messageJson = EmailTemplate.getOKRRegularReminderEmailContent(
                    EmailTemplate.OKR_REGULAR_REMINDER, coreUserProfile.getFullName(),
                    objective.getContent(), content, url, org.getShortName(),
                    coreUserProfile.getEmailAddress());
            LOGGER.info("sendObjectiveUpdateNotification send email:" + messageJson);
            rabbitMQProducer.sendMessage(messageJson);
          }
        }
      }
    }
    LOGGER.info("sendObjectiveUpdateNotification finish");
  }

  private String getUpdateContent(Objective objective, ObjectivePeriod objectivePeriod) {
    long daysBeforePeriodDeadline = getPeriodDeadlineDays(objectivePeriod);
    StringBuffer content = new StringBuffer();
    content.append("该目标的周期为「"+objectivePeriod.getName()+"」，离周期结束还剩 "
            + daysBeforePeriodDeadline + " 天。");
    long today = TimeUtils.getTimestampOfZeroOclockToday(TimeUtils.BEIJING);
    if (objective.getDeadline() != null && objective.getDeadline() != 0) {
      long daysBeforeObjectiveDeadline = (objective.getDeadline() - today) / ONE_DAY;
      if (daysBeforeObjectiveDeadline < 0) {
        daysBeforeObjectiveDeadline = 0;
      }
      content.append("<br/>");
      String date = TimeUtils.formatDateWithTimeZone(objective.getDeadline(), TimeUtils.BEIJING);
      content.append("目标截止日为「" + date + "」，还剩 " + daysBeforeObjectiveDeadline + "天。");
    }
    return content.toString();
  }

  private long getPeriodDeadlineDays(ObjectivePeriod objectivePeriod) {
    long today = TimeUtils.getTimestampOfZeroOclockToday(TimeUtils.BEIJING);
    int year = objectivePeriod.getYear();
    PeriodTimeSpan periodTimeSpan = PeriodTimeSpan.getEnumByCode(objectivePeriod.getPeriodTimeSpanId());
    long periodDeadline = TimeUtils.getLastDayOfMonth(year, periodTimeSpan.getEndMonth(), TimeUtils.BEIJING);
    long daysBeforePeriodDeadline = (periodDeadline - today) / ONE_DAY;
    return daysBeforePeriodDeadline;
  }

  private String getObjectiveUrl(Objective objective) {
    String objectiveIdString = getEncryptString(Long.toString(objective.getObjectiveId()));
    String ownerIdString = getEncryptString(Long.toString(objective.getOwnerId()));

    if (objectiveIdString == null || ownerIdString == null) {
      return null;
    }
    String urlSuffix = OKR_URL_PREFIX + objectiveIdString + OKR_URL_SUFFIX + objective.getType();
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
      urlCode = URLEncoder.encode(HASH_PRFIX + urlSuffix, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      LOGGER.error(e.toString());
    }
    String url = host + urlSuffix + "&urlCode=" + urlCode;
    return url;
  }

  private static String transferProgress(String progress) {
    BigDecimal bigdec = new BigDecimal(progress).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
    return bigdec + "%";
  }

  public static void main(String[] args) {
    BigDecimal bigdec = new BigDecimal("0.556").multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_UP);
    System.out.println(bigdec + "%");
  }
}
