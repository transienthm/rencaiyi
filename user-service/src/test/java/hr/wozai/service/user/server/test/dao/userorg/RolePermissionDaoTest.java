package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.dao.securitymodel.PermissionDao;
import hr.wozai.service.user.server.dao.securitymodel.RolePermissionDao;
import hr.wozai.service.user.server.model.securitymodel.Permission;
import hr.wozai.service.user.server.model.securitymodel.RolePermission;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/23
 */
public class RolePermissionDaoTest extends TestBase {
  @Autowired
  RolePermissionDao rolePermissionDao;

  @Autowired
  PermissionDao permissionDao;

  long orgId = 199L;
  long roleId = 299L;
  long permissionId = 399L;
  long userId = 499L;

  @Test
  public void testAll() throws Exception {
    RolePermission rolePermission = new RolePermission();
    rolePermission.setOrgId(orgId);
    rolePermission.setRoleId(roleId);
    rolePermission.setPermissionId(permissionId);
    rolePermission.setStatus(1);
    rolePermission.setCreatedUserId(userId);

    rolePermissionDao.batchInsertRolePermission(Arrays.asList(rolePermission));

    List<RolePermission> rolePermissionList = rolePermissionDao.getRolePermissionByRoleIds(orgId, Arrays.asList(roleId));
    Assert.assertEquals(1, rolePermissionList.size());

    rolePermissionList = rolePermissionDao.listRolePermissionsByOrgId(orgId);
    System.out.println(rolePermissionList);
    Assert.assertEquals(1, rolePermissionList.size());

    long rolePermissionId = rolePermissionList.get(0).getRolePermissionId();
    rolePermissionDao.deleteRolePermissoinByPrimaryKey(orgId, rolePermissionId, userId);


    rolePermissionList = rolePermissionDao.getRolePermissionByRoleIds(orgId, Arrays.asList(roleId));
    Assert.assertEquals(0, rolePermissionList.size());

    rolePermissionDao.batchDeleteRolePermissionsByRoleIdAndPermissionIds(orgId, roleId, Arrays.asList(permissionId), userId);

    rolePermissionList = rolePermissionDao.listRolePermissionsByRoleIdAndPermissionIds(orgId, roleId, Arrays.asList(permissionId));
    Assert.assertEquals(0, rolePermissionList.size());
  }

  @Test
  public void test() throws Exception {
    String resourceCode = "199";
    int actionCode = 1;
    int scope = 1;
    Permission permission = new Permission();
    permission.setResourceName("test");
    permission.setResourceCode(resourceCode);
    permission.setResourceType(1);
    permission.setActionCode(actionCode);
    permission.setScope(scope);
    permission.setCreatedUserId(199L);

    permissionDao.batchInsertPermissions(Arrays.asList(permission));

    List<Permission> permissionList = permissionDao.listPermissionByResourceCodeAndActionCode(resourceCode, actionCode);
    Assert.assertEquals(1, permissionList.size());

    long permissionId = permissionList.get(0).getPermissionId();
    RolePermission rolePermission = new RolePermission();
    rolePermission.setOrgId(orgId);
    rolePermission.setRoleId(roleId);
    rolePermission.setPermissionId(permissionId);
    rolePermission.setStatus(1);
    rolePermission.setCreatedUserId(userId);

    rolePermissionDao.batchInsertRolePermission(Arrays.asList(rolePermission));

    List<Integer> result = rolePermissionDao.listScopeByRoleIdAndResourceCodeAndActionCode(orgId, roleId,
            resourceCode, actionCode);
    Assert.assertEquals(1, result.size());
  }
}