// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.test.dao;

import hr.wozai.service.review.server.dao.ReviewInvitationDao;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.review.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class ReviewInvitationDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationDaoTest.class);

  @Autowired
  ReviewInvitationDao reviewInvitationDao;

  ReviewInvitation reviewInvitation;

  ReviewInvitation reviewInvitationResult;

  List<ReviewInvitation> reviewInvitations;

  long orgId = 19999999L;

  long templateId = 29999999L;

  long revieweeId = 39999999L;
  long reviewerId = 49999999L;

  int isManager = 1;
  int score = 0;
  int isSubmitted = 0;
  int isCanceled = 0;
  int isBackuped = 0;
  int isDeleted = 0;

  int pageNumber = 1;
  int pageSize = 10;

  int result;

  @Before
  public void setup() {

    reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(isManager);
    reviewInvitation.setScore(score);
    reviewInvitation.setIsSubmitted(isSubmitted);
    reviewInvitation.setIsCanceled(isCanceled);
    reviewInvitation.setIsBackuped(isBackuped);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitation.setIsDeleted(isDeleted);

  }


  @Test
  public void testInsertReviewInvitation() throws Exception {

    long invitationId = reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    Assert.assertEquals(reviewInvitation.getInvitationId().longValue(), invitationId);
    LOGGER.info("invitationId=" + invitationId);
  }

  public void batchInsertInvitations() {

    List<ReviewInvitation> newReviewInvitations = new ArrayList<>();
    newReviewInvitations.add(reviewInvitation);

    for(long i=13; i<15; i++) {
      ReviewInvitation newReviewInvitation = new ReviewInvitation();
      BeanUtils.copyProperties(reviewInvitation, newReviewInvitation);
      newReviewInvitation.setReviewerId(i);
      newReviewInvitations.add(newReviewInvitation);
    }

    reviewInvitationDao.batchInsertReviewInvitations(newReviewInvitations);
  }

  public void batchInsertStaffInvitations() {

    List<ReviewInvitation> newReviewInvitations = new ArrayList<>();
    reviewInvitation.setIsManager(0);
    newReviewInvitations.add(reviewInvitation);

    for(long i=13; i<15; i++) {
      ReviewInvitation newReviewInvitation = new ReviewInvitation();
      BeanUtils.copyProperties(reviewInvitation, newReviewInvitation);
      newReviewInvitation.setReviewerId(i);
      newReviewInvitation.setIsManager(0);
      newReviewInvitations.add(newReviewInvitation);
    }

    reviewInvitationDao.batchInsertReviewInvitations(newReviewInvitations);
  }


  @Test
  public void testBatchInsertReviewInvitations() throws Exception {

    List<ReviewInvitation> reviewInvitations = new ArrayList<>();
    for(long i=13; i<15; i++) {
      ReviewInvitation reviewInvitation = new ReviewInvitation();
      BeanUtils.copyProperties(this.reviewInvitation, reviewInvitation);
      reviewInvitation.setReviewerId(i);
      reviewInvitations.add(reviewInvitation);
    }
    reviewInvitationDao.batchInsertReviewInvitations(reviewInvitations);
  }

  @Test
  public void testFindReviewInvitation() throws Exception {

    Long invitationId = reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    Assert.assertEquals(invitationId, reviewInvitation.getInvitationId());

    reviewInvitationResult = reviewInvitationDao.findReviewInvitation(orgId, invitationId);
    Assert.assertEquals(reviewInvitationResult.getIsSubmitted().intValue(), isSubmitted);
    Assert.assertEquals(reviewInvitationResult.getLastModifiedUserId().longValue(), revieweeId);
  }

  @Test
  public void testFindReviewInvitationByTemplate() throws Exception {

    Long invitationId = reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    Assert.assertEquals(invitationId, reviewInvitation.getInvitationId());

    reviewInvitation = reviewInvitationDao.findReviewInvitationByTemplate(orgId, templateId, revieweeId, reviewerId);
    Assert.assertEquals(reviewInvitation.getReviewerId().longValue(), reviewerId);
  }

  @Test
  public void testListUnSubmittedReviewInvitation() throws Exception {

    batchInsertInvitations();

    reviewInvitations = reviewInvitationDao.listUnSubmittedReviewInvitation(orgId, reviewerId);
    Assert.assertEquals(reviewInvitations.size(), 1);

  }

  @Test
  public void testListSubmittedReviewInvitation() throws Exception {

    batchInsertInvitations();

    reviewInvitations = reviewInvitationDao.listSubmittedReviewInvitation(orgId, reviewerId, pageNumber, pageSize);
    Assert.assertEquals(reviewInvitations.size(), 0);

    reviewInvitations = reviewInvitationDao.listUnSubmittedReviewInvitation(orgId, reviewerId);
    reviewInvitation = reviewInvitations.get(0);

    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationDao.updateReviewInvitation(reviewInvitation);

    reviewInvitations = reviewInvitationDao.listSubmittedReviewInvitation(orgId, reviewerId, pageNumber, pageSize);
    Assert.assertEquals(reviewInvitations.size(), 1);

  }

  @Test
  public void testCountSubmittedReviewInvitation() throws Exception {

    batchInsertInvitations();

    reviewInvitations = reviewInvitationDao.listSubmittedReviewInvitation(orgId, reviewerId, pageNumber, pageSize);
    Assert.assertEquals(reviewInvitations.size(), 0);

    reviewInvitations = reviewInvitationDao.listUnSubmittedReviewInvitation(orgId, reviewerId);
    reviewInvitation = reviewInvitations.get(0);

    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationDao.updateReviewInvitation(reviewInvitation);

    long result = reviewInvitationDao.countSubmittedReviewInvitation(orgId, reviewerId);
    Assert.assertEquals(result, 1);
  }

  @Test
  public void testListCanceledReviewInvitation() throws Exception {

    batchInsertInvitations();

    reviewInvitations = reviewInvitationDao.listSubmittedReviewInvitation(orgId, reviewerId, pageNumber, pageSize);
    Assert.assertEquals(reviewInvitations.size(), 0);

    reviewInvitations = reviewInvitationDao.listUnSubmittedReviewInvitation(orgId, reviewerId);

    reviewInvitation = reviewInvitations.get(0);
    reviewInvitation.setIsCanceled(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);

    reviewInvitationDao.updateReviewInvitation(reviewInvitation);

    reviewInvitations = reviewInvitationDao.listCanceledReviewInvitation(orgId, reviewerId, pageNumber, pageSize);
    Assert.assertEquals(reviewInvitations.size(), 1);

  }

  @Test
  public void testCountCanceledReviewInvitation() throws Exception {

    batchInsertInvitations();

    reviewInvitations = reviewInvitationDao.listSubmittedReviewInvitation(orgId, reviewerId, pageNumber, pageSize);
    Assert.assertEquals(reviewInvitations.size(), 0);

    reviewInvitations = reviewInvitationDao.listUnSubmittedReviewInvitation(orgId, reviewerId);

    reviewInvitation = reviewInvitations.get(0);
    reviewInvitation.setIsCanceled(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);

    reviewInvitationDao.updateReviewInvitation(reviewInvitation);

    long result = reviewInvitationDao.countCanceledReviewInvitation(orgId, reviewerId);
    Assert.assertEquals(result, 1);
  }


  @Test
  public void testListUnCanceledReviewInvitationOfTemplate() throws Exception {

    batchInsertInvitations();

    reviewInvitations = reviewInvitationDao.listUnCanceledReviewInvitationOfTemplate(orgId, templateId);
    Assert.assertEquals(reviewInvitations.size(), 3);

    reviewInvitation = reviewInvitations.get(0);
    reviewInvitation.setIsCanceled(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationDao.updateReviewInvitation(reviewInvitation);

    reviewInvitations = reviewInvitationDao.listUnCanceledReviewInvitationOfTemplate(orgId, templateId);
    Assert.assertEquals(reviewInvitations.size(), 2);

  }

  @Test
  public void testListReviewInvitationOfTemplateAsReviewee() throws Exception {

    batchInsertInvitations();

    reviewInvitations = reviewInvitationDao.listReviewInvitationOfTemplateAsReviewee(orgId, templateId, revieweeId);
    Assert.assertEquals(reviewInvitations.size(), 3);
  }

  @Test
  public void testIsReviewerInvited() throws Exception {

    batchInsertInvitations();

    boolean result;
    result = reviewInvitationDao.isReviewerInvited(orgId, templateId, revieweeId, reviewerId);
    Assert.assertEquals(result, true);

    result = reviewInvitationDao.isReviewerInvited(orgId, templateId, revieweeId, reviewerId + 5);
    Assert.assertEquals(result, false);

  }

  @Test
  public void testUpdateReviewInvitation() throws Exception {

    Long invitationId = reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    Assert.assertEquals(invitationId, reviewInvitation.getInvitationId());

    isSubmitted = 1;
    reviewInvitation.setIsSubmitted(isSubmitted);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    result = reviewInvitationDao.updateReviewInvitation(reviewInvitation);
    Assert.assertEquals(result, 1);

    reviewInvitationResult = reviewInvitationDao.findReviewInvitation(orgId, invitationId);
    Assert.assertEquals(reviewInvitationResult.getIsSubmitted().intValue(), isSubmitted);
    Assert.assertEquals(reviewInvitationResult.getLastModifiedUserId().longValue(), reviewerId);

    isCanceled = 1;
    reviewInvitation.setIsCanceled(isCanceled);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    result = reviewInvitationDao.updateReviewInvitation(reviewInvitation);
    Assert.assertEquals(result, 1);

    reviewInvitationResult = reviewInvitationDao.findReviewInvitation(orgId, invitationId);
    Assert.assertEquals(reviewInvitationResult.getIsCanceled().intValue(), isCanceled);
    Assert.assertEquals(reviewInvitationResult.getLastModifiedUserId().longValue(), reviewerId);

  }

  @Test
  public void testBatchUpdateReviewInvitations() throws Exception {

    batchInsertInvitations();

    reviewInvitations = reviewInvitationDao.listUnCanceledReviewInvitationOfTemplate(orgId, templateId);

    for(ReviewInvitation reviewInvitation: reviewInvitations) {
      reviewInvitation.setIsCanceled(1);
      reviewInvitation.setLastModifiedUserId(reviewInvitation.getReviewerId());
    }

    reviewInvitationDao.batchUpdateReviewInvitations(reviewInvitations);

    reviewInvitations = reviewInvitationDao.listUnCanceledReviewInvitationOfTemplate(orgId, templateId);
    Assert.assertEquals(reviewInvitations.size(), 0);
  }

  @Test
  public void testUpdateReviewInvitationBackupStatus() throws Exception {

    Long invitationId = reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    Assert.assertEquals(invitationId, reviewInvitation.getInvitationId());

    int IS_BACKUP = 1;
    result = reviewInvitationDao.updateReviewInvitationBackupStatus(orgId, invitationId, IS_BACKUP);
    Assert.assertEquals(result, 1);

    reviewInvitationResult = reviewInvitationDao.findReviewInvitation(orgId, invitationId);
    Assert.assertEquals(reviewInvitationResult.getIsBackuped().intValue(), IS_BACKUP);

  }

  @Test
  public void testCountReviewInvitationOfTemplate() throws Exception {

    batchInsertInvitations();
    long amount = reviewInvitationDao.countReviewInvitationOfTemplate(orgId, templateId);
    Assert.assertEquals(amount, 3);

  }

  @Test
  public void testCountFinishedReviewInvitationOfTemplate() throws Exception {

    batchInsertInvitations();
    long amount = reviewInvitationDao.countFinishedReviewInvitationOfTemplate(orgId, templateId);
    Assert.assertEquals(amount, 0);

    reviewInvitations = reviewInvitationDao.listUnSubmittedReviewInvitation(orgId, reviewerId);
    reviewInvitation = reviewInvitations.get(0);

    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    result = reviewInvitationDao.updateReviewInvitation(reviewInvitation);
    Assert.assertEquals(result, 1);

    amount = reviewInvitationDao.countFinishedReviewInvitationOfTemplate(orgId, templateId);
    Assert.assertEquals(amount, 1);

  }


  @Test
  public void testListRevieweeReviewInvitation() throws Exception {

    batchInsertInvitations();

    reviewInvitations = reviewInvitationDao.listSubmittedReviewInvitation(orgId, reviewerId, pageNumber, pageSize);
    Assert.assertEquals(reviewInvitations.size(), 0);

    reviewInvitations = reviewInvitationDao.listUnSubmittedReviewInvitation(orgId, reviewerId);
    reviewInvitation = reviewInvitations.get(0);

    List<ReviewInvitation> revieweeInvitations =
        reviewInvitationDao.listRevieweeReviewInvitation(orgId, revieweeId, reviewerId);
    Assert.assertEquals(revieweeInvitations.size(), 1);

    ReviewInvitation reviewInvitation = revieweeInvitations.get(0);
    Assert.assertEquals(reviewInvitation.getTemplateId().longValue(), templateId);

    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationDao.updateReviewInvitation(reviewInvitation);

    revieweeInvitations =
        reviewInvitationDao.listRevieweeReviewInvitation(orgId, revieweeId, reviewerId);
    Assert.assertEquals(revieweeInvitations.size(), 1);

  }

  @Test
  public void testListStaffReviewerIdOfTemplate() throws Exception {

    batchInsertStaffInvitations();
    List<ReviewInvitation> staffInvitations = reviewInvitationDao.listStaffReviewerIdOfTemplate(orgId, templateId);
    Assert.assertEquals(staffInvitations.size(), 3);
  }

  @Test
  public void testListStaffSubmittedReviewerIdOfTemplate() throws Exception {

    batchInsertStaffInvitations();
    List<ReviewInvitation> staffInvitations = reviewInvitationDao.listUnCanceledReviewInvitationOfTemplate(orgId, templateId);
    Assert.assertEquals(staffInvitations.size(), 3);

    reviewInvitation = staffInvitations.get(0);

    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationDao.updateReviewInvitation(reviewInvitation);

    staffInvitations = reviewInvitationDao.listStaffSubmittedReviewerIdOfTemplate(orgId, templateId);
    Assert.assertEquals(staffInvitations.size(), 1);
  }

  @Test
  public void testListManagerReviewerIdOfTemplate() throws Exception {

    batchInsertInvitations();
    List<ReviewInvitation> managerInvitations = reviewInvitationDao.listManagerReviewerIdOfTemplate(orgId, templateId);
    Assert.assertEquals(managerInvitations.size(), 3);
  }

  @Test
  public void testListManagerSubmittedReviewerIdOfTemplate() throws Exception {

    batchInsertInvitations();
    List<ReviewInvitation> managerInvitations = reviewInvitationDao.listUnCanceledReviewInvitationOfTemplate(orgId, templateId);

    reviewInvitation = managerInvitations.get(0);

    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationDao.updateReviewInvitation(reviewInvitation);

    managerInvitations = reviewInvitationDao.listManagerSubmittedReviewerIdOfTemplate(orgId, templateId);
    Assert.assertEquals(managerInvitations.size(), 1);
  }

  @Test
  public void testListAllReviewInvitationOfTemplate() throws Exception {

    batchInsertInvitations();
    List<ReviewInvitation> invitations = reviewInvitationDao.listAllReviewInvitationOfTemplate(orgId, templateId);
    Assert.assertEquals(invitations.size(), 3);
  }

  @Test
  public void testListAllReviewInvitationByTemplateIdAndRevieweeId() {

    // prepare

    reviewInvitation.setIsManager(1);
    reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    int peerReviewerCount = 10;
    for (int i = 0; i < peerReviewerCount; i++) {
      reviewInvitation.setIsManager(0);
      reviewInvitation.setReviewerId(reviewInvitation.getReviewerId()+1);
      reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    }

    // verify

    List<ReviewInvitation> reviewInvitations = reviewInvitationDao
        .listAllReviewInvitationByTemplateIdAndRevieweeId(orgId, templateId, revieweeId);
    Assert.assertEquals(1 + peerReviewerCount, reviewInvitations.size());

  }

  @Test
  public void testListAllReviewInvitationByTemplateIdAndRevieweeIdExceptManager() {

    // prepare

    reviewInvitation.setIsManager(1);
    reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    int peerReviewerCount = 10;
    for (int i = 0; i < peerReviewerCount; i++) {
      reviewInvitation.setIsManager(0);
      reviewInvitation.setReviewerId(reviewInvitation.getReviewerId()+1);
      reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    }

    // verify

    List<ReviewInvitation> reviewInvitations = reviewInvitationDao
            .listAllReviewInvitationByTemplateIdAndRevieweeIdExceptManager(orgId, templateId, revieweeId);
    Assert.assertEquals(peerReviewerCount, reviewInvitations.size());

  }

  @Test
  public void testCountReviewInvitationScore() throws Exception {

    List<ReviewInvitation> newReviewInvitations = new ArrayList<>();
    newReviewInvitations.add(reviewInvitation);

    for(long i=13; i<15; i++) {
      ReviewInvitation newReviewInvitation = new ReviewInvitation();
      BeanUtils.copyProperties(reviewInvitation, newReviewInvitation);
      newReviewInvitation.setReviewerId(i);
      newReviewInvitations.add(newReviewInvitation);
    }
    reviewInvitationDao.batchInsertReviewInvitations(newReviewInvitations);

    List<ReviewInvitation> invitations = reviewInvitationDao.listAllReviewInvitationOfTemplate(orgId, templateId);
    for(ReviewInvitation reviewInvitation: invitations) {
      reviewInvitation.setScore(2);
      reviewInvitation.setIsSubmitted(1);
      reviewInvitation.setLastModifiedUserId(reviewerId);
      reviewInvitationDao.updateReviewInvitation(reviewInvitation);
    }

    Map<Integer, Long> scores = reviewInvitationDao.countReviewInvitationScore(orgId, templateId);
    Long amount = scores.get(2);
    Assert.assertEquals(amount.longValue(), 3);

  }

  @Test
  public void testListAllReviewInvitationByTemplateIdAndReviewerId() throws Exception {
    reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    List<ReviewInvitation> result =
            reviewInvitationDao.listAllReviewInvitationByTemplateIdAndReviewerId(
                    reviewInvitation.getOrgId(),
                    reviewInvitation.getTemplateId(),
                    reviewInvitation.getReviewerId()
            );
    Assert.assertEquals(result.size(), 1);
  }

  @Test
  public void testListAllReviewInvitationsByTemplatesAndReviewer() throws Exception {
    reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    List<ReviewInvitation> result =
            reviewInvitationDao.listAllReviewInvitationsByTemplatesAndReviewer(
                    reviewInvitation.getOrgId(),
                    new ArrayList<>(
                            Arrays.asList(reviewInvitation.getTemplateId())
                    ),
                    reviewInvitation.getReviewerId()
            );
    Assert.assertEquals(result.size(), 1);
  }
}
