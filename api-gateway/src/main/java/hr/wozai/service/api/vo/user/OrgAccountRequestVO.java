// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-10
 */
@Data
@NoArgsConstructor
public class OrgAccountRequestVO {

  private String orgFullName;

  private String orgShortName;

  private String orgAvatarUrl;

  private Integer orgTimeZone;

//  private SuperAdminVO superAdminVO;

  private OnboardingRequestVO onboardingRequestVO;

  private String sqStaffMobilePhone;

  private String smsVerificationCode;
}
