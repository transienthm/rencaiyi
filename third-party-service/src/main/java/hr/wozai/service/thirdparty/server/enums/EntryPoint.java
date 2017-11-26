package hr.wozai.service.thirdparty.server.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-07
 */
public enum EntryPoint {

  SIGNUP(1, "SIGNUP"),
  INVITATION(2, "INVITATION"),
  DOCUMENT(3, "DOCUMENT"),

  TEST(9999, "TEST");

  private Integer code;
  private String msg;

  private EntryPoint(Integer code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public static EntryPoint getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (EntryPoint entryPoint : EntryPoint.values()) {
      if (IntegerUtils.equals(entryPoint.code, code)) {
        return entryPoint;
      }
    }

    return null;
  }

  public static EntryPoint getEnumByMsg(String msg) {

    if (null == msg) {
      return null;
    }
    for (EntryPoint entryPoint : EntryPoint.values()) {
      if (entryPoint.getMsg().equals(msg)) {
        return entryPoint;
      }
    }

    return null;
  }

  public Integer getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
}
