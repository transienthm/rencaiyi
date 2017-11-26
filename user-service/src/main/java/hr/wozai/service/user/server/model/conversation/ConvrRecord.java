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
public class ConvrRecord {

  private Long convrRecordId;

  private Long orgId;

  private Long convrScheduleId;

  private Long convrDate;

  private String topicProgress;

  private String topicPlan;

  private String topicObstacle;

  private String topicHelp;

  private String topicCareer;

  private String topicElse;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

}
