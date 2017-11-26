package hr.wozai.service.user.server.test.service.impl;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.dao.userorg.OrgDao;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.UserAccount;
import hr.wozai.service.user.server.service.UserService;
import hr.wozai.service.user.server.test.utils.AopTargetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/11
 */
public class UserServiceTest extends TestBase {

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  UserService userService;

  @Autowired
  OrgDao orgDao;

  @Mock
  OrgDao spyOrgDao;

  private String emailAddress = "test2@shanqian.com";
  private String password = "Sq1234567@@";

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(userService), "orgDao", spyOrgDao);
  }

  @Test
  public void testAddUserAccountWithWrongEmail() throws Exception {
    UserAccount userAccount = new UserAccount();
    userAccount.setEmailAddress("");

    thrown.expect(ServiceStatusException.class);
    userService.addUserAccount(userAccount);
  }

  @Test
  public void testUserAccountAll() {
    UserAccount userAccount = new UserAccount();
    userAccount.setEmailAddress(emailAddress);
    userAccount.setEncryptedPassword(password);

    long userId = userService.addUserAccount(userAccount);
    Assert.assertNotNull(userId);

    UserAccount inDb = userService.getUserAccountByUserId(userId);
    Assert.assertEquals(emailAddress, inDb.getEmailAddress());

    inDb = userService.getUserAccountByEmailAddress(emailAddress);
    Assert.assertEquals(emailAddress, inDb.getEmailAddress());

    Assert.assertEquals(1, userService.listUserAccountByEmailAddress(Arrays.asList(emailAddress)).size());

    String updateEmail = "update@sqian.com";
    userAccount.setEmailAddress(updateEmail);
    userAccount.setEncryptedPassword(password + "@");
    userAccount.setLastModifiedUserId(-1L);
    userService.updateUserAccount(userAccount);

    inDb = userService.getUserAccountByUserId(userId);
    Assert.assertEquals(updateEmail, inDb.getEmailAddress());
    Assert.assertEquals(password + "@", inDb.getEncryptedPassword());

    userService.deleteUserAccount(userId, -1L);
    thrown.expect(ServiceStatusException.class);
    userService.getUserAccountByUserId(userId);

  }

  @Test
  public void testGetUserAccountWithInvalidEmail() throws Exception {
    thrown.expect(ServiceStatusException.class);
    userService.getUserAccountByEmailAddress(emailAddress);
  }



  @Test
  public void testOrgMemberAll() throws Exception {
    long orgId = 199L;
    long userId = 199L;
    Org org = new Org();
    org.setOrgId(orgId);
    Mockito.doReturn(org).when(spyOrgDao).findOrgByPrimaryKey(Mockito.anyLong());
    userService.addOrgMember(orgId, userId, -1L);

    Assert.assertEquals(orgId, userService.findOrgIdByUserId(userId));

    List<Long> userIds = userService.listAllUsersByOrgId(orgId);
    Assert.assertEquals(1, userIds.size());

    userService.deleteOrgMember(userId);
    userIds = userService.listAllUsersByOrgId(orgId);
    Assert.assertEquals(0, userIds.size());
  }

  @Test
  public void testAddOrgMemberWithInvalidOrgId() throws Exception {
    long orgId = 199L;
    long userId = 199L;
    Mockito.doReturn(null).when(spyOrgDao).findOrgByPrimaryKey(Mockito.anyLong());
    thrown.expect(ServiceStatusException.class);
    userService.addOrgMember(orgId, userId, -1L);
  }

  @Test
  public void testFindOrgIdWitnInvalidUserId() throws Exception {
    thrown.expect(ServiceStatusException.class);
    userService.findOrgIdByUserId(199L);
  }

  @Test
  public void testReportLine() throws Exception {
    long orgId = 199L;
    long userId = 199L;
    long reportor = 299L;

    userService.batchInsertReportLine(orgId, Arrays.asList(reportor), 0, -1L);
    userService.batchInsertReportLine(orgId, Arrays.asList(userId), reportor, -1L);
    Assert.assertEquals(1, userService.listReporteesByUserIdAndOrgId(orgId, reportor).size());

    Assert.assertTrue(userService.hasReportor(orgId, userId));

    Assert.assertEquals(reportor, userService.getReportorByUserIdAndOrgId(orgId, userId));

    Assert.assertEquals(1, userService.listReportLinesByUserIds(orgId, Arrays.asList(userId)).size());

    Assert.assertEquals(1, userService.listUpReportLineByUserId(orgId, userId).size());

    long newReportor = 399L;
    userService.batchUpdateReportLine(orgId, Arrays.asList(userId), newReportor, -1L);
    Assert.assertEquals(0, userService.listReporteesByUserIdAndOrgId(orgId, reportor).size());
    Assert.assertEquals(1, userService.listReporteesByUserIdAndOrgId(orgId, newReportor).size());

    userService.batchDeleteReportLine(orgId, Arrays.asList(userId), -1L);
    Assert.assertEquals(0, userService.listReporteesByUserIdAndOrgId(orgId, newReportor).size());
  }

  @Test
  public void testGetReportorWithInvalidOrgIdAndUserId() throws Exception {
    thrown.expect(ServiceStatusException.class);
    userService.getReportorByUserIdAndOrgId(199L, 199L);
  }

}