package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.dao.securitymodel.RoleDao;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.server.model.securitymodel.Role;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/23
 */
public class RoleDaoTest extends TestBase {
  @Autowired
  RoleDao roleDao;

  @Test
  public void testAll() throws Exception {
    long orgId = 199L;
    long userId = 199L;
    String roleName = DefaultRole.STAFF.getName();
    Role role = new Role();
    role.setOrgId(orgId);
    role.setRoleName(roleName);
    role.setRoleDesc("test");
    role.setCreatedUserId(userId);

    long roleId = roleDao.insertRole(role);

    Role afterInsert = roleDao.findRoleByPrimaryKey(orgId, roleId);
    Assert.assertEquals(orgId, afterInsert.getOrgId().longValue());
    Assert.assertEquals(roleName, afterInsert.getRoleName());

    afterInsert = roleDao.findRoleByRoleName(orgId, roleName);
    Assert.assertEquals(orgId, afterInsert.getOrgId().longValue());
    Assert.assertEquals(roleName, afterInsert.getRoleName());

    afterInsert.setRoleDesc("update");
    roleDao.updateRole(afterInsert);

    Role afterUpdate = roleDao.findRoleByPrimaryKey(orgId, roleId);
    Assert.assertEquals("update", afterUpdate.getRoleDesc());

    List<Role> roleList = roleDao.listRolesByOrgId(orgId);
    Assert.assertEquals(1, roleList.size());
  }

}