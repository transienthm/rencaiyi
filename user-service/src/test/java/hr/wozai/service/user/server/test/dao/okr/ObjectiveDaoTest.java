package hr.wozai.service.user.server.test.dao.okr;

import hr.wozai.service.user.client.okr.enums.ProgressMetric;
import hr.wozai.service.user.client.okr.enums.RegularRemindType;
import hr.wozai.service.user.server.dao.okr.ObjectiveDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.client.okr.enums.ObjectivePriority;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.server.model.okr.Objective;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/7
 */
public class ObjectiveDaoTest extends TestBase {
  @Autowired
  ObjectiveDao objectiveDao;

  private long orgId = 199L;
  private long userId = 199L;
  private long objectivePeriodId = 199L;
  private Objective objective;

  @Before
  public void setUp() throws Exception {
    objective = new Objective();
    objective.setOrgId(orgId);
    objective.setParentObjectiveId(0L);
    objective.setType(OkrType.ORG.getCode());
    objective.setOwnerId(orgId);
    objective.setContent("test");
    objective.setPriority(ObjectivePriority.P0.getCode());
    objective.setObjectivePeriodId(objectivePeriodId);
    objective.setIsAutoCalc(0);
    objective.setProgressMetricType(ProgressMetric.MONEY.getCode());
    objective.setStartingAmount(new BigDecimal("0"));
    objective.setGoalAmount(new BigDecimal("1000"));
    objective.setCurrentAmount(new BigDecimal("1"));
    objective.setUnit("元");
    objective.setDeadline(1000L);
    objective.setOrderIndex(1);
    objective.setIsPrivate(0);
    objective.setRegularRemindType(RegularRemindType.NOT.getCode());
    objective.setCreatedUserId(userId);
    objective.setLastModifiedUserId(userId);
  }

  @Test
  public void testInsertObjective() throws Exception {
    long objectiveId = objectiveDao.insertObjective(objective);

    Objective inDb = objectiveDao.findObjective(orgId, objectiveId, 0);
    Assert.assertNotNull(inDb);
    Assert.assertEquals(0L, inDb.getParentObjectiveId().longValue());
    Assert.assertEquals(0, inDb.getIsAutoCalc().intValue());
    Assert.assertEquals(ProgressMetric.MONEY.getCode(), inDb.getProgressMetricType());
    Assert.assertEquals(0L, inDb.getStartingAmount().longValue());
    Assert.assertEquals(1000L, inDb.getGoalAmount().longValue());
    Assert.assertEquals(1L, inDb.getCurrentAmount().longValue());
    Assert.assertEquals("元", inDb.getUnit());
    Assert.assertEquals(1000L, inDb.getDeadline().longValue());
    Assert.assertEquals(1, inDb.getOrderIndex().intValue());
    Assert.assertEquals(0, inDb.getIsPrivate().intValue());
    Assert.assertEquals(RegularRemindType.NOT.getCode().intValue(), inDb.getRegularRemindType().intValue());

    List<Objective> objectives = objectiveDao.listObjectivesByStartAndEndDeadline(orgId, 999L, 1001L);
    Assert.assertEquals(1, objectives.size());

    Assert.assertEquals(1, objectiveDao.getMaxOrderIndexByObjectivePeriod(orgId, objectivePeriodId));

    Assert.assertEquals(1, objectiveDao.listObjectivesByPriorityAndOrderItem(
            orgId, ObjectivePriority.P0.getCode(), 2).size());

    objective.setContent("test 2");
    objectiveDao.insertObjective(objective);

    List<Objective> objectiveLit = objectiveDao.listObjectiveByTypeAndOwnerIdAndQuarterId(orgId,
            OkrType.ORG.getCode(), orgId, objectivePeriodId, 1, 1);
    Assert.assertEquals(2, objectiveLit.size());
    Assert.assertEquals("test", objectiveLit.get(0).getContent());

    objectiveLit = objectiveDao.listObjectivesByStartAndEndOrderIndex(orgId, objectivePeriodId, 0, 10);
    Assert.assertEquals(2, objectiveLit.size());

    objectiveDao.deleteObjective(orgId, objectiveId, userId);

    Assert.assertNull(objectiveDao.findObjective(orgId, objectiveId, 0));
  }


