// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.thirdparty.client.utils.RabbitMQProducer;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.StatusType;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.enums.UserSysNotificationType;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.thirdparty.client.utils.SqsProducer;
import hr.wozai.service.user.client.conversation.enums.PeriodType;
import hr.wozai.service.user.client.conversation.enums.RemindDay;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.client.userorg.enums.ContentIndexType;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.server.dao.userorg.JobTransferDao;
import hr.wozai.service.user.server.dao.userorg.StatusUpdateDao;
import hr.wozai.service.user.server.dao.userorg.UserSysNotificationDao;
import hr.wozai.service.user.server.helper.JobTransferHelper;
import hr.wozai.service.user.server.helper.StatusUpdateHelper;
import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import hr.wozai.service.user.server.model.okr.ObjectivePeriod;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.model.userorg.JobTransfer;
import hr.wozai.service.user.server.model.userorg.StatusUpdate;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.model.userorg.UserSysNotification;
import hr.wozai.service.user.server.service.ConvrScheduleService;
import hr.wozai.service.user.server.service.EmployeeManagementService;
import hr.wozai.service.user.server.service.NameIndexService;
import hr.wozai.service.user.server.service.OkrService;
import hr.wozai.service.user.server.service.OnboardingTemplateService;
import hr.wozai.service.user.server.service.OrgService;
import hr.wozai.service.user.server.service.ProfileFieldService;
import hr.wozai.service.user.server.service.ProfileTemplateService;
import hr.wozai.service.user.server.service.RemindSettingService;
import hr.wozai.service.user.server.service.SecurityModelService;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.service.TokenService;
import hr.wozai.service.user.server.service.UserEmploymentService;
import hr.wozai.service.user.server.service.UserProfileService;
import hr.wozai.service.user.server.service.UserService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-10
 */
@Service("employManagementService")
public class EmployeeManagementServiceImpl implements EmployeeManagementService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeManagementServiceImpl.class);

  @Autowired
  JobTransferDao jobTransferDao;

  @Autowired
  StatusUpdateDao statusUpdateDao;

  @Autowired
  UserSysNotificationDao userSysNotificationDao;

  @Autowired
  TokenService tokenService;

