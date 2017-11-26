// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.conversation.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;

import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
@ThriftStruct
public final class ConvrScheduleDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long convrScheduleId;

  private Long orgId;

  private Long sourceUserId;

  private Long targetUserId;

  private Integer periodType;

  private Integer remindDay;

  private Integer isActive;

  private Integer convrCount;

  private Long lastConvrDate;

  private Integer currentPeriodStatus;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getConvrScheduleId() {
    return convrScheduleId;
  }

  @ThriftField
  public void setConvrScheduleId(Long convrScheduleId) {
    this.convrScheduleId = convrScheduleId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField(4)
  public Long getSourceUserId() {
    return sourceUserId;
  }

  @ThriftField
  public void setSourceUserId(Long sourceUserId) {
    this.sourceUserId = sourceUserId;
  }

  @ThriftField(5)
  public Long getTargetUserId() {
    return targetUserId;
  }

  @ThriftField
  public void setTargetUserId(Long targetUserId) {
    this.targetUserId = targetUserId;
  }

  @ThriftField(6)
  public Integer getPeriodType() {
    return periodType;
  }

  @ThriftField
  public void setPeriodType(Integer periodType) {
    this.periodType = periodType;
  }

  @ThriftField(7)
  public Integer getRemindDay() {
    return remindDay;
  }

  @ThriftField
  public void setRemindDay(Integer remindDay) {
    this.remindDay = remindDay;
  }

  @ThriftField(8)
  public Integer getIsActive() {
    return isActive;
  }

  @ThriftField
  public void setIsActive(Integer isActive) {
    this.isActive = isActive;
  }

  @ThriftField(9)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField(10)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(11)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(12)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(13)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

  @ThriftField(14)
  public Integer getConvrCount() {
    return convrCount;
  }

  @ThriftField
  public void setConvrCount(Integer convrCount) {
    this.convrCount = convrCount;
  }

  @ThriftField(15)
  public Long getLastConvrDate() {
    return lastConvrDate;
  }

  @ThriftField
  public void setLastConvrDate(Long lastConvrDate) {
    this.lastConvrDate = lastConvrDate;
  }

  @ThriftField(16)
  public Integer getCurrentPeriodStatus() {
    return currentPeriodStatus;
  }

  @ThriftField
  public void setCurrentPeriodStatus(Integer currentPeriodStatus) {
    this.currentPeriodStatus = currentPeriodStatus;
  }

}
