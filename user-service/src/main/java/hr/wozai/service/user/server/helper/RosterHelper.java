// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.helper;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import hr.wozai.service.servicecommons.commons.consts.TypeSpecConsts;
import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.utils.LongUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.enums.UserDegreeLevel;
import hr.wozai.service.user.client.userorg.enums.UserEmploymentProfileField;
import hr.wozai.service.user.client.userorg.enums.UserGender;
import hr.wozai.service.user.client.userorg.enums.UserMaritalStatus;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.OrgPickOption;
import hr.wozai.service.user.server.model.userorg.PickOption;
import hr.wozai.service.user.server.model.userorg.ProfileField;
import hr.wozai.service.user.server.model.userorg.UserEmployment;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-08-25
 */
public class RosterHelper {

  private static Logger LOGGER = LoggerFactory.getLogger(RosterHelper.class);

  private static final String MULTI_PICK_SEPERATOR = "#WZ#";


  /**
   * Should match all conditions:
   *  1) no dup referenceName
   *  2) referenceNames in fields of CUP or BUP or MUP or UserEmploymentProfileField
   *  3) each required field is enabled
   *  4) each field should not be (isSystemRequired == 1 && isMandatory == 1)
   *
   * @param referenceNames
   * @param profileFields
   * @return
   */
  @LogAround
  public static boolean isValidRequestOfRosterFile(List<String> referenceNames, List<ProfileField> profileFields) {

    if (CollectionUtils.isEmpty(referenceNames)
        || CollectionUtils.isEmpty(profileFields)) {
      LOGGER.error("isValidRequestOfRosterFile(): err 0, quick fail");
      return false;
    }

    Map<String, ProfileField> profileFieldMap = new HashMap<>();
    for (ProfileField profileField: profileFields) {
      profileFieldMap.put(profileField.getReferenceName(), profileField);
    }

    Set<String> referenceNameSet = new HashSet<>();
    for (String referenceName: referenceNames) {
      // 1)
      if (referenceNameSet.contains(referenceName)) {
        LOGGER.error("isValidRequestOfRosterFile(): err 1, referenceName={}", referenceName);
        return false;
      }
      referenceNameSet.add(referenceName);
      // 2)
      if (!profileFieldMap.containsKey(referenceName)
          && null == UserEmploymentProfileField.getEnumByReferenceName(referenceName)) {
        LOGGER.error("isValidRequestOfRosterFile(): err 2, referenceName={}", referenceName);
        return false;
      }
      if (profileFieldMap.containsKey(referenceName)) {
        ProfileField profileField = profileFieldMap.get(referenceName);
        // 3)
        if (profileField.getIsEnabled() == 0) {
          LOGGER.error("isValidRequestOfRosterFile(): err 3, referenceName={}", referenceName);
          return false;
        }
        // 4)
        if (profileField.getIsSystemRequired() == 1
            && profileField.getIsMandatory() == 1) {
          LOGGER.error("isValidRequestOfRosterFile(): err 4, referenceName={}", referenceName);
          return false;
        }
      }
    }

    return true;
  }

  /**
   * For now, just handle 3 fields: contractType, enrollDate, resignDate
   *
   * @param referenceNames
   * @param header
   */
  public static void fillUserEmploymentProfileFieldInHeaderOfRosterFile(
      List<String> referenceNames, StringBuilder header) {

    if (CollectionUtils.isEmpty(referenceNames)
        || null == header) {
      return;
    }

    for (String referenceName: referenceNames) {
      if (UserEmploymentProfileField.CONTRACT_TYPE.getReferenceName().equals(referenceName)) {
        header.append(", " + UserEmploymentProfileField.CONTRACT_TYPE.getCsvColumnName());
        header.append(" (选填) (单选类型) (选项: 全职/兼职/实习)");
      } else if (UserEmploymentProfileField.ENROLL_DATE.getReferenceName().equals(referenceName)) {
        header.append(", " + UserEmploymentProfileField.ENROLL_DATE.getCsvColumnName());
        header.append(" (选填) (日期类型) (例: 2016/03/01)");
      } else if (UserEmploymentProfileField.RESIGN_DATE.getReferenceName().equals(referenceName)) {
        header.append(", " + UserEmploymentProfileField.RESIGN_DATE.getCsvColumnName());
        header.append(" (选填) (日期类型) (例: 2016/03/01)");
      }
    }

  }

  /**
   * Handle CUP + BUP:
   *  1) handle field's displayName
   *  2) handle field's isMandatory
   *  3) handle field's dataType
   *  4) handle field's optional values (special cases: jobTitle & jobLevel & enums)
   *
   * @param referenceNames
   * @param profileFields
   * @param jobTitles
   * @param jobLevels
   * @param header
   */
  public static void fillSystemProfileFieldInHeaderOfRosterFile(
      List<String> referenceNames, List<ProfileField> profileFields,
      List<OrgPickOption> jobTitles, List<OrgPickOption> jobLevels, StringBuilder header) {

    if (CollectionUtils.isEmpty(referenceNames)
        || CollectionUtils.isEmpty(profileFields)
        || null == header) {
      return;
    }

    Map<String, ProfileField> profileFieldMap = new HashMap<>();
    for (ProfileField profileField: profileFields) {
      profileFieldMap.put(profileField.getReferenceName(), profileField);
    }

    for (String referenceName: referenceNames) {

      ProfileField profileField = profileFieldMap.get(referenceName);
      SystemProfileField systemProfileField = SystemProfileField.getEnumByReferenceName(referenceName);

      // 1)
      header.append(", " + systemProfileField.getCsvColumnName());

      // 2)
      appendIsMandatoryOfFieldInHeaderOfRosterFile(profileField, header);

      // 3)
      appendDataTypeOfFieldInHeaderOfRosterFile(profileField, header);

      // 4)
      if (SystemProfileField.JOB_TITLE.getReferenceName().equals(referenceName)) {
        appendValuesOfOrgPickOptionFieldInHeaderOfRosterFile(jobTitles, header);
      } else if (SystemProfileField.JOB_LEVEL.getReferenceName().equals(referenceName)) {
        appendValuesOfOrgPickOptionFieldInHeaderOfRosterFile(jobLevels, header);
      } else {
        appendValuesOfSystemFieldInHeaderOfRosterFile(profileField, header);
      }
    }

  }

