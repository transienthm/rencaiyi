// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.ReviewCommentDTO;
import hr.wozai.service.review.client.dto.ReviewProjectDetailDTO;
import hr.wozai.service.review.client.enums.ReviewItemType;
import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.client.facade.ReviewInvitationProjectFacade;
import hr.wozai.service.review.server.helper.FacadeExceptionHelper;
import hr.wozai.service.review.server.model.ReviewComment;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.review.server.model.ReviewProject;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.service.ReviewCommentService;
import hr.wozai.service.review.server.service.ReviewInvitationService;
import hr.wozai.service.review.server.service.ReviewProjectService;
import hr.wozai.service.review.server.service.ReviewTemplateService;
import hr.wozai.service.review.server.utils.ReviewUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-21
 */
@Service("reviewInvitationProjectFacadeImpl")
public class ReviewInvitationProjectFacadeImpl implements ReviewInvitationProjectFacade {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationProjectFacadeImpl.class);

  @Autowired
  private ReviewTemplateService reviewTemplateService;

  @Autowired
  private ReviewInvitationService reviewInvitationService;

  @Autowired
  private ReviewProjectService reviewProjectService;

  @Autowired
  private ReviewCommentService reviewCommentService;

  @Autowired
  private ReviewUtils reviewUtils;

  @LogAround
  @Override
  public LongDTO insertInvitationProjectComment(long orgId, long invitationId, long projectId,
                                                long managerUserId,
                                                String content,
                                                long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # parameter check
      if (null == content || content.trim().isEmpty()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }

      // # permission check
      ReviewInvitation reviewInvitation =
          reviewInvitationService.findReviewInvitation(orgId, invitationId);
      if (actorUserId != reviewInvitation.getReviewerId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # template status check and activity status check
      long templateId = reviewInvitation.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      boolean isCanceled = reviewInvitation.getIsCanceled() == 1;
      if (isCanceled) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      boolean isManger = actorUserId == managerUserId;
      boolean isSubmitted = reviewInvitation.getIsSubmitted() == 1;
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);
      boolean isTemplatePublic = reviewUtils.isTemplatePublic(reviewTemplate);

      boolean status = false;

      if (!isManger) {
        if (!isSubmitted && !isTemplatePublic) {
          status = true;
        } else if (isSubmitted && !isPeerReviewDeadline) {
          status = true;
        }
      } else {
        if (!isSubmitted) {
          status = true;
        } else if (isSubmitted && !isTemplatePublic) {
          status = true;
        }
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      long revieweeId = reviewInvitation.getRevieweeId();

      // # check whether comment already
      boolean existComment = reviewCommentService.existReviewItemCommentByReviewer(orgId, templateId,
          ReviewItemType.PROJECT.getCode(), projectId, revieweeId, actorUserId);
      if(existComment) {
        LOGGER.error("Exist comment!");
        throw new ServiceStatusException(ServiceStatus.REVIEW_COMMENT_EXIST);
      }

      // # insert comment
      ReviewComment reviewComment = new ReviewComment();
      reviewComment.setOrgId(orgId);
      reviewComment.setTemplateId(templateId);
      reviewComment.setRevieweeId(revieweeId);
      reviewComment.setReviewerId(actorUserId);
      reviewComment.setItemType(ReviewItemType.PROJECT.getCode());
      reviewComment.setItemId(projectId);
      reviewComment.setContent(content);
      reviewComment.setLastModifiedUserId(actorUserId);

      long commentId = reviewCommentService.insertReviewComment(reviewComment);

      result.setData(commentId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }


  //TODO: It is the same with question comment update
  @LogAround
  @Override
  public VoidDTO updateInvitationProjectComment(long orgId, long invitationId, long commentId,
                                                long managerUserId,
                                                String content,
                                                long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # parameter check
      if (null == content || content.trim().isEmpty()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }

      // # permission check
      ReviewComment reviewComment = reviewCommentService.findReviewComment(orgId, commentId);
      if (actorUserId != reviewComment.getReviewerId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # template status check and invitation status check
      long templateId = reviewComment.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      ReviewInvitation reviewInvitation = reviewInvitationService.findReviewInvitation(orgId, invitationId);
      boolean isCanceled = reviewInvitation.getIsCanceled() == 1;
      if (isCanceled) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      boolean isManger = actorUserId == managerUserId;
      boolean isSubmitted = reviewInvitation.getIsSubmitted() == 1;
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);
      boolean isTemplatePublic = reviewUtils.isTemplatePublic(reviewTemplate);

      boolean status = false;

      if (!isManger) {
        if (!isSubmitted && !isTemplatePublic) {
          status = true;
        } else if (isSubmitted && !isPeerReviewDeadline) {
          status = true;
        }
      } else {
        if (!isSubmitted) {
          status = true;
        } else if (isSubmitted && !isTemplatePublic) {
          status = true;
        }
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      reviewComment.setContent(content);
      reviewComment.setLastModifiedUserId(actorUserId);
      reviewCommentService.updateReviewComment(reviewComment);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @LogAround
  @Override
  public VoidDTO deleteInvitationProjectComment(long orgId, long invitationId, long commentId,
                                                long managerUserId,
                                                long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # permission check
      ReviewComment reviewComment = reviewCommentService.findReviewComment(orgId, commentId);
      if (actorUserId != reviewComment.getReviewerId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // # template status check and invitation status check
      long templateId = reviewComment.getTemplateId();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      ReviewInvitation reviewInvitation =
          reviewInvitationService.findReviewInvitation(orgId, invitationId);

      boolean isCanceled = reviewInvitation.getIsCanceled() == 1;
      if (isCanceled) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      boolean isManger = actorUserId == managerUserId;
      boolean isSubmitted = reviewInvitation.getIsSubmitted() == 1;
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);
      boolean isTemplatePublic = reviewUtils.isTemplatePublic(reviewTemplate);

      boolean status = false;

      if (!isManger) {
        if (!isSubmitted && !isTemplatePublic) {
          status = true;
        } else if (isSubmitted && !isPeerReviewDeadline) {
          status = true;
        }
      } else {
        if (!isSubmitted) {
          status = true;
        } else if (isSubmitted && !isTemplatePublic) {
          status = true;
        }
      }
      if (!status) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      reviewCommentService.deleteReviewComment(orgId, commentId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }


  @LogAround
  @Override
  public ReviewProjectDetailDTO getInvitationProjectDetail(long orgId, long invitationId, long projectId,
                                                           long managerUserId,
                                                           long actorUserId, long adminUserId) {

    ReviewProjectDetailDTO result = new ReviewProjectDetailDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // # permission check
      ReviewInvitation reviewInvitation =
          reviewInvitationService.findReviewInvitation(orgId, invitationId);
      if (actorUserId != reviewInvitation.getReviewerId()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      long templateId = reviewInvitation.getTemplateId();
      long revieweeId = reviewInvitation.getRevieweeId();

      // 1. find project basic information
      ReviewProject reviewProject = reviewProjectService.findReviewProject(orgId, projectId);
      BeanUtils.copyProperties(reviewProject, result);

      // 2. When manager, add submitted comments
      boolean isManager = (actorUserId == managerUserId);
      if (isManager) {
        List<Long> reviewerIds = reviewUtils.getSubmittedReviewerIds(orgId, templateId, revieweeId);
        // exclude actorUserId
        reviewerIds.remove(actorUserId);

        List<ReviewCommentDTO> submittedReviewCommentDTOs = reviewUtils.getReviewCommentDTOs(orgId, templateId,
            ReviewItemType.PROJECT.getCode(), projectId, revieweeId, reviewerIds);
        result.setSubmittedComments(submittedReviewCommentDTOs);
      }

      // 3. add reviewer user comment
      List<Long> reviewerIds = new ArrayList<>();
      reviewerIds.add(actorUserId);
      List<ReviewCommentDTO> reviewerUserReviewCommentDTOs = reviewUtils.getReviewCommentDTOs(orgId, templateId,
          ReviewItemType.PROJECT.getCode(), projectId, revieweeId, reviewerIds);
      if (reviewerUserReviewCommentDTOs.size() > 1) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
      } else if (reviewerUserReviewCommentDTOs.size() == 1) {
        result.setReviewerComment(reviewerUserReviewCommentDTOs.get(0));
      }

      // 4. set editable and deletable status
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      int status = reviewTemplate.getState();
      boolean isTemplatePublic = reviewUtils.isTemplatePublic(reviewTemplate);
      boolean isPeerReviewDeadline = reviewUtils.isPeerReviewDeadline(reviewTemplate);
      boolean isSubmitted = reviewInvitation.getIsSubmitted() == 1;

      if (ReviewTemplateStatus.IN_PROGRESS.getCode() == status &&
          reviewInvitation.getIsCanceled() == 0) {

        if (!isManager) {
          if (!isSubmitted && !isTemplatePublic) {
            result.setIsEditable(1);
            result.setIsDeletable(1);
          } else if (isSubmitted && !isPeerReviewDeadline) {
            result.setIsEditable(1);
            result.setIsDeletable(1);
          }
        } else {
          if (!isSubmitted) {
            result.setIsEditable(1);
            result.setIsDeletable(1);
          } else if (isSubmitted && !isTemplatePublic) {
            result.setIsEditable(1);
            result.setIsDeletable(1);
          }
        }
      }
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

}
