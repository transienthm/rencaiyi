// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.dao.userorg.JobTransferDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.JobTransfer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class JobTransferDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(JobTransferDaoTest.class);

  @Autowired
  JobTransferDao jobTransferDao;

  long orgId = 999L;
  long userId = 1999L;
  String transferType = "正常转正";
  long longValue = 2999L;
  JobTransfer jobTransfer = null;

  {
    jobTransfer = new JobTransfer();
    jobTransfer.setOrgId(orgId);
    jobTransfer.setUserId(userId);
    jobTransfer.setTransferType(transferType);
    jobTransfer.setTransferDate(longValue);
    jobTransfer.setBeforeTeamId(longValue);
    jobTransfer.setBeforeReporterId(longValue);
    jobTransfer.setBeforeJobLevelId(longValue);
    jobTransfer.setBeforeJobTitleId(longValue);
    jobTransfer.setAfterTeamId(longValue);
    jobTransfer.setAfterReporterId(longValue);
    jobTransfer.setAfterJobLevelId(longValue);
    jobTransfer.setAfterJobTitleId(longValue);
    jobTransfer.setCreatedUserId(userId);
  }


  @Before
  public void setup() {
  }

  @Test
  public void testInsertJobTransfer() {

    long jobTransferId = jobTransferDao.insertJobTransfer(jobTransfer);
    JobTransfer insertedJobTransfer = jobTransferDao.findJobTransferByOrgIdAndPrimaryKey(orgId, jobTransferId);
    Assert.assertEquals(transferType, insertedJobTransfer.getTransferType());

  }

  @Test
  public void testListJobTransferByOrgIdOrderByCreatedTimeDesc() {

    int count = 10;
    for (int i = 0; i < count; i++) {
      jobTransferDao.insertJobTransfer(jobTransfer);
    }
    List<JobTransfer> jobTransfers = jobTransferDao.listJobTransferByOrgIdOrderByCreatedTimeDesc(orgId, 1, 20);
    Assert.assertEquals(count, jobTransfers.size());

  }

  @Test
  public void testListJobTransferByOrgIdAndPrimaryKeys() {
    List<Long> jobTransferIds = new ArrayList<>();
    int count = 10;
    for (int i = 0; i < count; i++) {
      jobTransferIds.add(jobTransferDao.insertJobTransfer(jobTransfer));
    }
    List<JobTransfer> jobTransfers = jobTransferDao.listJobTransferByOrgIdAndPrimaryKeys(orgId, jobTransferIds);
    Assert.assertEquals(count, jobTransfers.size());
  }

  @Test
  public void testCountJobTransferByOrgId() {

    // prepare
    List<Long> jobTransferIds = new ArrayList<>();
    int count = 10;
    for (int i = 0; i < count; i++) {
      jobTransferIds.add(jobTransferDao.insertJobTransfer(jobTransfer));
    }

    // verify
    int insertedCount = jobTransferDao.countJobTransferByOrgId(orgId);
    Assert.assertEquals(count, insertedCount);

  }



}
