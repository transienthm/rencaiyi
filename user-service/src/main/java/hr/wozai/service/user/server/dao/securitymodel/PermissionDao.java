package hr.wozai.service.user.server.dao.securitymodel;

import hr.wozai.service.user.server.model.securitymodel.Permission;
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
@Repository("permissionDao")
public class PermissionDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.PermissionMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public void batchInsertPermissions(List<Permission> permissions) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertPermissions", permissions);
  }

  public List<Permission> listPermissionsByIds(List<Long> permissionIds) {
    List<Permission> result = new ArrayList<>();
    if (permissionIds.size() == 0) {
      return result;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("permissionIds", permissionIds);
    result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listPermissionsByIds", params);
    return result;
  }

  public List<Permission> listPermissionByResourceCodeAndActionCode(String resourceCode, int actionCode) {
    Map<String, Object> params = new HashMap<>();
    params.put("resourceCode", resourceCode);
    params.put("actionCode", actionCode);

    List<Permission> result = sqlSessionTemplate.selectList(BASE_PACKAGE + "getPermissionByResourceCodeAndActionCode", params);
    return result;
  }

  /*public Permission getPermissionByResourceCodeAndActionCodeAndScope(String resourceCode, int actionCode, int scopeCode) {
    Map<String, Object> params = new HashMap<>();
    params.put("resourceCode", resourceCode);
    params.put("actionCode", actionCode);
    params.put("scopeCode", scopeCode);

    Permission  result = sqlSessionTemplate.selectOne(BASE_PACKAGE + "getPermissionByResourceCodeAndActionCodeAndScope", params);
    return result;
  }*/
}
