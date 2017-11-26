// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.thrift.facade;

import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.service.ReviewActivityService;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.dto.ReviewInvitationDTO;
import hr.wozai.service.review.client.dto.ReviewInvitationListDTO;
import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.client.facade.ReviewInvitationFacade;
import hr.wozai.service.review.server.helper.FacadeExceptionHelper;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.service.ReviewInvitationService;
import hr.wozai.service.review.server.service.ReviewTemplateService;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-21
 */
@Service("reviewInvitationFacadeImpl")
public class ReviewInvitationFacadeImpl implements ReviewInvitationFacade {

  private Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationFacadeImpl.class);

  @Autowired
  private ReviewInvitationService reviewInvitationService;

  @Autowired
  private ReviewTemplateService reviewTemplateService;

  @Autowired
  private ReviewActivityService reviewActivityService;

  /**
   * Find review invitation
   * @param orgId
   * @param invitationId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewInvitationDTO findReviewInvitation(long orgId, long invitationId, long actorUserId, long adminUserId) {

    ReviewInvitationDTO result = new ReviewInvitationDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ReviewInvitation remoteResult = reviewInvitationService.findReviewInvitation(orgId, invitationId);
      BeanHelper.copyPropertiesHandlingJSON(remoteResult, result);
    } catch (Exception e) {
      LOGGER.error(e.toString());
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * List unsubmitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewInvitationListDTO listUnSubmittedReviewInvitation(long orgId, long reviewerId, long actorUserId, long adminUserId) {

    ReviewInvitationListDTO result = new ReviewInvitationListDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ReviewInvitation> reviewInvitations =
          reviewInvitationService.listUnSubmittedReviewInvitation(orgId, reviewerId);

      List<ReviewInvitationDTO> reviewInvitationDTOs = new ArrayList<>();
      for(ReviewInvitation reviewInvitation: reviewInvitations) {
        ReviewInvitationDTO reviewInvitationDTO = new ReviewInvitationDTO();
        BeanHelper.copyPropertiesHandlingJSON(reviewInvitation, reviewInvitationDTO);

        ReviewActivity reviewActivity = reviewActivityService.findReviewActivityByRevieweeId(
                orgId, reviewInvitation.getTemplateId(),
                reviewInvitation.getRevieweeId());
        if (reviewActivity.getIsSubmitted() == 0) {
          reviewInvitationDTO.setIsInActive(1);
        } else {
          reviewInvitationDTO.setIsInActive(0);
        }
        reviewInvitationDTOs.add(reviewInvitationDTO);
      }
      result.setReviewInvitationDTOs(reviewInvitationDTOs);
    } catch (Exception e) {
      LOGGER.error(e.toString());
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * List submitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewInvitationListDTO listSubmittedReviewInvitation(long orgId, long reviewerId,
                                                               int pageNumber, int pageSize,
                                                               long actorUserId, long adminUserId) {

    ReviewInvitationListDTO result = new ReviewInvitationListDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ReviewInvitation> reviewInvitations =
          reviewInvitationService.listSubmittedReviewInvitation(orgId, reviewerId, pageNumber, pageSize);

      List<ReviewInvitationDTO> reviewInvitationDTOs = new ArrayList<>();
      for(ReviewInvitation reviewInvitation: reviewInvitations) {
        ReviewInvitationDTO reviewInvitationDTO = new ReviewInvitationDTO();
        BeanHelper.copyPropertiesHandlingJSON(reviewInvitation, reviewInvitationDTO);
        reviewInvitationDTOs.add(reviewInvitationDTO);
      }
      result.setReviewInvitationDTOs(reviewInvitationDTOs);
    } catch (Exception e) {
      LOGGER.error(e.toString());
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * Count submitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public LongDTO countSubmittedReviewInvitation(long orgId, long reviewerId,
                                                long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long amount = reviewInvitationService.countSubmittedReviewInvitation(orgId, reviewerId);
      result.setData(amount);
    } catch (Exception e) {
      LOGGER.error(e.toString());
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * List canceled review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewInvitationListDTO listCanceledReviewInvitation(long orgId, long reviewerId,
                                                              int pageNumber, int pageSize,
                                                              long actorUserId, long adminUserId) {

    ReviewInvitationListDTO result = new ReviewInvitationListDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ReviewInvitation> reviewInvitations =
          reviewInvitationService.listCanceledReviewInvitation(orgId, reviewerId, pageNumber, pageSize);

      List<ReviewInvitationDTO> reviewInvitationDTOs = new ArrayList<>();
      for(ReviewInvitation reviewInvitation: reviewInvitations) {
        ReviewInvitationDTO reviewInvitationDTO = new ReviewInvitationDTO();
        BeanHelper.copyPropertiesHandlingJSON(reviewInvitation, reviewInvitationDTO);
        reviewInvitationDTOs.add(reviewInvitationDTO);
      }
      result.setReviewInvitationDTOs(reviewInvitationDTOs);
    } catch (Exception e) {
      LOGGER.error(e.toString());
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * Count canceled review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public LongDTO countCanceledReviewInvitation(long orgId, long reviewerId,
                                               long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long amount = reviewInvitationService.countCanceledReviewInvitation(orgId, reviewerId);
      result.setData(amount);
    } catch (Exception e) {
      LOGGER.error(e.toString());
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * Refuse review invitation
   * @param orgId
   * @param invitationId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public VoidDTO refuseReviewInvitation(long orgId, long invitationId,
                                        long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ReviewInvitation reviewInvitation = reviewInvitationService.findReviewInvitation(orgId, invitationId);
      long templateId = reviewInvitation.getTemplateId();

      if(1 == reviewInvitation.getIsManager()) {
        LOGGER.error("Manager cannot cancel invitation");
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // template status check
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      if (ReviewTemplateStatus.IN_PROGRESS.getCode() != reviewTemplate.getState()) {
        LOGGER.error("Review template not in progress");
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      boolean isSubmitted = reviewInvitation.getIsSubmitted() == 1;
      if (isSubmitted) {
        LOGGER.error("Review invitation already submitted");
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      // refuse invitation
      // comment delete is done in service as a transaction
      reviewInvitation.setIsCanceled(1);
      reviewInvitation.setLastModifiedUserId(actorUserId);
      reviewInvitationService.refuseReviewInvitation(reviewInvitation);
    } catch (Exception e) {
      LOGGER.error(e.toString());
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public ReviewInvitationListDTO listAllReviewInvitationByTemplateIdAndRevieweeId(
      long orgId, long templateId, long revieweeId, long actorUserId, long adminUserId) {

    ReviewInvitationListDTO result = new ReviewInvitationListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ReviewInvitation> reviewInvitations =
          reviewInvitationService.listAllReviewInvitationByTemplateIdAndRevieweeId(orgId, templateId, revieweeId);
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      List<ReviewInvitationDTO> reviewInvitationDTOs = new ArrayList<>();
      for(ReviewInvitation reviewInvitation: reviewInvitations) {
        ReviewInvitationDTO reviewInvitationDTO = new ReviewInvitationDTO();
        BeanHelper.copyPropertiesHandlingJSON(reviewInvitation, reviewInvitationDTO);
        // set isInActive
        // TODO: list, not one-by-one
        ReviewActivity reviewActivity = reviewActivityService
            .findReviewActivityByRevieweeId(orgId, reviewInvitation.getTemplateId(), reviewInvitation.getRevieweeId());
        reviewInvitationDTO.setIsInActive(reviewActivity.getIsSubmitted());
        reviewInvitationDTO.setSelfReviewDeadline(reviewTemplate.getSelfReviewDeadline());
        reviewInvitationDTO.setPeerReviewDeadline(reviewTemplate.getPeerReviewDeadline());
        reviewInvitationDTO.setPublicDeadline(reviewTemplate.getPublicDeadline());
        reviewInvitationDTOs.add(reviewInvitationDTO);
      }
      result.setReviewInvitationDTOs(reviewInvitationDTOs);
    } catch (Exception e) {
      LOGGER.error("listAllReviewInvitationByTemplateIdAndRevieweeId(): error", e.toString());
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public ReviewInvitationListDTO listAllReviewInvitationsByTemplatesAndReviewer(
          long orgId, List<Long> templatesList, long reviewerId, long actorUserId, long adminUserId) {

    ReviewInvitationListDTO result = new ReviewInvitationListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ReviewInvitation> reviewInvitations =
              reviewInvitationService.listAllReviewInvitationsByTemplatesAndReviewer(orgId, templatesList, reviewerId);
      List<ReviewInvitationDTO> reviewInvitationDTOs = new ArrayList<>();
      for(ReviewInvitation reviewInvitation: reviewInvitations) {
        ReviewInvitationDTO reviewInvitationDTO = new ReviewInvitationDTO();
        BeanHelper.copyPropertiesHandlingJSON(reviewInvitation, reviewInvitationDTO);
        reviewInvitationDTOs.add(reviewInvitationDTO);
      }
      result.setReviewInvitationDTOs(reviewInvitationDTOs);
    } catch (Exception e) {
      LOGGER.error("listAllReviewInvitationsByTemplatesAndReviewer(): error", e.toString());
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public ReviewInvitationListDTO listAllReviewInvitationByTemplateIdAndReviewerIdAndIsManager(
      long orgId, long templateId, long reviewerId, int isManager, long actorUserId, long adminUserId) {

    ReviewInvitationListDTO result = new ReviewInvitationListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ReviewInvitation> reviewInvitations = reviewInvitationService
          .listAllReviewInvitationByTemplateIdAndReviewerIdAndIsManager(orgId, templateId, reviewerId, isManager);
      List<ReviewInvitationDTO> reviewInvitationDTOs = new ArrayList<>();
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
      for(ReviewInvitation reviewInvitation: reviewInvitations) {
        ReviewInvitationDTO reviewInvitationDTO = new ReviewInvitationDTO();
        BeanHelper.copyPropertiesHandlingJSON(reviewInvitation, reviewInvitationDTO);
        // set isInActive
        // TODO: list, not one-by-one
        ReviewActivity reviewActivity = reviewActivityService
            .findReviewActivityByRevieweeId(orgId, reviewInvitation.getTemplateId(), reviewInvitation.getRevieweeId());
        reviewInvitationDTO.setIsInActive(reviewActivity.getIsSubmitted());
        reviewInvitationDTO.setSelfReviewDeadline(reviewTemplate.getSelfReviewDeadline());
        reviewInvitationDTO.setPeerReviewDeadline(reviewTemplate.getPeerReviewDeadline());
        reviewInvitationDTO.setPublicDeadline(reviewTemplate.getPublicDeadline());
        reviewInvitationDTOs.add(reviewInvitationDTO);
      }
      result.setReviewInvitationDTOs(reviewInvitationDTOs);
    } catch (Exception e) {
      LOGGER.error("listAllReviewInvitationByTemplateIdAndReviewerIdAndIsManager(): error", e.toString());
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

}
