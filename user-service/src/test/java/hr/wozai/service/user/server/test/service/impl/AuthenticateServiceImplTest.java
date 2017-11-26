package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.server.model.userorg.UserAccount;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.service.*;
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

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/27
 */
public class AuthenticateServiceImplTest extends TestBase {
  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  AuthenticationService authenticateService;

  @Autowired
  UserService userService;

  @Mock
  UserService spyUserService;

  @Autowired
  TokenService tokenService;

  @Mock
  TokenService spyTokenService;

  @Autowired
  UserProfileService userProfileService;

  @Mock
  UserProfileService spyUserProfileService;

  @Autowired
  UserEmploymentService userEmploymentService;

  @Mock
  UserEmploymentService spyUserEmploymentService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Mock
  PasswordEncoder spyPasswordEncoder;

  private String emailAddress = "test2@shanqian.com";
  private String password = "Sq1234567@@";

  private long orgId=199L;
  private long userId=199L;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(authenticateService), "userService", spyUserService);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(authenticateService), "tokenService", spyTokenService);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(authenticateService), "userProfileService", spyUserProfileService);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(authenticateService), "userEmploymentService", spyUserEmploymentService);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(authenticateService), "passwordEncoder", spyPasswordEncoder);

    Mockito.doReturn(userId).when(spyUserService).addUserAccount(Mockito.anyObject());
  }

  @Test
  public void testSignUpWithEmailAddress() throws Exception {
    long result = authenticateService.signUpWithEmailAddress(emailAddress, password);
    Assert.assertEquals(userId, result);

    UserAccount userAccount = new UserAccount();
    userAccount.setLoginFailTime(1);
    userAccount.setUserId(userId);
    userAccount.setEmailAddress(emailAddress);
    userAccount.setEncryptedPassword(password);
    Mockito.doReturn(userAccount).when(spyUserService).getUserAccountByUserId(Mockito.anyLong());
    Mockito.doReturn(true).when(spyPasswordEncoder).matches(Mockito.anyString(), Mockito.anyString());
    authenticateService.verifyUserAccountWithPassword(userId, password);
  }

  @Test
  public void testLoginWithInvalidEmail() throws Exception {
    // invalid email address
    thrown.expect(ServiceStatusException.class);
    authenticateService.loginWithEmail("aaa", password, true);
  }

  @Test
  public void testLoginWithWrongEmail() throws Exception {
    Mockito.doReturn(null).when(spyUserService).getUserAccountByEmailAddress(Mockito.anyString());
    thrown.expect(ServiceStatusException.class);
    authenticateService.loginWithEmail(emailAddress, password, true);
  }

  @Test
  public void testLoginWithLoginFailTimeMoreThanSeven() throws Exception {
    // test AS_INVALID_LOGIN_CREDENTIALS_NEED_CAPTCHA
    UserAccount userAccount = new UserAccount();
    userAccount.setLoginFailTime(8);
    Mockito.doReturn(userAccount).when(spyUserService).getUserAccountByEmailAddress(Mockito.anyString());
    thrown.expect(ServiceStatusException.class);
    authenticateService.loginWithEmail(emailAddress, password, false);
  }

  @Test
  public void testLoginWithAcitveAccount() throws Exception {
    // test login with active, fulltime person
    UserAccount userAccount = new UserAccount();
    userAccount.setLoginFailTime(1);
    userAccount.setUserId(userId);
    userAccount.setEmailAddress(emailAddress);
    userAccount.setEncryptedPassword(password);
    Mockito.doReturn(userAccount).when(spyUserService).getUserAccountByEmailAddress(Mockito.anyString());
    Mockito.doReturn(orgId).when(spyUserService).findOrgIdByUserId(Mockito.anyLong());

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setUserStatus(UserStatus.ACTIVE.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setFulltimeEnrollDate(TimeUtils.getNowTimestmapInMillis() - 3600 *24);
    Mockito.doReturn(userEmployment).when(spyUserEmploymentService).
            getUserEmployment(Mockito.anyLong(), Mockito.anyLong());

    Mockito.doReturn(true).when(spyPasswordEncoder).matches(Mockito.anyString(), Mockito.anyString());
    Mockito.doReturn(true).when(spyUserService).updateUserAccount(Mockito.anyObject());
    authenticateService.loginWithEmail(emailAddress, password, true);

    Mockito.doReturn(false).when(spyPasswordEncoder).matches(Mockito.anyString(), Mockito.anyString());
    thrown.expect(ServiceStatusException.class);
    authenticateService.loginWithEmail(emailAddress, password, true);
  }

  @Test
  public void testLoginWithResignedStatus() throws Exception {
    UserAccount userAccount = new UserAccount();
    userAccount.setLoginFailTime(1);
    userAccount.setUserId(userId);
    userAccount.setEmailAddress(emailAddress);
    userAccount.setEncryptedPassword(password);
    Mockito.doReturn(userAccount).when(spyUserService).getUserAccountByEmailAddress(Mockito.anyString());
    Mockito.doReturn(orgId).when(spyUserService).findOrgIdByUserId(Mockito.anyLong());

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setUserStatus(UserStatus.RESIGNED.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setFulltimeEnrollDate(TimeUtils.getNowTimestmapInMillis() - 3600 *24);
    Mockito.doReturn(userEmployment).when(spyUserEmploymentService).
            getUserEmployment(Mockito.anyLong(), Mockito.anyLong());
    thrown.expect(ServiceStatusException.class);
    authenticateService.loginWithEmail(emailAddress, password, true);
  }

  @Test
  public void testLoginWithInvitedStatus() throws Exception {
    UserAccount userAccount = new UserAccount();
    userAccount.setLoginFailTime(1);
    userAccount.setUserId(userId);
    userAccount.setEmailAddress(emailAddress);
    userAccount.setEncryptedPassword(password);
    Mockito.doReturn(userAccount).when(spyUserService).getUserAccountByEmailAddress(Mockito.anyString());
    Mockito.doReturn(orgId).when(spyUserService).findOrgIdByUserId(Mockito.anyLong());

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setUserStatus(UserStatus.INVITED.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setFulltimeEnrollDate(TimeUtils.getNowTimestmapInMillis() + 3600 *24);
    Mockito.doReturn(userEmployment).when(spyUserEmploymentService).
            getUserEmployment(Mockito.anyLong(), Mockito.anyLong());

    thrown.expect(ServiceStatusException.class);
    authenticateService.loginWithEmail(emailAddress, password, true);
  }

  @Test
  public void testLoginWithOtherStatus() throws Exception {
    UserAccount userAccount = new UserAccount();
    userAccount.setLoginFailTime(1);
    userAccount.setUserId(userId);
    userAccount.setEmailAddress(emailAddress);
    userAccount.setEncryptedPassword(password);
    Mockito.doReturn(userAccount).when(spyUserService).getUserAccountByEmailAddress(Mockito.anyString());
    Mockito.doReturn(orgId).when(spyUserService).findOrgIdByUserId(Mockito.anyLong());

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setFulltimeEnrollDate(TimeUtils.getNowTimestmapInMillis() - 3600 *24);
    Mockito.doReturn(userEmployment).when(spyUserEmploymentService).
            getUserEmployment(Mockito.anyLong(), Mockito.anyLong());

    Mockito.doReturn(true).when(spyPasswordEncoder).matches(Mockito.anyString(), Mockito.anyString());
    Mockito.doReturn(true).when(spyUserService).updateUserAccount(Mockito.anyObject());
    authenticateService.loginWithEmail(emailAddress, password, true);
  }

  @Test
  public void testInitPassword() throws Exception {
    UserAccount userAccount = new UserAccount();
    userAccount.setLoginFailTime(1);
    userAccount.setUserId(userId);
    userAccount.setEmailAddress(emailAddress);
    userAccount.setEncryptedPassword(password);

    Mockito.doReturn(userAccount).when(spyUserService).getUserAccountByUserId(Mockito.anyLong());

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setFulltimeEnrollDate(TimeUtils.getNowTimestmapInMillis() - 3600 *24);
    Mockito.doReturn(userEmployment).when(spyUserEmploymentService).
            getUserEmployment(Mockito.anyLong(), Mockito.anyLong());

    Mockito.doReturn(true).when(spyUserService).updateUserAccount(Mockito.anyObject());
    Mockito.doReturn(true).when(spyTokenService).
            deleteAllTokensByOrgIdAndUserId(Mockito.anyLong(), Mockito.anyLong());

    authenticateService.initPassword(orgId, userId, password);

    thrown.expect(ServiceStatusException.class);
    authenticateService.initPassword(orgId, userId, "aa");
  }

  @Test
  public void testInitPasswordWithWrongUserId() throws Exception {
    Mockito.doReturn(null).when(spyUserService).getUserAccountByUserId(Mockito.anyLong());

    thrown.expect(ServiceStatusException.class);
    authenticateService.initPassword(orgId, userId, password);
  }

  @Test
  public void testInitPasswordWithActiveUser() throws Exception {
    UserAccount userAccount = new UserAccount();
    userAccount.setLoginFailTime(1);
    userAccount.setUserId(userId);
    userAccount.setEmailAddress(emailAddress);
    userAccount.setEncryptedPassword(password);

    Mockito.doReturn(userAccount).when(spyUserService).getUserAccountByUserId(Mockito.anyLong());

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setUserStatus(UserStatus.ACTIVE.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setFulltimeEnrollDate(TimeUtils.getNowTimestmapInMillis() - 3600 *24);
    Mockito.doReturn(userEmployment).when(spyUserEmploymentService).
            getUserEmployment(Mockito.anyLong(), Mockito.anyLong());

    thrown.expect(ServiceStatusException.class);
    authenticateService.initPassword(orgId, userId, password);
  }

  @Test
  public void testChangePassword() throws Exception {
    UserAccount userAccount = new UserAccount();
    userAccount.setLoginFailTime(1);
    userAccount.setUserId(userId);
    userAccount.setEmailAddress(emailAddress);
    userAccount.setEncryptedPassword(password);

    Mockito.doReturn(userAccount).when(spyUserService).getUserAccountByUserId(Mockito.anyLong());
    Mockito.doReturn(true).when(spyPasswordEncoder).matches(Mockito.anyString(), Mockito.anyString());
    Mockito.doReturn(true).when(spyUserService).updateUserAccount(Mockito.anyObject());
    Mockito.doReturn(true).when(spyTokenService).
            deleteAllTokensByOrgIdAndUserId(Mockito.anyLong(), Mockito.anyLong());

    authenticateService.changePassword(orgId, userId, password, password + "aa", -1L);

    thrown.expect(ServiceStatusException.class);
    authenticateService.changePassword(orgId, userId, password, password, -1L);
  }

  @Test
  public void testResetPassword() throws Exception {
    UserAccount userAccount = new UserAccount();
    userAccount.setLoginFailTime(1);
    userAccount.setUserId(userId);
    userAccount.setEmailAddress(emailAddress);
    userAccount.setEncryptedPassword(password);

    Mockito.doReturn(userAccount).when(spyUserService).getUserAccountByUserId(Mockito.anyLong());

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setFulltimeEnrollDate(TimeUtils.getNowTimestmapInMillis() - 3600 *24);
    Mockito.doReturn(userEmployment).when(spyUserEmploymentService).
            getUserEmployment(Mockito.anyLong(), Mockito.anyLong());
    Mockito.doReturn(true).when(spyUserService).updateUserAccount(Mockito.anyObject());
    Mockito.doReturn(true).when(spyTokenService).
            deleteAllTokensByOrgIdAndUserId(Mockito.anyLong(), Mockito.anyLong());

    authenticateService.resetPassword(orgId, userId, password);

    Mockito.doReturn(null).when(spyUserService).getUserAccountByUserId(Mockito.anyLong());
    thrown.expect(ServiceStatusException.class);
    authenticateService.resetPassword(orgId, userId, password);
  }

  @Test
  public void testResetPasswordWithInvalidPassword() throws Exception {
    thrown.expect(ServiceStatusException.class);
    authenticateService.resetPassword(orgId, userId, "aaa");
  }




}