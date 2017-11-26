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
 * @Created: 2016-03-11
 */
@ThriftStruct
public final class CoreUserProfileListDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private List<CoreUserProfileDTO> coreUserProfileDTOs;

  private int totalNumber;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public List<CoreUserProfileDTO> getCoreUserProfileDTOs() {
    return coreUserProfileDTOs;
  }

  @ThriftField
  public void setCoreUserProfileDTOs(
      List<CoreUserProfileDTO> coreUserProfileDTOs) {
    this.coreUserProfileDTOs = coreUserProfileDTOs;
  }

  @ThriftField(3)
  public int getTotalNumber() {
    return totalNumber;
  }

  @ThriftField
  public void setTotalNumber(int totalNumber) {
    this.totalNumber = totalNumber;
  }
}