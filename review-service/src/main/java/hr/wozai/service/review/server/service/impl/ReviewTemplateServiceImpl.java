// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service.impl;

import hr.wozai.service.review.server.dao.ReviewInvitedTeamDao;
import hr.wozai.service.review.server.helper.ReviewTemplateHelper;
import hr.wozai.service.review.server.model.ReviewInvitedTeam;
import hr.wozai.service.review.server.service.ReviewTemplateService;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.server.dao.ReviewQuestionDao;
import hr.wozai.service.review.server.dao.ReviewTemplateDao;
import hr.wozai.service.review.server.model.ReviewQuestion;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-09
 */
@Service("reviewTemplateService")
public class ReviewTemplateServiceImpl implements ReviewTemplateService {

  @Autowired
  private ReviewTemplateDao reviewTemplateDao;

  @Autowired
  private ReviewQuestionDao reviewQuestionDao;

  @Autowired
  private ReviewInvitedTeamDao reviewInvitedTeamDao;

  /**
   * Insert review template
   * @param reviewTemplate
   */
  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long insertReviewTemplate(ReviewTemplate reviewTemplate) {

    ReviewTemplateHelper.checkReviewTemplateInsertParams(reviewTemplate);
    long result = reviewTemplateDao.insertReviewTemplate(reviewTemplate);

    long orgId = reviewTemplate.getOrgId();
    long lastModifiedUserId = reviewTemplate.getLastModifiedUserId();
    List<String> questions = reviewTemplate.getQuestions();
    insertQuestions(orgId, result, lastModifiedUserId, questions);

    return result;
  }

