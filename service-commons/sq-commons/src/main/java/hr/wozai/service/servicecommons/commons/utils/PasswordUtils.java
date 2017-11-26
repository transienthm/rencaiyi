// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.regex.Pattern;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-11
 */
public class PasswordUtils {

  private static String validPasswordPattern = "(?=.*[0-9])(?=.*[A-Za-z]).{8,20}$";

  public static boolean isValidPassword(String password) {
    if (StringUtils.isNullOrEmpty(password)) {
      return false;
    }
    Pattern validPattern = Pattern.compile(validPasswordPattern);
    return validPattern.matcher(password).matches();

  }

  /**
   * Calculate the strength of password
   *
   * @param password
   * @return:
   *  1: weak
   *  2: medium
   *  3: strong
   */
  public static int calculatePasswordStrength(String password) {
    // TODO: HOWTO
    return 1;
  }

  public static void main(String[] args) {

    System.out.println(isValidPassword("sqian111"));

//    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(13);
//    String passwordPlainText = "imzhechen&*Shanqian";
//    String passwordSecret = passwordEncoder.encode(passwordPlainText);
//    int n = 10;
//    long entryTs = System.currentTimeMillis();
//    for(int i = 0; i < 10; i++) {
//      passwordEncoder.matches(passwordPlainText, passwordSecret);
//    }
//    System.out.println("avg:" + (System.currentTimeMillis() - entryTs + 0.0) / (n * 1000));
  }

}
