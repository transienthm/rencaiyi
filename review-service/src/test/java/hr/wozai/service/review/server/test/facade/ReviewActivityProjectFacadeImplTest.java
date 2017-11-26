package hr.wozai.service.review.server.test.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.review.client.dto.ReviewProjectDTO;
import hr.wozai.service.review.client.dto.ReviewProjectDetailDTO;
import hr.wozai.service.review.client.facade.ReviewActivityProjectFacade;
import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.model.ReviewComment;
import hr.wozai.service.review.server.model.ReviewProject;
import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.review.server.service.*;
import hr.wozai.service.review.server.test.base.TestBase;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-24
 */
public class ReviewActivityProjectFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewActivityProjectFacadeImplTest.class);

  @Autowired
  private ReviewActivityProjectFacade reviewActivityProjectFacade;

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
    reviewTemplate.setState(2);
    reviewTemplate.setIsReviewerAnonymous(0);

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
  public void testInsertProject() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    ReviewProjectDTO reviewProjectDTO = new ReviewProjectDTO();
    reviewProjectDTO.setOrgId(orgId);
    reviewProjectDTO.setTemplateId(templateId);
    reviewProjectDTO.setRevieweeId(revieweeId);
    reviewProjectDTO.setName("DL & DM");
    reviewProjectDTO.setScore(3);
    reviewProjectDTO.setRole("manager");
    reviewProjectDTO.setComment("Excellent Job!");
    reviewProjectDTO.setLastModifiedUserId(revieweeId);

    LongDTO commentIdDTO = reviewActivityProjectFacade.insertProject(orgId, activityId, reviewProjectDTO, revieweeId, adminUserId);
    Assert.assertEquals(commentIdDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
    Assert.assertEquals(reviewProjects.size(), 1);
    ReviewProject reviewProject = reviewProjects.get(0);
    Assert.assertEquals(reviewProject.getName(), reviewProjectDTO.getName());

  }

  @Test
  public void testDeleteProject() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    ReviewProject reviewProject = new ReviewProject();
    reviewProject.setOrgId(orgId);
    reviewProject.setTemplateId(templateId);
    reviewProject.setRevieweeId(revieweeId);
    reviewProject.setName("DL & DM");
    reviewProject.setScore(3);
    reviewProject.setRole("manager");
    reviewProject.setComment("Excellent Job!");
    reviewProject.setLastModifiedUserId(revieweeId);

    long projectId = reviewProjectService.insertReviewProject(reviewProject);

    VoidDTO deleteResult = reviewActivityProjectFacade.deleteProject(orgId, activityId, projectId, revieweeId, adminUserId);
    Assert.assertEquals(deleteResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<ReviewProject> reviewProjects = reviewProjectService.listReviewProject(orgId, templateId, revieweeId);
    Assert.assertEquals(reviewProjects.size(), 0);
  }

  @Test
  public void testUpdateProject() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    ReviewProject reviewProject = new ReviewProject();
    reviewProject.setOrgId(orgId);
    reviewProject.setTemplateId(templateId);
    reviewProject.setRevieweeId(revieweeId);
    reviewProject.setName("DL & DM");
    reviewProject.setScore(3);
    reviewProject.setRole("manager");
    reviewProject.setComment("Excellent Job!");
    reviewProject.setLastModifiedUserId(revieweeId);

    long projectId = reviewProjectService.insertReviewProject(reviewProject);

    ReviewProjectDTO reviewProjectDTO = new ReviewProjectDTO();
    BeanUtils.copyProperties(reviewProject, reviewProjectDTO);

    int newScore = 5;
    reviewProjectDTO.setScore(newScore);

    VoidDTO deleteResult = reviewActivityProjectFacade.updateProject(orgId, activityId, reviewProjectDTO, revieweeId, adminUserId);
    Assert.assertEquals(deleteResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    reviewProject = reviewProjectService.findReviewProject(orgId, projectId);
    Assert.assertEquals(reviewProject.getScore().intValue(), newScore);

  }

  @Test
  public void testGetActivityProjectDetail() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    ReviewProject reviewProject = new ReviewProject();
    reviewProject.setOrgId(orgId);
    reviewProject.setTemplateId(templateId);
    reviewProject.setRevieweeId(revieweeId);
    reviewProject.setName("DL & DM");
    reviewProject.setScore(3);
    reviewProject.setRole("manager");
    reviewProject.setComment("Excellent Job!");
    reviewProject.setLastModifiedUserId(revieweeId);

    long projectId = reviewProjectService.insertReviewProject(reviewProject);

    ReviewProjectDetailDTO reviewProjectDetailDTO =
        reviewActivityProjectFacade.getActivityProjectDetail(orgId, activityId, projectId, revieweeId, adminUserId);
    Assert.assertEquals(reviewProjectDetailDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    Assert.assertEquals(reviewProjectDetailDTO.getComment(), reviewProject.getComment());
  }

  @Test
  public void testGetActivityProjectDetailByHR() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    ReviewProject reviewProject = new ReviewProject();
    reviewProject.setOrgId(orgId);
    reviewProject.setTemplateId(templateId);
    reviewProject.setRevieweeId(revieweeId);
    reviewProject.setName("DL & DM");
    reviewProject.setScore(3);
    reviewProject.setRole("manager");
    reviewProject.setComment("Excellent Job!");
    reviewProject.setLastModifiedUserId(revieweeId);

    long projectId = reviewProjectService.insertReviewProject(reviewProject);

    ReviewProjectDetailDTO reviewProjectDetailDTO =
            reviewActivityProjectFacade.getActivityProjectDetailByHR(orgId, activityId, projectId, revieweeId, adminUserId);
    Assert.assertEquals(reviewProjectDetailDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    Assert.assertEquals(reviewProjectDetailDTO.getComment(), reviewProject.getComment());

  }
}