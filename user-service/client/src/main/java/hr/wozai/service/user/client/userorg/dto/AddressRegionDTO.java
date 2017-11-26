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
public final class AddressRegionDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Integer regionId;

  private Integer parentId;

  private String regionName;

  private Integer regionType;

  private Integer agencyId;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Integer getRegionId() {
    return regionId;
  }

  @ThriftField
  public void setRegionId(Integer regionId) {
    this.regionId = regionId;
  }

  @ThriftField(3)
  public Integer getParentId() {
    return parentId;
  }

  @ThriftField
  public void setParentId(Integer parentId) {
    this.parentId = parentId;
  }

  @ThriftField(4)
  public String getRegionName() {
    return regionName;
  }

  @ThriftField
  public void setRegionName(String regionName) {
    this.regionName = regionName;
  }

  @ThriftField(5)
  public Integer getRegionType() {
    return regionType;
  }

  @ThriftField
  public void setRegionType(Integer regionType) {
    this.regionType = regionType;
  }

  @ThriftField(6)
  public Integer getAgencyId() {
    return agencyId;
  }

  @ThriftField
  public void setAgencyId(Integer agencyId) {
    this.agencyId = agencyId;
  }
}
