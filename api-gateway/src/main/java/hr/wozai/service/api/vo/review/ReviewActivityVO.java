// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.review;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-29
 */
@Data
@NoArgsConstructor
public class ReviewActivityVO {

  @JsonSerialize(using = EncodeSerializer.class)
  private Long activityId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long orgId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long templateId;

  private String templateName;

  private Long startTime;

  private Long endTime;

  private Long selfReviewDeadline;

  private Long peerReviewDeadline;

  private Long publicDeadline;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long revieweeId;

  private CoreUserProfileVO revieweeUserProfile;

  private ReviewInvitationVO managerReviewInvitationVO;

  private List<ReviewInvitationVO> peerReviewInvitationVOs;

  private Integer isReaded;

  private Integer isSubmitted;

  private Integer isCanceled;

  private Integer activityDisplayType;

}
