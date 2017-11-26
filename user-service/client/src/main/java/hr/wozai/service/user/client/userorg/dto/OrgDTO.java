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
public final class OrgDTO extends BaseThriftObject {

  private ServiceStatusDTO serviceStatusDTO;

  private Long orgId;

  private String fullName;

  private String shortName;

  private String description;

  private String avatarUrl;

  private Integer timeZone;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private Integer isDeleted;

  private Integer isNaviOrg;

  private Integer naviStep;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
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
  public String getFullName() {
    return fullName;
  }

  @ThriftField
  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @ThriftField(4)
  public String getShortName() {
    return shortName;
  }

  @ThriftField
  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  @ThriftField(5)
  public String getDescription() {
    return description;
  }

  @ThriftField
  public void setDescription(String description) {
    this.description = description;
  }

  @ThriftField(6)
  public String getAvatarUrl() {
    return avatarUrl;
  }

  @ThriftField
  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  @ThriftField(7)
  public Integer getTimeZone() {
    return timeZone;
  }

  @ThriftField
  public void setTimeZone(Integer timeZone) {
    this.timeZone = timeZone;
  }

  @ThriftField(8)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField(9)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField(10)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField(11)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField(12)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setIsDeleted(Integer isDeleted) {
    this.isDeleted = isDeleted;
  }

  @ThriftField(13)
  public Integer getIsNaviOrg() {
    return isNaviOrg;
  }

  @ThriftField
  public void setIsNaviOrg(Integer isNaviOrg) {
    this.isNaviOrg = isNaviOrg;
  }

  @ThriftField(14)
  public Integer getNaviStep() {
    return naviStep;
  }

  @ThriftField
  public void setNaviStep(Integer naviStep) {
    this.naviStep = naviStep;
  }
}
