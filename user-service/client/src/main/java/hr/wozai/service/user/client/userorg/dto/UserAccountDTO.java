// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * Created by lepujiu on 16/1/18.
 */
@ThriftStruct
public final class UserAccountDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long userId;

  private String emailAddress;

  private Long createdTime;

  private Long lastModifiedTime;

  private String extend;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getUserId() {
    return userId;
  }

  @ThriftField(3)
  public String getEmailAddress() {
    return emailAddress;
  }

  @ThriftField(4)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(5)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField(6)
  public String getExtend() {
    return extend;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @ThriftField
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField
  public void setExtend(String extend) {
    this.extend = extend;
  }

  public static UserAccountDTO getMock() {
    UserAccountDTO userAccountDTO = new UserAccountDTO();
    userAccountDTO.setUserId(1l);
    userAccountDTO.setEmailAddress("test@sqian.com");
    userAccountDTO.setCreatedTime(1l);
    userAccountDTO.setLastModifiedTime(1l);
    return userAccountDTO;
  }
}
