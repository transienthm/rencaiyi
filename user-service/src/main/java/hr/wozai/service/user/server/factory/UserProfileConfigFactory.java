// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.factory;

import com.alibaba.fastjson.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.FastJSONUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-01
 */
@Component("userProfileConfigFactory")
public class UserProfileConfigFactory {

  private static Logger LOGGER = LoggerFactory.getLogger(UserProfileConfigFactory.class);

  private List<UserProfileConfig> presetUserProfileConfigs = null;

  @PostConstruct
  public void init() {
    try {
      InputStream resource = getClass().getResourceAsStream("/preset/preset-user-profile-config.json");
      BufferedReader br = new BufferedReader(new InputStreamReader(resource));
      String line = null;
      StringBuilder stringBuilder = new StringBuilder();
      while (null != (line = br.readLine())) {
        stringBuilder.append(line);
      }
      JSONArray jsonArray = JSONArray.parseArray(stringBuilder.toString());
      presetUserProfileConfigs = FastJSONUtils.convertJSONArrayToObjectList(jsonArray, UserProfileConfig.class);
      setRemainingMeta(presetUserProfileConfigs);

      for (UserProfileConfig userProfileConfig: presetUserProfileConfigs) {
        userProfileConfig.setIsSystemRequired(1);
      }
      LOGGER.info("init(): presetUserProfileConfigs={}", presetUserProfileConfigs);
      if (!isValidPresetUserProfileConfigs(presetUserProfileConfigs)) {
        throw new ServiceStatusException(ServiceStatus.UO_INVALID_USER_PROFILE_CONFIG);
      }
    } catch (IOException e) {
      LOGGER.error("init(): fail to handle files", e);
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_PROFILE_FIELDS);
    } catch (ServiceStatusException e) {
      LOGGER.error("init(): fail to init preset configs", e);
      throw e;
    }
  }

  public List<UserProfileConfig> listPresetUserProfileConfig() {
    List<UserProfileConfig> clonedUserProfileConfigs = new ArrayList<>();
    for (UserProfileConfig userProfileConfig: presetUserProfileConfigs) {
      UserProfileConfig clonedUserProfileConfig = new UserProfileConfig();
      BeanUtils.copyProperties(userProfileConfig, clonedUserProfileConfig);
      clonedUserProfileConfigs.add(clonedUserProfileConfig);
    }
    return clonedUserProfileConfigs;
  }

  private void setRemainingMeta(List<UserProfileConfig> userProfileConfigs) {

    if (CollectionUtils.isEmpty(userProfileConfigs)) {
      return;
    }

    for (UserProfileConfig userProfileConfig: userProfileConfigs) {
      SystemProfileField systemProfileField = SystemProfileField.getEnumByCode(userProfileConfig.getFieldCode());
      if (null != systemProfileField) {
        userProfileConfig.setDataType(systemProfileField.getDataType());
        userProfileConfig.setReferenceName(systemProfileField.getReferenceName());
        userProfileConfig.setDbColumnName(systemProfileField.getDbColumnName());
      }
      userProfileConfig.setCreatedUserId(-1L);
    }

  }

  private boolean isValidPresetUserProfileConfigs(List<UserProfileConfig> userProfileConfigs) {

    if (CollectionUtils.isEmpty(userProfileConfigs)) {
      return false;
    }

    Set<Integer> booleanValues = new HashSet<>();
    booleanValues.add(0);
    booleanValues.add(1);

    for (UserProfileConfig userProfileConfig: userProfileConfigs) {
      if ((null == userProfileConfig.getFieldCode()
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
              || !booleanValues.contains(userProfileConfig.getIsMandatory()))) {
        LOGGER.error("isValidPresetUserProfileConfigs(): invalidConfig={}", userProfileConfig);
        return false;
      }
    }
    return true;
  }

}
