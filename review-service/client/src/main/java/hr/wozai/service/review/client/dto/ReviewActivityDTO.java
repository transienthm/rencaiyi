// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-18
 */
@ThriftStruct
public final class ReviewActivityDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long activityId;

  private Long orgId;

  private Long templateId;

  private Long revieweeId;

  private Integer isReaded;

  private Integer isSubmitted;

  private Integer isCanceled;

  private Integer isBackuped;

  private Long selfReviewDeadline;

  private Long peerReviewDeadline;

  private Long publicDeadline;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private String extend;

  private Integer isDeleted;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getActivityId() {
    return activityId;
  }

  @ThriftField
  public void setActivityId(Long activityId) {
    this.activityId = activityId;
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
  public Long getTemplateId() {
    return templateId;
  }

  @ThriftField
  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
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
  public Integer getIsReaded() {
    return isReaded;
  }

  @ThriftField
  public void setIsReaded(Integer isReaded) {
    this.isReaded = isReaded;
  }

  @ThriftField(7)
  public Integer getIsSubmitted() {
    return isSubmitted;
  }

  @ThriftField
  public void setIsSubmitted(Integer isSubmitted) {
    this.isSubmitted = isSubmitted;
  }

  @ThriftField(8)
  public Integer getIsCanceled() {
    return isCanceled;
  }

  @ThriftField
  public void setIsCanceled(Integer isCanceled) {
    this.isCanceled = isCanceled;
  }

  @ThriftField(9)
  public Integer getIsBackuped() {
    return isBackuped;
  }

  @ThriftField
  public void setIsBackuped(Integer isBackuped) {
    this.isBackuped = isBackuped;
  }

  @ThriftField(10)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(11)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(12)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(13)
  public String getExtend() {
    return extend;
  }

  @ThriftField
  public void setExtend(String extend) {
    this.extend = extend;
  }

  @ThriftField(14)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

  @ThriftField(15)
  public Long getSelfReviewDeadline() {
    return selfReviewDeadline;
  }

  @ThriftField
  public void setSelfReviewDeadline(Long selfReviewDeadline) {
    this.selfReviewDeadline = selfReviewDeadline;
  }

  @ThriftField(16)
  public Long getPeerReviewDeadline() {
    return peerReviewDeadline;
  }

  @ThriftField
  public void setPeerReviewDeadline(Long peerReviewDeadline) {
    this.peerReviewDeadline = peerReviewDeadline;
  }

  @ThriftField(17)
  public Long getPublicDeadline() {
    return publicDeadline;
  }

  @ThriftField
  public void setPublicDeadline(Long publicDeadline) {
    this.publicDeadline = publicDeadline;
  }
}
