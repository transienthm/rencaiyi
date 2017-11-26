package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.user.client.common.enums.RemindType;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.common.RemindSetting;
import hr.wozai.service.user.server.service.RemindSettingService;
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
public class RemindSettingServiceImplTest extends TestBase {
  @Autowired
  RemindSettingService remindSettingService;

  long orgId = 199L;
  long userId = 199L;

  @Test
  public void testInitRemindSettingByUserId() throws Exception {
    remindSettingService.initRemindSettingByUserId(orgId, userId, userId);

    List<RemindSetting> remindSettingList = remindSettingService.listRemindSettingByUserId(orgId, userId);
    Assert.assertNotEquals(0, remindSettingList.size());

    System.out.println(remindSettingList);

    int remindType = RemindType.NEWSFEED_AT.getCode();
    RemindSetting remindSetting = remindSettingService.getRemindSettingByUserIdAndRemindType(orgId, userId, remindType);
    Assert.assertEquals(1, remindSetting.getStatus().intValue());

    remindSetting.setStatus(0);
    remindSettingService.batchUpdateRemindSetting(Arrays.asList(remindSetting));

    RemindSetting afterUpdate = remindSettingService.getRemindSettingByUserIdAndRemindType(orgId, userId, remindType);
    Assert.assertEquals(0, afterUpdate.getStatus().intValue());

    remindSettingService.deleteRemindSettingByUserId(orgId, userId, -1L);
    remindSettingList = remindSettingService.listRemindSettingByUserId(orgId, userId);
    Assert.assertEquals(0, remindSettingList.size());
  }
}