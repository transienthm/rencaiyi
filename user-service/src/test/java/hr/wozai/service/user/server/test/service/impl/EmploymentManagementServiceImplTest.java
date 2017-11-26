// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.service.impl;

//import hr.wozai.service.user.server.test.base.TestBase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import hr.wozai.service.servicecommons.commons.enums.DocumentScenario;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.model.document.Document;
import hr.wozai.service.user.server.model.userorg.StatusUpdate;
import hr.wozai.service.user.server.service.DocumentService;
import hr.wozai.service.user.server.service.EmployeeManagementService;
import hr.wozai.service.user.server.service.S3DocumentService;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-25
 */
public class EmploymentManagementServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(EmploymentManagementServiceImplTest.class);

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  private EmployeeManagementService employeeManagementService;

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

  @Before
  public void setup() {
  }

  @Test
  public void test() {

  }

}
