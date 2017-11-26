// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-10
 */
@Data
@NoArgsConstructor
public class UserProfile {

  private Long orgId;

  private Long userId;

  private Long onboardingTemplateId;

  private Long profileTemplateId;

  /**
   * Including fields in:
   *  1) CoreUserProfile
   *  2) BasicUserProfile
   *  3) MetaUserProfile
   *
   * key: field's referenceName
   * value: field's value of this item
   */
  private List<ProfileField> profileFields;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
