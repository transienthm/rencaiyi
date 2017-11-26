package hr.wozai.service.review.server.test.service;

import hr.wozai.service.review.client.enums.ReviewTemplateStatus;
import hr.wozai.service.review.server.model.*;
import hr.wozai.service.review.server.service.*;
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
 * @Created: 2016-03-03
 */
public class ReviewTemplateServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewTemplateServiceImplTest.class);

  @Autowired
  private ReviewTemplateService reviewTemplateService;

  @Autowired
  private ReviewProjectService reviewProjectService;

  @Autowired
  private ReviewQuestionService reviewQuestionService;

  private long orgId = 100L;
  private long userId = 21L;

  private String templateName = "2016First";

  private ReviewTemplate reviewTemplate;
  private String question1 = "First Q";
  private String question2 = "Second Q";

  long mockOrgId = 19999999L;
  long mockReviewTemplateId = 29999999L;
  long mockTeamId = 39999999L;

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
    questions.add(question2);
    reviewTemplate.setQuestions(questions);
  }

  @Test
  public void testInsertReviewTemplate() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 2);

    ReviewQuestion reviewQuestion = reviewQuestions.get(0);
    Assert.assertEquals(reviewQuestion.getName(), question1);
  }

  @Test
  public void testBatchInsertReviewInvitedTeam() {

    // prepare

    int teamCount = 10;
    List<ReviewInvitedTeam> reviewInvitedTeams = new ArrayList<>();
    for (int i = 0; i < teamCount; i++) {
      ReviewInvitedTeam reviewInvitedTeam = new ReviewInvitedTeam();
      reviewInvitedTeam.setOrgId(mockOrgId);
      reviewInvitedTeam.setReviewTemplateId(mockReviewTemplateId);
      reviewInvitedTeam.setTeamId(mockTeamId + i);
      reviewInvitedTeams.add(reviewInvitedTeam);
    }
    reviewTemplateService.batchInsertReviewInvitedTeam(reviewInvitedTeams);

    // verify

    List<ReviewInvitedTeam> insertedReviewInvitedTeams =
        reviewTemplateService.listReviewInvitedTeam(mockOrgId, mockReviewTemplateId);
    Assert.assertEquals(teamCount, insertedReviewInvitedTeams.size());

  }

  @Test
  public void testListReviewTemplate() throws Exception {

    reviewTemplateService.insertReviewTemplate(reviewTemplate);

    List<Integer> statuses = new ArrayList<>();
    for (ReviewTemplateStatus reviewTemplateStatus: ReviewTemplateStatus.values()) {
      statuses.add(reviewTemplateStatus.getCode());
    }
    List<ReviewTemplate> reviewTemplates = reviewTemplateService.listReviewTemplate(orgId,  1, 20, statuses);
    Assert.assertEquals(reviewTemplates.size(), 1);

    List<String> reviewQuestions = reviewTemplates.get(0).getQuestions();
    Assert.assertEquals(reviewQuestions.size(), 2);
  }

  @Test
  public void testUpdateReviewTemplate() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    List<ReviewQuestion> reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 2);

    ReviewTemplate reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

    String templateName = "New name";
    reviewTemplate.setTemplateName(templateName);

    List<String> newQuestions = new ArrayList<>();
    String newQuestion = "New Q";
    newQuestions.add(newQuestion);
    reviewTemplate.setQuestions(newQuestions);
    reviewTemplate.setLastModifiedUserId(userId);

    reviewTemplateService.updateReviewTemplate(reviewTemplate);
    reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, templateId);

    Assert.assertEquals(reviewTemplate.getTemplateName(), templateName);

    reviewQuestions = reviewQuestionService.listReviewQuestion(orgId, templateId);
    Assert.assertEquals(reviewQuestions.size(), 1);
    Assert.assertEquals(reviewQuestions.get(0).getName(), newQuestion);
  }

  @Test
  public void testCancelReviewTemplate() throws Exception {

    long reviewTemplateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);
    long lastModifiedUserId = reviewTemplate.getLastModifiedUserId();

    reviewTemplateService.cancelReviewTemplate(orgId, reviewTemplateId, lastModifiedUserId);

    reviewTemplate = reviewTemplateService.findReviewTemplate(orgId, reviewTemplateId);
    Assert.assertEquals(reviewTemplate.getState().intValue(), ReviewTemplateStatus.CANCELED.getCode());

  }

  @Test
  public void testReivewProject() {
    try {
      reviewProjectService.findReviewProject(0, 0);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testReviewQuestion() {
    try {
      reviewQuestionService.findReviewQuestion(0, 0);
    } catch (Exception e) {
      e.printStackTrace();
    }

    try {
      ReviewQuestion reviewQuestion = new ReviewQuestion();
      reviewQuestion.setOrgId(0L);
      reviewQuestion.setTemplateId(0L);
      reviewQuestion.setName("Name");
      reviewQuestion.setIsDeleted(0);
      reviewQuestion.setExtend(null);
      reviewQuestion.setCreatedTime(0L);
      reviewQuestion.setLastModifiedTime(0L);
      reviewQuestion.setLastModifiedUserId(0L);
      long id = reviewQuestionService.insertReviewQuestion(reviewQuestion);
      reviewQuestionService.findReviewQuestion(0, id);
    } catch (Exception e) {
      e.printStackTrace();
    }

    reviewQuestionService.updateReviewQuestion(new ReviewQuestion());
    reviewQuestionService.deleteReviewQuestion(0, 0, 0);
    reviewQuestionService.deleteReviewQuestionOfTemplate(0, 0, 0);
  }

  @Test
  public void testReivewTemplate() {
    try {
      reviewTemplateService.batchInsertReviewInvitedTeam(null);
    } catch (Exception e) {
      e.printStackTrace();
    }

    reviewTemplateService.listActiveReviewTemplate();
    reviewTemplateService.finishReviewTemplate(0, 0);
    reviewTemplateService.listReviewTemplateByTemplateIds(0, null);
    reviewTemplateService.listAllValidReviewTemplates(0);
  }
}