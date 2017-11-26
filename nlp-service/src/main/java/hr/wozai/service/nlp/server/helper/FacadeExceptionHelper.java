package hr.wozai.service.nlp.server.helper;

import org.springframework.jdbc.UncategorizedSQLException;

import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;

public class FacadeExceptionHelper {
  public static void setServiceStatusForFacadeResult(ServiceStatusDTO serviceStatusDTO, Exception e) {
    ServiceStatus serviceStatus = ServiceStatus.COMMON_INTERNAL_SERVER_ERROR;
    if (e instanceof ServiceStatusException) {
      serviceStatus = ((ServiceStatusException) e).getServiceStatus();
    } else if (e instanceof UncategorizedSQLException) {
      if (e.getMessage().contains("Incorrect string value")) {
        serviceStatus = ServiceStatus.COMMON_ILLEGAL_CHARACTER;
      }
    }
    serviceStatusDTO.setCodeAndMessage(serviceStatus.getCode(), serviceStatus.getMsg());
  }
}