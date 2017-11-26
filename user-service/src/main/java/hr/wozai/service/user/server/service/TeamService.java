package hr.wozai.service.user.server.service;


import hr.wozai.service.user.server.model.userorg.*;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/17
 */
public interface TeamService {
  long addTeam(Team team);

  void deleteTeam(long orgId, long teamId, long actorUserId);

  long updateTeam(Team team);

  Team getTeamByTeamId(long orgId, long teamId);

  List<Team> listTeamByOrgIdAndTeamIds(long orgId, List<Long> teamIds);

  List<Team> listNextLevelTeams(long orgId, long teamId);

  List<Team> listSubTeams(long orgId, long teamId);

  List<Team> listUpTeams(long orgId, long teamId);

  List<Team> listAllTeams(long orgId);

  boolean batchUpdateTeamAndTeamMember(long orgId, List<Long> teamIds, List<Long> userIds,
                                       long toTeamId, long actorUserId);

  long addTeamMember(TeamMember teamMember);

  boolean batchAddTeamMember(List<TeamMember> teamMembers);

  boolean deleteTeamMember(long orgId, long userId, long actorUserId);

  boolean updateTeamAdmin(TeamMember teamMember);

  boolean batchDeleteTeamMembers(long orgId, long teamId, List<Long> userIds, long actorUserId);

  boolean batchUpdateTeamMembers(long orgId, List<Long> userIds, long fromTeamId, long toTeamId, long actorUserId);

  TeamMember getTeamMemberByUserIdAndOrgId(long orgId, long userId);

  List<Long> getUserIdsByOrgIdAndTeamIds(long orgId, List<Long> teamIds, int pageNumber, int pageSize);

  long countUserNumberByTeamId(long orgId, long teamId);

  List<TeamMemberInfo> listTeamMemberInfoByUserIds(long orgId, List<Long> userIds);

  // =============================ProjectTeam=============================================

  long addProjectTeam(ProjectTeam projectTeam);

  long deleteProjectTeam(long orgId, long projectTeamId, long actorUserId);

  long updateProjectTeam(ProjectTeam projectTeam);

  ProjectTeam getProjectTeamByPrimaryKeyAndOrgId(long orgId, long projectTeamId);

  List<ProjectTeam> listProjectTeamsByOrgIdAndTeamId(long orgId, long teamId);

  // =============================ProjectTeamMember=======================================
  int batchInsertProjectTeamMember(List<ProjectTeamMember> projectTeamMembers);

  int batchDeleteProjectTeamMember(List<ProjectTeamMember> projectTeamMembers);

  ProjectTeamMember getProjectTeamMember(long orgId, long projectTeamId, long userId);

  List<ProjectTeamMember> listProjectTeamMembersByOrgIdAndUserId(long orgId, long userId);

  List<Long> listUserIdsByOrgIdAndProjectTeamId(long orgId, long projectTeamId);

  List<ProjectTeam> listProjectTeamMemberInfoByUserId(long orgId, long userId);
}
