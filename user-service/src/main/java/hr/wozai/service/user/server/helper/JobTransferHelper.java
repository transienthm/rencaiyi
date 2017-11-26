// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.user.server.model.userorg.JobTransfer;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-29
 */
public class JobTransferHelper {

  public static boolean isValidJobTransferUponAdd(JobTransfer jobTransfer) {
    if (null == jobTransfer
        || null == jobTransfer.getOrgId()
        || null == jobTransfer.getUserId()
        || null == jobTransfer.getTransferType()
        || null == jobTransfer.getTransferDate()
        || null == jobTransfer.getAfterTeamId()
        || null == jobTransfer.getAfterReporterId()
        || null == jobTransfer.getAfterJobTitleId()
        || null == jobTransfer.getAfterJobLevelId()
        || null == jobTransfer.getCreatedUserId()) {
      return false;
    }
    return true;
  }

}
