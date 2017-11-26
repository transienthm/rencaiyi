// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-04
 */
public enum TemplateListType {

  VIEW(1, "VIEW"),
  MANAGE(2, "MANAGE");
  private Integer code;
  private String desc;

  private TemplateListType(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static TemplateListType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (TemplateListType templateListType : TemplateListType.values()) {
      if (IntegerUtils.equals(templateListType.code, code)) {
        return templateListType;
      }
    }

    return null;
  }

  public static TemplateListType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (TemplateListType templateListType : TemplateListType.values()) {
      if (templateListType.getDesc().equals(desc)) {
        return templateListType;
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

  public static boolean isValidType(int type) {
    for (TemplateListType objectiveType : TemplateListType.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }

}
