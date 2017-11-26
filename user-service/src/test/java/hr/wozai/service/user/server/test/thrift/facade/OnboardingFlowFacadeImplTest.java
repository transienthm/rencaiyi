package hr.wozai.service.user.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.OnboardingStatus;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongListDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingRequestDTO;
import hr.wozai.service.user.client.onboarding.dto.OrgAccountRequestDTO;
import hr.wozai.service.user.client.onboarding.dto.SuperAdminDTO;
import hr.wozai.service.user.client.onboarding.facade.OnboardingFlowFacade;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.enums.UserGender;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.user.server.dao.userorg.CoreUserProfileDao;
import hr.wozai.service.user.server.dao.userorg.UserEmploymentDao;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.service.UserProfileService;
import hr.wozai.service.user.server.service.UserService;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * OnboardingFlowFacadeImpl Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Apr 21, 2016</pre>
 */
public class OnboardingFlowFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(OnboardingFlowFacadeImplTest.class);

  @Autowired
  private OnboardingFlowFacade onboardingFlowFacade;

  @Autowired
  UserProfileService userProfileService;

  @Autowired
  UserProfileFacade userProfileFacade;

  @Autowired
  UserService userService;

  @Autowired
  TeamService teamService;

  @Autowired
  CoreUserProfileDao coreUserProfileDao;

  @Autowired
  UserEmploymentDao userEmploymentDao;

  // data
  private static long mockOrgId = 19999999L;
  private static long mockUserId = 29999999L;
  private static long enrollDate = 1459180800000L;
  private static long resignDate = 1459180800000L;
  private static int contractType = ContractType.FULLTIME.getCode();
  private static String fullName = "山前";
  private static String emailAddress = "ppllaa@gmail.com";
  private static String mobilePhone = "11111111111";
  private static Integer gender = UserGender.MALE.getCode();
  private static String personalEmail = "182u3812u03910391@@qq.com";

  private static String emailAddressOfSuperAdmin = "superadminwozai@sqian.com";
  private static String passwordOfSuperAdmin = "Wozai123";

  private static String orgFullName = "北京测试科技有限公司";
  private static String orgShortName = "北测公司";
  private static String orgAvatarUrl = "paosdp";

  private static OnboardingRequestDTO onboardingRequestDTO = null;
  private static OrgAccountRequestDTO orgAccountRequestDTO = null;

  {
    onboardingRequestDTO = new OnboardingRequestDTO();
    onboardingRequestDTO.setFullName(fullName);
    onboardingRequestDTO.setEmailAddress(emailAddress);
    onboardingRequestDTO.setMobilePhone(mobilePhone);
    onboardingRequestDTO.setGender(gender);
    onboardingRequestDTO.setContractType(contractType);
    onboardingRequestDTO.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    onboardingRequestDTO.setEnrollDate(enrollDate);
    onboardingRequestDTO.setResignDate(resignDate);

    SuperAdminDTO superAdminDTO = new SuperAdminDTO();


    orgAccountRequestDTO = new OrgAccountRequestDTO();
    orgAccountRequestDTO.setOnboardingRequestDTO(onboardingRequestDTO);
//    orgAccountRequestDTO.setSuperAdminDTO(superAdminDTO);
    orgAccountRequestDTO.setOrgFullName(orgFullName);
    orgAccountRequestDTO.setOrgShortName(orgShortName);
    orgAccountRequestDTO.setOrgAvatarUrl(orgAvatarUrl);
  }

  @Before
  public void before() throws Exception {
  }

  @After
  public void after() throws Exception {

  }

  @Test
  public void testAddOrgAndFirstUser() {

    // prepare
    onboardingFlowFacade.addOrgAndSuperAdminAndFirstUser(orgAccountRequestDTO);

  }
//  @Test
//  public void testLaunchOnboardingFlowOfIndivudualStaff() {
//
//    LongDTO rpcAddResult = onboardingFlowFacade.launchOnboardingFlowOfIndivudualStaff(
//        orgId, onboardingRequestDTO, userId, userId);
//    System.out.printf("rpcAddResult=" + rpcAddResult);
//    Assert.assertNotNull(rpcAddResult.getData());
//
//    CoreUserProfile insertedCUP = userProfileService.getOldCoreUserProfileByOrgIdAndUserId(orgId, rpcAddResult.getData());
//    System.out.println("insertedCUP=" + insertedCUP);
//    Assert.assertEquals(fullName, insertedCUP.getFullName());
//
//  }

