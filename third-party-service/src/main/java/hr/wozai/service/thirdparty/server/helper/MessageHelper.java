package hr.wozai.service.thirdparty.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.server.model.message.Message;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/11
 */
public class MessageHelper {
  public static void checkMessageInsertParama(Message message) {
    if (null == message
            || null == message.getOrgId()
            || null == message.getTemplateId()
            || null == message.getObjectId()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
  }
}
