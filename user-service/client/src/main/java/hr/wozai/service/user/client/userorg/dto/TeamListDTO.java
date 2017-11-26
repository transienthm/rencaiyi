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
public final class TeamListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<TeamDTO> teamDTOList;

  private List<CoreUserProfileDTO> coreUserProfileDTOs;

  private List<ProjectTeamDTO> projectTeamDTOs;

  private long totalTeamNumber;

  private long totalUserNumber;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<TeamDTO> getTeamDTOList() {
    return teamDTOList;
  }

  @ThriftField(3)
  public List<CoreUserProfileDTO> getCoreUserProfileDTOs() {
    return coreUserProfileDTOs;
  }

  @ThriftField(4)
  public long getTotalTeamNumber() {
    return totalTeamNumber;
  }

  @ThriftField(5)
  public long getTotalUserNumber() {
    return totalUserNumber;
  }

  @ThriftField(6)
  public List<ProjectTeamDTO> getProjectTeamDTOs() {
    return projectTeamDTOs;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setTeamDTOList(List<TeamDTO> teamDTOList) {
    this.teamDTOList = teamDTOList;
  }

  @ThriftField
  public void setCoreUserProfileDTOs(List<CoreUserProfileDTO> coreUserProfileDTOs) {
    this.coreUserProfileDTOs = coreUserProfileDTOs;
  }

  @ThriftField
  public void setTotalTeamNumber(long totalTeamNumber) {
    this.totalTeamNumber = totalTeamNumber;
  }

  @ThriftField
  public void setTotalUserNumber(long totalUserNumber) {
    this.totalUserNumber = totalUserNumber;
  }

  @ThriftField
  public void setProjectTeamDTOs(List<ProjectTeamDTO> projectTeamDTOs) {
    this.projectTeamDTOs = projectTeamDTOs;
  }
}
