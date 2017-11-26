// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.enums;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
public enum FileStorageStatus {

  REQUESTED(1, "REQUESTED"),
  UPLOADED(1, "UPLOADED"),
  ;

  private int code;
  private String msg;

  private FileStorageStatus(int code, String desc) {
    this.code = code;
    this.msg = desc;
  }

  public static FileStorageStatus getEnumByCode(int code) {
    for (FileStorageStatus storageStatuss : FileStorageStatus.values()) {
      if (storageStatuss.code == code) {
        return storageStatuss;
      }
    }
    return null;
  }

  public static FileStorageStatus getEnumByDesc(String desc) {
    if (null == desc) {
      return null;
    }
    for (FileStorageStatus storageStatuss : FileStorageStatus.values()) {
      if (storageStatuss.getMsg().equals(desc)) {
        return storageStatuss;
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
