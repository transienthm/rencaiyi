// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.thirdparty.client.dto.CaptchaDTO;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;


/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-08-24
 */
@ThriftService
public interface CaptchaFacade {

  /**
   * Get a captcha image
   *
   * @return the "data" field is a Base64-encoded captcha png
   */
  @ThriftMethod
  CaptchaDTO getCaptcha();

  /**
   * Verify user input against captcha in database
   *
   * @param createTime
   * @param verificationCode
   * @return
   */
  @ThriftMethod
  BooleanDTO verifyCaptcha(long createTime, String verificationCode);
}
