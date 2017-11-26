package hr.wozai.service.user.server.test.dao.survey;

import hr.wozai.service.user.server.dao.survey.SurveyActivityDao;
import hr.wozai.service.user.server.model.survey.SurveyActivity;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/28
 */
public class SurveyActivityDaoTest extends TestBase {
  @Autowired
  private SurveyActivityDao surveyActivityDao;

  private long orgId = 199L;
  private long userId = 199L;
  private SurveyActivity surveyActivity;

  @Before
  public void setUp() throws Exception {
    surveyActivity = new SurveyActivity();
    surveyActivity.setOrgId(orgId);
    surveyActivity.setCreatedUserId(userId);
    surveyActivity.setCreatedTime(1000L);
  }

  @Test
  public void testInsertSurveyActivity() throws Exception {
    long activityId = surveyActivityDao.insertSurveyActivity(surveyActivity);
    Assert.assertNotEquals(0, activityId);

    SurveyActivity inDb = surveyActivityDao.getSurveyActivityByOrgIdAndPrimaryKey(orgId, activityId);
    Assert.assertNotNull(inDb);

    List<SurveyActivity> surveyActivities = surveyActivityDao.listSurveyActivityByOrgId(orgId, 1, Integer.MAX_VALUE);
    Assert.assertEquals(1, surveyActivities.size());

    surveyActivities = surveyActivityDao.listSurveyActivityByOrgIdAndStartTimeAndEndTime(orgId, 1, Long.MAX_VALUE);
    Assert.assertEquals(1, surveyActivities.size());
  }
}