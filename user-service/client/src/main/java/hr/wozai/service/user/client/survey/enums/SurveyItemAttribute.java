// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.survey.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 16/10/9
 */
public enum SurveyItemAttribute {

  SCALE_QUESTION(1, "10项选择", 0),
  BOOLEAN_QUESTION(2, "2项选择", 0),
  COMMON_QUESTION(3, "文字回答", 0),

  ONE(4, "1", 1),
  TWO(5, "2", 2),
  THREE(6, "3", 3),
  FOUR(7, "4", 4),
  FIVE(8, "5", 5),
  SIX(9, "6", 6),
  SEVEN(10, "7", 7),
  EIGHT(11, "8", 8),
  NINE(12, "9", 9),
  TEN(13, "10", 10),

  YES(14, "YES", 0),
  NO(15, "NO", 0);


  private Integer code;
  private String desc;
  private Integer score;

  SurveyItemAttribute(Integer code, String desc, Integer score) {
    this.code = code;
    this.desc = desc;
    this.score = score;
  }

  public Integer getCode() {
    return code;
  }

  public String getDesc() {
    return desc;
  }

  public Integer getScore() {
    return score;
  }

  public static SurveyItemAttribute getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (SurveyItemAttribute surveyItemAttribute : SurveyItemAttribute.values()) {
      if (IntegerUtils.equals(surveyItemAttribute.code, code)) {
        return surveyItemAttribute;
      }
    }

    return null;
  }

  public static boolean isValidType(int code) {
    if (code == SCALE_QUESTION.getCode() || code == BOOLEAN_QUESTION.getCode() || code == COMMON_QUESTION.getCode()) {
      return true;
    }
    return false;
  }
}
