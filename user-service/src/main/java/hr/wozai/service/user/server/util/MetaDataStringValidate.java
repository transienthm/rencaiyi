package hr.wozai.service.user.server.util;

import hr.wozai.service.servicecommons.commons.consts.TypeSpecConsts;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.validator.StringLengthValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/8/18
 */
public class MetaDataStringValidate {
  public static final Logger LOGGER = LoggerFactory.getLogger(MetaDataStringValidate.class);

  public static boolean validate(int dataType, String dataValue) {
    boolean isValid;
    if (dataType == DataType.SHORT_TEXT.getCode()) {
      isValid = StringLengthValidator.validate(dataValue, TypeSpecConsts.CHINESE_MAX_LENGTH_OF_STXT_VALUE);
    } else if (dataType == DataType.LONG_TEXT.getCode()) {
      isValid = StringLengthValidator.validate(dataValue, TypeSpecConsts.CHINESE_MAX_LENGTH_OF_LTXT_VALUE);
    } else if (dataType == DataType.BLOCK_TEXT.getCode()) {
      isValid = StringLengthValidator.validate(dataValue, TypeSpecConsts.CHINESE_MAX_LENGTH_OF_BTXT_VALUE);
    } else {
      isValid = true;
    }

    return isValid;
  }
}