  /**
   * Handle MUP:
   *  1) handle field's displayName
   *  2) handle field's isMandatory
   *  3) handle field's dataType
   *  4) handle field's optional values of SINGLE_PICK & MULTI_PICK
   *
   * @param referenceNames
   * @param profileFields
   * @param pickOptionMap
   * @param header
   */
  public static void fillCustomizedProfileFieldInHeaderOfRosterFile(
      List<String> referenceNames, List<ProfileField> profileFields,
      Map<Long, List<PickOption>> pickOptionMap, StringBuilder header) {

    if (CollectionUtils.isEmpty(referenceNames)
        || CollectionUtils.isEmpty(profileFields)
        || null == header) {
      return;
    }

    LOGGER.info("fillCustomizedProfileFieldInHeaderOfRosterFile(): referenceNames={}", referenceNames);

    Map<String, ProfileField> profileFieldReferenceMap = new HashMap<>();
    Map<Long, ProfileField> profileFieldIdMap = new HashMap<>();
    for (ProfileField profileField: profileFields) {
      profileFieldReferenceMap.put(profileField.getReferenceName(), profileField);
      profileFieldIdMap.put(profileField.getProfileFieldId(), profileField);
    }

    for (String referenceName: referenceNames) {

      SystemProfileField systemProfileField = SystemProfileField.getEnumByReferenceName(referenceName);
      ProfileField profileField = profileFieldReferenceMap.get(referenceName);
      DataType dataType = DataType.getEnumByCode(profileField.getDataType());
      if (DataType.CONTAINER.equals(dataType)) {
        continue;
      }

      // 1)
      String containerDisplayName = profileFieldIdMap.get(profileField.getContainerId()).getDisplayName();
      String csvDisplayName = containerDisplayName + "_" + profileField.getDisplayName();
      header.append(", " + csvDisplayName);

      // 2)
      appendIsMandatoryOfFieldInHeaderOfRosterFile(profileField, header);

      // 3)
      appendDataTypeOfFieldInHeaderOfRosterFile(profileField, header);

      // 4)
      if (DataType.SINGLE_PICK.equals(dataType)
          || DataType.MULTI_PICK.equals(dataType)) {
        List<PickOption> pickOptions = pickOptionMap.get(profileField.getProfileFieldId());
        appendValuesOfPickOptionFieldInHeaderOfRosterFile(pickOptions, header);
      }
    }

  }

  /**
   * Steps:
   *  1) parse UserEmploymentProfileField
   *  2) parse SystemProfileField
   *  3) parse CustomizedProfileField
   *
   * Note: fill null for column not matched
   *
   * @param header
   * @param profileFields
   */
  public static List<String> convertRosterHeaderToReferenceNames(List<String> header, List<ProfileField> profileFields) {

    if (CollectionUtils.isEmpty(header)
        || CollectionUtils.isEmpty(profileFields)) {
      return Collections.EMPTY_LIST;
    }

    StringUtils.trimStringInList(header);
    List<String> referenceNames = new ArrayList<>();
    Map<Long, ProfileField> customizedProfileFieldIdMap = new HashMap<>();
    Map<String, ProfileField> customizedProfileFieldDisplayNameMap = new HashMap<>();
    for (ProfileField profileField : profileFields) {
      if (profileField.getIsSystemRequired() == 0) {
        customizedProfileFieldIdMap.put(profileField.getProfileFieldId(), profileField);
        customizedProfileFieldDisplayNameMap.put(profileField.getDisplayName(), profileField);
      }
    }

    for (String columnName : header) {
      boolean isColumnNameMatched = false;
      if (isValidColumnNameFormat(columnName)) {
        String containerDisplayName = parseContainerDisplayName(columnName);
        String dataDisplayName = parseDataDisplayName(columnName);
        // 1) & 2)
        if (StringUtils.isNullOrEmpty(containerDisplayName)) {
          if (null != getUserEmploymentProfileFieldByDisplayName(dataDisplayName)) {
            referenceNames.add(getUserEmploymentProfileFieldByDisplayName(dataDisplayName).getReferenceName());
            isColumnNameMatched = true;
          } else if (null != getSystemProfileField(dataDisplayName)) {
            referenceNames.add(getSystemProfileField(dataDisplayName).getReferenceName());
            isColumnNameMatched = true;
          }
        } else {
          // 3)
          ProfileField containerProfileField = customizedProfileFieldDisplayNameMap.get(containerDisplayName);
          ProfileField dataProfileField = customizedProfileFieldDisplayNameMap.get(dataDisplayName);
          LOGGER.info("convertRosterHeaderToReferenceNames(): columnName={}, ctn={}, data={}, 1={}, 2={}",
                      columnName, containerDisplayName, dataDisplayName, containerProfileField, dataProfileField);
          if (null != containerProfileField
              && null != dataProfileField
              && null != dataProfileField.getContainerId()
              && dataProfileField.getContainerId().equals(containerProfileField.getProfileFieldId())) {
            referenceNames.add(dataProfileField.getReferenceName());
            isColumnNameMatched = true;
          }
        }
      }
      if (!isColumnNameMatched) {
        referenceNames.add(null);
      }
    }


    return referenceNames;
  }

  /**
   * CheckPoints:
   *  1) header and line not empty
   *  2) first two lines are fullName & emailAddress, and at least one data column
   *  3) header and each line has same column count
   *
   * @param referenceNames
   * @param rawFieldValueList
   * @return
   */
  public static ValidationCheckResult validateRosterFileFormat(
      List<String> referenceNames, List<List<String>> rawFieldValueList) {

    ValidationCheckResult result = new ValidationCheckResult();
    StringBuilder errorMessageOfThisLine = new StringBuilder();

    // 1)
    if (CollectionUtils.isEmpty(referenceNames)
        || CollectionUtils.isEmpty(rawFieldValueList)) {
      errorMessageOfThisLine.append("\n\n-------------------- 空行检测 --------------------\n\n");
      result.setNoError(false);
      errorMessageOfThisLine.append("\n行数据不可为空");
    }

    // 2)
    if (result.isNoError()) {
      if (referenceNames.size() < 3
          || !SystemProfileField.FULL_NAME.getReferenceName().equals(referenceNames.get(0))
          || !SystemProfileField.EMAIL_ADDRESS.getReferenceName().equals(referenceNames.get(1))) {
        errorMessageOfThisLine.append("\n\n-------------------- 表头检测 --------------------\n\n");
        result.setNoError(false);
        errorMessageOfThisLine.append("前两列应为 (姓名) + (邮箱), 且至少包含一个其他数据列");
      }
    }

    // 3)
    if (result.isNoError()) {
      errorMessageOfThisLine.append("\n\n-------------------- 列数检测 --------------------\n\n");
      int columnSize = referenceNames.size();
      for (int i = 0; i < rawFieldValueList.size(); i++) {
        if (rawFieldValueList.get(i).size() != columnSize) {
          result.setNoError(false);
          errorMessageOfThisLine.append("\n第 " + (i + 1) + " 行: 列数有误, 应为" + columnSize);
        }
      }
    }

    result.setErrorMessage(errorMessageOfThisLine.toString());
    return result;
  }

