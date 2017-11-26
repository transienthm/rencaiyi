// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
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

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.review.ReviewInvitationListVO;
import hr.wozai.service.api.vo.review.ReviewInvitationVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.review.client.dto.ReviewInvitationDTO;
import hr.wozai.service.review.client.dto.ReviewInvitationListDTO;
import hr.wozai.service.review.client.helper.ReviewInvitationHelper;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-22
 */
@Controller("reviewInvitationController")
public class ReviewInvitationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  private ReviewUtils reviewUtils;

  @LogAround

  @RequestMapping(value = "/reviews/templates/{templateId}/invitations-as-reviewer",
      method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<ReviewInvitationListVO> listReviewInvitationAsReviewer(
      @PathVariable("templateId") String encryptedTemplateId,
      @RequestParam("userId") String encryptedUserId,
      @RequestParam("isManager") Integer isManager
  ) throws Exception {

    Result<ReviewInvitationListVO> result = new Result<>();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();
    long templateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedTemplateId));
    long reviewerId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));

    // 1) get invitations
    ReviewInvitationListDTO reviewInvitationListDTO =
        facadeFactory.getReviewInvitationFacade().listAllReviewInvitationByTemplateIdAndReviewerIdAndIsManager(
            orgId, templateId, reviewerId, isManager, actorUserId, adminUserId);
    ServiceStatus rpcListStatus = ServiceStatus.getEnumByCode(reviewInvitationListDTO.getServiceStatusDTO().getCode());
    if (!ServiceStatus.COMMON_OK.equals(rpcListStatus)) {
      throw new ServiceStatusException(rpcListStatus);
    }
    ReviewInvitationListVO reviewInvitationListVO = new ReviewInvitationListVO();
    List<ReviewInvitationVO> reviewInvitationVOs = new ArrayList<>();
    for (ReviewInvitationDTO reviewInvitationDTO: reviewInvitationListDTO.getReviewInvitationDTOs()) {
      ReviewInvitationVO reviewInvitationVO = new ReviewInvitationVO();
      BeanUtils.copyProperties(reviewInvitationDTO, reviewInvitationVO);
      Integer invitationDisplayType = ReviewInvitationHelper.getInvitationDisplayType(
          reviewInvitationDTO.getIsInActive(), reviewInvitationDTO.getIsManager(), reviewInvitationDTO.getIsSubmitted(),
          reviewInvitationDTO.getIsCanceled(), reviewInvitationDTO.getSelfReviewDeadline(),
          reviewInvitationDTO.getPeerReviewDeadline(), reviewInvitationDTO.getPublicDeadline());
      reviewInvitationVO.setInvitationDisplayType(invitationDisplayType);
      reviewInvitationVOs.add(reviewInvitationVO);
    }
    reviewInvitationListVO.setReviewInvitations(reviewInvitationVOs);
    result.setData(reviewInvitationListVO);

    // 2) set CUPs
    List<Long> revieweeUserIds = getRevieweeUserIds(reviewInvitationListDTO.getReviewInvitationDTOs());
    if (!CollectionUtils.isEmpty(revieweeUserIds)) {
      CoreUserProfileListDTO coreUserProfileListDTO = facadeFactory.getUserProfileFacade()
              .listCoreUserProfile(orgId, revieweeUserIds, actorUserId, adminUserId);
      ServiceStatus rpcCUPsStatus = ServiceStatus.getEnumByCode(coreUserProfileListDTO.getServiceStatusDTO().getCode());
      if (!ServiceStatus.COMMON_OK.equals(rpcCUPsStatus)) {
        throw new ServiceStatusException(rpcCUPsStatus);
      }
      Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap = new HashMap<>();
      for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileListDTO.getCoreUserProfileDTOs()) {
        coreUserProfileDTOMap.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
      }
      for (int i = 0; i < reviewInvitationVOs.size(); i++) {
        CoreUserProfileDTO coreUserProfileDTO = coreUserProfileDTOMap.get(reviewInvitationVOs.get(i).getRevieweeId());
        if (null != coreUserProfileDTO) {
          CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
          BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
          reviewInvitationVOs.get(i).setRevieweeUserProfile(coreUserProfileVO);
        } else {
          reviewInvitationVOs.remove(i);
          i--;
        }
      }
    }

    return result;
  }

  //peer review status
  //1 not submitted, not canceled
  //2 submitted, not canceled
  //3 canceled
  @LogAround

  @RequestMapping(value = "/reviews/invitations/unsubmitted",
      method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listUnSubmittedReviewInvitation() throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    ReviewInvitationListDTO remoteResult = facadeFactory.getReviewInvitationFacade()
            .listUnSubmittedReviewInvitation(orgId, actorUserId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    List<ReviewInvitationDTO> reviewInvitationDTOs = remoteResult.getReviewInvitationDTOs();
    List<ReviewInvitationVO> reviewInvitationVOs =
        reviewUtils.getReviewInvitationVO(reviewInvitationDTOs, actorUserId, adminUserId);

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    result.setData(reviewInvitationVOs);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/reviews/invitations/submitted",
      method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listSubmittedReviewInvitation(
      @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
      @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if(!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    ReviewInvitationListDTO remoteResult = facadeFactory.getReviewInvitationFacade()
            .listSubmittedReviewInvitation(orgId, actorUserId,
        pageNumber, pageSize, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    List<ReviewInvitationDTO> reviewInvitationDTOs = remoteResult.getReviewInvitationDTOs();
    List<ReviewInvitationVO> reviewInvitationVOs =
        reviewUtils.getReviewInvitationVO(reviewInvitationDTOs, actorUserId, adminUserId);

    LongDTO amount = facadeFactory.getReviewInvitationFacade()
            .countSubmittedReviewInvitation(orgId, actorUserId, actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != amount.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(amount.getServiceStatusDTO().getCode());
    }

    ReviewInvitationListVO reviewInvitationListVO = new ReviewInvitationListVO();
    reviewInvitationListVO.setReviewInvitations(reviewInvitationVOs);
    reviewInvitationListVO.setAmount(amount.getData());

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    result.setData(reviewInvitationListVO);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/reviews/invitations/canceled",
      method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listCanceledReviewInvitation(
      @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
      @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize
  ) throws Exception {

    Result<Object> result = new Result<>();

    try {
      long actorUserId = AuthenticationInterceptor.actorUserId.get();
      long adminUserId = AuthenticationInterceptor.adminUserId.get();
      long orgId = AuthenticationInterceptor.orgId.get();

      boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
      if(!isValid) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }

      ReviewInvitationListDTO remoteResult = facadeFactory.getReviewInvitationFacade()
              .listCanceledReviewInvitation(orgId, actorUserId,
          pageNumber, pageSize, actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
      }

      List<ReviewInvitationDTO> reviewInvitationDTOs = remoteResult.getReviewInvitationDTOs();

      List<ReviewInvitationVO> reviewInvitationVOs =
          reviewUtils.getReviewInvitationVO(reviewInvitationDTOs, actorUserId, adminUserId);

      LongDTO amount = facadeFactory.getReviewInvitationFacade()
              .countCanceledReviewInvitation(orgId, actorUserId, actorUserId, adminUserId);
      if(ServiceStatus.COMMON_OK.getCode() != amount.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(amount.getServiceStatusDTO().getCode());
      }

      ReviewInvitationListVO reviewInvitationListVO = new ReviewInvitationListVO();
      reviewInvitationListVO.setReviewInvitations(reviewInvitationVOs);
      reviewInvitationListVO.setAmount(amount.getData());

      result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
      result.setData(reviewInvitationListVO);

    } catch (Exception e) {
      LOGGER.error("listCanceledReviewInvitation()-fail", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


  @LogAround

  @RequestMapping(value = "/reviews/invitations/{invitationId}/refuse",
      method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> refuseReviewInvitation(
      @PathVariable(value = "invitationId") String encryptedInvitationId
  ) throws Exception {
    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long invitationId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedInvitationId));

    VoidDTO remoteResult = facadeFactory.getReviewInvitationFacade()
            .refuseReviewInvitation(orgId, invitationId, actorUserId, adminUserId);
    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));

    return result;
  }

  private List<Long> getRevieweeUserIds(List<ReviewInvitationDTO> reviewInvitationDTOs) {
    Set<Long> userIdSet = new HashSet<>();
    if (!CollectionUtils.isEmpty(reviewInvitationDTOs)) {
      for (ReviewInvitationDTO reviewInvitationDTO: reviewInvitationDTOs) {
        userIdSet.add(reviewInvitationDTO.getRevieweeId());
      }
    }
    return new ArrayList<>(userIdSet);
  }



}
