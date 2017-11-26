package hr.wozai.service.user.server.dao.securitymodel;

import hr.wozai.service.user.server.model.securitymodel.Role;
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
@Repository("roleDao")
public class RoleDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.RoleMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertRole(Role role) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertRole", role);
    return role.getRoleId();
  }

  public Role findRoleByPrimaryKey(long orgId, long roleId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("roleId", roleId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findRoleByPrimaryKey", params);
  }

  public Role findRoleByRoleName(long orgId, String roleName) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("roleName", roleName);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findRoleByRoleName", params);
  }

  public int updateRole(Role role) {
    int result = sqlSessionTemplate.update(BASE_PACKAGE + "updateRole", role);
    return result;
  }

  public List<Role> listRolesByOrgId(long orgId) {
    List<Role> result;
    result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listRolesByOrgId", orgId);

    return result;
  }
}
