// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.component;

import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-06-29
 */
@Component("orgPermissionChecker")
public class OrgPermissionChecker {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrgPermissionChecker.class);

  @Autowired
  private PermissionUtil permissionUtil;

  @LogAround
  public boolean canRead(long orgId, long actorUserId) {
    return permissionUtil.getPermissionForSingleObj(
        orgId, actorUserId, 0L, orgId, ResourceCode.ORG.getResourceCode(),
        ResourceType.ORG.getCode(), ActionCode.READ.getCode());
  }

  @LogAround
  public boolean canEdit(long orgId, long actorUserId) {
    return permissionUtil.getPermissionForSingleObj(
        orgId, actorUserId, 0L, orgId, ResourceCode.ORG.getResourceCode(),
        ResourceType.ORG.getCode(), ActionCode.EDIT.getCode());
  }

}
