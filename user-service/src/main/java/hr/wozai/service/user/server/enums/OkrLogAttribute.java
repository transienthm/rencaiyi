// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 16/3/17
 */
public enum OkrLogAttribute {

  OBJ_CONTENT(1, "目标内容"),
  OBJ_PARENT(2, "上级目标"),
  OBJ_PRIORITY(3, "目标优先级"),
  OBJ_PROGRESS(4, "目标完成度"),
  OBJ_VISIBILITY(5, "目标可见性"),
  OBJ_DEADLINE(6, "目标截止日"),
  OBJ_DIRECTOR(7, "目标负责人"),

  KR_CONTENT(8, "关键结果内容"),
  KR_PRIORITY(9, "关键结果优先级"),
  KR_PROGRESS(10, "关键结果完成度"),
  KR_DEADLINE(6, "关键结果截止日"),
  KR_DIRECTOR(7, "关键结果负责人");

  private Integer code;
  private String desc;

  private OkrLogAttribute(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static OkrLogAttribute getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (OkrLogAttribute refreshTokenStatus : OkrLogAttribute.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static OkrLogAttribute getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (OkrLogAttribute refreshTokenStatus : OkrLogAttribute.values()) {
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
    for (OkrLogAttribute objectiveType : OkrLogAttribute.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
