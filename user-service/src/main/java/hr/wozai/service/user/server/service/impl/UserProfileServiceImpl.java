// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.wozai.service.servicecommons.commons.consts.TypeSpecConsts;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.BooleanUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.enums.ConfigType;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.server.dao.userorg.BasicUserProfileDao;
import hr.wozai.service.user.server.dao.userorg.CoreUserProfileDao;
import hr.wozai.service.user.server.dao.userorg.MetaUserProfileDao;
import hr.wozai.service.user.server.dao.userorg.OrgPickOptionDao;
import hr.wozai.service.user.server.dao.userorg.PickOptionDao;
import hr.wozai.service.user.server.dao.userorg.ProfileFieldDao;
import hr.wozai.service.user.server.dao.userorg.ProfileTemplateDao;
import hr.wozai.service.user.server.dao.userorg.UserEmploymentDao;
import hr.wozai.service.user.server.dao.userorg.UserProfileConfigDao;
import hr.wozai.service.user.server.helper.PickOptionHelper;
import hr.wozai.service.user.server.helper.RosterHelper;
import hr.wozai.service.user.server.helper.UserProfileHelper;
import hr.wozai.service.user.server.helper.ValidationCheckResult;
import hr.wozai.service.user.server.model.userorg.BasicUserProfile;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.EncryptedPickOption;
import hr.wozai.service.user.server.model.userorg.MetaUserProfile;
import hr.wozai.service.user.server.model.userorg.OrgPickOption;
import hr.wozai.service.user.server.model.userorg.PickOption;
import hr.wozai.service.user.server.model.userorg.ProfileField;
import hr.wozai.service.user.server.model.userorg.ProfileTemplate;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.model.userorg.UserProfile;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;
import hr.wozai.service.user.server.service.ProfileFieldService;
import hr.wozai.service.user.server.service.UserProfileService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-13
 */
@Service("userProfileService")
public class UserProfileServiceImpl implements UserProfileService {

  private static Logger LOGGER = LoggerFactory.getLogger(UserProfileServiceImpl.class);

  private static int newStaffListSize = 3;
  private static int enrollAnniversaryListSize = 4;

//  @Autowired
//  UserProfileDao userProfileDao;

  @Autowired
  UserEmploymentDao userEmploymentDao;

  @Autowired
  ProfileTemplateDao profileTemplateDao;

  @Autowired
  ProfileFieldDao profileFieldDao;

  @Autowired
  PickOptionDao pickOptionDao;

  @Autowired
  OrgPickOptionDao orgPickOptionDao;

  @Autowired
  UserProfileConfigDao userProfileConfigDao;

  @Autowired
  CoreUserProfileDao coreUserProfileDao;

  @Autowired
  BasicUserProfileDao basicUserProfileDao;

  @Autowired
  MetaUserProfileDao metaUserProfileDao;

  @Autowired
  ProfileFieldService profileFieldService;

  // Note: comment out on 2016-08-09
//  @Override
//  @LogAround
//  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//  public long addUserProfile(UserProfile userProfile) {
//
//    if (!UserProfileHelper.isValidAddUserProfileRequest(userProfile)) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
//    }
//    userProfileDao.insertUserProfile(userProfile);
//
//    List<ProfileField> dataFields = profileFieldDao
//        .listDataProfileFieldByProfileTemplateIdForUpdate(userProfile.getOrgId(), userProfile.getProfileTemplateId());
//    Map<String, Object> userProfileRawMap = userProfileDao
//        .findUserProfileRawMapByOrgIdAndUserId(userProfile.getOrgId(), userProfile.getUserId(), dataFields);
//    OldCoreUserProfile oldCoreUserProfile = new OldCoreUserProfile();
//    UserProfileHelper.copyProperties(userProfileRawMap, oldCoreUserProfile);
//    oldCoreUserProfile.setCreatedUserId(userProfile.getCreatedUserId());
//
//    oldCoreUserProfileDao.insertCoreUserProfile(oldCoreUserProfile);
//
//    return userProfile.getUserProfileId();
//
//  }

//  @Override
//  @LogAround
//  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//  public long addUserProfileForOnboarding(UserProfile userProfile, Map<String, String> fieldValues) {
//
//    if (!UserProfileHelper.isValidAddUserProfileRequest(userProfile)) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
//    }
//
//    long orgId = userProfile.getOrgId();
//    long profileTemplateId = userProfile.getProfileTemplateId();
//    List<ProfileField> dataFields = profileFieldDao
//        .listDataProfileFieldByProfileTemplateIdForUpdate(orgId, profileTemplateId);
//
//    if (!UserProfileHelper.isValidFieldValueForAddUserProfileRequest(fieldValues, dataFields)) {
//      throw new ServiceStatusException(ServiceStatus.UP_INVALID_FIELD_VALUE);
//    }
//
//    // 1)
//    for (ProfileField dataField: dataFields) {
//      dataField.setDataValue(fieldValues.get(dataField.getReferenceName()));
//    }
//    userProfile.setProfileFields(dataFields);
//    userProfileDao.insertUserProfile(userProfile);
//
//    // 2)
//    Map<String, Object> userProfileRawMap = userProfileDao
//        .findUserProfileRawMapByOrgIdAndUserId(orgId, userProfile.getUserId(), dataFields);
//    OldCoreUserProfile oldCoreUserProfile = new OldCoreUserProfile();
//    UserProfileHelper.copyProperties(userProfileRawMap, oldCoreUserProfile);
//    oldCoreUserProfile.setCreatedUserId(userProfile.getCreatedUserId());
//
//    oldCoreUserProfileDao.insertCoreUserProfile(oldCoreUserProfile);
//
//    return userProfile.getUserId();
//  }

