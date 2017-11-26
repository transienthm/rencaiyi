package hr.wozai.service.user.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.okr.dto.*;
import hr.wozai.service.user.client.okr.enums.*;
import hr.wozai.service.user.client.okr.facade.OkrFacade;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.TeamMember;
import hr.wozai.service.user.server.service.*;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.factory.OkrFacadeFactory;
import hr.wozai.service.user.server.test.utils.AopTargetUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.user.server.test.utils.InitializationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
 * @created 16/3/9
 */
public class OkrFacadeImplTest extends TestBase {
  @Autowired
  OnboardingFlowService onboardingFlowService;

  @Autowired
  OkrFacade okrFacade;

  @Mock
  OkrFacadeFactory spyOkrFacadeFactory;

  @Autowired
  OkrFacadeFactory okrFacadeFactory;

  @Autowired
  TeamService teamService;

  private long userId;
  private long orgId;
  private long teamId;
  private int okrType = OkrType.ORG.getCode();
  private String periodName;
  private String content = "test";
  private ObjectiveDTO objectiveDTO;
  private DirectorDTO directorDTO;
  private KeyResultDTO keyResultDTO;
  private ObjectivePeriodDTO objectivePeriodDTO;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(okrFacade), "okrFacadeFactory", spyOkrFacadeFactory);

    Mockito.doNothing().when(spyOkrFacadeFactory).sendMessageAndEmailWhenCreateObjective(
            Mockito.anyObject(), Mockito.anyObject(), Mockito.anyLong());
    Mockito.doNothing().when(spyOkrFacadeFactory).sendMessageAndEmailWhenUpdateObjective(
            Mockito.anyObject(), Mockito.anyObject(),
            Mockito.anyObject(), Mockito.anyObject(), Mockito.anyLong());
    Mockito.doNothing().when(spyOkrFacadeFactory).sendMessageAndEmailWhenCreateKR(
            Mockito.anyObject(), Mockito.anyObject(), Mockito.anyLong());
    Mockito.doNothing().when(spyOkrFacadeFactory).sendMessageAndEmailWhenUpdateKR(Mockito.anyObject(), Mockito.anyObject(),
            Mockito.anyObject(), Mockito.anyObject(), Mockito.anyLong());

    CoreUserProfile adminUser = InitializationUtils.initDefaultOrgAndFirstUser(onboardingFlowService);
    orgId = adminUser.getOrgId();

    TeamMember teamMember = InitializationUtils.initDefaultUserAndTeam(onboardingFlowService, teamService, orgId);
    teamId = teamMember.getTeamId();
    userId = teamMember.getUserId();

    Integer year = TimeUtils.getCurrentYearWithTimeZone(TimeUtils.BEIJING);
    Integer perionTimeSpanId = PeriodTimeSpan.First_half_year.getCode();
    periodName = PeriodTimeSpan.getNameByYearAndPeriodTimeSpan(year, PeriodTimeSpan.First_half_year);

    objectivePeriodDTO = new ObjectivePeriodDTO();
    objectivePeriodDTO.setOrgId(orgId);
    objectivePeriodDTO.setType(okrType);
    objectivePeriodDTO.setOwnerId(orgId);
    objectivePeriodDTO.setPeriodTimeSpanId(perionTimeSpanId);
    objectivePeriodDTO.setYear(year);
    objectivePeriodDTO.setName(periodName);
    objectivePeriodDTO.setCreatedUserId(userId);
    objectivePeriodDTO.setLastModifiedUserId(userId);

    objectiveDTO = new ObjectiveDTO();
    objectiveDTO.setOrgId(orgId);
    objectiveDTO.setParentObjectiveId(0L);
    objectiveDTO.setType(okrType);
    objectiveDTO.setOwnerId(orgId);
    objectiveDTO.setContent(content);
    objectiveDTO.setPriority(ObjectivePriority.P0.getCode());
    objectiveDTO.setIsAutoCalc(1);
    objectiveDTO.setProgressMetricType(ProgressMetric.MONEY.getCode());
    objectiveDTO.setStartingAmount("0");
    objectiveDTO.setGoalAmount("1000");
    objectiveDTO.setCurrentAmount("100");
    objectiveDTO.setUnit(ProgressMetric.MONEY.getDefaultUnit());
    objectiveDTO.setIsPrivate(0);
    objectiveDTO.setRegularRemindType(RegularRemindType.EVERY_WEEK.getCode());
    objectiveDTO.setCreatedUserId(userId);
    objectiveDTO.setLastModifiedUserId(userId);
    objectiveDTO.setComment("");

    directorDTO = new DirectorDTO();
    directorDTO.setOrgId(orgId);
    directorDTO.setUserId(userId);
    directorDTO.setCreatedUserId(userId);
    directorDTO.setLastModifiedUserId(userId);

    keyResultDTO = new KeyResultDTO();
    keyResultDTO.setOrgId(orgId);
    keyResultDTO.setContent(content);
    keyResultDTO.setPriority(ObjectivePriority.P0.getCode());
    keyResultDTO.setProgressMetricType(ProgressMetric.MONEY.getCode());
    keyResultDTO.setStartingAmount("0");
    keyResultDTO.setGoalAmount("1000");
    keyResultDTO.setCurrentAmount("100");
    keyResultDTO.setUnit(ProgressMetric.MONEY.getDefaultUnit());
    keyResultDTO.setDeadline(1000L);
    keyResultDTO.setCreatedUserId(userId);
    keyResultDTO.setLastModifiedUserId(userId);
  }

  @Test
  public void testObjectivePeriod() throws Exception {
    // create一个org类型的period
    LongDTO objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);
    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), objectivePeriodId.getServiceStatusDTO().getCode());
    Assert.assertNotEquals(0L, objectivePeriodId.getData());

    //list
    ObjectivePeriodListDTO result = okrFacade.listObjectivePeriod(orgId, okrType, orgId, 0L, 0L);
    Assert.assertEquals(result.getPeriodDTOList().size(), 1);

    ObjectivePeriodDTO inDb = result.getPeriodDTOList().get(0);
    Assert.assertEquals(inDb.getName(), periodName);

    // update

    Integer year = TimeUtils.getCurrentYearWithTimeZone(TimeUtils.BEIJING);
    Integer perionTimeSpanId = PeriodTimeSpan.First_half_year.getCode();
    String updateName = PeriodTimeSpan.getNameByYearAndPeriodTimeSpan(year, PeriodTimeSpan.August);
    inDb.setPeriodTimeSpanId(perionTimeSpanId);
    inDb.setName(updateName);

    okrFacade.updateObjectivePeriod(inDb, 0L, 0L);
    ObjectivePeriodDTO objectivePeriod = okrFacade.getObjectivePeriod(orgId, objectivePeriodId.getData(), -1L, -1L);
    Assert.assertEquals(updateName, objectivePeriod.getName());

    // delete
    okrFacade.deleteObjectivePeriod(orgId, objectivePeriodId.getData(), 0L, 0L);
    result = okrFacade.listObjectivePeriod(orgId, okrType, orgId, 0L, 0L);
    Assert.assertEquals(result.getPeriodDTOList().size(), 0);

    okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);

    objectivePeriodDTO.setPeriodTimeSpanId(PeriodTimeSpan.July.getCode());
    String secondName = PeriodTimeSpan.getNameByYearAndPeriodTimeSpan(year, PeriodTimeSpan.July);
    objectivePeriodDTO.setName(secondName);
    okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);

    result = okrFacade.listObjectivePeriod(orgId, okrType, orgId, 0L, 0L);
    Assert.assertEquals(result.getPeriodDTOList().size(), 2);
    inDb = result.getPeriodDTOList().get(0);
    Assert.assertEquals(secondName, inDb.getName());

    // create一个team类型的period
    objectivePeriodDTO.setType(OkrType.TEAM.getCode());
    objectivePeriodDTO.setOwnerId(teamId);
    objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);
    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), objectivePeriodId.getServiceStatusDTO().getCode());

    // create一个user类型的period
    objectivePeriodDTO.setType(OkrType.PERSON.getCode());
    objectivePeriodDTO.setOwnerId(userId);
    objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);
    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), objectivePeriodId.getServiceStatusDTO().getCode());
  }

  @Test
  public void testObjectivePeriodWithException() throws Exception {
    objectivePeriodDTO.setType(100);
    LongDTO objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), objectivePeriodId.getServiceStatusDTO().getCode());

    objectivePeriodDTO.setType(OkrType.ORG.getCode());
    objectivePeriodDTO.setYear(null);
    VoidDTO voidDTO = okrFacade.updateObjectivePeriod(objectivePeriodDTO, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_INVALID_PARAM.getCode(), voidDTO.getServiceStatusDTO().getCode());

    ObjectivePeriodDTO periodDTO = okrFacade.getObjectivePeriod(orgId, -1L, -1L, -1L);
    Assert.assertEquals(ServiceStatus.OKR_OBJECTIVE_PERIOD_NOT_FOUND.getCode(), periodDTO.getServiceStatusDTO().getCode());
  }

  @Test
  public void testObjectivePeriodWithWrongProjectTeamId() throws Exception {
    objectivePeriodDTO.setType(OkrType.PROJECT_TEAM.getCode());
    objectivePeriodDTO.setOwnerId(199L);
    LongDTO objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);
    Assert.assertEquals(ServiceStatus.UO_PROJECT_TEAM_NOT_FOUND.getCode(),
            objectivePeriodId.getServiceStatusDTO().getCode());
  }

  @Test
  public void testCreateObjective() throws Exception {
    // create objective period
    LongDTO objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, userId, userId);
    Assert.assertNotNull(objectivePeriodId.getData());

    // create objective
    objectiveDTO.setObjectivePeriodId(objectivePeriodId.getData());
    directorDTO.setUserId(userId);
    objectiveDTO.setDirectorDTOList(Arrays.asList(directorDTO));
    LongDTO objectiveId = okrFacade.createObjective(objectiveDTO, userId, userId);
    Assert.assertNotEquals(0, objectiveId.getData());

    // get objective
    ObjectiveDTO inDb = okrFacade.getObjective(orgId, objectiveId.getData(), userId, userId);
    Assert.assertEquals(inDb.getContent(), content);
    Assert.assertEquals(inDb.getDirectorDTOList().size(), 1);
    Assert.assertEquals(new BigDecimal(0), new BigDecimal(inDb.getProgress()));
    Assert.assertEquals(RegularRemindType.EVERY_WEEK.getCode().intValue(), inDb.getRegularRemindType().intValue());

    ObjectiveListDTO search = okrFacade.searchObjectiveByKeywordInOrder(orgId, 0L, "test", userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), search.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, search.getObjectiveDTOList().size());

    search = okrFacade.searchObjectiveByKeywordInOrder(orgId, objectiveId.getData(), "test", userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), search.getServiceStatusDTO().getCode());
    Assert.assertEquals(0, search.getObjectiveDTOList().size());

    // update
    inDb.setContent("update");
    inDb.setDirectorDTOList(new ArrayList<>());
    okrFacade.updateObjective(inDb, userId, userId);
    inDb = okrFacade.getObjective(orgId, objectiveId.getData(), userId, userId);
    Assert.assertEquals(inDb.getContent(), "update");
    Assert.assertEquals(0, inDb.getDirectorDTOList().size());

    inDb.setDirectorDTOList(Arrays.asList(directorDTO));
    okrFacade.updateObjective(inDb, userId, userId);

    ObjectiveListDTO objectiveListDTO = okrFacade.listObjective(
            orgId, okrType, orgId, objectivePeriodId.getData(), false, 1, 1, userId, userId);
    Assert.assertEquals(1, objectiveListDTO.getObjectiveDTOList().size());

    objectiveListDTO = okrFacade.listObjective(
            orgId, okrType, orgId, objectivePeriodId.getData(), true, 1, 1, userId, userId);
    Assert.assertEquals(1, objectiveListDTO.getObjectiveDTOList().size());

    objectiveListDTO = okrFacade.listObjective(
            orgId, okrType, orgId, objectivePeriodId.getData(), false,
            ProgressStatus.NOT_BEGIN.getCode(), 1, userId, userId);
    Assert.assertEquals(1, objectiveListDTO.getObjectiveDTOList().size());

    objectiveListDTO = okrFacade.listObjective(
            orgId, okrType, orgId, objectivePeriodId.getData(), false,
            ProgressStatus.ON_GOING.getCode(), 1, userId, userId);
    Assert.assertEquals(0, objectiveListDTO.getObjectiveDTOList().size());

    objectiveListDTO = okrFacade.listObjectivesByObjectiveIds(orgId, Arrays.asList(objectiveId.getData()), userId, userId);
    Assert.assertEquals(1, objectiveListDTO.getObjectiveDTOList().size());

    ObjectivePeriodDTO periodDTO = okrFacade.getObjectivePeriodWithObjectiveId(orgId, objectiveId.getData(), userId, userId);
    Assert.assertEquals(objectivePeriodId.getData(), periodDTO.getObjectivePeriodId().longValue());

    objectiveListDTO = okrFacade.filterObjectives(orgId, -1,
            ProgressStatus.NOT_BEGIN.getCode(), false,
            ObjectiveOrderItem.DEADLINE.getCode(), 1, 10, userId, userId);
    Assert.assertEquals(1, objectiveListDTO.getObjectiveDTOList().size());

    // delete
    okrFacade.deleteObjective(orgId, objectiveId.getData(), userId, userId);
    inDb = okrFacade.getObjective(orgId, objectiveId.getData(), userId, userId);
    Assert.assertNull(inDb.getDirectorDTOList());
    Assert.assertNull(inDb.getKeyResultDTOList());
  }

  @Test
  public void testGetObjectiveWithDiffTypePeriod() throws Exception {
    // create team objective period
    objectivePeriodDTO.setType(OkrType.TEAM.getCode());
    objectivePeriodDTO.setOwnerId(teamId);
    LongDTO objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, userId, userId);
    Assert.assertNotNull(objectivePeriodId.getData());

    // create objective
    objectiveDTO.setObjectivePeriodId(objectivePeriodId.getData());
    LongDTO objectiveId = okrFacade.createObjective(objectiveDTO, userId, userId);
    Assert.assertNotEquals(0, objectiveId.getData());

    // get objective
    ObjectiveDTO inDb = okrFacade.getObjective(orgId, objectiveId.getData(), userId, userId);
    Assert.assertEquals(inDb.getContent(), content);

    // create user objective period
    objectivePeriodDTO.setType(OkrType.PERSON.getCode());
    objectivePeriodDTO.setOwnerId(userId);
    objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, userId, userId);
    Assert.assertNotNull(objectivePeriodId.getData());

    // create objective
    objectiveDTO.setObjectivePeriodId(objectivePeriodId.getData());
    objectiveId = okrFacade.createObjective(objectiveDTO, userId, userId);
    Assert.assertNotEquals(0, objectiveId.getData());

    // get objective
    inDb = okrFacade.getObjective(orgId, objectiveId.getData(), userId, userId);
    Assert.assertEquals(inDb.getContent(), content);

    ObjectiveListDTO objectiveListDTO = okrFacade.filterObjectives(orgId, -1,
            ProgressStatus.NOT_BEGIN.getCode(), false,
            ObjectiveOrderItem.DEADLINE.getCode(), 1, 10, userId, userId);
    Assert.assertEquals(2, objectiveListDTO.getObjectiveDTOList().size());

    objectiveListDTO = okrFacade.filterObjectives(orgId, -1,
            ProgressStatus.NOT_BEGIN.getCode(), false,
            ObjectiveOrderItem.PROGRESS.getCode(), 1, 10, userId, userId);
    Assert.assertEquals(2, objectiveListDTO.getObjectiveDTOList().size());

    objectiveListDTO = okrFacade.filterObjectives(orgId, -1,
            ProgressStatus.NOT_BEGIN.getCode(), false,
            ObjectiveOrderItem.PROGRESS_REVERSE.getCode(), 1, 10, userId, userId);
    Assert.assertEquals(2, objectiveListDTO.getObjectiveDTOList().size());
  }

  @Test
  public void testSearchObjectivesWithEmptyKeyword() throws Exception {
    // create team objective period
    objectivePeriodDTO.setType(OkrType.TEAM.getCode());
    objectivePeriodDTO.setOwnerId(teamId);
    LongDTO objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, userId, userId);
    Assert.assertNotNull(objectivePeriodId.getData());

    // create objective
    objectiveDTO.setObjectivePeriodId(objectivePeriodId.getData());
    LongDTO objectiveId = okrFacade.createObjective(objectiveDTO, userId, userId);
    Assert.assertNotEquals(0, objectiveId.getData());

    ObjectiveListDTO result = okrFacade.searchObjectiveByKeywordInOrder(orgId, 0L, "", userId, userId);
    Assert.assertEquals(1, result.getObjectiveDTOList().size());
  }

  @Test
  public void testObjectiveWithException() throws Exception {
    objectiveDTO.setObjectivePeriodId(-1L);
    LongDTO objectiveId = okrFacade.createObjective(objectiveDTO, 0L, 0L);
    Assert.assertEquals(ServiceStatus.OKR_OBJECTIVE_PERIOD_NOT_FOUND.getCode(), objectiveId.getServiceStatusDTO().getCode());
  }

  @Test
  public void testMoveObjective() throws Exception {
    // create objective period
    LongDTO objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);
    Assert.assertNotNull(objectivePeriodId.getData());

    // create objective
    objectiveDTO.setObjectivePeriodId(objectivePeriodId.getData());
    long objectiveId1 = okrFacade.createObjective(objectiveDTO, 0L, 0L).getData();
    Assert.assertNotEquals(0, objectiveId1);
    long objectiveId2 = okrFacade.createObjective(objectiveDTO, 0L, 0L).getData();
    Assert.assertNotEquals(0, objectiveId2);
    long objectiveId3 = okrFacade.createObjective(objectiveDTO, 0L, 0L).getData();
    Assert.assertNotEquals(0, objectiveId3);

    ObjectiveListDTO objectiveListDTO = okrFacade.listObjectivesByObjectiveIds(
            orgId, Arrays.asList(objectiveId1, objectiveId2, objectiveId3), -1, -1L);
    Assert.assertEquals(3, objectiveListDTO.getObjectiveDTOList().size());
    ObjectiveDTO obj1 = objectiveListDTO.getObjectiveDTOList().get(0);
    ObjectiveDTO obj2 = objectiveListDTO.getObjectiveDTOList().get(1);
    ObjectiveDTO obj3 = objectiveListDTO.getObjectiveDTOList().get(2);
    Assert.assertEquals(objectiveId1, obj1.getObjectiveId().longValue());
    Assert.assertEquals(objectiveId2, obj2.getObjectiveId().longValue());
    Assert.assertEquals(objectiveId3, obj3.getObjectiveId().longValue());
    Assert.assertEquals(1, obj1.getOrderIndex().intValue());
    Assert.assertEquals(2, obj2.getOrderIndex().intValue());
    Assert.assertEquals(3, obj3.getOrderIndex().intValue());

    okrFacade.moveObjective(orgId, objectiveId3, 1, -1L, -1L);
    objectiveListDTO = okrFacade.listObjectivesByObjectiveIds(
            orgId, Arrays.asList(objectiveId1, objectiveId2, objectiveId3), -1, -1L);
    Assert.assertEquals(3, objectiveListDTO.getObjectiveDTOList().size());
    obj1 = objectiveListDTO.getObjectiveDTOList().get(0);
    obj2 = objectiveListDTO.getObjectiveDTOList().get(1);
    obj3 = objectiveListDTO.getObjectiveDTOList().get(2);
    Assert.assertEquals(objectiveId1, obj1.getObjectiveId().longValue());
    Assert.assertEquals(objectiveId2, obj2.getObjectiveId().longValue());
    Assert.assertEquals(objectiveId3, obj3.getObjectiveId().longValue());
    Assert.assertEquals(2, obj1.getOrderIndex().intValue());
    Assert.assertEquals(3, obj2.getOrderIndex().intValue());
    Assert.assertEquals(1, obj3.getOrderIndex().intValue());
  }

  @Test
  public void testKeyResult() throws Exception {
    // create objective period
    LongDTO objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);
    Assert.assertNotNull(objectivePeriodId.getData());

    // create objective
    objectiveDTO.setObjectivePeriodId(objectivePeriodId.getData());
    objectiveDTO.setDirectorDTOList(Arrays.asList(directorDTO));
    LongDTO objectiveId = okrFacade.createObjective(objectiveDTO, 0L, 0L);
    Assert.assertNotNull(objectiveId.getData());

    // create key result
    keyResultDTO.setObjectiveId(objectiveId.getData());
    keyResultDTO.setDirectorDTOList(Arrays.asList(directorDTO));
    LongDTO keyResultId = okrFacade.createKeyResult(keyResultDTO, 0L, 0L);
    Assert.assertNotNull(keyResultId.getData());

    // get key result
    KeyResultDTO inDb = okrFacade.getKeyResult(orgId, keyResultId.getData(), 0L, 0L);
    Assert.assertEquals(inDb.getContent(), content);
    Assert.assertEquals(inDb.getDirectorDTOList().get(0).getUserId().longValue(), userId);
    Assert.assertEquals(inDb.getDeadline().longValue(), 1000L);

    // update key result
    inDb.setContent("update");
    inDb.setPriority(ObjectivePriority.P1.getCode());
    okrFacade.updadteKeyResult(inDb, 0L, 0L);
    inDb = okrFacade.getKeyResult(orgId, keyResultId.getData(), 0L, 0L);
    Assert.assertEquals(inDb.getContent(), "update");

    // get objective
    ObjectiveDTO objInDb = okrFacade.getObjective(orgId, objectiveId.getData(), userId, userId);
    Assert.assertEquals(1, objInDb.getKeyResultDTOList().size());
    Assert.assertEquals(1, objInDb.getDirectorDTOList().size());

    ObjectiveListDTO objectiveListDTO = okrFacade.filterObjectives(orgId, -1,
            ProgressStatus.ON_GOING.getCode(), false,
            ObjectiveOrderItem.DEADLINE.getCode(), 1, 10, userId, userId);
    Assert.assertEquals(1, objectiveListDTO.getObjectiveDTOList().size());

    ObjectivePeriodDTO periodDTO = okrFacade.getObjectivePeriodWithKeyResultId(orgId, keyResultId.getData(), userId, userId);
    Assert.assertEquals(objectivePeriodId.getData(), periodDTO.getObjectivePeriodId().longValue());

    // list director
    DirectorListDTO directorListDTO = okrFacade.listObjectiveOrKeyResultDirector(orgId,
            DirectorType.OBJECTIVE.getCode(), objInDb.getObjectiveId(), 0L, 0L);
    Assert.assertEquals(1, directorListDTO.getDirectorDTOList().size());
    Assert.assertEquals(userId, directorListDTO.getDirectorDTOList().get(0).getUserId().longValue());

    // update director
    /*DirectorDTO update = directorListDTO.getDirectorDTOList().get(0);
    update.setUserId(userId);
    okrFacade.updateObjectiveOrKeyResultDirector(orgId, DirectorType.OBJECTIVE.getCode(),
            objInDb.getObjectiveId(), directorListDTO, userId, 0L);
    objInDb = okrFacade.getObjective(orgId, objectiveId.getData(), 0L, 0L);
    Assert.assertEquals(userId, objInDb.getDirectorDTOList().get(0).getUserId().longValue());*/

    // delete key result
    okrFacade.deleteKeyResult(orgId, keyResultId.getData(), userId, 0L);
    objInDb = okrFacade.getObjective(orgId, objectiveId.getData(), userId, userId);
    Assert.assertEquals(0, objInDb.getKeyResultDTOList().size());
    inDb = okrFacade.getKeyResult(orgId, keyResultId.getData(), 0L, 0L);
    Assert.assertNull(inDb.getContent());
  }

  @Test
  public void testOkrCommentAndOkrUpdateLog() throws Exception {
    // create objective period
    LongDTO objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);
    Assert.assertNotNull(objectivePeriodId.getData());

    // create objective
    objectiveDTO.setObjectivePeriodId(objectivePeriodId.getData());
    directorDTO.setUserId(userId);
    objectiveDTO.setDirectorDTOList(Arrays.asList(directorDTO));
    LongDTO objectiveId = okrFacade.createObjective(objectiveDTO, 0L, 0L);
    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), objectiveId.getServiceStatusDTO().getCode());
    Assert.assertNotEquals(0, objectiveId.getData());

    // get objective
    ObjectiveDTO inDb = okrFacade.getObjective(orgId, objectiveId.getData(), userId, userId);
    Assert.assertEquals(inDb.getContent(), content);
    Assert.assertEquals(inDb.getDirectorDTOList().size(), 1);
    Assert.assertEquals(new BigDecimal(0), new BigDecimal(inDb.getProgress()));

    inDb.setPriority(ObjectivePriority.P2.getCode());
    inDb.setComment("upadte objective");
    VoidDTO voidDTO = okrFacade.updateObjective(inDb, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    OkrCommentListDTO result = okrFacade.listOkrComment(
            orgId, objectiveId.getData(), 0L, 1, Integer.MAX_VALUE, userId, userId);
    List<OkrCommentDTO> commentDTOList = result.getOkrCommentDTOList();
    Assert.assertEquals(2, commentDTOList.size());
    List<OkrUpdateLogDTO> okrUpdateLogDTOs = commentDTOList.get(0).getOkrUpdateLogDTOList();
    Assert.assertEquals(1, okrUpdateLogDTOs.size());

    keyResultDTO.setObjectiveId(objectiveId.getData());
    keyResultDTO.setDirectorDTOList(Arrays.asList(directorDTO));
    LongDTO keyResultId = okrFacade.createKeyResult(keyResultDTO, 0L, 0L);
    Assert.assertNotNull(keyResultId.getData());

    // update key result
    keyResultDTO.setKeyResultId(keyResultId.getData());
    keyResultDTO.setPriority(ObjectivePriority.P1.getCode());
    keyResultDTO.setComment("update keyresult");
    keyResultDTO.setLastModifiedUserId(userId);
    okrFacade.updadteKeyResult(keyResultDTO, userId, 0L);

    result = okrFacade.listOkrComment(
            orgId, objectiveId.getData(), 0L, 1, Integer.MAX_VALUE, userId, userId);
    commentDTOList = result.getOkrCommentDTOList();
    Assert.assertEquals(4, commentDTOList.size());
    okrUpdateLogDTOs = commentDTOList.get(0).getOkrUpdateLogDTOList();
    System.out.println(okrUpdateLogDTOs);
    Assert.assertEquals(1, okrUpdateLogDTOs.size());
  }

  @Test
  public void testParentObjective() throws Exception {
    // create objective period
    LongDTO objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);
    Assert.assertNotNull(objectivePeriodId.getData());

    // create objective
    objectiveDTO.setObjectivePeriodId(objectivePeriodId.getData());
    directorDTO.setUserId(userId);
    objectiveDTO.setDirectorDTOList(Arrays.asList(directorDTO));
    LongDTO parentObjectiveId = okrFacade.createObjective(objectiveDTO, 0L, 0L);
    Assert.assertNotEquals(0, parentObjectiveId.getData());

    objectiveDTO.setParentObjectiveId(parentObjectiveId.getData());
    LongDTO objectiveid= okrFacade.createObjective(objectiveDTO, 0L, 0L);
    Assert.assertNotEquals(0, objectiveid.getData());

    objectiveDTO.setParentObjectiveId(objectiveid.getData());
    LongDTO subordinateObjectiveId = okrFacade.createObjective(objectiveDTO, 0L, 0L);
    Assert.assertNotEquals(0, subordinateObjectiveId.getData());

    ObjectiveListDTO ancesters = okrFacade.listAncesterObjectives(orgId, objectiveid.getData(), userId, userId);
    ObjectiveListDTO subordiantes = okrFacade.listFirstLevelSubordinateObjectives(orgId, objectiveid.getData(), userId, userId);

    Assert.assertEquals(1, ancesters.getObjectiveDTOList().size());
    Assert.assertEquals(parentObjectiveId.getData(), ancesters.getObjectiveDTOList().get(0).getObjectiveId().longValue());
    Assert.assertEquals(1, subordiantes.getObjectiveDTOList().size());
    Assert.assertEquals(subordinateObjectiveId.getData(),
            subordiantes.getObjectiveDTOList().get(0).getObjectiveId().longValue());
  }

  @Test
  public void testGetBirdViewAndOkrComment() throws Exception {
    objectiveDTO.setDeadline(10L);
    // create objective period
    LongDTO objectivePeriodId = okrFacade.createObjectivePeriod(objectivePeriodDTO, 0L, 0L);
    Assert.assertNotNull(objectivePeriodId.getData());

    objectiveDTO.setObjectivePeriodId(objectivePeriodId.getData());
    directorDTO.setUserId(userId);
    LongDTO parentObjectiveId = okrFacade.createObjective(objectiveDTO, 0L, 0L);
    Assert.assertNotEquals(0, parentObjectiveId.getData());

    objectiveDTO.setParentObjectiveId(parentObjectiveId.getData());
    LongDTO objectiveId= okrFacade.createObjective(objectiveDTO, 0L, 0L);
    Assert.assertNotEquals(0, objectiveId.getData());

    objectiveDTO.setParentObjectiveId(objectiveId.getData());
    LongDTO subId1 = okrFacade.createObjective(objectiveDTO, 0L, 0L);
    Assert.assertNotEquals(0, subId1.getData());

    objectiveDTO.setParentObjectiveId(objectiveId.getData());
    LongDTO subId2 = okrFacade.createObjective(objectiveDTO, 0L, 0L);
    Assert.assertNotEquals(0, subId2.getData());

    ObjectiveTreeDTO objectiveTreeDTO = okrFacade.getBirdViewByObjectiveId(orgId, objectiveId.getData(), userId, userId);
    List<List<ObjectiveDTO>> objs = objectiveTreeDTO.getObjectiveTrees();
    Assert.assertEquals(3, objs.size());
    Assert.assertEquals(parentObjectiveId.getData(), objs.get(0).get(0).getObjectiveId().longValue());
    Assert.assertEquals(objectiveId.getData(), objs.get(1).get(0).getObjectiveId().longValue());
    Assert.assertEquals(2, objs.get(2).size());

    objectiveTreeDTO = okrFacade.getBirdViewByObjectiveId(orgId, parentObjectiveId.getData(), userId, userId);
    Assert.assertEquals(2, objectiveTreeDTO.getObjectiveTrees().size());

    ObjectiveListDTO objectiveListDTO = okrFacade.listObjectivesByStartAndEndDeadline(orgId, 0L, Long.MAX_VALUE);
    Assert.assertEquals(4, objectiveListDTO.getObjectiveDTOList().size());

    OkrCommentDTO okrCommentDTO = new OkrCommentDTO();
    okrCommentDTO.setOrgId(orgId);
    okrCommentDTO.setObjectiveId(objectiveId.getData());
    okrCommentDTO.setUserId(userId);
    okrCommentDTO.setContent("test");
    okrCommentDTO.setCreatedUserId(userId);

    LongDTO longDTO = okrFacade.addOkrComment(orgId, okrCommentDTO, userId, userId);
    Assert.assertNotNull(longDTO.getData());

    okrFacade.updateOkrComment(orgId, longDTO.getData(), "update", userId, userId);

    okrFacade.deleteOkrComment(orgId, longDTO.getData(), userId, userId);

    ObjectiveDTO inDb = okrFacade.getObjective(orgId, subId2.getData(), userId, userId);
    inDb.setComment("");
    inDb.setParentObjectiveId(parentObjectiveId.getData());
    okrFacade.updateObjective(inDb, userId, userId);

    OkrCommentListDTO result = okrFacade.listOkrComment(
            orgId, inDb.getObjectiveId(), 0L, 1, 10, userId, userId);
    Assert.assertEquals(2, result.getOkrCommentDTOList().size());
  }

  @Test
  public void testListOkrRemindSettingsByOrgId() {
    OkrRemindSettingListDTO result = okrFacade.listOkrRemindSettingsByOrgId(orgId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), result.getServiceStatusDTO().getCode());

    List<OkrRemindSettingDTO> remindSettingDTOs = result.getOkrRemindSettingDTOList();
    for (OkrRemindSettingDTO okrRemindSettingDTO : remindSettingDTOs) {
      okrRemindSettingDTO.setOrgId(orgId);
      okrRemindSettingDTO.setCreatedUserId(userId);
    }
    VoidDTO voidDTO = okrFacade.batchUpdateOkrRemindSettings(orgId, remindSettingDTOs, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());
  }
}