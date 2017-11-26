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

import hr.wozai.service.user.server.model.userorg.CoreUserProfile;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-10
 */
@Repository("coreUserProfileDao")
public class CoreUserProfileDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.CoreUserProfileMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  /**
   * Insert coreUserProfile
   *
   * @param coreUserProfile
   * @return
   */
  public long insertCoreUserProfile(CoreUserProfile coreUserProfile) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertCoreUserProfile", coreUserProfile);
    return coreUserProfile.getCoreUserProfileId();
  }

  public CoreUserProfile findCoreUserProfileByOrgIdAndUserId(long orgId, long userId) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findCoreUserProfileByOrgIdAndUserId", params);
  }

  public List<Long> listUserIdByOnboardingTemplateId(long orgId, long onboardingTemplateId) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("onboardingTemplateId", onboardingTemplateId);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listUserIdByOnboardingTemplateId", params);
  }

  public List<CoreUserProfile> listCoreUserProfileByOrgIdAndUserId(long orgId, List<Long> userIds) {
    List<CoreUserProfile> coreUserProfiles = Collections.EMPTY_LIST;
    if (!org.springframework.util.CollectionUtils.isEmpty(userIds)) {
      Map<Object, Object> params = new HashMap<>();
      params.put("orgId", orgId);
      params.put("userIds", userIds);
      coreUserProfiles = sqlSessionTemplate.selectList(BASE_PACKAGE + "listCoreUserProfileByOrgIdAndUserId", params);
    }
    return coreUserProfiles;
  }

  public List<CoreUserProfile> listCoreUserProfileByCreatedUserId(long orgId, long createdUserId) {
    List<CoreUserProfile> coreUserProfiles = Collections.EMPTY_LIST;
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("createdUserId", createdUserId);
    coreUserProfiles = sqlSessionTemplate.selectList(BASE_PACKAGE + "listCoreUserProfileByCreatedUserId", params);
    return coreUserProfiles;
  }

  public List<CoreUserProfile> listCoreUserProfileByOrgIdOrderByCreatedTimeDesc(
      long orgId, int pageNumber, int pageSize) {
    List<CoreUserProfile> coreUserProfiles = Collections.EMPTY_LIST;
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    coreUserProfiles = sqlSessionTemplate.selectList(
        BASE_PACKAGE + "listCoreUserProfileByOrgIdOrderByCreatedTimeDesc", params);
    return coreUserProfiles;
  }

  public int countCoreUserProfileByOrgId(long orgId) {
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "countCoreUserProfileByOrgId", orgId);
  }

  public List<CoreUserProfile> listCoreUserProfileFromOnboardingByOrgIdAndHasApproved(
      long orgId, int hasApproved, int pageNumber, int pageSize) {
    List<CoreUserProfile> coreUserProfiles = Collections.EMPTY_LIST;
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("hasApproved", hasApproved);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    coreUserProfiles = sqlSessionTemplate.selectList(
        BASE_PACKAGE + "listCoreUserProfileFromOnboardingByOrgIdAndHasApproved", params);
    return coreUserProfiles;
  }

  public int countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(long orgId, int hasApproved) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("hasApproved", hasApproved);
    return sqlSessionTemplate.selectOne(
        BASE_PACKAGE + "countCoreUserProfileFromOnboardingByOrgIdAndHasApproved", params);
  }

  public List<CoreUserProfile> listCoreUserProfileFromImportByUserStatus(
      long orgId, int userStatus, int pageNumber, int pageSize) {
    List<CoreUserProfile> coreUserProfiles = Collections.EMPTY_LIST;
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userStatus", userStatus);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    coreUserProfiles = sqlSessionTemplate.selectList(
        BASE_PACKAGE + "listCoreUserProfileFromImportByUserStatus", params);
    return coreUserProfiles;
  }

  public int countCoreUserProfileFromImportByUserStatus(long orgId, int userStatus) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userStatus", userStatus);
    return sqlSessionTemplate.selectOne(
        BASE_PACKAGE + "countCoreUserProfileFromImportByUserStatus", params);
  }

  public List<CoreUserProfile> listAllCoreUserProfileFromImport(long orgId, int pageNumber, int pageSize) {
    List<CoreUserProfile> coreUserProfiles = Collections.EMPTY_LIST;
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    coreUserProfiles = sqlSessionTemplate.selectList(
        BASE_PACKAGE + "listAllCoreUserProfileFromImport", params);
    return coreUserProfiles;
  }

  public int countAllCoreUserProfileFromImport(long orgId) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(
        BASE_PACKAGE + "countAllCoreUserProfileFromImport", params);
  }

  public List<CoreUserProfile> listFullNameAndEmailAddressWhichIsNotResignedForUpdate(long orgId) {
    return sqlSessionTemplate
        .selectList(BASE_PACKAGE + "listFullNameAndEmailAddressWhichIsNotResignedForUpdate", orgId);
  }

  public int updateCoreUserProfileByOrgIdAndUserId(CoreUserProfile coreUserProfile) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateCoreUserProfileByOrgIdAndUserId", coreUserProfile);
  }

  public int deleteCoreUserProfileByOrgIdAndUserId(long orgId, long userId, long actorUserId) {
    CoreUserProfile CoreUserProfile = new CoreUserProfile();
    CoreUserProfile.setOrgId(orgId);
    CoreUserProfile.setUserId(userId);
    CoreUserProfile.setIsDeleted(1);
    CoreUserProfile.setLastModifiedUserId(actorUserId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateCoreUserProfileByOrgIdAndUserId", CoreUserProfile);
  }

}
