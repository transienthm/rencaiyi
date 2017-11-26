package hr.wozai.service.user.server.test.service.impl;

import com.mysql.jdbc.TimeUtil;

import org.junit.After;
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

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.OnboardingStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.enums.UserGender;
import hr.wozai.service.user.server.dao.userorg.CoreUserProfileDao;
import hr.wozai.service.user.server.dao.userorg.UserEmploymentDao;
import hr.wozai.service.user.server.dao.userorg.UserProfileConfigDao;
import hr.wozai.service.user.server.factory.UserProfileConfigFactory;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.model.userorg.UserProfile;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;
import hr.wozai.service.user.server.service.OnboardingFlowService;
import hr.wozai.service.user.server.service.ProfileFieldService;
import hr.wozai.service.user.server.service.UserProfileService;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * UserProfileServiceImpl Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Mar 21, 2016</pre>
 */
public class UserProfileServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(UserProfileServiceImplTest.class);

  @Autowired
  private UserProfileService userProfileService;

  @Autowired
  private ProfileFieldService profileFieldService;

  @Autowired
  private OnboardingFlowService onboardingFlowService;

  @Autowired
  private UserProfileConfigFactory userProfileConfigFactory;

  @Autowired
  private UserProfileConfigDao userProfileConfigDao;

  @Autowired
  private CoreUserProfileDao coreUserProfileDao;

  @Autowired
  private UserEmploymentDao userEmploymentDao;

  private long mockOrgId = 19999999L;
  private long mockUserId = 29999999L;
  private long mockOnboardingTempalteId = 39999999L;
  private long mockProfileTemplateId = 49999999L;
  private long mockActorUserId = 59999999L;

  private String fullName = "马人才易";
  private String emailAddress = "mawozai@sqian.com";
  private String mobilePhone = "13566677777";
  private String personalEmail = "mawozaisqian@qq.com";
  private Integer gender = UserGender.MALE.getCode();

  private static String emailAddressOfSuperAdmin = "superadminwozai@sqian.com";
  private static String passwordOfSuperAdmin = "Wozai123";


  @Before
  public void before() throws Exception {
  }

  @After
  public void after() throws Exception {
  }

  /**
   * Method: addUserProfile(UserProfile userProfile)
   */
  @Test
  public void testAddUserProfile() throws Exception {

  }

  @Test
  public  void testAddUserProfileForOnboarding() {
//
//    Map<String, String> fieldValues = new HashMap<>();
//    fieldValues.put("fullName", "新员工姓名");
//    fieldValues.put("emailAddress", "blabla@bla.com");
//    fieldValues.put("mobilePhone", "15022299999");
//    fieldValues.put("citizenId", "100200192827367483");
//    fieldValues.put("enrollDate", "1460464012983");
//
//
//    userProfileService.addUserProfileForOnboarding(userProfile, fieldValues);
//    UserProfile insertedUP = userProfileService.getUserProfile(orgId, userId);
//    System.out.println("insertedUP=" + insertedUP);
//    Assert.assertTrue(insertedUP.getProfileFields().size() > 0);
//    for (ProfileField profileField: insertedUP.getProfileFields()) {
//      if (profileField.getIsMandatory() == 1) {
//        Assert.assertTrue(profileField.getDataValue().length() > 0);
//      }
//    }
//
//    fieldValues.remove("citizenId");
//    try {
//      userProfileService.addUserProfileForOnboarding(userProfile, fieldValues);
//    } catch (ServiceStatusException e) {
//      System.out.println("Now catch: 1");
//      Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), e.getServiceStatus().getCode());
//    }
//
//    CoreUserProfile coreUserProfile = userProfileService.getOldCoreUserProfileByOrgIdAndUserId(orgId, userId);
//    System.out.println("CUP=" + coreUserProfile);

  }

  /**
   * Method: addCoreUserProfile(CoreUserProfile coreUserProfile)
   */
  @Test
  public void testAddCoreUserProfile() throws Exception {
//TODO: Test goes here... 
  }

  /**
   * Method: addCoreUserProfileFromUserProfile(long orgId, long userId, long actorUserId)
   */
  @Test
  public void testAddCoreUserProfileFromUserProfile() throws Exception {
//TODO: Test goes here... 
  }

  @Test
  public void testListCoreUserProfileByCreatedUserId() {

  }

  /**
   * Method: getProfileTemplateId(long orgId, long userId)
   */
  @Test
  public void testGetProfileTemplateId() throws Exception {
//TODO: Test goes here... 
  }


  @Test
  public void testUpdateUserStatus() {

    // TODO
//    List<ProfileField> dataFields = profileFieldService.listDataProfileFieldOfTemplate(orgId, profileTemplateId);
//    for (ProfileField dataField : dataFields) {
//      if (dataField.getIsMandatory() == 1) {
//        dataField.setDataValue(dataField.getDisplayName());
//      }
//    }
//    userProfile.setProfileFields(dataFields);
//    userProfileService.addUserProfile(userProfile);
//
//    userProfile.setUserStatus(UserStatus.ONBOARDING.getCode());
//    userProfileService.updateUserStatus(orgId, userId, UserStatus.CANCELLED.getCode(), userId);
//
//    UserProfile insertedUserProfile = userProfileService.getUserProfile(orgId, userId);
//    Assert.assertEquals(UserStatus.CANCELLED.getCode(), insertedUserProfile.getUserStatus().intValue());
//    CoreUserProfile insertedCUP = userProfileService.getOldCoreUserProfileByOrgIdAndUserId(orgId, userId);
//    Assert.assertEquals(UserStatus.CANCELLED.getCode(), insertedCUP.getUserStatus().intValue());

  }

  @Test
  public void testUpdateUserProfileFieldDeprecated() {
//
//    List<ProfileField> dataFields = profileFieldService.listDataProfileFieldOfTemplate(orgId, profileTemplateId);
//    for (ProfileField dataField : dataFields) {
//      if (dataField.getIsMandatory() == 1) {
//        dataField.setDataValue(dataField.getReferenceName());
//      }
//    }
//    userProfile.setProfileFields(dataFields);
//    userProfileService.addUserProfile(userProfile);
//
//    UserProfile insertedUserProfile = userProfileService.getUserProfile(orgId, userId);
//    Map<String, String> fieldValues = new HashMap<>();
//    for (ProfileField profileField: insertedUserProfile.getProfileFields()) {
//      if (profileField.getIsMandatory() == 1) {
//        fieldValues.put(profileField.getReferenceName(),
//                        profileField.getReferenceName() + profileField.getReferenceName());
//      }
//    }
//    insertedUserProfile.setLastModifiedUserId(userId);
//    userProfileService.updateUserProfileField(orgId, userId, fieldValues, userId);
//
//    insertedUserProfile = userProfileService.getUserProfile(orgId, userId);
//    System.out.println("After-Update: up=" + insertedUserProfile);
//    for (ProfileField profileField : insertedUserProfile.getProfileFields()) {
//      if (profileField.getIsMandatory() == 1) {
//        System.out.println("SoWhat=" + profileField);
//        Assert.assertEquals(profileField.getReferenceName() + profileField.getReferenceName(),
//                            profileField.getDataValue());
//      }
//    }

  }



  @Test
  public void testInitUserProfileConfigUponCreateOrg() {

    // prepare
    List<UserProfileConfig> userProfileConfigs = userProfileConfigFactory.listPresetUserProfileConfig();
    int configCount = userProfileConfigs.size();
    for (UserProfileConfig userProfileConfig: userProfileConfigs) {
      userProfileConfig.setOrgId(mockOrgId);
    }
    userProfileService.initUserProfileConfigUponCreateOrg(userProfileConfigs);

    // verify
    List<UserProfileConfig> addedConfigs = userProfileConfigDao.listUserProfileConfigByOrgId(mockOrgId);
    Assert.assertEquals(configCount, addedConfigs.size());

  }

  @Test
  public void testAddCoreAndBasicAndMetaUserProfileForOnboarding() {

    // prepare
    List<UserProfileConfig> userProfileConfigs = userProfileConfigFactory.listPresetUserProfileConfig();
    for (UserProfileConfig userProfileConfig: userProfileConfigs) {
      userProfileConfig.setOrgId(mockOrgId);
    }
    userProfileService.initUserProfileConfigUponCreateOrg(userProfileConfigs);

    Map<String, String> fieldValues = new HashMap<>();
    fieldValues.put(SystemProfileField.FULL_NAME.getReferenceName(), fullName);
    fieldValues.put(SystemProfileField.EMAIL_ADDRESS.getReferenceName(), emailAddress);
    fieldValues.put(SystemProfileField.MOBILE_PHONE.getReferenceName(), mobilePhone);
    fieldValues.put(SystemProfileField.PERSONAL_EMAIL.getReferenceName(), personalEmail);
    fieldValues.put(SystemProfileField.GENDER.getReferenceName(), gender + "");

    // verify
    userProfileService.addCoreAndBasicAndMetaUserProfileForOnboarding(
        mockOrgId, mockUserId, mockOnboardingTempalteId, mockProfileTemplateId, fieldValues, -1L);

  }

  @Test
  public void testGetCoreUserProfileByOrgIdAndUserId() {

    // prepare
    CoreUserProfile coreUserProfile = new CoreUserProfile();
    coreUserProfile.setOrgId(mockOrgId);
    coreUserProfile.setUserId(mockUserId);
    coreUserProfile.setProfileTemplateId(mockProfileTemplateId);
    coreUserProfile.setFullName(fullName);
    coreUserProfile.setEmailAddress(emailAddress);
    coreUserProfile.setMobilePhone(mobilePhone);
    coreUserProfile.setPersonalEmail(personalEmail);
    coreUserProfile.setGender(gender);
    coreUserProfile.setCreatedUserId(mockActorUserId);
    coreUserProfileDao.insertCoreUserProfile(coreUserProfile);

    // verify
    CoreUserProfile addedCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(mockOrgId, mockUserId);
    Assert.assertEquals(fullName, addedCUP.getFullName());

  }

  @Test
  public void testListCoreUserProfileByOrgIdAndUserId() {

    // prepare
    int userCount = 5;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < userCount; i++) {
      CoreUserProfile coreUserProfile = new CoreUserProfile();
      coreUserProfile.setOrgId(mockOrgId);
      userIds.add(mockUserId + i);
      coreUserProfile.setUserId(mockUserId + i);
      coreUserProfile.setProfileTemplateId(mockProfileTemplateId);
      coreUserProfile.setFullName(fullName);
      coreUserProfile.setEmailAddress(emailAddress);
      coreUserProfile.setMobilePhone(mobilePhone);
      coreUserProfile.setPersonalEmail(personalEmail);
      coreUserProfile.setGender(gender);
      coreUserProfile.setCreatedUserId(mockActorUserId);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
    }

    // verify
    List<CoreUserProfile> addedCUPs = coreUserProfileDao.listCoreUserProfileByOrgIdAndUserId(mockOrgId, userIds);
    Assert.assertEquals(userCount, addedCUPs.size());

  }

  @Test
  public void testGetUserProfile() {

    // prepare

    // init org
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

    CoreUserProfile addedCUP = onboardingFlowService.createOrgAndFirstUser(org, fieldValues, userEmployment);

    // verify
    UserProfile userProfile = userProfileService.getUserProfile(addedCUP.getOrgId(), addedCUP.getUserId());
    Assert.assertTrue(userProfile.getProfileFields().size() > 0);
    LOGGER.info("testGetUserProfile(): userProfile={}", userProfile);

  }

  @Test
  public void testUpdateUserProfileField() {

    // prepare

    // init org
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

    CoreUserProfile addedCUP = onboardingFlowService.createOrgAndFirstUser(org, fieldValues, userEmployment);

    // verify
    Map<String, String> kvs = new HashMap<>();
    String updatedNickName = "updatedNickName";
    Long dateOfBirth = TimeUtils.getNowTimestmapInMillis();
    kvs.put(SystemProfileField.NICK_NAME.getReferenceName(), updatedNickName);
    kvs.put(SystemProfileField.DATE_OF_BIRTH.getReferenceName(), dateOfBirth + "");
    userProfileService.updateUserProfileField(addedCUP.getOrgId(), addedCUP.getUserId(), kvs, mockActorUserId);

    CoreUserProfile updatedCUP = coreUserProfileDao
        .findCoreUserProfileByOrgIdAndUserId(addedCUP.getOrgId(), addedCUP.getUserId());
    Assert.assertEquals(updatedNickName, updatedCUP.getNickName());
    Assert.assertEquals(dateOfBirth, updatedCUP.getDateOfBirth());
    LOGGER.info("updatedCUP={}", updatedCUP);

  }

  @Test
  public void testListCoreUserProfileFromOnboardingByOrgIdAndHasApproved() {

    // prepare

    long orgId = 299999999L;
    long userId = 399999999L;
    long profileTemplateId = 69999999L;
    long createdUserId = 79999999L;
    long onboardingTemplateId = 89999999L;
    String fullName = "WangShanQian";
    String emailAddress = "chenzhe@sqian.com";
    String mobilePhone = "13651325483";

    CoreUserProfile coreUserProfile = new CoreUserProfile();
    coreUserProfile.setOrgId(orgId);
    coreUserProfile.setUserId(userId);
    coreUserProfile.setProfileTemplateId(profileTemplateId);
    coreUserProfile.setFullName(fullName);
    coreUserProfile.setEmailAddress(emailAddress);
    coreUserProfile.setMobilePhone(mobilePhone);
    coreUserProfile.setPersonalEmail(emailAddress);
    coreUserProfile.setGender(UserGender.MALE.getCode());
    coreUserProfile.setCreatedUserId(createdUserId);

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setUserStatus(UserStatus.INVITED.getCode());
    userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userEmployment.setCreatedUserId(createdUserId);

    int userCount = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < userCount; i++) {
      coreUserProfile.setUserId(userId + i);
      coreUserProfile.setOnboardingTemplateId(onboardingTemplateId);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
      userEmployment.setUserId(userId + i);
      userEmploymentDao.insertUserEmployment(userEmployment);

      userIds.add(userId + i);
    }

    // verify

    List<CoreUserProfile> coreUserProfiles = userProfileService
        .listCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 0, 1, 20);
    LOGGER.info("coreUserProfiles={}", coreUserProfiles);
    Assert.assertEquals(userCount, coreUserProfiles.size());

    List<UserEmployment> insertedUEs = userEmploymentDao.listUserEmploymentByOrgIdAndUserId(orgId, userIds);
    UserEmployment firstUE = insertedUEs.get(0);
    firstUE.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    firstUE.setLastModifiedUserId(createdUserId);
    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(firstUE);

    coreUserProfiles = userProfileService
        .listCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 0, 1, 20);
    LOGGER.info("coreUserProfiles={}", coreUserProfiles);
    Assert.assertEquals(userCount - 1, coreUserProfiles.size());

    coreUserProfiles = userProfileService
        .listCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 1, 1, 20);
    LOGGER.info("coreUserProfiles={}", coreUserProfiles);
    Assert.assertEquals(1, coreUserProfiles.size());

  }

  @Test
  public void testCountCoreUserProfileFromOnboardingByOrgIdAndHasApproved() {

    // prepare

    long orgId = 299999999L;
    long userId = 399999999L;
    long profileTemplateId = 69999999L;
    long createdUserId = 79999999L;
    long onboardingTemplateId = 89999999L;
    String fullName = "WangShanQian";
    String emailAddress = "chenzhe@sqian.com";
    String mobilePhone = "13651325483";

    CoreUserProfile coreUserProfile = new CoreUserProfile();
    coreUserProfile.setOrgId(orgId);
    coreUserProfile.setUserId(userId);
    coreUserProfile.setProfileTemplateId(profileTemplateId);
    coreUserProfile.setFullName(fullName);
    coreUserProfile.setEmailAddress(emailAddress);
    coreUserProfile.setMobilePhone(mobilePhone);
    coreUserProfile.setPersonalEmail(emailAddress);
    coreUserProfile.setGender(UserGender.MALE.getCode());
    coreUserProfile.setCreatedUserId(createdUserId);

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setUserStatus(UserStatus.INVITED.getCode());
    userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userEmployment.setCreatedUserId(createdUserId);

    int userCount = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < userCount; i++) {
      coreUserProfile.setUserId(userId + i);
      coreUserProfile.setOnboardingTemplateId(onboardingTemplateId);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
      userEmployment.setUserId(userId + i);
      userEmploymentDao.insertUserEmployment(userEmployment);

      userIds.add(userId + i);
    }

    // verify

    int theCount = userProfileService.countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 0);
    Assert.assertEquals(userCount, theCount);

    List<UserEmployment> insertedUEs = userEmploymentDao.listUserEmploymentByOrgIdAndUserId(orgId, userIds);
    UserEmployment firstUE = insertedUEs.get(0);
    firstUE.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    firstUE.setLastModifiedUserId(createdUserId);
    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(firstUE);

    theCount = userProfileService.countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 0);
    Assert.assertEquals(userCount - 1, theCount);

    theCount = userProfileService.countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 1);
    Assert.assertEquals(1, theCount);

  }

  @Test
  public void testListCoreUserProfileFromImportByUserStatus() {

    // prepare

    long orgId = 299999999L;
    long userId = 399999999L;
    long profileTemplateId = 69999999L;
    long createdUserId = 79999999L;
    long onboardingTemplateId = 89999999L;
    String fullName = "WangShanQian";
    String emailAddress = "chenzhe@sqian.com";
    String mobilePhone = "13651325483";

    CoreUserProfile coreUserProfile = new CoreUserProfile();
    coreUserProfile.setOrgId(orgId);
    coreUserProfile.setUserId(userId);
    coreUserProfile.setProfileTemplateId(profileTemplateId);
    coreUserProfile.setFullName(fullName);
    coreUserProfile.setEmailAddress(emailAddress);
    coreUserProfile.setMobilePhone(mobilePhone);
    coreUserProfile.setPersonalEmail(emailAddress);
    coreUserProfile.setGender(UserGender.MALE.getCode());
    coreUserProfile.setCreatedUserId(createdUserId);

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setUserStatus(UserStatus.INVITED.getCode());
    userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userEmployment.setCreatedUserId(createdUserId);

    int userCount = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < userCount; i++) {
      coreUserProfile.setUserId(userId + i);
      coreUserProfile.setOnboardingTemplateId(null);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
      userEmployment.setUserId(userId + i);
      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
      userEmploymentDao.insertUserEmployment(userEmployment);
      userIds.add(userId + i);
    }

    // verify
    List<CoreUserProfile> coreUserProfiles = userProfileService
        .listCoreUserProfileFromImportByUserStatus(orgId, UserStatus.IMPORTED.getCode(), 1, 20);
    Assert.assertEquals(userCount, coreUserProfiles.size());

    List<UserEmployment> insertedUEs = userEmploymentDao.listUserEmploymentByOrgIdAndUserId(orgId, userIds);
    UserEmployment firstUE = insertedUEs.get(0);
    firstUE.setUserStatus(UserStatus.ACTIVE.getCode());
    firstUE.setLastModifiedUserId(createdUserId);
    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(firstUE);

    coreUserProfiles = userProfileService
        .listCoreUserProfileFromImportByUserStatus(orgId, UserStatus.IMPORTED.getCode(), 1, 20);
    Assert.assertEquals(userCount - 1, coreUserProfiles.size());

    coreUserProfiles = userProfileService
        .listCoreUserProfileFromImportByUserStatus(orgId, UserStatus.ACTIVE.getCode(), 1, 20);
    Assert.assertEquals(1, coreUserProfiles.size());


  }

  @Test
  public void testCountCoreUserProfileFromImportByUserStatus() {

    // prepare

    long orgId = 299999999L;
    long userId = 399999999L;
    long profileTemplateId = 69999999L;
    long createdUserId = 79999999L;
    long onboardingTemplateId = 89999999L;
    String fullName = "WangShanQian";
    String emailAddress = "chenzhe@sqian.com";
    String mobilePhone = "13651325483";

    CoreUserProfile coreUserProfile = new CoreUserProfile();
    coreUserProfile.setOrgId(orgId);
    coreUserProfile.setUserId(userId);
    coreUserProfile.setProfileTemplateId(profileTemplateId);
    coreUserProfile.setFullName(fullName);
    coreUserProfile.setEmailAddress(emailAddress);
    coreUserProfile.setMobilePhone(mobilePhone);
    coreUserProfile.setPersonalEmail(emailAddress);
    coreUserProfile.setGender(UserGender.MALE.getCode());
    coreUserProfile.setCreatedUserId(createdUserId);

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setUserStatus(UserStatus.INVITED.getCode());
    userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userEmployment.setCreatedUserId(createdUserId);

    int userCount = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < userCount; i++) {
      coreUserProfile.setUserId(userId + i);
      coreUserProfile.setOnboardingTemplateId(null);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
      userEmployment.setUserId(userId + i);
      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
      userEmploymentDao.insertUserEmployment(userEmployment);
      userIds.add(userId + i);
    }

    // verify
    int theCount = userProfileService.countCoreUserProfileFromImportByUserStatus(orgId, UserStatus.IMPORTED.getCode());
    Assert.assertEquals(userCount, theCount);

    List<UserEmployment> insertedUEs = userEmploymentDao.listUserEmploymentByOrgIdAndUserId(orgId, userIds);
    UserEmployment firstUE = insertedUEs.get(0);
    firstUE.setUserStatus(UserStatus.ACTIVE.getCode());
    firstUE.setLastModifiedUserId(createdUserId);
    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(firstUE);

    theCount = userProfileService.countCoreUserProfileFromImportByUserStatus(orgId, UserStatus.IMPORTED.getCode());
    Assert.assertEquals(userCount - 1, theCount);

    theCount = userProfileService.countCoreUserProfileFromImportByUserStatus(orgId, UserStatus.ACTIVE.getCode());
    Assert.assertEquals(1, theCount);


  }


} 
