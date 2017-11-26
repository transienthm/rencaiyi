package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.user.client.userorg.enums.RecentUsedObjectType;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.common.RecentUsedObject;
import hr.wozai.service.user.server.service.SearchHistoryService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/25
 */
public class SearchHistoryServiceImplTest extends TestBase{
  @Autowired
  SearchHistoryService searchHistoryService;

  private long orgId = 10L;
  private long userId = 10L;
  private long teamId = 10L;

  @Test
  public void testAllRecentUsedObject() throws Exception {
    RecentUsedObject recentUsedObject = new RecentUsedObject();
    recentUsedObject.setOrgId(orgId);
    recentUsedObject.setUserId(userId);
    recentUsedObject.setType(RecentUsedObjectType.TEAM_OKR.getCode());
    recentUsedObject.setUsedObjectId(Arrays.asList(String.valueOf(teamId)));
    recentUsedObject.setCreatedUserId(userId);
    long id = searchHistoryService.addRecentUsedObject(recentUsedObject);

    RecentUsedObject inDb = searchHistoryService.gerRecentUsedObjectByUserIdAndType(orgId, userId,
            RecentUsedObjectType.TEAM_OKR.getCode());
    Assert.assertEquals(1, inDb.getUsedObjectId().size());

    recentUsedObject.setUsedObjectId(Arrays.asList("11"));
    searchHistoryService.addRecentUsedObject(recentUsedObject);
    inDb = searchHistoryService.gerRecentUsedObjectByUserIdAndType(orgId, userId,
            RecentUsedObjectType.TEAM_OKR.getCode());
    Assert.assertEquals(2, inDb.getUsedObjectId().size());

    recentUsedObject.setUsedObjectId(Arrays.asList("11"));
    searchHistoryService.addRecentUsedObject(recentUsedObject);
    inDb = searchHistoryService.gerRecentUsedObjectByUserIdAndType(orgId, userId,
            RecentUsedObjectType.TEAM_OKR.getCode());
    Assert.assertEquals(2, inDb.getUsedObjectId().size());
  }

}