// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
public enum OssRequestType {

  GET(1, "GET"),
  PUT(2, "PUT"),
  ;

  private int code;
  private String msg;

  private OssRequestType(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static OssRequestType getEnumByCode(int code) {
    for (OssRequestType usability : OssRequestType.values()) {
      if (usability.code == code) {
        return usability;
      }
    }
    return null;
  }

  public static OssRequestType getEnumByDesc(String desc) {
    if (null == desc) {
      return null;
    }
    for (OssRequestType usability : OssRequestType.values()) {
      if (usability.getMsg().equals(desc)) {
        return usability;
      }
    }
    return null;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
  
}
