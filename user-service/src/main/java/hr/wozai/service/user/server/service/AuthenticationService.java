package hr.wozai.service.user.server.service;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/27
 */
public interface AuthenticationService {
  long signUpWithEmailAddress(String emailAddress, String password);

  boolean loginWithEmail(String emailAddress, String password, boolean captchaSuccess);

  void initPassword(long orgId, long userId, String passwordPlainText);

  void changePassword(long orgId, long userId, String currentPassword, String newPassword, long adminUserId);

  void resetPassword(long orgId, long userId, String password);

  boolean verifyUserAccountWithPassword(long userId, String password);

}
