// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-11
 */
public class IntegerUtils {

  /**
   * Judge whether a equals b in term of value
   */
  public static boolean equals(Integer a, Integer b) {

    return a == null ? b == null : a.equals(b);
  }

  public static boolean isInteger(String s) {
    return isInteger(s,10);
  }

  public static boolean isInteger(String s, int radix) {
    if(s.isEmpty()) return false;
    for(int i = 0; i < s.length(); i++) {
      if(i == 0 && s.charAt(i) == '-') {
        if(s.length() == 1) return false;
        else continue;
      }
      if(Character.digit(s.charAt(i),radix) < 0) return false;
    }
    return true;
  }

}
