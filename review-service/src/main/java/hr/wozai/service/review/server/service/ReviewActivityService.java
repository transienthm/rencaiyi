// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service;

import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.review.server.model.ReviewTemplate;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-18
 */
public interface ReviewActivityService {

  /**
   * Insert review activity
   * @param reviewActivity
   */
  public long insertReviewActivity(ReviewActivity reviewActivity);

  /**
   * Batch insert review activities
   * @param reviewActivities
   */
  public void batchInsertReviewActivities(List<ReviewActivity> reviewActivities);

  public void batchInsertReviewActivityAndManagerInvitation(
      List<ReviewActivity> reviewActivities, List<ReviewInvitation> reviewInvitations);

  /**
   * Find review activity
   * @param orgId
   * @param activityId
   */
  public ReviewActivity findReviewActivity(long orgId, long activityId);

  /**
   * Find review activity by template and revieweeId
   * @param orgId
   * @param templateId
   * @param revieweeId
   */
  public ReviewActivity findReviewActivityByRevieweeId(long orgId, long templateId, long revieweeId);

  /**
   * List unSubmitted review activity
   * @param orgId
   * @param revieweeId
   */
  public List<ReviewActivity> listUnSubmittedReviewActivity(long orgId, long revieweeId);

  /**
   * List other review activity
   * @param orgId
   * @param revieweeId
   * @param pageNumber
   * @param pageSize
   */
  public List<ReviewActivity> listOtherReviewActivity(long orgId, long revieweeId, int pageNumber, int pageSize);

  /**
   * Count other review activity
   * @param orgId
   * @param revieweeId
   */
  public long countOtherReviewActivity(long orgId, long revieweeId);

  /**
   * List uncanceled review activity of template
   * @param orgId
   * @param templateId
   */
  public List<ReviewActivity> listUnCanceledReviewActivityOfTemplate(long orgId, long templateId);

  /**
   * Update review activity
   * @param reviewActivity
   */
  public void updateReviewActivity(ReviewActivity reviewActivity);

  /**
   * Batch update review activities
   * @param reviewActivities
   */
  public void batchUpdateReviewActivities(List<ReviewActivity> reviewActivities);

  /**
   * Count review activity of template
   * @param orgId
   * @param templateId
   * @return
   */
  public long countReviewActivityOfTemplate(long orgId, long templateId);

  /**
   * List all revieweeId of template
   * @param orgId
   * @param templateId
   * @return
   */
  public List<Long> listAllRevieweeIdOfTemplate(long orgId, long templateId);

  /**
   * List submitted revieweeId of template
   * @param orgId
   * @param templateId
   * @return
   */
  public List<Long> listSubmittedRevieweeIdOfTemplate(long orgId, long templateId);

  /**
   * List all review activity of template
   * @param orgId
   * @param templateId
   * @return
   */
  public List<ReviewActivity> listAllReviewActivityOfTemplate(long orgId, long templateId);

  List<ReviewActivity> listAllReviewActivityOfRevieweeId(long orgId, long revieweeId);

  public List<Long> listAllValidReviewActivitiesByRevieweeAndTemplatesList(
          long orgId, List<Long> templates, long revieweeId);

}
