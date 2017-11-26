// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.conversation.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum PeriodType {

  EVERY_WEEK(1, "EVERY_WEEK"),
  HALF_MONTH(2, "HALF_MONTH"),
  EVERY_MONTH(3, "EVERY_MONTH"),
  IRREGULARLY(4, "IRREGULARLY"),
  ;

  private Integer code;
  private String desc;

  private PeriodType(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static PeriodType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (PeriodType periodType : PeriodType.values()) {
      if (IntegerUtils.equals(periodType.code, code)) {
        return periodType;
      }
    }

    return null;
  }

  public static PeriodType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (PeriodType configType : PeriodType.values()) {
      if (configType.getDesc().equals(desc)) {
        return configType;
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

  public static boolean isValidPeriodType(int type) {
    for (PeriodType objectiveType : PeriodType.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
