package hr.wozai.service.user.server.test.dao.token;

import hr.wozai.service.user.server.dao.token.RefreshTokenDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.token.RefreshToken;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/30
 */
public class RefreshTokenDaoTest extends TestBase {
  @Autowired
  RefreshTokenDao refreshTokenDao;

  @Test
  public void testAll() throws Exception {
    RefreshToken refreshToken = new RefreshToken();
    String refreshTokenKey = "a";
    String refreshTokenValue = "b";
    refreshToken.setRefreshTokenKey(refreshTokenKey);
    refreshToken.setRefreshTokenValue(refreshTokenValue);
    long refreshTokenId = refreshTokenDao.insertRefreshToken(refreshToken);
    Assert.assertNotNull(refreshTokenId);

    RefreshToken inDb = refreshTokenDao.findRefreshToken(refreshTokenId);
    Assert.assertEquals(refreshTokenKey, inDb.getRefreshTokenKey());
    Assert.assertEquals(refreshTokenValue, inDb.getRefreshTokenValue());

    refreshTokenDao.deleteByRefreshTokenKey(refreshTokenKey);
    inDb = refreshTokenDao.findRefreshToken(refreshTokenId);
    Assert.assertNull(inDb);
  }
}