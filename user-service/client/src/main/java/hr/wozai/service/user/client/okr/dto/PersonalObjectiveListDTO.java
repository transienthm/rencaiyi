package hr.wozai.service.user.client.okr.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/6
 */
@ThriftStruct
public final class PersonalObjectiveListDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private ObjectiveListDTO teamRelatedObjective;

  private ObjectiveListDTO personalObjective;

  private boolean hasTeamObjective;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public ObjectiveListDTO getTeamRelatedObjective() {
    return teamRelatedObjective;
  }

  @ThriftField(3)
  public ObjectiveListDTO getPersonalObjective() {
    return personalObjective;
  }

  @ThriftField(4)
  public boolean isHasTeamObjective() {
    return hasTeamObjective;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setTeamRelatedObjective(ObjectiveListDTO teamRelatedObjective) {
    this.teamRelatedObjective = teamRelatedObjective;
  }

  @ThriftField
  public void setPersonalObjective(ObjectiveListDTO personalObjective) {
    this.personalObjective = personalObjective;
  }

  @ThriftField
  public void setHasTeamObjective(boolean hasTeamObjective) {
    this.hasTeamObjective = hasTeamObjective;
  }
}
