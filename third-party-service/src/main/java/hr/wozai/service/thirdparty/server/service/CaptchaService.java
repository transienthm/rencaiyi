// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.service;

import hr.wozai.service.thirdparty.client.dto.CaptchaDTO;

import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-01
 */
public interface CaptchaService {

  /**
   * Get a captcha image
   *
   * @return the "data" field is a Base64-encoded captcha png
   */
  Map<String, Object> getCaptcha();

  /**
   * Verify user input against captcha in database
   *
   * @param createTime
   * @param verificationCode
   * @return
   */
  boolean verifyCaptcha(long createTime, String verificationCode);
}
