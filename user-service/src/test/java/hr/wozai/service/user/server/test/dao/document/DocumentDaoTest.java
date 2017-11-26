// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.document;

import hr.wozai.service.servicecommons.commons.enums.DocumentScenario;
import hr.wozai.service.user.server.dao.document.DocumentDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.document.Document;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-02-23
 */
public class DocumentDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(DocumentDaoTest.class);

  @Autowired
  DocumentDao documentDao;

  // data
  long userId = 10L;
  long orgId = 20L;
  int scenario = DocumentScenario.ONBOARDING_DOCUMENT.getCode();
  String documentKey = "AABBCCDD";
  String documentName = "新建文件";
  Document document = new Document();

  {
    document.setScenario(scenario);
    document.setDocumentKey(documentKey);
    document.setDocumentName(documentName);
    document.setDescription(documentName);
    document.setStorageStatus(2);
    document.setOrgId(orgId);
    document.setCreatedUserId(userId);
  }

  @Before
  public void init() {

  }

  /**
   * Test:
   *  1) insertDocument()
   *  2) findDocumentByPrimaryKey()
   *  3) listDocumentByOrgIdForOnboarding()
   *  4) updateDocumentByPrimaryKey()
   *  5) deleteDocumentByPrimaryKey()
   *
   */
  @Test
  public void testAll() {

    // 1) 2) 3)
    long insertedId = documentDao.insertDocument(document);
    Document insertedDoc = documentDao.findDocumentByPrimaryKey(orgId, insertedId);
    Assert.assertEquals(documentName, insertedDoc.getDocumentName());

    documentDao.insertDocument(document);
    List<Document> insertedDocs = documentDao.listDocumentByOrgIdForOnboarding(orgId);
    Assert.assertEquals(2, insertedDocs.size());

    // 4) 5)
    String newName = documentName + documentName;
    insertedDoc.setDocumentName(newName);
    documentDao.updateDocumentByPrimaryKey(insertedDoc);
    Document updatedDoc = documentDao.findDocumentByPrimaryKey(orgId, insertedId);
    Assert.assertEquals(newName, updatedDoc.getDocumentName());

    documentDao.deleteDocumentByPrimaryKey(orgId, insertedId, userId);
    Document deletedDoc = documentDao.findDocumentByPrimaryKey(orgId, insertedId);
    Assert.assertEquals(null, deletedDoc);
  }


}
