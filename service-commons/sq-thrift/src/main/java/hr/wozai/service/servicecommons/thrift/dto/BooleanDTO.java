// Copyright (C) 2015 Wozai
// All rights reserved
package hr.wozai.service.servicecommons.thrift.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * string 型 dto
 *
 * @author liangyafei
 * @version 1.0
 * @created 15-8-31 下午9:56
 */
@ThriftStruct
public final class BooleanDTO extends BaseThriftObject {

  private boolean data;

  private ServiceStatusDTO serviceStatusDTO;

  @ThriftField(1)
  public boolean getData() {
    return data;
  }

  @ThriftField
  public void setData(boolean data) {
    this.data = data;
  }

  @ThriftField(2)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }
}
