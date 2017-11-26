package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
@ThriftStruct
public final class DirectorDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long directorId;

  private Long orgId;

  private Long userId;

  // 1:objective 2:key result
  private Integer type;

  private Long objectId;

  private CoreUserProfileDTO coreUserProfileDTO;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private String userName;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getDirectorId() {
    return directorId;
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
  public Long getObjectId() {
    return objectId;
  }

  @ThriftField(7)
  public CoreUserProfileDTO getCoreUserProfileDTO() {
    return coreUserProfileDTO;
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

  @ThriftField(12)
  public String getUserName() {
    return userName;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setDirectorId(Long directorId) {
    this.directorId = directorId;
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
  public void setObjectId(Long objectId) {
    this.objectId = objectId;
  }

  @ThriftField
  public void setCoreUserProfileDTO(CoreUserProfileDTO coreUserProfileDTO) {
    this.coreUserProfileDTO = coreUserProfileDTO;
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
  public void setUserName(String userName) {
    this.userName = userName;
  }
}
