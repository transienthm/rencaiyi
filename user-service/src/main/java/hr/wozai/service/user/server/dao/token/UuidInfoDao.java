package hr.wozai.service.user.server.dao.token;

import hr.wozai.service.user.server.model.token.UuidInfo;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/14
 */
@Repository
public class UuidInfoDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.token.UuidInfoMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  public long insertUuidInfo(UuidInfo uuidInfo) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertUuidInfo", uuidInfo);
    return uuidInfo.getUuidInfoId();
  }

 /* public int deleteUuidInfo(long orgId, long uuidInfoId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("uuidInfoId", uuidInfoId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE + "deleteUuidInfo", params);

    return result;
  }*/

  public int deleteUuidInfoByOrgIdAndUserIdAndUsage(long orgId, long userId, int uuidUsage) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    params.put("uuidUsage", uuidUsage);
    int result = sqlSessionTemplate.update(BASE_PACKAGE + "deleteUuidInfoByOrgIdAndUserIdAndUsage", params);

    return result;
  }

  public UuidInfo getUuidInfoByUuid(String uuid) {
    Map<String, Object> params = new HashMap<>();
    params.put("uuid", uuid);
    UuidInfo result = sqlSessionTemplate.selectOne(BASE_PACKAGE + "getUuidInfoByUuid", params);

    return result;
  }

  public List<UuidInfo> listUuidsByOrgIdAndUserIdAndUuidUsage(long orgId, long userId, long uuidUsage, long expireTime) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    params.put("uuidUsage", uuidUsage);
    params.put("expireTime", expireTime);

    List<UuidInfo> result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listUuidsByOrgIdAndUserIdAndUuidUsage", params);
    return result;
  }
}
