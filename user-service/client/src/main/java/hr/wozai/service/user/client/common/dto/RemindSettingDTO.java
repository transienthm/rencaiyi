package hr.wozai.service.user.client.common.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/16
 */
@ThriftStruct
public final class RemindSettingDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long remindSettingId;

  private Long orgId;

  private Long userId;

  private Integer remindType;

  private String remindName;

  private Integer status;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getRemindSettingId() {
    return remindSettingId;
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
  public Integer getRemindType() {
    return remindType;
  }

  @ThriftField(6)
  public String getRemindName() {
    return remindName;
  }

  @ThriftField(7)
  public Integer getStatus() {
    return status;
  }

  @ThriftField(8)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField(9)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(10)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField(11)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setRemindSettingId(Long remindSettingId) {
    this.remindSettingId = remindSettingId;
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
  public void setRemindType(Integer remindType) {
    this.remindType = remindType;
  }

  @ThriftField
  public void setRemindName(String remindName) {
    this.remindName = remindName;
  }

  @ThriftField
  public void setStatus(Integer status) {
    this.status = status;
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
}
