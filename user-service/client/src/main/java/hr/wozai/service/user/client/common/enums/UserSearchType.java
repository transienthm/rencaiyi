// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.common.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum UserSearchType {

  NORMAL(1, "正常搜索,只能搜到未离职的人"),
  ACTIVE(2, "搜可离职的人"),
  UN_REGULAR(3, "搜可转正的人"),
  AT(4, "搜可@的人"),
  UN_RESIGNED(5, "搜未离职的人"),
  CONVERSATION(6, "搜未被邀请交谈的人");

  private Integer code;
  private String desc;

  private UserSearchType(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static UserSearchType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (UserSearchType refreshTokenStatus : UserSearchType.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static UserSearchType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (UserSearchType refreshTokenStatus : UserSearchType.values()) {
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
    for (UserSearchType contentIndexType : UserSearchType.values()) {
      if (contentIndexType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
