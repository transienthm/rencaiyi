// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.TeamDTO;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-09
 */
@ThriftStruct
public final class ReviewTemplateContainUserProfileDTO extends BaseThriftObject implements Comparable<ReviewTemplateContainUserProfileDTO> {

  private ServiceStatusDTO serviceStatusDTO;

  private Long templateId;

  private Long orgId;

  private String templateName;

  private Long publishedTime;

  private Long startTime;

  private Long endTime;

  private Long selfReviewDeadline;

  private Long peerReviewDeadline;

  private Long publicDeadline;

  private Integer isReviewerAnonymous;

  private Integer state; //1.draft 2.in progress 3.finish 4.canceled

  private List<String> questions;

  private List<Long> teamIds;

  private Long revieweeNumber;

  private Long invitedNumber;

  private Long finishedNumber;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private String extend;

  private Integer isDeleted;

  List<Long> revieweeIds;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getTemplateId() {
    return templateId;
  }

  @ThriftField
  public void setTemplateId(Long templateId) {
    this.templateId = templateId;
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
  public String getTemplateName() {
    return templateName;
  }

  @ThriftField
  public void setTemplateName(String templateName) {
    this.templateName = templateName;
  }

  @ThriftField(5)
  public Long getStartTime() {
    return startTime;
  }

  @ThriftField
  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  @ThriftField(6)
  public Long getEndTime() {
    return endTime;
  }

  @ThriftField
  public void setEndTime(Long endTime) {
    this.endTime = endTime;
  }

  @ThriftField(7)
  public Long getSelfReviewDeadline() {
    return selfReviewDeadline;
  }

  @ThriftField
  public void setSelfReviewDeadline(Long selfReviewDeadline) {
    this.selfReviewDeadline = selfReviewDeadline;
  }

  @ThriftField(8)
  public Long getPeerReviewDeadline() {
    return peerReviewDeadline;
  }

  @ThriftField
  public void setPeerReviewDeadline(Long peerReviewDeadline) {
    this.peerReviewDeadline = peerReviewDeadline;
  }

  @ThriftField(9)
  public Long getPublicDeadline() {
    return publicDeadline;
  }

  @ThriftField
  public void setPublicDeadline(Long publicDeadline) {
    this.publicDeadline = publicDeadline;
  }

  @ThriftField(10)
  public Integer getState() {
    return state;
  }

  @ThriftField
  public void setState(Integer state) {
    this.state = state;
  }

  @ThriftField(11)
  public List<String> getQuestions() {
    return questions;
  }

  @ThriftField
  public void setQuestions(List<String> questions) {
    this.questions = questions;
  }

  @ThriftField(12)
  public Long getRevieweeNumber() {
    return revieweeNumber;
  }

  @ThriftField
  public void setRevieweeNumber(Long revieweeNumber) {
    this.revieweeNumber = revieweeNumber;
  }

  @ThriftField(13)
  public Long getInvitedNumber() {
    return invitedNumber;
  }

  @ThriftField
  public void setInvitedNumber(Long invitedNumber) {
    this.invitedNumber = invitedNumber;
  }

  @ThriftField(14)
  public Long getFinishedNumber() {
    return finishedNumber;
  }

  @ThriftField
  public void setFinishedNumber(Long finishedNumber) {
    this.finishedNumber = finishedNumber;
  }

  @ThriftField(15)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(16)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(17)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(18)
  public String getExtend() {
    return extend;
  }

  @ThriftField
  public void setExtend(String extend) {
    this.extend = extend;
  }

  @ThriftField(19)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

  @ThriftField(20)
  public Long getPublishedTime() {
    return publishedTime;
  }

  @ThriftField
  public void setPublishedTime(Long publishedTime) {
    this.publishedTime = publishedTime;
  }

  @ThriftField(21)
  public List<Long> getTeamIds() {
    return this.teamIds;
  }

  @ThriftField
  public void setTeamIds(List<Long> teamIds) {
    this.teamIds = teamIds;
  }

  @ThriftField(22)
  public Integer getIsReviewerAnonymous() {
    return isReviewerAnonymous;
  }

  @ThriftField
  public void setIsReviewerAnonymous(Integer isReviewerAnonymous) {
    this.isReviewerAnonymous = isReviewerAnonymous;
  }

  @ThriftField(23)
  public List<Long> getRevieweeIds() {
    return this.revieweeIds;
  }

  @ThriftField
  public void setRevieweeIds(List<Long> revieweeIds) {
    this.revieweeIds = revieweeIds;
  }

  @Override
  public int compareTo(ReviewTemplateContainUserProfileDTO another) {
    if (this.getPublishedTime() == null) {
      return 1;
    }
    if (another.getPublishedTime() == null) {
      return -1;
    }
    if (this.getPublishedTime() > another.getPublishedTime()) {
      return 1;
    }
    if (this.getPublishedTime() < another.getPublishedTime()) {
      return -1;
    }
    return 0;
  }
}