  /**
   * CheckPoints:
   *  1) each column matches one field, and no dup column
   *  2) no dup emailAddress
   *  3) no user is not resigned
   *  4) emailAddress and fullName match
   *  5) UserEmploymentProfileField data ok
   *  6) SystemProfileField ok
   *  7) CustomizedProfileField ok
   *
   * @param referenceNames
   * @param rawFieldValueList
   * @param profileFields
   * @param jobTitles
   * @param jobLevels
   * @param pickOptionMap
   * @return
   */
  public static ValidationCheckResult validateRosterFileData(
      List<String> headers, List<String> referenceNames, List<List<String>> rawFieldValueList,
      List<ProfileField> profileFields, List<CoreUserProfile> coreUserProfiles,
      List<OrgPickOption> jobTitles, List<OrgPickOption> jobLevels, Map<Long, List<PickOption>> pickOptionMap) {

    LOGGER.info("validateRosterFileData(): headers={}, rn={}, rfvl={}, pfs={}, cups={}, jt={}, le={}, po={}",
                headers, referenceNames, rawFieldValueList, profileFields,
                coreUserProfiles, jobTitles, jobLevels, pickOptionMap);

    ValidationCheckResult result = new ValidationCheckResult();
    StringBuilder errMsg = new StringBuilder();

    // 1)
    Set<String> uniqueReferenceNames = new HashSet<>();
    errMsg.append("\n\n-------------------- 表头字段检测 --------------------\n\n");
    for (int i = 0; i < referenceNames.size(); i++) {
      if (StringUtils.isNullOrEmpty(referenceNames.get(i))) {
        errMsg.append("\n第" + (i + 1) + "列不存在: " + headers.get(i));
        result.setNoError(false);
      }
      if (uniqueReferenceNames.contains(referenceNames.get(i))) {
        errMsg.append("\n第" + (i + 1) + "列重复: " + headers.get(i));
        result.setNoError(false);
      } else {
        uniqueReferenceNames.add(referenceNames.get(i));
      }
    }

    // 2) & 3 & 4)
    errMsg.append("\n\n-------------------- 员工姓名邮箱检测 --------------------\n\n");
    boolean isFullNameAndEmailAddressOk =
        validateFullNameAndEmailAddressInRosterFile(errMsg, rawFieldValueList, coreUserProfiles);
    if (!isFullNameAndEmailAddressOk) {
      result.setNoError(false);
    }

    // 5) & 6) & 7)
    errMsg.append("\n\n-------------------- 花名册数据检测 --------------------\n\n");
    List<String> csvColumnNames = parseCsvColumnName(headers);
    for (int i = 0; i < rawFieldValueList.size(); i++) {
      List<String> fieldValues = rawFieldValueList.get(i);
      boolean isFieldValueOk = validateAndTranslateOneLineOfRosterData(
          errMsg, csvColumnNames, referenceNames, fieldValues, i, profileFields, jobTitles, jobLevels, pickOptionMap);
      if (!isFieldValueOk) {
        result.setNoError(false);
      }
      // TEST
      LOGGER.info("validateRosterFileData(): oneLineAfter={}", fieldValues);
    }

    result.setErrorMessage(errMsg.toString());
    return result;
  }

  public static Map<Long, UserEmployment> getUserEmploymentMapFromRosterFile(
      List<String> referenceNames, List<List<String>> translatedFieldValueList, List<CoreUserProfile> coreUserProfiles) {

    Map<Long, UserEmployment> userEmploymentMap = new HashMap<>();

    boolean hasUserEmploymentField = false;
    for (int i = 0; i < referenceNames.size(); i++) {
      for (UserEmploymentProfileField userEmploymentProfileField: UserEmploymentProfileField.values()) {
        if (userEmploymentProfileField.getReferenceName().equals(referenceNames.get(i))) {
          hasUserEmploymentField = true;
          break;
        }
      }
    }

    if (hasUserEmploymentField) {

      Map<String, Long> emailAddressAndUserIdMap = new HashMap<>();
      for (CoreUserProfile coreUserProfile: coreUserProfiles) {
        emailAddressAndUserIdMap.put(coreUserProfile.getEmailAddress(), coreUserProfile.getUserId());
      }

      for (int i = 0; i < translatedFieldValueList.size(); i++) {
        List<String> fieldValues = translatedFieldValueList.get(i);
        String emailAddress = fieldValues.get(1);
        long userId = emailAddressAndUserIdMap.get(emailAddress);
        UserEmployment userEmployment = new UserEmployment();
        userEmployment.setUserId(userId);
        ContractType contractType = null;
        Long enrollDate = null;
        Long resignDate = null;
        for (int j = 2; j < fieldValues.size(); j++) {
          String fieldValue = fieldValues.get(j);
          if (!StringUtils.isNullOrEmpty(fieldValue)) {
            if (UserEmploymentProfileField.CONTRACT_TYPE.getReferenceName().equals(referenceNames.get(j))) {
              contractType = ContractType.getEnumByCode(Integer.parseInt(fieldValue));
            } else if (UserEmploymentProfileField.ENROLL_DATE.getReferenceName().equals(referenceNames.get(j))) {
              enrollDate = Long.parseLong(fieldValue);
            } else if (UserEmploymentProfileField.RESIGN_DATE.getReferenceName().equals(referenceNames.get(j))) {
              resignDate = Long.parseLong(fieldValue);
            }
          }
        }
        if (null != contractType) {
          userEmployment.setContractType(contractType.getCode());
          UserEmploymentHelper.setContractTypeAndEnrollDateAndResignDate(
              userEmployment, contractType.getCode(), enrollDate, resignDate);
        }
        userEmploymentMap.put(userId, userEmployment);
      }

    }

    return userEmploymentMap;
  }

