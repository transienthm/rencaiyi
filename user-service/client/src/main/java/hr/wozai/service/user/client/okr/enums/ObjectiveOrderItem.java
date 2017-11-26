// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.okr.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum ObjectiveOrderItem {

  ID(1, "id"),
  ID_REVERSE(-1, "ID倒序"),
  PRIORITY(2, "优先级"),
  PRIORITY_REVERSE(-2, "优先级倒序"),
  DEADLINE(3, "截止日"),
  DEADLINE_REVERSE(-3, "截止日倒序"),
  PROGRESS(4, "完成度"),
  PROGRESS_REVERSE(-4, "完成度倒序"),
  LAST_MODIFIED_TIME(5, "最后更新时间"),
  LAST_MODIFIED_TIME_REVERSE(-5, "最后更新时间倒序");

  private Integer code;
  private String desc;

  private ObjectiveOrderItem(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static ObjectiveOrderItem getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ObjectiveOrderItem refreshTokenStatus : ObjectiveOrderItem.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static ObjectiveOrderItem getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (ObjectiveOrderItem refreshTokenStatus : ObjectiveOrderItem.values()) {
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
    for (ObjectiveOrderItem objectiveType : ObjectiveOrderItem.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
