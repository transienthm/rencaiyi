// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * Created by Zhe Chen on 16/8/01.
 */
@ThriftStruct
public final class OrgPickOptionListDTO extends BaseThriftObject {

  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<OrgPickOptionDTO> orgPickOptionDTOs;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public List<OrgPickOptionDTO> getOrgPickOptionDTOs() {
    return orgPickOptionDTOs;
  }

  @ThriftField
  public void setOrgPickOptionDTOs(
      List<OrgPickOptionDTO> orgPickOptionDTOs) {
    this.orgPickOptionDTOs = orgPickOptionDTOs;
  }
}
