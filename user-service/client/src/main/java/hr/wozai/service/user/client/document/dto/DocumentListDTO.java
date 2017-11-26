// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.document.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
@ThriftStruct
public final class DocumentListDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private List<DocumentDTO> documentDTOs;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public List<DocumentDTO> getDocumentDTOs() {
    return documentDTOs;
  }

  @ThriftField
  public void setDocumentDTOs(List<DocumentDTO> documentDTOs) {
    this.documentDTOs = documentDTOs;
  }
}
