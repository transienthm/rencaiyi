package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
@ThriftStruct
public final class ObjectivePeriodDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long objectivePeriodId;

  private Long orgId;

  private Integer type;

  private Long ownerId;

  private Integer periodTimeSpanId;

  private Integer year;

  private String name;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private boolean isDefault;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getObjectivePeriodId() {
    return objectivePeriodId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Integer getType() {
    return type;
  }

  @ThriftField(5)
  public Long getOwnerId() {
    return ownerId;
  }

  @ThriftField(6)
  public Integer getPeriodTimeSpanId() {
    return periodTimeSpanId;
  }

  @ThriftField(7)
  public Integer getYear() {
    return year;
  }

  @ThriftField(8)
  public String getName() {
    return name;
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
  public boolean isDefault() {
    return isDefault;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setObjectivePeriodId(Long objectivePeriodId) {
    this.objectivePeriodId = objectivePeriodId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setType(Integer type) {
    this.type = type;
  }

  @ThriftField
  public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
  }

  @ThriftField
  public void setPeriodTimeSpanId(Integer periodTimeSpanId) {
    this.periodTimeSpanId = periodTimeSpanId;
  }

  @ThriftField
  public void setYear(Integer year) {
    this.year = year;
  }

  @ThriftField
  public void setName(String name) {
    this.name = name;
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
  public void setDefault(boolean isDefault) {
    this.isDefault = isDefault;
  }
}
