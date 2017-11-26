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
public final class PickOptionDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long pickOptionId;

  private Long orgId;

  private Long profileFieldId;

  private String optionValue;

  private Integer optionIndex;

  private Integer isDefault;

  private Integer isDeprecated;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getPickOptionId() {
    return pickOptionId;
  }

  @ThriftField
  public void setPickOptionId(Long pickOptionId) {
    this.pickOptionId = pickOptionId;
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
  public Long getProfileFieldId() {
    return profileFieldId;
  }

  @ThriftField
  public void setProfileFieldId(Long profileFieldId) {
    this.profileFieldId = profileFieldId;
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
}
