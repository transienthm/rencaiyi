// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.userorg;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import hr.wozai.service.servicecommons.commons.enums.DataType;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.user.server.dao.userorg.ProfileFieldDao;
import hr.wozai.service.user.server.dao.userorg.UserProfileConfigDao;
import hr.wozai.service.user.server.model.userorg.ProfileField;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
public class UserProfileConfigDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(UserProfileConfigDaoTest.class);

  @Autowired
  UserProfileConfigDao userProfileConfigDao;

  // data

  long mockOrgId = 299999999L;
  long mockUserId = 399999999L;
  long mockActorUserId = 499999999;
  long mochAdminUserId = 599999999;
  int isEnabled = 0;

  UserProfileConfig userProfileConfig;

  {
    userProfileConfig = new UserProfileConfig();
    userProfileConfig.setOrgId(mockOrgId);
    userProfileConfig.setIsSystemRequired(1);
    userProfileConfig.setIsOnboardingStaffEditable(1);
    userProfileConfig.setIsActiveStaffEditable(1);
    userProfileConfig.setIsEnabled(isEnabled);
    userProfileConfig.setIsEnabledEditable(1);
    userProfileConfig.setIsMandatory(0);
    userProfileConfig.setCreatedUserId(-1L);
  }

  @Before
  public void setup() {}

  @Test
  public void testBatchInsertUserProfileConfig() {

    List<UserProfileConfig> userProfileConfigs = new ArrayList<>();
    int configCount = 30;
    for (int i = 0; i < configCount; i++) {
      UserProfileConfig newUserProfileConfig = new UserProfileConfig();
      BeanUtils.copyProperties(userProfileConfig, newUserProfileConfig);
      newUserProfileConfig.setFieldCode(i);
      newUserProfileConfig.setReferenceName(i + "");
      newUserProfileConfig.setDbColumnName(i + "");
      newUserProfileConfig.setDataType(1);
      userProfileConfigs.add(newUserProfileConfig);
    }

    userProfileConfigDao.batchInsertUserProfileConfig(userProfileConfigs);

  }

  @Test
  public void testListUserProfileConfigByOrgId() {

    // prepare
    List<UserProfileConfig> userProfileConfigs = new ArrayList<>();
    int configCount = 30;
    for (int i = 0; i < configCount; i++) {
      UserProfileConfig newUserProfileConfig = new UserProfileConfig();
      BeanUtils.copyProperties(userProfileConfig, newUserProfileConfig);
      newUserProfileConfig.setFieldCode(i);
      newUserProfileConfig.setReferenceName(i + "");
      newUserProfileConfig.setDbColumnName(i + "");
      newUserProfileConfig.setDataType(1);
      userProfileConfigs.add(newUserProfileConfig);
    }
    userProfileConfigDao.batchInsertUserProfileConfig(userProfileConfigs);

    // verify
    List<UserProfileConfig> addUserProfileConfigs = userProfileConfigDao.listUserProfileConfigByOrgId(mockOrgId);
    Assert.assertEquals(configCount, addUserProfileConfigs.size());

  }

  @Test
  public void testFindUserProfileConfigByOrgIdAndPrimaryKey() {

    // prepare
    List<UserProfileConfig> userProfileConfigs = new ArrayList<>();
    int configCount = 30;
    for (int i = 0; i < configCount; i++) {
      UserProfileConfig newUserProfileConfig = new UserProfileConfig();
      BeanUtils.copyProperties(userProfileConfig, newUserProfileConfig);
      newUserProfileConfig.setFieldCode(i);
      newUserProfileConfig.setReferenceName(i + "");
      newUserProfileConfig.setDbColumnName(i + "");
      newUserProfileConfig.setDataType(1);
      userProfileConfigs.add(newUserProfileConfig);
    }
    userProfileConfigDao.batchInsertUserProfileConfig(userProfileConfigs);
    List<UserProfileConfig> addUserProfileConfigs = userProfileConfigDao.listUserProfileConfigByOrgId(mockOrgId);
    Assert.assertEquals(configCount, addUserProfileConfigs.size());

    // verify
    long firstId = addUserProfileConfigs.get(0).getUserProfileConfigId();
    String firstReferenceName = addUserProfileConfigs.get(0).getReferenceName();
    UserProfileConfig firstAddedConfig =
        userProfileConfigDao.findUserProfileConfigByOrgIdAndPrimaryKey(mockOrgId, firstId);
    Assert.assertEquals(firstReferenceName, firstAddedConfig.getReferenceName());

  }

  @Test
  public void testFindUserProfileConfigByOrgIdAndReferenceName() {

    // prepare
    List<UserProfileConfig> userProfileConfigs = new ArrayList<>();
    int configCount = 30;
    for (int i = 0; i < configCount; i++) {
      UserProfileConfig newUserProfileConfig = new UserProfileConfig();
      BeanUtils.copyProperties(userProfileConfig, newUserProfileConfig);
      newUserProfileConfig.setFieldCode(i);
      newUserProfileConfig.setReferenceName(i + "");
      newUserProfileConfig.setDbColumnName(i + "");
      newUserProfileConfig.setDataType(1);
      userProfileConfigs.add(newUserProfileConfig);
    }
    userProfileConfigDao.batchInsertUserProfileConfig(userProfileConfigs);
    List<UserProfileConfig> addUserProfileConfigs = userProfileConfigDao.listUserProfileConfigByOrgId(mockOrgId);
    Assert.assertEquals(configCount, addUserProfileConfigs.size());

    // verify
    String firstReferenceName = addUserProfileConfigs.get(0).getReferenceName();
    long firstId = addUserProfileConfigs.get(0).getUserProfileConfigId();
    UserProfileConfig firstAddedConfig =
        userProfileConfigDao.findUserProfileConfigByOrgIdAndReferenceName(mockOrgId, firstReferenceName);
    Assert.assertEquals(firstId, firstAddedConfig.getUserProfileConfigId().longValue());

  }

  @Test
  public void testUpdateUserProfileConfigByPrimaryKeySelective() {

    // prepare
    List<UserProfileConfig> userProfileConfigs = new ArrayList<>();
    int configCount = 30;
    for (int i = 0; i < configCount; i++) {
      UserProfileConfig newUserProfileConfig = new UserProfileConfig();
      BeanUtils.copyProperties(userProfileConfig, newUserProfileConfig);
      newUserProfileConfig.setFieldCode(i);
      newUserProfileConfig.setReferenceName(i + "");
      newUserProfileConfig.setDbColumnName(i + "");
      newUserProfileConfig.setDataType(1);
      userProfileConfigs.add(newUserProfileConfig);
    }
    userProfileConfigDao.batchInsertUserProfileConfig(userProfileConfigs);
    List<UserProfileConfig> addUserProfileConfigs = userProfileConfigDao.listUserProfileConfigByOrgId(mockOrgId);
    Assert.assertEquals(configCount, addUserProfileConfigs.size());

    // verify
    UserProfileConfig firstUserProfileConfig = addUserProfileConfigs.get(0);
    int updatedIsEnabled = (isEnabled == 1) ? 0 : 1;
    firstUserProfileConfig.setIsEnabled(updatedIsEnabled);
    userProfileConfigDao.updateUserProfileConfigByPrimaryKeySelective(firstUserProfileConfig);

    UserProfileConfig updatedFirstUPC = userProfileConfigDao
        .findUserProfileConfigByOrgIdAndPrimaryKey(mockOrgId, firstUserProfileConfig.getUserProfileConfigId());
    Assert.assertEquals(updatedIsEnabled, updatedFirstUPC.getIsEnabled().intValue());

  }


}
