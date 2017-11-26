package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.navigation.Navigation;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/7
 */
public interface NavigationService {
  long insertNavigation(Navigation navigation);

  int updateNavigation(Navigation navigation);

  Navigation findNavigationByOrgIdAndUserId(long orgId, long userId);

  Navigation findNavigationByNaviOrgIdAndNaviUserId(long naviOrgId, long naviUserId);
}
