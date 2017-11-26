// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.okr.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum OkrType {

  ORG(1, "org", 3),
  TEAM(2, "team", 1),
  PERSON(3, "person", 4),
  PROJECT_TEAM(4, "project_team", 2);

  private Integer code;
  private String desc;
  private Integer order;

  private OkrType(Integer code, String desc, Integer order) {
    this.code = code;
    this.desc = desc;
    this.order = order;
  }

  public static OkrType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (OkrType refreshTokenStatus : OkrType.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static OkrType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (OkrType refreshTokenStatus : OkrType.values()) {
      if (refreshTokenStatus.getDesc().equals(desc)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static int compareWithTowOkrType(OkrType a, OkrType b) {
    if (a.getOrder().intValue() == b.getOrder().intValue()) {
      return 0;
    } else if (a.getOrder().intValue() < b.getOrder().intValue()) {
      return 1;
    } else {
      return -1;
    }
  }

  public Integer getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public Integer getOrder() {
    return order;
  }

  public static boolean isValidType(int type) {
    for (OkrType objectiveType : OkrType.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
