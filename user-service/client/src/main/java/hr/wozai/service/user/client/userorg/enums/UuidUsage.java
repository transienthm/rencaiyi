// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum UuidUsage {

  ONBOARDING(1, "ONBOARDING"),
  RESET_PWD(2, "RESET_PWD"),
  CHANGE_PWD(3, "CHANGE_PWD"),
  INIT_PWD(4, "INIT_PWD")
  ;

  private Integer code;
  private String desc;

  private UuidUsage(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static UuidUsage getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (UuidUsage refreshTokenStatus : UuidUsage.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static UuidUsage getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (UuidUsage refreshTokenStatus : UuidUsage.values()) {
      if (refreshTokenStatus.getDesc().equals(desc)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public Integer getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public static boolean isValidType(int type) {
    for (UuidUsage objectiveType : UuidUsage.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
