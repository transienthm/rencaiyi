// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

@ThriftStruct
public final class TokenPairDTO extends BaseThriftObject {

  private String accessToken;

  private String refreshToken;

  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  @ThriftField(1)
  public String getAccessToken() {
    return accessToken;
  }

  @ThriftField
  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  @ThriftField(2)
  public String getRefreshToken() {
    return refreshToken;
  }

  @ThriftField
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  @ThriftField(3)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }
}
