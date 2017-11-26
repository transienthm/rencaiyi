// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.user.server.model.userorg.Org;

import org.aspectj.weaver.ast.Or;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-16
 */
public class OrgHelper {

  public static boolean isValidAddOrgRequest(Org org) {
    if (null == org
        || null == org.getFullName()
        || null == org.getShortName()
        || null == org.getTimeZone()
        // NOTE: comment out createdUserId because the org is created without first user for now (2016-05-16)
        // || null == org.getCreatedUserId()
        ) {
      return false;
    }
    return true;
  }

  public static boolean isValidUpdateOrgRequest(Org org) {
    if (null == org
        || (null == org.getOrgId() || org.getOrgId() <= 0)
        || (null == org.getLastModifiedUserId() || org.getLastModifiedUserId() <= 0)) {
      return false;
    }
    return true;
  }

}
