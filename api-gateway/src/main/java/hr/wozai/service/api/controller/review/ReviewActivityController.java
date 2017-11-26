// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.review;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.ReviewHelper;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.client.helper.ReviewActivityHelper;
import hr.wozai.service.review.client.helper.ReviewInvitationHelper;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;

import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.dto.RoleDTO;
import hr.wozai.service.user.client.userorg.dto.RoleListDTO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.api.vo.review.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-22
 */
@Controller("reviewActivityController")
public class ReviewActivityController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewActivityController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  private ReviewUtils reviewUtils;

  @LogAround

  @RequestMapping(value = "/reviews/activities/{activityId}/detail",
      method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getReviewActivityDetail(
      @PathVariable(value = "activityId") String encryptedActivityId
  ) throws Exception {
    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long activityId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedActivityId));

    ReviewActivityDetailDTO reviewActivityDetailDTO = facadeFactory.getReviewActivityDetailFacade()
            .getReviewActivityDetailDTO(orgId, activityId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != reviewActivityDetailDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewActivityDetailDTO.getServiceStatusDTO().getCode());
    }
    ReviewActivityDTO reviewActivityDTO = facadeFactory.getReviewActivityFacade()
            .findReviewActivity(orgId, activityId, actorUserId, adminUserId);
    Integer activityDisplayType = ReviewActivityHelper.getActivityDisplayType(
        reviewActivityDTO.getIsSubmitted(), reviewActivityDTO.getSelfReviewDeadline(),
        reviewActivityDTO.getPeerReviewDeadline());

    int status = reviewActivityDetailDTO.getReviewTemplateDTO().getState();
    if (status == ReviewTemplateStatus.CANCELED.getCode()) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_ACTIVITY_NOT_FOUND);
    }

    List<ReviewProjectDTO> reviewProjectDTOs = reviewActivityDetailDTO.getReviewProjectDTOs();
    List<ReviewProjectSimpleVO> projects = reviewUtils.getReviewProjectSimpleVO(reviewProjectDTOs);

    List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs = reviewActivityDetailDTO.getReviewQuestionDetailDTOs();
    List<ReviewQuestionVO> reviewQuestionVOs = reviewUtils.getReviewQuestionVOs(orgId, reviewQuestionDetailDTOs,
        actorUserId, adminUserId);

    ReviewDetailVO reviewDetailVO = new ReviewDetailVO();
    BeanUtils.copyProperties(reviewActivityDetailDTO, reviewDetailVO);
    reviewDetailVO.setActivityDisplayType(activityDisplayType);

    ReviewTemplateDTO reviewTemplateDTO = reviewActivityDetailDTO.getReviewTemplateDTO();
    ReviewTemplateVO reviewTemplateVO = new ReviewTemplateVO();
    BeanUtils.copyProperties(reviewTemplateDTO, reviewTemplateVO);
    reviewDetailVO.setReviewTemplate(reviewTemplateVO);

    long revieweeId = reviewActivityDetailDTO.getRevieweeId();
    CoreUserProfileVO userProfileVO = reviewUtils.getCoreUserProfileVO(orgId, revieweeId, actorUserId, adminUserId);
    reviewDetailVO.setRevieweeUserProfile(userProfileVO);

    reviewDetailVO.setProjects(projects);
    reviewDetailVO.setQuestions(reviewQuestionVOs);

    // make review anonymous if required
    int isReviewerAnonymous = reviewTemplateDTO.getIsReviewerAnonymous();
    if (1 == isReviewerAnonymous) {
      RoleListDTO roleListDTO = facadeFactory.getSecurityModelFacade()
              .getRoleListDTOByUserId(orgId, actorUserId, actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != roleListDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(roleListDTO.getServiceStatusDTO().getCode());
      }
      boolean isHr = false;
      for (RoleDTO roleDTO: roleListDTO.getRoleDTOList()) {
        if (roleDTO.getRoleName().equals(DefaultRole.HR.getName())) {
          isHr = true;
          break;
        }
      }
      if (!isHr) {
        if (!CollectionUtils.isEmpty(reviewDetailVO.getQuestions())) {
          for (ReviewQuestionVO reviewQuestionVO: reviewDetailVO.getQuestions()) {
            if (!CollectionUtils.isEmpty(reviewQuestionVO.getSubmittedComment())) {
              for (ReviewCommentVO reviewCommentVO: reviewQuestionVO.getSubmittedComment()) {
                ReviewHelper.makeReviewCommentVOAnonymous(reviewCommentVO);
              }
            }
          }
        }
      }
    }

    result.setData(reviewDetailVO);
    result.setCodeAndMsg(ServiceStatus.getEnumByCode(ServiceStatus.COMMON_OK.getCode()));

    return result;
  }

  @LogAround

  @RequestMapping(value = "/reviews/templates/{templateId}/activities",
      method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<ReviewActivityVO> getReviewActivity(
      @RequestParam(value = "userId") String encryptedUserId,
      @PathVariable(value = "templateId") String encryptedTemplateId
  ) throws Exception {

    Result<ReviewActivityVO> result = new Result<>();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();
    long userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));
    long templateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedTemplateId));

    // 1) get reviewActivity
    ReviewActivityDTO reviewActivityDTO = facadeFactory.getReviewActivityFacade()
        .findReviewActivityByTemplateIdAndUserId(orgId, templateId, userId, actorUserId, adminUserId);
    ServiceStatus rpcGetStatus = ServiceStatus.getEnumByCode(reviewActivityDTO.getServiceStatusDTO().getCode());
    if (ServiceStatus.REVIEW_ACTIVITY_NOT_FOUND.equals(rpcGetStatus)) {
      result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    } else {
      if (ServiceStatus.COMMON_OK.equals(rpcGetStatus)) {
        ReviewActivityVO reviewActivityVO = new ReviewActivityVO();
        BeanUtils.copyProperties(reviewActivityDTO, reviewActivityVO);
        // 2) get reviewers
        ReviewInvitationListDTO reviewInvitationListDTO = facadeFactory.getReviewInvitationFacade()
            .listAllReviewInvitationByTemplateIdAndRevieweeId(orgId, templateId, userId, actorUserId, adminUserId);
        ServiceStatus rpcListStatus =
            ServiceStatus.getEnumByCode(reviewInvitationListDTO.getServiceStatusDTO().getCode());
        if (!ServiceStatus.COMMON_OK.equals(rpcListStatus)) {
          throw new ServiceStatusException(rpcListStatus);
        }
        List<Long> relevantUserIds = getReviewerUserIds(reviewInvitationListDTO.getReviewInvitationDTOs());
        relevantUserIds.add(userId);
        CoreUserProfileListDTO coreUserProfileListDTO = facadeFactory.getUserProfileFacade()
                .listCoreUserProfile(orgId, relevantUserIds, actorUserId, adminUserId);
        setReviewerAndCoreUserProfileVOInReviewActivityVO(
            reviewActivityVO, reviewInvitationListDTO.getReviewInvitationDTOs(),
            coreUserProfileListDTO.getCoreUserProfileDTOs());
        Integer activityDisplayType = ReviewActivityHelper.getActivityDisplayType(
            reviewActivityDTO.getIsSubmitted(), reviewActivityDTO.getSelfReviewDeadline(),
            reviewActivityDTO.getPeerReviewDeadline());
        reviewActivityVO.setActivityDisplayType(activityDisplayType);
        result.setData(reviewActivityVO);
      }
      result.setCodeAndMsg(rpcGetStatus);
    }

    return result;
  }


  @LogAround

  @RequestMapping(value = "/reviews/activities/{activityId}/invitedUsers",
      method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getReviewActivityInvitation(
      @PathVariable(value = "activityId") String encryptedActivityId
  ) throws Exception {
    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long activityId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedActivityId));

    long managerUserId = reviewUtils.getManagerUserId(orgId, actorUserId, actorUserId, adminUserId);

    ReviewInvitedUserListDTO reviewInvitedUserListDTO = facadeFactory.getReviewActivityDetailFacade()
            .getReviewActivityInvitation(orgId, activityId, managerUserId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != reviewInvitedUserListDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewInvitedUserListDTO.getServiceStatusDTO().getCode());
    }

    ReviewInvitedUsersVO reviewInvitedUsersVO = new ReviewInvitedUsersVO();

    // Add manager user
    ReviewInvitedUserDTO managerUserDTO = reviewInvitedUserListDTO.getManagerUserDTO();
    ReviewInvitedUserStatus managerUserStatus = getReviewInvitedUserStatus(orgId, managerUserDTO,
        actorUserId, adminUserId);
    reviewInvitedUsersVO.setManagerUser(managerUserStatus);

    // Add invited user
    List<ReviewInvitedUserDTO> invitedUserDTOs = reviewInvitedUserListDTO.getReviewInvitedUserDTOs();
    if(null == invitedUserDTOs) {
      invitedUserDTOs = Collections.EMPTY_LIST;
    }
    List<ReviewInvitedUserStatus> invitedUserStatus = new ArrayList<>();
    for(ReviewInvitedUserDTO reviewInvitedUserDTO: invitedUserDTOs) {
      ReviewInvitedUserStatus userStatus = getReviewInvitedUserStatus(orgId, reviewInvitedUserDTO,
          actorUserId, adminUserId);
      invitedUserStatus.add(userStatus);
    }
    reviewInvitedUsersVO.setInvitedUsers(invitedUserStatus);

    // Set isAddable
    reviewInvitedUsersVO.setIsAddable(reviewInvitedUserListDTO.getIsAddable());

    result.setData(reviewInvitedUsersVO);
    result.setCodeAndMsg(ServiceStatus.getEnumByCode(ServiceStatus.COMMON_OK.getCode()));

    return result;
  }

  private ReviewInvitedUserStatus getReviewInvitedUserStatus(long orgId, ReviewInvitedUserDTO reviewInvitedUserDTO,
                                                             long actorUserId, long adminUserId) {

    ReviewInvitedUserStatus userStatus = new ReviewInvitedUserStatus();

    long userId = reviewInvitedUserDTO.getUserId();
    CoreUserProfileVO coreUserProfileVO =
        reviewUtils.getCoreUserProfileVO(orgId, userId, actorUserId, adminUserId);
    userStatus.setInvitedUserProfile(coreUserProfileVO);
    userStatus.setStatus(reviewInvitedUserDTO.getStatus());

    return userStatus;
  }

  @LogAround

  @RequestMapping(value = "/reviews/activities/{activityId}/invitedUsers",
      method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> setReviewActivityInvitation(
      @PathVariable(value = "activityId") String encryptedActivityId,
      @RequestBody ReviewInputInvitedUsersVO invitedUsersVO
  ) throws Exception {
    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long activityId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedActivityId));

    List<IdVO> invitedUserIdVOs = invitedUsersVO.getInvitedUserIds();
    List<Long> invitedUserIds = new ArrayList<>();
    for(IdVO idVO: invitedUserIdVOs) {
      invitedUserIds.add(idVO.getIdValue());
    }

    long managerUserId = reviewUtils.getManagerUserId(orgId, actorUserId, actorUserId, adminUserId);

    VoidDTO remoteResult = facadeFactory.getReviewActivityDetailFacade().setReviewActivityInvitation(
        orgId, activityId, managerUserId, invitedUserIds,actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(ServiceStatus.COMMON_OK.getCode()));
    return result;
  }


  @LogAround

  @RequestMapping(value = "/reviews/activities/{activityId}/submit",
      method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> submitReviewActivity(
      @PathVariable(value = "activityId") String encryptedActivityId
  ) throws Exception {
    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long activityId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedActivityId));

    VoidDTO remoteResult = facadeFactory.getReviewActivityDetailFacade().submitReviewActivity(
        orgId, activityId, actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(ServiceStatus.COMMON_OK.getCode()));
    return result;
  }

  @LogAround

  @RequestMapping(value = "/reviews/activities/{activityId}/overview",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getReviewActivityOverview(
          @PathVariable(value = "activityId") String encryptedActivityId
  ) throws Exception {
    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    boolean isPermitted = reviewUtils.isPermitted(orgId, actorUserId,
            ResourceCode.REVIEW_ADMIN.getResourceCode(), ActionCode.CREATE.getCode());
    if(!isPermitted) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    long activityId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedActivityId));

    ReviewActivityDetailDTO reviewActivityDetailDTO =
            facadeFactory.getReviewActivityDetailFacade().getReviewActivityDetailDTOByHR(orgId, activityId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != reviewActivityDetailDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewActivityDetailDTO.getServiceStatusDTO().getCode());
    }

    List<ReviewProjectDTO> reviewProjectDTOs = reviewActivityDetailDTO.getReviewProjectDTOs();
    List<ReviewProjectSimpleVO> projects = reviewUtils.getReviewProjectSimpleVO(reviewProjectDTOs);

    List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs = reviewActivityDetailDTO.getReviewQuestionDetailDTOs();
    List<ReviewQuestionVO> reviewQuestionVOs = reviewUtils.getReviewQuestionVOs(orgId, reviewQuestionDetailDTOs,
            actorUserId, adminUserId);

    ReviewDetailVO reviewDetailVO = new ReviewDetailVO();
    BeanUtils.copyProperties(reviewActivityDetailDTO, reviewDetailVO);

    ReviewTemplateDTO reviewTemplateDTO = reviewActivityDetailDTO.getReviewTemplateDTO();
    ReviewTemplateVO reviewTemplateVO = new ReviewTemplateVO();
    BeanUtils.copyProperties(reviewTemplateDTO, reviewTemplateVO);
    reviewDetailVO.setReviewTemplate(reviewTemplateVO);

    long revieweeId = reviewActivityDetailDTO.getRevieweeId();
    CoreUserProfileVO userProfileVO = reviewUtils.getCoreUserProfileVO(orgId, revieweeId, actorUserId, adminUserId);
    reviewDetailVO.setRevieweeUserProfile(userProfileVO);

    reviewDetailVO.setProjects(projects);
    reviewDetailVO.setQuestions(reviewQuestionVOs);

    result.setData(reviewDetailVO);
    result.setCodeAndMsg(ServiceStatus.getEnumByCode(ServiceStatus.COMMON_OK.getCode()));

    return result;
  }

  @LogAround

  @RequestMapping(value = "/reviews/activities/unsubmitted", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listUnSubmittedActivity() throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    ReviewActivityListDTO remoteResult = facadeFactory.getReviewActivityFacade()
        .listUnSubmittedReviewActivity(orgId, actorUserId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    List<ReviewActivityDTO> reviewActivityDTOs = remoteResult.getReviewActivityDTOs();
    List<ReviewActivityVO> reviewActivityVOs = new ArrayList();
    for(ReviewActivityDTO reviewActivityDTO: reviewActivityDTOs) {
      ReviewActivityVO reviewActivityVO = new ReviewActivityVO();
      BeanUtils.copyProperties(reviewActivityDTO, reviewActivityVO);

      long templateId = reviewActivityDTO.getTemplateId();
      ReviewTemplateDTO reviewTemplateDTO = facadeFactory.getReviewTemplateFacade()
          .findReviewTemplate(orgId, templateId, actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != reviewTemplateDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(reviewTemplateDTO.getServiceStatusDTO().getCode());
      }

      BeanUtils.copyProperties(reviewTemplateDTO, reviewActivityVO);
      reviewActivityVOs.add(reviewActivityVO);
    }

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    result.setData(reviewActivityVOs);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/reviews/activities/others", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listOtherReviewActivity(
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

    ReviewActivityListDTO remoteResult = facadeFactory.getReviewActivityFacade()
        .listOtherReviewActivity(orgId, actorUserId, pageNumber, pageSize, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    List<ReviewActivityDTO> reviewActivityDTOs = remoteResult.getReviewActivityDTOs();
    List<ReviewActivityVO> reviewActivityVOs = new ArrayList();
    for(ReviewActivityDTO reviewActivityDTO: reviewActivityDTOs) {
      ReviewActivityVO reviewActivityVO = new ReviewActivityVO();
      BeanUtils.copyProperties(reviewActivityDTO, reviewActivityVO);

      long templateId = reviewActivityDTO.getTemplateId();
      ReviewTemplateDTO reviewTemplateDTO = facadeFactory.getReviewTemplateFacade()
          .findReviewTemplate(orgId, templateId, actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != reviewTemplateDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(reviewTemplateDTO.getServiceStatusDTO().getCode());
      }
      BeanUtils.copyProperties(reviewTemplateDTO, reviewActivityVO);

      reviewActivityVOs.add(reviewActivityVO);
    }

    LongDTO amount = facadeFactory.getReviewActivityFacade().countOtherReviewActivity(orgId, actorUserId, actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != amount.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(amount.getServiceStatusDTO().getCode());
    }

    ReviewActivityListVO reviewActivityListVO = new ReviewActivityListVO();
    reviewActivityListVO.setReviewActivities(reviewActivityVOs);
    reviewActivityListVO.setAmount(amount.getData());

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    result.setData(reviewActivityListVO);

    return result;
  }

  private List<Long> getReviewerUserIds(List<ReviewInvitationDTO> reviewInvitationDTOs) {
    Set<Long> userIdSet = new HashSet<>();
    if (!CollectionUtils.isEmpty(reviewInvitationDTOs)) {
      for (ReviewInvitationDTO reviewInvitationDTO: reviewInvitationDTOs) {
        userIdSet.add(reviewInvitationDTO.getReviewerId());
      }
    }
    return new ArrayList<>(userIdSet);
  }



  /**
   * Steps:
   *  1) set reviewee's CUP
   *  2) set manager reviewer's invitation
   *  3) set peer reviewers' invitations
   *
   * @param reviewActivityVO
   * @param reviewInvitationDTOs
   * @param coreUserProfileDTOs
   */
  private void setReviewerAndCoreUserProfileVOInReviewActivityVO(
      ReviewActivityVO reviewActivityVO, List<ReviewInvitationDTO> reviewInvitationDTOs,
      List<CoreUserProfileDTO> coreUserProfileDTOs) {

    if (null == reviewActivityVO
        || CollectionUtils.isEmpty(reviewInvitationDTOs)
        || CollectionUtils.isEmpty(coreUserProfileDTOs)) {
      return;
    }

    Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap=  new HashMap<>();
    for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
      coreUserProfileDTOMap.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
    }

    // 1)
    CoreUserProfileDTO revieweeCUP = coreUserProfileDTOMap.get(reviewActivityVO.getRevieweeId());
    if (null != revieweeCUP) {
      CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
      BeanUtils.copyProperties(revieweeCUP, coreUserProfileVO);
      reviewActivityVO.setRevieweeUserProfile(coreUserProfileVO);
    }

    // 2) & 3)
    List<ReviewInvitationVO> peerReviewInvitationVOs = new ArrayList<>();
    reviewActivityVO.setPeerReviewInvitationVOs(peerReviewInvitationVOs);
    for (ReviewInvitationDTO reviewInvitationDTO: reviewInvitationDTOs) {
      if (reviewInvitationDTO.getIsManager() == 1) {
        ReviewInvitationVO reviewInvitationVO = new ReviewInvitationVO();
        BeanUtils.copyProperties(reviewInvitationDTO, reviewInvitationVO);
        CoreUserProfileDTO coreUserProfileDTO = coreUserProfileDTOMap.get(reviewInvitationDTO.getReviewerId());
        if (null != coreUserProfileDTO) {
          CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
          BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
          Integer invitationDisplayType = ReviewInvitationHelper.getInvitationDisplayType(
              reviewInvitationDTO.getIsInActive(), reviewInvitationDTO.getIsManager(),
              reviewInvitationDTO.getIsSubmitted(), reviewInvitationDTO.getIsCanceled(),
              reviewInvitationDTO.getSelfReviewDeadline(), reviewInvitationDTO.getPeerReviewDeadline(),
              reviewInvitationDTO.getPublicDeadline());
          reviewInvitationVO.setInvitationDisplayType(invitationDisplayType);
          reviewInvitationVO.setReviewerUserProfile(coreUserProfileVO);
        }
        reviewActivityVO.setManagerReviewInvitationVO(reviewInvitationVO);
      } else {
        ReviewInvitationVO reviewInvitationVO = new ReviewInvitationVO();
        BeanUtils.copyProperties(reviewInvitationDTO, reviewInvitationVO);
        CoreUserProfileDTO coreUserProfileDTO = coreUserProfileDTOMap.get(reviewInvitationDTO.getReviewerId());
        if (null != coreUserProfileDTO) {
          CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
          BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
          Integer invitationDisplayType = ReviewInvitationHelper.getInvitationDisplayType(
              reviewInvitationDTO.getIsInActive(), reviewInvitationDTO.getIsManager(),
              reviewInvitationDTO.getIsSubmitted(), reviewInvitationDTO.getIsCanceled(),
              reviewInvitationDTO.getSelfReviewDeadline(), reviewInvitationDTO.getPeerReviewDeadline(),
              reviewInvitationDTO.getPublicDeadline());
          reviewInvitationVO.setInvitationDisplayType(invitationDisplayType);
          reviewInvitationVO.setReviewerUserProfile(coreUserProfileVO);
        }
        peerReviewInvitationVOs.add(reviewInvitationVO);
      }
    }
  }
}
