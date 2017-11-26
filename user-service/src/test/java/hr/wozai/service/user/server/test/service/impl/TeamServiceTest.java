package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.model.userorg.*;
import hr.wozai.service.user.server.service.OrgService;
import hr.wozai.service.user.server.service.UserEmploymentService;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.service.SecurityModelService;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.test.utils.AopTargetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/17
 */
public class TeamServiceTest extends TestBase {
  private static final Logger LOGGER = LoggerFactory.getLogger(TeamServiceTest.class);

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  TeamService teamService;

  @Autowired
  SecurityModelService securityModelService;

  @Mock
  OrgService spyOrgService;

  @Autowired
  OrgService orgService;

  @Mock
  UserEmploymentService spyUserEmploymentService;

  @Autowired
  UserEmploymentService userEmploymentService;

  private Team team;
  private TeamMember teamMember;
  private long userId = 1000l;
  private long orgId = 1000l;

  @Before
  public void setUp() throws Exception {
    team = new Team();
    team.setTeamName("test");
    team.setOrgId(orgId);
    team.setParentTeamId(0l);
    team.setCreatedUserId(userId);
    team.setLastModifiedUserId(userId);

    teamMember = new TeamMember();
    teamMember.setUserId(userId);
    teamMember.setTeamId(1l);
    teamMember.setOrgId(orgId);
    teamMember.setCreatedUserId(userId);
    teamMember.setLastModifiedUserId(userId);

    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(teamService), "orgService", spyOrgService);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(teamService), "userEmploymentService", spyUserEmploymentService);
  }

  @Test
  public void testAddTeam() throws Exception {
    long teamId = teamService.addTeam(team);
    LOGGER.info("after add team, teamId is {}", teamId);
    Assert.assertNotEquals(0l, teamId);

    List<Team> teams = teamService.listAllTeams(orgId);
    Assert.assertEquals(1, teams.size());
  }

  @Test
  public void testAddTeamWithException() {
    teamService.addTeam(team);

    // orgId is 0
    team = new Team();
    team.setTeamName("test2");
    team.setOrgId(0l);
    team.setParentTeamId(0l);
    team.setCreatedUserId(-1L);
    team.setLastModifiedUserId(-1L);
    thrown.expect(ServiceStatusException.class);
    teamService.addTeam(team);

    // parentTeamId < 0
    team = new Team();
    team.setTeamName("test2");
    team.setOrgId(orgId);
    team.setParentTeamId(-1l);
    team.setCreatedUserId(-1L);
    team.setLastModifiedUserId(-1L);
    thrown.expect(ServiceStatusException.class);
    teamService.addTeam(team);

    // parentTeamId not exist
    team = new Team();
    team.setTeamName("test2");
    team.setOrgId(orgId);
    team.setParentTeamId(100l);
    team.setCreatedUserId(-1L);
    team.setLastModifiedUserId(-1L);
    thrown.expect(ServiceStatusException.class);
    teamService.addTeam(team);
  }

  @Test
  public void testDeleteTeam() throws Exception {
    long teamId = teamService.addTeam(team);

    teamService.deleteTeam(orgId, teamId, 0L);

    Team afterDelete = teamService.getTeamByTeamId(orgId, teamId);
    Assert.assertNull(afterDelete);
  }

  @Test
  public void testDeleteTeamWhenHasSubTeams() {
    long teamId = teamService.addTeam(team);

    Team subTeam = new Team();
    subTeam.setTeamName("test2");
    subTeam.setOrgId(orgId);
    subTeam.setParentTeamId(teamId);
    subTeam.setCreatedUserId(userId);
    subTeam.setLastModifiedUserId(userId);
    teamService.addTeam(subTeam);

    thrown.expect(ServiceStatusException.class);
    teamService.deleteTeam(orgId, teamId, 0L);
  }

  @Test
  public void testDeleteTeamWhenHasUser() {
    long teamId = teamService.addTeam(team);

    TeamMember teamMember = new TeamMember();
    teamMember.setUserId(userId);
    teamMember.setTeamId(teamId);
    teamMember.setOrgId(orgId);
    teamMember.setCreatedUserId(userId);
    teamMember.setLastModifiedUserId(userId);
    teamService.addTeamMember(teamMember);

    thrown.expect(ServiceStatusException.class);
    teamService.deleteTeam(orgId, teamId, 0L);
  }

  @Test
  public void testDeleteTeamWHenHasResignedUser() {
    long teamId = teamService.addTeam(team);

    List<Long> resignedUserIds = new ArrayList<>();
    resignedUserIds.add(userId);
    Mockito.doReturn(resignedUserIds).when(spyUserEmploymentService).sublistUserIdByUserStatus(
            Mockito.anyLong(), Mockito.anyList(), Mockito.anyInt());

    teamMember.setTeamId(teamId);
    teamService.addTeamMember(teamMember);
    teamService.deleteTeam(orgId, teamId, 0L);
  }

  @Test
  public void testUpdateTeam() throws Exception {
    long teamId = teamService.addTeam(team);

    team.setTeamName("test2");
    teamService.updateTeam(team);

    Team inDb = teamService.getTeamByTeamId(orgId, teamId);
    Assert.assertEquals("test2", inDb.getTeamName());

    team.setTeamId(100l);
    teamService.updateTeam(team);
  }

  @Test
  public void testListSubTeams() throws Exception {
    long teamId = teamService.addTeam(team);

    Team team2 = new Team();
    team2.setTeamName("test2");
    team2.setOrgId(orgId);
    team2.setParentTeamId(teamId);
    team2.setCreatedUserId(userId);
    team2.setLastModifiedUserId(userId);
    long subTeamId = teamService.addTeam(team2);

    List<Team> teams = teamService.listSubTeams(orgId, teamId);
    Assert.assertEquals(1, teams.size());

    Assert.assertEquals(1, teamService.listTeamByOrgIdAndTeamIds(orgId, Arrays.asList(teamId)).size());

    Assert.assertEquals(1,teamService.listNextLevelTeams(orgId, teamId).size());

    Assert.assertEquals(1, teamService.listUpTeams(orgId, subTeamId).size());
  }

  @Test
  public void testAddTeamMember() throws Exception {
    teamService.addTeamMember(teamMember);

    TeamMember inDb = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId);
    Assert.assertEquals(1l, inDb.getTeamId().longValue());
  }

  @Test
  public void testAddTeamMemberWithException() throws Exception {
    long teamId = teamService.addTeam(team);

    teamMember.setTeamId(teamId);
    teamService.addTeamMember(teamMember);

    thrown.expect(ServiceStatusException.class);
    teamService.addTeamMember(teamMember);

    teamMember.setUserId(0l);
    thrown.expect(ServiceStatusException.class);
    teamService.addTeamMember(teamMember);

    teamMember = new TeamMember();
    teamMember.setUserId(userId);
    teamMember.setTeamId(0l);
    teamMember.setOrgId(orgId);
    thrown.expect(ServiceStatusException.class);
    teamService.addTeamMember(teamMember);

    teamMember = new TeamMember();
    teamMember.setUserId(userId);
    teamMember.setTeamId(1l);
    teamMember.setOrgId(0l);
    thrown.expect(ServiceStatusException.class);
    teamService.addTeamMember(teamMember);
  }

  @Test
  public void testBatchAddTeamMember() throws Exception {
    teamMember.setTeamId(1l);

    teamService.batchAddTeamMember(Arrays.asList(teamMember));

    TeamMember inDb = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId);
    Assert.assertEquals(1l, inDb.getTeamId().longValue());
  }

  @Test
  public void testDeleteTeamMember() throws Exception {
    teamService.addTeamMember(teamMember);

    teamService.deleteTeamMember(orgId, userId, 0);

    thrown.expect(ServiceStatusException.class);
    TeamMember inDb = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId);
    // Assert.assertNull(inDb);

  }

  @Test
  public void testBatchDeleteTeamMembers() throws Exception {
    long teamId = teamService.addTeam(team);
    teamMember.setTeamId(teamId);
    teamService.addTeamMember(teamMember);

    teamService.batchDeleteTeamMembers(orgId, teamId, Arrays.asList(userId), 0);

    thrown.expect(ServiceStatusException.class);
    TeamMember inDb = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId);
    Assert.assertNull(inDb);
  }

  @Test
  public void testGetUserIdsByOrgIdAndTeamIds() throws Exception {
    long teamId = teamService.addTeam(team);
    teamMember.setTeamId(teamId);

    teamService.addTeamMember(teamMember);

    List<Long> userIds = teamService.getUserIdsByOrgIdAndTeamIds(orgId, Arrays.asList(teamId), 1, 20);
    Assert.assertEquals(1, userIds.size());
  }

  @Test
  public void testUpdateTeamMembers() throws Exception {
    long teamId = teamService.addTeam(team);
    teamMember.setTeamId(teamId);

    teamService.addTeamMember(teamMember);

    TeamMember inDb = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId);

    inDb.setIsTeamAdmin(1);
    teamService.updateTeamAdmin(inDb);

    inDb = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId);
    Assert.assertEquals(1, inDb.getIsTeamAdmin().intValue());
  }

  @Test
  public void testBatchUpdateTeamMembers() throws Exception {
    long teamId = teamService.addTeam(team);
    teamMember.setTeamId(teamId);

    long teamMemberId = teamService.addTeamMember(teamMember);

    teamService.batchUpdateTeamMembers(orgId, Arrays.asList(userId), teamId, 10l, 0L);

    TeamMember inDb = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId);
    Assert.assertEquals(inDb.getTeamId().longValue(), 10l);
  }

  @Test
  public void testBatchUpdateTeamAndTeamMember() throws Exception {
    long teamId = teamService.addTeam(team);
    teamMember.setTeamId(teamId);
    teamService.addTeamMember(teamMember);

    team.setTeamName("update");
    long toTeamId = teamService.addTeam(team);
    teamService.batchUpdateTeamAndTeamMember(orgId, Arrays.asList(teamId), Arrays.asList(userId), toTeamId, userId);

    Team inDb = teamService.getTeamByTeamId(orgId, teamId);
    Assert.assertEquals(toTeamId, inDb.getParentTeamId().longValue());

    TeamMember member = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId);
    Assert.assertEquals(toTeamId, member.getTeamId().longValue());

    Assert.assertEquals(1L, teamService.countUserNumberByTeamId(orgId, toTeamId));

    Org org = new Org();
    org.setOrgId(orgId);
    org.setShortName("org");
    Mockito.doReturn(org).when(spyOrgService).getOrg(Mockito.anyLong());

    List<TeamMemberInfo> teamMemberInfos = teamService.listTeamMemberInfoByUserIds(orgId, Arrays.asList(userId));
    Assert.assertEquals(1, teamMemberInfos.size());
  }

  @Test
  public void testProjectTeamRelated() throws Exception {
    long teamId = teamService.addTeam(team);

    ProjectTeam projectTeam = new ProjectTeam();
    projectTeam.setOrgId(orgId);
    projectTeam.setTeamId(teamId);
    projectTeam.setProjectTeamName("test");
    projectTeam.setCreatedUserId(userId);

    long projectTeamId = teamService.addProjectTeam(projectTeam);
    ProjectTeam inDb = teamService.getProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId);
    Assert.assertEquals("test", inDb.getProjectTeamName());

    List<ProjectTeam> projectTeams = teamService.listProjectTeamsByOrgIdAndTeamId(orgId, teamId);
    Assert.assertEquals(1, projectTeams.size());
    Assert.assertEquals(inDb, projectTeams.get(0));

    inDb.setProjectTeamName("update");
    teamService.updateProjectTeam(inDb);
    inDb = teamService.getProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId);
    Assert.assertEquals("update", inDb.getProjectTeamName());

    ProjectTeamMember projectTeamMember = new ProjectTeamMember();
    projectTeamMember.setOrgId(orgId);
    projectTeamMember.setProjectTeamId(projectTeamId);
    projectTeamMember.setUserId(userId);
    projectTeamMember.setCreatedUserId(userId);
    teamService.batchInsertProjectTeamMember(Arrays.asList(projectTeamMember));

    ProjectTeamMember member = teamService.getProjectTeamMember(orgId, projectTeamId, userId);
    Assert.assertNotNull(member);

    List<ProjectTeamMember> projectTeamMembers = teamService.listProjectTeamMembersByOrgIdAndUserId(orgId, userId);
    Assert.assertEquals(1, projectTeamMembers.size());
    Assert.assertEquals(member, projectTeamMembers.get(0));

    List<Long> userIds = teamService.listUserIdsByOrgIdAndProjectTeamId(orgId, projectTeamId);
    Assert.assertEquals(1, userIds.size());

    projectTeams = teamService.listProjectTeamMemberInfoByUserId(orgId, userId);
    Assert.assertEquals(1, projectTeams.size());

    teamService.batchDeleteProjectTeamMember(projectTeamMembers);
    projectTeamMembers = teamService.listProjectTeamMembersByOrgIdAndUserId(orgId, userId);
    Assert.assertEquals(0, projectTeamMembers.size());

    teamService.deleteProjectTeam(orgId, projectTeamId, userId);
    inDb = teamService.getProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId);
    Assert.assertNull(inDb);
  }
}