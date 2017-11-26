// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.servicecommons.thrift.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-01-22
 */
@ThriftStruct
public final class LongListDTO extends BaseThriftObject {

    private List<Long> data;

    private ServiceStatusDTO serviceStatusDTO;

    @ThriftField(1)
    public List<Long> getData() {
      return data;
    }

    @ThriftField
    public void setData(List<Long> data) {
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
