// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
public enum FieldSearchability {

  UNSEARCHABLE(1, "UNSEARCHABLE"),
  INTERNAL_SEARCHABLE(2, "INTERNAL_SEARCHABLE"),
  ;

  private int code;
  private String msg;

  private FieldSearchability(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static FieldSearchability getEnumByCode(int code) {
    for (FieldSearchability searchability : FieldSearchability.values()) {
      if (searchability.code == code) {
        return searchability;
      }
    }
    return null;
  }

  public static FieldSearchability getEnumByDesc(String desc) {
    if (null == desc) {
      return null;
    }
    for (FieldSearchability searchability : FieldSearchability.values()) {
      if (searchability.getMsg().equals(desc)) {
        return searchability;
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