/*  @Autowired
  SqsProducer sqsProducer;*/

  @Autowired
  RabbitMQProducer rabbitMQProducer;

  @Autowired
  ProfileTemplateService profileTemplateService;

  @Autowired
  ProfileFieldService profileFieldService;

  @Autowired
  OnboardingTemplateService onboardingTemplateService;

  @Autowired
  UserProfileService userProfileService;

  @Autowired
  UserService userService;

  @Autowired
  TeamService teamService;

  @Autowired
  OrgService orgService;

  @Autowired
  SecurityModelService securityModelService;

  @Autowired
  NameIndexService nameIndexService;

  @Autowired
  UserEmploymentService userEmploymentService;

  @Autowired
  RemindSettingService remindSettingService;

  @Autowired
  OkrService okrService;

  @Autowired
  ConvrScheduleService convrScheduleService;

  /**
   * Steps:
   *  1) add jobTransfer
   *  2) update userProfile
   *  3) update team
   *  4) update reportLine
   *  5) handle notification
   *
   * @param jobTransfer
   * @return
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addJobTransfer(JobTransfer jobTransfer) {

    if (!JobTransferHelper.isValidJobTransferUponAdd(jobTransfer)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    // 1)
    jobTransferDao.insertJobTransfer(jobTransfer);

    // 2)
    long orgId = jobTransfer.getOrgId();
    long userId = jobTransfer.getUserId();
    long actorUserId = jobTransfer.getCreatedUserId();
    Map<String, String> fieldValues = new HashMap<>();
    if (null != jobTransfer.getAfterJobTitleId()) {
      fieldValues.put(SystemProfileField.JOB_TITLE.getReferenceName(),
              String.valueOf(jobTransfer.getAfterJobTitleId()));
    }
    if (null != jobTransfer.getAfterJobLevelId()) {
      fieldValues.put(SystemProfileField.JOB_LEVEL.getReferenceName(),
              String.valueOf(jobTransfer.getAfterJobLevelId()));
    }
    userProfileService.updateUserProfileField(orgId, userId, fieldValues, actorUserId);

    // 3)
    long afterTeamId = jobTransfer.getAfterTeamId();
    teamService.batchUpdateTeamAndTeamMember(orgId, null, Arrays.asList(userId), afterTeamId, actorUserId);

    // 4)
    long afterReporterId = jobTransfer.getAfterReporterId();
    userService.batchUpdateReportLine(orgId, Arrays.asList(userId), afterReporterId, actorUserId);
    if (afterReporterId != 0
        && userId != 0) {
      List<Long> existedTargetUserIds = convrScheduleService.listTargetUserIdBySourceUserId(afterReporterId, orgId);
      // add convrSchedule if not exists
      if (!existedTargetUserIds.contains(userId)) {
        ConvrSchedule convrSchedule = new ConvrSchedule();
        convrSchedule.setOrgId(orgId);
        convrSchedule.setSourceUserId(afterReporterId);
        convrSchedule.setTargetUserId(userId);
        convrSchedule.setPeriodType(PeriodType.HALF_MONTH.getCode());
        convrSchedule.setRemindDay(RemindDay.MONDAY.getCode());
        convrSchedule.setIsActive(1);
        convrSchedule.setCreatedUserId(0L);
        convrScheduleService.addConvrSchedule(convrSchedule);
      }
    }

    // 5)
    batchInsertUserSysNotification(
            orgId, jobTransfer.getToNotifyUserIds(), jobTransfer.getJobTransferId(),
            UserSysNotificationType.JOB_TRANSFER.getCode(), 1, 1, actorUserId);

    return jobTransfer.getJobTransferId();
  }

  /**
   * Steps:
   *  1) get jobTransfer
   *  2) get toNotifyUserIds
   *
   * @param orgId
   * @param jobTransferId
   * @return
   */
  @Override
  @LogAround
  public JobTransfer getJobTransfer(long orgId, long jobTransferId) {

    // 1)
    JobTransfer jobTransfer = jobTransferDao.findJobTransferByOrgIdAndPrimaryKey(orgId, jobTransferId);
    if (null == jobTransfer) {
      throw new ServiceStatusException(ServiceStatus.UP_JOB_TRANSFER_NOT_FOUNT);
    }

    // 2)
    List<Long> toNotifyUserIds = userSysNotificationDao.listUserIdByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex(
            orgId, jobTransferId, UserSysNotificationType.JOB_TRANSFER.getCode());
    jobTransfer.setToNotifyUserIds(toNotifyUserIds);

    return jobTransfer;
  }

  @Override
  @LogAround
  public List<JobTransfer> listJobTransfer(long orgId, int pageNumber, int pageSize) {
    List<JobTransfer> jobTransfers = jobTransferDao
            .listJobTransferByOrgIdOrderByCreatedTimeDesc(orgId, pageNumber, pageSize);
    if (!CollectionUtils.isEmpty(jobTransfers)) {
      for (JobTransfer jobTransfer: jobTransfers) {
        long jobTransferId = jobTransfer.getJobTransferId();
        List<Long> toNotifyUserIds = userSysNotificationDao
                .listUserIdByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex(
                        orgId, jobTransferId, UserSysNotificationType.JOB_TRANSFER.getCode());
        jobTransfer.setToNotifyUserIds(toNotifyUserIds);
      }
    }
    return jobTransfers;
  }

  @Override
  @LogAround
  public List<JobTransfer> listJobTransfer(long orgId, List<Long> jobTransferIds) {
    return jobTransferDao.listJobTransferByOrgIdAndPrimaryKeys(orgId, jobTransferIds);
  }

  @Override
  @LogAround
  public int countJobTransferByOrgId(long orgId) {
    return jobTransferDao.countJobTransferByOrgId(orgId);
  }

  /**
   * Steps:
   *  1) add statusUpdate
   *  2) add toNotifyUserIds
   *
   * @param statusUpdate
   * @return
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addPassProbationStatusUpdate(StatusUpdate statusUpdate) {

    if (!StatusUpdateHelper.isValidStatusUpdateUponAddPassProbation(statusUpdate)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    statusUpdateDao.insertStautsUpdate(statusUpdate);

    // 1)
    long orgId = statusUpdate.getOrgId();
    long userId = statusUpdate.getUserId();
    long actorUserId = statusUpdate.getCreatedUserId();
    UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userEmployment.setLastModifiedUserId(actorUserId);
    userEmploymentService.updateUserEmployment(userEmployment);

    // 2)
    if (!CollectionUtils.isEmpty(statusUpdate.getToNotifyUserIds())) {
      batchInsertUserSysNotification(
              orgId, statusUpdate.getToNotifyUserIds(), statusUpdate.getStatusUpdateId(),
              UserSysNotificationType.PASS_PROBATION.getCode(), 1, 1, statusUpdate.getCreatedUserId());
    }

    return statusUpdate.getStatusUpdateId();
  }

  /**
   * Steps:
   *  1) add statusUpdate
   *  2) add toNotifyUsers
   *  3) change userStatus
   *  4) disable all tokens
   *  5) set all resignedReportees' reporter as 0
   *
   * @param statusUpdate
   * @return
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addResignStatusUpdate(StatusUpdate statusUpdate) {

    if (!StatusUpdateHelper.isValidStatusUpdateUponResign(statusUpdate)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    List<Role> roles = securityModelService.getRolesByUserId(statusUpdate.getOrgId(), statusUpdate.getUserId());
    for (Role role: roles) {
      if (DefaultRole.SUPER_ADMIN.getName().equals(role.getRoleName())) {
        throw new ServiceStatusException(ServiceStatus.UP_SUPER_ADMIN_CANNOT_RESIGN);
      }
    }

    /**
     * get two lists:
     *  1) resigned reportees
     *  2) unresigned reportees
     */
    List<Long> reporteeUserIds = userService.listReporteesByUserIdAndOrgId(
            statusUpdate.getOrgId(), statusUpdate.getUserId());
    List<UserEmployment> userEmployments = userEmploymentService
            .listUserEmployment(statusUpdate.getOrgId(), reporteeUserIds);
    List<Long> resignedReporteeUserIds = new ArrayList<>();
    List<Long> unresignedReporteeUserIds = new ArrayList<>();

    for (UserEmployment userEmployment: userEmployments) {
      if (userEmployment.getUserStatus() == UserStatus.RESIGNED.getCode()) {
        resignedReporteeUserIds.add(userEmployment.getUserId());
      } else {
        unresignedReporteeUserIds.add(userEmployment.getUserId());
      }
    }

    if (!CollectionUtils.isEmpty(unresignedReporteeUserIds)) {
      throw new ServiceStatusException(ServiceStatus.UP_CANNOT_RESIGN_USER_WITH_REPORTEE);
    }

    // 1)
    statusUpdateDao.insertStautsUpdate(statusUpdate);

    // 2)
    if (!CollectionUtils.isEmpty(statusUpdate.getToNotifyUserIds())) {
      batchInsertUserSysNotification(
              statusUpdate.getOrgId(), statusUpdate.getToNotifyUserIds(), statusUpdate.getStatusUpdateId(),
              UserSysNotificationType.RESIGN.getCode(), 1, 1, statusUpdate.getCreatedUserId());
    }

    // 3)
    long orgId = statusUpdate.getOrgId();
    long userId = statusUpdate.getUserId();
    long actorUserId = statusUpdate.getCreatedUserId();
    userProfileService.updateUserStatus(orgId, userId, UserStatus.RESIGNED.getCode(), actorUserId);

    // 4)
    tokenService.deleteAllTokensByOrgIdAndUserId(orgId, statusUpdate.getUserId());

    // 5)
    if (!CollectionUtils.isEmpty(resignedReporteeUserIds)) {
      userService.batchUpdateReportLine(orgId, resignedReporteeUserIds, 0, actorUserId);
    }

    return statusUpdate.getStatusUpdateId();
  }

  /**
   * Steps:
   *  1) get statusUpdate
   *  2) get toNotifyUserIds
   *
   * @param orgId
   * @param statusUpdateId
   * @return
   */
  @Override
  @LogAround
  public StatusUpdate getStatusUpdate(long orgId, long statusUpdateId) {

    // 1)
    StatusUpdate statusUpdate = statusUpdateDao.findStatusUpdateByOrgIdAndPrimaryKey(orgId, statusUpdateId);
    if (null == statusUpdate) {
      throw new ServiceStatusException(ServiceStatus.UP_STATUS_UPDATE_NOT_FOUND);
    }

    // 2)
    List<Long> toNotifyUserIds = userSysNotificationDao.listUserIdByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex(
            orgId, statusUpdateId, UserSysNotificationType.PASS_PROBATION.getCode());
    statusUpdate.setToNotifyUserIds(toNotifyUserIds);

    return statusUpdate;
  }

  /**
   * Steps:
   *  1) list statusUpdates
   *  2) list toNotifyUserIds
   *
   * @param orgId
   * @param statusType
   * @param pageNumber
   * @param pageSize
   * @return
   */
  @Override
  @LogAround
  public List<StatusUpdate> listStatusUpdate(long orgId, int statusType, int pageNumber, int pageSize) {

    // 1)
    List<StatusUpdate> statusUpdates = statusUpdateDao
            .listStatusUpdateByOrgIdOrderByCreatedTimeDesc(orgId, statusType, pageNumber, pageSize);

    // 2)
    if (!CollectionUtils.isEmpty(statusUpdates)) {
      int objectType = UserSysNotificationType.PASS_PROBATION.getCode();
      if (StatusType.USER_STATUS.getCode() == statusType) {
        objectType = UserSysNotificationType.RESIGN.getCode();
      }
      for (StatusUpdate statusUpdate: statusUpdates) {
        List<Long> toNotifyUserIds = userSysNotificationDao
                .listUserIdByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex(
                        orgId, statusUpdate.getStatusUpdateId(), objectType);
        statusUpdate.setToNotifyUserIds(toNotifyUserIds);
      }
    }

    return statusUpdates;
  }

  /**
   *
   * @param orgId
   * @param statusUpdateIds
   * @return
   */
  @Override
  public List<StatusUpdate> listStatusUpdate(long orgId, List<Long> statusUpdateIds) {
    return statusUpdateDao.listStatusUpdateByOrgIdAndPrimaryKeys(orgId, statusUpdateIds);
  }

  @Override
  @LogAround
  public int countStatusUpdate(long orgId, int statusType) {
    return statusUpdateDao.countStatusUpdateByOrgIdAndStatusType(orgId, statusType);
  }

  /**
   * Steps:
   *  1) revoke statusUpdate
   *  2) change employmentStatus UserEmployment
   *
   * @param orgId
   * @param statusUpdateId
   * @param actorUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void revokePassProbationStatusUpdate(long orgId, long statusUpdateId, long actorUserId) {

    StatusUpdate statusUpdate = statusUpdateDao.findStatusUpdateByOrgIdAndPrimaryKey(orgId, statusUpdateId);
    if (null == statusUpdate
            || -1 == statusUpdate.getUpdateDate()) {
      throw new ServiceStatusException(ServiceStatus.UP_STATUS_UPDATE_NOT_FOUND);
    }
    long userId = statusUpdate.getUserId();

    // 1)
    statusUpdateDao.revokeStatusUpdateByPrimaryKeyAndOrgId(orgId, statusUpdateId, actorUserId);

    // 2)
    UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
    userEmployment.setEmploymentStatus(EmploymentStatus.PROBATIONARY.getCode());
    userEmployment.setLastModifiedUserId(actorUserId);
    userEmploymentService.updateUserEmployment(userEmployment);

  }

  /**
   * Steps:
   *  1) revode statusUpdate
   *  2) change userStatus
   *
   * @param orgId
   * @param statusUpdateId
   * @param actorUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void revokeResignStatusUpdate(long orgId, long statusUpdateId, long actorUserId) {

    StatusUpdate statusUpdate = statusUpdateDao.findStatusUpdateByOrgIdAndPrimaryKey(orgId, statusUpdateId);
    if (null == statusUpdate
            || -1 == statusUpdate.getUpdateDate()) {
      throw new ServiceStatusException(ServiceStatus.UP_STATUS_UPDATE_NOT_FOUND);
    }
    long userId = statusUpdate.getUserId();

    // 1)
    statusUpdateDao.revokeStatusUpdateByPrimaryKeyAndOrgId(orgId, statusUpdateId, actorUserId);

    // 2)
    userProfileService.updateUserStatus(orgId, userId, UserStatus.ACTIVE.getCode(), actorUserId);

  }

  /**
   *  0) delete okr
   *  1) delete UserAccount
   *  2) add UserProfile and CoreUserProfile
   *  3) add OrgMember
   *  4) add TeamMember (optional)
   *  5) add ReportLine (optional)
   *  6) add UserName
   *  7) add UserRole(disable admin role setting for ux-friendly)
   *  8) add UserEmployment
   *  9) delete remindSetting
   *  10) delete all tokens
   *  11) move resigned reportees
   * @param orgId
   * @param userId
   * @param actorUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteUser(long orgId, long userId, long actorUserId) {

    /*UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
    long enrollDate = UserEmploymentHelper.getEnrollDate(userEmployment);

    if (enrollDate <= TimeUtils.getNowTimestmapInMillis()
        && UserStatus.RESIGNED.getCode() != userEmployment.getUserStatus().intValue()) {
      throw new ServiceStatusException(ServiceStatus.UP_CANNOT_DELETE_ACTIVE_USER);
    }*/

    /**
     * get two lists:
     *  1) resigned reportees
     *  2) unresigned reportees
     */
    List<Long> reporteeUserIds = userService.listReporteesByUserIdAndOrgId(orgId, userId);
    List<UserEmployment> userEmployments = userEmploymentService
            .listUserEmployment(orgId, reporteeUserIds);
    List<Long> resignedReporteeUserIds = new ArrayList<>();
    List<Long> unresignedReporteeUserIds = new ArrayList<>();

    for (UserEmployment userEmployment: userEmployments) {
      if (userEmployment.getUserStatus() == UserStatus.RESIGNED.getCode()) {
        resignedReporteeUserIds.add(userEmployment.getUserId());
      } else {
        unresignedReporteeUserIds.add(userEmployment.getUserId());
      }
    }

    if (!CollectionUtils.isEmpty(unresignedReporteeUserIds)) {
      throw new ServiceStatusException(ServiceStatus.UP_CANNOT_RESIGN_USER_WITH_REPORTEE);
    }
    // 11)
    if (!CollectionUtils.isEmpty(resignedReporteeUserIds)) {
      userService.batchUpdateReportLine(orgId, resignedReporteeUserIds, 0, actorUserId);
    }

    // 10)
    tokenService.deleteAllTokensByOrgIdAndUserId(orgId, userId);

    // 9)
    remindSettingService.deleteRemindSettingByUserId(orgId, userId, actorUserId);

    // 8)
    userEmploymentService.deleteUserEmployment(orgId, userId, actorUserId);

    // 7)
    securityModelService.deleteUserRolesByUserId(orgId, userId, actorUserId);

    // 6)
    nameIndexService.deleteContentIndexByObjectIdAndType(
            orgId, userId, ContentIndexType.USER_NAME.getCode(), actorUserId);

    // 5)
    userService.batchDeleteReportLine(orgId, Arrays.asList(userId), actorUserId);

    // 4)
    teamService.deleteTeamMember(orgId, userId, actorUserId);

    // 3)
    userService.deleteOrgMember(userId);

    // 2)
    userProfileService.deleteUserProfile(orgId, userId, actorUserId);

    // 1)
    userService.deleteUserAccount(userId, actorUserId);

    // 0)
    List<ObjectivePeriod> objectivePeriods = okrService.listObjectivePeriodByOrgIdAndOwnerId(
            orgId, OkrType.PERSON.getCode(), userId);
    if (!CollectionUtils.isEmpty(objectivePeriods)) {
      for (ObjectivePeriod objectivePeriod : objectivePeriods) {
        okrService.deleteObjectivePeriod(orgId, objectivePeriod.getObjectivePeriodId(), actorUserId);
      }
    }
  }

  @Override
  public List<Long> listToNotifyUserIds(long orgId, long objectId, int objectType) {
    return userSysNotificationDao
            .listUserIdByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex(orgId, objectId, objectType);
  }

  /**
   *
   * @param orgId
   * @param toNotifyUserIds
   * @param objectId
   * @param objectType
   * @param needEmail
   * @param needMessageCenter
   * @param actorUserId
   */
  private void batchInsertUserSysNotification(
          long orgId, List<Long> toNotifyUserIds, long objectId, int objectType,
          int needEmail, int needMessageCenter, long actorUserId) {
    if (!CollectionUtils.isEmpty(toNotifyUserIds)) {
      for (int i = 0; i < toNotifyUserIds.size(); i++) {
        long toNotifyUserId = toNotifyUserIds.get(i);
        UserSysNotification userSysNotification = new UserSysNotification();
        userSysNotification.setOrgId(orgId);
        userSysNotification.setObjectId(objectId);
        userSysNotification.setObjectType(objectType);
        userSysNotification.setNotifyUserId(toNotifyUserId);
        userSysNotification.setLogicalIndex(i);
        userSysNotification.setNeedEmail(needEmail);
        userSysNotification.setNeedMessageCenter(needMessageCenter);
        userSysNotification.setCreatedUserId(actorUserId);
        userSysNotificationDao.insertUserSysNotification(userSysNotification);

      }
    }

  }

}
