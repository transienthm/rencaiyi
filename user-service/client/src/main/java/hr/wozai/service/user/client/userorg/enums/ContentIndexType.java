// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum ContentIndexType {

  USER_NAME(1, "user_name"),
  TEAM_NAME(2, "team_name"),
  PROJECT_TEAM_NAME(3, "project_team_name");

  private Integer code;
  private String desc;

  private ContentIndexType(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static ContentIndexType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ContentIndexType refreshTokenStatus : ContentIndexType.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static ContentIndexType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (ContentIndexType refreshTokenStatus : ContentIndexType.values()) {
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
    for (ContentIndexType contentIndexType : ContentIndexType.values()) {
      if (contentIndexType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
