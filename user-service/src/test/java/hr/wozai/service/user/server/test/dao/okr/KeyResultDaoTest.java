package hr.wozai.service.user.server.test.dao.okr;

import hr.wozai.service.user.client.okr.enums.ProgressMetric;
import hr.wozai.service.user.server.dao.okr.KeyResultDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.okr.KeyResult;
import hr.wozai.service.user.client.okr.enums.ObjectivePriority;
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
public class KeyResultDaoTest extends TestBase {
  @Autowired
  KeyResultDao keyResultDao;

  private long orgId = 199L;
  private long userId = 199L;
  private long objectiveId = 199L;
  private long startingAmount = 1L;
  private long goalAmount = 1000L;

  private KeyResult keyResult;

  @Before
  public void setUp() throws Exception {
    keyResult = new KeyResult();
    keyResult.setOrgId(orgId);
    keyResult.setContent("test");
    keyResult.setObjectiveId(objectiveId);
    keyResult.setPriority(ObjectivePriority.P0.getCode());
    keyResult.setProgressMetricType(ProgressMetric.MONEY.getCode());
    keyResult.setStartingAmount(new BigDecimal(startingAmount));
    keyResult.setGoalAmount(new BigDecimal(goalAmount));
    keyResult.setCurrentAmount(new BigDecimal(startingAmount));
    keyResult.setUnit("元");
    keyResult.setDeadline(1000L);
    keyResult.setCreatedUserId(userId);
    keyResult.setLastModifiedUserId(userId);
  }

  @Test
  public void testInsertKeyResult() throws Exception {
    long keyResultId = keyResultDao.insertKeyResult(keyResult);

    KeyResult inDb = keyResultDao.findKeyResult(orgId, keyResultId);
    Assert.assertEquals("test", inDb.getContent());
    Assert.assertEquals(ProgressMetric.MONEY.getCode(), inDb.getProgressMetricType());
    Assert.assertEquals(startingAmount, inDb.getStartingAmount().longValue());
    Assert.assertEquals(goalAmount, inDb.getGoalAmount().longValue());
    Assert.assertEquals(startingAmount, inDb.getCurrentAmount().longValue());
    Assert.assertEquals("元", inDb.getUnit());
    Assert.assertEquals(1000L, inDb.getDeadline().longValue());

    List<KeyResult> keyResults = keyResultDao.listKeyResultsByStartAndEndDeadline(orgId, 0L, 1001L);
    Assert.assertEquals(1, keyResults.size());

    List<KeyResult> keyResultList = keyResultDao.listKeyResultByObjectiveId(orgId, objectiveId);
    Assert.assertEquals(1, keyResultList.size());

    inDb.setContent("update");
    inDb.setPriority(ObjectivePriority.P1.getCode());
    inDb.setProgressMetricType(ProgressMetric.PERCENT.getCode());
    inDb.setStartingAmount(new BigDecimal(0));
    inDb.setGoalAmount(new BigDecimal(100));
    inDb.setCurrentAmount(new BigDecimal(1));
    inDb.setDeadline(2000L);
    keyResultDao.updateKeyResult(inDb);

    inDb = keyResultDao.findKeyResult(orgId, keyResultId);
    Assert.assertEquals("update", inDb.getContent());
    Assert.assertEquals(1, inDb.getPriority().intValue());
    Assert.assertEquals(ProgressMetric.PERCENT.getCode(), inDb.getProgressMetricType());
    Assert.assertEquals(0L, inDb.getStartingAmount().longValue());
    Assert.assertEquals(100L, inDb.getGoalAmount().longValue());
    Assert.assertEquals(1L, inDb.getCurrentAmount().longValue());
    Assert.assertEquals(2000L, inDb.getDeadline().longValue());

    keyResultList = keyResultDao.listSimpleKeyResultsByObjectiveIds(orgId, Arrays.asList(objectiveId));
    Assert.assertEquals(1, keyResultList.size());

    keyResultDao.deleteKeyResult(orgId, keyResultId, userId);
    Assert.assertNull(keyResultDao.findKeyResult(orgId, keyResultId));
  }

  @Test
  public void testDeleteKeyResultByObjectiveId() throws Exception {
    long keyResultId = keyResultDao.insertKeyResult(keyResult);
    Assert.assertNotNull(keyResultId);
    keyResultDao.deleteKeyResultByObjectiveId(orgId, objectiveId, userId);
    KeyResult inDb = keyResultDao.findKeyResult(orgId, keyResultId);
    Assert.assertNull(inDb);
  }

  @Test
  public void test() {
    BigDecimal b = new BigDecimal("1.1162");
    System.out.println(b.divide(new BigDecimal("1"), 2, BigDecimal.ROUND_HALF_UP));
  }
}