package hr.wozai.service.review.server.test.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.review.client.dto.ReviewInvitationDTO;
import hr.wozai.service.review.client.dto.ReviewInvitationListDTO;
import hr.wozai.service.review.client.enums.ReviewItemType;
import hr.wozai.service.review.client.facade.ReviewInvitationFacade;
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
import java.util.Arrays;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-05-06
 */
public class ReviewInvitationFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationFacadeImplTest.class);

  @Autowired
  private ReviewInvitationFacade reviewInvitationFacade;

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
  private long actorUserId = 0L;
  private long adminUserId = 0L;

  private long orgId = 99L;
  private long userId = 21L;

  private long managerUserId = 64;

  private long revieweeId = 61L;
  private long reviewerId = 62L;

  private String templateName = "2016First";

  private String question1 = "First Q";

  private long questionId;

  private ReviewTemplate reviewTemplate;

  private ReviewActivity reviewActivity;

  private ReviewInvitation reviewInvitation;

  private ReviewComment reviewComment;

  long invitationId;

  long managerInvitationId;

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
    reviewTemplate.setState(2);
    reviewTemplate.setIsReviewerAnonymous(0);

    List<String> questions = new ArrayList<>();
    questions.add(question1);
    reviewTemplate.setQuestions(questions);

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);
    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

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
    questionId = reviewQuestions.get(0).getQuestionId();

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

    reviewInvitation = new ReviewInvitation();
    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(managerUserId);
    reviewInvitation.setIsManager(1);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    managerInvitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);

    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsManager(0);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    invitationId = reviewInvitationService.insertReviewInvitation(reviewInvitation);
  }

  @Test
  public void testFindReviewInvitation() throws Exception {

    ReviewInvitationDTO reviewInvitationDTO = reviewInvitationFacade.findReviewInvitation(orgId, invitationId, actorUserId, adminUserId);
    Assert.assertEquals(reviewInvitationDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(reviewInvitationDTO.getReviewerId().longValue(), reviewerId);
  }

  @Test
  public void testListUnSubmittedReviewInvitation() throws Exception {

    ReviewInvitationListDTO reviewInvitationListDTO = reviewInvitationFacade.listUnSubmittedReviewInvitation(orgId, reviewerId, actorUserId, adminUserId);
    Assert.assertEquals(reviewInvitationListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<ReviewInvitationDTO> reviewInvitationDTOs = reviewInvitationListDTO.getReviewInvitationDTOs();
    Assert.assertEquals(reviewInvitationDTOs.size(), 1);
  }

  @Test
  public void testListSubmittedReviewInvitation() throws Exception {

    String content = "I am good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);

    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationService.updateReviewInvitation(reviewInvitation);

    ReviewInvitationListDTO reviewInvitationListDTO =
            reviewInvitationFacade.listSubmittedReviewInvitation(orgId, reviewerId, 1, 5, actorUserId, adminUserId);
    Assert.assertEquals(reviewInvitationListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
  }

  @Test
  public void testCountSubmittedReviewInvitation() throws Exception {

    String content = "I am good";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.QUESTION.getCode());
    reviewComment.setItemId(questionId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(revieweeId);

    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsSubmitted(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationService.updateReviewInvitation(reviewInvitation);

    LongDTO amount = reviewInvitationFacade.countSubmittedReviewInvitation(orgId, reviewerId, actorUserId, adminUserId);
    Assert.assertEquals(amount.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(amount.getData(), 1);
  }

  @Test
  public void testListCanceledReviewInvitation() throws Exception {

    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsCanceled(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationService.updateReviewInvitation(reviewInvitation);

    ReviewInvitationListDTO reviewInvitationListDTO =
            reviewInvitationFacade.listCanceledReviewInvitation(orgId, reviewerId, 1, 5, actorUserId, adminUserId);
    Assert.assertEquals(reviewInvitationListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    List<ReviewInvitationDTO> reviewInvitationDTOs = reviewInvitationListDTO.getReviewInvitationDTOs();
    Assert.assertEquals(reviewInvitationDTOs.size(), 1);

  }

  @Test
  public void testCountCanceledReviewInvitation() throws Exception {

    reviewInvitation.setReviewerId(reviewerId);
    reviewInvitation.setIsCanceled(1);
    reviewInvitation.setLastModifiedUserId(reviewerId);
    reviewInvitationService.updateReviewInvitation(reviewInvitation);

    LongDTO amount = reviewInvitationFacade.countCanceledReviewInvitation(orgId, reviewerId, actorUserId, adminUserId);
    Assert.assertEquals(amount.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(amount.getData(), 1);
  }

  @Test
  public void testRefuseReviewInvitation() throws Exception {

    VoidDTO result = reviewInvitationFacade.refuseReviewInvitation(orgId, invitationId, actorUserId, adminUserId);
    Assert.assertEquals(result.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ReviewInvitation reviewInvitation = reviewInvitationService.findReviewInvitation(orgId, invitationId);
    Assert.assertEquals(reviewInvitation.getIsCanceled().intValue(), 1);
  }

  @Test
  public void testListAllReviewInvitationByTemplateIdAndRevieweeId() {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(actorUserId);
    reviewInvitation.setIsManager(0);
    reviewInvitation.setScore(5);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    reviewInvitationFacade.listAllReviewInvitationByTemplateIdAndRevieweeId(
            reviewInvitation.getOrgId(),
            reviewInvitation.getTemplateId(),
            reviewInvitation.getRevieweeId(),
            actorUserId, adminUserId
    );
  }

  @Test
  public void testListAllReviewInvitationsByTemplatesAndReviewer() {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(actorUserId);
    reviewInvitation.setIsManager(0);
    reviewInvitation.setScore(5);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    List<Long> templates = new ArrayList<>();
    templates.add(reviewInvitation.getTemplateId());
    reviewInvitationFacade.listAllReviewInvitationsByTemplatesAndReviewer(
            reviewInvitation.getOrgId(),
            templates,
            reviewInvitation.getReviewerId(),
            actorUserId, adminUserId
    );
  }

  @Test
  public void testListAllReviewInvitationByTemplateIdAndReviewerIdAndIsManager() {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    ReviewInvitation reviewInvitation = new ReviewInvitation();

    reviewInvitation.setOrgId(orgId);
    reviewInvitation.setTemplateId(templateId);
    reviewInvitation.setRevieweeId(revieweeId);
    reviewInvitation.setReviewerId(actorUserId);
    reviewInvitation.setIsManager(0);
    reviewInvitation.setScore(5);
    reviewInvitation.setLastModifiedUserId(revieweeId);
    reviewInvitationService.insertReviewInvitation(reviewInvitation);

    reviewActivity.setTemplateId(reviewInvitation.getTemplateId());
    reviewActivity.setRevieweeId(revieweeId);
    reviewActivityService.updateReviewActivity(reviewActivity);

    reviewInvitationFacade.listAllReviewInvitationByTemplateIdAndReviewerIdAndIsManager(
            reviewInvitation.getOrgId(),
            reviewInvitation.getTemplateId(),
            reviewInvitation.getReviewerId(),
            0, actorUserId, adminUserId
    );
  }
}