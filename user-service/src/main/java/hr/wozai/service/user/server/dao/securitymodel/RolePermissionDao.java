package hr.wozai.service.user.server.dao.securitymodel;

import hr.wozai.service.user.server.model.securitymodel.RolePermission;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/22
 */
@Repository("rolePermissionDao")
public class RolePermissionDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.RolePermissionMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public void batchInsertRolePermission(List<RolePermission> rolePermissions) {
    if (null == rolePermissions || rolePermissions.size() == 0) {
      return;
    }
    sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertRolePermission", rolePermissions);
  }

  public int deleteRolePermissoinByPrimaryKey(long orgId, long rolePermissionId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("rolePermissionId", rolePermissionId);
    params.put("actorUserId", actorUserId);
    int result =  sqlSessionTemplate.update(BASE_PACKAGE + "deleteRolePermissoinByPrimaryKey", params);
    return result;
  }

  /*public int batchDeleteRolePermissionsByPrimaryKey(long orgId, List<Long> rolePermissionIds, long actorUserId) {
    if (null == rolePermissionIds || rolePermissionIds.size() == 0) {
      return 0;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("rolePermissionIds", rolePermissionIds);
    params.put("actorUserId", actorUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE + "batchDeleteRolePermissionsByPrimaryKey", params);
    return result;
  }*/

  public int batchDeleteRolePermissionsByRoleIdAndPermissionIds(long orgId, long roleId,
                                                                List<Long> permissionIds, long actorUserId) {
    if (null == permissionIds || permissionIds.size() == 0) {
      return 0;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("roleId", roleId);
    params.put("permissionIds", permissionIds);
    params.put("actorUserId", actorUserId);

    int result = sqlSessionTemplate.update(BASE_PACKAGE + "batchDeleteRolePermissionsByRoleIdAndPermissionIds", params);
    return result;
  }

  public List<RolePermission> getRolePermissionByRoleIds(long orgId, List<Long> roleIds) {
    List<RolePermission> result = new ArrayList<>();
    if (null == roleIds || roleIds.size() == 0) {
      return result;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("roleIds", roleIds);
    result =  sqlSessionTemplate.selectList(BASE_PACKAGE + "getRolePermissionByRoleIds", params);
    return result;
  }

  public List<RolePermission> listRolePermissionsByOrgId(long orgId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listRolePermissionsByOrgId", params);
  }

  public List<RolePermission> listRolePermissionsByRoleIdAndPermissionIds(long orgId, long roleId,
                                                                          List<Long> permissionIds) {
    List<RolePermission> result = new ArrayList<>();
    if (null == permissionIds || permissionIds.size() == 0) {
      return result;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("roleId", roleId);
    params.put("permissionIds", permissionIds);

    result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listRolePermissionsByRoleIdAndPermissionIds", params);
    return result;
  }

  public List<Integer> listScopeByRoleIdAndResourceCodeAndActionCode(long orgId, long roleId, String resourceCode,
                                                                  int actionCode) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("roleId", roleId);
    params.put("resourceCode", resourceCode);
    params.put("actionCode", actionCode);

    List<Integer> result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listScopeByRoleIdAndResourceCodeAndActionCode", params);
    return result;
  }
}
