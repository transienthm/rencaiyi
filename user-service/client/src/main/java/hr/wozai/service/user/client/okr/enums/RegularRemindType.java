// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.okr.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum RegularRemindType {

  NOT(1, "不提醒", 0),
  EVERY_WEEK(2, "每周提醒", 7),
  EVERY_TWO_WEEK(3, "每两周提醒", 14),
  EVERY_MONTH(4, "每月提醒", 30);

  private Integer code;
  private String desc;
  private Integer days;

  private RegularRemindType(Integer code, String desc, Integer days) {
    this.code = code;
    this.desc = desc;
    this.days = days;
  }

  public static RegularRemindType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (RegularRemindType refreshTokenStatus : RegularRemindType.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static RegularRemindType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (RegularRemindType refreshTokenStatus : RegularRemindType.values()) {
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

  public Integer getDays() {
    return days;
  }

  public static boolean isValidType(int type) {
    for (RegularRemindType regularRemindType : RegularRemindType.values()) {
      if (regularRemindType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
