// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.okr.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum PeriodTimeSpanType {

  MONTH(1, "month"),
  QUARTER(2, "quarter"),
  HALF_YEAR(3, "half_year"),
  YEAR(4, "year");

  private Integer code;
  private String desc;

  private PeriodTimeSpanType(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static PeriodTimeSpanType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (PeriodTimeSpanType refreshTokenStatus : PeriodTimeSpanType.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static PeriodTimeSpanType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (PeriodTimeSpanType refreshTokenStatus : PeriodTimeSpanType.values()) {
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
    for (PeriodTimeSpanType objectiveType : PeriodTimeSpanType.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
