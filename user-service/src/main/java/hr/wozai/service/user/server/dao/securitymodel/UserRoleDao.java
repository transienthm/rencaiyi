package hr.wozai.service.user.server.dao.securitymodel;

import hr.wozai.service.user.server.model.securitymodel.UserRole;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/22
 */
@Repository("userRoleDao")
public class UserRoleDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.UserRoleMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertUserRole(UserRole userRole) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertUserRole", userRole);
    return userRole.getUserRoleId();
  }

  public int batchDeleteUserRolesByPrimaryKey(long orgId, List<Long> userRoleIds) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userRoleIds", userRoleIds);
    int result = sqlSessionTemplate.update(BASE_PACKAGE + "batchDeleteUserRolesByPrimaryKey", params);
    return result;
  }

  public int deleteUserRolesByUserId(long orgId, long userId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    params.put("actorUserId", actorUserId);

    int result = sqlSessionTemplate.update(BASE_PACKAGE + "deleteUserRolesByUserId", params);
    return result;
  }

  public int deleteUserRolesByRoleId(long orgId, long roleId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("roleId", roleId);
    params.put("actorUserId", actorUserId);

    int result = sqlSessionTemplate.update(BASE_PACKAGE + "deleteUserRolesByRoleId", params);
    return result;
  }

  public int deleteUserRoleByUserIdAndRoleId(long orgId, long userId, long roleId, long teamId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    params.put("roleId", roleId);
    params.put("teamId", teamId);
    params.put("actorUserId", actorUserId);

    int result = sqlSessionTemplate.update(BASE_PACKAGE + "deleteUserRoleByUserIdAndRoleId", params);
    return result;
  }

  public List<UserRole> listUserRolesByUserId(long orgId, long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("orgId", orgId);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listUserRolesByUserIdAndOrgId", params);
  }

  public List<UserRole> listUserRolesByRoleId(long orgId, long roleId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("roleId", roleId);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listUserRolesByRoleIdAndOrgId", params);
  }

  public List<UserRole> listOrgAdmin(long orgId, long roleId) {
    Map<String, Object> params = new HashMap<>();
    params.put("roleId", roleId);
    params.put("orgId", orgId);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listOrgAdmin", params);
  }

  public List<Long> listTeamAdminsByOrgIdAndTeamId(long orgId, long roleId, long teamId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("roleId", roleId);
    params.put("teamId", teamId);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listTeamAdminsByOrgIdAndTeamId", params);
  }
}
