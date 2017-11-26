package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.enums.SurveyFrequency;
import hr.wozai.service.user.client.survey.enums.SurveyItemAttribute;
import hr.wozai.service.user.server.model.survey.SurveyActivity;
import hr.wozai.service.user.server.model.survey.SurveyConfig;
import hr.wozai.service.user.server.model.survey.SurveyItem;
import hr.wozai.service.user.server.model.survey.SurveyResponse;
import hr.wozai.service.user.server.service.SurveyService;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/29
 */
public class SurveyServiceImplTest extends TestBase {
  @Autowired
  private SurveyService surveyService;

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  private long orgId = 199L;
  private long userId = 199L;
  private String question = "question";
  private String description = "description";
  private String lowLabel = "low";
  private String highLabel = "high";
  private SurveyItem surveyItem;

  @Before
  public void setUp() throws Exception {
    surveyItem = new SurveyItem();
    surveyItem.setOrgId(orgId);
    surveyItem.setCreatedUserId(userId);

  }

  @Test
  public void testSurveyItem() throws Exception {
    long curTs = System.currentTimeMillis();
    long oneDay = 3600 * 24 * 1000;
    // ScaleQuestion
    surveyItem.setSurveyItemType(SurveyItemAttribute.SCALE_QUESTION.getCode());
    surveyItem.setQuestion(question);
    surveyItem.setDescription(description);
    surveyItem.setLowLabel(lowLabel);
    surveyItem.setHighLabel(highLabel);
    surveyItem.setStartTime(curTs + oneDay);
    surveyItem.setEndTime(curTs + 5 * oneDay);
    long scaleItemId = surveyService.insertSurveyItem(surveyItem);
    Assert.assertNotEquals(0, scaleItemId);

    SurveyItem inDb = surveyService.findSurveyItemByPrimaryKey(orgId, scaleItemId);
    Assert.assertNotNull(inDb);

    List<SurveyItem> surveyItems = surveyService.listSurveyItemsByOrgIdAndItemIds(orgId, Arrays.asList(scaleItemId));
    Assert.assertEquals(1, surveyItems.size());

    // BooleanQuestion
    surveyItem.setSurveyItemType(SurveyItemAttribute.BOOLEAN_QUESTION.getCode());
    surveyItem.setQuestion(question);
    surveyItem.setLowLabel(lowLabel);
    surveyItem.setHighLabel(highLabel);
    surveyItem.setStartTime(curTs + oneDay);
    surveyItem.setEndTime(curTs + 6 * oneDay);
    long booleanItemId = surveyService.insertSurveyItem(surveyItem);
    Assert.assertNotEquals(0, booleanItemId);

    // CommonQuestion
    surveyItem.setSurveyItemType(SurveyItemAttribute.COMMON_QUESTION.getCode());
    surveyItem.setQuestion(question);
    surveyItem.setDescription(description);
    surveyItem.setStartTime(curTs + oneDay);
    surveyItem.setEndTime(curTs + 6 * oneDay);
    long commonItemId = surveyService.insertSurveyItem(surveyItem);
    Assert.assertNotEquals(0, commonItemId);

    surveyItems = surveyService.listAvailableSurveyItemsByOrgIdAndTimestamp(
            orgId, curTs + 4 * oneDay, 1, 100);
    Assert.assertEquals(3, surveyItems.size());
    Assert.assertEquals(commonItemId, surveyItems.get(0).getSurveyItemId().longValue());

    int number = surveyService.countSurveyItemsByOrgId(orgId);
    Assert.assertEquals(3, number);

    surveyItems = surveyService.listSamePeriodSurveyItemsByOrgId(orgId, curTs + oneDay, curTs + 6 * oneDay);
    Assert.assertEquals(2, surveyItems.size());

    SurveyItem booleanItem = surveyItems.get(0);
    String update = "update";
    booleanItem.setQuestion(update);
    booleanItem.setLowLabel(update);
    booleanItem.setHighLabel(update);
    booleanItem.setEndTime(curTs + 7 * oneDay);
    surveyService.updateSurveyItem(booleanItem);

    surveyItems = surveyService.listSamePeriodSurveyItemsByOrgId(orgId, curTs + oneDay, curTs + 7 * oneDay);
    Assert.assertEquals(1, surveyItems.size());

    surveyService.deleteSurveyItemByPrimaryKey(orgId, booleanItemId, userId);
    surveyItems = surveyService.listAvailableSurveyItemsByOrgIdAndTimestamp(
            orgId, curTs + 4 * oneDay, 1, 100);
    Assert.assertEquals(2, surveyItems.size());
  }

  @Test
  public void testCreateSurveyItemWithException() {
    long curTs = System.currentTimeMillis();
    long oneDay = 3600 * 24 * 1000;
    // ScaleQuestion
    surveyItem.setSurveyItemType(SurveyItemAttribute.SCALE_QUESTION.getCode());
    surveyItem.setQuestion(question);
    surveyItem.setDescription(description);
    surveyItem.setLowLabel(lowLabel);
    surveyItem.setHighLabel(highLabel);
    surveyItem.setStartTime(curTs + oneDay);
    surveyItem.setEndTime(curTs + 1 * oneDay);

    thrown.expect(ServiceStatusException.class);
    surveyService.insertSurveyItem(surveyItem);
  }

