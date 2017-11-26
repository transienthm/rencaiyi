package hr.wozai.service.user.server.test.dao.survey;

import hr.wozai.service.user.server.dao.survey.SurveyConfigDao;
import hr.wozai.service.user.server.model.survey.SurveyConfig;
import hr.wozai.service.user.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/28
 */
public class SurveyConfigDaoTest extends TestBase {
  @Autowired
  private SurveyConfigDao surveyConfigDao;

  private long orgId = 199L;
  private long userId = 199L;
  private SurveyConfig surveyConfig;

  @Before
  public void setUp() throws Exception {
    surveyConfig = new SurveyConfig();
    surveyConfig.setOrgId(orgId);
    surveyConfig.setFrequency(1);
    surveyConfig.setCreatedUserId(userId);
  }

  @Test
  public void testInsertSurveyConfig() throws Exception {
    long configId = surveyConfigDao.insertSurveyConfig(surveyConfig);

    SurveyConfig inDb = surveyConfigDao.findSurveyConfigByOrgId(orgId);
    Assert.assertEquals(1, inDb.getFrequency().intValue());

    inDb.setFrequency(2);
    int result = surveyConfigDao.updateSurveyConfig(inDb);
    Assert.assertEquals(1, result);
    inDb = surveyConfigDao.findSurveyConfigByOrgId(orgId);
    Assert.assertEquals(2, inDb.getFrequency().intValue());
  }
}