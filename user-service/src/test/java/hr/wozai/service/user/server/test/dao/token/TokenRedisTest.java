package hr.wozai.service.user.server.test.dao.token;

import hr.wozai.service.user.server.UserServerApplication;
import hr.wozai.service.user.server.dao.token.TokenRedis;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = UserServerApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port:8811")
//@Transactional(value = "transactionManager")
public class TokenRedisTest{
  @Autowired
  TokenRedis tokenRedis;

  @Test
  public void testAccessToken() throws Exception {
    String accessTokenKey = "accessTokenKey";
    String accessTokenValue = "access token value";
    tokenRedis.addAccessToken(accessTokenKey, accessTokenValue);

    boolean isInDb = tokenRedis.isAccessTokenInDb(accessTokenKey, accessTokenValue);
    Assert.assertTrue(isInDb);

    tokenRedis.deleteOneAccessToken(accessTokenKey, accessTokenValue);
    Assert.assertFalse(tokenRedis.isAccessTokenInDb(accessTokenKey, accessTokenValue));
  }

  @Test
  public void testRefreshToken() throws Exception {
    String refreshTokenKey = "refreshTokenKey";
    String refreshTokenValue = "refreshTokenValue";

    tokenRedis.addRefreshToken(refreshTokenKey, refreshTokenValue);
    String value = tokenRedis.getRefreshTokenByKey(refreshTokenKey);
    Assert.assertEquals(refreshTokenValue, value);

    tokenRedis.deleteKey(refreshTokenKey);
  }

  @Test
  public void testTemporaryKey() throws Exception {
    String tempKey = "tempKey";
    String tempValue = "tempValue";

    tokenRedis.addTemporaryToken(tempKey, tempValue);

    boolean isInDb = tokenRedis.isAccessTokenInDb(tempKey, tempValue);
    Assert.assertTrue(isInDb);

    tokenRedis.deleteKey(tempKey);
    isInDb = tokenRedis.isAccessTokenInDb(tempKey, tempValue);
    Assert.assertFalse(isInDb);
  }
}