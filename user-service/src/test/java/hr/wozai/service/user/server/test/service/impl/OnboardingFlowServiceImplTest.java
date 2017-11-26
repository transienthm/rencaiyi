// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.service.impl;

import com.mysql.jdbc.TimeUtil;

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.enums.UserGender;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.model.userorg.UserProfile;
import hr.wozai.service.user.server.service.EmployeeManagementService;
import hr.wozai.service.user.server.service.OnboardingFlowService;
import hr.wozai.service.user.server.service.ProfileFieldService;
import hr.wozai.service.user.server.service.UserProfileService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-25
 */
public class OnboardingFlowServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(OnboardingFlowServiceImplTest.class);

  @Autowired
  private OnboardingFlowService onboardingFlowService;

  @Autowired
  private EmployeeManagementService employeeManagementService;

  @Autowired
  private UserProfileService userProfileService;

  @Autowired
  private ProfileFieldService profileFieldService;

  long mockOrgId = 19999999L;
  long mockUserId = 29999999L;

  private String fullName = "马人才易";
  private String emailAddress = "mawozai@sqian.com";
  private String mobilePhone = "13566677777";
  private String personalEmail = "mawozaisqian@qq.com";
  private Integer gender = UserGender.MALE.getCode();

  private String emailAddressOfSuperAdmin = "wozaisuperadmin@sqian.com";
  private String passwordPlainTextOfSuperAdmin = "Wozai123";

  UserProfile userProfile = null;
  UserEmployment userEmployment = null;
  Map<String, String> fieldValues = null;
  List<Long> roleIds = null;

  @Before
  public void init() {}

  @Test
  public void testLaunchOnboardingFlowForIndividualStaffByHR() {

//    long insertedUserId = onboardingFlowService.launchOnboardingFlowForIndividualStaffByHR(
//        orgId, userProfile, fieldValues, userEmployment, roleIds, actorUserId, adminUserId);
//    System.out.println("insertedUserId=" + insertedUserId);
//
//    // normal case
//    CoreUserProfile insertedCUP = userProfileService.getOldCoreUserProfileByOrgIdAndUserId(orgId, insertedUserId);
//    System.out.println("insertedCUP=" + insertedCUP);
//    Assert.assertNotNull(insertedCUP);
//
//    // adnormal case 1: dup email
//    try {
//      onboardingFlowService.launchOnboardingFlowForIndividualStaffByHR(
//          orgId, userProfile, fieldValues, userEmployment, roleIds, actorUserId, adminUserId);
//    } catch (ServiceStatusException e) {
//      System.out.printf("Gotcha: 1");
//      Assert.assertEquals(ServiceStatus.AS_EMAIL_EXIST.getCode(), e.getServiceStatus().getCode());
//    }

  }

  @Test
  public void testCreateOrgAndFirstUser() {

    Map<String, String> fieldValues = new HashMap<>();
    fieldValues.put(SystemProfileField.FULL_NAME.getReferenceName(), fullName);
    fieldValues.put(SystemProfileField.EMAIL_ADDRESS.getReferenceName(), emailAddress);
    fieldValues.put(SystemProfileField.MOBILE_PHONE.getReferenceName(), mobilePhone);
    fieldValues.put(SystemProfileField.PERSONAL_EMAIL.getReferenceName(), personalEmail);
    fieldValues.put(SystemProfileField.GENDER.getReferenceName(), gender + "");

    Org org = new Org();
    org.setFullName(fullName);
    org.setShortName(fullName);
    org.setAvatarUrl(fullName);
    org.setTimeZone(1);

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setFulltimeEnrollDate(TimeUtil.getCurrentTimeNanosOrMillis());
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());

    CoreUserProfile addedCUP = onboardingFlowService.createOrgAndFirstUser(
        org, fieldValues, userEmployment);
    Assert.assertNotNull(addedCUP);

  }

  @Test
  public void testBatchImportStaff() {

    // prepare

    // 开户
    Map<String, String> fieldValues = new HashMap<>();
    fieldValues.put(SystemProfileField.FULL_NAME.getReferenceName(), fullName);
    fieldValues.put(SystemProfileField.EMAIL_ADDRESS.getReferenceName(), emailAddress);
    fieldValues.put(SystemProfileField.MOBILE_PHONE.getReferenceName(), mobilePhone);

    Org org = new Org();
    org.setFullName(fullName);
    org.setShortName(fullName);
    org.setAvatarUrl(fullName);
    org.setTimeZone(1);

    UserEmployment userEmployment = new UserEmployment();

    CoreUserProfile addedCUP = onboardingFlowService.createOrgAndFirstUser(
        org, fieldValues, userEmployment);
    Assert.assertNotNull(addedCUP);

    // verify
    String fullName = "朱人才易";
    String mobilePhone = "13655566666";
    String personalEmail = "13655566666@ppqq.com";
    String gender = "男";
    int importCount = 3;
    List<List<String>> fieldValueList = new ArrayList<>();
    for (int i = 0; i < importCount; i++) {
      List<String> oneFieldValues = new ArrayList<>();
      oneFieldValues.add(fullName);
      oneFieldValues.add(i + personalEmail);
      oneFieldValues.add(mobilePhone);
//      oneFieldValues.add(personalEmail);
//      oneFieldValues.add(gender);
//      oneFieldValues.add("全职");
//      oneFieldValues.add("2016/02/01");
//      oneFieldValues.add("正式");
      fieldValueList.add(oneFieldValues);
    }

    List<Long> userIds = onboardingFlowService
        .batchImportStaff(addedCUP.getOrgId(), fieldValueList, addedCUP.getUserId(), 0);
    Assert.assertEquals(importCount, userIds.size());

  }

  @Test
  public void testDeleteUser() {
//
//    // normal case
//    userEmployment.setFulltimeEnrollDate(TimeUtils.getNowTimestmapInMillis() + TimeConst.ONE_DAY_IN_MILLIS * 10);
//    long insertedUserId = onboardingFlowService.launchOnboardingFlowForIndividualStaffByHR(
//        orgId, userProfile, fieldValues, userEmployment, roleIds, actorUserId, adminUserId);
//    System.out.println("insertedUserId=" + insertedUserId);
//
//    CoreUserProfile insertedCUP = userProfileService.getOldCoreUserProfileByOrgIdAndUserId(orgId, insertedUserId);
//    System.out.println("insertedCUP=" + insertedCUP);
//    Assert.assertNotNull(insertedCUP);
//
//    // deleteUser
//    employeeManagementService.deleteUser(orgId, insertedUserId, actorUserId);
//    try {
//      userProfileService.getOldCoreUserProfileByOrgIdAndUserId(orgId, insertedUserId);
//    } catch (ServiceStatusException e) {
//      System.out.println("Gotta: 2");
//      Assert.assertEquals(ServiceStatus.UP_USER_NOT_FOUND.getCode(), e.getServiceStatus().getCode());
//    }
//
//
//    // abnormal case
//    insertedUserId = onboardingFlowService.launchOnboardingFlowForIndividualStaffByHR(
//        orgId, userProfile, fieldValues, userEmployment, roleIds, actorUserId, adminUserId);
//    System.out.println("insertedUserId=" + insertedUserId);
//
//    insertedCUP = userProfileService.getOldCoreUserProfileByOrgIdAndUserId(orgId, insertedUserId);
//    System.out.println("insertedCUP=" + insertedCUP);
//    Assert.assertNotNull(insertedCUP);
//
//    // deleteUser
//    try {
//      employeeManagementService.deleteUser(orgId, insertedUserId, actorUserId);
//    } catch (ServiceStatusException e) {
//      LOGGER.info("Gotta: 1");
//      Assert.assertEquals(ServiceStatus.UP_CANNOT_DELETE_ACTIVE_USER.getCode(), e.getServiceStatus().getCode());
//    }



  }


}
