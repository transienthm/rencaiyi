// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.user.client.userorg.dto.OrgDTO;
import hr.wozai.service.user.client.userorg.facade.OrgFacade;
import hr.wozai.service.user.server.model.userorg.Team;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.dao.userorg.OrgDao;
import hr.wozai.service.user.server.model.userorg.Org;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class OrgFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(OrgFacadeImplTest.class);

  @Autowired
  OrgDao orgDao;

  @Autowired
  OrgFacade orgFacade;

  @Autowired
  TeamService teamService;

  // data
  String orgFullName = "北京闪签科技有限公司";
  String orgShortName = "闪签";
  String orgAvatarUrl = "http://some-url.com";
  int timeZone = 1;
  long userId = 2016;
  long invalidOrgId = 499999999L;
  Org org = null;

  {
    org = new Org();
    org.setFullName(orgFullName);
    org.setShortName(orgShortName);
    org.setAvatarUrl(orgAvatarUrl);
    org.setTimeZone(timeZone);
    org.setCreatedUserId(-1L);
  }

  /**
   * Test:
   *  1) getOrg()
   *  2) updateOrg()
   *
   */
  @Test
  public void testAll() {

    // insertOrg
    long orgId = orgDao.insertOrg(org);

    // insertTeam
    Team team = new Team();
    team.setOrgId(orgId);
    team.setTeamName(orgShortName);
    team.setParentTeamId(0L);
    team.setCreatedUserId(userId);
    long teamId = teamService.addTeam(team);

    // 1)
    OrgDTO rpcGetResult = orgFacade.getOrg(orgId, userId, userId);
    Assert.assertEquals(orgFullName, rpcGetResult.getFullName());

    // 2)
    rpcGetResult.setAvatarUrl(orgShortName);
    rpcGetResult.setShortName("update");
    orgFacade.updateOrg(orgId, rpcGetResult, userId, userId);
    rpcGetResult = orgFacade.getOrg(orgId, userId, userId);
    Assert.assertEquals(orgShortName, rpcGetResult.getAvatarUrl());

    Team update = teamService.getTeamByTeamId(orgId, teamId);
    Assert.assertEquals("update", update.getTeamName());
  }

  /**
   * Exception #1: cannot find org
   *
   */
  @Test
  public void testGetOrgExceptionOne() {
    OrgDTO rpcGetResult = orgFacade.getOrg(invalidOrgId, userId, userId);
    Assert.assertEquals(ServiceStatus.UO_ORG_NOT_FOUND.getCode(), rpcGetResult.getServiceStatusDTO().getCode());
  }

  /**
   * Exception #2: invalid update request
   *
   */
  @Test
  public void testUpdateOrgExceptionOne() {

    // prepare
    long orgId = orgDao.insertOrg(org);
    OrgDTO rpcGetResult = orgFacade.getOrg(orgId, userId, userId);
    Assert.assertEquals(orgFullName, rpcGetResult.getFullName());

    // verify
    rpcGetResult.setAvatarUrl(orgShortName);
    rpcGetResult.setLastModifiedUserId(null);
    orgFacade.updateOrg(-1, rpcGetResult, userId, userId);
    rpcGetResult = orgFacade.getOrg(invalidOrgId, userId, userId);
    Assert.assertEquals(ServiceStatus.UO_ORG_NOT_FOUND.getCode(), rpcGetResult.getServiceStatusDTO().getCode());

  }

}
