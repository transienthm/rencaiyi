// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.review;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.vo.review.ReviewCommentInputVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.ReviewInvitationDTO;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-22
 */
@Controller("reviewInvitationCommentControllerNew")
public class ReviewInvitationCommentController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationCommentController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  private ReviewUtils reviewUtils;

  @LogAround

  @RequestMapping(value = "/reviews/invitations/{invitationId}/questions/{questionId}/comments",
      method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> insertComment(
      @PathVariable(value = "invitationId") String encryptedInvitationId,
      @PathVariable(value = "questionId") String encryptedQuestionId,
      @RequestBody @Valid ReviewCommentInputVO reviewCommentInputVO,
      BindingResult bindingResult
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long invitationId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedInvitationId));
    long questionId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedQuestionId));

    ReviewInvitationDTO reviewInvitationDTO = facadeFactory.getReviewInvitationFacade()
            .findReviewInvitation(orgId, invitationId, actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != reviewInvitationDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewInvitationDTO.getServiceStatusDTO().getCode());
    }
    long managerUserId = reviewUtils.getManagerUserId(orgId, reviewInvitationDTO.getRevieweeId(),
        actorUserId, adminUserId);

    // # parameter check
    String content = reviewCommentInputVO.getContent();
    if(null == content || content.trim().isEmpty()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    LongDTO remoteResult = facadeFactory.getReviewInvitationDetailFacade().insertInvitationComment(orgId,
        invitationId, questionId, managerUserId, content, actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(remoteResult.getServiceStatusDTO().getCode());
    }

    IdVO data = new IdVO();
    data.setIdValue(remoteResult.getData());

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    result.setData(data);

    return result;
  }

  @LogAround

  @BindingResultMonitor
  @RequestMapping(value = "/reviews/invitations/{invitationId}/questions/{questionId}/comments/{commentId}",
      method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> updateComment(
      @PathVariable(value = "invitationId") String encryptedInvitationId,
      @PathVariable(value = "questionId") String encryptedQuestionId,
      @PathVariable(value = "commentId") String encryptedCommentId,
      @RequestBody @Valid ReviewCommentInputVO reviewCommentInputVO,
      BindingResult bindingResult
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long invitationId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedInvitationId));
    long questionId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedQuestionId));
    long commentId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedCommentId));

    // # parameter check
    String content = reviewCommentInputVO.getContent();
    if(null == content || content.trim().isEmpty()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    ReviewInvitationDTO reviewInvitationDTO = facadeFactory.getReviewInvitationFacade()
            .findReviewInvitation(orgId, invitationId, actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != reviewInvitationDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewInvitationDTO.getServiceStatusDTO().getCode());
    }
    long managerUserId = reviewUtils.getManagerUserId(orgId, reviewInvitationDTO.getRevieweeId(),
        actorUserId, adminUserId);

    VoidDTO remoteResult = facadeFactory.getReviewInvitationDetailFacade().updateInvitationComment(orgId,
        invitationId, commentId, managerUserId, content, actorUserId, adminUserId);

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));

    return result;
  }

}
