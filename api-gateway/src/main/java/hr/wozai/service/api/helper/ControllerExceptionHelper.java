// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.result.Result;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-24
 */
public class ControllerExceptionHelper {

  public static void setServiceStatusForControllerResult(Result result, Exception e) {

    ServiceStatus serviceStatus = null;
    if (e instanceof ServiceStatusException) {
      ServiceStatusException serviceStatusException = (ServiceStatusException) e;
      serviceStatus = serviceStatusException.getServiceStatus();
      result.setCodeAndMsg(serviceStatus);
    } else {
      result.setCodeAndMsg(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }
  }

}
