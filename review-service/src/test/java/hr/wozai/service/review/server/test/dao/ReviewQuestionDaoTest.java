// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.test.dao;

import hr.wozai.service.review.server.model.ReviewQuestion;
import hr.wozai.service.review.server.dao.ReviewQuestionDao;
import hr.wozai.service.review.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class ReviewQuestionDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewQuestionDaoTest.class);

  @Autowired
  ReviewQuestionDao reviewQuestionDao;

  List<ReviewQuestion> questions;

  ReviewQuestion reviewQuestion;

  long orgId = 100L;
  long templateId = 1L;
  long userId = 11;

  String name = "Your advantange";
  String newName = "Your disadvantange";

  @Before
  public void setup() {

    reviewQuestion = new ReviewQuestion();
    reviewQuestion.setOrgId(orgId);
    reviewQuestion.setTemplateId(templateId);
    reviewQuestion.setName(name);
    reviewQuestion.setLastModifiedUserId(userId);

  }

  @Test
  public void testInsertReviewQuestion() throws Exception {

    long questionId = reviewQuestionDao.insertReviewQuestion(reviewQuestion);
    Assert.assertEquals(questionId, reviewQuestion.getQuestionId().longValue());
  }

  @Test
  public void testFindReviewQuestion() throws Exception {
    long questionId = reviewQuestionDao.insertReviewQuestion(reviewQuestion);

    ReviewQuestion insertedReviewQuestion = reviewQuestionDao.findReviewQuestion(orgId, questionId);
    Assert.assertEquals(insertedReviewQuestion.getName(), name);
  }

  @Test
  public void testListReviewQuestion() throws Exception {

    for(long i=1; i<5; i++) {
      reviewQuestion.setName("question " + i);
      reviewQuestionDao.insertReviewQuestion(reviewQuestion);
    }

    questions = reviewQuestionDao.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(questions.size(), 4);
    LOGGER.info("question number " + questions.size());
  }

  @Test
  public void testUpdateReviewQuestion() throws Exception {

    long questionId = reviewQuestionDao.insertReviewQuestion(reviewQuestion);

    ReviewQuestion reviewQuestionResult;
    reviewQuestionResult = reviewQuestionDao.findReviewQuestion(orgId, questionId);

    String nameNew = "disadvantage";
    reviewQuestionResult.setName(nameNew);
    reviewQuestionResult.setLastModifiedUserId(userId);

    long result = reviewQuestionDao.updateReviewQuestion(reviewQuestionResult);
    Assert.assertEquals(result, 1);

    reviewQuestionResult = reviewQuestionDao.findReviewQuestion(orgId, questionId);
    Assert.assertEquals(reviewQuestionResult.getName(), nameNew);
    Assert.assertEquals(reviewQuestionResult.getLastModifiedUserId().longValue(), userId);
  }

  @Test
  public void testDeleteReviewQuestion() throws Exception {

    long questionId = reviewQuestionDao.insertReviewQuestion(reviewQuestion);

    long result = reviewQuestionDao.deleteReviewQuestion(orgId, questionId, userId);
    Assert.assertEquals(result, 1);

    questions = reviewQuestionDao.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(questions.size(), 0);
    LOGGER.info("question number " + questions.size());

  }

  @Test
  public void testDeleteReviewQuestionOfTemplate() throws Exception {

    reviewQuestionDao.insertReviewQuestion(reviewQuestion);

    reviewQuestion.setName(newName);
    reviewQuestionDao.insertReviewQuestion(reviewQuestion);

    questions = reviewQuestionDao.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(questions.size(), 2);

    reviewQuestionDao.deleteReviewQuestionOfTemplate(orgId, templateId, userId);
    questions = reviewQuestionDao.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(questions.size(), 0);

  }

  @Test
  public void testCountReviewQuestionOfTemplate() throws Exception {

    for(long i=1; i<5; i++) {
      reviewQuestion.setName("question " + i);
      reviewQuestionDao.insertReviewQuestion(reviewQuestion);
    }

    long result = reviewQuestionDao.countReviewQuestionOfTemplate(orgId, templateId);
    Assert.assertEquals(result, 4);
  }
}
