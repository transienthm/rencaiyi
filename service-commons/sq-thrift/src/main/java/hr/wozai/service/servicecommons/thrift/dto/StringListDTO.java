// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.thrift.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

import java.util.List;

import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-01-22
 */
@ThriftStruct
public final class StringListDTO extends BaseThriftObject {

    private List<String> data;

    private ServiceStatusDTO serviceStatusDTO;

    @ThriftField(1)
    public List<String> getData() {
      return data;
    }

    @ThriftField
    public void setData(List<String> data) {
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
