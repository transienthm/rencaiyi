// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.user.server.model.userorg.EncryptedPickOption;
import hr.wozai.service.user.server.model.userorg.PickOption;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-07-05
 */
public class PickOptionHelper {


  public static void copyPropertiesFromPickOptionToEncryptedPickOption(
      PickOption pickOption, EncryptedPickOption encryptedPickOption) {

    if (null == pickOption
        || null == encryptedPickOption) {
      return;
    }

    try {
      encryptedPickOption.setPickOptionId(
          EncryptUtils.symmetricEncrypt(String.valueOf(pickOption.getPickOptionId().longValue())).toUpperCase());
      encryptedPickOption.setOptionValue(pickOption.getOptionValue());
      encryptedPickOption.setOptionIndex(pickOption.getOptionIndex());
      encryptedPickOption.setIsDefault(pickOption.getIsDefault());
      encryptedPickOption.setIsDeprecated(pickOption.getIsDeprecated());
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

  }

  public static void copyPropertiesFromEncryptedPickOptionToPickOption(
      EncryptedPickOption encryptedPickOption, PickOption pickOption) {

    if (null == pickOption
        || null == encryptedPickOption) {
      return;
    }

    try {
      if (!StringUtils.isNullOrEmpty(encryptedPickOption.getPickOptionId())) {
        pickOption.setPickOptionId(
            Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedPickOption.getPickOptionId())));
      }
      pickOption.setOptionValue(encryptedPickOption.getOptionValue());
      pickOption.setOptionIndex(encryptedPickOption.getOptionIndex());
      pickOption.setIsDefault(encryptedPickOption.getIsDefault());
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

  }


}
