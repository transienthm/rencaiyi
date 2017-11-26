package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/22
 */
@ThriftStruct
public final class RoleDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long roleId;

  private String roleName;

  private String roleDesc;

  private String extend;
@ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }
  @ThriftField(2)
  public Long getRoleId() {
    return roleId;
  }
  @ThriftField(3)
  public String getRoleName() {
    return roleName;
  }
  @ThriftField(4)
  public String getRoleDesc() {
    return roleDesc;
  }
  @ThriftField(5)
  public String getExtend() {
    return extend;
  }
  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }
  @ThriftField
  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }
  @ThriftField
  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }
  @ThriftField
  public void setRoleDesc(String roleDesc) {
    this.roleDesc = roleDesc;
  }
  @ThriftField
  public void setExtend(String extend) {
    this.extend = extend;
  }
}
