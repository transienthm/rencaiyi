package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.userorg.enums.UuidUsage;
import hr.wozai.service.user.server.model.token.UuidInfo;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/15
 */
public class UuidInfoHelper {
  public static void checkUuidInfoInsertParams(UuidInfo uuidInfo) {
    if (null == uuidInfo
            || null == uuidInfo.getOrgId()
            || null == uuidInfo.getUserId()
            || null == uuidInfo.getUuid()
            || (null == uuidInfo.getUuidUsage() || !UuidUsage.isValidType(uuidInfo.getUuidUsage()))
            || null == uuidInfo.getExpireTime()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }
}
