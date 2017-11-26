// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.servicecommons.commons.enums.MemberStatus;
import hr.wozai.service.servicecommons.commons.enums.MemberType;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.server.dao.userorg.OrgDao;
import hr.wozai.service.user.server.dao.userorg.OrgMemberDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.OrgMember;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

public class OrgDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(OrgDaoTest.class);

  @Autowired
  OrgDao orgDao;

  @Autowired
  OrgMemberDao orgMemberDao;

  private long userId = 1L;

  @Before
  public void setup() {
  }

  /**
   * Test:
   *  1) addOrg()
   *  2) findOrgByPrimaryKey()
   *  3) updateOrgByPrimaryKeySelective()
   *  4) deleteOrgByPrimaryKey()
   */
  @Test
  public void testInsertOrg() {

    // arg
    String fullName = "Brand New Ltd.";

    // 1) 2)
    Org org = new Org();
    org.setFullName(fullName);
    org.setShortName(fullName);
    org.setCreatedUserId(userId);
    org.setTimeZone(1);
    long orgId = orgDao.insertOrg(org);
    Org insertedOrg = orgDao.findOrgByPrimaryKey(org.getOrgId());
    Assert.assertEquals(fullName, insertedOrg.getFullName());
    Assert.assertEquals(0, insertedOrg.getCreatedUserId().longValue());

    // 3)
    fullName = fullName + fullName;
    insertedOrg.setFullName(fullName);
    orgDao.updateOrgByPrimaryKeySelective(insertedOrg);
    insertedOrg = orgDao.findOrgByPrimaryKey(orgId);
    Assert.assertEquals(fullName, insertedOrg.getFullName());

    // 4)
    insertedOrg.setIsDeleted(1);
    insertedOrg.setLastModifiedUserId(userId);
    orgDao.deleteOrgByPrimaryKey(insertedOrg);
    insertedOrg = orgDao.findOrgByPrimaryKey(orgId);
    Assert.assertNull(insertedOrg);

    List<Org> orgs = orgDao.listAllOrgs();
    Assert.assertNotEquals(0, orgs.size());
  }

//  @Test
//  public void testDeleteOrgMember() {
//    long orgId = 199L;
//    long userId = 199L;
//    OrgMember o1 = new OrgMember();
//    o1.setUserId(userId);
//    o1.setOrgId(orgId);
//    o1.setCreatedUserId(userId);
//    orgMemberDao.insertOrgMember(o1);
//
//    Assert.assertEquals(orgId, orgMemberDao.findOrgIdByUserId(userId).longValue());
//    Assert.assertNotNull(orgMemberDao.findByUserIdAndOrgId(orgId, userId));
//
//    orgMemberDao.deleteOrgMemberByUserId(userId);
//    List<Long> userIdList = orgMemberDao.listUserIdListByOrgId(2l);
//    Assert.assertEquals(0, userIdList.size());
//  }


}
