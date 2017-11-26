// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.client.conversation.utils;

import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.conversation.enums.PeriodType;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;
import java.util.Date;
import java.util.TimeZone;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-12-01
 */
public class ConvrUtils {
  private final static Integer WEEK = 1;
  private final static Integer HALF_MONTH = 2;
  private final static Integer MONTH = 3;

  public static boolean isInCurrentPeriod(PeriodType periodType, long timestamp) throws Exception {
    Date checkDate = new Date(timestamp);

    Instant checkInstant = Instant.ofEpochMilli(checkDate.getTime());
    Date now = new Date(System.currentTimeMillis());
    Instant nowInstant = Instant.ofEpochMilli(now.getTime());

    LocalDate nowLocalDate =  LocalDateTime.ofInstant(nowInstant, ZoneId.of("Asia/Shanghai")).toLocalDate();
    LocalDate checkLocalDate = LocalDateTime.ofInstant(checkInstant, ZoneId.of("Asia/Shanghai")).toLocalDate();

    if (nowLocalDate.isEqual(checkLocalDate)) {
      return true;
    }
    if (periodType.equals(PeriodType.EVERY_MONTH)) {
      LocalDate firstDayOfMonth = nowLocalDate.with(TemporalAdjusters.firstDayOfMonth());
      LocalDate lastDayOfMonth = nowLocalDate.with(TemporalAdjusters.lastDayOfMonth());
      if (isBetweenTwoDays(checkLocalDate,firstDayOfMonth,lastDayOfMonth)) {
        return true;
      }
    } else if (periodType.equals(PeriodType.HALF_MONTH)) {
      LocalDate firstDayOfMonth = nowLocalDate.with(TemporalAdjusters.firstDayOfMonth());
      LocalDate dayOfMidMonth = firstDayOfMonth.plusDays(14);
      LocalDate beginOfSecondHalfMonth = dayOfMidMonth.plusDays(1);
      LocalDate lastDayOfMonth = nowLocalDate.with(TemporalAdjusters.lastDayOfMonth());
      if (nowLocalDate.isAfter(beginOfSecondHalfMonth) || nowLocalDate.isEqual(beginOfSecondHalfMonth)) {
        if (isBetweenTwoDays(checkLocalDate, beginOfSecondHalfMonth, lastDayOfMonth)) {
          return true;
        } else {
          return false;
        }
      }
      if (nowLocalDate.isBefore(dayOfMidMonth) || nowLocalDate.isEqual(dayOfMidMonth)) {
        if (isBetweenTwoDays(checkLocalDate, firstDayOfMonth, dayOfMidMonth)) {
          return true;
        } else {
          return false;
        }
      }

    } else if (periodType.equals(PeriodType.EVERY_WEEK)) {
      DayOfWeek dayOfWeek = nowLocalDate.getDayOfWeek();
      LocalDate firstDayOfWeek = nowLocalDate.minusDays(dayOfWeek.getValue() == 0 ? 6 : (dayOfWeek.getValue() - 1));
      LocalDate endDayOfWeek = nowLocalDate.plusDays(7 - (dayOfWeek.getValue() == 0 ? 7 : dayOfWeek.getValue()));
      if (isBetweenTwoDays(checkLocalDate, firstDayOfWeek, endDayOfWeek)) {
        return true;
      }
    }
    return false;
  }

