// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.StatusType;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.server.model.userorg.StatusUpdate;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-29
 */
public class StatusUpdateHelper {

  public static boolean isValidStatusUpdateUponAddPassProbation(StatusUpdate statusUpdate) {
    if (null == statusUpdate
        || null == statusUpdate.getOrgId()
        || null == statusUpdate.getUserId()
        || (null == statusUpdate.getStatusType()
            || StatusType.EMPLOYMENT_STATUS.getCode() != statusUpdate.getStatusType())
        || StringUtils.isNullOrEmpty(statusUpdate.getUpdateType())
        || null == statusUpdate.getUpdateDate()
        || null == statusUpdate.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidStatusUpdateUponResign(StatusUpdate statusUpdate) {
    if (null == statusUpdate
        || null == statusUpdate.getOrgId()
        || null == statusUpdate.getUserId()
        || (null == statusUpdate.getStatusType()
            || StatusType.USER_STATUS.getCode() != statusUpdate.getStatusType())
        || StringUtils.isNullOrEmpty(statusUpdate.getUpdateType())
        || null == statusUpdate.getUpdateDate()
        || null == statusUpdate.getCreatedUserId()) {
      return false;
    }
    return true;
  }

}
