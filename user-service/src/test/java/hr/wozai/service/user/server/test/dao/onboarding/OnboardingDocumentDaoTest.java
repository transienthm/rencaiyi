// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.dao.onboarding;

import hr.wozai.service.user.server.dao.onboarding.OnboardingDocumentDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.onboarding.OnboardingDocument;
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
 * @Created: 2016-02-23
 */
public class OnboardingDocumentDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(OnboardingDocumentDaoTest.class);

  @Autowired
  OnboardingDocumentDao onboardingDocumentDao;

  // data
  long userId = 10L;
  long orgId = 20L;
  long onboardingTemplateId = 30L;
  long documentId = 40L;
  OnboardingDocument onboardingDocument = new OnboardingDocument();

  {
    onboardingDocument.setOrgId(orgId);
    onboardingDocument.setOnboardingTemplateId(onboardingTemplateId);
    onboardingDocument.setDocumentId(documentId);
    onboardingDocument.setCreatedUserId(userId);
  }

  @Before
  public void init() {

  }

  @Test
  public void testBatchInsertOnboardingDocument() {

    List<OnboardingDocument> onboardingDocuments = new ArrayList<>();
    int docCount = 10;
    for (int i = 0; i < docCount; i++) {
      OnboardingDocument newOnDoc = new OnboardingDocument();
      BeanUtils.copyProperties(onboardingDocument, newOnDoc);
      newOnDoc.setLogicalIndex(i);
      onboardingDocuments.add(newOnDoc);
    }
    int insertedCount = onboardingDocumentDao.batchInsertOnboardingDocument(onboardingDocuments);
    Assert.assertEquals(docCount, insertedCount);

    List<OnboardingDocument> insertedDocs =
        onboardingDocumentDao.listOnboardingDocumentByOnboardingTemplateId(orgId, onboardingTemplateId);
    Assert.assertEquals(docCount, insertedDocs.size());

  }

  @Test
  public void testBatchUpdateLogicalIndexByPrimaryKey() {

    List<OnboardingDocument> onboardingDocuments = new ArrayList<>();
    int docCount = 10;
    for (int i = 0; i < docCount; i++) {
      OnboardingDocument newOnDoc = new OnboardingDocument();
      BeanUtils.copyProperties(onboardingDocument, newOnDoc);
      newOnDoc.setLogicalIndex(i);
      onboardingDocuments.add(newOnDoc);
    }
    onboardingDocumentDao.batchInsertOnboardingDocument(onboardingDocuments);
    List<OnboardingDocument> insertedDocs =
        onboardingDocumentDao.listOnboardingDocumentByOnboardingTemplateId(orgId, onboardingTemplateId);
    Assert.assertEquals(docCount, insertedDocs.size());

    long firstDocId = insertedDocs.get(0).getOnboardingDocumentId();
    for (int i = 0; i < insertedDocs.size(); i++) {
      insertedDocs.get(i).setLogicalIndex(docCount - 1 - i);
    }
    onboardingDocumentDao.batchUpdateLogicalIndexByPrimaryKey(insertedDocs);

    insertedDocs =
        onboardingDocumentDao.listOnboardingDocumentByOnboardingTemplateId(orgId, onboardingTemplateId);
    Assert.assertEquals(firstDocId, insertedDocs.get(docCount - 1).getOnboardingDocumentId().longValue());

  }

  @Test
  public void testBatchDeleteOnboardingDocumentsByPrimaryKey() {

    List<OnboardingDocument> onboardingDocuments = new ArrayList<>();
    int docCount = 10;
    for (int i = 0; i < docCount; i++) {
      OnboardingDocument newOnDoc = new OnboardingDocument();
      BeanUtils.copyProperties(onboardingDocument, newOnDoc);
      newOnDoc.setLogicalIndex(i);
      onboardingDocuments.add(newOnDoc);
    }
    onboardingDocumentDao.batchInsertOnboardingDocument(onboardingDocuments);

    List<OnboardingDocument> insertedDocs =
        onboardingDocumentDao.listOnboardingDocumentByOnboardingTemplateId(orgId, onboardingTemplateId);
    List<OnboardingDocument> toDeleteDocs = new ArrayList<>();
    toDeleteDocs.add(insertedDocs.get(0));
    toDeleteDocs.add(insertedDocs.get(1));
    onboardingDocumentDao.batchDeleteOnboardingDocumentsByPrimaryKey(toDeleteDocs);

    insertedDocs =
        onboardingDocumentDao.listOnboardingDocumentByOnboardingTemplateId(orgId, onboardingTemplateId);
    Assert.assertEquals(docCount - 2, insertedDocs.size());


  }

}
