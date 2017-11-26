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
public class SmsVerification {

  private Long smsVerificationId;

  private String mobilePhone;

  private String optionalIdentifier;
  
  private String verificationCode;

  private Integer entryPoint;

  private Long createTime;

  private Long expireTime;

  private Long verifyTime;

  /**
   * 1:sent; 2:verified
   */
  private Integer verificationStatus;

  private String extend;

}
