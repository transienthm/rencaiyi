// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 16/3/17
 */
public enum DefaultRole {

  ORG_ADMIN(1, "orgAdmin", "公司管理层"),
  // TEAM_ADMIN(2, "teamAdmin", "团队管理员"),
  HR(3, "HR", "HR"),
  STAFF(4, "staff", "成员"),
  //SENIOR_STAFF(5, "seniorStaff", "成员 (高级权限)"),
  SUPER_ADMIN(6, "superAdmin", "超级管理员");

  private Integer code;
  private String name;
  private String desc;

  DefaultRole(Integer code, String name, String desc) {
    this.code = code;
    this.name = name;
    this.desc = desc;
  }

  public static DefaultRole getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (DefaultRole defaultRole : DefaultRole.values()) {
      if (IntegerUtils.equals(defaultRole.code, code)) {
        return defaultRole;
      }
    }

    return null;
  }

  public static DefaultRole getEnumByName(String name) {

    if (null == name) {
      return null;
    }
    for (DefaultRole defaultRole : DefaultRole.values()) {
      if (defaultRole.getName().equals(name)) {
        return defaultRole;
      }
    }

    return null;
  }

  public Integer getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public String getDesc() {
    return desc;
  }

  public static boolean isValidType(int type) {
    for (DefaultRole defaultRole : DefaultRole.values()) {
      if (defaultRole.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
