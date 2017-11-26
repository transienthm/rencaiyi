package hr.wozai.service.api.controller.securitymodel;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;

import hr.wozai.service.user.client.userorg.dto.RolePermissionDTO;
import hr.wozai.service.user.client.userorg.dto.RolePermissionListDTO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ScopeCode;
import hr.wozai.service.user.client.userorg.facade.SecurityModelFacade;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.securitymodel.RolePermissionVO;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/3
 */
@Controller("rolePermissionController")
public class RolePermissionController {
  private static final Logger LOGGER = LoggerFactory.getLogger(RolePermissionController.class);

  private static final String SCOPE_ORG = "允许查看全部";
  private static final String SCOPE__UP = "只允许从本团队上溯分支";
  private static final String SCOPE__DOWN = "只允许查看本团队及子团队";
  private static final String SCOPE__TEAM = "只允许查看本团队";
  private static final String SCOPE__REPORTLINE = "只允许查看本人及下属的汇报对象";

  private static final List<Integer> SCOPE_ORG_LIST = new ArrayList<>();
  private static final List<Integer> SCOPE_UP_LIST = new ArrayList<>();
  private static final List<Integer> SCOPE_DOWN_LIST = new ArrayList<>();
  private static final List<Integer> SCOPE_TEAM_LIST = new ArrayList<>();
  private static final List<Integer> SCOPE_REPORTLINE_LIST = new ArrayList<>();

  static {
    int own = ScopeCode.OWNER.getCode();
    int subteam = ScopeCode.SUBTEAM.getCode();
    int team = ScopeCode.TEAM.getCode();
    int org = ScopeCode.ORG.getCode();
    int upteam = ScopeCode.UPTEAM.getCode();
    int subordinate = ScopeCode.SUBORDINATE.getCode();

    SCOPE_ORG_LIST.add(org);

    SCOPE_UP_LIST.add(own);
    SCOPE_UP_LIST.add(team);
    SCOPE_UP_LIST.add(upteam);

    SCOPE_DOWN_LIST.add(own);
    SCOPE_DOWN_LIST.add(team);
    SCOPE_DOWN_LIST.add(subteam);

    SCOPE_TEAM_LIST.add(own);
    SCOPE_TEAM_LIST.add(team);

    SCOPE_REPORTLINE_LIST.add(own);
    SCOPE_REPORTLINE_LIST.add(subordinate);
  }

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  PermissionUtil permissionUtil;

