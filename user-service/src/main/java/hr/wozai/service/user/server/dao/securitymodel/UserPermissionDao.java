package hr.wozai.service.user.server.dao.securitymodel;

import hr.wozai.service.user.server.model.securitymodel.UserPermission;
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
@Repository("userPermissionDao")
public class UserPermissionDao {
  /*private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.UserPermissionMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public void batchInsertUserPermission(List<UserPermission> userPermissions) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertUserPermission", userPermissions);
  }

  public int deleteUserPermissoinByPrimaryKey(long orgId, long userPermissionId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userPermissionId", userPermissionId);
    params.put("actorUserId", actorUserId);
    int result =  sqlSessionTemplate.update(BASE_PACKAGE + "deleteUserPermissoinByPrimaryKey", params);
    return result;
  }

  public int batchDeleteUserPermissionsByPrimaryKey(long orgId, List<Long> userPermissionIds) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userPermissionIds", userPermissionIds);

    int result = sqlSessionTemplate.update(BASE_PACKAGE + "batchDeleteUserPermissionsByPrimaryKey", params);
    return result;
  }

  public List<UserPermission> getUserPermissionByUserIds(long orgId, List<Long> userIds) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userIds", userIds);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "getUserPermissionByUserIds", params);
  }*/
}
