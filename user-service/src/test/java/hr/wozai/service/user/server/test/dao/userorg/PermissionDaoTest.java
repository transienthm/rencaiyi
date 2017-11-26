package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.dao.securitymodel.PermissionDao;
import hr.wozai.service.user.server.model.securitymodel.Permission;
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
public class PermissionDaoTest extends TestBase {
  @Autowired
  PermissionDao permissionDao;

  @Test
  public void testAll() throws Exception {
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
    permissionList = permissionDao.listPermissionsByIds(Arrays.asList(permissionId));
    Assert.assertEquals(1, permissionList.size());
  }
}