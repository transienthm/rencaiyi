package hr.wozai.service.thirdparty.server.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/29
 */
public enum MessageStatus {

  UNREAD(0, "UNREAD"),
  READ(1, "READ");

  private Integer code;
  private String desc;

  MessageStatus(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public MessageStatus getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (MessageStatus verificationStatus: MessageStatus.values()) {
      if (IntegerUtils.equals(verificationStatus.getCode(), code)) {
        return verificationStatus;
      }
    }

    return null;
  }

  public MessageStatus getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (MessageStatus verificationStatus: MessageStatus.values()) {
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
