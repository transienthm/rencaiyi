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
 * @Created: 2016-04-21
 */
@ThriftStruct
public final class ReviewInvitationDetailDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long invitationId;

  private Long orgId;

  private ReviewTemplateDTO reviewTemplateDTO;

  private Long revieweeId;

  private List<ReviewProjectDTO> reviewProjectDTOs;

  private List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs;

  private Integer isInActive;

  private Integer isSubmittable;

  private Integer isManager;

  private Integer isSubmitted;

  private Integer isCanceled;

  private Integer isBackuped;

  private Long selfReviewDeadline;

  private Long peerReviewDeadline;

  private Long publicDeadline;

  private Integer score;

  private List<ReviewPastInvitationDTO> pastInvitationDTOs;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getInvitationId() {
    return invitationId;
  }

  @ThriftField
  public void setInvitationId(Long invitationId) {
    this.invitationId = invitationId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField(4)
  public ReviewTemplateDTO getReviewTemplateDTO() {
    return reviewTemplateDTO;
  }

  @ThriftField
  public void setReviewTemplateDTO(ReviewTemplateDTO reviewTemplateDTO) {
    this.reviewTemplateDTO = reviewTemplateDTO;
  }

  @ThriftField(5)
  public Long getRevieweeId() {
    return revieweeId;
  }

  @ThriftField
  public void setRevieweeId(Long revieweeId) {
    this.revieweeId = revieweeId;
  }

  @ThriftField(6)
  public List<ReviewProjectDTO> getReviewProjectDTOs() {
    return reviewProjectDTOs;
  }

  @ThriftField
  public void setReviewProjectDTOs(List<ReviewProjectDTO> reviewProjectDTOs) {
    this.reviewProjectDTOs = reviewProjectDTOs;
  }

  @ThriftField(7)
  public List<ReviewQuestionDetailDTO> getReviewQuestionDetailDTOs() {
    return reviewQuestionDetailDTOs;
  }

  @ThriftField
  public void setReviewQuestionDetailDTOs(List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs) {
    this.reviewQuestionDetailDTOs = reviewQuestionDetailDTOs;
  }

  @ThriftField(8)
  public Integer getIsSubmittable() {
    return isSubmittable;
  }

  @ThriftField
  public void setIsSubmittable(Integer isSubmittable) {
    this.isSubmittable = isSubmittable;
  }

  @ThriftField(9)
  public Integer getIsManager() {
    return isManager;
  }

  @ThriftField
  public void setIsManager(Integer isManager) {
    this.isManager = isManager;
  }

  @ThriftField(10)
  public Integer getScore() {
    return score;
  }

  @ThriftField
  public void setScore(Integer score) {
    this.score = score;
  }

  @ThriftField(11)
  public List<ReviewPastInvitationDTO> getPastInvitationDTOs() {
    return pastInvitationDTOs;
  }

  @ThriftField
  public void setPastInvitationDTOs(List<ReviewPastInvitationDTO> pastInvitationDTOs) {
    this.pastInvitationDTOs = pastInvitationDTOs;
  }

  @ThriftField(12)
  public Integer getIsSubmitted() {
    return isSubmitted;
  }

  @ThriftField
  public void setIsSubmitted(Integer isSubmitted) {
    this.isSubmitted = isSubmitted;
  }

  @ThriftField(13)
  public Integer getIsCanceled() {
    return isCanceled;
  }

  @ThriftField
  public void setIsCanceled(Integer isCanceled) {
    this.isCanceled = isCanceled;
  }

  @ThriftField(14)
  public Integer getIsBackuped() {
    return isBackuped;
  }

  @ThriftField
  public void setIsBackuped(Integer isBackuped) {
    this.isBackuped = isBackuped;
  }

  @ThriftField(15)
  public Long getPeerReviewDeadline() {
    return peerReviewDeadline;
  }

  @ThriftField
  public void setPeerReviewDeadline(Long peerReviewDeadline) {
    this.peerReviewDeadline = peerReviewDeadline;
  }

  @ThriftField(16)
  public Long getPublicDeadline() {
    return publicDeadline;
  }

  @ThriftField
  public void setPublicDeadline(Long publicDeadline) {
    this.publicDeadline = publicDeadline;
  }

  @ThriftField(17)
  public Integer getIsInActive() {
    return isInActive;
  }

  @ThriftField
  public void setIsInActive(Integer isInActive) {
    this.isInActive = isInActive;
  }

  @ThriftField(18)
  public Long getSelfReviewDeadline() {
    return selfReviewDeadline;
  }

  @ThriftField
  public void setSelfReviewDeadline(Long selfReviewDeadline) {
    this.selfReviewDeadline = selfReviewDeadline;
  }
}

