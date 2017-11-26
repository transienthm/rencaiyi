package hr.wozai.service.user.server.dao.survey;

import hr.wozai.service.user.server.model.survey.SurveyResponse;
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
@Repository("surveyResponseDao")
public class SurveyResponseDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.survey.SurveyResponseMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  public int batchInsertSurveyResponse(List<SurveyResponse> surveyResponses) {
    return sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertSurveyResponse", surveyResponses);
  }

  public int batchUpdateSurveyResponse(List<SurveyResponse> surveyResponses) {
    return sqlSessionTemplate.insert(BASE_PACKAGE + "batchUpdateSurveyResponse", surveyResponses);
  }

  public List<SurveyResponse> listSurveyResponsesByOrgIdAndActivityIdAndUserId(long orgId, long userId, long activityId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    params.put("activityId", activityId);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listSurveyResponsesByOrgIdAndActivityIdAndUserId", params);
  }

  public int countSurveyResponseBySurveyItemId(long orgId, long surveyItemId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("surveyItemId", surveyItemId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "countSurveyResponseBySurveyItemId", params);
  }

  public List<SurveyResponse> listSurveyResponsesByOrgIdAndActivityIds(
          long orgId, List<Long> surveyActivityIds) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("surveyActivityIds", surveyActivityIds);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listSurveyResponsesByOrgIdAndActivityIds", params);
  }

  public List<SurveyResponse> searchResponsesByOrgIdAndActivityIdAndItemId(
          long orgId, long surveyActivityId, long surveyItemId, String keyword, int pageNumber, int pageSize) {
    keyword = "%" + keyword + "%";
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("surveyActivityId", surveyActivityId);
    params.put("surveyItemId", surveyItemId);
    params.put("keyword", keyword);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "searchResponsesByOrgIdAndActivityIdAndItemId", params);
  }


}
