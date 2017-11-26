// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.onboarding.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.user.client.onboarding.dto.OnboardingRequestDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-09
 */
@ThriftStruct
public final class OrgAccountRequestDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private String orgFullName;

  private String orgShortName;

  private String orgAvatarUrl;

  private Integer orgTimeZone;

//  /**
//   * For the superAdmin user
//   */
//  private SuperAdminDTO superAdminDTO;

  /**
   * For the first user
   */
  private OnboardingRequestDTO onboardingRequestDTO;

  private String sqStaffMobilePhone;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public String getOrgFullName() {
    return orgFullName;
  }

  @ThriftField
  public void setOrgFullName(String orgFullName) {
    this.orgFullName = orgFullName;
  }

  @ThriftField(3)
  public String getOrgShortName() {
    return orgShortName;
  }

  @ThriftField
  public void setOrgShortName(String orgShortName) {
    this.orgShortName = orgShortName;
  }

  @ThriftField(4)
  public String getOrgAvatarUrl() {
    return orgAvatarUrl;
  }

  @ThriftField
  public void setOrgAvatarUrl(String orgAvatarUrl) {
    this.orgAvatarUrl = orgAvatarUrl;
  }

  @ThriftField(5)
  public Integer getOrgTimeZone() {
    return orgTimeZone;
  }

  @ThriftField
  public void setOrgTimeZone(Integer orgTimeZone) {
    this.orgTimeZone = orgTimeZone;
  }

  @ThriftField(6)
  public OnboardingRequestDTO getOnboardingRequestDTO() {
    return onboardingRequestDTO;
  }

  @ThriftField
  public void setOnboardingRequestDTO(
      OnboardingRequestDTO onboardingRequestDTO) {
    this.onboardingRequestDTO = onboardingRequestDTO;
  }

  @ThriftField(7)
  public String getSqStaffMobilePhone() {
    return sqStaffMobilePhone;
  }

  @ThriftField
  public void setSqStaffMobilePhone(String sqStaffMobilePhone) {
    this.sqStaffMobilePhone = sqStaffMobilePhone;
  }

//  @ThriftField(8)
//  public SuperAdminDTO getSuperAdminDTO() {
//    return superAdminDTO;
//  }
//
//  @ThriftField
//  public void setSuperAdminDTO(SuperAdminDTO superAdminDTO) {
//    this.superAdminDTO = superAdminDTO;
//  }
}
