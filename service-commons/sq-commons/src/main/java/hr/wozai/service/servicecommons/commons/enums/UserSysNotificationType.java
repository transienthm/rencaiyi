// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-03
 */
public enum UserSysNotificationType {

  JOB_TRANSFER(1, "调岗"),
  PASS_PROBATION(2, "转正"),
  RESIGN(3, "离职"),

  ;

  private int code;
  private String msg;

  private UserSysNotificationType(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static UserSysNotificationType getEnumByCode(int code) {

    for (UserSysNotificationType userSysNotificationType : UserSysNotificationType.values()) {
      if (userSysNotificationType.code == code) {
        return userSysNotificationType;
      }
    }

    return null;
  }

  public static UserSysNotificationType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (UserSysNotificationType userSysNotificationType : UserSysNotificationType.values()) {
      if (userSysNotificationType.getMsg().equals(desc)) {
        return userSysNotificationType;
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
