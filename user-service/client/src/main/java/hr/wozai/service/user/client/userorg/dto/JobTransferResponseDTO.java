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
 * @Created: 2016-05-29
 */
@ThriftStruct
public final class JobTransferResponseDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long jobTransferId;

  private Long orgId;

  private Long userId;

  private SimpleUserProfileDTO userSimpleUserProfileDTO;

  private String transferType;

  private Long transferDate;

  private String description;

  private TeamDTO beforeTeamDTO;

  private SimpleUserProfileDTO beforeReporterSimpleUserProfileDTO;

  private OrgPickOptionDTO beforeJobTitleOrgPickOptionDTO;

  private OrgPickOptionDTO beforeJobLevelOrgPickOptionDTO;

  private TeamDTO afterTeamDTO;

  private SimpleUserProfileDTO afterReporterSimpleUserProfileDTO;

  private OrgPickOptionDTO afterJobTitleOrgPickOptionDTO;

  private OrgPickOptionDTO afterJobLevelOrgPickOptionDTO;

  private List<SimpleUserProfileDTO> simpleUserProfileDTOs;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

  @ThriftField(1)
  public Long getJobTransferId() {
    return jobTransferId;
  }

  @ThriftField
  public void setJobTransferId(Long jobTransferId) {
    this.jobTransferId = jobTransferId;
  }

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
  public SimpleUserProfileDTO getUserSimpleUserProfileDTO() {
    return userSimpleUserProfileDTO;
  }

  @ThriftField
  public void setUserSimpleUserProfileDTO(
      SimpleUserProfileDTO userSimpleUserProfileDTO) {
    this.userSimpleUserProfileDTO = userSimpleUserProfileDTO;
  }

  @ThriftField(5)
  public String getTransferType() {
    return transferType;
  }

  @ThriftField
  public void setTransferType(String transferType) {
    this.transferType = transferType;
  }

  @ThriftField(6)
  public Long getTransferDate() {
    return transferDate;
  }

  @ThriftField
  public void setTransferDate(Long transferDate) {
    this.transferDate = transferDate;
  }

  @ThriftField(7)
  public String getDescription() {
    return description;
  }

  @ThriftField
  public void setDescription(String description) {
    this.description = description;
  }

  @ThriftField(8)
  public TeamDTO getBeforeTeamDTO() {
    return beforeTeamDTO;
  }

  @ThriftField
  public void setBeforeTeamDTO(TeamDTO beforeTeamDTO) {
    this.beforeTeamDTO = beforeTeamDTO;
  }

  @ThriftField(9)
  public SimpleUserProfileDTO getBeforeReporterSimpleUserProfileDTO() {
    return beforeReporterSimpleUserProfileDTO;
  }

  @ThriftField
  public void setBeforeReporterSimpleUserProfileDTO(
      SimpleUserProfileDTO beforeReporterSimpleUserProfileDTO) {
    this.beforeReporterSimpleUserProfileDTO = beforeReporterSimpleUserProfileDTO;
  }

  @ThriftField(10)
  public OrgPickOptionDTO getBeforeJobTitleOrgPickOptionDTO() {
    return beforeJobTitleOrgPickOptionDTO;
  }

  @ThriftField
  public void setBeforeJobTitleOrgPickOptionDTO(
      OrgPickOptionDTO beforeJobTitleOrgPickOptionDTO) {
    this.beforeJobTitleOrgPickOptionDTO = beforeJobTitleOrgPickOptionDTO;
  }

  @ThriftField(11)
  public OrgPickOptionDTO getBeforeJobLevelOrgPickOptionDTO() {
    return beforeJobLevelOrgPickOptionDTO;
  }

  @ThriftField
  public void setBeforeJobLevelOrgPickOptionDTO(
      OrgPickOptionDTO beforeJobLevelOrgPickOptionDTO) {
    this.beforeJobLevelOrgPickOptionDTO = beforeJobLevelOrgPickOptionDTO;
  }

  @ThriftField(12)
  public TeamDTO getAfterTeamDTO() {
    return afterTeamDTO;
  }

  @ThriftField
  public void setAfterTeamDTO(TeamDTO afterTeamDTO) {
    this.afterTeamDTO = afterTeamDTO;
  }

  @ThriftField(13)
  public SimpleUserProfileDTO getAfterReporterSimpleUserProfileDTO() {
    return afterReporterSimpleUserProfileDTO;
  }

  @ThriftField
  public void setAfterReporterSimpleUserProfileDTO(
      SimpleUserProfileDTO afterReporterSimpleUserProfileDTO) {
    this.afterReporterSimpleUserProfileDTO = afterReporterSimpleUserProfileDTO;
  }

  @ThriftField(14)
  public OrgPickOptionDTO getAfterJobTitleOrgPickOptionDTO() {
    return afterJobTitleOrgPickOptionDTO;
  }

  @ThriftField
  public void setAfterJobTitleOrgPickOptionDTO(
      OrgPickOptionDTO afterJobTitleOrgPickOptionDTO) {
    this.afterJobTitleOrgPickOptionDTO = afterJobTitleOrgPickOptionDTO;
  }

  @ThriftField(15)
  public OrgPickOptionDTO getAfterJobLevelOrgPickOptionDTO() {
    return afterJobLevelOrgPickOptionDTO;
  }

  @ThriftField
  public void setAfterJobLevelOrgPickOptionDTO(
      OrgPickOptionDTO afterJobLevelOrgPickOptionDTO) {
    this.afterJobLevelOrgPickOptionDTO = afterJobLevelOrgPickOptionDTO;
  }

  @ThriftField(16)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField(17)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(18)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(19)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(20)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

  @ThriftField(21)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(22)
  public List<SimpleUserProfileDTO> getSimpleUserProfileDTOs() {
    return simpleUserProfileDTOs;
  }

  @ThriftField
  public void setSimpleUserProfileDTOs(List<SimpleUserProfileDTO> simpleUserProfileDTOs) {
    this.simpleUserProfileDTOs = simpleUserProfileDTOs;
  }
}
