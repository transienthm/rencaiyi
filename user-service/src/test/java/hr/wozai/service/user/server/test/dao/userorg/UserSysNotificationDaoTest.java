// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.dao.userorg.UserSysNotificationDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.OrgMember;
import hr.wozai.service.user.server.model.userorg.UserSysNotification;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserSysNotificationDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(UserSysNotificationDaoTest.class);

  @Autowired
  UserSysNotificationDao userSysNotificationDao;

  private long mockOrgId = 19999999L;
  private long mockActorUserId = 39999999L;
  private long mockObjectIdOne = 29999998L;
  private long mockObjectIdTwo = 29999999L;
  private int mockObjectTypeOne = 1;
  private int mockObjectTypeTwo = 2;
  private int needEmail = 1;
  private int needMsg = 1;

  UserSysNotification userSysNotification = null;

  @Before
  public void setup() {

    userSysNotification = new UserSysNotification();
    userSysNotification.setOrgId(mockOrgId);
    userSysNotification.setNeedEmail(needEmail);
    userSysNotification.setNeedMessageCenter(needMsg);
    userSysNotification.setCreatedUserId(mockActorUserId);
  }

  @Test
  public void testInsertUserSysNotification() {

    userSysNotification.setObjectId(mockObjectIdOne);
    userSysNotification.setObjectType(mockObjectTypeOne);
    userSysNotification.setNotifyUserId(mockActorUserId);
    userSysNotification.setLogicalIndex(0);

    userSysNotificationDao.insertUserSysNotification(userSysNotification);

  }

  @Test
  public void testListUserSysNotificationByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex() {

    int countOne = 3;
    int countTwo = 4;
    for (int i = 0; i < countOne; i++) {
      userSysNotification.setObjectId(mockObjectIdOne);
      userSysNotification.setObjectType(mockObjectTypeOne);
      userSysNotification.setNotifyUserId(mockActorUserId);
      userSysNotification.setLogicalIndex(i);
      userSysNotificationDao.insertUserSysNotification(userSysNotification);
    }

    for (int i = 0; i < countTwo; i++) {
      userSysNotification.setObjectId(mockObjectIdTwo);
      userSysNotification.setObjectType(mockObjectTypeTwo);
      userSysNotification.setNotifyUserId(mockActorUserId);
      userSysNotification.setLogicalIndex(i);
      userSysNotificationDao.insertUserSysNotification(userSysNotification);
    }

    List<UserSysNotification> userSysNotifications = userSysNotificationDao
        .listUserSysNotificationByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex(
            mockOrgId, mockObjectIdOne, mockObjectTypeOne);
    Assert.assertEquals(countOne, userSysNotifications.size());

    userSysNotifications = userSysNotificationDao
        .listUserSysNotificationByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex(
            mockOrgId, mockObjectIdTwo, mockObjectTypeTwo);
    Assert.assertEquals(countTwo, userSysNotifications.size());

  }

  @Test
  public void testListUserIdByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex() {

    int countOne = 3;
    int countTwo = 4;
    for (int i = 0; i < countOne; i++) {
      userSysNotification.setObjectId(mockObjectIdOne);
      userSysNotification.setObjectType(mockObjectTypeOne);
      userSysNotification.setNotifyUserId(mockActorUserId);
      userSysNotification.setLogicalIndex(i);
      userSysNotificationDao.insertUserSysNotification(userSysNotification);
    }

    for (int i = 0; i < countTwo; i++) {
      userSysNotification.setObjectId(mockObjectIdTwo);
      userSysNotification.setObjectType(mockObjectTypeTwo);
      userSysNotification.setNotifyUserId(mockActorUserId);
      userSysNotification.setLogicalIndex(i);
      userSysNotificationDao.insertUserSysNotification(userSysNotification);
    }

    List<Long> userIds = userSysNotificationDao
        .listUserIdByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex(mockOrgId, mockObjectIdOne, mockObjectTypeOne);
    Assert.assertEquals(countOne, userIds.size());

    userIds = userSysNotificationDao
        .listUserIdByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex(mockOrgId, mockObjectIdTwo, mockObjectTypeTwo);
    Assert.assertEquals(countTwo, userIds.size());

  }

}
