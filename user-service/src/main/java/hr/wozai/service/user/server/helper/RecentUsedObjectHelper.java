package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.userorg.enums.RecentUsedObjectType;
import hr.wozai.service.user.server.model.common.RecentUsedObject;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/24
 */
public class RecentUsedObjectHelper {
  public static void checkInsertParam(RecentUsedObject recentUsedObject) {
    if (null == recentUsedObject
            || null == recentUsedObject.getOrgId()
            || null == recentUsedObject.getUserId()
            || (null == recentUsedObject.getType() || !RecentUsedObjectType.isValidType(recentUsedObject.getType()))
            || null == recentUsedObject.getUsedObjectId()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }
}
