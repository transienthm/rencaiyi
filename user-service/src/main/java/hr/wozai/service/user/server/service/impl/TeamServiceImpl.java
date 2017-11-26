package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.LongUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.client.userorg.enums.ContentIndexType;
import hr.wozai.service.user.server.dao.userorg.ProjectTeamDao;
import hr.wozai.service.user.server.dao.userorg.ProjectTeamMemberDao;
import hr.wozai.service.user.server.dao.userorg.TeamDao;
import hr.wozai.service.user.server.dao.userorg.TeamMemberDao;
import hr.wozai.service.user.server.helper.TeamHelper;
import hr.wozai.service.user.server.model.okr.ObjectivePeriod;
import hr.wozai.service.user.server.model.userorg.*;
import hr.wozai.service.user.server.service.*;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/18
 */
@Service("teamService")
public class TeamServiceImpl implements TeamService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TeamServiceImpl.class);

  @Autowired
  TeamDao teamDao;

  @Autowired
  TeamMemberDao teamMemberDao;

  @Autowired
  ProjectTeamDao projectTeamDao;

  @Autowired
  ProjectTeamMemberDao projectTeamMemberDao;

  @Autowired
  UserEmploymentService userEmploymentService;

  @Autowired
  OrgService orgService;

  @Autowired
  OkrService okrService;

  @Autowired
  NameIndexService nameIndexService;

  /*@Autowired
  SecurityModelService securityModelService;*/

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addTeam(Team team) {
    TeamHelper.checkTeamParams(team);

    long parentTeamId = team.getParentTeamId();
    long orgId = team.getOrgId();
    Team parentTeam = getTeamByTeamId(orgId, parentTeamId);
    if (parentTeamId != 0 && parentTeam == null) {
      LOGGER.error("addTeam() fail, parentTeamId is not valid, parentTeamId:{}", parentTeamId);
      throw new ServiceStatusException(ServiceStatus.UO_PARENT_TEAM_NOT_FOUND, "parentTeamId is not valid");
    }

    /*Team existTeam = getTeamByTeamName(orgId, team.getTeamName());
    if (existTeam != null) {
      LOGGER.error("addTeam() fail, teamName is not unique:{}", team.getTeamName());
      throw new ServiceStatusException(ServiceStatus.UO_TEAM_EXIST);
    }*/

    teamDao.insertTeam(team);

    //添加索引
    nameIndexService.addContentIndex(team.getOrgId(), team.getTeamId(),
            ContentIndexType.TEAM_NAME.getCode(), team.getTeamName());
    return team.getTeamId();
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteTeam(long orgId, long teamId, long actorUserId) {
    List<Team> teams = listSubTeams(orgId, teamId);
    List<Long> userIds = getUserIdsByOrgIdAndTeamIds(orgId, Arrays.asList(teamId), 1, Integer.MAX_VALUE);
    List<ProjectTeam> projectTeams = listProjectTeamsByOrgIdAndTeamId(orgId, teamId);
    List<Long> resignedUserIds = userEmploymentService.sublistUserIdByUserStatus(orgId, userIds,
            UserStatus.RESIGNED.getCode());
    // 过滤离职的人
    userIds.removeAll(resignedUserIds);

    if (teams.size() != 0 || userIds.size() != 0 || projectTeams.size() != 0) {
      LOGGER.error("deleteTeam() error, team or team member or project team is not empty");
      throw new ServiceStatusException(ServiceStatus.UO_TEAM_DELETE_FAIL);
    }
    // 把离职的人移到"全公司"这个team下
    if (!CollectionUtils.isEmpty(resignedUserIds)) {
      List<Team> rootTeams = this.listNextLevelTeams(orgId, 0L);
      if (rootTeams.size() == 0 || rootTeams.size() > 1) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      Team rootTeam = rootTeams.get(0);
      teamMemberDao.batchUpdateTeamMembers(orgId, rootTeam.getTeamId(), resignedUserIds, actorUserId);
    }
    teamDao.deleteTeam(orgId, teamId, actorUserId);

    // 删除索引
    nameIndexService.deleteContentIndexByObjectIdAndType(orgId, teamId,
            ContentIndexType.TEAM_NAME.getCode(), actorUserId);

    // 删除项目组


    //删除okr
    List<ObjectivePeriod> objectivePeriods = okrService.listObjectivePeriodByOrgIdAndOwnerId(
            orgId, OkrType.TEAM.getCode(), teamId);
    if (!CollectionUtils.isEmpty(objectivePeriods)) {
      for (ObjectivePeriod objectivePeriod : objectivePeriods) {
        okrService.deleteObjectivePeriod(orgId, objectivePeriod.getObjectivePeriodId(), actorUserId);
      }
    }
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long updateTeam(Team team) {
    TeamHelper.checkoutTeamUpdateParams(team);

    // String teamName = team.getTeamName();
    long orgId = team.getOrgId();
    long teamId = team.getTeamId();

    Team existTeam = getTeamByTeamId(orgId, teamId);
    if (existTeam == null) {
      return 0L;
    }

    teamDao.updateTeam(team);

    //更新索引
    if (!StringUtils.isNullOrEmpty(team.getTeamName())) {
      nameIndexService.deleteContentIndexByObjectIdAndType(team.getOrgId(), team.getTeamId(),
              ContentIndexType.TEAM_NAME.getCode(), team.getLastModifiedUserId());
      nameIndexService.addContentIndex(team.getOrgId(), team.getTeamId(),
              ContentIndexType.TEAM_NAME.getCode(), team.getTeamName());
    }
    return team.getTeamId();
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public Team getTeamByTeamId(long orgId, long teamId) {
    return teamDao.getTeamByPrimaryKeyAndOrgId(orgId, teamId);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public List<Team> listTeamByOrgIdAndTeamIds(long orgId, List<Long> teamIds) {
    return teamDao.listTeamByOrgIdAndTeamIds(orgId, teamIds);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public List<Team> listNextLevelTeams(long orgId, long teamId) {
    List<Team> teams = teamDao.listNextLevelTeams(orgId, teamId);
    return teams;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public List<Team> listSubTeams(long orgId, long teamId) {
    Team team = getTeamByTeamId(orgId, teamId);
    List<Team> result = new ArrayList<>();

    if (team == null) {
      LOGGER.info("listSubTeams() success, result size is 0");
      return result;
    }

    Queue<Team> queue = new LinkedBlockingQueue<>();
    queue.add(team);
    while (!queue.isEmpty()) {
      Team r = queue.poll();
      List<Team> teams = teamDao.listNextLevelTeams(orgId, r.getTeamId());
      result.addAll(teams);
      queue.addAll(teams);
    }
    LOGGER.info("listSubTeams() success, result size is {}", result.size());
    return result;
  }

  @Override
  public List<Team> listUpTeams(long orgId, long teamId) {
    Team team = getTeamByTeamId(orgId, teamId);
    List<Team> result = new ArrayList<>();

    if (team == null) {
      LOGGER.info("listUpTeams() success, result size is 0");
      return result;
    }

    while (team != null) {
      long parentTeamId = team.getParentTeamId();
      team = getTeamByTeamId(orgId, parentTeamId);
      if (team != null) {
        result.add(team);
      }
    }
    LOGGER.info("listUpTeams() success, result size is {}", result.size());
    return result;
  }

  @Override
  public List<Team> listAllTeams(long orgId) {
    return teamDao.listAllTeams(orgId);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean batchUpdateTeamAndTeamMember(long orgId, List<Long> teamIds, List<Long> userIds,
                                              long toTeamId, long actorUserId) {
    if (userIds != null && !userIds.isEmpty()) {
      //Role teamAdmin = securityModelService.findRoleByRoleName(orgId, DefaultRole.TEAM_ADMIN.getName());
      List<TeamMember> teamMembers = new ArrayList<>();
      for (Long id : userIds) {
        /*securityModelService.deleteUserRoleByUserIdAndRoleId(orgId, id, teamAdmin.getRoleId(),
                getTeamMemberByUserIdAndOrgId(orgId, id).getTeamId(), actorUserId);*/
        TeamMember teamMember = new TeamMember();
        teamMember.setOrgId(orgId);
        teamMember.setUserId(id);
        teamMember.setTeamId(toTeamId);
        teamMember.setCreatedUserId(actorUserId);
        teamMember.setLastModifiedUserId(actorUserId);
        teamMembers.add(teamMember);
      }
      teamMemberDao.batchDeleteTeamMembersByUserIds(orgId, userIds, actorUserId);
      teamMemberDao.batchInsertTeamMember(teamMembers);
    }
    if (teamIds != null && !teamIds.isEmpty()) {
      for (Long teamId : teamIds) {
        if (!isSameTeamOrSubTeam(orgId, teamId, toTeamId)) {
          throw new ServiceStatusException(ServiceStatus.UO_TEAM_UPDATE_FAIL);
        }
      }
      teamDao.batchUpdateParentTeamId(orgId, teamIds, toTeamId, actorUserId);
    }

    return true;
  }

  private boolean isSameTeamOrSubTeam(long orgId, long sourceTeamId, long destTeamId) {
    if (sourceTeamId == destTeamId) {
      return false;
    }
    List<Team> teams = listSubTeams(orgId, sourceTeamId);
    for (Team team : teams) {
      if (team.getTeamId() == destTeamId) {
        return false;
      }
    }
    return true;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addTeamMember(TeamMember teamMember) {
    TeamHelper.checkTeamMemberParams(teamMember);

    long userId = teamMember.getUserId();
    long orgId = teamMember.getOrgId();
    TeamMember existOne = teamMemberDao.findByUserIdAndOrgId(orgId, userId);
    if (existOne != null) {
      LOGGER.error("addTeamMember() fail, exist in db");
      throw new ServiceStatusException(ServiceStatus.UO_TEAM_MEMBER_EXIST);
    }

    return teamMemberDao.insertTeamMember(teamMember);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean batchAddTeamMember(List<TeamMember> teamMembers) {
    for (TeamMember teamMember : teamMembers) {
      TeamHelper.checkTeamMemberParams(teamMember);
      TeamMember inDb = teamMemberDao.findByUserIdAndOrgId(teamMember.getOrgId(), teamMember.getUserId());
      if (inDb != null) {
        throw new ServiceStatusException(ServiceStatus.UO_TEAM_MEMBER_EXIST);
      }
    }

    teamMemberDao.batchInsertTeamMember(teamMembers);
    LOGGER.info("batchAddTeamMember() success, size is {}", teamMembers.size());
    return true;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean deleteTeamMember(long orgId, long userId, long actorUserId) {
    teamMemberDao.deleteTeamMember(orgId, userId, actorUserId);
    return true;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean updateTeamAdmin(TeamMember teamMember) {
    teamMemberDao.updateTeamMember(teamMember);
    return true;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean batchDeleteTeamMembers(long orgId, long teamId, List<Long> userIds, long actorUserId) {
    List<TeamMember> teamMembers = new ArrayList<>();
    for (long userId : userIds) {
      TeamMember teamMember = new TeamMember();
      teamMember.setUserId(userId);
      teamMember.setTeamId(teamId);
      teamMember.setOrgId(orgId);
      teamMember.setLastModifiedUserId(actorUserId);
      teamMembers.add(teamMember);
    }
    teamMemberDao.batchDeleteTeamMember(teamMembers);
    return true;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean batchUpdateTeamMembers(long orgId, List<Long> userIds, long fromTeamId, long toTeamId, long actorUserId) {
    teamMemberDao.batchUpdateTeamMembers(orgId, toTeamId, userIds, actorUserId);
    return true;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public TeamMember getTeamMemberByUserIdAndOrgId(long orgId, long userId) {
    TeamMember result = teamMemberDao.findByUserIdAndOrgId(orgId, userId);
    if (result == null) {
      throw new ServiceStatusException(ServiceStatus.UO_TEAM_NOT_FOUND, "team member not found");
    }
    return result;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public List<Long> getUserIdsByOrgIdAndTeamIds(long orgId, List<Long> teamIds, int pageNumber, int pageSize) {
    return teamMemberDao.listUserIdsByOrgIdAndTeamIds(orgId, teamIds, pageNumber, pageSize);
  }

  @Override
  public long countUserNumberByTeamId(long orgId, long teamId) {

    List<Team> subTeams = listSubTeams(orgId, teamId);
    List<Long> teamIds = new ArrayList<>();
    for (Team team : subTeams) {
      teamIds.add(team.getTeamId());
    }
    teamIds.add(teamId);
    return teamMemberDao.countUserNumberByTeamId(orgId, teamIds);
  }

  @Override
  public List<TeamMemberInfo> listTeamMemberInfoByUserIds(long orgId, List<Long> userIds) {
    List<TeamMemberInfo>  teamMemberInfos = teamMemberDao.listTeamMemberInfoByUserIds(orgId, userIds);
    Team orgTeam = this.listNextLevelTeams(orgId, 0L).get(0);
    Org org = orgService.getOrg(orgId);
    for (TeamMemberInfo teamMemberInfo : teamMemberInfos) {
      if (LongUtils.equals(teamMemberInfo.getTeamId(), orgTeam.getTeamId())) {
        teamMemberInfo.setTeamName(org.getShortName());
      }
    }
    return teamMemberInfos;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addProjectTeam(ProjectTeam projectTeam) {
    TeamHelper.checkAddProjectTeamParams(projectTeam);

    long teamId = projectTeam.getTeamId();
    Team team = getTeamByTeamId(projectTeam.getOrgId(), teamId);
    if (team == null) {
      throw new ServiceStatusException(ServiceStatus.UO_TEAM_NOT_FOUND);
    }

    long projectTeamId = projectTeamDao.insertProjectTeam(projectTeam);

    // 添加索引
    nameIndexService.addContentIndex(projectTeam.getOrgId(), projectTeamId,
            ContentIndexType.PROJECT_TEAM_NAME.getCode(), projectTeam.getProjectTeamName());

    return projectTeamId;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long deleteProjectTeam(long orgId, long projectTeamId, long actorUserId) {
    long result = projectTeamDao.deleteProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId, actorUserId);

    // 删除索引
    nameIndexService.deleteContentIndexByObjectIdAndType(orgId, projectTeamId,
            ContentIndexType.PROJECT_TEAM_NAME.getCode(), actorUserId);

    //删除okr
    List<ObjectivePeriod> objectivePeriods = okrService.listObjectivePeriodByOrgIdAndOwnerId(
            orgId, OkrType.PROJECT_TEAM.getCode(), projectTeamId);
    if (!CollectionUtils.isEmpty(objectivePeriods)) {
      for (ObjectivePeriod objectivePeriod : objectivePeriods) {
        okrService.deleteObjectivePeriod(orgId, objectivePeriod.getObjectivePeriodId(), actorUserId);
      }
    }

    return result;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long updateProjectTeam(ProjectTeam projectTeam) {
    TeamHelper.checkUpdateProjectTeamParams(projectTeam);
    long result = projectTeamDao.updateProjectTeam(projectTeam);

    // 更新索引
    if (!StringUtils.isNullOrEmpty(projectTeam.getProjectTeamName())) {
      nameIndexService.deleteContentIndexByObjectIdAndType(projectTeam.getOrgId(), projectTeam.getProjectTeamId(),
              ContentIndexType.PROJECT_TEAM_NAME.getCode(), projectTeam.getLastModifiedUserId());
      nameIndexService.addContentIndex(projectTeam.getOrgId(), projectTeam.getTeamId(),
              ContentIndexType.PROJECT_TEAM_NAME.getCode(), projectTeam.getProjectTeamName());
    }

    return result;
  }

  @Override
  @LogAround
  public ProjectTeam getProjectTeamByPrimaryKeyAndOrgId(long orgId, long projectTeamId) {
    return projectTeamDao.getProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId);
  }

  @Override
  @LogAround
  public List<ProjectTeam> listProjectTeamsByOrgIdAndTeamId(long orgId, long teamId) {
    return projectTeamDao.listProjectTeamsByOrgIdAndTeamId(orgId, teamId);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public int batchInsertProjectTeamMember(List<ProjectTeamMember> projectTeamMembers) {
    if (CollectionUtils.isEmpty(projectTeamMembers)) {
      return 0;
    }
    for (ProjectTeamMember member : projectTeamMembers) {
      TeamHelper.checkAddProjectTeamMemberParams(member);
    }

    return projectTeamMemberDao.batchInsertProjectTeamMember(projectTeamMembers);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public int batchDeleteProjectTeamMember(List<ProjectTeamMember> projectTeamMembers) {
    if (CollectionUtils.isEmpty(projectTeamMembers)) {
      return 0;
    }
    for (ProjectTeamMember member : projectTeamMembers) {
      TeamHelper.checkDeleteProjectTeamMemberParams(member);
    }

    return projectTeamMemberDao.batchDeleteProjectTeamMember(projectTeamMembers);
  }

  @Override
  @LogAround
  public ProjectTeamMember getProjectTeamMember(long orgId, long projectTeamId, long userId) {
    return projectTeamMemberDao.getProjectTeamMember(orgId, projectTeamId, userId);
  }

  @Override
  @LogAround
  public List<ProjectTeamMember> listProjectTeamMembersByOrgIdAndUserId(long orgId, long userId) {
    return projectTeamMemberDao.listProjectTeamMembersByOrgIdAndUserId(orgId, userId);
  }

  @Override
  @LogAround
  public List<Long> listUserIdsByOrgIdAndProjectTeamId(long orgId, long projectTeamId) {
    return projectTeamMemberDao.listUserIdsByOrgIdAndProjectTeamId(orgId, projectTeamId);
  }

  @Override
  @LogAround
  public List<ProjectTeam> listProjectTeamMemberInfoByUserId(long orgId, long userId) {
    return projectTeamMemberDao.listProjectTeamMemberInfoByUserId(orgId, userId);
  }
}
