package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.service.SecurityModelService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/6/15
 */
public class SecurityModelServiceImplTest extends TestBase {
  @Autowired
  SecurityModelService securityModelService;

  private long orgId = 1999L;
  private long userId = 1999L;
  private long teamId = 1999L;
  private long toUserId = 2999L;

  @Test
  public void testAll() throws Exception {
    securityModelService.initRoleAndRolePermission(orgId, userId);

    Assert.assertNotEquals(0, securityModelService.getPermissionByResourceCodeAndActionCode(
            ResourceCode.NEWS_FEED.getResourceCode(),
            ActionCode.CREATE.getCode()).size());

    Role staff = securityModelService.findRoleByRoleName(orgId, DefaultRole.STAFF.getName());
    Assert.assertEquals(staff.getRoleName(), DefaultRole.STAFF.getName());

    Assert.assertNotEquals(0, securityModelService.listRolesByOrgId(orgId).size());

    Assert.assertNotEquals(0, securityModelService.listScopeByRoleIdAndResourceCodeAndActionCode(orgId, staff.getRoleId(),
            ResourceCode.NEWS_FEED.getResourceCode(), ActionCode.CREATE.getCode()).size());

    securityModelService.assignRoleToUser(orgId, userId, staff.getRoleId(), 0, userId);
    Assert.assertEquals(1, securityModelService.getRolesByUserId(orgId, userId).size());
    Assert.assertEquals(1, securityModelService.getUserRolesByUserIdAndOrgId(orgId, userId).size());

    securityModelService.deleteUserRoleByUserId(orgId, userId, -1L);

    Assert.assertEquals(0, securityModelService.getRolesByUserId(orgId, userId).size());

    securityModelService.assignRolesToUser(orgId, userId, Arrays.asList(staff.getRoleId()), -1L);
    Assert.assertEquals(1, securityModelService.getRolesByUserId(orgId, userId).size());

    securityModelService.deleteUserRoleByUserIdAndRoleId(orgId, userId, staff.getRoleId(), 0, -1L);
    Assert.assertEquals(0, securityModelService.getRolesByUserId(orgId, userId).size());

    securityModelService.assignRoleToUser(orgId, userId, staff.getRoleId(), 0, userId);
    Assert.assertEquals(1, securityModelService.getRolesByUserId(orgId, userId).size());

    Role hr = securityModelService.findRoleByRoleName(orgId, DefaultRole.HR.getName());
    securityModelService.assignRolesToUser(orgId, userId, Arrays.asList(hr.getRoleId()), -1L);
    Assert.assertEquals(1, securityModelService.getRolesByUserId(orgId, userId).size());

    securityModelService.updateRolePermission(orgId, staff.getRoleId(),
            Arrays.asList(1L), Arrays.asList(2L), -1L);

    securityModelService.deleteUserRolesByUserId(orgId, userId, -1L);

    Assert.assertEquals(0, securityModelService.listOrgAdminUserIdByOrgId(orgId).size());

    Role superAdmin = securityModelService.findRoleByRoleName(orgId, DefaultRole.SUPER_ADMIN.getName());
    securityModelService.assignRolesToUser(orgId, userId, Arrays.asList(superAdmin.getRoleId()), -1L);
    Assert.assertEquals(1, securityModelService.getRolesByUserId(orgId, userId).size());

    securityModelService.transferSuperAdminRoleBetweenUsers(orgId, userId, toUserId, -1L);
    List<Role> roleList = securityModelService.getRolesByUserId(orgId, toUserId);
    Assert.assertEquals(1, roleList.size());
    Assert.assertEquals(DefaultRole.SUPER_ADMIN.getName(), roleList.get(0).getRoleName());
  }
}