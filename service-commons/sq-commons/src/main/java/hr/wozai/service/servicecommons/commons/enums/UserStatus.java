// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-03
 */
public enum UserStatus {

  INVITED(1, "INVITED"),
  IMPORTED(2, "IMPORTED"),
  ACTIVE(3, "ACTIVE"),
  RESIGNED(4, "RESIGNED"),

//  /**
//   * Special status: set upon the orgAdmin created,
//   * and will not change forever
//   */
//  SUPER_ADMIN(10, "SUPER_ADMIN"),

  ;

  private int code;
  private String msg;

  private UserStatus(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static UserStatus getEnumByCode(int code) {

    for (UserStatus userStatus : UserStatus.values()) {
      if (userStatus.code == code) {
        return userStatus;
      }
    }

    return null;
  }

  public static UserStatus getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (UserStatus userStatus : UserStatus.values()) {
      if (userStatus.getMsg().equals(desc)) {
        return userStatus;
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