  @Override
  @LogAround
  public void batchInsertReviewInvitedTeam(List<ReviewInvitedTeam> reviewInvitedTeams) {
    if (!ReviewTemplateHelper.isValidBatchAddReviewInvitedTeamRequest(reviewInvitedTeams)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    reviewInvitedTeamDao.batchInsertReviewInvitedTeam(reviewInvitedTeams);
  }

  @Override
  @LogAround
  public List<ReviewInvitedTeam> listReviewInvitedTeam(long orgId, long reviewTemplateId) {
    return reviewInvitedTeamDao.listInvitedTeamIdByOrgIdAndReviewTemplateId(orgId, reviewTemplateId);
  }

  @LogAround
  private void insertQuestions(long orgId, long templateId, long lastModifiedUserId,
                               List<String> questions) {
    ReviewQuestion reviewQuestion = new ReviewQuestion();
    reviewQuestion.setOrgId(orgId);
    reviewQuestion.setTemplateId(templateId);
    reviewQuestion.setLastModifiedUserId(lastModifiedUserId);
    for(String name: questions) {
      reviewQuestion.setName(name);
      reviewQuestionDao.insertReviewQuestion(reviewQuestion);
    }
  }

  /**
   * Find review template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public ReviewTemplate findReviewTemplate(long orgId, long templateId) {
    ReviewTemplate reviewTemplate = reviewTemplateDao.findReviewTemplate(orgId, templateId);
    if(null == reviewTemplate) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_TEMPLATE_NOT_FOUND);
    }
    return reviewTemplate;
  }

  /**
   * List review template
   * @param orgId
   * @param statuses
   */
  @LogAround
  @Override
  public List<ReviewTemplate> listReviewTemplate(long orgId, int pageNumber, int pageSize, List<Integer> statuses) {

    List<ReviewTemplate> reviewTemplates = reviewTemplateDao.listReviewTemplate(orgId, pageNumber, pageSize, statuses);
    for(ReviewTemplate reviewTemplate: reviewTemplates) {
      long templateId = reviewTemplate.getTemplateId();
      List<ReviewQuestion> reviewQuestions = reviewQuestionDao.listReviewQuestion(orgId, templateId);
      List<String> questions = new ArrayList<>();
      for (ReviewQuestion reviewQuestion : reviewQuestions) {
        questions.add(reviewQuestion.getName());
      }
      reviewTemplate.setQuestions(questions);
    }

    return reviewTemplates;
  }

  /**
   * Count review template
   * @param orgId
   */
  @LogAround
  @Override
  public long countReviewTemplate(long orgId) {
    long result = reviewTemplateDao.countReviewTemplate(orgId);
    return result;
  }

  /**
   * List active review template
   */
  @LogAround
  @Override
  public List<ReviewTemplate> listActiveReviewTemplate() {
    List<ReviewTemplate> reviewTemplates = reviewTemplateDao.listActiveReviewTemplate();
    return reviewTemplates;
  }

  /**
   * Update review template
   * @param reviewTemplate
   */
  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateReviewTemplate(ReviewTemplate reviewTemplate) {
    ReviewTemplateHelper.checkReviewTemplateInsertParams(reviewTemplate);
    reviewTemplate.setState(ReviewTemplateStatus.DRAFT.getCode());
    reviewTemplateDao.updateReviewTemplate(reviewTemplate);

    long orgId = reviewTemplate.getOrgId();
    long templateId = reviewTemplate.getTemplateId();
    long lastModifiedUserId = reviewTemplate.getLastModifiedUserId();

    reviewQuestionDao.deleteReviewQuestionOfTemplate(orgId, templateId, lastModifiedUserId);

    List<String> questions = reviewTemplate.getQuestions();
    insertQuestions(orgId, templateId, lastModifiedUserId, questions);
  }


  /**
   * Publish review template
   * @param orgId
   * @param templateId
   * @param lastModifiedUserId
   */
  @LogAround
  @Override
  public void publishReviewTemplate(long orgId, long templateId, long lastModifiedUserId) {

    ReviewTemplate reviewTemplate = new ReviewTemplate();
    reviewTemplate.setOrgId(orgId);
    reviewTemplate.setTemplateId(templateId);
    reviewTemplate.setPublishedTime(TimeUtils.getNowTimestmapInMillis());
    reviewTemplate.setState(ReviewTemplateStatus.IN_PROGRESS.getCode());
    reviewTemplate.setLastModifiedUserId(lastModifiedUserId);

    reviewTemplateDao.updateReviewTemplate(reviewTemplate);
  }

  /**
   * Cancel review template
   * @param orgId
   * @param templateId
   * @param lastModifiedUserId
   */
  @LogAround
  @Override
  public void cancelReviewTemplate(long orgId, long templateId, long lastModifiedUserId) {

    ReviewTemplate reviewTemplate = new ReviewTemplate();
    reviewTemplate.setOrgId(orgId);
    reviewTemplate.setTemplateId(templateId);
    reviewTemplate.setState(ReviewTemplateStatus.CANCELED.getCode());
    reviewTemplate.setLastModifiedUserId(lastModifiedUserId);

    reviewTemplateDao.updateReviewTemplate(reviewTemplate);
  }

  /**
   * Finish review template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public void finishReviewTemplate(long orgId, long templateId) {

    ReviewTemplate reviewTemplate = new ReviewTemplate();
    reviewTemplate.setOrgId(orgId);
    reviewTemplate.setTemplateId(templateId);
    reviewTemplate.setState(ReviewTemplateStatus.FINISH.getCode());

    reviewTemplateDao.finishReviewTemplate(orgId, templateId);
  }

  /**
   * List review template by template ids
   * @param orgId
   * @param templateIds
   */
  @LogAround
  @Override
  public List<ReviewTemplate> listReviewTemplateByTemplateIds(long orgId, List<Long> templateIds) {
    if(null == templateIds || templateIds.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    List<ReviewTemplate> reviewTemplates = reviewTemplateDao.listReviewTemplateByTemplateIds(orgId, templateIds);
    return reviewTemplates;
  }

  /**
   * @param orgId
   * @return
   */
  @LogAround
  @Override
  public List<ReviewTemplate> listAllValidReviewTemplates(long orgId) {
    List<ReviewTemplate> reviewTemplates = reviewTemplateDao.listAllValidReviewTemplates(orgId);
    return reviewTemplates;
  }

  @LogAround
  @Override
  public long insertReviewTemplateOnly(ReviewTemplate reviewTemplate) {
    return reviewTemplateDao.insertReviewTemplate(reviewTemplate);
  }
}