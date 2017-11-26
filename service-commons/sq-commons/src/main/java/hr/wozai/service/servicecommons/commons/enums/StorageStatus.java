package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 0.0.5
 * @Created: 2016-02-19
 */
public enum StorageStatus {

  REQUESTED(1, "REQUESTED"),
  UPLOADED(2, "UPLOADED"),
  REMOVED(3, "REMOVED"),
  ;

  private int code;
  private String msg;

  private StorageStatus(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static StorageStatus getEnumByCode(int code) {
    for (StorageStatus storageStatus : StorageStatus.values()) {
      if (storageStatus.code == code) {
        return storageStatus;
      }
    }
    return null;
  }

  public static StorageStatus getEnumByDesc(String desc) {
    if (null == desc) {
      return null;
    }
    for (StorageStatus storageStatus : StorageStatus.values()) {
      if (storageStatus.getMsg().equals(desc)) {
        return storageStatus;
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
