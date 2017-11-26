package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.okr.*;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
public interface OkrService {
  long createObjectivePeriod(ObjectivePeriod objectivePeriod);

  void deleteObjectivePeriod(long orgId, long objectivePeriodId, long actorUserId);

  void updateObjectivePeriod(ObjectivePeriod objectivePeriod);

  List<ObjectivePeriod> listObjectivePeriodByOrgIdAndOwnerId(long orgId, int type, long teamId);

  ObjectivePeriod getObjectivePeriod(long orgId, long objectivePeriodId);


  long createObjectiveAndDirector(Objective objective, List<Director> directors);

  void deleteObjective(long orgId, long objectiveId, long actorUserId);

  void updateObjective(Objective objective, String comment);

  void updateObjectiveAndDirectors(Objective objective, String comment, List<Director> directors, long actorUserId);

  Objective getObjective(long orgId, long objectiveId);

  List<Objective> listObjectiveByTypeAndOwnerIdAndPeriodId(
          long orgId, int type, long ownerId, long objectivePeriodId, int progressStatus, int orderBy);

  List<Objective> listObjectivesByObjectiveIds(long orgId, List<Long> objectiveIds);

  List<Objective> searchObjectiveByKeywordInOrder(
          long orgId, long objectiveId, String keyword, int type, long ownerId, long actorUserId);

  void moveObjective(long orgId, long objectiveId, int targetOrderIndex);

  List<Objective> listAncesterObjectives(long orgId, long objectiveId);

  List<Objective> listFirstLevelSubordinateObjectives(long orgId, long objectiveId);

  List<Objective> listObjectivesByStartAndEndDeadline(long orgId, long startDeadline, long endDeadline);

  List<Objective> listObjectivesByPriority(long orgId, int priority, int orderItem);

  long createKeyResultAndDirector(KeyResult keyResult, List<Director> directors);

  void deleteKeyResult(long orgId, long keyResultId, long actorUserId);

  void updateKeyResult(KeyResult keyResult, String comment);

  void updateKeyResultAndDirectors(KeyResult keyResult, String comment, List<Director> directors, long actorUserId);

  KeyResult getKeyResult(long orgId, long keyResultId);

  List<KeyResult> listKeyResultByOBjectiveId(long orgId, long objectiveId);

  List<KeyResult> listSimpleKeyResultsByObjectiveIds(long orgId, List<Long> objectiveIds);

  List<KeyResult> listKeyResultsByStartAndEndDeadline(long orgId, long startDeadline, long endDeadline);

  void batchInsertDirector(List<Director> directors);

  void batchDeleteDirector(long orgId, int type, List<Long> objectIds, long actorUserId);

  List<Director> listDirector(long orgId, int type, long objectId);

  List<Director> listDirectorsByObjectiveIds(long orgId, int directorType, List<Long> objectiveIds);

  List<Long> listObjectiveAndKeyResultDirectorsByObjectiveId(long orgId, long objetiveId);

  // ++++++++++++++++++++++OkrComment, OkrUpdateLog
  long addOkrCommentAndOkrUpdateLogs(OkrComment okrComment, List<OkrUpdateLog> okrUpdateLogs);

  long updateOkrComment(OkrComment okrComment);

  List<OkrComment> listOkrComment(long orgId, long objectiveId, long keyResultId, int pageNumber, int pageSize);

  long countOkrComment(long orgId, long objectiveId, long keyResultId);

  List<OkrUpdateLog> listOkrUpdateLogsByOkrCommentId(long orgId, long okrCommentId);

  // +++++++++++++++++OkrRemindSetting++++++++++++
  int batchUpdateOkrRemindSetting(long orgId, List<OkrRemindSetting> okrRemindSettings);

  OkrRemindSetting getOkrRemindSettingByOrgIdAndRemindType(long orgId, int remindType);
}
