// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.utils.BooleanUtils;
import hr.wozai.service.user.client.conversation.enums.PeriodType;
import hr.wozai.service.user.client.conversation.enums.RemindDay;
import hr.wozai.service.user.server.model.conversation.ConvrSchedule;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
public class ConvrScheduleHelper {

  public static boolean isValidAddRequest(ConvrSchedule convrSchedule) {
    if (null == convrSchedule
        || null == convrSchedule.getOrgId()
        || null == convrSchedule.getSourceUserId()
        || null == convrSchedule.getTargetUserId()
        || (null == convrSchedule.getPeriodType()
            || !PeriodType.isValidPeriodType(convrSchedule.getPeriodType()))
        || (null == convrSchedule.getRemindDay()
            || (!RemindDay.isValidRemindDay(convrSchedule.getRemindDay())
                && -1 != convrSchedule.getRemindDay()))) {
      return false;
    }
    return true;
  }

  public static boolean isValidUpdateRequest(ConvrSchedule convrSchedule) {
    if (null == convrSchedule
        || null == convrSchedule.getConvrScheduleId()
        || null == convrSchedule.getOrgId()
        || null == convrSchedule.getLastModifiedUserId()
        || (null != convrSchedule.getPeriodType()
            && !PeriodType.isValidPeriodType(convrSchedule.getPeriodType()))
        || (null != convrSchedule.getRemindDay()
            && (!RemindDay.isValidRemindDay(convrSchedule.getRemindDay())
                && -1 != convrSchedule.getRemindDay()))
        || (null != convrSchedule.getIsActive()
            && !BooleanUtils.isValidBooleanValue(convrSchedule.getIsActive()))) {
      return false;
    }
    return true;
  }

}
