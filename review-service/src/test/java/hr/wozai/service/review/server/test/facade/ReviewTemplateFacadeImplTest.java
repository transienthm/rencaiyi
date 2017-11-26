package hr.wozai.service.review.server.test.facade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.review.server.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

import hr.wozai.service.review.client.dto.ReviewActivityUserListDTO;
import hr.wozai.service.review.client.dto.ReviewReportDTO;
import hr.wozai.service.review.client.dto.ReviewTemplateDTO;
import hr.wozai.service.review.client.dto.ReviewTemplateListDTO;
import hr.wozai.service.review.client.enums.ReviewItemType;
import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.client.facade.ReviewTemplateFacade;
import hr.wozai.service.review.server.service.ReviewActivityService;
import hr.wozai.service.review.server.service.ReviewCommentService;
import hr.wozai.service.review.server.service.ReviewInvitationService;
import hr.wozai.service.review.server.service.ReviewProjectService;
import hr.wozai.service.review.server.service.ReviewQuestionService;
import hr.wozai.service.review.server.service.ReviewTemplateService;
import hr.wozai.service.review.server.test.base.TestBase;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;


/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-19
 */
public class ReviewTemplateFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewTemplateFacadeImplTest.class);

  @Autowired
  private ReviewTemplateFacade reviewTemplateFacade;

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

  private long userId = 64L;

  private long actorUserId = userId;
  private long adminUserId = userId;

  private long orgId = 99L;

  private long revieweeId = 54L;
  private long reviewerId = 55L;

  private String templateName = "2016First";

  private String question1 = "First Q";

  private ReviewTemplate reviewTemplate;

  private ReviewActivity reviewActivity;

  private ReviewInvitation reviewInvitation;

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

    reviewComment = new ReviewComment();
    reviewComment.setOrgId(orgId);
    reviewComment.setTemplateId(templateId);
  }

  @Test
  public void testInsertReviewTemplate() throws Exception {

    ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();
    BeanUtils.copyProperties(reviewTemplate, reviewTemplateDTO);

    reviewTemplateDTO.setTeamIds(new ArrayList<>(Arrays.asList(0L, 1L, 2L, 3L)));
    LongDTO reviewTemplateIdDTO =
        reviewTemplateFacade.insertReviewTemplate(orgId, reviewTemplateDTO, actorUserId, adminUserId);
    Assert.assertEquals(reviewTemplateIdDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    long templateId = reviewTemplateIdDTO.getData();

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 1);

    ReviewQuestion reviewQuestion = reviewQuestions.get(0);
    Assert.assertEquals(reviewQuestion.getName(), question1);
  }

  @Test
  public void testFindReviewTemplate() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);
    prepareData(templateId);
    reviewActivityService.insertReviewActivity(reviewActivity);
    reviewInvitation.setReviewerId(actorUserId);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    ReviewTemplateDTO reviewTemplateDTO =
        reviewTemplateFacade.findReviewTemplate(orgId, templateId, actorUserId, adminUserId);
    Assert.assertEquals(reviewTemplateDTO.getTemplateName(), templateName);
  }

  @Test
  public void testListReviewTemplate() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    List<Integer> statuses = new ArrayList<>();
    for (ReviewTemplateStatus reviewTemplateStatus : ReviewTemplateStatus.values()) {
      statuses.add(reviewTemplateStatus.getCode());
    }
    ReviewTemplateListDTO reviewTemplateListDTO =
        reviewTemplateFacade.listReviewTemplate(orgId,  1, 20, statuses, 2, false, actorUserId, adminUserId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), reviewTemplateListDTO.getServiceStatusDTO().getCode());

    List<ReviewTemplateDTO> reviewTemplateDTOs = reviewTemplateListDTO.getReviewTemplateDTOs();

