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
 * @Created: 2016-03-16
 */
@ThriftStruct
public final class OssAvatarPutRequestDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private String presignedPutUrl;

  private Long putEffectiveTime;

  private String publicGetUrl;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public String getPresignedPutUrl() {
    return presignedPutUrl;
  }

  @ThriftField
  public void setPresignedPutUrl(String presignedPutUrl) {
    this.presignedPutUrl = presignedPutUrl;
  }

  @ThriftField(3)
  public Long getPutEffectiveTime() {
    return putEffectiveTime;
  }

  @ThriftField
  public void setPutEffectiveTime(Long putEffectiveTime) {
    this.putEffectiveTime = putEffectiveTime;
  }

  @ThriftField(4)
  public String getPublicGetUrl() {
    return publicGetUrl;
  }

  @ThriftField
  public void setPublicGetUrl(String publicGetUrl) {
    this.publicGetUrl = publicGetUrl;
  }
}
