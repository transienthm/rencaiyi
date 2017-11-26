// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import hr.wozai.service.servicecommons.commons.consts.TypeSpecConsts;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.FastJSONUtils;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.dao.userorg.AddressRegionDao;
import hr.wozai.service.user.server.dao.userorg.MetaUserProfileDao;
import hr.wozai.service.user.server.dao.userorg.PickOptionDao;
import hr.wozai.service.user.server.dao.userorg.ProfileFieldDao;
import hr.wozai.service.user.server.dao.userorg.ProfileTemplateDao;
import hr.wozai.service.user.server.dao.userorg.UserProfileConfigDao;
import hr.wozai.service.user.server.factory.ProfileFieldFactory;
import hr.wozai.service.user.server.helper.PickOptionHelper;
import hr.wozai.service.user.server.helper.ProfileFieldHelper;
import hr.wozai.service.user.server.model.userorg.AddressRegion;
import hr.wozai.service.user.server.model.userorg.EncryptedPickOption;
import hr.wozai.service.user.server.model.userorg.PickOption;
import hr.wozai.service.user.server.model.userorg.ProfileField;
import hr.wozai.service.user.server.model.userorg.ProfileTemplate;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;
import hr.wozai.service.user.server.service.ProfileFieldService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-10
 */
@Service("fieldService")
public class ProfileFieldServiceImpl implements ProfileFieldService {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileFieldServiceImpl.class);

  private static final String TYPESPEC_PICKOPTIONS = "pickOptions";
  private static final String TYPESPEC_OPTIONINDEX = "optionIndex";
  private static final String TYPESPEC_OPTIONVALUE = "optionValue";

  @Autowired
  ProfileFieldDao profileFieldDao;

  @Autowired
  ProfileTemplateDao profileTemplateDao;

  @Autowired
  PickOptionDao pickOptionDao;

  @Autowired
  AddressRegionDao addressRegionDao;

  @Autowired
  ProfileFieldFactory profileFieldFactory;

  @Autowired
  MetaUserProfileDao metaUserProfileDao;

  @Autowired
  UserProfileConfigDao userProfileConfigDao;

  @PostConstruct
  public void init() {
    // validate the preset-profile-template config file is valid
    List<ProfileField> presetFields = profileFieldFactory.listFieldOfPresetProfileTemplate();
    Map<String, String> relations = profileFieldFactory.getContainingRelations();
    if (!ProfileFieldHelper.isValidSequenceOfPresetProfileField(presetFields, relations)) {
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_PROFILE_FIELDS);
    }
  }

  /**
   * Steps:
   *  0) validate: no dup displayName within same container
   *  1) set flag meta
   *  2) set typeSpec
   *  3) set physicalIndex and referenceName
   *  4) set entire logicalIndex
   *  5) insert new field
   *  6) update logical index of all fields
   *  7) handle pick options
   *
   * @param profileField
   * @return
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public long addCustomDataProfileField(ProfileField profileField) {

    if (!ProfileFieldHelper.isValidAddCustomDataFieldRequest(profileField)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    long orgId = profileField.getOrgId();
    long profileTemplateId = profileField.getProfileTemplateId();
    DataType dataType = DataType.getEnumByCode(profileField.getDataType());
    List<ProfileField> allExistingProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateIdForUpdate(orgId, profileTemplateId);

    // 0)
    String displayName = profileField.getDisplayName();
    long containerId = profileField.getContainerId();
    for (ProfileField existedProfileField : allExistingProfileFields) {
      if (null != existedProfileField.getContainerId()
          && containerId == existedProfileField.getContainerId()
          && displayName.equals(existedProfileField.getDisplayName())) {
        throw new ServiceStatusException(ServiceStatus.UP_DATA_FIELD_DUP_NAME);
      }
    }

    // 1)
    ProfileFieldHelper.setFlagForCustomDataProfileField(profileField);

    // 2)
    ProfileFieldHelper.setDefaultTypeSpecForCustomDataProfileField(profileField);

    // 3)
    // NOTE: should set physicalIndex before insert the new field into allExistingFields
    // if nextLogicalIndex is available(not -1), that means nextPhysicalIndex and nextReferenceName
    // should be available too, because they all use the same limit const in MetadataConst
    int nextPhysicalIndex = ProfileFieldHelper.getNextPhysicalIndex(allExistingProfileFields);
    profileField.setPhysicalIndex(nextPhysicalIndex);
    String nextReferenceName = ProfileFieldHelper.getNextReferenceName(allExistingProfileFields, dataType);
    profileField.setReferenceName(nextReferenceName);

    // 4)
    if (!ProfileFieldHelper.hasRoomForDataField(allExistingProfileFields)) {
      throw new ServiceStatusException(ServiceStatus.UP_DATA_FIELD_NUMBER_UPPERBOUND);
    }
    List<ProfileField> toMoveFields = new ArrayList<>();
    int toInsertIndex = -1;
    for (int i = 0; i < allExistingProfileFields.size(); i++) {
      ProfileField currField = allExistingProfileFields.get(i);
      DataType currDataType = DataType.getEnumByCode(currField.getDataType());
      if (currDataType == DataType.CONTAINER) {
        if (currField.getProfileFieldId().equals(profileField.getContainerId())) {
          toInsertIndex = i;
          toMoveFields.add(currField);
        }
      } else if (currField.getContainerId().equals(profileField.getContainerId())) {
        toMoveFields.add(currField);
      }
    }
    if (toInsertIndex == -1) {
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_CONTAINER_FOR_DATA_FIELD);
    }
    allExistingProfileFields.removeAll(toMoveFields);
    toMoveFields.add(profileField);
    allExistingProfileFields.addAll(toInsertIndex, toMoveFields);
    // set logicalIndex, plus lastModifiedUserId
    for (int i = 0; i < allExistingProfileFields.size(); i++) {
      allExistingProfileFields.get(i).setLogicalIndex(i);
      allExistingProfileFields.get(i).setLastModifiedUserId(profileField.getCreatedUserId());
    }
    if (!ProfileFieldHelper.isValidSequenceOfProfileField(allExistingProfileFields)) {
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_PROFILE_FIELDS);
    }

    // 5)
    profileFieldDao.insertProfileField(profileField);

    // 6)
    profileFieldDao.batchUpdateLogicalIndexAndContainerIdByPrimaryKey(allExistingProfileFields);

    // 7)
    if (dataType == DataType.SINGLE_PICK
        || dataType == DataType.MULTI_PICK) {
      handlePickOptionsUponAddField(profileField);
    }

    return profileField.getProfileFieldId();
  }

  /**
   * Steps:
   *  0) validate: no dup name against other container fields
   *  1) set logicalIndex
   *  2) set referenceName
   *  3) set flag meta
   *  4) insert
   *
   * @param profileField
   * @return
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addCustomContainerProfileField(ProfileField profileField) {

    if (!ProfileFieldHelper.isValidAddCustomContainerFieldRequest(profileField)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    long orgId = profileField.getOrgId();
    long profileTemplateId = profileField.getProfileTemplateId();
    DataType dataType = DataType.getEnumByCode(profileField.getDataType());
    List<ProfileField> allExistingProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateIdForUpdate(orgId, profileTemplateId);

    // 0)
    String displayName = profileField.getDisplayName();
    for (ProfileField existedProfileField: allExistingProfileFields) {
      if (DataType.CONTAINER.getCode() == existedProfileField.getDataType()
          && displayName.equals(existedProfileField.getDisplayName())) {
        throw new ServiceStatusException(ServiceStatus.UP_CONTAINER_DUP_NAME);
      }
    }

    // 1)
    if (!ProfileFieldHelper.hasRoomForContainerField(allExistingProfileFields)) {
      throw new ServiceStatusException(ServiceStatus.UP_CONTAINER_FIELD_NUMBER_UPPERBOUND);
    }
    int logicalIndex = CollectionUtils.size(allExistingProfileFields);
    profileField.setLogicalIndex(logicalIndex);

    // 2)
    // field of container type does NOT have physicalIndex
    // if hasRoomForContainerField(), that means nextPhysicalIndex and nextReferenceName
    // should be available too, because they all use the same limit const in MetadataConst
    String nextReferenceName = ProfileFieldHelper.getNextReferenceName(allExistingProfileFields, dataType);
    profileField.setReferenceName(nextReferenceName);

    // 3)
    ProfileFieldHelper.setFlagForCustomContainerProfileField(profileField);

    // 4)
    profileFieldDao.insertProfileField(profileField);

    return profileField.getProfileFieldId();
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void addAllPresetFieldForProfileTemplate(long orgId, long profileTemplateId, long actorUserId) {

    // 1) add all presetFields
    List<ProfileField> presetFields = profileFieldFactory.listFieldOfPresetProfileTemplate();

    LOGGER.info("BlaSize=" + presetFields.size());

    for (ProfileField profileField: presetFields) {
      profileField.setProfileTemplateId(profileTemplateId);
      profileField.setOrgId(orgId);
      profileField.setCreatedUserId(actorUserId);
    }
    profileFieldDao.batchInsertProfileField(presetFields);

    // 2) set containerId
    List<ProfileField> insertedAllFields = profileFieldDao
        .listProfileFieldByProfileTemplateId(orgId, profileTemplateId);
    Map<String, String> relations = profileFieldFactory.getContainingRelations();
    batchSetContainerIdForPresetFields(insertedAllFields, relations);
    for (ProfileField profileField: insertedAllFields) {
      profileField.setLastModifiedUserId(actorUserId);
    }
    profileFieldDao.batchUpdateLogicalIndexAndContainerIdByPrimaryKey(insertedAllFields);

    // 3) add pickOptions
    List<ProfileField> insertedFields = profileFieldDao
        .listDataProfileFieldByProfileTemplateId(orgId, profileTemplateId);
    Map<String, Long> profileFieldMap = new HashMap<>();
    for (ProfileField insertedField: insertedFields) {
      profileFieldMap.put(insertedField.getReferenceName(), insertedField.getProfileFieldId());
    }
    for (ProfileField profileField: presetFields) {
      DataType dataType = DataType.getEnumByCode(profileField.getDataType());
      if (dataType == DataType.SINGLE_PICK
          || dataType == DataType.MULTI_PICK) {
        profileField.setProfileFieldId(profileFieldMap.get(profileField.getReferenceName()));
        handlePickOptionsUponAddField(profileField);
      }
    }

  }

  @Override
  @LogAround
  public ProfileField getProfileField(long orgId, long profileFieldId) {

    ProfileField profileField = profileFieldDao.findProfileFieldByOrgIdAndPrimaryKey(orgId, profileFieldId);

    if (null == profileField) {
      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_FIELD_NOT_FOUND);
    }

    // populate typeSpec
    setTypeSpecForProfileField(profileField);

    return profileField;
  }

  @Override
  public ProfileField getProfileField(long orgId, long profileTemplateId, String referenceName) {

    ProfileField profileField =
        profileFieldDao.findProfileFieldByReferenceName(orgId, profileTemplateId, referenceName);

    if (null == profileField) {
      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_FIELD_NOT_FOUND);
    }

    // populate typeSpec
    setTypeSpecForProfileField(profileField);

    return profileField;
  }

  @Override
  @LogAround
  public List<ProfileField> listAllProfileFieldOfTemplate(long orgId, long profileTemplateId) {
    List<ProfileField> profileFields = profileFieldDao.listProfileFieldByProfileTemplateId(orgId, profileTemplateId);
    for (ProfileField profileField: profileFields) {
      setTypeSpecForProfileField(profileField);
    }
    return profileFields;
  }

  @Override
  @LogAround
  public List<ProfileField> listDataProfileFieldOfTemplate(long orgId, long profileTemplateId) {
    List<ProfileField> profileFields =
        profileFieldDao.listDataProfileFieldByProfileTemplateId(orgId, profileTemplateId);
    return profileFields;
  }

  @Override
  public List<ProfileField> listDataProfileFieldOfContainer(long orgId, long containerFieldId) {
    List<ProfileField> profileFields =
        profileFieldDao.listDataProfileFieldByContainerId(orgId, containerFieldId);
    return profileFields;
  }

  /**
   * Steps:
   *  1) validate: no dup displayName within the same container
   *  2) update
   *
   * @param profileField
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateDataProfileField(ProfileField profileField) {

    if (!ProfileFieldHelper.isValidUpdateCustomDataFieldRequest(profileField)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    // 1)
    long orgId = profileField.getOrgId();
    long profileTemplateId = profileField.getProfileTemplateId();
    List<ProfileField> allExistingProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateIdForUpdate(orgId, profileTemplateId);
    String displayName = profileField.getDisplayName();
    long containerId = profileField.getContainerId();
    for (ProfileField existedProfileField: allExistingProfileFields) {
      if (null != displayName
          && !profileField.getProfileFieldId().equals(existedProfileField.getProfileFieldId())
          && DataType.CONTAINER.getCode() != existedProfileField.getDataType()
          && containerId == existedProfileField.getContainerId()
          && displayName.equals(existedProfileField.getDisplayName())) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
    }

    // 2)
    ProfileField currProfileField = profileFieldDao.findProfileFieldByOrgIdAndPrimaryKeyForUpdate(
        profileField.getOrgId(), profileField.getProfileFieldId());
    if (null == currProfileField) {
      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_FIELD_NOT_FOUND);
    }

    cleanUpdateDataFieldRequestAgainstFlags(profileField, currProfileField);
    profileFieldDao.updateProfileFieldByPrimaryKeySelective(profileField);

    DataType dataType = DataType.getEnumByCode(currProfileField.getDataType());
    if (dataType == DataType.SINGLE_PICK
        || dataType == DataType.MULTI_PICK) {
      handlePickOptionsUponUpdateField(profileField);
    }

  }

  /**
   * Steps:
   *  1) validate: no dup displayName against other container fields
   *  2) update
   *
   * @param profileField
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateContainerProfileField(ProfileField profileField) {

    if (!ProfileFieldHelper.isValidUpdateCustomContainerFieldRequest(profileField)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    // 1)
    long orgId = profileField.getOrgId();
    long profileTemplateId = profileField.getProfileTemplateId();
    List<ProfileField> allExistingProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateIdForUpdate(orgId, profileTemplateId);
    String displayName = profileField.getDisplayName();
    for (ProfileField existedProfileField: allExistingProfileFields) {
      if (null != displayName
          && !profileField.getProfileFieldId().equals(existedProfileField.getProfileFieldId())
          && DataType.CONTAINER.getCode() == existedProfileField.getDataType()
          && displayName.equals(existedProfileField.getDisplayName())) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
    }

    // 2)
    cleanUpdateContainerFieldRequest(profileField);
    profileFieldDao.updateProfileFieldByPrimaryKeySelective(profileField);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void moveDataProfileFiled(ProfileField profileField) {

    if (!ProfileFieldHelper.isValidMoveDataRequest(profileField)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    // reorder entire field list
    List<ProfileField> currFields = profileFieldDao.listProfileFieldByProfileTemplateIdForUpdate(
        profileField.getOrgId(), profileField.getProfileTemplateId());
    List<ProfileField> newFields = new ArrayList<>();
    for (int i = 0; i < currFields.size(); i++) {
      ProfileField newPF = new ProfileField();
      BeanHelper.copyPropertiesHandlingJSON(currFields.get(i), newPF);
      newFields.add(newPF);
    }
    ProfileField theField = null;
    for (int i = 0; i < newFields.size(); i++) {
      ProfileField newProfileField = newFields.get(i);
      if (newProfileField.getProfileFieldId().equals(profileField.getProfileFieldId())) {
        theField = newFields.remove(i);
        break;
      }
    }

    int toLogicalIndex = profileField.getLogicalIndex();
    newFields.add(toLogicalIndex, theField);
    for (int i = 0; i < newFields.size(); i++) {
      ProfileField newProfileField = newFields.get(i);
      newProfileField.setLogicalIndex(i);
    }

    // validate
    if (!ProfileFieldHelper.isValidMove(currFields, newFields)) {
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_FIELD_MOVE);
    }

    // assign entire order
    profileFieldDao.batchUpdateLogicalIndexAndContainerIdByPrimaryKey(newFields);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void moveContainerProfileField(ProfileField profileField) {

    if (!ProfileFieldHelper.isValidMoveContainerRequest(profileField)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    // reorder entire field list
    List<ProfileField> currFields = profileFieldDao.listProfileFieldByProfileTemplateIdForUpdate(
        profileField.getOrgId(), profileField.getProfileTemplateId());
    List<ProfileField> newFields = new ArrayList<>();
    for (int i = 0; i < currFields.size(); i++) {
      ProfileField newField = new ProfileField();
      BeanHelper.copyPropertiesHandlingJSON(currFields.get(i), newField);
      newFields.add(newField);
    }
    List<ProfileField> toMoveFields = new ArrayList<>();
    for (int i = 0; i < newFields.size(); i++) {
      ProfileField currField = newFields.get(i);
      if (currField.getProfileFieldId().equals(profileField.getProfileFieldId())
          || (null != currField.getContainerId()
              && currField.getContainerId().equals(profileField.getProfileFieldId()))) {
        toMoveFields.add(currField);
      }
    }
    newFields.removeAll(toMoveFields);
    int toLogicalIndex = profileField.getLogicalIndex();
    newFields.addAll(toLogicalIndex, toMoveFields);
    for (int i = 0; i < newFields.size(); i++) {
      ProfileField newField = newFields.get(i);
      newField.setLogicalIndex(i);
    }

    // validate
    if (!ProfileFieldHelper.isValidMove(currFields, newFields)) {
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_FIELD_MOVE);
    }
    // assign entire order
    profileFieldDao.batchUpdateLogicalIndexAndContainerIdByPrimaryKey(newFields);
  }

  /**
   * Steps:
   *  1) validate
   *  2) delete the field itself
   *  3) reorder remaining fields
   *  4) delete pick options if needed
   *  5) wipe field value in userProfile
   *
   * @param orgId
   * @param profileFieldId
   * @param actorUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteDataProfileField(long orgId, long profileFieldId, long actorUserId) {

    // 1)
    ProfileField profileField = profileFieldDao.findProfileFieldByOrgIdAndPrimaryKey(orgId, profileFieldId);

    if (null == profileField
        || null == DataType.getEnumByCode(profileField.getDataType())
        || DataType.CONTAINER.getCode() == profileField.getDataType()) {
      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_FIELD_NOT_FOUND);
    }
    if (profileField.getDataType() == DataType.CONTAINER.getCode()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    if (profileField.getIsSystemRequired() == 1) {
      throw new ServiceStatusException(ServiceStatus.UP_CANNOT_DELETE_SYSTEM_REQURIED_FIELD);
    }

    // 2)
    profileFieldDao.deleteProfileFieldByPrimaryKey(orgId, profileFieldId, actorUserId);

    // 3)
    List<ProfileField> existedProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateIdForUpdate(orgId, profileField.getProfileTemplateId());
    allignOrderAfterDeletion(existedProfileFields);
    profileFieldDao.batchUpdateLogicalIndexAndContainerIdByPrimaryKey(existedProfileFields);

    // 4)
    DataType dataType = DataType.getEnumByCode(profileField.getDataType());
    if (dataType == DataType.SINGLE_PICK
        || dataType == DataType.MULTI_PICK) {
      pickOptionDao.batchDeletePickOptionByProfileFieldId(orgId, profileFieldId, actorUserId);
    }

    // 5)
    metaUserProfileDao.wipeFieldValueByOrgIdAndProfileTemplateIdAndPhysicalIndex(
        orgId, profileField.getProfileTemplateId(), profileField.getPhysicalIndex(), actorUserId);

  }

  /**
   * Steps:
   *  1) validate
   *  2) delete the container field and its containing data fields
   *  3) reorder remaining fields
   *  4) delete pick options of contained dataFields if needed
   *  5) wipe remaining fields
   *
   * @param orgId
   * @param profileFieldId
   * @param actorUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteContainerProfileField(long orgId, long profileFieldId, long actorUserId) {

    // 1)
    ProfileField profileField = profileFieldDao.findProfileFieldByOrgIdAndPrimaryKey(orgId, profileFieldId);
    if (null == profileField
        || null == DataType.getEnumByCode(profileField.getDataType())
        || DataType.CONTAINER.getCode() != profileField.getDataType()) {
      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_FIELD_NOT_FOUND);
    }
    if (profileField.getDataType() != DataType.CONTAINER.getCode()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    if (profileField.getIsSystemRequired() == 1) {
      throw new ServiceStatusException(ServiceStatus.UP_CANNOT_DELETE_SYSTEM_REQURIED_FIELD);
    }

    // 2)
    List<ProfileField> existedProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateIdForUpdate(orgId, profileField.getProfileTemplateId());
    List<Long> toDeleteProfileFieldIds = new ArrayList<>();
    List<Long> toDeletePickProfileFieldIds = new ArrayList<>();
    for (ProfileField oneProfileField: existedProfileFields) {
      if (profileFieldId == oneProfileField.getProfileFieldId()
          || (null != oneProfileField.getContainerId()
              && profileFieldId == oneProfileField.getContainerId())) {
        toDeleteProfileFieldIds.add(oneProfileField.getProfileFieldId());
        DataType dataType = DataType.getEnumByCode(oneProfileField.getDataType());
        if (dataType == DataType.SINGLE_PICK
            || dataType == DataType.MULTI_PICK) {
          toDeletePickProfileFieldIds.add(oneProfileField.getProfileFieldId());
        }
      }
    }
    profileFieldDao.batchDeleteProfileFieldByPrimaryKey(orgId, toDeleteProfileFieldIds, actorUserId);

    // Note: Do not handle containing fields here, but in facade
    // 3)
    existedProfileFields =
        profileFieldDao.listProfileFieldByProfileTemplateIdForUpdate(orgId, profileField.getProfileTemplateId());
    allignOrderAfterDeletion(existedProfileFields);
    profileFieldDao.batchUpdateLogicalIndexAndContainerIdByPrimaryKey(existedProfileFields);

    // 4)
    for (Long pickProfileFieldId: toDeletePickProfileFieldIds) {
      pickOptionDao.batchDeletePickOptionByProfileFieldId(orgId, pickProfileFieldId, actorUserId);
    }

  }

  /**
   * Steps:
   *  1) delete all profileFields
   *  2) delete pickOptions
   *
   * @param orgId
   * @param profileTemplateId
   * @param actorUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteAllProfileFieldOfTemplate(long orgId, long profileTemplateId, long actorUserId) {

    List<ProfileField> profileFields = profileFieldDao.listProfileFieldByProfileTemplateId(orgId, profileTemplateId);
    if (CollectionUtils.isEmpty(profileFields)) {
      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_TEMPLATE_NOT_FOUND);
    }
    List<Long> profileFieldIds = new ArrayList<>();
    List<Long> pickProfileFieldIds = new ArrayList<>();
    for (ProfileField profileField: profileFields) {
      profileFieldIds.add(profileField.getProfileFieldId());
      DataType dataType = DataType.getEnumByCode(profileField.getDataType());
      if (dataType == DataType.SINGLE_PICK
          || dataType == DataType.MULTI_PICK) {
        pickProfileFieldIds.add(profileField.getProfileFieldId());
      }
    }

    // 1)
    profileFieldDao.batchDeleteProfileFieldByPrimaryKey(orgId, profileFieldIds, actorUserId);

    // 2)
    for (Long pickProfileFieldId: pickProfileFieldIds) {
      pickOptionDao.batchDeletePickOptionByProfileFieldId(orgId, pickProfileFieldId, actorUserId);
    }

  }

  @Override
  @LogAround
  public List<PickOption> listPickOptionsByOrgIdAndPickOptionIds(long orgId, List<Long> pickOptionIds) {
    if (CollectionUtils.isEmpty(pickOptionIds)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    return pickOptionDao.listPickOptionByOrgIdAndPickOptionIds(orgId, pickOptionIds);
  }

  @Override
  @LogAround
  public List<PickOption> listPickOptionByOrgIdAndProfileFieldIdForUpdate(long orgId, long profileFieldId) {
    List<PickOption> pickOptions =
        pickOptionDao.listPickOptionByProfileFieldIdForUpdate(orgId, profileFieldId);
    return pickOptions;
  }

  @Override
  @LogAround
  public List<AddressRegion> listAddressRegion(long parentId) {
    return addressRegionDao.listAddressRegionByParentId(parentId);
  }

  /**
   * Merge fields' meta of:
   *  1) CoreUserProfile
   *  2) BasicUserProfile
   *  3) MetaUserProfile
   *
   * @param orgId
   * @return
   */
  @Override
  @LogAround
  public List<ProfileField> listAllProfileFieldOfOrg(long orgId) {

    List<ProfileField> profileFieldsOfAll = new ArrayList<>();

    // 1) 2)
    List<UserProfileConfig> userProfileConfigs = userProfileConfigDao.listUserProfileConfigByOrgId(orgId);
    if (CollectionUtils.isEmpty(userProfileConfigs)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    for (UserProfileConfig userProfileConfig: userProfileConfigs) {
      ProfileField profileField = new ProfileField();
      BeanUtils.copyProperties(userProfileConfig, profileField);
      profileFieldsOfAll.add(profileField);
    }

    // 3)
    ProfileTemplate profileTemplate = profileTemplateDao.findTheOnlyProfileTemplateByOrgId(orgId);
    List<ProfileField> profileFieldsOfMetaUserProfile =
        profileFieldDao.listProfileFieldByProfileTemplateId(orgId, profileTemplate.getProfileTemplateId());
    for (ProfileField profileField: profileFieldsOfMetaUserProfile) {
      if (DataType.SINGLE_PICK.getCode() == profileField.getDataType()
          || DataType.MULTI_PICK.getCode() == profileField.getDataType()) {
        List<PickOption> pickOptions =
            pickOptionDao.listPickOptionByProfileFieldId(orgId, profileField.getProfileFieldId());
        List<EncryptedPickOption> encryptedPickOption = new ArrayList<>();
        for (PickOption pickOption: pickOptions) {
          EncryptedPickOption cleanPickOption = new EncryptedPickOption();
          PickOptionHelper.copyPropertiesFromPickOptionToEncryptedPickOption(pickOption, cleanPickOption);
          encryptedPickOption.add(cleanPickOption);
        }
        JSONObject typeSpec = new JSONObject();
        typeSpec.put(TYPESPEC_PICKOPTIONS, encryptedPickOption);
        profileField.setTypeSpec(typeSpec);
      }
    }
    profileFieldsOfAll.addAll(profileFieldsOfMetaUserProfile);

    return profileFieldsOfAll;
  }

  /**
   * Merge fields' meta of:
   *  1) CoreUserProfile
   *  2) BasicUserProfile
   *  3) MetaUserProfile
   *
   * @param orgId
   * @return
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public List<ProfileField> listAllProfileFieldOfOrgForUpdate(long orgId) {

    List<ProfileField> profileFieldsOfAll = new ArrayList<>();

    // 1) 2)
    List<UserProfileConfig> userProfileConfigs = userProfileConfigDao.listUserProfileConfigByOrgIdForUpdate(orgId);
    if (CollectionUtils.isEmpty(userProfileConfigs)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    for (UserProfileConfig userProfileConfig: userProfileConfigs) {
      ProfileField profileField = new ProfileField();
      BeanUtils.copyProperties(userProfileConfig, profileField);
      profileFieldsOfAll.add(profileField);
    }

    // 3)
    ProfileTemplate profileTemplate = profileTemplateDao.findTheOnlyProfileTemplateByOrgId(orgId);
    List<ProfileField> profileFieldsOfMetaUserProfile =
        profileFieldDao.listProfileFieldByProfileTemplateIdForUpdate(orgId, profileTemplate.getProfileTemplateId());
    profileFieldsOfAll.addAll(profileFieldsOfMetaUserProfile);

    return profileFieldsOfAll;
  }


  /**
   * For now, only 'isEnabled' can be modified by user
   *
   * @param userProfileConfig
   */
  @Override
  @LogAround
  public void updateUserProfileConfig(UserProfileConfig userProfileConfig) {

    if (!ProfileFieldHelper.isValidUpdateUserProfileConfigRequest(userProfileConfig)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    String referenceName = userProfileConfig.getReferenceName();
    long orgId = userProfileConfig.getOrgId();
    UserProfileConfig foundUserProfileConfig =
        userProfileConfigDao.findUserProfileConfigByOrgIdAndReferenceName(orgId, referenceName);
    if (null == foundUserProfileConfig) {
      throw new ServiceStatusException(ServiceStatus.UP_PROFILE_FIELD_NOT_FOUND);
    }

    if (0 == foundUserProfileConfig.getIsEnabledEditable()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    } else {
      foundUserProfileConfig.setIsEnabled(userProfileConfig.getIsEnabled());
      foundUserProfileConfig.setLastModifiedUserId(userProfileConfig.getLastModifiedUserId());
      userProfileConfigDao.updateUserProfileConfigByPrimaryKeySelective(foundUserProfileConfig);
    }

  }

  /**
   * Validate and add pickOptions
   *
   * @param profileField
   */
  private void handlePickOptionsUponAddField(ProfileField profileField) {

    if (null == profileField.getTypeSpec()) {
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_PICK_OPTIONS);
    }
    List<PickOption> pickOptions = null;
    try {
      JSONArray pickOptionsArray = profileField.getTypeSpec().getJSONArray(TypeSpecConsts.PICK_OPTIONS_KEY);
      pickOptions = FastJSONUtils.convertJSONArrayToObjectList(pickOptionsArray, PickOption.class);
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_PICK_OPTIONS);
    }
    for (PickOption pickOption: pickOptions) {
      pickOption.setOrgId(profileField.getOrgId());
      pickOption.setProfileFieldId(profileField.getProfileFieldId());
      pickOption.setIsDeprecated(0);
      pickOption.setCreatedUserId(profileField.getCreatedUserId());
    }
    if (!ProfileFieldHelper.isValidSequenceOfPickOption(
        pickOptions, DataType.getEnumByCode(profileField.getDataType().intValue()))) {
      LOGGER.error("Exception: POSeq=" + pickOptions);
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_PICK_OPTIONS);
    }

    for (PickOption option : pickOptions) {
      option.setOrgId(profileField.getOrgId());
      option.setProfileFieldId(profileField.getProfileFieldId());
      option.setCreatedUserId(profileField.getCreatedUserId());
    }
    pickOptionDao.batchInsertPickOption(pickOptions);

  }

  /**
   * Validate and add pickOptions
   *
   * @param profileField
   */
  private void handlePickOptionsUponUpdateField(ProfileField profileField) {

    if (null == profileField.getTypeSpec()) {
      return;
    }

    List<EncryptedPickOption> encryptedPickOptions = null;
    try {
      JSONArray pickOptionsArray = profileField.getTypeSpec().getJSONArray(TypeSpecConsts.PICK_OPTIONS_KEY);
      encryptedPickOptions = FastJSONUtils.convertJSONArrayToObjectList(pickOptionsArray, EncryptedPickOption.class);
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_PICK_OPTIONS);
    }
    List<PickOption> pickOptions = new ArrayList<>();
    if (!CollectionUtils.isEmpty(encryptedPickOptions)) {
      for (EncryptedPickOption encryptedPickOption: encryptedPickOptions) {
        if (null == encryptedPickOption.getIsDeprecated()
            || encryptedPickOption.getIsDeprecated() == 0) {
          PickOption pickOption = new PickOption();
          PickOptionHelper.copyPropertiesFromEncryptedPickOptionToPickOption(encryptedPickOption, pickOption);
          pickOptions.add(pickOption);
        }
      }
    }
    for (PickOption pickOption: pickOptions) {
      pickOption.setOrgId(profileField.getOrgId());
      pickOption.setProfileFieldId(profileField.getProfileFieldId());
      if (null == pickOption.getPickOptionId()) {
        pickOption.setCreatedUserId(profileField.getLastModifiedUserId());
      } else {
        pickOption.setLastModifiedUserId(profileField.getLastModifiedUserId());
      }
    }
    if (!ProfileFieldHelper.isValidSequenceOfPickOption(
          pickOptions, DataType.getEnumByCode(profileField.getDataType().intValue()))) {
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_PICK_OPTIONS);
    }

    List<PickOption> currPickOptions = pickOptionDao.listPickOptionByProfileFieldIdForUpdate(
        profileField.getOrgId(), profileField.getProfileFieldId());
    for (PickOption currPickOption: currPickOptions) {
      currPickOption.setLastModifiedUserId(profileField.getLastModifiedUserId());
    }

    // add pickOptions
    List<PickOption> pickOptionsToAdd =
        ProfileFieldHelper.listPickOptionToAddUponUpdateField(currPickOptions, pickOptions);
    if (!CollectionUtils.isEmpty(pickOptionsToAdd)) {
      pickOptionDao.batchInsertPickOption(pickOptionsToAdd);
    }

    // deprecate pickOptions
    List<Long> pickOptionsIdsToDeprecate =
        ProfileFieldHelper.listPickOptionIdsToDeprecatedUponUpdateField(currPickOptions, pickOptions);
    if (!CollectionUtils.isEmpty(pickOptionsIdsToDeprecate)) {
      pickOptionDao.batchDeprecatePickOptionByPrimaryKey(
          profileField.getOrgId(), pickOptionsIdsToDeprecate, profileField.getLastModifiedUserId());
    }

    // update pickOptions
    List<PickOption> pickOptionsToUpdate =
        ProfileFieldHelper.listPickOptionToUpdateUponUpdateField(currPickOptions, pickOptions);
    if (!CollectionUtils.isEmpty(pickOptionsToUpdate)) {
      pickOptionDao.batchUpdatePickOption(pickOptionsToUpdate);
    }

  }


  private void setTypeSpecForProfileField(ProfileField profileField) {

    DataType dataType = DataType.getEnumByCode(profileField.getDataType());

    // handle SINGLE_PICK and MULTI_PICK
    if (DataType.SINGLE_PICK == dataType
        || DataType.MULTI_PICK == dataType) {
      setPickOptionsForProfileField(profileField);
    }

  }

  private void setPickOptionsForProfileField(ProfileField profileField) {
    List<PickOption> pickOptions =
        pickOptionDao.listPickOptionByProfileFieldId(profileField.getOrgId(), profileField.getProfileFieldId());
    List<EncryptedPickOption> encryptedPickOptions = new ArrayList<>();
    for (int i = 0; i < pickOptions.size(); i++) {
      EncryptedPickOption encryptedPickOption = new EncryptedPickOption();
      PickOptionHelper.copyPropertiesFromPickOptionToEncryptedPickOption(pickOptions.get(i), encryptedPickOption);
      encryptedPickOptions.add(encryptedPickOption);
    }
    JSONObject typeSpec = new JSONObject();
    typeSpec.put(TypeSpecConsts.PICK_OPTIONS_KEY, encryptedPickOptions);
    profileField.setTypeSpec(typeSpec);
  }

  private void batchSetContainerIdForPresetFields(List<ProfileField> presetFields, Map<String, String> relations) {
    Map<String, Long> containerIdMap = new HashMap<>();
    for (ProfileField profileField: presetFields) {
      if (DataType.CONTAINER.getCode() == profileField.getDataType().intValue()) {
        containerIdMap.put(profileField.getReferenceName(), profileField.getProfileFieldId());
      }
    }
    for (ProfileField profileField: presetFields) {
      String containerRefName = relations.get(profileField.getReferenceName());
      if (null != containerRefName) {
        profileField.setContainerId(containerIdMap.get(containerRefName));
      }
    }
  }

  private void cleanUpdateDataFieldRequestAgainstFlags(ProfileField newProfileField, ProfileField currProfileField) {
    if (currProfileField.getIsPublicVisibleEditable() == 0) {
      newProfileField.setIsPublicVisible(null);
    }
    if (currProfileField.getIsTypeSpecEditable() == 0) {
      newProfileField.setTypeSpec(null);
    }
    if (currProfileField.getIsEnabledEditable() == 0) {
      newProfileField.setIsEnabled(null);
    }
    if (currProfileField.getIsMandatoryEditable() == 0) {
      newProfileField.setIsMandatory(null);
    }
  }

  private void cleanUpdateContainerFieldRequest(ProfileField profileField) {
    profileField.setTypeSpec(null);
    profileField.setIsPublicVisible(null);
    profileField.setIsEnabled(null);
    profileField.setIsMandatory(null);
  }

  private void allignOrderAfterDeletion(List<ProfileField> profileFields) {
    for (int i = 0; i < profileFields.size(); i++) {
      profileFields.get(i).setLogicalIndex(i);
    }
  }

//  public static void main(String[] args) {
//    JSONArray jsonArray = JSONArray.parseArray("[{\"isDefault\":0,\"isDeprecated\":0,\"pickOptionId\":\"75E6094327E73991\",\"optionIndex\":0,\"optionValue\":\"0\"},{\"isDefault\":0,\"isDeprecated\":0,\"pickOptionId\":\"E4A277DBF2E3C2E1\",\"optionIndex\":1,\"optionValue\":\"1\"},{\"isDefault\":0,\"isDeprecated\":0,\"pickOptionId\":\"070BEC021F3023D6\",\"optionIndex\":2,\"optionValue\":\"2\"}]");
//    for (int i = 0; i < jsonArray.size(); i++) {
//      JSON.toJavaObject(jsonArray.getJSONObject(i), PickOption.class);
//    }
//  }

}