// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

import com.google.common.base.CaseFormat;

import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-11
 */
public class StringUtils {

  public static boolean isNullOrEmpty(String str) {
    return (null == str) || 0 == str.trim().length();
  }

  public static boolean isEqual(String s1, String s2) {
    return (null != s1) ? s1.equals(s2) : null == s2;
  }

  /**
   * String length between 1 and 100
   *
   * @param str
   * @return
   */
  public static boolean isValidVarchar100(String str) {
    return (null != str && str.length() > 0 && str.length() <= 100);
  }

  public static boolean isValidVarchar255(String str) {
    return (null != str && str.length() > 0 && str.length() <= 255);
  }

  public static boolean isValidStringOfMaxLength(String str, int maxLength) {
    return (null != str && str.length() > 0 && str.length() <= maxLength);
  }



  /**
   * Convert string of db field name to model field name
   * e.g. user_id --> userId
   *
   * @param str
   * @return
   */
  public static String mapUnderscoreToCamelCase(String str) {
    if (null == str) {
      return null;
    }
    return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
  }

  public static void trimStringInList(List<String> strs) {
    if (CollectionUtils.isEmpty(strs)) {
      return;
    }
    for (int i = 0; i < strs.size(); i++) {
      if (null != strs.get(i)) {
        strs.set(i, strs.get(i).trim());
      }
    }
  }

  public static void main(String[] args) {
    // 100 chinese chars
    String str = "一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十一二三四五六七八九十";
    System.out.println(StringUtils.isValidVarchar100(str));

    System.out.println(mapUnderscoreToCamelCase("user_profile_id"));
  }

}
