// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.OnboardingStatus;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.service.UserEmploymentService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class UserEmploymentServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(UserEmploymentServiceImplTest.class);

  @Autowired
  UserEmploymentService userEmploymentService;

  long orgId = 1999;
  long userId = 2999;
  int userStatus = UserStatus.ACTIVE.getCode();
  int onboardingStatus = OnboardingStatus.APPROVED.getCode();
  int contractType = ContractType.FULLTIME.getCode();
  int employmentStatus = EmploymentStatus.REGULAR.getCode();
  UserEmployment userEmployment = null;

  {
    userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setUserId(userId);
    userEmployment.setUserStatus(userStatus);
    userEmployment.setOnboardingStatus(onboardingStatus);
    userEmployment.setContractType(contractType);
    userEmployment.setEmploymentStatus(employmentStatus);
    userEmployment.setCreatedUserId(userId);
  }

  @Before
  public void init() {
  }

  @Test
  public void testAddUserEmployment() {

    userEmploymentService.addUserEmployment(userEmployment);

    UserEmployment insertedUE = userEmploymentService.getUserEmployment(orgId, userId);
    Assert.assertNotNull(insertedUE);

  }

  @Test
  public  void testUpdateUserEmployment() {

    userEmploymentService.addUserEmployment(userEmployment);

    UserEmployment insertedUE = userEmploymentService.getUserEmployment(orgId, userId);
    Assert.assertNotNull(insertedUE);

    userStatus = UserStatus.RESIGNED.getCode();
    insertedUE.setUserStatus(userStatus);
    insertedUE.setLastModifiedUserId(userId);
    userEmploymentService.updateUserEmployment(insertedUE);

    insertedUE = userEmploymentService.getUserEmployment(orgId, userId);
    Assert.assertEquals(userStatus, insertedUE.getUserStatus().intValue());

  }

  @Test
  public void testListUserIdByOrgIdAndOnboardingStatus() {

    // prepare
    int count = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      userEmployment.setUserId(0L + i);
      userEmployment.setOnboardingStatus(OnboardingStatus.SUBMITTED.getCode());
      userEmploymentService.addUserEmployment(userEmployment);
      userIds.add(userEmployment.getUserId());
    }
    userEmployment.setUserId(9L);
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    userEmploymentService.addUserEmployment(userEmployment);
    userIds.add(userEmployment.getUserId());

    // verify
    List<Long> submittedUserIds = userEmploymentService
        .listUserIdByOrgIdAndOnboardingStatus(orgId, OnboardingStatus.SUBMITTED.getCode(), 1, 20);
    Assert.assertEquals(count, submittedUserIds.size());

    List<Long> approvedUserIds = userEmploymentService
        .listUserIdByOrgIdAndOnboardingStatus(orgId, OnboardingStatus.APPROVED.getCode(), 1, 20);
    Assert.assertEquals(1, approvedUserIds.size());

  }

  @Test
  public void testCountUserIdByOrgIdAndOnboardingStatus() {

    // prepare
    int count = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      userEmployment.setUserId(0L + i);
      userEmployment.setOnboardingStatus(OnboardingStatus.SUBMITTED.getCode());
      userEmploymentService.addUserEmployment(userEmployment);
      userIds.add(userEmployment.getUserId());
    }
    userEmployment.setUserId(9L);
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    userEmploymentService.addUserEmployment(userEmployment);
    userIds.add(userEmployment.getUserId());

    // verify
    int submittedCount = userEmploymentService
        .countUserIdByOrgIdAndOnboardingStatus(orgId, OnboardingStatus.SUBMITTED.getCode());
    Assert.assertEquals(count, submittedCount);

    int approvedCount = userEmploymentService
        .countUserIdByOrgIdAndOnboardingStatus(orgId, OnboardingStatus.APPROVED.getCode());
    Assert.assertEquals(1, approvedCount);

  }

  @Test
  public void testListUserIdByOrgIdAndOnboardingHasApproved() {

    // prepare
    int countOne = 3;
    int countTwo = 4;
    for (int i = 0; i < countOne  ; i++) {
      userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
      userEmployment.setUserId(0L + i);
      userEmploymentService.addUserEmployment(userEmployment);
    }
    for (int i = 0; i < countTwo  ; i++) {
      userEmployment.setOnboardingStatus(OnboardingStatus.SUBMITTED.getCode());
      userEmployment.setUserId(0L + countOne + i);
      userEmploymentService.addUserEmployment(userEmployment);
    }
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    userEmployment.setUserId(9L);
    userEmploymentService.addUserEmployment(userEmployment);

    // verify
    List<Long> notApprovedUserIds = userEmploymentService.listUserIdByOrgIdAndOnboardingHasApproved(orgId, 0, 1, 20);
    Assert.assertEquals(countOne + countTwo, notApprovedUserIds.size());

    List<Long> hasApprovedUserIds = userEmploymentService.listUserIdByOrgIdAndOnboardingHasApproved(orgId, 1, 1, 20);
    Assert.assertEquals(1, hasApprovedUserIds.size());

  }

  @Test
  public void testCountUserIdByOrgIdAndOnboardingHasApproved() {

    // prepare
    int countOne = 3;
    int countTwo = 4;
    for (int i = 0; i < countOne  ; i++) {
      userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
      userEmployment.setUserId(0L + i);
      userEmploymentService.addUserEmployment(userEmployment);
    }
    for (int i = 0; i < countTwo  ; i++) {
      userEmployment.setOnboardingStatus(OnboardingStatus.SUBMITTED.getCode());
      userEmployment.setUserId(0L + countOne + i);
      userEmploymentService.addUserEmployment(userEmployment);
    }
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    userEmployment.setUserId(9L);
    userEmploymentService.addUserEmployment(userEmployment);

    // verify
    int notApprovedCount = userEmploymentService.countUserIdByOrgIdAndOnboardingHasApproved(orgId, 0);
    Assert.assertEquals(countOne + countTwo, notApprovedCount);

    int hasApprovedCount = userEmploymentService.countUserIdByOrgIdAndOnboardingHasApproved(orgId, 1);
    Assert.assertEquals(1, hasApprovedCount);

  }


  @Test
  public void sublistUserIdByUserStatus() {

    int count = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < count - 1; i++) {
      userEmployment.setUserId(0L + i);
      userEmployment.setUserStatus(UserStatus.ACTIVE.getCode());
      userEmploymentService.addUserEmployment(userEmployment);
      userIds.add(userEmployment.getUserId());
    }
    userEmployment.setUserId(9L);
    userEmployment.setUserStatus(UserStatus.INVITED.getCode());
    userEmploymentService.addUserEmployment(userEmployment);
    userIds.add(userEmployment.getUserId());

    List<Long> subbedUserIds = userEmploymentService
        .sublistUserIdByUserStatus(orgId, userIds, UserStatus.ACTIVE.getCode());
    Assert.assertEquals(count - 1, subbedUserIds.size());
  }

  @Test
  public void testSublistUserIdWhichAreNotResignedByEmploymentStatus() {

    int count = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < count - 1; i++) {
      userEmployment.setUserId(0L + i);
      userEmployment.setEmploymentStatus(EmploymentStatus.PROBATIONARY.getCode());
      userEmploymentService.addUserEmployment(userEmployment);
      userIds.add(userEmployment.getUserId());
    }
    userEmployment.setUserId(9L);
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userIds.add(userEmployment.getUserId());

    List<Long> subUserIds = userEmploymentService.sublistUserIdNotResignedByEmploymentStatus(
        orgId, userIds, EmploymentStatus.PROBATIONARY.getCode());
    Assert.assertEquals(count - 1, subUserIds.size());
  }

  @Test
  public void testListUserIdOfNewStaffByOrgId() {

    int count = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < count - 1; i++) {
      userEmployment.setUserId(0L + i);
      userEmployment.setEmploymentStatus(EmploymentStatus.PROBATIONARY.getCode());
      userEmployment.setContractType(ContractType.INTERNSHIP.getCode());
      userEmployment.setInternshipEnrollDate(TimeUtils.getNowTimestmapInMillis());
      userEmploymentService.addUserEmployment(userEmployment);
      userIds.add(userEmployment.getUserId());
    }

    List<Long> newStaffIds = userEmploymentService.listUserIdOfNewStaffByOrgId(orgId);
    Assert.assertEquals(5, newStaffIds.size());

  }

  @Test
  public void testListUserIdOfEnrollAnniversaryByOrgId() {
//
//    int count = 10;
//    List<Long> userIds = new ArrayList<>();
//    for (int i = 0; i < count - 1; i++) {
//      userEmployment.setUserId(0L + i);
//      userEmployment.setEmploymentStatus(EmploymentStatus.PROBATIONARY.getCode());
//      userEmployment.setContractType(ContractType.INTERNSHIP.getCode());
//      userEmployment.setInternshipEnrollDate(TimeUtils.getNowTimestmapInMillis());
//      userEmploymentService.addUserEmployment(userEmployment);
//      userIds.add(userEmployment.getUserId());
//    }
//
//    List<Long> anniIds = userEmploymentService.listUserIdOfEnrollAnniversaryByOrgId(orgId);
//    Assert.assertEquals(5, anniIds.size());

  }

  @Test
  public  void testDeleteUserEmployment() {

    userEmploymentService.addUserEmployment(userEmployment);

    UserEmployment insertedUE = userEmploymentService.getUserEmployment(orgId, userId);
    Assert.assertNotNull(insertedUE);

    userEmploymentService.deleteUserEmployment(orgId, userId, userId);
    try {
      userEmploymentService.getUserEmployment(orgId, userId);
    } catch (ServiceStatusException e) {
      LOGGER.error("Got you");
      Assert.assertEquals(ServiceStatus.UP_USER_NOT_FOUND.getCode(), e.getServiceStatus().getCode());
    }

  }




}
