// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-08-25
 */
@Data
@NoArgsConstructor
public class CaptchaVerification {

  private Long createTime;

  private String verificationCode;

  private Long expireTime;

  private String extend;

}
