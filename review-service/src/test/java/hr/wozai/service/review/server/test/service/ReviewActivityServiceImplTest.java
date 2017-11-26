package hr.wozai.service.review.server.test.service;

import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.model.ReviewComment;
import hr.wozai.service.review.server.service.ReviewActivityService;
import hr.wozai.service.review.server.service.ReviewCommentService;
import hr.wozai.service.review.server.test.base.TestBase;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-31
 */
public class ReviewActivityServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewActivityServiceImplTest.class);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Autowired
  private ReviewActivityService reviewActivityService;

  @Autowired
  private ReviewCommentService reviewCommentService;

  private ReviewActivity reviewActivity;

  private ReviewComment reviewComment;

  private long mockOrgId = 19999999L;

  private long mockTemplateId = 29999999L;

  private long mockRevieweeId = 39999999L;
  private long mockReviewerId = 49999999L;
  private long newRevieweeId = 59999999L;
  private long newReviewerId = newRevieweeId;

  private int isSubmitted = 0;
  private int isCanceled = 0;
  private int isBackuped = 0;

  private int pageNumber = 1;
  private int pageSize = 5;

  private int itemType = 0;
  private long itemId = 55;
  String content = "好";

  @Before
  public void setup() throws Exception {

    reviewActivity = new ReviewActivity();

    reviewActivity.setOrgId(3L);
    reviewActivity.setTemplateId(1L);
    reviewActivity.setRevieweeId(98L);
    reviewActivity.setIsSubmitted(isSubmitted);
    reviewActivity.setIsCanceled(isCanceled);
    reviewActivity.setIsBackuped(isBackuped);
    reviewActivity.setLastModifiedUserId(98L);


    reviewComment = new ReviewComment();

    reviewComment.setOrgId(3L);
    reviewComment.setTemplateId(1L);
    reviewComment.setRevieweeId(98L);
    reviewComment.setReviewerId(98L);
    reviewComment.setItemType(itemType);
    reviewComment.setItemId(itemId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(7L);
  }

//  @Test
//  public void testInsertReviewActivity() throws Exception {
//
//  }


  @Test
  public void testFindReviewActivityByRevieweeId() {

    // prepare
    reviewActivity = new ReviewActivity();
    reviewActivity.setOrgId(mockOrgId);
    reviewActivity.setTemplateId(mockTemplateId);
    reviewActivity.setRevieweeId(mockRevieweeId);
    reviewActivity.setIsSubmitted(isSubmitted);
    reviewActivity.setIsCanceled(isCanceled);
    reviewActivity.setIsBackuped(isBackuped);
    reviewActivity.setLastModifiedUserId(mockReviewerId);

    reviewActivityService.insertReviewActivity(reviewActivity);

    // case 1: normal
    ReviewActivity reviewActivity =
        reviewActivityService.findReviewActivityByRevieweeId(mockOrgId, mockTemplateId, mockRevieweeId);
    Assert.assertNotNull(reviewActivity);

    // case 2: abnormal, not found
    thrown.expect(ServiceStatusException.class);
    reviewActivityService.findReviewActivityByRevieweeId(mockOrgId, 0, mockRevieweeId);
  }

  @Test
  public void testReviewActivityServiceImpl() {
    reviewActivityService.batchInsertReviewActivityAndManagerInvitation(null, null);
    try {
      reviewActivityService.findReviewActivity(9999999L, 9999999L);
    } catch (Exception e) {
      e.printStackTrace();
    }
    reviewActivityService.batchUpdateReviewActivities(null);
    List<ReviewActivity> reviewActivities = new ArrayList<>();
    reviewActivity.setIsReaded(0);
    reviewActivities.add(reviewActivity);
    reviewActivityService.batchUpdateReviewActivities(reviewActivities);
    reviewActivityService.countReviewActivityOfTemplate(9999999L, 9999999L);
    reviewActivityService.listAllValidReviewActivitiesByRevieweeAndTemplatesList(3, new ArrayList<>(Arrays.asList(1L)), 98);
  }

  //@Test
  //public void testBatchInsertReviewActivities() throws Exception {

  //}

  //@Test
  //public void testFindReviewActivity() throws Exception {

  //}

  //@Test
  //public void testListUnSubmittedReviewActivity() throws Exception {

  //}

  //@Test
  //public void testListOtherReviewActivity() throws Exception {

  //}

  //@Test
  //public void testListUnCanceledReviewActivityOfTemplate() throws Exception {

  //}

  //@Test
  //public void testUpdateReviewActivity() throws Exception {

  //}


  /*
  @Test
  public void testCancelReviewActivity() throws Exception {

  }
  */

  //@Test
  //public void testBatchUpdateReviewActivities() throws Exception {

  //}

  @Test
  public void testReviewComment() {
    try {
      reviewCommentService.findReviewComment(999999L, 999999L);
    } catch (Exception e) {
      e.printStackTrace();
    }
    reviewCommentService.listActivityOfTemplate(0L, 0L, null);

    reviewComment.setOrgId(0L);
    reviewComment.setTemplateId(0L);
    reviewComment.setItemType(0);
    reviewComment.setItemId(0L);
    reviewComment.setRevieweeId(1L);
    reviewComment.setReviewerId(2L);

    try {
      reviewCommentService.insertReviewComment(reviewComment);
      reviewCommentService.existReviewItemCommentByReviewer(0L, 0L, 0, 0, 1, 2);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      reviewCommentService.existReviewItemCommentByReviewer(0L, 0L, 0, 0, 1, 3);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      reviewCommentService.insertReviewComment(reviewComment);
      reviewCommentService.existReviewItemCommentByReviewer(0L, 0L, 0, 0, 1, 2);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}