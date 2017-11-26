package hr.wozai.service.review.server.test.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.review.client.dto.ReviewInvitationDetailDTO;
import hr.wozai.service.review.client.dto.ReviewInvitedUserDTO;
import hr.wozai.service.review.client.dto.ReviewInvitedUserListDTO;
import hr.wozai.service.review.client.dto.ReviewQuestionDetailDTO;
import hr.wozai.service.review.client.enums.ReviewItemType;
import hr.wozai.service.review.client.facade.ReviewInvitationDetailFacade;
import hr.wozai.service.review.server.model.*;
import hr.wozai.service.review.server.service.*;
import hr.wozai.service.review.server.test.base.TestBase;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
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
 * @Created: 2016-04-24
 */
public class ReviewInvitationDetailFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationDetailFacadeImplTest.class);

  @Autowired
  private ReviewInvitationDetailFacade reviewInvitationDetailFacade;

  @Autowired
  private ReviewTemplateService reviewTemplateService;

  @Autowired
  private ReviewActivityService reviewActivityService;

  @Autowired
  private ReviewInvitationService reviewInvitationService;

  @Autowired
  private ReviewQuestionService reviewQuestionService;

  @Autowired
  private ReviewProjectService reviewProjectService;

  @Autowired
  private ReviewCommentService reviewCommentService;

  private long orgId = 19999999L;
  private long userId = 29999999L;
  private long revieweeId = 39999999L;
  private long reviewerId = 49999999L;
  private long managerUserId = 59999999L;
  private long actorUserId = 69999999L;
  private long adminUserId = 79999999L;
  private String templateName = "2016First";
  private String question1 = "First Q";

  private ReviewTemplate reviewTemplate;
  private ReviewActivity reviewActivity;
  private ReviewComment reviewComment;

  @Before
  public void setup() throws Exception {
    reviewTemplate = new ReviewTemplate();

    reviewTemplate.setOrgId(orgId);
    reviewTemplate.setTemplateName(templateName);
    reviewTemplate.setStartTime(System.currentTimeMillis() - 2000);
    reviewTemplate.setEndTime(System.currentTimeMillis() - 1000);
    reviewTemplate.setSelfReviewDeadline(System.currentTimeMillis() + 100000);
    reviewTemplate.setPeerReviewDeadline(System.currentTimeMillis() + 500000);
    reviewTemplate.setPublicDeadline(System.currentTimeMillis() + 1000000);
    reviewTemplate.setLastModifiedUserId(userId);
    reviewTemplate.setIsReviewerAnonymous(0);
    reviewTemplate.setState(2);

    List<String> questions = new ArrayList<>();
    questions.add(question1);
    reviewTemplate.setQuestions(questions);
  }


  private void prepareData(long templateId) {

    reviewActivity = new ReviewActivity();
    reviewActivity.setOrgId(orgId);
    reviewActivity.setTemplateId(templateId);
    reviewActivity.setRevieweeId(revieweeId);
    reviewActivity.setLastModifiedUserId(userId);

    reviewComment = new ReviewComment();
    reviewComment.setOrgId(orgId);
    reviewComment.setTemplateId(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 1);

    long questionId = reviewQuestions.get(0).getQuestionId();

    String content = "I am good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(revieweeId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);

    reviewCommentService.insertReviewComment(reviewComment);

    // update activity
    reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(actorUserId);
    reviewActivityService.updateReviewActivity(reviewActivity);

    ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
    Assert.assertEquals(reviewActivity.getIsSubmitted().intValue(), 1);

  }

  @Test
  public void testGetReviewInvitationDetail() throws Exception {
    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    long invitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);

    ReviewInvitationDetailDTO reviewInvitationDetailDTO =
        reviewInvitationDetailFacade.getReviewInvitationDetail(orgId, invitationId, reviewerId, adminUserId);
    Assert.assertEquals(reviewInvitationDetailDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(reviewInvitationDetailDTO.getIsSubmittable().intValue(), 1);

    LOGGER.info(reviewInvitationDetailDTO.toString());

    List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs =
        reviewInvitationDetailDTO.getReviewQuestionDetailDTOs();
    Assert.assertEquals(reviewQuestionDetailDTOs.size(), 1);

    ReviewQuestionDetailDTO reviewQuestionDetailDTO = reviewQuestionDetailDTOs.get(0);
    Assert.assertEquals(reviewQuestionDetailDTO.getName(), question1);
    Assert.assertEquals(reviewQuestionDetailDTO.getIsEditable().intValue(), 1);
  }

  @Test
  public void testSubmitReviewInvitation() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    long invitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    ReviewQuestion reviewQuestion = reviewQuestions.get(0);

    long questionId = reviewQuestion.getQuestionId();

    String content = "He is good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    reviewCommentService.insertReviewComment(reviewComment);

    int score = 3;
    VoidDTO submitResult = reviewInvitationDetailFacade.submitManagerReviewInvitation(orgId, invitationId,
        reviewerId, score, reviewerId, adminUserId);
    Assert.assertEquals(submitResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    reviewInvitation = reviewInvitationService.findReviewInvitation(orgId, invitationId);
    Assert.assertEquals(reviewInvitation.getIsSubmitted().intValue(), 1);
    Assert.assertEquals(reviewInvitation.getScore().intValue(), score);

    ReviewInvitationDetailDTO reviewInvitationDetailDTO =
            reviewInvitationDetailFacade.getReviewInvitationDetail(orgId, invitationId, reviewerId, adminUserId);
    Assert.assertEquals(reviewInvitationDetailDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(reviewInvitationDetailDTO.getIsSubmittable().intValue(), 1);
    Assert.assertEquals(reviewInvitationDetailDTO.getScore().intValue(), score);

    score = 4;
    submitResult = reviewInvitationDetailFacade.submitManagerReviewInvitation(orgId, invitationId,
            reviewerId, score, reviewerId, adminUserId);
    Assert.assertEquals(submitResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    reviewInvitationDetailDTO =
            reviewInvitationDetailFacade.getReviewInvitationDetail(orgId, invitationId, reviewerId, adminUserId);
    Assert.assertEquals(reviewInvitationDetailDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(reviewInvitationDetailDTO.getIsSubmittable().intValue(), 1);
    Assert.assertEquals(reviewInvitationDetailDTO.getScore().intValue(), score);
  }

  @Test
  public void testInsertInvitationComment() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    long invitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    ReviewQuestion reviewQuestion = reviewQuestions.get(0);

    long questionId = reviewQuestion.getQuestionId();

    String content = "He is good";
    LongDTO commentIdDTO = reviewInvitationDetailFacade.insertInvitationComment(orgId, invitationId,
        questionId, managerUserId, content, reviewerId, adminUserId);
    Assert.assertEquals(commentIdDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ReviewComment reviewComment = reviewCommentService.findReviewComment(orgId, commentIdDTO.getData());
    Assert.assertEquals(reviewComment.getContent(), content);
  }

  @Test
  public void testUpdateInvitationComment() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    long invitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    ReviewQuestion reviewQuestion = reviewQuestions.get(0);

    long questionId = reviewQuestion.getQuestionId();

    String content = "He is good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);

    long commentId= reviewCommentService.insertReviewComment(reviewComment);

    ReviewComment reviewComment = reviewCommentService.findReviewComment(orgId, commentId);
    Assert.assertEquals(reviewComment.getContent(), content);

    String newContent = "He is very good";
    VoidDTO updateResult = reviewInvitationDetailFacade.updateInvitationComment(orgId, invitationId,
        commentId, managerUserId, newContent, reviewerId, adminUserId);
    Assert.assertEquals(updateResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    reviewComment = reviewCommentService.findReviewComment(orgId, commentId);
    Assert.assertEquals(reviewComment.getContent(), newContent);
  }

  @Test
  public void testGetReviewActivityInvitation() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    long invitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);

    ReviewInvitedUserListDTO reviewInvitedUserListDT =
            reviewInvitationDetailFacade.getReviewActivityInvitation(orgId, invitationId, reviewerId, reviewerId, adminUserId);
    Assert.assertEquals(reviewInvitedUserListDT.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ReviewInvitedUserDTO reviewInvitedUserDTO = reviewInvitedUserListDT.getManagerUserDTO();
    Assert.assertEquals(reviewInvitedUserDTO.getUserId().longValue(), reviewerId);

  }

  @Test
  public void testSubmitPeerReviewInvitation() {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setScore(5);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);
    reviewInvitationDetailFacade.submitPeerReviewInvitation(
            reviewInvitation.getOrgId(),
            reviewInvitation.getInvitationId(),
            managerUserId,
            reviewInvitation.getScore(),
            actorUserId, adminUserId
    );
  }

  @Test
  public void testCancelSubmissionOfPeerReviewInvitation() {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(actorUserId);
    reviewInvitation.setIsManager(0);
    reviewInvitation.setScore(5);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    reviewInvitationDetailFacade.cancelSubmissionOfPeerReviewInvitation(
            reviewInvitation.getOrgId(),
            reviewInvitation.getInvitationId(),
            actorUserId, adminUserId
    );
  }

  @Test
  public void testCancelSubmissionOfManagerReviewInvitation() {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(actorUserId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setScore(5);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    reviewInvitationDetailFacade.cancelSubmissionOfManagerReviewInvitation(
            reviewInvitation.getOrgId(),
            reviewInvitation.getInvitationId(),
            actorUserId, adminUserId
    );
  }
}