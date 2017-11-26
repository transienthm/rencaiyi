package hr.wozai.service.user.server.dao.survey;

import hr.wozai.service.user.server.model.survey.SurveyConfig;
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
@Repository("surveyConfigDao")
public class SurveyConfigDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.survey.SurveyConfigMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  public long insertSurveyConfig(SurveyConfig surveyConfig) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertSurveyConfig", surveyConfig);
    return surveyConfig.getSurveyConfigId();
  }

  public SurveyConfig findSurveyConfigByOrgId(long orgId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findSurveyConfigByOrgId", params);
  }

  public int updateSurveyConfig(SurveyConfig surveyConfig) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateSurveyConfig", surveyConfig);
  }
}
