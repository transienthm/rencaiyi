// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.user.server.model.userorg.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.wozai.service.servicecommons.commons.consts.SystemFieldConsts;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserSysNotificationType;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.thrift.dto.IntegerDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.StringDTO;
import hr.wozai.service.servicecommons.thrift.dto.StringListDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.enums.ConfigType;
import hr.wozai.service.user.client.userorg.enums.ContentIndexType;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.enums.UserEmploymentProfileField;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.user.client.userorg.helper.RosterAvailabilityHelper;
import hr.wozai.service.user.server.component.EmployeeManagementNotifier;
import hr.wozai.service.user.server.helper.CoreUserProfileHelper;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.helper.RosterHelper;
import hr.wozai.service.user.server.helper.UserEmploymentHelper;
import hr.wozai.service.user.server.helper.UserProfileHelper;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.service.EmployeeManagementService;
import hr.wozai.service.user.server.service.NameIndexService;
import hr.wozai.service.user.server.service.OrgPickOptionService;
import hr.wozai.service.user.server.service.OrgService;
import hr.wozai.service.user.server.service.ProfileFieldService;
import hr.wozai.service.user.server.service.SecurityModelService;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.service.TokenService;
import hr.wozai.service.user.server.service.UserEmploymentService;
import hr.wozai.service.user.server.service.UserProfileService;
import hr.wozai.service.user.server.service.UserService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-14
 */
@Service("userProfileFacade")
public class UserProfileFacadeImpl implements UserProfileFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileFacadeImpl.class);

  @Autowired
  private ProfileFieldService profileFieldService;

  @Autowired
  private UserProfileService userProfileService;

  @Autowired
  private UserEmploymentService userEmploymentService;

  @Autowired
  private EmployeeManagementService employeeManagementService;

  @Autowired
  private OrgService orgService;

  @Autowired
  private UserService userService;

  @Autowired
  private TeamService teamService;

  @Autowired
  private NameIndexService nameIndexService;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private SecurityModelService securityModelService;

  @Autowired
  private EmployeeManagementNotifier employeeManagementNotifier;

  @Autowired
  private OrgPickOptionService orgPickOptionService;

  @Override
  @LogAround
  public UserProfileDTO getUserProfile(long orgId, long userId, long actorUserId, long adminUserId) {

    UserProfileDTO result = new UserProfileDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      UserProfile userProfile = userProfileService.getUserProfile(orgId, userId);
      BeanUtils.copyProperties(userProfile, result);
      List<ProfileFieldDTO> profileFieldDTOs = new ArrayList<>();
      for (int i = 0; i < userProfile.getProfileFields().size(); i++) {
        ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
        BeanHelper.copyPropertiesHandlingJSON(userProfile.getProfileFields().get(i), profileFieldDTO);
        profileFieldDTOs.add(profileFieldDTO);
      }
      result.setProfileFieldDTOs(profileFieldDTOs);
      // handle UserEmployment
      UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
      UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO();
      BeanUtils.copyProperties(userEmployment, userEmploymentDTO);
      result.setUserEmploymentDTO(userEmploymentDTO);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getUserProfile()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateUserProfileStatus(
      long orgId, long userId, int userStatus, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      userProfileService.updateUserStatus(orgId, userId, userStatus, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateUserStatus()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateUserProfileField(
      long orgId, long userId, Map<String, String> fieldValues, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      CoreUserProfile currCoreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
      userProfileService.updateUserProfileField(orgId, userId, fieldValues, actorUserId);
      // handle userName
      if (!StringUtils.isNullOrEmpty(fieldValues.get(SystemFieldConsts.FULL_NAME_REF_NAME))) {
        String newFullName = fieldValues.get(SystemFieldConsts.FULL_NAME_REF_NAME);
        LOGGER.info("#3: CUP={}, newName={}", currCoreUserProfile, newFullName);
        if (!StringUtils.isEqual(newFullName, currCoreUserProfile.getFullName())) {
          nameIndexService.deleteContentIndexByObjectIdAndType(orgId, userId,
                  ContentIndexType.USER_NAME.getCode(), actorUserId);
          nameIndexService.addContentIndex(orgId, userId, ContentIndexType.USER_NAME.getCode(), newFullName);
        }
      }
      // handle emailAddress
      if (!StringUtils.isNullOrEmpty(fieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME))) {
        UserAccount userAccount = userService.getUserAccountByUserId(userId);
        userAccount.setEmailAddress(fieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME));
        userAccount.setLastModifiedUserId(actorUserId);
        userService.updateUserAccount(userAccount);
        tokenService.deleteAllTokensByOrgIdAndUserId(orgId, userId);
      }
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateUserProfileField()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public CoreUserProfileDTO getCoreUserProfile(long orgId, long userId, long actorUserId, long adminUserId) {

    CoreUserProfileDTO result = new CoreUserProfileDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      CoreUserProfile coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
      if (null != coreUserProfile) {
        BeanUtils.copyProperties(coreUserProfile, result);
        // handle userEmployment
        UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
        UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO();
        BeanUtils.copyProperties(userEmployment, userEmploymentDTO);
        result.setUserEmploymentDTO(userEmploymentDTO);
        // handle jobTitleName
        if (null != coreUserProfile.getJobTitle()) {
          List<OrgPickOption> jobTitles = orgPickOptionService
              .listPickOptionOfConfigType(orgId, ConfigType.JOB_TITLE.getCode());
          for (OrgPickOption jobTitle : jobTitles) {
            if (jobTitle.getOrgPickOptionId().equals(coreUserProfile.getJobTitle())) {
              result.setJobTitleName(jobTitle.getOptionValue());
            }
          }
        }
        // handle teamMemberInfo
        List<TeamMemberInfo> teamMemberInfos = teamService.listTeamMemberInfoByUserIds(orgId, Arrays.asList(userId));
        if (!CollectionUtils.isEmpty(teamMemberInfos)) {
          TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
          BeanUtils.copyProperties(teamMemberInfos.get(0), teamMemberDTO);
          result.setTeamMemberDTO(teamMemberDTO);
          result.setTeamAdmin(teamMemberDTO.getIsTeamAdmin() == 1);
        }
        // handle project team member
        List<ProjectTeam> projectTeams = teamService.listProjectTeamMemberInfoByUserId(orgId, userId);
        if (!CollectionUtils.isEmpty(projectTeams)) {
          List<ProjectTeamDTO> projectTeamDTOs = new ArrayList<>();
          for (ProjectTeam projectTeam : projectTeams) {
            ProjectTeamDTO projectTeamDTO = new ProjectTeamDTO();
            BeanUtils.copyProperties(projectTeam, projectTeamDTO);
            projectTeamDTOs.add(projectTeamDTO);
          }
          result.setProjectTeamDTOs(projectTeamDTOs);
        }
        // handle reporter
        long reporterUserId = userService.getReportorByUserIdAndOrgId(orgId, userId);
        if (reporterUserId > 0) {
          CoreUserProfile reporterCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, reporterUserId);
          result.setReporterFullName(reporterCUP.getFullName());
        }
        // handle reportee
        List<Long> reporteeIds = userService.listReporteesByUserIdAndOrgId(orgId, userId);
        if (!CollectionUtils.isEmpty(reporteeIds)) {
          result.setHasReportee(true);
        }
      }
      // handle role
      List<Role> roles = securityModelService.getRolesByUserId(orgId, userId);
      List<RoleDTO> roleDTOs = new ArrayList<>();
      for (Role role : roles) {
        RoleDTO roleDTO = new RoleDTO();
        BeanUtils.copyProperties(role, roleDTO);
        roleDTOs.add(roleDTO);
      }
      result.setRoleListDTO(roleDTOs);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getCoreUserProfile()-error", e);
    }

    return result;
  }

  // TODO: comment out on 2016-08-07; will delete when stable
