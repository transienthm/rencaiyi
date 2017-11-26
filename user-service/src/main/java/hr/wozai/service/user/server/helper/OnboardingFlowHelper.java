// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hr.wozai.service.servicecommons.commons.consts.SystemFieldConsts;
import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.utils.BooleanUtils;
import hr.wozai.service.servicecommons.commons.utils.EmailUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.enums.UserGender;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.UserAccount;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.model.userorg.UserProfile;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-21
 */
public class OnboardingFlowHelper {

  private static Logger LOGGER = LoggerFactory.getLogger(OnboardingFlowHelper.class);

  private static final int nonMetadataDrivenFieldCount = 4;
  private static final String MULTI_PICK_SEPERATOR = "#WZ#";
  private static final String ERROR_FIELD_COUNT = " 列数有误;";
  private static final String ERROR_FULL_NAME = " 「姓名」有误";
  private static final String ERROR_EMAIL_ADDRESS = " 「企业邮箱」有误";
  private static final String ERROR_DUP_EMAIL_ADDRESS = " 「企业邮箱」不可重复使用";
  private static final String ERROR_MOBILE_PHONE = " 「手机号」有误";
  private static final String ERROR_PERSONAL_EMAIL = " 「个人邮箱」有误";
  private static final String ERROR_GENDER = " 「性别」有误";
  private static final String ERROR_CONTRACT_TYPE = " 「合同类型」有误;";
  private static final String ERROR_ENROLL_DATE = " 「入职日期」有误;";
  private static final String ERROR_EMPLOYMENT_STATUS = " 「转正状态」有误;";


  public static boolean isValidOrgForCreateAccount(Org org) {
    if (null == org
        || StringUtils.isNullOrEmpty(org.getFullName())
        || StringUtils.isNullOrEmpty(org.getShortName())
//        || StringUtils.isNullOrEmpty(org.getAvatarUrl())
        || null == org.getTimeZone()) {
      LOGGER.error("isValidOrgForCreateAccount(): invalid org = {}", org);
      return false;
    }
    return true;
  }

  public static boolean isValidFieldValuesForCreateAccount(Map<String, String> fieldValues) {
    if (null == fieldValues
        || StringUtils.isNullOrEmpty(fieldValues.get(SystemProfileField.EMAIL_ADDRESS.getReferenceName()))
        || StringUtils.isNullOrEmpty(fieldValues.get(SystemProfileField.MOBILE_PHONE.getReferenceName()))
        || StringUtils.isNullOrEmpty(fieldValues.get(SystemProfileField.FULL_NAME.getReferenceName()))) {
//        || !BooleanUtils.isValidBooleanString(fieldValues.get(SystemProfileField.GENDER.getReferenceName()))) {
      return false;
    }
    return true;
  }

  public static boolean isValidUserProfileForLaunchOnboardingFlowRequest(UserProfile userProfile) {

    if (null == userProfile
        || null == userProfile.getOrgId()
        || null == userProfile.getOnboardingTemplateId()
        || null == userProfile.getCreatedUserId()) {
      return false;
    }

    return true;
  }

  public static boolean isValidFieldValuesForLaunchOnboardingFlowRequest(Map<String, String> fieldValues) {
    if (null == fieldValues) {
      return false;
    }
    String emailAddress = fieldValues.get(SystemProfileField.EMAIL_ADDRESS.getReferenceName());
    String mobilePhone = fieldValues.get(SystemProfileField.MOBILE_PHONE.getReferenceName());
    String fullName = fieldValues.get(SystemProfileField.FULL_NAME.getReferenceName());
    String gender = fieldValues.get(SystemProfileField.GENDER.getReferenceName());

    if (StringUtils.isNullOrEmpty(emailAddress)
        || StringUtils.isNullOrEmpty(mobilePhone)
        || StringUtils.isNullOrEmpty(fullName)
        || !BooleanUtils.isValidBooleanString(gender)) {
      return false;
    }

    return true;
  }

