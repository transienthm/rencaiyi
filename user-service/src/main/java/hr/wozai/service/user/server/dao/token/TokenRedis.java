package hr.wozai.service.user.server.dao.token;

import hr.wozai.service.user.server.model.userorg.Org;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/28
 */
@Repository("tokenRedis")
public class TokenRedis {
  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  private SetOperations<String, String> setOperations;

  private ValueOperations<String, String> valueOperations;


  @PostConstruct
  public void init() {
    redisTemplate.setEnableTransactionSupport(true);
    setOperations = redisTemplate.opsForSet();
    valueOperations = redisTemplate.opsForValue();
  }

  public void addAccessToken(String key, String accessToken) {
    setOperations.add(key, accessToken);
  }

  public void deleteOneAccessToken(String key, String accessToken) {
    setOperations.remove(key, accessToken);
  }

  public boolean isAccessTokenInDb(String key, String accessToken) {
    return setOperations.isMember(key, accessToken);
  }

  public void addRefreshToken(String key, String refreshToken) {
    valueOperations.set(key, refreshToken);
  }

  public String getRefreshTokenByKey(String refreshTokenKey) {
    return valueOperations.get(refreshTokenKey);
  }

  public void deleteKey(String key) {
    redisTemplate.delete(key);
  }

  public void addTemporaryToken(String key, String tokenValue) {
    setOperations.add(key, tokenValue);
  }
}
