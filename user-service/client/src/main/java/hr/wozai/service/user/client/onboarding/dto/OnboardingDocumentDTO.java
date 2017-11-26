// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.onboarding.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-09
 */
@ThriftStruct
public final class OnboardingDocumentDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long onboardingDocumentId;

  private Long orgId;

  private Long onboardingTemplateId;

  private Long documentId;

  private Integer logicalIndex;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

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
  public Long getOnboardingDocumentId() {
    return onboardingDocumentId;
  }

  @ThriftField
  public void setOnboardingDocumentId(Long onboardingDocumentId) {
    this.onboardingDocumentId = onboardingDocumentId;
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
  public Long getOnboardingTemplateId() {
    return onboardingTemplateId;
  }

  @ThriftField
  public void setOnboardingTemplateId(Long onboardingTemplateId) {
    this.onboardingTemplateId = onboardingTemplateId;
  }

  @ThriftField(5)
  public Long getDocumentId() {
    return documentId;
  }

  @ThriftField
  public void setDocumentId(Long documentId) {
    this.documentId = documentId;
  }

  @ThriftField(6)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField(7)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(8)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(9)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(10)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

  @ThriftField(12)
  public Integer getLogicalIndex() {
    return logicalIndex;
  }

  @ThriftField
  public void setLogicalIndex(Integer logicalIndex) {
    this.logicalIndex = logicalIndex;
  }
}
