package hr.wozai.service.review.server.test.facade;

import hr.wozai.service.review.client.facade.ReviewActivityDetailFacade;
import hr.wozai.service.review.server.dao.ReviewTemplateDao;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.review.client.enums.ReviewItemType;
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
 * @Created: 2016-04-19
 */
public class ReviewActivityDetailFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewTemplateFacadeImplTest.class);

  @Autowired
  private ReviewActivityDetailFacade reviewActivityDetailFacade;

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

  @Autowired
  private ReviewTemplateDao reviewTemplateDao;

  private long actorUserId = 0L;
  private long adminUserId = 0L;

  private long orgId = 100L;
  private long userId = 21L;

  private long revieweeId = 98L;
  private long reviewerId = 12L;

  private long managerUserId = 17;

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
    reviewTemplate.setSelfReviewDeadline(System.currentTimeMillis() + 1000000);
    reviewTemplate.setPeerReviewDeadline(System.currentTimeMillis() + 5000000);
    reviewTemplate.setPublicDeadline(System.currentTimeMillis() + 10000000);
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
  }


  @Test
  public void testGetReviewActivityDetailDTO() throws Exception {

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

    ReviewInvitation reviewInvitation = new ReviewInvitation();
    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(managerUserId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitation.setScore(0);
    reviewInvitation.setIsSubmitted(0);
    reviewInvitation.setIsCanceled(0);
    reviewInvitation.setIsBackuped(0);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    ReviewActivityDetailDTO reviewActivityDetailDTO =
            reviewActivityDetailFacade.getReviewActivityDetailDTO(orgId, activityId, revieweeId, adminUserId);
    Assert.assertEquals(reviewActivityDetailDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    LOGGER.info(reviewActivityDetailDTO.toString());

    List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs = reviewActivityDetailDTO.getReviewQuestionDetailDTOs();
    Assert.assertEquals(reviewQuestionDetailDTOs.size(), 1);

    ReviewQuestionDetailDTO reviewQuestionDetailDTO = reviewQuestionDetailDTOs.get(0);
    ReviewCommentDTO reviewCommentDTO = reviewQuestionDetailDTO.getRevieweeComment();

    Assert.assertEquals(reviewCommentDTO.getContent(), content);
  }

  @Test
  public void testGetReviewActivityInvitation() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    ReviewInvitation reviewInvitation = new ReviewInvitation();
    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(managerUserId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitation.setScore(0);
    reviewInvitation.setIsSubmitted(0);
    reviewInvitation.setIsCanceled(0);
    reviewInvitation.setIsBackuped(0);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    ReviewInvitedUserListDTO reviewInvitedUserListDTO = reviewActivityDetailFacade.getReviewActivityInvitation(orgId,
        activityId, managerUserId, revieweeId, adminUserId);
    Assert.assertEquals(reviewInvitedUserListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ReviewInvitedUserDTO managerUserDTO = reviewInvitedUserListDTO.getManagerUserDTO();
    Assert.assertEquals(managerUserDTO.getUserId().longValue(), managerUserId);
  }

  @Test
  public void testGetReviewActivityInvitation1() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

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

    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(revieweeId);
    reviewActivityService.updateReviewActivity(reviewActivity);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(managerUserId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    ReviewInvitedUserListDTO reviewInvitedUserListDTO = reviewActivityDetailFacade.getReviewActivityInvitation(orgId,
            activityId, managerUserId, revieweeId, adminUserId);
    Assert.assertEquals(reviewInvitedUserListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ReviewInvitedUserDTO managerUserDTO = reviewInvitedUserListDTO.getManagerUserDTO();
    Assert.assertEquals(managerUserDTO.getUserId().longValue(), managerUserId);
  }


  @Test
  public void testSetReviewActivityInvitation() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

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

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(managerUserId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    reviewInvitation.setReviewerId(13L);
    reviewInvitation.setIsManager(0);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    List<ReviewInvitation> reviewInvitations =
        reviewInvitationService.listReviewInvitationOfTemplateAsReviewee(orgId, templateId, revieweeId);
    Assert.assertEquals(reviewInvitations.size(), 2);

    List<Long> userIds = new ArrayList<>();
    userIds.add(14L);
    userIds.add(15L);
    userIds.add(managerUserId);

    VoidDTO inviteResult = reviewActivityDetailFacade.setReviewActivityInvitation(orgId, activityId,
        managerUserId, userIds, revieweeId, adminUserId);
    Assert.assertEquals(inviteResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    reviewInvitations =
        reviewInvitationService.listReviewInvitationOfTemplateAsReviewee(orgId, templateId, revieweeId);
    Assert.assertEquals(reviewInvitations.size(), 3);

  }

  @Test
  public void testSubmitReviewActivity() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    reviewTemplate.setSelfReviewDeadline(System.currentTimeMillis()-100);
    reviewTemplateDao.updateReviewTemplate(reviewTemplate);

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

    List<Long> invitedUserIds = new ArrayList<>();
    invitedUserIds.add(13L);
    invitedUserIds.add(14L);

    VoidDTO submitResult = reviewActivityDetailFacade.submitReviewActivity(orgId, activityId, revieweeId, adminUserId);
    Assert.assertEquals(submitResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
    Assert.assertEquals(reviewActivity.getIsSubmitted().intValue(), 1);

    ReviewInvitation reviewInvitation = new ReviewInvitation();
    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setIsManager(0);
    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);
    List<ReviewInvitation> reviewInvitations =
        reviewInvitationService.listReviewInvitationOfTemplateAsReviewee(reviewInvitation.getOrgId(), reviewInvitation.getTemplateId(), reviewInvitation.getRevieweeId());
    Assert.assertEquals(reviewInvitations.size(), 1);

  }

  @Test
  public void testInsertActivityComment() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);
    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);
    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 1);

    long questionId = reviewQuestions.get(0).getQuestionId();

    String content = "I am good";

    LongDTO commentIdDTO = reviewActivityDetailFacade.insertActivityComment(orgId, activityId, questionId, content, revieweeId, adminUserId);
    Assert.assertEquals(commentIdDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ReviewComment reviewComment = reviewCommentService.findReviewComment(orgId, commentIdDTO.getData());

    Assert.assertEquals(reviewComment.getContent(), content);
    Assert.assertEquals(reviewComment.getRevieweeId().longValue(), revieweeId);
    Assert.assertEquals(reviewComment.getReviewerId().longValue(), revieweeId);
  }

  @Test
  public void testUpdateActivityComment() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);
    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);
    prepareData(templateId);

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

    long commentId = reviewCommentService.insertReviewComment(reviewComment);
    ReviewComment reviewComment = reviewCommentService.findReviewComment(orgId, commentId);

    Assert.assertEquals(reviewComment.getContent(), content);
    Assert.assertEquals(reviewComment.getRevieweeId().longValue(), revieweeId);
    Assert.assertEquals(reviewComment.getReviewerId().longValue(), revieweeId);

    String newContent = "I am very good";
    VoidDTO updateResult =reviewActivityDetailFacade.updateActivityComment(orgId, activityId, commentId, newContent, revieweeId, adminUserId);
    Assert.assertEquals(updateResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    reviewComment = reviewCommentService.findReviewComment(orgId, commentId);
    Assert.assertEquals(reviewComment.getContent(), newContent);
  }

  @Test
  public void testGetReviewActivityDetailDTOByHR() throws Exception {

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

    ReviewActivityDetailDTO reviewActivityDetailDTO =
            reviewActivityDetailFacade.getReviewActivityDetailDTOByHR(orgId, activityId, actorUserId, adminUserId);
    Assert.assertEquals(reviewActivityDetailDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    LOGGER.info(reviewActivityDetailDTO.toString());

    List<ReviewQuestionDetailDTO> reviewQuestionDetailDTOs = reviewActivityDetailDTO.getReviewQuestionDetailDTOs();
    Assert.assertEquals(reviewQuestionDetailDTOs.size(), 1);

    ReviewQuestionDetailDTO reviewQuestionDetailDTO = reviewQuestionDetailDTOs.get(0);
    ReviewCommentDTO reviewCommentDTO = reviewQuestionDetailDTO.getRevieweeComment();

    Assert.assertEquals(reviewCommentDTO.getContent(), content);

    reviewActivity.setIsSubmitted(1);
    reviewActivityService.updateReviewActivity(reviewActivity);
    reviewActivityDetailFacade.getReviewActivityDetailDTOByHR(orgId, activityId, actorUserId, adminUserId);
  }

  @Test
  public void testReivewActivityFacade() {
    try {
      reviewActivityService.insertReviewActivity(reviewActivity);
      reviewActivityDetailFacade.getReviewActivityDetailDTO(reviewActivity.getOrgId(), reviewActivity.getActivityId(), actorUserId, adminUserId);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testOthers() {
  }
}