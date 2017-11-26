// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import hr.wozai.service.user.server.dao.userorg.MetaUserProfileDao;
import hr.wozai.service.user.server.model.onboarding.OnboardingDocument;
import hr.wozai.service.user.server.model.onboarding.OnboardingTemplate;
import hr.wozai.service.user.server.service.OnboardingTemplateService;
import hr.wozai.service.user.server.service.ProfileFieldService;
import hr.wozai.service.user.server.service.ProfileTemplateService;
import hr.wozai.service.user.server.test.base.TestBase;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-09
 */
public class OnboardingTemplateServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(OnboardingTemplateServiceImplTest.class);

  /******************* service to test *******************/

  @Autowired
  OnboardingTemplateService onboardingTemplateService;

  /******************* Helper vars *******************/

  @Autowired
  MetaUserProfileDao metaUserProfileDao;

  @Autowired
  ProfileFieldService profileFieldService;

  @Autowired
  ProfileTemplateService profileTemplateService;

  long orgId = 10L;
  long userId = 20L;
  String displayName = "普通员工日志模板";
  String prologue = "欢迎";
  String epilogue = "结束";
  long profileTemplateId = 50L;
  long documentId = 30L;
  long onboardingTemplateId = 40L;


  long mockOrgId = 299999999L;
  long mockUserId = 399999999L;
  long mockActorUserId = 499999999;

  OnboardingTemplate onboardingTemplate;
  OnboardingDocument onboardingDocument;

  {
    onboardingTemplate = new OnboardingTemplate();
    onboardingTemplate.setOrgId(orgId);
    onboardingTemplate.setDisplayName(displayName);
    onboardingTemplate.setPrologue(prologue);
    onboardingTemplate.setEpilogue(epilogue);
    onboardingTemplate.setProfileTemplateId(profileTemplateId);
    onboardingTemplate.setIsPreset(1);
    onboardingTemplate.setCreatedUserId(userId);

    onboardingDocument = new OnboardingDocument();
    onboardingDocument.setDocumentId(documentId);
    onboardingDocument.setCreatedUserId(userId);
  }

  @Before
  public void init() {}

  @Test
  public void testAddOnboardingTemplate() {
//
//    List<OnboardingDocument> onboardingDocuments = new ArrayList<>();
//    int docCount = 10;
//    for (int i = 0; i < docCount; i++) {
//      OnboardingDocument toAddDoc = new OnboardingDocument();
//      BeanUtils.copyProperties(onboardingDocument, toAddDoc);
//      toAddDoc.setLogicalIndex(1);
//      onboardingDocuments.add(toAddDoc);
//    }
//    onboardingTemplate.setOnboardingDocuments(onboardingDocuments);
//
//    try {
//      onboardingTemplateService.addOnboardingTemplate(onboardingTemplate);
//    } catch (ServiceStatusException e) {
//      LOGGER.info("Oops-1");
//      Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), e.getServiceStatus().getCode());
//    }
//
//    onboardingDocuments.clear();
//    for (int i = 0; i < docCount; i++) {
//      OnboardingDocument toAddDoc = new OnboardingDocument();
//      BeanUtils.copyProperties(onboardingDocument, toAddDoc);
//      toAddDoc.setLogicalIndex(i);
//      toAddDoc.setDocumentId(i + 0L);
//      onboardingDocuments.add(toAddDoc);
//    }
//    long insertedTemplateId = onboardingTemplateService.addOnboardingTemplate(onboardingTemplate);
//
//    OnboardingTemplate insertedTemplate = onboardingTemplateService.getOnboardingTemplate(orgId, insertedTemplateId);
//    Assert.assertEquals(insertedTemplateId, insertedTemplate.getOnboardingTemplateId().longValue());
//    Assert.assertEquals(docCount, insertedTemplate.getOnboardingDocuments().size());

  }

  @Test
  public void testUpdateOnboardingTemplate() {

    List<OnboardingDocument> onboardingDocuments = new ArrayList<>();
    int docCount = 10;
    for (int i = 0; i < docCount; i++) {
      OnboardingDocument toAddDoc = new OnboardingDocument();
      BeanUtils.copyProperties(onboardingDocument, toAddDoc);
      toAddDoc.setLogicalIndex(i);
      toAddDoc.setDocumentId(i + 0L);
      onboardingDocuments.add(toAddDoc);
    }
    onboardingTemplate.setOnboardingDocuments(onboardingDocuments);
    long insertedTemplateId = onboardingTemplateService.addOnboardingTemplate(onboardingTemplate);

    OnboardingTemplate insertedTemplate = onboardingTemplateService.getOnboardingTemplate(orgId, insertedTemplateId);
    onboardingDocuments = insertedTemplate.getOnboardingDocuments();
    long sixthDocId = onboardingDocuments.get(docCount - 4).getOnboardingDocumentId();
    onboardingDocuments.get(docCount - 4).setOnboardingDocumentId(null);
    onboardingTemplateService.updateOnboardingTemplate(insertedTemplate);

    insertedTemplate = onboardingTemplateService.getOnboardingTemplate(orgId, insertedTemplateId);
    onboardingDocuments = insertedTemplate.getOnboardingDocuments();
    Assert.assertEquals(docCount, onboardingDocuments.size());
    Assert.assertNotEquals(sixthDocId, onboardingDocuments.get(docCount - 4).getOnboardingDocumentId().longValue());

  }

  @Test
  public void testDeleteOnboardingTemplate() {

//    List<OnboardingDocument> onboardingDocuments = new ArrayList<>();
//    int docCount = 10;
//    for (int i = 0; i < docCount; i++) {
//      OnboardingDocument toAddDoc = new OnboardingDocument();
//      BeanUtils.copyProperties(onboardingDocument, toAddDoc);
//      toAddDoc.setLogicalIndex(i);
//      toAddDoc.setDocumentId(i + 0L);
//      onboardingDocuments.add(toAddDoc);
//    }
//    onboardingTemplate.setOnboardingDocuments(onboardingDocuments);
//    onboardingTemplate = new OnboardingTemplate();
//    onboardingTemplate.setOrgId(mockOrgId);
//    onboardingTemplate.setDisplayName(displayName);
//    onboardingTemplate.setPrologue(prologue);
//    onboardingTemplate.setEpilogue(epilogue);
//    onboardingTemplate.setProfileTemplateId(profileTemplateId);
//    onboardingTemplate.setIsPreset(1);
//    onboardingTemplate.setCreatedUserId(mockActorUserId);
//    long insertedTemplateId = onboardingTemplateService.addOnboardingTemplate(onboardingTemplate);
//
//    // adnormal-case-1: has user in use
//    long profileTemplateId = InitializationUtils
//        .initPresetProfileTemplateAndFields(profileTemplateService, profileFieldService, mockOrgId);
//    UserProfile userProfile = new UserProfile();
//    userProfile.setOrgId(mockOrgId);
//    userProfile.setUserId(mockUserId);
//    userProfile.setOnboardingTemplateId(insertedTemplateId);
//    userProfile.setProfileTemplateId(profileTemplateId);
////    userProfile.setUserStatus(UserStatus.INVITED.getCode());
//    userProfile.setCreatedUserId(mockActorUserId);
//    List<ProfileField> dataFields = profileFieldService
//        .listDataProfileFieldOfTemplate(mockOrgId, profileTemplateId);
//    for (ProfileField dataField: dataFields) {
//      if (dataField.getIsMandatory() == 1) {
//        dataField.setDataValue(dataField.getDisplayName());
//      }
//    }
//    userProfile.setProfileFields(dataFields);
//    metaUserProfileDao.insertMetaUserProfile(userProfile);
//    try {
//      onboardingTemplateService.deleteOnboardingTemplate(mockOrgId, insertedTemplateId, mockActorUserId);
//    } catch (ServiceStatusException e) {
//      System.out.println("Gotcha-1");
//      Assert.assertEquals(ServiceStatus.OB_ONBOARDING_TEMPLATE_IN_USE.getCode(), e.getServiceStatus().getCode());
//    }
//
//    // normal-case: delete when not in use
//    metaUserProfileDao.deleteUserProfile(mockOrgId, userProfile.getUserId(), mockActorUserId);
//    onboardingTemplateService.deleteOnboardingTemplate(mockOrgId, insertedTemplateId, userId);
//
//    // adnormal-case-2
//    try {
//      onboardingTemplateService.getOnboardingTemplate(orgId, insertedTemplateId);
//    } catch (ServiceStatusException e) {
//      LOGGER.info("Gotcha-2");
//      Assert.assertEquals(ServiceStatus.OB_ONBOARDING_TEMPLATE_NOT_FOUND.getCode(), e.getServiceStatus().getCode());
//    }
//
  }

}
