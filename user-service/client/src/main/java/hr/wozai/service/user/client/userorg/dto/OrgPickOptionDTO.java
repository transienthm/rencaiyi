// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-06
 */
@ThriftStruct
public final class OrgPickOptionDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long orgPickOptionId;

  private Long orgId;

  private Integer configType;

  private String optionValue;

  private Integer optionIndex;

  private Integer isDefault;

  private Integer isDeprecated;

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
  public Long getOrgPickOptionId() {
    return orgPickOptionId;
  }

  @ThriftField
  public void setOrgPickOptionId(Long orgPickOptionId) {
    this.orgPickOptionId = orgPickOptionId;
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
  public Integer getConfigType() {
    return configType;
  }

  @ThriftField
  public void setConfigType(Integer configType) {
    this.configType = configType;
  }

  @ThriftField(5)
  public String getOptionValue() {
    return optionValue;
  }

  @ThriftField
  public void setOptionValue(String optionValue) {
    this.optionValue = optionValue;
  }

  @ThriftField(6)
  public Integer getOptionIndex() {
    return optionIndex;
  }

  @ThriftField
  public void setOptionIndex(Integer optionIndex) {
    this.optionIndex = optionIndex;
  }

  @ThriftField(7)
  public Integer getIsDefault() {
    return isDefault;
  }

  @ThriftField
  public void setIsDefault(Integer isDefault) {
    this.isDefault = isDefault;
  }

  @ThriftField(8)
  public Integer getIsDeprecated() {
    return isDeprecated;
  }

  @ThriftField
  public void setIsDeprecated(Integer isDeprecated) {
    this.isDeprecated = isDeprecated;
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
}
