package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/16
 */
@ThriftStruct
public final class ProjectTeamMemberDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long projectTeamMemberId;

  private Long orgId;

  private Long projectTeamId;

  private Long userId;

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
  public Long getProjectTeamMemberId() {
    return projectTeamMemberId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public Long getProjectTeamId() {
    return projectTeamId;
  }

  @ThriftField(5)
  public Long getUserId() {
    return userId;
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

  @ThriftField(10)
  public Integer getIsDeleted() {
    return isDeleted;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setProjectTeamMemberId(Long projectTeamMemberId) {
    this.projectTeamMemberId = projectTeamMemberId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setProjectTeamId(Long projectTeamId) {
    this.projectTeamId = projectTeamId;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
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
