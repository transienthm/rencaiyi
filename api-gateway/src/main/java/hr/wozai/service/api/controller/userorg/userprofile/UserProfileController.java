package hr.wozai.service.api.controller.userorg.userprofile;

import com.alibaba.fastjson.JSONObject;

import hr.wozai.service.api.component.ProfileMetaPermissionChecker;
import hr.wozai.service.api.vo.orgteam.ProjectTeamVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;

import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import hr.wozai.service.api.component.UserProfilePermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.user.AddressRegionListVO;
import hr.wozai.service.api.vo.user.AddressRegionVO;
import hr.wozai.service.api.vo.user.CoreUserProfileListVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.api.vo.user.ProfileFieldVO;
import hr.wozai.service.api.vo.user.UserEmploymentVO;
import hr.wozai.service.api.vo.user.UserProfileVO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/8
 */
@Controller("coreUserProfileController")
public class UserProfileController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserProfileController.class);

  private static final String CSV_DOWNLOAD_CONTENT_TYPE = "application/octet-stream; charset=utf-8";
  private static final String CSV_DOWNLOAD_CONTENT_DISPOSITION_KEY = "Content-Disposition";
  private static final String CSV_DOWNLOAD_CONTENT_DISPOSITION_VALUE = "attachment; filename=staff.csv";

  private static final String PARAM_ACCESS_TOKEN = "X-Access-Token";
  private static final String PARAM_REFRESH_TOKEN = "X-Refresh-Token";
  private static final String PARAM_REFERENCE_NAMES = "referenceNames";

  @Autowired
  private UserProfilePermissionChecker userProfilePermissionChecker;

  @Autowired
  private ProfileMetaPermissionChecker profileMetaPermissionChecker;

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  PermissionUtil permissionUtil;

  /**************** Methods after refraction(2016-08-08) below ****************/

  @LogAround

  @RequestMapping(
      value = "/users/{userId}/complete-profile",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<UserProfileVO> getUserProfileOfAllFields(
      @PathVariable("userId") String encryptedUserId
  ) {

    Result<UserProfileVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long userId = 0;

    try {
      userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
    } catch (Exception e) {
      LOGGER.error("getUserProfileOfAllFields()-error: invalid userId");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!userProfilePermissionChecker.canRead(authedOrgId, authedActorUserId, userId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      UserProfileDTO rpcGetResult = facadeFactory.getUserProfileFacade().getUserProfile(
          authedOrgId, userId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcGetResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        UserProfileVO userProfileVO = new UserProfileVO();
        BeanUtils.copyProperties(rpcGetResult, userProfileVO);
        // handle UserEmployment
        UserEmploymentDTO userEmploymentDTO = rpcGetResult.getUserEmploymentDTO();
        UserEmploymentVO userEmploymentVO = new UserEmploymentVO();
        if (null != userEmploymentDTO) {
          BeanUtils.copyProperties(userEmploymentDTO, userEmploymentVO);
        }
        userProfileVO.setUserEmploymentVO(userEmploymentVO);
        int userStatus = userEmploymentDTO.getUserStatus();
        boolean isSelf = (userId == authedActorUserId) ? true : false;
        List<ProfileFieldVO> profileFieldVOs = new ArrayList<>();
        for (ProfileFieldDTO profileFieldDTO: rpcGetResult.getProfileFieldDTOs()) {
          ProfileFieldVO profileFieldVO = new ProfileFieldVO();
          BeanHelper.copyPropertiesHandlingJSON(profileFieldDTO, profileFieldVO);
          if (!isSelf
              || (userStatus == UserStatus.ACTIVE.getCode()
                  && profileFieldDTO.getIsActiveStaffEditable() == 1)
              || (userStatus == UserStatus.INVITED.getCode()
                  && profileFieldDTO.getIsOnboardingStaffEditable() == 1)) {
            profileFieldVO.setIsEditable(1);
          } else {
            profileFieldVO.setIsEditable(0);
          }
          profileFieldVOs.add(profileFieldVO);
        }
        userProfileVO.setProfileFieldVOs(profileFieldVOs);

        // TODO: opt int 0.9.2
        if (null == userProfileVO.getOnboardingTemplateId()
            || userProfileVO.getOnboardingTemplateId() <= 0) {
          userProfileVO.setOnboardingTemplateId(null);
        }

        result.setData(userProfileVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("getUserProfileOfAllFields()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/users/{userId}/core-profile",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<CoreUserProfileVO> getCoreUserProfile(
      @PathVariable("userId") String encryptedUserId
  ) {

    Result<CoreUserProfileVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long userId = 0;

    try {
      userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
    } catch (Exception e) {
      LOGGER.error("getCoreUserProfile()-error: invalid userId");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!userProfilePermissionChecker.canRead(authedOrgId, authedActorUserId, userId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      CoreUserProfileDTO coreUserProfileDTO =
          facadeFactory.getUserProfileFacade().getCoreUserProfile(authedOrgId, userId, authedActorUserId, authedAdminUserId);
      LOGGER.info("coreUserProfileDTO=" + coreUserProfileDTO);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(coreUserProfileDTO.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(serviceStatus);
      if (ServiceStatus.COMMON_OK == serviceStatus) {
        CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
        BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
        // handle teamVO
        if (null != coreUserProfileDTO.getTeamMemberDTO()){
          TeamVO teamVO = new TeamVO();
          BeanUtils.copyProperties(coreUserProfileDTO.getTeamMemberDTO(), teamVO);
          coreUserProfileVO.setTeamVO(teamVO);
        }
        // handle projectTeamVO
        if (!CollectionUtils.isEmpty(coreUserProfileDTO.getProjectTeamDTOs())) {
          List<ProjectTeamVO> projectTeamVOs = new ArrayList<>();
          for (ProjectTeamDTO projectTeamDTO : coreUserProfileDTO.getProjectTeamDTOs()) {
            ProjectTeamVO projectTeamVO = new ProjectTeamVO();
            BeanUtils.copyProperties(projectTeamDTO, projectTeamVO);
            projectTeamVOs.add(projectTeamVO);
          }
          coreUserProfileVO.setProjectTeamVOs(projectTeamVOs);
        }
        //handle role
        List<String> roles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(coreUserProfileDTO.getRoleListDTO())) {
          for (RoleDTO roleDTO : coreUserProfileDTO.getRoleListDTO()) {
            roles.add(roleDTO.getRoleName());
          }
        }
        coreUserProfileVO.setRoleNameList(roles);
        // handle userEmployment
        if (null != coreUserProfileDTO.getUserEmploymentDTO()) {
          UserEmploymentVO userEmploymentVO = new UserEmploymentVO();
          BeanUtils.copyProperties(coreUserProfileDTO.getUserEmploymentDTO(), userEmploymentVO);
          coreUserProfileVO.setUserEmploymentVO(userEmploymentVO);
        }
        result.setData(coreUserProfileVO);
      }
    } catch (Exception e) {
      LOGGER.info("getCoreUserProfile()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/users/{userId}/user-employment",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result updateUserEmployment(
      @PathVariable("userId") String encryptedUserId,
      @RequestBody UserEmploymentVO userEmploymentVO
  ) {

    Result<CoreUserProfileVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long userId = 0;

    try {
      userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
    } catch (Exception e) {
      LOGGER.info("updateUserEmployment(): invalid userId");
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    if (!userProfilePermissionChecker.canEdit(authedOrgId, authedActorUserId, userId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO();
      BeanUtils.copyProperties(userEmploymentVO, userEmploymentDTO);
      userEmploymentDTO.setUserId(userId);
      VoidDTO rpcUpdate = facadeFactory.getUserProfileFacade().updateUserEmployment(
          authedOrgId, userEmploymentDTO, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStauts = ServiceStatus.getEnumByCode(rpcUpdate.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStauts);
    } catch (Exception e) {
      LOGGER.info("updateUserEmployment()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  /**************** Methods before refraction(2016-08-08) below ****************/

  @LogAround

  @RequestMapping(
      value = "/users/core-profiles/new-staff",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<CoreUserProfileListVO> listCoreUserProfileOfNewStaff() {

    Result<CoreUserProfileListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!userProfilePermissionChecker.canList(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {

      CoreUserProfileListDTO rpcListResult = facadeFactory.getUserProfileFacade()
          .listCoreUserProfileOfNewStaffByOrgId(authedOrgId, authedActorUserId, authedAdminUserId);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(rpcListResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(serviceStatus);
      if (ServiceStatus.COMMON_OK == serviceStatus) {
        CoreUserProfileListVO coreUserProfileListVO = new CoreUserProfileListVO();
        if (!CollectionUtils.isEmpty(rpcListResult.getCoreUserProfileDTOs())) {
          List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
          for (CoreUserProfileDTO coreUserProfileDTO: rpcListResult.getCoreUserProfileDTOs()) {
            CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
            BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
            coreUserProfileVOs.add(coreUserProfileVO);
          }
          coreUserProfileListVO.setCoreUserProfileVOs(coreUserProfileVOs);
        }
        result.setData(coreUserProfileListVO);
      }
    } catch (Exception e) {
      LOGGER.info("getCoreUserProfileOfOthers()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/users/core-profiles/enroll-anniversary",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<CoreUserProfileListVO> listCoreUserProfileOfEnrollAnniversary() {

    Result<CoreUserProfileListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!userProfilePermissionChecker.canList(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      CoreUserProfileListDTO rpcListResult = facadeFactory.getUserProfileFacade()
          .listCoreUserProfileOfEnrollAnniversaryByOrgId(authedOrgId, authedActorUserId, authedAdminUserId);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(rpcListResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(serviceStatus);
      if (ServiceStatus.COMMON_OK == serviceStatus) {
        CoreUserProfileListVO coreUserProfileListVO = new CoreUserProfileListVO();
        if (!CollectionUtils.isEmpty(rpcListResult.getCoreUserProfileDTOs())) {
          List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
          for (CoreUserProfileDTO coreUserProfileDTO: rpcListResult.getCoreUserProfileDTOs()) {
            CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
            BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
            coreUserProfileVOs.add(coreUserProfileVO);
          }
          coreUserProfileListVO.setCoreUserProfileVOs(coreUserProfileVOs);
        }
        result.setData(coreUserProfileListVO);
      }
    } catch (Exception e) {
      LOGGER.info("listCoreUserProfileOfEnrollAnniversary()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


//  @LogAround
//
//  @RequestMapping(
//      value = "/users/{userId}/profile",
//      method = RequestMethod.GET,
//      produces = "application/json")
//  @ResponseBody
  public Result<UserProfileVO> getUserProfile(
      @PathVariable("userId") String encryptedUserId
  ) {

    Result<UserProfileVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long userId = 0;

    try {
      userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
    } catch (Exception e) {
      LOGGER.error("getUserProfile()-error: invalid userId");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!userProfilePermissionChecker.canRead(authedOrgId, authedActorUserId, userId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      UserProfileDTO rpcGetResult = facadeFactory.getUserProfileFacade().getUserProfile(
          authedOrgId, userId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcGetResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        UserProfileVO userProfileVO = new UserProfileVO();
        BeanUtils.copyProperties(rpcGetResult, userProfileVO);
        List<ProfileFieldVO> profileFieldVOs = new ArrayList<>();
        // handle UserEmployment
        UserEmploymentDTO userEmploymentDTO = rpcGetResult.getUserEmploymentDTO();
        UserEmploymentVO userEmploymentVO = new UserEmploymentVO();
        if (null != userEmploymentDTO) {
          BeanUtils.copyProperties(userEmploymentDTO, userEmploymentVO);
        }
        userProfileVO.setUserEmploymentVO(userEmploymentVO);
        for (ProfileFieldDTO profileFieldDTO: rpcGetResult.getProfileFieldDTOs()) {
          ProfileFieldVO profileFieldVO = new ProfileFieldVO();
          BeanHelper.copyPropertiesHandlingJSON(profileFieldDTO, profileFieldVO);
          profileFieldVOs.add(profileFieldVO);
        }
        userProfileVO.setProfileFieldVOs(profileFieldVOs);

        // TODO: opt int 0.9.2
        if (userProfileVO.getOnboardingTemplateId() <= 0) {
          userProfileVO.setOnboardingTemplateId(null);
        }

        result.setData(userProfileVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("getUserProfile()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/users/{userId}/profiles/status",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result updateUserProfileStatus(
      @PathVariable("userId") String encryptedUserId,
      @RequestBody UserProfileVO userProfileVO
  ) {

    Result result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    int userStatus  = 0;
    long userId = 0;

    try {
      userStatus = userProfileVO.getUserStatus();
      userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!userProfilePermissionChecker.canEdit(authedOrgId, authedActorUserId, userId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      VoidDTO rpcUpdateResult = facadeFactory.getUserProfileFacade().updateUserProfileStatus(
          authedOrgId, userId, userStatus, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcUpdateResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("updateUserProfileStatus()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/users/{userId}/complete-profile/fields",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result updateUserProfileField(
      @PathVariable("userId") String encryptedUserId,
      @RequestBody JSONObject jsonObject
  ) {

    Result result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    Map<String, String> fieldValues = convertJSONObjectToMap(jsonObject);
    long userId = 0;

    try {
      userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!userProfilePermissionChecker.canEdit(authedOrgId, authedActorUserId, userId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      VoidDTO rpcUpdateResult = facadeFactory.getUserProfileFacade().updateUserProfileField(
          authedOrgId, userId, fieldValues, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcUpdateResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("updateUserProfileField()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/addresses",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<AddressRegionListVO> listAddressRegion(
      @RequestParam("parentId") int parentId
  ) {

    Result<AddressRegionListVO> result = new Result<>();
    // TODO: HOW ?
//    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
//    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
//    long authedOrgId = AuthenticationInterceptor.orgId.get();

    try {
      AddressRegionListDTO rpcListResult =
          facadeFactory.getUserProfileFacade().listAddressRegion(-1L, parentId, -1L, -1L);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcListResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        AddressRegionListVO addressRegionListVO = new AddressRegionListVO();
        List<AddressRegionVO> addressRegionVOs = new ArrayList<>();
        for (AddressRegionDTO addressRegionDTO: rpcListResult.getAddressRegionDTOs()) {
          AddressRegionVO addressRegionVO = new AddressRegionVO();
          BeanUtils.copyProperties(addressRegionDTO, addressRegionVO);
          addressRegionVOs.add(addressRegionVO);
        }
        addressRegionListVO.setAddressRegionVOs(addressRegionVOs);
        result.setData(addressRegionListVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("getUserProfile()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/users/profiles/reportline-info/{userId}",
          method = RequestMethod.GET,
          produces = "application/json")
  @ResponseBody
  public Result<Object> listReportLineInfo(@PathVariable String userId) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptUserId = decryptLongValue(userId);

    TeamMemberDTO teamMemberDTO = facadeFactory.getUserFacade().getTeamMemberByUserId(
            orgId, decryptUserId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(teamMemberDTO.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    boolean teamPermitted = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, 0L, teamMemberDTO.getTeamId(),
            ResourceCode.USER_ORG.getResourceCode(), ResourceType.TEAM.getCode(), ActionCode.READ.getCode());

    boolean reportLinePermitted = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, 0L, decryptUserId,
            ResourceCode.REPORT_LINE.getResourceCode(), ResourceType.TEAM.getCode(), ActionCode.READ.getCode());

    boolean reporteeTeamPermitted = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, 0L, orgId,
            ResourceCode.USER_ORG.getResourceCode(), ResourceType.ORG.getCode(), ActionCode.READ.getCode());

    List<CoreUserProfileVO> reportors = new ArrayList<>();
    List<CoreUserProfileVO> reportees = new ArrayList<>();
    TeamVO teamVO = new TeamVO();

    if (reportLinePermitted) {
      ReportLineInfoDTO reportLineInfoDTO = facadeFactory.getUserFacade().getReportLineInfo(orgId, decryptUserId, reporteeTeamPermitted,
              actorUserId, adminUserId);
      serviceStatus = ServiceStatus.getEnumByCode(reportLineInfoDTO.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        throw new ServiceStatusException(serviceStatus);
      }
      if (!CollectionUtils.isEmpty(reportLineInfoDTO.getReportors())) {
        CoreUserProfileListDTO reportorDTO = facadeFactory.getUserProfileFacade().listCoreUserProfile(orgId, reportLineInfoDTO.getReportors(),
                actorUserId, adminUserId);
        serviceStatus = ServiceStatus.getEnumByCode(reportorDTO.getServiceStatusDTO().getCode());
        if (serviceStatus != ServiceStatus.COMMON_OK) {
          throw new ServiceStatusException(serviceStatus);
        }
        convertCoreUserProfileDTOToVO(reportorDTO.getCoreUserProfileDTOs(), reportors);
      }
      if (!CollectionUtils.isEmpty(reportLineInfoDTO.getReportees())) {
        CoreUserProfileListDTO reporteeDTO = facadeFactory.getUserProfileFacade().listCoreUserProfile(orgId, reportLineInfoDTO.getReportees(),
                actorUserId, adminUserId);
        serviceStatus = ServiceStatus.getEnumByCode(reporteeDTO.getServiceStatusDTO().getCode());
        if (serviceStatus != ServiceStatus.COMMON_OK) {
          throw new ServiceStatusException(serviceStatus);
        }
        convertCoreUserProfileDTOToVO(reporteeDTO.getCoreUserProfileDTOs(), reportees);
      }
      if (!reporteeTeamPermitted) {
        for (CoreUserProfileVO coreUserProfileVO : reportors) {
          coreUserProfileVO.setTeamVO(null);
        }
        for (CoreUserProfileVO coreUserProfileVO : reportees) {
          coreUserProfileVO.setTeamVO(null);
        }
      }
    }

    if (teamPermitted) {
      TeamDTO teamDTO = facadeFactory.getUserFacade().getTeam(orgId, teamMemberDTO.getTeamId(), actorUserId, adminUserId);
      serviceStatus = ServiceStatus.getEnumByCode(teamDTO.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        throw new ServiceStatusException(serviceStatus);
      }
      BeanUtils.copyProperties(teamDTO, teamVO);
    }

    Map<String, Object> map = new HashMap<>();
    map.put("reportors", reportors);
    map.put("reportees", reportees);
    map.put("team", teamVO);

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    result.setData(map);
    return result;
  }

  private long decryptLongValue(String encryptValue) {
    long decryptValue = -1;
    try {
      decryptValue = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptValue));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    return decryptValue;
  }

  private Map<String, String> convertJSONObjectToMap(JSONObject jsonObject) {
    if (null == jsonObject
        || jsonObject.size() == 0) {
      return null;
    }
    Map<String, String> stringMap = new HashMap<>();
    for (String key: jsonObject.keySet()) {
      stringMap.put(key, jsonObject.getString(key));
    }
    return stringMap;
  }

  public void convertCoreUserProfileDTOToVO(List<CoreUserProfileDTO> source, List<CoreUserProfileVO> dest) {
    if (CollectionUtils.isEmpty(source)) {
      return;
    }
    for (CoreUserProfileDTO dto : source) {
      CoreUserProfileVO vo = new CoreUserProfileVO();
      BeanUtils.copyProperties(dto, vo);

      // copy team
      if (dto.getTeamMemberDTO() != null) {
        TeamVO teamVO = new TeamVO();
        BeanUtils.copyProperties(dto.getTeamMemberDTO(), teamVO);
        vo.setTeamVO(teamVO);
      }
      dest.add(vo);
    }
  }

}