//  public CoreUserProfileDTO getCoreUserProfile(long orgId, long userId, long actorUserId, long adminUserId) {
//
//    CoreUserProfileDTO result = new CoreUserProfileDTO();
//    ServiceStatusDTO serviceStatusDTO =
//        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
//    result.setServiceStatusDTO(serviceStatusDTO);
//
//    try {
//      CoreUserProfile coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
//      if (null != coreUserProfile) {
//        BeanUtils.copyProperties(coreUserProfile, result);
//
//        // handle jobTitleName
//        if (null != coreUserProfile.getJobTitle()) {
//          List<PickOption> jobTitles = profileFieldService
//              .listPickOptionsByOrgIdAndPickOptionIds(orgId, Arrays.asList(coreUserProfile.getJobTitle()));
//          if (!CollectionUtils.isEmpty(jobTitles)) {
//            result.setJobTitleName(jobTitles.get(0).getOptionValue());
//          }
//        }
//        // handle teamMemberInfo
//        List<TeamMemberInfo> teamMemberInfos = teamService.listTeamMemberInfoByUserIds(orgId, Arrays.asList(userId));
//        if (!CollectionUtils.isEmpty(teamMemberInfos)) {
//          TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
//          BeanUtils.copyProperties(teamMemberInfos.get(0), teamMemberDTO);
//          result.setTeamMemberDTO(teamMemberDTO);
//        }
//        // handle userEmployment
//        UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
//        UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO();
//        BeanUtils.copyProperties(userEmployment, userEmploymentDTO);
//        result.setUserEmploymentDTO(userEmploymentDTO);
//        // handle reporter
//        long reporterUserId = userService.getReportorByUserIdAndOrgId(orgId, userId);
//        if (reporterUserId > 0) {
//          CoreUserProfile reporterCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, reporterUserId);
//          result.setReporterFullName(reporterCUP.getFullName());
//        }
//        // handle role
//        if (userId == actorUserId) {
//          List<Role> roles = securityModelService.getRolesByUserId(orgId, userId);
//          List<RoleDTO> roleDTOs = new ArrayList<>();
//          for (Role role : roles) {
//            RoleDTO roleDTO = new RoleDTO();
//            BeanUtils.copyProperties(role, roleDTO);
//            roleDTOs.add(roleDTO);
//          }
//          result.setRoleListDTO(roleDTOs);
//        }
//      }
//
//
//    } catch (Exception e) {
//      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
//      LOGGER.error("getCoreUserProfile()-error", e);
//    }
//
//    return result;
//  }

  @LogAround
  @Override
  public CoreUserProfileListDTO listCoreUserProfile(
      long orgId, List<Long> userIds, long actorUserId, long adminUserId) {

    CoreUserProfileListDTO result = new CoreUserProfileListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      if (CollectionUtils.isEmpty(userIds)) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      List<CoreUserProfile>
          coreUserProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId, userIds);
      List<CoreUserProfileDTO> coreUserProfileDTOs = new ArrayList<>();
      if (!CollectionUtils.isEmpty(coreUserProfiles)) {
        // get jobTitles of org
        List<OrgPickOption> jobTitles = orgPickOptionService
            .listPickOptionOfConfigType(orgId, ConfigType.JOB_TITLE.getCode());
        Map<Long, OrgPickOption> jobTitleMap = new HashMap<>();
        for (OrgPickOption jobTitle: jobTitles) {
          jobTitleMap.put(jobTitle.getOrgPickOptionId(), jobTitle);
        }
        for (CoreUserProfile coreUserProfile : coreUserProfiles) {
          CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
          BeanUtils.copyProperties(coreUserProfile, coreUserProfileDTO);
          // handle jobTitleName
          if (jobTitleMap.containsKey(coreUserProfile.getJobTitle())) {
            coreUserProfileDTO.setJobTitleName(jobTitleMap.get(coreUserProfile.getJobTitle()).getOptionValue());
          }
          coreUserProfileDTOs.add(coreUserProfileDTO);
        }
        // handle team
        List<TeamMemberInfo> teamMemberInfos = teamService.listTeamMemberInfoByUserIds(orgId, userIds);
        if (!CollectionUtils.isEmpty(teamMemberInfos)) {
          Map<Long, TeamMemberInfo> teamMemberInfoMap = new HashMap<>();
          for (TeamMemberInfo teamMemberInfo : teamMemberInfos) {
            teamMemberInfoMap.put(teamMemberInfo.getUserId(), teamMemberInfo);
          }
          for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
            if (teamMemberInfoMap.containsKey(coreUserProfileDTO.getUserId())) {
              TeamMemberInfo teamMemberInfo = teamMemberInfoMap.get(coreUserProfileDTO.getUserId());
              TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
              BeanUtils.copyProperties(teamMemberInfo, teamMemberDTO);
              coreUserProfileDTO.setTeamMemberDTO(teamMemberDTO);
              coreUserProfileDTO.setTeamAdmin(teamMemberDTO.getIsTeamAdmin() == 1);
            }
          }
        }
        // handle userEmployment
        List<UserEmployment> userEmployments = userEmploymentService.listUserEmployment(orgId, userIds);
        if (!CollectionUtils.isEmpty(userEmployments)) {
          Map<Long, UserEmployment> userEmploymentMap = new HashMap<>();
          for (UserEmployment userEmployment : userEmployments) {
            userEmploymentMap.put(userEmployment.getUserId(), userEmployment);
          }
          for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
            if (userEmploymentMap.containsKey(coreUserProfileDTO.getUserId())) {
              UserEmployment userEmployment = userEmploymentMap.get(coreUserProfileDTO.getUserId());
              UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO();
              BeanUtils.copyProperties(userEmployment, userEmploymentDTO);
              coreUserProfileDTO.setUserEmploymentDTO(userEmploymentDTO);
            }
          }
        }