  public static Map<Long, Map<String, String>> getProfileFieldValueMapFromRosterFile(
      List<String> referenceNames, List<List<String>> translatedFieldValueList,
      List<ProfileField> profileFields, List<CoreUserProfile> coreUserProfiles) {

    Map<Long, Map<String, String>> profileFieldValueMap = new HashMap<>();

    Map<String, ProfileField> profileFieldMap = new HashMap<>();
    for (ProfileField profileField: profileFields) {
      profileFieldMap.put(profileField.getReferenceName(), profileField);
    }
    boolean hasProfileField = false;
    for (int i = 2; i < referenceNames.size(); i++) {
      if (null != profileFieldMap.get(referenceNames.get(i))) {
        hasProfileField = true;
        break;
      }
    }

    if (hasProfileField) {

      Map<String, Long> emailAddressAndUserIdMap = new HashMap<>();
      for (CoreUserProfile coreUserProfile: coreUserProfiles) {
        emailAddressAndUserIdMap.put(coreUserProfile.getEmailAddress(), coreUserProfile.getUserId());
      }

      for (int i = 0; i < translatedFieldValueList.size(); i++) {
        List<String> fieldValues = translatedFieldValueList.get(i);
        String emailAddress = fieldValues.get(1);
        long userId = emailAddressAndUserIdMap.get(emailAddress);
        Map<String, String> referenceNameAndValueMap = new HashMap<>();
        for (int j = 2; j < fieldValues.size(); j++) {
          String referenceName = referenceNames.get(j);
          if (null != profileFieldMap.get(referenceName)) {
            referenceNameAndValueMap.put(referenceName, fieldValues.get(j));
          }
        }
        profileFieldValueMap.put(userId, referenceNameAndValueMap);
      }

    }

    return profileFieldValueMap;
  }

  private static void appendIsMandatoryOfFieldInHeaderOfRosterFile(ProfileField profileField, StringBuilder header) {
    if (profileField.getIsMandatory() == 1) {
      header.append(" (必填)");
    } else {
      header.append(" (选填)");
    }
  }

  private static void appendDataTypeOfFieldInHeaderOfRosterFile(ProfileField profileField, StringBuilder header) {
    if (DataType.SINGLE_PICK.getCode() == profileField.getDataType()) {
      header.append(" (单选类型)");
    } else if (DataType.MULTI_PICK.getCode() == profileField.getDataType()) {
      header.append(" (多选类型)");
    } else if (DataType.DATETIME.getCode() == profileField.getDataType()) {
      header.append(" (日期类型)");
    } else if (DataType.ADDRESS.getCode() == profileField.getDataType()) {
      header.append(" (地址类型)");
    }
  }

  private static void appendValuesOfSystemFieldInHeaderOfRosterFile(ProfileField profileField, StringBuilder header) {
    String referenceName = profileField.getReferenceName();
    DataType dataType = DataType.getEnumByCode(profileField.getDataType());
    if (SystemProfileField.GENDER.getReferenceName().equals(referenceName)) {
      header.append(" (取值: ");
      UserGender[] userGenders = UserGender.values();
      for (int i = 0; i < userGenders.length - 1; i++) {
        header.append(userGenders[i].getDesc() + "/");
      }
      header.append(userGenders[userGenders.length - 1].getDesc());
      header.append(")");
    } else if (SystemProfileField.DEGREE_LEVEL.getReferenceName().equals(referenceName)) {
      header.append(" (取值: ");
      UserDegreeLevel[] degreeLevels = UserDegreeLevel.values();
      for (int i = 0; i < degreeLevels.length - 1; i++) {
        header.append(degreeLevels[i].getDesc() + "/");
      }
      header.append(degreeLevels[degreeLevels.length - 1].getDesc());
      header.append(")");
    } else if (SystemProfileField.MARITAL_STATUS.getReferenceName().equals(referenceName)) {
      header.append(" (取值: ");
      UserMaritalStatus[] maritalStatuses = UserMaritalStatus.values();
      for (int i = 0; i < maritalStatuses.length - 1; i++) {
        header.append(maritalStatuses[i].getDesc() + "/");
      }
      header.append(maritalStatuses[maritalStatuses.length - 1].getDesc());
      header.append(")");
    } else if (dataType.equals(DataType.DATETIME)) {
      header.append(" (例: 2016/03/01)");
    }
  }

  private static void appendValuesOfOrgPickOptionFieldInHeaderOfRosterFile(
      List<OrgPickOption> orgPickOptions, StringBuilder header) {
    header.append(" (取值: ");
    for (int i = 0; i < orgPickOptions.size() - 1; i++) {
      header.append(orgPickOptions.get(i).getOptionValue() + "/");
    }
    header.append(orgPickOptions.get(orgPickOptions.size() - 1).getOptionValue());
    header.append(")");
  }

  private static void appendValuesOfPickOptionFieldInHeaderOfRosterFile(
      List<PickOption> pickOptions, StringBuilder header) {
    header.append(" (取值: ");
    for (int i = 0; i < pickOptions.size() - 1; i++) {
      header.append(pickOptions.get(i).getOptionValue() + "/");
    }
    header.append(pickOptions.get(pickOptions.size() - 1).getOptionValue());
    header.append(")");
  }

  private static boolean isValidColumnNameFormat(String columnName) {
    if (StringUtils.isNullOrEmpty(columnName)
        || columnName.split(" ").length == 0
        || columnName.split(" ")[0].split("_").length == 0) {
      return false;
    }
    String[] names = columnName.split(" ")[0].split("_");
    if (names.length == 0
        || names.length > 2
        || StringUtils.isNullOrEmpty(names[0])
        || (names.length == 2
            && StringUtils.isNullOrEmpty(names[1]))) {
      return false;
    }
    return true;
  }

  private static String parseCsvColumnName(String column) {
    return column.split(" ")[0];
  }

  private static List<String> parseCsvColumnName(List<String> headers) {
    List<String> csvColumnNames = new ArrayList<>();
    if (!CollectionUtils.isEmpty(headers)) {
      for (String header: headers) {
        csvColumnNames.add(parseCsvColumnName(header));
      }
    }
    return csvColumnNames;
  }

  private static String parseContainerDisplayName(String column) {
    String[] names = column.split(" ")[0].split("_");
    if (names.length == 2) {
      return names[0];
    }
    return null;
  }

  private static String parseDataDisplayName(String column) {
    String[] names = column.split(" ")[0].split("_");
    if (names.length == 2) {
      return names[1];
    }
    return names[0];
  }

