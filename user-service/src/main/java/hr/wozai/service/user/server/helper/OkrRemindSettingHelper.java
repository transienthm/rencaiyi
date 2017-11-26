package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.okr.enums.*;
import hr.wozai.service.user.server.enums.OkrRemindType;
import hr.wozai.service.user.server.model.okr.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
public class OkrRemindSettingHelper {

  public static void checkOkrRemindSettingInsertParams(OkrRemindSetting okrRemindSetting) {
    if (okrRemindSetting == null
            || okrRemindSetting.getOrgId() == null
            || (okrRemindSetting.getRemindType() == null || !OkrRemindType.isValidType(okrRemindSetting.getRemindType()))
            || (okrRemindSetting.getFrequency() == null || okrRemindSetting.getFrequency() < 0)
            || okrRemindSetting.getCreatedUserId() == null) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }
}
