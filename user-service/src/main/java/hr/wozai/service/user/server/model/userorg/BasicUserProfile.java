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
public class BasicUserProfile {

  private Long basicUserProfileId;

  private Long orgId;

  private Long userId;

  private String selfIntro;

  private String resume;

  private String citizenId;

  private Integer degreeLevel;

  private String collegeName;

  private Integer maritalStatus;

  private String livingAddress;

  private String weixinAccount;

  private String weiboAccount;

  private String qqAccount;

  private String linkedinAccount;

  private String personalWebsite;

  private String payrollAccount;

  private String payrollBank;

  private String gongjijinAccount;

  private String shebaoAccount;

  private Integer residenceType;

  private String residenceAddress;

  private String officialPhoto;

  private String citizenIdCopy;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
