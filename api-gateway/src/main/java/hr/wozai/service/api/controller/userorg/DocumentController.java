// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.userorg;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
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

import hr.wozai.service.api.component.DocumentPermissionChecker;
import hr.wozai.service.api.component.OnboardingFlowPermissionChecker;
import hr.wozai.service.api.component.UserProfilePermissionChecker;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.user.DocumentListVO;
import hr.wozai.service.api.vo.user.DocumentVO;
import hr.wozai.service.api.vo.user.S3DocumentRequestVO;
import hr.wozai.service.servicecommons.commons.enums.DocumentScenario;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.document.dto.DocumentDTO;
import hr.wozai.service.user.client.document.dto.DocumentListDTO;
import hr.wozai.service.user.client.document.dto.S3DocumentRequestDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-11
 */
@Controller("documentController")
public class DocumentController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentController.class);

  @Autowired
  private DocumentPermissionChecker documentPermissionChecker;

  @Autowired
  private UserProfilePermissionChecker userProfilePermissionChecker;

  @Autowired
  private OnboardingFlowPermissionChecker onboardingFlowPermissionChecker;
  
  @Autowired
  FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(
      value = "/documents",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<S3DocumentRequestVO> addDocument(
      @RequestBody @Valid DocumentVO documentVO,
      BindingResult bindingResult
  ) {

    Result<S3DocumentRequestVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    int scenario = documentVO.getScenario();
    if (null == DocumentScenario.getEnumByCode(scenario)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    } else if ((scenario == DocumentScenario.ONBOARDING_DOCUMENT.getCode()
                || scenario == DocumentScenario.ONBOARDING_TEMPLATE_IMAGE.getCode())
               && !documentPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    } else if (scenario == DocumentScenario.USER_PROFILE_FIELD.getCode()
               && !userProfilePermissionChecker.canEdit(authedOrgId, authedActorUserId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    } else if (scenario == DocumentScenario.MANUAL_OPERATION_CSV.getCode()
               && !onboardingFlowPermissionChecker.canCreate(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      DocumentDTO documentDTO = new DocumentDTO();
      BeanUtils.copyProperties(documentVO, documentDTO);
      S3DocumentRequestDTO addResult =
          facadeFactory.getDocumentFacade().addDocument(authedOrgId, documentDTO, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(addResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        S3DocumentRequestVO s3DocumentRequestVO = new S3DocumentRequestVO();
        BeanUtils.copyProperties(addResult, s3DocumentRequestVO);
        result.setData(s3DocumentRequestVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("addDocument()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


  @LogAround

  @RequestMapping(
      value = "/documents/temp",
      method = RequestMethod.POST,
      produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<S3DocumentRequestVO> addDocumentByOnboardingStaff(
      @RequestBody @Valid DocumentVO documentVO,
      BindingResult bindingResult
  ) {

    Result<S3DocumentRequestVO> result = new Result<>();
    long tempOrgId = AuthenticationInterceptor.tempOrgId.get();
    long tempUserId = AuthenticationInterceptor.tempUserId.get();

    int scenario = documentVO.getScenario();
    if (scenario == DocumentScenario.ONBOARDING_DOCUMENT.getCode()
        && !documentPermissionChecker.canCreate(tempOrgId, tempUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    } else if (!userProfilePermissionChecker.canEdit(tempOrgId, tempUserId, tempUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      DocumentDTO documentDTO = new DocumentDTO();
      BeanUtils.copyProperties(documentVO, documentDTO);
      S3DocumentRequestDTO addResult =
          facadeFactory.getDocumentFacade().addDocument(tempOrgId, documentDTO, tempUserId, 0);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(addResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        S3DocumentRequestVO s3DocumentRequestVO = new S3DocumentRequestVO();
        BeanUtils.copyProperties(addResult, s3DocumentRequestVO);
        result.setData(s3DocumentRequestVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("addDocument()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }


  @LogAround

  @RequestMapping(
      value = "/documents/{documentId}",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<DocumentVO> getDocument(
      @PathVariable("documentId") String encryptedDocumentId
   ) {

    Result<DocumentVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long documentId = 0;

    try {
      documentId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedDocumentId));
    } catch (Exception e) {
      LOGGER.error("getDocument-error: invalid param");
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    DocumentDTO getResult = facadeFactory.getDocumentFacade().getDocument(authedOrgId, documentId, authedActorUserId, authedAdminUserId);

    if (!userProfilePermissionChecker.canRead(authedOrgId, authedActorUserId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(getResult.getServiceStatusDTO().getCode());
    if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
      DocumentVO documentVO = new DocumentVO();
      BeanUtils.copyProperties(getResult, documentVO);
      result.setData(documentVO);
      result.setCodeAndMsg(rpcStatus);
    } else {
      throw new ServiceStatusException(rpcStatus);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/documents/{documentId}/download",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<S3DocumentRequestVO> downloadDocument(
      @PathVariable("documentId") String encryptedDocumentId
  ) {

    Result<S3DocumentRequestVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long documentId = 0;

    try {
      documentId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedDocumentId));
    } catch (Exception e) {
      LOGGER.error("downloadDocument-error: invalid param");
    }

    // check permission
    if (!userProfilePermissionChecker.canRead(authedOrgId, authedActorUserId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    S3DocumentRequestDTO downloadResult =
        facadeFactory.getDocumentFacade().downloadDocument(authedOrgId, documentId, authedActorUserId, authedAdminUserId);
    ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(downloadResult.getServiceStatusDTO().getCode());
    if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
      S3DocumentRequestVO s3DocumentRequestVO = new S3DocumentRequestVO();
      BeanUtils.copyProperties(downloadResult, s3DocumentRequestVO);
      result.setData(s3DocumentRequestVO);
      result.setCodeAndMsg(rpcStatus);
    } else {
      throw new ServiceStatusException(rpcStatus);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/documents/temp/{documentId}/download",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<S3DocumentRequestVO> downloadDocumentByOnboardingStaff(
      @PathVariable("documentId") String encryptedDocumentId
  ) {

    Result<S3DocumentRequestVO> result = new Result<>();
    long tempOrgId = AuthenticationInterceptor.tempOrgId.get();
    long tempUserId = AuthenticationInterceptor.tempUserId.get();
    long documentId = 0;

    try {
      documentId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedDocumentId));
    } catch (Exception e) {
      LOGGER.error("downloadDocument-error: invalid param");
    }

    // check permission
    if (!userProfilePermissionChecker.canRead(tempOrgId, tempUserId, tempUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    S3DocumentRequestDTO downloadResult =
        facadeFactory.getDocumentFacade().downloadDocument(tempOrgId, documentId, tempUserId, 0);
    ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(downloadResult.getServiceStatusDTO().getCode());
    if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
      S3DocumentRequestVO s3DocumentRequestVO = new S3DocumentRequestVO();
      BeanUtils.copyProperties(downloadResult, s3DocumentRequestVO);
      result.setData(s3DocumentRequestVO);
      result.setCodeAndMsg(rpcStatus);
    } else {
      throw new ServiceStatusException(rpcStatus);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/documents",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<DocumentListVO> listDocuments() {

    Result<DocumentListVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!documentPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      DocumentListDTO listResult = facadeFactory.getDocumentFacade().listDocument(authedOrgId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(listResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        DocumentListVO documentListVO = new DocumentListVO();
        List<DocumentVO> documentVOs = new ArrayList<>();
        for (int i = 0; i < listResult.getDocumentDTOs().size(); i++) {
          DocumentVO documentVO = new DocumentVO();
          BeanUtils.copyProperties(listResult.getDocumentDTOs().get(i), documentVO);
          documentVOs.add(documentVO);
        }
        documentListVO.setDocumentVOs(documentVOs);
        result.setData(documentListVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("downloadDocument()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/documents/{documentId}",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result updateDocumentName(
      @PathVariable("documentId") String encryptedDocumentId,
      @RequestBody @Valid DocumentVO documentVO,
      BindingResult bindingResult
  ) {

    Result result = new Result();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long documentId = 0;

    try {
      documentId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedDocumentId));
    } catch (Exception e) {
      LOGGER.error("updateDocumentName-error: invalid param");
    }

    if (!documentPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      DocumentDTO documentDTO = new DocumentDTO();
      BeanUtils.copyProperties(documentVO, documentDTO);
      documentDTO.setDocumentId(documentId);
      VoidDTO updateResult =
          facadeFactory.getDocumentFacade().updateDocument(authedOrgId, documentDTO, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(updateResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("updateDocumentName()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/documents/{documentId}",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseBody
  public Result deleteDocument(
      @PathVariable("documentId") String encryptedDocumentId
  ) {

    Result<S3DocumentRequestVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    long documentId = 0;

    if (!documentPermissionChecker.canDelete(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      documentId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedDocumentId));
    } catch (Exception e) {
      LOGGER.error("deleteDocument-error: invalid param");
    }

    try {
      VoidDTO deleteResult =
          facadeFactory.getDocumentFacade().deleteDocument(authedOrgId, documentId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(deleteResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("deleteDocument()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }
  
}
