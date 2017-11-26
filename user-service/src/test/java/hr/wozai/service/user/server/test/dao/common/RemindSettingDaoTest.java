package hr.wozai.service.user.server.test.dao.common;

import hr.wozai.service.user.client.common.enums.RemindType;
import hr.wozai.service.user.server.dao.common.RemindSettingDao;
import hr.wozai.service.user.server.test.base.TestBase;

import hr.wozai.service.user.server.model.common.RemindSetting;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/16
 */
public class RemindSettingDaoTest extends TestBase {
  @Autowired
  RemindSettingDao remindSettingDao;

  long orgId = 199L;
  long userId = 199L;

  @Test
  public void testAll() throws Exception {
    RemindSetting remindSetting = new RemindSetting();
    remindSetting.setOrgId(orgId);
    remindSetting.setUserId(userId);
    remindSetting.setRemindType(RemindType.NEWSFEED_AT.getCode());
    remindSetting.setStatus(1);
    remindSetting.setCreatedUserId(userId);

    int result = remindSettingDao.batchInsertRemindSetting(Arrays.asList(remindSetting));
    Assert.assertEquals(1, result);

    List<RemindSetting> remindSettingList = remindSettingDao.listRemindSettingByUserId(orgId, userId);
    Assert.assertEquals(1, remindSettingList.size());
    Assert.assertEquals(1, remindSettingList.get(0).getStatus().intValue());

    remindSetting = remindSettingList.get(0);
    remindSetting.setStatus(0);

    int updateResult = remindSettingDao.batchUpdateRemindSetting(Arrays.asList(remindSetting, remindSetting));
    Assert.assertEquals(1, updateResult);
    remindSettingList = remindSettingDao.listRemindSettingByUserId(orgId, userId);
    Assert.assertEquals(1, remindSettingList.size());
    Assert.assertEquals(0, remindSettingList.get(0).getStatus().intValue());

    RemindSetting inDb = remindSettingDao.getRemindSettingByUserIdAndRemindType(orgId, userId,
            RemindType.NEWSFEED_AT.getCode());
    Assert.assertEquals(0, inDb.getStatus().intValue());

    int deleteNum = remindSettingDao.deleteRemindSettingByUserId(orgId, userId, -1L);
    Assert.assertNotNull(deleteNum);

    remindSettingList = remindSettingDao.listRemindSettingByUserId(orgId, userId);
    Assert.assertEquals(0, remindSettingList.size());
  }
}