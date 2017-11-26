// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-04
 */
@Data
@NoArgsConstructor
public class CoreUserProfile {

  private Long coreUserProfileId;

  private Long orgId;

  private Long userId;

  private Long profileTemplateId;

  private Long onboardingTemplateId;

  private String fullName;

  private String emailAddress;

  private String mobilePhone;

  private String personalEmail;

  private String employeeId;

  private Long jobTitle;

  private Long jobLevel;

  private String nickName;

  private Integer gender;

  private String avatarUrl;

  private Long dateOfBirth;

  private String signatureLine;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
