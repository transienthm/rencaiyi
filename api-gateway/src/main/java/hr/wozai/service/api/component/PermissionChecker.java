package hr.wozai.service.api.component;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/6/27
 */
@Component("permissionChecker")
public class PermissionChecker {
  @Autowired
  PermissionUtil permissionUtil;

  public void permissionCheck(long orgId, long actorUserId, long objOwnerId,
                                         String resourceCode, int resourceType, int actionCode) {
    boolean result = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, 0L,
            objOwnerId, resourceCode, resourceType, actionCode);
    if (!result) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }
    return;
  }
}
