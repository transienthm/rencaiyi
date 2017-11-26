// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.model.navigation.Navigation;
import hr.wozai.service.user.server.model.userorg.Org;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016-11-06
 */
public class NavigationHelper {

  public static void isValidAddNavigationRequest(Navigation navigation) {
    if (null == navigation
            || null == navigation.getOrgId()
            || null == navigation.getUserId()
            || null == navigation.getNaviOrgId()
            || null == navigation.getNaviUserId()
            || null == navigation.getNaviModule()
            || null == navigation.getNaviStep()
            || null == navigation.getCreatedUserId()
            ) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void isValidUpdateNavigationRequest(Navigation navigation) {
    if (null == navigation
            || null == navigation.getNaviOrgId()
            || null == navigation.getNaviUserId()
            || null == navigation.getLastModifiedUserId()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

}
