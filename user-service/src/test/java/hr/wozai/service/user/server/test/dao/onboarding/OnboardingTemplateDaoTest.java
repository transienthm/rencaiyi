// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.onboarding;

import hr.wozai.service.user.server.dao.onboarding.OnboardingTemplateDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.onboarding.OnboardingTemplate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
public class OnboardingTemplateDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(OnboardingTemplateDaoTest.class);

  @Autowired
  OnboardingTemplateDao onboardingTemplateDao;

  // data
  long userId = 10L;
  long orgId = 20L;
  long profileTemplateId = 30L;
  String displayName = "普通员工入职模板";
  String prologue = "欢迎你";
  String epilogue = "欢送你";
  OnboardingTemplate onboardingTemplate = new OnboardingTemplate();

  {
    onboardingTemplate.setPrologue(prologue);
    onboardingTemplate.setEpilogue(epilogue);
    onboardingTemplate.setProfileTemplateId(profileTemplateId);
    onboardingTemplate.setOrgId(orgId);
    onboardingTemplate.setDisplayName(displayName);
    onboardingTemplate.setIsPreset(1);
    onboardingTemplate.setCreatedUserId(userId);
  }

  @Before
  public void init() {}

  /**
   * Test:
   *  1) insertOnboardingTemplate()
   *  2) findOnboardingTemplateByOrgIdAndPrimaryKey()
   *  3) listOnboardingTemplateByOrgId()
   *  4) updateOnboardingTemplateByPrimaryKey()
   *  5) deleteOnboardingTemplateByPrimaryKey()
   */
  @Test
  public void testAll() {

    // 1) 2) 4)
    long insertedId = onboardingTemplateDao.insertOnboardingTemplate(onboardingTemplate);
    OnboardingTemplate insertdTemplate = onboardingTemplateDao.findOnboardingTemplateByOrgIdAndPrimaryKey(orgId, insertedId);
    Assert.assertEquals(displayName, insertdTemplate.getDisplayName());

    String updatedEpilogue = epilogue + prologue;
    insertdTemplate.setEpilogue(updatedEpilogue);
    onboardingTemplateDao.updateOnboardingTemplateByPrimaryKey(insertdTemplate);
    OnboardingTemplate updatedTemplate = onboardingTemplateDao.findOnboardingTemplateByOrgIdAndPrimaryKey(orgId, insertedId);
    Assert.assertEquals(updatedEpilogue, updatedTemplate.getEpilogue());


    // 3) 5)
    onboardingTemplateDao.insertOnboardingTemplate(onboardingTemplate);
    List<OnboardingTemplate> onboardingTemplates = onboardingTemplateDao.listOnboardingTemplateByOrgId(orgId);
    Assert.assertEquals(2, onboardingTemplates.size());
    onboardingTemplateDao.deleteOnboardingTemplateByPrimaryKey(orgId, insertedId, userId);
    OnboardingTemplate deletedTemplate = onboardingTemplateDao.findOnboardingTemplateByOrgIdAndPrimaryKey(orgId, insertedId);
    Assert.assertEquals(null, deletedTemplate);

  }


}
