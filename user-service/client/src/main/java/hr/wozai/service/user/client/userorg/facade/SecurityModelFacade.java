package hr.wozai.service.user.client.userorg.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.user.client.userorg.dto.IdListDTO;
import hr.wozai.service.user.client.userorg.dto.RoleListDTO;
import hr.wozai.service.user.client.userorg.dto.RolePermissionDTO;
import hr.wozai.service.user.client.userorg.dto.RolePermissionListDTO;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/23
 */
@ThriftService
public interface SecurityModelFacade {
  // ###########################security-model相关接口##############################################
  @ThriftMethod
  public BooleanDTO checkUserPermissionOnFunctionalResource(long orgId, long operatorUserId, String resourceCode,
                                                            int actionCode);

  @ThriftMethod
  public BooleanDTO checkUserPermissionOnRecordResource(long orgId, long actorUserId, String resourceCode, int actionCode,
                                                        int resourceType, long ownerUserId);

  @ThriftMethod
  public RoleListDTO getRoleListDTOByUserId(long orgId, long userId, long actorUserId, long adminUserId);

 /* @ThriftMethod
  public BooleanDTO updateRolePermission(long orgId, String roleName, String resourceCode, int actionCode,
                                         List<Integer> scopeList, long actorUserId, long adminUserId);*/

  /**
   * 展示:user-org, report-line, objective的权限范围
   * @return
   */
  /*@ThriftMethod
  public RolePermissionListDTO listSpecificRolePermissions(long orgId);*/

  @ThriftMethod
  public RoleListDTO listRoleListDTOByOrgId(long orgId, long actorUserId, long adminUserId);

  /**
   * 设置该user为本team的team admin
   * @param orgId
   * @param userId
   * @param actorUserId
   * @return
   */
  /*@ThriftMethod
  public VoidDTO assignTeamAdminRoleToUser(long orgId, long userId, long actorUserId, long adminUserId);*/

  /*@ThriftMethod
  VoidDTO deleteTeamAdminRoleFromUser(long orgId, long userId, long actorUserId, long adminUserId);*/

  /**
   * 1. delete previous roles
   * 2. assign new roles
   * @param orgId
   * @param userId
   * @param roleIds
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO assignRolesToUser(long orgId, long userId, List<Long> roleIds, long actorUserId, long adminUserId);

  @ThriftMethod
  IdListDTO listOrgAdminUserIdByOrgId(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO transferSuperAdminRoleBetweenUsers(long orgId, long fromUserId, long toUserId, long actorUserId, long adminUserId);
}