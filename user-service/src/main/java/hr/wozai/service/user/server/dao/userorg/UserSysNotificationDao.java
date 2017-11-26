// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.UserSysNotification;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("userSysNotificationDao")
public class UserSysNotificationDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.UserSysNotificationMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertUserSysNotification(UserSysNotification userSysNotification) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertUserSysNotification", userSysNotification);
    return userSysNotification.getUserSysNotificationId();
  }
  public List<UserSysNotification> listUserSysNotificationByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex(
      long orgId, long objectId, int objectType) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("objectId", objectId);
    params.put("objectType", objectType);
    List<UserSysNotification> userSysNotifications = sqlSessionTemplate.selectList(
        BASE_PACKAGE + "listUserSysNotificationByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex", params);
    return userSysNotifications;
  }

  public List<Long> listUserIdByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex (
      long orgId, long objectId, int objectType) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("objectId", objectId);
    params.put("objectType", objectType);
    List<Long> userIds = sqlSessionTemplate
        .selectList(BASE_PACKAGE + "listUserIdByOrgIdAndObjectIdAndObjectTypeOrderByLogicalIndex", params);
    return userIds;
  }

}
