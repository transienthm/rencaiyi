// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.okr.enums;

import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;

import java.text.ParseException;
import java.util.*;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016-06-22
 */
public enum PeriodTimeSpan {
  December(100, "12月", PeriodTimeSpanType.MONTH.getCode(), 12, 12),
  November(200, "11月", PeriodTimeSpanType.MONTH.getCode(), 11, 11),
  October(300, "10月", PeriodTimeSpanType.MONTH.getCode(), 10, 10),

  Fourth_quarter(400, "第四季度", PeriodTimeSpanType.QUARTER.getCode(), 10, 12),

  September(500, "9月", PeriodTimeSpanType.MONTH.getCode(), 9, 9),
  August(600, "8月", PeriodTimeSpanType.MONTH.getCode(), 8, 8),
  July(700, "7月", PeriodTimeSpanType.MONTH.getCode(), 7, 7),

  Third_quarter(800, "第三季度", PeriodTimeSpanType.QUARTER.getCode(), 7, 9),

  Second_half_year(900, "下半年", PeriodTimeSpanType.HALF_YEAR.getCode(), 7, 12),

  June(1000, "6月", PeriodTimeSpanType.MONTH.getCode(), 6, 6),
  May(1100, "5月", PeriodTimeSpanType.MONTH.getCode(), 5, 5),
  April(1200, "4月", PeriodTimeSpanType.MONTH.getCode(), 4, 4),

  Second_quarter(1300, "第二季度", PeriodTimeSpanType.QUARTER.getCode(), 4, 6),

  March(1400, "3月", PeriodTimeSpanType.MONTH.getCode(), 3, 3),
  February(1500, "2月", PeriodTimeSpanType.MONTH.getCode(), 2, 2),
  January(1600, "1月", PeriodTimeSpanType.MONTH.getCode(), 1, 1),

  First_quarter(1700, "第一季度", PeriodTimeSpanType.QUARTER.getCode(), 1, 3),

  First_half_year(1800, "上半年", PeriodTimeSpanType.HALF_YEAR.getCode(), 1, 6),

  Year(1900, "", PeriodTimeSpanType.YEAR.getCode(), 1, 12);

  private Integer code;
  private String name;
  private Integer type;
  private Integer startMonth;
  private Integer endMonth;

  PeriodTimeSpan(Integer code, String name, Integer type, Integer startYear, Integer endYear) {
    this.code = code;
    this.name = name;
    this.type = type;
    this.startMonth = startYear;
    this.endMonth = endYear;
  }

  public static PeriodTimeSpan getEnumByCode(Integer code) {

    if (null == code) {
      return null;
    }
    for (PeriodTimeSpan refreshTokenStatus : PeriodTimeSpan.values()) {
      if (IntegerUtils.equals(refreshTokenStatus.code, code)) {
        return refreshTokenStatus;
      }
    }

    return null;
  }

  public static PeriodTimeSpan getEnumByName(String name) {

    if (null == name) {
      return null;
    }
    for (PeriodTimeSpan predefinedPeriod : PeriodTimeSpan.values()) {
      if (predefinedPeriod.getName().equals(name)) {
        return predefinedPeriod;
      }
    }

    return null;
  }

  public static boolean isValidPeriodTimeSpan(int code) {
    for (PeriodTimeSpan periodTimeSpan : PeriodTimeSpan.values()) {
      if (periodTimeSpan.getCode() == code) {
        return true;
      }
    }
    return false;
  }

  public static boolean isInMonthRegion(Integer month, PeriodTimeSpan predefinedPeriod) {
    return month >= predefinedPeriod.getStartMonth() && month <= predefinedPeriod.getEndMonth();
  }

  public static List<PeriodTimeSpan> getPeriodListByType(Integer type) {
    List<PeriodTimeSpan> result = new ArrayList<>();
    if (null == type) {
      return result;
    }
    for (PeriodTimeSpan predefinedPeriod : PeriodTimeSpan.values()) {
      if (IntegerUtils.equals(predefinedPeriod.type, type)) {
        result.add(predefinedPeriod);
      }
    }
    Collections.reverse(result);
    return result;
  }

  public static String getNameByYearAndPeriodTimeSpan(Integer year, PeriodTimeSpan periodTimeSpan) {
    return year + "年" + periodTimeSpan.getName();
  }



  public Integer getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public Integer getType() {
    return type;
  }

  public Integer getStartMonth() {
    return startMonth;
  }

  public Integer getEndMonth() {
    return endMonth;
  }

  public static int getYearByCurrentYearAndPeriodTimeSpan(int year, PeriodTimeSpan periodTimeSpan) {
    if (periodTimeSpan == PeriodTimeSpan.December
            || periodTimeSpan == PeriodTimeSpan.Fourth_quarter
            || periodTimeSpan == PeriodTimeSpan.Second_half_year
            || periodTimeSpan == PeriodTimeSpan.Year) {
      return year + 1;
    } else {
      return year;
    }
  }

  public static PeriodTimeSpan getNextPeriodTimeSpan(PeriodTimeSpan periodTimeSpan) {
    if (periodTimeSpan == PeriodTimeSpan.January) {
      return PeriodTimeSpan.February;
    } else if (periodTimeSpan == PeriodTimeSpan.February) {
      return PeriodTimeSpan.March;
    } else if (periodTimeSpan == PeriodTimeSpan.March) {
      return PeriodTimeSpan.April;
    } else if (periodTimeSpan == PeriodTimeSpan.April) {
      return PeriodTimeSpan.May;
    } else if (periodTimeSpan == PeriodTimeSpan.May) {
      return PeriodTimeSpan.June;
    } else if (periodTimeSpan == PeriodTimeSpan.June) {
      return PeriodTimeSpan.July;
    } else if (periodTimeSpan == PeriodTimeSpan.July) {
      return PeriodTimeSpan.August;
    } else if (periodTimeSpan == PeriodTimeSpan.August) {
      return PeriodTimeSpan.September;
    } else if (periodTimeSpan == PeriodTimeSpan.September) {
      return PeriodTimeSpan.October;
    } else if (periodTimeSpan == PeriodTimeSpan.October) {
      return PeriodTimeSpan.November;
    } else if (periodTimeSpan == PeriodTimeSpan.November) {
      return PeriodTimeSpan.December;
    } else if (periodTimeSpan == PeriodTimeSpan.December) {
      return PeriodTimeSpan.January;
    } else if (periodTimeSpan == PeriodTimeSpan.First_quarter) {
      return PeriodTimeSpan.Second_quarter;
    } else if (periodTimeSpan == PeriodTimeSpan.Second_quarter) {
      return PeriodTimeSpan.Third_quarter;
    } else if (periodTimeSpan == PeriodTimeSpan.Third_quarter) {
      return PeriodTimeSpan.Fourth_quarter;
    } else if (periodTimeSpan == PeriodTimeSpan.Fourth_quarter) {
      return PeriodTimeSpan.First_quarter;
    } else if (periodTimeSpan == PeriodTimeSpan.First_half_year) {
      return PeriodTimeSpan.Second_half_year;
    } else if (periodTimeSpan == PeriodTimeSpan.Second_half_year) {
      return PeriodTimeSpan.First_half_year;
    } else {
      return PeriodTimeSpan.Year;
    }
  }

  public static List<Integer> getPeriodTimeSpanIdListByMonth (int month) {
    List<Integer> result = new ArrayList<>();
    for (PeriodTimeSpan periodTimeSpan : PeriodTimeSpan.values()) {
      if (month == periodTimeSpan.getEndMonth()) {
        result.add(periodTimeSpan.getCode());
      }
    }
    return result;
  }
}
