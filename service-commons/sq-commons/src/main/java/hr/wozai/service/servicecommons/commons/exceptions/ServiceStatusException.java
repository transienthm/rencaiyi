// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.commons.exceptions;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;

/**
 * Exception including thrift status
 *
 * @author Zhe Chen
 * @version 1.0
 * @created 15-9-5 上午7:42
 */
public class ServiceStatusException extends RuntimeException {

  private ServiceStatus serviceStatus;

  private String errInfo;

  public ServiceStatusException(String msg) {
    super(msg);
  }

  public ServiceStatusException(ServiceStatus serviceStatus) {
    this.serviceStatus = serviceStatus;
  }

  public ServiceStatusException(ServiceStatus serviceStatus, String errInfo) {
    this.serviceStatus = serviceStatus;
    this.errInfo = errInfo;
  }

  public ServiceStatusException(int code) {
    this.serviceStatus = ServiceStatus.getEnumByCode(code);
  }

  public ServiceStatus getServiceStatus() {
    return serviceStatus;
  }

  public String getErrInfo() {
    return errInfo;
  }

  public void setErrInfo(String errInfo) {
    this.errInfo = errInfo;
  }

  @Override
  public String toString() {
    return "ServiceStatus=" + serviceStatus
           + ", ServiceStatus.msg=" + serviceStatus.getMsg()
           + ", errInfo=" + errInfo;
  }

}
