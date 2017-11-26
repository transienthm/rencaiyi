// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.OnboardingStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.user.server.dao.userorg.UserEmploymentDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.OrgMember;
import hr.wozai.service.user.server.model.userorg.UserEmployment;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class UserEmploymentDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(UserEmploymentDaoTest.class);

  @Autowired
  UserEmploymentDao userEmploymentDao;

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
  public void setup() {
  }

  /**
   * Test:
   *  1) insertUserEmployment()
   *  2) findUserEmploymentByOrgIdAndUserId()
   *
   */
  @Test
  public void testInsertUserEmployment() {

    userEmploymentDao.insertUserEmployment(userEmployment);

    UserEmployment insertedUE = userEmploymentDao.findUserEmploymentByOrgIdAndUserId(orgId, userId);
    Assert.assertNotNull(insertedUE);

  }

  @Test
  public void testListUserIdByOrgIdAndOnboardingStatus() {

    int count = 10;
    for (int i = 0; i < count; i++) {
      userEmployment.setOnboardingStatus(OnboardingStatus.SUBMITTED.getCode());
      userEmployment.setUserId(0L + i);
      userEmploymentDao.insertUserEmployment(userEmployment);
    }
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    userEmployment.setUserId(9L);
    userEmploymentDao.insertUserEmployment(userEmployment);

    List<Long> submittedUserIds = userEmploymentDao.listUserIdByOrgIdAndOnboardingStatus(
        orgId, OnboardingStatus.SUBMITTED.getCode(), 1, 20);
    Assert.assertEquals(count, submittedUserIds.size());

    List<Long> approvedUserIds = userEmploymentDao.listUserIdByOrgIdAndOnboardingStatus(
        orgId, OnboardingStatus.APPROVED.getCode(), 1, 20);
    Assert.assertEquals(1, approvedUserIds.size());

  }

  @Test
  public void testListUserEmploymentByOrgIdAndUserId() {

    // prepare
    int count = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      long userId = 0L + i;
      userIds.add(userId);
      userEmployment.setOnboardingStatus(OnboardingStatus.SUBMITTED.getCode());
      userEmployment.setUserId(userId);
      userEmploymentDao.insertUserEmployment(userEmployment);
    }

    // verify
    List<UserEmployment> insertedUEs = userEmploymentDao.listUserEmploymentByOrgIdAndUserId(orgId, userIds);
    Assert.assertEquals(count, insertedUEs.size());

  }

  @Test
  public void testCountUserIdByOrgIdAndOnboardingStatus() {

    int count = 10;
    for (int i = 0; i < count; i++) {
      userEmployment.setOnboardingStatus(OnboardingStatus.SUBMITTED.getCode());
      userEmployment.setUserId(0L + i);
      userEmploymentDao.insertUserEmployment(userEmployment);
    }
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    userEmployment.setUserId(9L);
    userEmploymentDao.insertUserEmployment(userEmployment);

    int submittedCount = userEmploymentDao
        .countUserIdByOrgIdAndOnboardingStatus(orgId, OnboardingStatus.SUBMITTED.getCode());
    Assert.assertEquals(submittedCount, count);
    int approvedCount = userEmploymentDao
        .countUserIdByOrgIdAndOnboardingStatus(orgId, OnboardingStatus.APPROVED.getCode());
  }

  @Test
  public void testListUserIdByOrgIdWhichOnboardingStatusIsNotApproved() {

    // prepare
    int countOne = 3;
    int countTwo = 4;
    for (int i = 0; i < countOne  ; i++) {
      userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
      userEmployment.setUserId(0L + i);
      userEmploymentDao.insertUserEmployment(userEmployment);
    }
    for (int i = 0; i < countTwo  ; i++) {
      userEmployment.setOnboardingStatus(OnboardingStatus.SUBMITTED.getCode());
      userEmployment.setUserId(0L + countOne + i);
      userEmploymentDao.insertUserEmployment(userEmployment);
    }
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    userEmployment.setUserId(9L);
    userEmploymentDao.insertUserEmployment(userEmployment);

    //  verify
    List<Long> notApprovedIds = userEmploymentDao
        .listUserIdByOrgIdAndOnboardingHasApproved(orgId, 0, 1, 20);
    Assert.assertEquals(countOne + countTwo, notApprovedIds.size());

    List<Long> hasApprovedIds = userEmploymentDao
        .listUserIdByOrgIdAndOnboardingHasApproved(orgId, 1, 1, 20);
    Assert.assertEquals(1, hasApprovedIds.size());


  }

  @Test
  public void testCountUserIdByOrgIdWhichOnboardingStatusIsNotApproved() {

    // prepare
    int countOne = 3;
    int countTwo = 4;
    for (int i = 0; i < countOne  ; i++) {
      userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
      userEmployment.setUserId(0L + i);
      userEmploymentDao.insertUserEmployment(userEmployment);
    }
    for (int i = 0; i < countTwo  ; i++) {
      userEmployment.setOnboardingStatus(OnboardingStatus.SUBMITTED.getCode());
      userEmployment.setUserId(0L + countOne + i);
      userEmploymentDao.insertUserEmployment(userEmployment);
    }
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    userEmployment.setUserId(9L);
    userEmploymentDao.insertUserEmployment(userEmployment);

    // verify
    int notApprovedCount = userEmploymentDao.countUserIdByOrgIdAndOnboardingHasApproved(orgId, 0);
    Assert.assertEquals(countOne + countTwo, notApprovedCount);

    int hasApprovedCount = userEmploymentDao.countUserIdByOrgIdAndOnboardingHasApproved(orgId, 1);
    Assert.assertEquals(1, hasApprovedCount);

  }

  @Test
  public void testSublistUserIdByUserStatus() {

    int count = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < count - 1; i++) {
      userEmployment.setUserStatus(UserStatus.INVITED.getCode());
      userEmployment.setUserId(0L + i);
      userEmploymentDao.insertUserEmployment(userEmployment);
      userIds.add(0L + i);
    }
    userEmployment.setUserStatus(UserStatus.ACTIVE.getCode());
    userEmployment.setUserId(9L);
    userEmploymentDao.insertUserEmployment(userEmployment);
    userIds.add(9L);


    List<Long> subUserIds = userEmploymentDao.sublistUserIdByUserStatus(orgId, userIds, UserStatus.INVITED.getCode());
    Assert.assertEquals(count - 1, subUserIds.size());
  }

  @Test
  public void testSublistUserIdByEmploymentStatus() {

    int count = 10;
    List<Long> userIds = new ArrayList<>();
    userEmployment.setUserStatus(UserStatus.ACTIVE.getCode());
    for (int i = 0; i < count - 1; i++) {
      userEmployment.setEmploymentStatus(EmploymentStatus.PROBATIONARY.getCode());
      userEmployment.setUserId(0L + i);
      userEmploymentDao.insertUserEmployment(userEmployment);
      userIds.add(0L + i);
    }
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userEmployment.setUserId(9L);
    userEmploymentDao.insertUserEmployment(userEmployment);
    userIds.add(9L);

    List<Long> subUserIds = userEmploymentDao.sublistUserIdByEmploymentStatus(
        orgId, userIds, EmploymentStatus.PROBATIONARY.getCode());
    Assert.assertEquals(count - 1, subUserIds.size());

  }

  @Test
  public void testListUserIdByOrgIdAndLimitOrderByEnrollDateDesc() {

    int count = 10;
    List<Long> userIds = new ArrayList<>();
    userEmployment.setUserStatus(UserStatus.ACTIVE.getCode());
    for (int i = 0; i < count - 1; i++) {
      userEmployment.setEmploymentStatus(EmploymentStatus.PROBATIONARY.getCode());
      userEmployment.setUserId(0L + i);
      userEmploymentDao.insertUserEmployment(userEmployment);
      userIds.add(0L + i);
    }
    int newStaffCount = 5;
    List<Long> newStaffIds = userEmploymentDao.listUserIdByOrgIdAndLimitOrderByEnrollDateDesc(orgId, newStaffCount);
    Assert.assertEquals(newStaffCount, newStaffIds.size());

  }

  @Test
  public void testListUserIdByOrgIdAndLimitOrderByComingAnniversaryGapAsc() {
//
//    int count = 10;
//    List<Long> userIds = new ArrayList<>();
//    userEmployment.setUserStatus(UserStatus.ACTIVE.getCode());
//    for (int i = 0; i < count - 1; i++) {
//      userEmployment.setEmploymentStatus(EmploymentStatus.PROBATIONARY.getCode());
//      userEmployment.setUserId(0L + i);
//      userEmploymentDao.insertUserEmployment(userEmployment);
//      userIds.add(0L + i);
//    }
//    int anniCount = 5;
//    List<Long> anniIds = userEmploymentDao.listUserIdByOrgIdAndLimitOrderByComingAnniversaryGapAsc(orgId, anniCount);
//    Assert.assertEquals(anniCount, anniIds.size());

  }

  @Test
  public void testUpdateUserEmploymentByOrgIdAndUserIdSelective() {

    userEmploymentDao.insertUserEmployment(userEmployment);

    UserEmployment insertedUE = userEmploymentDao.findUserEmploymentByOrgIdAndUserId(orgId, userId);
    userStatus = UserStatus.RESIGNED.getCode();
    insertedUE.setUserStatus(userStatus);
    insertedUE.setLastModifiedUserId(userId);
    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(insertedUE);

    insertedUE = userEmploymentDao.findUserEmploymentByOrgIdAndUserId(orgId, userId);
    Assert.assertEquals(userStatus, insertedUE.getUserStatus().intValue());

  }


  @Test
  public void testDeleteUserEmploymentByOrgIdAndUserId() {

    userEmploymentDao.insertUserEmployment(userEmployment);

    UserEmployment insertedUE = userEmploymentDao.findUserEmploymentByOrgIdAndUserId(orgId, userId);
    Assert.assertNotNull(insertedUE);

    userEmploymentDao.deleteUserEmploymentByOrgIdAndUserId(orgId, userId, userId);
    insertedUE = userEmploymentDao.findUserEmploymentByOrgIdAndUserId(orgId, userId);
    Assert.assertNull(insertedUE);

  }


}
