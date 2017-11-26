// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.onboarding;

import hr.wozai.service.user.server.model.onboarding.OnboardingTemplate;
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
 * @Created: 2016-03-07
 */
@Repository("onboardingTemplateDao")
public class OnboardingTemplateDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.onboarding.OnboardingTemplateMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertOnboardingTemplate(OnboardingTemplate onboardingTemplate) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertOnboardingTemplate", onboardingTemplate);
    return onboardingTemplate.getOnboardingTemplateId();
  }

  public OnboardingTemplate findOnboardingTemplateByOrgIdAndPrimaryKey(long orgId, long onboardingTemplateId) {
    Map<String, Object> params = new HashMap<>();
    params.put("onboardingTemplateId", onboardingTemplateId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findOnboardingTemplateByPrimaryKeyAndOrgId", params);
  }

  public List<OnboardingTemplate> listOnboardingTemplateByOrgId(long orgId) {
    List<OnboardingTemplate> onboardingTemplates =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listOnboardingTemplateByOrgId", orgId);
    if (CollectionUtils.isEmpty(onboardingTemplates)) {
      onboardingTemplates = Collections.EMPTY_LIST;
    }
    return onboardingTemplates;
  }

  public long updateOnboardingTemplateByPrimaryKey(OnboardingTemplate onboardingTemplate) {
    return sqlSessionTemplate
        .update(BASE_PACKAGE + "updateOnboardingTemplateByPrimaryKeySelective", onboardingTemplate);
  }

  public long deleteOnboardingTemplateByPrimaryKey(long orgId, long onboardingTemplateId, long actorUserId) {
    OnboardingTemplate onboardingTemplate = new OnboardingTemplate();
    onboardingTemplate.setOnboardingTemplateId(onboardingTemplateId);
    onboardingTemplate.setLastModifiedUserId(actorUserId);
    onboardingTemplate.setOrgId(orgId);
    onboardingTemplate.setIsDeleted(1);
    System.out.println("onboardingTemplate=" + onboardingTemplate);
    return sqlSessionTemplate
        .update(BASE_PACKAGE + "updateOnboardingTemplateByPrimaryKeySelective", onboardingTemplate);
  }
}
