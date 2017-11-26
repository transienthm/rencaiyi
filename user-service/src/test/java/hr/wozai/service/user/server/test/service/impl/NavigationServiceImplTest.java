package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.enums.NaviModule;
import hr.wozai.service.user.server.model.navigation.Navigation;
import hr.wozai.service.user.server.service.NavigationService;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/14
 */
public class NavigationServiceImplTest extends TestBase {
  @Autowired
  private NavigationService navigationService;

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  private long orgId = 199L;
  private long userId = 199L;
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
  public void testAll() throws Exception {
    long id = navigationService.insertNavigation(navigation);

    Navigation inDb = navigationService.findNavigationByOrgIdAndUserId(orgId, userId);
    Assert.assertEquals(id, inDb.getNavigationId().longValue());
    Assert.assertEquals(navigation.getNaviStep(), inDb.getNaviStep());

    inDb = navigationService.findNavigationByNaviOrgIdAndNaviUserId(naviOrgId, naviUserId);
    Assert.assertEquals(id, inDb.getNavigationId().longValue());
    Assert.assertEquals(navigation.getNaviStep(), inDb.getNaviStep());

    navigation.setNaviStep(2);
    navigation.setLastModifiedUserId(userId);
    navigationService.updateNavigation(navigation);
    inDb = navigationService.findNavigationByNaviOrgIdAndNaviUserId(naviOrgId, naviUserId);
    Assert.assertEquals(id, inDb.getNavigationId().longValue());
    Assert.assertEquals(navigation.getNaviStep(), inDb.getNaviStep());

    navigation.setIsDeleted(1);
    navigationService.updateNavigation(navigation);
    inDb = navigationService.findNavigationByOrgIdAndUserId(orgId, userId);
    Assert.assertNull(inDb);

    thrown.expect(ServiceStatusException.class);
    navigationService.findNavigationByNaviOrgIdAndNaviUserId(naviOrgId, naviUserId);
  }
}