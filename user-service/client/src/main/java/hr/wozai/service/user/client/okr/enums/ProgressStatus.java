// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.okr.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016-08-29
 */
public enum ProgressStatus {

  ALL(1, "全部"),
  NOT_BEGIN(2, "尚未开始"),
  ON_GOING(3, "进行中"),
  FINISH(4, "接近完成");

  private Integer code;
  private String desc;

  ProgressStatus(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static ProgressStatus getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ProgressStatus refreshTokenStatus : ProgressStatus.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static ProgressStatus getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (ProgressStatus refreshTokenStatus : ProgressStatus.values()) {
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
    for (ProgressStatus objectiveType : ProgressStatus.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
