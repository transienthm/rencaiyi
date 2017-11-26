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
public final class ReviewProjectDetailDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long projectId;

  private Long orgId;

  private Long templateId;

  private Long revieweeId;

  private String name;

  private String role;

  private Integer score;

  private String comment;

  private List<ReviewCommentDTO> submittedComments;

  private ReviewCommentDTO reviewerComment;

  private Integer isEditable;

  private Integer isDeletable;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getProjectId() {
    return projectId;
  }

  @ThriftField
  public void setProjectId(Long projectId) {
    this.projectId = projectId;
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
  public String getName() {
    return name;
  }

  @ThriftField
  public void setName(String name) {
    this.name = name;
  }

  @ThriftField(7)
  public String getRole() {
    return role;
  }

  @ThriftField
  public void setRole(String role) {
    this.role = role;
  }

  @ThriftField(8)
  public Integer getScore() {
    return score;
  }

  @ThriftField
  public void setScore(Integer score) {
    this.score = score;
  }

  @ThriftField(9)
  public String getComment() {
    return comment;
  }

  @ThriftField
  public void setComment(String comment) {
    this.comment = comment;
  }

  @ThriftField(10)
  public List<ReviewCommentDTO> getSubmittedComments() {
    return submittedComments;
  }

  @ThriftField
  public void setSubmittedComments(List<ReviewCommentDTO> submittedComments) {
    this.submittedComments = submittedComments;
  }

  @ThriftField(11)
  public ReviewCommentDTO getReviewerComment() {
    return reviewerComment;
  }

  @ThriftField
  public void setReviewerComment(ReviewCommentDTO reviewerComment) {
    this.reviewerComment = reviewerComment;
  }

  @ThriftField(12)
  public Integer getIsEditable() {
    return isEditable;
  }

  @ThriftField
  public void setIsEditable(Integer isEditable) {
    this.isEditable = isEditable;
  }

  @ThriftField(13)
  public Integer getIsDeletable() {
    return isDeletable;
  }

  @ThriftField
  public void setIsDeletable(Integer isDeletable) {
    this.isDeletable = isDeletable;
  }

}
