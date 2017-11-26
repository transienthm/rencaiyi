// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum UserDegreeLevel {

  PRIMARY_SCHOOL(0, "小学"),
  JUNIOR_HIGH_SCHOOL(1, "初中"),
  SENIOR_HIGH_SCHOOL(2, "高中"),
  VOCATIONAL_COLLEGE(3, "大专"),
  BACHELOR(4, "本科"),
  MASTER(5, "研究生"),
  PHD(6, "博士"),

  ;

  private Integer code;
  private String desc;

  private UserDegreeLevel(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static UserDegreeLevel getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (UserDegreeLevel userDegreeLevel : UserDegreeLevel.values()) {
      if (IntegerUtils.equals(userDegreeLevel.code, code)) {
        return userDegreeLevel;
      }
    }

    return null;
  }

  public static UserDegreeLevel getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (UserDegreeLevel userDegreeLevel : UserDegreeLevel.values()) {
      if (userDegreeLevel.getDesc().equals(desc)) {
        return userDegreeLevel;
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
