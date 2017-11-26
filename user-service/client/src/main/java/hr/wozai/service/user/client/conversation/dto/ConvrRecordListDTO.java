// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.conversation.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

import java.util.List;

import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
@ThriftStruct
public final class ConvrRecordListDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private List<ConvrRecordDTO> convrRecordDTOs;

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
  public List<ConvrRecordDTO> getConvrRecordDTOs() {
    return convrRecordDTOs;
  }

  @ThriftField
  public void setConvrRecordDTOs(
      List<ConvrRecordDTO> convrRecordDTOs) {
    this.convrRecordDTOs = convrRecordDTOs;
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
