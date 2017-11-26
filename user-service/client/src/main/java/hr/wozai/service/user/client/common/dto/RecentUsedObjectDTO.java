package hr.wozai.service.user.client.common.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/25
 */
@ThriftStruct
public final class RecentUsedObjectDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long recentUsedObjectId;

  private Long orgId;

  private Long userId;

  private Integer type;

  private List<String> usedObjectId;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getRecentUsedObjectId() {
    return recentUsedObjectId;
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
  public Integer getType() {
    return type;
  }

  @ThriftField(6)
  public List<String> getUsedObjectId() {
    return usedObjectId;
  }

  @ThriftField(7)
  public Long getCreatedUserId() {
    return createdUserId;
  }

  @ThriftField(8)
  public Long getCreatedTime() {
    return createdTime;
  }

  @ThriftField(9)
  public Long getLastModifiedUserId() {
    return lastModifiedUserId;
  }

  @ThriftField(10)
  public Long getLastModifiedTime() {
    return lastModifiedTime;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setRecentUsedObjectId(Long recentUsedObjectId) {
    this.recentUsedObjectId = recentUsedObjectId;
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
  public void setType(Integer type) {
    this.type = type;
  }

  @ThriftField
  public void setUsedObjectId(List<String> usedObjectId) {
    this.usedObjectId = usedObjectId;
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
