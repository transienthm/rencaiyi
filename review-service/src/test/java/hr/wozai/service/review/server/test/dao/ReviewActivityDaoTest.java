// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.test.dao;

import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.dao.ReviewActivityDao;
import hr.wozai.service.review.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class ReviewActivityDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewActivityDaoTest.class);

  @Autowired
  ReviewActivityDao reviewActivityDao;

  ReviewActivity reviewActivity;

  long orgId = 100L;

  long templateId = 1L;
  long revieweeId = 11;
  int isSubmitted = 0;
  int isReaded = 0;
  int score = 0;

  int pageNumber = 1;
  int pageSize = 10;

  @Before
  public void setup() {

    reviewActivity = new ReviewActivity();
    reviewActivity.setOrgId(orgId);
    reviewActivity.setTemplateId(templateId);
    reviewActivity.setRevieweeId(revieweeId);
    reviewActivity.setIsReaded(isReaded);
    reviewActivity.setIsSubmitted(isSubmitted);
    reviewActivity.setLastModifiedUserId(revieweeId);
  }

  @Test
  public void testInsertReviewActivity() throws Exception {
    Long activityId = reviewActivityDao.insertReviewActivity(reviewActivity);
    Assert.assertEquals(activityId, reviewActivity.getActivityId());
  }

  private void batchInsertReviewActivities() {

    List<ReviewActivity> reviewActivities = new ArrayList<>();
    for(long i=0; i<5; i++) {
      ReviewActivity reviewActivityTemp = new ReviewActivity();
      BeanUtils.copyProperties(reviewActivity, reviewActivityTemp);
      reviewActivityTemp.setRevieweeId(revieweeId + i);
      reviewActivityTemp.setIsSubmitted(null);
      reviewActivities.add(reviewActivityTemp);
    }
    reviewActivityDao.batchInsertReviewActivities(reviewActivities);
  }

  @Test
  public void testBatchInsertReviewActivities() throws Exception {

    batchInsertReviewActivities();

    List<ReviewActivity> activities;
    activities = reviewActivityDao.listUnCanceledReviewActivityOfTemplate(orgId, templateId);
    Assert.assertEquals(activities.size(), 5);
    LOGGER.info("activity number " + activities.size());
  }

  @Test
  public void testFindReviewActivity() throws Exception {
    Long activityId = reviewActivityDao.insertReviewActivity(reviewActivity);
    Assert.assertEquals(activityId, reviewActivity.getActivityId());

    ReviewActivity reviewActivity = reviewActivityDao.findReviewActivity(orgId, activityId);
    Assert.assertEquals(reviewActivity.getActivityId(), activityId);
  }

  @Test
  public void testFindReviewActivityByRevieweeId() throws Exception {
    Long activityId = reviewActivityDao.insertReviewActivity(reviewActivity);
    Assert.assertEquals(activityId, reviewActivity.getActivityId());

    ReviewActivity reviewActivity = reviewActivityDao.findReviewActivityByRevieweeId(orgId, templateId, revieweeId);
    Assert.assertEquals(reviewActivity.getActivityId(), activityId);
  }

  @Test
  public void testListUnsubmittedReviewActivity() throws Exception {

    reviewActivityDao.insertReviewActivity(reviewActivity);

    reviewActivity.setTemplateId(templateId + 1);
    reviewActivityDao.insertReviewActivity(reviewActivity);

    reviewActivity.setTemplateId(templateId + 2);
    reviewActivityDao.insertReviewActivity(reviewActivity);

    List<ReviewActivity> activities = reviewActivityDao.listUnSubmittedReviewActivity(0, 0);
    Assert.assertEquals(activities.size(), 0);
    activities = reviewActivityDao.listUnSubmittedReviewActivity(orgId, revieweeId);
    Assert.assertEquals(activities.size(), 3);
    LOGGER.info("activity number " + activities.size());

    ReviewActivity reviewActivity;

    // submitted
    reviewActivity = activities.get(0);
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId);

    reviewActivityDao.updateReviewActivity(reviewActivity);

    activities = reviewActivityDao.listUnSubmittedReviewActivity(orgId, revieweeId);
    Assert.assertEquals(activities.size(), 2);

    // canceled
    reviewActivity = activities.get(1);
    reviewActivity.setIsCanceled(1);
    reviewActivity.setLastModifiedUserId(revieweeId);

    reviewActivityDao.updateReviewActivity(reviewActivity);

    activities = reviewActivityDao.listUnSubmittedReviewActivity(orgId, revieweeId);
    Assert.assertEquals(activities.size(), 1);
  }

  @Test
  public void testListOtherReviewActivity() throws Exception {

    reviewActivityDao.insertReviewActivity(reviewActivity);

    reviewActivity.setTemplateId(templateId + 1);
    reviewActivityDao.insertReviewActivity(reviewActivity);

    reviewActivity.setTemplateId(templateId + 2);
    reviewActivityDao.insertReviewActivity(reviewActivity);

    List<ReviewActivity> unsubmittedActivities = reviewActivityDao.listUnSubmittedReviewActivity(orgId, revieweeId);
    Assert.assertEquals(unsubmittedActivities.size(), 3);

    List<ReviewActivity> activities;
    activities = reviewActivityDao.listOtherReviewActivity(orgId, revieweeId, pageNumber, pageSize);
    Assert.assertEquals(activities.size(), 0);
    LOGGER.info("activity number " + activities.size());

    ReviewActivity reviewActivity;

    // submitted
    reviewActivity = unsubmittedActivities.get(0);
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId);

    reviewActivityDao.updateReviewActivity(reviewActivity);

    activities = reviewActivityDao.listOtherReviewActivity(orgId, revieweeId, pageNumber, pageSize);
    Assert.assertEquals(activities.size(), 1);

    // canceled
    reviewActivity = unsubmittedActivities.get(1);
    reviewActivity.setIsCanceled(1);
    reviewActivity.setLastModifiedUserId(revieweeId);
    reviewActivityDao.updateReviewActivity(reviewActivity);

    activities = reviewActivityDao.listOtherReviewActivity(orgId, revieweeId, pageNumber, pageSize);
    Assert.assertEquals(activities.size(), 2);
  }

  @Test
  public void testCountOtherReviewActivity() throws Exception {

    reviewActivityDao.insertReviewActivity(reviewActivity);

    reviewActivity.setTemplateId(templateId + 1);
    reviewActivityDao.insertReviewActivity(reviewActivity);

    reviewActivity.setTemplateId(templateId + 2);
    reviewActivityDao.insertReviewActivity(reviewActivity);

    List<ReviewActivity> unsubmittedActivities = reviewActivityDao.listUnSubmittedReviewActivity(orgId, revieweeId);

    long result = reviewActivityDao.countOtherReviewActivity(orgId, revieweeId);
    Assert.assertEquals(result, 0);

    // submitted
    reviewActivity = unsubmittedActivities.get(0);
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId);

    reviewActivityDao.updateReviewActivity(reviewActivity);

    result = reviewActivityDao.countOtherReviewActivity(orgId, revieweeId);
    Assert.assertEquals(result, 1);

  }

  @Test
  public void testListUnCanceledReviewActivityOfTemplate() throws Exception {

    batchInsertReviewActivities();

    List<ReviewActivity> activities;
    activities = reviewActivityDao.listUnCanceledReviewActivityOfTemplate(orgId, templateId);
    Assert.assertEquals(activities.size(), 5);
    LOGGER.info("activity number " + activities.size());

    // canceled
    reviewActivity = activities.get(1);
    reviewActivity.setIsCanceled(1);
    reviewActivity.setLastModifiedUserId(revieweeId);

    reviewActivityDao.updateReviewActivity(reviewActivity);

    activities = reviewActivityDao.listUnCanceledReviewActivityOfTemplate(orgId, templateId);
    Assert.assertEquals(activities.size(), 4);

  }

  @Test
  public void testUpdateReviewActivity() throws Exception {

    Long activityId = reviewActivityDao.insertReviewActivity(reviewActivity);
    Assert.assertEquals(activityId, reviewActivity.getActivityId());

    // submitted
    reviewActivity = reviewActivityDao.findReviewActivity(orgId, activityId);
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId);

    reviewActivityDao.updateReviewActivity(reviewActivity);
    reviewActivity = reviewActivityDao.findReviewActivity(orgId, activityId);
    Assert.assertEquals(reviewActivity.getIsSubmitted().intValue(), 1);

    // canceled
    reviewActivity.setIsCanceled(1);
    reviewActivity.setLastModifiedUserId(revieweeId);

    reviewActivityDao.updateReviewActivity(reviewActivity);
    reviewActivity = reviewActivityDao.findReviewActivity(orgId, activityId);
    Assert.assertEquals(reviewActivity.getIsCanceled().intValue(), 1);

  }

  @Test
  public void testBatchUpdateReviewActivities() throws Exception {

    batchInsertReviewActivities();

    List<ReviewActivity> activities;
    activities = reviewActivityDao.listUnCanceledReviewActivityOfTemplate(orgId, templateId);

    List<ReviewActivity> updateActivities = new ArrayList<>();
    for(ReviewActivity reviewActivity: activities) {
      reviewActivity.setIsCanceled(1);
      updateActivities.add(reviewActivity);
    }

    reviewActivityDao.batchUpdateReviewActivities(updateActivities);

    activities = reviewActivityDao.listUnCanceledReviewActivityOfTemplate(orgId, templateId);
    Assert.assertEquals(activities.size(), 0);

  }

  @Test
  public void testCountReviewActivityOfTemplate() throws Exception {

    batchInsertReviewActivities();

    long amount = reviewActivityDao.countReviewActivityOfTemplate(orgId, templateId);
    Assert.assertEquals(amount, 5);

  }

  @Test
  public void testListAllRevieweeIdOfTemplate() throws Exception {

    batchInsertReviewActivities();

    List<Long> result = reviewActivityDao.listAllRevieweeIdOfTemplate(orgId, templateId);
    Assert.assertEquals(result.size(), 5);
  }

  @Test
  public void testListSubmittedRevieweeIdOfTemplate() throws Exception {

    batchInsertReviewActivities();

    List<ReviewActivity> activities =
            reviewActivityDao.listUnSubmittedReviewActivity(orgId, revieweeId);

    reviewActivity = activities.get(0);

    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId);

    reviewActivityDao.updateReviewActivity(reviewActivity);

    List<Long> result = reviewActivityDao.listSubmittedRevieweeIdOfTemplate(orgId, templateId);
    Assert.assertEquals(result.size(), 1);

  }

  @Test
  public void testListAllReviewActivityOfTemplate() throws Exception {

    batchInsertReviewActivities();

    List<ReviewActivity> result = reviewActivityDao.listAllReviewActivityOfTemplate(orgId, templateId);
    Assert.assertEquals(result.size(), 5);
  }

  @Test
  public void testListAllValidReviewActivitiesByRevieweeAndTemplatesList() throws Exception {
    reviewActivityDao.insertReviewActivity(reviewActivity);
    List<Long> result = reviewActivityDao.listAllValidReviewActivitiesByRevieweeAndTemplatesList(
            reviewActivity.getOrgId(),
            new ArrayList<>(
                    Arrays.asList(
                            reviewActivity.getTemplateId()
                    )
            ),
            reviewActivity.getRevieweeId()
    );
    Assert.assertEquals(result.size(), 1);
  }
}