  public static String getPeriodStartDayAndEndDay(Integer periodType, String date) {
    LocalDate localDate = LocalDate.parse(date);
    StringBuilder sb = new StringBuilder();
    LocalDate firstDayOfMonth = localDate.with(TemporalAdjusters.firstDayOfMonth());
    LocalDate midDayOfMonth = firstDayOfMonth.plusDays(14);
    LocalDate firstDayOfSecondMonth = midDayOfMonth.plusDays(1);
    LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
    Date firstDayOfMonthDate = convertLocalDateToDate(firstDayOfMonth);
    Date midDayOfMonthDate = convertLocalDateToDate(midDayOfMonth);
    Date firstDayOfSecondMonthDate = convertLocalDateToDate(firstDayOfSecondMonth);
    Date lastDayOfMonthDate = convertLocalDateToDate(lastDayOfMonth);
    if (periodType.equals(WEEK)) {
      int dayOfWeek = (localDate.getDayOfWeek().getValue() == DayOfWeek.SUNDAY.getValue()) ? 7 : localDate.getDayOfWeek().getValue();
      LocalDate firstDayOfWeek = localDate.minusDays(dayOfWeek - 1);
      LocalDate lastDayOfWeek = localDate.plusDays(7 - dayOfWeek);
      Date firstDayOfWeekDate = convertLocalDateToDate(firstDayOfWeek);
      Date lastDayOfWeekDate = convertLocalDateToDate(lastDayOfWeek);

      sb.append(TimeUtils.getDateStringOfBeijingTimezone(firstDayOfWeekDate.getTime()));
      return sb.toString();
    }
    if (periodType.equals(HALF_MONTH)) {

      if (isBetweenTwoDays(localDate, firstDayOfMonth, midDayOfMonth)) {
        sb.append(TimeUtils.getDateStringOfBeijingTimezone(firstDayOfMonthDate.getTime()));
        return sb.toString();
      } else if (isBetweenTwoDays(localDate, firstDayOfSecondMonth, lastDayOfMonth)) {
        sb.append(TimeUtils.getDateStringOfBeijingTimezone(firstDayOfSecondMonthDate.getTime()));
        return sb.toString();
      }
    }

    if (periodType.equals(MONTH)) {
      sb.append(TimeUtils.getDateStringOfBeijingTimezone(firstDayOfMonthDate.getTime()));
      return sb.toString();
    }
    return null;
  }

  public static boolean isBetweenTwoDays(LocalDate checkLocalDate, LocalDate begin, LocalDate end) {
    if ((checkLocalDate.isAfter(begin) && checkLocalDate.isBefore(end))
        || checkLocalDate.isEqual(begin) || checkLocalDate.isEqual(end)) {
      return true;
    } else {
      return false;
    }
  }

  /*public static void main(String[] args) throws Exception{
    Long now = System.currentTimeMillis();
    Long firstDayOfMonth = TimeUtils.getFirstDayOfMonth(2016, 11, TimeUtils.BEIJING);
    //isInCurrentPeriod(PeriodType.EVERY_MONTH, firstDayOfMonth);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Date date = sdf.parse("2016-11-28");
    Long timestamp = date.getTime();
    System.out.println(isInCurrentPeriod(PeriodType.EVERY_WEEK, timestamp));

  }*/

  public static void main(String[] args) {
/*    ConvrUtils convrScheduleTask = new ConvrUtils();
    Date date = new Date();
    String result = convrScheduleTask.nextTimeToRemind(date, 4, PeriodType.EVERY_MONTH);
    System.out.println(result);

    result = convrScheduleTask.nextTimeToRemind(date, 7, PeriodType.EVERY_WEEK);
    System.out.println(result);

    result = convrScheduleTask.nextTimeToRemind(date, 4, PeriodType.HALF_MONTH);
    System.out.println(result);*/



    ConvrUtils.getFirstDayOfCurrentQuarterByLocalDate(LocalDate.now());

  }



