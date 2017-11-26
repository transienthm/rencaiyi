// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.review.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-09-18
 */
@Data
@NoArgsConstructor
public class ReviewInvitedTeam {

  private Long reviewInvitedTeamId;

  private Long orgId;

  private Long reviewTemplateId;

  private Long teamId;

  private Integer isDeleted;

}
