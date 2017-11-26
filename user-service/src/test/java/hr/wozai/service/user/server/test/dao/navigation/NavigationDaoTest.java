package hr.wozai.service.user.server.test.dao.navigation;

import hr.wozai.service.user.server.dao.navigation.NavigationDao;
import hr.wozai.service.user.server.enums.NaviModule;
import hr.wozai.service.user.server.model.navigation.Navigation;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/2
 */
public class NavigationDaoTest extends TestBase {
  @Autowired
  NavigationDao navigationDao;

  private long orgId = 99L;
  private long userId = 99L;
  private long naviOrgId = 199L;
  private long naviUserId = 199L;
  private Navigation navigation;

  @Before
  public void setUp() throws Exception {
    navigation = new Navigation();
    navigation.setOrgId(orgId);
    navigation.setUserId(userId);
    navigation.setNaviOrgId(naviOrgId);
    navigation.setNaviUserId(naviUserId);
    navigation.setNaviModule(NaviModule.OBJECTIVE.getCode());
    navigation.setNaviStep(1);
    navigation.setCreatedUserId(userId);
  }

  @Test
  public void testInsertNavigation() throws Exception {
    long navigationId = navigationDao.insertNavigation(navigation);

    Navigation inDb = navigationDao.findNavigationByOrgIdAndUserId(orgId, userId);
    Assert.assertEquals(naviOrgId, inDb.getNaviOrgId().longValue());
    Assert.assertEquals(naviUserId, inDb.getNaviUserId().longValue());
    Assert.assertEquals(NaviModule.OBJECTIVE.getCode(), inDb.getNaviModule());
    Assert.assertEquals(1, inDb.getNaviStep().intValue());

    inDb = navigationDao.findNavigationByNaviOrgIdAndNaviUserId(naviOrgId, naviUserId);
    Assert.assertEquals(orgId, inDb.getOrgId().longValue());
    Assert.assertEquals(userId, inDb.getUserId().longValue());
    Assert.assertEquals(NaviModule.OBJECTIVE.getCode(), inDb.getNaviModule());
    Assert.assertEquals(1, inDb.getNaviStep().intValue());

    navigation.setNaviModule(2);
    navigation.setNaviStep(2);
    navigation.setLastModifiedUserId(userId);
    navigationDao.updateNavigation(navigation);
    inDb = navigationDao.findNavigationByOrgIdAndUserId(orgId, userId);
    Assert.assertEquals(naviOrgId, inDb.getNaviOrgId().longValue());
    Assert.assertEquals(naviUserId, inDb.getNaviUserId().longValue());
    Assert.assertEquals(2, inDb.getNaviModule().intValue());
    Assert.assertEquals(2, inDb.getNaviStep().intValue());

    navigation.setIsDeleted(1);
    navigationDao.updateNavigation(navigation);
    inDb = navigationDao.findNavigationByOrgIdAndUserId(orgId, userId);
    Assert.assertNull(inDb);
  }
}