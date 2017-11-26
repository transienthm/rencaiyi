// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service;

import hr.wozai.service.review.server.model.ReviewInvitedTeam;
import hr.wozai.service.review.server.model.ReviewTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-09
 */
public interface ReviewTemplateService {

  /**
   * Insert review template
   * @param reviewTemplate
   */
  long insertReviewTemplate(ReviewTemplate reviewTemplate);
  
  void batchInsertReviewInvitedTeam(List<ReviewInvitedTeam> reviewInvitedTeams);

  List<ReviewInvitedTeam> listReviewInvitedTeam(long orgId, long reviewTemplateId);

  /**
   * Find review template
   * @param orgId
   * @param templateId
   */
  ReviewTemplate findReviewTemplate(long orgId, long templateId);

  /**
   * List review template
   * @param orgId
   * @param statuses
   */
  List<ReviewTemplate> listReviewTemplate(long orgId, int pageNumber, int pageSize, List<Integer> statuses);

  /**
   * Count review template
   * @param orgId
   */
  long countReviewTemplate(long orgId);

  /**
   * List active review template
   */
  List<ReviewTemplate> listActiveReviewTemplate();

  /**
   * Update review template
   * @param reviewTemplate
   */
  void updateReviewTemplate(ReviewTemplate reviewTemplate);

  /**
   * Publish review template
   * @param orgId
   * @param templateId
   * @param lastModifiedUserId
   */
  void publishReviewTemplate(long orgId, long templateId, long lastModifiedUserId);

  /**
   * Cancel review template
   * @param orgId
   * @param templateId
   * @param lastModifiedUserId
   */
  void cancelReviewTemplate(long orgId, long templateId, long lastModifiedUserId);

  /**
   * Finish review template
   * @param orgId
   * @param templateId
   */
  void finishReviewTemplate(long orgId, long templateId);

  /**
   * List review template by template ids
   * @param orgId
   * @param templateIds
   */
  List<ReviewTemplate> listReviewTemplateByTemplateIds(long orgId, List<Long> templateIds);

  /**
   * @param orgId
   * @return
   */
  List<ReviewTemplate> listAllValidReviewTemplates(long orgId);

  long insertReviewTemplateOnly(ReviewTemplate reviewTemplate);
}
