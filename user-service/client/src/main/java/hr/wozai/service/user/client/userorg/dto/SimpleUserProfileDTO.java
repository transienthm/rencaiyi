// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-11
 */
@ThriftStruct
public final class SimpleUserProfileDTO extends BaseThriftObject {

  private Long userId;

  private String fullName;

  private String avatarUrl;

  private Integer gender;

  @ThriftField(1)
  public Long getUserId() {
    return userId;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @ThriftField(2)
  public String getFullName() {
    return fullName;
  }

  @ThriftField
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @ThriftField(3)
  public String getAvatarUrl() {
    return avatarUrl;
  }

  @ThriftField
  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  @ThriftField(4)
  public Integer getGender() {
    return gender;
  }

  @ThriftField
  public void setGender(Integer gender) {
    this.gender = gender;
  }

}
