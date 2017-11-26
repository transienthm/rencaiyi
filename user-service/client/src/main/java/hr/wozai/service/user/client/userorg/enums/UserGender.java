// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum UserGender {

  MALE(0, "男"),
  FEMALE(1, "女"),
  ;

  private Integer code;
  private String desc;

  private UserGender(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static UserGender getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (UserGender userGender : UserGender.values()) {
      if (IntegerUtils.equals(userGender.code, code)) {
        return userGender;
      }
    }

    return null;
  }

  public static UserGender getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (UserGender userGender : UserGender.values()) {
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