  @Test
  public void testUpdateObjective() throws Exception {
    long objectiveId = objectiveDao.insertObjective(objective);
    Objective inDb = objectiveDao.findObjective(orgId, objectiveId, 0);
    inDb.setParentObjectiveId(200L);
    inDb.setContent("update");
    inDb.setPriority(ObjectivePriority.P1.getCode());
    inDb.setIsAutoCalc(1);
    inDb.setProgressMetricType(ProgressMetric.NUMBER.getCode());
    inDb.setStartingAmount(new BigDecimal("1"));
    inDb.setGoalAmount(new BigDecimal("2000"));
    inDb.setCurrentAmount(new BigDecimal("2"));
    inDb.setUnit("个");
    inDb.setDeadline(2000L);
    inDb.setOrderIndex(2);
    inDb.setIsPrivate(1);
    inDb.setRegularRemindType(RegularRemindType.EVERY_WEEK.getCode());

    objectiveDao.updateObjective(inDb);

    inDb = objectiveDao.findObjective(orgId, objectiveId, 0);
    Assert.assertEquals(200L, inDb.getParentObjectiveId().longValue());
    Assert.assertEquals("update", inDb.getContent());
    Assert.assertEquals(ObjectivePriority.P1.getCode(), inDb.getPriority());
    Assert.assertEquals(1, inDb.getIsAutoCalc().intValue());
    Assert.assertEquals(ProgressMetric.NUMBER.getCode(), inDb.getProgressMetricType());
    Assert.assertEquals(1L, inDb.getStartingAmount().longValue());
    Assert.assertEquals(2000L, inDb.getGoalAmount().longValue());
    Assert.assertEquals(2L, inDb.getCurrentAmount().longValue());
    Assert.assertEquals("个", inDb.getUnit());
    Assert.assertEquals(2000L, inDb.getDeadline().longValue());
    Assert.assertEquals(2, inDb.getOrderIndex().intValue());
    Assert.assertEquals(1, inDb.getIsPrivate().intValue());
    Assert.assertEquals(RegularRemindType.EVERY_WEEK.getCode().intValue(), inDb.getRegularRemindType().intValue());

    List<Objective> objectives = objectiveDao.listObjectivesByObjectiveIds(orgId, Arrays.asList(objectiveId));
    Assert.assertEquals(1, objectives.size());

    inDb = objectives.get(0);
    inDb.setOrderIndex(2);
    objectiveDao.batchUpdateOrderIndexOfObjectives(objectives);

    List<Objective> afterBatchUpdate = objectiveDao.listObjectivesByObjectiveIds(orgId, Arrays.asList(objectiveId));
    Assert.assertEquals(1, afterBatchUpdate.size());
    Assert.assertEquals(2, afterBatchUpdate.get(0).getOrderIndex().intValue());

    Assert.assertEquals(1, objectiveDao.searchObjectiveByKeyword(orgId, "pda", OkrType.ORG.getCode() , orgId).size());
  }

  @Test
  public void testListFirstLevelSubordinateObjectives() throws Exception {
    long parentObjectiveId = objectiveDao.insertObjective(objective);

    objective.setParentObjectiveId(parentObjectiveId);
    long objectiveId = objectiveDao.insertObjective(objective);

    List<Objective> objectives = objectiveDao.listFirstLevelSubordinateObjectives(orgId, parentObjectiveId);
    Assert.assertEquals(1, objectives.size());
    Assert.assertEquals(objectiveId, objectives.get(0).getObjectiveId().longValue());
  }

  @Test
  public void testListObjectivesWithRegularRemindType() {
    List<Objective> objectives = objectiveDao.listObjectivesWithRegularRemindType(orgId);
    Assert.assertEquals(0, objectives.size());
  }
}