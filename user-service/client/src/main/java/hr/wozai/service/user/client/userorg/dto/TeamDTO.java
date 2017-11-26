package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/16
 */
@ThriftStruct
public final class TeamDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long teamId;

  private Long orgId;

  private String teamName;

  private Long parentTeamId;

  private Long createdUserId;

  private Long createdTime;

  private Long lastModifiedUserId;

  private Long lastModifiedTime;

  private String extend;

  private List<CoreUserProfileDTO> coreUserProfileDTOs;

  private Long teamMemberNumber;

  private Boolean hasSubordinate;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getTeamId() {
    return teamId;
  }

  @ThriftField(3)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(4)
  public String getTeamName() {
    return teamName;
  }

  @ThriftField(5)
  public Long getParentTeamId() {
    return parentTeamId;
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
  public String getExtend() {
    return extend;
  }

  @ThriftField(11)
  public List<CoreUserProfileDTO> getCoreUserProfileDTOs() {
    return coreUserProfileDTOs;
  }

  @ThriftField(12)
  public Long getTeamMemberNumber() {
    return teamMemberNumber;
  }

  @ThriftField(13)
  public Boolean getHasSubordinate() {
    return hasSubordinate;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setTeamId(Long teamId) {
    this.teamId = teamId;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
  }

  @ThriftField
  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  @ThriftField
  public void setParentTeamId(Long parentTeamId) {
    this.parentTeamId = parentTeamId;
  }

  @ThriftField
  public void setCreatedTime(Long createdTime) {
    this.createdTime = createdTime;
  }

  @ThriftField
  public void setLastModifiedTime(Long lastModifiedTime) {
    this.lastModifiedTime = lastModifiedTime;
  }

  @ThriftField
  public void setExtend(String extend) {
    this.extend = extend;
  }

  @ThriftField
  public void setCreatedUserId(Long createdUserId) {
    this.createdUserId = createdUserId;
  }

  @ThriftField
  public void setLastModifiedUserId(Long lastModifiedUserId) {
    this.lastModifiedUserId = lastModifiedUserId;
  }

  @ThriftField
  public void setCoreUserProfileDTOs(List<CoreUserProfileDTO> coreUserProfileDTOs) {
    this.coreUserProfileDTOs = coreUserProfileDTOs;
  }

  @ThriftField
  public void setTeamMemberNumber(Long teamMemberNumber) {
    this.teamMemberNumber = teamMemberNumber;
  }

  @ThriftField
  public void setHasSubordinate(Boolean hasSubordinate) {
    this.hasSubordinate = hasSubordinate;
  }
}
