// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.commons.utils;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-11
 */
public class EmailUtils {

  public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                              + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

  private static EmailValidator emailValidator = EmailValidator.getInstance();

  private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);

  public static boolean isValidEmailAddressByRegex(String emailAddress) {
    if (StringUtils.isNullOrEmpty(emailAddress)) {
      return false;
    }
    Matcher matcher = pattern.matcher(emailAddress);
    return matcher.matches();
  }

  public static boolean isValidEmailAddressByApache(String emailAddress) {

    return emailValidator.isValid(emailAddress);
  }
}
