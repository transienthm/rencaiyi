// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.model.conversation;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-25
 */
@Data
@NoArgsConstructor
public class ConvrSchedule {

  private Long convrScheduleId;

  private Long orgId;

  private Long sourceUserId;

  private Long targetUserId;

  private Integer periodType;

  private Integer remindDay;

  private Integer isActive;

  private Integer convrCount;

  private Long lastConvrDate;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
