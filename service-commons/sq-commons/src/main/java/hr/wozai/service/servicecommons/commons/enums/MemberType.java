// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-04
 */
public enum MemberType {

  OWNER(1, "OWNER"),
  ADMIN(2, "ADMIN"),
  STAFF(3, "STAFF"),
  ;

  private int code;
  private String msg;

  private MemberType(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static MemberType getEnumByCode(int code) {
    for (MemberType memberType : MemberType.values()) {
      if (memberType.code == code) {
        return memberType;
      }
    }
    return null;
  }

  public static MemberType getEnumByDesc(String desc) {
    if (null == desc) {
      return null;
    }
    for (MemberType memberType : MemberType.values()) {
      if (memberType.getMsg().equals(desc)) {
        return memberType;
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
