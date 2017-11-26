package hr.wozai.service.user.server.dao.okr;

import hr.wozai.service.user.server.model.okr.Objective;
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
@Repository("objectiveDao")
public class ObjectiveDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.okr.ObjectiveMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;


  @LogAround
  public long insertObjective(Objective objective) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertObjective", objective);
    return objective.getObjectiveId();
  }

  @LogAround
  public Objective findObjective(long orgId, long objectiveId, int forUpdate) {
    Map map = new HashMap();
    map.put("objectiveId", objectiveId);
    map.put("orgId", orgId);
    map.put("forUpdate", forUpdate);
    Objective objective = sqlSessionTemplate.selectOne(BASE_PACKAGE +
            "findObjective", map);
    return objective;
  }

  @LogAround
  public List<Objective> listObjectivesByStartAndEndOrderIndex(
          long orgId, long objectivePeriodId, int startOrderIndex, int endOrderIndex) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("objectivePeriodId", objectivePeriodId);
    map.put("startOrderIndex", startOrderIndex);
    map.put("endOrderIndex", endOrderIndex);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listObjectivesByStartAndEndOrderIndex", map);
  }

  /**
   * 1. 获取某个季度的个人 团队 公司目标
   * @param orgId
   * @param type
   * @param ownerId
   * @param objectivePeriodId
   * @param progressStatus
   * @param orderBy
   * @return
   */
  @LogAround
  public List<Objective> listObjectiveByTypeAndOwnerIdAndQuarterId(
          long orgId, int type, long ownerId, long objectivePeriodId, int progressStatus, int orderBy) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("type", type);
    map.put("ownerId", ownerId);
    map.put("objectivePeriodId", objectivePeriodId);
    map.put("progressStatus", progressStatus);
    map.put("orderBy", orderBy);
    List<Objective> objectives = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listObjectiveByTypeAndOwnerIdAndQuarterId", map);
    return objectives;
  }

  @LogAround
  public int updateObjective(Objective objective) {
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
            "updateObjective", objective);
    return result;
  }

  @LogAround
  public int batchUpdateOrderIndexOfObjectives(List<Objective> objectives) {
    if (CollectionUtils.isEmpty(objectives)) {
      return 0;
    }

    return sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateOrderIndexOfObjectives", objectives);
  }

  @LogAround
  public int deleteObjective(long orgId, long objectiveId, long lastModifiedUserId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("objectiveId", objectiveId);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
            "deleteObjective", map);
    return result;
  }

  @LogAround
  public List<Objective> listObjectivesByObjectiveIds(long orgId, List<Long> objectiveIds) {
    List<Objective> result = new ArrayList<>();
    if (objectiveIds == null || objectiveIds.size() == 0) {
      return result;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("objectiveIds", objectiveIds);

    result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listObjectivesByObjectiveIds", params);
    return result;
  }

  @LogAround
  public int getMaxOrderIndexByObjectivePeriod(long orgId, long objectivePeriodId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("objectivePeriodId", objectivePeriodId);

    Integer result = sqlSessionTemplate.selectOne(BASE_PACKAGE + "getMaxOrderIndexByObjectivePeriod", params);
    if (result == null) {
      return 0;
    }
    return result;
  }

  @LogAround
  public List<Objective> searchObjectiveByKeyword(long orgId, String keyword, int type, Long ownerId) {
    keyword = "%" + keyword + "%";
    Map<String, Object> params = new HashMap<>();
    params.put("keyword", keyword);
    params.put("orgId", orgId);
    params.put("type", type);
    params.put("ownerId", ownerId);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "searchObjectiveByKeyword", params);
  }

  public List<Objective> listFirstLevelSubordinateObjectives(long orgId, long parentObjectiveId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("parentObjectiveId", parentObjectiveId);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listFirstLevelSubordinateObjectives", params);
  }

  public List<Objective> listObjectivesByStartAndEndDeadline(long orgId, long startDeadline, long endDeadline) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("startDeadline", startDeadline);
    params.put("endDeadline", endDeadline);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listObjectivesByStartAndEndDeadline", params);
  }

  public List<Objective> listObjectivesByPriorityAndOrderItem(long orgId, int priority, int orderItem) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("priority", priority);
    params.put("orderItem", orderItem);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listObjectivesByPriorityAndOrderItem", params);
  }

  public List<Objective> listObjectivesWithRegularRemindType(long orgId) {
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listObjectivesWithRegularRemindType", orgId);
  }
}
