// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.user.client.onboarding.dto.OnboardingDocumentDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingTemplateDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingTemplateListDTO;
import hr.wozai.service.user.client.onboarding.facade.OnboardingTemplateFacade;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.helper.OnboardingDocumentHelper;
import hr.wozai.service.user.server.helper.OnboardingTemplateHelper;
import hr.wozai.service.user.server.model.onboarding.OnboardingDocument;
import hr.wozai.service.user.server.model.onboarding.OnboardingTemplate;
import hr.wozai.service.user.server.model.userorg.ProfileTemplate;
import hr.wozai.service.user.server.service.OnboardingTemplateService;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.service.ProfileTemplateService;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-09
 */
@Service("onboardingTemplateFacade")
public class OnboardingTemplateFacadeImpl implements OnboardingTemplateFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingTemplateFacadeImpl.class);

  @Autowired
  OnboardingTemplateService onboardingTemplateService;

  @Autowired
  ProfileTemplateService profileTemplateService;

  @Override
  @LogAround
  public LongDTO addCustomOnboardingTemplate(
      long orgId, OnboardingTemplateDTO onboardingTemplateDTO, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ProfileTemplate profileTemplate = profileTemplateService.findTheOnlyProfileTemplateOfOrg(orgId);
      OnboardingTemplate onboardingTemplate = new OnboardingTemplate();
      BeanUtils.copyProperties(onboardingTemplateDTO, onboardingTemplate);
      onboardingTemplate.setOnboardingDocuments(OnboardingDocumentHelper.convertFromOnboardingDocumentDTOs(
          onboardingTemplateDTO.getOnboardingDocumentDTOs()));
      onboardingTemplate.setOrgId(orgId);
      onboardingTemplate.setIsPreset(0);
      onboardingTemplate.setProfileTemplateId(profileTemplate.getProfileTemplateId());
      onboardingTemplate.setCreatedUserId(actorUserId);
      long onboardingTemplateId = onboardingTemplateService.addOnboardingTemplate(onboardingTemplate);
      result.setData(onboardingTemplateId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addCustomOnboardingTemplate()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public OnboardingTemplateDTO getOnboardingTemplate(
      long orgId, long onboardingTemplateId, long actorUserId, long adminUserId) {

    OnboardingTemplateDTO result = new OnboardingTemplateDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      OnboardingTemplate onboardingTemplate =
          onboardingTemplateService.getOnboardingTemplate(orgId, onboardingTemplateId);
      if (null != onboardingTemplate) {
        BeanUtils.copyProperties(onboardingTemplate, result);
        result.setOnboardingDocumentDTOs(OnboardingDocumentHelper.convertFromOnboardingDocuments(
                onboardingTemplate.getOnboardingDocuments()));
      }
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getOnboardingTemplate()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public OnboardingTemplateListDTO listOnboardingTemplate(
      long orgId, long actorUserId, long adminUserId) {

    OnboardingTemplateListDTO result = new OnboardingTemplateListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<OnboardingTemplate> onboardingTemplates = onboardingTemplateService.listOnboardingTemplate(orgId);
      List<OnboardingTemplateDTO> onboardingTemplateDTOs = null;
      if (!CollectionUtils.isEmpty(onboardingTemplates)) {
        onboardingTemplateDTOs = new ArrayList<>();
        for (OnboardingTemplate onboardingTemplate: onboardingTemplates) {
          OnboardingTemplateDTO onboardingTemplateDTO = new OnboardingTemplateDTO();
          BeanUtils.copyProperties(onboardingTemplate, onboardingTemplateDTO);
          onboardingTemplateDTOs.add(onboardingTemplateDTO);
        }
      } else {
        onboardingTemplateDTOs = Collections.EMPTY_LIST;
      }
      result.setOnboardingTemplateDTOs(onboardingTemplateDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listOnboardingTemplate()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateOnboardingTemplate(
      long orgId, OnboardingTemplateDTO onboardingTemplateDTO, long actorUserId, long adminUserID) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      OnboardingTemplate onboardingTemplate = new OnboardingTemplate();
      BeanUtils.copyProperties(onboardingTemplateDTO, onboardingTemplate);
      onboardingTemplate.setOnboardingDocuments(OnboardingDocumentHelper.convertFromOnboardingDocumentDTOs(
          onboardingTemplateDTO.getOnboardingDocumentDTOs()));
      onboardingTemplate.setOrgId(orgId);
      onboardingTemplate.setLastModifiedUserId(actorUserId);
      onboardingTemplateService.updateOnboardingTemplate(onboardingTemplate);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateOnboardingTemplate()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO deleteOnboardingTemplate(
      long orgId, long onboardingTemplateId, long actorUserId, long adminUserID) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      onboardingTemplateService.deleteOnboardingTemplate(orgId, onboardingTemplateId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("deleteOnboardingTemplate()-error", e);
    }

    return result;
  }

}
