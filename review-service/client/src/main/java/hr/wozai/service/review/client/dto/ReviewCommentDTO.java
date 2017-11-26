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
 * @Created: 2016-03-09
 */
@ThriftStruct
public final class ReviewCommentDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long commentId;

  private Long orgId;

  private Long templateId;

  private Long revieweeId;

  private Long reviewerId;

  private String content;

  private Integer itemType;

  private Long itemId;

  private Long updatedTime;

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
  public Long getCommentId() {
    return commentId;
  }

  @ThriftField
  public void setCommentId(Long commentId) {
    this.commentId = commentId;
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
  public Long getReviewerId() {
    return reviewerId;
  }

  @ThriftField
  public void setReviewerId(Long reviewerId) {
    this.reviewerId = reviewerId;
  }

  @ThriftField(7)
  public String getContent() {
    return content;
  }

  @ThriftField
  public void setContent(String content) {
    this.content = content;
  }

  @ThriftField(8)
  public Integer getItemType() {
    return itemType;
  }

  @ThriftField
  public void setItemType(Integer itemType) {
    this.itemType = itemType;
  }

  @ThriftField(9)
  public Long getItemId() {
    return itemId;
  }

  @ThriftField
  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  @ThriftField(10)
  public Long getUpdatedTime() {
    return updatedTime;
  }

  @ThriftField
  public void setUpdatedTime(Long updatedTime) {
    this.updatedTime = updatedTime;
  }

  @ThriftField(11)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(12)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(13)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(14)
  public String getExtend() {
    return extend;
  }

  @ThriftField
  public void setExtend(String extend) {
    this.extend = extend;
  }

  @ThriftField(15)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

}
