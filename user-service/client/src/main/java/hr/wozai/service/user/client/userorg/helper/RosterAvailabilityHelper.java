// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.client.userorg.helper;

import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-25
 */
public class RosterAvailabilityHelper {

  /**
   * Tmp usage
   *
   * @param referenceName
   * @param datType
   * @return
   */
  public static boolean isAvailableProfileField(String referenceName, int datType) {

    if (SystemProfileField.AVATAR_URL.getReferenceName().equals(referenceName)
        || DataType.FILE.getCode() == datType
        || DataType.FILES.getCode() == datType
        || DataType.BLOCK_TEXT.getCode() == datType) {
      return false;
    }
    return true;
  }

}
