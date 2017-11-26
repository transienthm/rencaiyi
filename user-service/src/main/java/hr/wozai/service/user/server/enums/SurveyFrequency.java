// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 16/10/9
 */
public enum SurveyFrequency {

  ONE_WEEK(1, "每周", 1, 7),
  TWO_WEEK(2, "每两周", 2, 14);


  private Integer code;
  private String desc;
  private Integer times;
  private Integer days;

  SurveyFrequency(Integer code, String desc, Integer times, Integer days) {
    this.code = code;
    this.desc = desc;
    this.times = times;
    this.days = days;
  }

  public static SurveyFrequency getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (SurveyFrequency surveyFrequency : SurveyFrequency.values()) {
      if (IntegerUtils.equals(surveyFrequency.code, code)) {
        return surveyFrequency;
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

  public Integer getTimes() {
    return times;
  }

  public Integer getDays() {
    return days;
  }

  public static boolean isValidType(int code) {
    for (SurveyFrequency frequency : SurveyFrequency.values()) {
      if (frequency.getCode() == code) {
        return true;
      }
    }
    return false;
  }
}
