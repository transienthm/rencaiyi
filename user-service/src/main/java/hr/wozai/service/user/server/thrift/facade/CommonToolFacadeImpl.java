package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.LongUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongListDTO;
import hr.wozai.service.user.client.common.dto.RecentUsedObjectDTO;
import hr.wozai.service.user.client.common.dto.RemindSettingDTO;
import hr.wozai.service.user.client.common.dto.RemindSettingListDTO;
import hr.wozai.service.user.client.common.enums.RemindType;
import hr.wozai.service.user.client.common.enums.UserSearchType;
import hr.wozai.service.user.client.common.facade.CommonToolFacade;
import hr.wozai.service.user.client.conversation.facade.ConvrFacade;
import hr.wozai.service.user.client.okr.dto.UserAndTeamListDTO;
import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.user.client.userorg.enums.ContentIndexType;
import hr.wozai.service.user.client.userorg.enums.RecentUsedObjectType;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.user.client.userorg.util.PageUtil;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.model.common.RecentUsedObject;
import hr.wozai.service.user.server.model.common.RemindSetting;
import hr.wozai.service.user.server.model.userorg.ProjectTeam;
import hr.wozai.service.user.server.model.userorg.Team;
import hr.wozai.service.user.server.service.*;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/25
 */
@Service("commonToolFacadeImpl")
public class CommonToolFacadeImpl implements CommonToolFacade {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommonToolFacadeImpl.class);

  @Autowired
  NameIndexService nameIndexService;

  @Autowired
  private TeamService teamService;

  @Autowired
  private UserProfileFacade userProfileFacade;

  @Autowired
  SearchHistoryService searchHistoryService;

  @Autowired
  RemindSettingService remindSettingService;

  @Autowired
  UserEmploymentService userEmploymentService;

  @Autowired
  ConvrFacade convrFacade;

  @Override
  @LogAround
  public UserAndTeamListDTO listRecentCheckedOkrUserAndTeam(long orgId, long userId, long actorUserId, long adminUserId) {
    UserAndTeamListDTO result = new UserAndTeamListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<CoreUserProfileDTO> coreUserProfileDTOs = new ArrayList<>();
      List<TeamDTO> teamDTOs = new ArrayList<>();
      List<Long> userIds = new ArrayList<>();
      RecentUsedObject users = searchHistoryService.gerRecentUsedObjectByUserIdAndType(orgId, userId,
              RecentUsedObjectType.USER_OKR.getCode());
      RecentUsedObject teams = searchHistoryService.gerRecentUsedObjectByUserIdAndType(orgId, userId,
              RecentUsedObjectType.TEAM_OKR.getCode());
      RecentUsedObject projectTeams = searchHistoryService.gerRecentUsedObjectByUserIdAndType(orgId, userId,
              RecentUsedObjectType.PROJECT_TEAM_OKR.getCode());
      if (null != users && users.getUsedObjectId().size() > 0) {
        for (String id : users.getUsedObjectId()) {
          userIds.add(Long.parseLong(id));
        }
        coreUserProfileDTOs = getUnResignedCoreUserProfileListDTO(
                orgId, userIds, actorUserId, adminUserId).getCoreUserProfileDTOs();
      }

      if (null != teams && teams.getUsedObjectId().size() > 0) {
        List<Long> teamIds = new ArrayList<>();
        for (String teamIdString : teams.getUsedObjectId()) {
          long teamId = Long.parseLong(teamIdString);
          teamIds.add(teamId);
        }
        List<Team> teamList = teamService.listTeamByOrgIdAndTeamIds(orgId, teamIds);
        for (Team team : teamList) {
          TeamDTO teamDTO = new TeamDTO();
          BeanUtils.copyProperties(team, teamDTO);
          teamDTOs.add(teamDTO);
        }
      }

      List<ProjectTeamDTO> projectTeamList = new ArrayList<>();
      if (null != projectTeams && projectTeams.getUsedObjectId().size() > 0) {
        for (String idString : projectTeams.getUsedObjectId()) {
          long id = Long.parseLong(idString);
          ProjectTeam projectTeam = teamService.getProjectTeamByPrimaryKeyAndOrgId(orgId, id);
          ProjectTeamDTO projectTeamDTO = new ProjectTeamDTO();
          BeanUtils.copyProperties(projectTeam, projectTeamDTO);
          projectTeamList.add(projectTeamDTO);
        }
      }

      result.setCoreUserProfileDTOList(coreUserProfileDTOs);
      result.setTeamDTOList(teamDTOs);
      result.setProjectTeamDTOList(projectTeamList);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listRecentCheckedUserAndTeam()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO addRecentUsedObject(long orgId, RecentUsedObjectDTO recentUsedObjectDTO,
                                     long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      RecentUsedObject recentUsedObject = new RecentUsedObject();
      BeanUtils.copyProperties(recentUsedObjectDTO, recentUsedObject);
      recentUsedObject.setOrgId(orgId);

      searchHistoryService.addRecentUsedObject(recentUsedObject);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addRecentUsedObject()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public TeamListDTO searchUserAndTeamNamesByKeyword(long orgId, String keyword, int pageNumber, int pageSize, long actorUserId, long adminUserId) {
    TeamListDTO result = new TeamListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> teamIds = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
              ContentIndexType.TEAM_NAME.getCode(), pageNumber, pageSize);
      long teamNumber = nameIndexService.countIdNumByKeywordAndType(orgId, keyword, ContentIndexType.TEAM_NAME.getCode());
      List<Long> userIds = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
              ContentIndexType.USER_NAME.getCode(), pageNumber, pageSize);
      long userNumber = nameIndexService.countIdNumByKeywordAndType(orgId, keyword, ContentIndexType.USER_NAME.getCode());

      List<Long> projectTeamIds = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
              ContentIndexType.PROJECT_TEAM_NAME.getCode(), pageNumber, pageSize);

      List<CoreUserProfileDTO> coreUserProfileDTOs = new ArrayList<>();
      if (!CollectionUtils.isEmpty(userIds)) {
        coreUserProfileDTOs = getUnResignedCoreUserProfileListDTO(
                orgId, userIds, actorUserId, adminUserId).getCoreUserProfileDTOs();
      }

      List<TeamDTO> teamDTOs = new ArrayList<>();
      if (!CollectionUtils.isEmpty(teamIds)) {
        List<Team> teamList = teamService.listTeamByOrgIdAndTeamIds(orgId, teamIds);
        for (Team team : teamList) {
          TeamDTO teamDTO = new TeamDTO();
          BeanUtils.copyProperties(team, teamDTO);
          teamDTOs.add(teamDTO);
        }
      }

      List<ProjectTeamDTO> projectTeamDTOs = new ArrayList<>();
      for (Long projectTeamId : projectTeamIds) {
        ProjectTeam projectTeam = teamService.getProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId);
        if (projectTeam != null) {
          ProjectTeamDTO projectTeamDTO = new ProjectTeamDTO();
          BeanUtils.copyProperties(projectTeam, projectTeamDTO);
          projectTeamDTOs.add(projectTeamDTO);
        }
      }

      result.setTeamDTOList(teamDTOs);
      result.setCoreUserProfileDTOs(coreUserProfileDTOs);
      result.setProjectTeamDTOs(projectTeamDTOs);
      result.setTotalTeamNumber(teamNumber);
      result.setTotalUserNumber(userNumber);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("searchTeamNamesByKeyword()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public UserNameListDTO listUsersByUserNameOrPinyinOrAbbreviation(long orgId, String keyword, int pageNumber, int pageSize, long actorUserId, long adminUserId) {
    UserNameListDTO result = new UserNameListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> userIds = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
              ContentIndexType.USER_NAME.getCode(), pageNumber, pageSize);
      long totalRecordNum = nameIndexService.countIdNumByKeywordAndType(orgId, keyword, ContentIndexType.USER_NAME.getCode());
      result.setIdList(userIds);
      result.setTotalRecordNum(totalRecordNum);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public UserNameListDTO searchUsersWithTeamScope(long orgId, long teamId, String keyword, int pageNumber, int pageSize, long actorUserId, long adminUserId) {
    UserNameListDTO result = new UserNameListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> userIds = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
              ContentIndexType.USER_NAME.getCode(), 1, Integer.MAX_VALUE);
      List<Long> teamUserIds = teamService.getUserIdsByOrgIdAndTeamIds(orgId, Arrays.asList(teamId), 1, Integer.MAX_VALUE);

      List<Long> finalUserIds = PageUtil.filterUserIds(userIds, teamUserIds);

      result.setIdList(PageUtil.getPagingList(finalUserIds, pageNumber, pageSize));
      result.setTotalRecordNum(finalUserIds.size());
    } catch (Exception e) {
      LOGGER.error("searchUsersWithTeamScope-error():", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public UserNameListDTO searchDirectorsByKeyword(long orgId, String keyword, int pageNumber, int pageSize,
                                                  long actorUserId, long adminUserId) {
    UserNameListDTO result = new UserNameListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> userIds = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
              ContentIndexType.USER_NAME.getCode(), 1, Integer.MAX_VALUE);

      long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
      List<Long> teamUserIds = teamService.getUserIdsByOrgIdAndTeamIds(orgId, Arrays.asList(teamId), 1, Integer.MAX_VALUE);

      List<Long> finalUserIds = PageUtil.filterUserIds(userIds, teamUserIds);

      result.setIdList(PageUtil.getPagingList(finalUserIds, pageNumber, pageSize));
      result.setTotalRecordNum(finalUserIds.size());
    } catch (Exception e) {
      LOGGER.error("searchDirectorsByKeyword-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public UserNameListDTO searchUsersByKeywordAndType(long orgId, String keyword, int type, int pageNumber, int pageSize,
                                                     long actorUserId, long adminUserId) {
    UserNameListDTO result = new UserNameListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long totalRecordNum = nameIndexService.countIdNumByKeywordAndType(orgId, keyword, ContentIndexType.USER_NAME.getCode());
      List<Long> userIds = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
              ContentIndexType.USER_NAME.getCode(), 1, (int)totalRecordNum);

      List<Long> subList = new ArrayList<>();
      if (type == UserSearchType.NORMAL.getCode()) {
        List<Long> resignedUserIds = userEmploymentService.sublistUserIdByUserStatus(orgId, userIds,
                UserStatus.RESIGNED.getCode());
        userIds.removeAll(resignedUserIds);
        subList = userIds;
      } else if (type == UserSearchType.ACTIVE.getCode()) {
        subList = userEmploymentService.sublistUserIdByUserStatus(orgId, userIds,
                UserStatus.ACTIVE.getCode());
      } else if (type == UserSearchType.UN_REGULAR.getCode()) {
        subList = userEmploymentService.sublistUserIdNotResignedByEmploymentStatus(orgId, userIds,
                EmploymentStatus.PROBATIONARY.getCode());
      } else if (type == UserSearchType.AT.getCode()) {
        for (Long id : userIds) {
          if (!LongUtils.equals(id, actorUserId)) {
            subList.add(id);
          }
        }
      } else if (type == UserSearchType.UN_RESIGNED.getCode()) {
        List<Long> resignedUserIds = userEmploymentService.sublistUserIdByUserStatus(orgId, userIds,
                UserStatus.RESIGNED.getCode());
        userIds.removeAll(resignedUserIds);
        subList = userIds;
      } else if (type == UserSearchType.CONVERSATION.getCode()) {
        LongListDTO longListDTO = convrFacade.listTargetUserIdsOfSourceUser(orgId, actorUserId,
                actorUserId, actorUserId);
        if (longListDTO.getServiceStatusDTO().getCode() != ServiceStatus.COMMON_OK.getCode()) {
          throw new ServiceStatusException(ServiceStatus.getEnumByCode(longListDTO.getServiceStatusDTO().getCode()));
        }
        userIds.removeAll(longListDTO.getData());
        subList = userIds;
      } else {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM, "type is wrong");
      }
      result.setIdList(PageUtil.getPagingList(subList, pageNumber, pageSize));
      result.setTotalRecordNum(subList.size());
    } catch (Exception e) {
      LOGGER.error("searchUsersByKeywordAndType-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public RemindSettingListDTO listRemindSettingByUserId(long orgId, long userId, long actorUserId, long adminUserId) {
    RemindSettingListDTO result = new RemindSettingListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<RemindSettingDTO> remindSettingDTOs = new ArrayList<>();
      List<RemindSetting> remindSettings = remindSettingService.listRemindSettingByUserId(orgId, userId);
      for (RemindSetting remindSetting : remindSettings) {
        RemindSettingDTO remindSettingDTO = new RemindSettingDTO();
        BeanUtils.copyProperties(remindSetting, remindSettingDTO);

        int remindType = remindSetting.getRemindType();
        remindSettingDTO.setRemindName(RemindType.getEnumByCode(remindType).getDesc());
        remindSettingDTOs.add(remindSettingDTO);
      }
      result.setRemindSettingDTOList(remindSettingDTOs);
    } catch (Exception e) {
      LOGGER.error("listRemindSettingByUserId-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO batchUpdateRemindSetting(RemindSettingListDTO remindSettingListDTO, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<RemindSetting> remindSettings = new ArrayList<>();
      for (RemindSettingDTO remindSettingDTO : remindSettingListDTO.getRemindSettingDTOList()) {
        RemindSetting remindSetting = new RemindSetting();
        BeanUtils.copyProperties(remindSettingDTO, remindSetting);
        remindSettings.add(remindSetting);
      }
      remindSettingService.batchUpdateRemindSetting(remindSettings);
    } catch (Exception e) {
      LOGGER.error("batchUpdateRemindSetting-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public RemindSettingDTO getRemindSettingByUserIdAndRemindType(long orgId, long userId, int remindType, long actorUserId, long adminUserId) {
    RemindSettingDTO result = new RemindSettingDTO();

    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      RemindSetting remindSetting = remindSettingService.getRemindSettingByUserIdAndRemindType(orgId, userId, remindType);
      BeanUtils.copyProperties(remindSetting, result);
    } catch (Exception e) {
      LOGGER.error("getRemindSettingByUserIdAndRemindType-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  public CoreUserProfileListDTO getUnResignedCoreUserProfileListDTO(
          long orgId, List<Long> userIds, long actorUserId, long adminUserId) {
    CoreUserProfileListDTO result = new CoreUserProfileListDTO();
    List<Long> resignedUsers = userEmploymentService.sublistUserIdByUserStatus(orgId, userIds,
            UserStatus.RESIGNED.getCode());
    userIds.removeAll(resignedUsers);
    if (!CollectionUtils.isEmpty(userIds)) {
      result = userProfileFacade.listCoreUserProfile(orgId, userIds, actorUserId, adminUserId);
    } else {
      result.setCoreUserProfileDTOs(new ArrayList<>());
    }

    return result;
  }
}
