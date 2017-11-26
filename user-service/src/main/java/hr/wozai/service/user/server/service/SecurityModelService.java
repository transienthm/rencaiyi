package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.securitymodel.Permission;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.model.securitymodel.RolePermission;
import hr.wozai.service.user.server.model.securitymodel.UserRole;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/23
 */
public interface SecurityModelService {
  void initRoleAndRolePermission(long orgId, long actorUserId);

  //permission
  /*List<Permission> listPermissionsByIds(List<Long> permissionIds);*/

  List<Permission> getPermissionByResourceCodeAndActionCode(String resourceCode, int actionCode);

  // role
  /*long insertRole(Role role);

  Role findRoleByRoleId(long orgId, long roleId);*/

  Role findRoleByRoleName(long orgId, String roleName);

  List<Role> listRolesByOrgId(long orgId);

  //role permission
  /*void batchInsertRolePermission(List<RolePermission> rolePermissions);

  List<RolePermission> getRolePermissionByRoleIds(long orgId, List<Long> roleIds);*/

  void updateRolePermission(long orgId, long roleId, List<Long> olePermissionId,
                            List<Long> newPermissionId, long actorUserId);

  /*List<RolePermission> listRolePermissionsByRoleIdAndPermissionIds(long orgId, long roleId, List<Long> permissionIds);*/

  List<Integer> listScopeByRoleIdAndResourceCodeAndActionCode(long orgId, long roleId, String resourceCode, int actionCode);

  //user role
  /*long insertUserRole(UserRole userRole);*/

  void deleteUserRolesByUserId(long orgId, long userId, long actorUserId);

  /*void deleteUserRolesByRoleId(long orgId, long roleId, long actorUserId);*/

  void deleteUserRoleByUserIdAndRoleId(long orgId, long userId, long roleId, long teamId, long actorUserId);

  /*List<UserRole> listUserRolesByUserId(long orgId, long userId);

  List<UserRole> listUserRolesByRoleId(long orgId, long roleId);*/

  List<Long> listOrgAdminUserIdByOrgId(long orgId);

  // others
  void assignRoleToUser(long orgId, long userId, long roleId, long teamId, long actorUserId);

  /**
   * assign roles to user, except teamAdmin role
   * @param orgId
   * @param userId
   * @param roleIds
   * @param actorUserId
   */
  void assignRolesToUser(long orgId, long userId, List<Long> roleIds, long actorUserId);

  List<UserRole> getUserRolesByUserIdAndOrgId(long orgId, long userId);

  List<Role> getRolesByUserId(long orgId, long userId);

  boolean deleteUserRoleByUserId(long orgId, long userId, long actorUserId);

  boolean checkUserPermissionOnRecordResource(long orgId, long operatorUserId, String resourceCode, int actionCode,
                                              int resourceType, long ownerId);

  boolean checkUserPermissionOnFunctionalResource(long orgId, long operatorUserId, String resourceCode, int actionCode);

  boolean checkReportLinePermission(long orgId, long actorUserId, long ownerUserId);

  void transferSuperAdminRoleBetweenUsers(long orgId, long fromUserId, long toUserId, long actorUserId);

}
