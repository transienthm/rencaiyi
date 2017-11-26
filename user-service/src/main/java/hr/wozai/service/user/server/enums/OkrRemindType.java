// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 16/10/9
 */
public enum OkrRemindType {

  OBJECTIVE_DEADLINE(0, 3),
  OBJECTIVE_PERIOD_DEADLINE(1, 7),
  KEY_RESULT_DEADLINE(2, 3);

  private Integer code;
  private Integer defaultFrequency;

  OkrRemindType(Integer code, Integer defaultFrequency) {
    this.code = code;
    this.defaultFrequency = defaultFrequency;
  }

  public Integer getCode() {
    return code;
  }

  public Integer getDefaultFrequency() {
    return defaultFrequency;
  }

  public static boolean isValidType(int type) {
    for (OkrRemindType okrRemindType : OkrRemindType.values()) {
      if (okrRemindType.getCode() == type) {
        return true;
      }
    }
    return false;
  }
}
