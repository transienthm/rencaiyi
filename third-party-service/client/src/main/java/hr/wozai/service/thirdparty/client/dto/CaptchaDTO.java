// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-08
 */
@ThriftStruct
public final class CaptchaDTO {

  private long createTime;

  private String captchaImageBase64;

  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  @ThriftField(1)
  public long getCreateTime() {
    return createTime;
  }

  @ThriftField
  public void setCreateTime(long createTime) {
    this.createTime = createTime;
  }

  @ThriftField(2)
  public String getCaptchaImageBase64() {
    return captchaImageBase64;
  }

  @ThriftField
  public void setCaptchaImageBase64(String captchaImageBase64) {
    this.captchaImageBase64 = captchaImageBase64;
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
