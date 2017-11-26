// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum RecentUsedObjectType {

  AT_USER(1, "at user"),
  USER_OKR(2, "user_okr"),
  TEAM_OKR(3, "team_okr"),
  PROJECT_TEAM_OKR(4, "project_team_okr");

  private Integer code;
  private String desc;

  private RecentUsedObjectType(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static RecentUsedObjectType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (RecentUsedObjectType refreshTokenStatus : RecentUsedObjectType.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static RecentUsedObjectType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (RecentUsedObjectType refreshTokenStatus : RecentUsedObjectType.values()) {
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
    for (RecentUsedObjectType recentUsedObjectType : RecentUsedObjectType.values()) {
      if (recentUsedObjectType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
