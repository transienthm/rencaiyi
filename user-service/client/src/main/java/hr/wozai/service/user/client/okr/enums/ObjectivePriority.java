// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.okr.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum ObjectivePriority {

  P0(0, "0.6", "极高"),
  P1(1, "0.25", "高"),
  P2(2, "0.1", "中"),
  P3(3, "0.05", "低");

  private Integer code;
  private String desc;
  private String name;

  private ObjectivePriority(Integer code, String desc, String name) {
    this.code = code;
    this.desc = desc;
    this.name = name;
  }

  public static ObjectivePriority getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ObjectivePriority refreshTokenStatus : ObjectivePriority.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static ObjectivePriority getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (ObjectivePriority refreshTokenStatus : ObjectivePriority.values()) {
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

  public String getName() {
    return name;
  }

  public static boolean isValidPriority(int priority) {
    for (ObjectivePriority objectivePriority : ObjectivePriority.values()) {
      if (objectivePriority.getCode() == priority) {
        return true;
      }
    }
    return false;
  }
}
