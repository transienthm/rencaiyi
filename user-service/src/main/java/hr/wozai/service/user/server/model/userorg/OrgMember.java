// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrgMember {

  private Long orgMemberId;

  private Long userId;

  private Long orgId;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private String extend;

  private Integer isDeleted;

}
