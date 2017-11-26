// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.userorg;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.util.IOUtils;

import hr.wozai.service.user.client.userorg.dto.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.wozai.service.api.component.OnboardingFlowPermissionChecker;
import hr.wozai.service.api.component.OrgPermissionChecker;
import hr.wozai.service.api.component.UserProfilePermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.OnboardingTemplateHelper;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.TotalCountVO;
import hr.wozai.service.api.vo.document.OssAvatarPutRequestVO;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.user.CoreUserProfileListVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.api.vo.user.DocumentVO;
import hr.wozai.service.api.vo.user.OnboardingRequestVO;
import hr.wozai.service.api.vo.user.OnboardingTemplateVO;
import hr.wozai.service.api.vo.user.OrgAccountRequestVO;
import hr.wozai.service.api.vo.user.ProfileFieldVO;
import hr.wozai.service.api.vo.user.S3DocumentRequestVO;
import hr.wozai.service.api.vo.user.UserEmploymentVO;
import hr.wozai.service.api.vo.user.UserProfileVO;
import hr.wozai.service.servicecommons.commons.consts.SystemFieldConsts;
import hr.wozai.service.servicecommons.commons.consts.TimeConst;
import hr.wozai.service.servicecommons.commons.consts.TypeSpecConsts;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.OnboardingStatus;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.BooleanUtils;
import hr.wozai.service.servicecommons.commons.utils.EncodingUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.IntegerDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongListDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.document.dto.DocumentDTO;
import hr.wozai.service.user.client.document.dto.DocumentListDTO;
import hr.wozai.service.user.client.document.dto.OssAvatarPutRequestDTO;
import hr.wozai.service.user.client.document.dto.S3DocumentRequestDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingRequestDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingTemplateDTO;
import hr.wozai.service.user.client.onboarding.dto.OrgAccountRequestDTO;
import hr.wozai.service.user.client.userorg.enums.UuidUsage;
import hr.wozai.service.user.client.userorg.util.ExternalUrlUtils;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-11
 */
@Controller("onboardingFlowController")
public class OnboardingFlowController {

