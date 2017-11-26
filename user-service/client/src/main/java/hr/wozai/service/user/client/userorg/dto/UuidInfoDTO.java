package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/25
 */
@ThriftStruct
public final class UuidInfoDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long uuidInfoId;

  private Long orgId;

  private Long userId;

  private String uuid;

  private Integer uuidUsage;

  private Long expireTime;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }
  @ThriftField(2)
  public Long getUuidInfoId() {
    return uuidInfoId;
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
  public String getUuid() {
    return uuid;
  }

  @ThriftField(6)
  public Integer getUuidUsage() {
    return uuidUsage;
  }

  @ThriftField(7)
  public Long getExpireTime() {
    return expireTime;
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
  public void setUuidInfoId(Long uuidInfoId) {
    this.uuidInfoId = uuidInfoId;
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
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }
  @ThriftField
  public void setUuidUsage(Integer uuidUsage) {
    this.uuidUsage = uuidUsage;
  }
  @ThriftField
  public void setExpireTime(Long expireTime) {
    this.expireTime = expireTime;
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
