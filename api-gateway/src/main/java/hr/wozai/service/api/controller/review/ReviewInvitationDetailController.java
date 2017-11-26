// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.review;

import com.alibaba.fastjson.JSONObject;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.ReviewHelper;
import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.client.helper.ReviewInvitationHelper;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.api.vo.review.*;
import hr.wozai.service.user.client.userorg.dto.RoleDTO;
import hr.wozai.service.user.client.userorg.dto.RoleListDTO;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-22
 */
@Controller("reviewInvitationDetailControllerNew")
public class ReviewInvitationDetailController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationDetailController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  private ReviewUtils reviewUtils;

  @LogAround

  @RequestMapping(value = "/reviews/invitations/{invitationId}/detail", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getInvitationReviewDetail(
      @PathVariable(value = "invitationId") String encryptedInvitationId
  ) throws Exception {
    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long invitationId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedInvitationId));

    ReviewInvitationDetailDTO reviewInvitationDetailDTO =
        facadeFactory.getReviewInvitationDetailFacade().getReviewInvitationDetail(orgId, invitationId, actorUserId, adminUserId);

    if (ServiceStatus.COMMON_OK.getCode() != reviewInvitationDetailDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewInvitationDetailDTO.getServiceStatusDTO().getCode());
    }
    Integer invitationDisplayType = ReviewInvitationHelper.getInvitationDisplayType(
        reviewInvitationDetailDTO.getIsInActive(), reviewInvitationDetailDTO.getIsManager(),
        reviewInvitationDetailDTO.getIsSubmitted(), reviewInvitationDetailDTO.getIsCanceled(),
        reviewInvitationDetailDTO.getSelfReviewDeadline(), reviewInvitationDetailDTO.getPeerReviewDeadline(),
        reviewInvitationDetailDTO.getPublicDeadline());

    if (reviewInvitationDetailDTO.getReviewTemplateDTO().getState() == ReviewTemplateStatus.CANCELED.getCode()) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_INVITATION_NOT_FOUND);
    }

    List<ReviewProjectDTO> reviewProjectDTOs = reviewInvitationDetailDTO.getReviewProjectDTOs();
    List<ReviewProjectSimpleVO> projects = reviewUtils.getReviewProjectSimpleVO(reviewProjectDTOs);

    List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs = reviewInvitationDetailDTO.getReviewQuestionDetailDTOs();
    List<ReviewQuestionVO> reviewQuestions = reviewUtils.getReviewQuestionVOs(orgId, reviewQuestionDetailDTOs,
        actorUserId, adminUserId);

    List<ReviewPastInvitationDTO> pastInvitationDTOs = reviewInvitationDetailDTO.getPastInvitationDTOs();
    List<ReviewPastInvitationVO> pastInvitationVOs = new ArrayList<>();
    for(ReviewPastInvitationDTO pastInvitationDTO: pastInvitationDTOs) {
      if (invitationId == pastInvitationDTO.getInvitationId()) {
        continue;
      }
      ReviewPastInvitationVO pastInvitationVO = new ReviewPastInvitationVO();
      BeanUtils.copyProperties(pastInvitationDTO, pastInvitationVO);
      pastInvitationVOs.add(pastInvitationVO);
    }

    ReviewDetailVO reviewDetailVO = new ReviewDetailVO();
    BeanUtils.copyProperties(reviewInvitationDetailDTO, reviewDetailVO);
    reviewDetailVO.setInvitationDisplayType(invitationDisplayType);

    ReviewTemplateDTO reviewTemplateDTO = reviewInvitationDetailDTO.getReviewTemplateDTO();
    ReviewTemplateVO reviewTemplateVO = new ReviewTemplateVO();
    BeanUtils.copyProperties(reviewTemplateDTO, reviewTemplateVO);
    reviewDetailVO.setReviewTemplate(reviewTemplateVO);

    long revieweeId = reviewInvitationDetailDTO.getRevieweeId();
    CoreUserProfileVO coreUserProfileVO = reviewUtils.getCoreUserProfileVO(orgId, revieweeId, actorUserId, adminUserId);
    reviewDetailVO.setRevieweeUserProfile(coreUserProfileVO);

    reviewDetailVO.setProjects(projects);
    reviewDetailVO.setQuestions(reviewQuestions);
    reviewDetailVO.setPastInvitations(pastInvitationVOs);

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

  @RequestMapping(value = "/reviews/invitations/{invitationId}/invitedUsers",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getReviewActivityInvitation(
          @PathVariable(value = "invitationId") String encryptedInvitationId
  ) throws Exception {
    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long invitationId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedInvitationId));

    ReviewInvitationDTO reviewInvitationDTO = facadeFactory.getReviewInvitationFacade()
            .findReviewInvitation(orgId, invitationId, actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != reviewInvitationDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewInvitationDTO.getServiceStatusDTO().getCode());
    }

    long revieweeId = reviewInvitationDTO.getRevieweeId();
    long managerUserId = reviewUtils.getManagerUserId(orgId, revieweeId, actorUserId, adminUserId);

    ReviewInvitedUserListDTO reviewInvitedUserListDTO = facadeFactory.getReviewInvitationDetailFacade()
            .getReviewActivityInvitation(orgId, invitationId, managerUserId, actorUserId, adminUserId);
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
    userStatus.setStatus(userStatus.getStatus());

    return userStatus;
  }

  @LogAround

  @RequestMapping(value = "/reviews/invitations/{invitationId}/submit",
      method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> submitReviewInvitation(
      @PathVariable(value = "invitationId") String encryptedInvitationId,
      @RequestBody(required = false) JSONObject jsonObject
  ) throws Exception {
    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long invitationId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedInvitationId));

    ReviewInvitationDTO reviewInvitationDTO = facadeFactory.getReviewInvitationFacade()
            .findReviewInvitation(orgId, invitationId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != reviewInvitationDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewInvitationDTO.getServiceStatusDTO().getCode());
    }

    long managerUserId = reviewUtils.getManagerUserId(orgId, reviewInvitationDTO.getRevieweeId(),
        actorUserId, adminUserId);
    boolean isManager = (actorUserId == managerUserId);

    int score = 0;
    if(isManager) {
      if (null == jsonObject || !jsonObject.containsKey("score")) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      } else {
        score = Integer.parseInt(jsonObject.get("score").toString());
      }
      reviewInvitationDTO.setScore(score);
    }

    VoidDTO remoteResult = null;
    if (isManager) {
      remoteResult = facadeFactory.getReviewInvitationDetailFacade()
          .submitManagerReviewInvitation(orgId, invitationId, managerUserId, score, actorUserId, adminUserId);
    } else {
      remoteResult = facadeFactory.getReviewInvitationDetailFacade()
          .submitPeerReviewInvitation(orgId, invitationId, managerUserId, score, actorUserId, adminUserId);
    }
    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));

    return result;
  }

  @LogAround

  @RequestMapping(value = "/reviews/invitations/{invitationId}/cancel-submission",
      method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> cancelSubmissionReviewInvitation(
      @PathVariable(value = "invitationId") String encryptedInvitationId
  ) throws Exception {

    Result<Object> result = new Result<>();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long invitationId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedInvitationId));
    LOGGER.info("cancelSubmissionReviewInvitation(): invitationId={}", invitationId);

    ReviewInvitationDTO reviewInvitationDTO = facadeFactory.getReviewInvitationFacade()
            .findReviewInvitation(orgId, invitationId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != reviewInvitationDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewInvitationDTO.getServiceStatusDTO().getCode());
    }

    long managerUserId = reviewUtils
        .getManagerUserId(orgId, reviewInvitationDTO.getRevieweeId(), actorUserId, adminUserId);
    boolean isManager = (actorUserId == managerUserId);

    VoidDTO remoteResult = null;
    if (isManager) {
      remoteResult = facadeFactory.getReviewInvitationDetailFacade()
          .cancelSubmissionOfManagerReviewInvitation(orgId, invitationId, actorUserId, adminUserId);
    } else {
      remoteResult = facadeFactory.getReviewInvitationDetailFacade()
          .cancelSubmissionOfPeerReviewInvitation(orgId, invitationId, actorUserId, adminUserId);
    }
    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));

    return result;
  }

}