  private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingFlowController.class);

  private static final String PARAM_ACCESS_TOKEN = "X-Access-Token";
  private static final String PARAM_REFRESH_TOKEN = "X-Refresh-Token";

  private static final String PARAM_KEY_ONBOARDING_STATUS = "onboardingStatus";
  private static final String HTTP_ENDPOINT_PREFIX = "http://hr.sqian.com/onboarding-flows/staff?uuid=";
  private static final long ONE_HUNDRED_YEAR_IN_MILLIS = 1000 * 60 * 60 * 24 * 365 * 100;
  private static final String CSV_DOWNLOAD_CONTENT_TYPE = "application/octet-stream; charset=utf-8";
  private static final String CSV_DOWNLOAD_CONTENT_DISPOSITION_KEY = "Content-Disposition";
  private static final String CSV_DOWNLOAD_CONTENT_DISPOSITION_VALUE = "attachment; filename=staff.csv";

  private static final String ENCODING_UTF8 = "UTF-8";
  private static final String ENCODING_UTF16 = "UTF-16";
  private static final String ENCODING_GBK = "GBK";

  @Value("${url.host}")
  String host;

  @Autowired
  private OnboardingFlowPermissionChecker onboardingFlowPermissionChecker;

  @Autowired
  private OrgPermissionChecker orgPermissionChecker;

  @Autowired
  private UserProfilePermissionChecker userProfilePermissionChecker;

  @Autowired
  private FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/org-account",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  public Result createOrgAndFirstUser(
      @RequestBody OrgAccountRequestVO orgAccountRequestVO
  ) {

    Result result = new Result();

    try {
      // 1) verify sms code
      BooleanDTO rpcVerifyResult = facadeFactory.getSmsFacade().verifySmsCodeOfTrustedMobilePhone(
              orgAccountRequestVO.getSqStaffMobilePhone(), orgAccountRequestVO.getSmsVerificationCode());
      ServiceStatus rpcVerifyStatus = ServiceStatus.getEnumByCode(rpcVerifyResult.getServiceStatusDTO().getCode());
      if (!rpcVerifyResult.getData()) {
        result.setCodeAndMsg(rpcVerifyStatus);
      } else {
        // 2) create org account
        OrgAccountRequestDTO orgAccountRequestDTO = new OrgAccountRequestDTO();
        BeanUtils.copyProperties(orgAccountRequestVO, orgAccountRequestDTO);
        OnboardingRequestDTO onboardingRequestDTO = new OnboardingRequestDTO();
        BeanUtils.copyProperties(orgAccountRequestVO.getOnboardingRequestVO(), onboardingRequestDTO);
        onboardingRequestDTO.setRoleIds(null);
        orgAccountRequestDTO.setOnboardingRequestDTO(onboardingRequestDTO);
//        SuperAdminDTO superAdminDTO = new SuperAdminDTO();
//        BeanUtils.copyProperties(orgAccountRequestVO.getSuperAdminVO(), superAdminDTO);
//        orgAccountRequestDTO.setSuperAdminDTO(superAdminDTO);
        CoreUserProfileDTO rpcAddResult = facadeFactory.getOnboardingFlowFacade().addOrgAndSuperAdminAndFirstUser(orgAccountRequestDTO);
        ServiceStatus rpcAddStatus = ServiceStatus.getEnumByCode(rpcAddResult.getServiceStatusDTO().getCode());
        result.setCodeAndMsg(rpcAddStatus);
        // 3) create default reward medal
        VoidDTO voidDTO = facadeFactory.getRewardFacade().initRewardMedal(rpcAddResult.getOrgId(), rpcAddResult.getUserId(), rpcAddResult.getUserId());

        if (voidDTO.getServiceStatusDTO().getCode() != ServiceStatus.COMMON_OK.getCode()) {
          LOGGER.error("initRewardMedal()-error");
          throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
      }
    } catch (Exception e) {
      LOGGER.error("createOrgAndFirstUser()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/org-account/avatar",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<OssAvatarPutRequestVO> getOrgAvatarPutRequest(
      @RequestParam("x") String x,
      @RequestParam("y") String y,
      @RequestParam("e") String e
  ) {

    Result<OssAvatarPutRequestVO> result = new Result<>();
    try {
      OssAvatarPutRequestDTO getResult =
          facadeFactory.getAvatarFacade().addAvatar(-1L, x, y, e, -1L, -1L);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(getResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        OssAvatarPutRequestVO ossAvatarPutRequestVO = new OssAvatarPutRequestVO();
        BeanUtils.copyProperties(getResult, ossAvatarPutRequestVO);
        result.setData(ossAvatarPutRequestVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception ex) {
      LOGGER.info("getOrgAvatarPutRequest()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/org-account/avatar",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result<OssAvatarPutRequestVO> updateOrgAvatarPutRequest(
      @RequestParam("x") String x,
      @RequestParam("y") String y,
      @RequestParam("e") String e,
      @RequestBody JSONObject jsonObject
  ) {

    Result<OssAvatarPutRequestVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    String publicGetUrl = null;

    if (!orgPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      publicGetUrl = jsonObject.getString("publicGetUrl");
    } catch (Exception ex) {
      System.out.println("Christ! jsonObject=" + jsonObject);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {
      OssAvatarPutRequestDTO updateRequest = facadeFactory.getAvatarFacade()
          .updateAvatar(authedOrgId, publicGetUrl, x, y, e, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(updateRequest.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        OssAvatarPutRequestVO ossAvatarPutRequestVO = new OssAvatarPutRequestVO();
        BeanUtils.copyProperties(updateRequest, ossAvatarPutRequestVO);
        result.setData(ossAvatarPutRequestVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception ex) {
      LOGGER.info("updateOrgAvatarPutRequest()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/hr",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  public Result<IdVO> launchOnboardingByHR(
      @RequestBody OnboardingRequestVO onboardingRequestVO
  ) {

    Result<IdVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!onboardingFlowPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      OnboardingRequestDTO onboardingRequestDTO = new OnboardingRequestDTO();
      BeanUtils.copyProperties(onboardingRequestVO, onboardingRequestDTO);
      List<Long> roleIds = new ArrayList<>();
      for (int i = 0; i < onboardingRequestVO.getRoleIds().size(); i++) {
        roleIds.add(onboardingRequestVO.getRoleIds().get(i).getIdValue());
      }
      onboardingRequestDTO.setRoleIds(roleIds);

      LongDTO rpcFlow = facadeFactory.getOnboardingFlowFacade().launchOnboardingFlowOfIndivudualStaff(
          authedOrgId, onboardingRequestDTO, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcFlowStatus = ServiceStatus.getEnumByCode(rpcFlow.getServiceStatusDTO().getCode());
      if (rpcFlowStatus.equals(ServiceStatus.COMMON_CREATED)) {
        IdVO idVO = new IdVO();
        idVO.setIdValue(rpcFlow.getData());
        result.setData(idVO);
      }
      result.setCodeAndMsg(rpcFlowStatus);
    } catch (Exception e) {
      LOGGER.info("launchOnboardingByHR()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/hr/unhandled-tasks",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<List<Long>> countUnhandledTasks() {

    Result<List<Long>> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!onboardingFlowPermissionChecker.canList(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      LongListDTO rpcResult = facadeFactory.getOnboardingFlowFacade().countTodoNumbersOfOnboardingAndImporting(
          authedOrgId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        result.setData(rpcResult.getData());
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("countUnhandledTasks()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/hr/imported-users",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<CoreUserProfileListVO> listImportedUserByHR(
      @RequestParam(name = "isActivated") int isActivated,
      @RequestParam(name = "pageNumber") int pageNumber,
      @RequestParam(name = "pageSize") int pageSize
  ) {

    Result<CoreUserProfileListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if ((-1 != isActivated
         && !BooleanUtils.isValidBooleanValue(isActivated))
        || !PageUtils.isPageParamValid(pageNumber, pageSize)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!onboardingFlowPermissionChecker.canList(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      CoreUserProfileListDTO rpcResult = facadeFactory.getOnboardingFlowFacade().listImportedStaffByHR(
          authedOrgId, isActivated, pageNumber, pageSize, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        CoreUserProfileListVO coreUserProfileListVO = new CoreUserProfileListVO();
        List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
        for (int i = 0; i < rpcResult.getCoreUserProfileDTOs().size(); i++) {
          CoreUserProfileDTO coreUserProfileDTO = rpcResult.getCoreUserProfileDTOs().get(i);
          CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
          UserEmploymentVO userEmploymentVO = new UserEmploymentVO();
          if (null != coreUserProfileDTO.getUserEmploymentDTO()) {
            BeanUtils.copyProperties(coreUserProfileDTO.getUserEmploymentDTO(), userEmploymentVO);
          }
          coreUserProfileVO.setUserEmploymentVO(userEmploymentVO);
          BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
          // handle isUserDeletable
//          long enrollDate = UserEmploymentDTOHelper.getEnrollDate(coreUserProfileDTO.getUserEmploymentDTO());
//          if (enrollDate <= TimeUtils.getNowTimestmapInMillis()
//              && coreUserProfileVO.getUserEmploymentVO().getUserStatus().intValue() != UserStatus.RESIGNED.getCode()) {
//            coreUserProfileVO.setIsUserDeletable(0);
//          } else {
            coreUserProfileVO.setIsUserDeletable(1);
//          }
          coreUserProfileVOs.add(coreUserProfileVO);
        }
        coreUserProfileListVO.setCoreUserProfileVOs(coreUserProfileVOs);
        coreUserProfileListVO.setTotalNumber(rpcResult.getTotalNumber());
        result.setData(coreUserProfileListVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("listImportedUserByHR()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/hr/imported-users/{userId}/resend-init-pwd-email",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result resendInitPasswordUrlToImportedStaffByHR(
      @PathVariable("userId") String encryptedUserId
  ) {

    Result result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long userId = 0;

    try {
      userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
    } catch (Exception e) {
      LOGGER.info("resendInitPasswordUrlToImportedStaffByHR()-error: invalid userId", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    // Note: if only canEdit(), the staff self can also do it too, which is not allowed
    if (!onboardingFlowPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      // clear old url
      VoidDTO rpcDeleteResult = facadeFactory.getTokenFacade().deleteAllUUIDAndTemporaryToken(
          authedOrgId, userId, UuidUsage.INIT_PWD.getCode(), authedActorUserId, authedAdminUserId);
      ServiceStatus rpcDeleteStatus = ServiceStatus.getEnumByCode(rpcDeleteResult.getServiceStatusDTO().getCode());
      if (rpcDeleteStatus.equals(ServiceStatus.COMMON_OK)) {
        VoidDTO rpcResendResult = facadeFactory.getOnboardingFlowFacade().resendInitPasswordUrlToImportedStaffByHR(
            authedOrgId, userId, authedActorUserId, authedAdminUserId);
        ServiceStatus rpcResendStatus = ServiceStatus.getEnumByCode(rpcResendResult.getServiceStatusDTO().getCode());
        result.setCodeAndMsg(rpcResendStatus);
      } else {
        result.setCodeAndMsg(rpcDeleteStatus);
      }
    } catch (Exception e) {
      LOGGER.info("resendInitPasswordUrlToImportedStaffByHR()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/hr",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<CoreUserProfileListVO> listOnboardingFlowByHR(
      @RequestParam(name = "hasApproved") int hasApproved,
      @RequestParam(name = "pageNumber") int pageNumber,
      @RequestParam(name = "pageSize") int pageSize
  ) {

    Result<CoreUserProfileListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!PageUtils.isPageParamValid(pageNumber, pageSize)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!onboardingFlowPermissionChecker.canList(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      CoreUserProfileListDTO rpcResult = facadeFactory.getOnboardingFlowFacade().listOnboardingStaffByHR(
          authedOrgId, hasApproved, pageNumber, pageSize, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        CoreUserProfileListVO coreUserProfileListVO = new CoreUserProfileListVO();
        List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
        for (int i = 0; i < rpcResult.getCoreUserProfileDTOs().size(); i++) {
          CoreUserProfileDTO coreUserProfileDTO = rpcResult.getCoreUserProfileDTOs().get(i);
          CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
          UserEmploymentVO userEmploymentVO = new UserEmploymentVO();
          if (null != coreUserProfileDTO.getUserEmploymentDTO()) {
            BeanUtils.copyProperties(coreUserProfileDTO.getUserEmploymentDTO(), userEmploymentVO);
          }
          coreUserProfileVO.setUserEmploymentVO(userEmploymentVO);
          BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
          // handle isUserDeletable
//          long enrollDate = UserEmploymentDTOHelper.getEnrollDate(coreUserProfileDTO.getUserEmploymentDTO());
//          if (enrollDate <= TimeUtils.getNowTimestmapInMillis()
//              && coreUserProfileVO.getUserEmploymentVO().getUserStatus().intValue() != UserStatus.RESIGNED.getCode()) {
//            coreUserProfileVO.setIsUserDeletable(0);
//          } else {
            coreUserProfileVO.setIsUserDeletable(1);
//          }
          coreUserProfileVOs.add(coreUserProfileVO);
        }
        coreUserProfileListVO.setCoreUserProfileVOs(coreUserProfileVOs);
        coreUserProfileListVO.setTotalNumber(rpcResult.getTotalNumber());
        result.setData(coreUserProfileListVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("listOnboardingFlows()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/hr/{userId}/invitation-url",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<String> getOnboardingInvitationUrlByHR(
      @PathVariable("userId") String encryptedUserId
  ) {

    Result<String> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long userId = 0;

    // Note: if only canEdit(), the staff self can also do it too, which is not allowed
    if (!onboardingFlowPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
    } catch (Exception e) {
      LOGGER.info("updateOnboardingStatus()-error: invalid userId", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {

      // 1) validate UE: only allow onboardingStatus==ONBOARDING
      CoreUserProfileDTO rpcCUP = facadeFactory.getUserProfileFacade().getCoreUserProfile(
          authedOrgId, userId, authedActorUserId, authedAdminUserId);
      UserEmploymentDTO userEmploymentDTO = rpcCUP.getUserEmploymentDTO();
      if (OnboardingStatus.ONBOARDING.getCode() == userEmploymentDTO.getOnboardingStatus()) {
        // use existed UUID if available
        String invitationUUID = null;
        UuidInfoListDTO rpcUUIDList = facadeFactory.getTokenFacade().listUUIDInfosByUserIdAndUsage(
            authedOrgId, userId, UuidUsage.ONBOARDING.getCode(),
            TimeUtils.getNowTimestmapInMillis(), authedActorUserId, authedAdminUserId);
        ServiceStatus rpcListStatus = ServiceStatus.getEnumByCode(rpcUUIDList.getServiceStatusDTO().getCode());
        if (ServiceStatus.COMMON_OK.equals(rpcListStatus)) {
          List<UuidInfoDTO> uuidInfoDTOs = rpcUUIDList.getUuidInfoDTOList();
          if (!CollectionUtils.isEmpty(uuidInfoDTOs)) {
            invitationUUID = uuidInfoDTOs.get(uuidInfoDTOs.size() - 1).getUuid();
          }
          if (null != invitationUUID) {
            result.setCodeAndMsg(ServiceStatus.COMMON_OK);
          }
        }
        if (StringUtils.isNullOrEmpty(invitationUUID)) {
          // generate new UUID
          UuidInfoDTO uuidInfoDTO = new UuidInfoDTO();
          uuidInfoDTO.setOrgId(authedOrgId);
          uuidInfoDTO.setUserId(userId);
          uuidInfoDTO.setUuidUsage(UuidUsage.ONBOARDING.getCode());
          uuidInfoDTO.setExpireTime(TimeUtils.getNowTimestmapInMillis() + TimeConst.ONE_DAY_IN_MILLIS);
          uuidInfoDTO.setCreatedUserId(-1L);
          UuidInfoDTO rpcUUID = facadeFactory.getTokenFacade().addUUIDInfo(uuidInfoDTO, authedActorUserId, authedAdminUserId);
          invitationUUID = rpcUUID.getUuid();
        }
        // 3) generate url
        String invitationUrl = ExternalUrlUtils.generateInvitationUrlOfOnboardingFlowForStaff(host, invitationUUID);
        result.setData(invitationUrl);
        result.setCodeAndMsg(ServiceStatus.COMMON_OK);
      } else {
        result.setCodeAndMsg(ServiceStatus.UP_USER_NOT_FOUND);
      }
    } catch (Exception e) {
      LOGGER.info("getOnboardingInvitationUrl()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/hr/{userId}/resend-invitation-email",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result resendInvitationUrlByHR(
      @PathVariable("userId") String encryptedUserId
  ) {

    Result result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long userId = 0;

    try {
      userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
    } catch (Exception e) {
      LOGGER.info("resendInvitationUrlByHR()-error: invalid userId", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    // Note: if only canEdit(), the staff self can also do it too, which is not allowed
    if (!onboardingFlowPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      // clear old url
//      UuidInfoListDTO rpcList = facadeFactory.getTokenFacade().listUUIDInfosByUserIdAndUsage(
//          authedOrgId, userId, UuidUsage.ONBOARDING.getCode(),
//          TimeUtils.getNowTimestmapInMillis(), authedActorUserId, authedAdminUserId);
      VoidDTO rpcDeleteResult = facadeFactory.getTokenFacade().deleteAllUUIDAndTemporaryToken(
          authedOrgId, userId, UuidUsage.ONBOARDING.getCode(), authedActorUserId, authedAdminUserId);
      ServiceStatus rpcDeleteStatus = ServiceStatus.getEnumByCode(rpcDeleteResult.getServiceStatusDTO().getCode());
      if (rpcDeleteStatus.equals(ServiceStatus.COMMON_OK)) {
        VoidDTO rpcResendResult = facadeFactory.getOnboardingFlowFacade().resendInvitationUrlToOnboardingStaffByHR(
            authedOrgId, userId, authedActorUserId, authedAdminUserId);
        ServiceStatus rpcResendStatus = ServiceStatus.getEnumByCode(rpcResendResult.getServiceStatusDTO().getCode());
        result.setCodeAndMsg(rpcResendStatus);
      } else {
        result.setCodeAndMsg(rpcDeleteStatus);
      }
    } catch (Exception e) {
      LOGGER.info("resendInvitationUrlByHR()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/hr/{userId}/status",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result updateOnboardingStatusByHR(
      @PathVariable("userId") String encryptedUserId,
      @RequestBody JSONObject jsonObject
  ) {

    Result result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long userId = 0;
    int onboardingStatus = 0;

    // Note: if only canEdit(), the staff self can also do it too, which is not allowed
    if (!onboardingFlowPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
      onboardingStatus = jsonObject.getInteger(PARAM_KEY_ONBOARDING_STATUS);
      if (null == UserStatus.getEnumByCode(onboardingStatus)) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
    } catch (Exception e) {
      LOGGER.info("updateOnboardingStatus()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    // TODO: fix all status
    try {
      VoidDTO rpcUpdateResult = null;
      ServiceStatus rpcUpdateStatus = null;
      if (onboardingStatus == OnboardingStatus.APPROVED.getCode()) {
        rpcUpdateResult = facadeFactory.getOnboardingFlowFacade().approveOnboardingByHR(
            authedOrgId, userId, authedActorUserId, authedAdminUserId);
        rpcUpdateStatus = ServiceStatus.getEnumByCode(rpcUpdateResult.getServiceStatusDTO().getCode());
        result.setCodeAndMsg(rpcUpdateStatus);

        // disable previous urls
        facadeFactory.getTokenFacade().deleteAllUUIDAndTemporaryToken(
            authedOrgId, userId, UuidUsage.ONBOARDING.getCode(), authedActorUserId, authedAdminUserId);

      } else if (onboardingStatus == OnboardingStatus.ONBOARDING.getCode()) {
        rpcUpdateResult = facadeFactory.getOnboardingFlowFacade().rejectOnboardingSubmisisonByHR(
            authedOrgId, userId, authedActorUserId, authedAdminUserId);
        rpcUpdateStatus = ServiceStatus.getEnumByCode(rpcUpdateResult.getServiceStatusDTO().getCode());
        result.setCodeAndMsg(rpcUpdateStatus);
      }
    } catch (Exception e) {
      LOGGER.info("updateOnboardingStatus()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/staff/init-pwd-email",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result resendInitPasswordEmail(
      @RequestParam("emailAddress") String emailAddress
  ) {

    Result result = new Result<>();

    try {
      VoidDTO rpcResult = facadeFactory.getOnboardingFlowFacade().resendInitPasswordEmail(emailAddress);
      result.setCodeAndMsg(ServiceStatus.getEnumByCode(rpcResult.getServiceStatusDTO().getCode()));
    } catch (Exception e) {
      LOGGER.info("resendInitPasswordEmail()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/staff/onboarding-template",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<OnboardingTemplateVO> getOnboardingTemplateByStaff() {

    Result<OnboardingTemplateVO> result = new Result<>();
    long tempOrgId = AuthenticationInterceptor.tempOrgId.get();
    long tempUserId = AuthenticationInterceptor.tempUserId.get();

    if (!userProfilePermissionChecker.canRead(tempOrgId, tempUserId, tempUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      OnboardingTemplateDTO rpcGetResult = facadeFactory.getOnboardingFlowFacade().getOnboardingTemplateByStaff(tempOrgId, tempUserId);
      DocumentListDTO rpcDocuments = facadeFactory.getDocumentFacade().listDocument(tempOrgId, tempUserId, tempUserId);

      ServiceStatus rpcOnboardingTemplateStatus =
          ServiceStatus.getEnumByCode(rpcGetResult.getServiceStatusDTO().getCode());
      ServiceStatus rpcDocumentsStatus =
          ServiceStatus.getEnumByCode(rpcDocuments.getServiceStatusDTO().getCode());
      if (rpcDocumentsStatus.equals(ServiceStatus.COMMON_OK)
          && rpcOnboardingTemplateStatus.equals(ServiceStatus.COMMON_OK)) {
        OnboardingTemplateVO onboardingTemplateVO = new OnboardingTemplateVO();
        BeanUtils.copyProperties(rpcGetResult, onboardingTemplateVO);
        onboardingTemplateVO.setOnboardingDocumentVOs(OnboardingTemplateHelper.
                generateOnboardingDocumentVOs(rpcGetResult.getOnboardingDocumentDTOs(), rpcDocuments.getDocumentDTOs()));
        result.setData(onboardingTemplateVO);
        result.setCodeAndMsg(ServiceStatus.COMMON_OK);
      } else if (!rpcOnboardingTemplateStatus.equals(ServiceStatus.COMMON_OK)) {
        result.setCodeAndMsg(rpcOnboardingTemplateStatus);
      } else {
        result.setCodeAndMsg(rpcDocumentsStatus);
      }
    } catch (Exception e) {
      LOGGER.info("getOnboardingTemplateByStaff()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/staff/onboarding-documents/{documentId}/download",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<S3DocumentRequestVO> downloadOnboardingDocumentByStaff(
      @PathVariable("documentId") String encryptedDocumentId
  ) {

    Result<S3DocumentRequestVO> result = new Result<>();
    long tempOrgId = AuthenticationInterceptor.tempOrgId.get();
    long tempUserId = AuthenticationInterceptor.tempUserId.get();
    long documentId = 0;

    try {
      documentId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedDocumentId));
    } catch (Exception e) {
      LOGGER.error("downloadOnboardingDocumentByStaff-error: invalid documentId");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!userProfilePermissionChecker.canRead(tempOrgId, tempUserId, tempUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      S3DocumentRequestDTO rpcGetResult = facadeFactory.getOnboardingFlowFacade()
          .downloadOnboardingDocumentByStaff(tempOrgId, tempUserId, documentId);
      S3DocumentRequestVO s3DocumentRequestVO = new S3DocumentRequestVO();
      BeanUtils.copyProperties(rpcGetResult, s3DocumentRequestVO);
      ServiceStatus rpcGetStatus = ServiceStatus.getEnumByCode(rpcGetResult.getServiceStatusDTO().getCode());
      result.setData(s3DocumentRequestVO);
      result.setCodeAndMsg(rpcGetStatus);
    } catch (Exception e) {
      LOGGER.info("getOnboardingTemplateByStaff()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/staff/core-profile",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<CoreUserProfileVO> getCoreUserProfileOfOnboardingStaffSelf() {

    Result<CoreUserProfileVO> result = new Result<>();
    long tempOrgId = AuthenticationInterceptor.tempOrgId.get();
    long tempUserId = AuthenticationInterceptor.tempUserId.get();

    if (!userProfilePermissionChecker.canRead(tempOrgId, tempUserId, tempUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      CoreUserProfileDTO coreUserProfileDTO =
          facadeFactory.getUserProfileFacade().getCoreUserProfile(tempOrgId, tempUserId, tempUserId, tempUserId);
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
      LOGGER.info("getCoreUserProfileOfOnboardingStaffSelf()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/staff/complete-profile",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<UserProfileVO> getUserProfileByStaff() {

    Result<UserProfileVO> result = new Result<>();
    long tempOrgId = AuthenticationInterceptor.tempOrgId.get();
    long tempUserId = AuthenticationInterceptor.tempUserId.get();

    if (!userProfilePermissionChecker.canRead(tempOrgId, tempUserId, tempUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      UserProfileDTO rpcGetResult = facadeFactory.getOnboardingFlowFacade().getUserProfileByStaff(tempOrgId, tempUserId);
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
        profileFieldVO.setIsEditable(profileFieldDTO.getIsOnboardingStaffEditable());
        profileFieldVOs.add(profileFieldVO);
      }
      userProfileVO.setProfileFieldVOs(profileFieldVOs);
      ServiceStatus rpcGetStatus = ServiceStatus.getEnumByCode(rpcGetResult.getServiceStatusDTO().getCode());
      // TODO: how to handle multiple RPC status
      BooleanDTO rpcHasPassword = facadeFactory.getUserFacade().hasPassword(tempUserId);
      userProfileVO.setCanInitPassword(rpcHasPassword.getData() ? 0 : 1);
      result.setData(userProfileVO);
      result.setCodeAndMsg(rpcGetStatus);
    } catch (Exception e) {
      LOGGER.info("getUserProfileByStaff()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/staff/documents",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  public Result<S3DocumentRequestVO> addDocumentByStaff(
      @RequestBody DocumentVO documentVO
  ) {

    Result<S3DocumentRequestVO> result = new Result<>();
    long tempOrgId = AuthenticationInterceptor.tempOrgId.get();
    long tempUserId = AuthenticationInterceptor.tempUserId.get();

    if (!userProfilePermissionChecker.canEdit(tempOrgId, tempUserId, tempUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      DocumentDTO documentDTO = new DocumentDTO();
      BeanUtils.copyProperties(documentVO, documentDTO);
      S3DocumentRequestDTO addResult =
          facadeFactory.getDocumentFacade().addDocument(tempOrgId, documentDTO, tempUserId, -1L);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(addResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        S3DocumentRequestVO s3DocumentRequestVO = new S3DocumentRequestVO();
        BeanUtils.copyProperties(addResult, s3DocumentRequestVO);
        result.setData(s3DocumentRequestVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("addDocumentByStaff()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/staff/avatars",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<OssAvatarPutRequestVO> getAvatarPutRequestByStaff(
      @RequestParam("x") String x,
      @RequestParam("y") String y,
      @RequestParam("e") String e
  ) {

    Result<OssAvatarPutRequestVO> result = new Result<>();
    long tempOrgId = AuthenticationInterceptor.tempOrgId.get();
    long tempUserId = AuthenticationInterceptor.tempUserId.get();

    if (!userProfilePermissionChecker.canRead(tempOrgId, tempUserId, tempUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      OssAvatarPutRequestDTO getResult =
          facadeFactory.getAvatarFacade().addAvatar(tempOrgId, x, y, e, tempUserId, -1L);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(getResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        OssAvatarPutRequestVO ossAvatarPutRequestVO = new OssAvatarPutRequestVO();
        BeanUtils.copyProperties(getResult, ossAvatarPutRequestVO);
        result.setData(ossAvatarPutRequestVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception ex) {
      LOGGER.info("getAvatarPutRequestByStaff()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/staff/user-profile/fields",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result updateUserProfileFieldByStaff(
      @RequestBody JSONObject jsonObject
  ) {
    Result result = new Result<>();
    long tempOrgId = AuthenticationInterceptor.tempOrgId.get();
    long tempUserId = AuthenticationInterceptor.tempUserId.get();
    Map<String, String> fieldValues = convertJSONObjectToMap(jsonObject);

    if (!userProfilePermissionChecker.canEdit(tempOrgId, tempUserId, tempUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      VoidDTO rpcUpdateResult = facadeFactory.getOnboardingFlowFacade().updateUserProfileFieldByStaff(tempOrgId, tempUserId, fieldValues);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcUpdateResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("updateUserProfileFieldByStaff()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/staff/submission",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result submitOnboardingByStaff() {

    Result result = new Result<>();
    long tempOrgId = AuthenticationInterceptor.tempOrgId.get();
    long tempUserId = AuthenticationInterceptor.tempUserId.get();

    if (!userProfilePermissionChecker.canEdit(tempOrgId, tempUserId, tempUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      VoidDTO rpcUpdateResult = facadeFactory.getOnboardingFlowFacade().submitOnboardingRequestByStaff(tempOrgId, tempUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcUpdateResult.getServiceStatusDTO().getCode());

      // disable previous urls
      facadeFactory.getTokenFacade().deleteAllUUIDAndTemporaryToken(tempOrgId, tempUserId, UuidUsage.ONBOARDING.getCode(), -1L, -1L);

      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("submitOnboardingByStaff()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/profile-templates-csv-files/download",
      method = RequestMethod.POST)
  public void downloadProfileTemplateCSVFile(
      HttpServletRequest request,
      HttpServletResponse response
  ) {

    String accessTokenString = request.getParameter(PARAM_ACCESS_TOKEN);
    String refreshTokenString = request.getParameter(PARAM_REFRESH_TOKEN);

    long authedActorUserId = AuthenticationInterceptor.getActorUserIdFromTokenPair(accessTokenString, refreshTokenString);
    long authedAdminUserId = AuthenticationInterceptor.getAdminUserIdFromTokenPair(accessTokenString, refreshTokenString);
    long authedOrgId = AuthenticationInterceptor.getOrgIdFromTokenPair(accessTokenString, refreshTokenString);

    if (!onboardingFlowPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      response.setContentType(CSV_DOWNLOAD_CONTENT_TYPE);
      response.setHeader(CSV_DOWNLOAD_CONTENT_DISPOSITION_KEY, CSV_DOWNLOAD_CONTENT_DISPOSITION_VALUE);
      String header = "sep=,\n" + generateFixedSchemaCsvHeader();
      InputStream inputStream = new ByteArrayInputStream(header.getBytes("GBK"));
      IOUtils.copy(inputStream, response.getOutputStream());
      response.flushBuffer();
    } catch (Exception e) {
      LOGGER.info("downloadProfileTemplateCSVFile()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/profile-templates-csv-files/manual-operation/{documentId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result grantManualOperationOfCSVFile(
      @PathVariable("documentId") String encrypedDocumentId
  ) {

    Result result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long documentId = 0L;

    if (!onboardingFlowPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      documentId = Long.parseLong(EncryptUtils.symmetricDecrypt(encrypedDocumentId));
    } catch (Exception e) {
      LOGGER.error("grantManualOperationOfCSVFile()-error: invalid id", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {
      VoidDTO rpcResult = facadeFactory.getOnboardingFlowFacade()
          .grantManualOperationOfCSVFile(authedOrgId, documentId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("grantManualOperationOfCSVFile()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/profile-templates-csv-files/upload",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  public Result<TotalCountVO> addStaffFromCSVFile(
      @RequestParam(value = "file") MultipartFile csvFile
  ) {

    Result<TotalCountVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!onboardingFlowPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {

      String encoding = EncodingUtils.detectCharset(csvFile.getBytes());
      LOGGER.info("addStaffFromCSVFile(): csv-encoding=" + encoding);
      if (!ENCODING_UTF8.equals(encoding)
          && !ENCODING_UTF16.equals(encoding)
          && !ENCODING_GBK.equals(encoding)) {
        encoding = ENCODING_GBK;
      }
      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(csvFile.getInputStream(), encoding));
      List<List<String>> rawFieldValuesList = new ArrayList<>();
      String line = null;
      int lineCount = 0;
      while ((line = bufferedReader.readLine()) != null) {
        if (lineCount == 0) {
          // compatibility 1: has or hasnot first line (sep=,)
          if (!line.contains("sep=")) {
            lineCount ++;
          }
        }
        if (lineCount <= 1) {
          lineCount ++;
          continue;
        }

        // compatibility 1: allow ; as separator
        line = line.replace(";", ",");

        if (!StringUtils.isNullOrEmpty(line)) {
          List<String> oneLineFieldValues = Arrays.asList(line.split(",", -1));
          rawFieldValuesList.add(oneLineFieldValues);
          LOGGER.info("addStaffFromCSVFile(): line={}, size={}, currLine={}, line={}",
                      lineCount, oneLineFieldValues.size(), oneLineFieldValues, line);
        }
        lineCount++;
      }
      if (lineCount <= 2) {
        result.setCodeAndMsg(ServiceStatus.UP_CSV_EMPTY);
      } else {
        if (CollectionUtils.isEmpty(rawFieldValuesList)) {
          throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
        }
        IntegerDTO rpcResult = facadeFactory.getOnboardingFlowFacade().batchImportStaffByOrgAdmin(
            authedOrgId, rawFieldValuesList, authedActorUserId, authedAdminUserId);
        ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcResult.getServiceStatusDTO().getCode());
        if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
          TotalCountVO totalCountVO = new TotalCountVO();
          totalCountVO.setTotalCount(rpcResult.getData());
          result.setData(totalCountVO);
        }
        result.setCodeAndMsg(rpcStatus);
        result.setErrorInfo(rpcResult.getServiceStatusDTO().getErrorInfo());
      }
    } catch (Exception e) {
      LOGGER.error("addStaffFromCSVFile-error: invalid csv file");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-flows/hr/staff",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  public Result individuallyImportStaff(
      @RequestBody CoreUserProfileVO coreUserProfileVO
  ) {

    Result<IdVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!onboardingFlowPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      LongDTO addedUserId = facadeFactory.getOnboardingFlowFacade().individuallyImportStaff(
          authedOrgId, coreUserProfileVO.getFullName(), coreUserProfileVO.getEmailAddress(),
          coreUserProfileVO.getMobilePhone(), authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(addedUserId.getServiceStatusDTO().getCode());
      if (ServiceStatus.COMMON_CREATED.equals(rpcStatus)) {
        IdVO idVO = new IdVO();
        idVO.setIdValue(addedUserId.getData());
        result.setData(idVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("individuallyImportStaff()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  private String generateInvitationUrl(String uuid) {
    return HTTP_ENDPOINT_PREFIX + uuid;
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

  /**
   * Handle:
   *  1) fieldName
   *  2) 必填/选填/不填
   *  3) 类型
   *  4) 取值
   *
   * @param profileFieldDTOs
   * @return
   */
  private String generateCSVFileHeader(List<ProfileFieldDTO> profileFieldDTOs) {

    List<ProfileFieldDTO> enabledDataFieldDTOs = new ArrayList<>();
    for (int i = 0; i < profileFieldDTOs.size(); i++) {
      if (DataType.CONTAINER.getCode() != profileFieldDTOs.get(i).getDataType()
          && profileFieldDTOs.get(i).getIsEnabled() == 1) {
        enabledDataFieldDTOs.add(profileFieldDTOs.get(i));
      }
    }

    LOGGER.info("generateCSVFileHeader(): columnSize=" + enabledDataFieldDTOs.size());

    StringBuilder header = new StringBuilder();
    String insertedBetweenFields = ", 合同类型 (必填) (全职/兼职/实习), 入职日期 (必填) (日期类型), 合同到期日期 (选填) (日期类型), 转正状态 (必填) (试用/正式), ";
    int fieldCount = enabledDataFieldDTOs.size();
    for (int i = 0;i < fieldCount; i++) {

      ProfileFieldDTO dataFieldDTO = enabledDataFieldDTOs.get(i);

      if (dataFieldDTO.getIsEnabled() == 0) {
        continue;
      }

      // 1)
      header.append(dataFieldDTO.getDisplayName());

      // 2)
      DataType dataType = DataType.getEnumByCode(dataFieldDTO.getDataType());
      String referenceName = dataFieldDTO.getReferenceName();
      if (SystemFieldConsts.TEAM_ID_REF_NAME.equals(referenceName)
          || SystemFieldConsts.REPORTED_ID_REF_NAME.equals(referenceName)) {
        header.append(" (不填)");
      } else {
        if (dataFieldDTO.getIsMandatory() == 1) {
          header.append(" (必填)");
        } else if (dataType.equals(DataType.INTEGER)
                   || dataType.equals(DataType.DECIMAL)
                   || dataType.equals(DataType.DATETIME)
                   || dataType.equals(DataType.SHORT_TEXT)
                   || dataType.equals(DataType.LONG_TEXT)
                   || dataType.equals(DataType.BLOCK_TEXT)
                   || dataType.equals(DataType.ADDRESS)
                   || dataType.equals(DataType.SINGLE_PICK)
                   || dataType.equals(DataType.MULTI_PICK)) {
          header.append(" (选填)");
        } else {
          header.append(" (不填)");
        }
      }

      // 3)
      if (dataType.equals(DataType.ADDRESS)) {
        header.append(" (地址类型)");
      } else if (dataType.equals(DataType.DATETIME)) {
        header.append(" (日期类型)");
      } else if (dataType.equals(DataType.SINGLE_PICK)) {
        header.append(" (单选类型)");
      } else if (dataType.equals(DataType.MULTI_PICK)) {
        header.append(" (多选类型)");
      }

      // 4)
      if (dataType.equals(DataType.SINGLE_PICK)
          || dataType.equals(DataType.MULTI_PICK)) {
        JSONArray pickOptions = JSONArray.parseArray(
            JSON.parseObject(dataFieldDTO.getTypeSpec()).get(TypeSpecConsts.PICK_OPTIONS_KEY).toString());
        if (null != pickOptions
            && pickOptions.size() > 0) {
          header.append(" (");
          for (int j = 0; j < pickOptions.size(); j++) {
            JSONObject pickOption = pickOptions.getJSONObject(j);
            String value = pickOption.getString("optionValue");
            header.append(value);
            if (j != pickOptions.size() - 1) {
              header.append("/");
            }
          }
          header.append(" )");
        }
      }

      if (i == 1) {
        header.append(insertedBetweenFields);
      } else if (i < fieldCount - 1) {
        header.append(", ");
      }

    }

    return header.toString();
  }

  /**
   * Eight field in total:
   *  1) fullName
   *  2) emailAddress
   *  3) mobilePhone

   *
   * @return
   */
  private String generateFixedSchemaCsvHeader() {
    String header = "姓名, 企业邮箱, 手机号";
    return header;
  }

  public static void main(String[] args) {
    String line = "AAA,,,";
    String [] split = line.split(",", -1);
    for (int i = 0; i < split.length; i++) {
      System.out.println(i + ": " + split[i]);
    }
  }

}
