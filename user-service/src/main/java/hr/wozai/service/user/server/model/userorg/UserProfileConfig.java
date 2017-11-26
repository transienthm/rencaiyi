// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-05
 */
@Data
@NoArgsConstructor
public class UserProfileConfig {

  private Long userProfileConfigId;

  private Long orgId;

  private Integer fieldCode;

  private String referenceName;

  private String dbColumnName;

  private Integer dataType;

  private String typeSpec;

  private String promptInfo;

  private Integer isSystemRequired;

  private Integer isOnboardingStaffEditable;

  private Integer isActiveStaffEditable;

  private Integer isEnabled;

  private Integer isEnabledEditable;

  private Integer isMandatory;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
