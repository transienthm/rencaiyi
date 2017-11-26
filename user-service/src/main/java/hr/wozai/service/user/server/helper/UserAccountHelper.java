package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.EmailUtils;
import hr.wozai.service.servicecommons.commons.utils.PasswordUtils;
import hr.wozai.service.user.server.model.userorg.UserAccount;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/18
 */
public class UserAccountHelper {
  public static boolean isAcceptableUpdateRequest(UserAccount userAccount) {
    if (null == userAccount) {
      return false;
    }
    return true;
  }

  public static void signupParamsCheck(String password, String mobilePhone) {
    if (!PasswordUtils.isValidPassword(password)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM, "password is not valid");
    }
    if (!EmailUtils.isValidEmailAddressByRegex(mobilePhone)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM, "email is not valid");
    }
  }
}