  public static boolean isValidUserEmploymentForCreateOrgAndFirstHRUser(UserEmployment userEmployment) {
    if (null == userEmployment) {
//        || (null == userEmployment.getEmploymentStatus()
//            || null == EmploymentStatus.getEnumByCode(userEmployment.getEmploymentStatus()))
//        || !UserEmploymentHelper.isValidContractTypeAndEnrollDateAndResignDate(userEmployment)) {
      LOGGER.error("isValidUserEmploymentForCreateOrgAndFirstHRUser(): invalid ue = {}", userEmployment);
      return false;
    }

    return true;
  }

  public static boolean isValidFieldValuesForBatchImportRequest(Map<String, String> fieldValues) {
    if (null == fieldValues) {
      return false;
    }
    String emailAddress = fieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME);
    String mobilePhone = fieldValues.get(SystemFieldConsts.MOBILE_PHONE_REF_NAME);
    String fullName = fieldValues.get(SystemFieldConsts.FULL_NAME_REF_NAME);
//    String citizenId = fieldValues.get(SystemFieldConsts.CITIZEN_ID_REF_NAME);
    String teamId = fieldValues.get(SystemFieldConsts.TEAM_ID_REF_NAME);
    String reporterId = fieldValues.get(SystemFieldConsts.REPORTED_ID_REF_NAME);

    if (StringUtils.isNullOrEmpty(emailAddress)
        || StringUtils.isNullOrEmpty(mobilePhone)
        || StringUtils.isNullOrEmpty(fullName)) {
//        || StringUtils.isNullOrEmpty(citizenId)) {
      return false;
    }

    try {

      if (!StringUtils.isNullOrEmpty(teamId)) {
        Long.parseLong(teamId);
      }
      if (!StringUtils.isNullOrEmpty(reporterId)) {
        Long.parseLong(reporterId);
      }
    } catch (Exception e) {
      return false;
    }

    return true;
  }

  public static boolean isValidUserEmploymentForLaunchOnboardingFlowRequest(UserEmployment userEmployment) {
    if (null == userEmployment
        || null == userEmployment.getOrgId()
        || !UserEmploymentHelper.isValidContractTypeAndEnrollDateAndResignDate(userEmployment)
        || null == userEmployment.getCreatedUserId()) {
      return false;
    }

    return true;
  }

  public static boolean isValidFieldCountOfEachLine(List<List<String>> rawFieldList) {
    // 0~3: fullName, emailAddress, mobilePhone, gender 
    // 4~6: contractType, enrollDate, employmentStatus
    int validFieldCountEachLine = 3;
    for (int i = 0; i < rawFieldList.size(); i++) {
      List<String> currLine = rawFieldList.get(i);
      if (CollectionUtils.isEmpty(currLine)
          || currLine.size() != validFieldCountEachLine) {
        LOGGER.error("isValidFieldCountOfEachLine()-error: currLine.size={}, validFieldCount={}, currLine={}"
                     + currLine.size(), validFieldCountEachLine, currLine);
        return false;
      }
    }

    return true;
  }

