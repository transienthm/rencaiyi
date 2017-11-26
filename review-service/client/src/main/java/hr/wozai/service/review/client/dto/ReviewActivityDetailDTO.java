// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-19
 */
@ThriftStruct
public final class ReviewActivityDetailDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long activityId;

  private Long orgId;

  private ReviewTemplateDTO reviewTemplateDTO;

  private Long revieweeId;

  private List<ReviewProjectDTO> reviewProjectDTOs;

  private List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs;

  private Integer isProjectAddable;

  private Integer isSubmittable;

  private Integer score;

  @ThriftField(1)
  public Integer getScore() {
    return score;
  }

  @ThriftField
  public void setScore(Integer score) {
    this.score = score;
  }

  @ThriftField(2)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(3)
  public Long getActivityId() {
    return activityId;
  }

  @ThriftField
  public void setActivityId(Long activityId) {
    this.activityId = activityId;
  }

  @ThriftField(4)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField(5)
  public ReviewTemplateDTO getReviewTemplateDTO() {
    return reviewTemplateDTO;
  }

  @ThriftField
  public void setReviewTemplateDTO(ReviewTemplateDTO reviewTemplateDTO) {
    this.reviewTemplateDTO = reviewTemplateDTO;
  }

  @ThriftField(6)
  public Long getRevieweeId() {
    return revieweeId;
  }

  @ThriftField
  public void setRevieweeId(Long revieweeId) {
    this.revieweeId = revieweeId;
  }

  @ThriftField(7)
  public List<ReviewProjectDTO> getReviewProjectDTOs() {
    return reviewProjectDTOs;
  }

  @ThriftField
  public void setReviewProjectDTOs(List<ReviewProjectDTO> reviewProjectDTOs) {
    this.reviewProjectDTOs = reviewProjectDTOs;
  }

  @ThriftField(8)
  public List<ReviewQuestionDetailDTO> getReviewQuestionDetailDTOs() {
    return reviewQuestionDetailDTOs;
  }

  @ThriftField
  public void setReviewQuestionDetailDTOs(List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs) {
    this.reviewQuestionDetailDTOs = reviewQuestionDetailDTOs;
  }

  @ThriftField(9)
  public Integer getIsProjectAddable() {
    return isProjectAddable;
  }

  @ThriftField
  public void setIsProjectAddable(Integer isProjectAddable) {
    this.isProjectAddable = isProjectAddable;
  }

  @ThriftField(10)
  public Integer getIsSubmittable() {
    return isSubmittable;
  }

  @ThriftField
  public void setIsSubmittable(Integer isSubmittable) {
    this.isSubmittable = isSubmittable;
  }
}
