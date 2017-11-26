// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-29
 */
@Data
@NoArgsConstructor
public class ProfileTemplate {

  private Long profileTemplateId;

  private Long orgId;

  private String displayName;

  private Integer isPreset;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
