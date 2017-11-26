// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.conversation.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum CurrentPeriodStatus {

  COMPLETE_BY_SELF(1, "COMPLETE_BY_SELF"),
  COMPLETE_BY_OTHER(2, "COMPLETE_BY_OTHER"),
  INCOMPLETE(3, "INCOMPLETE"),
  ;

  private Integer code;
  private String desc;

  private CurrentPeriodStatus(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static CurrentPeriodStatus getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (CurrentPeriodStatus currentPeriodStatus : CurrentPeriodStatus.values()) {
      if (IntegerUtils.equals(currentPeriodStatus.code, code)) {
        return currentPeriodStatus;
      }
    }

    return null;
  }

  public static CurrentPeriodStatus getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (CurrentPeriodStatus currentPeriodStatus : CurrentPeriodStatus.values()) {
      if (currentPeriodStatus.getDesc().equals(desc)) {
        return currentPeriodStatus;
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

  public static boolean isValidCurrentPeriodStatus(int type) {
    for (CurrentPeriodStatus objectiveType : CurrentPeriodStatus.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
