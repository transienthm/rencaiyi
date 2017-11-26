// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-04
 */
public enum MemberStatus {

  INVITED(1, "INVITED"),
  ACTIVE(2, "ACTIVE"),
  REMOVED(3, "REMOVED"),
  ;

  private int code;
  private String msg;

  private MemberStatus(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static MemberStatus getEnumByCode(int code) {
    for (MemberStatus memberStatus : MemberStatus.values()) {
      if (memberStatus.code == code) {
        return memberStatus;
      }
    }
    return null;
  }

  public static MemberStatus getEnumByDesc(String desc) {
    if (null == desc) {
      return null;
    }
    for (MemberStatus memberStatus : MemberStatus.values()) {
      if (memberStatus.getMsg().equals(desc)) {
        return memberStatus;
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
