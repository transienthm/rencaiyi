package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.server.model.userorg.ProjectTeam;
import hr.wozai.service.user.server.model.userorg.ProjectTeamMember;
import hr.wozai.service.user.server.model.userorg.TeamMember;
import hr.wozai.service.user.server.model.userorg.Team;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/17
 */
public class TeamHelper {
  public static void checkTeamParams(Team team) {
    if (team == null
            || (team.getTeamName() == null || !StringUtils.isValidVarchar100(team.getTeamName()))
            || team.getOrgId() == null
            || (team.getParentTeamId() == null || team.getParentTeamId() < 0)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkoutTeamUpdateParams(Team team) {
    if (team == null
            || (team.getTeamName() != null && !StringUtils.isValidVarchar100(team.getTeamName()))
            || team.getOrgId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkTeamMemberParams(TeamMember teamMember) {
    if (teamMember == null
            || (teamMember.getUserId() == null || teamMember.getUserId() <= 0)
            || (teamMember.getTeamId() == null || teamMember.getTeamId() < 0)
            || (teamMember.getOrgId() == null)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkAddProjectTeamParams(ProjectTeam projectTeam) {
    if (projectTeam == null
            || projectTeam.getOrgId() == null
            || projectTeam.getTeamId() == null
            || projectTeam.getProjectTeamName() == null
            || projectTeam.getCreatedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkUpdateProjectTeamParams(ProjectTeam projectTeam) {
    if (projectTeam == null
            || projectTeam.getOrgId() == null
            || projectTeam.getProjectTeamId() == null
            || projectTeam.getLastModifiedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkAddProjectTeamMemberParams(ProjectTeamMember projectTeamMember) {
    if (projectTeamMember == null
            || projectTeamMember.getOrgId() == null
            || projectTeamMember.getUserId() == null
            || projectTeamMember.getProjectTeamId() == null
            || projectTeamMember.getCreatedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }

  public static void checkDeleteProjectTeamMemberParams(ProjectTeamMember projectTeamMember) {
    if (projectTeamMember == null
            || projectTeamMember.getOrgId() == null
            || projectTeamMember.getUserId() == null
            || projectTeamMember.getProjectTeamId() == null
            || projectTeamMember.getLastModifiedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }
}
