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
public final class TeamMemberDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long userId;

  private Long teamId;

  private String teamName;

  private Integer isTeamAdmin;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getUserId() {
    return userId;
  }

  @ThriftField(3)
  public Long getTeamId() {
    return teamId;
  }

  @ThriftField(4)
  public String getTeamName() {
    return teamName;
  }

  @ThriftField(5)
  public Integer getIsTeamAdmin() {
    return isTeamAdmin;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setUserId(Long userId) {
    this.userId = userId;
  }

  @ThriftField
  public void setTeamId(Long teamId) {
    this.teamId = teamId;
  }

  @ThriftField
  public void setTeamName(String teamName) {
    this.teamName = teamName;
  }

  @ThriftField
  public void setIsTeamAdmin(Integer isTeamAdmin) {
    this.isTeamAdmin = isTeamAdmin;
  }
}
