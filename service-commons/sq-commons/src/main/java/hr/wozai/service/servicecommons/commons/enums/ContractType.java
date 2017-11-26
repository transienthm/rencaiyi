// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-03
 */
public enum ContractType {

  INTERNSHIP(1, "实习"),
  PARTTIME(2, "兼职"),
  FULLTIME(3, "全职"),

  ;

  private int code;
  private String msg;

  private ContractType(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static ContractType getEnumByCode(int code) {

    for (ContractType contractType : ContractType.values()) {
      if (contractType.code == code) {
        return contractType;
      }
    }

    return null;
  }

  public static ContractType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (ContractType contractType : ContractType.values()) {
      if (contractType.getMsg().equals(desc)) {
        return contractType;
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
