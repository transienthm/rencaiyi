// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.DocumentScenario;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.user.client.document.dto.DocumentDTO;
import hr.wozai.service.user.client.document.dto.DocumentListDTO;
import hr.wozai.service.user.client.document.dto.S3DocumentRequestDTO;
import hr.wozai.service.user.client.document.facade.DocumentFacade;
import hr.wozai.service.user.server.test.base.TestBase;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class DocumentFacadeImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(DocumentFacadeImplTest.class);

  // data
  long orgId = 10L;
  int scenario = DocumentScenario.ONBOARDING_DOCUMENT.getCode();
  String documentKey = "SOME_KEY";
  String documentName = "新文件";
  int storageStatus = 2;
  long userId = 20L;
  long invalidDocumentId = 2829472817L;
  long invalidOrgId = 3917295717L;
  DocumentDTO documentDTO = null;

  {
    documentDTO = new DocumentDTO();
    documentDTO.setScenario(scenario);
    documentDTO.setOrgId(orgId);
    documentDTO.setDocumentKey(documentKey);
    documentDTO.setDocumentName(documentName);
    documentDTO.setStorageStatus(storageStatus);
    documentDTO.setCreatedUserId(userId);
  }

  @Autowired
  DocumentFacade documentFacade;

  @Before
  public void init() {
  }

  /**
   * Test:
   *  1) addDocument()
   *  2) getDocument()
   *  3) downloadDocument()
   *  4) listDocumentForOnboarding()
   *  5) updateDocument()
   *  6) deleteDocument()
   */
  @Test
  public void testAll() {

    // 1) 2) 3) 4)
    S3DocumentRequestDTO addResult = documentFacade.addDocument(orgId, documentDTO, userId, userId);

    System.out.println("addResult=" + addResult);

    long insertedDocId = addResult.getDocumentId();
    System.out.println("addResult=" + addResult);

    DocumentDTO getResult = documentFacade.getDocument(orgId, insertedDocId, userId, userId);
    Assert.assertEquals(documentName, getResult.getDocumentName());

    S3DocumentRequestDTO downloadResult = documentFacade.downloadDocument(orgId, insertedDocId, userId, userId);
    System.out.println("downloadResult=" + downloadResult);
    Assert.assertEquals(insertedDocId, downloadResult.getDocumentId().longValue());

    documentFacade.addDocument(orgId, documentDTO, userId, userId);
    DocumentListDTO addListResult = documentFacade.listDocument(orgId, userId, userId);
    Assert.assertEquals(2, addListResult.getDocumentDTOs().size());

    // 5) 6)
    String updateName = documentName + documentName;
    getResult.setDocumentName(updateName);
    documentFacade.updateDocument(orgId, getResult, userId, userId);
    DocumentDTO updateResult = documentFacade.getDocument(orgId, insertedDocId, userId, userId);
    Assert.assertEquals(updateName, updateResult.getDocumentName());

    documentFacade.deleteDocument(orgId, insertedDocId, userId, userId);
    DocumentDTO deletedResult = documentFacade.getDocument(orgId, insertedDocId, userId, userId);
    Assert.assertEquals(null, deletedResult.getDocumentId());

  }

  /**
   * Method: addDocument()
   * Case:   #2, abnormal, document.documentName == null
   *
   */
  @Test
  public void testAddDocumentCase2() {
//
//    documentDTO.setDocumentName(null);
//    S3DocumentRequestDTO addResult = documentFacade.addDocument(orgId, documentDTO, userId, userId);
//    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), addResult.getServiceStatusDTO().getCode());

  }

  /**
   * Method: downloadDocument()
   * Case:   #2, abnormal, document does not exist
   *
   */
  @Test
  public void testDownloadDocumentCase2() {

    S3DocumentRequestDTO downloadResult = documentFacade.downloadDocument(orgId, invalidDocumentId, userId, userId);
    Assert.assertEquals(ServiceStatus.DOC_NOT_FOUND.getCode(), downloadResult.getServiceStatusDTO().getCode());

  }

  /**
   * Method: listDocument()
   * Case:   #2, normal, empty list
   *
   */
  @Test
  public void testListDocumentCase2() {
    DocumentListDTO listResult = documentFacade.listDocument(invalidOrgId, userId, userId);
    Assert.assertEquals(0, listResult.getDocumentDTOs().size());
  }

}
