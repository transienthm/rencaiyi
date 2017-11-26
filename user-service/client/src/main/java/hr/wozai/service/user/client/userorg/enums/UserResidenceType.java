// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum UserResidenceType {

  LOCAL_CITY(0, "本地城镇"),
  LOCAL_COUNTRY(1, "本地农村"),
  EXTERNAL_CITY(2, "外地城镇"),
  EXTERNAL_COUNTRY(3, "外地农村"),

  ;

  private Integer code;
  private String desc;

  private UserResidenceType(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static UserResidenceType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (UserResidenceType userGender : UserResidenceType.values()) {
      if (IntegerUtils.equals(userGender.code, code)) {
        return userGender;
      }
    }

    return null;
  }

  public static UserResidenceType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (UserResidenceType userGender : UserResidenceType.values()) {
      if (userGender.getDesc().equals(desc)) {
        return userGender;
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
