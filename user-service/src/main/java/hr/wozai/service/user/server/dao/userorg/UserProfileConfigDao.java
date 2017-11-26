// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.wozai.service.user.server.model.userorg.UserProfileConfig;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
@Repository("userProfileConfigDao")
public class UserProfileConfigDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.UserProfileConfigMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public void batchInsertUserProfileConfig(List<UserProfileConfig> userProfileConfigs) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertUserProfileConfig", userProfileConfigs);
  }

  public UserProfileConfig findUserProfileConfigByOrgIdAndPrimaryKey(long orgId, long userProfileConfigId) {
    Map<String, Object> params = new HashMap<>();
    params.put("userProfileConfigId", userProfileConfigId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findUserProfileConfigByPrimaryKeyAndOrgId", params);
  }

  public UserProfileConfig findUserProfileConfigByOrgIdAndReferenceName(long orgId, String referenceName) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("referenceName", referenceName);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findUserProfileConfigByOrgIdAndReferenceName", params);
  }

  public List<UserProfileConfig> listUserProfileConfigByOrgId(long orgId) {
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listUserProfileConfigByOrgId", orgId);
  }

  public List<UserProfileConfig> listUserProfileConfigByOrgIdForUpdate(long orgId) {
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listUserProfileConfigByOrgIdForUpdate", orgId);
  }

  public int updateUserProfileConfigByPrimaryKeySelective(UserProfileConfig userProfileConfig) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateUserProfileConfigByPrimaryKeySelective", userProfileConfig);
  }

}
