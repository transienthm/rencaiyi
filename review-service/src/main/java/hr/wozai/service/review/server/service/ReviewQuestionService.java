// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service;

import hr.wozai.service.review.server.model.ReviewQuestion;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-08
 */
public interface ReviewQuestionService {

  /**
   * Insert review question
   * @param reviewQuestion
   */
  public long insertReviewQuestion(ReviewQuestion reviewQuestion);

  /**
   * Find review question
   * @param orgId
   * @param questionId
   */
  public ReviewQuestion findReviewQuestion(long orgId, long questionId);

  /**
   * List review question of template
   * @param orgId
   * @param templateId
   */
  public List<ReviewQuestion> listReviewQuestion(long orgId, long templateId);

  /**
   * Count review question of template
   * @param orgId
   * @param templateId
   */
  public long countReviewQuestionOfTemplate(long orgId, long templateId);

  /**
   * Update review question
   * @param reviewQuestion
   */
  public void updateReviewQuestion(ReviewQuestion reviewQuestion);

  /**
   * Delete review question
   * @param orgId
   * @param questionId
   * @param lastModifiedUserId
   */
  public void deleteReviewQuestion(long orgId, long questionId, long lastModifiedUserId);

  /**
   * Delete review question of template
   * @param orgId
   * @param templateId
   * @param lastModifiedUserId
   */
  public void deleteReviewQuestionOfTemplate(long orgId, long templateId, long lastModifiedUserId);

}
