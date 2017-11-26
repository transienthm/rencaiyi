// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.wozai.service.user.server.model.userorg.MetaUserProfile;
import hr.wozai.service.user.server.model.userorg.ProfileField;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-10
 */
@Repository("metaUserProfileDao")
public class MetaUserProfileDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.MetaUserProfileMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  /**
   * Insert metaUserProfile
   *
   * @param metaUserProfile
   * @return 
   */
  public long insertMetaUserProfile(MetaUserProfile metaUserProfile) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertMetaUserProfile", metaUserProfile);
    return metaUserProfile.getMetaUserProfileId();
  }

  public MetaUserProfile findMetaUserProfileWithFieldDataByOrgIdAndUserId(
      long orgId, long userId, List<ProfileField> profileFields) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    MetaUserProfile metaUserProfile =
        sqlSessionTemplate.selectOne(BASE_PACKAGE + "findMetaUserProfileWithoutFieldDataByOrgIdAndUserId", params);
    params.put("profileFields", profileFields);
    Map<String, Object> resultMap =
        sqlSessionTemplate.selectOne(BASE_PACKAGE + "findMetaUserProfileRawMapByOrgIdAndUserId", params);
    for (ProfileField profileField: profileFields) {
      if (resultMap.containsKey(profileField.getReferenceName())) {
        profileField.setDataValue((String) resultMap.get(profileField.getReferenceName()));
      }
    }
    metaUserProfile.setProfileFields(profileFields);
    return metaUserProfile;
  }

  public Map<String, Object> findMetaUserProfileRawMapByOrgIdAndUserId(
      long orgId, long userId, List<ProfileField> profileFields) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    params.put("profileFields", profileFields);
    Map<String, Object> resultMap = sqlSessionTemplate
        .selectOne(BASE_PACKAGE + "findMetaUserProfileRawMapByOrgIdAndUserId", params);
    return resultMap;
  }

  public Long findProfileTemplateIdByOrgIdAndUserId(long orgId, long userId) {
    Map<Object, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findProfileTemplateIdByOrgIdAndUserId", params);
  }

  public List<Long> listUserIdByProfileTemplateId(long orgId, long profileTemplateId) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("profileTemplateId", profileTemplateId);
    List<Long> userIds = sqlSessionTemplate.selectList(BASE_PACKAGE + "listUserIdByProfileTemplateId", params);
    if (CollectionUtils.isEmpty(userIds)) {
      userIds = Collections.EMPTY_LIST;
    }
    return userIds;
  }

  public int updateMetaUserProfileByOrgIdAndUserIdSelective(MetaUserProfile metaUserProfile) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateMetaUserProfileByOrgIdAndUserIdSelective", metaUserProfile);
  }

  public int deleteMetaUserProfile(long orgId, long userId, long actorUserId) {
    MetaUserProfile metaUserProfile = new MetaUserProfile();
    metaUserProfile.setOrgId(orgId);
    metaUserProfile.setUserId(userId);
    metaUserProfile.setLastModifiedUserId(actorUserId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "deleteMetaUserProfileOrgIdAndUserId", metaUserProfile);
  }

  public int wipeFieldValueByOrgIdAndProfileTemplateIdAndPhysicalIndex(
      long orgId, long profileTemplateId, long physicalIndex, long actorUserId) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("profileTemplateId", profileTemplateId);
    params.put("physicalIndex", physicalIndex);
    params.put("lastModifiedUserId", actorUserId);
    return sqlSessionTemplate.update(
        BASE_PACKAGE + "wipeFieldValueByOrgIdAndProfileTemplateIdAndPhysicalIndex", params);
  }

}
