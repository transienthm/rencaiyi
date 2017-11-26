package hr.wozai.service.user.server.test.dao.okr;

import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.client.okr.enums.PeriodTimeSpan;
import hr.wozai.service.user.server.dao.okr.ObjectivePeriodDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.okr.ObjectivePeriod;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/7
 */
public class ObjectivePeriodDaoTest extends TestBase {
  @Autowired
  ObjectivePeriodDao objectivePeriodDao;

  private Long orgId = 199L;
  private Long userId = 199L;
  private Long teamId = 199L;

  @Test
  public void testAll() throws Exception {
    Integer year = TimeUtils.getCurrentYearWithTimeZone(TimeUtils.BEIJING);
    Integer perionTimeSpanId = PeriodTimeSpan.First_half_year.getCode();
    String name = PeriodTimeSpan.getNameByYearAndPeriodTimeSpan(year, PeriodTimeSpan.First_half_year);
    //test org
    ObjectivePeriod orgPeriod = new ObjectivePeriod();
    orgPeriod.setOrgId(orgId);
    orgPeriod.setType(OkrType.ORG.getCode());
    orgPeriod.setOwnerId(orgId);
    orgPeriod.setPeriodTimeSpanId(perionTimeSpanId);
    orgPeriod.setYear(year);
    orgPeriod.setName(name);
    orgPeriod.setCreatedUserId(userId);
    long orgPeriodId = objectivePeriodDao.insertObjectivePeriod(orgPeriod);

    ObjectivePeriod inDb = objectivePeriodDao.findObjectivePeriod(orgId, orgPeriodId);
    Assert.assertEquals(name, inDb.getName());
    Assert.assertEquals(year, inDb.getYear());
    Assert.assertEquals(perionTimeSpanId, inDb.getPeriodTimeSpanId());
    Assert.assertEquals(OkrType.ORG.getCode(), inDb.getType());
    Assert.assertEquals(orgId, inDb.getOwnerId());

    List<ObjectivePeriod> list = objectivePeriodDao.listObjectivePeriodByOrgIdAndOwnerId(orgId,
            OkrType.ORG.getCode(), orgId);
    Assert.assertEquals(1, list.size());

    inDb = objectivePeriodDao.findObjectivePeriodByName(orgId, name, OkrType.ORG.getCode(),
            orgId);
    Assert.assertNotNull(inDb);

    orgPeriod.setName("update");
    orgPeriod.setLastModifiedUserId(-1L);
    objectivePeriodDao.updateObjectivePeriod(orgPeriod);
    inDb = objectivePeriodDao.findObjectivePeriod(orgId, orgPeriodId);
    Assert.assertEquals("update", inDb.getName());

    list = objectivePeriodDao.listObjectivePeriodsByOrgIdAndYearAndPeriodSpanIds(
            orgId, year, Arrays.asList(perionTimeSpanId));
    Assert.assertEquals(1, list.size());

    objectivePeriodDao.deleteObjectivePeriod(orgId, orgPeriodId, -1);
    inDb = objectivePeriodDao.findObjectivePeriod(orgId, orgPeriodId);
    Assert.assertNull(inDb);

    //test team period
    orgPeriod.setType(OkrType.TEAM.getCode());
    orgPeriod.setOwnerId(teamId);
    orgPeriod.setName(name);
    orgPeriodId = objectivePeriodDao.insertObjectivePeriod(orgPeriod);

    inDb = objectivePeriodDao.findObjectivePeriod(orgId, orgPeriodId);
    Assert.assertEquals(name, inDb.getName());
    Assert.assertEquals(year, inDb.getYear());
    Assert.assertEquals(perionTimeSpanId, inDb.getPeriodTimeSpanId());
    Assert.assertEquals(OkrType.TEAM.getCode(), inDb.getType());
    Assert.assertEquals(teamId, inDb.getOwnerId());

    list = objectivePeriodDao.listObjectivePeriodByOrgIdAndOwnerId(orgId,
            OkrType.TEAM.getCode(), teamId);
    Assert.assertEquals(1, list.size());

    //test personal period
    orgPeriod.setType(OkrType.PERSON.getCode());
    orgPeriod.setOwnerId(userId);
    orgPeriodId = objectivePeriodDao.insertObjectivePeriod(orgPeriod);

    inDb = objectivePeriodDao.findObjectivePeriod(orgId, orgPeriodId);
    Assert.assertEquals(name, inDb.getName());
    Assert.assertEquals(year, inDb.getYear());
    Assert.assertEquals(perionTimeSpanId, inDb.getPeriodTimeSpanId());
    Assert.assertEquals(OkrType.PERSON.getCode(), inDb.getType());
    Assert.assertEquals(userId, inDb.getOwnerId());

    orgPeriod.setPeriodTimeSpanId(PeriodTimeSpan.Second_half_year.getCode());
    objectivePeriodDao.insertObjectivePeriod(orgPeriod);

    orgPeriod.setPeriodTimeSpanId(PeriodTimeSpan.January.getCode());
    orgPeriod.setYear(year + 1);
    objectivePeriodDao.insertObjectivePeriod(orgPeriod);

    orgPeriod.setPeriodTimeSpanId(PeriodTimeSpan.April.getCode());
    orgPeriod.setYear(year);
    objectivePeriodDao.insertObjectivePeriod(orgPeriod);

    list = objectivePeriodDao.listObjectivePeriodByOrgIdAndOwnerId(orgId,
            OkrType.PERSON.getCode(), userId);
    Assert.assertEquals(4, list.size());

    Assert.assertEquals(PeriodTimeSpan.January.getCode(), list.get(0).getPeriodTimeSpanId());
    Assert.assertEquals(PeriodTimeSpan.Second_half_year.getCode(), list.get(1).getPeriodTimeSpanId());
    Assert.assertEquals(PeriodTimeSpan.April.getCode(), list.get(2).getPeriodTimeSpanId());
    Assert.assertEquals(PeriodTimeSpan.First_half_year.getCode(), list.get(3).getPeriodTimeSpanId());
  }
}