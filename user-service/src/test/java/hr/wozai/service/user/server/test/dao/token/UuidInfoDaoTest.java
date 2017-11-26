package hr.wozai.service.user.server.test.dao.token;

import hr.wozai.service.user.client.userorg.enums.UuidUsage;
import hr.wozai.service.user.server.dao.token.UuidInfoDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.token.UuidInfo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/14
 */
public class UuidInfoDaoTest extends TestBase {
  @Autowired
  UuidInfoDao uuidInfoDao;

  @Test
  public void testAll() throws Exception {
    long orgId = 10L;
    long userId = 10L;
    UuidInfo uuidInfo = new UuidInfo();
    uuidInfo.setOrgId(orgId);
    uuidInfo.setUserId(userId);
    uuidInfo.setUUIDValue();
    uuidInfo.setUuidUsage(UuidUsage.ONBOARDING.getCode());
    uuidInfo.setExpireTime(System.currentTimeMillis());
    uuidInfo.setCreatedUserId(userId);
    System.out.println(uuidInfo);

    uuidInfoDao.insertUuidInfo(uuidInfo);

    String uuid = uuidInfo.getUuid();
    UuidInfo inDB = uuidInfoDao.getUuidInfoByUuid(uuid);
    Assert.assertNotNull(inDB);

    List<UuidInfo> list = uuidInfoDao.listUuidsByOrgIdAndUserIdAndUuidUsage(orgId, userId,
            UuidUsage.ONBOARDING.getCode(), Long.MIN_VALUE);
    Assert.assertEquals(1, list.size());

    uuidInfoDao.deleteUuidInfoByOrgIdAndUserIdAndUsage(orgId, userId, UuidUsage.ONBOARDING.getCode());
    inDB = uuidInfoDao.getUuidInfoByUuid(uuid);
    Assert.assertNull(inDB);
  }
}