//        coreUserProfileDTOs = CoreUserProfileDTOHelper.removeSuperAdminFromList(coreUserProfileDTOs);
        // handle enrollDate
        batchSetEnrollDateInDTOs(userEmployments, coreUserProfileDTOs);
      }
      result.setCoreUserProfileDTOs(coreUserProfileDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listCoreUserProfile()-error", e);
    }

    return result;
  }

  // TODO: comment out on 2016-08-07; will delete when stable
//  @Override
//  public CoreUserProfileListDTO listCoreUserProfile(
//      long orgId, List<Long> userIds, long actorUserId, long adminUserId) {
//
//    CoreUserProfileListDTO result = new CoreUserProfileListDTO();
//    ServiceStatusDTO serviceStatusDTO =
//        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
//    result.setServiceStatusDTO(serviceStatusDTO);
//
//    try {
//      if (CollectionUtils.isEmpty(userIds)) {
//        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
//      }
//      List<CoreUserProfile>
//          coreUserProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId, userIds);
//      List<CoreUserProfileDTO> coreUserProfileDTOs = new ArrayList<>();
//      for (CoreUserProfile coreUserProfile : coreUserProfiles) {
//        CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
//        BeanUtils.copyProperties(coreUserProfile, coreUserProfileDTO);
//        coreUserProfileDTOs.add(coreUserProfileDTO);
//      }
//      // handle team
//      List<TeamMemberInfo> teamMemberInfos = teamService.listTeamMemberInfoByUserIds(orgId, userIds);
//      if (!CollectionUtils.isEmpty(teamMemberInfos)) {
//        Map<Long, TeamMemberInfo> teamMemberInfoMap = new HashMap<>();
//        for (TeamMemberInfo teamMemberInfo: teamMemberInfos) {
//          teamMemberInfoMap.put(teamMemberInfo.getUserId(), teamMemberInfo);
//        }
//        for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
//          if (teamMemberInfoMap.containsKey(coreUserProfileDTO.getUserId())) {
//            TeamMemberInfo teamMemberInfo = teamMemberInfoMap.get(coreUserProfileDTO.getUserId());
//            TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
//            BeanUtils.copyProperties(teamMemberInfo, teamMemberDTO);
//            coreUserProfileDTO.setTeamMemberDTO(teamMemberDTO);
//          }
//        }
//      }
//      // handle jobTitleNames
//      batchSetJobTitleNameInDTOs(orgId, coreUserProfiles, coreUserProfileDTOs);
//      // handle userEmployment
//      List<UserEmployment> userEmployments = userEmploymentService.listUserEmployment(orgId, userIds);
//      if (!CollectionUtils.isEmpty(userEmployments)) {
//        Map<Long, UserEmployment> userEmploymentMap = new HashMap<>();
//        for (UserEmployment userEmployment: userEmployments) {
//          userEmploymentMap.put(userEmployment.getUserId(), userEmployment);
//        }
//        for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
//          if (userEmploymentMap.containsKey(coreUserProfileDTO.getUserId())) {
//            UserEmployment userEmployment = userEmploymentMap.get(coreUserProfileDTO.getUserId());
//            UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO();
//            BeanUtils.copyProperties(userEmployment, userEmploymentDTO);
//            coreUserProfileDTO.setUserEmploymentDTO(userEmploymentDTO);
//          }
//        }
//      }
//      // handle enrollDate
//      batchSetEnrollDateInDTOs(userEmployments, coreUserProfileDTOs);
//
//      result.setCoreUserProfileDTOs(coreUserProfileDTOs);
//    } catch (Exception e) {
//      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
//      LOGGER.error("listCoreUserProfile()-error", e);
//    }
//
//    return result;
//  }

  @Override
  @LogAround
  public CoreUserProfileListDTO listCoreUserProfileOfNewStaffByOrgId(long orgId, long actorUserId, long adminUserId) {

    CoreUserProfileListDTO result = new CoreUserProfileListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> matchedUserIds = userEmploymentService.listUserIdOfNewStaffByOrgId(orgId);
      List<CoreUserProfile> coreUserProfiles = userProfileService
          .listCoreUserProfileByOrgIdAndUserId(orgId, matchedUserIds);
      List<CoreUserProfileDTO> coreUserProfileDTOs = new ArrayList<>();
      for (CoreUserProfile coreUserProfile : coreUserProfiles) {
        CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
        BeanUtils.copyProperties(coreUserProfile, coreUserProfileDTO);
        coreUserProfileDTOs.add(coreUserProfileDTO);
      }
      // handle jobTitleNames
      List<OrgPickOption> jobTitles = orgPickOptionService
          .listPickOptionOfConfigType(orgId, ConfigType.JOB_TITLE.getCode());
      batchSetJobTitleNameInDTOs(coreUserProfiles, coreUserProfileDTOs, jobTitles);
      // handle userEmployment
      List<UserEmployment> userEmployments = userEmploymentService.listUserEmployment(orgId, matchedUserIds);
      if (!CollectionUtils.isEmpty(userEmployments)) {
        Map<Long, UserEmployment> userEmploymentMap = new HashMap<>();
        for (UserEmployment userEmployment: userEmployments) {
          userEmploymentMap.put(userEmployment.getUserId(), userEmployment);
        }
        for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
          if (userEmploymentMap.containsKey(coreUserProfileDTO.getUserId())) {
            UserEmployment userEmployment = userEmploymentMap.get(coreUserProfileDTO.getUserId());
            UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO();
            BeanUtils.copyProperties(userEmployment, userEmploymentDTO);
            coreUserProfileDTO.setUserEmploymentDTO(userEmploymentDTO);
          }
        }
      }
