package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
public enum FieldUsability {

  USED_CHANGABLE(1, "USED_CHANGABLE"),
  USED_UNCHANGABLE(2, "USED_UNCHANGABLE"),
  UNUSED_CHANGABLE(3, "UNUSED_CHANGABLE"),
  UNUSED_UNCHANGABLE(4, "UNUSED_UNCHANGABLE"),
  ;

  private int code;
  private String msg;

  private FieldUsability(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static FieldUsability getEnumByCode(int code) {
    for (FieldUsability usability : FieldUsability.values()) {
      if (usability.code == code) {
        return usability;
      }
    }
    return null;
  }

  public static FieldUsability getEnumByDesc(String desc) {
    if (null == desc) {
      return null;
    }
    for (FieldUsability usability : FieldUsability.values()) {
      if (usability.getMsg().equals(desc)) {
        return usability;
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
