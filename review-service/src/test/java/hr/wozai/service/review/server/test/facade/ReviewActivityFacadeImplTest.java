package hr.wozai.service.review.server.test.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.review.client.dto.*;
import hr.wozai.service.review.client.facade.ReviewActivityDetailFacade;
import hr.wozai.service.review.client.facade.ReviewActivityFacade;
import hr.wozai.service.user.client.userorg.dto.ReportLineDTO;
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
public class ReviewActivityFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewTemplateFacadeImplTest.class);

  @Autowired
  private ReviewActivityFacade reviewActivityFacade;

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

  private long actorUserId = 0L;
  private long adminUserId = 0L;

  private long orgId = 99L;
  private long userId = 21L;

  private long revieweeId = 54L;
  private long reviewerId = 55L;

  private String templateName = "2016First";

  private String question1 = "First Q";

  private ReviewTemplate reviewTemplate;

  private ReviewActivity reviewActivity;

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
  }


  private void prepareData(long templateId) {

    reviewActivity = new ReviewActivity();
    reviewActivity.setOrgId(orgId);
    reviewActivity.setTemplateId(templateId);
    reviewActivity.setRevieweeId(revieweeId);
    reviewActivity.setLastModifiedUserId(userId);
  }
  @Test
  public void testBatchInsertReviewActivities() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    List<Long> userIds = new ArrayList<>();
    userIds.add(revieweeId);
    List<ReportLineDTO> reportLineDTOs = new ArrayList<>();
    ReportLineDTO reportLineDTO = new ReportLineDTO();
    reportLineDTO.setUserId(revieweeId);
    reportLineDTO.setReportUserId(reviewerId);
    reportLineDTOs.add(reportLineDTO);

    VoidDTO insertResult = reviewActivityFacade.batchInsertReviewActivities(orgId, templateId,
            reportLineDTOs, actorUserId, adminUserId);
    //Assert.assertEquals(insertResult.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    ReviewActivity reviewActivity = reviewActivityService.findReviewActivityByRevieweeId(orgId, templateId, revieweeId);
    Assert.assertNotNull(reviewActivity);
    Assert.assertEquals(reviewActivity.getRevieweeId().longValue(), revieweeId);

    Assert.assertNotNull(reviewInvitationService.findManagerInvitation(orgId, templateId, revieweeId));

    ReviewInvitedUserListDTO result = reviewActivityDetailFacade.getReviewActivityInvitation(orgId,
            reviewActivity.getActivityId(), -1L, revieweeId, -1L);
    System.out.println(result);

  }

  @Test
  public void testFindReviewActivity() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);
    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    ReviewActivityDTO reviewActivityDTO = reviewActivityFacade.findReviewActivity(orgId, activityId, actorUserId, adminUserId);
    Assert.assertEquals(reviewActivityDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    Assert.assertEquals(reviewActivityDTO.getRevieweeId().longValue(), revieweeId);
  }

  @Test
  public void testFindReviewActivityByTemplateIdAndUserId() throws Exception {

    // prepare
    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);
    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);
    prepareData(templateId);
    reviewActivityService.insertReviewActivity(reviewActivity);

    ReviewActivityDTO reviewActivityDTO = reviewActivityFacade
        .findReviewActivityByTemplateIdAndUserId(orgId, templateId, revieweeId, actorUserId, adminUserId);
    Assert.assertEquals(reviewActivityDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(reviewActivityDTO.getRevieweeId().longValue(), revieweeId);

  }

  @Test
  public void testListUnSubmittedReviewActivity() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    List<ReviewActivity> oldActivities = reviewActivityService.listUnSubmittedReviewActivity(orgId, revieweeId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    ReviewActivityListDTO reviewActivityListDTO =
        reviewActivityFacade.listUnSubmittedReviewActivity(orgId, revieweeId, revieweeId, adminUserId);
    Assert.assertEquals(reviewActivityListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<ReviewActivityDTO> reviewActivityDTOs = reviewActivityListDTO.getReviewActivityDTOs();
    Assert.assertEquals(reviewActivityDTOs.size(), oldActivities.size() + 1);

    ReviewActivityDTO reviewActivityDTO = reviewActivityDTOs.get(0);
    Assert.assertEquals(reviewActivityDTO.getActivityId().longValue(), activityId);
  }

  @Test
  public void testListOtherReviewActivity() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    reviewTemplateService.cancelReviewTemplate(orgId, templateId, userId);

    cancelReviewActivities(orgId, templateId, userId, userId, adminUserId);


    ReviewActivityListDTO reviewActivityListDTO =
        reviewActivityFacade.listOtherReviewActivity(orgId, revieweeId, 1, 5, revieweeId, adminUserId);
    Assert.assertEquals(reviewActivityListDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    List<ReviewActivityDTO> reviewActivityDTOs = reviewActivityListDTO.getReviewActivityDTOs();
    Assert.assertEquals(reviewActivityDTOs.size(), 1);

    ReviewActivityDTO reviewActivityDTO = reviewActivityDTOs.get(0);
    Assert.assertEquals(reviewActivityDTO.getActivityId().longValue(), activityId);
    Assert.assertEquals(reviewActivityDTO.getIsCanceled().intValue(), 1);
  }

  @Test
  public void testCountOtherReviewActivity() throws Exception {

    long templateId = reviewTemplateService.insertReviewTemplate(reviewTemplate);

    reviewTemplateService.publishReviewTemplate(orgId, templateId, userId);

    prepareData(templateId);

    //insert activity
    long activityId = reviewActivityService.insertReviewActivity(reviewActivity);

    reviewTemplateService.cancelReviewTemplate(orgId, templateId, userId);

    cancelReviewActivities(orgId, templateId, userId, userId, adminUserId);

    LongDTO amount = reviewActivityFacade.countOtherReviewActivity(orgId, revieweeId,
        revieweeId, adminUserId);
    Assert.assertEquals(amount.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    Assert.assertEquals(amount.getData(), 1);
  }


  private void cancelReviewActivities(long orgId, long templateId,
                                      long lastModifiedUserId,
                                      long actorUserId, long adminUserId) {

    // Cancel all activity
    List<ReviewActivity> reviewActivities =
        reviewActivityService.listUnCanceledReviewActivityOfTemplate(orgId, templateId);

    for(ReviewActivity reviewActivity: reviewActivities) {

      reviewActivity.setIsCanceled(1);
      reviewActivity.setLastModifiedUserId(lastModifiedUserId);
      reviewActivityService.updateReviewActivity(reviewActivity);

      long revieweeId = reviewActivity.getRevieweeId();
      boolean isSubmitted = reviewActivity.getIsSubmitted() == 1;

      if( !isSubmitted ) {
        reviewCommentService.deleteReviewCommentByReviewer(orgId, templateId,
            revieweeId, revieweeId, lastModifiedUserId);
      }
    }
  }

}