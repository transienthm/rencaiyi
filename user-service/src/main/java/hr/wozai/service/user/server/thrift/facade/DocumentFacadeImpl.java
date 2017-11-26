// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.OssRequestType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.user.client.document.dto.DocumentDTO;
import hr.wozai.service.user.client.document.dto.DocumentListDTO;
import hr.wozai.service.user.client.document.dto.S3DocumentRequestDTO;
import hr.wozai.service.user.client.document.facade.DocumentFacade;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.model.document.Document;
import hr.wozai.service.user.server.service.DocumentService;
import hr.wozai.service.user.server.service.S3DocumentService;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.servicecommons.utils.uuid.UUIDGenerator;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
@Service("documentFacade")
public class DocumentFacadeImpl implements DocumentFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentFacadeImpl.class);

  private static final long ONE_HOUR_IN_MILLIS = 1000 * 60 * 60;
  private static final long ONE_DAY_IN_MILLIS  = 1000 * 60 * 60 * 24;

  @Autowired
  DocumentService documentService;

  @Autowired
  @Qualifier("ossDocumentService")
  S3DocumentService ossDocumentService;

  @Override
  @LogAround
  public S3DocumentRequestDTO addDocument(long orgId, DocumentDTO documentDTO, long actorUserId, long adminUserId) {

    S3DocumentRequestDTO result = new S3DocumentRequestDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);


    try {
      String documentKey = UUIDGenerator.generateDocumentKey(documentDTO.getDocumentName());
      // 1) add document
      Document document = new Document();
      BeanUtils.copyProperties(documentDTO, document);
      document.setDocumentKey(documentKey);
      document.setOrgId(orgId);
      document.setCreatedUserId(actorUserId);
      long docId = documentService.addDocument(document);
      // 2) generate put url
      long effectiveTime = ONE_DAY_IN_MILLIS;
      String putUrl = ossDocumentService.generatePresignedPutUrl(documentKey, effectiveTime);
      result.setDocumentId(docId);
      result.setRequestType(OssRequestType.PUT.getCode());
      result.setEffectiveTime(effectiveTime);
      result.setPresignedUrl(putUrl);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addDocument()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public DocumentDTO getDocument(long orgId, long documentId, long actorUserId, long adminUserId) {

    DocumentDTO result = new DocumentDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Document document = documentService.getDocument(orgId, documentId);
      if (null != document) {
        BeanHelper.copyPropertiesHandlingJSON(document, result);
      }
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getDocument()-error", e);
    }

    return result;
  }

  @Override
  public S3DocumentRequestDTO downloadDocument(long orgId, long documentId, long actorUserId, long adminUserId) {

    S3DocumentRequestDTO result = new S3DocumentRequestDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // 1) add document
      Document document = documentService.getDocument(orgId, documentId);
      // 2) generate put url
      long effectiveTime = ONE_DAY_IN_MILLIS;
      String getUrl = ossDocumentService.generatePresignedGetUrl(
          document.getDocumentKey(), document.getDocumentName(), effectiveTime);
      result.setDocumentId(documentId);
      result.setRequestType(OssRequestType.GET.getCode());
      result.setEffectiveTime(effectiveTime);
      result.setPresignedUrl(getUrl);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("downloadDocument()-error", e);
    }

    return result;
  }

  @Override
  public DocumentListDTO listDocument(long orgId, long actorUserId, long adminUserId) {

    DocumentListDTO result = new DocumentListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Document> documents = documentService.listDocumentForOnboarding(orgId);
      List<DocumentDTO> documentDTOs = null;
      if (!CollectionUtils.isEmpty(documents)) {
        documentDTOs = new ArrayList<>();
        for (Document document: documents) {
          DocumentDTO documentDTO = new DocumentDTO();
          BeanUtils.copyProperties(document, documentDTO);
          documentDTOs.add(documentDTO);
        }
      } else {
        documentDTOs = Collections.EMPTY_LIST;
      }
      result.setDocumentDTOs(documentDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listDocumentForOnboarding()-error", e);
    }

    return result;
  }

  @Override
  public VoidDTO updateDocument(long orgId, DocumentDTO documentDTO, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Document document = new Document();
      BeanUtils.copyProperties(documentDTO, document);
      document.setOrgId(orgId);
      document.setLastModifiedUserId(actorUserId);
      documentService.updateDocument(document);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateDocument()-error", e);
    }

    return result;
  }

  @Override
  public VoidDTO deleteDocument(long orgId, long documentId, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      documentService.deleteDocument(orgId, documentId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("deleteDocument()-error", e);
    }

    return result;
  }
}
