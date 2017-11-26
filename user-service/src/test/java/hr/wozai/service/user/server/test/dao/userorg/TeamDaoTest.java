package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.dao.userorg.TeamDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.Team;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/17
 */
public class TeamDaoTest extends TestBase {
  private static Logger LOGGER = LoggerFactory.getLogger(TeamDaoTest.class);

  @Autowired
  TeamDao teamDao;

  private long orgId = 199l;
  private Team team;

  private long userId = 199L;

  @Before
  public void init() {
    team = new Team();
    team.setOrgId(orgId);
    team.setTeamName("test");
    team.setParentTeamId(0l);
    team.setCreatedUserId(userId);
    team.setLastModifiedUserId(userId);
  }

  @Test
  public void testAll() throws Exception {
    long teamId = teamDao.insertTeam(team);

    Team afterInsert = teamDao.getTeamByPrimaryKeyAndOrgId(orgId, teamId);
    LOGGER.info("after insert:{}", afterInsert);
    Assert.assertNotNull(afterInsert);

    afterInsert.setTeamName("update");
    afterInsert.setParentTeamId(3l);
    afterInsert.setLastModifiedUserId(-1L);
    teamDao.updateTeam(afterInsert);

    Team afterUpdate = teamDao.getTeamByPrimaryKeyAndOrgId(orgId, teamId);
    Assert.assertEquals("update", afterUpdate.getTeamName());
    Assert.assertEquals(3l, afterUpdate.getParentTeamId().longValue());
    Assert.assertEquals(-1L, afterUpdate.getLastModifiedUserId().longValue());

    teamDao.batchUpdateParentTeamId(orgId, Arrays.asList(teamId), 4L, userId);
    afterUpdate = teamDao.getTeamByPrimaryKeyAndOrgId(orgId, teamId);
    Assert.assertEquals(4l, afterUpdate.getParentTeamId().longValue());

    Assert.assertEquals(1, teamDao.listTeamByOrgIdAndTeamIds(orgId, Arrays.asList(teamId)).size());
    Assert.assertEquals(1, teamDao.listAllTeams(orgId).size());

    teamDao.deleteTeam(orgId, teamId, userId);
    Team afterDelete = teamDao.getTeamByPrimaryKeyAndOrgId(orgId, teamId);
    Assert.assertNull(afterDelete);
  }

  @Test
  public void testGetSubTeamsByTeamIdAndOrgId() throws Exception {
    long parentTeamId = teamDao.insertTeam(team);

    team.setParentTeamId(parentTeamId);
    teamDao.insertTeam(team);
    teamDao.insertTeam(team);

    List<Team> teamList = teamDao.listNextLevelTeams(orgId, parentTeamId);
    Assert.assertEquals(2, teamList.size());
  }
}