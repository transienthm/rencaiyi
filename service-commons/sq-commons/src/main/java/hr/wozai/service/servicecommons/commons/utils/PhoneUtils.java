// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-11
 */
public class PhoneUtils {

  // TODO: where(frontend/backend) & how(format) to validate phone number
  public static boolean isValidMobileNumber(String mobileNumber) {

    try {
      long numberLong = Long.parseLong(mobileNumber);
      // China mainland's mobile phone number starts with 1, and has 11 digits in total
      if (numberLong / 10000000000L == 1) {
        return true;
      } else {
        return false;
      }
    } catch (NumberFormatException e) {
      return false;
    } catch (NullPointerException e) {
      return false;
    }
  }
}
