// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
public class UserProfileDaoTest extends TestBase {
//
//  private static Logger LOGGER = LoggerFactory.getLogger(UserProfileDaoTest.class);
//
//  /******************* Dao to test *******************/
//
//  @Autowired
//  UserProfileDao userProfileDao;
//
//  /******************* Helper vars *******************/
//
//  @Autowired
//  OnboardingFlowService onboardingFlowService;
//
//  @Autowired
//  ProfileTemplateService profileTemplateService;
//
//  @Autowired
//  ProfileFieldService profileFieldService;
//
//  String orgFullName = "测试北京闪签科技有限公司";
//  String orgShortName = "测试闪签";
//  String orgAvatarUrl = "http://";
//  int timeZone = 1;
//
//  int userStatus = UserStatus.INVITED.getCode();
//  String emailAddress = "blahiasidaosndit@sqian.com";
//  String mobilePhone = "13566677888";
//  String fullName = "王山前";
//  String citizenId = "101098199008180765";
//
//  long mockOrgId = 299999999L;
//  long mockUserId = 399999999L;
//  long mockActorUserId = 499999999;
//  long mochAdminUserId = 599999999;
//  long mockOnboardingTemplateId = 800000000;
//
//  long profileTemplateId = 0L;
//
//  /**
//   * Test:
//   *  1) insertUserProfile()
//   *  2) findCoreUserProfileFromUserProfileByOrgIdAndUserId()
//   *
//   * @throws IllegalAccessException
//   */
//  @Test
//  public void testAll() throws IllegalAccessException {
//
//    // setup
//    long profileTemplateId = InitializationUtils
//        .initPresetProfileTemplateAndFields(profileTemplateService, profileFieldService, mockOrgId);
//    UserProfile userProfile = new UserProfile();
//    userProfile.setOrgId(mockOrgId);
//    userProfile.setUserId(mockUserId);
//    userProfile.setOnboardingTemplateId(mockOnboardingTemplateId);
//    userProfile.setProfileTemplateId(profileTemplateId);
////    userProfile.setUserStatus(userStatus);
//    userProfile.setCreatedUserId(mockActorUserId);
//    List<ProfileField> dataFields = profileFieldService
//        .listDataProfileFieldOfTemplate(mockOrgId, profileTemplateId);
//    for (ProfileField dataField: dataFields) {
//      if (dataField.getIsMandatory() == 1) {
//        dataField.setDataValue(dataField.getDisplayName());
//      }
//    }
//    userProfile.setProfileFields(dataFields);
//
//    // do
//    userProfileDao.insertUserProfile(userProfile);
//
////    // verify
////    OldCoreUserProfile insertedCUP = userProfileDao
////        .findCoreUserProfileFromUserProfileByOrgIdAndUserId(mockOrgId, mockUserId, dataFields);
////    LOGGER.info("insertedCUP={}", insertedCUP);
////    Assert.assertEquals(userStatus, insertedCUP.getUserStatus().intValue());
//
//  }
//
//  @Test
//  public void testFindProfileTemplateIdByOrgIdAndUserId() {
//
//    // prepare
//    int beforeUserStatus = UserStatus.IMPORTED.getCode();
//    long profileTemplateId = InitializationUtils
//        .initPresetProfileTemplateAndFields(profileTemplateService, profileFieldService, mockOrgId);
//    UserProfile userProfile = new UserProfile();
//    userProfile.setOrgId(mockOrgId);
//    userProfile.setUserId(mockUserId);
//    userProfile.setOnboardingTemplateId(mockOnboardingTemplateId);
//    userProfile.setProfileTemplateId(profileTemplateId);
////    userProfile.setUserStatus(beforeUserStatus);
//    userProfile.setCreatedUserId(mockActorUserId);
//    List<ProfileField> dataFields = profileFieldService
//        .listDataProfileFieldOfTemplate(mockOrgId, profileTemplateId);
//    for (ProfileField dataField: dataFields) {
//      if (dataField.getIsMandatory() == 1) {
//        dataField.setDataValue(dataField.getDisplayName());
//      }
//    }
//    userProfile.setProfileFields(dataFields);
//    userProfileDao.insertUserProfile(userProfile);
//
//    // verify
//    long insertedProfileTemplate = userProfileDao.findProfileTemplateIdByOrgIdAndUserId(mockOrgId, mockUserId);
//    Assert.assertEquals(profileTemplateId, insertedProfileTemplate);
//
//  }
//
//  @Test
//  public void testUpdateUserStatusByOrgIdAndUserId() {
//
//    // prepare
//    int beforeUserStatus = UserStatus.IMPORTED.getCode();
//    long profileTemplateId = InitializationUtils
//        .initPresetProfileTemplateAndFields(profileTemplateService, profileFieldService, mockOrgId);
//    UserProfile userProfile = new UserProfile();
//    userProfile.setOrgId(mockOrgId);
//    userProfile.setUserId(mockUserId);
//    userProfile.setOnboardingTemplateId(mockOnboardingTemplateId);
//    userProfile.setProfileTemplateId(profileTemplateId);
////    userProfile.setUserStatus(beforeUserStatus);
//    userProfile.setCreatedUserId(mockActorUserId);
//    List<ProfileField> dataFields = profileFieldService
//        .listDataProfileFieldOfTemplate(mockOrgId, profileTemplateId);
//    for (ProfileField dataField: dataFields) {
//      if (dataField.getIsMandatory() == 1) {
//        dataField.setDataValue(dataField.getDisplayName());
//      }
//    }
//    userProfile.setProfileFields(dataFields);
//    userProfileDao.insertUserProfile(userProfile);
//
//    // verify
//    List<ProfileField> insertedDataFields =
//        profileFieldService.listDataProfileFieldOfTemplate(mockOrgId, profileTemplateId);
//    UserProfile insertedUP =
//        userProfileDao.findUserProfileWithFieldDataByOrgIdAndUserId(mockOrgId, mockUserId, insertedDataFields);
////    Assert.assertEquals(beforeUserStatus, insertedUP.getUserStatus().intValue());
//
//    int afterUserStatus = UserStatus.ACTIVE.getCode();
////    insertedUP.setUserStatus(afterUserStatus);
//    insertedUP.setLastModifiedUserId(mockActorUserId);
//    userProfileDao.updateUserStatusByOrgIdAndUserId(insertedUP);
//    UserProfile updatedUP =
//        userProfileDao.findUserProfileWithFieldDataByOrgIdAndUserId(mockOrgId, mockUserId, insertedDataFields);
////    Assert.assertEquals(afterUserStatus, updatedUP.getUserStatus().intValue());
//
//  }
//
//  @Test
//  public void testUpdateUserProfileByOrgIdAndUserIdSelective() {
//
//    // prepare
//    int beforeUserStatus = UserStatus.IMPORTED.getCode();
//    long profileTemplateId = InitializationUtils
//        .initPresetProfileTemplateAndFields(profileTemplateService, profileFieldService, mockOrgId);
//    UserProfile userProfile = new UserProfile();
//    userProfile.setOrgId(mockOrgId);
//    userProfile.setUserId(mockUserId);
//    userProfile.setOnboardingTemplateId(mockOnboardingTemplateId);
//    userProfile.setProfileTemplateId(profileTemplateId);
////    userProfile.setUserStatus(beforeUserStatus);
//    userProfile.setCreatedUserId(mockActorUserId);
//    List<ProfileField> dataFields = profileFieldService
//        .listDataProfileFieldOfTemplate(mockOrgId, profileTemplateId);
//    for (ProfileField dataField: dataFields) {
//      if (dataField.getIsMandatory() == 1) {
//        dataField.setDataValue(dataField.getDisplayName());
//      }
//    }
//    userProfile.setProfileFields(dataFields);
//    userProfileDao.insertUserProfile(userProfile);
//
//    // verify
//    List<ProfileField> insertedDataFields =
//        profileFieldService.listDataProfileFieldOfTemplate(mockOrgId, profileTemplateId);
//    UserProfile insertedUP =
//        userProfileDao.findUserProfileWithFieldDataByOrgIdAndUserId(mockOrgId, mockUserId, insertedDataFields);
//    String newFullName = SystemFieldConsts.FULL_NAME_REF_NAME + SystemFieldConsts.FULL_NAME_REF_NAME;
//    for (ProfileField profileField: insertedUP.getProfileFields()) {
//      if (SystemFieldConsts.FULL_NAME_REF_NAME.equals(profileField.getReferenceName())) {
//        profileField.setDataValue(newFullName);
//        break;
//      }
//    }
//    insertedUP.setLastModifiedUserId(mockActorUserId);
//    userProfileDao.updateUserProfileByOrgIdAndUserIdSelective(insertedUP);
//
//    UserProfile updateUP =
//        userProfileDao.findUserProfileWithFieldDataByOrgIdAndUserId(mockOrgId, mockUserId, insertedDataFields);
//    Assert.assertEquals(mockActorUserId, updateUP.getLastModifiedUserId().longValue());
//    String updateFullName = null;
//    for (ProfileField profileField: updateUP.getProfileFields()) {
//      if (SystemFieldConsts.FULL_NAME_REF_NAME.equals(profileField.getReferenceName())) {
//        updateFullName = profileField.getDataValue();
//      }
//    }
//    Assert.assertEquals(newFullName, updateFullName);
//
//  }
//
//  @Test
//  public void testWipeFieldValueByOrgIdAndProfileTemplateIdAndPhysicalIndex() {
//
//    // prepare
//    long profileTemplateId = InitializationUtils
//        .initPresetProfileTemplateAndFields(profileTemplateService, profileFieldService, mockOrgId);
//    UserProfile userProfile = new UserProfile();
//    userProfile.setOrgId(mockOrgId);
//    userProfile.setUserId(mockUserId);
//    userProfile.setOnboardingTemplateId(mockOnboardingTemplateId);
//    userProfile.setProfileTemplateId(profileTemplateId);
////    userProfile.setUserStatus(userStatus);
//    userProfile.setCreatedUserId(mockActorUserId);
//    List<ProfileField> dataFields = profileFieldService
//        .listDataProfileFieldOfTemplate(mockOrgId, profileTemplateId);
//    for (ProfileField dataField: dataFields) {
//      if (dataField.getIsMandatory() == 1) {
//        dataField.setDataValue(dataField.getDisplayName());
//      }
//    }
//    userProfile.setProfileFields(dataFields);
//    userProfileDao.insertUserProfile(userProfile);
//
//    int physicalIndexOfFirstShortTextField = 0;
//    String dataValueOfFirstShortTextField = null;
//    for (ProfileField dataField: dataFields) {
//      if (DataType.SHORT_TEXT.getCode() == dataField.getDataType()) {
//        physicalIndexOfFirstShortTextField = dataField.getPhysicalIndex();
//        dataValueOfFirstShortTextField = dataField.getDisplayName();
//        break;
//      }
//    }
//    UserProfile insertedUP = userProfileDao
//        .findUserProfileWithFieldDataByOrgIdAndUserId(mockOrgId, mockUserId, dataFields);
//    String insertedDataValueOfFirstShortTextField = null;
//    for (ProfileField profileField: insertedUP.getProfileFields()) {
//      if (profileField.getPhysicalIndex().intValue() == physicalIndexOfFirstShortTextField) {
//        LOGGER.info("Gotcha: 1");
//        insertedDataValueOfFirstShortTextField = profileField.getDataValue();
//        Assert.assertEquals(dataValueOfFirstShortTextField, insertedDataValueOfFirstShortTextField);
//        break;
//      }
//    }
//
//    // do
//    int updatedRows = userProfileDao.wipeFieldValueByOrgIdAndProfileTemplateIdAndPhysicalIndex(
//        mockOrgId, profileTemplateId, physicalIndexOfFirstShortTextField, mockActorUserId);
//
//    // verify
//    for (ProfileField dataField: dataFields) {
//      dataField.setDataValue(null);
//    }
//    UserProfile updatedUP = userProfileDao
//        .findUserProfileWithFieldDataByOrgIdAndUserId(mockOrgId, mockUserId, dataFields);
//    for (ProfileField profileField: updatedUP.getProfileFields()) {
//      if (profileField.getPhysicalIndex().intValue() == physicalIndexOfFirstShortTextField) {
//        LOGGER.info("Gotcha: 2");
//        Assert.assertNull(profileField.getDataValue());
//        break;
//      }
//    }
//
//  }

}
