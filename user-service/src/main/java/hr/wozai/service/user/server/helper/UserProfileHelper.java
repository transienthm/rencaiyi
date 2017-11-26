// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import com.alibaba.fastjson.JSONObject;

import hr.wozai.service.user.server.util.MetaDataStringValidate;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hr.wozai.service.servicecommons.commons.consts.TypeSpecConsts;
import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.LongUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.utils.validator.StringLengthValidator;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.enums.UserGender;
import hr.wozai.service.user.server.model.userorg.BasicUserProfile;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.MetaUserProfile;
import hr.wozai.service.user.server.model.userorg.ProfileField;
import hr.wozai.service.user.server.model.userorg.UserProfile;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-12
 */
public class UserProfileHelper {

  private static Logger LOGGER = LoggerFactory.getLogger(UserProfileHelper.class);

  private static List<String> skipFieldNames = Arrays.asList("createdUserId",
                                                             "createdTime",
                                                             "lastModifiedUserId",
                                                             "lastModifiedTime",
                                                             "isDeleted");

  /**
   * Check data integrity
   *
   * @param userProfile
   * @return
   */
  public static boolean isValidAddUserProfileRequest(UserProfile userProfile) {
    if (null == userProfile
        || null == userProfile.getOrgId()
        || null == userProfile.getUserId()
        || null == userProfile.getOnboardingTemplateId()
        || null == userProfile.getProfileTemplateId()
//        || null == userProfile.getUserStatus()
        || null == userProfile.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  /**
   * Must satisfy requirements:
   *  1) each input referenceName has referred field
   *  2) each mandatory field has value
   *  3) all fieldValue satisfy typeSpec
   *
   * @param dataValues
   * @param dataFields
   * @return
   */
  public static boolean isValidFieldValueForAddUserProfileRequest(
      Map<String, String> dataValues, List<ProfileField> dataFields) {

    LOGGER.info("ToCheck: values=" + dataValues);
    LOGGER.info("ToCheck: fields=" + dataFields);

    Map<String, ProfileField> dataFieldMap = new HashMap<>();
    for (ProfileField dataField: dataFields) {
      dataFieldMap.put(dataField.getReferenceName(), dataField);
    }

    LOGGER.info("isValidFieldValueForAddUserProfileRequest(): Checkpoint #1");

    for (String referenceName: dataValues.keySet()) {
      if (!dataFieldMap.containsKey(referenceName)) {
        LOGGER.info("isValidFieldValueForAddUserProfileRequest(): errorReferencenName={}", referenceName);
        return false;
      }
    }

    LOGGER.info("isValidFieldValueForAddUserProfileRequest(): Checkpoint #2");

    for (ProfileField dataField: dataFields) {
      String dataValue = dataValues.get(dataField.getReferenceName());
      // 2)
      if (dataField.getIsMandatory() == 1) {
        if (StringUtils.isNullOrEmpty(dataValue)) {
          LOGGER.info("isValidFieldValueForAddUserProfileRequest(): errorDataField={}", dataField);
          return false;
        }
      }
      // 3)
      if (!isValidDataValueOfSpecificType(dataValue, dataField)) {
        return false;
      }
    }

    // TEST
    LOGGER.info("isValidFieldValueForAddUserProfileRequest(): Checkpoint #3");

    return true;
  }

//  public static boolean isValidAddCoreUserProfileRequest(OldCoreUserProfile oldCoreUserProfile) {
//    if (null == oldCoreUserProfile
//        || null == oldCoreUserProfile.getOrgId()
//        || null == oldCoreUserProfile.getUserId()
//        || null == oldCoreUserProfile.getProfileTemplateId()
//        || null == oldCoreUserProfile.getUserStatus()
//        || null == oldCoreUserProfile.getCreatedUserId()) {
//      return false;
//    }
//    return true;
//  }
//
//  public static OldCoreUserProfile parseCoreUserProfileFromMap(Map<String, Object> rawMap) {
//
//    if (null == rawMap
//        || rawMap.size() == 0) {
//      return null;
//    }
//    OldCoreUserProfile oldCoreUserProfile = new OldCoreUserProfile();
//    Field[] declaredFields = OldCoreUserProfile.class.getDeclaredFields();
//    for (Field field: declaredFields) {
//      String fieldName = field.getName();
//      if (rawMap.containsKey(fieldName)) {
//        Object fieldValue = rawMap.get(fieldName);
//        Class fieldClass = field.getType();
//        field.setAccessible(true);
//        try {
//          if (Long.class.equals(fieldClass)) {
//            if (fieldValue instanceof Long) {
//              field.set(oldCoreUserProfile, fieldValue);
//            } else {
//              field.set(oldCoreUserProfile, Long.parseLong((String) fieldValue));
//            }
//          } else if (Integer.class.equals(fieldClass)) {
//            if (fieldValue instanceof Integer) {
//              field.set(oldCoreUserProfile, fieldValue);
//            } else {
//              field.set(oldCoreUserProfile, Integer.parseInt((String) fieldValue));
//            }
//          } else if (String.class.equals(fieldClass)) {
//            if (fieldValue instanceof String) {
//              field.set(oldCoreUserProfile, fieldValue);
//            } else {
//              field.set(oldCoreUserProfile, (String) fieldValue);
//            }
//          }
//        } catch (Exception e) {
//          LOGGER.error("parseCoreUserProfileFromMap()-error: fail to parse", e);
//          throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
//        }
//      }
//    }
//    return oldCoreUserProfile;
//  }

  /**
   * Must satisfy requirements:
   *  1) all input field referenceName exist;
   *  2) at least one field value needs update
   *  3) for field to update: each mandatory field has value
   *  4) for field to update: the value satisfy typeSpec
   *
   * @param dataValues
   * @param dataFields
   * @return
   */
  public static boolean isValidUpdateUserProfileFieldRequest(
      Map<String, String> dataValues, List<ProfileField> dataFields) {

    Set<String> existedReferenceNames = new HashSet<>();
    for (ProfileField profileField: dataFields) {
      existedReferenceNames.add(profileField.getReferenceName());
    }
    boolean needUpdate = false;
    for (String inputReferenceName: dataValues.keySet()) {
      // 1)
      if (!existedReferenceNames.contains(inputReferenceName)) {
        return false;
      }
      if (null != dataValues.get(inputReferenceName)) {
        needUpdate = true;
      }
    }
    // 2)
    if (!needUpdate) {
      return false;
    }

    for (ProfileField dataField: dataFields) {
      String currReferenceName = dataField.getReferenceName();
      if (null != dataValues.get(currReferenceName)) {
        // 3)
        String dataValue = dataValues.get(currReferenceName);
        if (dataField.getIsMandatory() == 1) {
          if (StringUtils.isNullOrEmpty(dataValue)) {
            return false;
          }
        }
        // 4)
        if (!isValidDataValueOfSpecificType(dataValue, dataField)) {
          return false;
        }
      }
    }

    return true;
  }

  private static boolean isValidDataValueOfSpecificType(String dataValue, ProfileField profileField) {
    DataType dataType = DataType.getEnumByCode(profileField.getDataType());
    if (!StringUtils.isNullOrEmpty(dataValue)) {
      if (dataType.equals(DataType.INTEGER) && !LongUtils.isLong(dataValue)) {
          return false;
      } else if (dataType.equals(DataType.DECIMAL) && !NumberUtils.isNumber(dataValue)) {
            return false;
      } else if (dataType.equals(DataType.DATETIME) && !LongUtils.isLong(dataValue)) {
            return false;
      } else if (dataType.equals(DataType.SHORT_TEXT)) {
          if (!StringLengthValidator.validate(dataValue, TypeSpecConsts.CHINESE_MAX_LENGTH_OF_STXT_VALUE)) {
            return false;
          }
      } else if (dataType.equals(DataType.LONG_TEXT)) {
          if (!StringLengthValidator.validate(dataValue, TypeSpecConsts.CHINESE_MAX_LENGTH_OF_LTXT_VALUE)) {
            return false;
          }
      } else if (dataType.equals(DataType.BLOCK_TEXT)) {
          if (!StringLengthValidator.validate(dataValue, TypeSpecConsts.CHINESE_MAX_LENGTH_OF_LTXT_VALUE)) {
            return false;
          }
        }
      }
    return true;
  }

//  /**
//   * Copy properties from userProfile to coreUserProfile
//   *
//   * @param userProfileRawMap
//   * @param oldCoreUserProfile
//   */
//  public static void copyProperties(Map<String, Object> userProfileRawMap, OldCoreUserProfile oldCoreUserProfile) {
//
//    Assert.notEmpty(userProfileRawMap, "userProfileRawMap cannot be null/empty");
//    Assert.notNull(oldCoreUserProfile, "coreUserProfile cannot be null");
//
//    Field[] declaredFields = OldCoreUserProfile.class.getDeclaredFields();
//    for (Field field: declaredFields) {
//      String fieldName = field.getName();
//      if (userProfileRawMap.containsKey(fieldName)
//          && !skipFieldNames.contains(fieldName)) {
//        Object fieldValue = userProfileRawMap.get(fieldName);
//        Class fieldClass = field.getType();
//        field.setAccessible(true);
//        try {
//          if (Long.class.equals(fieldClass)) {
//            if (fieldValue instanceof Long) {
//              field.set(oldCoreUserProfile, fieldValue);
//            } else {
//              field.set(oldCoreUserProfile, Long.parseLong((String) fieldValue));
//            }
//          } else if (Integer.class.equals(fieldClass)) {
//            if (fieldValue instanceof Integer) {
//              field.set(oldCoreUserProfile, fieldValue);
//            } else {
//              field.set(oldCoreUserProfile, Integer.parseInt((String) fieldValue));
//            }
//          } else if (String.class.equals(fieldClass)) {
//            if (fieldValue instanceof String) {
//              field.set(oldCoreUserProfile, fieldValue);
//            } else {
//              field.set(oldCoreUserProfile, (String) fieldValue);
//            }
//          } else if (JSONObject.class.equals(fieldClass)) {
//            JSONObject jsonObject = JSONObject.parseObject((String) fieldValue);
//            field.set(oldCoreUserProfile, jsonObject);
//          }
//        } catch (Exception e) {
//          LOGGER.error("copyProperties()-error: fail to parse", e);
//          throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
//        }
//      }
//    }
//  }

  public static boolean isValidPresetUserProfileConfigs(List<UserProfileConfig> userProfileConfigs) {

    if (CollectionUtils.isEmpty(userProfileConfigs)) {
      return false;
    }

    Set<Integer> booleanValues = new HashSet<>();
    booleanValues.add(0);
    booleanValues.add(1);

    for (UserProfileConfig userProfileConfig: userProfileConfigs) {
      if (null == userProfileConfig.getOrgId()
          || (null == userProfileConfig.getFieldCode()
              || null == SystemProfileField.getEnumByCode(userProfileConfig.getFieldCode()))
          || StringUtils.isNullOrEmpty(userProfileConfig.getReferenceName())
          || StringUtils.isNullOrEmpty(userProfileConfig.getDbColumnName())
          || (null == userProfileConfig.getDataType()
              || null == DataType.getEnumByCode(userProfileConfig.getDataType()))
          || (null == userProfileConfig.getIsOnboardingStaffEditable()
              || !booleanValues.contains(userProfileConfig.getIsOnboardingStaffEditable()))
          || (null == userProfileConfig.getIsActiveStaffEditable()
              || !booleanValues.contains(userProfileConfig.getIsActiveStaffEditable()))
          || (null == userProfileConfig.getIsEnabled()
              || !booleanValues.contains(userProfileConfig.getIsEnabled()))
          || (null == userProfileConfig.getIsEnabledEditable()
              || !booleanValues.contains(userProfileConfig.getIsEnabledEditable()))
          || (null == userProfileConfig.getIsMandatory()
              || !booleanValues.contains(userProfileConfig.getIsMandatory()))
        ||  null == userProfileConfig.getCreatedUserId()) {
        LOGGER.error("isValidPresetUserProfileConfigs(): invalidConfig={}", userProfileConfig);
        return false;
      }
    }
    return true;
  }

  /**************** methods after refraction ****************/

  public static boolean isValidAddCoreUserProfileRequest(CoreUserProfile coreUserProfile) {
    if (null == coreUserProfile
        || null == coreUserProfile.getOrgId()
        || null == coreUserProfile.getUserId()
        || null == coreUserProfile.getOnboardingTemplateId()
        || null == coreUserProfile.getProfileTemplateId()
        || !StringUtils.isValidVarchar255(coreUserProfile.getFullName())
        || !StringUtils.isValidVarchar255(coreUserProfile.getEmailAddress())
        || !StringUtils.isValidVarchar255(coreUserProfile.getMobilePhone())
        || (null == coreUserProfile.getGender()
            || null == UserGender.getEnumByCode(coreUserProfile.getGender()))
        || !StringUtils.isValidVarchar255(coreUserProfile.getPersonalEmail())
        || null == coreUserProfile.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidAddBasicUserProfileRequest(BasicUserProfile basicUserProfile) {
    if (null == basicUserProfile
        || null == basicUserProfile.getOrgId()
        || null == basicUserProfile.getUserId()
        || null == basicUserProfile.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidAddMetaUserProfileRequest(MetaUserProfile metaUserProfile) {
    if (null == metaUserProfile
        || null == metaUserProfile.getOrgId()
        || null == metaUserProfile.getUserId()
        || null == metaUserProfile.getProfileTemplateId()
        || null == metaUserProfile.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  public static void validateDataValue(String referenceName, UserProfileConfig userProfileConfig, String value) {
    boolean isValid;
    if (referenceName.equals(SystemProfileField.FULL_NAME.getReferenceName())) {
      isValid = StringLengthValidator.validate(value, TypeSpecConsts.FULL_NAME_LENGTH);
    } else {
      isValid = MetaDataStringValidate.validate(userProfileConfig.getDataType(), value);
    }

    if (!isValid) {
      LOGGER.info("meta data string validate fail, referenceName:{}, value:{}", referenceName, value);
      throw new ServiceStatusException(ServiceStatus.COMMON_STRING_VALIDATE_FAIL);
    }
  }

  /**
   * Checkpoints:
   *  1) must be in the map if mandatory
   *  2) fieldValue matches DataType
   *
   * @param userProfileConfigs
   * @param fieldValues
   * @return
   */
  public static boolean isValidAddCoreUserProfileFieldValue(
      List<UserProfileConfig> userProfileConfigs, Map<String, String> fieldValues) {

    LOGGER.info("isValidAddCoreUserProfileFieldValue(): fv={}", fieldValues);

    for (UserProfileConfig userProfileConfig: userProfileConfigs) {
      if (userProfileConfig.getFieldCode() < 100) {
        // 1)
        if (1 == userProfileConfig.getIsMandatory()
            && null == fieldValues.get(userProfileConfig.getReferenceName())) {
          return false;
        }
        // 2)
        if (fieldValues.containsKey(userProfileConfig.getReferenceName())) {
          String dataValue = fieldValues.get(userProfileConfig.getReferenceName());
          DataType dataType = DataType.getEnumByCode(userProfileConfig.getDataType());
          if (!isValidFieldValueOfDataType(dataValue, dataType)) {
            return false;
          }
          // 名字单独检测
          if (userProfileConfig.getReferenceName().equals(SystemProfileField.FULL_NAME.getReferenceName())
                  && !StringLengthValidator.validate(dataValue, TypeSpecConsts.FULL_NAME_LENGTH)) {
            return false;
          }
        }
      }
    }

    LOGGER.info("isValidAddCoreUserProfileFieldValue(): OK");

    return true;
  }

  /**
   * Checkpoints:
   *  1) must be in the map if mandatory
   *  2) fieldValue matches DataType
   *
   * @param userProfileConfigs
   * @param fieldValues
   * @return
   */
  public static boolean isValidAddBasicUserProfileFieldValue(
      List<UserProfileConfig> userProfileConfigs, Map<String, String> fieldValues) {

    for (UserProfileConfig userProfileConfig: userProfileConfigs) {
      if (userProfileConfig.getFieldCode() >= 100) {
        // 1)
        if (1 == userProfileConfig.getIsMandatory()
            && !fieldValues.containsKey(userProfileConfig.getReferenceName())) {
          return false;
        }
        // 2)
        if (null != fieldValues.get(userProfileConfig.getReferenceName())) {
          String dataValue = fieldValues.get(userProfileConfig.getReferenceName());
          DataType dataType = DataType.getEnumByCode(userProfileConfig.getDataType());
          if (!isValidFieldValueOfDataType(dataValue, dataType)) {
            return false;
          }
        }
      }
    }

    LOGGER.info("isValidAddBasicUserProfileFieldValue(): OK");

    return true;
  }

  public static void setFieldInCoreUserProfileFromMap(
      CoreUserProfile coreUserProfile, Map<String, String> fieldValues) {

    if (null == coreUserProfile
        || MapUtils.isEmpty(fieldValues)) {
      return;
    }

    Field[] declaredFields = CoreUserProfile.class.getDeclaredFields();
    for (Field field: declaredFields) {
      String fieldName = field.getName();
      if (null != fieldValues.get(fieldName)) {
        Object fieldValue = fieldValues.get(fieldName);
        Class fieldClass = field.getType();
        field.setAccessible(true);
        try {
          if (Long.class.equals(fieldClass)) {
            if (fieldValue instanceof Long) {
              field.set(coreUserProfile, fieldValue);
            } else {
              field.set(coreUserProfile, Long.parseLong((String) fieldValue));
            }
          } else if (Integer.class.equals(fieldClass)) {
            if (fieldValue instanceof Integer) {
              field.set(coreUserProfile, fieldValue);
            } else {
              field.set(coreUserProfile, Integer.parseInt((String) fieldValue));
            }
          } else if (String.class.equals(fieldClass)) {
            if (fieldValue instanceof String) {
              field.set(coreUserProfile, fieldValue);
            } else {
              field.set(coreUserProfile, (String) fieldValue);
            }
          }
        } catch (Exception e) {
          LOGGER.error("setFieldInCoreUserProfileFromMap()-error: fail to parse, fieldValue={}, cup={}, fieldValues={}",
                       fieldValue, coreUserProfile, fieldValue, e);
          throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
      }
    }
  }

  public static void setFieldInBasicUserProfileFromMap(
      BasicUserProfile basicUserProfile, Map<String, String> fieldValues) {

    if (null == basicUserProfile
        || MapUtils.isEmpty(fieldValues)) {
      return;
    }

    Field[] declaredFields = CoreUserProfile.class.getDeclaredFields();
    for (Field field: declaredFields) {
      String fieldName = field.getName();
      if (null != fieldValues.get(fieldName)) {
        Object fieldValue = fieldValues.get(fieldName);
        Class fieldClass = field.getType();
        field.setAccessible(true);
        try {
          if (Long.class.equals(fieldClass)) {
            if (fieldValue instanceof Long) {
              field.set(basicUserProfile, fieldValue);
            } else {
              field.set(basicUserProfile, Long.parseLong((String) fieldValue));
            }
          } else if (Integer.class.equals(fieldClass)) {
            if (fieldValue instanceof Integer) {
              field.set(basicUserProfile, fieldValue);
            } else {
              field.set(basicUserProfile, Integer.parseInt((String) fieldValue));
            }
          } else if (String.class.equals(fieldClass)) {
            if (fieldValue instanceof String) {
              field.set(basicUserProfile, fieldValue);
            } else {
              field.set(basicUserProfile, (String) fieldValue);
            }
          }
        } catch (Exception e) {
          LOGGER.error("setFieldInBasicUserProfileFromMap()-error: fail to parse, fieldValue={}, bup={}, fieldValues={}",
                       fieldValue, basicUserProfile, fieldValue, e);
          throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
      }
    }
  }

  private static boolean isValidFieldValueOfDataType(String dataValue, DataType dataType) {

    if (StringUtils.isNullOrEmpty(dataValue)
        || null == dataType) {
      return false;
    }

    if (!StringUtils.isNullOrEmpty(dataValue)) {
      if (dataType.equals(DataType.INTEGER) && !LongUtils.isLong(dataValue)) {
        return false;
      } else if (dataType.equals(DataType.DECIMAL) && !NumberUtils.isNumber(dataValue)) {
        return false;
      } else if (dataType.equals(DataType.DATETIME) && !LongUtils.isLong(dataValue)) {
        return false;
      } else if (dataType.equals(DataType.SHORT_TEXT)) {
        if (!StringLengthValidator.validate(dataValue, TypeSpecConsts.CHINESE_MAX_LENGTH_OF_STXT_VALUE)) {
          return false;
        }
      } else if (dataType.equals(DataType.LONG_TEXT)) {
        if (!StringLengthValidator.validate(dataValue, TypeSpecConsts.CHINESE_MAX_LENGTH_OF_LTXT_VALUE)) {
          return false;
        }
      } else if (dataType.equals(DataType.BLOCK_TEXT)) {
        if (!StringLengthValidator.validate(dataValue, TypeSpecConsts.CHINESE_MAX_LENGTH_OF_LTXT_VALUE)) {
          return false;
        }
      }
    }

    return true;
  }

}
