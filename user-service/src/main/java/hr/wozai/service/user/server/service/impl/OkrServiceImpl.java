package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.LongUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.client.okr.enums.PeriodTimeSpan;
import hr.wozai.service.user.server.dao.okr.*;
import hr.wozai.service.user.server.enums.OkrLogAttribute;
import hr.wozai.service.user.server.helper.OkrCommentHelper;
import hr.wozai.service.user.server.helper.OkrRemindSettingHelper;
import hr.wozai.service.user.server.model.okr.*;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.ProjectTeamMember;
import hr.wozai.service.user.server.service.OkrService;
import hr.wozai.service.user.client.okr.enums.DirectorType;
import hr.wozai.service.user.server.helper.ObjectiveHelper;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.service.UserProfileService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
@Service("okrService")
public class OkrServiceImpl implements OkrService {
  private static final Logger LOGGER = LoggerFactory.getLogger(OkrServiceImpl.class);

  private static final String LEFT = "{{";
  private static final String RIGHT = "}}";
  private static final String DEFAULT_NAME = "未知";

  @Autowired
  ObjectivePeriodDao objectivePeriodDao;

  @Autowired
  ObjectiveDao objectiveDao;

  @Autowired
  KeyResultDao keyResultDao;

  @Autowired
  DirectorDao directorDao;

  @Autowired
  OkrCommentDao okrCommentDao;

  @Autowired
  OkrUpdateLogDao okrUpdateLogDao;

  @Autowired
  TeamService teamService;

  @Autowired
  UserProfileService userProfileService;

