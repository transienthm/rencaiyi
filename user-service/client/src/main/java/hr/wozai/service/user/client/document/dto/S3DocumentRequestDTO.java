// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.document.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
@ThriftStruct
public final class S3DocumentRequestDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long documentId;

  private Integer requestType;

  private Long effectiveTime;

  private String presignedUrl;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getDocumentId() {
    return documentId;
  }

  @ThriftField
  public void setDocumentId(Long documentId) {
    this.documentId = documentId;
  }

  @ThriftField(3)
  public Integer getRequestType() {
    return requestType;
  }

  @ThriftField
  public void setRequestType(Integer requestType) {
    this.requestType = requestType;
  }

  @ThriftField(4)
  public Long getEffectiveTime() {
    return effectiveTime;
  }

  @ThriftField
  public void setEffectiveTime(Long effectiveTime) {
    this.effectiveTime = effectiveTime;
  }

  @ThriftField(5)
  public String getPresignedUrl() {
    return presignedUrl;
  }

  @ThriftField
  public void setPresignedUrl(String presignedUrl) {
    this.presignedUrl = presignedUrl;
  }
}
