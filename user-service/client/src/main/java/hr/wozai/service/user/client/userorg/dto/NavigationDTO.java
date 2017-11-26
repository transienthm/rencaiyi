// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-06
 */
@ThriftStruct
public final class NavigationDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  public Long navigationId;

  public Long orgId;

  public Long userId;

  public Long naviOrgId;

  public Long naviUserId;

  public Integer naviModule;

  public Integer naviStep;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getNavigationId() {
    return navigationId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Long getUserId() {
    return userId;
  }

  @ThriftField(5)
  public Long getNaviOrgId() {
    return naviOrgId;
  }

  @ThriftField(6)
  public Long getNaviUserId() {
    return naviUserId;
  }

  @ThriftField(7)
  public Integer getNaviModule() {
    return naviModule;
  }

  @ThriftField(8)
  public Integer getNaviStep() {
    return naviStep;
  }

  @ThriftField(9)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField(10)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(11)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField(12)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField(13)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setNavigationId(Long navigationId) {
    this.navigationId = navigationId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @ThriftField
  public void setNaviOrgId(Long naviOrgId) {
    this.naviOrgId = naviOrgId;
  }

  @ThriftField
  public void setNaviUserId(Long naviUserId) {
    this.naviUserId = naviUserId;
  }

  @ThriftField
  public void setNaviModule(Integer naviModule) {
    this.naviModule = naviModule;
  }

  @ThriftField
  public void setNaviStep(Integer naviStep) {
    this.naviStep = naviStep;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }
}
