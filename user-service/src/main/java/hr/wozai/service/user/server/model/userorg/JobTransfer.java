// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.model.userorg;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-28
 */
@Data
@NoArgsConstructor
public class JobTransfer {

  private Long jobTransferId;

  private Long orgId;

  private Long userId;

  private String transferType;

  private Long transferDate;

  private String description;

  private Long beforeTeamId;

  private Long beforeReporterId;

  private Long beforeJobTitleId;

  private Long beforeJobLevelId;

  private Long afterTeamId;

  private Long afterReporterId;

  private Long afterJobTitleId;

  private Long afterJobLevelId;

  private List<Long> toNotifyUserIds;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
