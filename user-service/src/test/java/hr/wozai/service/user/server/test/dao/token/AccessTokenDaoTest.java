package hr.wozai.service.user.server.test.dao.token;

import hr.wozai.service.user.server.dao.token.AccessTokenDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.token.AccessToken;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/30
 */
public class AccessTokenDaoTest extends TestBase {
  @Autowired
  AccessTokenDao accessTokenDao;

  @Test
  public void testAll() throws Exception {
    AccessToken accessToken = new AccessToken();
    String accessTokenKey = "a";
    String accessTokenValue = "b";
    accessToken.setAccessTokenKey(accessTokenKey);
    accessToken.setAccessTokenValue(accessTokenValue);
    long accessTokenId = accessTokenDao.insertAccessToken(accessToken);
    Assert.assertNotNull(accessTokenId);

    AccessToken inDb = accessTokenDao.findAccessToken(accessTokenId);
    Assert.assertEquals(accessTokenKey, inDb.getAccessTokenKey());
    Assert.assertEquals(accessTokenValue, inDb.getAccessTokenValue());

    accessTokenDao.deleteAccessTokenByKeyAndValue(accessTokenKey, accessTokenValue);
    inDb = accessTokenDao.findAccessToken(accessTokenId);
    Assert.assertNull(inDb);

    AccessToken accessToken2 = new AccessToken();
    accessToken2.setAccessTokenKey("a");
    accessToken2.setAccessTokenValue("b");

    AccessToken accessToken3 = new AccessToken();
    accessToken3.setAccessTokenKey("a");
    accessToken3.setAccessTokenValue("c");

    long id2 = accessTokenDao.insertAccessToken(accessToken2);
    long id3 = accessTokenDao.insertAccessToken(accessToken3);

    accessTokenDao.deleteByAccessTokenKey("a");
    Assert.assertNull(accessTokenDao.findAccessToken(id2));
    Assert.assertNull(accessTokenDao.findAccessToken(id3));
  }
}