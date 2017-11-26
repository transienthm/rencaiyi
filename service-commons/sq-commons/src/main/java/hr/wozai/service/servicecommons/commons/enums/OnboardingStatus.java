// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-03
 */
public enum OnboardingStatus {

  ONBOARDING(1, "ONBOARDING"),
  SUBMITTED(2, "SUBMITTED"),
  APPROVED(3, "APPROVED"),

  ;

  private int code;
  private String msg;

  private OnboardingStatus(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static OnboardingStatus getEnumByCode(int code) {

    for (OnboardingStatus onboardingStatus : OnboardingStatus.values()) {
      if (onboardingStatus.code == code) {
        return onboardingStatus;
      }
    }

    return null;
  }

  public static OnboardingStatus getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (OnboardingStatus onboardingStatus : OnboardingStatus.values()) {
      if (onboardingStatus.getMsg().equals(desc)) {
        return onboardingStatus;
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
