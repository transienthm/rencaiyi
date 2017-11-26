package hr.wozai.service.user.server.dao.okr;

import hr.wozai.service.user.server.model.okr.KeyResult;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/4
 */
@Repository("keyResultDao")
public class KeyResultDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.okr.KeyResultMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;


  @LogAround
  public long insertKeyResult(KeyResult keyResult) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertKeyResult", keyResult);
    return keyResult.getKeyResultId();
  }

  @LogAround
  public KeyResult findKeyResult(long orgId, long keyResultId) {
    Map map = new HashMap();
    map.put("keyResultId", keyResultId);
    map.put("orgId", orgId);
    KeyResult keyResult = sqlSessionTemplate.selectOne(BASE_PACKAGE +
            "findKeyResult", map);
    return keyResult;
  }

  @LogAround
  public List<KeyResult> listKeyResultByObjectiveId(long orgId, long objectiveId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("objectiveId", objectiveId);
    List<KeyResult> keyResults = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listKeyResultByObjectiveId", map);
    return keyResults;
  }

  @LogAround
  public int updateKeyResult(KeyResult keyResult) {
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
            "updateKeyResult", keyResult);
    return result;
  }

  @LogAround
  public int deleteKeyResult(long orgId, long keyResultId, long lastModifiedUserId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("keyResultId", keyResultId);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
            "deleteKeyResult", map);
    return result;
  }

  @LogAround
  public int deleteKeyResultByObjectiveId(long orgId, long objectiveId, long lastModifiedUserId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("objectiveId", objectiveId);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
            "deleteKeyResultByObjectiveId", map);
    return result;
  }

  public List<KeyResult> listSimpleKeyResultsByObjectiveIds(long orgId, List<Long> objectiveIds) {
    List<KeyResult> result = new ArrayList<>();
    if (CollectionUtils.isEmpty(objectiveIds)) {
      return result;
    }
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("objectiveIds", objectiveIds);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listSimpleKeyResultsByObjectiveIds", map);
  }

  public List<KeyResult> listKeyResultsByStartAndEndDeadline(long orgId, long startDeadline, long endDeadline) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("startDeadline", startDeadline);
    params.put("endDeadline", endDeadline);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listKeyResultsByStartAndEndDeadline", params);
  }
}
