// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.test.dao;

import hr.wozai.service.review.server.model.ReviewComment;
import hr.wozai.service.review.server.dao.ReviewCommentDao;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.review.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-16
 */
public class ReviewCommentDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewCommentDaoTest.class);

  @Autowired
  ReviewCommentDao reviewCommentDao;

  ReviewComment reviewComment;

  long orgId = 100L;

  long templateId = 1L;

  long revieweeId = 11L;
  long reviewerId = 12L;

  int itemType = 0;
  long itemId = 55;
  String content = "好";
  String contentNew = "no好";

  @Before
  public void setup() {

    reviewComment = new ReviewComment();
    reviewComment.setOrgId(orgId);
    reviewComment.setTemplateId(templateId);
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId - 1);
    reviewComment.setItemType(itemType);
    reviewComment.setItemId(itemId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);

  }

  @Test
  public void testInsertReviewComment() throws Exception {
    Long commentId = reviewCommentDao.insertReviewComment(reviewComment);
    Assert.assertEquals(commentId, reviewComment.getCommentId());
    LOGGER.info("commentId=" + commentId);
  }

  @Test
  public void testFindReviewComment() throws Exception {

    Long commentId = reviewCommentDao.insertReviewComment(reviewComment);
    Assert.assertEquals(commentId, reviewComment.getCommentId());
    LOGGER.info("commentId=" + commentId);

    ReviewComment reviewComment = reviewCommentDao.findReviewComment(orgId, commentId);
    Assert.assertEquals(reviewComment.getContent(), content);
    Assert.assertEquals(reviewComment.getRevieweeId().longValue(), revieweeId);

  }

  public void batchInsertComments() {

    // reviewee: 11
    // reviewer: 11
    reviewCommentDao.insertReviewComment(reviewComment);

    // itemId: 55
    // reviewee: 11
    // reviewer: 12 13
    for(long i=12; i<14; i++) {
      reviewComment.setReviewerId(i);
      reviewCommentDao.insertReviewComment(reviewComment);
    }

    // itemId: 56
    // reviewee: 11
    // reviewer: 11 12
    for(long i=11; i<13; i++) {
      reviewComment.setItemId(itemId + 1);
      reviewComment.setReviewerId(i);
      reviewCommentDao.insertReviewComment(reviewComment);
    }
  }

  @Test
  public void testListReviewItemCommentOfReviewers() throws Exception {

    batchInsertComments();

    List<Long> reviewerIds = new ArrayList<>();
    reviewerIds.add(11L);
    reviewerIds.add(12L);

    List<ReviewComment> comments;
    comments = reviewCommentDao.listReviewItemCommentOfReviewers(orgId, templateId,
        itemType, itemId, revieweeId, reviewerIds);
    LOGGER.info("comment number " + comments.size());
    Assert.assertEquals(comments.size(), 2);

  }

  @Test
  public void testListReviewAllCommentByReviewer() throws Exception {

    batchInsertComments();

    long reviewerId = 11;

    List<ReviewComment> comments;
    comments = reviewCommentDao.listReviewAllCommentByReviewer(orgId, templateId,
        revieweeId, reviewerId);
    LOGGER.info("comment number " + comments.size());
    Assert.assertEquals(comments.size(), 2);

  }

  @Test
  public void testUpdateReviewComment() throws Exception {
    Long commentId = reviewCommentDao.insertReviewComment(reviewComment);
    Assert.assertEquals(commentId, reviewComment.getCommentId());
    LOGGER.info("commentId=" + commentId);

    reviewComment.setContent(contentNew);
    reviewComment.setOrgId(orgId);
    reviewComment.setLastModifiedUserId(reviewerId);

    int result = reviewCommentDao.updateReviewComment(reviewComment);
    Assert.assertEquals(result, 1);

    ReviewComment reviewCommentResult = reviewCommentDao.findReviewComment(orgId, reviewComment.getCommentId());
    Assert.assertEquals(reviewCommentResult.getContent(), contentNew);
  }

  @Test
  public void testDeleteReviewComment() throws Exception {
    Long commentId = reviewCommentDao.insertReviewComment(reviewComment);
    Assert.assertEquals(commentId, reviewComment.getCommentId());
    LOGGER.info("commentId=" + commentId);

    long amount = reviewCommentDao.countReviewQuestionByReviewer(orgId, templateId, revieweeId, reviewerId-1);
    Assert.assertEquals(amount, 1);

    int result = reviewCommentDao.deleteReviewComment(orgId, commentId, reviewerId);
    Assert.assertEquals(result, 1);

    amount = reviewCommentDao.countReviewQuestionByReviewer(orgId, templateId, revieweeId, reviewerId-1);
    Assert.assertEquals(amount, 0);
  }

  @Test
  public void testDeleteReviewCommentByReviewer() throws Exception {

    Long commentId = reviewCommentDao.insertReviewComment(reviewComment);
    Assert.assertEquals(commentId, reviewComment.getCommentId());

    reviewCommentDao.deleteReviewCommentByReviewer(orgId, templateId, revieweeId, reviewerId-1, revieweeId+1);

    long amount = reviewCommentDao.countReviewQuestionByReviewer(orgId, templateId, revieweeId, reviewerId-1);
    Assert.assertEquals(amount, 0);

  }

  @Test
  public void testCountReviewQuestionByReviewer() throws Exception {

    reviewCommentDao.insertReviewComment(reviewComment);

    long amount;
    amount = reviewCommentDao.countReviewQuestionByReviewer(orgId, templateId, revieweeId, reviewerId-1);
    Assert.assertEquals(amount, 1);

  }

  @Test
  public void testCountReviewItemCommentByReviewer() throws Exception {

    reviewCommentDao.insertReviewComment(reviewComment);

    long amount;
    amount = reviewCommentDao.countReviewItemCommentByReviewer(orgId, templateId,
        itemType, itemId, revieweeId, reviewerId-1);
    Assert.assertEquals(amount, 1);
  }

  @Test
  public void testCountReviewAllCommentByReviewer() throws Exception {

    reviewCommentDao.insertReviewComment(reviewComment);

    long amount;
    amount = reviewCommentDao.countReviewAllCommentByReviewer(orgId, templateId,
        revieweeId, reviewerId-1);
    Assert.assertEquals(amount, 1);
  }

  @Test
  public void testListActivityOfTemplate() throws Exception {

    reviewCommentDao.insertReviewComment(reviewComment);

    List<Long> reviewerIds = new ArrayList<>();
    reviewerIds.add(revieweeId);

    List<Long> result = reviewCommentDao.listActivityOfTemplate(orgId, templateId, reviewerIds);
    Assert.assertEquals(result.size(), 1);
  }

  @Test
  public void testListInvitationOfTemplate() throws Exception {

    reviewComment.setReviewerId(reviewerId);
    reviewCommentDao.insertReviewComment(reviewComment);

    List<Long> reviewerIds = new ArrayList<>();
    reviewerIds.add(reviewerId);

    List<ReviewInvitation> result = reviewCommentDao.listInvitationOfTemplate(orgId, templateId, reviewerIds);
    Assert.assertEquals(result.size(), 1);
  }

}
