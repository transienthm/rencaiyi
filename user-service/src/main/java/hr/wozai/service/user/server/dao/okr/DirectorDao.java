package hr.wozai.service.user.server.dao.okr;

import hr.wozai.service.user.server.model.okr.Director;
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
@Repository("directorDao")
public class DirectorDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.okr.DirectorMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;


  @LogAround
  public int batchInsertDirector(List<Director> directors) {
    if (null == directors || directors.isEmpty()) {
      return 0;
    }
    int result = sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertDirector", directors);
    return result;
  }

  @LogAround
  public List<Director> listDirectorByTypeAndObjectId(long orgId, int type, long objectId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("type", type);
    map.put("objectId", objectId);
    List<Director> directors = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listDirectorByTypeAndObjectId", map);
    return directors;
  }

  @LogAround
  public int batchDeleteDirectorByTypeAndObjectId(long orgId, int type,
                                                  List<Long> objectIds, long lastModifiedUserId) {
    if (objectIds == null || objectIds.size() == 0) {
      return 0;
    }
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("type", type);
    map.put("objectIds", objectIds);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
            "batchDeleteDirectorByTypeAndObjectId", map);
    return result;
  }

  public List<Director> listDirectorsByObjectIds(long orgId, int directorType, List<Long> objectIds) {
    if (CollectionUtils.isEmpty(objectIds)) {
      return new ArrayList<>();
    }

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("directorType", directorType);
    map.put("objectIds", objectIds);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listDirectorsByObjectIds", map);
  }

  public List<Long> listObjectiveAndKeyResultDirectorsByObjectiveId(long orgId, long objectiveId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("objectiveId", objectiveId);

    return sqlSessionTemplate.selectList("listObjectiveAndKeyResultDirectorsByObjectiveId", map);
  }
}
