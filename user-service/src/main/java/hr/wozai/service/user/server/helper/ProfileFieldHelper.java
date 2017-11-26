// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.commons.consts.MetadataConsts;
import hr.wozai.service.servicecommons.commons.consts.TypeSpecConsts;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.utils.BooleanUtils;
import hr.wozai.service.servicecommons.commons.utils.LongUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.server.model.userorg.PickOption;
import hr.wozai.service.user.server.model.userorg.ProfileField;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-12-10
 */
public class ProfileFieldHelper {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileFieldHelper.class);

  /**
   * Check if it's a valid add request
   *
   * @param profileField
   * @return
   */
  public static boolean isValidAddCustomContainerFieldRequest(ProfileField profileField) {
    if (null == profileField
        || null == profileField.getOrgId()
        || null == profileField.getProfileTemplateId()
        || null != profileField.getContainerId()
        || !isValidFieldDisplayName(profileField.getDisplayName())
        || (null == profileField.getDataType() || null == DataType.getEnumByCode(profileField.getDataType())
            || DataType.CONTAINER != DataType.getEnumByCode(profileField.getDataType().intValue()))
        || null == profileField.getIsPublicVisible()
        || null == profileField.getIsEnabled()
        || null == profileField.getIsMandatory()
        || null == profileField.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  /**
   * Check if it's a valid add request
   *
   * @param profileField
   * @return
   */
  public static boolean isValidAddCustomDataFieldRequest(ProfileField profileField) {
    if (null == profileField
        || null == profileField.getOrgId()
        || null == profileField.getProfileTemplateId()
        || null == profileField.getContainerId()
        || !isValidFieldDisplayName(profileField.getDisplayName())
        || (null == profileField.getDataType()
            || null == DataType.getEnumByCode(profileField.getDataType())
            || DataType.CONTAINER == DataType.getEnumByCode(profileField.getDataType().intValue()))
        || null == profileField.getIsPublicVisible()
        || null == profileField.getIsEnabled()
        || null == profileField.getIsMandatory()
        || null == profileField.getCreatedUserId()) {
      return false;
    }

    return true;
  }

  public static boolean isValidUpdateCustomDataFieldRequest(ProfileField profileField) {

    if (null == profileField
        || null == profileField.getOrgId()
        || null == profileField.getProfileTemplateId()
        || (null != profileField.getDisplayName()
            && !isValidFieldDisplayName(profileField.getDisplayName()))
        || (null != profileField.getIsPublicVisible()
           && !BooleanUtils.isValidBooleanValue(profileField.getIsPublicVisible()))
        || (null != profileField.getIsEnabled()
            && !BooleanUtils.isValidBooleanValue(profileField.getIsEnabled()))
        || (null != profileField.getIsMandatory()
            && !BooleanUtils.isValidBooleanValue(profileField.getIsMandatory()))
        || null == profileField.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidUpdateCustomContainerFieldRequest(ProfileField profileField) {

    if (null == profileField
        || null == profileField.getOrgId()
        || null != profileField.getContainerId()
        || null == profileField.getProfileTemplateId()
        || (null != profileField.getDisplayName()
            && !isValidFieldDisplayName(profileField.getDisplayName()))
        || null == profileField.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }

  /**
   * Apply the same rule to each single field
   *
   * @param profileFields
   * @return
   */
  public static boolean isAcceptableBatchCreateRequest(List<ProfileField> profileFields) {
    if (CollectionUtils.isEmpty(profileFields)) {
      return false;
    }
    for (ProfileField profileField : profileFields) {
      if (!isValidAddCustomContainerFieldRequest(profileField)) {
        return false;
      }
    }
    return true;
  }


  /**
   * Check if the update request is acceptable
   *
   * @param profileField
   * @return
   */
  public static boolean isAcceptableUpdateRequest(ProfileField profileField) {
    // TODO: howto, should consider diff with originalProfileField
    if (null == profileField
        || (null == profileField.getOrgId() )
        || (null == profileField.getProfileTemplateId())
        || !StringUtils.isValidVarchar100(profileField.getDisplayName())
        || (null == profileField.getDataType() || null == DataType.getEnumByCode(profileField.getDataType()))
        || (DataType.CONTAINER != DataType.getEnumByCode(profileField.getDataType().intValue())
            && null == profileField.getContainerId())) {
      return false;
    }
    return true;
  }

  /**
   * Apply the same rule to each single field
   *
   * @param profileFields
   * @return
   */
  public static boolean isAcceptableBatchUpdateRequest(List<ProfileField> profileFields) {
    if (CollectionUtils.isEmpty(profileFields)) {
      return false;
    }
    for (ProfileField profileField : profileFields) {
      if (!isAcceptableUpdateRequest(profileField)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Check if the container-field number upperbound is hitted
   *
   * @param allExistingProfileField
   * @return
   */
  public static boolean hasRoomForContainerField(List<ProfileField> allExistingProfileField) {
    if (null == allExistingProfileField) {
      return true;
    }
    int containerFieldCount = 0;
    for (ProfileField profileField : allExistingProfileField) {
      if (profileField.getDataType() == DataType.CONTAINER.getCode()) {
        containerFieldCount += 1;
      }
    }
    if (containerFieldCount == MetadataConsts.MAX_CONTAINER_FIELD_INDEX + 1) {
      return false;
    }
    return true;
  }

  /**
   * Check if the data-field number upperbound is hitted
   *
   * @param allExistingProfileField
   * @return
   */
  public static boolean hasRoomForDataField(List<ProfileField> allExistingProfileField) {
    if (null == allExistingProfileField) {
      return true;
    }
    int fieldFieldCount = 0;
    for (ProfileField profileField : allExistingProfileField) {
      if (profileField.getDataType() != DataType.CONTAINER.getCode()) {
        fieldFieldCount += 1;
      }
    }
    if (fieldFieldCount == MetadataConsts.MAX_DATA_FIELD_INDEX + 1) {
      return false;
    }
    return true;
  }

  /**
   * Get the next logical index for field, which is one larger than current largest logicalIndex.
   * If need keep continuous increasing like (0, 1, 2, 3..), then should re-assign
   * logicalIndex of each field when delete a field
   *
   * @param allExistingProfileFields
   * @return -1 if no room
   */
  public static int getNextLogicalIndex(List<ProfileField> allExistingProfileFields, DataType dataType) {
    if ((dataType.equals(DataType.CONTAINER) && hasRoomForContainerField(allExistingProfileFields))
        || (!dataType.equals(DataType.CONTAINER) && hasRoomForDataField(allExistingProfileFields))) {
      return CollectionUtils.size(allExistingProfileFields);
    }
    return -1;
  }

  /**
   * Get the next physical index for field
   * The physical index refers to N of 'valN' in Item table
   * Container-type fields do NOT occupy any physical index
   *
   * @param allExistingProfileFields
   * @return
   */
  public static int getNextPhysicalIndex(List<ProfileField> allExistingProfileFields) {
    Set<Integer> allIndices = ProfileFieldHelper.getIndexFullSetInclusive(0, MetadataConsts.MAX_DATA_FIELD_INDEX);
    for (ProfileField profileField : allExistingProfileFields) {
      DataType dataType = DataType.getEnumByCode(profileField.getDataType());
      if (DataType.CONTAINER != dataType) {
        allIndices.remove(profileField.getPhysicalIndex());
      }
    }
    return ProfileFieldHelper.getNextAvailableIndexInSetInRangeInclusive(
        allIndices, 0, MetadataConsts.MAX_DATA_FIELD_INDEX);
  }

  /**
   * Get next referenceName for user defined field
   * ReferenceName format: ${DATATYPE_NAME}_${INDEX},
   * where ${INDEX} is in data-type scope
   *
   * @param allExistingProfileFields
   * @param dataType
   * @return
   */
  public static String getNextReferenceName(List<ProfileField> allExistingProfileFields, DataType dataType) {

    int maxIndex = (dataType.equals(DataType.CONTAINER) ? MetadataConsts.MAX_CONTAINER_FIELD_INDEX
                                                        : MetadataConsts.MAX_DATA_FIELD_INDEX);
    String referenceNamePrefix = dataType.getMsg() + MetadataConsts.FIELD_REFERENCE_NAME_SEPARATOR;
    Set<Integer> allIndices = ProfileFieldHelper.getIndexFullSetInclusive(0, maxIndex);
    String nextReferenceName = null;

    for (ProfileField profileField : allExistingProfileFields) {
      if (dataType.getCode() == profileField.getDataType()) {
        if (!StringUtils.isNullOrEmpty(profileField.getReferenceName().replaceAll("[\\D]", ""))) {
          int currReferenceIndex = Integer.parseInt(profileField.getReferenceName().replaceAll("[\\D]", ""));
          allIndices.remove(currReferenceIndex);
        }
      }
    }
    int nextAvailableReferenceIndex = ProfileFieldHelper
        .getNextAvailableIndexInSetInRangeInclusive(allIndices, 0, maxIndex);
    if (nextAvailableReferenceIndex != -1) {
      nextReferenceName = referenceNamePrefix + nextAvailableReferenceIndex;
    }

    return nextReferenceName;
  }

  private static Set<Integer> getIndexFullSetInclusive(int lowerBound, int upperBound) {
    Set<Integer> allIndices = new HashSet<>();
    for (int i = lowerBound; i < upperBound + 1; i++) {
      allIndices.add(i);
    }
    return allIndices;
  }

  private static int getNextAvailableIndexInSetInRangeInclusive(
      Set<Integer> remainingIndices,
      int lowerBound,
      int upperBound) {

    if (CollectionUtils.size(remainingIndices) <= 0) {
      return -1;
    }
    for (int i = lowerBound; i <= upperBound; i++) {
      if (remainingIndices.contains(i)) {
        return i;
      }
    }
    return -1;
  }

  public static boolean isValidMoveDataRequest(ProfileField profileField) {
    if (null == profileField
        || null == profileField.getProfileFieldId()
        || null == profileField.getProfileTemplateId()
        || null == profileField.getContainerId()
        || null == profileField.getLogicalIndex()
        || null == profileField.getLastModifiedUserId()) {
      return false;
    }
    if (DataType.CONTAINER.getCode() != profileField.getDataType()
        && null == profileField.getContainerId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidMoveContainerRequest(ProfileField profileField) {
    if (null == profileField
        || null == profileField.getProfileFieldId()
        || null == profileField.getProfileTemplateId()
        || null == profileField.getLogicalIndex()
        || null == profileField.getLastModifiedUserId()) {
      return false;
    }
    if (DataType.CONTAINER.getCode() != profileField.getDataType()
        && null == profileField.getContainerId()) {
      return false;
    }
    return true;
  }

  /**
   * Must satisfy requirements:
   *  1) both are valid sequence
   *  2) two list contain exactly the same fields
   *  3) all system-required fields remain the same relevant order
   *
   * @param currFields
   * @param newFields
   * @return
   */
  public static boolean isValidMove(
      List<ProfileField> currFields, List<ProfileField> newFields) {

    // 1)
    if (!isValidSequenceOfProfileField(currFields)
        || !isValidSequenceOfProfileField(newFields)) {
      return false;
    }

    Map<Long, ProfileField> currFieldMap = new HashMap<>();
    List<ProfileField> currSystemFieldList = new ArrayList<>();
    List<ProfileField> newSystemFieldList = new ArrayList<>();
    for (ProfileField currField: currFields) {
      currFieldMap.put(currField.getProfileFieldId(), currField);
      if (currField.getIsSystemRequired() == 1) {
        currSystemFieldList.add(currField);
      }
    }
    for (ProfileField newPF: newFields) {
      if (newPF.getIsSystemRequired() == 1) {
        newSystemFieldList.add(newPF);
      }
    }

    // 2)
    if (CollectionUtils.size(currFields) != CollectionUtils.size(newFields)) {
      return false;
    }

    for (ProfileField newField: newFields) {
      if (!currFieldMap.containsKey(newField.getProfileFieldId())) {
        return false;
      }
    }

    // 3)
    if (currSystemFieldList.size() != newSystemFieldList.size()) {
      return false;
    }

    for (int i = 0; i < newSystemFieldList.size(); i++) {
      if (!LongUtils.equals(currSystemFieldList.get(i).getProfileFieldId(),
              newSystemFieldList.get(i).getProfileFieldId())) {
        return false;
      }
    }

    return true;
  }

  /**
   * Checkpoints:
   *  1) either is empty
   *  2) containing relation and list have same field count
   *  3) each data field has a container
   *
   * @param profileFields
   * @param relations
   * @return
   */
  public static boolean isValidSequenceOfPresetProfileField(
      List<ProfileField> profileFields, Map<String, String> relations) {

    // 1)
    if (CollectionUtils.isEmpty(profileFields)
        || MapUtils.isEmpty(relations)) {
      return false;
    }

    // 2)
    Set<String> fieldReferenceNameInRelations = new HashSet<>();
    for (Map.Entry<String, String> entry: relations.entrySet()) {
      fieldReferenceNameInRelations.add(entry.getKey());
      fieldReferenceNameInRelations.add(entry.getValue());
    }

    // 3)
    for (ProfileField profileField: profileFields) {
      if (profileField.getDataType().intValue() != DataType.CONTAINER.getCode()) {
        if (!relations.containsKey(profileField.getReferenceName())) {
          return false;
        }
      }
    }

    return true;
  }

    /**
     * Must satisfy requirements:
     *  1) non-empty
     *  2) list-index equals field.logicalIndex
     *  3) every data field belongs to the specific container field in order
     *  4) no customized container field before any system-required container field
     *  5) within each container field, no customized data field before any system-required data field
     *
     * @param profileFields
     * @return
     */
  public static boolean isValidSequenceOfProfileField(List<ProfileField> profileFields) {

    // 1
    if (CollectionUtils.isEmpty(profileFields)) {
      return false;
    }

    // 2)
    for (int i = 0; i < profileFields.size(); i++) {
      if (i != profileFields.get(i).getLogicalIndex()) {
        return false;
      }
    }

    // 3)
    long currContainerId = -1;
    for (int i = 0; i < profileFields.size(); i++) {
      ProfileField profileField = profileFields.get(i);
      DataType dataType = DataType.getEnumByCode(profileField.getDataType());
      if (dataType == DataType.CONTAINER) {
        currContainerId = profileField.getProfileFieldId();
      } else {
        if (profileField.getContainerId() != currContainerId) {
          return false;
        }
      }
    }

    // 4)
    int maxSystemContainerIndex = Integer.MIN_VALUE;
    int minCustomContainerIndex = Integer.MAX_VALUE;
    for (int i = 0; i < profileFields.size(); i++) {
      ProfileField profileField = profileFields.get(i);
      if (DataType.CONTAINER.getCode() == profileField.getDataType()) {
        if (profileField.getIsSystemRequired() == 1) {
          maxSystemContainerIndex = i;
        } else if (i < minCustomContainerIndex) {
          minCustomContainerIndex = i;
        }
      }
    }
    if (minCustomContainerIndex < maxSystemContainerIndex) {
      return false;
    }

    // 5)

    currContainerId = 0;
    for (int i = 0; i < profileFields.size(); i++) {
      ProfileField profileField = profileFields.get(i);
      DataType dataType = DataType.getEnumByCode(profileField.getDataType());
      if (dataType == DataType.CONTAINER) {
        currContainerId = profileField.getProfileFieldId();
      } else {
        if (profileField.getContainerId().equals(currContainerId)
            && profileField.getIsSystemRequired() == 0
            && (i + 1 < profileFields.size()
                && null != profileFields.get(i + 1).getContainerId()
                && profileFields.get(i + 1).getContainerId().equals(currContainerId)
                && profileFields.get(i + 1).getIsSystemRequired() == 1)) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Must satisfy requirements:
   *  1) each pickOption is valid itself
   *  2) each optionIndex equals to list index
   *  3) optionValues should be unique
   *  4) SINGLE_PICK has one default option at most
   * @param pickOptions
   */
  public static boolean isValidSequenceOfPickOption(List<PickOption> pickOptions, DataType dataType) {

    LOGGER.info("POSeq=" + pickOptions);

    if (CollectionUtils.isEmpty(pickOptions)) {
      return false;
    }

    int defaultCount = 0;
    Set<String> optionValues = new HashSet<>();
    for (int i = 0; i < pickOptions.size(); i++) {
      PickOption pickOption = pickOptions.get(i);
      // 1)
      if (null == pickOption.getPickOptionId()) {
        if (!isValidAddPickOptionRequest(pickOption)) {
          return false;
        }
      } else {
        if (!isValidUpdatePickOptionRequest(pickOption)) {
          return false;
        }
      }
      // 2)
      if (pickOption.getOptionIndex() != i) {
        return false;
      }
      optionValues.add(pickOption.getOptionValue());
      if (pickOption.getIsDefault() == 1) {
        defaultCount ++;
      }
    }

    //3)
    if (optionValues.size() < pickOptions.size()) {
      return false;
    }

    // 4)
    if (defaultCount > 1
        && dataType == DataType.SINGLE_PICK) {
      return false;
    }

    return true;
  }

  public static boolean isValidAddPickOptionRequest(PickOption pickOption) {
    if (null == pickOption
        || null == pickOption.getOrgId()
        || null == pickOption.getProfileFieldId()
        || !isValidOptionValue(pickOption.getOptionValue())
        || null == pickOption.getOptionIndex()
        || null == pickOption.getIsDefault()
        || null == pickOption.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidUpdatePickOptionRequest(PickOption pickOption) {
    if (null == pickOption
        || null == pickOption.getPickOptionId()
        || null == pickOption.getOrgId()
        || null == pickOption.getProfileFieldId()
        || !isValidOptionValue(pickOption.getOptionValue())
        || null == pickOption.getOptionIndex()
        || null == pickOption.getIsDefault()
        || null == pickOption.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }


  public static void setFlagForCustomContainerProfileField(ProfileField profileField) {
    profileField.setIsTypeSpecEditable(0);
    profileField.setIsSystemRequired(0);
    profileField.setIsOnboardingStaffEditable(0);
    profileField.setIsActiveStaffEditable(0);
    profileField.setIsPublicVisibleEditable(1);
    profileField.setIsEnabledEditable(1);
    profileField.setIsMandatoryEditable(1);
  }

  public static void setFlagForCustomDataProfileField(ProfileField profileField) {
    DataType dataType = DataType.getEnumByCode(profileField.getDataType());
    if (dataType == DataType.SINGLE_PICK
        || dataType == DataType.MULTI_PICK) {
      profileField.setIsTypeSpecEditable(1);
    } else {
      profileField.setIsTypeSpecEditable(0);
    }
    profileField.setIsSystemRequired(0);
    profileField.setIsOnboardingStaffEditable(1);
    profileField.setIsActiveStaffEditable(1);
    profileField.setIsPublicVisibleEditable(1);
    profileField.setIsEnabledEditable(1);
    profileField.setIsMandatoryEditable(1);
  }

  public static void setDefaultTypeSpecForCustomDataProfileField(ProfileField profileField) {

    DataType dataType = DataType.getEnumByCode(profileField.getDataType());
    if (dataType == DataType.SHORT_TEXT) {
      JSONObject typeSpec = new JSONObject();
      typeSpec.put("maxLength", TypeSpecConsts.MAX_LENGTH_OF_STXT_VALUE);
      profileField.setTypeSpec(typeSpec);
    }
    if (dataType == DataType.LONG_TEXT) {
      JSONObject typeSpec = new JSONObject();
      typeSpec.put("maxLength", TypeSpecConsts.MAX_LENGTH_OF_LTXT_VALUE);
      profileField.setTypeSpec(typeSpec);
    }
    if (dataType == DataType.BLOCK_TEXT) {
      JSONObject typeSpec = new JSONObject();
      typeSpec.put("maxLength", TypeSpecConsts.MAX_LENGTH_OF_BTXT_VALUE);
      profileField.setTypeSpec(typeSpec);
    }

  }

  public static List<PickOption> listPickOptionToAddUponUpdateField(
      List<PickOption> currPickOptions, List<PickOption> newPickOptions) {

    List<PickOption> pickOptionsToAdd = new ArrayList<>();
    for (int i = 0; i < newPickOptions.size(); i++) {
      PickOption newPickOption = newPickOptions.get(i);
      if (null == newPickOption.getPickOptionId()) {
        pickOptionsToAdd.add(newPickOption);
      }
    }

    return pickOptionsToAdd;
  }

  public static List<Long> listPickOptionIdsToDeprecatedUponUpdateField(
      List<PickOption> currPickOptions, List<PickOption> newPickOptions) {

    Set<Long> newPickOptionIds = new HashSet<>();
    for (PickOption newPickOption: newPickOptions) {
      if (null != newPickOption.getPickOptionId()) {
        newPickOptionIds.add(newPickOption.getPickOptionId());
      }
    }

    List<Long> pickOptionIdsToDeprecate = new ArrayList<>();
    for (int i = 0; i < currPickOptions.size(); i++) {
      PickOption currPickOption = currPickOptions.get(i);
      if (currPickOption.getIsDeprecated() == 0
          && !newPickOptionIds.contains(currPickOption.getPickOptionId())) {
        pickOptionIdsToDeprecate.add(currPickOption.getPickOptionId());
      }
    }

    return pickOptionIdsToDeprecate;
  }

  public static List<PickOption> listPickOptionToUpdateUponUpdateField(
      List<PickOption> currPickOptions, List<PickOption> newPickOptions) {


    Set<Long> newPickOptionIds = new HashSet<>();
    for (PickOption newPickOption: newPickOptions) {
      if (null != newPickOption.getPickOptionId()) {
        newPickOptionIds.add(newPickOption.getPickOptionId());
      }
    }

    List<PickOption> pickOptionsToUpdate = new ArrayList<>();
    for (int i = 0; i < currPickOptions.size(); i++) {
      PickOption currPickOption = currPickOptions.get(i);
      if (currPickOption.getIsDeprecated() == 0
          && newPickOptionIds.contains(currPickOption.getPickOptionId())) {
        pickOptionsToUpdate.add(currPickOption);
      }
    }

    return pickOptionsToUpdate;
  }

  public static boolean isValidUpdateUserProfileConfigRequest(UserProfileConfig userProfileConfig) {
    if (null == userProfileConfig
        || null == userProfileConfig.getOrgId()
        || null == SystemProfileField.getEnumByReferenceName(userProfileConfig.getReferenceName())
        || (null == userProfileConfig.getIsEnabled()
            || !BooleanUtils.isValidBooleanValue(userProfileConfig.getIsEnabled()))
        || null == userProfileConfig.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }

  private static boolean isValidFieldDisplayName(String displayName) {
    if (!StringUtils.isValidVarchar100(displayName)
        || displayName.contains(" ")
        || displayName.contains("_")) {
      return false;
    }
    return true;
  }

  public static boolean isValidOptionValue(String optionValue) {
    if (StringUtils.isNullOrEmpty(optionValue)
        || optionValue.contains("/")
        || optionValue.contains("#WZ#")) {
      return false;
    }
    return true;
  }



  public static void main(String[] args) {

  }

}
