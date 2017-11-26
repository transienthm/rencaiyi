// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.conversation.enums.PeriodType;
import hr.wozai.service.user.client.conversation.enums.RemindDay;
import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.user.client.userorg.enums.ContentIndexType;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.UuidUsage;
import hr.wozai.service.user.client.userorg.facade.NavigationFacade;
import hr.wozai.service.user.client.userorg.facade.UserFacade;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.user.client.userorg.util.PageUtil;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.helper.UserEmploymentHelper;
import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.model.userorg.*;
import hr.wozai.service.user.server.service.*;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/15
 */
@Service("userFacadeImpl")
public class UserFacadeImpl implements UserFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserFacadeImpl.class);

  @Autowired
  private UserService userService;

  @Autowired
  private TeamService teamService;

  @Autowired
  private SecurityModelService securityModelService;

  @Autowired
  private UserProfileService userProfileService;

  @Autowired
  private NameIndexService nameIndexService;

  @Autowired
  private AuthenticationService authenticateService;

  @Autowired
  private OrgService orgService;

  @Autowired
  private UserEmploymentService userEmploymentService;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private ConvrScheduleService convrScheduleService;

  @Autowired
  private UserProfileFacade userProfileFacade;

  @Autowired
  private NavigationFacade navigationFacade;

  @Override
  public VoidDTO initPassword(long orgId, long userId, String passwordPlainText) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      authenticateService.initPassword(orgId, userId, passwordPlainText);
      tokenService.deleteUuidInfoByUserIdAndUsage(orgId, userId, UuidUsage.INIT_PWD.getCode());

      // set status as active for IMPORTED users
      UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
      if (UserStatus.IMPORTED.getCode() == userEmployment.getUserStatus()) {
        userProfileService.updateUserStatus(orgId, userId, UserStatus.ACTIVE.getCode(), -1L);
      }
    } catch (Exception e) {
      LOGGER.error("initPassword()-error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;

  }

  @Override
  @LogAround
  public VoidDTO changePassword(
          long orgId, long userId, String currentPassword, String newPassword, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      authenticateService.changePassword(orgId, userId, currentPassword, newPassword, adminUserId);
    } catch (Exception e) {
      LOGGER.error("changePassword-error():", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO resetPasswordWhenOnboarding(long orgId, long userId, String password) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      authenticateService.resetPassword(orgId, userId, password);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  public VoidDTO resetPasswordWhenMissingPwd(long orgId, long userId, String password) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      authenticateService.resetPassword(orgId, userId, password);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  /*@Override
  @LogAround
  public BooleanDTO deleteUserAccount(long userId, long actorUserId, long adminUserId) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      boolean isSuccessful = userService.deleteUserAccount(userId, actorUserId);
      result.setData(isSuccessful);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public BooleanDTO updateUserAccount(UserAccountDTO userAccountDTO, long actorUserId, long adminUserId) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      UserAccount userAccount = new UserAccount();
      BeanUtils.copyProperties(userAccountDTO, userAccount);
      boolean isSuccessufl = userService.updateUserAccount(userAccount);
      result.setData(isSuccessufl);
    } catch (Exception e) {
      LOGGER.info("updateUserAccount()-fail", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public UserAccountDTO getUserAccountByUserId(long userId, long actorUserId, long adminUserId) {
    UserAccountDTO result = new UserAccountDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      UserAccount userAccount = userService.getUserAccountByUserId(userId);
      BeanUtils.copyProperties(userAccount, result);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }*/

  @Override
  @LogAround
  public UserAccountDTO getUserAccountByEmail(String emailAddress, long actorUserId, long adminUserId) {
    UserAccountDTO result = new UserAccountDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      UserAccount userAccount = userService.getUserAccountByEmailAddress(emailAddress);
      BeanUtils.copyProperties(userAccount, result);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /*@Override
  @LogAround
  public IdListDTO listAllUserByOrgId(long orgId, long actorUserId, long adminUserId) {
    IdListDTO result = new IdListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> idList = userService.listAllUsersByOrgId(orgId);
      result.setIdList(idList);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }*/

  @Override
  @LogAround
  public IdListDTO listUsersWhoHasReportorByOrgId(long orgId, long actorUserId, long adminUserId) {
    IdListDTO result = new IdListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> idList = userService.listAllUsersByOrgId(orgId);
      Iterator<Long> iter = idList.iterator();
      while (iter.hasNext()) {
        Long userId = iter.next();
        if (!userService.hasReportor(orgId, userId)) {
          iter.remove();
        }
      }
      result.setIdList(idList);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public LongDTO getOrgIdByUserId(long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long orgId = userService.findOrgIdByUserId(actorUserId);
      result.setData(orgId);
    } catch (Exception e) {
      LOGGER.error("getOrgIdByUserId-error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;

  }

  @Override
  @LogAround
  public BooleanDTO hasPassword(long userId) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      UserAccount userAccount = userService.getUserAccountByUserId(userId);
      if (StringUtils.isNullOrEmpty(userAccount.getEncryptedPassword())) {
        result.setData(false);
      } else {
        result.setData(true);
      }
    } catch (Exception e) {
      LOGGER.error("hasPassword-error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public BooleanDTO verifyUserAccountWithPassword(long orgId, long userId, String password, long adminUserId) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      boolean b = authenticateService.verifyUserAccountWithPassword(userId, password);
      result.setData(b);
    } catch (Exception e) {
      LOGGER.error("verifyUserAccountWithPassword-error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public BooleanDTO signUpWithEmail(String orgName, String email, String password) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long userId = authenticateService.signUpWithEmailAddress(email, password);
      result.setData(true);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO loginWithEmail(String email, String password, boolean captchaSuccess) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      boolean isFirstLogin = authenticateService.loginWithEmail(email, password, captchaSuccess);

      /*if (isFirstLogin) {
        // init navi org
        UserAccount userAccount = userService.getUserAccountByEmailAddress(email);
        long userId = userAccount.getUserId();
        long orgId = userService.findOrgIdByUserId(userId);
        VoidDTO naviResult = navigationFacade.initNaviOrg(orgId, userId, userId, userId);
        ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(naviResult.getServiceStatusDTO().getCode());
        if (serviceStatus != ServiceStatus.COMMON_OK) {
          throw new ServiceStatusException(serviceStatus);
        }
      }*/
    } catch (Exception e) {
      LOGGER.info("loginWithEmail-error:{}", e);
      if (((ServiceStatusException) e).getServiceStatus().getCode() == ServiceStatus.UO_USER_NOT_FOUND.getCode()) {
        e = new ServiceStatusException(ServiceStatus.AS_INVALID_LOGIN_CREDENTIALS);
      }
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public UserInfoDTO getUserInfoByEmail(String email) {
    UserInfoDTO result = new UserInfoDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      UserAccount userAccount = userService.getUserAccountByEmailAddress(email);
      long userId = userAccount.getUserId();
      long orgId = userService.findOrgIdByUserId(userId);
      CoreUserProfile coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
      UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
      Long enrollDate = UserEmploymentHelper.getEnrollDate(userEmployment);
      if ((enrollDate != null && enrollDate > TimeUtils.getNowTimestmapInMillis()) ||
              userEmployment.getUserStatus() == UserStatus.RESIGNED.getCode() ||
              userEmployment.getUserStatus() == UserStatus.IMPORTED.getCode()) {
        throw new ServiceStatusException(ServiceStatus.UO_USER_NOT_FOUND);
      }

      CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
      BeanUtils.copyProperties(coreUserProfile, coreUserProfileDTO);

      Org org = orgService.getOrg(orgId);
      OrgDTO orgDTO = new OrgDTO();
      BeanUtils.copyProperties(org, orgDTO);

      result.setCoreUserProfileDTO(coreUserProfileDTO);
      result.setOrgDTO(orgDTO);
    } catch (Exception e) {
      LOGGER.error("getUserInfoByEmail-error():", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  // ###################new add on 2016/2/16##########################

  @Override
  @LogAround
  public LongDTO addTeam(TeamDTO teamDTO, long actorUserId, long adminUserId) {
    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Team team = new Team();
      BeanUtils.copyProperties(teamDTO, team);
      long teamId = teamService.addTeam(team);
      /*nameIndexService.addContentIndex(team.getOrgId(), teamId,
              ContentIndexType.TEAM_NAME.getCode(), team.getTeamName());*/
      result.setData(teamId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public TeamDTO getTeam(long orgId, long teamId, long actorUserId, long adminUserId) {
    TeamDTO result = new TeamDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);


    try {
      Team team = teamService.getTeamByTeamId(orgId, teamId);
      if (team == null) {
        throw new ServiceStatusException(ServiceStatus.UO_TEAM_NOT_FOUND);
      }
      BeanUtils.copyProperties(team, result);
      if (team.getParentTeamId() == 0L) {
        result.setTeamName(orgService.getOrg(orgId).getShortName());
      }
      result.setServiceStatusDTO(serviceStatusDTO);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /*@Override
  @LogAround
  public TeamListDTO listSubTeams(long orgId, long teamId, long actorUserId, long adminUserId) {
    TeamListDTO result = new TeamListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Team> teams = teamService.listSubTeams(orgId, teamId);
      List<TeamDTO> teamDTOs = new ArrayList<>();
      for (Team team : teams) {
        TeamDTO teamDTO = new TeamDTO();
        BeanUtils.copyProperties(team, teamDTO);
        teamDTOs.add(teamDTO);
      }
      result.setTeamDTOList(teamDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }*/

  @Override
  @LogAround
  public TeamListDTO listNextLevelTeams(long orgId, long teamId, long actorUserId, long adminUserId) {
    TeamListDTO result = new TeamListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Team> teams = teamService.listNextLevelTeams(orgId, teamId);
      List<TeamDTO> teamDTOs = new ArrayList<>();

      for (Team team : teams) {
        TeamDTO teamDTO = new TeamDTO();
        BeanUtils.copyProperties(team, teamDTO);
        if (team.getParentTeamId() == 0L) {
          teamDTO.setTeamName(orgService.getOrg(orgId).getShortName());
        }
        teamDTOs.add(teamDTO);
      }
      result.setTeamDTOList(teamDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public LongDTO updateTeam(TeamDTO teamDTO, long actorUserId, long adminUserId) {
    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Team team = new Team();
      BeanUtils.copyProperties(teamDTO, team);
      teamService.updateTeam(team);

      result.setData(team.getTeamId());
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public BooleanDTO deleteTeam(long orgId, long teamId, long actorUserId, long adminUserId) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      teamService.deleteTeam(orgId, teamId, actorUserId);

      result.setData(true);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public TeamListDTO listSubordinateTeamsAndMembers(long orgId, long teamId, long actorUserId, long adminUserId) {
    TeamListDTO result = new TeamListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Team> teams = teamService.listNextLevelTeams(orgId, teamId);
      List<TeamDTO> teamDTOs = new ArrayList<>();
      for (Team team : teams) {
        TeamDTO teamDTO = new TeamDTO();
        BeanUtils.copyProperties(team, teamDTO);
        if (team.getParentTeamId() == 0L) {
          teamDTO.setTeamName(orgService.getOrg(orgId).getShortName());
        }
        List<Team> subTeams = teamService.listSubTeams(orgId, team.getTeamId());
        List<Long> teamIds = new ArrayList<>();
        for (Team team1 : subTeams) {
          teamIds.add(team1.getTeamId());
        }
        teamIds.add(team.getTeamId());
        List<Long> userIds = teamService.getUserIdsByOrgIdAndTeamIds(orgId, teamIds, 1, Integer.MAX_VALUE);

        // 过滤离职的人
        List<Long> resignedUsers = userEmploymentService.sublistUserIdByUserStatus(orgId, userIds,
                UserStatus.RESIGNED.getCode());
        userIds.removeAll(resignedUsers);

        teamDTO.setTeamMemberNumber((long)userIds.size());
        List<Team> nextLevelTeams = teamService.listNextLevelTeams(orgId, team.getTeamId());
        teamDTO.setHasSubordinate(nextLevelTeams.size() > 0 || userIds.size() > 0);
        teamDTOs.add(teamDTO);
      }
      // List<Long> teamAdmins = securityModelService.listTeamAdminUserIdByOrgIdAndTeamId(orgId, teamId);
      List<Long> userIds = teamService.getUserIdsByOrgIdAndTeamIds(orgId, Arrays.asList(teamId), 1, Integer.MAX_VALUE);
      List<Long> resignedUserIds = userEmploymentService.sublistUserIdByUserStatus(orgId, userIds,
              UserStatus.RESIGNED.getCode());
      // 过滤离职的人
      userIds.removeAll(resignedUserIds);

      List<CoreUserProfileDTO> coreUserProfileDTOs = new ArrayList<>();
      if (!CollectionUtils.isEmpty(userIds)) {
        CoreUserProfileListDTO facadeResult = userProfileFacade.listCoreUserProfile(orgId, userIds, actorUserId, adminUserId);
        ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(facadeResult.getServiceStatusDTO().getCode());
        if (serviceStatus != ServiceStatus.COMMON_OK) {
          throw new ServiceStatusException(serviceStatus);
        }
        coreUserProfileDTOs = facadeResult.getCoreUserProfileDTOs();
        /*for (CoreUserProfileDTO coreUserProfile : coreUserProfileDTOs) {
          if (teamAdmins.contains(coreUserProfile.getUserId())) {
            coreUserProfile.setTeamAdmin(true);
          }
        }*/
      }

      result.setTeamDTOList(teamDTOs);
      result.setCoreUserProfileDTOs(coreUserProfileDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO transferTeamsAndTeamMembers(long orgId, List<Long> teamIds, List<Long> userIds,
                                             long toTeamId, long actorUserId, long adminUserId) {
    LOGGER.info("transferTeamsAndTeamMembers()-request:adminUserId:{}, actorUserId:{}", adminUserId, actorUserId);
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      teamService.batchUpdateTeamAndTeamMember(orgId, teamIds, userIds, toTeamId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public TeamListDTO listAllTeams(long orgId, long actorUserId, long adminUserId) {
    LOGGER.info("listAllTeams()-request: actorUserId:{}, adminUserId:{}", actorUserId, adminUserId);
    TeamListDTO result = new TeamListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Team> teams = teamService.listAllTeams(orgId);
      List<TeamDTO> teamDTOs = new ArrayList<>();
      for (Team team : teams) {
        TeamDTO teamDTO = new TeamDTO();
        BeanUtils.copyProperties(team, teamDTO);
        if (team.getParentTeamId() == 0L) {
          teamDTO.setTeamName(orgService.getOrg(orgId).getShortName());
        }
        teamDTOs.add(teamDTO);
      }

      result.setTeamDTOList(teamDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public TeamListDTO listUpTeamLineByTeamId(long orgId, long teamId, long actorUserId, long adminUserId) {
    TeamListDTO result = new TeamListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      List<TeamDTO> teamDTOs = new ArrayList<>();
      Team exist = teamService.getTeamByTeamId(orgId, teamId);
      List<Team> teams = teamService.listUpTeams(orgId, teamId);
      teams.add(exist);
      for (Team team : teams) {
        TeamDTO teamDTO = new TeamDTO();
        BeanUtils.copyProperties(team, teamDTO);
        if (team.getParentTeamId() == 0L) {
          teamDTO.setTeamName(orgService.getOrg(orgId).getShortName());
        }
        teamDTOs.add(teamDTO);
      }
      result.setTeamDTOList(teamDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public TeamListDTO listTeamsByTeamIds(long orgId, List<Long> teamIds, long actorUserId, long adminUserId) {
    TeamListDTO result = new TeamListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      List<TeamDTO> teamDTOs = new ArrayList<>();

      List<Team> teams = teamService.listTeamByOrgIdAndTeamIds(orgId, teamIds);
      for (Team team : teams) {
        TeamDTO teamDTO = new TeamDTO();
        BeanUtils.copyProperties(team, teamDTO);
        teamDTOs.add(teamDTO);
      }
      result.setTeamDTOList(teamDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /*@Override
  @LogAround
  public TeamListDTO listTeamsByTeamNameOrPinyinOrAbbreviation(long orgId, String keyword, int pageNumber, int pageSize, long actorUserId, long adminUserId) {
    TeamListDTO result = new TeamListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> teamIds = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
              ContentIndexType.TEAM_NAME.getCode(), pageNumber, pageSize);
      List<TeamDTO> teamDTOs = new ArrayList<>();
      for (Long teamId : teamIds) {
        Team team = teamService.getTeamByTeamId(orgId, teamId);
        TeamDTO teamDTO = new TeamDTO();
        BeanUtils.copyProperties(team, teamDTO);
        teamDTOs.add(teamDTO);
      }
      result.setTeamDTOList(teamDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }*/

  @Override
  @LogAround
  public BooleanDTO assignUsersToTeam(long orgId, List<Long> userIds, long teamId, long actorUserId, long adminUserId) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    LOGGER.info("assignUsersToTeam()-request: userIds={}, teamId={}, orgId={}", userIds, teamId, orgId);
    try {
      List<TeamMember> teamMembers = new ArrayList<>();
      for (Long userId : userIds) {
        TeamMember teamMember = new TeamMember();
        teamMember.setUserId(userId);
        teamMember.setTeamId(teamId);
        teamMember.setOrgId(orgId);
        teamMember.setCreatedUserId(actorUserId);
        teamMember.setLastModifiedUserId(actorUserId);
        teamMembers.add(teamMember);
      }
      teamService.batchAddTeamMember(teamMembers);
      result.setData(true);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /*@Override
  @LogAround
  public BooleanDTO batchDeleteTeamMembers(long orgId, List<Long> userIds, long teamId, long actorUserId, long adminUserId) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    LOGGER.info("batchDeleteTeamMembers()-request: userIds={}, teamId={}, orgId={}", userIds, teamId, orgId);
    try {
      teamService.batchDeleteTeamMembers(orgId, teamId, userIds, 0);
      result.setData(true);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public BooleanDTO transferUsers(long orgId, List<Long> userIds, long fromTeamId, long toTeamId, long actorUserId, long adminUserId) {
    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      teamService.batchUpdateTeamMembers(orgId, userIds, fromTeamId, toTeamId, actorUserId);
      result.setData(true);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }*/

  @Override
  @LogAround
  public UserNameListDTO listTeamMembers(long orgId, long teamId, String keyword, int pageNumber,
                                         int pageSize, long actorUserId, long adminUserId) {
    UserNameListDTO result = new UserNameListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Role> roles = securityModelService.getRolesByUserId(orgId, actorUserId);
      boolean isHr = false;
      boolean isSuperAdmin = false;
      for (Role role : roles) {
        if (role.getRoleName().equals(DefaultRole.HR.getName())) {
          isHr = true;
        } else if (role.getRoleName().equals(DefaultRole.SUPER_ADMIN.getName())) {
          isSuperAdmin = true;
        }
      }
      List<Team> teams = teamService.listSubTeams(orgId, teamId);
      List<Long> teamIds = new ArrayList<>();
      for (Team team : teams) {
        teamIds.add(team.getTeamId());
      }
      teamIds.add(teamId);
      List<Long> teamUserIds = teamService.getUserIdsByOrgIdAndTeamIds(orgId, teamIds, 1, Integer.MAX_VALUE);

      List<Long> searchResult = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
              ContentIndexType.USER_NAME.getCode(), 1, Integer.MAX_VALUE);

      List<Long> finalUserIds = PageUtil.filterUserIds(searchResult, teamUserIds);

      List<Long> subList;
      if (isHr || isSuperAdmin) {
        subList = finalUserIds;
      } else {
        subList = userEmploymentService.sublistUserIdByUserStatus(orgId, finalUserIds,
                UserStatus.ACTIVE.getCode());
      }

      result.setIdList(PageUtil.getPagingList(subList, pageNumber, pageSize));
      result.setTotalRecordNum(subList.size());

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public IdListDTO listUnResignedAndHasReportorTeamMembersForReview(
          long orgId, List<Long> teamIds, long actorUserId, long adminUserId) {
    IdListDTO result = new IdListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Set<Long> allTeamIds = new HashSet<>();
      for (Long teamId : teamIds) {
        allTeamIds.add(teamId);
        List<Team> teams = teamService.listSubTeams(orgId, teamId);
        for (Team team : teams) {
          allTeamIds.add(team.getTeamId());
        }
      }

      List<Long> idList = teamService.getUserIdsByOrgIdAndTeamIds(
              orgId, new ArrayList<>(allTeamIds), 1, Integer.MAX_VALUE);
      Iterator<Long> iter = idList.iterator();
      while (iter.hasNext()) {
        Long userId = iter.next();
        UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
        Long enrollDate = UserEmploymentHelper.getEnrollDate(userEmployment);
        if (!userService.hasReportor(orgId, userId)
                || IntegerUtils.equals(userEmployment.getUserStatus(), UserStatus.RESIGNED.getCode())
                || (enrollDate != null && enrollDate > TimeUtils.getNowTimestmapInMillis())) {
          iter.remove();
        }
      }
      result.setIdList(idList);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateTeamAdmin(long orgId, TeamMemberDTO teamMemberDTO, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, teamMemberDTO.getUserId()).getTeamId();
      TeamMember teamMember = new TeamMember();
      BeanUtils.copyProperties(teamMemberDTO, teamMember);
      teamMember.setOrgId(orgId);
      teamMember.setTeamId(teamId);
      teamMember.setLastModifiedUserId(actorUserId);
      teamService.updateTeamAdmin(teamMember);
    } catch (Exception e) {
      LOGGER.error("updateTeamMember-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public TeamMemberDTO getTeamMemberByUserId(long orgId, long userId, long actorUserId, long adminUserId) {
    TeamMemberDTO result = new TeamMemberDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      TeamMember teamMember = teamService.getTeamMemberByUserIdAndOrgId(orgId, userId);
      if (teamMember == null) {
        throw new ServiceStatusException(ServiceStatus.UO_TEAM_MEMBER_NOT_FOUND);
      }
      BeanUtils.copyProperties(teamMember, result);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public TeamMemberListDTO listTeamMemberInfoByUserIds(
          long orgId, List<Long> userIds, long actorUserId, long adminUserId) {
    TeamMemberListDTO result = new TeamMemberListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<TeamMemberInfo> teamMemberInfos = teamService.listTeamMemberInfoByUserIds(orgId, userIds);
      List<TeamMemberDTO> teamMemberDTOs = new ArrayList<>();
      for (TeamMemberInfo t : teamMemberInfos) {
        TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
        BeanUtils.copyProperties(t, teamMemberDTO);
        teamMemberDTOs.add(teamMemberDTO);
      }
      result.setTeamMemberDTOList(teamMemberDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public CoreUserProfileListDTO getTeamMembersByTeamId(long orgId, long teamId, long actorUserId, long adminUserId) {
    CoreUserProfileListDTO result = new CoreUserProfileListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> userIds = teamService.getUserIdsByOrgIdAndTeamIds(orgId, Arrays.asList(teamId), 1, Integer.MAX_VALUE);
      // 过滤离职的人
      List<Long> resignedUsers = userEmploymentService.sublistUserIdByUserStatus(orgId, userIds,
              UserStatus.RESIGNED.getCode());
      userIds.removeAll(resignedUsers);
      if (!CollectionUtils.isEmpty(userIds)) {
        result = userProfileFacade.listCoreUserProfile(orgId, userIds, actorUserId, adminUserId);
      } else {
        result.setCoreUserProfileDTOs(new ArrayList<>());
      }

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public LongDTO addProjectTeam(long orgId, ProjectTeamDTO projectTeamDTO, long actorUserId, long adminUserId) {
    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProjectTeam projectTeam = new ProjectTeam();
      BeanUtils.copyProperties(projectTeamDTO, projectTeam);
      projectTeam.setOrgId(orgId);
      projectTeam.setCreatedUserId(actorUserId);
      long projectTeamId = teamService.addProjectTeam(projectTeam);
      result.setData(projectTeamId);
    } catch (Exception e) {
      LOGGER.error("addProjectTeam-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO deleteProjectTeam(long orgId, long projectTeamId, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      teamService.deleteProjectTeam(orgId, projectTeamId, actorUserId);
    } catch (Exception e) {
      LOGGER.error("deleteProjectTeam-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateProjectTeam(long orgId, ProjectTeamDTO projectTeamDTO, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProjectTeam projectTeam = new ProjectTeam();
      BeanUtils.copyProperties(projectTeamDTO, projectTeam);
      projectTeam.setOrgId(orgId);
      projectTeam.setLastModifiedUserId(actorUserId);

      teamService.updateProjectTeam(projectTeam);
    } catch (Exception e) {
      LOGGER.error("deleteProjectTeam-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public ProjectTeamDTO getProjectTeamByPrimaryKeyAndOrgId(
          long orgId, long projectTeamId, long actorUserId, long adminUserId) {
    ProjectTeamDTO result = new ProjectTeamDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProjectTeam projectTeam = teamService.getProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId);
      BeanUtils.copyProperties(projectTeam, result);
    } catch (Exception e) {
      LOGGER.error("getProjectTeamByPrimaryKeyAndOrgId-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public ProjectTeamListDTO listProjectTeamsByOrgIdAndTeamId(
          long orgId, long teamId, long actorUserId, long adminUserId) {
    ProjectTeamListDTO result = new ProjectTeamListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ProjectTeam> projectTeams = teamService.listProjectTeamsByOrgIdAndTeamId(orgId, teamId);
      List<ProjectTeamDTO> projectTeamDTOs = new ArrayList<>();
      for (ProjectTeam projectTeam : projectTeams) {
        ProjectTeamDTO projectTeamDTO = new ProjectTeamDTO();
        BeanUtils.copyProperties(projectTeam, projectTeamDTO);
        projectTeamDTOs.add(projectTeamDTO);
      }
      result.setProjectTeamDTOs(projectTeamDTOs);
    } catch (Exception e) {
      LOGGER.error("listProjectTeamsByOrgIdAndTeamId-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO batchInsertProjectTeamMember(
          long orgId, ProjectTeamMemberListDTO projectTeamMemberListDTO, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ProjectTeamMember> projectTeamMembers = new ArrayList<>();
      for (ProjectTeamMemberDTO projectTeamMemberDTO : projectTeamMemberListDTO.getProjectTeamMemberDTOs()) {
        ProjectTeamMember projectTeamMember = new ProjectTeamMember();
        BeanUtils.copyProperties(projectTeamMemberDTO, projectTeamMember);
        projectTeamMember.setOrgId(orgId);
        projectTeamMember.setCreatedUserId(actorUserId);
        projectTeamMembers.add(projectTeamMember);
      }
      teamService.batchInsertProjectTeamMember(projectTeamMembers);
    } catch (Exception e) {
      LOGGER.error("batchInsertProjectTeamMember-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO batchDeleteProjectTeamMember(
          long orgId, ProjectTeamMemberListDTO projectTeamMemberListDTO, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ProjectTeamMember> projectTeamMembers = new ArrayList<>();
      for (ProjectTeamMemberDTO projectTeamMemberDTO : projectTeamMemberListDTO.getProjectTeamMemberDTOs()) {
        ProjectTeamMember projectTeamMember = new ProjectTeamMember();
        BeanUtils.copyProperties(projectTeamMemberDTO, projectTeamMember);
        projectTeamMember.setOrgId(orgId);
        projectTeamMember.setLastModifiedUserId(actorUserId);
        projectTeamMembers.add(projectTeamMember);
      }
      teamService.batchDeleteProjectTeamMember(projectTeamMembers);
    } catch (Exception e) {
      LOGGER.error("batchDeleteProjectTeamMember-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public ProjectTeamMemberListDTO listProjectTeamMembersByOrgIdAndUserId(
          long orgId, long userId, long actorUserId, long adminUserId) {
    ProjectTeamMemberListDTO result = new ProjectTeamMemberListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ProjectTeamMember> projectTeamMembers = teamService.listProjectTeamMembersByOrgIdAndUserId(orgId, userId);
      List<ProjectTeamMemberDTO> projectTeamMemberDTOs = new ArrayList<>();
      for (ProjectTeamMember projectTeamMember : projectTeamMembers) {
        ProjectTeamMemberDTO projectTeamMemberDTO = new ProjectTeamMemberDTO();
        BeanUtils.copyProperties(projectTeamMember, projectTeamMemberDTO);
        projectTeamMemberDTOs.add(projectTeamMemberDTO);
      }
      result.setProjectTeamMemberDTOs(projectTeamMemberDTOs);
    } catch (Exception e) {
      LOGGER.error("listProjectTeamMembersByOrgIdAndUserId-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public CoreUserProfileListDTO listProjectTeamMembersByOrgIdAndProjectTeamId(
          long orgId, long projectTeamId, long actorUserId, long adminUserId) {
    CoreUserProfileListDTO result = new CoreUserProfileListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> userIds = teamService.listUserIdsByOrgIdAndProjectTeamId(orgId, projectTeamId);
      // 过滤离职的人
      List<Long> resignedUsers = userEmploymentService.sublistUserIdByUserStatus(orgId, userIds,
              UserStatus.RESIGNED.getCode());
      userIds.removeAll(resignedUsers);
      if (!CollectionUtils.isEmpty(userIds)) {
        result = userProfileFacade.listCoreUserProfile(orgId, userIds, actorUserId, adminUserId);
      } else {
        result.setCoreUserProfileDTOs(new ArrayList<>());
      }

    } catch (Exception e) {
      LOGGER.error("listProjectTeamMembersByOrgIdAndProjectTeamId-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public CoreUserProfileListDTO listReporteesByUserIdAndOrgId(long orgId, long userId, long actorUserId, long adminUserId) {
    CoreUserProfileListDTO result = new CoreUserProfileListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> userIds = userService.listReporteesByUserIdAndOrgId(orgId, userId);
      List<Long> resignedUserIds = userEmploymentService.sublistUserIdByUserStatus(orgId, userIds,
              UserStatus.RESIGNED.getCode());

      // 过滤离职的人
      userIds.removeAll(resignedUserIds);

      List<CoreUserProfileDTO> coreUserProfileDTOs = new ArrayList<>();

      if (!CollectionUtils.isEmpty(userIds)) {
        CoreUserProfileListDTO facadeResult = userProfileFacade.listCoreUserProfile(orgId, userIds, actorUserId, adminUserId);
        ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(facadeResult.getServiceStatusDTO().getCode());
        if (serviceStatus != ServiceStatus.COMMON_OK) {
          throw new ServiceStatusException(serviceStatus);
        }

        coreUserProfileDTOs = facadeResult.getCoreUserProfileDTOs();
        for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
          userIds = userService.listReporteesByUserIdAndOrgId(orgId, coreUserProfileDTO.getUserId());
          resignedUserIds = userEmploymentService.sublistUserIdByUserStatus(orgId, userIds,
                  UserStatus.RESIGNED.getCode());
          if (userIds.size() != resignedUserIds.size()) {
            coreUserProfileDTO.setHasReportee(true);
          }
        }
      }
      result.setCoreUserProfileDTOs(coreUserProfileDTOs);
    } catch (Exception e) {
      LOGGER.error("listReporteesByUserIdAndOrgId-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public CoreUserProfileDTO getReportorByUserIdAndOrgId(long orgId, long userId, long actorUserId, long adminUserId) {
    CoreUserProfileDTO result = new CoreUserProfileDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long reportUserId = userService.getReportorByUserIdAndOrgId(orgId, userId);
      LOGGER.info("+++++++++reportUserId:{}", reportUserId);
      CoreUserProfile coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, reportUserId);
      if (null != coreUserProfile) {
        BeanUtils.copyProperties(coreUserProfile, result);
      }
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO batchInsertReportLine(long orgId, List<Long> userIds, long reportUserId, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      userService.batchInsertReportLine(orgId, userIds, reportUserId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO batchUpdateReportLine(long orgId, List<Long> userIds,
                                       long newReportUserId, long actorUserId, long adminUserId) {
    LOGGER.info("batchUpdateReportLine()-request:actorUserId:{}, adminUserId:{}", actorUserId, userIds);
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      userService.batchUpdateReportLine(orgId, userIds, newReportUserId, actorUserId);
      // update convrSchedule
      if (newReportUserId != 0) {
        List<Long> existedTargetUserIds = convrScheduleService.listTargetUserIdBySourceUserId(newReportUserId, orgId);
        // add convrSchedule if not exists
        for (Long userId: userIds) {
          if (!existedTargetUserIds.contains(userId)) {
            ConvrSchedule convrSchedule = new ConvrSchedule();
            convrSchedule.setOrgId(orgId);
            convrSchedule.setSourceUserId(newReportUserId);
            convrSchedule.setTargetUserId(userId);
            convrSchedule.setPeriodType(PeriodType.HALF_MONTH.getCode());
            convrSchedule.setRemindDay(RemindDay.MONDAY.getCode());
            convrSchedule.setIsActive(1);
            convrSchedule.setCreatedUserId(0L);
            convrScheduleService.addConvrSchedule(convrSchedule);
          }
        }
      }
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public ReportLineInfoDTO getReportLineInfo(long orgId, long userId, boolean needTeamInfo, long actorUserId, long adminUserId) {
    ReportLineInfoDTO result = new ReportLineInfoDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> reportors = userService.listUpReportLineByUserId(orgId, userId);

      List<Long> reportees = userService.listReporteesByUserIdAndOrgId(orgId, userId);

      /*if (needTeamInfo) {
        batchInsertTeamMemberInfo(orgId, reporteeDTOs);
      }*/

      result.setReportors(reportors);
      result.setReportees(reportees);
    } catch (Exception e) {
      LOGGER.error("getReportLineInfo-request():" + e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public ReportLineListDTO listReportLineByUserIds(long orgId, List<Long> userIds, long actorUserId, long adminUserId) {
    ReportLineListDTO result = new ReportLineListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ReportLineDTO> reportLineDTOs = new ArrayList<>();
      List<ReportLine> reportLines = userService.listReportLinesByUserIds(orgId, userIds);
      for (ReportLine reportLine : reportLines) {
        ReportLineDTO reportLineDTO = new ReportLineDTO();
        BeanUtils.copyProperties(reportLine, reportLineDTO);
        reportLineDTOs.add(reportLineDTO);
      }
      result.setReportLineDTOList(reportLineDTOs);
    } catch (Exception e) {
      LOGGER.error("listReportLineByUserIds-error():" + e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /*private List<CoreUserProfileDTO> getCoreProfileDTOFromIds(long orgId, List<Long> userIds) {
    List<CoreUserProfile> coreUserProfiles = userProfileService.listOldCoreUserProfileByOrgIdAndUserId(orgId, userIds);
    List<CoreUserProfileDTO> coreUserProfileDTOs = new ArrayList<>();
    for (CoreUserProfile coreUserProfile : coreUserProfiles) {
      CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
      BeanUtils.copyProperties(coreUserProfile, coreUserProfileDTO);
      coreUserProfileDTOs.add(coreUserProfileDTO);
    }
    return coreUserProfileDTOs;
  }*/

  /*private void batchInsertTeamMemberInfo(long orgId, List<CoreUserProfileDTO> coreUserProfileDTOs) {
    if (CollectionUtils.isEmpty(coreUserProfileDTOs)) {
      return;
    }
    List<Long> userIds = new ArrayList<>();
    for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
      userIds.add(coreUserProfileDTO.getUserId());
    }
    List<TeamMemberInfo> teamMemberInfos = teamService.listTeamMemberInfoByUserIds(orgId, userIds);
    Map<Long, TeamMemberDTO> map = new HashMap<>();
    for (TeamMemberInfo teamMemberInfo : teamMemberInfos) {
      TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
      BeanUtils.copyProperties(teamMemberInfo, teamMemberDTO);
      map.put(teamMemberInfo.getUserId(), teamMemberDTO);
    }
    for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
      TeamMemberDTO teamMemberDTO = map.get(coreUserProfileDTO.getUserId());
      coreUserProfileDTO.setTeamMemberDTO(teamMemberDTO);
    }
  }*/

  @Override
  @LogAround
  public TeamListDTO fetchTeamAndUserProfiles(long orgId, List<Long> teamIds, List<Long> userIds) {
    TeamListDTO result = new TeamListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<TeamDTO> teamDTOs = new ArrayList<>();
      if (!CollectionUtils.isEmpty(teamIds)) {
        List<Team> teams = teamService.listTeamByOrgIdAndTeamIds(orgId, teamIds);
        if (!CollectionUtils.isEmpty(teams)) {
          for (Team team : teams) {
            TeamDTO teamDTO = new TeamDTO();
            BeanUtils.copyProperties(team, teamDTO);
            teamDTOs.add(teamDTO);
          }
        }
      }
      result.setTeamDTOList(teamDTOs);

      List<CoreUserProfileDTO> coreUserProfileDTOs = new ArrayList<>();
      if (!CollectionUtils.isEmpty(userIds)) {
        List<CoreUserProfile> coreUserProfiles =
                userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId, userIds);
        if (!CollectionUtils.isEmpty(coreUserProfiles)) {
          for (CoreUserProfile coreUserProfile : coreUserProfiles) {
            CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
            BeanUtils.copyProperties(coreUserProfile, coreUserProfileDTO);
            coreUserProfileDTOs.add(coreUserProfileDTO);
          }
        }
      }
      result.setCoreUserProfileDTOs(coreUserProfileDTOs);

    } catch (Exception e) {
      LOGGER.error("fetchTeamAndUserProfiles-error():" + e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }
}

