// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016-3-11
 */
public enum ResourceCode {

  //LARGEST:16
  //okr相关
  OKR(1, "001", "okr"),
  OKR_PERIOD(15, "015", "okr_period"),
  //newsfeed相关
  NEWS_FEED(2, "002", "news_feed"),
  NEWS_FEED_COMMENT(3, "003", "news_feed_comment"),

  //onboarding相关
  PROFILE_META(4, "004", "profile_meta"),
  ONBOARDING_META(5, "005", "onboarding_meta"),
  DOCUMENT(6, "006", "document"),
  ONBOARDING_FLOW(7, "007", "onboarding_flow"),
  USER_PROFILE(8, "008", "user_profile"),

  //组织架构相关
  USER_ORG(9, "009", "user_org"),


  //评价管理
  REVIEW_ADMIN(10, "010", "review_admin"),

  //入职管理
  ONBOARDING_ADMIN(11, "011", "onboarding_admin"),

  //组织架构管理
  USER_ORG_ADMIN(12, "012", "user_org_admin"),

  //OKR管理
  OKR_ADMIN(13, "013", "okr_admin"),

  //系统设置:公司设置,员工档案模板设置,职位职级设置,权限设置
  SYSTEM_ADMIN(14, "014", "system_admin"),

  //汇报关系
  REPORT_LINE(16, "016", "report_line"),

  //org相关
  ORG(17, "017", "org"),

  //user_role 相关
  USER_ROLE(18, "018", "user_role"),

  //匿名调研管理
  SURVEY_ADMIN(19, "019", "survey_admin");



  private Integer code;
  private String resourceCode;
  private String resourceName;

  ResourceCode(Integer code, String resourceCode, String resourceName) {
    this.code = code;
    this.resourceCode = resourceCode;
    this.resourceName = resourceName;
  }

  public static ResourceCode getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ResourceCode refreshTokenStatus : ResourceCode.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static ResourceCode getEnumByDesc(String resourceCode) {

    if (null == resourceCode) {
      return null;
    }
    for (ResourceCode refreshTokenStatus : ResourceCode.values()) {
      if (refreshTokenStatus.getResourceCode().equals(resourceCode)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public Integer getCode() {
    return code;
  }

  public String getResourceCode() {
    return resourceCode;
  }

  public String getResourceName() {
    return resourceName;
  }

  public static boolean isValidType(int type) {
    for (ResourceCode objectiveType : ResourceCode.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
