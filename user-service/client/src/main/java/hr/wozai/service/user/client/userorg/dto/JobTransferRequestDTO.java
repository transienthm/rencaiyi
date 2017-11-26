// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-29
 */
@ThriftStruct
public final class JobTransferRequestDTO extends BaseThriftObject{

  private Long jobTransferId;

  private Long orgId;

  private Long userId;

  private String transferType;

  private Long transferDate;

  private String description;

  private Long beforeTeamId;

  private Long beforeReporterId;

  private Long beforeJobTitleId;

  private Long beforeJobLevelId;

  private Long afterTeamId;

  private Long afterReporterId;

  private Long afterJobTitleId;

  private List<Long> toNotifyUserIds;

  private Long afterJobLevelId;

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
  public Long getBeforeTeamId() {
    return beforeTeamId;
  }

  @ThriftField
  public void setBeforeTeamId(Long beforeTeamId) {
    this.beforeTeamId = beforeTeamId;
  }

  @ThriftField(9)
  public Long getBeforeReporterId() {
    return beforeReporterId;
  }

  @ThriftField
  public void setBeforeReporterId(Long beforeReporterId) {
    this.beforeReporterId = beforeReporterId;
  }

  @ThriftField(10)
  public Long getBeforeJobTitleId() {
    return beforeJobTitleId;
  }

  @ThriftField
  public void setBeforeJobTitleId(Long beforeJobTitleId) {
    this.beforeJobTitleId = beforeJobTitleId;
  }

  @ThriftField(11)
  public Long getBeforeJobLevelId() {
    return beforeJobLevelId;
  }

  @ThriftField
  public void setBeforeJobLevelId(Long beforeJobLevelId) {
    this.beforeJobLevelId = beforeJobLevelId;
  }

  @ThriftField(12)
  public Long getAfterTeamId() {
    return afterTeamId;
  }

  @ThriftField
  public void setAfterTeamId(Long afterTeamId) {
    this.afterTeamId = afterTeamId;
  }

  @ThriftField(13)
  public Long getAfterReporterId() {
    return afterReporterId;
  }

  @ThriftField
  public void setAfterReporterId(Long afterReporterId) {
    this.afterReporterId = afterReporterId;
  }

  @ThriftField(14)
  public Long getAfterJobTitleId() {
    return afterJobTitleId;
  }

  @ThriftField
  public void setAfterJobTitleId(Long afterJobTitleId) {
    this.afterJobTitleId = afterJobTitleId;
  }

  @ThriftField(15)
  public Long getAfterJobLevelId() {
    return afterJobLevelId;
  }

  @ThriftField
  public void setAfterJobLevelId(Long afterJobLevelId) {
    this.afterJobLevelId = afterJobLevelId;
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
  public List<Long> getToNotifyUserIds() {
    return toNotifyUserIds;
  }

  @ThriftField
  public void setToNotifyUserIds(List<Long> toNotifyUserIds) {
    this.toNotifyUserIds = toNotifyUserIds;
  }
}
