// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.thrift.facade;

import com.mysql.jdbc.TimeUtil;

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.OnboardingStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.user.client.userorg.dto.UserEmploymentDTO;
import hr.wozai.service.user.client.userorg.dto.UserProfileDTO;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.enums.UserGender;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.user.server.dao.userorg.UserProfileConfigDao;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;
import hr.wozai.service.user.server.service.OnboardingFlowService;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.model.userorg.UserProfile;
import hr.wozai.service.user.server.service.ProfileFieldService;
import hr.wozai.service.user.server.service.UserEmploymentService;
import hr.wozai.service.user.server.service.UserProfileService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class UserProfileFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(UserProfileFacadeImplTest.class);


  @Autowired
  UserProfileService userProfileService;

  @Autowired
  ProfileFieldService profileFieldService;

  @Autowired
  UserEmploymentService userEmploymentService;

  @Autowired
  UserProfileFacade userProfileFacade;

  @Autowired
  OnboardingFlowService onboardingFlowService;

  @Autowired
  UserProfileConfigDao userProfileConfigDao;

  // data
  private static long onboardingTemplateId = 119999999;
  private static long profileTemplateId = 129999999L;
  private static long orgId = 139999999L;
  private static long userId = 149999999L;
  private static int userStatus = UserStatus.ACTIVE.getCode();
  private static int onboardingStatus = OnboardingStatus.APPROVED.getCode();
  private static int contractType = ContractType.FULLTIME.getCode();
  private static int employmentStatus = EmploymentStatus.REGULAR.getCode();

  private static long mockOrgId = 19999999L;
  private static long mockUserId = 29999999L;
  private static long mockProfileTemplateId = 39999999L;
  private static String dataFieldReferenceNameOne = "DataFieldOne";
  private static String dataFieldReferenceNameTwo = "DataFieldTwo";
  private static String containerFieldReferenceNameOne = "ContainerFieldOne";
  private static String containerFieldReferenceNameTwo = "ContainerFieldTwo";

  private static String fullName = "马人才易";
  private static String emailAddress = "mawozai@sqian.com";
  private static String mobilePhone = "13566677777";
  private static String personalEmail = "mawozaisqian@qq.com";
  private static Integer gender = UserGender.MALE.getCode();

  private static String emailAddressOfSuperAdmin = "superadminwozai@sqian.com";
  private static String passwordOfSuperAdmin = "Wozai123";

  private static UserProfileDTO userProfileDTO = null;
  private static UserProfile userProfile = null;
  private static UserEmployment userEmployment = null;

  {
    userProfileDTO = new UserProfileDTO();
    userProfileDTO.setOrgId(orgId);
    userProfileDTO.setUserId(userId);
    userProfileDTO.setProfileTemplateId(onboardingTemplateId);
    userProfileDTO.setProfileTemplateId(profileTemplateId);
//    userProfileDTO.setUserStatus(1);
    userProfileDTO.setCreatedUserId(userId);

    userProfile = new UserProfile();
    userProfile.setOrgId(orgId);
    userProfile.setUserId(userId);
    userProfile.setOnboardingTemplateId(onboardingTemplateId);
    userProfile.setProfileTemplateId(profileTemplateId);
//    userProfile.setUserStatus(1);
    userProfile.setCreatedUserId(userId);

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
  public void testGetUserProfile() {

//    List<ProfileField> dataFields = profileFieldService.listDataProfileFieldOfTemplate(orgId, profileTemplateId);
//    for (ProfileField dataField : dataFields) {
//      if (dataField.getIsMandatory() == 1) {
//        dataField.setDataValue(dataField.getDisplayName());
//      }
//    }
//    userProfile.setProfileFields(dataFields);
//    userProfileService.addUserProfile(userProfile);
//
//    UserProfileDTO getResult = userProfileFacade.getUserProfile(orgId, userId, userId, userId);
//    System.out.println("getResult=" + getResult);
//    Assert.assertTrue(getResult.getProfileFieldDTOs().size() > 0);

  }

  @Test
  public void testUpdateUserProfileStatus() {
//
//    List<ProfileField> dataFields = profileFieldService.listDataProfileFieldOfTemplate(orgId, profileTemplateId);
//    for (ProfileField dataField : dataFields) {
//      if (dataField.getIsMandatory() == 1) {
//        dataField.setDataValue(dataField.getDisplayName());
//      }
//    }
//    userProfile.setProfileFields(dataFields);
//    userProfileService.addUserProfile(userProfile);

    // TODO
//    userProfileFacade.updateUserStatus(orgId, userId, UserStatus.CANCELLED.getCode(), userId, userId);
//    UserProfileDTO getResult = userProfileFacade.getUserProfile(orgId, userId, userId, userId);
//    Assert.assertEquals(UserStatus.CANCELLED.getCode(), getResult.getUserStatus().intValue());

  }

  @Test
  public void testUpdateUserProfileField() {
//    List<ProfileField> dataFields = profileFieldService.listDataProfileFieldOfTemplate(orgId, profileTemplateId);
//    for (ProfileField dataField : dataFields) {
//      if (dataField.getIsMandatory() == 1) {
//        dataField.setDataValue(dataField.getDisplayName());
//      }
//    }
//    userProfile.setProfileFields(dataFields);
//    userProfileService.addUserProfile(userProfile);
//
//    UserProfileDTO getResult = userProfileFacade.getUserProfile(orgId, userId, userId, userId);
//    Map<String, String> updateFieldValues = new HashMap<>();
//    for (ProfileFieldDTO profileFieldDTO: getResult.getProfileFieldDTOs()) {
//      if (profileFieldDTO.getIsMandatory() == 1) {
//        updateFieldValues.put(profileFieldDTO.getReferenceName(),
//                              profileFieldDTO.getReferenceName() + profileFieldDTO.getReferenceName());
//      }
//    }
//    userProfileFacade.updateUserProfileField(orgId, userId, updateFieldValues, userId, userId);
//    getResult = userProfileFacade.getUserProfile(orgId, userId, userId, userId);
//    System.out.println("updatedUserProfile=" + getResult);
//    for (ProfileFieldDTO profileFieldDTO: getResult.getProfileFieldDTOs()) {
//      if (profileFieldDTO.getIsMandatory() == 1) {
//        Assert.assertEquals(profileFieldDTO.getReferenceName() + profileFieldDTO.getReferenceName(),
//                            profileFieldDTO.getDataValue());
//      }
//    }

  }

  @Test
  public void testListCoreUserProfileOfEnrollAnniversaryByOrgId() {

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
    long orgId = addedCUP.getOrgId();
    List<UserProfileConfig> addedUPCs = userProfileConfigDao.listUserProfileConfigByOrgId(orgId);
    Assert.assertTrue(addedUPCs.size() > 0);
    UserProfileConfig theUPC = null;
    for (UserProfileConfig userProfileConfig: addedUPCs) {
      if (userProfileConfig.getIsEnabledEditable() == 1) {
        theUPC = userProfileConfig;
        break;
      }
    }
    int currIsEnabled = theUPC.getIsEnabled();
    int updatedIsEnabled = (1 == currIsEnabled) ? 0 : 1;
    theUPC.setIsEnabled(updatedIsEnabled);
    theUPC.setLastModifiedUserId(addedCUP.getUserId());
    profileFieldService.updateUserProfileConfig(theUPC);

  }

  @Test
  public void testUpdateUserEmployment() {

    // prepare

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setUserId(userId);
    userEmployment.setUserStatus(UserStatus.INVITED.getCode());
    userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
    userEmployment.setCreatedUserId(-1L);
    userEmploymentService.addUserEmployment(userEmployment);

    // verify

    UserEmployment insertedUE = userEmploymentService.getUserEmployment(orgId, userId);
    Assert.assertEquals(orgId, insertedUE.getOrgId().longValue());

    insertedUE.setContractType(ContractType.FULLTIME.getCode());
    UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO() ;
    BeanUtils.copyProperties(insertedUE, userEmploymentDTO);
    userProfileFacade.updateUserEmployment(orgId, userEmploymentDTO, userId, userId);

    UserEmployment updatedUE = userEmploymentService.getUserEmployment(orgId, userId);
    Assert.assertEquals(UserStatus.ACTIVE.getCode(), updatedUE.getUserStatus().intValue());

  }

}