//    ReviewTemplateDTO reviewTemplateDTO = reviewTemplateDTOs.get(0);
//    Assert.assertEquals(reviewTemplateDTO.getTemplateId().longValue(), templateId);
//    Assert.assertEquals(reviewTemplateDTO.getFinishedNumber().longValue(), 0);
  }

  @Test
  public void testCountReviewTemplate() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    LongDTO amount = reviewTemplateFacade.countReviewTemplate(orgId);
    Assert.assertEquals(amount.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

  }

  @Test
  public void testUpdateReviewTemplate() throws Exception {

//    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);
//
//    String newTemlateName = "2017fff";
//    reviewTemplate.setTemplateName(newTemlateName);
//
//    ReviewTemplateDTO reviewTemplateDTO = new ReviewTemplateDTO();
//    BeanUtils.copyProperties(reviewTemplate, reviewTemplateDTO);
//
//    VoidDTO result = reviewTemplateFacade.updateReviewTemplate(orgId, reviewTemplateDTO, actorUserId, adminUserId);
//    Assert.assertEquals(result.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
//
//    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
//    Assert.assertEquals(reviewTemplate.getTemplateName(), newTemlateName);

  }

  @Test
  public void testPublishReviewTemplate() throws Exception {

//    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);
//
//    VoidDTO result = reviewTemplateFacade.publishReviewTemplate(orgId, templateId, userId, actorUserId, adminUserId);
//    Assert.assertEquals(result.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
//
//    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);
//    Assert.assertEquals(reviewTemplate.getState().intValue(), ReviewTemplateStatus.IN_PROGRESS.getCode());
  }

  @Test
  public void testCancelReviewTemplate() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 1);

    long questionId = reviewQuestions.get(0).getQuestionId();

    //insert comment
    String content = "I am good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(revieweeId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);

    reviewCommentService.insertReviewComment(reviewComment);

    List<ReviewComment> comments = reviewCommentService
        .listReviewAllCommentByReviewer(orgId, templateId, revieweeId, revieweeId);
    Assert.assertEquals(comments.size(), 1);
    ReviewComment reviewComment = comments.get(0);
    Assert.assertEquals(reviewComment.getContent(), content);

    //cancel template
    long lastModifiedUserId = userId;
    VoidDTO cancelResult = reviewTemplateFacade.cancelReviewTemplate(
        orgId, templateId, lastModifiedUserId, actorUserId, adminUserId);
    Assert.assertEquals(cancelResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ReviewActivity result = reviewActivityService.findReviewActivity(orgId, activityId);
    Assert.assertEquals(result.getIsCanceled().longValue(), 1L);
    Assert.assertEquals(result.getIsSubmitted().longValue(), 0L);

    comments = reviewCommentService.listReviewAllCommentByReviewer(orgId, templateId, revieweeId, revieweeId);
    Assert.assertEquals(comments.size(), 0);
  }

  @Test
  public void testCancelReviewTemplate1() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 1);

    long questionId = reviewQuestions.get(0).getQuestionId();

    //insert comment
    String content = "I am good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(revieweeId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);

    reviewCommentService.insertReviewComment(reviewComment);

    ReviewActivity result = reviewActivityService.findReviewActivity(orgId, activityId);
    Assert.assertEquals(result.getIsCanceled().intValue(), 0);
    Assert.assertEquals(result.getIsSubmitted().intValue(), 0);

    List<ReviewComment> reviewComments = reviewCommentService
        .listReviewAllCommentByReviewer(orgId, templateId, revieweeId, revieweeId);
    Assert.assertEquals(reviewComments.size(), 1);

    // submit
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId);
    reviewActivityService.updateReviewActivity(reviewActivity);

    // cancel template
    VoidDTO cancelResult = reviewTemplateFacade.cancelReviewTemplate(
        orgId, templateId, userId, actorUserId, adminUserId);
    Assert.assertEquals(cancelResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    result = reviewActivityService.findReviewActivity(orgId, activityId);
    Assert.assertEquals(result.getIsCanceled().longValue(), 1L);
    Assert.assertEquals(result.getIsSubmitted().longValue(), 1L);

    reviewComments = reviewCommentService
        .listReviewAllCommentByReviewer(orgId, templateId, revieweeId, revieweeId);
    Assert.assertEquals(reviewComments.size(), 1);
  }

  @Test
  public void testCancelReviewTemplate2() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    reviewActivityService.insertReviewActivity(reviewActivity);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 1);

    long questionId = reviewQuestions.get(0).getQuestionId();

    //insert comment
    String content = "I am good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(revieweeId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);

    reviewCommentService.insertReviewComment(reviewComment);

    // submit
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId);
    reviewActivityService.updateReviewActivity(reviewActivity);

    //cancel before submitted "reviewerId"
    long invitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);

    // peer review comment
    content = "He is good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    reviewCommentService.insertReviewComment(reviewComment);

    List<ReviewComment> reviewComments = reviewCommentService.listReviewAllCommentByReviewer(
        orgId, templateId, revieweeId, reviewerId);
    Assert.assertEquals(reviewComments.size(), 1);

    ReviewInvitation result = reviewInvitationService.findReviewInvitation(orgId, invitationId);
    Assert.assertEquals(result.getIsCanceled().intValue(), 0);
    Assert.assertEquals(result.getIsSubmitted().intValue(), 0);

    // cancel template
    VoidDTO cancelResult = reviewTemplateFacade.cancelReviewTemplate(
        orgId, templateId, userId, actorUserId, adminUserId);
    Assert.assertEquals(cancelResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    result = reviewInvitationService.findReviewInvitation(orgId, invitationId);
    Assert.assertEquals(result.getIsCanceled().longValue(), 1L);
    Assert.assertEquals(result.getIsSubmitted().longValue(), 0L);

    reviewComments = reviewCommentService.listReviewAllCommentByReviewer(
        orgId, templateId, revieweeId, reviewerId);
    Assert.assertEquals(reviewComments.size(), 0);
  }

  @Test
  public void testCancelReviewTemplate3() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    reviewActivityService.insertReviewActivity(reviewActivity);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 1);

    long questionId = reviewQuestions.get(0).getQuestionId();

    //insert comment
    String content = "I am good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(revieweeId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);

    reviewCommentService.insertReviewComment(reviewComment);

    // submit
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId);
    reviewActivityService.updateReviewActivity(reviewActivity);

    //cancel before submitted "reviewerId"
    long invitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);

    // peer review comment
    content = "He is good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    reviewCommentService.insertReviewComment(reviewComment);

    List<ReviewComment> reviewComments = reviewCommentService.listReviewAllCommentByReviewer(
        orgId, templateId, revieweeId, reviewerId);
    Assert.assertEquals(reviewComments.size(), 1);

    ReviewInvitation result = reviewInvitationService.findReviewInvitation(orgId, invitationId);
    Assert.assertEquals(result.getIsCanceled().intValue(), 0);
    Assert.assertEquals(result.getIsSubmitted().intValue(), 0);

    //cancel after submitted by newReviewerId
    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationService.updateReviewInvitation(reviewInvitation);

    VoidDTO cancelResult = reviewTemplateFacade.cancelReviewTemplate(
        orgId, templateId, userId, actorUserId, adminUserId);
    Assert.assertEquals(cancelResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    result = reviewInvitationService.findReviewInvitation(orgId, invitationId);
    Assert.assertEquals(result.getIsCanceled().longValue(), 1L);
    Assert.assertEquals(result.getIsSubmitted().longValue(), 1L);

    reviewComments = reviewCommentService.listReviewAllCommentByReviewer(
        orgId, templateId, revieweeId, reviewerId);
    Assert.assertEquals(reviewComments.size(), 1);
  }

  @Test
  public void testListReviewTemplateByTemplateIds() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    List<Long> templateIds = new ArrayList<>();
    templateIds.add(templateId);
    ReviewTemplateListDTO
        reviewTemplateListDTO =
        reviewTemplateFacade.listReviewTemplateByTemplateIds(orgId, templateIds, actorUserId, adminUserId);
    Assert.assertEquals(reviewTemplateListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<ReviewTemplateDTO> reviewTemplateDTOs = reviewTemplateListDTO.getReviewTemplateDTOs();
    Assert.assertEquals(reviewTemplateDTOs.size(), 1);

    ReviewTemplateDTO reviewTemplateDTO = reviewTemplateDTOs.get(0);
    Assert.assertEquals(reviewTemplateDTO.getTemplateId().longValue(), templateId);

  }

  @Test
  public void testGetReviewTemplateReport() throws Exception {

    revieweeId = 11L;
    reviewerId = 12L;

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    reviewActivity.setRevieweeId(revieweeId - 1);
    reviewActivityService.insertReviewActivity(reviewActivity);

    reviewActivity.setRevieweeId(revieweeId + 1);
    reviewActivityService.insertReviewActivity(reviewActivity);

    reviewActivity.setRevieweeId(revieweeId);
    reviewActivityService.insertReviewActivity(reviewActivity);

    ReviewReportDTO reviewReportDTO =
        reviewTemplateFacade.getReviewTemplateReport(orgId, templateId, actorUserId, adminUserId);
    Assert.assertEquals(reviewReportDTO.getSelfNotBegin().intValue(), 3);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 1);

    long questionId = reviewQuestions.get(0).getQuestionId();

    //insert comment
    String content = "I am good";
    reviewComment.setRevieweeId(revieweeId + 1);
    reviewComment.setReviewerId(revieweeId + 1);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);
    reviewCommentService.insertReviewComment(reviewComment);

    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(revieweeId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);
    reviewCommentService.insertReviewComment(reviewComment);
    reviewReportDTO = reviewTemplateFacade.getReviewTemplateReport(orgId, templateId, actorUserId, adminUserId);
    Assert.assertEquals(reviewReportDTO.getSelfInProgress().intValue(), 2);

    // submit
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId);
    reviewActivityService.updateReviewActivity(reviewActivity);
    reviewReportDTO = reviewTemplateFacade.getReviewTemplateReport(orgId, templateId, actorUserId, adminUserId);
    Assert.assertEquals(reviewReportDTO.getSelfFinished().intValue(), 1);

    // staff invitation
    reviewInvitation.setReviewerId(reviewerId + 1);
    reviewInvitation.setIsManager(0);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);
    reviewReportDTO = reviewTemplateFacade.getReviewTemplateReport(orgId, templateId, actorUserId, adminUserId);
    Assert.assertEquals(reviewReportDTO.getPeerNotBegin().intValue(), 1);

    content = "He is good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId + 1);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    reviewCommentService.insertReviewComment(reviewComment);
    reviewReportDTO = reviewTemplateFacade.getReviewTemplateReport(orgId, templateId, actorUserId, adminUserId);
    Assert.assertEquals(reviewReportDTO.getPeerInProgress().intValue(), 1);

    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationService.updateReviewInvitation(reviewInvitation);

    reviewReportDTO = reviewTemplateFacade.getReviewTemplateReport(orgId, templateId, actorUserId, adminUserId);
    Assert.assertEquals(reviewReportDTO.getPeerFinished().intValue(), 1);

    // manager invitation
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(1);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);
    reviewReportDTO = reviewTemplateFacade.getReviewTemplateReport(orgId, templateId, actorUserId, adminUserId);
    Assert.assertEquals(reviewReportDTO.getManagerNotBegin().intValue(), 1);

    content = "He is good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    reviewCommentService.insertReviewComment(reviewComment);
    reviewReportDTO = reviewTemplateFacade.getReviewTemplateReport(orgId, templateId, actorUserId, adminUserId);
    Assert.assertEquals(reviewReportDTO.getManagerInProgress().intValue(), 1);

    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setScore(3);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationService.updateReviewInvitation(reviewInvitation);

    reviewReportDTO = reviewTemplateFacade.getReviewTemplateReport(orgId, templateId, actorUserId, adminUserId);
    Assert.assertEquals(reviewReportDTO.getManagerFinished().intValue(), 1);

    Map<Integer, Long> scores = reviewReportDTO.getManagerScore();
    Assert.assertEquals(scores.get(3).longValue(), 1);

  }

  @Test
  public void testGetActivitiesOfTemplate() throws Exception {

    revieweeId = 61L;
    reviewerId = 62L;

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    reviewActivity.setRevieweeId(revieweeId - 1);
    reviewActivityService.insertReviewActivity(reviewActivity);

    reviewActivity.setRevieweeId(revieweeId + 1);
    long activityIdp1 = reviewActivityService.insertReviewActivity(reviewActivity);

    reviewActivity.setRevieweeId(revieweeId);
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 1);

    long questionId = reviewQuestions.get(0).getQuestionId();

    //insert comment
    String content = "I am good";
    reviewComment.setRevieweeId(revieweeId + 1);
    reviewComment.setReviewerId(revieweeId + 1);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);
    reviewCommentService.insertReviewComment(reviewComment);

    // submit
    reviewActivity.setActivityId(activityIdp1);
    reviewActivity.setRevieweeId(revieweeId + 1);
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId + 1);
    reviewActivityService.updateReviewActivity(reviewActivity);

    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(revieweeId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);
    reviewCommentService.insertReviewComment(reviewComment);

    // submit
    reviewActivity.setActivityId(activityId);
    reviewActivity.setRevieweeId(revieweeId);
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId);
    reviewActivityService.updateReviewActivity(reviewActivity);

    // staff invitation
    reviewInvitation.setReviewerId(reviewerId + 1);
    reviewInvitation.setIsManager(0);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    content = "He is good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId + 1);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    reviewCommentService.insertReviewComment(reviewComment);

    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationService.updateReviewInvitation(reviewInvitation);

    // manager invitation
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(1);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    content = "He is good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    reviewCommentService.insertReviewComment(reviewComment);

    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setScore(3);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationService.updateReviewInvitation(reviewInvitation);

    // manager invitation
    reviewInvitation.setRevieweeId(revieweeId + 1);
    reviewInvitation.setReviewerId(reviewerId - 2);
    reviewInvitation.setIsManager(1);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    content = "He is good";
    reviewComment.setRevieweeId(revieweeId + 1);
    reviewComment.setReviewerId(reviewerId - 2);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId - 2);
    reviewCommentService.insertReviewComment(reviewComment);

    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setScore(2);
    reviewInvitation.setLastModifiedUserId(reviewerId - 2);
    reviewInvitationService.updateReviewInvitation(reviewInvitation);

    String orderBy = "name";
    String direction = "DESC";
    ReviewActivityUserListDTO eviewActivityUserListDTO = reviewTemplateFacade
        .getActivitiesOfTemplate(orgId, templateId, orderBy, direction, actorUserId, adminUserId);
  }

  @Test
  public void testInitReviewGuide() {
    String stringJsonTemplatesInfo = "{" +
            "\"selfEvaluate\":{\"reviewTemplate\":{\"templateName\":\"[ZICH] 反馈引导演示（自评）\"},\"reviewActivities\":[{\"revieweeId\":7,\"isSubmitted\":0}],\"reviewInvitations\":[{\"revieweeId\":7,\"reviewerId\":6,\"isManager\":1,\"score\":0,\"isSubmitted\":0}],\"reviewComments\":[],\"reviewQuestions\":[{\"questionId\":1,\"name\":\"Self question 1 有哪些地方你觉得自己做得不错，并且打算继续保持？\"},{\"questionId\":2,\"name\":\"Self question 2 有哪些地方有待改进？\"}],\"reviewProjects\":[],\"reviewInvitedTeams\":[{\"teamId\":3}]}," +
            "\"peerEvaluate\":{\"reviewTemplate\":{\"templateName\":\"[ZICH] 反馈引导演示（互评）\"},\"reviewActivities\":[{\"revieweeId\":8,\"isSubmitted\":1}],\"reviewInvitations\":[{\"revieweeId\":8,\"reviewerId\":6,\"isManager\":1,\"score\":0,\"isSubmitted\":0},{\"revieweeId\":8,\"reviewerId\":7,\"isManager\":0,\"score\":0,\"isSubmitted\":0}],\"reviewComments\":[{\"itemType\":0,\"itemId\":1,\"revieweeId\":8,\"reviewerId\":8,\"content\":\"Peer-self Question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":8,\"reviewerId\":8,\"content\":\"Peer-self Question 2 comment\"}],\"reviewQuestions\":[{\"questionId\":1,\"name\":\"Peer question 1 有哪些地方你觉得自己做得不错，并且打算继续保持？\"},{\"questionId\":2,\"name\":\"Peer question 2 有哪些地方有待改进？\"}],\"reviewProjects\":[{\"revieweeId\":8,\"name\":\"Peer project name\",\"role\":\"Peer project role\",\"score\":5,\"comment\":\"Peer project comment\"}],\"reviewInvitedTeams\":[{\"teamId\":3}]}," +
            "\"managerEvaluate\":{\"reviewTemplate\":{\"templateName\":\"[ZICH] 反馈引导演示（主管评价）\"},\"reviewActivities\":[{\"revieweeId\":10,\"isSubmitted\":1},{\"revieweeId\":11,\"isSubmitted\":1}],\"reviewInvitations\":[{\"revieweeId\":10,\"reviewerId\":7,\"isManager\":1,\"score\":0,\"isSubmitted\":0},{\"revieweeId\":10,\"reviewerId\":11,\"isManager\":0,\"score\":5,\"isSubmitted\":1},{\"revieweeId\":11,\"reviewerId\":7,\"isManager\":1,\"score\":0,\"isSubmitted\":0},{\"revieweeId\":11,\"reviewerId\":10,\"isManager\":0,\"score\":5,\"isSubmitted\":1}],\"reviewComments\":[{\"itemType\":0,\"itemId\":1,\"revieweeId\":10,\"reviewerId\":10,\"content\":\"Manager-self question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":10,\"reviewerId\":10,\"content\":\"Manager-self question 2 comment\"},{\"itemType\":1,\"itemId\":0,\"revieweeId\":10,\"reviewerId\":11,\"content\":\"Manager-peer evaluate comment\"},{\"itemType\":0,\"itemId\":1,\"revieweeId\":10,\"reviewerId\":11,\"content\":\"Manager-peer question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":10,\"reviewerId\":11,\"content\":\"Manager-peer question 2 comment\"},{\"itemType\":0,\"itemId\":1,\"revieweeId\":11,\"reviewerId\":11,\"content\":\"Manager-self question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":11,\"reviewerId\":11,\"content\":\"Manager-self question 2 comment\"},{\"itemType\":1,\"itemId\":0,\"revieweeId\":11,\"reviewerId\":10,\"content\":\"Manager-peer evaluate comment\"},{\"itemType\":0,\"itemId\":1,\"revieweeId\":11,\"reviewerId\":10,\"content\":\"Manager-peer question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":11,\"reviewerId\":10,\"content\":\"Manager-peer question 2 comment\"}],\"reviewQuestions\":[{\"questionId\":1,\"name\":\"Manager question 1 有哪些地方你觉得自己做得不错，并且打算继续保持？\"},{\"questionId\":2,\"name\":\"Manager question 2 有哪些地方有待改进？\"}],\"reviewProjects\":[{\"revieweeId\":10,\"name\":\"Manager project name\",\"role\":\"Manager role\",\"score\":5,\"comment\":\"Manager project comment\"},{\"revieweeId\":11,\"name\":\"Manager project name\",\"role\":\"Manager role\",\"score\":5,\"comment\":\"Manager project comment\"}],\"reviewInvitedTeams\":[{\"teamId\":3}]}," +
            "\"publicDay\":{\"reviewTemplate\":{\"templateName\":\"[ZICH] 反馈引导演示（公示日）\"},\"reviewActivities\":[{\"revieweeId\":7,\"isSubmitted\":1}],\"reviewInvitations\":[{\"revieweeId\":7,\"reviewerId\":8,\"isManager\":0,\"score\":5,\"isSubmitted\":1},{\"revieweeId\":7,\"reviewerId\":6,\"isManager\":1,\"score\":5,\"isSubmitted\":1}],\"reviewComments\":[{\"itemType\":0,\"itemId\":1,\"revieweeId\":7,\"reviewerId\":7,\"content\":\"Public-self question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":7,\"reviewerId\":7,\"content\":\"Public-self question 2 comment\"},{\"itemType\":1,\"itemId\":0,\"revieweeId\":7,\"reviewerId\":8,\"content\":\"Public-peer evaluate comment\"},{\"itemType\":0,\"itemId\":1,\"revieweeId\":7,\"reviewerId\":8,\"content\":\"Public-peer question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":7,\"reviewerId\":8,\"content\":\"Public-peer question 2 comment\"},{\"itemType\":1,\"itemId\":0,\"revieweeId\":7,\"reviewerId\":6,\"content\":\"Public-manager evaluate comment\"},{\"itemType\":0,\"itemId\":1,\"revieweeId\":7,\"reviewerId\":6,\"content\":\"Public-manager question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":7,\"reviewerId\":6,\"content\":\"Public-manager question 2 comment\"}],\"reviewQuestions\":[{\"questionId\":1,\"name\":\"Public question 1 有哪些地方你觉得自己做得不错，并且打算继续保持？\"},{\"questionId\":2,\"name\":\"Public question 2 有哪些地方有待改进？\"}],\"reviewProjects\":[{\"revieweeId\":7,\"name\":\"Public project name\",\"role\":\"Public role\",\"score\":5,\"comment\":\"Public project comment\"}],\"reviewInvitedTeams\":[{\"teamId\":3}]}" +
            "}";

    HashMap<Long, Long> mapUserNumberToId = new HashMap<>();
    mapUserNumberToId.put(6L, 58L);
    mapUserNumberToId.put(7L, 78L);
    mapUserNumberToId.put(8L, 98L);
    mapUserNumberToId.put(9L, 99L);
    mapUserNumberToId.put(10L, 100L);
    mapUserNumberToId.put(11L, 115L);

    reviewTemplateFacade.initReviewGuide(3, 98, stringJsonTemplatesInfo, mapUserNumberToId, new HashMap<>(3, 3));
  }

  @Test
  public void testListAllValidTemplatesForInvitationsOfHomepage() {
    String stringJsonTemplatesInfo = "{" +
            "\"selfEvaluate\":{\"reviewTemplate\":{\"templateName\":\"[ZICH] 反馈引导演示（自评）\"},\"reviewActivities\":[{\"revieweeId\":7,\"isSubmitted\":0}],\"reviewInvitations\":[{\"revieweeId\":7,\"reviewerId\":6,\"isManager\":1,\"score\":0,\"isSubmitted\":0}],\"reviewComments\":[],\"reviewQuestions\":[{\"questionId\":1,\"name\":\"Self question 1 有哪些地方你觉得自己做得不错，并且打算继续保持？\"},{\"questionId\":2,\"name\":\"Self question 2 有哪些地方有待改进？\"}],\"reviewProjects\":[],\"reviewInvitedTeams\":[{\"teamId\":3}]}," +
            "\"peerEvaluate\":{\"reviewTemplate\":{\"templateName\":\"[ZICH] 反馈引导演示（互评）\"},\"reviewActivities\":[{\"revieweeId\":8,\"isSubmitted\":1}],\"reviewInvitations\":[{\"revieweeId\":8,\"reviewerId\":6,\"isManager\":1,\"score\":0,\"isSubmitted\":0},{\"revieweeId\":8,\"reviewerId\":7,\"isManager\":0,\"score\":0,\"isSubmitted\":0}],\"reviewComments\":[{\"itemType\":0,\"itemId\":1,\"revieweeId\":8,\"reviewerId\":8,\"content\":\"Peer-self Question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":8,\"reviewerId\":8,\"content\":\"Peer-self Question 2 comment\"}],\"reviewQuestions\":[{\"questionId\":1,\"name\":\"Peer question 1 有哪些地方你觉得自己做得不错，并且打算继续保持？\"},{\"questionId\":2,\"name\":\"Peer question 2 有哪些地方有待改进？\"}],\"reviewProjects\":[{\"revieweeId\":8,\"name\":\"Peer project name\",\"role\":\"Peer project role\",\"score\":5,\"comment\":\"Peer project comment\"}],\"reviewInvitedTeams\":[{\"teamId\":3}]}," +
            "\"managerEvaluate\":{\"reviewTemplate\":{\"templateName\":\"[ZICH] 反馈引导演示（主管评价）\"},\"reviewActivities\":[{\"revieweeId\":10,\"isSubmitted\":1},{\"revieweeId\":11,\"isSubmitted\":1}],\"reviewInvitations\":[{\"revieweeId\":10,\"reviewerId\":7,\"isManager\":1,\"score\":0,\"isSubmitted\":0},{\"revieweeId\":10,\"reviewerId\":11,\"isManager\":0,\"score\":5,\"isSubmitted\":1},{\"revieweeId\":11,\"reviewerId\":7,\"isManager\":1,\"score\":0,\"isSubmitted\":0},{\"revieweeId\":11,\"reviewerId\":10,\"isManager\":0,\"score\":5,\"isSubmitted\":1}],\"reviewComments\":[{\"itemType\":0,\"itemId\":1,\"revieweeId\":10,\"reviewerId\":10,\"content\":\"Manager-self question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":10,\"reviewerId\":10,\"content\":\"Manager-self question 2 comment\"},{\"itemType\":1,\"itemId\":0,\"revieweeId\":10,\"reviewerId\":11,\"content\":\"Manager-peer evaluate comment\"},{\"itemType\":0,\"itemId\":1,\"revieweeId\":10,\"reviewerId\":11,\"content\":\"Manager-peer question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":10,\"reviewerId\":11,\"content\":\"Manager-peer question 2 comment\"},{\"itemType\":0,\"itemId\":1,\"revieweeId\":11,\"reviewerId\":11,\"content\":\"Manager-self question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":11,\"reviewerId\":11,\"content\":\"Manager-self question 2 comment\"},{\"itemType\":1,\"itemId\":0,\"revieweeId\":11,\"reviewerId\":10,\"content\":\"Manager-peer evaluate comment\"},{\"itemType\":0,\"itemId\":1,\"revieweeId\":11,\"reviewerId\":10,\"content\":\"Manager-peer question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":11,\"reviewerId\":10,\"content\":\"Manager-peer question 2 comment\"}],\"reviewQuestions\":[{\"questionId\":1,\"name\":\"Manager question 1 有哪些地方你觉得自己做得不错，并且打算继续保持？\"},{\"questionId\":2,\"name\":\"Manager question 2 有哪些地方有待改进？\"}],\"reviewProjects\":[{\"revieweeId\":10,\"name\":\"Manager project name\",\"role\":\"Manager role\",\"score\":5,\"comment\":\"Manager project comment\"},{\"revieweeId\":11,\"name\":\"Manager project name\",\"role\":\"Manager role\",\"score\":5,\"comment\":\"Manager project comment\"}],\"reviewInvitedTeams\":[{\"teamId\":3}]}," +
            "\"publicDay\":{\"reviewTemplate\":{\"templateName\":\"[ZICH] 反馈引导演示（公示日）\"},\"reviewActivities\":[{\"revieweeId\":7,\"isSubmitted\":1}],\"reviewInvitations\":[{\"revieweeId\":7,\"reviewerId\":8,\"isManager\":0,\"score\":5,\"isSubmitted\":1},{\"revieweeId\":7,\"reviewerId\":6,\"isManager\":1,\"score\":5,\"isSubmitted\":1}],\"reviewComments\":[{\"itemType\":0,\"itemId\":1,\"revieweeId\":7,\"reviewerId\":7,\"content\":\"Public-self question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":7,\"reviewerId\":7,\"content\":\"Public-self question 2 comment\"},{\"itemType\":1,\"itemId\":0,\"revieweeId\":7,\"reviewerId\":8,\"content\":\"Public-peer evaluate comment\"},{\"itemType\":0,\"itemId\":1,\"revieweeId\":7,\"reviewerId\":8,\"content\":\"Public-peer question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":7,\"reviewerId\":8,\"content\":\"Public-peer question 2 comment\"},{\"itemType\":1,\"itemId\":0,\"revieweeId\":7,\"reviewerId\":6,\"content\":\"Public-manager evaluate comment\"},{\"itemType\":0,\"itemId\":1,\"revieweeId\":7,\"reviewerId\":6,\"content\":\"Public-manager question 1 comment\"},{\"itemType\":0,\"itemId\":2,\"revieweeId\":7,\"reviewerId\":6,\"content\":\"Public-manager question 2 comment\"}],\"reviewQuestions\":[{\"questionId\":1,\"name\":\"Public question 1 有哪些地方你觉得自己做得不错，并且打算继续保持？\"},{\"questionId\":2,\"name\":\"Public question 2 有哪些地方有待改进？\"}],\"reviewProjects\":[{\"revieweeId\":7,\"name\":\"Public project name\",\"role\":\"Public role\",\"score\":5,\"comment\":\"Public project comment\"}],\"reviewInvitedTeams\":[{\"teamId\":3}]}" +
            "}";

    HashMap<Long, Long> mapUserNumberToId = new HashMap<>();
    mapUserNumberToId.put(6L, 58L);
    mapUserNumberToId.put(7L, 78L);
    mapUserNumberToId.put(8L, 98L);
    mapUserNumberToId.put(9L, 99L);
    mapUserNumberToId.put(10L, 100L);
    mapUserNumberToId.put(11L, 115L);

    reviewTemplateFacade.initReviewGuide(3, 98, stringJsonTemplatesInfo, mapUserNumberToId, new HashMap<>(3, 3));
    reviewTemplateFacade.listAllValidTemplatesForInvitationsOfHomepage(3, 98);
    reviewTemplateFacade.listAllValidTemplatesForActivitiesOfHomepage(3, 98);
  }

  @Test
  public void test() {
    reviewTemplateFacade.getActivitiesOfTemplate(394L, 1442L, "name", "ASC", 1311L, 0L);
  }

  @Test
  public void testReviewObject() {
    ReviewObject reviewObject = new ReviewObject();
    reviewObject.getOrgId();
    reviewObject.getUserId();
    reviewObject.setOrgId(0L);
    reviewObject.setUserId(0L);
  }

}
