// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Org {

  private Long orgId;

  private String fullName;

  private String shortName;

  private String description;

  private String avatarUrl;

  private Integer timeZone;

  private Integer isNaviOrg;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
