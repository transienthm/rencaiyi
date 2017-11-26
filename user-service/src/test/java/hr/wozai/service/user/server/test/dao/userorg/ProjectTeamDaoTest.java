package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.dao.userorg.ProjectTeamDao;
import hr.wozai.service.user.server.model.userorg.ProjectTeam;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/16
 */
public class ProjectTeamDaoTest extends TestBase {
  @Autowired
  ProjectTeamDao projectTeamDao;

  private long orgId = 199L;
  private long userId = 199L;
  private long teamId = 199L;
  private ProjectTeam projectTeam;

  @Before
  public void setUp() throws Exception {
    projectTeam = new ProjectTeam();
    projectTeam.setOrgId(orgId);
    projectTeam.setTeamId(teamId);
    projectTeam.setProjectTeamName("project_team");
    projectTeam.setCreatedUserId(userId);
  }

  @Test
  public void testAll() {
    long projectTeamId = projectTeamDao.insertProjectTeam(projectTeam);

    ProjectTeam inDb = projectTeamDao.getProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId);
    Assert.assertEquals(orgId, inDb.getOrgId().longValue());
    Assert.assertEquals(teamId, inDb.getTeamId().longValue());
    Assert.assertEquals("project_team", inDb.getProjectTeamName());

    List<ProjectTeam> projectTeams = projectTeamDao.listProjectTeamsByOrgIdAndTeamId(orgId, teamId);
    Assert.assertEquals(1, projectTeams.size());
    Assert.assertEquals(inDb, projectTeams.get(0));

    inDb.setProjectTeamName("update_name");
    projectTeamDao.updateProjectTeam(inDb);

    inDb = projectTeamDao.getProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId);
    Assert.assertEquals("update_name", inDb.getProjectTeamName());

    long projectTeamId2 = projectTeamDao.insertProjectTeam(projectTeam);
    projectTeams = projectTeamDao.listProjectTeamsByOrgIdAndTeamId(orgId, teamId);
    Assert.assertEquals(2, projectTeams.size());
    Assert.assertEquals(projectTeamId2, projectTeams.get(0).getProjectTeamId().longValue());

    projectTeamDao.deleteProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId, userId);

    inDb = projectTeamDao.getProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId);
    Assert.assertNull(inDb);
  }
}