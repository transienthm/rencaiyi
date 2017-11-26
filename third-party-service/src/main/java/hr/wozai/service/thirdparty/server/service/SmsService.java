// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.service;

import com.facebook.swift.service.ThriftMethod;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-07
 */
public interface SmsService {
  /**
   * Send a verification code to mobile
   *
   * @param mobilePhoneNumber
   * @return
   */
  boolean sendSmsMessage(String mobilePhoneNumber);

  /**
   * Verify user input against sent message
   *
   * @param mobilePhoneNumber
   * @param verificationCode
   * @return
   */
  boolean verifySmsMessage(String mobilePhoneNumber, String verificationCode);

  /**
   * Send message to mobile
   */

  boolean sendSmsMessage(String mobilePhoneNumber, String text,long tplId);

}
