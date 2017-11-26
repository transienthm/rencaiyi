package hr.wozai.service.user.server.dao.navigation;

import hr.wozai.service.user.server.model.navigation.Navigation;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/2
 */
@Repository("navigationDao")
public class NavigationDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.navigation.NavigationMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  public long insertNavigation(Navigation navigation) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertNavigation", navigation);
    return navigation.getNavigationId();
  }

  public int updateNavigation(Navigation navigation) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateNavigation", navigation);
  }

  public Navigation findNavigationByOrgIdAndUserId(long orgId, long userId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("userId", userId);

    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findNavigationByOrgIdAndUserId", map);
  }

  public Navigation findNavigationByNaviOrgIdAndNaviUserId(long naviOrgId, long naviUserId) {
    Map map = new HashMap<>();
    map.put("naviOrgId", naviOrgId);
    map.put("naviUserId", naviUserId);

    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findNavigationByNaviOrgIdAndNaviUserId", map);
  }
}