//      coreUserProfileDTOs = CoreUserProfileDTOHelper.removeSuperAdminFromList(coreUserProfileDTOs);
      // handle enrollDate
      batchSetEnrollDateInDTOs(userEmployments, coreUserProfileDTOs);
      result.setCoreUserProfileDTOs(coreUserProfileDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listCoreUserProfileOfNewStaffByOrgId()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public CoreUserProfileListDTO listCoreUserProfileOfEnrollAnniversaryByOrgId(
      long orgId, long actorUserId, long adminUserId) {

    CoreUserProfileListDTO result = new CoreUserProfileListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      List<Long> matchedUserIds = userEmploymentService.listUserIdOfEnrollAnniversaryByOrgId(orgId);
      List<CoreUserProfile> coreUserProfiles = userProfileService
          .listCoreUserProfileByOrgIdAndUserId(orgId, matchedUserIds);
      List<CoreUserProfileDTO> coreUserProfileDTOs = new ArrayList<>();
      for (CoreUserProfile coreUserProfile : coreUserProfiles) {
        CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
        BeanUtils.copyProperties(coreUserProfile, coreUserProfileDTO);
        coreUserProfileDTOs.add(coreUserProfileDTO);
      }
      // handle jobTitleNames
      List<OrgPickOption> jobTitles = orgPickOptionService
          .listPickOptionOfConfigType(orgId, ConfigType.JOB_TITLE.getCode());
      batchSetJobTitleNameInDTOs(coreUserProfiles, coreUserProfileDTOs, jobTitles);
      // handle userEmployment
      List<UserEmployment> userEmployments = userEmploymentService.listUserEmployment(orgId, matchedUserIds);
      if (!CollectionUtils.isEmpty(userEmployments)) {
        Map<Long, UserEmployment> userEmploymentMap = new HashMap<>();
        for (UserEmployment userEmployment: userEmployments) {
          userEmploymentMap.put(userEmployment.getUserId(), userEmployment);
        }
        for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
          if (userEmploymentMap.containsKey(coreUserProfileDTO.getUserId())) {
            UserEmployment userEmployment = userEmploymentMap.get(coreUserProfileDTO.getUserId());
            UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO();
            BeanUtils.copyProperties(userEmployment, userEmploymentDTO);
            coreUserProfileDTO.setUserEmploymentDTO(userEmploymentDTO);
          }
        }
      }
//      coreUserProfileDTOs = CoreUserProfileDTOHelper.removeSuperAdminFromList(coreUserProfileDTOs);
      // handle enrollDate
      batchSetEnrollDateInDTOs(userEmployments, coreUserProfileDTOs);
      result.setCoreUserProfileDTOs(coreUserProfileDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listCoreUserProfileOfEnrollAnniversaryByOrgId()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public AddressRegionListDTO listAddressRegion(long orgId, long parentId, long actorUserId, long adminUserId) {

    AddressRegionListDTO result = new AddressRegionListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<AddressRegion> addressRegions = profileFieldService.listAddressRegion(parentId);
      List<AddressRegionDTO> addressRegionDTOs = null;
      if (!CollectionUtils.isEmpty(addressRegions)) {
        addressRegionDTOs = new ArrayList<>();
        for (int i = 0; i < addressRegions.size(); i++) {
          AddressRegionDTO addressRegionDTO = new AddressRegionDTO();
          BeanUtils.copyProperties(addressRegions.get(i), addressRegionDTO);
          addressRegionDTOs.add(addressRegionDTO);
        }
      } else {
        addressRegionDTOs = Collections.EMPTY_LIST;
      }
      result.setAddressRegionDTOs(addressRegionDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listAddressRegion()-error", e);
    }

    return result;
  }

  /**
   * Cannot update userStatus & onboardingStatus in this method
   *
   * @param orgId
   * @param userEmploymentDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public VoidDTO updateUserEmployment(
      long orgId, UserEmploymentDTO userEmploymentDTO, long actorUserId, long adminUserId) {


    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      UserEmployment userEmployment = new UserEmployment();
      BeanUtils.copyProperties(userEmploymentDTO, userEmployment);
      // Cannot update userStatus & onboardingStatus in this method
      userEmployment.setUserStatus(null);
      userEmployment.setOnboardingStatus(null);
      userEmployment.setEmploymentStatus(null);
      userEmployment.setOrgId(orgId);
      userEmployment.setLastModifiedUserId(actorUserId);
      userEmploymentService.updateUserEmployment(userEmployment);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateUserEmployment()-error", e);
    }

    return result;

  }

  /**
   * Steps:
   *  1) add jobTransfer
   *  2) handle email & notification
   *
   * @param orgId
   * @param jobTransferRequestDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public LongDTO addJobTransfer(
      long orgId, JobTransferRequestDTO jobTransferRequestDTO, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      // 1)
      JobTransfer jobTransfer = new JobTransfer();
      BeanUtils.copyProperties(jobTransferRequestDTO, jobTransfer);
      jobTransfer.setOrgId(orgId);
      jobTransfer.setCreatedUserId(actorUserId);
      long jobTransferId = employeeManagementService.addJobTransfer(jobTransfer);
      result.setData(jobTransferId);

      // 2)
      JobTransferResponseDTO jobTransferResponseDTO = new JobTransferResponseDTO();
      copyAllFieldsFromJobTransferToDTO(jobTransfer, jobTransferResponseDTO);
      setToNotifyUserIds(jobTransfer, jobTransferResponseDTO);
      Org org = orgService.getOrg(orgId);
      CoreUserProfile hrCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, actorUserId);
      CoreUserProfile staffCUP = userProfileService
          .getCoreUserProfileByOrgIdAndUserId(orgId, jobTransferRequestDTO.getUserId());
      List<Long> toNotifyUserIds = employeeManagementService
          .listToNotifyUserIds(orgId, jobTransferId, UserSysNotificationType.JOB_TRANSFER.getCode());
      List<CoreUserProfile> toNotifyUserCUPs = userProfileService
          .listCoreUserProfileByOrgIdAndUserId(orgId, toNotifyUserIds);
      employeeManagementNotifier.sendEmailAndMessageAfterJobTransfer(
          org, jobTransferResponseDTO, hrCUP, staffCUP, toNotifyUserCUPs);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addJobTransfer()-error", e);
    }

    return result;
  }

  /**
   * Steps:
   *  1) get jobTransfer
   *  2) fill all fields
   *  3) get toNotifyUserIds
   *
   * @param orgId
   * @param jobTransferid
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public JobTransferResponseDTO getJobTransfer(long orgId, long jobTransferid, long actorUserId, long adminUserId) {

    JobTransferResponseDTO result = new JobTransferResponseDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      // 1)
      JobTransfer jobTransfer = employeeManagementService.getJobTransfer(orgId, jobTransferid);

      // 2)
      copyAllFieldsFromJobTransferToDTO(jobTransfer, result);

      // 3)
      setToNotifyUserIds(jobTransfer, result);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getJobTransfer()-error", e);
    }

    return result;
  }

  /**
   *  1) get jobTransfer
   *  2) fill all fields
   *  3) batch get toNotifyUserIds
   *
   * @param orgId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public JobTransferResponseListDTO listJobTransfer(
      long orgId, int pageNumber, int pageSize, long actorUserId, long adminUserId) {

    JobTransferResponseListDTO result = new JobTransferResponseListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // TODO: opt to reduce calls
      // 1)
      List<JobTransfer> jobTransfers = employeeManagementService.listJobTransfer(orgId, pageNumber, pageSize);
      List<JobTransferResponseDTO> jobTransferResponseDTOs = new ArrayList<>();
      for (JobTransfer jobTransfer: jobTransfers) {
        JobTransferResponseDTO jobTransferResponseDTO = new JobTransferResponseDTO();
        // 2)
        copyAllFieldsFromJobTransferToDTO(jobTransfer, jobTransferResponseDTO);
        // 3)
        setToNotifyUserIds(jobTransfer, jobTransferResponseDTO);
        jobTransferResponseDTOs.add(jobTransferResponseDTO);
      }
      result.setJobTransferDTOs(jobTransferResponseDTOs);
      int totalNumber = employeeManagementService.countJobTransferByOrgId(orgId);
      result.setTotalNumber(totalNumber);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listJobTransfer()-error", e);
    }

    return result;
  }

  @Override
  public JobTransferResponseListDTO listJobTransferByJobTransferIds(
      long orgId, List<Long> jobTransferIds, long actorUserId, long adminUserId) {

    JobTransferResponseListDTO result = new JobTransferResponseListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<JobTransfer> jobTransfers = employeeManagementService.listJobTransfer(orgId, jobTransferIds);
      List<JobTransferResponseDTO> jobTransferResponseDTOs = new ArrayList<>();
      for (JobTransfer jobTransfer: jobTransfers) {
        JobTransferResponseDTO jobTransferResponseDTO = new JobTransferResponseDTO();
        copyAllFieldsFromJobTransferToDTO(jobTransfer, jobTransferResponseDTO);
        jobTransferResponseDTOs.add(jobTransferResponseDTO);
      }
      result.setJobTransferDTOs(jobTransferResponseDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listJobTransferByJobTransferIds()-error", e);
    }

    return result;
  }

  /**
   * Steps:
   *  1) add statusUpdate
   *  2) handle email and message
   *
   * @param orgId
   * @param statusUpdateDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public LongDTO addPassProbationStatusUpdate(
      long orgId, StatusUpdateDTO statusUpdateDTO, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      // 1)
      StatusUpdate statusUpdate = new StatusUpdate();
      BeanUtils.copyProperties(statusUpdateDTO, statusUpdate);
      statusUpdate.setOrgId(orgId);
      statusUpdate.setCreatedUserId(actorUserId);
      long statusUpdateId = employeeManagementService.addPassProbationStatusUpdate(statusUpdate);
      result.setData(statusUpdateId);

      // 2)
      long staffUserId = statusUpdate.getUserId();
      Org org = orgService.getOrg(orgId);
      CoreUserProfile staffCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, staffUserId);
      CoreUserProfileDTO staffCUPDTO = new CoreUserProfileDTO();
      BeanUtils.copyProperties(staffCUP, staffCUPDTO);
      if (null != staffCUP.getJobTitle()) {
        List<OrgPickOption> jobTitles = orgPickOptionService
            .listPickOptionOfConfigType(orgId, ConfigType.JOB_TITLE.getCode());
        for (OrgPickOption jobTitle: jobTitles) {
          if (staffCUP.getJobTitle().equals(jobTitle.getOrgPickOptionId())) {
            staffCUPDTO.setJobTitleName(jobTitle.getOptionValue());
          }
        }
//        List<PickOption> jobTitles = profileFieldService
//            .listPickOptionsByOrgIdAndPickOptionIds(orgId, Arrays.asList(staffCUP.getJobTitle()));
//        if (!CollectionUtils.isEmpty(jobTitles)) {
//          staffCUPDTO.setJobTitleName(jobTitles.get(0).getOptionValue());
//        }
      }
      List<TeamMemberInfo> teamMemberInfos = teamService.listTeamMemberInfoByUserIds(orgId, Arrays.asList(staffUserId));
      if (!CollectionUtils.isEmpty(teamMemberInfos)) {
        TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
        BeanUtils.copyProperties(teamMemberInfos.get(0), teamMemberDTO);
        staffCUPDTO.setTeamMemberDTO(teamMemberDTO);
      }
      List<Long> toNotifyUserIds = employeeManagementService
          .listToNotifyUserIds(orgId, statusUpdateId, UserSysNotificationType.PASS_PROBATION.getCode());
      List<CoreUserProfile> toNotifyUserCUPs = userProfileService
          .listCoreUserProfileByOrgIdAndUserId(orgId, toNotifyUserIds);
      employeeManagementNotifier.sendEmailAndMessageAfterPassProbation(
          org, statusUpdate, staffCUPDTO, toNotifyUserCUPs, actorUserId);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addPassProbationStatusUpdate()-error", e);
    }

    return result;
  }

  /**
   * Steps:
   *  1) add statusUpdate
   *  2) handle email and message
   *
   * @param orgId
   * @param statusUpdateDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public LongDTO addResignStatusUpdate(
      long orgId, StatusUpdateDTO statusUpdateDTO, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      // 1)
      StatusUpdate statusUpdate = new StatusUpdate();
      BeanUtils.copyProperties(statusUpdateDTO, statusUpdate);
      statusUpdate.setOrgId(orgId);
      statusUpdate.setCreatedUserId(actorUserId);
      long statusUpdateId = employeeManagementService.addResignStatusUpdate(statusUpdate);
      result.setData(statusUpdateId);

      // 2)
      long staffUserId = statusUpdate.getUserId();
      Org org = orgService.getOrg(orgId);
      CoreUserProfile staffCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, staffUserId);
      CoreUserProfileDTO staffCUPDTO = new CoreUserProfileDTO();
      BeanUtils.copyProperties(staffCUP, staffCUPDTO);
      if (null != staffCUP.getJobTitle()) {
        List<OrgPickOption> jobTitles = orgPickOptionService
            .listPickOptionOfConfigType(orgId, ConfigType.JOB_TITLE.getCode());
        for (OrgPickOption jobTitle: jobTitles) {
          if (staffCUP.getJobTitle().equals(jobTitle.getOrgPickOptionId())) {
            staffCUPDTO.setJobTitleName(jobTitle.getOptionValue());
          }
        }
//        List<PickOption> jobTitles = profileFieldService
//            .listPickOptionsByOrgIdAndPickOptionIds(orgId, Arrays.asList(staffCUP.getJobTitle()));
//        if (!CollectionUtils.isEmpty(jobTitles)) {
//          staffCUPDTO.setJobTitleName(jobTitles.get(0).getOptionValue());
//        }
      }
      List<TeamMemberInfo> teamMemberInfos = teamService.listTeamMemberInfoByUserIds(orgId, Arrays.asList(staffUserId));
      if (!CollectionUtils.isEmpty(teamMemberInfos)) {
        TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
        BeanUtils.copyProperties(teamMemberInfos.get(0), teamMemberDTO);
        staffCUPDTO.setTeamMemberDTO(teamMemberDTO);
      }
      List<Long> toNotifyUserIds = employeeManagementService
          .listToNotifyUserIds(orgId, statusUpdateId, UserSysNotificationType.RESIGN.getCode());
      List<CoreUserProfile> toNotifyUserCUPs = userProfileService
          .listCoreUserProfileByOrgIdAndUserId(orgId, toNotifyUserIds);
      employeeManagementNotifier.sendEmailAndMessageAfterResign(
          org, statusUpdate, staffCUPDTO, toNotifyUserCUPs, actorUserId);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addResignStatusUpdate()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public StatusUpdateDTO getStatusUpdate(long orgId, long statusUpdateId, long actorUserId, long adminUserId) {

    StatusUpdateDTO result = new StatusUpdateDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      StatusUpdate statusUpdate = employeeManagementService.getStatusUpdate(orgId, statusUpdateId);
      BeanUtils.copyProperties(statusUpdate, result);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getStatusUpdate()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public StatusUpdateListDTO listStatusUpdate(
      long orgId, int statusType, int pageNumber, int pageSize, long actorUserId, long adminUserId) {

    StatusUpdateListDTO result = new StatusUpdateListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<StatusUpdate> statusUpdates = employeeManagementService
          .listStatusUpdate(orgId, statusType, pageNumber, pageSize);
      List<StatusUpdateDTO> statusUpdateDTOs = null;
      if (!CollectionUtils.isEmpty(statusUpdates)) {
        statusUpdateDTOs = new ArrayList<>();
        for (StatusUpdate statusUpdate: statusUpdates) {
          StatusUpdateDTO statusUpdateDTO = new StatusUpdateDTO();
          BeanUtils.copyProperties(statusUpdate, statusUpdateDTO);
          statusUpdateDTOs.add(statusUpdateDTO);
        }
      } else {
        statusUpdateDTOs = Collections.EMPTY_LIST;
      }
      result.setStatusUpdateDTOs(statusUpdateDTOs);
      int totalNumber = employeeManagementService.countStatusUpdate(orgId, statusType);
      result.setTotalNumber(totalNumber);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listStatusUpdate()-error", e);
    }

    return result;
  }

  @Override
  public StatusUpdateListDTO listStatusUpdateByStatusUpdateIds(
      long orgId, List<Long> statusUpdateIds, long actorUserId, long adminUserId) {

    StatusUpdateListDTO result = new StatusUpdateListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<StatusUpdate> statusUpdates = employeeManagementService.listStatusUpdate(orgId, statusUpdateIds);
      List<StatusUpdateDTO> statusUpdateDTOs = null;
      if (!CollectionUtils.isEmpty(statusUpdates)) {
        statusUpdateDTOs = new ArrayList<>();
        for (StatusUpdate statusUpdate: statusUpdates) {
          StatusUpdateDTO statusUpdateDTO = new StatusUpdateDTO();
          BeanUtils.copyProperties(statusUpdate, statusUpdateDTO);
          statusUpdateDTOs.add(statusUpdateDTO);
        }
      } else {
        statusUpdateDTOs = Collections.EMPTY_LIST;
      }
      result.setStatusUpdateDTOs(statusUpdateDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listStatusUpdateByStatusUpdateIds()-error", e);
    }

    return result;
  }

  @Override
  public VoidDTO revokePassProbationStatusUpdate(
      long orgId, long statusUpdateId, int statusUpdateType, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      employeeManagementService.revokePassProbationStatusUpdate(orgId, statusUpdateId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("revokePassProbationStatusUpdate()-error", e);
    }

    return result;
  }

  @Override
  public VoidDTO revokeResignStatusUpdate(
      long orgId, long statusUpdateId, int statusUpdateType, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      employeeManagementService.revokeResignStatusUpdate(orgId, statusUpdateId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("revokeResignStatusUpdate()-error", e);
    }

    return result;
  }

  @Override
  public VoidDTO deleteUser(long orgId, long userId, long actorUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      employeeManagementService.deleteUser(orgId, userId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("deleteUser()-error", e);
    }

    return result;
  }

  /**
   * Steps:
   *  1) validate requested fields
   *  2) populate meta of fields in UserEmployment
   *  3) populate meta of fields in CUP & BUP
   *  4) populate meta of fields in MUP
   *
   * @param orgId
   * @param refefenceNames
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public StringDTO getHeaderOfRosterFile(long orgId, List<String> refefenceNames, long actorUserId, long adminUserId) {

    StringDTO result = new StringDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      if (CollectionUtils.isEmpty(refefenceNames)) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }
      StringUtils.trimStringInList(refefenceNames);

      // 1)
      List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfOrg(orgId);
      List<ProfileField> availableProfileFields = new ArrayList<>();
      for (ProfileField profileField: profileFields) {
        if (RosterAvailabilityHelper.isAvailableProfileField(profileField.getReferenceName(), profileField.getDataType())) {
          availableProfileFields.add(profileField);
        }
      }
      if (!RosterHelper.isValidRequestOfRosterFile(refefenceNames, availableProfileFields)) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }

      StringBuilder header = new StringBuilder("姓名 (请勿修改), 企业邮箱 (请勿修改)");
      List<String> userEmploymentReferenceName = new ArrayList<>();
      List<String> systemReferenceNames = new ArrayList<>();
      List<String> customizedReferenceNames = new ArrayList<>();
      for (String referenceName: refefenceNames) {
        if (null != UserEmploymentProfileField.getEnumByReferenceName(referenceName)) {
          userEmploymentReferenceName.add(referenceName);
        } else if (null != SystemProfileField.getEnumByReferenceName(referenceName)) {
          systemReferenceNames.add(referenceName);
        } else {
          customizedReferenceNames.add(referenceName);
        }
      }
      List<ProfileField> systemProfileFields = new ArrayList<>();
      List<ProfileField> customizedProfileFields = new ArrayList<>();
      for (ProfileField profileField: availableProfileFields) {
        if (profileField.getIsSystemRequired() == 1) {
          systemProfileFields.add(profileField);
        } else {
          customizedProfileFields.add(profileField);
        }
      }

      // 2)
      RosterHelper.fillUserEmploymentProfileFieldInHeaderOfRosterFile(refefenceNames, header);

      // 3)
      List<OrgPickOption> jobTitles = orgPickOptionService
          .listPickOptionOfConfigType(orgId, ConfigType.JOB_TITLE.getCode());
      List<OrgPickOption> jobLevels = orgPickOptionService
          .listPickOptionOfConfigType(orgId, ConfigType.JOB_LEVEL.getCode());
      RosterHelper.fillSystemProfileFieldInHeaderOfRosterFile(
          systemReferenceNames, systemProfileFields, jobTitles, jobLevels, header);

      // 4)
      Map<Long, List<PickOption>> pickOptionMap = new HashMap<>();
      for (ProfileField profileField: customizedProfileFields) {
        if (DataType.SINGLE_PICK.getCode() == profileField.getDataType()
            || DataType.MULTI_PICK.getCode() == profileField.getDataType()) {
          List<PickOption> pickOptions = profileFieldService
              .listPickOptionByOrgIdAndProfileFieldIdForUpdate(orgId, profileField.getProfileFieldId());
          pickOptionMap.put(profileField.getProfileFieldId(), pickOptions);
        }
      }
      RosterHelper.fillCustomizedProfileFieldInHeaderOfRosterFile(
          customizedReferenceNames, customizedProfileFields, pickOptionMap, header);

      header.append("\n");

      result.setData(header.toString());
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getHeaderOfRosterFile()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public StringListDTO listStaffOfRosterFile(long orgId, long actorUserId, long adminUserId) {

    StringListDTO result = new StringListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<String> staff = new ArrayList<>();
      List<CoreUserProfile> coreUserProfiles = userProfileService.listFullNameAndEmailAddressWhichIsNotResigned(orgId);
      for (CoreUserProfile coreUserProfile: coreUserProfiles) {
        staff.add(coreUserProfile.getFullName() + "," + coreUserProfile.getEmailAddress());
      }
      result.setData(staff);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listStaffOfRosterFile()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public IntegerDTO batchUpdateRosterData(
      long orgId, List<String> headers, List<List<String>> rawFieldLists, long actorUserId, long adminUserId) {
    IntegerDTO result = new IntegerDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      StringUtils.trimStringInList(headers);
      for (List<String> oneList: rawFieldLists) {
        StringUtils.trimStringInList(oneList);
      }
      int updatedUserCount = userProfileService.batchUpdateRosterData(orgId, headers, rawFieldLists, actorUserId);
      result.setData(updatedUserCount);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("batchUpdateRosterData()-error", e);
    }

    return result;
  }


  private void batchSetJobTitleNameInDTOs(List<CoreUserProfile> coreUserProfiles,
                                          List<CoreUserProfileDTO> coreUserProfileDTOs,
                                          List<OrgPickOption> jobTitles)  {

    if (CollectionUtils.isEmpty(coreUserProfiles)
        || CollectionUtils.isEmpty(coreUserProfileDTOs)
        || CollectionUtils.isEmpty(jobTitles)) {
      return;
    }

    Map<Long, OrgPickOption> jobTitleMap = new HashMap<>();
    for (OrgPickOption jobTitle: jobTitles) {
      jobTitleMap.put(jobTitle.getOrgPickOptionId(), jobTitle);
    }
    Map<Long, CoreUserProfile> coreUserProfileMap = new HashMap<>();
    for (CoreUserProfile coreUserProfile: coreUserProfiles) {
      coreUserProfileMap.put(coreUserProfile.getUserId(), coreUserProfile);
    }

    for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
      long userId = coreUserProfileDTO.getUserId();
      if (null != coreUserProfileMap.get(userId).getJobTitle()) {
        long jobTitleId = coreUserProfileMap.get(userId).getJobTitle();
        if (null != jobTitleMap.get(jobTitleId)) {
          coreUserProfileDTO.setJobTitleName(jobTitleMap.get(jobTitleId).getOptionValue());
        }
      }
    }

  }

  private void batchSetEnrollDateInDTOs(
      List<UserEmployment> userEmployments, List<CoreUserProfileDTO> coreUserProfileDTOs) {

    Map<Long, UserEmployment> userEmploymentMap = new HashMap<>();
    for (UserEmployment userEmployment: userEmployments) {
      userEmploymentMap.put(userEmployment.getUserId(), userEmployment);
    }
    for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
      if (userEmploymentMap.containsKey(coreUserProfileDTO.getUserId())) {
        UserEmployment userEmployment = userEmploymentMap.get(coreUserProfileDTO.getUserId());
        coreUserProfileDTO.setEnrollDate(UserEmploymentHelper.getEnrollDate(userEmployment));
      }
    }

  }

  private void copyAllFieldsFromJobTransferToDTO(JobTransfer jobTransfer, JobTransferResponseDTO result) {

    long orgId = jobTransfer.getOrgId();

    BeanUtils.copyProperties(jobTransfer, result);

    // fill reporter
    Long userId = jobTransfer.getUserId();
    Long beforeReporterId = jobTransfer.getBeforeReporterId();
    Long afterReporterId = jobTransfer.getAfterReporterId();
    List<Long> userIds = new ArrayList<>();
    if (null != userId) {
      userIds.add(userId);
    }
    if (null != beforeReporterId) {
      userIds.add(beforeReporterId);
    }
    if (null != afterReporterId) {
      userIds.add(afterReporterId);
    }
    List<CoreUserProfile> coreUserProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId, userIds);
    Map<Long, CoreUserProfile> coreUserProfileMap = new HashMap<>();
    for (CoreUserProfile coreUserProfile : coreUserProfiles) {
      coreUserProfileMap.put(coreUserProfile.getUserId(), coreUserProfile);
    }
    if (null != userId
        && coreUserProfileMap.containsKey(userId)) {
      SimpleUserProfileDTO userSUP = new SimpleUserProfileDTO();
      BeanUtils.copyProperties(coreUserProfileMap.get(userId), userSUP);
      result.setUserSimpleUserProfileDTO(userSUP);
    }
    if (null != beforeReporterId
        && coreUserProfileMap.containsKey(beforeReporterId)) {
      SimpleUserProfileDTO beforeReporterSUP = new SimpleUserProfileDTO();
      BeanUtils.copyProperties(coreUserProfileMap.get(beforeReporterId), beforeReporterSUP);
      result.setBeforeReporterSimpleUserProfileDTO(beforeReporterSUP);
    }
    if (null != afterReporterId
        && coreUserProfileMap.containsKey(afterReporterId)) {
      SimpleUserProfileDTO afterReporterSUP = new SimpleUserProfileDTO();
      BeanUtils.copyProperties(coreUserProfileMap.get(afterReporterId), afterReporterSUP);
      result.setAfterReporterSimpleUserProfileDTO(afterReporterSUP);
    }

    // fill team
    Long beforeTeamId = jobTransfer.getBeforeTeamId();
    Long afterTeamId = jobTransfer.getAfterTeamId();
    List<Team> teams = teamService.listAllTeams(orgId);
    Map<Long, Team> teamMap = new HashMap<>();
    for (Team team: teams) {
      teamMap.put(team.getTeamId(), team);
    }
    if (null != beforeTeamId
        && teamMap.containsKey(beforeTeamId)) {
      TeamDTO beforeTeamDTO = new TeamDTO();
      BeanUtils.copyProperties(teamMap.get(beforeTeamId), beforeTeamDTO);
      result.setBeforeTeamDTO(beforeTeamDTO);
    }
    if (null != afterTeamId
        && teamMap.containsKey(afterTeamId)) {
      TeamDTO afterTeamDTO = new TeamDTO();
      BeanUtils.copyProperties(teamMap.get(afterTeamId), afterTeamDTO);
      result.setAfterTeamDTO(afterTeamDTO);
    }

    // fill jobTitle & jobLevel
    Long beforeJobTitleId = jobTransfer.getBeforeJobTitleId();
    Long beforeJobLevelId = jobTransfer.getBeforeJobLevelId();
    Long afterJobTitleId = jobTransfer.getAfterJobTitleId();
    Long afterJobLevelId = jobTransfer.getAfterJobLevelId();
    List<OrgPickOption> jobTitles = orgPickOptionService
        .listPickOptionOfConfigType(orgId,ConfigType.JOB_TITLE.getCode());
    List<OrgPickOption> jobLevels = orgPickOptionService
        .listPickOptionOfConfigType(orgId, ConfigType.JOB_LEVEL.getCode());
    if (!CollectionUtils.isEmpty(jobTitles)) {
      for (OrgPickOption jobTitle: jobTitles) {
        if (null != beforeJobTitleId
            && beforeJobTitleId.equals(jobTitle.getOrgPickOptionId())) {
          OrgPickOptionDTO orgPickOptionDTO = new OrgPickOptionDTO();
          BeanUtils.copyProperties(jobTitle, orgPickOptionDTO);
          result.setBeforeJobTitleOrgPickOptionDTO(orgPickOptionDTO);
        }
        if (null != afterJobTitleId
            && afterJobTitleId.equals(jobTitle.getOrgPickOptionId())) {
          OrgPickOptionDTO orgPickOptionDTO = new OrgPickOptionDTO();
          BeanUtils.copyProperties(jobTitle, orgPickOptionDTO);
          result.setAfterJobTitleOrgPickOptionDTO(orgPickOptionDTO);
        }
      }
    }
    if (!CollectionUtils.isEmpty(jobLevels)) {
      for (OrgPickOption jobLevel: jobLevels) {
        if (null != beforeJobLevelId
            && beforeJobLevelId.equals(jobLevel.getOrgPickOptionId())) {
          OrgPickOptionDTO orgPickOptionDTO = new OrgPickOptionDTO();
          BeanUtils.copyProperties(jobLevel, orgPickOptionDTO);
          result.setBeforeJobLevelOrgPickOptionDTO(orgPickOptionDTO);
        }
        if (null != afterJobLevelId
            && afterJobLevelId.equals(jobLevel.getOrgPickOptionId())) {
          OrgPickOptionDTO orgPickOptionDTO = new OrgPickOptionDTO();
          BeanUtils.copyProperties(jobLevel, orgPickOptionDTO);
          result.setAfterJobLevelOrgPickOptionDTO(orgPickOptionDTO);
        }
      }
    }
  }

  private void setToNotifyUserIds(JobTransfer jobTransfer, JobTransferResponseDTO jobTransferResponseDTO) {
    if (!CollectionUtils.isEmpty(jobTransfer.getToNotifyUserIds())) {
      List<CoreUserProfile> coreUserProfiles = userProfileService
          .listCoreUserProfileByOrgIdAndUserId(jobTransfer.getOrgId(), jobTransfer.getToNotifyUserIds());
      Map<Long, CoreUserProfile> coreUserProfileMap = CoreUserProfileHelper
          .convertCoreUserProfileListToMap(coreUserProfiles);
      List<SimpleUserProfileDTO> simpleUserProfileDTOs = new ArrayList<>();
      for (int i = 0; i < jobTransfer.getToNotifyUserIds().size(); i++) {
        long userId = jobTransfer.getToNotifyUserIds().get(i);
        if (coreUserProfileMap.containsKey(userId)) {
          SimpleUserProfileDTO simpleUserProfileDTO = new SimpleUserProfileDTO();
          BeanUtils.copyProperties(coreUserProfileMap.get(userId), simpleUserProfileDTO);
          simpleUserProfileDTOs.add(simpleUserProfileDTO);
        }
      }
      jobTransferResponseDTO.setSimpleUserProfileDTOs(simpleUserProfileDTOs);
    }
  }

}
