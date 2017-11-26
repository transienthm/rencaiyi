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
 * @created 16/11/16
 */
@ThriftStruct
public final class ProjectTeamMemberListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private List<ProjectTeamMemberDTO> projectTeamMemberDTOs;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public List<ProjectTeamMemberDTO> getProjectTeamMemberDTOs() {
    return projectTeamMemberDTOs;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setProjectTeamMemberDTOs(List<ProjectTeamMemberDTO> projectTeamMemberDTOs) {
    this.projectTeamMemberDTOs = projectTeamMemberDTOs;
  }
}