  /**
   * Steps:
   *  1) fill in fields in CoreUserProfile
   *  2) encrypt jobTitle and jobLevel
   *  3) fill in fields in BasicUserProfile
   *  4) fill in fields in MetaUserProfile
   *  5) filter out fields where isEnabled==0
   *
   * @param orgId
   * @param userId
   * @return
   */
  @Override
  @LogAround
  public UserProfile getUserProfile(long orgId, long userId) {

    ProfileTemplate profileTemplate = profileTemplateDao.findTheOnlyProfileTemplateByOrgId(orgId);
    List<UserProfileConfig> userProfileConfigs = userProfileConfigDao.listUserProfileConfigByOrgId(orgId);
    List<OrgPickOption> jobTitles = orgPickOptionDao
        .listOrgPickOptionByConfigType(orgId, ConfigType.JOB_TITLE.getCode());
    List<OrgPickOption> jobLevels = orgPickOptionDao
        .listOrgPickOptionByConfigType(orgId, ConfigType.JOB_LEVEL.getCode());
    List<ProfileField> profileFields = new ArrayList<>();
    if (null == profileTemplate
        || CollectionUtils.isEmpty(userProfileConfigs)
        || CollectionUtils.isEmpty(jobTitles)
        || CollectionUtils.isEmpty(jobLevels)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    UserProfile userProfile = new UserProfile();
    for (UserProfileConfig userProfileConfig: userProfileConfigs) {
      ProfileField profileField = new ProfileField();
      BeanUtils.copyProperties(userProfileConfig, profileField);
      profileFields.add(profileField);
    }

    // 1)
    CoreUserProfile coreUserProfile = coreUserProfileDao.findCoreUserProfileByOrgIdAndUserId(orgId, userId);
    if (null == coreUserProfile) {
      throw new ServiceStatusException(ServiceStatus.UP_USER_NOT_FOUND);
    }
    BeanUtils.copyProperties(coreUserProfile, userProfile);
    extractProfileFieldFromCoreUserProfile(coreUserProfile, profileFields);

    // 2)
    try {
      if (null != coreUserProfile.getJobTitle()) {
        long jobTitleId = coreUserProfile.getJobTitle();
        for (int j = 0; j < profileFields.size(); j++) {
          if (SystemProfileField.JOB_TITLE.getReferenceName()
              .equals(profileFields.get(j).getReferenceName())) {
            profileFields.get(j).setDataValue(EncryptUtils.symmetricEncrypt(String.valueOf(jobTitleId)).toUpperCase());
            break;
          }
        }
      }
      if (null != coreUserProfile.getJobLevel()) {
        long jobLevelId = coreUserProfile.getJobLevel();
        for (int j = 0; j < profileFields.size(); j++) {
          if (SystemProfileField.JOB_LEVEL.getReferenceName()
              .equals(profileFields.get(j).getReferenceName())) {
            profileFields.get(j).setDataValue(EncryptUtils.symmetricEncrypt(String.valueOf(jobLevelId)).toUpperCase());
            break;
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("getUserProfile(): fail to encrypt");
    }

    // 3)
    BasicUserProfile basicUserProfile = basicUserProfileDao.findBasicUserProfileByOrgIdAndUserId(orgId, userId);
    if (null == basicUserProfile) {
      throw new ServiceStatusException(ServiceStatus.UP_USER_NOT_FOUND);
    }
    extractProfileFieldFromBasicUserProfile(basicUserProfile, profileFields);

    // 4)
    List<ProfileField> profileFieldsOfMetaUserProfile =
        profileFieldDao.listProfileFieldByProfileTemplateId(orgId, profileTemplate.getProfileTemplateId());
    if (!CollectionUtils.isEmpty(profileFieldsOfMetaUserProfile)) {
      MetaUserProfile metaUserProfile = metaUserProfileDao
          .findMetaUserProfileWithFieldDataByOrgIdAndUserId(orgId, userId, profileFieldsOfMetaUserProfile);
      for (ProfileField profileField: metaUserProfile.getProfileFields()) {
        setTypeSpecAndEncrypteDataValueForProfileField(profileField);
      }
      profileFields.addAll(metaUserProfile.getProfileFields());
    }

    // 5)
    List<ProfileField> enabledProfileFields = new ArrayList<>();
    for (ProfileField profileField : profileFields) {
      if (1 == profileField.getIsEnabled()) {
        enabledProfileFields.add(profileField);
      }
    }
    userProfile.setProfileFields(enabledProfileFields);

    return userProfile;
  }

  @Override
  @LogAround
  public List<CoreUserProfile> listCoreUserProfileFromOnboardingByOrgIdAndHasApproved(
      long orgId, int hasApproved, int pageNumber, int pageSize) {

    if (!BooleanUtils.isValidBooleanValue(hasApproved)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    return coreUserProfileDao
        .listCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, hasApproved, pageNumber, pageSize);
  }

  @Override
  @LogAround
  public int countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(long orgId, int hasApproved) {

    if (!BooleanUtils.isValidBooleanValue(hasApproved)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    return coreUserProfileDao.countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, hasApproved);
  }

  @Override
  @LogAround
  public List<CoreUserProfile> listCoreUserProfileFromImportByUserStatus(
      long orgId, int userStatus, int pageNumber, int pageSize) {

    if (-1 != userStatus
        && null == UserStatus.getEnumByCode(userStatus)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    List<CoreUserProfile> coreUserProfiles = Collections.EMPTY_LIST;
    if (-1 == userStatus) {
      coreUserProfiles = coreUserProfileDao.listAllCoreUserProfileFromImport(orgId, pageNumber, pageSize);
    } else {
      coreUserProfiles = coreUserProfileDao
          .listCoreUserProfileFromImportByUserStatus(orgId, userStatus, pageNumber, pageSize);
    }

    return coreUserProfiles;
  }

  @Override
  @LogAround
  public int countCoreUserProfileFromImportByUserStatus(long orgId, int userStatus) {

    if (-1 != userStatus
        && null == UserStatus.getEnumByCode(userStatus)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    int totalNumber = 0;
    if (-1 == userStatus) {
      totalNumber = coreUserProfileDao.countAllCoreUserProfileFromImport(orgId);
    } else {
      totalNumber = coreUserProfileDao.countCoreUserProfileFromImportByUserStatus(orgId, userStatus);
    }

    return totalNumber;
  }

  @Override
  @LogAround
  public List<CoreUserProfile> listFullNameAndEmailAddressWhichIsNotResigned(long orgId) {
    return coreUserProfileDao.listFullNameAndEmailAddressWhichIsNotResignedForUpdate(orgId);
  }

  /**
   * Steps:
   *  1) parse referenceNames
   *  2) validate format
   *  3) validate data
   *  4) batch update UserEmployment
   *  5) batch update CUP & BUP & MUP
   *
   * @param orgId
   * @param headers
   * @param rawFieldValueList
   * @param actorUserId
   * @return
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public int batchUpdateRosterData(
      long orgId, List<String> headers, List<List<String>> rawFieldValueList, long actorUserId) {

    // 1)
    List<ProfileField> profileFields = profileFieldService.listAllProfileFieldOfOrgForUpdate(orgId);
    List<String> referenceNames = RosterHelper.convertRosterHeaderToReferenceNames(headers, profileFields);
    LOGGER.info("batchUpdateRosterData(): referenceNames={}", referenceNames);

    // 2)
    ValidationCheckResult formatValidateResult =
        RosterHelper.validateRosterFileFormat(referenceNames, rawFieldValueList);
    if (!formatValidateResult.isNoError()) {
      LOGGER.error("batchUpdateRosterData(): format error, {}", formatValidateResult.getErrorMessage());
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_CSV_FORMAT, formatValidateResult.errorMessage);
    }

    // 3)
    List<CoreUserProfile> coreUserProfiles = coreUserProfileDao
        .listFullNameAndEmailAddressWhichIsNotResignedForUpdate(orgId);
    List<OrgPickOption> jobTitles = orgPickOptionDao
        .listOrgPickOptionByConfigTypeForUpdate(orgId, ConfigType.JOB_TITLE.getCode());
    List<OrgPickOption> jobLevels = orgPickOptionDao
        .listOrgPickOptionByConfigTypeForUpdate(orgId, ConfigType.JOB_LEVEL.getCode());
    List<ProfileField> customizedProfileFields = new ArrayList<>();
    for (ProfileField profileField: profileFields) {
      if (profileField.getIsSystemRequired() == 0) {
        customizedProfileFields.add(profileField);
      }
    }
    Map<Long, List<PickOption>> pickOptionMap = new HashMap<>();
    for (ProfileField profileField: customizedProfileFields) {
      if (DataType.SINGLE_PICK.getCode() == profileField.getDataType()
          || DataType.MULTI_PICK.getCode() == profileField.getDataType()) {
        List<PickOption> pickOptions = profileFieldService
            .listPickOptionByOrgIdAndProfileFieldIdForUpdate(orgId, profileField.getProfileFieldId());
        if (!CollectionUtils.isEmpty(pickOptions)) {
          pickOptionMap.put(profileField.getProfileFieldId(), pickOptions);
        }
      }
    }
    ValidationCheckResult dataValidateResult = RosterHelper.validateRosterFileData(
        headers, referenceNames, rawFieldValueList, profileFields, coreUserProfiles,
        jobTitles, jobLevels, pickOptionMap);
    if (!dataValidateResult.isNoError()) {
      LOGGER.error("batchUpdateRosterData(): data error, {}", dataValidateResult.getErrorMessage());
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_CSV_DATA, dataValidateResult.errorMessage);
    }

    // 4)
    Map<Long, UserEmployment> userEmploymentMap =
        RosterHelper.getUserEmploymentMapFromRosterFile(referenceNames, rawFieldValueList, coreUserProfiles);
    LOGGER.info("batchUpdateRosterData(): userEmploymentMap={}", userEmploymentMap);
    if (!MapUtils.isEmpty(userEmploymentMap)) {
      for (Long userId: userEmploymentMap.keySet()) {
        UserEmployment userEmployment = userEmploymentMap.get(userId);
        userEmployment.setOrgId(orgId);
        userEmployment.setLastModifiedUserId(actorUserId);
        userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(userEmployment);
      }
    }

    // 5)
    Map<Long, Map<String, String>> fieldValueMap = RosterHelper.getProfileFieldValueMapFromRosterFile(
        referenceNames, rawFieldValueList, profileFields, coreUserProfiles);
    LOGGER.info("batchUpdateRosterData(): fieldValueMap={}", fieldValueMap);
    if (!MapUtils.isEmpty(fieldValueMap)) {
      for (Long userId: fieldValueMap.keySet()) {
        updateUserProfileField(orgId, userId, fieldValueMap.get(userId), actorUserId);
      }
    }

    // change
    return rawFieldValueList.size();
  }

  /**
   * Steps:
   *  (Deprecated) 1) update userProfile
   *  (Deprecated) 2) update coreUserProfile
   *  3) update userEmployment
   *
   * @param orgId
   * @param userId
   * @param userStatus
   * @param actorUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateUserStatus(long orgId, long userId, int userStatus, long actorUserId) {

//    // 1)
//    UserProfile userProfile = new UserProfile();
//    userProfile.setOrgId(orgId);
//    userProfile.setUserId(userId);
//    userProfile.setUserStatus(userStatus);
//    userProfile.setLastModifiedUserId(actorUserId);
//    userProfileDao.updateUserStatusByOrgIdAndUserId(userProfile);
//
//    // 2)
//    OldCoreUserProfile oldCoreUserProfile = oldCoreUserProfileDao.findCoreUserProfileByOrgIdAndUserId(orgId, userId);
//    oldCoreUserProfile.setUserStatus(userStatus);
//    oldCoreUserProfileDao.updateCoreUserProfileByOrgIdAndUserId(oldCoreUserProfile);

    // 3)
    UserEmployment userEmployment = userEmploymentDao.findUserEmploymentByOrgIdAndUserId(orgId, userId);
    userEmployment.setUserStatus(userStatus);
    userEmployment.setLastModifiedUserId(actorUserId);
    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(userEmployment);

  }

//  /**
//   * Steps:
//   *  1) update userProfile
//   *  2) update coreUserProfile
//   *  3) update userEmployment
//   *
//   * @param orgId
//   * @param userId
//   * @param userStatus
//   * @param actorUserId
//   */
//  @Override
//  @LogAround
//  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//  public void updateUserStatus(long orgId, long userId, int userStatus, long actorUserId) {
//
//    // 1)
//    UserProfile userProfile = new UserProfile();
//    userProfile.setOrgId(orgId);
//    userProfile.setUserId(userId);
//    userProfile.setUserStatus(userStatus);
//    userProfile.setLastModifiedUserId(actorUserId);
//    userProfileDao.updateUserStatusByOrgIdAndUserId(userProfile);
//
//    // 2)
//    OldCoreUserProfile oldCoreUserProfile = oldCoreUserProfileDao.findCoreUserProfileByOrgIdAndUserId(orgId, userId);
//    oldCoreUserProfile.setUserStatus(userStatus);
//    oldCoreUserProfileDao.updateCoreUserProfileByOrgIdAndUserId(oldCoreUserProfile);
//
//    // 3)
//    UserEmployment userEmployment = userEmploymentDao.findUserEmploymentByOrgIdAndUserId(orgId, userId);
//    userEmployment.setUserStatus(userStatus);
//    userEmployment.setLastModifiedUserId(actorUserId);
//    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(userEmployment);
//
//  }

  /**
   * Steps:
   *  1) update CUP
   *  2) update BUP
   *  3) update MUP
   *
   * @param orgId
   * @param userId
   * @param fieldValues
   * @param actorUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateUserProfileField(long orgId, long userId, Map<String, String> fieldValues, long actorUserId) {

    ProfileTemplate profileTemplate = profileTemplateDao.findTheOnlyProfileTemplateByOrgId(orgId);
    List<UserProfileConfig> userProfileConfigs = userProfileConfigDao.listUserProfileConfigByOrgId(orgId);
    CoreUserProfile coreUserProfile = coreUserProfileDao.findCoreUserProfileByOrgIdAndUserId(orgId, userId);
    BasicUserProfile basicUserProfile = basicUserProfileDao.findBasicUserProfileByOrgIdAndUserId(orgId, userId);
    UserEmployment userEmployment = userEmploymentDao.findUserEmploymentByOrgIdAndUserId(orgId, userId);
    if (null == profileTemplate
        || CollectionUtils.isEmpty(userProfileConfigs)
        || null == coreUserProfile
        || null == basicUserProfile
        || null == userEmployment) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    // 1) 2)
    Map<String, UserProfileConfig> userProfileConfigMap = new HashMap<>();
    for (UserProfileConfig userProfileConfig: userProfileConfigs) {
      userProfileConfigMap.put(userProfileConfig.getReferenceName(), userProfileConfig);
    }
    int userStatus = userEmployment.getUserStatus();
    for (String referenceName: userProfileConfigMap.keySet()) {
      if (null != fieldValues.get(referenceName)) {
        // permission control
        UserProfileConfig userProfileConfig = userProfileConfigMap.get(referenceName);
        if (userId == actorUserId) {
          if (userStatus == UserStatus.ACTIVE.getCode()
              && userProfileConfig.getIsActiveStaffEditable() == 0) {
            throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
          } else if (userStatus == UserStatus.INVITED.getCode()
                     && userProfileConfig.getIsOnboardingStaffEditable() == 0) {
            throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
          }
        }
        String value = fieldValues.get(referenceName);
        // validate data value
        UserProfileHelper.validateDataValue(referenceName, userProfileConfig, value);
        if (userProfileConfig.getIsMandatory() == 1
            && StringUtils.isNullOrEmpty(value)) {
          throw new ServiceStatusException(ServiceStatus.UP_FIELD_IS_MANDATORY);
        }
        // set value
        int fieldCode = userProfileConfig.getFieldCode();
        if (fieldCode < 100) {
          setDataValueOfCoreUserProfile(coreUserProfile, referenceName, value);
        } else {
          setDataValueOfBasicUserProfile(basicUserProfile, referenceName, value);
        }
      }
    }
    coreUserProfile.setLastModifiedUserId(actorUserId);
    coreUserProfileDao.updateCoreUserProfileByOrgIdAndUserId(coreUserProfile);
    basicUserProfile.setLastModifiedUserId(actorUserId);
    basicUserProfileDao.updateBasicUserProfileByOrgIdAndUserId(basicUserProfile);

    // 3)
    List<ProfileField> dataFields = profileFieldDao
        .listDataProfileFieldByProfileTemplateId(orgId, profileTemplate.getProfileTemplateId());
    Map<String, String> metaFieldValues = new HashMap<>();
    for (Map.Entry<String, String> entry: fieldValues.entrySet()) {
      if (null == SystemProfileField.getEnumByReferenceName(entry.getKey())) {
        metaFieldValues.put(entry.getKey(), entry.getValue());
      }
    }
    if (!CollectionUtils.isEmpty(dataFields)
        && !MapUtils.isEmpty(metaFieldValues)) {
      decryptedPickOptionIdsInFieldValues(dataFields, metaFieldValues);
      if (!UserProfileHelper.isValidUpdateUserProfileFieldRequest(metaFieldValues, dataFields)) {
        throw new ServiceStatusException(ServiceStatus.UP_INVALID_FIELD_VALUE_UPDATE);
      }
      for (ProfileField profileField : dataFields) {
        profileField.setDataValue(metaFieldValues.get(profileField.getReferenceName()));
      }
      MetaUserProfile metaUserProfile = new MetaUserProfile();
      metaUserProfile.setOrgId(orgId);
      metaUserProfile.setUserId(userId);
      metaUserProfile.setLastModifiedUserId(actorUserId);
      metaUserProfile.setProfileFields(dataFields);
      LOGGER.info("CCCCCCCC: MUP={}", metaUserProfile);
      metaUserProfileDao.updateMetaUserProfileByOrgIdAndUserIdSelective(metaUserProfile);
    }

  }

//  /**
//   * Steps:
//   *  1) update userProfile
//   *  2) update coreUserProfile
//   *
//   * @param orgId
//   * @param userId
//   * @param fieldValues
//   * @param actorUserId
//   */
//  @Override
//  @LogAround
//  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//  public void updateUserProfileField(long orgId, long userId, Map<String, String> fieldValues, long actorUserId) {
//
//    Long profileTemplateId = userProfileDao.findProfileTemplateIdByOrgIdAndUserId(orgId, userId);
//    if (null == profileTemplateId) {
//      throw new ServiceStatusException(ServiceStatus.UP_USER_NOT_FOUND);
//    }
//    List<ProfileField> dataFields = profileFieldDao.listDataProfileFieldByProfileTemplateId(orgId, profileTemplateId);
//    if (CollectionUtils.isEmpty(dataFields)) {
//      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_FIELDS_EMPTY);
//    }
//    decryptedPickOptionIdsInFieldValues(dataFields, fieldValues);
//    if (!UserProfileHelper.isValidUpdateUserProfileFieldRequest(fieldValues, dataFields)) {
//      throw new ServiceStatusException(ServiceStatus.UP_INVALID_FIELD_VALUE_UPDATE);
//    }
//
//    for (ProfileField profileField: dataFields) {
//      profileField.setDataValue(fieldValues.get(profileField.getReferenceName()));
//    }
//    UserProfile userProfile = new UserProfile();
//    userProfile.setOrgId(orgId);
//    userProfile.setUserId(userId);
//    userProfile.setProfileFields(dataFields);
//    userProfile.setLastModifiedUserId(actorUserId);
//    userProfileDao.updateUserProfileByOrgIdAndUserIdSelective(userProfile);
//
//    OldCoreUserProfile oldCoreUserProfile = userProfileDao
//        .findCoreUserProfileFromUserProfileByOrgIdAndUserId(orgId, userId, dataFields);
//    oldCoreUserProfileDao.updateCoreUserProfileByOrgIdAndUserId(oldCoreUserProfile);
//
//  }

//  @Override
//  @LogAround
//  public OldCoreUserProfile getOldCoreUserProfileByOrgIdAndUserId(long orgId, long userId) {
//    OldCoreUserProfile result =  oldCoreUserProfileDao.findCoreUserProfileByOrgIdAndUserId(orgId, userId);
//    if (result == null) {
//      throw new ServiceStatusException(ServiceStatus.UP_USER_NOT_FOUND);
//    }
//    return result;
//  }
//
//  @Override
//  @LogAround
//  public List<OldCoreUserProfile> listOldCoreUserProfileByOrgIdAndUserId(long orgId, List<Long> userIds) {
//    List<OldCoreUserProfile> result = new ArrayList<>();
//
//    if (CollectionUtils.isEmpty(userIds)) {
//      return result;
//    }
//    List<OldCoreUserProfile> inDb = oldCoreUserProfileDao.listCoreUserProfileByOrgIdAndUserId(orgId, userIds);
//    Map<Long, OldCoreUserProfile> map = getMapFromCoreUserProfiles(inDb);
//
//    for (Long id : userIds) {
//      if (map.containsKey(id)) {
//        result.add(map.get(id));
//      }
//    }
//    return result;
//  }
//
//  private Map<Long, OldCoreUserProfile> getMapFromCoreUserProfiles(List<OldCoreUserProfile> oldCoreUserProfiles) {
//    Map<Long, OldCoreUserProfile> result = new HashMap<>();
//    for (OldCoreUserProfile oldCoreUserProfile : oldCoreUserProfiles) {
//      result.put(oldCoreUserProfile.getUserId(), oldCoreUserProfile);
//    }
//    return result;
//  }
//
//  @Override
//  @LogAround
//  public List<OldCoreUserProfile> listCoreUserProfileByCreatedUserId(long orgId, long createdUserId) {
//    return oldCoreUserProfileDao.listCoreUserProfileByCreatedUserId(orgId, createdUserId);
//  }
//
//  @Override
//  @LogAround
//  public List<OldCoreUserProfile> listCoreUserProfileByOrgId(long orgId, int pageNumber, int pageSize) {
//    return oldCoreUserProfileDao.listCoreUserProfileByOrgIdOrderByCreatedTimeDesc(orgId, pageNumber, pageSize);
//  }
//
//  @Override
//  @LogAround
//  public int countCoreUserProfileByOrgId(long orgId) {
//    return oldCoreUserProfileDao.countCoreUserProfileByOrgId(orgId);
//  }
//
//  @Override
//  @LogAround
//  public long getProfileTemplateId(long orgId, long userId) {
//    Long profileTemplateId = userProfileDao.findProfileTemplateIdByOrgIdAndUserId(orgId, userId);
//    if (null == profileTemplateId) {
//      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_TEMPLATE_NOT_FOUND);
//    }
//    return profileTemplateId;
//  }
//
  /**
   * Steps:
   *  1) del coreUserProfile
   *  2) del userProfile
   *
   * @param orgId
   * @param userId
   * @param actorUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteUserProfile(long orgId, long userId, long actorUserId) {
//    // 1)
//    coreUserProfileDao.deleteCoreUserProfileByOrgIdAndUserId(orgId, userId, actorUserId);
//    // 2)
//    userProfileDao.deleteUserProfile(orgId, userId, actorUserId);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void wipeUserProfileDataOfField(long orgId, long profileFieldId, long actorUserId) {

    ProfileField profileField = profileFieldDao.findProfileFieldByOrgIdAndPrimaryKeyForUpdate(orgId, profileFieldId);
    if (null == profileField) {
      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_FIELD_NOT_FOUND);
    } else if (DataType.CONTAINER.getCode() == profileField.getDataType()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    metaUserProfileDao.wipeFieldValueByOrgIdAndProfileTemplateIdAndPhysicalIndex(
        orgId, profileField.getProfileTemplateId(), profileField.getPhysicalIndex(), actorUserId);

  }

  /************************ methods after refraction ************************/

  @Override
  public void initUserProfileConfigUponCreateOrg(List<UserProfileConfig> presetUserProfileConfigs) {

    if (!UserProfileHelper.isValidPresetUserProfileConfigs(presetUserProfileConfigs)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    userProfileConfigDao.batchInsertUserProfileConfig(presetUserProfileConfigs);

  }

  /**
   * Steps:
   *  0) validate fields for CoreUserProfile & BasicUserProfile
   *  1) add CoreUserProfile
   *  2) add BasicUserProfile
   *  3) add MetaUserProfile
   *
   * @param orgId
   * @param userId
   * @param onboardingTemplateId
   * @param profileTemplateId
   * @param fieldValues
   */
  @Override
  @LogAround
  public void addCoreAndBasicAndMetaUserProfileForOnboarding(
      long orgId, long userId, Long onboardingTemplateId, long profileTemplateId,
      Map<String, String> fieldValues, long actorUserId) {

    // TODO: validate MetaUserProfile
    // 0)
    List<UserProfileConfig> userProfileConfigs = userProfileConfigDao.listUserProfileConfigByOrgId(orgId);
    if (!UserProfileHelper.isValidAddCoreUserProfileFieldValue(userProfileConfigs, fieldValues)
        || !UserProfileHelper.isValidAddBasicUserProfileFieldValue(userProfileConfigs, fieldValues)) {
      LOGGER.error("addCoreAndBasicAndMetaUserProfileForOnboarding(): bad add request");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    Map<String, String> fieldValueInCoreUserProfile = new HashMap<>();
    Map<String, String> fieldValueInBasicUserProfile = new HashMap<>();
    for (String referenceName: fieldValues.keySet()) {
      if (null != SystemProfileField.getEnumByReferenceName(referenceName)) {
        SystemProfileField systemProfileField = SystemProfileField.getEnumByReferenceName(referenceName);
        if (systemProfileField.getFieldCode() < 100) {
          fieldValueInCoreUserProfile.put(referenceName, fieldValues.get(referenceName));
        } else {
          fieldValueInBasicUserProfile.put(referenceName, fieldValues.get(referenceName));
        }
      }
    }

    // 1)
    CoreUserProfile coreUserProfile = new CoreUserProfile();
    coreUserProfile.setOrgId(orgId);
    coreUserProfile.setUserId(userId);
    coreUserProfile.setProfileTemplateId(profileTemplateId);
    coreUserProfile.setOnboardingTemplateId(
        (null == onboardingTemplateId || 0 == onboardingTemplateId) ? null : onboardingTemplateId);
    coreUserProfile.setCreatedUserId(actorUserId);
    UserProfileHelper.setFieldInCoreUserProfileFromMap(coreUserProfile, fieldValueInCoreUserProfile);
    coreUserProfileDao.insertCoreUserProfile(coreUserProfile);

    // 2)
    BasicUserProfile basicUserProfile = new BasicUserProfile();
    basicUserProfile.setOrgId(orgId);
    basicUserProfile.setUserId(userId);
    basicUserProfile.setCreatedUserId(actorUserId);
    UserProfileHelper.setFieldInBasicUserProfileFromMap(basicUserProfile, fieldValueInBasicUserProfile);
    basicUserProfileDao.insertBasicUserProfile(basicUserProfile);

    // 3)
    MetaUserProfile metaUserProfile = new MetaUserProfile();
    metaUserProfile.setOrgId(orgId);
    metaUserProfile.setUserId(userId);
    metaUserProfile.setProfileTemplateId(profileTemplateId);
    metaUserProfile.setCreatedUserId(actorUserId);
    metaUserProfileDao.insertMetaUserProfile(metaUserProfile);

//    if (!UserProfileHelper.isValidAddUserProfileRequest(userProfile)) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
//    }
//
//    long orgId = userProfile.getOrgId();
//    long profileTemplateId = userProfile.getProfileTemplateId();
//    List<ProfileField> dataFields = profileFieldDao
//        .listDataProfileFieldByProfileTemplateIdForUpdate(orgId, profileTemplateId);
//
//    if (!UserProfileHelper.isValidFieldValueForAddUserProfileRequest(fieldValues, dataFields)) {
//      throw new ServiceStatusException(ServiceStatus.UP_INVALID_FIELD_VALUE);
//    }
//
//    // 1)
//    for (ProfileField dataField: dataFields) {
//      dataField.setDataValue(fieldValues.get(dataField.getReferenceName()));
//    }
//    userProfile.setProfileFields(dataFields);
//    userProfileDao.insertUserProfile(userProfile);
//
//    // 2)
//    Map<String, Object> userProfileRawMap = userProfileDao
//        .findUserProfileRawMapByOrgIdAndUserId(orgId, userProfile.getUserId(), dataFields);
//    OldCoreUserProfile oldCoreUserProfile = new OldCoreUserProfile();
//    UserProfileHelper.copyProperties(userProfileRawMap, oldCoreUserProfile);
//    oldCoreUserProfile.setCreatedUserId(userProfile.getCreatedUserId());
//
//    oldCoreUserProfileDao.insertCoreUserProfile(oldCoreUserProfile);
//
//    return userProfile.getUserId();

  }

  @Override
  public CoreUserProfile getCoreUserProfileByOrgIdAndUserId(long orgId, long userId) {
    CoreUserProfile coreUserProfile = coreUserProfileDao.findCoreUserProfileByOrgIdAndUserId(orgId, userId);
    if (null == coreUserProfile) {
      throw new ServiceStatusException(ServiceStatus.UP_USER_NOT_FOUND);
    }
    return coreUserProfile;
  }

  @Override
  public List<CoreUserProfile> listCoreUserProfileByOrgIdAndUserId(long orgId, List<Long> userIds) {

    List<CoreUserProfile> coreUserProfilesInDB =
        coreUserProfileDao.listCoreUserProfileByOrgIdAndUserId(orgId, userIds);
    List<CoreUserProfile> coreUserProfilesInOrder = new ArrayList<>();
    if (!CollectionUtils.isEmpty(coreUserProfilesInDB)) {
      Map<Long, CoreUserProfile> coreUserProfileMap = new HashMap<>();
      for (CoreUserProfile coreUserProfile: coreUserProfilesInDB) {
        coreUserProfileMap.put(coreUserProfile.getUserId(), coreUserProfile);
      }
      for (Long userId: userIds) {
        if (coreUserProfileMap.containsKey(userId)) {
          coreUserProfilesInOrder.add(coreUserProfileMap.get(userId));
        }
      }
    }
    return coreUserProfilesInOrder;

  }


  private void setTypeSpecAndEncrypteDataValueForProfileField(ProfileField profileField) {

    DataType dataType = DataType.getEnumByCode(profileField.getDataType());

    // handle SINGLE_PICK and MULTI_PICK
    if (DataType.SINGLE_PICK == dataType
        || DataType.MULTI_PICK == dataType) {
      setPickOptionsForProfileField(profileField);
      encrypteDataValueOfPickOptionIdForProfileField(profileField);
    }

  }

  private void setPickOptionsForProfileField(ProfileField profileField) {
    List<PickOption> pickOptions =
        pickOptionDao.listPickOptionByProfileFieldId(profileField.getOrgId(), profileField.getProfileFieldId());
    // TODO: tmp trick to encrypte Ids; should use systematic way in refractor
    List<EncryptedPickOption> encryptedPickOptions = new ArrayList<>();
    if (!CollectionUtils.isEmpty(pickOptions)) {
      for (int i = 0; i < pickOptions.size(); i++) {
        EncryptedPickOption encryptedPickOption = new EncryptedPickOption();
        PickOptionHelper.copyPropertiesFromPickOptionToEncryptedPickOption(pickOptions.get(i), encryptedPickOption);
        encryptedPickOptions.add(encryptedPickOption);
      }
    }
    JSONObject typeSpec = new JSONObject();
    typeSpec.put(TypeSpecConsts.PICK_OPTIONS_KEY, encryptedPickOptions);
    profileField.setTypeSpec(typeSpec);
  }

  private void encrypteDataValueOfPickOptionIdForProfileField(ProfileField profileField) {

    if (!StringUtils.isNullOrEmpty(profileField.getDataValue())) {
      try {
        if (DataType.SINGLE_PICK.getCode() == profileField.getDataType()) {
          String encryptedPickOptionId = EncryptUtils.symmetricEncrypt(profileField.getDataValue()).toUpperCase();
          profileField.setDataValue(encryptedPickOptionId);
        } else if (DataType.MULTI_PICK.getCode() == profileField.getDataType()) {
          String [] rawPickOptionIds = profileField.getDataValue().split(",");
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < rawPickOptionIds.length; i++) {
            String encryptedPickOptionId = EncryptUtils.symmetricEncrypt(rawPickOptionIds[i]).toUpperCase();
            sb.append(encryptedPickOptionId);
            if (i != rawPickOptionIds.length - 1) {
              sb.append(",");
            }
          }
          profileField.setDataValue(sb.toString());
        }
      } catch (Exception e) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
      }
    }
  }

  private void decryptedPickOptionIdsInFieldValues(List<ProfileField> profileFields, Map<String, String> fieldValues) {
    for (ProfileField profileField : profileFields) {
      String referenceName = profileField.getReferenceName();
      String dataValue = fieldValues.get(profileField.getReferenceName());
      if (!StringUtils.isNullOrEmpty(dataValue)) {
        try {
          if (profileField.getDataType() == DataType.SINGLE_PICK.getCode()) {
            LOGGER.info("decryptedPickOptionIdsInFieldValues(): id={}", dataValue);
            String decryptedPickOptionId = EncryptUtils.symmetricDecrypt(dataValue);
            fieldValues.put(referenceName, decryptedPickOptionId);
          } else if (profileField.getDataType() == DataType.MULTI_PICK.getCode()) {
            LOGGER.info("decryptedPickOptionIdsInFieldValues(): ids={}", dataValue);
            String[] rawPickOptionIds = dataValue.split(",");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < rawPickOptionIds.length; i++) {
              String decryptedPickOptionId = EncryptUtils.symmetricDecrypt(rawPickOptionIds[i]);
              sb.append(decryptedPickOptionId);
              if (i != rawPickOptionIds.length - 1) {
                sb.append(",");
              }
            }
            fieldValues.put(referenceName, sb.toString());
          }
        } catch (Exception e) {
          throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
      }
    }
  }

  private void extractProfileFieldFromCoreUserProfile(
      CoreUserProfile coreUserProfile, List<ProfileField> profileFields) {

    if (null == coreUserProfile
        || CollectionUtils.isEmpty(profileFields)) {
      return;
    }

    Map<String, ProfileField> profileFieldMap = new HashMap<>();
    for (ProfileField profileField: profileFields) {
      profileFieldMap.put(profileField.getReferenceName(), profileField);
    }
    Map<String, PropertyDescriptor> sourcePdMap = getPropertyDescriptorMap(coreUserProfile);
    for (Map.Entry<String, PropertyDescriptor> entry : sourcePdMap.entrySet()) {
      String sourcePropertyName = entry.getKey();
      if (null != profileFieldMap.get(sourcePropertyName)) {
        PropertyDescriptor sourcePd = entry.getValue();
        Method readMethod = sourcePd.getReadMethod();
        try {
          if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
            readMethod.setAccessible(true);
          }
          Object value = readMethod.invoke(coreUserProfile);
          String stringValue = (null == value) ? null : value.toString();
          profileFieldMap.get(sourcePropertyName).setDataValue(stringValue);
        } catch (Exception e) {
          LOGGER.error("extractProfileFieldFromBasicUserProfile()-error: "
                       + "Could not copy entry {}", entry, e);
          throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
      }
    }
  }

  private void extractProfileFieldFromBasicUserProfile(
      BasicUserProfile basicUserProfile, List<ProfileField> profileFields) {

    if (null == basicUserProfile
        || CollectionUtils.isEmpty(profileFields)) {
      return;
    }

    Map<String, ProfileField> profileFieldMap = new HashMap<>();
    for (ProfileField profileField : profileFields) {
      profileFieldMap.put(profileField.getReferenceName(), profileField);
    }
    Map<String, PropertyDescriptor> sourcePdMap = getPropertyDescriptorMap(basicUserProfile);
    for (Map.Entry<String, PropertyDescriptor> entry : sourcePdMap.entrySet()) {
      String sourcePropertyName = entry.getKey();
      if (null != profileFieldMap.get(sourcePropertyName)) {
        PropertyDescriptor sourcePd = entry.getValue();
        Method readMethod = sourcePd.getReadMethod();
        try {
          if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
            readMethod.setAccessible(true);
          }
          Object value = readMethod.invoke(basicUserProfile);
          String stringValue = (null == value) ? null : value.toString();
          profileFieldMap.get(sourcePropertyName).setDataValue(stringValue);
        } catch (Exception e) {
          LOGGER.error("extractProfileFieldFromBasicUserProfile()-error: "
                       + "Could not copy entry {}", entry, e);
          throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
      }
    }
  }

  private void setDataValueOfCoreUserProfile(
      CoreUserProfile coreUserProfile, String referenceName, String dataValue) {

    if (null == coreUserProfile
        || StringUtils.isNullOrEmpty(referenceName)
        // Note: when dataValue == "", then set value as null
        || null == dataValue) {
      return;
    }

    Map<String, PropertyDescriptor> sourcePdMap = getPropertyDescriptorMap(coreUserProfile);
    for (Map.Entry<String, PropertyDescriptor> entry : sourcePdMap.entrySet()) {
      String sourcePropertyName = entry.getKey();
      if (referenceName.equals(sourcePropertyName)) {
        PropertyDescriptor sourcePd = entry.getValue();
        Class sourcePropertyClass = sourcePd.getPropertyType();
        Method writeMethod = sourcePd.getWriteMethod();
        try {
          if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
            writeMethod.setAccessible(true);
          }
          if (String.class.equals(sourcePropertyClass)
              && "".equals(dataValue)) {
            // allow set txt as empty string
            writeMethod.invoke(coreUserProfile, "");
          } else if (!StringUtils.isNullOrEmpty(dataValue)) {
            if (Integer.class.equals(sourcePropertyClass)) {
              Integer intValue = Integer.parseInt(dataValue);
              writeMethod.invoke(coreUserProfile, intValue);
            } else if (Long.class.equals(sourcePropertyClass)) {
              Long longValue = Long.parseLong(dataValue);
              writeMethod.invoke(coreUserProfile, longValue);
            } else {
              writeMethod.invoke(coreUserProfile, dataValue);
            }
          }
        } catch (Exception e) {
          LOGGER.error("setDataValueOfCoreUserProfile()-error: "
                       + "Could not copy entry {}", entry, e);
          throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
      }
    }

  }

  private void setDataValueOfBasicUserProfile(
      BasicUserProfile basicUserProfile, String referenceName, String dataValue) {

    if (null == basicUserProfile
        || StringUtils.isNullOrEmpty(referenceName)
        // Note: when dataValue == "", then set value as null
        || null == dataValue) {
      return;
    }

    Map<String, PropertyDescriptor> sourcePdMap = getPropertyDescriptorMap(basicUserProfile);
    for (Map.Entry<String, PropertyDescriptor> entry : sourcePdMap.entrySet()) {
      String sourcePropertyName = entry.getKey();
      if (referenceName.equals(sourcePropertyName)) {
        PropertyDescriptor sourcePd = entry.getValue();
        Class sourcePropertyClass = sourcePd.getPropertyType();
        Method writeMethod = sourcePd.getWriteMethod();
        try {
          if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
            writeMethod.setAccessible(true);
          }
          if (String.class.equals(sourcePropertyClass)
              && "".equals(dataValue)) {
            // allow set txt as empty string
            writeMethod.invoke(basicUserProfile, "");
          } else if (!StringUtils.isNullOrEmpty(dataValue)) {
            if (Integer.class.equals(sourcePropertyClass)) {
              Integer intValue = Integer.parseInt(dataValue);
              writeMethod.invoke(basicUserProfile, intValue);
            } else if (Long.class.equals(sourcePropertyClass)) {
              Long longValue = Long.parseLong(dataValue);
              writeMethod.invoke(basicUserProfile, longValue);
            } else {
              writeMethod.invoke(basicUserProfile, dataValue);
            }
          }
        } catch (Exception e) {
          LOGGER.error("setDataValueOfBasicUserProfile()-error: "
                       + "Could not copy entry {}", entry, e);
          throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
      }
    }

  }

  /**
   * Convert pdList to pdMap, more time-efficient to use
   *
   * @param obj
   * @return
   */
  private Map<String, PropertyDescriptor> getPropertyDescriptorMap(Object obj) {
    final PropertyDescriptor[] pdList = BeanUtils.getPropertyDescriptors(obj.getClass());
    final Map<String, PropertyDescriptor> pdMap = new HashMap<>();
    for (PropertyDescriptor pd : pdList) {
      pdMap.put(pd.getName(), pd);
    }
    return pdMap;
  }

}
