// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-03
 */
public enum EmploymentStatus {

  /**
   * 试用期
   */
  PROBATIONARY(1, "试用"),

  /**
   * 已转正
   */
  REGULAR(2, "正式"),
  ;

  private int code;
  private String msg;

  private EmploymentStatus(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static EmploymentStatus getEnumByCode(int code) {

    for (EmploymentStatus employmentStatus : EmploymentStatus.values()) {
      if (employmentStatus.code == code) {
        return employmentStatus;
      }
    }

    return null;
  }

  public static EmploymentStatus getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (EmploymentStatus employmentStatus : EmploymentStatus.values()) {
      if (employmentStatus.getMsg().equals(desc)) {
        return employmentStatus;
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
