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
 * @Created: 2016-05-29
 */
@ThriftStruct
public final class JobTransferResponseListDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private List<JobTransferResponseDTO> jobTransferDTOs;

  private Integer totalNumber;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public List<JobTransferResponseDTO> getJobTransferDTOs() {
    return jobTransferDTOs;
  }

  @ThriftField
  public void setJobTransferDTOs(
      List<JobTransferResponseDTO> jobTransferDTOs) {
    this.jobTransferDTOs = jobTransferDTOs;
  }

  @ThriftField(3)
  public Integer getTotalNumber() {
    return totalNumber;
  }

  @ThriftField
  public void setTotalNumber(Integer totalNumber) {
    this.totalNumber = totalNumber;
  }
}
