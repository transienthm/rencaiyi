// Copyright (C) 2015 Wozai
// All rights reserved

//package hr.wozai.service.user.client.common.enums;
package hr.wozai.service.user.client.common.enums;
import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum RemindType {

  NEWSFEED_AT(1, "有人在动态中@我", 1),
  NEWSFEED_COMMENT(2, "有人评论我发布的动态", 1),
  TEAM_OKR_UPDATE(3, "所负责的目标有更新", 1);

  private Integer code;
  private String desc;
  private Integer defaultStatus;

  private RemindType(Integer code, String desc, Integer defaultStatus) {
    this.code = code;
    this.desc = desc;
    this.defaultStatus = defaultStatus;
  }

  public static RemindType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (RemindType refreshTokenStatus : RemindType.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static RemindType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (RemindType refreshTokenStatus : RemindType.values()) {
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

  public Integer getDefaultStatus() {
    return defaultStatus;
  }

  public static boolean isValidType(int type) {
    for (RemindType contentIndexType : RemindType.values()) {
      if (contentIndexType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
