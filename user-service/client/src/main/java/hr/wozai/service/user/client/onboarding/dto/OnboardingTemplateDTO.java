// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.onboarding.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-09
 */
@ThriftStruct
public final class OnboardingTemplateDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long onboardingTemplateId;

  private Long orgId;

  private String displayName;

  private String prologue;

  private String epilogue;

  private Long profileTemplateId;

  private List<OnboardingDocumentDTO> onboardingDocumentDTOs;

  private Integer isPreset;

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
  public Long getOnboardingTemplateId() {
    return onboardingTemplateId;
  }

  @ThriftField
  public void setOnboardingTemplateId(Long onboardingTemplateId) {
    this.onboardingTemplateId = onboardingTemplateId;
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
  public String getDisplayName() {
    return displayName;
  }

  @ThriftField
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @ThriftField(5)
  public String getPrologue() {
    return prologue;
  }

  @ThriftField
  public void setPrologue(String prologue) {
    this.prologue = prologue;
  }

  @ThriftField(6)
  public String getEpilogue() {
    return epilogue;
  }

  @ThriftField
  public void setEpilogue(String epilogue) {
    this.epilogue = epilogue;
  }

  @ThriftField(7)
  public Long getProfileTemplateId() {
    return profileTemplateId;
  }

  @ThriftField
  public void setProfileTemplateId(Long profileTemplateId) {
    this.profileTemplateId = profileTemplateId;
  }

  @ThriftField(8)
  public Integer getIsPreset() {
    return isPreset;
  }

  @ThriftField
  public void setIsPreset(Integer isPreset) {
    this.isPreset = isPreset;
  }

  @ThriftField(9)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
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
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

  @ThriftField(14)
  public List<OnboardingDocumentDTO> getOnboardingDocumentDTOs() {
    return onboardingDocumentDTOs;
  }

  @ThriftField
  public void setOnboardingDocumentDTOs(
      List<OnboardingDocumentDTO> onboardingDocumentDTOs) {
    this.onboardingDocumentDTOs = onboardingDocumentDTOs;
  }
}
