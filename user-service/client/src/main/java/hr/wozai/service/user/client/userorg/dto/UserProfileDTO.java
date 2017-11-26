// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-15
 */
@ThriftStruct
public final class UserProfileDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

//  private Long userProfileId;

  private Long orgId;

  private Long userId;

  private Long onboardingTemplateId;

  private Long profileTemplateId;

//  private Integer userStatus;

  /**
   * key: field's referenceName
   * value: field's value of this item
   */
  private List<ProfileFieldDTO> profileFieldDTOs;

  private UserEmploymentDTO userEmploymentDTO;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

//  @ThriftField(1)
//  public Long getUserProfileId() {
//    return userProfileId;
//  }
//
//  @ThriftField
//  public void setUserProfileId(Long userProfileId) {
//    this.userProfileId = userProfileId;
//  }

  @ThriftField(2)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField(3)
  public Long getUserId() {
    return userId;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @ThriftField(4)
  public Long getProfileTemplateId() {
    return profileTemplateId;
  }

  @ThriftField
  public void setProfileTemplateId(Long profileTemplateId) {
    this.profileTemplateId = profileTemplateId;
  }

//  @ThriftField(5)
//  public Integer getUserStatus() {
//    return userStatus;
//  }
//
//  @ThriftField
//  public void setUserStatus(Integer userStatus) {
//    this.userStatus = userStatus;
//  }

  @ThriftField(6)
  public List<ProfileFieldDTO> getProfileFieldDTOs() {
    return profileFieldDTOs;
  }

  @ThriftField
  public void setProfileFieldDTOs(
      List<ProfileFieldDTO> profileFieldDTOs) {
    this.profileFieldDTOs = profileFieldDTOs;
  }

  @ThriftField(7)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField(8)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(9)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(10)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(11)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

  @ThriftField(12)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(13)
  public Long getOnboardingTemplateId() {
    return onboardingTemplateId;
  }

  @ThriftField
  public void setOnboardingTemplateId(Long onboardingTemplateId) {
    this.onboardingTemplateId = onboardingTemplateId;
  }

  @ThriftField(14)
  public UserEmploymentDTO getUserEmploymentDTO() {
    return userEmploymentDTO;
  }

  @ThriftField
  public void setUserEmploymentDTO(UserEmploymentDTO userEmploymentDTO) {
    this.userEmploymentDTO = userEmploymentDTO;
  }
}
