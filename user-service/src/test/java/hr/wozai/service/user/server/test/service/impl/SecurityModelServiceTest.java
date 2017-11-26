package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.user.client.userorg.dto.RoleDTO;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.user.server.model.userorg.ProjectTeam;
import hr.wozai.service.user.server.model.userorg.ProjectTeamMember;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.dao.securitymodel.PermissionDao;
import hr.wozai.service.user.server.dao.securitymodel.RoleDao;
import hr.wozai.service.user.server.dao.securitymodel.RolePermissionDao;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.model.securitymodel.RolePermission;
import hr.wozai.service.user.server.model.userorg.Team;
import hr.wozai.service.user.server.model.userorg.TeamMember;
import hr.wozai.service.user.server.model.securitymodel.UserRole;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.server.service.SecurityModelService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/25
 */
public class SecurityModelServiceTest extends TestBase {
  private static Logger LOGGER = LoggerFactory.getLogger(SecurityModelServiceTest.class);

  @Autowired
  SecurityModelService securityModelService;

  @Autowired
  RoleDao roleDao;

  @Autowired
  RolePermissionDao rolePermissionDao;

  @Autowired
  TeamService teamService;

  long userId1 = 200L;
  long userId2 = 201L;
  long userId3 = 202L;

  long teamId1 = 0L;
  long teamId2 = 0L;
  long teamId3 = 0L;

  long projectTeamId = 199L;

  long orgId = -1L;

  @Before
  public void setUp() throws Exception {
    // 1->2,1->3,3->4
    Team team1 = new Team();
    team1.setOrgId(orgId);
    team1.setTeamName("team1");
    team1.setParentTeamId(0L);
    team1.setCreatedUserId(userId1);
    team1.setLastModifiedUserId(userId1);
    teamId1 = teamService.addTeam(team1);

    Team son1 = new Team();
    son1.setOrgId(orgId);
    son1.setTeamName("team2");
    son1.setParentTeamId(teamId1);
    son1.setCreatedUserId(userId1);
    son1.setLastModifiedUserId(userId1);
    teamId2 = teamService.addTeam(son1);

    Team son2 = new Team();
    son2.setOrgId(orgId);
    son2.setTeamName("team3");
    son2.setParentTeamId(teamId1);
    son2.setCreatedUserId(userId1);
    son2.setLastModifiedUserId(userId1);
    teamId3 = teamService.addTeam(son2);

    TeamMember teamMember = new TeamMember();
    teamMember.setUserId(userId1);
    teamMember.setTeamId(teamId1);
    teamMember.setOrgId(orgId);
    teamMember.setCreatedUserId(userId1);
    teamMember.setLastModifiedUserId(userId1);

    TeamMember teamMember1 = new TeamMember();
    teamMember1.setUserId(userId2);
    teamMember1.setTeamId(teamId2);
    teamMember1.setOrgId(orgId);
    teamMember1.setCreatedUserId(userId1);
    teamMember1.setLastModifiedUserId(userId1);

    TeamMember teamMember2 = new TeamMember();
    teamMember2.setUserId(userId3);
    teamMember2.setTeamId(teamId3);
    teamMember2.setOrgId(orgId);
    teamMember2.setCreatedUserId(userId1);
    teamMember2.setLastModifiedUserId(userId1);

    teamService.batchAddTeamMember(Arrays.asList(teamMember, teamMember1, teamMember2));

    ProjectTeamMember projectTeamMember = new ProjectTeamMember();
    projectTeamMember.setOrgId(orgId);
    projectTeamMember.setProjectTeamId(projectTeamId);
    projectTeamMember.setUserId(userId2);
    projectTeamMember.setCreatedUserId(userId2);
    teamService.batchInsertProjectTeamMember(Arrays.asList(projectTeamMember));

    Long orgAdmin = roleDao.findRoleByRoleName(orgId, DefaultRole.ORG_ADMIN.getName()).getRoleId();
    Long hr = roleDao.findRoleByRoleName(orgId, DefaultRole.HR.getName()).getRoleId();
    Long staff = roleDao.findRoleByRoleName(orgId, DefaultRole.STAFF.getName()).getRoleId();
    Long superAdmin = roleDao.findRoleByRoleName(orgId, DefaultRole.SUPER_ADMIN.getName()).getRoleId();

    securityModelService.assignRolesToUser(orgId, userId1, Arrays.asList(superAdmin, orgAdmin, hr, staff), -1L);
    securityModelService.assignRolesToUser(orgId, userId2, Arrays.asList(hr, staff), -1L);
    securityModelService.assignRolesToUser(orgId, userId3, Arrays.asList(staff), -1L);
  }

  @Test
  public void testAssignRoleToUser() throws Exception {
    long orgId = 100L;
    long userId = 100L;
    long roleId = 1L;
    long teamId = 0L;

    securityModelService.assignRoleToUser(orgId, userId, roleId, teamId, 0L);

    List<UserRole> userRoleList = securityModelService.getUserRolesByUserIdAndOrgId(orgId, userId);
    Assert.assertEquals(1, userRoleList.size());
    UserRole userRole = userRoleList.get(0);
    Assert.assertEquals(roleId, userRole.getRoleId().longValue());
  }

