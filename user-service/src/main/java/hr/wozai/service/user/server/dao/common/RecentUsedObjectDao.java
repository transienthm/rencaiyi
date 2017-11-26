package hr.wozai.service.user.server.dao.common;

import hr.wozai.service.user.server.model.common.ContentIndex;
import hr.wozai.service.user.server.model.common.RecentUsedObject;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/22
 */
@Repository("recentUsedObjectDao")
public class RecentUsedObjectDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.common.RecentUsedObjectMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public int insertRecentUsedObject(RecentUsedObject recentUsedObject) {
    return sqlSessionTemplate.insert(BASE_PACKAGE + "insertRecentUsedObject", recentUsedObject);
  }

  public RecentUsedObject getRecentUsedObjectByUserIdAndType(long orgId, long userId, long type) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    params.put("type", type);

    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "getRecentUsedObjectByUserIdAndType", params);
  }

  public int deleteRecentUsedObjectByUserIdAndType(long orgId, long userId, long type) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    params.put("type", type);

    return sqlSessionTemplate.update(BASE_PACKAGE + "deleteRecentUsedObjectByUserIdAndType", params);
  }
}
