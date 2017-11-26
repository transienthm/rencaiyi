// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.userorg.userprofile;

import hr.wozai.service.api.vo.user.OrgPickOptionVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.StatusType;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.ReviewInvitationListDTO;
import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.dto.JobTransferRequestDTO;
import hr.wozai.service.user.client.userorg.dto.JobTransferResponseDTO;
import hr.wozai.service.user.client.userorg.dto.JobTransferResponseListDTO;
import hr.wozai.service.user.client.userorg.dto.SimpleUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.StatusUpdateDTO;
import hr.wozai.service.user.client.userorg.dto.StatusUpdateListDTO;
import hr.wozai.service.api.component.OnboardingAdminPermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.api.vo.user.JobTransferRequestVO;
import hr.wozai.service.api.vo.user.JobTransferResponseListVO;
import hr.wozai.service.api.vo.user.JobTransferResponseVO;
import hr.wozai.service.api.vo.user.SimpleUserProfileVO;
import hr.wozai.service.api.vo.user.StatusUpdateResponseListVO;
import hr.wozai.service.api.vo.user.StatusUpdateRequestVO;
import hr.wozai.service.api.vo.user.StatusUpdateResponseVO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.validation.Valid;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-15
 */
@Controller("employManagementController")
public class EmployManagementController {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmployManagementController.class);

  @Autowired
  private OnboardingAdminPermissionChecker onboardingAdminPermissionChecker;

  @Autowired
  FacadeFactory facadeFactory;

  @PostConstruct
  public void init() throws Exception {
  }

  @LogAround

  @RequestMapping(
          value = "/employ-managements/job-transfers",
          method = RequestMethod.POST,
          produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<IdVO> addJobTransfer(
          @RequestBody @Valid JobTransferRequestVO jobTransferRequestVO,
          BindingResult bindingResult
  ) {

    Result<IdVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    List<Long> toNotifyUserIds = null;

    try {
      toNotifyUserIds = decrypteUserIds(jobTransferRequestVO.getToNotifyUserIds());
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!onboardingAdminPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    if (jobTransferRequestVO.getUserId() == authedActorUserId) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {
      if (jobTransferRequestVO.getAfterReporterId() == null) {
        jobTransferRequestVO.setAfterReporterId(0L);
      }
      JobTransferRequestDTO jobTransferRequestDTO = new JobTransferRequestDTO();
      BeanUtils.copyProperties(jobTransferRequestVO, jobTransferRequestDTO);
      jobTransferRequestDTO.setToNotifyUserIds(toNotifyUserIds);
      LongDTO rpcAddResult = facadeFactory.getUserProfileFacade().addJobTransfer(
              authedOrgId, jobTransferRequestDTO, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcAddResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        IdVO idVO = new IdVO();
        idVO.setIdValue(rpcAddResult.getData());
        result.setData(idVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("addJobTransfer()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/employ-managements/job-transfers/{jobTransferId}",
          method = RequestMethod.GET,
          produces = "application/json")
  @ResponseBody
  public Result<JobTransferResponseVO> getJobTransfer(
          @PathVariable("jobTransferId") String encryptedJobTransferId
  ) {

    Result<JobTransferResponseVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long jobTransferId = 0L;

    try {
      jobTransferId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedJobTransferId));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!onboardingAdminPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      JobTransferResponseDTO rpcGetResult = facadeFactory.getUserProfileFacade().getJobTransfer(
              authedOrgId, jobTransferId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcGetResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        JobTransferResponseVO jobTransferResponseVO = new JobTransferResponseVO();
        copyAllPropertiesFromDTOToVO(rpcGetResult, jobTransferResponseVO);
        result.setData(jobTransferResponseVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("getJobTransfer()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/employ-managements/job-transfers",
          method = RequestMethod.GET,
          produces = "application/json")
  @ResponseBody
  public Result<JobTransferResponseListVO> listJobTransfer(
          @RequestParam(name = "pageNumber") int pageNumber,
          @RequestParam(name = "pageSize") int pageSize
  ) {

    Result<JobTransferResponseListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!PageUtils.isPageParamValid(pageNumber, pageSize)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!onboardingAdminPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      JobTransferResponseListDTO rpcListResult = facadeFactory.getUserProfileFacade().listJobTransfer(
              authedOrgId, pageNumber, pageSize, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcListResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        JobTransferResponseListVO jobTransferResponseListVO = new JobTransferResponseListVO();
        List<JobTransferResponseVO> jobTransferResponseVOs = new ArrayList();
        for (JobTransferResponseDTO jobTransferResponseDTO : rpcListResult.getJobTransferDTOs()) {
          if (null != jobTransferResponseDTO.getUserSimpleUserProfileDTO()) {
            JobTransferResponseVO jobTransferResponseVO = new JobTransferResponseVO();
            copyAllPropertiesFromDTOToVO(jobTransferResponseDTO, jobTransferResponseVO);
            jobTransferResponseVOs.add(jobTransferResponseVO);
          }
        }
        jobTransferResponseListVO.setJobTransferResponseVOs(jobTransferResponseVOs);
        jobTransferResponseListVO.setTotalNumber(rpcListResult.getTotalNumber());
        result.setData(jobTransferResponseListVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("getJobTransfer()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/employ-managements/status-updates",
          method = RequestMethod.POST,
          produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<IdVO> addStatusUpdate(
          @RequestBody @Valid StatusUpdateRequestVO statusUpdateRequestVO,
          BindingResult bindingResult
  ) {

    Result<IdVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (null == StatusType.getEnumByCode(statusUpdateRequestVO.getStatusType())) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (statusUpdateRequestVO.getUserId() == authedActorUserId) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    List<Long> toNotifyUserIds = null;

    try {
      toNotifyUserIds = decrypteUserIds(statusUpdateRequestVO.getToNotifyUserIds());
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!onboardingAdminPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      StatusUpdateDTO statusUpdateDTO = new StatusUpdateDTO();
      BeanUtils.copyProperties(statusUpdateRequestVO, statusUpdateDTO);
      statusUpdateDTO.setToNotifyUserIds(toNotifyUserIds);
      int statusType = statusUpdateRequestVO.getStatusType();
      LongDTO rpcAddResult = null;
      ServiceStatus rpcStatus = null;
      if (statusType == StatusType.EMPLOYMENT_STATUS.getCode()) {
        rpcAddResult = facadeFactory.getUserProfileFacade().addPassProbationStatusUpdate(
                authedOrgId, statusUpdateDTO, authedActorUserId, authedAdminUserId);
        rpcStatus = ServiceStatus.getEnumByCode(rpcAddResult.getServiceStatusDTO().getCode());
      } else if (statusType == StatusType.USER_STATUS.getCode()) {
        // check no remaining review
        ReviewInvitationListDTO remoteResult = facadeFactory.getReviewInvitationFacade().listUnSubmittedReviewInvitation(
                authedOrgId, statusUpdateDTO.getUserId(), authedActorUserId, authedAdminUserId);
        if (!CollectionUtils.isEmpty(remoteResult.getReviewInvitationDTOs())) {
          rpcStatus = ServiceStatus.REVIEW_REMAINING_REVIEW_TODO;
        } else {
          // resign & disable all tokens
          rpcAddResult = facadeFactory.getUserProfileFacade().addResignStatusUpdate(
                  authedOrgId, statusUpdateDTO, authedActorUserId, authedAdminUserId);
          rpcStatus = ServiceStatus.getEnumByCode(rpcAddResult.getServiceStatusDTO().getCode());
        }
      } else {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        IdVO idVO = new IdVO();
        idVO.setIdValue(rpcAddResult.getData());
        result.setData(idVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("addStatusUpdate()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/employ-managements/status-updates/{statusUpdateId}",
          method = RequestMethod.GET,
          produces = "application/json")
  @ResponseBody
  public Result<StatusUpdateResponseVO> getStatusUpdate(
          @PathVariable("statusUpdateId") String encryptedStatusUpdateId
  ) {

    Result<StatusUpdateResponseVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long statusUpdateId = 0L;

    try {
      statusUpdateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedStatusUpdateId));
    } catch (Exception e) {
      LOGGER.error("getStatusUpdate()-error: invalid userId");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!onboardingAdminPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      StatusUpdateDTO rpcStatusUpdateDTO = facadeFactory.getUserProfileFacade().getStatusUpdate(
              authedOrgId, statusUpdateId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatusUpdateStatus = ServiceStatus
              .getEnumByCode(rpcStatusUpdateDTO.getServiceStatusDTO().getCode());
      if (rpcStatusUpdateStatus.equals(ServiceStatus.COMMON_OK)) {
        StatusUpdateResponseVO statusUpdateResponseVO = new StatusUpdateResponseVO();
        BeanUtils.copyProperties(rpcStatusUpdateDTO, statusUpdateResponseVO);
        CoreUserProfileDTO rpcCUPDTO = facadeFactory.getUserProfileFacade().getCoreUserProfile(
                authedOrgId, rpcStatusUpdateDTO.getUserId(), authedActorUserId, authedAdminUserId);
        if (ServiceStatus.COMMON_OK.getCode() == rpcCUPDTO.getServiceStatusDTO().getCode()) {
          CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
          BeanUtils.copyProperties(rpcCUPDTO, coreUserProfileVO);
          statusUpdateResponseVO.setUserCoreUserProfileVO(coreUserProfileVO);
        }
        // handle toNotifyUsers
        if (!CollectionUtils.isEmpty(rpcStatusUpdateDTO.getToNotifyUserIds())) {
          CoreUserProfileListDTO rpcCUPList = facadeFactory.getUserProfileFacade().listCoreUserProfile(
                  authedOrgId, rpcStatusUpdateDTO.getToNotifyUserIds(), authedActorUserId, authedAdminUserId);
          if (!CollectionUtils.isEmpty(rpcCUPList.getCoreUserProfileDTOs())) {
            Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap = new HashMap<>();
            for (CoreUserProfileDTO coreUserProfileDTO : rpcCUPList.getCoreUserProfileDTOs()) {
              coreUserProfileDTOMap.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
            }
            List<SimpleUserProfileVO> simpleUserProfileVOs = new ArrayList<>();
            for (Long userId : rpcStatusUpdateDTO.getToNotifyUserIds()) {
              if (coreUserProfileDTOMap.containsKey(userId)) {
                SimpleUserProfileVO simpleUserProfileVO = new SimpleUserProfileVO();
                BeanUtils.copyProperties(coreUserProfileDTOMap.get(userId), simpleUserProfileVO);
                simpleUserProfileVOs.add(simpleUserProfileVO);
              }
            }
            statusUpdateResponseVO.setToNotifySimpleUserProfileVOs(simpleUserProfileVOs);
          }
        }
        result.setData(statusUpdateResponseVO);
      }
      result.setCodeAndMsg(rpcStatusUpdateStatus);
    } catch (Exception e) {
      LOGGER.info("getStatusUpdate()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/employ-managements/status-updates",
          method = RequestMethod.GET,
          produces = "application/json")
  @ResponseBody
  public Result<StatusUpdateResponseListVO> listStatusUpdate(
          @RequestParam("statusType") int statusType,
          @RequestParam("pageNumber") int pageNumber,
          @RequestParam("pageSize") int pageSize
  ) {

    Result<StatusUpdateResponseListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!PageUtils.isPageParamValid(pageNumber, pageSize)
            || null == StatusType.getEnumByCode(statusType)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {
    } catch (Exception e) {
      LOGGER.error("listStatusUpdate()-error: invalid userId");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!onboardingAdminPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      StatusUpdateListDTO rpcListResult = facadeFactory.getUserProfileFacade().listStatusUpdate(
              authedOrgId, statusType, pageNumber, pageSize, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcListStatus = ServiceStatus.getEnumByCode(rpcListResult.getServiceStatusDTO().getCode());
      if (rpcListStatus.equals(ServiceStatus.COMMON_OK)) {
        StatusUpdateResponseListVO statusUpdateResponseListVO = new StatusUpdateResponseListVO();
        List<StatusUpdateResponseVO> statusUpdateResponseVOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(rpcListResult.getStatusUpdateDTOs())) {
          Set<Long> userIdMap = new HashSet<>();
          for (StatusUpdateDTO statusUpdateDTO : rpcListResult.getStatusUpdateDTOs()) {
            userIdMap.add(statusUpdateDTO.getUserId());
            userIdMap.addAll(statusUpdateDTO.getToNotifyUserIds());
          }
          List<Long> userIds = new ArrayList<>(userIdMap);
          CoreUserProfileListDTO rpcCUPListDTO = facadeFactory.getUserProfileFacade()
                  .listCoreUserProfile(authedOrgId, userIds, authedActorUserId, authedAdminUserId);
          if (ServiceStatus.COMMON_OK.getCode() == rpcCUPListDTO.getServiceStatusDTO().getCode()
                  && !CollectionUtils.isEmpty(rpcCUPListDTO.getCoreUserProfileDTOs())) {
            Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap = new HashMap<>();
            for (CoreUserProfileDTO coreUserProfileDTO : rpcCUPListDTO.getCoreUserProfileDTOs()) {
              coreUserProfileDTOMap.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
            }
            for (StatusUpdateDTO statusUpdateDTO : rpcListResult.getStatusUpdateDTOs()) {
              StatusUpdateResponseVO statusUpdateResponseVO = new StatusUpdateResponseVO();
              BeanUtils.copyProperties(statusUpdateDTO, statusUpdateResponseVO);
              // fill in CUP
              if (coreUserProfileDTOMap.containsKey(statusUpdateResponseVO.getUserId())) {
                CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
                BeanUtils.copyProperties(coreUserProfileDTOMap.get(
                        statusUpdateResponseVO.getUserId()), coreUserProfileVO);
                statusUpdateResponseVO.setUserCoreUserProfileVO(coreUserProfileVO);
              }
              // fill in toNotifyUsers
              if (!CollectionUtils.isEmpty(statusUpdateDTO.getToNotifyUserIds())) {
                List<SimpleUserProfileVO> simpleUserProfileVOs = new ArrayList<>();
                for (Long toNotifyUserId : statusUpdateDTO.getToNotifyUserIds()) {
                  if (coreUserProfileDTOMap.containsKey(toNotifyUserId)) {
                    SimpleUserProfileVO simpleUserProfileVO = new SimpleUserProfileVO();
                    BeanUtils.copyProperties(coreUserProfileDTOMap.get(toNotifyUserId), simpleUserProfileVO);
                    simpleUserProfileVOs.add(simpleUserProfileVO);
                  }
                }
                statusUpdateResponseVO.setToNotifySimpleUserProfileVOs(simpleUserProfileVOs);
              }
              statusUpdateResponseVOs.add(statusUpdateResponseVO);
            }

          }
        }
        // remove items which find no CUP (the user has been deleted)
        List<StatusUpdateResponseVO> cleanStatusUpdateVOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(statusUpdateResponseVOs)) {
          for (StatusUpdateResponseVO statusUpdateResponseVO : statusUpdateResponseVOs) {
            if (null != statusUpdateResponseVO.getUserCoreUserProfileVO()) {
              cleanStatusUpdateVOs.add(statusUpdateResponseVO);
            }
          }
        }
        statusUpdateResponseListVO.setStatusUpdateResponseVOs(statusUpdateResponseVOs);
        statusUpdateResponseListVO.setTotalNumber(rpcListResult.getTotalNumber());
        result.setData(statusUpdateResponseListVO);
      }
      result.setCodeAndMsg(rpcListStatus);
    } catch (Exception e) {
      LOGGER.info("listStatusUpdate()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/employ-managements/status-updates/{statusUpdateId}/revoke",
          method = RequestMethod.PUT,
          produces = "application/json")
  @ResponseBody
  public Result revokeStatusUpdate(
          @PathVariable("statusUpdateId") String encryptedStatusUpdateId,
          @RequestBody StatusUpdateRequestVO statusUpdateRequestVO
  ) {

    Result result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long statusUpdateId = 0L;
    int statusUpdateType = statusUpdateRequestVO.getStatusType();

    try {
      statusUpdateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedStatusUpdateId));
    } catch (Exception e) {
      LOGGER.error("revokeStatusUpdate()-error: invalid statusUpdateId");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (null == StatusType.getEnumByCode(statusUpdateType)) {
      LOGGER.error("revokeStatusUpdate()-error: invalid statusUpdateType");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!onboardingAdminPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      VoidDTO rpcResult = null;
      if (StatusType.EMPLOYMENT_STATUS.getCode() == statusUpdateType) {
        rpcResult = facadeFactory.getUserProfileFacade().revokePassProbationStatusUpdate(
                authedOrgId, statusUpdateId, statusUpdateType, authedActorUserId, authedAdminUserId);
      } else {
        rpcResult = facadeFactory.getUserProfileFacade().revokeResignStatusUpdate(
                authedOrgId, statusUpdateId, statusUpdateType, authedActorUserId, authedAdminUserId);
      }
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("revokeStatusUpdate()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/employ-managements/user-accounts/{userId}",
          method = RequestMethod.DELETE,
          produces = "application/json")
  @ResponseBody
  public Result deleteUserAccount(
          @PathVariable("userId") String encryptedUserId
  ) {

    Result result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long userId = 0L;

    try {
      userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
    } catch (Exception e) {
      LOGGER.error("deleteUserAccount()-error: invalid userId");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (userId == authedActorUserId) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!onboardingAdminPermissionChecker.canDelete(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    // 检测是否有剩余的评价活动
    ReviewInvitationListDTO remoteResult = facadeFactory.getReviewInvitationFacade().listUnSubmittedReviewInvitation(
            authedOrgId, userId, authedActorUserId, authedAdminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    } else if (serviceStatus == ServiceStatus.COMMON_OK
            && !CollectionUtils.isEmpty(remoteResult.getReviewInvitationDTOs())) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_REMAINING_REVIEW_TODO);
    }

    VoidDTO rpcDeleteResult = facadeFactory.getUserProfileFacade().deleteUser(authedOrgId, userId, authedActorUserId);
    ServiceStatus rpcDeleteStatus = ServiceStatus.getEnumByCode(rpcDeleteResult.getServiceStatusDTO().getCode());
    result.setCodeAndMsg(rpcDeleteStatus);


    return result;
  }

  private void copyAllPropertiesFromDTOToVO(
          JobTransferResponseDTO jobTransferResponseDTO, JobTransferResponseVO jobTransferResponseVO) {
    BeanUtils.copyProperties(jobTransferResponseDTO, jobTransferResponseVO);
    if (null != jobTransferResponseDTO.getUserSimpleUserProfileDTO()) {
      SimpleUserProfileVO simpleUserProfileVO = new SimpleUserProfileVO();
      BeanUtils.copyProperties(jobTransferResponseDTO.getUserSimpleUserProfileDTO(), simpleUserProfileVO);
      jobTransferResponseVO.setUserSimpleUserProfileVO(simpleUserProfileVO);
    }
    if (null != jobTransferResponseDTO.getBeforeTeamDTO()) {
      TeamVO teamVO = new TeamVO();
      BeanUtils.copyProperties(jobTransferResponseDTO.getBeforeTeamDTO(), teamVO);
      jobTransferResponseVO.setBeforeTeamVO(teamVO);
    }
    if (null != jobTransferResponseDTO.getBeforeReporterSimpleUserProfileDTO()) {
      SimpleUserProfileVO simpleUserProfileVO = new SimpleUserProfileVO();
      BeanUtils.copyProperties(jobTransferResponseDTO.getBeforeReporterSimpleUserProfileDTO(), simpleUserProfileVO);
      jobTransferResponseVO.setBeforeReporterSimpleUserProfileVO(simpleUserProfileVO);
    }
    if (null != jobTransferResponseDTO.getBeforeJobTitleOrgPickOptionDTO()) {
      OrgPickOptionVO orgPickOptionVO = new OrgPickOptionVO();
      BeanUtils.copyProperties(jobTransferResponseDTO.getBeforeJobTitleOrgPickOptionDTO(), orgPickOptionVO);
      jobTransferResponseVO.setBeforeJobTitleOrgPickOptionVO(orgPickOptionVO);
    }
    if (null != jobTransferResponseDTO.getBeforeJobLevelOrgPickOptionDTO()) {
      OrgPickOptionVO orgPickOptionVO = new OrgPickOptionVO();
      BeanUtils.copyProperties(jobTransferResponseDTO.getBeforeJobLevelOrgPickOptionDTO(), orgPickOptionVO);
      jobTransferResponseVO.setBeforeJobLevelOrgPickOptionVO(orgPickOptionVO);
    }
    if (null != jobTransferResponseDTO.getAfterTeamDTO()) {
      TeamVO teamVO = new TeamVO();
      BeanUtils.copyProperties(jobTransferResponseDTO.getAfterTeamDTO(), teamVO);
      jobTransferResponseVO.setAfterTeamVO(teamVO);
    }
    if (null != jobTransferResponseDTO.getAfterReporterSimpleUserProfileDTO()) {
      SimpleUserProfileVO simpleUserProfileVO = new SimpleUserProfileVO();
      BeanUtils.copyProperties(jobTransferResponseDTO.getAfterReporterSimpleUserProfileDTO(), simpleUserProfileVO);
      jobTransferResponseVO.setAfterReporterSimpleUserProfileVO(simpleUserProfileVO);
    }
    if (null != jobTransferResponseDTO.getAfterJobTitleOrgPickOptionDTO()) {
      OrgPickOptionVO orgPickOptionVO = new OrgPickOptionVO();
      BeanUtils.copyProperties(jobTransferResponseDTO.getAfterJobTitleOrgPickOptionDTO(), orgPickOptionVO);
      jobTransferResponseVO.setAfterJobTitleOrgPickOptionVO(orgPickOptionVO);
    }
    if (null != jobTransferResponseDTO.getAfterJobLevelOrgPickOptionDTO()) {
      OrgPickOptionVO orgPickOptionVO = new OrgPickOptionVO();
      BeanUtils.copyProperties(jobTransferResponseDTO.getAfterJobLevelOrgPickOptionDTO(), orgPickOptionVO);
      jobTransferResponseVO.setAfterJobLevelOrgPickOptionVO(orgPickOptionVO);
    }
    if (!CollectionUtils.isEmpty(jobTransferResponseDTO.getSimpleUserProfileDTOs())) {
      List<SimpleUserProfileDTO> simpleUserProfileDTOs = jobTransferResponseDTO.getSimpleUserProfileDTOs();
      List<SimpleUserProfileVO> simpleUserProfileVOs = new ArrayList<>();
      for (int i = 0; i < simpleUserProfileDTOs.size(); i++) {
        SimpleUserProfileVO simpleUserProfileVO = new SimpleUserProfileVO();
        BeanUtils.copyProperties(simpleUserProfileDTOs.get(i), simpleUserProfileVO);
        simpleUserProfileVOs.add(simpleUserProfileVO);
      }
      jobTransferResponseVO.setToNotifySimpleUserProfileVOs(simpleUserProfileVOs);
    }
  }

  private List<Long> decrypteUserIds(List<String> encryptedUserIds) throws Exception {
    List<Long> userIds = new ArrayList<>();
    if (!CollectionUtils.isEmpty(encryptedUserIds)) {
      for (int i = 0; i < encryptedUserIds.size(); i++) {
        userIds.add(Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserIds.get(i))));
      }
    }
    return userIds;
  }

}
