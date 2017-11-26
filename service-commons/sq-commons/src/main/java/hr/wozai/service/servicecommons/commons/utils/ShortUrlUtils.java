// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

import java.util.Random;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-10-21
 */
public class ShortUrlUtils {

  private static final String VERIFY_CODES = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
  private static final int VERIFY_SIZE = 16;

  public static String generateVerifyCode() {


    int codesLen = VERIFY_CODES.length();
    Random rand = new Random(System.currentTimeMillis());
    StringBuilder verifyCode = new StringBuilder(VERIFY_SIZE);
    for (int i = 0; i < VERIFY_SIZE; i++) {
      verifyCode.append(VERIFY_CODES.charAt(rand.nextInt(codesLen - 1)));
    }
    return verifyCode.toString();

  }


}
