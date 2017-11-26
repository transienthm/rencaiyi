package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.dao.userorg.TeamDao;
import hr.wozai.service.user.server.dao.userorg.TeamMemberDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.Team;
import hr.wozai.service.user.server.model.userorg.TeamMember;
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
public class TeamMemberDaoTest extends TestBase {
  private static Logger LOGGER = LoggerFactory.getLogger(TeamMemberDaoTest.class);

  @Autowired
  TeamMemberDao teamMemberDao;

  @Autowired
  TeamDao teamDao;

  private TeamMember teamMember;
  private long userId = 199l;
  private long teamId = 199l;
  private long orgId = 199l;
  @Before
  public void setUp() throws Exception {
    teamMember = new TeamMember();
    teamMember.setUserId(userId);
    teamMember.setTeamId(teamId);
    teamMember.setOrgId(orgId);
    teamMember.setCreatedUserId(userId);
    teamMember.setLastModifiedUserId(userId);
  }

  @Test
  public void testInsertTeamMember() throws Exception {
    teamMemberDao.insertTeamMember(teamMember);

    TeamMember afterInsert = teamMemberDao.findByUserIdAndOrgId(orgId, userId);
    LOGGER.info("after insert:{}", afterInsert);
    Assert.assertEquals(teamId, afterInsert.getTeamId().longValue());
    Assert.assertEquals(userId, afterInsert.getCreatedUserId().longValue());
    Assert.assertEquals(0, afterInsert.getIsTeamAdmin().intValue());

    afterInsert.setIsTeamAdmin(1);
    teamMemberDao.updateTeamMember(afterInsert);

    TeamMember afterUpdate = teamMemberDao.findByUserIdAndOrgId(orgId, userId);
    Assert.assertEquals(1, afterUpdate.getIsTeamAdmin().intValue());
  }

  @Test
  public void testBatchInsertTeamMember() throws Exception {
    teamMemberDao.batchInsertTeamMember(Arrays.asList(teamMember));

    Team team = new Team();
    team.setOrgId(orgId);
    team.setTeamName("test");
    team.setParentTeamId(0l);
    team.setCreatedUserId(userId);
    team.setLastModifiedUserId(userId);
    long toTeamId = teamDao.insertTeam(team);
    teamMemberDao.batchUpdateTeamMembers(orgId, toTeamId, Arrays.asList(userId), -1L);
    Assert.assertEquals(toTeamId, teamMemberDao.findByUserIdAndOrgId(orgId, userId).getTeamId().longValue());

    Assert.assertEquals(1, teamMemberDao.countUserNumberByTeamId(orgId, Arrays.asList(toTeamId)).longValue());
    Assert.assertEquals(1, teamMemberDao.listTeamMemberInfoByUserIds(orgId, Arrays.asList(userId)).size());
  }

  @Test
  public void testDelete() throws Exception {
    teamMemberDao.insertTeamMember(teamMember);

    teamMemberDao.deleteTeamMember(orgId, userId, 0);
    TeamMember afterDelete = teamMemberDao.findByUserIdAndOrgId(orgId, userId);
    Assert.assertNull(afterDelete);

    teamMemberDao.insertTeamMember(teamMember);
    teamMemberDao.batchDeleteTeamMember(Arrays.asList(teamMember));
    Assert.assertNull(teamMemberDao.findByUserIdAndOrgId(orgId, userId));

    teamMemberDao.insertTeamMember(teamMember);
    teamMemberDao.batchDeleteTeamMembersByUserIds(orgId, Arrays.asList(userId), -1L);
    Assert.assertNull(teamMemberDao.findByUserIdAndOrgId(orgId, userId));
  }

  @Test
  public void testFindUserIdsByOrgIdAndTeamIds() throws Exception {
    teamMemberDao.insertTeamMember(teamMember);

    List<Long> userIdList = teamMemberDao.listUserIdsByOrgIdAndTeamIds(orgId, Arrays.asList(teamId), 1, 20);

    Assert.assertEquals(1, userIdList.size());
  }
}