// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-11
 */
@ThriftStruct
public final class CoreUserProfileDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long orgId;

  private Long userId;

  private Long profileTemplateId;

  private Long onboardingTemplateId;

  /* fields */

  private String emailAddress;

  private String mobilePhone;

  private String fullName;

  private String personalEmail;

  private Integer gender;

  private String avatarUrl;

  private TeamMemberDTO teamMemberDTO;

  private UserEmploymentDTO userEmploymentDTO;

  private String jobTitleName;

  private String reporterFullName;

  private Long enrollDate;

  private Long createdTime;

  private boolean isTeamAdmin;

  private boolean hasReportee;

  private List<RoleDTO> roleListDTO;

  private List<ProjectTeamDTO> projectTeamDTOs;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

//  @ThriftField(2)
//  public Long getUserProfileId() {
//    return coreUserProfileId;
//  }
//
//  @ThriftField
//  public void setUserProfileId(Long userProfileId) {
//    this.coreUserProfileId = userProfileId;
//  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField(4)
  public Long getUserId() {
    return userId;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @ThriftField(5)
  public Long getProfileTemplateId() {
    return profileTemplateId;
  }

  @ThriftField
  public void setProfileTemplateId(Long profileTemplateId) {
    this.profileTemplateId = profileTemplateId;
  }

//  @ThriftField(6)
//  public Integer getUserStatus() {
//    return userStatus;
//  }
//
//  @ThriftField
//  public void setUserStatus(Integer userStatus) {
//    this.userStatus = userStatus;
//  }

  @ThriftField(7)
  public String getEmailAddress() {
    return emailAddress;
  }

  @ThriftField
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  @ThriftField(8)
  public String getMobilePhone() {
    return mobilePhone;
  }

  @ThriftField
  public void setMobilePhone(String mobilePhone) {
    this.mobilePhone = mobilePhone;
  }

  @ThriftField(9)
  public String getFullName() {
    return fullName;
  }

  @ThriftField
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @ThriftField(10)
  public String getAvatarUrl() {
    return avatarUrl;
  }

  @ThriftField
  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }


  @ThriftField(12)
  public Integer getGender() {
    return gender;
  }

  @ThriftField
  public void setGender(Integer gender) {
    this.gender = gender;
  }

  @ThriftField(13)
  public TeamMemberDTO getTeamMemberDTO() {
    return teamMemberDTO;
  }

  @ThriftField
  public void setTeamMemberDTO(TeamMemberDTO teamMemberDTO) {
    this.teamMemberDTO = teamMemberDTO;
  }

  @ThriftField(14)
  public String getJobTitleName() {
    return jobTitleName;
  }

  @ThriftField
  public void setJobTitleName(String jobTitleName) {
    this.jobTitleName = jobTitleName;
  }

  @ThriftField(15)
  public Long getEnrollDate() {
    return enrollDate;
  }

  @ThriftField
  public void setEnrollDate(Long enrollDate) {
    this.enrollDate = enrollDate;
  }

  @ThriftField(16)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(17)
  public String getReporterFullName() {
    return reporterFullName;
  }

  @ThriftField
  public void setReporterFullName(String reporterFullName) {
    this.reporterFullName = reporterFullName;
  }

  @ThriftField(18)
  public UserEmploymentDTO getUserEmploymentDTO() {
    return userEmploymentDTO;
  }

  @ThriftField
  public void setUserEmploymentDTO(UserEmploymentDTO userEmploymentDTO) {
    this.userEmploymentDTO = userEmploymentDTO;
  }

  @ThriftField(19)
  public boolean isTeamAdmin() {
    return isTeamAdmin;
  }

  @ThriftField
  public void setTeamAdmin(boolean teamAdmin) {
    isTeamAdmin = teamAdmin;
  }

  @ThriftField(20)
  public boolean isHasReportee() {
    return hasReportee;
  }

  @ThriftField
  public void setHasReportee(boolean hasReportee) {
    this.hasReportee = hasReportee;
  }

  @ThriftField(21)
  public List<RoleDTO> getRoleListDTO() {
    return roleListDTO;
  }

  @ThriftField
  public void setRoleListDTO(List<RoleDTO> roleListDTO) {
    this.roleListDTO = roleListDTO;
  }

  @ThriftField(22)
  public Long getOnboardingTemplateId() {
    return onboardingTemplateId;
  }

  @ThriftField
  public void setOnboardingTemplateId(Long onboardingTemplateId) {
    this.onboardingTemplateId = onboardingTemplateId;
  }

  @ThriftField(23)
  public String getPersonalEmail() {
    return personalEmail;
  }

  @ThriftField
  public void setPersonalEmail(String personalEmail) {
    this.personalEmail = personalEmail;
  }

  @ThriftField(24)
  public List<ProjectTeamDTO> getProjectTeamDTOs() {
    return projectTeamDTOs;
  }

  @ThriftField
  public void setProjectTeamDTOs(List<ProjectTeamDTO> projectTeamDTOs) {
    this.projectTeamDTOs = projectTeamDTOs;
  }
}
