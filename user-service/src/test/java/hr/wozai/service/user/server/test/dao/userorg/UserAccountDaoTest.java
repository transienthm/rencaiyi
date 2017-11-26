package hr.wozai.service.user.server.test.dao.userorg;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.user.server.dao.userorg.UserAccountDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.UserAccount;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/29
 */
public class UserAccountDaoTest extends TestBase {
  @Autowired
  UserAccountDao userAccountDao;

  private UserAccount userAccount;
  private String email = "test@qq.com";
  private String password = "sq12345567@@";

  @Before
  public void setUp() throws Exception {
    userAccount = new UserAccount();
    userAccount.setEmailAddress(email);
    userAccount.setEncryptedPassword(password);
    userAccount.setCreatedUserId(0L);
  }

  @Test
  public void testAll() throws Exception {
    long userId = userAccountDao.insertUserAccount(userAccount);

    UserAccount afterInsert = userAccountDao.findByPrimaryKey(userId);
    Assert.assertEquals(email, afterInsert.getEmailAddress());
    Assert.assertEquals(password, afterInsert.getEncryptedPassword());

    afterInsert = userAccountDao.findByEmailAddress(email);
    Assert.assertEquals(email, afterInsert.getEmailAddress());
    Assert.assertEquals(password, afterInsert.getEncryptedPassword());

    afterInsert.setExtend(JSONObject.parseObject("{\"test\":\"test\"}"));
    afterInsert.setLoginFailTime(3);
    userAccountDao.updateByPrimaryKeySelective(afterInsert);
    afterInsert = userAccountDao.findByEmailAddress(email);
    Assert.assertEquals(email, afterInsert.getEmailAddress());
    Assert.assertEquals(password, afterInsert.getEncryptedPassword());
    Assert.assertEquals("{\"test\":\"test\"}", afterInsert.getExtend().toString());
    Assert.assertEquals(3, afterInsert.getLoginFailTime().intValue());

    String newPwd = "sq12345567@@@";
    afterInsert.setEncryptedPassword(newPwd);
    userAccountDao.updateByPrimaryKeySelective(afterInsert);
    afterInsert = userAccountDao.findByEmailAddress(email);
    Assert.assertEquals(newPwd, afterInsert.getEncryptedPassword());

    Assert.assertEquals(1, userAccountDao.listUserAccountByEmailAddress(Arrays.asList(email)).size());

    userAccountDao.deleteUserAccountByPrimaryKey(userId, 0L);
    afterInsert = userAccountDao.findByEmailAddress(email);
    Assert.assertNull(afterInsert);
  }
}