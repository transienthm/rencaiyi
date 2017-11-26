package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/17
 */
@ThriftStruct
public final class UserInfoDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private CoreUserProfileDTO coreUserProfileDTO;

  private OrgDTO orgDTO;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public CoreUserProfileDTO getCoreUserProfileDTO() {
    return coreUserProfileDTO;
  }

  @ThriftField(3)
  public OrgDTO getOrgDTO() {
    return orgDTO;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setCoreUserProfileDTO(CoreUserProfileDTO coreUserProfileDTO) {
    this.coreUserProfileDTO = coreUserProfileDTO;
  }

  @ThriftField
  public void setOrgDTO(OrgDTO orgDTO) {
    this.orgDTO = orgDTO;
  }
}
