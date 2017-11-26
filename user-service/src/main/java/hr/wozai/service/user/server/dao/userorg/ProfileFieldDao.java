// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import com.alibaba.fastjson.JSONObject;

import hr.wozai.service.user.server.model.userorg.ProfileField;

import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
@Repository("profileFieldDao")
public class ProfileFieldDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.ProfileFieldMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertProfileField(ProfileField profileField) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertProfileField", profileField);
    return profileField.getProfileFieldId();
  }

  public void batchInsertProfileField(List<ProfileField> profileFields) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertProfileField", profileFields);
  }

  public ProfileField findProfileFieldByOrgIdAndPrimaryKey(long orgId, long profileFieldId) {
    Map<String, Object> params = new HashMap<>();
    params.put("profileFieldId", profileFieldId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findProfileFieldByPrimaryKeyAndOrgId", params);
  }

  public ProfileField findProfileFieldByOrgIdAndPrimaryKeyForUpdate(long orgId, long profileFieldId) {
    Map<String, Object> params = new HashMap<>();
    params.put("profileFieldId", profileFieldId);
    params.put("orgId", orgId);
    params.put("forUpdate", 1);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findProfileFieldByPrimaryKeyAndOrgId", params);
  }

  public ProfileField findProfileFieldByReferenceName(long orgId, long profileTemplateId, String referenceName) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("profileTemplateId", profileTemplateId);
    params.put("referenceName", referenceName);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findProfileFieldByReferenceName", params);
  }

  public List<ProfileField> listProfileFieldByProfileTemplateId(long orgId, long profileTemplateId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("profileTemplateId", profileTemplateId);
    List<ProfileField> profileFields =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listProfileFieldByProfileTemplateId", params);
    return profileFields;
  }

  public List<ProfileField> listProfileFieldByProfileTemplateIdForUpdate(long orgId, long profileTemplateId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("profileTemplateId", profileTemplateId);
    params.put("forUpdate", 1);
    List<ProfileField> profileFields =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listProfileFieldByProfileTemplateId", params);
    return profileFields;
  }

  public List<ProfileField> listDataProfileFieldByProfileTemplateId(long orgId, long profileTemplateId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("profileTemplateId", profileTemplateId);
    List<ProfileField> profileFields =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listDataProfileFieldByProfileTemplateId", params);
    return profileFields;
  }

  public List<ProfileField> listDataProfileFieldByProfileTemplateIdForUpdate(long orgId, long profileTemplateId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("profileTemplateId", profileTemplateId);
    params.put("forUpdate", 1);
    List<ProfileField> profileFields =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listDataProfileFieldByProfileTemplateId", params);

    return profileFields;
  }

  public List<ProfileField> listDataProfileFieldByContainerId(long orgId, long containerId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("containerId", containerId);
    List<ProfileField> profileFields =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listDataProfileFieldByContainerId", params);

    return profileFields;
  }

  public int findNextLogicalIndexByProfileTemplateIdForUpdate(long orgId, long profileTemplateId) {
    Map<String, Object> params = new HashMap<>();
    params.put("profileTemplateId", profileTemplateId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findNextLogicalIndexByProfileTemplateIdForUpdate", params);
  }


  public int batchUpdateLogicalIndexAndContainerIdByPrimaryKey(List<ProfileField> profileFields) {
    if (CollectionUtils.isEmpty(profileFields)) {
      return 0;
    }
    return sqlSessionTemplate.update(
        BASE_PACKAGE + "batchUpdateLogicalIndexAndContainerIdByPrimaryKey", profileFields);

  }

  public int batchDeleteProfileFieldByPrimaryKey(long orgId, List<Long> profileFieldIds, long actorUserId) {
    if (CollectionUtils.isEmpty(profileFieldIds)) {
      return 0;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("profileFieldIds", profileFieldIds);
    params.put("lastModifiedUserId", actorUserId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "batchDeleteProfileFieldByPrimaryKey", params);
  }

  public int updateProfileFieldByPrimaryKeySelective(ProfileField profileField) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateProfileFieldByPrimaryKeySelective", profileField);
  }

  public long deleteProfileFieldByPrimaryKey(long orgId, long profileFieldId, long actorUserId) {
    ProfileField profileField = new ProfileField();
    profileField.setProfileFieldId(profileFieldId);
    profileField.setOrgId(orgId);
    profileField.setLastModifiedUserId(actorUserId);
    profileField.setIsDeleted(1);
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateProfileFieldByPrimaryKeySelective", profileField);
  }

}