//  public static boolean isValidFieldCountOfEachLine(
//      List<List<String>> rawFieldList, List<ProfileField> enabledDataFields) {
//    int validFieldCountEachLine = 8;
//    for (int i = 0; i < rawFieldList.size(); i++) {
//      List<String> currLine = rawFieldList.get(i);
//      if (CollectionUtils.isEmpty(currLine)
//          || currLine.size() != validFieldCountEachLine) {
//        LOGGER.error("isValidFieldCountOfEachLine()-error: currLine.size={}, validFieldCount={}, currLine={}, fields={}"
//                     + currLine.size(), validFieldCountEachLine, currLine, enabledDataFields);
//        return false;
//      }
//    }
//
//    return true;
//  }

     /**
     * For each line, do steps:
     *  1) validate each line has right column number
     *  2) validate 4 fields in CoreUserProfile
     *  3) validate 3 fields in UserEmployment
     *
     * @param rawFieldList
     * @return
     */
  public static ValidationCheckResult validateFieldValueOfEachStaffUponBatchImport(
      List<List<String>> rawFieldList) {

    ValidationCheckResult result = new ValidationCheckResult();

    // trim each field
    trimEachFieldString(rawFieldList);
    StringBuilder errorMessageOfThisLine = new StringBuilder();

    // 1)
    // field 0~2: fullName, emailAddress, mobilePhone
    int validFieldCountEachLine = 3;
    errorMessageOfThisLine.append("\n\n-------------------- 列数检测 --------------------\n\n");
    for (int i = 0; i < rawFieldList.size(); i++) {
      List<String> currLine = rawFieldList.get(i);
      errorMessageOfThisLine.append("\n第 " + (i + 1) + " 行 (" + currLine.get(0) + "): ");
      if (CollectionUtils.isEmpty(currLine)
          || currLine.size() != validFieldCountEachLine) {
        result.setNoError(false);
        errorMessageOfThisLine.append(ERROR_FIELD_COUNT);
      } else {
        errorMessageOfThisLine.append("OK");
      }
    }
    result.setErrorMessage(errorMessageOfThisLine.toString());

    // 2
    if (result.noError) {
      Set<String> emailAddresses = new HashSet<>();
      errorMessageOfThisLine.append("\n\n-------------------- 数据字段检测 --------------------\n\n");
      for (int i = 0; i < rawFieldList.size(); i++) {

        List<String> currLine = rawFieldList.get(i);
        errorMessageOfThisLine.append("\n第 " + (i + 1) + " 行: ");

        boolean thisLineOk = true;

        // 2)
        String fullName = currLine.get(0);
        String emailAddress = currLine.get(1);
        String mobilePhone = currLine.get(2);
        if (StringUtils.isNullOrEmpty(fullName)) {
          result.setNoError(false);
          thisLineOk = false;
          errorMessageOfThisLine.append(ERROR_FULL_NAME);
        }
        if (!EmailUtils.isValidEmailAddressByRegex(emailAddress)) {
          result.setNoError(false);
          thisLineOk = false;
          errorMessageOfThisLine.append(ERROR_EMAIL_ADDRESS);
        }
        if (emailAddresses.contains(emailAddress)) {
          result.setNoError(false);
          thisLineOk = false;
          errorMessageOfThisLine.append(ERROR_DUP_EMAIL_ADDRESS);
        }
        emailAddresses.add(emailAddress);
        if (StringUtils.isNullOrEmpty(mobilePhone)) {
          result.setNoError(false);
          thisLineOk = false;
          errorMessageOfThisLine.append(ERROR_MOBILE_PHONE);
        }

        if (thisLineOk) {
          errorMessageOfThisLine.append("OK");
        }

      }
      result.setErrorMessage(errorMessageOfThisLine.toString());
    }


    return result;
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

//  /**
//   * Check:
//   *  1) emailAddress is in valid format
//   *  2) if mandatory field has value
//   *  3) check input against each dataType
//   *
//   * @param timeZone
//   * @param fieldValues
//   * @param dataFields
//   * @param pickOptionMap
//   * @param errorMessage
//   * @return
//   */
//  private static boolean isValidAndTransformatDataField(
//      int timeZone, List<String> fieldValues, List<ProfileField> dataFields,
//      Map<Long, List<PickOption>>  pickOptionMap, StringBuilder errorMessage) {
//
//    boolean isValid = true;
//    for (int i = 0; i < fieldValues.size(); i++) {
//
//      ProfileField currProfileField = dataFields.get(i);
//
//      // skip teamId and reporterId
//      String referenceName = currProfileField.getReferenceName();
//      if (SystemFieldConsts.TEAM_ID_REF_NAME.equals(referenceName)
//          || SystemFieldConsts.REPORTED_ID_REF_NAME.equals(referenceName)) {
//        fieldValues.set(i, null);
//        continue;
//      }
//
//      String currFieldValue = fieldValues.get(i);
//
//      // 1)
//      if (SystemFieldConsts.EMAIL_ADDRESS_REF_NAME.equals(currProfileField.getReferenceName())) {
//        if (!EmailUtils.isValidEmailAddressByRegex(currFieldValue)) {
//          errorMessage.append(ERROR_EMAIL_FORMAT);
//          isValid = false;
//          continue;
//        }
//      }
//
//      // 2)
//      if (currProfileField.getIsMandatory() == 1
//          && StringUtils.isNullOrEmpty(currFieldValue)) {
//        errorMessage.append(" 「" + currProfileField.getDisplayName() + "」必填;");
//        isValid = false;
//        continue;
//      }
//
//      // 3)
//      if (DataType.INTEGER.getCode() == currProfileField.getDataType()) {
//        if (!StringUtils.isNullOrEmpty(currFieldValue)
//            && !LongUtils.isLong(currFieldValue)) {
//          errorMessage.append(" 「" + currProfileField.getDisplayName() + "」有误;");
//          isValid = false;
//        }
//      } else if (DataType.DECIMAL.getCode() == currProfileField.getDataType()) {
//        if (!StringUtils.isNullOrEmpty(currFieldValue)
//            && !NumberUtils.isNumber(currFieldValue)) {
//          errorMessage.append(" 「" + currProfileField.getDisplayName() + "」有误;");
//          isValid = false;
//        }
//      } else if (DataType.DATETIME.getCode() == currProfileField.getDataType()) {
//        // check and convert
//        if (!StringUtils.isNullOrEmpty(currFieldValue)) {
//          if (!isValidCsvDateStringInFormat(currFieldValue)) {
//            errorMessage.append(" 「" + currProfileField.getDisplayName() + "」有误 (例: 2016/12/31);");
//            isValid = false;
//          } else {
//            long zeroTimestamp = TimeUtils.getTimestampOfZeroOclockTodayOfInputDateInBeijingTime(
//                convertFromCsvDateToCanonicalDate(currFieldValue));
//            fieldValues.set(i, String.valueOf(zeroTimestamp));
//          }
//        }
//      } else if (DataType.SHORT_TEXT.getCode() == currProfileField.getDataType()) {
//        if (!StringUtils.isNullOrEmpty(currFieldValue)
//            && !StringUtils.isValidStringOfMaxLength(currFieldValue, TypeSpecConsts.MAX_LENGTH_OF_STXT_VALUE)) {
//          errorMessage.append(" 「" + currProfileField.getDisplayName() + "」过长;");
//          isValid = false;
//        }
//      } else if (DataType.LONG_TEXT.getCode() == currProfileField.getDataType()) {
//        if (!StringUtils.isNullOrEmpty(currFieldValue)
//            && !StringUtils.isValidStringOfMaxLength(currFieldValue, TypeSpecConsts.MAX_LENGTH_OF_LTXT_VALUE)) {
//          errorMessage.append(" 「" + currProfileField.getDisplayName() + "」过长;");
//          isValid = false;
//        }
//      } else if (DataType.BLOCK_TEXT.getCode() == currProfileField.getDataType()) {
//        if (!StringUtils.isNullOrEmpty(currFieldValue)
//            && !StringUtils.isValidStringOfMaxLength(currFieldValue, TypeSpecConsts.MAX_LENGTH_OF_BTXT_VALUE)) {
//          errorMessage.append(" 「" + currProfileField.getDisplayName() + "」过长;");
//          isValid = false;
//        }
//      } else if (DataType.ADDRESS.getCode() == currProfileField.getDataType()) {
//        if (!StringUtils.isNullOrEmpty(currFieldValue)
//            && !StringUtils.isValidStringOfMaxLength(currFieldValue, TypeSpecConsts.MAX_LENGTH_OF_LTXT_VALUE)) {
//          errorMessage.append(" 「" + currProfileField.getDisplayName() + "」过长;");
//          isValid = false;
//        } else {
//          JSONObject jsonObject = new JSONObject();
//          jsonObject.put("detail", currFieldValue);
//          fieldValues.set(i, jsonObject.toString());
//        }
//      } else if (DataType.SINGLE_PICK.getCode() == currProfileField.getDataType()) {
//        // check and convert
//        PickOption inputPickOption = null;
//        if (!StringUtils.isNullOrEmpty(currFieldValue)) {
//          List<PickOption> pickOptionsOfField = pickOptionMap.get(currProfileField.getProfileFieldId());
//          for (PickOption pickOption: pickOptionsOfField) {
//            if (currFieldValue.equals(pickOption.getOptionValue())) {
//              inputPickOption = pickOption;
//              break;
//            }
//          }
//          if (null == inputPickOption) {
//            errorMessage.append(" 「" + currProfileField.getDisplayName() + "」选项不存在;");
//            isValid = false;
//          } else {
//            fieldValues.set(i, String.valueOf(inputPickOption.getPickOptionId()));
//          }
//        }
//      }
//      else if (DataType.MULTI_PICK.getCode() == currProfileField.getDataType()) {
//        // check and convert
//        if (!StringUtils.isNullOrEmpty(currFieldValue)) {
//          List<String> inputPickOptionValues = Arrays.asList(currFieldValue.split(MULTI_PICK_SEPERATOR));
//          List<PickOption> pickOptionsOfField = pickOptionMap.get(currProfileField.getProfileFieldId());
//          List<PickOption> inputPickOptions = new ArrayList<>();
//          for (String inputPickOptionValue: inputPickOptionValues) {
//            PickOption inputPickOption = null;
//            for (PickOption pickOption: pickOptionsOfField) {
//              if (inputPickOptionValue.equals(pickOption.getOptionValue())) {
//                inputPickOption = pickOption;
//                break;
//              }
//            }
//            if (null == inputPickOption) {
//              errorMessage.append(" 「" + currProfileField.getDisplayName() + "」选项不存在;");
//              isValid = false;
//              break;
//            } else {
//              inputPickOptions.add(inputPickOption);
//            }
//          }
//          if (!CollectionUtils.isEmpty(inputPickOptions)) {
//            String convertedValue = String.valueOf(inputPickOptions.get(i).getPickOptionId());
//            for (int j = 1; i < inputPickOptions.size(); j++) {
//              convertedValue += "," + inputPickOptions.get(j).getPickOptionId();
//            }
//            fieldValues.set(i, convertedValue);
//          }
//        }
//      } else {
//        // DO NOT accept field input of other dataTypes
//        fieldValues.set(i, null);
//      }
//    }
//
//    return isValid;
//  }

  private static void trimEachFieldString(List<List<String>> rawFieldList) {
    for (int i = 0; i < rawFieldList.size(); i++) {
      List<String> currLine = rawFieldList.get(i);
      for (int j = 0; j < currLine.size(); j++) {
        currLine.set(j, (currLine.get(j) != null ? currLine.get(j).trim() : null));
      }
    }
  }

  public static ValidationCheckResult validateIfExistedEmailAddress(
      List<List<String>> rawFieldList, List<UserAccount> existedUserAccouts) {
    ValidationCheckResult checkResult = new ValidationCheckResult();
    if (!CollectionUtils.isEmpty(existedUserAccouts)) {
      StringBuilder errorMessage = new StringBuilder();
      Set<String> existedEmails = new HashSet<>();
      for (UserAccount userAccount : existedUserAccouts) {
        existedEmails.add(userAccount.getEmailAddress());
      }
      for (int i = 0; i < rawFieldList.size(); i++) {
        if (existedEmails.contains(rawFieldList.get(i).get(1))) {
          errorMessage.append("\n第 " + (i + 1) + " 行: 该企业邮箱已被占用");
          checkResult.setNoError(false);
        }
      }
      checkResult.setErrorMessage(errorMessage.toString());
    }

    return checkResult;
  }

  private static boolean isValidCsvDateStringInFormat(String csvDateStr) {
    if (StringUtils.isNullOrEmpty(csvDateStr)) {
      return false;
    }
    String re = "[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(csvDateStr);
    return m.matches();
  }

  /**
   * Convert to yyyy-MM-dd
   *
   * @param csvDateStr
   * @return
   */
  private static String convertFromCsvDateToCanonicalDate(String csvDateStr) {
    String[] bits = csvDateStr.trim().split("/");
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

  public static void main(String[] args) {
    String csvDateStr = "2016/1/2";
    String re = "[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(csvDateStr);
    System.out.println("match=" + m.matches());

  }


}