  /*@LogAround
  @RequestMapping(value = "/role-permissions/config-setup", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listConfigurationSetUp() {
    SecurityModelFacade securityModelFacade = facadeFactory.getSecurityModelFacade();
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();

    boolean isPermitted = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, orgId, orgId,
            ResourceCode.SYSTEM_ADMIN.getResourceCode(), 2, ActionCode.EDIT.getCode());
    if (!isPermitted) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    RolePermissionListDTO remoteResult = securityModelFacade.listSpecificRolePermissions(orgId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    LOGGER.info("###remoteResult:{}", remoteResult);

    List<RolePermissionVO> rolePermissionVOs = new ArrayList<>();
    rolePermissionVOs.add(getRolePermission(DefaultRole.STAFF, ResourceCode.USER_ORG, SCOPE_ORG, SCOPE_ORG_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.STAFF, ResourceCode.USER_ORG, SCOPE__UP, SCOPE_UP_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.STAFF, ResourceCode.USER_ORG, SCOPE__DOWN, SCOPE_DOWN_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.STAFF, ResourceCode.USER_ORG, SCOPE__TEAM, SCOPE_TEAM_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.SENIOR_STAFF, ResourceCode.USER_ORG, SCOPE_ORG, SCOPE_ORG_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.SENIOR_STAFF, ResourceCode.USER_ORG, SCOPE__UP, SCOPE_UP_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.SENIOR_STAFF, ResourceCode.USER_ORG, SCOPE__DOWN, SCOPE_DOWN_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.SENIOR_STAFF, ResourceCode.USER_ORG, SCOPE__TEAM, SCOPE_TEAM_LIST));

    rolePermissionVOs.add(getRolePermission(DefaultRole.STAFF, ResourceCode.REPORT_LINE, SCOPE_ORG, SCOPE_ORG_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.STAFF, ResourceCode.REPORT_LINE, SCOPE__REPORTLINE, SCOPE_REPORTLINE_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.SENIOR_STAFF, ResourceCode.REPORT_LINE, SCOPE_ORG, SCOPE_ORG_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.SENIOR_STAFF, ResourceCode.REPORT_LINE, SCOPE__REPORTLINE, SCOPE_REPORTLINE_LIST));

    rolePermissionVOs.add(getRolePermission(DefaultRole.STAFF, ResourceCode.OKR, SCOPE_ORG, SCOPE_ORG_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.STAFF, ResourceCode.OKR, SCOPE__UP, SCOPE_UP_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.STAFF, ResourceCode.OKR, SCOPE__DOWN, SCOPE_DOWN_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.STAFF, ResourceCode.OKR, SCOPE__TEAM, SCOPE_TEAM_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.SENIOR_STAFF, ResourceCode.OKR, SCOPE_ORG, SCOPE_ORG_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.SENIOR_STAFF, ResourceCode.OKR, SCOPE__UP, SCOPE_UP_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.SENIOR_STAFF, ResourceCode.OKR, SCOPE__DOWN, SCOPE_DOWN_LIST));
    rolePermissionVOs.add(getRolePermission(DefaultRole.SENIOR_STAFF, ResourceCode.OKR, SCOPE__TEAM, SCOPE_TEAM_LIST));

    for (RolePermissionDTO rolePermissionDTO : remoteResult.getRolePermissions()) {
      setDefault(rolePermissionVOs, rolePermissionDTO.getRoleName(),
              rolePermissionDTO.getResourceCode(), rolePermissionDTO.getScope());
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);
    result.setData(rolePermissionVOs);
    return result;
  }*/

  private RolePermissionVO getRolePermission(DefaultRole role, ResourceCode resourceCode,
                                             String scopeName, List<Integer> scopes) {
    RolePermissionVO rolePermissionVO = new RolePermissionVO();
    rolePermissionVO.setRoleName(role.getName());
    rolePermissionVO.setRoleDesc(role.getDesc());
    rolePermissionVO.setResourceName(resourceCode.getResourceName());
    rolePermissionVO.setResourceCode(resourceCode.getResourceCode());
    rolePermissionVO.setActionCode(ActionCode.READ.getCode());
    rolePermissionVO.setScopeName(scopeName);
    rolePermissionVO.setScope(scopes);
    rolePermissionVO.setDefault(false);

    return rolePermissionVO;
  }

  private void setDefault(List<RolePermissionVO> vos, String roleName, String resourceCode, List<Integer> scope) {
    for (RolePermissionVO vo : vos) {
      List<Integer> integers = vo.getScope();
      if (vo.getRoleName().equals(roleName) && vo.getResourceCode().equals(resourceCode)
              && isListEqual(integers, scope)) {
        vo.setDefault(true);
      }
    }
  }

  private boolean isListEqual(List<Integer> a, List<Integer> b) {
    for (Integer i : a) {
      if (!b.contains(i)) {
        return false;
      }
    }

    for (Integer i : b) {
      if (!a.contains(i)) {
        return false;
      }
    }

    return true;
  }

  /*@LogAround

  @RequestMapping(value = "/role-permissions/config-setup", method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> updateRolePermission(@RequestBody RolePermissionVO rolePermissionVO) {
    LOGGER.info("#######:{}", rolePermissionVO);
    SecurityModelFacade securityModelFacade = facadeFactory.getSecurityModelFacade();
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    String roleName = rolePermissionVO.getRoleName();
    String resourceCode = rolePermissionVO.getResourceCode();
    int actionCode = rolePermissionVO.getActionCode();
    List<Integer> scope = rolePermissionVO.getScope();
    BooleanDTO remoteResult = securityModelFacade.updateRolePermission(orgId, roleName, resourceCode,
            actionCode, scope, actorUserId, adminUserId);
    LOGGER.info("#######:{}", remoteResult);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);
    return result;
  }*/

}
