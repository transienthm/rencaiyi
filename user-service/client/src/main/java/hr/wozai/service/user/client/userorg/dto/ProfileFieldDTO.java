// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-03
 */
@ThriftStruct
public final class ProfileFieldDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long profileFieldId;

  private Long orgId;

  private Long profileTemplateId;

  private Long containerId;

  private String displayName;

  private String referenceName;

  /**
   * The index of field in logical schema
   * Range [0, 199] (2016-02-19)
   */
  private Integer logicalIndex;

  /**
   * The index of field in Item
   * Range [0, 199], referring to val[0, 199] in Item table (2016-02-19)
   */
  private Integer physicalIndex;

  private Integer dataType;

  private String typeSpec;

  private String promptInfo;

  private String dataValue;

  private Integer isTypeSpecEditable;

  private Integer isSystemRequired;

  private Integer isOnboardingStaffEditable;

  private Integer isActiveStaffEditable;

  private Integer isPublicVisible;

  private Integer isPublicVisibleEditable;

  private Integer isEnabled;

  private Integer isEnabledEditable;

  private Integer isMandatory;

  private Integer isMandatoryEditable;

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
  public Long getProfileFieldId() {
    return profileFieldId;
  }

  @ThriftField
  public void setProfileFieldId(Long profileFieldId) {
    this.profileFieldId = profileFieldId;
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
  public Long getProfileTemplateId() {
    return profileTemplateId;
  }

  @ThriftField
  public void setProfileTemplateId(Long profileTemplateId) {
    this.profileTemplateId = profileTemplateId;
  }

  @ThriftField(5)
  public Long getContainerId() {
    return containerId;
  }

  @ThriftField
  public void setContainerId(Long containerId) {
    this.containerId = containerId;
  }

  @ThriftField(6)
  public String getDisplayName() {
    return displayName;
  }

  @ThriftField
  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  @ThriftField(7)
  public String getReferenceName() {
    return referenceName;
  }

  @ThriftField
  public void setReferenceName(String referenceName) {
    this.referenceName = referenceName;
  }

  @ThriftField(8)
  public Integer getLogicalIndex() {
    return logicalIndex;
  }

  @ThriftField
  public void setLogicalIndex(Integer logicalIndex) {
    this.logicalIndex = logicalIndex;
  }

  @ThriftField(9)
  public Integer getPhysicalIndex() {
    return physicalIndex;
  }

  @ThriftField
  public void setPhysicalIndex(Integer physicalIndex) {
    this.physicalIndex = physicalIndex;
  }

  @ThriftField(10)
  public Integer getDataType() {
    return dataType;
  }

  @ThriftField
  public void setDataType(Integer dataType) {
    this.dataType = dataType;
  }

  @ThriftField(11)
  public String getTypeSpec() {
    return typeSpec;
  }

  @ThriftField
  public void setTypeSpec(String typeSpec) {
    this.typeSpec = typeSpec;
  }

  @ThriftField(12)
  public String getDataValue() {
    return dataValue;
  }

  @ThriftField
  public void setDataValue(String dataValue) {
    this.dataValue = dataValue;
  }

  @ThriftField(13)
  public Integer getIsSystemRequired() {
    return isSystemRequired;
  }

  @ThriftField
  public void setIsSystemRequired(Integer isSystemRequired) {
    this.isSystemRequired = isSystemRequired;
  }

  @ThriftField(14)
  public Integer getIsOnboardingStaffEditable() {
    return isOnboardingStaffEditable;
  }

  @ThriftField
  public void setIsOnboardingStaffEditable(Integer isOnboardingStaffEditable) {
    this.isOnboardingStaffEditable = isOnboardingStaffEditable;
  }

  @ThriftField(15)
  public Integer getIsPublicVisible() {
    return isPublicVisible;
  }

  @ThriftField
  public void setIsPublicVisible(Integer isPublicVisible) {
    this.isPublicVisible = isPublicVisible;
  }

  @ThriftField(16)
  public Integer getIsEnabled() {
    return isEnabled;
  }

  @ThriftField
  public void setIsEnabled(Integer isEnabled) {
    this.isEnabled = isEnabled;
  }

  @ThriftField(17)
  public Integer getIsEnabledEditable() {
    return isEnabledEditable;
  }

  @ThriftField
  public void setIsEnabledEditable(Integer isEnabledEditable) {
    this.isEnabledEditable = isEnabledEditable;
  }

  @ThriftField(18)
  public Integer getIsMandatory() {
    return isMandatory;
  }

  @ThriftField
  public void setIsMandatory(Integer isMandatory) {
    this.isMandatory = isMandatory;
  }

  @ThriftField(19)
  public Integer getIsMandatoryEditable() {
    return isMandatoryEditable;
  }

  @ThriftField
  public void setIsMandatoryEditable(Integer isMandatoryEditable) {
    this.isMandatoryEditable = isMandatoryEditable;
  }

  @ThriftField(20)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField(21)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(22)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(23)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(24)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

  @ThriftField(25)
  public Integer getIsTypeSpecEditable() {
    return isTypeSpecEditable;
  }

  @ThriftField
  public void setIsTypeSpecEditable(Integer isTypeSpecEditable) {
    this.isTypeSpecEditable = isTypeSpecEditable;
  }

  @ThriftField(26)
  public Integer getIsActiveStaffEditable() {
    return isActiveStaffEditable;
  }

  @ThriftField
  public void setIsActiveStaffEditable(Integer isActiveStaffEditable) {
    this.isActiveStaffEditable = isActiveStaffEditable;
  }

  @ThriftField(27)
  public String getPromptInfo() {
    return promptInfo;
  }

  @ThriftField
  public void setPromptInfo(String promptInfo) {
    this.promptInfo = promptInfo;
  }

  @ThriftField(28)
  public Integer getIsPublicVisibleEditable() {
    return isPublicVisibleEditable;
  }

  @ThriftField
  public void setIsPublicVisibleEditable(Integer isPublicVisibleEditable) {
    this.isPublicVisibleEditable = isPublicVisibleEditable;
  }
}
