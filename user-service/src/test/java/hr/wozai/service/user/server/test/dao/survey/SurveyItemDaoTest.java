package hr.wozai.service.user.server.test.dao.survey;

import hr.wozai.service.user.server.dao.survey.SurveyItemDao;
import hr.wozai.service.user.client.survey.enums.SurveyItemAttribute;
import hr.wozai.service.user.server.model.survey.SurveyItem;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/28
 */
public class SurveyItemDaoTest extends TestBase {
  @Autowired
  private SurveyItemDao surveyItemDao;

  private long orgId = 199L;
  private long userId = 199L;
  private SurveyItem surveyItem;

  @Before
  public void setUp() throws Exception {
    surveyItem = new SurveyItem();
    surveyItem.setOrgId(orgId);
    surveyItem.setSurveyItemType(SurveyItemAttribute.SCALE_QUESTION.getCode());
    surveyItem.setQuestion("question");
    surveyItem.setDescription("description");
    surveyItem.setLowLabel("low lable");
    surveyItem.setHighLabel(null);
    surveyItem.setStartTime(0L);
    surveyItem.setEndTime(1000L);
    surveyItem.setCreatedUserId(userId);
    surveyItem.setLastModifiedUserId(userId);
  }

  @Test
  public void testAll() throws Exception {
    long surveyItemId = surveyItemDao.insertSurveyItem(surveyItem);

    SurveyItem inDb = surveyItemDao.findSurveyItemByPrimaryKey(orgId, surveyItemId);
    Assert.assertEquals(orgId, inDb.getOrgId().longValue());
    Assert.assertEquals("question", inDb.getQuestion());
    Assert.assertEquals("description", inDb.getDescription());
    Assert.assertEquals("", inDb.getHighLabel());

    List<SurveyItem> surveyItems = surveyItemDao.listSurveyItemsByOrgIdAndItemIds(orgId, Arrays.asList(surveyItemId));
    Assert.assertEquals(1, surveyItems.size());

    surveyItems = surveyItemDao.listAvailableSurveyItemsByOrgIdAndTimestamp(orgId, 100L, 1, 10);
    Assert.assertEquals(1, surveyItems.size());

    int number = surveyItemDao.countSurveyItemsByOrgId(orgId);
    Assert.assertEquals(1, number);

    surveyItems = surveyItemDao.listSamePeriodSurveyItemsByOrgId(orgId, 0L, 1000L);
    Assert.assertEquals(1, surveyItems.size());

    inDb.setQuestion("update");
    inDb.setDescription("update");
    inDb.setLowLabel("low");
    inDb.setHighLabel("high");
    inDb.setEndTime(2000L);
    surveyItemDao.updateSurveyItem(inDb);

    inDb = surveyItemDao.findSurveyItemByPrimaryKey(orgId, surveyItemId);
    Assert.assertEquals("update", inDb.getQuestion());
    Assert.assertEquals("update", inDb.getDescription());
    Assert.assertEquals("low", inDb.getLowLabel());
    Assert.assertEquals("high", inDb.getHighLabel());
    Assert.assertEquals(2000L, inDb.getEndTime().longValue());

    int result = surveyItemDao.deleteSurveyItemByPrimaryKey(orgId, surveyItemId, userId);
    Assert.assertEquals(1, result);
    inDb = surveyItemDao.findSurveyItemByPrimaryKey(orgId, surveyItemId);
    Assert.assertNull(inDb);
  }
}