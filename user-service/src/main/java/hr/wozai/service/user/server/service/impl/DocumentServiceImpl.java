// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.dao.document.DocumentDao;

import hr.wozai.service.user.server.helper.DocumentHelper;

import hr.wozai.service.user.server.model.document.Document;
import hr.wozai.service.user.server.service.DocumentService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-07
 */
@Service("documentService")
public class DocumentServiceImpl implements DocumentService {

  private static Logger LOGGER = LoggerFactory.getLogger(DocumentServiceImpl.class);

  @Autowired
  DocumentDao documentDao;

  @PostConstruct
  public void init(){}

  @LogAround
  @Override
  public long addDocument(Document document) {

    if (!DocumentHelper.isAcceptableAddRequest(document)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    documentDao.insertDocument(document);
    return document.getDocumentId();
  }

  @LogAround
  @Override
  public Document getDocument(long orgId, long documentId) {

    Document document = documentDao.findDocumentByPrimaryKey(orgId, documentId);
    if (null == document) {
      throw new ServiceStatusException(ServiceStatus.DOC_NOT_FOUND);
    }

    return document;
  }

  @LogAround
  @Override
  public List<Document> listDocumentForOnboarding(long orgId) {
    List<Document> documents = documentDao.listDocumentByOrgIdForOnboarding(orgId);
    return documents;
  }

  @LogAround
  @Override
  public void updateDocument(Document document) {
    if (!DocumentHelper.isAcceptableUpdateRequest(document)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    documentDao.updateDocumentByPrimaryKey(document);
  }

  @LogAround
  @Override
  public void deleteDocument(long orgId, long documentId, long actorUserId) {
    documentDao.deleteDocumentByPrimaryKey(orgId, documentId, actorUserId);
  }

}
