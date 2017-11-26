// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.userorg;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import hr.wozai.service.user.client.document.dto.DocumentListDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingTemplateDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingTemplateListDTO;
import hr.wozai.service.api.component.OnboardingMetaPermissionChecker;
import hr.wozai.service.api.helper.OnboardingTemplateHelper;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.user.OnboardingTemplateListVO;
import hr.wozai.service.api.vo.user.OnboardingTemplateVO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-11
 */
@Controller("onboardingTemplateController")
public class OnboardingTemplateController {

  private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingTemplateController.class);
  
  @Autowired
  private FacadeFactory facadeFactory;
  
  @Autowired
  private OnboardingMetaPermissionChecker onboardingMetaPermissionChecker;

  @LogAround

  @RequestMapping(
      value = "/onboarding-templates",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<IdVO> addCustomOnboardingTemplate(
      @RequestBody @Valid OnboardingTemplateVO onboardingTemplateVO,
      BindingResult bindingResult
  ) {

    Result<IdVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!onboardingMetaPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      OnboardingTemplateDTO onboardingTemplateDTO = new OnboardingTemplateDTO();
      BeanUtils.copyProperties(onboardingTemplateVO, onboardingTemplateDTO);
      onboardingTemplateDTO.setOnboardingDocumentDTOs(OnboardingTemplateHelper.convertFromOnboardingDocumentVOs(
          onboardingTemplateVO.getOnboardingDocumentVOs()));
      LongDTO addResult = facadeFactory.getOnboardingTemplateFacade().addCustomOnboardingTemplate(
          authedOrgId, onboardingTemplateDTO, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(addResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        IdVO idVO = new IdVO();
        idVO.setIdValue(addResult.getData());
        result.setData(idVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("addCustomOnboardingTemplate()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-templates/{onboardingTemplateId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<OnboardingTemplateVO> getOnboardingTemplate(
      @PathVariable("onboardingTemplateId") String encryptedOnboardingTemplateId
  ) {

    Result<OnboardingTemplateVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long onboardingTemplateId = 0;

    try {
      onboardingTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedOnboardingTemplateId));
    } catch (Exception e) {
      LOGGER.error("getOnboardingTemplate()-error: invalid params");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    if (!onboardingMetaPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      OnboardingTemplateDTO rpcOnboardingTemplate = facadeFactory.getOnboardingTemplateFacade().getOnboardingTemplate(
          authedOrgId, onboardingTemplateId, authedActorUserId, authedAdminUserId);
      DocumentListDTO rpcDocuments = facadeFactory.getDocumentFacade().listDocument(authedOrgId, authedActorUserId, authedAdminUserId);

      ServiceStatus rpcOnboardingTemplateStatus =
          ServiceStatus.getEnumByCode(rpcOnboardingTemplate.getServiceStatusDTO().getCode());
      ServiceStatus rpcDocumentsStatus =
          ServiceStatus.getEnumByCode(rpcDocuments.getServiceStatusDTO().getCode());
      if (rpcDocumentsStatus.equals(ServiceStatus.COMMON_OK)
          && rpcOnboardingTemplateStatus.equals(ServiceStatus.COMMON_OK)) {
        OnboardingTemplateVO onboardingTemplateVO = new OnboardingTemplateVO();
        BeanUtils.copyProperties(rpcOnboardingTemplate, onboardingTemplateVO);
        onboardingTemplateVO.setOnboardingDocumentVOs(OnboardingTemplateHelper.generateOnboardingDocumentVOs(
            rpcOnboardingTemplate.getOnboardingDocumentDTOs(), rpcDocuments.getDocumentDTOs()));
        result.setData(onboardingTemplateVO);
        result.setCodeAndMsg(ServiceStatus.COMMON_OK);
      } else if (!rpcOnboardingTemplateStatus.equals(ServiceStatus.COMMON_OK)) {
        result.setCodeAndMsg(rpcOnboardingTemplateStatus);
      } else {
        result.setCodeAndMsg(rpcDocumentsStatus);
      }
    } catch (Exception e) {
      LOGGER.info("getOnboardingTemplate()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-templates",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<OnboardingTemplateListVO> listOnboardingTemplate() {

    Result<OnboardingTemplateListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!onboardingMetaPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      OnboardingTemplateListDTO listResult = facadeFactory.getOnboardingTemplateFacade().listOnboardingTemplate(
          authedOrgId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(listResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        OnboardingTemplateListVO onboardingTemplateListVO = new OnboardingTemplateListVO();
        List<OnboardingTemplateVO> onboardingTemplateVOs = new ArrayList<>();
        for (int i = 0; i < listResult.getOnboardingTemplateDTOs().size(); i++) {
          OnboardingTemplateVO onboardingTemplateVO = new OnboardingTemplateVO();
          BeanUtils.copyProperties(listResult.getOnboardingTemplateDTOs().get(i), onboardingTemplateVO);
          onboardingTemplateVOs.add(onboardingTemplateVO);
        }
        onboardingTemplateListVO.setOnboardingTemplateVOs(onboardingTemplateVOs);
        result.setData(onboardingTemplateListVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("listOnboardingTemplate()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-templates/{onboardingTemplateId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result updateOnboardingTemplate(
      @PathVariable("onboardingTemplateId") String encryptedOnboardingTemplateId,
      @RequestBody @Valid OnboardingTemplateVO onboardingTemplateVO,
      BindingResult bindingResult
  ) {

    Result result = new Result();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long onboardingTemplateId = 0;

    if (!onboardingMetaPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      onboardingTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedOnboardingTemplateId));
    } catch (Exception e) {
      LOGGER.error("updateOnboardingTemplate()-error: invalid params");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {

      OnboardingTemplateDTO onboardingTemplateDTO = new OnboardingTemplateDTO();
      BeanUtils.copyProperties(onboardingTemplateVO, onboardingTemplateDTO);
      onboardingTemplateDTO.setOnboardingDocumentDTOs(OnboardingTemplateHelper.convertFromOnboardingDocumentVOs(
          onboardingTemplateVO.getOnboardingDocumentVOs()));
      onboardingTemplateDTO.setOnboardingTemplateId(onboardingTemplateId);
      VoidDTO updateResult = facadeFactory.getOnboardingTemplateFacade().updateOnboardingTemplate(
          authedOrgId, onboardingTemplateDTO, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(updateResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("updateOnboardingTemplate()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/onboarding-templates/{onboardingTemplateId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseBody
  public Result deleteOnboardingTemplate(
      @PathVariable("onboardingTemplateId") String encryptedOnboardingTemplateId
  ) {

    Result result = new Result();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long onboardingTemplateId = 0;

    if (!onboardingMetaPermissionChecker.canDelete(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      onboardingTemplateId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedOnboardingTemplateId));
    } catch (Exception e) {
      LOGGER.error("deleteOnboardingTemplate()-error: invalid params");
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {
      VoidDTO deleteResult = facadeFactory.getOnboardingTemplateFacade().deleteOnboardingTemplate(
          authedOrgId, onboardingTemplateId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(deleteResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("deleteOnboardingTemplate()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

}
