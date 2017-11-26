// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.user.server.model.conversation.ConvrRecord;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
public class ConvrRecordHelper {

  public static boolean isValidAddRequest(ConvrRecord convrRecord) {
    if (null == convrRecord
        || null == convrRecord.getOrgId()
        || null == convrRecord.getConvrScheduleId()
        || null == convrRecord.getConvrDate()) {
      return false;
    }
    return true;
  }

  public static boolean isValidUpdateRequest(ConvrRecord convrRecord) {
    if (null == convrRecord
        || null == convrRecord.getConvrRecordId()
        || null == convrRecord.getOrgId()
        || null == convrRecord.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }

}
