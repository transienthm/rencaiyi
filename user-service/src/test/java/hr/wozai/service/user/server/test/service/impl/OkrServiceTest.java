package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.okr.enums.*;
import hr.wozai.service.user.server.component.OkrNotificationTask;
import hr.wozai.service.user.server.enums.OkrRemindType;
import hr.wozai.service.user.server.model.common.RemindSetting;
import hr.wozai.service.user.server.model.okr.*;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Team;
import hr.wozai.service.user.server.model.userorg.TeamMember;
import hr.wozai.service.user.server.service.OkrService;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.service.UserProfileService;
import hr.wozai.service.user.server.test.utils.AopTargetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
public class OkrServiceTest extends TestBase {

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  OkrService okrService;

  @Mock
  UserProfileService spyUserProfileService;

  @Autowired
  UserProfileService userProfileService;

  @Mock
  TeamService spyTeamService;

  @Autowired
  TeamService teamService;

  @Autowired
  OkrNotificationTask okrNotificationTask;

  private long orgId = 10L;
  private long userId = 10L;
  private long teamId = 0L;
  private ObjectivePeriod objectivePeriod;
  private Objective objective;
  private KeyResult keyResult;
  private Director director;
  private BigDecimal beginProgress = new BigDecimal("0");
  private BigDecimal endProgress = new BigDecimal("1");

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(okrService), "userProfileService", spyUserProfileService);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(okrService), "teamService", spyTeamService);

    CoreUserProfile coreUserProfile = new CoreUserProfile();
    coreUserProfile.setFullName("test");

    Mockito.doReturn(coreUserProfile).when(spyUserProfileService).
        getCoreUserProfileByOrgIdAndUserId(Mockito.anyLong(), Mockito.anyLong());
    Mockito.doReturn(Arrays.asList(coreUserProfile)).when(spyUserProfileService).
            listCoreUserProfileByOrgIdAndUserId(Mockito.anyLong(), Mockito.anyList());

    Integer year = TimeUtils.getCurrentYearWithTimeZone(TimeUtils.BEIJING);
    Integer perionTimeSpanId = PeriodTimeSpan.First_half_year.getCode();
    String name = PeriodTimeSpan.getNameByYearAndPeriodTimeSpan(year, PeriodTimeSpan.First_half_year);
    objectivePeriod = new ObjectivePeriod();
    objectivePeriod.setOrgId(orgId);
    objectivePeriod.setType(OkrType.ORG.getCode());
    objectivePeriod.setOwnerId(orgId);
    objectivePeriod.setPeriodTimeSpanId(perionTimeSpanId);
    objectivePeriod.setYear(year);
    objectivePeriod.setName(name);
    objectivePeriod.setCreatedUserId(userId);
    objectivePeriod.setLastModifiedUserId(userId);

    objective = new Objective();
    objective.setOrgId(orgId);
    objective.setParentObjectiveId(0L);
    objective.setType(OkrType.ORG.getCode());
    objective.setOwnerId(orgId);
    objective.setContent("test");
    objective.setPriority(ObjectivePriority.P0.getCode());
    objective.setIsAutoCalc(1);
    objective.setIsPrivate(0);
    objective.setDeadline(1000L);
    objective.setRegularRemindType(RegularRemindType.NOT.getCode());
    objective.setCreatedUserId(userId);
    objective.setLastModifiedUserId(userId);

    keyResult = new KeyResult();
    keyResult.setOrgId(orgId);
    keyResult.setContent("test");
    keyResult.setPriority(ObjectivePriority.P0.getCode());
    keyResult.setProgressMetricType(ProgressMetric.PERCENT.getCode());
    keyResult.setStartingAmount(new BigDecimal(0L));
    keyResult.setGoalAmount(new BigDecimal(100L));
    keyResult.setCurrentAmount(new BigDecimal(0L));
    keyResult.setUnit(ProgressMetric.PERCENT.getDefaultUnit());
    keyResult.setDeadline(1000L);
    keyResult.setCreatedUserId(userId);
    keyResult.setLastModifiedUserId(userId);

    director = new Director();
    director.setOrgId(orgId);
    director.setUserId(userId);
    director.setCreatedUserId(userId);
    director.setLastModifiedUserId(userId);

  }

  @Test
  public void testObjectivePeriodAll() throws Exception {
    // add objectivePeriod
    long objectivePeriodId = okrService.createObjectivePeriod(objectivePeriod);

    ObjectivePeriod objectivePeriod = okrService.getObjectivePeriod(orgId, objectivePeriodId);
    Assert.assertNotNull(objectivePeriod);

    // list objectivePeriod
    List<ObjectivePeriod> periodList = okrService.listObjectivePeriodByOrgIdAndOwnerId(orgId,
            objectivePeriod.getType(), objectivePeriod.getOwnerId());
    Assert.assertEquals(1, periodList.size());

    objectivePeriod.setName("update");
    okrService.updateObjectivePeriod(objectivePeriod);
    periodList = okrService.listObjectivePeriodByOrgIdAndOwnerId(orgId,
            objectivePeriod.getType(), objectivePeriod.getOwnerId());
    Assert.assertEquals(1, periodList.size());
    Assert.assertEquals("update", periodList.get(0).getName());

    objective.setObjectivePeriodId(objectivePeriodId);
    okrService.createObjectiveAndDirector(objective, Arrays.asList(director));

    // delete objectivePeriod
    okrService.deleteObjectivePeriod(orgId, objectivePeriodId, userId);
    periodList = okrService.listObjectivePeriodByOrgIdAndOwnerId(orgId,
            objectivePeriod.getType(), objectivePeriod.getOwnerId());
    Assert.assertEquals(0, periodList.size());
  }

  @Test
  public void testObjectiveAll() throws Exception {
    // add objectivePeriod
    long objectivePeriodId = okrService.createObjectivePeriod(objectivePeriod);

    // add objective
    objective.setObjectivePeriodId(objectivePeriodId);
    long objectiveId = okrService.createObjectiveAndDirector(objective, Arrays.asList(director));

    // get objective
    Objective inDb = okrService.getObjective(orgId, objectiveId);
    Assert.assertEquals(OkrType.ORG.getCode(), inDb.getType());
    Assert.assertEquals(orgId, inDb.getOwnerId().longValue());
    Assert.assertEquals("test", inDb.getContent());

    Assert.assertEquals(1, okrService.listDirectorsByObjectiveIds(
            orgId, DirectorType.OBJECTIVE.getCode(), Arrays.asList(objectiveId)).size());
    Assert.assertEquals(1, okrService.listObjectiveAndKeyResultDirectorsByObjectiveId(orgId, objectiveId).size());

    // list objective
    List<Objective> objectives = okrService.listObjectiveByTypeAndOwnerIdAndPeriodId(orgId,
            OkrType.ORG.getCode(), orgId, objectivePeriodId, 1, 1);
    Assert.assertEquals(objectives.size(), 1);

    Assert.assertEquals(1, okrService.listObjectivesByObjectiveIds(orgId, Arrays.asList(objectiveId)).size());

    objectives = okrService.listObjectivesByPriority(orgId, ObjectivePriority.P0.getCode(), 2);
    Assert.assertEquals(1, objectives.size());

    objectives = okrService.listObjectivesByStartAndEndDeadline(orgId, 1L, 1001L);
    Assert.assertEquals(1, objectives.size());

    // update
    inDb.setContent("update");
    inDb.setPriority(ObjectivePriority.P1.getCode());

    okrService.updateObjective(inDb, "");
    inDb = okrService.getObjective(orgId, objectiveId);
    Assert.assertEquals("update", inDb.getContent());
    Assert.assertEquals(1, inDb.getPriority().intValue());

    okrService.updateObjectiveAndDirectors(inDb, "", Arrays.asList(director), -1);
    Assert.assertEquals(1, okrService.listDirector(orgId, DirectorType.OBJECTIVE.getCode(), objectiveId).size());

    objective.setParentObjectiveId(objectiveId);
    okrService.createObjectiveAndDirector(objective, Arrays.asList(director));

    // delete
    okrService.deleteObjective(orgId, objectiveId, userId);

    thrown.expect(ServiceStatusException.class);
    okrService.getObjective(orgId, objectiveId);
  }

  @Test
  public void testUpdateObjectiveException() {
    // add objectivePeriod
    long objectivePeriodId = okrService.createObjectivePeriod(objectivePeriod);

    // add objective
    objective.setObjectivePeriodId(objectivePeriodId);
    long objectiveId = okrService.createObjectiveAndDirector(objective, Arrays.asList(director));

    objective.setParentObjectiveId(objectiveId);
    long objectiveId2 = okrService.createObjectiveAndDirector(objective, Arrays.asList(director));

    Objective inDb = okrService.getObjective(orgId, objectiveId);
    inDb.setParentObjectiveId(objectiveId2);

    thrown.expect(ServiceStatusException.class);
    okrService.updateObjective(inDb, "");

  }

  @Test
  public void testKeyResultAll() throws Exception {
    // add objectivePeriod
    long objectivePeriodId = okrService.createObjectivePeriod(objectivePeriod);

    // add objective
    objective.setObjectivePeriodId(objectivePeriodId);
    long objectiveId = okrService.createObjectiveAndDirector(objective, Arrays.asList(director));

    // add key result
    keyResult.setObjectiveId(objectiveId);
    long keyResultId = okrService.createKeyResultAndDirector(keyResult, Arrays.asList(director));

    Assert.assertNotNull(okrService.getKeyResult(orgId, keyResultId));

    // list key result
    List<KeyResult> keyResults = okrService.listKeyResultByOBjectiveId(orgId, objectiveId);
    Assert.assertEquals(1, keyResults.size());
    KeyResult inDb = keyResults.get(0);
    Assert.assertEquals("test", inDb.getContent());
    Assert.assertEquals(0, inDb.getPriority().intValue());

    keyResults = okrService.listKeyResultsByStartAndEndDeadline(orgId, 0L, 1001L);
    Assert.assertEquals(1, keyResults.size());

    keyResults = okrService.listSimpleKeyResultsByObjectiveIds(orgId, Arrays.asList(objectiveId));
    Assert.assertEquals(1, keyResults.size());

    // update
    inDb.setContent("update");
    inDb.setPriority(ObjectivePriority.P1.getCode());
    okrService.updateKeyResult(inDb, "");
    keyResults = okrService.listKeyResultByOBjectiveId(orgId, objectiveId);
    inDb = keyResults.get(0);
    Assert.assertEquals("update", inDb.getContent());
    Assert.assertEquals(1, inDb.getPriority().intValue());

    okrService.updateKeyResultAndDirectors(inDb, "", Arrays.asList(director), -1L);
    Assert.assertEquals(1, okrService.listDirector(orgId, DirectorType.KEYRESULT.getCode(), keyResultId).size());

    okrService.deleteKeyResult(orgId, keyResultId, userId);
    keyResults = okrService.listKeyResultByOBjectiveId(orgId, objectiveId);
    Assert.assertEquals(0, keyResults.size());

  }

  @Test
  public void testBatchInsertDirector() throws Exception {
    director.setType(DirectorType.OBJECTIVE.getCode());
    director.setObjectId(1L);
    okrService.batchInsertDirector(Arrays.asList(director));

    List<Director> directors = okrService.listDirector(orgId, DirectorType.OBJECTIVE.getCode(), 1L);
    Assert.assertEquals(1, directors.size());

    okrService.batchDeleteDirector(orgId, DirectorType.OBJECTIVE.getCode(), Arrays.asList(1L), userId);
    directors = okrService.listDirector(orgId, DirectorType.OBJECTIVE.getCode(), 1L);
    Assert.assertEquals(0, directors.size());
  }

  @Test
  public void testMoveObjective() throws Exception {
    // add objectivePeriod
    long objectivePeriodId = okrService.createObjectivePeriod(objectivePeriod);

    // add objective
    objective.setObjectivePeriodId(objectivePeriodId);
    long objectiveId1 = okrService.createObjectiveAndDirector(objective, Arrays.asList(director));
    long objectiveId2 = okrService.createObjectiveAndDirector(objective, Arrays.asList(director));
    long objectiveId3 = okrService.createObjectiveAndDirector(objective, Arrays.asList(director));

    List<Objective> objectives = okrService.listObjectivesByObjectiveIds(
            orgId, Arrays.asList(objectiveId1, objectiveId2, objectiveId3));
    Assert.assertEquals(3, objectives.size());
    Assert.assertEquals(objectiveId1, objectives.get(0).getObjectiveId().longValue());
    Assert.assertEquals(1, objectives.get(0).getOrderIndex().intValue());

    Assert.assertEquals(objectiveId2, objectives.get(1).getObjectiveId().longValue());
    Assert.assertEquals(2, objectives.get(1).getOrderIndex().intValue());

    Assert.assertEquals(objectiveId3, objectives.get(2).getObjectiveId().longValue());
    Assert.assertEquals(3, objectives.get(2).getOrderIndex().intValue());

    okrService.moveObjective(orgId, objectiveId1, 3);

    objectives = okrService.listObjectivesByObjectiveIds(
            orgId, Arrays.asList(objectiveId1, objectiveId2, objectiveId3));
    Assert.assertEquals(3, objectives.size());
    Assert.assertEquals(objectiveId1, objectives.get(0).getObjectiveId().longValue());
    Assert.assertEquals(3, objectives.get(0).getOrderIndex().intValue());

    Assert.assertEquals(objectiveId2, objectives.get(1).getObjectiveId().longValue());
    Assert.assertEquals(1, objectives.get(1).getOrderIndex().intValue());

    Assert.assertEquals(objectiveId3, objectives.get(2).getObjectiveId().longValue());
    Assert.assertEquals(2, objectives.get(2).getOrderIndex().intValue());

    okrService.moveObjective(orgId, objectiveId1, 1);
  }

  @Test
  public void testSearchObjectiveByKeywordInOrder() throws Exception {
    // add org okr
    long orgPeriod = okrService.createObjectivePeriod(objectivePeriod);
    objective.setObjectivePeriodId(orgPeriod);
    objective.setContent("test_org_okr");
    long orgObjective = okrService.createObjectiveAndDirector(objective, new ArrayList<>());

    // add personal okr
    objectivePeriod.setType(OkrType.PERSON.getCode());
    long personalPeriod = okrService.createObjectivePeriod(objectivePeriod);
    objective.setType(OkrType.PERSON.getCode());
    objective.setOwnerId(userId);
    objective.setObjectivePeriodId(personalPeriod);
    objective.setContent("test_person_okr");
    long personalObjective = okrService.createObjectiveAndDirector(objective, new ArrayList<>());

    // add creator's team's okr
    long teamId = 199L;
    objectivePeriod.setType(OkrType.TEAM.getCode());
    objectivePeriod.setOwnerId(teamId);
    long teamPeriod = okrService.createObjectivePeriod(objectivePeriod);
    objective.setType(OkrType.TEAM.getCode());
    objective.setOwnerId(teamId);
    objective.setObjectivePeriodId(teamPeriod);
    objective.setContent("test_team_okr");
    long teamObjective = okrService.createObjectiveAndDirector(objective, new ArrayList<>());

    // add parent team okr
    long parentTeamId = 299L;
    objectivePeriod.setType(OkrType.TEAM.getCode());
    objectivePeriod.setOwnerId(parentTeamId);
    long parentTeamPeriod = okrService.createObjectivePeriod(objectivePeriod);
    objective.setType(OkrType.TEAM.getCode());
    objective.setOwnerId(parentTeamId);
    objective.setObjectivePeriodId(parentTeamPeriod);
    objective.setContent("test_parent_team_okr");
    long parentObjective = okrService.createObjectiveAndDirector(objective, new ArrayList<>());

    // add other team okr
    long otherTeamId = 399L;
    objectivePeriod.setType(OkrType.TEAM.getCode());
    objectivePeriod.setOwnerId(otherTeamId);
    long otherTeamPeriod = okrService.createObjectivePeriod(objectivePeriod);
    objective.setType(OkrType.TEAM.getCode());
    objective.setOwnerId(otherTeamId);
    objective.setObjectivePeriodId(otherTeamPeriod);
    objective.setContent("test_other_team_okr");
    long otherTeamObjective = okrService.createObjectiveAndDirector(objective, new ArrayList<>());

    long otherTeamObjective2 = okrService.createObjectiveAndDirector(objective, new ArrayList<>());

    TeamMember teamMember = new TeamMember();
    teamMember.setTeamId(teamId);
    Mockito.doReturn(teamMember).when(spyTeamService).
            getTeamMemberByUserIdAndOrgId(Mockito.anyLong(), Mockito.anyLong());
    Team team = new Team();
    team.setTeamId(teamId);
    team.setParentTeamId(parentTeamId);
    Mockito.doReturn(team).when(spyTeamService).getTeamByTeamId(orgId, teamId);

    List<Objective> objectives = okrService.searchObjectiveByKeywordInOrder(orgId, 0L, "test", 2, -1L, userId);
    Assert.assertEquals(6, objectives.size());
    Assert.assertEquals(parentObjective, objectives.get(0).getObjectiveId().longValue());
    Assert.assertEquals(teamObjective, objectives.get(1).getObjectiveId().longValue());
    Assert.assertEquals(otherTeamObjective2, objectives.get(2).getObjectiveId().longValue());
    Assert.assertEquals(otherTeamObjective, objectives.get(3).getObjectiveId().longValue());
    Assert.assertEquals(personalObjective, objectives.get(4).getObjectiveId().longValue());
    Assert.assertEquals(orgObjective, objectives.get(5).getObjectiveId().longValue());

    Assert.assertEquals(0, okrService.searchObjectiveByKeywordInOrder(
            orgId, orgObjective, "test_org_okr", 2, -1L, userId).size());
  }

  @Test
  public void testListFirstLevelSubordinateObjectives() throws Exception {
    // add org okr
    long periodId = okrService.createObjectivePeriod(objectivePeriod);
    objective.setObjectivePeriodId(periodId);
    long ancester = okrService.createObjectiveAndDirector(objective, new ArrayList<>());

    objective.setParentObjectiveId(ancester);
    long objectiveId = okrService.createObjectiveAndDirector(objective, new ArrayList<>());

    objective.setParentObjectiveId(objectiveId);
    long subordinate = okrService.createObjectiveAndDirector(objective, new ArrayList<>());

    List<Objective> objectives = okrService.listFirstLevelSubordinateObjectives(orgId, ancester);
    Assert.assertEquals(1, objectives.size());
    Assert.assertEquals(objectiveId, objectives.get(0).getObjectiveId().longValue());

    objectives = okrService.listAncesterObjectives(orgId, subordinate);
    Assert.assertEquals(2, objectives.size());
  }

  @Test
  public void testAboutOkrCommentAndOkrUpdateLog() throws Exception {
    long objectiveId = 199L;

    OkrComment okrComment = new OkrComment();
    okrComment.setOrgId(orgId);
    okrComment.setObjectiveId(objectiveId);
    okrComment.setKeyResultId(0L);
    okrComment.setKeyResultContent("");
    okrComment.setUserId(userId);
    okrComment.setContent("content");
    okrComment.setCreatedUserId(userId);

    OkrUpdateLog okrUpdateLog = new OkrUpdateLog();
    okrUpdateLog.setOrgId(orgId);
    okrUpdateLog.setAttribute("attribute");
    okrUpdateLog.setBeforeValue("before");
    okrUpdateLog.setAfterValue("after");
    okrUpdateLog.setCreatedUserId(userId);

    long okrCommentId = okrService.addOkrCommentAndOkrUpdateLogs(okrComment, Arrays.asList(okrUpdateLog));

    List<OkrComment> okrComments = okrService.listOkrComment(orgId, objectiveId, 0L, 1, Integer.MAX_VALUE);
    Assert.assertEquals(okrCommentId, okrComments.get(0).getOkrCommentId().longValue());

    okrComment.setContent("update");
    okrComment.setLastModifiedUserId(userId);
    okrService.updateOkrComment(okrComment);

    okrComments = okrService.listOkrComment(orgId, objectiveId, 0L, 1, Integer.MAX_VALUE);
    Assert.assertEquals("update", okrComments.get(0).getContent());

    Assert.assertEquals(1, okrService.countOkrComment(orgId, objectiveId, 0L));

    List<OkrUpdateLog> okrUpdateLogs = okrService.listOkrUpdateLogsByOkrCommentId(orgId, okrCommentId);
    Assert.assertEquals(1, okrUpdateLogs.size());

    okrComment.setIsDeleted(1);
    okrComment.setLastModifiedUserId(userId);
    okrService.updateOkrComment(okrComment);

    okrComments = okrService.listOkrComment(orgId, objectiveId, 0L, 1, Integer.MAX_VALUE);
    Assert.assertEquals(0, okrComments.size());
  }

  @Test
  public void testOkrCommentWhenCreateAndUpdateObjective() throws Exception {
    // add objectivePeriod
    long objectivePeriodId = okrService.createObjectivePeriod(objectivePeriod);

    // add objective
    objective.setObjectivePeriodId(objectivePeriodId);
    long objectiveId = okrService.createObjectiveAndDirector(objective, Arrays.asList(director));

    Assert.assertEquals(1, okrService.listOkrComment(orgId, objectiveId, 0L, 1, Integer.MAX_VALUE).size());

    objective.setContent("parent objective");
    long parentObjectiveId = okrService.createObjectiveAndDirector(objective, new ArrayList<>());

    objective.setObjectiveId(objectiveId);
    objective.setParentObjectiveId(parentObjectiveId);
    objective.setIsPrivate(1);
    objective.setPriority(ObjectivePriority.P1.getCode());
    objective.setIsAutoCalc(0);
    objective.setProgressMetricType(ProgressMetric.PERCENT.getCode());
    objective.setStartingAmount(new BigDecimal(0));
    objective.setGoalAmount(new BigDecimal(100));
    objective.setCurrentAmount(new BigDecimal(0));
    objective.setUnit("%");
    objective.setDeadline(TimeUtils.getNowTimestmapInMillis());
    okrService.updateObjectiveAndDirectors(objective, "update objective private", new ArrayList<>(), userId);

    List<OkrComment> okrComments = okrService.listOkrComment(orgId, objectiveId, 0L, 1, Integer.MAX_VALUE);
    Assert.assertEquals(2, okrComments.size());

    List<OkrUpdateLog> okrUpdateLogs = okrService.listOkrUpdateLogsByOkrCommentId(orgId, okrComments.get(0).getOkrCommentId());
    Assert.assertEquals(7, okrUpdateLogs.size());
    for (OkrUpdateLog okrUpdateLog : okrUpdateLogs) {
      System.out.println(okrUpdateLog);
    }
  }

  @Test
  public void testOkrCommentWhenCreateAndUpdateKeyresult() throws Exception {
    // add objectivePeriod
    long objectivePeriodId = okrService.createObjectivePeriod(objectivePeriod);

    // add objective
    objective.setObjectivePeriodId(objectivePeriodId);
    long objectiveId = okrService.createObjectiveAndDirector(objective, Arrays.asList(director));

    keyResult.setObjectiveId(objectiveId);
    long keyResultId = okrService.createKeyResultAndDirector(keyResult, new ArrayList<>());

    List<OkrComment> okrComments = okrService.listOkrComment(orgId, objectiveId, 0L, 1, Integer.MAX_VALUE);
    Assert.assertEquals(2, okrComments.size());

    keyResult.setPriority(ObjectivePriority.P2.getCode());
    keyResult.setProgressMetricType(ProgressMetric.MONEY.getCode());
    keyResult.setStartingAmount(new BigDecimal(0L));
    keyResult.setGoalAmount(new BigDecimal(100L));
    keyResult.setCurrentAmount(new BigDecimal(10L));
    keyResult.setUnit(ProgressMetric.MONEY.getDefaultUnit());
    okrService.updateKeyResultAndDirectors(keyResult, "", Arrays.asList(director), userId);

    okrComments = okrService.listOkrComment(orgId, objectiveId, 0L, 1, Integer.MAX_VALUE);
    Assert.assertEquals(3, okrComments.size());

    List<OkrUpdateLog> okrUpdateLogs = okrService.listOkrUpdateLogsByOkrCommentId(orgId, okrComments.get(0).getOkrCommentId());
    Assert.assertEquals(3, okrUpdateLogs.size());
    for (OkrUpdateLog okrUpdateLog : okrUpdateLogs) {
      System.out.println(okrUpdateLog);
    }
  }

  @Test
  public void testOkrRemindSetting() {
    OkrRemindSetting objectiveRemind = okrService.getOkrRemindSettingByOrgIdAndRemindType(
            orgId, OkrRemindType.OBJECTIVE_DEADLINE.getCode());
    if (objectiveRemind == null) {
      objectiveRemind = new OkrRemindSetting();
      objectiveRemind.setOrgId(orgId);
      objectiveRemind.setRemindType(OkrRemindType.OBJECTIVE_DEADLINE.getCode());
      objectiveRemind.setFrequency(OkrRemindType.OBJECTIVE_DEADLINE.getDefaultFrequency());
      objectiveRemind.setCreatedUserId(userId);
    }

    OkrRemindSetting periodRemind = okrService.getOkrRemindSettingByOrgIdAndRemindType(
            orgId, OkrRemindType.OBJECTIVE_PERIOD_DEADLINE.getCode());
    if (periodRemind == null) {
      periodRemind = new OkrRemindSetting();
      periodRemind.setOrgId(orgId);
      periodRemind.setRemindType(OkrRemindType.OBJECTIVE_PERIOD_DEADLINE.getCode());
      periodRemind.setFrequency(OkrRemindType.OBJECTIVE_PERIOD_DEADLINE.getDefaultFrequency());
      periodRemind.setCreatedUserId(userId);
    }

    OkrRemindSetting keyResultRemind = okrService.getOkrRemindSettingByOrgIdAndRemindType(
            orgId, OkrRemindType.KEY_RESULT_DEADLINE.getCode());
    if (keyResultRemind == null) {
      keyResultRemind = new OkrRemindSetting();
      keyResultRemind.setOrgId(orgId);
      keyResultRemind.setRemindType(OkrRemindType.KEY_RESULT_DEADLINE.getCode());
      keyResultRemind.setFrequency(OkrRemindType.KEY_RESULT_DEADLINE.getDefaultFrequency());
      keyResultRemind.setCreatedUserId(userId);
    }

    List<OkrRemindSetting> remindSettings = new ArrayList<>();
    remindSettings.add(objectiveRemind);
    remindSettings.add(periodRemind);
    remindSettings.add(keyResultRemind);
    int result = okrService.batchUpdateOkrRemindSetting(orgId, remindSettings);
    Assert.assertEquals(3, result);

    OkrRemindSetting inDb = okrService.getOkrRemindSettingByOrgIdAndRemindType(
            orgId, OkrRemindType.OBJECTIVE_DEADLINE.getCode());
    Assert.assertEquals(inDb.getFrequency(), objectiveRemind.getFrequency());
  }
}