package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.LongUtils;
import hr.wozai.service.user.client.userorg.enums.*;
import hr.wozai.service.user.server.dao.securitymodel.PermissionDao;
import hr.wozai.service.user.server.model.securitymodel.Permission;
import hr.wozai.service.user.server.model.userorg.ProjectTeamMember;
import hr.wozai.service.user.server.model.userorg.TeamMember;
import hr.wozai.service.user.server.model.securitymodel.UserRole;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.dao.securitymodel.RoleDao;
import hr.wozai.service.user.server.dao.securitymodel.RolePermissionDao;
import hr.wozai.service.user.server.dao.securitymodel.UserRoleDao;
import hr.wozai.service.user.server.model.userorg.Team;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.model.securitymodel.RolePermission;
import hr.wozai.service.user.server.service.SecurityModelService;
import hr.wozai.service.user.server.service.UserService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import lombok.extern.java.Log;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/23
 */
@Service("securityModelService")
public class SecurityModelServiceImpl implements SecurityModelService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SecurityModelServiceImpl.class);
  private static final Long DEFAULT_ORG = -1L;

  @Autowired
  RoleDao roleDao;

  @Autowired
  UserRoleDao userRoleDao;

  @Autowired
  RolePermissionDao rolePermissionDao;

  @Autowired
  PermissionDao permissionDao;

  @Autowired
  TeamService teamService;

  @Autowired
  UserService userService;

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void initRoleAndRolePermission(long orgId, long actorUserId) {
    // 获取新的role id
    long orgAdmin = roleDao.insertRole(getDefaultRole(orgId, DefaultRole.ORG_ADMIN, actorUserId));
    //long teamAdmin = roleDao.insertRole(getDefaultRole(orgId, DefaultRole.TEAM_ADMIN, actorUserId));
    long hr = roleDao.insertRole(getDefaultRole(orgId, DefaultRole.HR, actorUserId));
    long staff = roleDao.insertRole(getDefaultRole(orgId, DefaultRole.STAFF, actorUserId));
    // long seniorStaff = roleDao.insertRole(getDefaultRole(orgId, DefaultRole.SENIOR_STAFF, actorUserId));
    long superAdmin = roleDao.insertRole(getDefaultRole(orgId, DefaultRole.SUPER_ADMIN, actorUserId));

    //默认的role id
    List<Role> defaultRoles = roleDao.listRolesByOrgId(DEFAULT_ORG);
    Map<Long, Long> roleIdMap = new HashMap<>();
    for (Role role : defaultRoles) {
      if (role.getRoleName().equals(DefaultRole.ORG_ADMIN.getName())) {
        roleIdMap.put(role.getRoleId(), orgAdmin);
      } else if (role.getRoleName().equals(DefaultRole.HR.getName())) {
        roleIdMap.put(role.getRoleId(), hr);
      } else if (role.getRoleName().equals(DefaultRole.STAFF.getName())) {
        roleIdMap.put(role.getRoleId(), staff);
      } else if (role.getRoleName().equals(DefaultRole.SUPER_ADMIN.getName())) {
        roleIdMap.put(role.getRoleId(), superAdmin);
      } else {
        throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
      }
    }

    List<RolePermission> defaultRolePermission = rolePermissionDao.listRolePermissionsByOrgId(DEFAULT_ORG);
    List<RolePermission> rolePermissions = new ArrayList<>();
    for (RolePermission rolePermission : defaultRolePermission) {
      rolePermission.setOrgId(orgId);
      long roleId = roleIdMap.get(rolePermission.getRoleId());
      rolePermission.setRoleId(roleId);
      rolePermission.setCreatedUserId(actorUserId);
      rolePermissions.add(rolePermission);
    }
    rolePermissionDao.batchInsertRolePermission(rolePermissions);
  }

  private Role getDefaultRole(long orgId, DefaultRole defaultRole, long actorUserId) {
    Role role = new Role();
    role.setOrgId(orgId);
    role.setRoleName(defaultRole.getName());
    role.setRoleDesc(defaultRole.getDesc());
    role.setCreatedUserId(actorUserId);
    return role;
  }

  /*@Override
  public List<Permission> listPermissionsByIds(List<Long> permissionIds) {
    return null;
  }*/

  @Override
  public List<Permission> getPermissionByResourceCodeAndActionCode(String resourceCode, int actionCode) {
    return permissionDao.listPermissionByResourceCodeAndActionCode(resourceCode, actionCode);
  }

  /*@Override
  public long insertRole(Role role) {
    return 0;
  }

  @Override
  public Role findRoleByRoleId(long orgId, long roleId) {
    return null;
  }*/

  @Override
  public Role findRoleByRoleName(long orgId, String roleName) {
    Role result = roleDao.findRoleByRoleName(orgId, roleName);
    if (result == null) {
      throw new ServiceStatusException(ServiceStatus.UO_ROLE_NOT_FOUND);
    }
    return result;
  }

  @Override
  @LogAround
  public List<Role> listRolesByOrgId(long orgId) {
    return roleDao.listRolesByOrgId(orgId);
  }

  /*@Override
  public void batchInsertRolePermission(List<RolePermission> rolePermissions) {
    rolePermissionDao.batchInsertRolePermission(rolePermissions);
  }

  @Override
  public List<RolePermission> getRolePermissionByRoleIds(long orgId, List<Long> roleIds) {
    return rolePermissionDao.getRolePermissionByRoleIds(orgId, roleIds);
  }*/

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateRolePermission(long orgId, long roleId, List<Long> olePermissionId,
                                   List<Long> newPermissionId, long actorUserId) {
    rolePermissionDao.batchDeleteRolePermissionsByRoleIdAndPermissionIds(orgId, roleId, olePermissionId, actorUserId);

    List<RolePermission> rolePermissions = new ArrayList<>();
    for(Long permissionId : newPermissionId) {
      RolePermission rolePermission = new RolePermission();
      rolePermission.setOrgId(orgId);
      rolePermission.setRoleId(roleId);
      rolePermission.setPermissionId(permissionId);
      rolePermission.setStatus(1);
      rolePermission.setCreatedUserId(actorUserId);
      rolePermissions.add(rolePermission);
    }
    rolePermissionDao.batchInsertRolePermission(rolePermissions);
  }

  /*@Override
  public List<RolePermission> listRolePermissionsByRoleIdAndPermissionIds(long orgId, long roleId, List<Long> permissionIds) {
    return rolePermissionDao.listRolePermissionsByRoleIdAndPermissionIds(orgId, roleId, permissionIds);
  }*/

  @Override
  public List<Integer> listScopeByRoleIdAndResourceCodeAndActionCode(long orgId, long roleId, String resourceCode, int actionCode) {
    return rolePermissionDao.listScopeByRoleIdAndResourceCodeAndActionCode(orgId, roleId, resourceCode, actionCode);
  }

  /*@Override
  public long insertUserRole(UserRole userRole) {
    return userRoleDao.insertUserRole(userRole);
  }
*/
  @Override
  public void deleteUserRolesByUserId(long orgId, long userId, long actorUserId) {
    userRoleDao.deleteUserRolesByUserId(orgId, userId, actorUserId);
  }

  /*@Override
  public void deleteUserRolesByRoleId(long orgId, long roleId, long actorUserId) {
    userRoleDao.deleteUserRolesByRoleId(orgId, roleId, actorUserId);
  }*/

  @Override
  public void deleteUserRoleByUserIdAndRoleId(long orgId, long userId, long roleId, long teamId, long actorUserId) {
    userRoleDao.deleteUserRoleByUserIdAndRoleId(orgId, userId, roleId, teamId, actorUserId);
  }

  @Override
  @LogAround
  public List<Long> listOrgAdminUserIdByOrgId(long orgId) {
    Role orgAdmin = roleDao.findRoleByRoleName(orgId, DefaultRole.ORG_ADMIN.getName());
    List<UserRole> userRoles = userRoleDao.listOrgAdmin(orgId, orgAdmin.getRoleId());
    List<Long> result = new ArrayList<>();
    for (UserRole userRole : userRoles) {
      result.add(userRole.getUserId());
    }
    return result;
  }

  /*@Override
  public List<UserRole> listUserRolesByUserId(long orgId, long userId) {
    return userRoleDao.listUserRolesByUserId(orgId, userId);
  }

  @Override
  public List<UserRole> listUserRolesByRoleId(long orgId, long roleId) {
    return userRoleDao.listUserRolesByRoleId(orgId, roleId);
  }*/

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void assignRoleToUser(long orgId, long userId, long roleId, long teamId, long actorUserId) {
    UserRole userRole = new UserRole();
    userRole.setUserId(userId);
    userRole.setRoleId(roleId);
    userRole.setTeamId(teamId);
    userRole.setOrgId(orgId);
    userRole.setCreatedUserId(actorUserId);
    userRoleDao.insertUserRole(userRole);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void assignRolesToUser(long orgId, long userId, List<Long> roleIds, long actorUserId) {
    List<UserRole> userRoles = getUserRolesByUserIdAndOrgId(orgId, userId);

    List<Long> deleteIds = new ArrayList<>();
    for (UserRole userRole : userRoles) {
      deleteIds.add(userRole.getUserRoleId());
    }
    if (!CollectionUtils.isEmpty(deleteIds)) {
      userRoleDao.batchDeleteUserRolesByPrimaryKey(orgId, deleteIds);
    }

    if (CollectionUtils.isEmpty(roleIds)) {
      return;
    }
    for (Long roleId : roleIds) {
      assignRoleToUser(orgId, userId, roleId, 0L, actorUserId);
    }
  }

  /**
   * 获取一个用户对应的所有roles
   *
   * @param orgId
   * @param userId
   * @return
   */
  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public List<UserRole> getUserRolesByUserIdAndOrgId(long orgId, long userId) {
    return userRoleDao.listUserRolesByUserId(orgId, userId);
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public List<Role> getRolesByUserId(long orgId, long userId) {
    List<UserRole> userRoles = getUserRolesByUserIdAndOrgId(orgId, userId);
    List<Role> result = new ArrayList<>();
    for (UserRole userRole : userRoles) {
      Role role = roleDao.findRoleByPrimaryKey(orgId, userRole.getRoleId());
      result.add(role);
    }
    return result;
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean deleteUserRoleByUserId(long orgId, long userId, long actorUserId) {
    userRoleDao.deleteUserRolesByUserId(orgId, userId, actorUserId);
    return true;
  }

  /**
   * 获取用户对数据型资源的权限,包括增删改查,需要检查组织架构的继承关系
   *
   * @param orgId
   * @param userId
   * @param resourceCode
   * @param actionCode
   * @param ownerId
   * @return
   */
  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean checkUserPermissionOnRecordResource(long orgId, long userId, String resourceCode, int actionCode,
                                                     int resourceType, long ownerId) {
    /*if (checkPermissionAsStaffRole(userId, resourceCode, actionCode, resourceType, ownerId, orgId)) {
      return true;
    } else */
    if (checkPermissionAsOtherRole(userId, resourceCode, actionCode, resourceType, ownerId, orgId)) {
      return true;
    } else {
      return false;
    }
  }

  /*public boolean checkPermissionAsStaffRole(long userId, String resourceCode, int actionCode,
                                            int resourceType, long ownerId, long orgId) {
    Role staff = roleDao.findRoleByRoleName(orgId, STAFF);
    long adminTeamId = 0L;
    List<Permission> permissions = getPermissionsFromRolePermissions(orgId,
            staff.getRoleId(), resourceCode, actionCode);
    return checkPermissionFromPermissions(userId, resourceType, ownerId, orgId, adminTeamId, permissions);
  }*/

  public boolean checkPermissionAsOtherRole(long userId, String resourceCode, int actionCode,
                                            int resourceType, long ownerId, long orgId) {
    List<UserRole> userRoles = getUserRolesByUserIdAndOrgId(orgId, userId);
    for (UserRole userRole : userRoles) {
      long adminTeamId = userRole.getTeamId();
      List<Permission> permissions = getPermissionsFromRolePermissions(orgId,
              userRole.getRoleId(), resourceCode, actionCode);
      if (checkPermissionFromPermissions(userId, resourceType, ownerId, orgId, adminTeamId, permissions)) {
        return true;
      }
    }
    return false;
  }

  private boolean checkPermissionFromPermissions(long userId, int resourceType, long ownerId,
                                                 long orgId, long adminTeamId, List<Permission> permissions) {
    if (checkPermissionWithScope(permissions, ScopeCode.ORG.getCode())) {
      return true;
    }
    if (resourceType == ResourceType.ORG.getCode()) {
      return checkPermissionWithScope(permissions, ScopeCode.ORG_BELONG.getCode());
    } else if (resourceType == ResourceType.TEAM.getCode()) {
      TeamMember teamMember = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId);
      if (teamMember == null) {
        return false;
      }
      Long teamId = teamMember.getTeamId();
      return checkPermissionAcrossTeam(teamId, ownerId, orgId, adminTeamId, permissions);
    } else if (resourceType == ResourceType.PERSON.getCode()) {
      if (userId ==  ownerId&& checkPermissionWithScope(permissions, ScopeCode.OWNER.getCode())) {
        return true;
      } else {
        return false;
      }
    } else if (resourceType == ResourceType.PROJECT_TEAM.getCode()){
      ProjectTeamMember projectTeamMember = teamService.getProjectTeamMember(orgId, ownerId, userId);
      if (projectTeamMember == null) {
        return false;
      } else {
        return checkPermissionWithScope(permissions, ScopeCode.PROJECT_TEAM.getCode());
      }
    } else {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  private boolean checkPermissionAcrossTeam(Long operatorTeamId, long ownerTeamId, long orgId,
                                            long adminTeamId, List<Permission> permissions) {
    List<Long> subTeamId = getSubTeamIds(orgId, operatorTeamId);
    List<Long> upTeamId = getUpTeamIds(orgId, operatorTeamId);
    List<Long> subTeamIdWithAdminTeam = new ArrayList<>();
    List<Long> upTeamIdWithAdminTeam = new ArrayList<>();
    if (adminTeamId != 0L) {
      subTeamIdWithAdminTeam = getSubTeamIds(orgId, adminTeamId);
      upTeamIdWithAdminTeam = getUpTeamIds(orgId, adminTeamId);
    }
    subTeamId.addAll(subTeamIdWithAdminTeam);
    upTeamId.addAll(upTeamIdWithAdminTeam);
    if (((operatorTeamId == ownerTeamId || adminTeamId == ownerTeamId)
            && checkPermissionWithScope(permissions, ScopeCode.TEAM.getCode()))
            || (subTeamId.contains(ownerTeamId)
            && checkPermissionWithScope(permissions, ScopeCode.SUBTEAM.getCode()))
            || (upTeamId.contains(ownerTeamId)
            && checkPermissionWithScope(permissions, ScopeCode.UPTEAM.getCode()))) {
      return true;
    }
    return false;
  }

  public boolean checkPermissionWithScope(List<Permission> permissions, int scopeCode) {
    for (Permission permission : permissions) {
      if (permission.getScope().intValue() == scopeCode) {
        return true;
      }
    }
    return false;
  }

  private List<Permission> getPermissionsFromRolePermissions(long orgId, long roleId,
                                                             String resouorceCode, int actionCode) {
    List<Permission> permissions;
    List<Permission> result = new ArrayList<>();
    List<RolePermission> rolePermissions = rolePermissionDao.
            getRolePermissionByRoleIds(orgId, Arrays.asList(roleId));
    List<Long> permissionIds = new ArrayList<>();
    for (RolePermission rolePermission : rolePermissions) {
      permissionIds.add(rolePermission.getPermissionId());
    }
    permissions = permissionDao.listPermissionsByIds(permissionIds);
    for (Permission permission : permissions) {
      if (permission.getResourceCode().equals(resouorceCode) && actionCode == permission.getActionCode()) {
        result.add(permission);
      }
    }
    return result;
  }

  private List<Long> getSubTeamIds(long orgId, long teamId) {
    List<Long> allTeamIds = new ArrayList<>();
    List<Team> subTeams = teamService.listSubTeams(orgId, teamId);
    for (Team team : subTeams) {
      allTeamIds.add(team.getTeamId());
    }
    return allTeamIds;
  }

  private List<Long> getUpTeamIds(long orgId, long teamId) {
    List<Long> allTeamIds = new ArrayList<>();
    List<Team> upTeams = teamService.listUpTeams(orgId, teamId);
    for (Team team : upTeams) {
      allTeamIds.add(team.getTeamId());
    }
    return allTeamIds;
  }

  /**
   * 获取用户对功能型资源的权限
   *
   * @param orgId
   * @param operatorUserId
   * @param resourceCode
   * @param actionCode
   * @return
   */
  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean checkUserPermissionOnFunctionalResource(long orgId, long operatorUserId, String resourceCode, int actionCode) {
    List<Permission> permissions = getPermissionsByUserIdAndOrgId(operatorUserId, orgId);

    return checkPermissionWithResourceAndAction(resourceCode, actionCode, permissions);
  }

  /**
   * 获取单个user所拥有的权限:包括模块可见性,数据的CRED
   *
   * @param userId
   * @param orgId
   * @return
   */
  private List<Permission> getPermissionsByUserIdAndOrgId(long userId, long orgId) {
    List<UserRole> userRoles = userRoleDao.listUserRolesByUserId(orgId, userId);
    List<Long> roleIds = new ArrayList<>();
    for (UserRole userRole : userRoles) {
      roleIds.add(userRole.getRoleId());
    }
    /*Role staff = roleDao.findRoleByRoleName(orgId, STAFF);
    roleIds.add(staff.getRoleId());*/

    List<RolePermission> rolePermissions = rolePermissionDao.getRolePermissionByRoleIds(orgId, roleIds);
    // Todo: merge user permission

    Set<Long> permissionIdSet = new HashSet<>();
    for (RolePermission rolePermission : rolePermissions) {
      if (rolePermission.getStatus() == 1) {
        permissionIdSet.add(rolePermission.getPermissionId());
      }
    }
    List<Long> permissionIds = new ArrayList<>();
    permissionIds.addAll(permissionIdSet);
    List<Permission> permissions = permissionDao.listPermissionsByIds(permissionIds);

    return permissions;
  }

  private boolean checkPermissionWithResourceAndAction(String resourceCode, int actionCode,
                                                       List<Permission> permissions) {
    for (Permission permission : permissions) {
      if (permission.getResourceCode().equals(resourceCode) &&
              permission.getActionCode() == actionCode) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean checkReportLinePermission(long orgId, long actorUserId, long ownerUserId) {
    List<Permission> permissions = getPermissionsByUserIdAndOrgId(actorUserId, orgId);
    boolean isSubordinate = false;
    List<Long> reportees = userService.listReporteesByUserIdAndOrgId(orgId, actorUserId);
    if (reportees.contains(ownerUserId)) {
      isSubordinate = true;
    }
    List<Permission> reportLinePermissions = new ArrayList<>();
    for (Permission permission : permissions) {
      if (permission.getResourceCode().equals(ResourceCode.REPORT_LINE.getResourceCode())
              && permission.getActionCode().intValue() == (ActionCode.READ.getCode().intValue())) {
        reportLinePermissions.add(permission);
      }
    }

    boolean isSelf = (actorUserId == ownerUserId)?true:false;

    if (checkPermissionWithScope(reportLinePermissions, ScopeCode.ORG.getCode())) {
      return true;
    }else if (isSubordinate && checkPermissionWithScope(reportLinePermissions, ScopeCode.SUBORDINATE.getCode())) {
      return true;
    } else if (isSelf && checkPermissionWithScope(reportLinePermissions, ScopeCode.OWNER.getCode())){
      return true;
    } else {
      return false;
    }
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void transferSuperAdminRoleBetweenUsers(long orgId, long fromUserId, long toUserId, long actorUserId) {
    Role superAdmin = this.findRoleByRoleName(orgId, DefaultRole.SUPER_ADMIN.getName());

    this.deleteUserRoleByUserIdAndRoleId(orgId, fromUserId, superAdmin.getRoleId(), 0L, actorUserId);
    this.assignRoleToUser(orgId, toUserId, superAdmin.getRoleId(), 0L, actorUserId);
  }
}
