// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-05
 */
public class BooleanUtils {

  public static boolean isValidBooleanValue(int val) {
    if (val == 0
        || val == 1) {
      return true;
    }
    return false;
  }

  public static boolean isValidBooleanString(String val) {
    if (StringUtils.isNullOrEmpty(val)) {
      return false;
    }
    try {
      int booleanVal = Integer.parseInt(val.trim());
      if (booleanVal != 0
          && booleanVal != 1) {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static void main(String[] args) {
    System.out.println(isValidBooleanString("1"));
    System.out.println(isValidBooleanString("2"));
  }

}
