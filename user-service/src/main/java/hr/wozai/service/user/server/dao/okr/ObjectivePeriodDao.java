package hr.wozai.service.user.server.dao.okr;

import hr.wozai.service.user.server.model.okr.ObjectivePeriod;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/4
 */
@Repository("objectivePeriodDao")
public class ObjectivePeriodDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.okr.ObjectivePeriodMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;


  @LogAround
  public long insertObjectivePeriod(ObjectivePeriod objectivePeriod) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertObjectivePeriod", objectivePeriod);
    return objectivePeriod.getObjectivePeriodId();
  }

  @LogAround
  public ObjectivePeriod findObjectivePeriod(long orgId, long objectivePeriodId) {
    Map map = new HashMap();
    map.put("objectivePeriodId", objectivePeriodId);
    map.put("orgId", orgId);
    ObjectivePeriod objectivePeriod = sqlSessionTemplate.selectOne(BASE_PACKAGE +
            "findObjectivePeriod", map);
    return objectivePeriod;
  }

  @LogAround
  public ObjectivePeriod findObjectivePeriodByName(long orgId, String name, int type, long ownerId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("name", name);
    map.put("type", type);
    map.put("ownerId", ownerId);

    ObjectivePeriod objectivePeriod= sqlSessionTemplate.selectOne(BASE_PACKAGE +
            "findObjectivePeriodByName", map);
    return objectivePeriod;
  }

  @LogAround
  public List<ObjectivePeriod> listObjectivePeriodByOrgIdAndOwnerId(long orgId, int type, long ownerId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("type", type);
    map.put("ownerId", ownerId);
    List<ObjectivePeriod> objectivePeriods = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listObjectivePeriodByOrgIdAndTypeAndOwnerId", map);
    return objectivePeriods;
  }

  @LogAround
  public int updateObjectivePeriod(ObjectivePeriod objectivePeriod) {
    int result = sqlSessionTemplate.update(BASE_PACKAGE + "updateObjectivePeriod", objectivePeriod);
    return result;
  }

  @LogAround
  public int deleteObjectivePeriod(long orgId, long objectivePeriodId, long lastModifidUserId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("objectivePeriodId", objectivePeriodId);
    map.put("lastModifiedUserId", lastModifidUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
            "deleteObjectivePeriod", map);
    return result;
  }

  public List<ObjectivePeriod> listObjectivePeriodsByOrgIdAndYearAndPeriodSpanIds(
          long orgId, int year, List<Integer> periodSpanIds) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("year", year);
    map.put("periodSpanIds", periodSpanIds);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listObjectivePeriodsByOrgIdAndYearAndPeriodSpanIds", map);
  }
}
