// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-06
 */
public enum ConfigType {

  JOB_TITLE(1, "JOB_TITLE"),
  JOB_LEVEL(2, "JOB_LEVEL"),
  ;

  private Integer code;
  private String desc;

  private ConfigType(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static ConfigType getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (ConfigType configType : ConfigType.values()) {
      if (IntegerUtils.equals(configType.code, code)) {
        return configType;
      }
    }

    return null;
  }

  public static ConfigType getEnumByDesc(String desc) {

    if (null == desc) {
      return null;
    }
    for (ConfigType configType : ConfigType.values()) {
      if (configType.getDesc().equals(desc)) {
        return configType;
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
    for (ConfigType objectiveType : ConfigType.values()) {
      if (objectiveType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
