package hr.wozai.service.user.server.test.dao.userorg;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.OnboardingStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.user.client.userorg.enums.UserGender;
import hr.wozai.service.user.server.dao.userorg.CoreUserProfileDao;
import hr.wozai.service.user.server.dao.userorg.UserEmploymentDao;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * CoreUserProfileDao Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Mar 20, 2016</pre>
 */
public class CoreUserProfileDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileFieldDaoTest.class);

  @Autowired
  private CoreUserProfileDao coreUserProfileDao;

  @Autowired
  private UserEmploymentDao userEmploymentDao;

  long orgId = 299999999L;
  long userId = 399999999L;
  long profileTemplateId = 69999999L;
  long createdUserId = 79999999L;
  long onboardingTemplateId = 89999999L;

  String fullName = "WangShanQian";
  String emailAddress = "chenzhe@sqian.com";
  String mobilePhone = "13651325483";
  CoreUserProfile coreUserProfile = null;

  {
    coreUserProfile = new CoreUserProfile();
    coreUserProfile.setOrgId(orgId);
    coreUserProfile.setUserId(userId);
    coreUserProfile.setProfileTemplateId(profileTemplateId);
    coreUserProfile.setOnboardingTemplateId(onboardingTemplateId);
    coreUserProfile.setFullName(fullName);
    coreUserProfile.setEmailAddress(emailAddress);
    coreUserProfile.setMobilePhone(mobilePhone);
    coreUserProfile.setPersonalEmail(emailAddress);
    coreUserProfile.setGender(UserGender.MALE.getCode());
    coreUserProfile.setCreatedUserId(createdUserId);
  }

  @Before
  public void setup() throws Exception {
  }

  @After
  public void teardown() throws Exception {
  }

  /**
   * Method: insertCoreUserProfile(CoreUserProfile coreUserProfile)
   */
  @Test
  public void testInsertCoreUserProfile() throws Exception {
    coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
    CoreUserProfile insertedCoreUserProfile =
        coreUserProfileDao.findCoreUserProfileByOrgIdAndUserId(orgId, userId);
    Assert.assertEquals(emailAddress, insertedCoreUserProfile.getEmailAddress());
  }

  @Test
  public void testListUserIdByOnboardingTemplateId() {
    int count = 10;
    for (long i = 1; i <= count; i++) {
      coreUserProfile.setUserId(i);
      coreUserProfile.setCreatedUserId(userId);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
    }
    List<Long> userIds = coreUserProfileDao.listUserIdByOnboardingTemplateId(orgId, onboardingTemplateId);
    Assert.assertEquals(count, userIds.size());
  }


  /**
   * Method: listOldCoreUserProfileByOrgIdAndUserId(long orgId, List<Long> userIds)
   */
  @Test
  public void testListCoreUserProfileByOrgIdAndUserId() throws Exception {
    List<Long> userIds = new ArrayList<>();
    int count = 10;
    for (long i = 1; i <= count; i++) {
      coreUserProfile.setUserId(i);
      coreUserProfile.setCreatedUserId(userId);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
      userIds.add(i);
    }
    List<CoreUserProfile> insertedProfiles = coreUserProfileDao.listCoreUserProfileByOrgIdAndUserId(orgId, userIds);
    Assert.assertEquals(count, insertedProfiles.size());
  }

  @Test
  public void testListCoreUserProfileByCreatedUserId() {

    List<Long> userIds = new ArrayList<>();
    int count = 10;
    for (long i = 1; i <= count; i++) {
      coreUserProfile.setUserId(i);
      coreUserProfile.setCreatedUserId(createdUserId);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
      userIds.add(i);
    }
    List<CoreUserProfile> insertedProfiles = coreUserProfileDao
        .listCoreUserProfileByCreatedUserId(orgId, createdUserId);
    Assert.assertEquals(count, insertedProfiles.size());

    for (int i = 0; i < insertedProfiles.size(); i++) {
      Assert.assertEquals(createdUserId, insertedProfiles.get(i).getCreatedUserId().longValue());
    }

  }

  @Test
  public void testListCoreUserProfileByOrgIdOrderByCreatedTimeDesc() {

    List<Long> userIds = new ArrayList<>();
    int count = 10;
    int pageNumber = 1;
    int pageSize = 20;
    for (long i = 1; i <= count; i++) {
      coreUserProfile.setUserId(i);
      coreUserProfile.setCreatedUserId(userId);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
      userIds.add(i);
    }
    List<CoreUserProfile> insertedProfiles = coreUserProfileDao
        .listCoreUserProfileByOrgIdOrderByCreatedTimeDesc(orgId, pageNumber, pageSize);
    Assert.assertEquals(count, insertedProfiles.size());

    for (int i = 0; i < insertedProfiles.size() - 1; i++) {
      Assert.assertTrue(insertedProfiles.get(i).getCreatedTime().longValue()
                        >= insertedProfiles.get(i + 1).getCreatedUserId().longValue());
    }
  }

  @Test
  public void testCountCoreUserProfileByOrgId() {

    List<Long> userIds = new ArrayList<>();
    int count = 10;
    for (long i = 1; i <= count; i++) {
      coreUserProfile.setUserId(i);
      coreUserProfile.setCreatedUserId(userId);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
      userIds.add(i);
    }
    int insertedCount = coreUserProfileDao.countCoreUserProfileByOrgId(orgId);
    Assert.assertEquals(count, insertedCount);

  }

  /**
   * Method: updateCoreUserProfileByOrgIdAndUserId(CoreUserProfile coreUserProfile)
   */
  @Test
  public void testUpdateCoreUserProfileByOrgIdAndUserId() throws Exception {
    coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
    CoreUserProfile insertedProfile = coreUserProfileDao.findCoreUserProfileByOrgIdAndUserId(orgId, userId);
    String newFullName = fullName + fullName;
    insertedProfile.setFullName(newFullName);
    insertedProfile.setLastModifiedUserId(userId);
    coreUserProfileDao.updateCoreUserProfileByOrgIdAndUserId(insertedProfile);
    CoreUserProfile updatedProfile = coreUserProfileDao.findCoreUserProfileByOrgIdAndUserId(orgId, userId);
    Assert.assertEquals(newFullName, updatedProfile.getFullName());
  }

  @Test
  public void testDeleteCoreUserProfileByOrgIdAndUserId() {

    // prepare
    coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
    CoreUserProfile insertedProfile = coreUserProfileDao.findCoreUserProfileByOrgIdAndUserId(orgId, userId);
    coreUserProfileDao.deleteCoreUserProfileByOrgIdAndUserId(orgId, insertedProfile.getUserId(), createdUserId);

    // verify
    CoreUserProfile deleteCUP = coreUserProfileDao
        .findCoreUserProfileByOrgIdAndUserId(orgId, insertedProfile.getUserId());
    Assert.assertNull(deleteCUP);

  }

  @Test
  public void testListCoreUserProfileFromOnboardingByOrgIdAndHasApproved() {

    // prepare

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

    List<CoreUserProfile> coreUserProfiles = coreUserProfileDao
        .listCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 0, 1, 20);
    LOGGER.info("coreUserProfiles={}", coreUserProfiles);
    Assert.assertEquals(userCount, coreUserProfiles.size());

    List<UserEmployment> insertedUEs = userEmploymentDao.listUserEmploymentByOrgIdAndUserId(orgId, userIds);
    UserEmployment firstUE = insertedUEs.get(0);
    firstUE.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    firstUE.setLastModifiedUserId(createdUserId);
    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(firstUE);

    coreUserProfiles = coreUserProfileDao
        .listCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 0, 1, 20);
    LOGGER.info("coreUserProfiles={}", coreUserProfiles);
    Assert.assertEquals(userCount - 1, coreUserProfiles.size());

    coreUserProfiles = coreUserProfileDao
        .listCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 1, 1, 20);
    LOGGER.info("coreUserProfiles={}", coreUserProfiles);
    Assert.assertEquals(1, coreUserProfiles.size());

  }

  @Test
  public void testCountCoreUserProfileFromOnboardingByOrgIdAndHasApproved() {

    // prepare

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

    int theCount = coreUserProfileDao.countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 0);
    Assert.assertEquals(userCount, theCount);

    List<UserEmployment> insertedUEs = userEmploymentDao.listUserEmploymentByOrgIdAndUserId(orgId, userIds);
    UserEmployment firstUE = insertedUEs.get(0);
    firstUE.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    firstUE.setLastModifiedUserId(createdUserId);
    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(firstUE);

    theCount = coreUserProfileDao.countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 0);
    Assert.assertEquals(userCount - 1, theCount);

    theCount = coreUserProfileDao.countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 1);
    Assert.assertEquals(1, theCount);

  }

  @Test
  public void testListCoreUserProfileFromImportByUserStatus() {

    // prepare

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
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
    List<CoreUserProfile> coreUserProfiles = coreUserProfileDao
        .listCoreUserProfileFromImportByUserStatus(orgId, UserStatus.IMPORTED.getCode(), 1, 20);
    Assert.assertEquals(userCount, coreUserProfiles.size());

    List<UserEmployment> insertedUEs = userEmploymentDao.listUserEmploymentByOrgIdAndUserId(orgId, userIds);
    UserEmployment firstUE = insertedUEs.get(0);
    firstUE.setUserStatus(UserStatus.ACTIVE.getCode());
    firstUE.setLastModifiedUserId(createdUserId);
    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(firstUE);

    coreUserProfiles = coreUserProfileDao
        .listCoreUserProfileFromImportByUserStatus(orgId, UserStatus.IMPORTED.getCode(), 1, 20);
    Assert.assertEquals(userCount - 1, coreUserProfiles.size());

    coreUserProfiles = coreUserProfileDao
        .listCoreUserProfileFromImportByUserStatus(orgId, UserStatus.ACTIVE.getCode(), 1, 20);
    Assert.assertEquals(1, coreUserProfiles.size());


  }

  @Test
  public void testCountCoreUserProfileFromImportByUserStatus() {

    // prepare

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
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
    int theCount = coreUserProfileDao.countCoreUserProfileFromImportByUserStatus(orgId, UserStatus.IMPORTED.getCode());
    Assert.assertEquals(userCount, theCount);

    List<UserEmployment> insertedUEs = userEmploymentDao.listUserEmploymentByOrgIdAndUserId(orgId, userIds);
    UserEmployment firstUE = insertedUEs.get(0);
    firstUE.setUserStatus(UserStatus.ACTIVE.getCode());
    firstUE.setLastModifiedUserId(createdUserId);
    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(firstUE);

    theCount = coreUserProfileDao.countCoreUserProfileFromImportByUserStatus(orgId, UserStatus.IMPORTED.getCode());
    Assert.assertEquals(userCount - 1, theCount);

    theCount = coreUserProfileDao.countCoreUserProfileFromImportByUserStatus(orgId, UserStatus.ACTIVE.getCode());
    Assert.assertEquals(1, theCount);


  }

  @Test
  public void testListAllCoreUserProfileFromImport() {

    // prepare

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userEmployment.setCreatedUserId(createdUserId);

    int nonActivatedUserCount = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < nonActivatedUserCount; i++) {
      coreUserProfile.setUserId(userId + i);
      coreUserProfile.setOnboardingTemplateId(null);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
      userEmployment.setUserId(userId + i);
      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
      userEmploymentDao.insertUserEmployment(userEmployment);
      userIds.add(userId + i);
    }

    int activatedUserCount = 15;
    for (int i = 0; i < activatedUserCount; i++) {
      coreUserProfile.setUserId(userId + userId + i);
      coreUserProfile.setOnboardingTemplateId(null);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
      userEmployment.setUserId(userId + userId+ i);
      userEmployment.setUserStatus(UserStatus.ACTIVE.getCode());
      userEmploymentDao.insertUserEmployment(userEmployment);
      userIds.add(userId + i);
    }

    // verify
    List<CoreUserProfile> coreUserProfiles = coreUserProfileDao.listAllCoreUserProfileFromImport(orgId, 1, 50);
    Assert.assertEquals(nonActivatedUserCount + activatedUserCount, coreUserProfiles.size());

  }

  @Test
  public void testCountAllCoreUserProfileFromImport() {

//    // prepare
//
//    UserEmployment userEmployment = new UserEmployment();
//    userEmployment.setOrgId(orgId);
//    userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
//    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
//    userEmployment.setContractType(ContractType.FULLTIME.getCode());
//    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
//    userEmployment.setCreatedUserId(createdUserId);
//
//    int nonActivatedUserCount = 10;
//    List<Long> userIds = new ArrayList<>();
//    for (int i = 0; i < nonActivatedUserCount; i++) {
//      coreUserProfile.setUserId(userId + i);
//      coreUserProfile.setOnboardingTemplateId(null);
//      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
//      userEmployment.setUserId(userId + i);
//      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
//      userEmploymentDao.insertUserEmployment(userEmployment);
//      userIds.add(userId + i);
//    }
//
//    int activatedUserCount = 11;
//    for (int i = 0; i < activatedUserCount; i++) {
//      coreUserProfile.setUserId(userId + userId + i);
//      coreUserProfile.setOnboardingTemplateId(null);
//      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
//      userEmployment.setUserId(userId + i);
//      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
//      userEmploymentDao.insertUserEmployment(userEmployment);
//      userIds.add(userId + i);
//    }
//
//    // verify
//    int theCount = coreUserProfileDao.countCoreUserProfileFromImportByUserStatus(orgId, UserStatus.IMPORTED.getCode());
//    Assert.assertEquals(nonActivatedUserCount + activatedUserCount, theCount);

  }

  @Test
  public void testListFullNameAndEmailAddressWhichIsNotResigned() {

    // prepare

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userEmployment.setCreatedUserId(createdUserId);

    int nonActivatedUserCount = 10;
    List<Long> userIds = new ArrayList<>();
    for (int i = 0; i < nonActivatedUserCount; i++) {
      coreUserProfile.setUserId(userId + i);
      coreUserProfile.setOnboardingTemplateId(null);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);
      userEmployment.setUserId(userId + i);
      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
      userEmploymentDao.insertUserEmployment(userEmployment);
      userIds.add(userId + i);
    }

    // verify
    List<CoreUserProfile> coreUserProfiles = coreUserProfileDao.listFullNameAndEmailAddressWhichIsNotResignedForUpdate(orgId);
    Assert.assertEquals(nonActivatedUserCount, coreUserProfiles.size());

  }

} 
