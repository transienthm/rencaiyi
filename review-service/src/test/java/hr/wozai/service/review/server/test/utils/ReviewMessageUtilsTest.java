/*
package hr.wozai.service.review.server.test.utils;


import hr.wozai.service.review.client.enums.ReviewItemType;
import hr.wozai.service.review.server.model.*;
import hr.wozai.service.review.server.service.*;
import hr.wozai.service.review.server.test.base.TestBase;
import hr.wozai.service.review.server.utils.ReviewMessageUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

*/
/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-19
 *//*

public class ReviewMessageUtilsTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewEmailUtilsTest.class);

  @Autowired
  private ReviewMessageUtils reviewMessageUtils;

  @Autowired
  private IReviewTemplateService reviewTemplateService;

  @Autowired
  private IReviewActivityService reviewActivityService;

  @Autowired
  private IReviewInvitationService reviewInvitationService;

  @Autowired
  private IReviewProjectService reviewProjectService;

  @Autowired
  private IReviewQuestionService reviewQuestionService;

  @Autowired
  private IReviewCommentService reviewCommentService;

  private long orgId = 99;
  private long userId = 57L;

  private long revieweeId = 56L;
  private long reviewerId = 57L;

  private long actorUserId = 0L;
  private long adminUserId = 0L;

  private String templateName = "2016First";

  private ReviewTemplate reviewTemplate;

  private ReviewActivity reviewActivity;

  private ReviewInvitation reviewInvitation;

  private ReviewProject reviewProject;

  private ReviewComment reviewComment;


  @Before
  public void setup() {

    reviewTemplate = new ReviewTemplate();

    reviewTemplate.setOrgId(orgId);
    reviewTemplate.setTemplateName(templateName);
    reviewTemplate.setStartTime(System.currentTimeMillis() - 2000);
    reviewTemplate.setEndTime(System.currentTimeMillis() - 1000);
    reviewTemplate.setSelfReviewDeadline(System.currentTimeMillis() + 100);
    reviewTemplate.setPeerReviewDeadline(System.currentTimeMillis() + 500);
    reviewTemplate.setPublicDeadline(System.currentTimeMillis() + 1000);
    reviewTemplate.setLastModifiedUserId(userId);


    List<String> questions = new ArrayList<>();
    questions.add("First Q");
    questions.add("Second Q");
    reviewTemplate.setQuestions(questions);
  }

  private void prepareData(long templateId) {

    int isManager = 1;

    reviewActivity = new ReviewActivity();
    reviewActivity.setOrgId(orgId);
    reviewActivity.setTemplateId(templateId);
    reviewActivity.setRevieweeId(revieweeId);
    reviewActivity.setLastModifiedUserId(userId);

    reviewInvitation = new ReviewInvitation();
    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(isManager);
    reviewInvitation.setLastModifiedUserId(revieweeId);

    String name = "DL & DM";
    String role = "staff";
    Integer score = 5;
    String comment = "好";

    reviewProject = new ReviewProject();
    reviewProject.setOrgId(orgId);
    reviewProject.setTemplateId(templateId);
    reviewProject.setRevieweeId(revieweeId);
    reviewProject.setName(name);
    reviewProject.setRole(role);
    reviewProject.setScore(score);
    reviewProject.setComment(comment);
    reviewProject.setLastModifiedUserId(revieweeId);
  }


  @Test
  public void newReview(){

    reviewActivity.setCreatedTime(new Date().getTime());

    try {
      reviewMessageUtils.sendTemplateBeginMessage(99l, reviewActivity, 60l);
    }catch (Exception e){

    }

  }

  @Test
  public void testSendTemplateBeginMessage() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    prepareData(templateId);

    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    reviewMessageUtils.sendTemplateBeginMessage(orgId, reviewActivity,
            revieweeId);
  }

  @Test
  public void testSendInvitationMessage() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    prepareData(templateId);

    long invitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);

//    reviewMessageUtils.sendInvitationMessage(orgId, templateId, invitationId, revieweeId, reviewerId,
//            actorUserId, adminUserId);
  }

  @Test
  public void testSendPublicEmail() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    prepareData(templateId);
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    reviewMessageUtils.sendPublicMessage(orgId, templateId,
            revieweeId, actorUserId, adminUserId);
  }

  @Test
  public void testSendSubmittedCancelEmail() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);
    reviewMessageUtils.sendCancelMessage(orgId, templateId, revieweeId, actorUserId, adminUserId);
  }

  @Test
  public void testSendUnSubmittedActivityCancelEmail() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    prepareData(templateId);
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    long projectId = reviewProjectService.insertReviewProject(reviewProject);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);

    //reviewee comments
    long questionId = reviewQuestions.get(0).getQuestionId();
    String content = "I am good";
    reviewComment = new ReviewComment();
    reviewComment.setOrgId(orgId);
    reviewComment.setTemplateId(templateId);
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(revieweeId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);

    reviewCommentService.insertReviewComment(reviewComment);

    reviewMessageUtils.sendCancelMessage(orgId, templateId, revieweeId,
            actorUserId, adminUserId);
  }

  @Test
  public void testSendUnSubmittedInvitationCancelEmail() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    prepareData(templateId);
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    long projectId = reviewProjectService.insertReviewProject(reviewProject);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);

    //reviewer comments
    long questionId = reviewQuestions.get(0).getQuestionId();
    String content = "He is good";
    reviewComment = new ReviewComment();
    reviewComment.setOrgId(orgId);
    reviewComment.setTemplateId(templateId);
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);

    reviewCommentService.insertReviewComment(reviewComment);

    reviewMessageUtils.sendCancelMessage(orgId, templateId, reviewerId,
            actorUserId, adminUserId);

  }
}*/
