// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-03
 */
public enum StatusType {

  EMPLOYMENT_STATUS(1, "EMPLOYMENT_STATUS"),
  USER_STATUS(2, "USER_STATUS"),

  ;

  private int code;
  private String msg;

  private StatusType(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static StatusType getEnumByCode(int code) {

    for (StatusType statusType : StatusType.values()) {
      if (statusType.code == code) {
        return statusType;
      }
    }

    return null;
  }

  public static StatusType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (StatusType statusType : StatusType.values()) {
      if (statusType.getMsg().equals(desc)) {
        return statusType;
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
