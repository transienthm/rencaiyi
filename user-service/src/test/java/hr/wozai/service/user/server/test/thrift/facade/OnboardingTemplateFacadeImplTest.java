// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.user.client.onboarding.dto.OnboardingDocumentDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingTemplateDTO;
import hr.wozai.service.user.client.onboarding.facade.OnboardingTemplateFacade;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.service.UserProfileService;
import hr.wozai.service.user.server.service.UserService;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class OnboardingTemplateFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(OnboardingTemplateFacadeImplTest.class);

  @Autowired
  OnboardingTemplateFacade onboardingTemplateFacade;

  // for assert
  @Autowired
  TeamService teamService;

  @Autowired
  UserService userService;

  @Autowired
  UserProfileService userProfileService;

  long orgId = 10L;
  long userId = 20L;
  String displayName = "普通员工日志模板";
  String prologue = "欢迎";
  String epilogue = "结束";
  long profileTemplateId = 50L;
  long documentId = 30L;
  OnboardingTemplateDTO onboardingTemplateDTO;
  OnboardingDocumentDTO onboardingDocumentDTO;

  {
    onboardingTemplateDTO = new OnboardingTemplateDTO();
    onboardingTemplateDTO.setOrgId(orgId);
    onboardingTemplateDTO.setDisplayName(displayName);
    onboardingTemplateDTO.setPrologue(prologue);
    onboardingTemplateDTO.setEpilogue(epilogue);
    onboardingTemplateDTO.setProfileTemplateId(profileTemplateId);
    onboardingTemplateDTO.setIsPreset(1);
    onboardingTemplateDTO.setCreatedUserId(userId);

    onboardingDocumentDTO = new OnboardingDocumentDTO();
    onboardingDocumentDTO.setOrgId(orgId);
    onboardingDocumentDTO.setDocumentId(documentId);
  }
  
  @Before
  public void init() {}

  @Test
  public void testAddCustomOnboardingTemplate() {

    List<OnboardingDocumentDTO> onboardingDocumentDTOs = new ArrayList<>();
    int docCount = 10;
    for (int i = 0; i < docCount; i++) {
      OnboardingDocumentDTO theDocDTO = new OnboardingDocumentDTO();
      BeanUtils.copyProperties(onboardingDocumentDTO, theDocDTO);
      theDocDTO.setLogicalIndex(i);
      theDocDTO.setDocumentId(i + 0L);
      onboardingDocumentDTOs.add(theDocDTO);
    }
    onboardingTemplateDTO.setOnboardingDocumentDTOs(onboardingDocumentDTOs);
    LongDTO addResult = onboardingTemplateFacade
        .addCustomOnboardingTemplate(orgId, onboardingTemplateDTO, userId, userId);

    OnboardingTemplateDTO insertedTemplate = onboardingTemplateFacade
        .getOnboardingTemplate(orgId, addResult.getData(), userId, userId);
    Assert.assertEquals(addResult.getData(), insertedTemplate.getOnboardingTemplateId().longValue());
  }

  @Test
  public void testUpdateOnboardingTemplate() {

    List<OnboardingDocumentDTO> onboardingDocumentDTOs = new ArrayList<>();
    int docCount = 10;
    for (int i = 0; i < docCount; i++) {
      OnboardingDocumentDTO theDocDTO = new OnboardingDocumentDTO();
      BeanUtils.copyProperties(onboardingDocumentDTO, theDocDTO);
      theDocDTO.setDocumentId(documentId + 1);
      theDocDTO.setLogicalIndex(i);
      theDocDTO.setDocumentId(i + 0L);
      onboardingDocumentDTOs.add(theDocDTO);
    }
    onboardingTemplateDTO.setOnboardingDocumentDTOs(onboardingDocumentDTOs);
    LongDTO addResult = onboardingTemplateFacade
        .addCustomOnboardingTemplate(orgId, onboardingTemplateDTO, userId, userId);

    OnboardingTemplateDTO insertedTemplate = onboardingTemplateFacade
        .getOnboardingTemplate(orgId, addResult.getData(), userId, userId);
    onboardingDocumentDTOs = insertedTemplate.getOnboardingDocumentDTOs();
    onboardingDocumentDTOs.remove(docCount - 1);
    long ninthDocId = onboardingDocumentDTOs.get(docCount - 2).getOnboardingDocumentId();
    onboardingDocumentDTOs.get(docCount - 2).setOnboardingDocumentId(null);
    onboardingTemplateFacade.updateOnboardingTemplate(orgId, insertedTemplate, userId, userId);

    insertedTemplate = onboardingTemplateFacade.getOnboardingTemplate(orgId, addResult.getData(), userId, userId);
    Assert.assertEquals(docCount - 1, insertedTemplate.getOnboardingDocumentDTOs().size());
    Assert.assertNotEquals(ninthDocId,
                           insertedTemplate.getOnboardingDocumentDTOs().get(docCount - 2).getOnboardingDocumentId().longValue());

  }

  @Test
  public void testDeleteOnboardingTemplate() {

    List<OnboardingDocumentDTO> onboardingDocumentDTOs = new ArrayList<>();
    int docCount = 10;
    for (int i = 0; i < docCount; i++) {
      OnboardingDocumentDTO theDocDTO = new OnboardingDocumentDTO();
      BeanUtils.copyProperties(onboardingDocumentDTO, theDocDTO);
      theDocDTO.setLogicalIndex(i);
      theDocDTO.setDocumentId(i + 0L);
      onboardingDocumentDTOs.add(theDocDTO);
    }
    onboardingTemplateDTO.setOnboardingDocumentDTOs(onboardingDocumentDTOs);
    LongDTO addResult = onboardingTemplateFacade
        .addCustomOnboardingTemplate(orgId, onboardingTemplateDTO, userId, userId);
    Assert.assertNotEquals(0, addResult.getData());

    onboardingTemplateFacade.deleteOnboardingTemplate(orgId, addResult.getData(), userId, userId);
    OnboardingTemplateDTO deletedTemplate =
        onboardingTemplateFacade.getOnboardingTemplate(orgId, addResult.getData(), userId, userId);
    Assert.assertEquals(ServiceStatus.OB_ONBOARDING_TEMPLATE_NOT_FOUND.getCode(), deletedTemplate.getServiceStatusDTO().getCode());

  }

}
