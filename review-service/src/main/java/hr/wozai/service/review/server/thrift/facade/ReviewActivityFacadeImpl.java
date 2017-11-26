// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.thrift.facade;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import hr.wozai.service.review.client.dto.ReviewActivityDTO;
import hr.wozai.service.review.client.dto.ReviewActivityListDTO;
import hr.wozai.service.review.client.facade.ReviewActivityFacade;
import hr.wozai.service.review.server.helper.FacadeExceptionHelper;
import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.service.ReviewActivityService;
import hr.wozai.service.review.server.service.ReviewTemplateService;
import hr.wozai.service.review.server.utils.ReviewEmailUtils;
import hr.wozai.service.review.server.utils.ReviewMessageUtils;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.dto.ReportLineDTO;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-19
 */
@Service("reviewActivityFacadeImpl")
public class ReviewActivityFacadeImpl implements ReviewActivityFacade {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewActivityFacadeImpl.class);

  @Autowired
  private ReviewActivityService reviewActivityService;

  @Autowired
  private ReviewTemplateService reviewTemplateService;

  @Autowired
  private ReviewEmailUtils reviewEmailUtils;

  @Autowired
  private ReviewMessageUtils reviewMessageUtils;

  /**
   * Steps:
   *  1) add review activities for staff having reporter, add invitations to reporters of staff
   *  2) send email & msg to each reviewee
   *
   * @param orgId
   * @param templateId
   * @param reportLineDTOs
   * @param actorUserId
   * @param adminUserId
   */
  public VoidDTO batchInsertReviewActivities(
      long orgId, long templateId, List<ReportLineDTO> reportLineDTOs, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      List<ReviewActivity> reviewActivities = new ArrayList<>();
      List<ReviewInvitation> reviewInvitations = new ArrayList<>();
      for(ReportLineDTO reportLineDTO: reportLineDTOs) {

        long revieweeId = reportLineDTO.getUserId();
        ReviewActivity reviewActivity = new ReviewActivity();
        reviewActivity.setOrgId(orgId);
        reviewActivity.setTemplateId(templateId);
        reviewActivity.setRevieweeId(revieweeId);
        reviewActivity.setLastModifiedUserId(actorUserId);
        reviewActivities.add(reviewActivity);

        ReviewInvitation reviewInvitation = new ReviewInvitation();
        reviewInvitation.setOrgId(orgId);
        reviewInvitation.setTemplateId(templateId);
        reviewInvitation.setRevieweeId(revieweeId);
        reviewInvitation.setReviewerId(reportLineDTO.getReportUserId());
        reviewInvitation.setIsManager(1);
        reviewInvitation.setLastModifiedUserId(actorUserId);
        reviewInvitations.add(reviewInvitation);
      }

      // 1)
      reviewActivityService.batchInsertReviewActivityAndManagerInvitation(reviewActivities, reviewInvitations);

      // 2)
      for (ReportLineDTO reportLineDTO : reportLineDTOs) {
        long revieweeId = reportLineDTO.getUserId();
        ReviewActivity reviewActivity =
            reviewActivityService.findReviewActivityByRevieweeId(orgId, templateId, revieweeId);
        long activityId = reviewActivity.getActivityId();
        reviewEmailUtils.sendTemplateBeginEmail(orgId, templateId, activityId, revieweeId, actorUserId, adminUserId);
        reviewMessageUtils.sendTemplateBeginMessage(orgId, reviewActivity, revieweeId);
      }

    } catch (Exception e) {
      LOGGER.error("batchInsertReviewActivities(): error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * Find review activity
   * @param orgId
   * @param activityId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewActivityDTO findReviewActivity(long orgId, long activityId, long actorUserId, long adminUserId) {

    ReviewActivityDTO result = new ReviewActivityDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ReviewActivity remoteResult = reviewActivityService.findReviewActivity(orgId, activityId);
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, remoteResult.getTemplateId());
      BeanHelper.copyPropertiesHandlingJSON(remoteResult, result);
      result.setSelfReviewDeadline(reviewTemplate.getSelfReviewDeadline());
      result.setPeerReviewDeadline(reviewTemplate.getPeerReviewDeadline());
      result.setPublicDeadline(reviewTemplate.getPublicDeadline());
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public ReviewActivityDTO findReviewActivityByTemplateIdAndUserId(
      long orgId, long templateId, long userId, long actorUserId, long adminUserId) {

    ReviewActivityDTO result = new ReviewActivityDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ReviewActivity reviewActivity = reviewActivityService.findReviewActivityByRevieweeId(orgId, templateId, userId);
      ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, reviewActivity.getTemplateId());
      BeanHelper.copyPropertiesHandlingJSON(reviewActivity, result);
      result.setSelfReviewDeadline(reviewTemplate.getSelfReviewDeadline());
      result.setPeerReviewDeadline(reviewTemplate.getPeerReviewDeadline());
      result.setPublicDeadline(reviewTemplate.getPublicDeadline());
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * List unSubmitted review activity as reviewee
   * @param orgId
   * @param revieweeId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewActivityListDTO listUnSubmittedReviewActivity(long orgId, long revieweeId, long actorUserId, long adminUserId) {

    ReviewActivityListDTO result = new ReviewActivityListDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ReviewActivity> reviewActivities = reviewActivityService.listUnSubmittedReviewActivity(orgId, revieweeId);

      List<ReviewActivityDTO> reviewActivityDTOs = new ArrayList<>();
      for(ReviewActivity reviewActivity: reviewActivities) {
        ReviewActivityDTO reviewActivityDTO = new ReviewActivityDTO();
        BeanHelper.copyPropertiesHandlingJSON(reviewActivity, reviewActivityDTO);
        reviewActivityDTOs.add(reviewActivityDTO);
      }
      result.setReviewActivityDTOs(reviewActivityDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }


  /**
   * List other review activity as reviewee
   * @param orgId
   * @param revieweeId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public ReviewActivityListDTO listOtherReviewActivity(long orgId, long revieweeId,
                                                       int pageNumber, int pageSize,
                                                       long actorUserId, long adminUserId) {

    ReviewActivityListDTO result = new ReviewActivityListDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ReviewActivity> reviewActivities = reviewActivityService.listOtherReviewActivity(orgId, revieweeId, pageNumber, pageSize);

      List<ReviewActivityDTO> reviewActivityDTOs = new ArrayList<>();
      for(ReviewActivity reviewActivity: reviewActivities) {
        ReviewActivityDTO reviewActivityDTO = new ReviewActivityDTO();
        BeanHelper.copyPropertiesHandlingJSON(reviewActivity, reviewActivityDTO);
        reviewActivityDTOs.add(reviewActivityDTO);
      }
      result.setReviewActivityDTOs(reviewActivityDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  /**
   * Count other review activity
   * @param orgId
   * @param revieweeId
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  @Override
  public LongDTO countOtherReviewActivity(long orgId, long revieweeId, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();

    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long remoteResult = reviewActivityService.countOtherReviewActivity(orgId, revieweeId);
      result.setData(remoteResult);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }
}
