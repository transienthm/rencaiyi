// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.service.OrgService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-25
 */
public class OrgServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(OrgServiceImplTest.class);

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  private OrgService orgService;

  // data
  String orgFullName = "北京闪签科技有限公司";
  String orgShortName = "闪签";
  String orgAvatarUrl = "http://some-url.com";
  int timeZone = 1;
  long userId = 2015;
  Org org = null;

  {
    org = new Org();
    org.setFullName(orgFullName);
    org.setShortName(orgShortName);
    org.setAvatarUrl(orgAvatarUrl);
    org.setTimeZone(timeZone);
    //org.setIsNaviOrg(0);
  }

  @Before
  public void init() {
  }

  /**
   * Test:
   *  1) addOrg()
   *  2) getOrg()
   *  3) updateOrg()
   *  4) deleteOrg()
   *
   */
  @Test
  public void testAll() {

    // 1) 2)
    long orgId = orgService.addOrg(org);
    Org insertedOrg = orgService.getOrg(orgId);
    System.out.println(insertedOrg);
    Assert.assertNotNull(org);

    // 3)
    String updatedFullName = orgShortName + orgFullName;
    insertedOrg.setFullName(updatedFullName);
    insertedOrg.setLastModifiedUserId(userId);
    orgService.updateOrg(insertedOrg);
    insertedOrg = orgService.getOrg(orgId);
    Assert.assertEquals(updatedFullName, insertedOrg.getFullName());

    // 4)
    orgService.deleteOrg(orgId, userId);
    try {
      insertedOrg = orgService.getOrg(orgId);
    } catch (ServiceStatusException e) {
      System.out.println("Got you! E=" + e);
      Assert.assertEquals(ServiceStatus.UO_ORG_NOT_FOUND.getCode(), e.getServiceStatus().getCode());
    }

  }

  @Test
  public void testAddOrgExceptionOne() {

    thrown.expect(ServiceStatusException.class);
    org.setFullName(null);
    orgService.addOrg(org);

  }

  @Test
  public void testUpdateOrgExceptionOne() {

    // prepare
    long orgId = orgService.addOrg(org);
    Org insertedOrg = orgService.getOrg(orgId);
    Assert.assertNotNull(org);

    // verify
    thrown.expect(ServiceStatusException.class);
    String updatedFullName = orgShortName + orgFullName;
    insertedOrg.setFullName(updatedFullName);
    insertedOrg.setLastModifiedUserId(null);
    orgService.updateOrg(insertedOrg);

  }


}
