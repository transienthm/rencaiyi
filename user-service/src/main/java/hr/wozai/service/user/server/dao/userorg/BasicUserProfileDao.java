// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.wozai.service.user.server.model.userorg.BasicUserProfile;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-10
 */
@Repository("basicUserProfileDao")
public class BasicUserProfileDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.BasicUserProfileMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  /**
   * Insert basicUserProfile
   *
   * @param basicUserProfile
   * @return
   */
  public long insertBasicUserProfile(BasicUserProfile basicUserProfile) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertBasicUserProfile", basicUserProfile);
    return basicUserProfile.getBasicUserProfileId();
  }

  public BasicUserProfile findBasicUserProfileByOrgIdAndUserId(long orgId, long userId) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findBasicUserProfileByOrgIdAndUserId", params);
  }

  public List<BasicUserProfile> listBasicUserProfileByOrgIdAndUserId(long orgId, List<Long> userIds) {
    List<BasicUserProfile> basicUserProfiles = Collections.EMPTY_LIST;
    if (!org.springframework.util.CollectionUtils.isEmpty(userIds)) {
      Map<Object, Object> params = new HashMap<>();
      params.put("orgId", orgId);
      params.put("userIds", userIds);
      basicUserProfiles = sqlSessionTemplate.selectList(BASE_PACKAGE + "listBasicUserProfileByOrgIdAndUserId", params);
    }
    return basicUserProfiles;
  }

  public List<BasicUserProfile> listBasicUserProfileByCreatedUserId(long orgId, long createdUserId) {
    List<BasicUserProfile> basicUserProfiles = Collections.EMPTY_LIST;
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("createdUserId", createdUserId);
    basicUserProfiles = sqlSessionTemplate.selectList(BASE_PACKAGE + "listBasicUserProfileByCreatedUserId", params);
    return basicUserProfiles;
  }

  public List<BasicUserProfile> listBasicUserProfileByOrgIdOrderByCreatedTimeDesc(
      long orgId, int pageNumber, int pageSize) {
    List<BasicUserProfile> basicUserProfiles = Collections.EMPTY_LIST;
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    basicUserProfiles = sqlSessionTemplate.selectList(
        BASE_PACKAGE + "listBasicUserProfileByOrgIdOrderByCreatedTimeDesc", params);
    return basicUserProfiles;
  }

  public int countBasicUserProfileByOrgId(long orgId) {
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "countBasicUserProfileByOrgId", orgId);
  }

  public int updateBasicUserProfileByOrgIdAndUserId(BasicUserProfile basicUserProfile) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateBasicUserProfileByOrgIdAndUserId", basicUserProfile);
  }

  public int deleteBasicUserProfileByOrgIdAndUserId(long orgId, long userId, long actorUserId) {
    BasicUserProfile BasicUserProfile = new BasicUserProfile();
    BasicUserProfile.setOrgId(orgId);
    BasicUserProfile.setUserId(userId);
    BasicUserProfile.setIsDeleted(1);
    BasicUserProfile.setLastModifiedUserId(actorUserId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateBasicUserProfileByOrgIdAndUserId", BasicUserProfile);
  }

}
