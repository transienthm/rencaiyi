// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.api.result;

import com.alibaba.fastjson.JSON;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;

/**
 * 通用的Ajax接口返回
 *
 * @author liangyafei
 * @version 1.0
 * @created 15-9-8 下午10:06
 */
public class Result<T> {

  private int code = ServiceStatus.COMMON_OK.getCode();

  private String msg = ServiceStatus.COMMON_OK.getMsg();

  private String errorInfo = null;

  private T data;

  public String getMsg() {
    return msg;
  }

  public void setCodeAndMsg(ServiceStatus serviceStatus) {
    this.code = serviceStatus.getCode();
    this.msg = serviceStatus.getMsg();
  }

  public void setErrorInfo(String errorInfo) {
    this.errorInfo = errorInfo;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public String getErrorInfo() {
    return errorInfo;
  }

  public String toJsonString() {
    return JSON.toJSONString(this);
  }

  public String toString() {
    return "{code=" + code + ", msg=" + msg + " data=" + data + "}\n";
  }

}
