package hr.wozai.service.thirdparty.server.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * Author:  Zhe Chen Created: 2015-08-20
 */
public enum VerificationStatus {

  SENT(1, "SENT"),
  VERIFIED(2, "VERIFIED");

  private Integer code;
  private String desc;

  private VerificationStatus(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public VerificationStatus getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (VerificationStatus verificationStatus: VerificationStatus.values()) {
      if (IntegerUtils.equals(verificationStatus.getCode(), code)) {
        return verificationStatus;
      }
    }

    return null;
  }

  public VerificationStatus getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (VerificationStatus verificationStatus: VerificationStatus.values()) {
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
