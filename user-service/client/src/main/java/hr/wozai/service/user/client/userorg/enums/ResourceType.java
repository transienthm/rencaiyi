// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum ResourceType {

  ORG(1, "org"),
  TEAM(2, "team"),
  PERSON(3, "person"),
  PROJECT_TEAM(4, "project_team");

  private Integer code;
  private String desc;

  private ResourceType(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static ResourceType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ResourceType refreshTokenStatus : ResourceType.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static ResourceType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (ResourceType refreshTokenStatus : ResourceType.values()) {
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
    for (ResourceType objectiveType : ResourceType.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
