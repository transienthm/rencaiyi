package hr.wozai.service.user.server.dao.token;

import hr.wozai.service.user.server.model.token.AccessToken;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/28
 */
@Repository("accessTokenDao")
public class AccessTokenDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.token.AccessTokenMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  public long insertAccessToken(AccessToken accessToken) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertAccessToken", accessToken);
    return accessToken.getAccessTokenId();
  }

  public int deleteAccessTokenByKeyAndValue(String accessTokenKey, String accessTokenValue) {
    Map<String, Object> params = new HashMap<>();
    params.put("accessTokenKey", accessTokenKey);
    params.put("accessTokenValue", accessTokenValue);
    int result = sqlSessionTemplate.update(BASE_PACKAGE + "deleteAccessTokenByKeyAndValue", params);
    return result;
  }

  public void deleteByAccessTokenKey(String accessTokenKey) {
    Map<String, Object> params = new HashMap<>();
    params.put("accessTokenKey", accessTokenKey);
    sqlSessionTemplate.update(BASE_PACKAGE + "deleteByAccessTokenKey", params);
  }

  public AccessToken findAccessToken(long accessTokenId) {
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findAccessToken", accessTokenId);
  }
}
