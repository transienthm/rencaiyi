// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.thrift.dto;

import com.facebook.swift.codec.ThriftConstructor;
import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.facebook.swift.service.ThriftMethod;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-18
 */
@ThriftStruct
public final class ServiceStatusDTO extends BaseThriftObject {

  private int code;

  private String msg;

  private String errorInfo;

  //  @ThriftConstructor
  private ServiceStatusDTO() {
  }

  @ThriftConstructor
  public ServiceStatusDTO(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  @ThriftMethod
  public void setCodeAndMessage(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  @ThriftMethod
  public void setCodeAndMessageAndErrorInfo(int code, String msg, String errorInfo) {
    this.code = code;
    this.msg = msg;
    this.errorInfo = errorInfo;
  }

  @ThriftField(1)
  public int getCode() {
    return code;
  }

  @ThriftField
  public void setCode(int code) {
    this.code = code;
  }

  @ThriftField(2)
  public String getMsg() {
    return msg;
  }

  @ThriftField
  public void setMsg(String msg) {
    this.msg = msg;
  }

  @ThriftField(3)
  public String getErrorInfo() {
    return errorInfo;
  }

  @ThriftField
  public void setErrorInfo(String errorInfo) {
    this.errorInfo = errorInfo;
  }
}
