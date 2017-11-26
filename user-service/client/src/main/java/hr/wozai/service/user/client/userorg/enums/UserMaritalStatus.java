// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum UserMaritalStatus {

  HAS_SPOUSE(0, "有配偶"),
  NO_SPOUSE(1, "无配偶"),
  ;

  private Integer code;
  private String desc;

  private UserMaritalStatus(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static UserMaritalStatus getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (UserMaritalStatus userMaritalStatus : UserMaritalStatus.values()) {
      if (IntegerUtils.equals(userMaritalStatus.code, code)) {
        return userMaritalStatus;
      }
    }

    return null;
  }

  public static UserMaritalStatus getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (UserMaritalStatus userMaritalStatus : UserMaritalStatus.values()) {
      if (userMaritalStatus.getDesc().equals(desc)) {
        return userMaritalStatus;
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
