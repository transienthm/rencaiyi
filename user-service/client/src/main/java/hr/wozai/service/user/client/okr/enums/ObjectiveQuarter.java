// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.okr.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum ObjectiveQuarter {

  ONE(1, "一季度"),
  TWO(2, "二季度"),
  THREE(3, "三季度"),
  FOUR(4, "四季度");

  private Integer code;
  private String desc;

  private ObjectiveQuarter(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static ObjectiveQuarter getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ObjectiveQuarter refreshTokenStatus : ObjectiveQuarter.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static ObjectiveQuarter getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (ObjectiveQuarter refreshTokenStatus : ObjectiveQuarter.values()) {
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

  public static boolean isValidQuater(int quarter) {
    for (ObjectiveQuarter objectiveQuarter : ObjectiveQuarter.values()) {
      if (objectiveQuarter.getCode() == quarter) {
        return true;
      }
    }
    return false;
  }
}