  private static UserEmploymentProfileField getUserEmploymentProfileFieldByDisplayName(String displayName) {
    for (UserEmploymentProfileField userEmploymentProfileField: UserEmploymentProfileField.values()) {
      if (userEmploymentProfileField.getCsvColumnName().equals(displayName)) {
        return userEmploymentProfileField;
      }
    }
    return null;
  }

  private static SystemProfileField getSystemProfileField(String displayName) {
    for (SystemProfileField systemProfileField: SystemProfileField.values()) {
      if (systemProfileField.getCsvColumnName().equals(displayName)) {
        return systemProfileField;
      }
    }
    return null;
  }

  /**
   * Check points:
   *  1) no dup emailAddress
   *  2) no user is not resigned
   *  3) emailAddress and fullName match
   *
   * @param errMsg
   * @param rawFieldValueList
   * @param coreUserProfiles
   * @return
   */
  private static boolean validateFullNameAndEmailAddressInRosterFile(
      StringBuilder errMsg, List<List<String>> rawFieldValueList, List<CoreUserProfile> coreUserProfiles) {

    LOGGER.info("validateFullNameAndEmailAddressInRosterFile(): rawFieldValueList={}, cups={}",
                rawFieldValueList, coreUserProfiles);

    boolean isOk = true;
    Set<String> uniqueEmailAddresses = new HashSet<>();
    Map<String, String> emailAddressAndFullNameMap = new HashMap<>();
    for (CoreUserProfile coreUserProfile: coreUserProfiles) {
      emailAddressAndFullNameMap.put(coreUserProfile.getEmailAddress(), coreUserProfile.getFullName());
    }
    for (int i = 0; i < rawFieldValueList.size(); i++) {
      StringBuilder errMsgOfCurrLine = new StringBuilder("\n第" + (i + 1) + "行: ");
      List<String> fieldValues = rawFieldValueList.get(i);
      String fullName = fieldValues.get(0);
      String emailAddress = fieldValues.get(1);
      boolean thisLineOk = true;

      // 1)
      if (uniqueEmailAddresses.contains(emailAddress)) {
        errMsg.append("邮箱<" + emailAddress + ">在文件中不可重复, ");
        thisLineOk = false;
      }
      uniqueEmailAddresses.add(emailAddress);
      // 2)
      if (null == emailAddressAndFullNameMap.get(emailAddress)) {
        errMsg.append("邮箱<" + emailAddress + ">未注册, ");
        thisLineOk = false;
      } else {
        uniqueEmailAddresses.add(emailAddress);
      }
      // 3)
      String fullNameInSystem = emailAddressAndFullNameMap.get(emailAddress);
      if (StringUtils.isNullOrEmpty(fullName)
          || !fullName.equals(fullNameInSystem)) {
        errMsg.append("邮箱<" + emailAddress + ">和员工姓名<" + fullName + ">不符合, ");
        thisLineOk = false;
      }

      if (thisLineOk) {
        errMsgOfCurrLine.append("检测通过");
      } else {
        isOk = false;
      }
      errMsg.append(errMsgOfCurrLine.toString());
    }

    return isOk;
  }

  /**
   * Check points:
   *  1) UserEmploymentProfileField data ok
   *  2) SystemProfileField ok
   *  3) CustomizedProfileField ok
   *
   * @param errMsg
   * @param columnDisplayNames
   * @param referenceNames
   * @param profileFields
   * @param jobTitles
   * @param jobLevels
   * @param pickOptionMap
   * @return
   */
  private static boolean validateAndTranslateOneLineOfRosterData(
      StringBuilder errMsg, List<String> columnDisplayNames, List<String> referenceNames, List<String> fieldValues,
      int lineIndex, List<ProfileField> profileFields, List<OrgPickOption> jobTitles, List<OrgPickOption> jobLevels,
      Map<Long, List<PickOption>> pickOptionMap) {

    Map<String, ProfileField> profileFieldMap = new HashMap<>();
    for (ProfileField profileField: profileFields) {
      profileFieldMap.put(profileField.getReferenceName(), profileField);
    }

    boolean isOk = true;
    StringBuilder errMsgOfCurrLine = new StringBuilder("\n第" + (lineIndex + 1) + "行: ");
    for (int i = 2; i < fieldValues.size(); i++) {
      String referenceName = referenceNames.get(i);
      String fieldValue = fieldValues.get(i);

      UserEmploymentProfileField userEmploymentProfileField =
          UserEmploymentProfileField.getEnumByReferenceName(referenceName);
      SystemProfileField systemProfileField = SystemProfileField.getEnumByReferenceName(referenceName);

      if (null != userEmploymentProfileField) {
        // 1)
        boolean isUserEmploymentProfileFieldOk =
            validateAndTranslateUserEmploymentProfileFieldValue(fieldValues, i, userEmploymentProfileField);
        if (!isUserEmploymentProfileFieldOk) {
          errMsgOfCurrLine.append("第" + (i + 1) + "列[" + columnDisplayNames.get(i) + "]数据有误, ");
          isOk = false;
        }
      } else if (null != systemProfileField) {
        // 2)
        ProfileField profileField = profileFieldMap.get(referenceName);
        String columnDisplayName = columnDisplayNames.get(i);
        boolean isSystemProfileFieldOk = validateAndTranslateSystemProfileFieldValue(
            errMsgOfCurrLine, columnDisplayName, fieldValues, i,
            systemProfileField, profileField, jobTitles, jobLevels);
        if (!isSystemProfileFieldOk) {
          isOk = false;
        }
      } else {
        // 3)
        ProfileField profileField = profileFieldMap.get(referenceName);
        List<PickOption> pickOptions = pickOptionMap.get(profileField.getProfileFieldId());
        boolean isStandardProfileFieldOk = validateAndTranslateProfileFieldValueOfStandardDataType(
            errMsgOfCurrLine, columnDisplayNames.get(i), fieldValues, i, profileField, pickOptions);
        if (!isStandardProfileFieldOk) {
          isOk = false;
        }
      }



    }
    if (isOk) {
      errMsgOfCurrLine.append("检测通过");
    }
    errMsg.append(errMsgOfCurrLine.toString());

    return isOk;
  }

