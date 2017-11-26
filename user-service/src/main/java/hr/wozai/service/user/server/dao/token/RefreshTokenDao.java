package hr.wozai.service.user.server.dao.token;

import hr.wozai.service.user.server.model.okr.Objective;
import hr.wozai.service.user.server.model.token.RefreshToken;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.lang.annotation.Repeatable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/28
 */
@Repository("refreshTokenDao2")
public class RefreshTokenDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.token.RefreshTokenMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  public long insertRefreshToken(RefreshToken refreshToken) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertRefreshToken", refreshToken);
    return refreshToken.getRefreshTokenId();
  }

  public int deleteByRefreshTokenKey(String refreshTokenKey) {
    Map<String, Object> params = new HashMap<>();
    params.put("refreshTokenKey", refreshTokenKey);
    int result = sqlSessionTemplate.update(BASE_PACKAGE + "deleteByRefreshTokenKey", params);
    return result;
  }

  public RefreshToken findRefreshToken(long refreshTokenId) {
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findRefreshToken", refreshTokenId);
  }
}
