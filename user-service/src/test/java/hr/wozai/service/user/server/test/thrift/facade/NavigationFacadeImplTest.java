package hr.wozai.service.user.server.test.thrift.facade;

import hr.wozai.service.review.client.facade.ReviewTemplateFacade;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.userorg.dto.NavigationDTO;
import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;
import hr.wozai.service.user.client.userorg.facade.NavigationFacade;
import hr.wozai.service.user.server.model.navigation.Navigation;
import hr.wozai.service.user.server.service.NavigationService;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.test.utils.AopTargetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/4
 */
public class NavigationFacadeImplTest extends TestBase {
  @Autowired
  NavigationFacade navigationFacade;

  @Autowired
  NavigationService navigationService;

  long orgId = 199L;
  long userId = 199L;

  @Test
  public void testInitNaviOrg() throws Exception {
    VoidDTO result = navigationFacade.initNaviOrg(orgId, userId, 0L, 0L);
    Assert.assertNotEquals(ServiceStatus.COMMON_OK.getCode(), result.getServiceStatusDTO().getCode());

    Navigation navigation = navigationService.findNavigationByOrgIdAndUserId(orgId, userId);
    Assert.assertEquals(orgId, navigation.getOrgId().longValue());

    long naviOrgId = navigation.getNaviOrgId();
    long naviUserId = navigation.getNaviUserId();

    NavigationDTO navigationDTO = navigationFacade.getNavigation(naviOrgId, naviUserId, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), navigationDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(orgId, navigationDTO.getOrgId().longValue());
    Assert.assertEquals(0, navigationDTO.getNaviStep().intValue());

    navigationDTO.setNaviStep(2);
    navigationFacade.updateNavigation(navigationDTO, -1L, -1L);
    navigationDTO = navigationFacade.getNavigation(naviOrgId, naviUserId, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), navigationDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(orgId, navigationDTO.getOrgId().longValue());
    Assert.assertEquals(2, navigationDTO.getNaviStep().intValue());

    TokenPairDTO tokenPairDTO = navigationFacade.deleteNaviOrgAndRedirectToTrueOrg(naviOrgId, naviUserId, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), tokenPairDTO.getServiceStatusDTO().getCode());

    navigationDTO = navigationFacade.getNavigation(naviOrgId, naviUserId, -1L, -1L);
    Assert.assertEquals(ServiceStatus.UO_NAVI_NOT_FOUND.getCode(), navigationDTO.getServiceStatusDTO().getCode());
  }
}