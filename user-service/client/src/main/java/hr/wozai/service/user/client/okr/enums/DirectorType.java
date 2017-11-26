// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.okr.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum DirectorType {

  OBJECTIVE(1, "objective"),
  KEYRESULT(2, "keyresult");

  private Integer code;
  private String desc;

  private DirectorType(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static DirectorType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (DirectorType refreshTokenStatus : DirectorType.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static DirectorType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (DirectorType refreshTokenStatus : DirectorType.values()) {
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
    for (DirectorType objectiveType : DirectorType.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
