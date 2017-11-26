// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.UserEmployment;

import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("userEmploymentDao")
public class UserEmploymentDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.UserEmploymentMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertUserEmployment(UserEmployment userEmployment) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertUserEmployment", userEmployment);
    return userEmployment.getUserEmploymentId();
  }

  public UserEmployment findUserEmploymentByOrgIdAndUserId(long orgId, long userId) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findUserEmploymentByOrgIdAndUserId", params);
  }

  public List<UserEmployment> listUserEmploymentByOrgIdAndUserId(long orgId, List<Long> userIds) {
    List<UserEmployment> userEmployments = Collections.EMPTY_LIST;
    if (!CollectionUtils.isEmpty(userIds)) {
      Map<Object, Object> params = new HashMap<>();
      params.put("orgId", orgId);
      params.put("userIds", userIds);
      userEmployments = sqlSessionTemplate.selectList(BASE_PACKAGE + "listUserEmploymentByOrgIdAndUserId", params);
    }
    return userEmployments;
  }

  public List<Long> listUserIdByOrgIdAndOnboardingHasApproved(
      long orgId, int hasApproved, int pageNumber, int pageSize) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("hasApproved", hasApproved);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    List<Long> userIds = sqlSessionTemplate
        .selectList(BASE_PACKAGE + "listUserIdByOrgIdAndOnboardingHasApproved", params);
    return userIds;
  }

  public int countUserIdByOrgIdAndOnboardingHasApproved(long orgId, int hasApproved) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("hasApproved", hasApproved);
    return sqlSessionTemplate.selectOne(
        BASE_PACKAGE + "countUserIdByOrgIdAndOnboardingHasApproved", params);
  }

  public List<Long> listUserIdByOrgIdAndOnboardingStatus(
      long orgId, int onboardingStatus, int pageNumber, int pageSize) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("onboardingStatus", onboardingStatus);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    List<Long> userIds = sqlSessionTemplate.selectList(BASE_PACKAGE + "listUserIdByOrgIdAndOnboardingStatus", params);
    return userIds;
  }

  public int countUserIdByOrgIdAndOnboardingStatus(long orgId, int onboardingStatus) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("onboardingStatus", onboardingStatus);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "countUserIdByOrgIdAndOnboardingStatus", params);
  }


  public List<Long> sublistUserIdByUserStatus(long orgId, List<Long> userIds, int userStatus) {
    List<Long> sublistUserIds = Collections.EMPTY_LIST;
    if (!CollectionUtils.isEmpty(userIds)) {
      Map<Object, Object> params = new HashMap<>();
      params.put("orgId", orgId);
      params.put("userIds", userIds);
      params.put("userStatus", userStatus);
      sublistUserIds = sqlSessionTemplate.selectList(BASE_PACKAGE + "sublistUserIdByUserStatus", params);
    }
    return sublistUserIds;
  }

  public List<Long> sublistUserIdByEmploymentStatus(long orgId, List<Long> userIds, int employmentStatus) {
    List<Long> sublistUserIds = Collections.EMPTY_LIST;
    if (!CollectionUtils.isEmpty(userIds)) {
      Map<Object, Object> params = new HashMap<>();
      params.put("orgId", orgId);
      params.put("userIds", userIds);
      params.put("employmentStatus", employmentStatus);
      sublistUserIds = sqlSessionTemplate
          .selectList(BASE_PACKAGE + "sublistUserIdNotResignedByEmploymentStatus", params);
    }
    return sublistUserIds;
  }

  public List<Long> listUserIdByOrgIdAndLimitOrderByEnrollDateDesc(long orgId, int listSize) {
    List<Long> userIds = null;
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("listSize", listSize);
    userIds = sqlSessionTemplate
        .selectList(BASE_PACKAGE + "listUserIdByOrgIdAndLimitOrderByEnrollDateDesc", params);
    return userIds;
  }

  public List<Long> listUserIdByOrgIdAndLimitOrderByComingAnniversaryGapAsc(long orgId, int listSize) {
    List<Long> userIds = null;
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("listSize", listSize);
    userIds = sqlSessionTemplate
        .selectList(BASE_PACKAGE + "listUserIdByOrgIdAndLimitOrderByComingAnniversaryGapAsc", params);
    return userIds;
  }

  public int updateUserEmploymentByOrgIdAndUserIdSelective(UserEmployment userEmployment) {
    cleanUserEmploymentForUpdate(userEmployment);
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateUserEmploymentByOrgIdAndUserIdSelective", userEmployment);
  }

  public int deleteUserEmploymentByOrgIdAndUserId(long orgId, long userId, long lastModifiedUserId) {
    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setUserId(userId);
    userEmployment.setIsDeleted(1);
    userEmployment.setLastModifiedUserId(lastModifiedUserId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateUserEmploymentByOrgIdAndUserIdSelective", userEmployment);
  }

  private void cleanUserEmploymentForUpdate(UserEmployment userEmployment) {
    userEmployment.setIsDeleted(null);
  }

}