  /**
   * Steps:
   *  1) allow null
   *  2) handle fields
   *
   * @param fieldValues
   * @param index
   * @param userEmploymentProfileField
   * @return
   */
  private static boolean validateAndTranslateUserEmploymentProfileFieldValue(
      List<String> fieldValues, int index, UserEmploymentProfileField userEmploymentProfileField) {
    String fieldValue = fieldValues.get(index);
    if (null == userEmploymentProfileField) {
      return false;
    }

    // 1)
    if (StringUtils.isNullOrEmpty(fieldValue)) {
      fieldValues.set(index, null);
      return true;
    }

    // 2)
    if (userEmploymentProfileField.equals(UserEmploymentProfileField.CONTRACT_TYPE)) {
      boolean isValueMatched = false;
      for (ContractType contractType: ContractType.values()) {
        if (contractType.getMsg().equals(fieldValue)) {
          isValueMatched = true;
          fieldValues.set(index, String.valueOf(contractType.getCode()));
          break;
        }
      }
      if (!isValueMatched) {
        return false;
      }
    } else if (userEmploymentProfileField.equals(UserEmploymentProfileField.ENROLL_DATE)) {
      if (!isValidCsvDateStringInFormat(fieldValue)) {
        return false;
      } else {
        fieldValues.set(index, String.valueOf(getFirstTimestampOfDateInBeijingTime(fieldValue)));
      }
    } else if (userEmploymentProfileField.equals(UserEmploymentProfileField.RESIGN_DATE)) {
      if (!isValidCsvDateStringInFormat(fieldValue)) {
        return false;
      } else {
        fieldValues.set(index, String.valueOf(getFirstTimestampOfDateInBeijingTime(fieldValue)));
      }
    } else {
      return false;
    }
    return true;
  }

  /**
   * Steps:
   *  0) handle non-mandatory & null-valued fields
   *  1) handle preset enums: gender, degreeLevel, maritalStatus
   *  2) handle jobTitle and jobLevel
   *  3) handle INT, DATETIME, TXT, SINGLE_PICK
   *
   * @param fieldValues
   * @param index
   * @param systemProfileField
   * @param jobTitles
   * @param jobLevels
   * @return
   */
  private static boolean validateAndTranslateSystemProfileFieldValue(
      StringBuilder errMsg, String columnDisplayName, List<String> fieldValues, int index,
      SystemProfileField systemProfileField, ProfileField profileField,
      List<OrgPickOption> jobTitles, List<OrgPickOption> jobLevels) {
    String fieldValue = fieldValues.get(index);
    if (null == systemProfileField) {
      return false;
    }

    // 0)
    if (profileField.getIsMandatory() == 1
        && StringUtils.isNullOrEmpty(fieldValue)) {
      errMsg.append(" [" + columnDisplayName + "]必填; ");
      return false;
    } else if ("".equals(fieldValue)) {
      fieldValues.set(index, null);
      return true;
    }

    // 1)
    if (systemProfileField.equals(SystemProfileField.GENDER)) {
      boolean isValueMatched = false;
      for (UserGender userGender: UserGender.values()) {
        if (userGender.getDesc().equals(fieldValue)) {
          isValueMatched = true;
          fieldValues.set(index, String.valueOf(userGender.getCode()));
          break;
        }
      }
      if (!isValueMatched) {
        errMsg.append(" [" + columnDisplayName + "]有误; ");
        return false;
      }
    } else if (systemProfileField.equals(SystemProfileField.DEGREE_LEVEL)) {
      boolean isValueMatched = false;
      for (UserDegreeLevel userDegreeLevel: UserDegreeLevel.values()) {
        if (userDegreeLevel.getDesc().equals(fieldValue)) {
          isValueMatched = true;
          fieldValues.set(index, String.valueOf(userDegreeLevel.getCode()));
          break;
        }
      }
      if (!isValueMatched) {
        errMsg.append(" [" + columnDisplayName + "]有误; ");
        return false;
      }
    } else if (systemProfileField.equals(SystemProfileField.MARITAL_STATUS)) {
      boolean isValueMatched = false;
      for (UserMaritalStatus userMaritalStatus: UserMaritalStatus.values()) {
        if (userMaritalStatus.getDesc().equals(fieldValue)) {
          isValueMatched = true;
          fieldValues.set(index, String.valueOf(userMaritalStatus.getCode()));
          break;
        }
      }
      if (!isValueMatched) {
        errMsg.append(" [" + columnDisplayName + "]有误; ");
        return false;
      }
    } else if (systemProfileField.equals(SystemProfileField.JOB_TITLE)) {
      // 2)
      boolean isValueMatched = false;
      for (OrgPickOption jobTitle: jobTitles) {
        if (jobTitle.getIsDeprecated() == 0
            && jobTitle.getOptionValue().equals(fieldValue)) {
          isValueMatched = true;
          fieldValues.set(index, String.valueOf(jobTitle.getOrgPickOptionId()));
          break;
        }
      }
      if (!isValueMatched) {
        errMsg.append(" [" + columnDisplayName + "]有误; ");
        return false;
      }
    } else if (systemProfileField.equals(SystemProfileField.JOB_LEVEL)) {
      // 2)
      boolean isValueMatched = false;
      for (OrgPickOption jobLevel: jobLevels) {
        if (jobLevel.getIsDeprecated() == 0
            && jobLevel.getOptionValue().equals(fieldValue)) {
          isValueMatched = true;
          fieldValues.set(index, String.valueOf(jobLevel.getOrgPickOptionId()));
          break;
        }
      }
      if (!isValueMatched) {
        errMsg.append(" [" + columnDisplayName + "]有误; ");
        return false;
      }
    } else {
      boolean isValidValue = validateAndTranslateProfileFieldValueOfStandardDataType(
          errMsg, columnDisplayName, fieldValues, index, profileField, null);
      if (!isValidValue) {
        return false;
      }
    }

    return true;
  }

  private static boolean isValidCsvDateStringInFormat(String csvDateStr) {
    if (StringUtils.isNullOrEmpty(csvDateStr)) {
      return false;
    }
    String reFormatOne = "[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}";
    String reFormatTwo = "[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}";
    String reFormatThree = "[0-9]{4}.[0-9]{1,2}.[0-9]{1,2}";
    Pattern pOne = Pattern.compile(reFormatOne);
    Pattern pTwo = Pattern.compile(reFormatTwo);
    Pattern pThree = Pattern.compile(reFormatThree);
    return (pOne.matcher(csvDateStr).matches()
            || pTwo.matcher(csvDateStr).matches()
            || pThree.matcher(csvDateStr).matches());
  }

  private static long getFirstTimestampOfDateInBeijingTime(String csvDateStr) {
    String canonicalDateStr = convertFromCsvDateToCanonicalDate(csvDateStr);
    return TimeUtils.getTimestampOfZeroOclockTodayOfInputDateInBeijingTime(canonicalDateStr);
  }

