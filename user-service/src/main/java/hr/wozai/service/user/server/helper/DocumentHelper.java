// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.enums.DocumentScenario;
import hr.wozai.service.user.server.model.document.Document;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
public class DocumentHelper {

  public static boolean isAcceptableAddRequest(Document document) {
    if (null == document
        || null == document.getOrgId()
        || (null == document.getScenario()
            || null == DocumentScenario.getEnumByCode(document.getScenario()))
        || null == document.getDocumentName()
        || null == document.getStorageStatus()
        || null == document.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isAcceptableUpdateRequest(Document document) {
    if (null == document
        || (null == document.getOrgId() || document.getOrgId() <= 0)
        || (null == document.getLastModifiedUserId() || document.getLastModifiedUserId() <= 0)) {
      return false;
    }
    return true;
  }
}
