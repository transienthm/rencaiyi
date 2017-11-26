// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.okr.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016-08-29
 */
public enum ProgressMetric {

  PERCENT(1, "percent", "%"),
  MONEY(2, "money", "元"),
  NUMBER(3, "number", "");

  private Integer code;
  private String desc;
  private String defaultUnit;

  private ProgressMetric(Integer code, String desc, String defaultUnit) {
    this.code = code;
    this.desc = desc;
    this.defaultUnit = defaultUnit;
  }

  public static ProgressMetric getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ProgressMetric refreshTokenStatus : ProgressMetric.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static ProgressMetric getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (ProgressMetric refreshTokenStatus : ProgressMetric.values()) {
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

  public String getDefaultUnit() {
    return defaultUnit;
  }

  public static boolean isValidType(int type) {
    for (ProgressMetric objectiveType : ProgressMetric.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
