package hr.wozai.service.user.server.test.dao.survey;

import hr.wozai.service.user.server.dao.survey.SurveyResponseDao;
import hr.wozai.service.user.client.survey.enums.SurveyItemAttribute;
import hr.wozai.service.user.server.model.survey.SurveyResponse;
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
public class SurveyResponseDaoTest extends TestBase {
  @Autowired
  private SurveyResponseDao surveyResponseDao;

  private long orgId = 199L;
  private long userId = 199L;
  private long surveyItemId = 199L;
  private long surveyActivityId = 199L;
  private SurveyResponse surveyResponse;

  @Before
  public void setUp() throws Exception {
    surveyResponse = new SurveyResponse();
    surveyResponse.setOrgId(orgId);
    surveyResponse.setUserId(userId);
    surveyResponse.setSurveyActivityId(surveyActivityId);
    surveyResponse.setSurveyItemId(surveyItemId);
    surveyResponse.setSurveyItemType(SurveyItemAttribute.SCALE_QUESTION.getCode());
    surveyResponse.setResponse(1);
    surveyResponse.setResponseDetail("detail");
    surveyResponse.setCreatedUserId(userId);
  }

  @Test
  public void testBatchInsertSurveyResponse() throws Exception {
    int result = surveyResponseDao.batchInsertSurveyResponse(Arrays.asList(surveyResponse));
    Assert.assertEquals(1, result);

    List<SurveyResponse> surveyResponseList = surveyResponseDao.listSurveyResponsesByOrgIdAndActivityIdAndUserId(
            orgId, userId, surveyActivityId);
    Assert.assertEquals(1, surveyResponseList.size());

    for (SurveyResponse sr : surveyResponseList) {
      sr.setResponse(2);
      sr.setResponseDetail("update");
      sr.setIsSubmit(1);
    }
    result = surveyResponseDao.batchUpdateSurveyResponse(surveyResponseList);
    Assert.assertEquals(1, result);

    surveyResponseList = surveyResponseDao.listSurveyResponsesByOrgIdAndActivityIdAndUserId(
            orgId, userId, surveyActivityId);
    Assert.assertEquals(1, surveyResponseList.size());

    int number = surveyResponseDao.countSurveyResponseBySurveyItemId(orgId, surveyItemId);
    Assert.assertEquals(1, number);

    surveyResponseList = surveyResponseDao.listSurveyResponsesByOrgIdAndActivityIds(orgId, Arrays.asList(surveyActivityId));
    Assert.assertEquals(1, surveyResponseList.size());

    surveyResponseList = surveyResponseDao.searchResponsesByOrgIdAndActivityIdAndItemId
            (orgId, surveyActivityId, surveyItemId, "", 1, 20);
    Assert.assertEquals(1, surveyResponseList.size());

    surveyResponseList = surveyResponseDao.searchResponsesByOrgIdAndActivityIdAndItemId
            (orgId, surveyActivityId, surveyItemId, "test", 1, 20);
    Assert.assertEquals(0, surveyResponseList.size());
  }
}