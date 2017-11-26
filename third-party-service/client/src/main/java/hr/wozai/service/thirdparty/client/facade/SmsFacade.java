// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.thirdparty.client.dto.TrustedMobilePhoneDTO;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-08-25
 */
@ThriftService
public interface SmsFacade {

  /**
   * Send a message to mobile
   *
   * @param mobilePhone
   * @return
   */
  @ThriftMethod
  BooleanDTO sendSmsMessage(String mobilePhone);

  /**
   * Verify user input against sent message
   *
   * @param mobilePhone
   * @param verificationCode
   * @return
   */
  @ThriftMethod
  BooleanDTO verifySmsMessage(String mobilePhone, String verificationCode);

  /**
   * send Sms Code to trusted mobile phone
   *
   * @param mobilePhone
   * @return
   */
  @ThriftMethod
  public BooleanDTO sendSmsCodeToTrustedMobilePhone(String mobilePhone);

  /**
   * verify code and trusted mobile phone
   *
   * @param mobilePhone
   * @param smsCode
   * @return
   */
  @ThriftMethod
  public BooleanDTO verifySmsCodeOfTrustedMobilePhone(String mobilePhone, String smsCode);
}

