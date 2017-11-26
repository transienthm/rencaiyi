// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.conversation.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum RemindDay {

  MONDAY(1, "MONDAY"),
  TUESDAY(2, "TUESDAY"),
  WEDNESDAY(3, "WEDNESDAY"),
  THURSDAY(4, "THURSDAY"),
  FRIDAY(5, "FRIDAY"),
  SATURDAY(6, "SATURDAY"),
  SUNDAY(7, "SUNDAY"),
  ;

  private Integer code;
  private String desc;

  private RemindDay(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static RemindDay getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (RemindDay remindDay : RemindDay.values()) {
      if (IntegerUtils.equals(remindDay.code, code)) {
        return remindDay;
      }
    }

    return null;
  }

  public static RemindDay getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (RemindDay configType : RemindDay.values()) {
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

  public static boolean isValidRemindDay(int type) {
    for (RemindDay objectiveType : RemindDay.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }

}
