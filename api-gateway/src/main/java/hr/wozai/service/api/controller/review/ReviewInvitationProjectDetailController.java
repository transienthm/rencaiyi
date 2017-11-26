// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.review;

import hr.wozai.service.api.controller.FacadeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hr.wozai.service.api.helper.ReviewHelper;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.review.ReviewCommentVO;
import hr.wozai.service.api.vo.review.ReviewProjectVO;
import hr.wozai.service.review.client.dto.ReviewCommentDTO;
import hr.wozai.service.review.client.dto.ReviewInvitationDTO;
import hr.wozai.service.review.client.dto.ReviewProjectDetailDTO;
import hr.wozai.service.review.client.dto.ReviewTemplateDTO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.dto.RoleDTO;
import hr.wozai.service.user.client.userorg.dto.RoleListDTO;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-22
 */
@Controller("reviewInvitationProjectDetailControllerNew")
public class ReviewInvitationProjectDetailController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationProjectDetailController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  private ReviewUtils reviewUtils;
  
  @LogAround

  @RequestMapping(value = "/reviews/invitations/{invitationId}/projects/{projectId}",
      method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getInvitationProjectDetail(
      @PathVariable(value = "invitationId") String encryptedInvitationId,
      @PathVariable(value = "projectId") String encryptedProjectId
  ) throws Exception {

    Result<Object> result = new Result<>();

    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long orgId = AuthenticationInterceptor.orgId.get();

    long invitationId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedInvitationId));
    long projectId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedProjectId));

    ReviewInvitationDTO reviewInvitationDTO = facadeFactory.getReviewInvitationFacade()
            .findReviewInvitation(orgId, invitationId, actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != reviewInvitationDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewInvitationDTO.getServiceStatusDTO().getCode());
    }
    long managerUserId = reviewUtils.getManagerUserId(orgId, reviewInvitationDTO.getRevieweeId(),
        actorUserId, adminUserId);

    ReviewProjectDetailDTO reviewProjectDetailDTO = facadeFactory.getReviewInvitationProjectFacade()
            .getInvitationProjectDetail(orgId, invitationId, projectId, managerUserId, actorUserId, adminUserId);
    if(ServiceStatus.COMMON_OK.getCode() != reviewProjectDetailDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(reviewProjectDetailDTO.getServiceStatusDTO().getCode());
    }

    ReviewProjectVO reviewProjectVO = new ReviewProjectVO();

    BeanUtils.copyProperties(reviewProjectDetailDTO, reviewProjectVO);

    List<ReviewCommentDTO> submittedCommentDTOs = reviewProjectDetailDTO.getSubmittedComments();
    if(null == submittedCommentDTOs) {
      submittedCommentDTOs = Collections.EMPTY_LIST;
    }
    List<ReviewCommentVO> submittedCommentVOs = new ArrayList<>();
    for(ReviewCommentDTO reviewCommentDTO: submittedCommentDTOs) {
      ReviewCommentVO reviewCommentVO = reviewUtils.getReviewCommentVO(orgId, reviewCommentDTO, actorUserId, adminUserId);
      submittedCommentVOs.add(reviewCommentVO);
    }
    reviewProjectVO.setSubmittedComments(submittedCommentVOs);

    ReviewCommentDTO reviewerCommentDTO = reviewProjectDetailDTO.getReviewerComment();
    if(null != reviewerCommentDTO) {
      ReviewCommentVO reviewerCommentVO = reviewUtils.getReviewCommentVO(orgId, reviewerCommentDTO, actorUserId, adminUserId);
      reviewProjectVO.setReviewerComment(reviewerCommentVO);
    }

    // make review anonymous if required
    ReviewTemplateDTO reviewTemplateDTO = facadeFactory.getReviewTemplateFacade()
        .findReviewTemplate(orgId, reviewProjectDetailDTO.getTemplateId(), actorUserId, adminUserId);
    int isReviewerAnonymous = reviewTemplateDTO.getIsReviewerAnonymous();
    if (1 == isReviewerAnonymous) {
      RoleListDTO roleListDTO =
          facadeFactory.getSecurityModelFacade().getRoleListDTOByUserId(orgId, actorUserId, actorUserId, adminUserId);
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
        if (!CollectionUtils.isEmpty(reviewProjectVO.getSubmittedComments())) {
          for (ReviewCommentVO reviewCommentVO: reviewProjectVO.getSubmittedComments()) {
            ReviewHelper.makeReviewCommentVOAnonymous(reviewCommentVO);
          }
        }
      }
    }

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(reviewProjectDetailDTO.getServiceStatusDTO().getCode()));
    result.setData(reviewProjectVO);

    return result;
  }
}