  @Test
  public void testSurveyConfig() {
    SurveyConfig surveyConfig = new SurveyConfig();
    surveyConfig.setOrgId(orgId);
    surveyConfig.setFrequency(SurveyFrequency.ONE_WEEK.getCode());
    surveyConfig.setCreatedUserId(userId);

    long surConfigId = surveyService.insertSurveyConfig(surveyConfig);
    Assert.assertNotEquals(0, surConfigId);

    SurveyConfig inDb = surveyService.getSurveyConfig(orgId);
    Assert.assertNotNull(inDb);

    inDb.setFrequency(SurveyFrequency.TWO_WEEK.getCode());
    long result = surveyService.updateSurveyConfig(inDb);
    Assert.assertEquals(1L, result);
  }

  @Test
  public void testSurveyActivityAndResponse() {
    long curTs = System.currentTimeMillis();
    long oneDay = 3600 * 24 * 1000;
    // ScaleQuestion
    surveyItem.setSurveyItemType(SurveyItemAttribute.SCALE_QUESTION.getCode());
    surveyItem.setQuestion(question);
    surveyItem.setDescription(description);
    surveyItem.setLowLabel(lowLabel);
    surveyItem.setHighLabel(highLabel);
    surveyItem.setStartTime(curTs + 5000L);
    surveyItem.setEndTime(curTs + 5 * oneDay);
    long scaleItemId = surveyService.insertSurveyItem(surveyItem);
    Assert.assertNotEquals(0, scaleItemId);

    // BooleanQuestion
    surveyItem.setSurveyItemType(SurveyItemAttribute.BOOLEAN_QUESTION.getCode());
    surveyItem.setQuestion(question);
    surveyItem.setLowLabel(lowLabel);
    surveyItem.setHighLabel(highLabel);
    surveyItem.setStartTime(curTs + 5000L);
    surveyItem.setEndTime(curTs + 6 * oneDay);
    long booleanItemId = surveyService.insertSurveyItem(surveyItem);
    Assert.assertNotEquals(0, booleanItemId);

    // CommonQuestion
    surveyItem.setSurveyItemType(SurveyItemAttribute.COMMON_QUESTION.getCode());
    surveyItem.setQuestion(question);
    surveyItem.setDescription(description);
    surveyItem.setStartTime(curTs + 5000L);
    surveyItem.setEndTime(curTs + 6 * oneDay);
    long commonItemId = surveyService.insertSurveyItem(surveyItem);
    Assert.assertNotEquals(0, commonItemId);

    SurveyActivity surveyActivity = new SurveyActivity();
    surveyActivity.setOrgId(orgId);
    surveyActivity.setCreatedTime(curTs + 10000L);
    surveyActivity.setCreatedUserId(userId);

    surveyService.initSurveyActivity(orgId, surveyActivity, Arrays.asList(userId));

    List<SurveyActivity> surveyActivities = surveyService.listSurveyActivities(orgId, 1, Integer.MAX_VALUE);
    Assert.assertEquals(1, surveyActivities.size());

    surveyActivities = surveyService.listSurveyActivityByOrgIdAndStartTimeAndEndTime(orgId, 1L, Long.MAX_VALUE);
    Assert.assertEquals(1, surveyActivities.size());

    long activityId = surveyActivities.get(0).getSurveyActivityId();

    List<SurveyResponse> surveyResponses = surveyService.
            listSurveyResponsesByOrgIdAndActivityIdAndUserId(orgId, userId, activityId);
    Assert.assertEquals(3, surveyResponses.size());
    for (SurveyResponse surveyResponse : surveyResponses) {
      Assert.assertEquals(0, surveyResponse.getIsSubmit().intValue());
      surveyResponse.setResponse(2);
      surveyResponse.setResponseDetail("update");
      surveyResponse.setIsSubmit(1);
    }

    surveyService.batchUpdateSurveyResponses(surveyResponses);

    surveyResponses = surveyService.
            listSurveyResponsesByOrgIdAndActivityIdAndUserId(orgId, userId, activityId);
    Assert.assertEquals(3, surveyResponses.size());
    for (SurveyResponse surveyResponse : surveyResponses) {
      Assert.assertEquals(1, surveyResponse.getIsSubmit().intValue());
      Assert.assertEquals(2, surveyResponse.getResponse().intValue());
      Assert.assertEquals("update", surveyResponse.getResponseDetail());
    }

    int number = surveyService.countSurveyResponseBySurveyItemId(orgId, scaleItemId);
    Assert.assertEquals(1, number);

    surveyResponses = surveyService.listSurveyResponsesByOrgIdAndActivityIds(orgId, Arrays.asList(activityId));
    Assert.assertEquals(3, surveyResponses.size());

    surveyResponses = surveyService.searchResponsesByOrgIdAndActivityIdAndItemId(orgId, activityId, scaleItemId, "", 1, 20);
    Assert.assertEquals(1, surveyResponses.size());

    surveyResponses = surveyService.searchResponsesByOrgIdAndActivityIdAndItemId(orgId, activityId, scaleItemId, "test", 1, 20);
    Assert.assertEquals(0, surveyResponses.size());
  }

  @Test
  public void test() {
    System.out.println(surveyService.listSurveyResponsesByOrgIdAndActivityIds(3, Arrays.asList(31L)));
    System.out.println(surveyService.searchResponsesByOrgIdAndActivityIdAndItemId(3, 31, 70, "", 1, 20));
  }
}