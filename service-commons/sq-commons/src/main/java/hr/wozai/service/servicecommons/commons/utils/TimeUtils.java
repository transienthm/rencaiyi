// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters.*;
/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-07
 */
public class TimeUtils {
  public static final String BEIJING = "GMT+8";

  public static long getNowTimestmapInMillis() {
    return System.currentTimeMillis();
  }

  public static long getTimestampOfZeroOclockToday(String timeZone){
    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTimeInMillis();
  }

  public static long getTimestampOfTwentyFourOclockToday(){
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 24);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTimeInMillis();
  }

  public static long getTimestampOfZeroOclockTodayOfInputTimestamp(long timestamp, String timeZone) {
    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(timestamp);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal.getTimeInMillis();
  }

  /**
   * Only accept YYYY-MM-DD format
   *
   * @param dateStr
   * @return
   */
  public static long getTimestampOfZeroOclockTodayOfInputDateInBeijingTime(String dateStr) {

    long result = 0;

    try {
      DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      Date date = (Date) formatter.parse(dateStr);
      Calendar cal = Calendar.getInstance();
      cal.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
      cal.setTime(date);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.MILLISECOND, 0);
      result = cal.getTimeInMillis();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  /**
   * @param timestamp
   * @return
   */
  public static long getTimestampOfZeroOclockTodayOfInputTimestampInBeijingTime(long timestamp) {

    long result = 0;

    try {
      Date date = new Date(timestamp);
      Calendar cal = Calendar.getInstance();
      cal.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
      cal.setTime(date);
      cal.set(Calendar.HOUR_OF_DAY, 0);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.MILLISECOND, 0);
      result = cal.getTimeInMillis();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return result;
  }

  public static long getTimestampOfEightOclockTodayOfInputTimestampInBeijingTime(long timestamp) {

    long result = 0;

    try {
      Date date = new Date(timestamp);
      Calendar cal = Calendar.getInstance();
      cal.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
      cal.setTime(date);
      cal.set(Calendar.HOUR_OF_DAY, 8);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MINUTE, 0);
      cal.set(Calendar.MILLISECOND, 0);
      result = cal.getTimeInMillis();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  public static String formatDateWithTimeZone(Long dateMS, String timeZone) {

    Date date = new Date(dateMS);
    //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
    return sdf.format(date);
  }

  /**
   * In format of yyyy-MM-dd
   *
   * @param timestamp
   * @return
   */
  public static String getDateStringOfBeijingTimezone(long timestamp) {
    Date date = new Date(timestamp);
    //注意format的格式要与日期String的格式相匹配
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    return sdf.format(date);
  }

  public static Integer getCurrentYearWithTimeZone(String timeZone) {
    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    Calendar calendar = Calendar.getInstance();
    return calendar.get(Calendar.YEAR);
  }

  public static Integer getCurrentMonthWithTimeZone(String timeZone) {
    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    Calendar calendar = Calendar.getInstance();
    return calendar.get(Calendar.MONTH) + 1;
  }

  /**
   * 获取某月的最后一天
   * @Title:getLastDayOfMonth
   * @Description:
   * @param:@param year
   * @param:@param month
   * @param:@return
   * @return:String
   * @throws
   */
  public static long getLastDayOfMonth(int year,int month, String timeZone)
  {
    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    Calendar cal = Calendar.getInstance();
    //设置年份
    cal.set(Calendar.YEAR,year);
    //设置月份
    cal.set(Calendar.MONTH, month-1);
    //获取某月最大天数
    int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    //设置日历中月份的最大天数
    cal.set(Calendar.DAY_OF_MONTH, lastDay + 1);
    //格式化日期
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String lastDayOfMonth = sdf.format(cal.getTime());
    return getTimestampOfZeroOclockTodayOfInputDateInBeijingTime(lastDayOfMonth);
  }

  /**
   * 获取某月的第一天
   * @Title:getFirstDayOfMonth
   * @Description:
   * @param:@param year
   * @param:@param month
   * @param:@return
   * @return:String
   * @throws
   */
  public static long getFirstDayOfMonth(int year,int month, String timeZone)
  {
    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    Calendar cal = Calendar.getInstance();
    //设置年份
    cal.set(Calendar.YEAR,year);
    //设置月份
    cal.set(Calendar.MONTH, month - 2);
    //获取某月最大天数
    int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
    //设置日历中月份的最大天数
    cal.set(Calendar.DAY_OF_MONTH, lastDay + 1);
    //格式化日期
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String lastDayOfMonth = sdf.format(cal.getTime());
    return getTimestampOfZeroOclockTodayOfInputDateInBeijingTime(lastDayOfMonth);
  }

  public static int getYearFromTimestamp(long timestamp, String timeZone){
    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(timestamp);
    return cal.get(Calendar.YEAR);
  }

  public static int getMonthFromTimestamp(long timestamp, String timeZone){
    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(timestamp);
    return cal.get(Calendar.MONTH) + 1;
  }

  public static int getDayOfWeekFromDate(Date date) {
    Calendar calendar = Calendar.getInstance();
    if (date != null) {
      calendar.setTime(date);
    }
    int w = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    if (w < 0) {
      w = 7;
    }
    return w;
  }
  /**
   * 获取当前时间戳是星期几
   * @param timestamp
   * @param timeZone
   * @return
   */
  public static int getWeekFromTimestamp(long timestamp, String timeZone) {
    TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    Calendar cal = Calendar.getInstance();
    cal.setTimeInMillis(timestamp);
    return cal.get(Calendar.DAY_OF_WEEK) - 1;
  }

  public static void main(String[] args) {
    /*System.out.println("ThisMorning=" + getTimestampOfZeroOclockToday());
    System.out.println("Tonight=" + getTimestampOfTwentyFourOclockToday());

    long now = TimeUtils.getNowTimestmapInMillis();
    System.out.println("getTimestampOfZeroOclockTodayOfInputTimestamp="
                       + getTimestampOfZeroOclockTodayOfInputTimestamp(now));
    String someDay = "2016-06-01";
    System.out.println("someDay=" + getTimestampOfZeroOclockTodayOfInputDateInBeijingTime(someDay));
    System.out.println(TimeUtils.getCurrentMonthWithTimeZone(TimeUtils.BEIJING));
    System.out.println(TimeUtils.getCurrentYearWithTimeZone(TimeUtils.BEIJING));*/
    long lastDay = getLastDayOfMonth(2016, 2, TimeUtils.BEIJING);
    long firstDay = getFirstDayOfMonth(2016, 2, TimeUtils.BEIJING);
    System.out.println("获取当前月的最后一天：" + lastDay);
    System.out.println("获取当前月的第一天: " + firstDay);

    System.out.println(getDateStringOfBeijingTimezone(TimeUtils.getNowTimestmapInMillis()));

    Date date = new Date();

    int i = getDayOfWeekFromDate(date);
    System.out.println(date);
    System.out.println("今天周" + i);

    long oneDay = 3600 * 24 * 1000;
    System.out.println(TimeUtils.getWeekFromTimestamp(System.currentTimeMillis(), TimeUtils.BEIJING));
    System.out.println(TimeUtils.getWeekFromTimestamp(System.currentTimeMillis() + oneDay, TimeUtils.BEIJING));
    System.out.println(TimeUtils.getWeekFromTimestamp(System.currentTimeMillis() + 2*oneDay, TimeUtils.BEIJING));
    System.out.println(TimeUtils.getWeekFromTimestamp(System.currentTimeMillis() + 3*oneDay, TimeUtils.BEIJING));
    System.out.println(TimeUtils.getWeekFromTimestamp(System.currentTimeMillis() + 4*oneDay, TimeUtils.BEIJING));
    System.out.println(TimeUtils.getWeekFromTimestamp(System.currentTimeMillis() + 5*oneDay, TimeUtils.BEIJING));
    System.out.println(TimeUtils.getWeekFromTimestamp(System.currentTimeMillis() + 6*oneDay, TimeUtils.BEIJING));
  }

}
