package hr.wozai.service.user.client.userorg.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/9
 */
@ThriftStruct
public final class RolePermissionDTO extends BaseThriftObject {
  @JsonIgnore
  private ServiceStatusDTO serviceStatusDTO;

  private Long orgId;

  private Long roleId;

  private String roleName;

  private String roleDesc;

  private String resourceName;

  private String resourceCode;

  private Integer actionCode;

  private List<Integer> scope;

  private String scopeName;

  private boolean isDefault;

  @ThriftField(1)
  public ServiceStatusDTO getServiceStatusDTO() {
    return serviceStatusDTO;
  }

  @ThriftField(2)
  public Long getOrgId() {
    return orgId;
  }

  @ThriftField(3)
  public Long getRoleId() {
    return roleId;
  }

  @ThriftField(4)
  public String getRoleName() {
    return roleName;
  }

  @ThriftField(5)
  public String getRoleDesc() {
    return roleDesc;
  }

  @ThriftField(6)
  public String getResourceName() {
    return resourceName;
  }

  @ThriftField(7)
  public String getResourceCode() {
    return resourceCode;
  }

  @ThriftField(8)
  public Integer getActionCode() {
    return actionCode;
  }

  @ThriftField(9)
  public List<Integer> getScope() {
    return scope;
  }

  @ThriftField(10)
  public String getScopeName() {
    return scopeName;
  }

  @ThriftField(11)
  public boolean isDefault() {
    return isDefault;
  }

  @ThriftField
  public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
    this.serviceStatusDTO = serviceStatusDTO;
  }

  @ThriftField
  public void setOrgId(Long orgId) {
    this.orgId = orgId;
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
  public void setResourceName(String resourceName) {
    this.resourceName = resourceName;
  }

  @ThriftField
  public void setResourceCode(String resourceCode) {
    this.resourceCode = resourceCode;
  }

  @ThriftField
  public void setActionCode(Integer actionCode) {
    this.actionCode = actionCode;
  }

  @ThriftField
  public void setScope(List<Integer> scope) {
    this.scope = scope;
  }

  @ThriftField
  public void setScopeName(String scopeName) {
    this.scopeName = scopeName;
  }

  @ThriftField
  public void setDefault(boolean aDefault) {
    isDefault = aDefault;
  }
}
