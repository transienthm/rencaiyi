// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.ProfileTemplate;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
@Repository("profileTemplateDao")
public class ProfileTemplateDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.ProfileTemplateMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertProfileTemplate(ProfileTemplate profileTemplate) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertProfileTemplate", profileTemplate);
    return profileTemplate.getProfileTemplateId();
  }

  public ProfileTemplate findProfileTemplateByPrimaryKey(long orgId, long profileTemplateId) {
    Map<String, Object> params = new HashMap<>();
    params.put("profileTemplateId", profileTemplateId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findProfileTemplateByPrimaryKey", params);
  }

  /**
   * This method assume:
   *  each org has and has only one profileTemplate
   *
   * @param orgId
   * @return
   */
  public ProfileTemplate findTheOnlyProfileTemplateByOrgId(long orgId) {
    List<ProfileTemplate> profileTemplates =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listProfileTemplateByOrgId", orgId);
    if (CollectionUtils.isEmpty(profileTemplates)) {
      profileTemplates = Collections.EMPTY_LIST;
    }
    ProfileTemplate profileTemplate = null;
    if (!CollectionUtils.isEmpty(profileTemplates)) {
      profileTemplate = profileTemplates.get(0);
    }
    return profileTemplate;
  }

  public List<ProfileTemplate> listProfileTemplateByOrgId(long orgId) {
    List<ProfileTemplate> profileTemplates =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listProfileTemplateByOrgId", orgId);
    if (CollectionUtils.isEmpty(profileTemplates)) {
      profileTemplates = Collections.EMPTY_LIST;
    }
    return profileTemplates;
  }

  public int updateProfileTemplateDisplayName(long orgId, long profileTemplateId, String displayName, long actorId) {
    ProfileTemplate profileTemplate = new ProfileTemplate();
    profileTemplate.setProfileTemplateId(profileTemplateId);
    profileTemplate.setOrgId(orgId);
    profileTemplate.setDisplayName(displayName);
    profileTemplate.setLastModifiedUserId(actorId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateProfileTemplateByPrimaryKeySelective", profileTemplate);
  }

  public int deleteProfileTemplateByOrgId(long orgId, long profileTemplateId, long actorId) {
    ProfileTemplate profileTemplate = new ProfileTemplate();
    profileTemplate.setProfileTemplateId(profileTemplateId);
    profileTemplate.setOrgId(orgId);
    profileTemplate.setIsDeleted(1);
    profileTemplate.setLastModifiedUserId(actorId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateProfileTemplateByPrimaryKeySelective", profileTemplate);
  }
}
