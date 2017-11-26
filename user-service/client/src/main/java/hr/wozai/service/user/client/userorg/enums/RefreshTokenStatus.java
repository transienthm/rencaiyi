// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum RefreshTokenStatus {

  ACTIVE(1, "ACTIVE"),
  REVOKED(2, "REVOKED");

  private Integer code;
  private String desc;

  private RefreshTokenStatus(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static RefreshTokenStatus getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (RefreshTokenStatus refreshTokenStatus : RefreshTokenStatus.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static RefreshTokenStatus getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (RefreshTokenStatus refreshTokenStatus : RefreshTokenStatus.values()) {
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
