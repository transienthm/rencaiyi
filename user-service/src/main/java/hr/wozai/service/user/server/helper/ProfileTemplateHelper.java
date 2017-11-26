// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.server.model.userorg.ProfileTemplate;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-29
 */
public class ProfileTemplateHelper {

  /**
   * Validate the add request
   *
   * @param profileTemplate
   * @return
   */
  public static boolean isValidAddRequest(ProfileTemplate profileTemplate) {
    if (null == profileTemplate
        || null == profileTemplate.getOrgId()
        || StringUtils.isNullOrEmpty(profileTemplate.getDisplayName())
        || null == profileTemplate.getIsPreset()
        || null == profileTemplate.getCreatedUserId()) {
      return false;
    }
    return true;
  }

}
