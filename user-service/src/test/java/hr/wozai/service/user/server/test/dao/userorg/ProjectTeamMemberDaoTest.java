package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.client.userorg.dto.ProjectTeamDTO;
import hr.wozai.service.user.server.dao.userorg.ProjectTeamDao;
import hr.wozai.service.user.server.dao.userorg.ProjectTeamMemberDao;
import hr.wozai.service.user.server.model.userorg.ProjectTeam;
import hr.wozai.service.user.server.model.userorg.ProjectTeamMember;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/16
 */
public class ProjectTeamMemberDaoTest extends TestBase {
  @Autowired
  ProjectTeamMemberDao projectTeamMemberDao;

  @Autowired
  ProjectTeamDao projectTeamDao;

  private long orgId = 199L;
  private long userId1 = 299L;
  private long userId2 = 399L;
  private long projectTeamId = 499L;
  private ProjectTeamMember projectTeamMember1;

  private ProjectTeamMember projectTeamMember2;

  private ProjectTeam projectTeam;

  @Before
  public void setUp() throws Exception {
    projectTeamMember1 = new ProjectTeamMember();
    projectTeamMember1.setOrgId(orgId);
    projectTeamMember1.setUserId(userId1);
    projectTeamMember1.setProjectTeamId(projectTeamId);
    projectTeamMember1.setCreatedUserId(userId1);
    projectTeamMember1.setLastModifiedUserId(userId1);

    projectTeamMember2 = new ProjectTeamMember();
    projectTeamMember2.setOrgId(orgId);
    projectTeamMember2.setUserId(userId2);
    projectTeamMember2.setProjectTeamId(projectTeamId);
    projectTeamMember2.setCreatedUserId(userId2);
    projectTeamMember2.setLastModifiedUserId(userId1);

    projectTeam = new ProjectTeam();
    projectTeam.setOrgId(orgId);
    projectTeam.setTeamId(199L);
    projectTeam.setProjectTeamName("project_team");
    projectTeam.setCreatedUserId(userId1);
  }

  @Test
  public void testAll() {
    long projectTeamId = projectTeamDao.insertProjectTeam(projectTeam);
    projectTeamMember1.setProjectTeamId(projectTeamId);
    projectTeamMember2.setProjectTeamId(projectTeamId);

    int result = projectTeamMemberDao.batchInsertProjectTeamMember(Arrays.asList(projectTeamMember1, projectTeamMember2));
    Assert.assertEquals(2, result);

    ProjectTeamMember inDb = projectTeamMemberDao.getProjectTeamMember(orgId, projectTeamId, userId1);
    Assert.assertEquals(userId1, inDb.getUserId().longValue());

    List<ProjectTeamMember> projectTeamMembers = projectTeamMemberDao.listProjectTeamMembersByOrgIdAndUserId(orgId, userId1);
    Assert.assertEquals(1, projectTeamMembers.size());

    List<Long> userIds = projectTeamMemberDao.listUserIdsByOrgIdAndProjectTeamId(orgId, projectTeamId);
    Assert.assertEquals(2, userIds.size());

    List<ProjectTeam> projectTeams = projectTeamMemberDao.listProjectTeamMemberInfoByUserId(orgId, userId1);
    Assert.assertEquals(1, projectTeams.size());
    Assert.assertEquals("project_team", projectTeams.get(0).getProjectTeamName());

    projectTeamMemberDao.batchDeleteProjectTeamMember(Arrays.asList(projectTeamMember1, projectTeamMember2));
    projectTeamMembers = projectTeamMemberDao.listProjectTeamMembersByOrgIdAndUserId(orgId, userId1);
    Assert.assertEquals(0, projectTeamMembers.size());

    userIds = projectTeamMemberDao.listUserIdsByOrgIdAndProjectTeamId(orgId, projectTeamId);
    Assert.assertEquals(0, userIds.size());
  }

}