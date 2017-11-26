package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 0.0.5
 * @Created: 2016-02-19
 */
public enum DataType {

  CONTAINER(1, "CTN"),
  INTEGER(2, "INT"),
  DECIMAL(3, "DCM"),
  DATETIME(4, "DT"),
  SHORT_TEXT(5, "STXT"),  // VARCHAR(100)
  LONG_TEXT(6, "LTXT"),   // VARCHAR(255)
  BLOCK_TEXT(7, "BTXT"),  
  ADDRESS(8, "ADDR"),
  BOOLEAN(9, "BOOL"),
  SINGLE_PICK(10, "SPK"),
  MULTI_PICK(11, "MPK"),
  REFERENCE(12, "REF"),
  FILE(13, "FILE"),
  FILES(14, "FILES"),

  ;

  private int code;
  private String msg;

  private DataType(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static DataType getEnumByCode(int code) {
    for (DataType dataType : DataType.values()) {
      if (dataType.code == code) {
        return dataType;
      }
    }
    return null;
  }

  public static DataType getEnumByDesc(String desc) {
    if (null == desc) {
      return null;
    }
    for (DataType dataType : DataType.values()) {
      if (dataType.getMsg().equals(desc)) {
        return dataType;
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
