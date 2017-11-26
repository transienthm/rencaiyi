package hr.wozai.service.user.server.test.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.TimeUtil;

import hr.wozai.service.servicecommons.commons.consts.TypeSpecConsts;
import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.enums.UserGender;
import hr.wozai.service.user.server.dao.userorg.ProfileTemplateDao;
import hr.wozai.service.user.server.dao.userorg.UserProfileConfigDao;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.ProfileTemplate;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;
import hr.wozai.service.user.server.service.OnboardingFlowService;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.dao.userorg.PickOptionDao;
import hr.wozai.service.user.server.dao.userorg.ProfileFieldDao;
import hr.wozai.service.user.server.model.userorg.AddressRegion;
import hr.wozai.service.user.server.model.userorg.PickOption;
import hr.wozai.service.user.server.model.userorg.ProfileField;
import hr.wozai.service.user.server.service.ProfileFieldService;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-01
 */
public class ProfileFieldServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileFieldServiceImplTest.class);

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  ProfileFieldDao profileFieldDao;

  @Autowired
  PickOptionDao pickOptionDao;

  @Autowired
  ProfileFieldService profileFieldService;

  @Autowired
  OnboardingFlowService onboardingFlowService;

  @Autowired
  UserProfileConfigDao userProfileConfigDao;

  @Autowired
  ProfileTemplateDao profileTemplateDao;

  // data
  long mockOrgId = 19999999L;
  long mockUserId = 29999999L;
  String displayName = "模板";
  ProfileTemplate profileTemplate = new ProfileTemplate();

  // data
  long userId = 19999999L;
  long orgId = 29999999L;
  long profileTemplateId = 39999999L;
  long containerId = 49999999L;
  String dataFieldReferenceNameOne = "DataFieldOne";
  String dataFieldReferenceNameTwo = "DataFieldTwo";
  String containerFieldReferenceNameOne = "ContainerFieldOne";
  String containerFieldReferenceNameTwo = "ContainerFieldTwo";

  private String fullName = "马人才易";
  private String emailAddress = "mawozai@sqian.com";
  private String mobilePhone = "13566677777";
  private String personalEmail = "mawozaisqian@qq.com";
  private Integer gender = UserGender.MALE.getCode();

  private static String emailAddressOfSuperAdmin = "superadminwozai@sqian.com";
  private static String passwordOfSuperAdmin = "Wozai123";

  int physicalIndex = 41;
  int isTypeSpecEditable = 1;
  int isOnboardingStaffEditable = 1;
  int isActiveStaffEditable = 1;
  int isPublicVisible = 1;
  int isEnabled = 1;
  int isEnabledEditable = 0;
  int isMandatory = 1;
  int isMandatoryEditable = 0;
  ProfileField dataField;
  ProfileField containerField;

  {
    profileTemplate.setOrgId(mockOrgId);
    profileTemplate.setDisplayName(displayName);
    profileTemplate.setIsPreset(1);
    profileTemplate.setCreatedUserId(mockUserId);

    dataField = new ProfileField();
    dataField.setOrgId(orgId);
    dataField.setProfileTemplateId(profileTemplateId);
    dataField.setContainerId(containerId);
    dataField.setDisplayName(displayName);
    dataField.setReferenceName(dataFieldReferenceNameOne);
    dataField.setPhysicalIndex(physicalIndex);
    dataField.setIsTypeSpecEditable(isTypeSpecEditable);
    dataField.setIsOnboardingStaffEditable(isOnboardingStaffEditable);
    dataField.setIsActiveStaffEditable(isActiveStaffEditable);
    dataField.setIsPublicVisible(isPublicVisible);
    dataField.setIsEnabled(isEnabled);
    dataField.setIsEnabledEditable(isEnabledEditable);
    dataField.setIsMandatory(isMandatory);
    dataField.setIsMandatoryEditable(isMandatoryEditable);
    dataField.setCreatedUserId(userId);

    containerField = new ProfileField();
    containerField.setDataType(DataType.CONTAINER.getCode());
    containerField.setOrgId(orgId);
    containerField.setProfileTemplateId(profileTemplateId);
    containerField.setContainerId(null);
    containerField.setReferenceName(containerFieldReferenceNameOne);
    containerField.setDisplayName(containerFieldReferenceNameOne);
    containerField.setPhysicalIndex(null);
    containerField.setIsTypeSpecEditable(isTypeSpecEditable);
    containerField.setIsOnboardingStaffEditable(isOnboardingStaffEditable);
    containerField.setIsActiveStaffEditable(isActiveStaffEditable);
    containerField.setIsPublicVisible(isPublicVisible);
    containerField.setIsEnabled(isEnabled);
    containerField.setIsEnabledEditable(isEnabledEditable);
    containerField.setIsMandatory(isMandatory);
    containerField.setIsMandatoryEditable(isMandatoryEditable);
    containerField.setCreatedUserId(userId);
  }


  @Test
  public void testAddCustomDataProfileField() throws Exception {

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);

    int presetFieldCount = profileFields.size();
    long lastContainerId = -1;
    for (int i = 0; i < profileFields.size(); i++) {
      if (DataType.CONTAINER.getCode() == profileFields.get(i).getDataType()) {
        lastContainerId = profileFields.get(i).getProfileFieldId();
      }
    }

    // test normal case
    ProfileField toAddContainerField = new ProfileField();
    toAddContainerField.setOrgId(orgId);
    toAddContainerField.setProfileTemplateId(profileTemplateId);
    toAddContainerField.setContainerId(lastContainerId);
    toAddContainerField.setDataType(DataType.SHORT_TEXT.getCode());
    toAddContainerField.setDisplayName(dataFieldReferenceNameOne);
    toAddContainerField.setIsPublicVisible(1);
    toAddContainerField.setIsEnabled(1);
    toAddContainerField.setIsMandatory(0);
    toAddContainerField.setCreatedUserId(userId);
    profileFieldService.addCustomDataProfileField(toAddContainerField);
    List<ProfileField> addedProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateId(orgId, profileTemplateId);
    Assert.assertEquals(presetFieldCount + 1, addedProfileFields.size());

    // test abnormal case: no containerId or non-existed containerId
    toAddContainerField.setContainerId(null);
    try {
      profileFieldService.addCustomDataProfileField(toAddContainerField);
    } catch (ServiceStatusException e) {
      LOGGER.error("I got you first");
      Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), e.getServiceStatus().getCode());
    }
    toAddContainerField.setContainerId(1982183718L);
    try {
      profileFieldService.addCustomDataProfileField(toAddContainerField);
    } catch (ServiceStatusException e) {
      LOGGER.error("I got you second");
      Assert.assertEquals(ServiceStatus.UP_INVALID_CONTAINER_FOR_DATA_FIELD.getCode(), e.getServiceStatus().getCode());
    }

  }

  @Test
  public void testAddCustomContainerProfileField() throws Exception {

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    int containerCount = 0;
    for (ProfileField profileField: profileFields) {
      if (DataType.CONTAINER.getCode() == profileField.getDataType()) {
        containerCount ++;
      }
    }

    // test normal case
    ProfileField toAddContainerField = new ProfileField();
    toAddContainerField.setOrgId(orgId);
    toAddContainerField.setProfileTemplateId(profileTemplateId);
    toAddContainerField.setDataType(DataType.CONTAINER.getCode());
    toAddContainerField.setDisplayName(dataFieldReferenceNameOne);
    toAddContainerField.setIsPublicVisible(1);
    toAddContainerField.setIsEnabled(1);
    toAddContainerField.setIsMandatory(0);
    toAddContainerField.setCreatedUserId(userId);
    profileFieldService.addCustomContainerProfileField(toAddContainerField);
    profileFieldService.addCustomContainerProfileField(toAddContainerField);
    List<ProfileField> addedProfileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    ProfileField firstContainer = addedProfileFields.get(addedProfileFields.size() - 2);
    ProfileField secondContainer = addedProfileFields.get(addedProfileFields.size() - 1);
    Assert.assertEquals("CTN_0", firstContainer.getReferenceName());
    Assert.assertEquals("CTN_1", secondContainer.getReferenceName());

    // have passed the following abnormal case; block it for the time being
//    // test abnormal case: too many containers
//    for (int i = 0; i <= MetadataConsts.MAX_CONTAINER_FIELD_INDEX - 2 - containerCount; i++) {
//      profileFieldService.addCustomContainerProfileField(toAddContainerField);
//    }
//
//    try {
//      profileFieldService.addCustomContainerProfileField(toAddContainerField);
//    } catch (ServiceStatusException e) {
//      LOGGER.error("Here we go");
//      Assert.assertEquals(ServiceStatus.CONTAINER_FIELD_NUMBER_UPPERBOUND.getCode(), e.getServiceStatus().getCode());
//    }

  }

  @Test
  public void testPickOption() {

//    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
//    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
//    int presetFieldCount = profileFields.size();
//    long lastContainerId = -1;
//    for (int i = 0; i < profileFields.size(); i++) {
//      if (DataType.CONTAINER.getCode() == profileFields.get(i).getDataType()) {
//        lastContainerId = profileFields.get(i).getProfileFieldId();
//      }
//    }
//
//    // normal case: add field
//    ProfileField toAddDataField = new ProfileField();
//
//    toAddDataField = new ProfileField();
//    toAddDataField.setOrgId(orgId);
//    toAddDataField.setProfileTemplateId(profileTemplateId);
//    toAddDataField.setContainerId(lastContainerId);
//    toAddDataField.setDataType(DataType.SINGLE_PICK.getCode());
//    toAddDataField.setDisplayName(dataFieldReferenceNameOne);
//    toAddDataField.setIsPublicVisible(1);
//    toAddDataField.setIsEnabled(1);
//    toAddDataField.setIsMandatory(0);
//    toAddDataField.setCreatedUserId(userId);
//
//    List<PickOption> pickOptions = new ArrayList<>();
//    int optionCount = 3;
//    for (int i = 0; i < optionCount; i++) {
//      PickOption pickOption = new PickOption();
//      pickOption.setOrgId(orgId);
//      pickOption.setProfileFieldId(dataField.getProfileFieldId());
//      pickOption.setOptionValue(i + "");
//      pickOption.setOptionIndex(i);
//      pickOption.setIsDefault(0);
//      pickOptions.add(pickOption);
//    }
//
//    JSONObject typeSpec = new JSONObject();
//    JSONArray pickOptionArray = JSONArray.parseArray(JSONArray.toJSONString(pickOptions));
//    typeSpec.put(TypeSpecConsts.PICK_OPTIONS_KEY, pickOptionArray);
//    toAddDataField.setTypeSpec(typeSpec);
//
//    long singlePickFieldId = profileFieldService.addCustomDataProfileField(toAddDataField);
//    ProfileField addedSinglePickField = profileFieldService.getProfileField(orgId, singlePickFieldId);
//    Assert.assertEquals(
//        optionCount, addedSinglePickField.getTypeSpec().getJSONArray(TypeSpecConsts.PICK_OPTIONS_KEY).size());
//
//    // normal case: update field
//    pickOptionArray = addedSinglePickField.getTypeSpec().getJSONArray(TypeSpecConsts.PICK_OPTIONS_KEY);
//    System.out.println("------------------------PickOptionArray1---------------------=" + pickOptionArray);
//    pickOptions = FastJSONUtils.convertJSONArrayToObjectList(pickOptionArray, PickOption.class);
//    System.out.println("------------------------PickOptionArray2---------------------=" + pickOptionArray);
//    PickOption lastPickOption = pickOptions.get(optionCount - 1);
//    lastPickOption.setPickOptionId(null);
//    pickOptionArray = JSONArray.parseArray(JSONArray.toJSONString(pickOptions));
//    addedSinglePickField.getTypeSpec().put(TypeSpecConsts.PICK_OPTIONS_KEY, pickOptionArray);
//
//    profileFieldService.updateDataProfileField(addedSinglePickField);
//    ProfileField updatedSinglePickField = profileFieldService.getProfileField(orgId, singlePickFieldId);
//
//    System.out.println("addedSinglePickField=" + addedSinglePickField);
//    System.out.println("updatedSinglePickField=" + updatedSinglePickField);
//
//    pickOptionArray = updatedSinglePickField.getTypeSpec().getJSONArray(TypeSpecConsts.PICK_OPTIONS_KEY);
//    pickOptions = FastJSONUtils.convertJSONArrayToObjectList(pickOptionArray, PickOption.class);
//    Assert.assertEquals(optionCount + 1, pickOptions.size());
//
//    // abnormal case 1: no pick option
//    toAddDataField = new ProfileField();
//    toAddDataField.setOrgId(orgId);
//    toAddDataField.setProfileTemplateId(profileTemplateId);
//    toAddDataField.setContainerId(lastContainerId);
//    toAddDataField.setDataType(DataType.SINGLE_PICK.getCode());
//    toAddDataField.setDisplayName(dataFieldReferenceNameOne);
//    toAddDataField.setIsPublicVisible(1);
//    toAddDataField.setIsEnabled(1);
//    toAddDataField.setIsMandatory(0);
//    toAddDataField.setCreatedUserId(userId);
//    try {
//      profileFieldService.addCustomDataProfileField(toAddDataField);
//    } catch (ServiceStatusException e) {
//      LOGGER.error("I got you 1");
//      Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), e.getServiceStatus().getCode());
//    }
//
//    // abnormal case 2: invalid optionIndex
//    toAddDataField = new ProfileField();
//    toAddDataField.setOrgId(orgId);
//    toAddDataField.setProfileTemplateId(profileTemplateId);
//    toAddDataField.setContainerId(lastContainerId);
//    toAddDataField.setDataType(DataType.SINGLE_PICK.getCode());
//    toAddDataField.setDisplayName(dataFieldReferenceNameOne);
//    toAddDataField.setIsPublicVisible(1);
//    toAddDataField.setIsEnabled(1);
//    toAddDataField.setIsMandatory(0);
//    toAddDataField.setCreatedUserId(userId);
//
//    pickOptions = new ArrayList<>();
//    for (int i = 0; i < 3; i++) {
//      PickOption pickOption = new PickOption();
//      pickOption.setOrgId(orgId);
//      pickOption.setProfileFieldId(dataField.getProfileFieldId());
//      pickOption.setOptionValue(i + "");
//      pickOption.setOptionIndex(1);
//      pickOption.setIsDefault(0);
//      pickOptions.add(pickOption);
//    }
//    typeSpec = new JSONObject();
//    pickOptionArray = JSONArray.parseArray(JSONArray.toJSONString(pickOptions));
//    typeSpec.put(TypeSpecConsts.PICK_OPTIONS_KEY, pickOptionArray);
//    toAddDataField.setTypeSpec(typeSpec);
//    try {
//      profileFieldService.addCustomDataProfileField(toAddDataField);
//    } catch (ServiceStatusException e) {
//      LOGGER.error("I got you 2");
//      Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), e.getServiceStatus().getCode());
//    }
//
//    // abnormal case 3: dup optionValue
//    toAddDataField = new ProfileField();
//    toAddDataField.setOrgId(orgId);
//    toAddDataField.setProfileTemplateId(profileTemplateId);
//    toAddDataField.setContainerId(lastContainerId);
//    toAddDataField.setDataType(DataType.SINGLE_PICK.getCode());
//    toAddDataField.setDisplayName(dataFieldReferenceNameOne);
//    toAddDataField.setIsPublicVisible(1);
//    toAddDataField.setIsEnabled(1);
//    toAddDataField.setIsMandatory(0);
//    toAddDataField.setCreatedUserId(userId);
//
//    pickOptions = new ArrayList<>();
//    for (int i = 0; i < 3; i++) {
//      PickOption pickOption = new PickOption();
//      pickOption.setOrgId(orgId);
//      pickOption.setProfileFieldId(dataField.getProfileFieldId());
//      pickOption.setOptionValue("100");
//      pickOption.setOptionIndex(i);
//      pickOption.setIsDefault(0);
//      pickOptions.add(pickOption);
//    }
//    typeSpec = new JSONObject();
//    pickOptionArray = JSONArray.parseArray(JSONArray.toJSONString(pickOptions));
//    typeSpec.put(TypeSpecConsts.PICK_OPTIONS_KEY, pickOptionArray);
//    toAddDataField.setTypeSpec(typeSpec);
//    try {
//      profileFieldService.addCustomDataProfileField(toAddDataField);
//    } catch (ServiceStatusException e) {
//      LOGGER.error("I got you 3");
//      Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), e.getServiceStatus().getCode());
//    }
//
//    // abnormal case 4: SINGLE_PICK has more than one default option
//    // abnormal case 3: dup optionValue
//    toAddDataField = new ProfileField();
//    toAddDataField.setOrgId(orgId);
//    toAddDataField.setProfileTemplateId(profileTemplateId);
//    toAddDataField.setContainerId(lastContainerId);
//    toAddDataField.setDataType(DataType.SINGLE_PICK.getCode());
//    toAddDataField.setDisplayName(dataFieldReferenceNameOne);
//    toAddDataField.setIsPublicVisible(1);
//    toAddDataField.setIsEnabled(1);
//    toAddDataField.setIsMandatory(0);
//    toAddDataField.setCreatedUserId(userId);
//
//    pickOptions = new ArrayList<>();
//    for (int i = 0; i < 3; i++) {
//      PickOption pickOption = new PickOption();
//      pickOption.setOrgId(orgId);
//      pickOption.setProfileFieldId(dataField.getProfileFieldId());
//      pickOption.setOptionValue(i+ "");
//      pickOption.setOptionIndex(i);
//      pickOption.setIsDefault(1);
//      pickOptions.add(pickOption);
//    }
//    typeSpec = new JSONObject();
//    pickOptionArray = JSONArray.parseArray(JSONArray.toJSONString(pickOptions));
//    typeSpec.put(TypeSpecConsts.PICK_OPTIONS_KEY, pickOptionArray);
//    toAddDataField.setTypeSpec(typeSpec);
//    try {
//      profileFieldService.addCustomDataProfileField(toAddDataField);
//    } catch (ServiceStatusException e) {
//      LOGGER.error("I got you 4");
//      Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), e.getServiceStatus().getCode());
//    }
  }

  @Test
  public void testAddAllPresetFieldForProfileTemplate() throws Exception {
    // already tested; skip
  }

  @Test
  public void testGetProfileFieldByReferenceName() throws Exception {

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);

    ProfileField jobTitleField = profileFieldService.getProfileField(orgId, profileTemplateId, "jobTitle");
    Assert.assertNotNull(jobTitleField);

  }

  @Test
  public void testListProfileField() throws Exception {
    // already tested; skip
  }

  @Test
  public void testListDataProfileField() throws Exception {
    // already tested; skip
  }

  @Test
  public void testUpdateDataProfileField() throws Exception {

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    int presetFieldCount = profileFields.size();
    long lastContainerId = -1;
    for (int i = 0; i < profileFields.size(); i++) {
      if (DataType.CONTAINER.getCode() == profileFields.get(i).getDataType()) {
        lastContainerId = profileFields.get(i).getProfileFieldId();
      }
    }

    // test normal case
    ProfileField toAddContainerField = new ProfileField();
    toAddContainerField.setOrgId(orgId);
    toAddContainerField.setProfileTemplateId(profileTemplateId);
    toAddContainerField.setContainerId(lastContainerId);
    toAddContainerField.setDataType(DataType.SHORT_TEXT.getCode());
    toAddContainerField.setDisplayName(dataFieldReferenceNameOne);
    toAddContainerField.setIsPublicVisible(1);
    toAddContainerField.setIsEnabled(1);
    toAddContainerField.setIsMandatory(0);
    toAddContainerField.setCreatedUserId(userId);
    long addedId = profileFieldService.addCustomDataProfileField(toAddContainerField);

    ProfileField addedProfileField = profileFieldService.getProfileField(orgId, addedId);
    addedProfileField.setDisplayName("newName");
    addedProfileField.setIsPublicVisible(0);
    addedProfileField.setIsEnabled(0);
    addedProfileField.setIsMandatory(1);
    profileFieldService.updateDataProfileField(addedProfileField);
    ProfileField updatedProfileField = profileFieldService.getProfileField(orgId, addedId);
    Assert.assertEquals("newName", updatedProfileField.getDisplayName());
    Assert.assertEquals(0, updatedProfileField.getIsPublicVisible().intValue());
    Assert.assertEquals(0, updatedProfileField.getIsEnabled().intValue());
    Assert.assertEquals(1, updatedProfileField.getIsMandatory().intValue());

  }

  @Test
  public void testUpdateContainerProfileField() throws Exception {

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    int containerCount = 0;
    for (ProfileField profileField: profileFields) {
      if (DataType.CONTAINER.getCode() == profileField.getDataType()) {
        containerCount ++;
      }
    }

    // test normal case
    ProfileField toAddContainerField = new ProfileField();
    toAddContainerField.setOrgId(orgId);
    toAddContainerField.setProfileTemplateId(profileTemplateId);
    toAddContainerField.setDataType(DataType.CONTAINER.getCode());
    toAddContainerField.setDisplayName(dataFieldReferenceNameOne);
    toAddContainerField.setIsPublicVisible(1);
    toAddContainerField.setIsEnabled(1);
    toAddContainerField.setIsMandatory(0);
    toAddContainerField.setCreatedUserId(userId);
    long addedContainerId = profileFieldService.addCustomContainerProfileField(toAddContainerField);

    ProfileField addedContainerField = profileFieldService.getProfileField(orgId, addedContainerId);
    String updateDisplayName = "someContainer";
    addedContainerField.setDisplayName(updateDisplayName);
    profileFieldService.updateContainerProfileField(addedContainerField);
    ProfileField updatedContainerField = profileFieldService.getProfileField(orgId, addedContainerId);
    Assert.assertEquals(updateDisplayName, updatedContainerField.getDisplayName());

  }

  @Test
  public void testMoveDataProfileFiled() throws Exception {

    // prepare
    ProfileField toAddDataField = new ProfileField();
    toAddDataField.setOrgId(orgId);
    toAddDataField.setProfileTemplateId(profileTemplateId);
    toAddDataField.setDataType(DataType.SHORT_TEXT.getCode());
    toAddDataField.setDisplayName(dataFieldReferenceNameOne);
    toAddDataField.setIsPublicVisible(1);
    toAddDataField.setIsEnabled(1);
    toAddDataField.setIsMandatory(0);
    toAddDataField.setCreatedUserId(userId);

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    long insertedContainerId = profileFieldService.addCustomContainerProfileField(containerField);
    toAddDataField.setContainerId(insertedContainerId);
    long insertedDataFieldIdOne = profileFieldService.addCustomDataProfileField(toAddDataField);
    toAddDataField.setReferenceName(dataFieldReferenceNameTwo);
    long insertedDateFieldIdTwo = profileFieldService.addCustomDataProfileField(toAddDataField);

    ProfileField insertedDataFieldOne = profileFieldService.getProfileField(orgId, insertedDataFieldIdOne);
    ProfileField insertedDataFieldTwo = profileFieldService.getProfileField(orgId, insertedDateFieldIdTwo);
    int logicalIndexOne = insertedDataFieldOne.getLogicalIndex();
    int logicalIndexTwo = insertedDataFieldTwo.getLogicalIndex();
    insertedDataFieldTwo.setLogicalIndex(logicalIndexOne);
    insertedDataFieldTwo.setLastModifiedUserId(userId);
    profileFieldService.moveDataProfileFiled(insertedDataFieldTwo);

    // verify
    ProfileField movedDataFieldOne = profileFieldService.getProfileField(orgId, insertedDataFieldIdOne);
    ProfileField movedDataFieldTwo = profileFieldService.getProfileField(orgId, insertedDateFieldIdTwo);
    Assert.assertEquals(logicalIndexOne, movedDataFieldTwo.getLogicalIndex().intValue());
    Assert.assertEquals(logicalIndexTwo, movedDataFieldOne.getLogicalIndex().intValue());

  }

  @Test
  public void testMoveContainerProfileFiled() throws Exception {

    // prepare
    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    long insertedContainerIdOne = profileFieldService.addCustomContainerProfileField(containerField);
    containerField.setReferenceName(containerFieldReferenceNameTwo);
    long insertedContainerIdTwo = profileFieldService.addCustomContainerProfileField(containerField);

    ProfileField toAddDataField = new ProfileField();
    toAddDataField.setOrgId(orgId);
    toAddDataField.setContainerId(insertedContainerIdOne);
    toAddDataField.setProfileTemplateId(profileTemplateId);
    toAddDataField.setDataType(DataType.SHORT_TEXT.getCode());
    toAddDataField.setDisplayName(dataFieldReferenceNameOne);
    toAddDataField.setIsPublicVisible(1);
    toAddDataField.setIsEnabled(1);
    toAddDataField.setIsMandatory(0);
    toAddDataField.setCreatedUserId(userId);
    long insertedDataFieldId = profileFieldService.addCustomDataProfileField(toAddDataField);

    ProfileField insertedContainerOne = profileFieldService.getProfileField(orgId, insertedContainerIdOne);
    ProfileField insertedContainerTwo = profileFieldService.getProfileField(orgId, insertedContainerIdTwo);
    ProfileField insertedDataField = profileFieldService.getProfileField(orgId, insertedDataFieldId);
    int logicalIndexContainerOne = insertedContainerOne.getLogicalIndex();
    int logicalIndexContainerTwo = insertedContainerTwo.getLogicalIndex();
    int logicalIndexDataField = insertedDataField.getLogicalIndex();
    insertedContainerTwo.setLogicalIndex(logicalIndexContainerOne);
    insertedContainerTwo.setLastModifiedUserId(userId);
    profileFieldService.moveContainerProfileField(insertedContainerTwo);

    // verify
    ProfileField movedContainerFieldOne = profileFieldService.getProfileField(orgId, insertedContainerIdOne);
    ProfileField movedContainerFieldTwo = profileFieldService.getProfileField(orgId, insertedContainerIdTwo);
    ProfileField movedDataField = profileFieldService.getProfileField(orgId, insertedDataFieldId);

    Assert.assertEquals(logicalIndexContainerOne, movedContainerFieldTwo.getLogicalIndex().intValue());
    Assert.assertEquals(logicalIndexDataField, movedContainerFieldOne.getLogicalIndex().intValue());
    Assert.assertEquals(logicalIndexContainerTwo, movedDataField.getLogicalIndex().intValue());

  }

  @Test
  public void testDeleteDataProfileField() throws Exception {

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    long lastContainerId = -1;
    for (int i = 0; i < profileFields.size(); i++) {
      if (DataType.CONTAINER.getCode() == profileFields.get(i).getDataType()) {
        lastContainerId = profileFields.get(i).getProfileFieldId();
      }
    }

    // normal case: add field
    ProfileField toAddDataField = new ProfileField();

    toAddDataField = new ProfileField();
    toAddDataField.setOrgId(orgId);
    toAddDataField.setProfileTemplateId(profileTemplateId);
    toAddDataField.setContainerId(lastContainerId);
    toAddDataField.setDataType(DataType.SINGLE_PICK.getCode());
    toAddDataField.setDisplayName(dataFieldReferenceNameOne);
    toAddDataField.setIsPublicVisible(1);
    toAddDataField.setIsEnabled(1);
    toAddDataField.setIsMandatory(0);
    toAddDataField.setCreatedUserId(userId);

    List<PickOption> pickOptions = new ArrayList<>();
    int optionCount = 3;
    for (int i = 0; i < optionCount; i++) {
      PickOption pickOption = new PickOption();
      pickOption.setOrgId(orgId);
      pickOption.setProfileFieldId(dataField.getProfileFieldId());
      pickOption.setOptionValue(i + "");
      pickOption.setOptionIndex(i);
      pickOption.setIsDefault(0);
      pickOptions.add(pickOption);
    }

    JSONObject typeSpec = new JSONObject();
    JSONArray pickOptionArray = JSONArray.parseArray(JSONArray.toJSONString(pickOptions));
    typeSpec.put(TypeSpecConsts.PICK_OPTIONS_KEY, pickOptionArray);
    toAddDataField.setTypeSpec(typeSpec);

    long singlePickFieldId = profileFieldService.addCustomDataProfileField(toAddDataField);
    ProfileField addedSinglePickField = profileFieldService.getProfileField(orgId, singlePickFieldId);
    Assert.assertEquals(
        optionCount, addedSinglePickField.getTypeSpec().getJSONArray(TypeSpecConsts.PICK_OPTIONS_KEY).size());

    profileFieldService.deleteDataProfileField(orgId, singlePickFieldId, userId);
    try {
      profileFieldService.getProfileField(orgId, singlePickFieldId);
    } catch (ServiceStatusException e) {
      LOGGER.error("AHA!");
      Assert.assertEquals(ServiceStatus.UP_PROFILE_FIELD_NOT_FOUND.getCode(), e.getServiceStatus().getCode());
    }

    List<ProfileField> remainingProfileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    for (int i = 0; i < remainingProfileFields.size(); i++) {
      Assert.assertEquals(i, remainingProfileFields.get(i).getLogicalIndex().intValue());
    }

    List<PickOption> deletedPickOptions = pickOptionDao.listPickOptionByProfileFieldId(orgId, singlePickFieldId);
    Assert.assertEquals(0, deletedPickOptions.size());


  }

  @Test
  public void testDeleteContainerProfileField() throws Exception {

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    int presetFieldCount = profileFields.size();

    // test normal case
    ProfileField toAddContainerField = new ProfileField();
    toAddContainerField.setOrgId(orgId);
    toAddContainerField.setProfileTemplateId(profileTemplateId);
    toAddContainerField.setDataType(DataType.CONTAINER.getCode());
    toAddContainerField.setDisplayName(dataFieldReferenceNameOne);
    toAddContainerField.setIsPublicVisible(1);
    toAddContainerField.setIsEnabled(1);
    toAddContainerField.setIsMandatory(0);
    toAddContainerField.setCreatedUserId(userId);
    long addedContainerId = profileFieldService.addCustomContainerProfileField(toAddContainerField);
    toAddContainerField.setDataType(DataType.SHORT_TEXT.getCode());
    toAddContainerField.setContainerId(toAddContainerField.getProfileFieldId());
    profileFieldService.addCustomDataProfileField(toAddContainerField);

    profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    Assert.assertEquals(presetFieldCount + 2, profileFields.size());

    profileFieldService.deleteContainerProfileField(orgId, addedContainerId, userId);
    profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    Assert.assertEquals(presetFieldCount, profileFields.size());

  }

  @Test
  public void testDeleteAllProfileFieldOfTemplate() throws Exception {
    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    Assert.assertNotEquals(0, profileFields.size());

    profileFieldService.deleteAllProfileFieldOfTemplate(orgId, profileTemplateId, userId);
    profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    Assert.assertEquals(0, profileFields.size());

  }

  @Test
  public void testListAddressRegion() {

    List<AddressRegion> addressRegions = null;
    int parentId = 0;

    addressRegions = profileFieldService.listAddressRegion(parentId);
    Assert.assertEquals(1, addressRegions.size());

    parentId = 10;
    addressRegions = profileFieldService.listAddressRegion(parentId);
    Assert.assertTrue(addressRegions.size() > 1);

  }

  /**
   * Method: listDataProfileFieldOfContainer
   * Case:   #1, normal
   *
   */
  @Test
  public void testListDataProfileFieldOfContainerCase1() {

    List<ProfileField> profileFields = profileFieldService.listDataProfileFieldOfContainer(orgId, containerId);
    Assert.assertEquals(0, profileFields.size());

  }

  /**
   * Method: listPickOptionsByOrgIdAndPickOptionIds()
   * Case:   #1, normal
   *
   */
  @Test
  public void testListPickOptionsByOrgIdAndPickOptionIdsCase1() {

    // prepare
    List<Long> pickOptionIds = new ArrayList<>();
    pickOptionIds.add(orgId);

    // verify
    List<PickOption> pickOptions = profileFieldService.listPickOptionsByOrgIdAndPickOptionIds(orgId, pickOptionIds);
    Assert.assertEquals(0, pickOptions.size());

  }

  /**
   * Method: listPickOptionsByOrgIdAndPickOptionIds()
   * Case:   #2, abnormal, empty id list
   *
   */
  @Test
  public void testListPickOptionsByOrgIdAndPickOptionIdsCase2() {

    thrown.expect(ServiceStatusException.class);
    profileFieldService.listPickOptionsByOrgIdAndPickOptionIds(orgId, Collections.EMPTY_LIST);

  }

  @Test
  public void testListPickOptionByOrgIdAndProfileFieldIdForUpdate() {
    List<PickOption> pickOptions = profileFieldService.listPickOptionByOrgIdAndProfileFieldIdForUpdate(orgId, orgId);
    Assert.assertEquals(0, pickOptions.size());
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

    CoreUserProfile addedCUP = onboardingFlowService.createOrgAndFirstUser(org, fieldValues, userEmployment);

    long orgId = addedCUP.getOrgId();

    ProfileTemplate profileTemplate = profileTemplateDao.findTheOnlyProfileTemplateByOrgId(orgId);
    long theProfileTemplateId = profileTemplate.getProfileTemplateId();
    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, theProfileTemplateId, userId);

    // verify
    List<ProfileField> profileFieldsOfAll = profileFieldService.listAllProfileFieldOfOrg(orgId);
    LOGGER.info("profileFieldsOfAll={}", profileFieldsOfAll);
    Assert.assertTrue(profileFieldsOfAll.size() > 0);

    // verify
    profileFieldsOfAll = profileFieldService.listAllProfileFieldOfOrgForUpdate(orgId);
    Assert.assertTrue(profileFieldsOfAll.size() > 0);

  }

  /**
   * Case #1: normal
   */
  @Test
  public void testUpdateUserProfileConfigCase1() {

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

    UserProfileConfig updatedUPC = userProfileConfigDao
        .findUserProfileConfigByOrgIdAndReferenceName(orgId, theUPC.getReferenceName());
    Assert.assertEquals(updatedIsEnabled, updatedUPC.getIsEnabled().intValue());

  }

  /**
   * Case 1: normal, valid displayName
   * Case 2: abnormal, invalid displayName with '_'
   * Rule: displayName should not contain ' ' OR '_'
   *
   */
  @Test
  public void testProfileFieldDisplayNameCheckCase1() {

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);

    int presetFieldCount = profileFields.size();
    long lastContainerId = -1;
    for (int i = 0; i < profileFields.size(); i++) {
      if (DataType.CONTAINER.getCode() == profileFields.get(i).getDataType()) {
        lastContainerId = profileFields.get(i).getProfileFieldId();
      }
    }

    // case 1
    String validDisplayName = "STXT01";
    ProfileField toAddContainerField = new ProfileField();
    toAddContainerField.setOrgId(orgId);
    toAddContainerField.setProfileTemplateId(profileTemplateId);
    toAddContainerField.setContainerId(lastContainerId);
    toAddContainerField.setDataType(DataType.SHORT_TEXT.getCode());
    toAddContainerField.setDisplayName(validDisplayName);
    toAddContainerField.setIsPublicVisible(1);
    toAddContainerField.setIsEnabled(1);
    toAddContainerField.setIsMandatory(0);
    toAddContainerField.setCreatedUserId(userId);
    profileFieldService.addCustomDataProfileField(toAddContainerField);
    List<ProfileField> addedProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateId(orgId, profileTemplateId);
    Assert.assertEquals(presetFieldCount + 1, addedProfileFields.size());

  }

  /**
   * Case 2: abnormal, invalid displayName with '_'
   * Rule: displayName should not contain ' ' OR '_'
   *
   */
  @Test
  public void testProfileFieldDisplayNameCheckCase2() {

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);

    int presetFieldCount = profileFields.size();
    long lastContainerId = -1;
    for (int i = 0; i < profileFields.size(); i++) {
      if (DataType.CONTAINER.getCode() == profileFields.get(i).getDataType()) {
        lastContainerId = profileFields.get(i).getProfileFieldId();
      }
    }

    // case 2
    String invalidDisplayName = "STXT_01";
    ProfileField toAddContainerField = new ProfileField();
    toAddContainerField.setOrgId(orgId);
    toAddContainerField.setProfileTemplateId(profileTemplateId);
    toAddContainerField.setContainerId(lastContainerId);
    toAddContainerField.setDataType(DataType.SHORT_TEXT.getCode());
    toAddContainerField.setDisplayName(invalidDisplayName);
    toAddContainerField.setIsPublicVisible(1);
    toAddContainerField.setIsEnabled(1);
    toAddContainerField.setIsMandatory(0);
    toAddContainerField.setCreatedUserId(userId);
    thrown.expect(ServiceStatusException.class);
    profileFieldService.addCustomDataProfileField(toAddContainerField);


  }

  /**
   * Case 1: normal case, no dup displayNames upon add
   * Case 2: dup displayNames upon add
   *
   */
  @Test
  public void testContainerFieldDupNameCheckCase1And2() {

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);

    int presetFieldCount = profileFields.size();
    long lastContainerId = -1;
    for (int i = 0; i < profileFields.size(); i++) {
      if (DataType.CONTAINER.getCode() == profileFields.get(i).getDataType()) {
        lastContainerId = profileFields.get(i).getProfileFieldId();
      }
    }

    // case 1
    String validDisplayName = "CONTAINER01";
    ProfileField toAddContainerField = new ProfileField();
    toAddContainerField.setOrgId(orgId);
    toAddContainerField.setProfileTemplateId(profileTemplateId);
    toAddContainerField.setDataType(DataType.CONTAINER.getCode());
    toAddContainerField.setDisplayName(validDisplayName);
    toAddContainerField.setIsPublicVisible(1);
    toAddContainerField.setIsEnabled(1);
    toAddContainerField.setIsMandatory(0);
    toAddContainerField.setCreatedUserId(userId);
    profileFieldService.addCustomContainerProfileField(toAddContainerField);

    // case 2
    thrown.expect(ServiceStatusException.class);
    profileFieldService.addCustomContainerProfileField(toAddContainerField);

  }


  /**
   * Case 1: normal case, no dup displayNames upon add
   * Case 2: dup displayNames within same containerId upon add
   *
   */
  @Test
  public void testDataFieldDupNameCheckCase1And2() {

    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);

    int presetFieldCount = profileFields.size();
    long lastContainerId = -1;
    for (int i = 0; i < profileFields.size(); i++) {
      if (DataType.CONTAINER.getCode() == profileFields.get(i).getDataType()) {
        lastContainerId = profileFields.get(i).getProfileFieldId();
      }
    }

    String validDisplayName = "CONTAINER01";
    ProfileField toAddContainerField = new ProfileField();
    toAddContainerField.setOrgId(orgId);
    toAddContainerField.setProfileTemplateId(profileTemplateId);
    toAddContainerField.setDataType(DataType.CONTAINER.getCode());
    toAddContainerField.setDisplayName(validDisplayName);
    toAddContainerField.setIsPublicVisible(1);
    toAddContainerField.setIsEnabled(1);
    toAddContainerField.setIsMandatory(0);
    toAddContainerField.setCreatedUserId(userId);
    long insertedContainerId = profileFieldService.addCustomContainerProfileField(toAddContainerField);

    // case 1)
    String validDataDisplayName = "STXT01";
    ProfileField toAddDataField = new ProfileField();
    toAddDataField.setOrgId(orgId);
    toAddDataField.setProfileTemplateId(profileTemplateId);
    toAddDataField.setContainerId(insertedContainerId);
    toAddDataField.setDataType(DataType.SHORT_TEXT.getCode());
    toAddDataField.setDisplayName(validDataDisplayName);
    toAddDataField.setIsPublicVisible(1);
    toAddDataField.setIsEnabled(1);
    toAddDataField.setIsMandatory(0);
    toAddDataField.setCreatedUserId(userId);
    profileFieldService.addCustomDataProfileField(toAddDataField);

    // case 2)
    thrown.expect(ServiceStatusException.class);
    profileFieldService.addCustomDataProfileField(toAddDataField);

  }

  /**
   * Normal success
   *
   */
  @Test
  public void testGetProfileFieldCase01() {

    // prepare
    profileFieldService.addCustomContainerProfileField(containerField);

    // verify
    ProfileField insertedPF =
        profileFieldService.getProfileField(orgId, profileTemplateId, containerFieldReferenceNameOne);
    Assert.assertEquals(containerFieldReferenceNameOne, insertedPF.getReferenceName());

  }

  /**
   * Abnormal: not found
   *
   */
  @Test
  public void testGetProfileFieldCase02() {

    // verify
    thrown.expect(ServiceStatusException.class);
    profileFieldService.getProfileField(userId, profileTemplateId, "RANDOM");

  }

  /**
   * Case 01: normal
   *
   */
  @Test
  public void testListAllProfileFieldOfTemplateCase01() {

    // prepare
    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);

    // verify
    List<ProfileField> allPFs = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    Assert.assertTrue(allPFs.size() > 0);

  }

  /**
   * Case 01: normal
   *
   */
  @Test
  public void testListDataProfileFieldOfTemplateCase01() {

    // prepare
    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);

    // verify
    List<ProfileField> allDataPFs = profileFieldService.listDataProfileFieldOfTemplate(orgId, profileTemplateId);
    Assert.assertTrue(allDataPFs.size() > 0);

  }

  /**
   * Case 01: normal
   *
   */
  @Test
  public void testMoveDataProfileFiledCase01() {

    // prepare
    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    long theContainerId = profileFieldService.addCustomContainerProfileField(containerField);
    dataField.setContainerId(theContainerId);
    dataField.setDataType(DataType.SHORT_TEXT.getCode());
    LOGGER.info("TheDataPF={}", dataField);
    dataField.setDisplayName("FIRST");
    long firstDataPFId = profileFieldService.addCustomDataProfileField(dataField);
    dataField.setDisplayName("SECOND");
    long secondDataPFId = profileFieldService.addCustomDataProfileField(dataField);

    // verify
    List<ProfileField> allPFs = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    ProfileField firstPF = null;
    int firstLogicalIndex = 0;
    int secondLogicalIndex = 0;
    for (ProfileField oneProfileField: allPFs) {
      if (firstDataPFId == oneProfileField.getProfileFieldId()) {
        firstLogicalIndex = oneProfileField.getLogicalIndex();
        firstPF = oneProfileField;
      } else if (secondDataPFId == oneProfileField.getProfileFieldId()) {
        secondLogicalIndex = oneProfileField.getLogicalIndex();
      }
    }
    firstPF.setLogicalIndex(secondLogicalIndex);
    firstPF.setLastModifiedUserId(userId);

    profileFieldService.moveDataProfileFiled(firstPF);
    allPFs = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    firstPF = null;
    ProfileField secondPF = null;
    for (ProfileField oneProfileField: allPFs) {
      if (firstDataPFId == oneProfileField.getProfileFieldId()) {
        firstPF = oneProfileField;
      } else if (secondDataPFId == oneProfileField.getProfileFieldId()) {
        secondPF = oneProfileField;
      }
    }
    Assert.assertNotNull(firstPF);
    Assert.assertNotNull(secondPF);
    Assert.assertEquals(secondLogicalIndex, firstPF.getLogicalIndex().intValue());
    Assert.assertEquals(firstLogicalIndex, secondPF.getLogicalIndex().intValue());

  }

  /**
   * Case 01: normal
   *
   */
  @Test
  public void testMoveContainerProfileFiledCase01() {

    // prepare
    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    containerField.setDisplayName("FIRST");
    long firstContainerId = profileFieldService.addCustomContainerProfileField(containerField);
    containerField.setDisplayName("SECOND");
    long secondContainerId = profileFieldService.addCustomContainerProfileField(containerField);

    // verify
    ProfileField firstPF = null;
    ProfileField secondPF = null;
    int firstLogicalIndex = -1;
    int secondLogicalIndex = -1;
    List<ProfileField> allPFs = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    for (ProfileField oneProfileField: allPFs) {
      if (firstContainerId == oneProfileField.getProfileFieldId()) {
        firstLogicalIndex = oneProfileField.getLogicalIndex();
        firstPF = oneProfileField;
      } else if (secondContainerId == oneProfileField.getProfileFieldId()) {
        secondLogicalIndex = oneProfileField.getLogicalIndex();
      }
    }
    firstPF.setLogicalIndex(secondLogicalIndex);
    firstPF.setLastModifiedUserId(userId);
    profileFieldService.moveContainerProfileField(firstPF);

    allPFs = profileFieldService.listAllProfileFieldOfTemplate(orgId, profileTemplateId);
    firstPF = null;
    secondPF = null;
    for (ProfileField oneProfileField: allPFs) {
      if (firstContainerId == oneProfileField.getProfileFieldId()) {
        firstPF = oneProfileField;
      } else if (secondContainerId == oneProfileField.getProfileFieldId()) {
        secondPF = oneProfileField;
      }
    }
    Assert.assertNotNull(firstPF);
    Assert.assertNotNull(secondPF);
    Assert.assertEquals(secondLogicalIndex, firstPF.getLogicalIndex().intValue());
    Assert.assertEquals(firstLogicalIndex, secondPF.getLogicalIndex().intValue());

  }

  /**
   * Case 01: normal
   *
   */
  @Test
  public void testHandlePickOptionsUponUpdateFieldCase01() {

    // prepare
    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, userId);
    long theContainerId = profileFieldService.addCustomContainerProfileField(containerField);
    List<PickOption> pickOptions = new ArrayList<>();
    int optionCount = 3;
    for (int i = 0; i < optionCount; i++) {
      PickOption pickOption = new PickOption();
      pickOption.setOptionValue("" + i);
      pickOption.setOptionIndex(i);
      pickOption.setIsDefault(0);
      pickOptions.add(pickOption);
    }
    JSONObject typeSpec = new JSONObject();
    typeSpec.put("pickOptions", pickOptions);
    dataField.setContainerId(theContainerId);
    dataField.setDataType(DataType.SINGLE_PICK.getCode());
    dataField.setTypeSpec(typeSpec);
    long pfId = profileFieldService.addCustomDataProfileField(dataField);
    List<PickOption> addedPOs = pickOptionDao.listPickOptionByProfileFieldId(orgId, pfId);
    Assert.assertEquals(optionCount, addedPOs.size());

    // verify
    ProfileField addedPF = profileFieldService.getProfileField(orgId, pfId);
    pickOptions.remove(optionCount - 1);
    typeSpec.put("pickOptions", pickOptions);
    addedPF.setTypeSpec(typeSpec);
    profileFieldService.updateDataProfileField(addedPF);
    List<PickOption> updatedPOs = pickOptionDao.listPickOptionByProfileFieldId(orgId, pfId);
    int deprecatedPOCount = 0;
    for (PickOption pickOption: updatedPOs) {
      if (pickOption.getIsDeprecated() == 1) {
        deprecatedPOCount += 1;
      }
    }
    Assert.assertEquals(optionCount, deprecatedPOCount);
    Assert.assertEquals(optionCount + optionCount - 1, updatedPOs.size());

  }


}