  public static String nextTimeToRemind(Date date, Integer remindDay, PeriodType periodType) {
    long eightOclockTimeStamp = TimeUtils.getTimestampOfEightOclockTodayOfInputTimestampInBeijingTime(date.getTime());
    if (periodType.equals(PeriodType.EVERY_MONTH)) {
      if (isTimeToEveryMonthReminder(date, remindDay)) {
        if (date.getTime() > eightOclockTimeStamp) {
          LocalDate localDate = convertDateToLocalDate(date);
          LocalDate nextTimeToRemind = localDate.plusMonths(1);
          String nextTimeToRemindStr = getFormatedTimeStrForReminder(convertLocalDateToDate(nextTimeToRemind));
          //sdf.format(convertLocalDateToDate(nextTimeToRemind));
          return nextTimeToRemindStr;
        } else {
          return getFormatedTimeStrForReminder(date);
        }
      } else {
        LocalDate firstReminderDayOfMonth = getFirstRemindDayOfMonth(convertDateToLocalDate(date), remindDay);
        if (isBeforeTimeToEveryMonthReminder(date, remindDay)) {
          return getFormatedTimeStrForReminder(convertLocalDateToDate(firstReminderDayOfMonth));
        } else {
          LocalDate nextRemindDay = firstReminderDayOfMonth.plusMonths(1);
          nextRemindDay = getFirstRemindDayOfMonth(nextRemindDay, remindDay);
          return getFormatedTimeStrForReminder(convertLocalDateToDate(nextRemindDay));
        }
      }
    }
    if (periodType.equals(PeriodType.EVERY_WEEK)) {
      Integer dayOfWeek = TimeUtils.getDayOfWeekFromDate(date);
      if (dayOfWeek.equals(remindDay)) {
        if (date.getTime() > eightOclockTimeStamp) {
          LocalDate localDate = convertDateToLocalDate(date);
          LocalDate nextTimeToRemind = localDate.plusWeeks(1);
          String nextTimeToRemindStr = getFormatedTimeStrForReminder(convertLocalDateToDate(nextTimeToRemind));
          return nextTimeToRemindStr;
        } else {
          return getFormatedTimeStrForReminder(date);
        }
      } else if (dayOfWeek < remindDay) {
        LocalDate localDate = convertDateToLocalDate(date);
        LocalDate nextTimeToRemind = localDate.plusDays(remindDay - dayOfWeek);
        return getFormatedTimeStrForReminder(convertLocalDateToDate(nextTimeToRemind));
      } else {
        LocalDate localDate = convertDateToLocalDate(date);
        LocalDate nextTimeToRemind = localDate.plusDays(7 - dayOfWeek + remindDay);
        return getFormatedTimeStrForReminder(convertLocalDateToDate(nextTimeToRemind));
      }
    }
    if (periodType.equals(PeriodType.HALF_MONTH)) {
      if (isTimeTohalfMonthReminder(date, remindDay)) {
        if (date.getTime() > eightOclockTimeStamp) {
          LocalDate localDate = convertDateToLocalDate(date);
          LocalDate nextTimeToRemind = localDate.plusDays(14);
          return getFormatedTimeStrForReminder(convertLocalDateToDate(nextTimeToRemind));
        } else {
          return getFormatedTimeStrForReminder(date);
        }
      } else {
        LocalDate nextRemindDay = getRemindDayOfHalfMonth(convertDateToLocalDate(date), remindDay);
        return getFormatedTimeStrForReminder(convertLocalDateToDate(nextRemindDay));
      }
    }
    return null;
  }

  public static boolean isTimeToEveryMonthReminder(Date date, Integer remindDay) {
    LocalDate localDate = convertDateToLocalDate(date);
    LocalDate first = getFirstRemindDayOfMonth(localDate, remindDay);
    return localDate.isEqual(first);
  }

  public static LocalDate convertDateToLocalDate(Date date) {
    Instant instant = date.toInstant();
    ZoneId zone = ZoneId.of("Asia/Shanghai");
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
    LocalDate localDate = localDateTime.toLocalDate();
    return localDate;
  }

  private static LocalDate getFirstRemindDayOfMonth(LocalDate localDate, Integer remindDay) {
    DayOfWeek dayOfWeek = convertDayOfWeekFromInteger(remindDay);
    LocalDate first = localDate.with(TemporalAdjusters.firstInMonth(dayOfWeek));
    return first;
  }

  private static DayOfWeek convertDayOfWeekFromInteger(Integer dayOfWeek) {
    switch (dayOfWeek) {
      case 1:
        return DayOfWeek.MONDAY;
      case 2:
        return DayOfWeek.TUESDAY;
      case 3:
        return DayOfWeek.WEDNESDAY;
      case 4:
        return DayOfWeek.THURSDAY;
      case 5:
        return DayOfWeek.FRIDAY;
      case 6:
        return DayOfWeek.SATURDAY;
      case 7:
        return DayOfWeek.SUNDAY;
    }
    return null;
  }

