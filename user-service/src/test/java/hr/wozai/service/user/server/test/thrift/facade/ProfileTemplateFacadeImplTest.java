package hr.wozai.service.user.server.test.thrift.facade;

import com.mysql.jdbc.TimeUtil;

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.userorg.dto.AddressRegionListDTO;
import hr.wozai.service.user.client.userorg.dto.OrgPickOptionDTO;
import hr.wozai.service.user.client.userorg.dto.OrgPickOptionListDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileFieldDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileFieldListDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileTemplateDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileTemplateListDTO;
import hr.wozai.service.user.client.userorg.enums.ConfigType;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.enums.UserGender;
import hr.wozai.service.user.client.userorg.facade.ProfileTemplateFacade;
import hr.wozai.service.user.server.dao.userorg.UserProfileConfigDao;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;
import hr.wozai.service.user.server.service.OnboardingFlowService;
import hr.wozai.service.user.server.service.OrgPickOptionService;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
 * @Created: 2016-03-29
 */
public class ProfileTemplateFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileTemplateFacadeImplTest.class);

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  ProfileTemplateFacade profileTemplateFacade;

  @Autowired
  OrgPickOptionService orgPickOptionService;

  @Autowired
  OnboardingFlowService onboardingFlowService;

  @Autowired
  UserProfileConfigDao userProfileConfigDao;

  // data
  long userId = 10L;
  long orgId = 20L;
  long profileFieldId = 30L;
  long profileTemplateId = 40;
  String displayName = "模板";

  // data
  long mockOrgId = 19999999L;
  long mockUserId = 29999999L;
  long mockpProfileTemplateId = 39999999L;

  private String fullName = "马人才易";
  private String emailAddress = "mawozai@sqian.com";
  private String mobilePhone = "13566677777";
  private String personalEmail = "mawozaisqian@qq.com";
  private Integer gender = UserGender.MALE.getCode();

  private static String emailAddressOfSuperAdmin = "superadminwozai@sqian.com";
  private static String passwordOfSuperAdmin = "Wozai123";

  long containerId = 40L;
  String referenceName = "Field";
  int physicalIndex = 41;
  int isTypeSpecEditable = 1;
  int isOnboardingStaffEditable = 1;
  int isActiveStaffEditable = 1;
  int isPublicVisible = 1;
  int isEnabled = 1;
  int isEnabledEditable = 0;
  int isMandatory = 1;
  int isMandatoryEditable = 0;
  
  ProfileTemplateDTO profileTemplateDTO = new ProfileTemplateDTO();
  ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();

  {
    profileTemplateDTO.setOrgId(orgId);
    profileTemplateDTO.setDisplayName(displayName);
    profileTemplateDTO.setIsPreset(1);
    profileTemplateDTO.setCreatedUserId(userId);

    profileFieldDTO.setOrgId(orgId);
    profileFieldDTO.setProfileTemplateId(profileTemplateId);
    profileFieldDTO.setReferenceName(referenceName);
    profileFieldDTO.setPhysicalIndex(physicalIndex);
    profileFieldDTO.setIsTypeSpecEditable(isTypeSpecEditable);
    profileFieldDTO.setIsOnboardingStaffEditable(isOnboardingStaffEditable);
    profileFieldDTO.setIsActiveStaffEditable(isActiveStaffEditable);
    profileFieldDTO.setIsPublicVisible(isPublicVisible);
    profileFieldDTO.setIsEnabled(isEnabled);
    profileFieldDTO.setIsEnabledEditable(isEnabledEditable);
    profileFieldDTO.setIsMandatory(isMandatory);
    profileFieldDTO.setIsMandatoryEditable(isMandatoryEditable);
    profileFieldDTO.setCreatedUserId(userId);
  }

  @Test
  public void testAddPresetProfileTemplate() throws Exception {

    LongDTO id = profileTemplateFacade.addPresetProfileTemplate(orgId, userId, userId);
    ProfileTemplateDTO insertedTemp = profileTemplateFacade.getProfileTemplate(orgId, id.getData(), userId, userId);

    Assert.assertEquals(orgId, insertedTemp.getOrgId().longValue());
    ProfileFieldListDTO profileFieldListDTO =
        profileTemplateFacade.listProfileField(orgId, insertedTemp.getProfileTemplateId(), userId, userId);
    Assert.assertNotEquals(0, profileFieldListDTO.getProfileFieldDTOs().size());

  }

  @Test
  public void testAddCustomProfileTemplate() throws Exception {

    // test normal case

    LongDTO id = profileTemplateFacade.addCustomProfileTemplate(orgId, displayName, userId, userId);
    ProfileTemplateDTO insertedTemp = profileTemplateFacade.getProfileTemplate(orgId, id.getData(), userId, userId);

    Assert.assertEquals(displayName, insertedTemp.getDisplayName());
    ProfileFieldListDTO profileFieldListDTO =
        profileTemplateFacade.listProfileField(orgId, insertedTemp.getProfileTemplateId(), userId, userId);
    Assert.assertNotEquals(0, profileFieldListDTO.getProfileFieldDTOs().size());

    // test abnormal case
    try {
      profileTemplateFacade.addCustomProfileTemplate(orgId, "", userId, userId);
    } catch (ServiceStatusException e) {
      Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), e.getServiceStatus().getCode());
    }
  }

  @Test
  public void testGetProfileTemplate() throws Exception {
    // already tested, skip
  }

  @Test
  public void testListProfileTemplate() throws Exception {
    profileTemplateFacade.addCustomProfileTemplate(orgId, displayName, userId, userId);
    profileTemplateFacade.addPresetProfileTemplate(orgId, userId, userId);

    ProfileTemplateListDTO profileTemplateListDTO = profileTemplateFacade.listProfileTemplate(orgId, userId, userId);
    Assert.assertEquals(2, profileTemplateListDTO.getProfileTemplateDTOs().size());
  }

  @Test
  public void testUpdateProfileTemplateDisplayName() throws Exception {

    long insertedId = profileTemplateFacade.addCustomProfileTemplate(orgId, displayName, userId, userId).getData();
    String updatedDisplayName = displayName + displayName;
    profileTemplateFacade.updateProfileTemplateDisplayName(orgId, insertedId, updatedDisplayName, userId, userId);

    ProfileTemplateDTO updatedTemp = profileTemplateFacade.getProfileTemplate(orgId, insertedId, userId, userId);
    Assert.assertEquals(updatedDisplayName, updatedTemp.getDisplayName());
  }

  @Test
  public void testDeleteProfileTemplate() throws Exception {
    long insertedId = profileTemplateFacade.addCustomProfileTemplate(orgId, displayName, userId, userId).getData();
    profileTemplateFacade.deleteProfileTemplate(orgId, insertedId, userId, userId);

    ProfileTemplateDTO updatedTemp = profileTemplateFacade.getProfileTemplate(orgId, insertedId, userId, userId);
    Assert.assertEquals(ServiceStatus.UP_PROFILE_TEMPLATE_NOT_FOUND.getCode(), updatedTemp.getServiceStatusDTO().getCode());
  }

  @Test
  public void testGetProfileField() throws Exception {
    LongDTO id = profileTemplateFacade.addPresetProfileTemplate(orgId, userId, userId);
    ProfileTemplateDTO insertedTemp = profileTemplateFacade.getProfileTemplate(orgId, id.getData(), userId, userId);

    Assert.assertEquals(orgId, insertedTemp.getOrgId().longValue());
    ProfileFieldListDTO profileFieldListDTO =
        profileTemplateFacade.listProfileField(orgId, insertedTemp.getProfileTemplateId(), userId, userId);
    ProfileFieldDTO firstPF = profileFieldListDTO.getProfileFieldDTOs().get(1);

    ProfileFieldDTO profileFieldDTO =
        profileTemplateFacade.getProfileField(orgId, firstPF.getProfileFieldId(), userId, userId);
    Assert.assertEquals(firstPF.getContainerId(), profileFieldDTO.getContainerId());
  }

  /**
   *
   * Method: addContainerProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId)
   *
   */
  @Test
  public void testAddContainerProfileField() throws Exception {

    long currTemplateId = profileTemplateFacade.addCustomProfileTemplate(orgId, displayName, userId, userId).getData();

    profileFieldDTO.setDataType(DataType.CONTAINER.getCode());
    profileFieldDTO.setProfileTemplateId(currTemplateId);
    profileFieldDTO.setDisplayName(displayName);
    LongDTO addResult = profileTemplateFacade.addContainerProfileField(orgId, profileFieldDTO, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), addResult.getServiceStatusDTO().getCode());

    ProfileFieldDTO getResult = profileTemplateFacade.getProfileField(orgId, addResult.getData(), userId, userId);
    Assert.assertEquals(getResult.getProfileFieldId().longValue(), addResult.getData());

  }

  /**
   *
   * Method: addDataProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId)
   *
   */
  @Test
  public void testAddDataProfileField() throws Exception {

    long currTemplateId = profileTemplateFacade.addCustomProfileTemplate(orgId, displayName, userId, userId).getData();

    profileFieldDTO.setDataType(DataType.CONTAINER.getCode());
    profileFieldDTO.setProfileTemplateId(currTemplateId);
    profileFieldDTO.setDisplayName(displayName);
    LongDTO addResult = profileTemplateFacade.addContainerProfileField(orgId, profileFieldDTO, userId, userId);
    ProfileFieldDTO getResult = profileTemplateFacade.getProfileField(orgId, addResult.getData(), userId, userId);

    profileFieldDTO.setDataType(DataType.SHORT_TEXT.getCode());
    profileFieldDTO.setProfileTemplateId(currTemplateId);
    profileFieldDTO.setDisplayName(displayName);
    profileFieldDTO.setContainerId(getResult.getProfileFieldId());
    addResult = profileTemplateFacade.addDataProfileField(orgId, profileFieldDTO, userId, userId);
    getResult = profileTemplateFacade.getProfileField(orgId, addResult.getData(), userId, userId);
    System.out.println("fine=" + addResult);
    Assert.assertEquals(addResult.getData(), getResult.getProfileFieldId().longValue());
  }

  /**
   *
   * Method: listAllProfileFieldOfTemplate(long orgId, long profileTemplateId, long actorUserId, long adminUserId)
   *
   */
  @Test
  public void testListProfileField() throws Exception {
//TODO: Test goes here...
  }

  /**
   *
   * Method: updateContainerProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId)
   *
   */
  @Test
  public void testUpdateContainerProfileField() throws Exception {
//TODO: Test goes here...
  }

  /**
   *
   * Method: updateDataProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId)
   *
   */
  @Test
  public void testUpdateDataProfileField() throws Exception {
//TODO: Test goes here...
  }

  /**
   *
   * Method: moveContainerProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId)
   *
   */
  @Test
  public void testMoveContainerProfileField() throws Exception {
//TODO: Test goes here...
  }

  /**
   *
   * Method: moveDataProfileField(long orgId, ProfileFieldDTO profileFieldDTO, long actorUserId, long adminUserId)
   *
   */
  @Test
  public void testMoveDataProfileField() throws Exception {
//TODO: Test goes here...
  }

  /**
   *
   * Method: deleteContainerProfileField(long orgId, long profileFieldId, long actorUserId, long adminUserId)
   *
   */
  @Test
  public void testDeleteContainerProfileField() throws Exception {
//TODO: Test goes here...
  }

  /**
   *
   * Method: deleteDataProfileField(long orgId, long profileFieldId, long actorUserId, long adminUserId)
   *
   */
  @Test
  public void testDeleteDataProfileField() throws Exception {
//TODO: Test goes here...
  }

  @Test
  public void testListAddressRegion() {

    int parentId = 1000000;
    AddressRegionListDTO listResult = null;

    listResult = profileTemplateFacade.listAddressRegion(orgId, parentId, userId, userId);
    Assert.assertEquals(0, listResult.getAddressRegionDTOs().size());

    parentId = 0;
    listResult = profileTemplateFacade.listAddressRegion(orgId, parentId, userId, userId);
    Assert.assertEquals(1, listResult.getAddressRegionDTOs().size());

    parentId = 10;
    listResult = profileTemplateFacade.listAddressRegion(orgId, parentId, userId, userId);
    Assert.assertTrue(listResult.getAddressRegionDTOs().size() > 1);
  }

  /**
   * Method: listPickOptionOfConfigType()
   * Case 1: normal
   */
  @Test
  public void testListPickOptionOfConfigTypeCase1() {

    /**
     * prepare
     */
    orgPickOptionService.initJobTitleAndJobLevelOfOrg(mockOrgId);

    /**
     * verify
     */
    OrgPickOptionListDTO rpcOrgPickOptionList =
        profileTemplateFacade.listOrgPickOptionOfConfigType(mockOrgId, ConfigType.JOB_TITLE.getCode());
    LOGGER.info("rpcOrgPickOptionList={}", rpcOrgPickOptionList);
    Assert.assertTrue(rpcOrgPickOptionList.getOrgPickOptionDTOs().size() > 0);

  }

  /**
   * Method: batchUpdateOrgPickOptions()
   * Case 1: normal
   */
  @Test
  public void testBatchUpdateOrgPickOptionsCase1() {

    /**
     * prepare
     */
    orgPickOptionService.initJobTitleAndJobLevelOfOrg(mockOrgId);
    OrgPickOptionListDTO rpcInitOrgPickOptionList =
        profileTemplateFacade.listOrgPickOptionOfConfigType(mockOrgId, ConfigType.JOB_TITLE.getCode());
    Assert.assertTrue(rpcInitOrgPickOptionList.getOrgPickOptionDTOs().size() > 0);

    /**
     * verify
     */
    OrgPickOptionDTO orgPickOptionDTO = new OrgPickOptionDTO();
    List<OrgPickOptionDTO> initOrgPickOptionDTOs = rpcInitOrgPickOptionList.getOrgPickOptionDTOs();
    int initSize = rpcInitOrgPickOptionList.getOrgPickOptionDTOs().size();
    BeanUtils.copyProperties(initOrgPickOptionDTOs.get(initSize - 1), orgPickOptionDTO);
    String newOptonValue = "BrandNewValue";
    orgPickOptionDTO.setOrgPickOptionId(null);
    orgPickOptionDTO.setOptionValue(newOptonValue);
    orgPickOptionDTO.setOptionIndex(initSize);
    initOrgPickOptionDTOs.add(orgPickOptionDTO);
    OrgPickOptionListDTO orgPickOptionListDTO = new OrgPickOptionListDTO();
    orgPickOptionListDTO.setOrgPickOptionDTOs(initOrgPickOptionDTOs);
    profileTemplateFacade.batchUpdateOrgPickOptions(mockOrgId, orgPickOptionListDTO, mockUserId);

    OrgPickOptionListDTO addedOrgPickOptionList =
        profileTemplateFacade.listOrgPickOptionOfConfigType(mockOrgId, ConfigType.JOB_TITLE.getCode());
    Assert.assertEquals(initSize + 1, addedOrgPickOptionList.getOrgPickOptionDTOs().size());
    Assert.assertEquals(newOptonValue, addedOrgPickOptionList.getOrgPickOptionDTOs().get(initSize).getOptionValue());

  }

  /**
   * Method: batchUpdateOrgPickOptions()
   * Case 2: abnormal, invalid option sequence with dup optionValue
   */
  @Test
  public void testBatchUpdateOrgPickOptionsCase2() {

    /**
     * prepare
     */
    orgPickOptionService.initJobTitleAndJobLevelOfOrg(mockOrgId);
    OrgPickOptionListDTO rpcInitOrgPickOptionList =
        profileTemplateFacade.listOrgPickOptionOfConfigType(mockOrgId, ConfigType.JOB_TITLE.getCode());
    Assert.assertTrue(rpcInitOrgPickOptionList.getOrgPickOptionDTOs().size() > 0);

    /**
     * verify
     */
    OrgPickOptionDTO orgPickOptionDTO = new OrgPickOptionDTO();
    List<OrgPickOptionDTO> initOrgPickOptionDTOs = rpcInitOrgPickOptionList.getOrgPickOptionDTOs();
    int initSize = rpcInitOrgPickOptionList.getOrgPickOptionDTOs().size();
    BeanUtils.copyProperties(initOrgPickOptionDTOs.get(initSize - 1), orgPickOptionDTO);
    orgPickOptionDTO.setOrgPickOptionId(null);
    orgPickOptionDTO.setOptionIndex(initSize);
    initOrgPickOptionDTOs.add(orgPickOptionDTO);
    OrgPickOptionListDTO orgPickOptionListDTO = new OrgPickOptionListDTO();
    orgPickOptionListDTO.setOrgPickOptionDTOs(initOrgPickOptionDTOs);

    VoidDTO rpcUpdateResult =
        profileTemplateFacade.batchUpdateOrgPickOptions(mockOrgId, rpcInitOrgPickOptionList, mockUserId);
    Assert.assertEquals(ServiceStatus.UO_INVALID_PICK_OPTION.getCode(),
                        rpcUpdateResult.getServiceStatusDTO().getCode());

  }

  @Test
  public void testListAllProfileFieldOfOrg() {

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

    CoreUserProfile addedCUP = onboardingFlowService.createOrgAndFirstUser(
        org, fieldValues, userEmployment);

    // verify
    long orgId = addedCUP.getOrgId();
    ProfileFieldListDTO profileFieldListDTO =
        profileTemplateFacade.listAllProfileFieldOfOrg(orgId, mockUserId, mockUserId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), profileFieldListDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(SystemProfileField.values().length, profileFieldListDTO.getProfileFieldDTOs().size());

  }

  @Test
  public void testUpdateUserProfileConfig() {

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

    CoreUserProfile addedCUP = onboardingFlowService.createOrgAndFirstUser(
        org, fieldValues, userEmployment);

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
    ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
    BeanUtils.copyProperties(theUPC, profileFieldDTO);
    profileTemplateFacade.updateUserProfileConfig(orgId, profileFieldDTO, mockUserId, mockUserId);

    UserProfileConfig updatedUPC = userProfileConfigDao
        .findUserProfileConfigByOrgIdAndReferenceName(orgId, theUPC.getReferenceName());
    Assert.assertEquals(updatedIsEnabled, updatedUPC.getIsEnabled().intValue());

  }


}