  private static String convertFromCsvDateToCanonicalDate(String csvDateStr) {
    if (csvDateStr.contains("/")) {
      csvDateStr = csvDateStr.replace("/", "-");
    }
    if (csvDateStr.contains(".")) {
      csvDateStr = csvDateStr.replace(".", "-");
    }
    String[] bits = csvDateStr.split("-");
    String year = bits[0];
    String month = bits[1];
    if (month.length() == 1) {
      month = "0" + month;
    }
    String date = bits[2];
    if (date.length() == 1) {
      date = "0" + date;
    }
    return year + "-" + month + "-" + date;
  }

  /**
   * Checkpoints:
   *  1) if mandatory field has value
   *  2) validate and convert input against each dataType
   *
   * @param errorMessage
   * @param columnDisplayName
   * @param fieldValues
   * @param index
   * @param profileField
   * @param pickOptions
   * @return
   */
  private static boolean validateAndTranslateProfileFieldValueOfStandardDataType(
      StringBuilder errorMessage, String columnDisplayName, List<String> fieldValues, int index,
      ProfileField profileField, List<PickOption> pickOptions) {

    String fieldValue = fieldValues.get(index);
    boolean isFieldValueOk = true;

    // 1)
    if (profileField.getIsMandatory() == 1
        && StringUtils.isNullOrEmpty(fieldValue)) {
      errorMessage.append(" [" + columnDisplayName + "]必填; ");
      isFieldValueOk = false;
    } else if ("".equals(fieldValue)) {
      fieldValues.set(index, null);
      return true;
    }

    // 2)
    if (DataType.INTEGER.getCode() == profileField.getDataType()) {
      if (!StringUtils.isNullOrEmpty(fieldValue)
          && !LongUtils.isLong(fieldValue)) {
        errorMessage.append(" [" + columnDisplayName + "]有误; ");
        isFieldValueOk = false;
      }
    } else if (DataType.DECIMAL.getCode() == profileField.getDataType()) {
      if (!StringUtils.isNullOrEmpty(fieldValue)
          && !NumberUtils.isNumber(fieldValue)) {
        errorMessage.append(" [" + columnDisplayName + "]有误; ");
        isFieldValueOk = false;
      }
    } else if (DataType.DATETIME.getCode() == profileField.getDataType()) {
      // TEST
      LOGGER.info("fv={}, true={}, pf={}", fieldValue, isValidCsvDateStringInFormat(fieldValue), profileField);
      // check and convert
      if (!StringUtils.isNullOrEmpty(fieldValue)
          && !isValidCsvDateStringInFormat(fieldValue)) {
        errorMessage.append(" [" + columnDisplayName + "]有误 (例: 2016/12/31); ");
        isFieldValueOk = false;
      } else {
        fieldValues.set(index, String.valueOf(getFirstTimestampOfDateInBeijingTime(fieldValue)));
      }
    } else if (DataType.SHORT_TEXT.getCode() == profileField.getDataType()) {
      if (!StringUtils.isNullOrEmpty(fieldValue)
          && !StringUtils.isValidStringOfMaxLength(fieldValue, TypeSpecConsts.MAX_LENGTH_OF_STXT_VALUE)) {
        errorMessage.append(" [" + columnDisplayName + "]过长;");
        isFieldValueOk = false;
      }
    } else if (DataType.LONG_TEXT.getCode() == profileField.getDataType()) {
      if (!StringUtils.isNullOrEmpty(fieldValue)
          && !StringUtils.isValidStringOfMaxLength(fieldValue, TypeSpecConsts.MAX_LENGTH_OF_LTXT_VALUE)) {
        errorMessage.append(" [" + columnDisplayName + "]过长;");
        isFieldValueOk = false;
      }
//    } else if (DataType.BLOCK_TEXT.getCode() == pf.getDataType()) {
//      if (!StringUtils.isNullOrEmpty(currFieldValue)
//          && !StringUtils.isValidStringOfMaxLength(currFieldValue, TypeSpecConsts.MAX_LENGTH_OF_BTXT_VALUE)) {
//        errorMessage.append(" 「" + currProfileField.getDisplayName() + "」过长;");
//        isFieldValueOk = false;
//      }
    } else if (DataType.ADDRESS.getCode() == profileField.getDataType()) {
      if (!StringUtils.isNullOrEmpty(fieldValue)
          && !StringUtils.isValidStringOfMaxLength(fieldValue, TypeSpecConsts.MAX_LENGTH_OF_LTXT_VALUE)) {
        errorMessage.append(" [" + columnDisplayName + "]过长;");
        isFieldValueOk = false;
      } else {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("detail", fieldValue);
        fieldValues.set(index, jsonObject.toString());
      }
    } else if (DataType.SINGLE_PICK.getCode() == profileField.getDataType()) {
      // check and convert
      PickOption inputPickOption = null;
      if (!StringUtils.isNullOrEmpty(fieldValue)) {
        for (PickOption pickOption : pickOptions) {
          if (pickOption.getOptionValue().equals(fieldValue)) {
            inputPickOption = pickOption;
            break;
          }
        }
        if (null == inputPickOption) {
          errorMessage.append(" [" + profileField.getDisplayName() + "]选项不存在;");
          isFieldValueOk = false;
        } else {
          try {
            fieldValues.set(index, EncryptUtils.symmetricEncrypt(String.valueOf(inputPickOption.getPickOptionId())));
          } catch (Exception e) {
            LOGGER.error("validateAndTranslateProfileFieldValueOfStandardDataType(): encrypt error");
          }
        }
      }
    } else if (DataType.MULTI_PICK.getCode() == profileField.getDataType()) {
      // check and convert
      if (!StringUtils.isNullOrEmpty(fieldValue)) {
        List<String> inputPickOptionValues = Arrays.asList(fieldValue.split(MULTI_PICK_SEPERATOR));
        List<PickOption> inputPickOptions = new ArrayList<>();
        for (String inputPickOptionValue : inputPickOptionValues) {
          PickOption inputPickOption = null;
          for (PickOption pickOption : pickOptions) {
            if (inputPickOptionValue.equals(pickOption.getOptionValue())) {
              inputPickOption = pickOption;
              break;
            }
          }
          if (null == inputPickOption) {
            errorMessage.append(" [" + columnDisplayName + "]选项不存在;");
            isFieldValueOk = false;
            break;
          } else {
            inputPickOptions.add(inputPickOption);
          }
        }
        if (!CollectionUtils.isEmpty(inputPickOptions)) {
          try {

            String convertedValue = EncryptUtils.symmetricEncrypt(
                String.valueOf(inputPickOptions.get(0).getPickOptionId()));
            for (int j = 1; j < inputPickOptions.size(); j++) {
              convertedValue += "," + EncryptUtils.symmetricEncrypt(
                  String.valueOf(inputPickOptions.get(j).getPickOptionId()));
            }
            fieldValues.set(index, convertedValue);
          } catch (Exception e) {
            LOGGER.error("validateAndTranslateProfileFieldValueOfStandardDataType(): encrypt error");
          }
        }
      }
    } else {
      // DO NOT accept field input of other dataTypes
      fieldValues.set(index, null);
    }

    return isFieldValueOk;
  }

