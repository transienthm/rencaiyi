// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.dao.userorg.StatusUpdateDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.StatusUpdate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class StatusUpdateDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(StatusUpdateDaoTest.class);

  long orgId = 19999999L;
  long userId = 29999999L;
  int statusType = 1;
  String updateType = "BLA";
  long updateDate = 3999L;
  StatusUpdate statusUpdate = null;

  {
    statusUpdate = new StatusUpdate();
    statusUpdate.setOrgId(orgId);
    statusUpdate.setUserId(userId);
    statusUpdate.setStatusType(statusType);
    statusUpdate.setUpdateType(updateType);
    statusUpdate.setUpdateDate(updateDate);
    statusUpdate.setCreatedUserId(userId);
  }

  @Autowired
  StatusUpdateDao statusUpdateDao;

  @Before
  public void init() {
  }

  /**
   * Test:
   *  1) insert
   *  2) find
   *  3) list
   *  4) count
   *
   */
  @Test
  public void testInsertStautsUpdate() {

    long insertedId = statusUpdateDao.insertStautsUpdate(statusUpdate);
    StatusUpdate insertedSU = statusUpdateDao.findStatusUpdateByOrgIdAndPrimaryKey(orgId, insertedId);
    Assert.assertEquals(updateType, insertedSU.getUpdateType());

    int count = 5;
    for (int i = 0; i < count - 1; i++) {
      statusUpdateDao.insertStautsUpdate(statusUpdate);
    }

    List<StatusUpdate> statusUpdates = statusUpdateDao.listStatusUpdateByOrgIdOrderByCreatedTimeDesc(orgId, statusType, 1, 20);
    Assert.assertEquals(count, statusUpdates.size());

    int insertedCount = statusUpdateDao.countStatusUpdateByOrgIdAndStatusType(orgId, statusType);
    Assert.assertEquals(count, insertedCount);

  }

  @Test
  public void testListStatusUpdateByOrgIdAndPrimaryKeys() {
    List<Long> statusUpdateIds = new ArrayList<>();
    int count = 5;
    for (int i = 0; i < count; i++) {
      statusUpdateIds.add(statusUpdateDao.insertStautsUpdate(statusUpdate));
    }
    List<StatusUpdate> statusUpdates = statusUpdateDao.listStatusUpdateByOrgIdAndPrimaryKeys(orgId, statusUpdateIds);
    Assert.assertEquals(count, statusUpdates.size());
  }

  @Test
  public void testRevokeStatusUpdateByPrimaryKeyAndOrgId() {

    // prepare
    long insertedStatusUpdateId = statusUpdateDao.insertStautsUpdate(statusUpdate);
    StatusUpdate insertedStatusUpdate = statusUpdateDao
        .findStatusUpdateByOrgIdAndPrimaryKey(orgId, insertedStatusUpdateId);
    Assert.assertNotEquals(-1, insertedStatusUpdate.getUpdateDate().longValue());

    // verify
    statusUpdateDao.revokeStatusUpdateByPrimaryKeyAndOrgId(orgId, insertedStatusUpdateId, userId);
    StatusUpdate updatedStatusUpdate = statusUpdateDao
        .findStatusUpdateByOrgIdAndPrimaryKey(orgId, insertedStatusUpdateId);
    Assert.assertEquals(-1, updatedStatusUpdate.getUpdateDate().longValue());

    int affecedRowCount = statusUpdateDao.revokeStatusUpdateByPrimaryKeyAndOrgId(orgId, insertedStatusUpdateId, userId);
    Assert.assertEquals(0, affecedRowCount);


  }

}