  @Test
  public void testCheckPermissionOnRecordResourceWithOrgAdminRole() throws Exception {
    long roleId = 1L;
    securityModelService.assignRoleToUser(orgId, userId1, roleId, 0L, 0L);

    String resourceCode = ResourceCode.NEWS_FEED.getResourceCode();
    int actionCode = ActionCode.DELETE.getCode();
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1, resourceCode,
            actionCode, 3, userId2));

    resourceCode = ResourceCode.NEWS_FEED_COMMENT.getResourceCode();
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1, resourceCode,
            actionCode, 3, userId2));
  }

  @Test
  public void testCheckPermissionOnFunctionalResource() throws Exception {
    // userId1 is orgAdmin
    securityModelService.assignRoleToUser(orgId, userId1, 1L, 0L, 0L);
    String resourceCode = ResourceCode.SYSTEM_ADMIN.getResourceCode();
    int actionCode = ActionCode.VISIBLE.getCode();
    Assert.assertTrue(securityModelService.checkUserPermissionOnFunctionalResource(orgId, userId1,
            resourceCode, actionCode));
  }

  @Test
  public void testCheckMenuVisibleToOperator() throws Exception {
    securityModelService.checkUserPermissionOnRecordResource(99L, 57L, "002", 4, 3, 57L);
  }

  @Test
  public void testOkr() throws Exception {
    String resourceCode = ResourceCode.OKR.getResourceCode();

    //操作公司资源
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.ORG.getCode(), orgId));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.ORG.getCode(), orgId));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.EDIT.getCode(), ResourceType.ORG.getCode(), orgId));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.READ.getCode(), ResourceType.ORG.getCode(), orgId));

    //操作本人team资源
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.TEAM.getCode(), teamId2));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.TEAM.getCode(), teamId2));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.EDIT.getCode(), ResourceType.TEAM.getCode(), teamId2));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.READ.getCode(), ResourceType.TEAM.getCode(), teamId2));

    //操作本人
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.PERSON.getCode(), userId3));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.PERSON.getCode(), userId3));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.EDIT.getCode(), ResourceType.PERSON.getCode(), userId3));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.READ.getCode(), ResourceType.PERSON.getCode(), userId3));

    //操作其他team
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.TEAM.getCode(), teamId3));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.TEAM.getCode(), teamId3));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.EDIT.getCode(), ResourceType.TEAM.getCode(), teamId3));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.READ.getCode(), ResourceType.TEAM.getCode(), teamId3));

    //操作其他team里的人
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.PERSON.getCode(), userId3));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.PERSON.getCode(), userId3));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.EDIT.getCode(), ResourceType.PERSON.getCode(), userId3));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.READ.getCode(), ResourceType.PERSON.getCode(), userId3));

    //操作本人项目组
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.PROJECT_TEAM.getCode(), projectTeamId));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.PROJECT_TEAM.getCode(), projectTeamId));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.EDIT.getCode(), ResourceType.PROJECT_TEAM.getCode(), projectTeamId));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.READ.getCode(), ResourceType.PROJECT_TEAM.getCode(), projectTeamId));

    //操作其他项目组
    projectTeamId = 499L;
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.PROJECT_TEAM.getCode(), projectTeamId));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.PROJECT_TEAM.getCode(), projectTeamId));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.EDIT.getCode(), ResourceType.PROJECT_TEAM.getCode(), projectTeamId));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId2,
            resourceCode, ActionCode.READ.getCode(), ResourceType.PROJECT_TEAM.getCode(), projectTeamId));
  }

  @Test
  public void testOkrPeriod() throws Exception {
    String resourceCode = ResourceCode.OKR_PERIOD.getResourceCode();

    //操作公司资源
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.ORG.getCode(), orgId));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.PERSON.getCode(), userId2));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.TEAM.getCode(), teamId2));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.ORG.getCode(), orgId));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.PERSON.getCode(), userId2));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.TEAM.getCode(), teamId2));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.READ.getCode(), ResourceType.ORG.getCode(), orgId));

    //普通员工
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.PERSON.getCode(), userId3));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.TEAM.getCode(), teamId3));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.PERSON.getCode(), userId3));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.TEAM.getCode(), teamId3));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.READ.getCode(), ResourceType.ORG.getCode(), orgId));

  }

  @Test
  public void testOrgAndUserRole() throws Exception {
    String resourceCode = ResourceCode.ORG.getResourceCode();
    //org管理员
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.ORG.getCode(), orgId));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.READ.getCode(), ResourceType.ORG.getCode(), orgId));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.EDIT.getCode(), ResourceType.ORG.getCode(), orgId));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.ORG.getCode(), orgId));

    //staff
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.ORG.getCode(), orgId));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.READ.getCode(), ResourceType.ORG.getCode(), orgId));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.EDIT.getCode(), ResourceType.ORG.getCode(), orgId));
    Assert.assertFalse(securityModelService.checkUserPermissionOnRecordResource(orgId, userId3,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.ORG.getCode(), orgId));

    //HR操作user_role
    resourceCode = ResourceCode.USER_ROLE.getResourceCode();
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.CREATE.getCode(), ResourceType.PERSON.getCode(), userId2));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.READ.getCode(), ResourceType.PERSON.getCode(), userId2));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.EDIT.getCode(), ResourceType.PERSON.getCode(), userId2));
    Assert.assertTrue(securityModelService.checkUserPermissionOnRecordResource(orgId, userId1,
            resourceCode, ActionCode.DELETE.getCode(), ResourceType.PERSON.getCode(), userId2));
  }

  @Test
  public void testInitRolePermission() throws Exception {
    long orgId = 394L;
    securityModelService.initRoleAndRolePermission(orgId, -1L);

    List<Role> roles = securityModelService.listRolesByOrgId(orgId);
    System.out.println("roles:" + roles);

    System.out.println(roleDao.listRolesByOrgId(orgId).size());
    System.out.println(rolePermissionDao.listRolePermissionsByOrgId(orgId).size());
  }

  @Test
  public void testCheckReportLinePermission() throws Exception {
    Assert.assertTrue(securityModelService.checkReportLinePermission(orgId, userId1, userId1));
  }
}