// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.userorg;

import hr.wozai.service.user.server.dao.userorg.ProfileTemplateDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.userorg.ProfileTemplate;
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
public class ProfileTemplateDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileTemplateDaoTest.class);

  @Autowired
  ProfileTemplateDao profileTemplateDao;

  // data
  long mockOrgId = 19999999L;
  long mockUserId = 29999999L;
  String displayName = "模板";
  ProfileTemplate profileTemplate = new ProfileTemplate();

  {
    profileTemplate.setOrgId(mockOrgId);
    profileTemplate.setDisplayName(displayName);
    profileTemplate.setIsPreset(1);
    profileTemplate.setCreatedUserId(mockUserId);
  }

  @Before
  public void setup() {
  }

  @Test
  public void testInsertProfileTemplate() throws Exception {
    profileTemplateDao.insertProfileTemplate(profileTemplate);
    ProfileTemplate insertedProfileTemplate =
        profileTemplateDao.findProfileTemplateByPrimaryKey(mockOrgId, profileTemplate.getProfileTemplateId());
    Assert.assertEquals(displayName, insertedProfileTemplate.getDisplayName());
  }

  @Test
  public void testFindProfileTemplateByPrimaryKey() throws Exception {
    profileTemplateDao.insertProfileTemplate(profileTemplate);

    ProfileTemplate insertedTemplate =
        profileTemplateDao.findProfileTemplateByPrimaryKey(mockOrgId, profileTemplate.getProfileTemplateId());
    Assert.assertEquals(displayName, insertedTemplate.getDisplayName());

    ProfileTemplate nullTemplate = profileTemplateDao.findProfileTemplateByPrimaryKey(0, 1);
    Assert.assertEquals(null, nullTemplate);
  }

  @Test
  public void testFindTheOnlyProfileTemplateByOrgId() {

    // prepare
    profileTemplateDao.insertProfileTemplate(profileTemplate);

    // verify
    ProfileTemplate addedPT = profileTemplateDao.findTheOnlyProfileTemplateByOrgId(mockOrgId);
    Assert.assertEquals(displayName, addedPT.getDisplayName());

  }

  @Test
  public void testListProfileTemplateByOrgId() throws Exception {
    int count = 10;
    for (int i = 0; i < count; i++) {
      profileTemplateDao.insertProfileTemplate(profileTemplate);
    }

    List<ProfileTemplate> insertedTemplates = profileTemplateDao.listProfileTemplateByOrgId(mockOrgId);
    Assert.assertEquals(count, insertedTemplates.size());

    List<ProfileTemplate> empptyTemplates = profileTemplateDao.listProfileTemplateByOrgId(mockOrgId + mockOrgId);
    Assert.assertEquals(0, empptyTemplates.size());
  }

  @Test
  public void testUpdateProfileTemplateDisplayName() throws Exception {
    profileTemplateDao.insertProfileTemplate(profileTemplate);
    String updatedName = displayName + displayName;
    profileTemplateDao.updateProfileTemplateDisplayName(
        mockOrgId, profileTemplate.getProfileTemplateId(), updatedName, mockUserId);

    ProfileTemplate updatedTemplate =
        profileTemplateDao.findProfileTemplateByPrimaryKey(mockOrgId, profileTemplate.getProfileTemplateId());
    Assert.assertEquals(updatedName, updatedTemplate.getDisplayName());
  }

  @Test
  public void testDeleteProfileTemplateByOrgId() throws Exception {
    profileTemplateDao.insertProfileTemplate(profileTemplate);
    profileTemplateDao.deleteProfileTemplateByOrgId(mockOrgId, profileTemplate.getProfileTemplateId(), mockUserId);

    ProfileTemplate deletedTemplate =
        profileTemplateDao.findProfileTemplateByPrimaryKey(mockOrgId, profileTemplate.getProfileTemplateId());
    Assert.assertEquals(null, deletedTemplate);
  }

}