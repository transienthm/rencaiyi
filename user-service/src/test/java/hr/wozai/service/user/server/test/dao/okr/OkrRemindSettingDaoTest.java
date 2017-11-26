package hr.wozai.service.user.server.test.dao.okr;

import hr.wozai.service.user.server.dao.okr.OkrRemindSettingDao;
import hr.wozai.service.user.server.enums.OkrRemindType;
import hr.wozai.service.user.server.model.okr.OkrRemindSetting;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/10/10
 */
public class OkrRemindSettingDaoTest extends TestBase {
  @Autowired
  OkrRemindSettingDao okrRemindSettingDao;

  long orgId = 199L;
  long userId = 299L;
  OkrRemindSetting okrRemindSetting;

  @Before
  public void setUp() throws Exception {
    okrRemindSetting = new OkrRemindSetting();
    okrRemindSetting.setOrgId(orgId);
    okrRemindSetting.setRemindType(OkrRemindType.OBJECTIVE_DEADLINE.getCode());
    okrRemindSetting.setFrequency(7);
    okrRemindSetting.setCreatedUserId(userId);
  }

  @Test
  public void testAll() throws Exception {
    okrRemindSettingDao.batchInsertOkrRemindSetting(Arrays.asList(okrRemindSetting));

    OkrRemindSetting inDb = okrRemindSettingDao.getOkrRemindSettingByOrgIdAndRemindType(
            orgId, OkrRemindType.OBJECTIVE_DEADLINE.getCode());
    Assert.assertEquals(7, inDb.getFrequency().intValue());

    okrRemindSettingDao.deleteOkrRemindSettingByOrgId(orgId);
    inDb = okrRemindSettingDao.getOkrRemindSettingByOrgIdAndRemindType(
            orgId, OkrRemindType.OBJECTIVE_DEADLINE.getCode());

    Assert.assertNull(inDb);
  }
}