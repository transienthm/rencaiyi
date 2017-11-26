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
public final class ReviewQuestionDetailDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long questionId;

  private Long orgId;

  private Long templateId;

  private String name;

  private ReviewCommentDTO revieweeComment;

  private List<ReviewCommentDTO> submittedComment;

  private ReviewCommentDTO reviewerComment;

  private Integer isEditable;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getQuestionId() {
    return questionId;
  }

  @ThriftField
  public void setQuestionId(Long questionId) {
    this.questionId = questionId;
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
  public String getName() {
    return name;
  }

  @ThriftField
  public void setName(String name) {
    this.name = name;
  }

  @ThriftField(6)
  public ReviewCommentDTO getRevieweeComment() {
    return revieweeComment;
  }

  @ThriftField
  public void setRevieweeComment(ReviewCommentDTO revieweeComment) {
    this.revieweeComment = revieweeComment;
  }

  @ThriftField(7)
  public List<ReviewCommentDTO> getSubmittedComment() {
    return submittedComment;
  }

  @ThriftField
  public void setSubmittedComment(List<ReviewCommentDTO> submittedComment) {
    this.submittedComment = submittedComment;
  }

  @ThriftField(8)
  public ReviewCommentDTO getReviewerComment() {
    return reviewerComment;
  }

  @ThriftField
  public void setReviewerComment(ReviewCommentDTO reviewerComment) {
    this.reviewerComment = reviewerComment;
  }

  @ThriftField(9)
  public Integer getIsEditable() {
    return isEditable;
  }

  @ThriftField
  public void setIsEditable(Integer isEditable) {
    this.isEditable = isEditable;
  }

}
