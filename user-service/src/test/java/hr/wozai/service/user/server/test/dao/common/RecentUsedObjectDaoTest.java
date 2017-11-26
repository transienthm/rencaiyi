package hr.wozai.service.user.server.test.dao.common;

import hr.wozai.service.user.client.userorg.enums.RecentUsedObjectType;
import hr.wozai.service.user.server.dao.common.RecentUsedObjectDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.common.RecentUsedObject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/22
 */
public class RecentUsedObjectDaoTest extends TestBase {
  @Autowired
  RecentUsedObjectDao recentUsedObjectDao;
  long orgId = 10L;
  long userId = 10L;

  @Test
  public void testInsertRecentUsedObject() throws Exception {
    RecentUsedObject recentUsedObject = new RecentUsedObject();
    recentUsedObject.setOrgId(orgId);
    recentUsedObject.setUserId(userId);
    Integer type = RecentUsedObjectType.AT_USER.getCode();
    recentUsedObject.setType(type);
    recentUsedObject.setUsedObjectId(Arrays.asList("1", "2"));
    recentUsedObject.setCreatedUserId(userId);

    recentUsedObjectDao.insertRecentUsedObject(recentUsedObject);

    RecentUsedObject inDb = recentUsedObjectDao.getRecentUsedObjectByUserIdAndType(orgId, userId, type);
    Assert.assertNotNull(inDb);
    System.out.println(inDb);

    recentUsedObjectDao.deleteRecentUsedObjectByUserIdAndType(orgId, userId, type);
    inDb = recentUsedObjectDao.getRecentUsedObjectByUserIdAndType(orgId, userId, type);
    Assert.assertNull(inDb);
  }

  @Test
  public void testGetRecentUsedObjectByUserIdAndType() throws Exception {

  }

  @Test
  public void testDeleteRecentUsedObjectByUserIdAndType() throws Exception {

  }
}