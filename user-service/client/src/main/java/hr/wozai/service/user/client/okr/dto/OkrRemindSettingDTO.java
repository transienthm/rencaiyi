package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
@ThriftStruct
public final class OkrRemindSettingDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long okrRemindSettingId;

  private Long orgId;

  private Integer remindType;

  private Integer frequency;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getOkrRemindSettingId() {
    return okrRemindSettingId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Integer getRemindType() {
    return remindType;
  }

  @ThriftField(5)
  public Integer getFrequency() {
    return frequency;
  }

  @ThriftField(6)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField(7)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(8)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField(9)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setOkrRemindSettingId(Long okrRemindSettingId) {
    this.okrRemindSettingId = okrRemindSettingId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setRemindType(Integer remindType) {
    this.remindType = remindType;
  }

  @ThriftField
  public void setFrequency(Integer frequency) {
    this.frequency = frequency;
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
