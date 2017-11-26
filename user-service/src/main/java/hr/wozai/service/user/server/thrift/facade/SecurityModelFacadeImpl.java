package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ScopeCode;
import hr.wozai.service.user.client.userorg.facade.SecurityModelFacade;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.model.securitymodel.Permission;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.model.securitymodel.RolePermission;
import hr.wozai.service.user.server.service.SecurityModelService;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/23
 */
@Service("permissionFacadeImpl")
public class SecurityModelFacadeImpl implements SecurityModelFacade {
  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityModelFacadeImpl.class);

  @Autowired
  private SecurityModelService securityModelService;

  @Autowired
  private TeamService teamService;

  @Override
  public BooleanDTO checkUserPermissionOnFunctionalResource(long orgId, long operatorUserId, String resourceCode,
                                                            int actionCode) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      boolean serviceResult = securityModelService.checkUserPermissionOnFunctionalResource(orgId, operatorUserId,
              resourceCode, actionCode);
      result.setData(serviceResult);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  public BooleanDTO checkUserPermissionOnRecordResource(long orgId, long actorUserId, String resourceCode, int actionCode,
                                                        int resourceType, long ownerUserId) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);
;
    try {
      boolean serviceResult;
      if (resourceCode.equals(ResourceCode.REPORT_LINE.getResourceCode()) && actionCode == ActionCode.READ.getCode()) {
         serviceResult = securityModelService.checkReportLinePermission(orgId, actorUserId, ownerUserId);
      } else {
        serviceResult = securityModelService.checkUserPermissionOnRecordResource(orgId, actorUserId,
                resourceCode, actionCode, resourceType, ownerUserId);
      }
      result.setData(serviceResult);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public RoleListDTO getRoleListDTOByUserId(long orgId, long userId, long actorUserId, long adminUserId) {
    RoleListDTO result = new RoleListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Role> roleList = securityModelService.getRolesByUserId(orgId, userId);
      List<RoleDTO> roleDTOs = new ArrayList<>();
      LOGGER.info("roleList: " + roleList);
      for (Role role : roleList) {
        LOGGER.info("role: " + role);
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        roleDTOs.add(roleDTO);
      }
      result.setRoleDTOList(roleDTOs);
    } catch (Exception e) {
      LOGGER.info("getRoleListDTOByUserId()-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /*@Override
  @LogAround
  public BooleanDTO updateRolePermission(long orgId, String roleName, String resourceCode,
                                         int actionCode, List<Integer> scopeList, long actorUserId, long adminUserId) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Role role = securityModelService.findRoleByRoleName(orgId, roleName);
      List<Permission> allPermissions = securityModelService.getPermissionByResourceCodeAndActionCode(resourceCode, actionCode);
      List<Long> oldPermissionIds = new ArrayList<>();
      List<Long> newPermissionIds = new ArrayList<>();
      for(Permission permission : allPermissions) {
        Long permissionId = permission.getPermissionId();
        oldPermissionIds.add(permissionId);
        if (scopeList.contains(permission.getScope())) {
          newPermissionIds.add(permissionId);
        }
      }
      securityModelService.updateRolePermission(orgId, role.getRoleId(), oldPermissionIds, newPermissionIds, actorUserId);
      result.setData(true);
    } catch (Exception e) {
      LOGGER.error("updateRolePermission-request():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }*/

  /*@Override
  @LogAround
  public RolePermissionListDTO listSpecificRolePermissions(long orgId) {
    RolePermissionListDTO result = new RolePermissionListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<RolePermissionDTO> rolePermissionDTOs = new ArrayList<>();
      Role staff = securityModelService.findRoleByRoleName(orgId, DefaultRole.STAFF.getName());
      Role seniorStaff = securityModelService.findRoleByRoleName(orgId, DefaultRole.SENIOR_STAFF.getName());
      int actionCode = ActionCode.READ.getCode();

      // get user-org r-p
      List<Integer> userOrgStaffScope = securityModelService.listScopeByRoleIdAndResourceCodeAndActionCode(
              orgId, staff.getRoleId(), ResourceCode.USER_ORG.getResourceCode(), actionCode);
      List<Integer> userOrgSeniorStaffScope = securityModelService.listScopeByRoleIdAndResourceCodeAndActionCode(
              orgId, seniorStaff.getRoleId(), ResourceCode.USER_ORG.getResourceCode(), actionCode);

      // get report-line r-p
      List<Integer> reportLineStaffScope = securityModelService.listScopeByRoleIdAndResourceCodeAndActionCode(
              orgId, staff.getRoleId(), ResourceCode.REPORT_LINE.getResourceCode(), actionCode);
      List<Integer> reportLineSeniorStaffScope = securityModelService.listScopeByRoleIdAndResourceCodeAndActionCode(
              orgId, seniorStaff.getRoleId(), ResourceCode.REPORT_LINE.getResourceCode(), actionCode);

      // get objective r-p
      List<Integer> objectiveStaffScope = securityModelService.listScopeByRoleIdAndResourceCodeAndActionCode(
              orgId, staff.getRoleId(), ResourceCode.OKR.getResourceCode(), actionCode);
      List<Integer> objectiveSeniorStaffScope = securityModelService.listScopeByRoleIdAndResourceCodeAndActionCode(
              orgId, seniorStaff.getRoleId(), ResourceCode.OKR.getResourceCode(), actionCode);

      rolePermissionDTOs.add(getRolePermissionWithScope(staff.getRoleName(),
              ResourceCode.USER_ORG.getResourceCode(), userOrgStaffScope));
      rolePermissionDTOs.add(getRolePermissionWithScope(seniorStaff.getRoleName(),
              ResourceCode.USER_ORG.getResourceCode(), userOrgSeniorStaffScope));
      rolePermissionDTOs.add(getRolePermissionWithScope(staff.getRoleName(),
              ResourceCode.REPORT_LINE.getResourceCode(), reportLineStaffScope));
      rolePermissionDTOs.add(getRolePermissionWithScope(seniorStaff.getRoleName(),
              ResourceCode.REPORT_LINE.getResourceCode(), reportLineSeniorStaffScope));
      rolePermissionDTOs.add(getRolePermissionWithScope(staff.getRoleName(),
              ResourceCode.OKR.getResourceCode(), objectiveStaffScope));
      rolePermissionDTOs.add(getRolePermissionWithScope(seniorStaff.getRoleName(),
              ResourceCode.OKR.getResourceCode(), objectiveSeniorStaffScope));
      result.setRolePermissions(rolePermissionDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }*/

  @Override
  @LogAround
  public RoleListDTO listRoleListDTOByOrgId(long orgId, long actorUserId, long adminUserId) {
    RoleListDTO result = new RoleListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Role> roleList = securityModelService.listRolesByOrgId(orgId);
      List<RoleDTO> roleDTOs = new ArrayList<>();
      for (Role role : roleList) {
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        roleDTOs.add(roleDTO);
      }
      result.setRoleDTOList(roleDTOs);
    } catch (Exception e) {
      LOGGER.info("listRoleListDTOByOrgId()-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /*@Override
  @LogAround
  public VoidDTO assignTeamAdminRoleToUser(long orgId, long userId, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId).getTeamId();
      Role teamAdminRole = securityModelService.findRoleByRoleName(orgId, DefaultRole.TEAM_ADMIN.getName());
      securityModelService.assignRoleToUser(orgId, userId, teamAdminRole.getRoleId(), teamId, actorUserId);
    } catch (Exception e) {
      LOGGER.info("assignTeamAdminRoleToUser()-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }*/

  /*@Override
  @LogAround
  public VoidDTO deleteTeamAdminRoleFromUser(long orgId, long userId, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId).getTeamId();
      Role teamAdminRole = securityModelService.findRoleByRoleName(orgId, DefaultRole.TEAM_ADMIN.getName());
      securityModelService.deleteUserRoleByUserIdAndRoleId(orgId, userId,
              teamAdminRole.getRoleId(), teamId, actorUserId);
    } catch (Exception e) {
      LOGGER.info("deleteTeamAdminRoleFromUser()-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }*/

  @Override
  @LogAround
  public VoidDTO assignRolesToUser(long orgId, long userId, List<Long> roleIds, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      securityModelService.assignRolesToUser(orgId, userId, roleIds, actorUserId);
    } catch (Exception e) {
      LOGGER.info("assignRolesToUser()-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public IdListDTO listOrgAdminUserIdByOrgId(long orgId, long actorUserId, long adminUserId) {
    IdListDTO result = new IdListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> userIds = securityModelService.listOrgAdminUserIdByOrgId(orgId);
      result.setIdList(userIds);
    } catch (Exception e) {
      LOGGER.info("listOrgAdminUserIdByOrgId-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO transferSuperAdminRoleBetweenUsers(long orgId, long fromUserId, long toUserId, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      securityModelService.transferSuperAdminRoleBetweenUsers(orgId, fromUserId, toUserId, actorUserId);
    } catch (Exception e) {
      LOGGER.info("transferSuperAdminRoleBetweenUsers-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }
}
