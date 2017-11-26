// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.onboarding.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-20
 */
@ThriftStruct
public final class OnboardingRequestDTO extends BaseThriftObject {

  /** UserProfile fields **/

  private String fullName;

  private String emailAddress;

  private String mobilePhone;

  private Integer gender;

//  private String personalEmail;

//  private String citizenId;

  private String employeeId;

  private Long onboardingTemplateId;

  private Long jobTitle;

  private Long jobLevel;

  private Long reporterUserId;

  private Long teamId;

  private Integer isTeamAdmin;

  /** UserEmployment fields **/

  private Integer userStatus;

  private Integer onoboardingStatus;

  private Integer employmentStatus;

  private Integer contractType;

  private Long enrollDate;

  private Long resignDate;

  /** other fields **/

  private List<Long> roleIds;

  @ThriftField(1)
  public String getFullName() {
    return fullName;
  }

  @ThriftField
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @ThriftField(2)
  public String getEmailAddress() {
    return emailAddress;
  }

  @ThriftField
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @ThriftField(3)
  public String getMobilePhone() {
    return mobilePhone;
  }

  @ThriftField
  public void setMobilePhone(String mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

//  @ThriftField(4)
//  public String getCitizenId() {
//    return citizenId;
//  }
//
//  @ThriftField
//  public void setCitizenId(String citizenId) {
//    this.citizenId = citizenId;
//  }

  @ThriftField(5)
  public Integer getContractType() {
    return contractType;
  }

  @ThriftField
  public void setContractType(Integer contractType) {
    this.contractType = contractType;
  }

  @ThriftField(6)
  public Long getEnrollDate() {
    return enrollDate;
  }

  @ThriftField
  public void setEnrollDate(Long enrollDate) {
    this.enrollDate = enrollDate;
  }

  @ThriftField(7)
  public Long getResignDate() {
    return resignDate;
  }

  @ThriftField
  public void setResignDate(Long resignDate) {
    this.resignDate = resignDate;
  }

  @ThriftField(8)
  public String getEmployeeId() {
    return employeeId;
  }

  @ThriftField
  public void setEmployeeId(String employeeId) {
    this.employeeId = employeeId;
  }

  @ThriftField(9)
  public Long getOnboardingTemplateId() {
    return onboardingTemplateId;
  }

  @ThriftField
  public void setOnboardingTemplateId(Long onboardingTemplateId) {
    this.onboardingTemplateId = onboardingTemplateId;
  }

  @ThriftField(10)
  public Long getJobTitle() {
    return jobTitle;
  }

  @ThriftField
  public void setJobTitle(Long jobTitle) {
    this.jobTitle = jobTitle;
  }

  @ThriftField(11)
  public Long getJobLevel() {
    return jobLevel;
  }

  @ThriftField
  public void setJobLevel(Long jobLevel) {
    this.jobLevel = jobLevel;
  }

  @ThriftField(12)
  public Long getReporterUserId() {
    return reporterUserId;
  }

  @ThriftField
  public void setReporterUserId(Long reporterUserId) {
    this.reporterUserId = reporterUserId;
  }

  @ThriftField(13)
  public Long getTeamId() {
    return teamId;
  }

  @ThriftField
  public void setTeamId(Long teamId) {
    this.teamId = teamId;
  }

  @ThriftField(14)
  public List<Long> getRoleIds() {
    return roleIds;
  }

  @ThriftField
  public void setRoleIds(List<Long> roleIds) {
    this.roleIds = roleIds;
  }

  @ThriftField(15)
  public Integer getUserStatus() {
    return userStatus;
  }

  @ThriftField
  public void setUserStatus(Integer userStatus) {
    this.userStatus = userStatus;
  }

  @ThriftField(16)
  public Integer getOnoboardingStatus() {
    return onoboardingStatus;
  }

  @ThriftField
  public void setOnoboardingStatus(Integer onoboardingStatus) {
    this.onoboardingStatus = onoboardingStatus;
  }

  @ThriftField(17)
  public Integer getEmploymentStatus() {
    return employmentStatus;
  }

  @ThriftField
  public void setEmploymentStatus(Integer employmentStatus) {
    this.employmentStatus = employmentStatus;
  }

  @ThriftField(18)
  public Integer getGender() {
    return gender;
  }

  @ThriftField
  public void setGender(Integer gender) {
    this.gender = gender;
  }

  @ThriftField(19)
  public Integer getIsTeamAdmin() {
    return isTeamAdmin;
  }

  @ThriftField
  public void setIsTeamAdmin(Integer isTeamAdmin) {
    this.isTeamAdmin = isTeamAdmin;
  }

  //  @ThriftField(19)
//  public String getPersonalEmail() {
//    return personalEmail;
//  }
//
//  @ThriftField
//  public void setPersonalEmail(String personalEmail) {
//    this.personalEmail = personalEmail;
//  }

}