  @Autowired
  OkrRemindSettingDao okrRemindSettingDao;


  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long createObjectivePeriod(ObjectivePeriod objectivePeriod) {
    ObjectiveHelper.checkObjectivePeriodInsertParams(objectivePeriod);

    ObjectivePeriod inDb = objectivePeriodDao.findObjectivePeriodByName(
            objectivePeriod.getOrgId(), objectivePeriod.getName(),
            objectivePeriod.getType(), objectivePeriod.getOwnerId());
    if (null != inDb) {
      throw new ServiceStatusException(ServiceStatus.OKR_OBJECTIVE_PERIOD_EXIST);
    }

    long result = objectivePeriodDao.insertObjectivePeriod(objectivePeriod);
    return result;
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteObjectivePeriod(long orgId, long objectivePeriodId, long actorUserId) {
    ObjectivePeriod objectivePeriod = objectivePeriodDao.findObjectivePeriod(orgId, objectivePeriodId);
    if (objectivePeriod == null) {
      return;
    }
    List<Objective> objectives = objectiveDao.listObjectiveByTypeAndOwnerIdAndQuarterId(
            orgId, objectivePeriod.getType(), objectivePeriod.getOwnerId(), objectivePeriodId, 0, 2);
    for (Objective objective : objectives) {
      this.deleteObjective(orgId, objective.getObjectiveId(), actorUserId);
    }

    objectivePeriodDao.deleteObjectivePeriod(orgId, objectivePeriodId, actorUserId);
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateObjectivePeriod(ObjectivePeriod objectivePeriod) {
    ObjectiveHelper.checkObjectivePeriodUpdateParams(objectivePeriod);

    ObjectivePeriod inDb = objectivePeriodDao.findObjectivePeriod(objectivePeriod.getOrgId(),
            objectivePeriod.getObjectivePeriodId());
    ObjectivePeriod existOne = objectivePeriodDao.findObjectivePeriodByName(
            inDb.getOrgId(), objectivePeriod.getName(), inDb.getType(), inDb.getOwnerId());
    if (null != existOne) {
      throw new ServiceStatusException(ServiceStatus.OKR_OBJECTIVE_PERIOD_EXIST);
    }
    objectivePeriodDao.updateObjectivePeriod(objectivePeriod);
  }

  @LogAround
  @Override
  public List<ObjectivePeriod> listObjectivePeriodByOrgIdAndOwnerId(long orgId, int type, long teamId) {
    return objectivePeriodDao.listObjectivePeriodByOrgIdAndOwnerId(orgId, type, teamId);
  }

  @Override
  @LogAround
  public ObjectivePeriod getObjectivePeriod(long orgId, long objectivePeriodId) {
    ObjectivePeriod result = objectivePeriodDao.findObjectivePeriod(orgId, objectivePeriodId);
    if (null == result) {
      throw new ServiceStatusException(ServiceStatus.OKR_OBJECTIVE_PERIOD_NOT_FOUND);
    }
    return result;
  }

  /**
   * 1. 添加objective 2. 添加负责人
   *
   * @param objective
   * @param directors
   * @return
   */
  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long createObjectiveAndDirector(Objective objective, List<Director> directors) {
    ObjectiveHelper.checkObjectiveInsertParams(objective);

    ObjectivePeriod objectivePeriod = objectivePeriodDao.findObjectivePeriod(objective.getOrgId(),
            objective.getObjectivePeriodId());
    if (objectivePeriod == null) {
      throw new ServiceStatusException(ServiceStatus.OKR_OBJECTIVE_PERIOD_NOT_FOUND);
    }

    long objectiveParentId = objective.getParentObjectiveId();
    if (objectiveParentId != 0) {
      getObjective(objective.getOrgId(), objectiveParentId);
    }

    // 设置order_id
    int curMaxOrderId = objectiveDao.getMaxOrderIndexByObjectivePeriod(
            objective.getOrgId(), objective.getObjectivePeriodId());
    objective.setOrderIndex(curMaxOrderId + 1);

    long objectiveId = objectiveDao.insertObjective(objective);

    for (Director director : directors) {
      director.setType(DirectorType.OBJECTIVE.getCode());
      director.setObjectId(objectiveId);
    }
    batchInsertDirector(directors);

    createOkrCommentWhenCreateObjective(objective);

    return objectiveId;
  }

  private void createOkrCommentWhenCreateObjective(Objective objective) {
    OkrComment okrComment = new OkrComment();
    okrComment.setOrgId(objective.getOrgId());
    okrComment.setObjectiveId(objective.getObjectiveId());
    okrComment.setKeyResultId(0L);
    okrComment.setKeyResultContent("");
    okrComment.setUserId(objective.getCreatedUserId());

    String content = "目标已创建。";
    okrComment.setContent(content);
    okrComment.setCreatedUserId(objective.getCreatedUserId());

    this.addOkrCommentAndOkrUpdateLogs(okrComment, new ArrayList<>());
  }

  /**
   * 1. delete object 2. delete object_director 3. delete key results 4. delete key_results_director
   *
   * @param orgId
   * @param objectiveId
   * @param actorUserId
   */
  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteObjective(long orgId, long objectiveId, long actorUserId) {
    Objective inDb = getObjective(orgId, objectiveId);
    if (inDb == null) {
      return;
    }
    objectiveDao.deleteObjective(orgId, objectiveId, actorUserId);
    batchDeleteDirector(orgId, DirectorType.OBJECTIVE.getCode(),
            Arrays.asList(objectiveId), actorUserId);

    List<KeyResult> keyResults = keyResultDao.listKeyResultByObjectiveId(orgId, objectiveId);
    List<Long> keyResultsIds = new ArrayList<>();
    for (KeyResult keyResult : keyResults) {
      keyResultsIds.add(keyResult.getKeyResultId());
    }
    keyResultDao.deleteKeyResultByObjectiveId(orgId, objectiveId, actorUserId);
    batchDeleteDirector(orgId, DirectorType.KEYRESULT.getCode(),
            keyResultsIds, actorUserId);

    List<Objective> subordinates = listFirstLevelSubordinateObjectives(orgId, objectiveId);
    if (!CollectionUtils.isEmpty(subordinates)) {
      for (Objective objective : subordinates) {
        objective.setParentObjectiveId(0L);
        objective.setLastModifiedUserId(actorUserId);
        updateObjective(objective, "");
      }
    }
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateObjective(Objective objective, String comment) {
    ObjectiveHelper.checkObjectiveUpdateParams(objective);
    Objective inDb = objectiveDao.findObjective(objective.getOrgId(), objective.getObjectiveId(), 0);

    if (objective.getParentObjectiveId() != null && objective.getParentObjectiveId() != 0) {
      // 判断父目标存不存在
      getObjective(objective.getOrgId(), objective.getParentObjectiveId());
      if (listSubObjectiveIds(objective.getOrgId(), objective.getObjectiveId()).
              contains(objective.getParentObjectiveId())
              || LongUtils.equals(objective.getObjectiveId(), objective.getParentObjectiveId())) {
        throw new ServiceStatusException(ServiceStatus.OKR_SET_PARENT_OBJECTIVE_ERROR);
      }
    }

    objectiveDao.updateObjective(objective);

    Objective afterUpdate = objectiveDao.findObjective(objective.getOrgId(), objective.getObjectiveId(), 0);
    createOkrCommentWhenUpdateObjective(inDb, afterUpdate, null, null, comment);
  }

  private List<Long> listSubObjectiveIds(long orgId, long objectiveId) {
    Objective objective = getObjective(orgId, objectiveId);
    List<Objective> result = new ArrayList<>();

    if (objective == null) {
      return new ArrayList<>();
    }

    Queue<Objective> queue = new LinkedBlockingQueue<>();
    queue.add(objective);
    while (!queue.isEmpty()) {
      Objective r = queue.poll();
      List<Objective> objectives = listFirstLevelSubordinateObjectives(orgId, r.getObjectiveId());
      result.addAll(objectives);
      queue.addAll(objectives);
    }
    LOGGER.info("listSubObjectiveIds() success, result size is {}", result.size());

    List<Long> finalResult = new ArrayList<>();
    for (Objective objective1 : result) {
      finalResult.add(objective1.getObjectiveId());
    }
    return finalResult;
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateObjectiveAndDirectors(Objective objective, String comment, List<Director> directors, long actorUserId) {
    ObjectiveHelper.checkObjectiveUpdateParams(objective);

    if (objective.getParentObjectiveId() != null && objective.getParentObjectiveId() != 0) {
      // 判断父目标存不存在
      getObjective(objective.getOrgId(), objective.getParentObjectiveId());
      if (listSubObjectiveIds(objective.getOrgId(), objective.getObjectiveId()).
              contains(objective.getParentObjectiveId())
              || LongUtils.equals(objective.getObjectiveId(), objective.getParentObjectiveId())) {
        throw new ServiceStatusException(ServiceStatus.OKR_SET_PARENT_OBJECTIVE_ERROR);
      }
    }

    Objective inDb = objectiveDao.findObjective(objective.getOrgId(), objective.getObjectiveId(), 0);

    objectiveDao.updateObjective(objective);

    List<Director> preDirectors = listDirector(
            objective.getOrgId(), DirectorType.OBJECTIVE.getCode(), objective.getObjectiveId());
    batchDeleteDirector(
            objective.getOrgId(), DirectorType.OBJECTIVE.getCode(), Arrays.asList(objective.getObjectiveId()), actorUserId);

    if (!CollectionUtils.isEmpty(directors)) {
      batchInsertDirector(directors);
    }

    Objective afterUpdate = objectiveDao.findObjective(objective.getOrgId(), objective.getObjectiveId(), 0);
    createOkrCommentWhenUpdateObjective(inDb, afterUpdate, preDirectors, directors, comment);

  }

  private void createOkrCommentWhenUpdateObjective(
          Objective before, Objective after,
          List<Director> beforeDirecotrs, List<Director> afterDirecotrs,
          String comment) {
    //todo: add okrComment, add OkrUpdateLogs
    OkrComment okrComment = new OkrComment();
    okrComment.setOrgId(after.getOrgId());
    okrComment.setObjectiveId(after.getObjectiveId());
    okrComment.setKeyResultId(0L);
    okrComment.setKeyResultContent("");
    okrComment.setUserId(after.getLastModifiedUserId());
    okrComment.setContent(comment);
    okrComment.setCreatedUserId(after.getLastModifiedUserId());

    List<OkrUpdateLog> okrUpdateLogs = OkrCommentHelper.generateOkrUpdateLogsWhenUpdateObjective(before, after);

    // 负责人
    if (beforeDirecotrs != null && afterDirecotrs != null) {
      if (!isEqualDirectorList(beforeDirecotrs, afterDirecotrs)) {
        OkrUpdateLog okrUpdateLog = OkrCommentHelper.generateOkrUpdateLog(before.getOrgId(), "",
                OkrLogAttribute.OBJ_DIRECTOR.getDesc(),
                getNameStringFromDirectors(before.getOrgId(), beforeDirecotrs),
                getNameStringFromDirectors(before.getOrgId(), afterDirecotrs),
                after.getLastModifiedUserId());
        okrUpdateLogs.add(okrUpdateLog);
      }
    }

    if (!StringUtils.isNullOrEmpty(comment) || !CollectionUtils.isEmpty(okrUpdateLogs)) {
      this.addOkrCommentAndOkrUpdateLogs(okrComment, okrUpdateLogs);
    }
  }

  @LogAround
  @Override
  public Objective getObjective(long orgId, long objectiveId) {
    Objective result = objectiveDao.findObjective(orgId, objectiveId, 0);
    if (result == null) {
      throw new ServiceStatusException(ServiceStatus.OKR_OBJECTIVE_NOT_FOUND);
    }
    return result;
  }

  @LogAround
  @Override
  public List<Objective> listObjectiveByTypeAndOwnerIdAndPeriodId(
          long orgId, int type, long ownerId, long objectivePeriodId, int progressStatus, int orderBy) {
    ObjectivePeriod objectivePeriod = objectivePeriodDao.findObjectivePeriod(orgId, objectivePeriodId);
    if (objectivePeriod == null) {
      throw new ServiceStatusException(ServiceStatus.OKR_OBJECTIVE_PERIOD_NOT_FOUND, "objective period not found");
    }

    List<Objective> result = objectiveDao.listObjectiveByTypeAndOwnerIdAndQuarterId(orgId, type,
            ownerId, objectivePeriodId, progressStatus, orderBy);
    return result;
  }

  @Override
  public List<Objective> listObjectivesByObjectiveIds(long orgId, List<Long> objectiveIds) {
    return objectiveDao.listObjectivesByObjectiveIds(orgId, objectiveIds);
  }

  @Override
  @LogAround
  public List<Objective> searchObjectiveByKeywordInOrder(
          long orgId, long objectiveId, String keyword, int type, long ownerId, long actorUserId) {
    List<Objective> objectives = objectiveDao.searchObjectiveByKeyword(orgId, keyword, type, ownerId);
    if (CollectionUtils.isEmpty(objectives)) {
      return objectives;
    } else {
      // filter sub objectives and itselc
      List<Objective> objsAfterFilter = filterSubObjectives(orgId, objectiveId, objectives);
      return bubbleSortObjectives(orgId, objsAfterFilter, actorUserId);
    }
  }

  private List<Objective> filterSubObjectives(long orgId, long objectiveId, List<Objective> searchResult) {
    if (objectiveId == 0L) {
      return searchResult;
    }
    List<Long> subObjIds = listSubObjectiveIds(orgId, objectiveId);
    subObjIds.add(objectiveId);

    List<Objective> result = new ArrayList<>();
    for (Objective objective : searchResult) {
      if (!subObjIds.contains(objective.getObjectiveId())) {
        result.add(objective);
      }
    }
    return result;
  }

  /**
   * 根据teamId,period等条件进行排序
   *
   * @param objectives
   * @return
   */
  private List<Objective> bubbleSortObjectives(long orgId, List<Objective> objectives, long actorUserId) {
    long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
    long parentTeamId = teamService.getTeamByTeamId(orgId, teamId).getParentTeamId();
    List<ProjectTeamMember> projectTeamMembers = teamService.listProjectTeamMembersByOrgIdAndUserId(orgId, actorUserId);
    List<Long> projectTeamIds = getProjectTeamIdsFromProjectTeamMembers(projectTeamMembers);

    Objective[] array = objectives.toArray(new Objective[objectives.size()]);

    for (int i = 0; i < array.length; i++) {

      for (int j = i + 1; j < array.length; j++) {
        if (compareTwoObjective(array[i], array[j], teamId, parentTeamId, projectTeamIds, actorUserId) == -1) {
          Objective temp = array[i];
          array[i] = array[j];
          array[j] = temp;
        }
      }
    }
    return Arrays.asList(array);
  }

  private List<Long> getProjectTeamIdsFromProjectTeamMembers(List<ProjectTeamMember> projectTeamMembers) {
    List<Long> result = new ArrayList<>();
    if (!CollectionUtils.isEmpty(projectTeamMembers)) {
      for (ProjectTeamMember projectTeamMember : projectTeamMembers) {
        result.add(projectTeamMember.getProjectTeamId());
      }
    }
    return result;
  }

  private int compareTwoObjective(
          Objective a, Objective b, long teamId, long parentTeamId, List<Long> projectTeamIds, long actorUserId) {
    int teamCompareResult = compareTwoObjectiveWithOkrTypeAndTeamId(a, b, teamId, parentTeamId, projectTeamIds, actorUserId);
    if (teamCompareResult == 0) {
      int periodCompareResult = compareTwoObjectiveWithPeriod(a, b);
      if (periodCompareResult == 0) {
        return compareTwoObjectiveWithUpdateTime(a, b);
      } else {
        return periodCompareResult;
      }
    } else {
      return teamCompareResult;
    }

  }

  private int compareTwoObjectiveWithUpdateTime(Objective a, Objective b) {
    return Long.compare(a.getLastModifiedTime(), b.getLastModifiedTime());
  }

  private int compareTwoObjectiveWithOkrTypeAndTeamId(
          Objective a, Objective b, long teamId, long parentTeamId, List<Long> projectTeamIds, long actorUserId) {
    int weightA = getObjectiveWeightByTypeAndOwner(a, teamId, parentTeamId, projectTeamIds, actorUserId);
    int weightB = getObjectiveWeightByTypeAndOwner(b, teamId, parentTeamId, projectTeamIds, actorUserId);

    return Integer.compare(weightA, weightB);
  }

  private int getObjectiveWeightByTypeAndOwner(
          Objective objective, long teamId, long parentTeamId, List<Long> projectTeamIds, long actorUserId) {
    long type = objective.getType();
    long ownerId = objective.getOwnerId();
    // 本项目组
    if (type == OkrType.PROJECT_TEAM.getCode() && projectTeamIds.contains(ownerId)) {
      return 8;
      // 父team
    } else if (type == OkrType.TEAM.getCode() && ownerId == parentTeamId) {
      return 7;
      // 本team
    } else if (type == OkrType.TEAM.getCode() && ownerId == teamId){
      return 6;
      // 其他项目组
    } else if (type == OkrType.PROJECT_TEAM.getCode() && !projectTeamIds.contains(ownerId)) {
      return 5;
      // 其他team
    } else if (type == OkrType.TEAM.getCode() && (ownerId != teamId || ownerId != parentTeamId)) {
      return 4;
      // 本人
    } else if (type == OkrType.PERSON.getCode() && ownerId == actorUserId) {
      return 3;
      // 其他人
    } else if (type == OkrType.PERSON.getCode() && ownerId != actorUserId) {
      return 2;
    } else {
      return 1;
    }
  }

  private int compareTwoObjectiveWithPeriod(Objective a, Objective b) {
    ObjectivePeriod objectivePeriodA = this.getObjectivePeriod(a.getOrgId(), a.getObjectivePeriodId());
    ObjectivePeriod objectivePeriodB = this.getObjectivePeriod(b.getOrgId(), b.getObjectivePeriodId());

    PeriodTimeSpan spanA = PeriodTimeSpan.getEnumByCode(objectivePeriodA.getPeriodTimeSpanId());
    PeriodTimeSpan spanB = PeriodTimeSpan.getEnumByCode(objectivePeriodB.getPeriodTimeSpanId());

    // 如果都在本年
    if ((isPeriodInCurrentYear(objectivePeriodA) && isPeriodInCurrentYear(objectivePeriodB))) {
        if (isMonthInPeriodRegion(objectivePeriodA) && isMonthInPeriodRegion(objectivePeriodB)) {
          return Integer.compare(spanA.getCode(), spanB.getCode());
        } else if (isMonthInPeriodRegion(objectivePeriodA) && !isMonthInPeriodRegion(objectivePeriodB)) {
          return 1;
        } else if (!isMonthInPeriodRegion(objectivePeriodA) && isMonthInPeriodRegion(objectivePeriodB)) {
          return -1;
        } else {
          return Integer.compare(spanA.getCode(), spanB.getCode());
        }
    } else if (isPeriodInCurrentYear(objectivePeriodA) && !isPeriodInCurrentYear(objectivePeriodB)) {
      return 1;
    } else if (!isPeriodInCurrentYear(objectivePeriodA) && isPeriodInCurrentYear(objectivePeriodB)) {
      return -1;
    } else {
      return Integer.compare(objectivePeriodA.getYear(), objectivePeriodB.getYear());
    }
  }

  private boolean isPeriodInCurrentYear(ObjectivePeriod objectivePeriod) {
    if (objectivePeriod.getYear() == TimeUtils.getCurrentYearWithTimeZone(TimeUtils.BEIJING).intValue()) {
      return true;
    }
    return false;
  }

  private boolean isMonthInPeriodRegion(ObjectivePeriod objectivePeriod) {
    return PeriodTimeSpan.isInMonthRegion(TimeUtils.getCurrentMonthWithTimeZone(TimeUtils.BEIJING),
            PeriodTimeSpan.getEnumByCode(objectivePeriod.getPeriodTimeSpanId()));
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void moveObjective(long orgId, long objectiveId, int targetOrderIndex) {
    Objective objective = objectiveDao.findObjective(orgId, objectiveId, 1);

    int startOrderIndex;
    int endOrderIndex;
    List<Objective> movedObjectiveIds;
    List<Objective> objToUpdate = new ArrayList<>();
    if (objective.getOrderIndex() > targetOrderIndex) {
      startOrderIndex = targetOrderIndex - 1;
      endOrderIndex = objective.getOrderIndex();
      movedObjectiveIds = objectiveDao.listObjectivesByStartAndEndOrderIndex(
              orgId, objective.getObjectivePeriodId(), startOrderIndex, endOrderIndex);
      // order_id + 1
      for (Objective obj : movedObjectiveIds) {
        obj.setOrderIndex(obj.getOrderIndex() + 1);
        objToUpdate.add(obj);
      }
    } else if (objective.getOrderIndex() < targetOrderIndex) {
      startOrderIndex = objective.getOrderIndex();
      endOrderIndex = targetOrderIndex + 1;
      movedObjectiveIds = objectiveDao.listObjectivesByStartAndEndOrderIndex(
              orgId, objective.getObjectivePeriodId(), startOrderIndex, endOrderIndex);
      // order_id - 1
      for (Objective obj : movedObjectiveIds) {
        obj.setOrderIndex(obj.getOrderIndex() - 1);
        objToUpdate.add(obj);
      }
    } else {
      return;
    }
    objective.setOrderIndex(targetOrderIndex);
    objToUpdate.add(objective);
    objectiveDao.batchUpdateOrderIndexOfObjectives(objToUpdate);
  }

  @Override
  @LogAround
  public List<Objective> listAncesterObjectives(long orgId, long objectiveId) {
    Objective objective = getObjectiveWithOutException(orgId, objectiveId);
    List<Objective> result = new ArrayList<>();

    if (objective == null) {
      return result;
    }

    while (objective != null) {
      long parentObjectiveId = objective.getParentObjectiveId();

      objective = getObjectiveWithOutException(orgId, parentObjectiveId);
      if (objective != null) {
        result.add(objective);
      }
    }
    return result;
  }

  @LogAround
  private Objective getObjectiveWithOutException(long orgId, long objectiveId) {
    Objective result = objectiveDao.findObjective(orgId, objectiveId, 0);
    return result;
  }

  @Override
  @LogAround
  public List<Objective> listFirstLevelSubordinateObjectives(long orgId, long objectiveId) {
    return objectiveDao.listFirstLevelSubordinateObjectives(orgId, objectiveId);
  }

  @Override
  @LogAround
  public List<Objective> listObjectivesByStartAndEndDeadline(long orgId, long startDeadline, long endDeadline) {
    return objectiveDao.listObjectivesByStartAndEndDeadline(orgId, startDeadline, endDeadline);
  }

  @Override
  @LogAround
  public List<Objective> listObjectivesByPriority(long orgId, int priority, int orderItem) {
    return objectiveDao.listObjectivesByPriorityAndOrderItem(orgId, priority, orderItem);
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long createKeyResultAndDirector(KeyResult keyResult, List<Director> directors) {
    ObjectiveHelper.checkKeyResultInsertParams(keyResult);
    Objective objective = getObjective(keyResult.getOrgId(), keyResult.getObjectiveId());
    if (objective == null) {
      throw new ServiceStatusException(ServiceStatus.OKR_OBJECTIVE_NOT_FOUND, "objective not found");
    }

    long keyResultId = keyResultDao.insertKeyResult(keyResult);
    for (Director director : directors) {
      director.setType(DirectorType.KEYRESULT.getCode());
      director.setObjectId(keyResultId);
    }
    batchInsertDirector(directors);

    createOkrCommentWhenDeleteKeyResult(keyResult, keyResult.getCreatedUserId());

    return keyResultId;
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteKeyResult(long orgId, long keyResultId, long actorUserId) {
    KeyResult inDb = keyResultDao.findKeyResult(orgId, keyResultId);
    if (inDb == null) {
      return;
    }
    keyResultDao.deleteKeyResult(orgId, keyResultId, actorUserId);
    batchDeleteDirector(orgId, DirectorType.KEYRESULT.getCode(), Arrays.asList(keyResultId), actorUserId);

  }

  private void createOkrCommentWhenDeleteKeyResult(KeyResult keyResult, long actorUserId) {
    OkrComment okrComment = new OkrComment();
    okrComment.setOrgId(keyResult.getOrgId());
    okrComment.setObjectiveId(keyResult.getObjectiveId());
    okrComment.setKeyResultId(keyResult.getKeyResultId());
    okrComment.setKeyResultContent(keyResult.getContent());
    okrComment.setUserId(actorUserId);

    String content = "关键结果已创建。";
    okrComment.setContent(content);
    okrComment.setCreatedUserId(actorUserId);

    this.addOkrCommentAndOkrUpdateLogs(okrComment, new ArrayList<>());
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateKeyResult(KeyResult keyResult, String comment) {
    ObjectiveHelper.checkKeyResultUpdateParams(keyResult);

    KeyResult inDb = keyResultDao.findKeyResult(keyResult.getOrgId(), keyResult.getKeyResultId());
    if (inDb == null) {
      return;
    }
    keyResultDao.updateKeyResult(keyResult);

    KeyResult afterUpdate = keyResultDao.findKeyResult(keyResult.getOrgId(), keyResult.getKeyResultId());
    createOkrCommentWhenUpdateKeyResult(inDb, afterUpdate, null, null, comment);
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateKeyResultAndDirectors(KeyResult keyResult, String comment, List<Director> directors, long actorUserId) {
    ObjectiveHelper.checkKeyResultUpdateParams(keyResult);

    KeyResult inDb = keyResultDao.findKeyResult(keyResult.getOrgId(), keyResult.getKeyResultId());
    if (inDb == null) {
      return;
    }
    keyResultDao.updateKeyResult(keyResult);

    List<Director> preDirectors = listDirector(
            keyResult.getOrgId(), DirectorType.KEYRESULT.getCode(), keyResult.getKeyResultId());
    batchDeleteDirector(
            keyResult.getOrgId(), DirectorType.KEYRESULT.getCode(), Arrays.asList(keyResult.getKeyResultId()), actorUserId);

    if (!CollectionUtils.isEmpty(directors)) {
      batchInsertDirector(directors);
    }

    KeyResult afterUpdate = keyResultDao.findKeyResult(keyResult.getOrgId(), keyResult.getKeyResultId());
    createOkrCommentWhenUpdateKeyResult(inDb, afterUpdate, preDirectors, directors, comment);
  }

  private void createOkrCommentWhenUpdateKeyResult(
          KeyResult before, KeyResult after,
          List<Director> beforeDirectors, List<Director> afterDirectors,
          String comment) {
    OkrComment okrComment = new OkrComment();
    okrComment.setOrgId(after.getOrgId());
    okrComment.setObjectiveId(after.getObjectiveId());
    okrComment.setKeyResultId(after.getKeyResultId());
    okrComment.setKeyResultContent(after.getContent());
    okrComment.setUserId(after.getLastModifiedUserId());
    okrComment.setContent(comment);
    okrComment.setCreatedUserId(after.getLastModifiedUserId());

    List<OkrUpdateLog> okrUpdateLogs = OkrCommentHelper.generateOkrUpdateLogsWhenUpdateKeyresult(before, after);

    // 负责人
    if (beforeDirectors != null && afterDirectors != null) {
      if (!isEqualDirectorList(beforeDirectors, afterDirectors)) {
        OkrUpdateLog okrUpdateLog = OkrCommentHelper.generateOkrUpdateLog(before.getOrgId(), "",
                OkrLogAttribute.KR_DIRECTOR.getDesc(),
                getNameStringFromDirectors(before.getOrgId(), beforeDirectors),
                getNameStringFromDirectors(before.getOrgId(), afterDirectors),
                after.getLastModifiedUserId());
        okrUpdateLogs.add(okrUpdateLog);
      }
    }

    if (!StringUtils.isNullOrEmpty(comment) || !CollectionUtils.isEmpty(okrUpdateLogs)) {
      this.addOkrCommentAndOkrUpdateLogs(okrComment, okrUpdateLogs);
    }
  }

  @Override
  public KeyResult getKeyResult(long orgId, long keyResultId) {
    KeyResult result = keyResultDao.findKeyResult(orgId, keyResultId);
    if (result == null) {
      throw new ServiceStatusException(ServiceStatus.OKR_KEYRESULT_NOT_FOUND);
    }

    return result;
  }

  @LogAround
  @Override
  public List<KeyResult> listKeyResultByOBjectiveId(long orgId, long objectiveId) {
    List<KeyResult> result = keyResultDao.listKeyResultByObjectiveId(orgId, objectiveId);
    return result;
  }

  @Override
  public List<KeyResult> listSimpleKeyResultsByObjectiveIds(long orgId, List<Long> objectiveIds) {
    return keyResultDao.listSimpleKeyResultsByObjectiveIds(orgId, objectiveIds);
  }

  @LogAround
  @Override
  public List<KeyResult> listKeyResultsByStartAndEndDeadline(long orgId, long startDeadline, long endDeadline) {
    return keyResultDao.listKeyResultsByStartAndEndDeadline(orgId, startDeadline, endDeadline);
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void batchInsertDirector(List<Director> directors) {
    for (Director director : directors) {
      ObjectiveHelper.checkDirectorInsertParams(director);
    }
    directorDao.batchInsertDirector(directors);
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void batchDeleteDirector(long orgId, int type, List<Long> objectIds, long actorUserId) {
    directorDao.batchDeleteDirectorByTypeAndObjectId(orgId, type, objectIds, actorUserId);
  }

  private boolean isEqualDirectorList(List<Director> pre, List<Director> cur) {
    List<Long> preUserIds = new ArrayList<>();
    for (Director director : pre) {
      preUserIds.add(director.getUserId());
    }
    List<Long> curUserIds = new ArrayList<>();
    for (Director director : cur) {
      curUserIds.add(director.getUserId());
    }
    return CollectionUtils.isEqualCollection(preUserIds, curUserIds);
  }

  private String getNameStringFromDirectors(long orgId, List<Director> directors) {
    List<Long> userIds = new ArrayList<>();
    for (Director director : directors) {
      userIds.add(director.getUserId());
    }
    List<CoreUserProfile> userProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId, userIds);

    StringBuffer s = new StringBuffer();
    if (CollectionUtils.isEmpty(userProfiles)) {
      s.append("无");
    } else {
      for (CoreUserProfile oldCoreUserProfile : userProfiles) {
        s.append(oldCoreUserProfile.getFullName());
        s.append(",");
      }
      s.deleteCharAt(s.length() - 1);
    }

    return s.toString();
  }

  @LogAround
  @Override
  public List<Director> listDirector(long orgId, int type, long objectId) {
    List<Director> directors = directorDao.listDirectorByTypeAndObjectId(orgId, type, objectId);
    return directors;
  }

  @Override
  @LogAround
  public List<Director> listDirectorsByObjectiveIds(long orgId, int directorType, List<Long> objectiveIds) {
    return directorDao.listDirectorsByObjectIds(orgId, directorType, objectiveIds);
  }

  @Override
  @LogAround
  public List<Long> listObjectiveAndKeyResultDirectorsByObjectiveId(long orgId, long objetiveId) {
    return directorDao.listObjectiveAndKeyResultDirectorsByObjectiveId(orgId, objetiveId);
  }

  // ++++++++++++++++++++++++++++++++++++++
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addOkrCommentAndOkrUpdateLogs(OkrComment okrComment, List<OkrUpdateLog> okrUpdateLogs) {
    OkrCommentHelper.isAcceptableAddOkrCommentRequest(okrComment);

    long okrCommentId = okrCommentDao.insertOkrComment(okrComment);

    if (!CollectionUtils.isEmpty(okrUpdateLogs)) {
      for (OkrUpdateLog okrUpdateLog : okrUpdateLogs) {
        okrUpdateLog.setOkrCommentId(okrCommentId);
        OkrCommentHelper.isAcceptableAddOkrUpdateLogRequest(okrUpdateLog);
      }
      okrUpdateLogDao.batchInsertOkrUpdateLog(okrUpdateLogs);
    }
    return okrCommentId;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long updateOkrComment(OkrComment okrComment) {
    OkrCommentHelper.isAcceptableUpdateOkrCommentRequest(okrComment);

    return okrCommentDao.updateOkrComment(okrComment);
  }

  @Override
  public List<OkrComment> listOkrComment(long orgId, long objectiveId, long keyResultId, int pageNumber, int pageSize) {
    return okrCommentDao.listOkrCommentsByObjectiveId(orgId, objectiveId, keyResultId, pageNumber, pageSize);
  }

  @Override
  public long countOkrComment(long orgId, long objectiveId, long keyResultId) {
    return okrCommentDao.countOkrCommentByObjectiveId(orgId, objectiveId, keyResultId);
  }

  @Override
  public List<OkrUpdateLog> listOkrUpdateLogsByOkrCommentId(long orgId, long okrCommentId) {
    return okrUpdateLogDao.listOkrUpdateLogsByOkrCommentId(orgId, okrCommentId);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public int batchUpdateOkrRemindSetting(long orgId, List<OkrRemindSetting> okrRemindSettings) {
    if (!CollectionUtils.isEmpty(okrRemindSettings)) {
      for (OkrRemindSetting okrRemindSetting : okrRemindSettings) {
        OkrRemindSettingHelper.checkOkrRemindSettingInsertParams(okrRemindSetting);
      }
    }
    okrRemindSettingDao.deleteOkrRemindSettingByOrgId(orgId);

    return okrRemindSettingDao.batchInsertOkrRemindSetting(okrRemindSettings);
  }

  @Override
  @LogAround
  public OkrRemindSetting getOkrRemindSettingByOrgIdAndRemindType(long orgId, int remindType) {
    return okrRemindSettingDao.getOkrRemindSettingByOrgIdAndRemindType(orgId, remindType);
  }
}
