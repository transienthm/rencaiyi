package hr.wozai.service.user.server.dao.survey;

import hr.wozai.service.user.server.model.survey.SurveyItem;
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
@Repository("surveyItemDao")
public class SurveyItemDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.survey.SurveyItemMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  public long insertSurveyItem(SurveyItem surveyItem) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertSurveyItem", surveyItem);
    return surveyItem.getSurveyItemId();
  }

  public int deleteSurveyItemByPrimaryKey(long orgId, long surveyItemId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("surveyItemId", surveyItemId);
    params.put("actorUserId", actorUserId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "deleteSurveyItemByPrimaryKey", params);
  }

  public int updateSurveyItem(SurveyItem surveyItem) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateSurveyItem", surveyItem);
  }

  public SurveyItem findSurveyItemByPrimaryKey(long orgId, long surveyItemId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("surveyItemId", surveyItemId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findSurveyItemByPrimaryKey", params);
  }

  public List<SurveyItem> listSurveyItemsByOrgIdAndItemIds(long orgId, List<Long> surveyItemIds) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("surveyItemIds", surveyItemIds);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listSurveyItemsByOrgIdAndItemIds", params);
  }

  public List<SurveyItem> listAvailableSurveyItemsByOrgIdAndTimestamp(
          long orgId, long timestamp, int pageNumber, int pageSize) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("timestamp", timestamp);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listAvailableSurveyItemsByOrgIdAndTimestamp", params);
  }

  public int countSurveyItemsByOrgId(long orgId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "countSurveyItemsByOrgId", params);
  }

  public List<SurveyItem> listSamePeriodSurveyItemsByOrgId(long orgId, long startTime, long endTime) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("startTime", startTime);
    params.put("endTime", endTime);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listSamePeriodSurveyItemsByOrgId", params);
  }

}
