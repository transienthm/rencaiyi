package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.dao.navigation.NavigationDao;
import hr.wozai.service.user.server.helper.NavigationHelper;
import hr.wozai.service.user.server.model.navigation.Navigation;
import hr.wozai.service.user.server.service.NavigationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/7
 */
@Service("navigationService")
public class NavigationServiceImpl implements NavigationService {
  @Autowired
  NavigationDao navigationDao;

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long insertNavigation(Navigation navigation) {
    NavigationHelper.isValidAddNavigationRequest(navigation);
    return navigationDao.insertNavigation(navigation);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public int updateNavigation(Navigation navigation) {
    NavigationHelper.isValidUpdateNavigationRequest(navigation);
    return navigationDao.updateNavigation(navigation);
  }

  @Override
  @LogAround
  public Navigation findNavigationByOrgIdAndUserId(long orgId, long userId) {
    return navigationDao.findNavigationByOrgIdAndUserId(orgId, userId);
  }

  @Override
  @LogAround
  public Navigation findNavigationByNaviOrgIdAndNaviUserId(long naviOrgId, long naviUserId) {
    Navigation navigation = navigationDao.findNavigationByNaviOrgIdAndNaviUserId(naviOrgId, naviUserId);
    if (navigation == null) {
      throw new ServiceStatusException(ServiceStatus.UO_NAVI_NOT_FOUND);
    }
    return navigation;
  }
}