  //   /**
//     * For each line, do steps:
//     *  1) each line has valid fieldCount
//     *  2) validate and convert non-metadata-driven fields
//     *  3) validate and convert fields in CUP & BUP & MUP
//     *
//     * @param rawFieldList
//     * @param dataFields
//     * @param pickOptionMap
//     * @return
//     */
//  public static ValidationCheckResult validateFieldValueOfEachStaffUponBatchImport(
//      int timeZone, List<List<String>> rawFieldList,
//      List<ProfileField> dataFields, Map<Long, List<PickOption>> pickOptionMap) {
//
//    ValidationCheckResult result = new ValidationCheckResult();
//    int validFieldCountEachLine = SystemProfileField.values().length + dataFields.size() + nonMetadataDrivenFieldCount;
//
//    // trim each field
//    trimEachFieldString(rawFieldList);
//    StringBuilder errorMessageOfThisLine = new StringBuilder();
//
//    // validate
//    errorMessageOfThisLine.append("\n\n-------------------- 列数检测 --------------------\n\n");
//    for (int i = 0; i < rawFieldList.size(); i++) {
//
//      List<String> currLine = rawFieldList.get(i);
//      errorMessageOfThisLine.append("\n第 " + (i + 1) + " 行: ");
//
//      // 1)
//      if (CollectionUtils.isEmpty(currLine)
//          || currLine.size() != validFieldCountEachLine) {
//        result.setNoError(false);
//        errorMessageOfThisLine.append(ERROR_FIELD_COUNT);
//      } else {
//        errorMessageOfThisLine.append("OK");
//      }
//
//    }
//    result.setErrorMessage(errorMessageOfThisLine.toString());
//    if (result.noError) {
//      errorMessageOfThisLine.append("\n\n-------------------- 数据字段检测 --------------------\n\n");
//      for (int i = 0; i < rawFieldList.size(); i++) {
//
//        boolean thisLineOk = true;
//
//        List<String> currLine = rawFieldList.get(i);
//        errorMessageOfThisLine.append("\n第 " + (i + 1) + " 行: ");
//
//        // 1)
//        if (CollectionUtils.isEmpty(currLine)
//            || currLine.size() != validFieldCountEachLine) {
//          result.setNoError(false);
//          thisLineOk = false;
//          errorMessageOfThisLine.append(ERROR_FIELD_COUNT);
//        }
//
//        // 2)
//        String contractType = currLine.get(2);
//        String enrollDate = currLine.get(3);
//        String resignDate = currLine.get(4);
//        String employStatus = currLine.get(5);
//        if (null == ContractType.getEnumByDesc(contractType)) {
//          result.setNoError(false);
//          thisLineOk = false;
//          errorMessageOfThisLine.append(ERROR_CONTRACT_TYPE);
//        }
//        if (!isValidCsvDateStringInFormat(enrollDate)) {
//          result.setNoError(false);
//          thisLineOk = false;
//          errorMessageOfThisLine.append(ERROR_ENROLL_DATE);
//        } else {
//          currLine.set(3, String.valueOf(TimeUtils.getTimestampOfZeroOclockTodayOfInputDateInBeijingTime(
//              convertFromCsvDateToCanonicalDate(enrollDate))));
//        }
//        if (!StringUtils.isNullOrEmpty(resignDate)
//            && !isValidCsvDateStringInFormat(resignDate)) {
//          result.setNoError(false);
//          thisLineOk = false;
//          errorMessageOfThisLine.append(ERROR_RESIGN_DATE);
//        } else if (!StringUtils.isNullOrEmpty(resignDate)) {
//          currLine.set(4, String.valueOf(TimeUtils.getTimestampOfZeroOclockTodayOfInputDateInBeijingTime(
//              convertFromCsvDateToCanonicalDate(resignDate))));
//        }
//        if (isValidCsvDateStringInFormat(enrollDate)
//            && isValidCsvDateStringInFormat(resignDate)) {
//          long enrollDateTs = TimeUtils.getTimestampOfZeroOclockTodayOfInputDateInBeijingTime(
//              convertFromCsvDateToCanonicalDate(enrollDate));
//          long resignDateTs = TimeUtils.getTimestampOfZeroOclockTodayOfInputDateInBeijingTime(
//              convertFromCsvDateToCanonicalDate(resignDate));
//          if (resignDateTs < enrollDateTs) {
//            result.setNoError(false);
//            thisLineOk = false;
//            errorMessageOfThisLine.append(ERROR_RESIGN_BEFORE_ENROLL);
//          }
//        }
//        if (null == EmploymentStatus.getEnumByDesc(employStatus)) {
//          result.setNoError(false);
//          thisLineOk = false;
//          errorMessageOfThisLine.append(ERROR_EMPLOYMENT_STATUS);
//        }
//
//        // 3)
//        List<String> metadataDrivenFieldValues = new ArrayList<>();
//        metadataDrivenFieldValues.addAll(currLine.subList(0, 2));
//        metadataDrivenFieldValues.addAll(currLine.subList(6, currLine.size()));
//        boolean isDataFieldValid = isValidAndTransformatDataField(
//            timeZone, metadataDrivenFieldValues, dataFields, pickOptionMap, errorMessageOfThisLine);
//        if (!isDataFieldValid) {
//          result.setNoError(false);
//          thisLineOk = false;
//        } else {
//          metadataDrivenFieldValues.addAll(2, currLine.subList(2, 6));
//          rawFieldList.set(i, metadataDrivenFieldValues);
//        }
//
//        if (thisLineOk) {
//          errorMessageOfThisLine.append("OK");
//        }
//
//      }
//
//      result.setErrorMessage(errorMessageOfThisLine.toString());
//    }
//
//    return result;
//  }

}
