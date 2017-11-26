package hr.wozai.service.user.server.service.impl;

import com.alibaba.fastjson.JSONObject;

import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.EmailUtils;
import hr.wozai.service.servicecommons.commons.utils.PasswordUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.userorg.enums.UuidUsage;
import hr.wozai.service.user.server.helper.UserAccountHelper;
import hr.wozai.service.user.server.helper.UserEmploymentHelper;
import hr.wozai.service.user.server.model.userorg.UserAccount;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.service.AuthenticationService;
import hr.wozai.service.user.server.service.TokenService;
import hr.wozai.service.user.server.service.UserEmploymentService;
import hr.wozai.service.user.server.service.UserProfileService;
import hr.wozai.service.user.server.service.UserService;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/27
 */
@Service("authenticationService")
public class AuthenticationServiceImpl implements AuthenticationService {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationServiceImpl.class);
  public static final String FIRST_LOGIN_TIMESTAMP = "first_login_timestamp";

  @Autowired
  UserService userService;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  TokenService tokenService;

  @Autowired
  UserProfileService userProfileService;

  @Autowired
  UserEmploymentService userEmploymentService;

  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long signUpWithEmailAddress(String emailAddress, String password) {

    UserAccountHelper.signupParamsCheck(password, emailAddress);

    // add user
    UserAccount userAccount = new UserAccount();
    userAccount.setEmailAddress(emailAddress);
    userAccount.setEncryptedPassword(passwordEncoder.encode(password));
    long userId = userService.addUserAccount(userAccount);

    return userId;
  }

  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean loginWithEmail(String emailAddress, String password, boolean captchaSuccess) {

    // validate
    if (!EmailUtils.isValidEmailAddressByApache(emailAddress)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    UserAccount userAccount = userService.getUserAccountByEmailAddress(emailAddress);
    if (null == userAccount) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_LOGIN_CREDENTIALS);
    }
    if (!captchaSuccess && userAccount.getLoginFailTime() >= 7) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_LOGIN_CREDENTIALS_NEED_CAPTCHA);
    }

    // auth logic
    String passwordInDb = userAccount.getEncryptedPassword();
    long userId = userAccount.getUserId();
    long orgId = userService.findOrgIdByUserId(userId);
    UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
    UserStatus currUserStatus = UserStatus.getEnumByCode(userEmployment.getUserStatus());

    if (currUserStatus.equals(UserStatus.ACTIVE)) {
      ;
    } else if (currUserStatus.equals(UserStatus.RESIGNED)) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_LOGIN_CREDENTIALS);
    } else if (currUserStatus.equals(UserStatus.IMPORTED)) {
      ;
    } else if (currUserStatus.equals(UserStatus.INVITED)) {
      Long enrollDate = UserEmploymentHelper.getEnrollDate(userEmployment);
      if ((enrollDate != null && enrollDate > TimeUtils.getNowTimestmapInMillis())) {
        throw new ServiceStatusException(ServiceStatus.AS_NO_AUTH_BEFORE_ENROLL_DATE);
      }
    }

    if (StringUtils.isNullOrEmpty(passwordInDb)) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_LOGIN_CREDENTIALS);
    }

    if (!passwordEncoder.matches(password, passwordInDb)) {
      this.addCaptchaFailTime(userAccount);
      userService.updateUserAccount(userAccount);
      if (userAccount.getLoginFailTime() >= 7) {
        throw new ServiceStatusException(ServiceStatus.AS_INVALID_LOGIN_CREDENTIALS_NEED_CAPTCHA);
      } else {
        throw new ServiceStatusException(ServiceStatus.AS_INVALID_LOGIN_CREDENTIALS);
      }
    }
    this.resetCaptchaFailTime(userAccount);

    if (currUserStatus.equals(UserStatus.INVITED)) {
      userProfileService.updateUserStatus(orgId, userId, UserStatus.ACTIVE.getCode(), userId);
    }

    boolean isFirstLogin = false;
    if (null == userAccount.getExtend()
        || null == userAccount.getExtend().getLong(FIRST_LOGIN_TIMESTAMP)) {
      setFirstLoginTimestamp(userAccount);
      isFirstLogin = true;
    }

    userService.updateUserAccount(userAccount);

    return isFirstLogin;
  }

  /**
   * Do not change userStatus here, upon login instead
   *
   * @param orgId
   * @param userId
   * @param passwordPlainText
   */
  @Override
  public void initPassword(long orgId, long userId, String passwordPlainText) {

    if (!PasswordUtils.isValidPassword(passwordPlainText)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    UserAccount userAccount = userService.getUserAccountByUserId(userId);
    if (null == userAccount) {
      throw new ServiceStatusException(ServiceStatus.UO_USER_NOT_FOUND);
    }

    UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
    if (UserStatus.IMPORTED.getCode() != userEmployment.getUserStatus().intValue()
        && UserStatus.INVITED.getCode() != userEmployment.getUserStatus().intValue()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    userAccount.setEncryptedPassword(passwordEncoder.encode(passwordPlainText));
    userService.updateUserAccount(userAccount);
    tokenService.deleteAllTokensByOrgIdAndUserId(orgId, userId);
  }

  private void addCaptchaFailTime(UserAccount userAccount) {
    int loginFailTime = userAccount.getLoginFailTime();
    userAccount.setLoginFailTime(loginFailTime + 1);
  }

  private void resetCaptchaFailTime(UserAccount userAccount) {
    userAccount.setLoginFailTime(0);
  }

  private void setFirstLoginTimestamp(UserAccount userAccount) {
    JSONObject jsonObject = userAccount.getExtend();
    if (null == jsonObject) {
      jsonObject = new JSONObject();
    }
    jsonObject.put(FIRST_LOGIN_TIMESTAMP, TimeUtils.getNowTimestmapInMillis());
    userAccount.setExtend(jsonObject);
  }

  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void changePassword(long orgId, long userId, String currentPassword, String newPassword, long adminUserId) {
    if (!PasswordUtils.isValidPassword(newPassword)
            || !PasswordUtils.isValidPassword(currentPassword)
            || currentPassword.equals(newPassword)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    UserAccount userAccount = userService.getUserAccountByUserId(userId);

    if (null == userAccount) {
      throw new ServiceStatusException(ServiceStatus.UO_USER_NOT_FOUND);
    }
    String password = userAccount.getEncryptedPassword();
    if (!passwordEncoder.matches(currentPassword, password) || password.isEmpty()) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_LOGIN_CREDENTIALS);
    }
    userAccount.setEncryptedPassword(passwordEncoder.encode(newPassword));
    userService.updateUserAccount(userAccount);

    tokenService.deleteAllTokensByOrgIdAndUserId(orgId, userId);
  }

  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void resetPassword(long orgId, long userId, String password) {
    if (!PasswordUtils.isValidPassword(password)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    UserAccount userAccount = userService.getUserAccountByUserId(userId);
    if (null == userAccount) {
      throw new ServiceStatusException(ServiceStatus.UO_USER_NOT_FOUND);
    }

    UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
    Long enrollDate = UserEmploymentHelper.getEnrollDate(userEmployment);
    if ((enrollDate != null && enrollDate > TimeUtils.getNowTimestmapInMillis()) ||
            userEmployment.getUserStatus().intValue() == UserStatus.RESIGNED.getCode()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    userAccount.setEncryptedPassword(passwordEncoder.encode(password));
    userService.updateUserAccount(userAccount);

    tokenService.deleteAllTokensByOrgIdAndUserId(orgId, userId);
    tokenService.deleteUuidInfoByUserIdAndUsage(orgId, userId, UuidUsage.RESET_PWD.getCode());
  }

  @Override
  public boolean verifyUserAccountWithPassword(long userId, String password) {
    UserAccount userAccount = userService.getUserAccountByUserId(userId);

    if (null == userAccount) {
      throw new ServiceStatusException(ServiceStatus.UO_USER_NOT_FOUND);
    }
    String passwordInDb = userAccount.getEncryptedPassword();
    return passwordEncoder.matches(password, passwordInDb);
  }
}
