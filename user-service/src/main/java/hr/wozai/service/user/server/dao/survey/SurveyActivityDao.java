package hr.wozai.service.user.server.dao.survey;

import hr.wozai.service.user.server.model.survey.SurveyActivity;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/25
 */
@Repository("surveyActivityDao")
public class SurveyActivityDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.survey.SurveyActivityMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  public long insertSurveyActivity(SurveyActivity surveyActivity) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertSurveyActivity", surveyActivity);

    return surveyActivity.getSurveyActivityId();
  }

  public SurveyActivity getSurveyActivityByOrgIdAndPrimaryKey(long orgId, long surveyActivityId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("surveyActivityId", surveyActivityId);

    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "getSurveyActivityByOrgIdAndPrimaryKey", params);
  }

  public List<SurveyActivity> listSurveyActivityByOrgId(long orgId, int pageNumber, int pageSize) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listSurveyActivityByOrgId", params);
  }

  public List<SurveyActivity> listSurveyActivityByOrgIdAndStartTimeAndEndTime(
          long orgId, long startTime, long endTime) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("startTime", startTime);
    params.put("endTime", endTime);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listSurveyActivityByOrgIdAndStartTimeAndEndTime", params);
  }
}
