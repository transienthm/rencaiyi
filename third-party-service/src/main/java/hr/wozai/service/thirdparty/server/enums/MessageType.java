package hr.wozai.service.thirdparty.server.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/29
 */
public enum MessageType {

  PERSONAL(0, "PERSONAL"),
  SYSTEM(1, "SYSTEM");

  private Integer code;
  private String desc;

  MessageType(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public MessageType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (MessageType verificationStatus: MessageType.values()) {
      if (IntegerUtils.equals(verificationStatus.getCode(), code)) {
        return verificationStatus;
      }
    }

    return null;
  }

  public MessageType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (MessageType verificationStatus: MessageType.values()) {
      if (verificationStatus.getDesc().equals(desc)) {
        return verificationStatus;
      }
    }

    return null;
  }

  public Integer getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }
}
