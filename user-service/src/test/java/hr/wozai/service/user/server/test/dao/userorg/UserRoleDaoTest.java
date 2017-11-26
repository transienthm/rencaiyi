package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.dao.securitymodel.UserRoleDao;
import hr.wozai.service.user.server.model.securitymodel.UserRole;
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
public class UserRoleDaoTest extends TestBase {
  @Autowired
  UserRoleDao userRoleDao;

  @Test
  public void testUpdateUserRole() throws Exception {
    long orgId = 199L;
    long userId = 199L;
    long roleId = 199L;
    UserRole userRole = new UserRole();
    userRole.setOrgId(orgId);
    userRole.setUserId(userId);
    userRole.setRoleId(roleId);
    userRole.setTeamId(0l);
    userRole.setCreatedUserId(userId);

    long userRoleId = userRoleDao.insertUserRole(userRole);

    List<UserRole> userRoles = userRoleDao.listUserRolesByUserId(orgId, userId);
   Assert.assertEquals(1, userRoles.size());

    userRoles = userRoleDao.listUserRolesByRoleId(orgId, roleId);
    Assert.assertEquals(1, userRoles.size());

    int result = userRoleDao.batchDeleteUserRolesByPrimaryKey(orgId, Arrays.asList(userRoleId));
    Assert.assertEquals(1, result);

    result = userRoleDao.deleteUserRoleByUserIdAndRoleId(orgId, userId, roleId, 0L, userId);
    Assert.assertEquals(1, result);

    result = userRoleDao.deleteUserRolesByRoleId(orgId, roleId, userId);
    Assert.assertEquals(1, result);

    userRoleDao.deleteUserRolesByUserId(orgId, userId, 0L);
    userRoles = userRoleDao.listUserRolesByUserId(orgId, userId);
    Assert.assertEquals(0, userRoles.size());
  }

  @Test
  public void testTeamAdmin() throws Exception {
    long orgId = 199L;
    long userId = 299L;
    long roleId = 399L;
    long teamId = 499L;
    UserRole userRole = new UserRole();
    userRole.setOrgId(orgId);
    userRole.setUserId(userId);
    userRole.setRoleId(roleId);
    userRole.setTeamId(teamId);
    userRole.setCreatedUserId(userId);

    long userRoleId = userRoleDao.insertUserRole(userRole);
    Assert.assertNotNull(userRoleId);

    List<Long> teamAdmins = userRoleDao.listTeamAdminsByOrgIdAndTeamId(orgId, roleId, teamId);
    Assert.assertEquals(1, teamAdmins.size());
    Assert.assertEquals(userId, teamAdmins.get(0).longValue());
  }

  @Test
  public void testOrgAdmin() throws Exception {
    long orgId = 199L;
    long userId = 299L;
    long roleId = 399L;
    long teamId = 0L;

    UserRole userRole = new UserRole();
    userRole.setOrgId(orgId);
    userRole.setUserId(userId);
    userRole.setRoleId(roleId);
    userRole.setTeamId(teamId);
    userRole.setCreatedUserId(userId);

    long userRoleId = userRoleDao.insertUserRole(userRole);
    Assert.assertNotNull(userRoleId);

    List<UserRole> orgAdmin = userRoleDao.listOrgAdmin(orgId, roleId);
    Assert.assertEquals(1, orgAdmin.size());
  }
}