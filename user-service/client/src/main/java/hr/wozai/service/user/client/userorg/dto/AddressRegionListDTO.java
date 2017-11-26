// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-03
 */
@ThriftStruct
public final class AddressRegionListDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private List<AddressRegionDTO> addressRegionDTOs;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public List<AddressRegionDTO> getAddressRegionDTOs() {
    return addressRegionDTOs;
  }

  @ThriftField
  public void setAddressRegionDTOs(
      List<AddressRegionDTO> addressRegionDTOs) {
    this.addressRegionDTOs = addressRegionDTOs;
  }
}