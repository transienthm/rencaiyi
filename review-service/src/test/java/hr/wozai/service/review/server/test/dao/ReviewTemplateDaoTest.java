// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.test.dao;

import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.dao.ReviewTemplateDao;
import hr.wozai.service.review.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class ReviewTemplateDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewTemplateDaoTest.class);

  @Autowired
  ReviewTemplateDao reviewTemplateDao;

  List<ReviewTemplate> templates;

  ReviewTemplate reviewTemplate;

  long orgId = 100L;
  long userId = 11L;

  int pageNumber = 1;
  int pageSize = 10;

  String templateName = "2016First";

  //1. draft 2. in progress 3. finished 4. canceled
  int state = 1;

  @Before
  public void setup() {

    reviewTemplate = new ReviewTemplate();

    reviewTemplate.setOrgId(orgId);
    reviewTemplate.setTemplateName(templateName);
    reviewTemplate.setStartTime(System.currentTimeMillis()-2000);
    reviewTemplate.setEndTime(System.currentTimeMillis()-1000);
    reviewTemplate.setSelfReviewDeadline(System.currentTimeMillis()+100);
    reviewTemplate.setPeerReviewDeadline(System.currentTimeMillis()+500);
    reviewTemplate.setPublicDeadline(System.currentTimeMillis()+1000);
    reviewTemplate.setState(state);
    reviewTemplate.setLastModifiedUserId(userId);
    reviewTemplate.setIsReviewerAnonymous(0);

  }

  @Test
  public void testInsertReviewTemplate() throws Exception {

    long templateId = reviewTemplateDao.insertReviewTemplate(reviewTemplate);
    Assert.assertEquals(templateId, reviewTemplate.getTemplateId().longValue());
    LOGGER.info("templateId=" + templateId);
  }

  @Test
  public void testFindReviewTemplate() throws Exception {

    long templateId = reviewTemplateDao.insertReviewTemplate(reviewTemplate);
    Assert.assertEquals(templateId, reviewTemplate.getTemplateId().longValue());

    ReviewTemplate insertedReviewTemplate = reviewTemplateDao.findReviewTemplate(orgId, reviewTemplate.getTemplateId());
    Assert.assertEquals(insertedReviewTemplate.getTemplateName(), templateName);
  }

  @Test
  public void testListReviewTemplate() throws Exception {

    for(long i=1; i<5; i++) {
      reviewTemplate.setTemplateName("templateName" + i);
      reviewTemplateDao.insertReviewTemplate(reviewTemplate);
    }

    List<Integer> statuses = new ArrayList<>();
    for (ReviewTemplateStatus reviewTemplateStatus: ReviewTemplateStatus.values()) {
      statuses.add(reviewTemplateStatus.getCode());
    }
    templates = reviewTemplateDao.listReviewTemplate(orgId,  1, 20, statuses);
    Assert.assertEquals(templates.size(), 4);
    LOGGER.info("template number " + templates.size());
  }

  @Test
  public void testListActiveReviewTemplate() throws Exception {

    for(long i=1; i<5; i++) {
      reviewTemplate.setTemplateName("templateName" + i);
      reviewTemplateDao.insertReviewTemplate(reviewTemplate);
    }

    templates = reviewTemplateDao.listActiveReviewTemplate();
    Assert.assertNotNull(templates.size());

    List<Integer> statuses = new ArrayList<>();
    for (ReviewTemplateStatus reviewTemplateStatus: ReviewTemplateStatus.values()) {
      statuses.add(reviewTemplateStatus.getCode());
    }
    templates = reviewTemplateDao.listReviewTemplate(orgId, 1, 20, statuses);
    reviewTemplate = templates.get(0);
    reviewTemplate.setState(2);
    reviewTemplate.setLastModifiedUserId(userId);

    reviewTemplateDao.updateReviewTemplate(reviewTemplate);

    templates = reviewTemplateDao.listActiveReviewTemplate();
    Assert.assertNotNull(templates.size());

  }

  @Test
  public void testUpdateReviewTemplate() throws Exception {

    long templateId = reviewTemplateDao.insertReviewTemplate(reviewTemplate);
    Assert.assertEquals(templateId, reviewTemplate.getTemplateId().longValue());

    ReviewTemplate reviewTemplateResult;
    reviewTemplateResult = reviewTemplateDao.findReviewTemplate(orgId, templateId);

    long publishedTime = 10000;
    reviewTemplateResult.setPublishedTime(publishedTime);
    reviewTemplateResult.setLastModifiedUserId(userId + 1);

    reviewTemplateDao.updateReviewTemplate(reviewTemplateResult);

    reviewTemplateResult = reviewTemplateDao.findReviewTemplate(orgId, templateId);
    Assert.assertEquals(publishedTime, reviewTemplateResult.getPublishedTime().longValue());
  }

  @Test
  public void testListReviewTemplateByTemplateIds() throws Exception {
    List<Long> templateIds = new ArrayList<>();
    for(long i=1; i<5; i++) {
      reviewTemplate.setTemplateName("templateName" + i);
      long templateId = reviewTemplateDao.insertReviewTemplate(reviewTemplate);
      templateIds.add(templateId);
    }

    templates = reviewTemplateDao.listReviewTemplateByTemplateIds(orgId, templateIds);
    Assert.assertEquals(templates.size(), 4);
    LOGGER.info("template number " + templates.size());

  }

  @Test
  public void testFinishReviewTemplate() throws Exception {
    reviewTemplateDao.insertReviewTemplate(reviewTemplate);
    reviewTemplateDao.finishReviewTemplate(
            reviewTemplate.getOrgId(),
            reviewTemplate.getTemplateId()
    );
  }

  @Test
  public void testListAllValidReviewTemplates() throws Exception {
    reviewTemplateDao.insertReviewTemplate(reviewTemplate);
    List<ReviewTemplate> result = reviewTemplateDao.listAllValidReviewTemplates(
            reviewTemplate.getOrgId()
    );
    Assert.assertEquals(result.size(), 1);
  }
}
