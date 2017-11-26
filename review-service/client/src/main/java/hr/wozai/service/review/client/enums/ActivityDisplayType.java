// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-04
 */
public enum ActivityDisplayType {

  EDIT(1, "EDIT"),
  VIEW_OR_UPDATE(2, "VIEW_OR_UPDATE"),
  VIEW(3, "VIEW"),
  EXPIRED_CAN_EDIT(4, "EXPIRED_CAN_EDIT"),
  EXPIRED_CANNOT_EDIT(5, "EXPIRED_CANNOT_EDIT"),

  ;

  private int code;
  private String name;

  ActivityDisplayType(int code, String desc) {
    this.name = name;
    this.code = code;
  }

  public static ActivityDisplayType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ActivityDisplayType activityDisplayType : ActivityDisplayType.values()) {
      if (IntegerUtils.equals(activityDisplayType.code, code)) {
        return activityDisplayType;
      }
    }

    return null;
  }


  public static String getName(int code) {
    for(ActivityDisplayType activityDisplayType: ActivityDisplayType.values()) {
      if(activityDisplayType.getCode() == code) {
        return activityDisplayType.name;
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
