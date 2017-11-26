// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-10-15
 */
public enum PageStatus {

  TEAM_INVITATION_OK(10000, "您被邀请使用闪签，请补全信息完成注册"),
  RESET_PASSWORD_URL_OK(10001, "请输入您的新密码以进行重置"),

  EMAIL_ACTIVATION_OK(20000, "邮件激活成功，请登录"),

  EMAIL_ACTIVATION_EXISTING(30400, "账户已经激活，请直接登录"),

  RESET_PASSWORD_URL_INVALID(40123, "重置密码链接不可用"),
  EMAIL_ACTIVATION_EXPIRED(40130, "邮件激活链接过期"),
  TEAM_INVITATION_EXPIRED(40131, "团队邀请链接过期，请联系团队管理员重新发送邀请"),
  EMAIL_ACTIVATION_INVALID(40121, "邮件激活链接非法"),
  TEAM_INVITATION_INVALID(40122, "团队邀请链接非法"),

  PageStatus(9999,"TEST");

  private int code;
  private String msg;

  private PageStatus(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static PageStatus getEnumByCode(int code) {

    for (PageStatus pageStatus : PageStatus.values()) {
      if (pageStatus.code == code) {
        return pageStatus;
      }
    }

    return null;
  }

  public static PageStatus getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }

    for (PageStatus pageStatus : PageStatus.values()) {
      if (pageStatus.getMsg().equals(desc)) {
        return pageStatus;
      }
    }

    return null;
  }

  public int getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
}
