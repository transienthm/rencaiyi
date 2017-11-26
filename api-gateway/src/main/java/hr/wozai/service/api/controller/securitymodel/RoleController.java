package hr.wozai.service.api.controller.securitymodel;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.userorg.dto.RoleDTO;
import hr.wozai.service.user.client.userorg.dto.RoleListDTO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.api.component.PermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.ParamName;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.securitymodel.RoleListVO;
import hr.wozai.service.api.vo.securitymodel.RoleVO;
import hr.wozai.service.api.vo.securitymodel.UserRoleVO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/3
 */
@Controller("roleController")
public class RoleController {
  private static final Logger LOGGER = LoggerFactory.getLogger(RoleController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  PermissionChecker permissionChecker;

  @LogAround
  @RequestMapping(value = "/roles/tranfer-role", method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> transferSuperAdminRoleToUser(@RequestBody UserRoleVO userRoleVO) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    if (userRoleVO.getUserId().longValue() == actorUserId) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.USER_ROLE.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.EDIT.getCode());

    VoidDTO remoteResult = facadeFactory.getSecurityModelFacade().transferSuperAdminRoleBetweenUsers(orgId, actorUserId,
            userRoleVO.getUserId(), actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/roles/set-up-roles", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> assignRolesToUser(@RequestBody UserRoleVO userRoleVO) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    /*if (userRoleVO.getUserId().longValue() == actorUserId) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }*/

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.USER_ROLE.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.EDIT.getCode());


    List<Long> roleIdList = new ArrayList<>();
    if (!CollectionUtils.isEmpty(userRoleVO.getRoleIds())) {
      for (IdVO id : userRoleVO.getRoleIds()) {
        roleIdList.add(id.getIdValue());
      }
    }
    VoidDTO remoteResult = facadeFactory.getSecurityModelFacade().assignRolesToUser(orgId, userRoleVO.getUserId(),
            roleIdList, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround
  @RequestMapping(value = "/roles/set-up-roles", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<RoleListVO> listDefaultRolesByRole(@RequestParam(value = "userId", required = false, defaultValue = "")
                                                   String encryptedUserId) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    // operator role
    RoleListDTO remoteResult = facadeFactory.getSecurityModelFacade()
            .getRoleListDTOByUserId(orgId, actorUserId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    RoleListDTO defaultRoles = facadeFactory.getSecurityModelFacade()
            .listRoleListDTOByOrgId(orgId, actorUserId, adminUserId);
    serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    boolean isSuperAdmin = false;
    for (RoleDTO roleDTO : remoteResult.getRoleDTOList()) {
      String roleName = roleDTO.getRoleName();
      if (roleName.equals(DefaultRole.SUPER_ADMIN.getName())) {
        isSuperAdmin = true;
        break;
      }
    }

    RoleListVO roleListVO = new RoleListVO();
    List<RoleVO> roleVOs;
    if (isSuperAdmin) {
      roleVOs = getDefaultRolesBySuperAdmin(defaultRoles);
    } else {
      roleVOs = getDefaultRolesByHR(defaultRoles);
    }

    if (!StringUtils.isNullOrEmpty(encryptedUserId)) {
      long userId = ParamName.getDecryptValueFromString(encryptedUserId);
      // user role
      RoleListDTO userRoles = facadeFactory.getSecurityModelFacade()
              .getRoleListDTOByUserId(orgId, userId, actorUserId, adminUserId);
      serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        throw new ServiceStatusException(serviceStatus);
      }
      List<RoleDTO> userRoleDTOs = userRoles.getRoleDTOList();
      List<Long> userRoleId = new ArrayList<>();
      for (RoleDTO roleDTO : userRoleDTOs) {
        userRoleId.add(roleDTO.getRoleId());
      }

      for (RoleVO roleVO : roleVOs) {
        if (userRoleId.contains(roleVO.getRoleId())) {
          roleVO.setFlag(true);
        } else {
          roleVO.setFlag(false);
        }
      }
    }

    roleListVO.setRoles(roleVOs);

    Result<RoleListVO> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);
    result.setData(roleListVO);
    return result;
  }

  private List<RoleVO> getDefaultRolesByHR(RoleListDTO defaultRoles) {
    List<RoleVO> result = new ArrayList<>();
    RoleVO staff = getRoleByDefaultRole(DefaultRole.STAFF, defaultRoles);
    staff.setEditable(false);
    // RoleVO senionStaff = getRoleByDefaultRole(DefaultRole.SENIOR_STAFF, defaultRoles);
    /*RoleVO hr = getRoleByDefaultRole(DefaultRole.HR, defaultRoles);
    RoleVO orgAdmin = getRoleByDefaultRole(DefaultRole.ORG_ADMIN, defaultRoles);
    orgAdmin.setEditable(false);*/

    result.add(staff);
    // result.add(senionStaff);
    // result.add(hr);
    //result.add(orgAdmin);
    return result;
  }

  private List<RoleVO> getDefaultRolesBySuperAdmin(RoleListDTO defaultRoles) {
    List<RoleVO> result = new ArrayList<>();
    RoleVO staff = getRoleByDefaultRole(DefaultRole.STAFF, defaultRoles);
    staff.setEditable(false);
    RoleVO hr = getRoleByDefaultRole(DefaultRole.HR, defaultRoles);
    RoleVO orgAdmin = getRoleByDefaultRole(DefaultRole.ORG_ADMIN, defaultRoles);
    orgAdmin.setEditable(true);
    RoleVO superAdmin = getRoleByDefaultRole(DefaultRole.SUPER_ADMIN, defaultRoles);
    superAdmin.setEditable(false);

    result.add(staff);
    result.add(hr);
    result.add(orgAdmin);
    result.add(superAdmin);
    return result;
  }

  private RoleVO getRoleByDefaultRole(DefaultRole defaultRole, RoleListDTO defaultRoles) {
    RoleVO result = new RoleVO();
    result.setRoleName(defaultRole.getName());
    result.setRoleDesc(defaultRole.getDesc());
    for (RoleDTO roleDTO : defaultRoles.getRoleDTOList()) {
      if (roleDTO.getRoleName().equals(defaultRole.getName())) {
        result.setRoleId(roleDTO.getRoleId());
      }
    }

    return result;
  }
}
