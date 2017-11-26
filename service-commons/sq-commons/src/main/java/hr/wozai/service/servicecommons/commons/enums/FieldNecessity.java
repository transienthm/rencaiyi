package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
public enum FieldNecessity {

  REQUIRED_CHANGABLE(1, "REQUIRED_CHANGABLE"),
  REQUIRED_UNCHANGABLE(2, "REQUIRED_UNCHANGABLE"),
  UNREQUIRED_CHANGABLE(3, "UNREQUIRED_CHANGABLE"),
  UNREQUIRED_UNCHANGABLE(4, "UNREQUIRED_UNCHANGABLE"),
  ;

  private int code;
  private String msg;

  private FieldNecessity(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static FieldNecessity getEnumByCode(int code) {
    for (FieldNecessity necessity : FieldNecessity.values()) {
      if (necessity.code == code) {
        return necessity;
      }
    }
    return null;
  }

  public static FieldNecessity getEnumByDesc(String desc) {
    if (null == desc) {
      return null;
    }
    for (FieldNecessity necessity : FieldNecessity.values()) {
      if (necessity.getMsg().equals(desc)) {
        return necessity;
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
