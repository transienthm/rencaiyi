// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.dao.userorg.ProfileTemplateDao;
import hr.wozai.service.user.server.model.userorg.ProfileTemplate;
import hr.wozai.service.user.server.service.ProfileTemplateService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-25
 */
public class ProfileTemplateServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ProfileTemplateServiceImplTest.class);

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Autowired
  private ProfileTemplateService profileTemplateService;

  // data
  long orgId = 19999999L;
  long userId = 29999999L;
  String displayName = "模板";
  ProfileTemplate profileTemplate = new ProfileTemplate();

  {
    profileTemplate.setOrgId(orgId);
    profileTemplate.setDisplayName(displayName);
    profileTemplate.setIsPreset(1);
    profileTemplate.setCreatedUserId(userId);
  }

  @Before
  public void init() {
  }

  @Test
  public void testFindTheOnlyProfileTemplateOfOrg() {

    // prepare

    profileTemplate.setDisplayName(displayName);
    profileTemplateService.addProfileTemplate(profileTemplate);
    ProfileTemplate insertedTemplate =
        profileTemplateService.getProfileTemplate(orgId, profileTemplate.getProfileTemplateId());

    // verify
    ProfileTemplate thePT = profileTemplateService.findTheOnlyProfileTemplateOfOrg(orgId);
    Assert.assertNotNull(thePT);

  }

  @Test
  public void testAddProfileTemplate() throws Exception {

    profileTemplate.setDisplayName(displayName);
    profileTemplateService.addProfileTemplate(profileTemplate);
    ProfileTemplate insertedTemplate =
        profileTemplateService.getProfileTemplate(orgId, profileTemplate.getProfileTemplateId());
    Assert.assertEquals(displayName, insertedTemplate.getDisplayName());

  }

  @Test
  public void testGetProfileTemplate() throws Exception {

    profileTemplateService.addProfileTemplate(profileTemplate);
    ProfileTemplate insertedTemplate =
        profileTemplateService.getProfileTemplate(orgId, profileTemplate.getProfileTemplateId());
    Assert.assertEquals(displayName, insertedTemplate.getDisplayName());

    try {
      insertedTemplate = profileTemplateService.getProfileTemplate(orgId, orgId);
    } catch (ServiceStatusException e) {
      Assert.assertEquals(ServiceStatus.UP_PROFILE_TEMPLATE_NOT_FOUND.getCode(), e.getServiceStatus().getCode());
    }
  }

  @Test
  public void testListProfileTemplateId() throws Exception {

    int count=  10;
    for (int i = 0; i < count; i++) {
      profileTemplateService.addProfileTemplate(profileTemplate);
    }

    List<ProfileTemplate> insertedTemplates = profileTemplateService.listProfileTemplateId(orgId);
    Assert.assertEquals(count, insertedTemplates.size());

    insertedTemplates = profileTemplateService.listProfileTemplateId(orgId + orgId);
    Assert.assertEquals(0, insertedTemplates.size());

  }

  /**
   * Normal success
   *
   * @throws Exception
   */
  @Test
  public void testUpdateProfileTemplateDisplayNameCase01() throws Exception {

    profileTemplateService.addProfileTemplate(profileTemplate);
    ProfileTemplate insertedTemplate =
        profileTemplateService.getProfileTemplate(orgId, profileTemplate.getProfileTemplateId());
    String updatedDisplayName = displayName + displayName;
    profileTemplateService.updateProfileTemplateDisplayName(
        orgId, insertedTemplate.getProfileTemplateId(), updatedDisplayName, userId);

    ProfileTemplate updatedTemplate =
        profileTemplateService.getProfileTemplate(orgId, profileTemplate.getProfileTemplateId());
    Assert.assertEquals(updatedDisplayName, updatedTemplate.getDisplayName());
  }

  /**
   * Abnormal: empty displayName
   *
   * @throws Exception
   */
  @Test
  public void testUpdateProfileTemplateDisplayNameCase02() throws Exception {

    // prepare
    profileTemplateService.addProfileTemplate(profileTemplate);
    ProfileTemplate insertedTemplate =
        profileTemplateService.getProfileTemplate(orgId, profileTemplate.getProfileTemplateId());

    // verify
    String updatedDisplayName = null;
    thrown.expect(ServiceStatusException.class);
    profileTemplateService.updateProfileTemplateDisplayName(
        orgId, insertedTemplate.getProfileTemplateId(), updatedDisplayName, userId);

  }

  @Test
  public void testDeleteProfileTemplate() throws Exception {

    profileTemplateService.addProfileTemplate(profileTemplate);
    ProfileTemplate insertedTemplate =
        profileTemplateService.getProfileTemplate(orgId, profileTemplate.getProfileTemplateId());
    Assert.assertNotNull(insertedTemplate);

    profileTemplateService.deleteProfileTemplate(orgId, insertedTemplate.getProfileTemplateId(), userId);
    try {
      ProfileTemplate deletedTemplate =
          profileTemplateService.getProfileTemplate(orgId, profileTemplate.getProfileTemplateId());
    } catch (ServiceStatusException e) {
      Assert.assertEquals(ServiceStatus.UP_PROFILE_TEMPLATE_NOT_FOUND.getCode(), e.getServiceStatus().getCode());
    }


  }
}
