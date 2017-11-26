// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.service.impl;

//import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.servicecommons.commons.enums.DocumentScenario;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.document.Document;
import hr.wozai.service.user.server.service.DocumentService;
import hr.wozai.service.user.server.service.S3DocumentService;

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
public class DocumentServiceImplTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(DocumentServiceImplTest.class);

  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  private DocumentService documentService;

  @Autowired
  private S3DocumentService s3DocumentService;

  // data
  long orgId = 10L;
  int scenario = DocumentScenario.ONBOARDING_DOCUMENT.getCode();
  String documentKey = "SOME_KEY";
  String documentName = "新文件";
  int storageStatus = 2;
  long userId = 20L;
  Document document = null;

  {
    document = new Document();
    document.setScenario(scenario);
    document.setOrgId(orgId);
    document.setDocumentKey(documentKey);
    document.setDocumentName(documentName);
    document.setStorageStatus(storageStatus);
    document.setCreatedUserId(userId);
  }

  @Before
  public void init() {
  }


  /**
   * Test:
   *  1) addDocument()
   *  2) getDocument()
   *  3) listDocumentForOnboarding()
   *  4) updateDocument()
   *  5) deleteDocument()
   *
   *  6) generatePresignedPutUrl()
   *  7) generatePresignedGetUrl()
   */
  @Test
  public void testAll() {

    // 1) 2) 3)
    long insertedId = documentService.addDocument(document);
    Document insertedDoc = documentService.getDocument(orgId, insertedId);
    Assert.assertEquals(documentName, insertedDoc.getDocumentName());
    documentService.addDocument(document);
    List<Document> insertedDocs = documentService.listDocumentForOnboarding(orgId);
    Assert.assertEquals(2, insertedDocs.size());

    // 4) 5)
    String updateName = documentName + documentName;
    insertedDoc.setDocumentName(updateName);
    documentService.updateDocument(insertedDoc);
    Document updatedDoc = documentService.getDocument(orgId, insertedId);
    Assert.assertEquals(updateName, updatedDoc.getDocumentName());

    documentService.deleteDocument(orgId, insertedDoc.getDocumentId(), userId);
    try {
      Document deletedDocument = documentService.getDocument(orgId, insertedDoc.getDocumentId());
    } catch (ServiceStatusException e) {
      Assert.assertEquals(ServiceStatus.DOC_NOT_FOUND.getCode(), e.getServiceStatus().getCode());
    }

    // 6) 7)
    String putUrl = s3DocumentService.generatePresignedPutUrl(documentKey, 1000 * 60);
    String getUrl = s3DocumentService.generatePresignedGetUrl(documentKey, documentName, 1000 * 60);
    LOGGER.info("putUrl=" + putUrl);
    LOGGER.info("getUrl=" + getUrl);

  }

  @Test
  public void testAddDocumentExceptionOne() {

    thrown.expect(ServiceStatusException.class);
    document.setDocumentName(null);
    documentService.addDocument(document);

  }

  @Test
  public void testUpdateDocumentExceptionOne() {

    // prepare
    long insertedId = documentService.addDocument(document);
    Document insertedDoc = documentService.getDocument(orgId, insertedId);
    Assert.assertEquals(documentName, insertedDoc.getDocumentName());

    // verify
    thrown.expect(ServiceStatusException.class);
    insertedDoc.setLastModifiedUserId(null);
    documentService.updateDocument(insertedDoc);

  }

}
