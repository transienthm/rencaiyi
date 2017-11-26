// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum ActionCode {

  VISIBLE(1, "visible"),
  OPERATIONAL(2, "operational"),
  CREATE(3, "create"),
  READ(4, "read"),
  EDIT(5, "edit"),
  DELETE(6, "delete");


  private Integer code;
  private String desc;

  private ActionCode(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static ActionCode getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ActionCode refreshTokenStatus : ActionCode.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static ActionCode getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (ActionCode refreshTokenStatus : ActionCode.values()) {
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
}
