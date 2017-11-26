// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.server.model.ReviewQuestion;
import hr.wozai.service.review.server.dao.ReviewQuestionDao;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-09
 */
@Service("reviewQuestionService")
public class ReviewQuestionServiceImpl implements hr.wozai.service.review.server.service.ReviewQuestionService {

  @Autowired
  private ReviewQuestionDao reviewQuestionDao;

  /**
   * Insert review question
   * @param reviewQuestion
   */
  @LogAround
  @Override
  public long insertReviewQuestion(ReviewQuestion reviewQuestion) {
    long result = reviewQuestionDao.insertReviewQuestion(reviewQuestion);
    return result;
  }

  /**
   * Find review question
   * @param orgId
   * @param questionId
   */
  @LogAround
  @Override
  public ReviewQuestion findReviewQuestion(long orgId, long questionId) {
    ReviewQuestion reviewQuestion = reviewQuestionDao.findReviewQuestion(orgId, questionId);
    if(null == reviewQuestion) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_QUESTION_NOT_FOUND);
    }
    return reviewQuestion;
  }

  /**
   * List review question of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public List<ReviewQuestion> listReviewQuestion(long orgId, long templateId) {
    List<ReviewQuestion> reviewQuestions = reviewQuestionDao.listReviewQuestion(orgId, templateId);
    return reviewQuestions;
  }

  /**
   * Count review question of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public long countReviewQuestionOfTemplate(long orgId, long templateId) {
    long result = reviewQuestionDao.countReviewQuestionOfTemplate(orgId, templateId);
    return result;
  }

  /**
   * Update review question
   * @param reviewQuestion
   */
  @LogAround
  @Override
  public void updateReviewQuestion(ReviewQuestion reviewQuestion) {
    reviewQuestionDao.updateReviewQuestion(reviewQuestion);
  }

  /**
   * Delete review question
   * @param orgId
   * @param questionId
   * @param lastModifiedUserId
   */
  @LogAround
  @Override
  public void deleteReviewQuestion(long orgId, long questionId, long lastModifiedUserId) {
    reviewQuestionDao.deleteReviewQuestion(orgId, questionId, lastModifiedUserId);
  }

  /**
   * Delete review question of template
   * @param orgId
   * @param templateId
   * @param lastModifiedUserId
   */
  @LogAround
  @Override
  public void deleteReviewQuestionOfTemplate(long orgId, long templateId, long lastModifiedUserId) {
    reviewQuestionDao.deleteReviewQuestionOfTemplate(orgId, templateId, lastModifiedUserId);
  }

}
