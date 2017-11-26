// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import org.springframework.jdbc.UncategorizedSQLException;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-24
 */
public class FacadeExceptionHelper {

  public static void setServiceStatusForFacadeResult(ServiceStatusDTO serviceStatusDTO, Exception e) {

    ServiceStatus serviceStatus = null;
    if (e instanceof ServiceStatusException) {
      ServiceStatusException serviceStatusException = (ServiceStatusException) e;
      serviceStatus = serviceStatusException.getServiceStatus();
    } else if (e instanceof UncategorizedSQLException) {
      if (e.getMessage().contains("Incorrect string value")) {
        serviceStatus = ServiceStatus.COMMON_ILLEGAL_CHARACTER;
        serviceStatusDTO.setCodeAndMessage(serviceStatus.getCode(), serviceStatus.getMsg());
      }
    } else {
      serviceStatus = ServiceStatus.COMMON_INTERNAL_SERVER_ERROR;
      serviceStatusDTO.setCodeAndMessage(serviceStatus.getCode(), serviceStatus.getMsg());
    }

    serviceStatusDTO.setCodeAndMessage(serviceStatus.getCode(), serviceStatus.getMsg());
  }

  public static void setServiceStatusForCanceledActivity(ServiceStatusDTO serviceStatusDTO) {
    ServiceStatus serviceStatus = ServiceStatus.REVIEW_ACTIVITY_NOT_FOUND;
    serviceStatusDTO.setCodeAndMessage(serviceStatus.getCode(), serviceStatus.getMsg());
  }
}
