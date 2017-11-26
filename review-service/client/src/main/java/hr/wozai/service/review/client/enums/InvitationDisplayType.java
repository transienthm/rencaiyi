// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-04
 */
public enum InvitationDisplayType {

  REVIEWEE_UNSUBMITTED_UNEXPIRED(1, "REVIEWEE_UNSUBMITTED_UNEXPIRED"),
  EDIT(2, "EDIT"),
  VIEW_OR_UPDATE(3, "VIEW_OR_UPDATE"),
  VIEW(4, "VIEW"),
  EXPIRED_CAN_EDIT(5, "EXPIRED_CAN_EDIT"),
  EXPIRED_CANNOT_EDIT(6, "EXPIRED_CANNOT_EDIT"),
  PEER_REVIEW_REJECTED(7, "PEER_REVIEW_REJECTED"),
  FLOW_PENDING(8, "FLOW_PENDING"),
  REVIEWEE_UNSUBMITTED_EXPIRED(9, "REVIEWEE_UNSUBMITTED_EXPIRED"),

  ;

  private int code;
  private String name;

  InvitationDisplayType(int code, String desc) {
    this.name = name;
    this.code = code;
  }

  public static InvitationDisplayType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (InvitationDisplayType invitationDisplayType : InvitationDisplayType.values()) {
      if (IntegerUtils.equals(invitationDisplayType.code, code)) {
        return invitationDisplayType;
      }
    }

    return null;
  }

  public static String getName(int code) {
    for(InvitationDisplayType invitationDisplayType: InvitationDisplayType.values()) {
      if(invitationDisplayType.getCode() == code) {
        return invitationDisplayType.name;
      }
    }
    return null;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

}
