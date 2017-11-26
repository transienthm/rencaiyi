package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.ProjectTeamDTO;
import hr.wozai.service.user.client.userorg.dto.TeamDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/25
 */
@ThriftStruct
public final class UserAndTeamListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<CoreUserProfileDTO> coreUserProfileDTOList;

  private List<TeamDTO> teamDTOList;

  private List<ProjectTeamDTO> projectTeamDTOList;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<CoreUserProfileDTO> getCoreUserProfileDTOList() {
    return coreUserProfileDTOList;
  }

  @ThriftField(3)
  public List<TeamDTO> getTeamDTOList() {
    return teamDTOList;
  }

  @ThriftField(4)
  public List<ProjectTeamDTO> getProjectTeamDTOList() {
    return projectTeamDTOList;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setCoreUserProfileDTOList(List<CoreUserProfileDTO> coreUserProfileDTOList) {
    this.coreUserProfileDTOList = coreUserProfileDTOList;
  }

  @ThriftField
  public void setTeamDTOList(List<TeamDTO> teamDTOList) {
    this.teamDTOList = teamDTOList;
  }

  @ThriftField
  public void setProjectTeamDTOList(List<ProjectTeamDTO> projectTeamDTOList) {
    this.projectTeamDTOList = projectTeamDTOList;
  }
}
