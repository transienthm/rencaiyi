package hr.wozai.service.review.server.test.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.review.client.dto.ReviewCommentDTO;
import hr.wozai.service.review.client.dto.ReviewProjectDetailDTO;
import hr.wozai.service.review.client.enums.ReviewItemType;
import hr.wozai.service.review.client.facade.ReviewInvitationProjectFacade;
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
 * @Created: 2016-04-25
 */
public class ReviewInvitationProjectFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewInvitationDetailFacadeImplTest.class);

  @Autowired
  private ReviewInvitationProjectFacade reviewInvitationProjectFacade;

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

  private long orgId = 100L;
  private long userId = 21L;

  private long revieweeId = 11L;
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

    ReviewProject reviewProject = new ReviewProject();
    reviewProject.setOrgId(orgId);
    reviewProject.setTemplateId(templateId);
    reviewProject.setRevieweeId(revieweeId);
    reviewProject.setName("DL & DM");
    reviewProject.setScore(3);
    reviewProject.setRole("manager");
    reviewProject.setComment("Excellent Job!");
    reviewProject.setLastModifiedUserId(revieweeId);
    reviewProjectService.insertReviewProject(reviewProject);

    // update activity
    reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
    reviewActivity.setIsSubmitted(1);
    reviewActivity.setLastModifiedUserId(actorUserId);
    reviewActivityService.updateReviewActivity(reviewActivity);

    ReviewActivity reviewActivity = reviewActivityService.findReviewActivity(orgId, activityId);
    Assert.assertEquals(reviewActivity.getIsSubmitted().intValue(), 1);
  }

  @Test
  public void testInsertInvitationProjectComment() throws Exception {

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

    List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
    ReviewProject reviewProject = reviewProjects.get(0);

    long projectId = reviewProject.getProjectId();

    String content = "The project is excellent";
    LongDTO commentIdDTO = reviewInvitationProjectFacade.insertInvitationProjectComment(orgId, invitationId,
        projectId, managerUserId, content, reviewerId, adminUserId);
    Assert.assertEquals(commentIdDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<Long> reviewerIds = new ArrayList<>();
    reviewerIds.add(reviewerId);
    List<ReviewComment> reviewComments = reviewCommentService.listReviewItemCommentOfReviewers(orgId, templateId,
        ReviewItemType.PROJECT.getCode(), projectId, revieweeId, reviewerIds);
    Assert.assertEquals(reviewComments.size(), 1);

    ReviewComment reviewComment = reviewComments.get(0);
    Assert.assertEquals(reviewComment.getContent(), content);
  }

  @Test
  public void testInsertInvitationProjectComment1() throws Exception {

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

    List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
    ReviewProject reviewProject = reviewProjects.get(0);

    long projectId = reviewProject.getProjectId();

    String content = "The project is excellent";
    LongDTO commentIdDTO = reviewInvitationProjectFacade.insertInvitationProjectComment(orgId, invitationId,
            projectId, reviewerId, content, reviewerId, adminUserId);
    Assert.assertEquals(commentIdDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<Long> reviewerIds = new ArrayList<>();
    reviewerIds.add(reviewerId);
    List<ReviewComment> reviewComments = reviewCommentService.listReviewItemCommentOfReviewers(orgId, templateId,
            ReviewItemType.PROJECT.getCode(), projectId, revieweeId, reviewerIds);
    Assert.assertEquals(reviewComments.size(), 1);

    ReviewComment reviewComment = reviewComments.get(0);
    Assert.assertEquals(reviewComment.getContent(), content);
  }

  @Test
  public void testUpdateInvitationProjectComment() throws Exception {

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

    List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
    ReviewProject reviewProject = reviewProjects.get(0);
    long projectId = reviewProject.getProjectId();

    String content = "The project is excellent";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.PROJECT.getCode());
    reviewComment.setItemId(projectId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    long commentId = reviewCommentService.insertReviewComment(reviewComment);

    String newContent = "The project is superb";
    VoidDTO updateResult = reviewInvitationProjectFacade.updateInvitationProjectComment(orgId, invitationId,
        commentId, managerUserId, newContent, reviewerId, adminUserId);
    Assert.assertEquals(updateResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<Long> reviewerIds = new ArrayList<>();
    reviewerIds.add(reviewerId);
    List<ReviewComment> reviewComments = reviewCommentService.listReviewItemCommentOfReviewers(orgId, templateId,
        ReviewItemType.PROJECT.getCode(), projectId, revieweeId, reviewerIds);
    Assert.assertEquals(reviewComments.size(), 1);

    ReviewComment reviewComment = reviewComments.get(0);
    Assert.assertEquals(reviewComment.getContent(), newContent);
  }

  @Test
  public void testUpdateInvitationProjectComment1() throws Exception {

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

    List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
    ReviewProject reviewProject = reviewProjects.get(0);
    long projectId = reviewProject.getProjectId();

    String content = "The project is excellent";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.PROJECT.getCode());
    reviewComment.setItemId(projectId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    long commentId = reviewCommentService.insertReviewComment(reviewComment);

    String newContent = "The project is superb";
    VoidDTO updateResult = reviewInvitationProjectFacade.updateInvitationProjectComment(orgId, invitationId,
            commentId, reviewerId, newContent, reviewerId, adminUserId);
    Assert.assertEquals(updateResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<Long> reviewerIds = new ArrayList<>();
    reviewerIds.add(reviewerId);
    List<ReviewComment> reviewComments = reviewCommentService.listReviewItemCommentOfReviewers(orgId, templateId,
            ReviewItemType.PROJECT.getCode(), projectId, revieweeId, reviewerIds);
    Assert.assertEquals(reviewComments.size(), 1);

    ReviewComment reviewComment = reviewComments.get(0);
    Assert.assertEquals(reviewComment.getContent(), newContent);
  }

  @Test
  public void testDeleteInvitationProjectComment() throws Exception {

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

    List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
    ReviewProject reviewProject = reviewProjects.get(0);

    long projectId = reviewProject.getProjectId();

    String content = "The project is excellent";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.PROJECT.getCode());
    reviewComment.setItemId(projectId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    long commentId = reviewCommentService.insertReviewComment(reviewComment);

    VoidDTO deleteResult = reviewInvitationProjectFacade.deleteInvitationProjectComment(orgId, invitationId,
        commentId, managerUserId, reviewerId, adminUserId);
    Assert.assertEquals(deleteResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<Long> reviewerIds = new ArrayList<>();
    reviewerIds.add(reviewerId);
    List<ReviewComment> reviewComments = reviewCommentService.listReviewItemCommentOfReviewers(orgId, templateId,
        ReviewItemType.PROJECT.getCode(), projectId, revieweeId, reviewerIds);
    Assert.assertEquals(reviewComments.size(), 0);
  }

  @Test
  public void testDeleteInvitationProjectComment1() throws Exception {

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

    List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
    ReviewProject reviewProject = reviewProjects.get(0);

    long projectId = reviewProject.getProjectId();

    String content = "The project is excellent";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.PROJECT.getCode());
    reviewComment.setItemId(projectId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    long commentId = reviewCommentService.insertReviewComment(reviewComment);

    VoidDTO deleteResult = reviewInvitationProjectFacade.deleteInvitationProjectComment(orgId, invitationId,
            commentId, reviewerId, reviewerId, adminUserId);
    Assert.assertEquals(deleteResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<Long> reviewerIds = new ArrayList<>();
    reviewerIds.add(reviewerId);
    List<ReviewComment> reviewComments = reviewCommentService.listReviewItemCommentOfReviewers(orgId, templateId,
            ReviewItemType.PROJECT.getCode(), projectId, revieweeId, reviewerIds);
    Assert.assertEquals(reviewComments.size(), 0);
  }

  @Test
  public void testGetInvitationProjectDetail() throws Exception {

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

    List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
    ReviewProject reviewProject = reviewProjects.get(0);

    long projectId = reviewProject.getProjectId();

    String content = "The project is excellent";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.PROJECT.getCode());
    reviewComment.setItemId(projectId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    reviewCommentService.insertReviewComment(reviewComment);

    ReviewProjectDetailDTO reviewProjectDetailDTO =
        reviewInvitationProjectFacade.getInvitationProjectDetail(orgId, invitationId, projectId, managerUserId, reviewerId, adminUserId);
    Assert.assertEquals(reviewProjectDetailDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ReviewCommentDTO reviewCommentDTO = reviewProjectDetailDTO.getReviewerComment();
    Assert.assertEquals(reviewCommentDTO.getContent(), content);

  }

  @Test
  public void testGetInvitationProjectDetail1() throws Exception {

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

    List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
    ReviewProject reviewProject = reviewProjects.get(0);

    long projectId = reviewProject.getProjectId();

    String content = "The project is excellent";
    reviewComment.setRevieweeId(revieweeId);
    reviewComment.setReviewerId(reviewerId);
    reviewComment.setItemType(ReviewItemType.PROJECT.getCode());
    reviewComment.setItemId(projectId);
    reviewComment.setContent(content);
    reviewComment.setLastModifiedUserId(reviewerId);
    reviewCommentService.insertReviewComment(reviewComment);

    ReviewProjectDetailDTO reviewProjectDetailDTO =
            reviewInvitationProjectFacade.getInvitationProjectDetail(orgId, invitationId, projectId, reviewerId, reviewerId, adminUserId);
    Assert.assertEquals(reviewProjectDetailDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ReviewCommentDTO reviewCommentDTO = reviewProjectDetailDTO.getReviewerComment();
    Assert.assertEquals(reviewCommentDTO.getContent(), content);

  }

}