  private static String getFormatedTimeStrForReminder(Date date) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
    sdf.setTimeZone(TimeZone.getTimeZone(TimeUtils.BEIJING));
//        String nextTimeToRemindStr = sdf.format(convertLocalDateToDate(nextTimeToRemind));
    return sdf.format(date);
  }

  public static String getFormatedTimeStrFromLocalDate(LocalDate localDate) {
    Date date = convertLocalDateToDate(localDate);
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    sdf.setTimeZone(TimeZone.getTimeZone(TimeUtils.BEIJING));
//        String nextTimeToRemindStr = sdf.format(convertLocalDateToDate(nextTimeToRemind));
    return sdf.format(date);
  }
  public static Date convertLocalDateToDate(LocalDate localDate) {
    ZoneId zone = ZoneId.of("Asia/Shanghai");
    Date date = Date.from(localDate.atStartOfDay(zone).toInstant());
    return date;
  }

  private static boolean isBeforeTimeToEveryMonthReminder(Date date, Integer remindDay) {
    LocalDate localDate = convertDateToLocalDate(date);
    LocalDate first = getFirstRemindDayOfMonth(localDate, remindDay);
    return localDate.isBefore(first);
  }


  public static boolean isTimeTohalfMonthReminder(Date date,Integer remindDay) {
        /*    java.util.Date date = new java.util.Date();
    Instant instant = date.toInstant();
    ZoneId zone = ZoneId.systemDefault();
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
    LocalDate localDate = localDateTime.toLocalDate();*/
    Instant instant = date.toInstant();
    ZoneId zone = ZoneId.of("Asia/Shanghai");
    LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
    LocalDate localDate = localDateTime.toLocalDate();
    DayOfWeek dayOfWeek = convertDayOfWeekFromInteger(remindDay);
    LocalDate first = localDate.with(TemporalAdjusters.firstInMonth(dayOfWeek));
    LocalDate third = first.plusWeeks(2);
    return localDate.isEqual(first) || localDate.isEqual(third);
  }

  private static LocalDate getRemindDayOfHalfMonth(LocalDate localDate, Integer remindDay) {
    //如果早于或者等于本月第一个提醒日，返回第一个提醒日
    LocalDate first = getFirstRemindDayOfMonth(localDate, remindDay);
    LocalDate third = first.plusWeeks(2);
    LocalDate nextMonthFirst = getFirstRemindDayOfMonth(first.plusMonths(1), remindDay);
    if (localDate.isBefore(first)
            || localDate.isEqual(first)) {
      return first;
    } else if (localDate.isBefore(third)
            || localDate.isEqual(third)) {
      return third;
    } else {
      return nextMonthFirst;
    }
  }

  public static LocalDate getFirstDayOfCurrentQuarterByLocalDate(LocalDate localDate) {
    LocalDate firstDayOfFirstQuarter = localDate.with(TemporalAdjusters.firstDayOfYear());
    LocalDate lastDayOfFirstQuarter = firstDayOfFirstQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    LocalDate firstDayOfSecondQuarter = firstDayOfFirstQuarter.plusMonths(3);
    LocalDate lastDayOfSecondQuarter = firstDayOfSecondQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    LocalDate firstDayOfThirdQuarter = firstDayOfSecondQuarter.plusMonths(3);
    LocalDate lastDayOfThirdQuarter = firstDayOfThirdQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    LocalDate firstDayOfFourthQuarter = firstDayOfThirdQuarter.plusMonths(3);
    LocalDate lastDayOfFourthQuarter = firstDayOfFourthQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());

    if (isBetweenTwoDays(localDate, firstDayOfFirstQuarter, lastDayOfFirstQuarter)) {
      return firstDayOfFirstQuarter;
    } else if (isBetweenTwoDays(localDate, firstDayOfSecondQuarter, lastDayOfSecondQuarter)) {
      return firstDayOfSecondQuarter;
    } else if (isBetweenTwoDays(localDate, firstDayOfThirdQuarter, lastDayOfThirdQuarter)) {
      return firstDayOfThirdQuarter;
    } else if (isBetweenTwoDays(localDate, firstDayOfFourthQuarter, lastDayOfFourthQuarter)) {
      return firstDayOfFourthQuarter;
    }
    return null;
  }

  public static LocalDate getLastDayOfCurrentQuarterByLocalDate(LocalDate localDate) {
    LocalDate firstDayOfFirstQuarter = localDate.with(TemporalAdjusters.firstDayOfYear());
    LocalDate lastDayOfFirstQuarter = firstDayOfFirstQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    LocalDate firstDayOfSecondQuarter = firstDayOfFirstQuarter.plusMonths(3);
    LocalDate lastDayOfSecondQuarter = firstDayOfSecondQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    LocalDate firstDayOfThirdQuarter = firstDayOfSecondQuarter.plusMonths(3);
    LocalDate lastDayOfThirdQuarter = firstDayOfThirdQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    LocalDate firstDayOfFourthQuarter = firstDayOfThirdQuarter.plusMonths(3);
    LocalDate lastDayOfFourthQuarter = firstDayOfFourthQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());

    if (isBetweenTwoDays(localDate, firstDayOfFirstQuarter, lastDayOfFirstQuarter)) {
      return lastDayOfFirstQuarter;
    } else if (isBetweenTwoDays(localDate, firstDayOfSecondQuarter, lastDayOfSecondQuarter)) {
      return lastDayOfSecondQuarter;
    } else if (isBetweenTwoDays(localDate, firstDayOfThirdQuarter, lastDayOfThirdQuarter)) {
      return lastDayOfThirdQuarter;
    } else if (isBetweenTwoDays(localDate, firstDayOfFourthQuarter, lastDayOfFourthQuarter)) {
      return lastDayOfFourthQuarter;
    }
    return null;
  }


  public static Integer getCurrentQuarterByLocalDate(LocalDate localDate) {
    LocalDate firstDayOfFirstQuarter = localDate.with(TemporalAdjusters.firstDayOfYear());
    LocalDate lastDayOfFirstQuarter = firstDayOfFirstQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    LocalDate firstDayOfSecondQuarter = firstDayOfFirstQuarter.plusMonths(3);
    LocalDate lastDayOfSecondQuarter = firstDayOfSecondQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    LocalDate firstDayOfThirdQuarter = firstDayOfSecondQuarter.plusMonths(3);
    LocalDate lastDayOfThirdQuarter = firstDayOfThirdQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
    LocalDate firstDayOfFourthQuarter = firstDayOfThirdQuarter.plusMonths(3);
    LocalDate lastDayOfFourthQuarter = firstDayOfFourthQuarter.plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());

    if (isBetweenTwoDays(localDate, firstDayOfFirstQuarter, lastDayOfFirstQuarter)) {
      return 1;
    } else if (isBetweenTwoDays(localDate, firstDayOfSecondQuarter, lastDayOfSecondQuarter)) {
      return 2;
    } else if (isBetweenTwoDays(localDate, firstDayOfThirdQuarter, lastDayOfThirdQuarter)) {
      return 3;
    } else if (isBetweenTwoDays(localDate, firstDayOfFourthQuarter, lastDayOfFourthQuarter)) {
      return 4;
    }
    return -1;
  }

  public static LocalDate getFirstDayOfCurMonth(LocalDate localDate) {
    LocalDate firstDayOfCurMonth = localDate.with(TemporalAdjusters.firstDayOfMonth());
    return firstDayOfCurMonth;
  }

  public static LocalDate getLastDayOfCurMonth(LocalDate localDate) {
    LocalDate lastDayOfCurMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
    return lastDayOfCurMonth;
  }

}
