// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.vo.review;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-24
 */
@Data
@NoArgsConstructor
public class ReviewDetailVO {

  @JsonSerialize(using = EncodeSerializer.class)
  private Long activityId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long invitationId;

  @JsonSerialize(using = EncodeSerializer.class)
  private Long orgId;

  private ReviewTemplateVO reviewTemplate;

  private CoreUserProfileVO revieweeUserProfile;

  private List<ReviewProjectSimpleVO> projects;

  private List<ReviewQuestionVO> questions;

  private Integer isProjectAddable;

  private Integer isSubmittable;

  private Integer isManager;

  private Integer score;

  private List<ReviewPastInvitationVO> pastInvitations;

  // Only userd for reviewee
  private Integer activityDisplayType;

  // Only used for reviewer
  private Integer invitationDisplayType;

}
