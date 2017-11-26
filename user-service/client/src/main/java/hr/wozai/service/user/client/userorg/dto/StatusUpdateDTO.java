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
public final class StatusUpdateDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long statusUpdateId;

  private Long orgId;

  private Long userId;

  private Integer statusType;

  private String updateType;

  private Long updateDate;

  private String description;

  private List<Long> toNotifyUserIds;

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
  public Long getStatusUpdateId() {
    return statusUpdateId;
  }

  @ThriftField
  public void setStatusUpdateId(Long statusUpdateId) {
    this.statusUpdateId = statusUpdateId;
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
  public Long getUserId() {
    return userId;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @ThriftField(5)
  public Integer getStatusType() {
    return statusType;
  }

  @ThriftField
  public void setStatusType(Integer statusType) {
    this.statusType = statusType;
  }

  @ThriftField(6)
  public String getUpdateType() {
    return updateType;
  }

  @ThriftField
  public void setUpdateType(String updateType) {
    this.updateType = updateType;
  }

  @ThriftField(7)
  public Long getUpdateDate() {
    return updateDate;
  }

  @ThriftField
  public void setUpdateDate(Long updateDate) {
    this.updateDate = updateDate;
  }

  @ThriftField(8)
  public String getDescription() {
    return description;
  }

  @ThriftField
  public void setDescription(String description) {
    this.description = description;
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
  public List<Long> getToNotifyUserIds() {
    return toNotifyUserIds;
  }

  @ThriftField
  public void setToNotifyUserIds(List<Long> toNotifyUserIds) {
    this.toNotifyUserIds = toNotifyUserIds;
  }
}
