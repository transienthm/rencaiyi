// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.review;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import hr.wozai.service.servicecommons.utils.validator.StringLengthConstraint;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-23
 */
@Data
@NoArgsConstructor
public class ReviewTemplateVO implements Comparable<ReviewTemplateVO> {

  @JsonSerialize(using = EncodeSerializer.class)
  private Long templateId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long orgId;

  @StringLengthConstraint(lengthConstraint = 40)
  private String templateName;

  private Long publishedTime;

  private Long startTime;

  private Long endTime;

  private Long selfReviewDeadline;

  private Long peerReviewDeadline;

  private Long publicDeadline;

  private Integer isReviewerAnonymous;

  private Integer state; //1.draft 2.in progress 3.finish 4.canceled

  @StringLengthConstraint(lengthConstraint = 40)
  private List<String> questions;

  private List<String> encryptedTeamIds;

  private List<TeamVO> invitedTeamVOs;

  private Long revieweeNumber;

  private Long invitedNumber;

  private Long finishedNumber;

  private Long createdTime;

  @Override
  public int compareTo(ReviewTemplateVO another) {
    if (this.getCreatedTime() > another.getCreatedTime()) {
      return 1;
    }
    if (this.getCreatedTime() < another.getCreatedTime()) {
      return -1;
    }
    return 0;
  }
}
