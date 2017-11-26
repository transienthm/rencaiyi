// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service.impl;

import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.server.dao.ReviewInvitationDao;
import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.dao.ReviewActivityDao;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-18
 */
@Service("reviewActivityService")
public class ReviewActivityServiceImpl implements hr.wozai.service.review.server.service.ReviewActivityService {

  @Autowired
  private ReviewActivityDao reviewActivityDao;

  @Autowired
  private ReviewInvitationDao reviewInvitationDao;

  /**
   * Insert review activity
   * @param reviewActivity
   */
  @LogAround
  @Override
  public long insertReviewActivity(ReviewActivity reviewActivity) {
    long result = reviewActivityDao.insertReviewActivity(reviewActivity);

    return result;
  }

  /**
   * Batch insert review activity
   * @param reviewActivities
   */
  @LogAround
  @Override
  public void batchInsertReviewActivities(List<ReviewActivity> reviewActivities) {
    if(null == reviewActivities || reviewActivities.isEmpty()) {
      return;
    }
    reviewActivityDao.batchInsertReviewActivities(reviewActivities);
  }

  /**
   *
   * @param reviewActivities
   * @param reviewInvitations
   */
  @LogAround
  @Override
  public void batchInsertReviewActivityAndManagerInvitation(
      List<ReviewActivity> reviewActivities, List<ReviewInvitation> reviewInvitations) {

    batchInsertReviewActivities(reviewActivities);
    if(null == reviewInvitations
       || reviewInvitations.isEmpty()) {
      return;
    }
    reviewInvitationDao.batchInsertReviewInvitations(reviewInvitations);
  }

  /**
   * Find review activity
   * @param orgId
   * @param activityId
   */
  @LogAround
  @Override
  public ReviewActivity findReviewActivity(long orgId, long activityId) {
    ReviewActivity reviewActivity = reviewActivityDao.findReviewActivity(orgId, activityId);
    if(null == reviewActivity) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_ACTIVITY_NOT_FOUND);
    }
    return reviewActivity;
  }

  /**
   * Find review activity by template and revieweeId
   * @param orgId
   * @param templateId
   * @param revieweeId
   */
  @LogAround
  @Override
  public ReviewActivity findReviewActivityByRevieweeId(long orgId, long templateId, long revieweeId) {

    ReviewActivity reviewActivity = reviewActivityDao.findReviewActivityByRevieweeId(orgId, templateId, revieweeId);
    if(null == reviewActivity) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_ACTIVITY_NOT_FOUND);
    }
    return reviewActivity;
  }

  /**
   * List unSubmitted review activity
   * @param orgId
   * @param revieweeId
   */
  @LogAround
  @Override
  public List<ReviewActivity> listUnSubmittedReviewActivity(long orgId, long revieweeId) {
    List<ReviewActivity> reviewActivities = reviewActivityDao.listUnSubmittedReviewActivity(orgId, revieweeId);
    return reviewActivities;
  }

  /**
   * List other review activity
   * @param orgId
   * @param revieweeId
   * @param pageNumber
   * @param pageSize
   */
  @LogAround
  @Override
  public List<ReviewActivity> listOtherReviewActivity(long orgId, long revieweeId,
                                                      int pageNumber, int pageSize) {
    List<ReviewActivity> reviewActivities = reviewActivityDao.listOtherReviewActivity(orgId, revieweeId, pageNumber, pageSize);
    return reviewActivities;
  }

  /**
   * Count other review activity
   * @param orgId
   * @param revieweeId
   */
  @LogAround
  @Override
  public long countOtherReviewActivity(long orgId, long revieweeId) {

    long result = reviewActivityDao.countOtherReviewActivity(orgId, revieweeId);
    return result;
  }

  /**
   * List uncanceled review activity of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public List<ReviewActivity> listUnCanceledReviewActivityOfTemplate(long orgId, long templateId) {
    List<ReviewActivity> reviewActivities = reviewActivityDao.listUnCanceledReviewActivityOfTemplate(orgId, templateId);
    return reviewActivities;
  }

  //TODO: update only include submit and cancel status
  /**
   * Update review activity
   * @param reviewActivity
   */
  @LogAround
  @Override
  public void updateReviewActivity(ReviewActivity reviewActivity) {
    reviewActivityDao.updateReviewActivity(reviewActivity);
  }

  /**
   * Batch update review activities
   * @param reviewActivities
   */
  @LogAround
  @Override
  public void batchUpdateReviewActivities(List<ReviewActivity> reviewActivities) {
    if(null == reviewActivities || reviewActivities.isEmpty()) {
      return;
    }
    reviewActivityDao.batchUpdateReviewActivities(reviewActivities);
  }

  /**
   * Count review activity of template
   * @param orgId
   * @param templateId
   * @return
   */
  @LogAround
  @Override
  public long countReviewActivityOfTemplate(long orgId, long templateId) {
    long result = reviewActivityDao.countReviewActivityOfTemplate(orgId, templateId);
    return result;
  }

  /**
   * List all revieweeId of template
   * @param orgId
   * @param templateId
   * @return
   */
  @LogAround
  @Override
  public List<Long> listAllRevieweeIdOfTemplate(long orgId, long templateId) {
    List<Long> result = reviewActivityDao.listAllRevieweeIdOfTemplate(orgId, templateId);
    return result;
  }

  /**
   * List submitted revieweeId of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public List<Long> listSubmittedRevieweeIdOfTemplate(long orgId, long templateId) {
    List<Long> result = reviewActivityDao.listSubmittedRevieweeIdOfTemplate(orgId, templateId);
    return result;
  }

  /**
   * List all review activity of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public List<ReviewActivity> listAllReviewActivityOfTemplate(long orgId, long templateId) {
    List<ReviewActivity> reviewActivities = reviewActivityDao.listAllReviewActivityOfTemplate(orgId, templateId);
    return reviewActivities;
  }

  @Override
  @LogAround
  public List<ReviewActivity> listAllReviewActivityOfRevieweeId(long orgId, long revieweeId) {
    return reviewActivityDao.listAllReviewActivityOfRevieweeId(orgId, revieweeId);
  }

  /**
   *
   * @param orgId
   * @param revieweeId
   * @return
   */
  @LogAround
  @Override
  public List<Long> listAllValidReviewActivitiesByRevieweeAndTemplatesList(
          long orgId, List<Long> templates, long revieweeId) {
    return reviewActivityDao.listAllValidReviewActivitiesByRevieweeAndTemplatesList(orgId, templates, revieweeId);
  }
}
