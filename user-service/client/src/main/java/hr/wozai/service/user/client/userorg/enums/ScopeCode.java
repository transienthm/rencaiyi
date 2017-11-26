// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum ScopeCode {

  // ORG表示全局范围,ORG_BELONG表示只属于org
  OWNER(1, "owner"),
  SUBTEAM(2, "subteam"),
  TEAM(3, "team"),
  ORG(4, "org"),
  UPTEAM(5, "upteam"),
  SUBORDINATE(6, "subordinate"),
  ORG_BELONG(7, "org_belong"),
  PROJECT_TEAM(8, "project_team");

  private Integer code;
  private String desc;

  private ScopeCode(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static ScopeCode getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ScopeCode refreshTokenStatus : ScopeCode.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static ScopeCode getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (ScopeCode refreshTokenStatus : ScopeCode.values()) {
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
}
