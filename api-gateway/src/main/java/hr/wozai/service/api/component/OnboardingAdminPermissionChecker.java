// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.component;

import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-06-29
 */
@Component("onboardingAdminPermissionChecker")
public class OnboardingAdminPermissionChecker {

  private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingAdminPermissionChecker.class);

  @Autowired
  private PermissionUtil permissionUtil;

  @LogAround
  public boolean canCreate(long orgId, long actorUserId) {
    return permissionUtil.getPermissionForSingleObj(
        orgId, actorUserId, 0L, orgId, ResourceCode.ONBOARDING_ADMIN.getResourceCode(),
        ResourceType.ORG.getCode(), ActionCode.CREATE.getCode());
  }

  @LogAround
  public boolean canRead(long orgId, long actorUserId) {
    return permissionUtil.getPermissionForSingleObj(
        orgId, actorUserId, 0L, orgId, ResourceCode.ONBOARDING_ADMIN.getResourceCode(),
        ResourceType.ORG.getCode(), ActionCode.READ.getCode());
  }

  @LogAround
  public boolean canEdit(long orgId, long actorUserId) {
    return permissionUtil.getPermissionForSingleObj(
        orgId, actorUserId, 0L, orgId, ResourceCode.ONBOARDING_ADMIN.getResourceCode(),
        ResourceType.ORG.getCode(), ActionCode.EDIT.getCode());
  }

  @LogAround
  public boolean canList(long orgId, long actorUserId) {
    return permissionUtil.getPermissionForSingleObj(
        orgId, actorUserId, 0L, orgId, ResourceCode.ONBOARDING_ADMIN.getResourceCode(),
        ResourceType.ORG.getCode(), ActionCode.READ.getCode());
  }

  @LogAround
  public boolean canDelete(long orgId, long actorUserId) {
    return permissionUtil.getPermissionForSingleObj(
        orgId, actorUserId, 0L, orgId, ResourceCode.ONBOARDING_ADMIN.getResourceCode(),
        ResourceType.ORG.getCode(), ActionCode.DELETE.getCode());
  }

}
