// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.dao.userorg.OrgDao;
import hr.wozai.service.user.server.helper.OrgHelper;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.service.OrgService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-10
 */
@Service("orgService")
public class OrgServiceImpl implements OrgService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrgServiceImpl.class);

  @Autowired
  OrgDao orgDao;

  @Override
  @LogAround
  public long addOrg(Org org) {

    if (!OrgHelper.isValidAddOrgRequest(org)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    long orgId = orgDao.insertOrg(org);
    return orgId;

  }

  @Override
  @LogAround
  public Org getOrg(long orgId) {

    Org org = orgDao.findOrgByPrimaryKey(orgId);
    if (null == org) {
      throw new ServiceStatusException(ServiceStatus.UO_ORG_NOT_FOUND);
    }
    return org;

  }

  @Override
  @LogAround
  public void updateOrg(Org org) {

    if (!OrgHelper.isValidUpdateOrgRequest(org)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    orgDao.updateOrgByPrimaryKeySelective(org);

  }

  @Override
  @LogAround
  public void deleteOrg(long orgId, long actorUserId) {

    Org org = new Org();
    org.setOrgId(orgId);
    org.setLastModifiedUserId(actorUserId);
    orgDao.deleteOrgByPrimaryKey(org);

  }
}