//  @Test
//  public void testAddOrgAndFirstUser() {
//
//    OrgAccountRequestDTO orgAccountRequestDTO = new OrgAccountRequestDTO();
//    orgAccountRequestDTO.setOrgFullName(fullName);
//    orgAccountRequestDTO.setOrgShortName(fullName);
//    orgAccountRequestDTO.setOrgAvatarUrl(fullName);
//    orgAccountRequestDTO.setOrgTimeZone(1);
//    OnboardingRequestDTO onboardingRequestDTO = new OnboardingRequestDTO();
//    onboardingRequestDTO.setEmailAddress(emailAddress);
//    onboardingRequestDTO.setMobilePhone(mobilePhone);
//    onboardingRequestDTO.setFullName(fullName);
//    onboardingRequestDTO.setCitizenId(citizenId);
//    orgAccountRequestDTO.setOnboardingRequestDTO(onboardingRequestDTO);
//
//    CoreUserProfileDTO rpcAddResult = onboardingFlowFacade.addOrgAndSuperAdminAndFirstUser(orgAccountRequestDTO);
//    System.out.printf("rpcAddResult=" + rpcAddResult);
//    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), rpcAddResult.getServiceStatusDTO().getCode());
//  }

//
//  /**
//   * Method: launchOnboardingFlowOfIndivudualStaff(long orgId, OnboardingRequestDTO onboardingRequestDTO, long actorUserId, long
//   * adminUserId)
//   */
//  @Test
//  public void testLaunchOnboardingForStaffByHR() throws Exception {
//
//    LongDTO addResult = onboardingFlowFacade
//        .launchOnboardingForStaffByHR(orgId, onboardingRequestDTO, 55L, 0);
//    long insertedUserId = addResult.getData();
//    Assert.assertTrue(insertedUserId > 0);
//
//    // 1) validate userAccount
//    UserAccount userAccount = userService.getUserAccountByEmailAddress(emailAddress);
//    Assert.assertNotNull(userAccount);
//
//    // 2) validate userProfile
//    UserProfile userProfile = userProfileService.getUserProfile(orgId, insertedUserId);
//    Assert.assertNotNull(userProfile);
//
//    // 3) validate coreUserProfile
//    CoreUserProfile coreUserProfile = userProfileService.getOldCoreUserProfileByOrgIdAndUserId(orgId, insertedUserId);
//    Assert.assertNotNull(coreUserProfile);
//
//    // 4) validate orgMember
//    List<Long> orgMembers = userService.listAllUsersByOrgId(orgId);
//    boolean isThere = false;
//    for (Long thereId: orgMembers) {
//      if (thereId.equals(insertedUserId)) {
//        isThere = true;
//      }
//    }
//    Assert.assertTrue(isThere);
//
//    // 5) validate teamMember
//    TeamMember teamMember = teamService.getTeamMemberByUserIdAndOrgId(orgId, insertedUserId);
//    Assert.assertNotNull(teamMember);
//
//    // 6) validate reportLine
//    long insertedReporterUserId = userService.getReportorByUserIdAndOrgId(orgId, insertedUserId);
//    Assert.assertEquals(reporterUserId, insertedReporterUserId);
//
//    // 7) validate userName
//
//    // TODO: 8) validate userRole
//
//  }
//
//  /**
//   * Method: listOnboardingStaffByHR(long orgId, long actorUserId, long adminUserId)
//   */
//  @Test
//  public void testListOnboardingStaffByHR() throws Exception {
//
//    int count = 10;
//    for (int i = 0; i < count; i++) {
//      onboardingRequestDTO.setEmailAddress(i + emailAddress);
//      onboardingFlowFacade.launchOnboardingForStaffByHR(orgId, onboardingRequestDTO, userId, userId);
//    }
//
//    CoreUserProfileListDTO coreUserProfileListDTO = onboardingFlowFacade
//        .listOnboardingStaffByHR(orgId, userId, userId);
//    System.out.println("insertedOnbaordingUsers=" + coreUserProfileListDTO);
//    Assert.assertEquals(count, coreUserProfileListDTO.getCoreUserProfileDTOs().size());
//
//    // abnormal case 1: existed_email
//    onboardingRequestDTO.setEmailAddress(0 + emailAddress);
//    LongDTO addResult = onboardingFlowFacade
//        .launchOnboardingForStaffByHR(orgId, onboardingRequestDTO, userId, userId);
//    Assert.assertEquals(ServiceStatus.EXISTING_EMAIL.getCode(), addResult.getServiceStatusDTO().getCode());
//
//  }
//
//
//  /**
//   * Method: approveOnboardingSubmisisonByHR(long orgId, long userId, long actorUserId, long adminUserId)
//   */
//  @Test
//  public void testApproveOnboardingSubmisisonByHR() throws Exception {
//
//    LongDTO addResult = onboardingFlowFacade
//        .launchOnboardingForStaffByHR(orgId, onboardingRequestDTO, userId, userId);
//    long insertedUserId = addResult.getData();
//    Assert.assertTrue(insertedUserId > 0);
//
//    onboardingFlowFacade.approveOnboardingByHR(orgId, insertedUserId, userId, userId);
//    UserProfile userProfile = userProfileService.getUserProfile(orgId, insertedUserId);
//    Assert.assertEquals(UserStatus.ACTIVE.getCode(), userProfile.getUserStatus().intValue());
//
//  }
//
//  /**
//   * Method: rejectOnboardingSubmisisonByHR(long orgId, long userId, long actorUserId, long adminUserId)
//   */
//  @Test
//  public void testRejectOnboardingSubmisisonByHR() throws Exception {
//
//    LongDTO addResult = onboardingFlowFacade
//        .launchOnboardingForStaffByHR(orgId, onboardingRequestDTO, userId, userId);
//    long insertedUserId = addResult.getData();
//    Assert.assertTrue(insertedUserId > 0);
//
//    onboardingFlowFacade.rejectOnboardingSubmisisonByHR(orgId, insertedUserId, userId, userId);
//    UserProfile userProfile = userProfileService.getUserProfile(orgId, insertedUserId);
//    // TODO
////    Assert.assertEquals(UserStatus.ONBOARDING.getCode(), userProfile.getUserStatus().intValue());
//
//
//  }
//
//  /**
//   * Method: cancelOnboardingByHR(long orgId, long userId, long actorUserId, long adminUserId)
//   */
//  @Test
//  public void testCancelOnboardingByHR() throws Exception {
//
//    LongDTO addResult = onboardingFlowFacade
//        .launchOnboardingForStaffByHR(orgId, onboardingRequestDTO, userId, userId);
//    long insertedUserId = addResult.getData();
//    Assert.assertTrue(insertedUserId > 0);
//
//    onboardingFlowFacade.cancelOnboardingByHR(orgId, insertedUserId, userId, userId);
//
//    // 1) validate userAccount
//    try {
//     userService.getUserAccountByEmailAddress(emailAddress);
//    } catch (ServiceStatusException e) {
//      LOGGER.error("Now What: 1");
//      Assert.assertEquals(ServiceStatus.NOT_FOUND.getCode(), e.getServiceStatus().getCode());
//    }
//
//    // 2) validate userProfile
//    UserProfile userProfile = userProfileService.getUserProfile(orgId, insertedUserId);
//    // TODO
////    Assert.assertEquals(UserStatus.CANCELLED.getCode(), userProfile.getUserStatus().intValue());
//
//    // 3) validate coreUserProfile
//    CoreUserProfile coreUserProfile = userProfileService.getOldCoreUserProfileByOrgIdAndUserId(orgId, insertedUserId);
//    // TODO
////    Assert.assertEquals(UserStatus.CANCELLED.getCode(), userProfile.getUserStatus().intValue());
//
//    // 4) validate orgMember
//    List<Long> orgMembers = userService.listAllUsersByOrgId(orgId);
//    boolean isThere = false;
//    for (Long thereId: orgMembers) {
//      if (thereId.equals(insertedUserId)) {
//        isThere = true;
//      }
//    }
//    Assert.assertTrue(!isThere);
//
//    // 5) validate teamMember
//    try {
//      teamService.getTeamMemberByUserIdAndOrgId(orgId, insertedUserId);
//    } catch (ServiceStatusException e) {
//      LOGGER.error("Now What: 2");
//      Assert.assertEquals(ServiceStatus.NOT_FOUND.getCode(), e.getServiceStatus().getCode());
//    }
//
//    // 6) validate reportLine
//    try {
//      userService.getReportorByUserIdAndOrgId(orgId, insertedUserId);
//    } catch (ServiceStatusException e) {
//      LOGGER.error("Now What: 3");
//      Assert.assertEquals(ServiceStatus.NOT_FOUND.getCode(), e.getServiceStatus().getCode());
//    }
//
//
//
//  }
//
//  /**
//   * Method: getOnboardingTemplateByStaff(long userId)
//   */
//  @Test
//  public void testGetOnboardingTemplateByStaff() throws Exception {
//
//    LongDTO addResult = onboardingFlowFacade
//        .launchOnboardingForStaffByHR(orgId, onboardingRequestDTO, userId, userId);
//    long insertedUserId = addResult.getData();
//    Assert.assertTrue(insertedUserId > 0);
//
//    OnboardingTemplateDTO onboardingTemplateDTO = onboardingFlowFacade
//        .getOnboardingTemplateByStaff(orgId, insertedUserId);
//    Assert.assertEquals(onboardingTemplateId, onboardingTemplateDTO.getOnboardingTemplateId().longValue());
//  }
//
//  /**
//   * Method: downloadOnboardingDocumentByStaff(long userId, long documentId)
//   */
//  @Test
//  public void testDownloadOnboardingDocumentByStaff() throws Exception {
//
//    LongDTO addResult = onboardingFlowFacade
//        .launchOnboardingForStaffByHR(orgId, onboardingRequestDTO, userId, userId);
//    long insertedUserId = addResult.getData();
//    Assert.assertTrue(insertedUserId > 0);
//
//    S3DocumentRequestDTO s3DocumentRequestDTO = onboardingFlowFacade
//        .downloadOnboardingDocumentByStaff(orgId, insertedUserId, toDownloadDocumentId);
//    System.out.println("ToDownload=" + s3DocumentRequestDTO);
//    Assert.assertTrue(s3DocumentRequestDTO.getPresignedUrl().length() > 0);
//
//  }
//
//  @Test
//  public void testGetUserProfileFieldByStaff() {
//
//    LongDTO addResult = onboardingFlowFacade
//        .launchOnboardingForStaffByHR(orgId, onboardingRequestDTO, userId, userId);
//    long insertedUserId = addResult.getData();
//    Assert.assertTrue(insertedUserId > 0);
//
//    UserProfileDTO userProfileDTO = onboardingFlowFacade.getUserProfileByStaff(orgId, insertedUserId);
//    Assert.assertTrue(userProfileDTO.getProfileFieldDTOs().size() > 0);
//    for (ProfileFieldDTO profileFieldDTO: userProfileDTO.getProfileFieldDTOs()) {
//      if (profileFieldDTO.getIsMandatory() == 1) {
//        Assert.assertTrue(!StringUtils.isNullOrEmpty(profileFieldDTO.getDataValue()));
//      }
//    }
//
//  }
//
//  @Test
//  public void testUpdateUserProfileFieldByStaff() {
//
//    LongDTO addResult = onboardingFlowFacade
//        .launchOnboardingForStaffByHR(orgId, onboardingRequestDTO, userId, userId);
//    long insertedUserId = addResult.getData();
//    Assert.assertTrue(insertedUserId > 0);
//
//    UserProfileDTO getResult = userProfileFacade.getUserProfile(orgId, insertedUserId, userId, userId);
//    Map<String, String> updateFieldValues = new HashMap<>();
//    for (ProfileFieldDTO profileFieldDTO: getResult.getProfileFieldDTOs()) {
//      if (profileFieldDTO.getIsMandatory() == 1) {
//        updateFieldValues.put(profileFieldDTO.getReferenceName(),
//                              profileFieldDTO.getReferenceName() + profileFieldDTO.getReferenceName());
//      }
//    }
//    userProfileFacade.updateUserProfileField(orgId, insertedUserId, updateFieldValues, userId, userId);
//    getResult = userProfileFacade.getUserProfile(orgId, insertedUserId, userId, userId);
//    System.out.println("updatedUserProfileHere=" + getResult);
//    for (ProfileFieldDTO profileFieldDTO: getResult.getProfileFieldDTOs()) {
//      if (profileFieldDTO.getIsMandatory() == 1) {
//        Assert.assertEquals(profileFieldDTO.getReferenceName() + profileFieldDTO.getReferenceName(),
//                            profileFieldDTO.getDataValue());
//      }
//    }
//
//  }
//
//
//  @Test
//  public void testSubmitOnboardingRequestByStaff() {
//
//    LongDTO addResult = onboardingFlowFacade
//        .launchOnboardingForStaffByHR(orgId, onboardingRequestDTO, userId, userId);
//    long insertedUserId = addResult.getData();
//    Assert.assertTrue(insertedUserId > 0);
//
//    onboardingFlowFacade.submitOnboardingRequestByStaff(orgId, insertedUserId);
//    CoreUserProfile coreUserProfile = userProfileService.getOldCoreUserProfileByOrgIdAndUserId(orgId, insertedUserId);
//    // TODO
////    Assert.assertEquals(UserStatus.SUBMITTED.getCode(), coreUserProfile.getUserStatus().intValue());
//
//  }

  @Test
  public void testCountTodoNumbersOfOnboardingAndImporting() {

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

    int onboardingCount = 10;
    for (int i = 0; i < onboardingCount; i++) {
      coreUserProfile.setUserId(userId + i);
      coreUserProfile.setOnboardingTemplateId(onboardingTemplateId);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);

      userEmployment.setUserId(userId + i);
      userEmployment.setUserStatus(UserStatus.INVITED.getCode());
      userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
      userEmploymentDao.insertUserEmployment(userEmployment);
    }

    int importedCount = 5;
    for (int i = 0; i < importedCount; i++) {
      coreUserProfile.setUserId(userId + userId + i);
      coreUserProfile.setOnboardingTemplateId(null);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);

      userEmployment.setUserId(userId + userId + i);
      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
      userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
      userEmploymentDao.insertUserEmployment(userEmployment);
    }

    // verify
    LongListDTO rpcResult = onboardingFlowFacade.countTodoNumbersOfOnboardingAndImporting(orgId, userId, userId);
    LOGGER.info("rpcResult={}", rpcResult);
    Assert.assertEquals(onboardingCount, rpcResult.getData().get(0).intValue());
    Assert.assertEquals(importedCount,rpcResult.getData().get(1).intValue());

  }

  @Test
  public void testListOnboardingStaffByHR() {

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
    userEmployment.setFulltimeEnrollDate(TimeUtils.getNowTimestmapInMillis());
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userEmployment.setCreatedUserId(createdUserId);

    int onboardingCount = 10;
    for (int i = 0; i < onboardingCount; i++) {
      coreUserProfile.setUserId(userId + i);
      coreUserProfile.setOnboardingTemplateId(onboardingTemplateId);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);

      userEmployment.setUserId(userId + i);
      userEmployment.setUserStatus(UserStatus.INVITED.getCode());
      userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
      userEmploymentDao.insertUserEmployment(userEmployment);
    }

    int importedCount = 5;
    for (int i = 0; i < importedCount; i++) {
      coreUserProfile.setUserId(userId + userId + i);
      coreUserProfile.setOnboardingTemplateId(null);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);

      userEmployment.setUserId(userId + userId + i);
      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
      userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
      userEmploymentDao.insertUserEmployment(userEmployment);
    }

    // verify
    int pageNumber = 1;
    int pageSize = 5;
    CoreUserProfileListDTO rpcResult = onboardingFlowFacade
        .listOnboardingStaffByHR(orgId, 0, pageNumber, pageSize, createdUserId, createdUserId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), rpcResult.getServiceStatusDTO().getCode());
    Assert.assertEquals(onboardingCount, rpcResult.getTotalNumber());
    Assert.assertEquals(pageSize, rpcResult.getCoreUserProfileDTOs().size());

  }

  @Test
  public void testListImportedStaffByHR() {

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
    userEmployment.setFulltimeEnrollDate(TimeUtils.getNowTimestmapInMillis());
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userEmployment.setCreatedUserId(createdUserId);

    int onboardingCount = 10;
    for (int i = 0; i < onboardingCount; i++) {
      coreUserProfile.setUserId(userId + i);
      coreUserProfile.setOnboardingTemplateId(onboardingTemplateId);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);

      userEmployment.setUserId(userId + i);
      userEmployment.setUserStatus(UserStatus.INVITED.getCode());
      userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
      userEmploymentDao.insertUserEmployment(userEmployment);
    }

    int importedCount = 8;
    for (int i = 0; i < importedCount; i++) {
      coreUserProfile.setUserId(userId + userId + i);
      coreUserProfile.setOnboardingTemplateId(null);
      coreUserProfileDao.insertCoreUserProfile(coreUserProfile);

      userEmployment.setUserId(userId + userId + i);
      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
      userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
      userEmploymentDao.insertUserEmployment(userEmployment);
    }

    // verify
    int pageNumber = 1;
    int pageSize = 5;
    CoreUserProfileListDTO rpcResult = onboardingFlowFacade
        .listImportedStaffByHR(orgId, 0, pageNumber, pageSize, createdUserId, createdUserId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), rpcResult.getServiceStatusDTO().getCode());
    Assert.assertEquals(importedCount, rpcResult.getTotalNumber());
    Assert.assertEquals(pageSize, rpcResult.getCoreUserProfileDTOs().size());

  }
} 
