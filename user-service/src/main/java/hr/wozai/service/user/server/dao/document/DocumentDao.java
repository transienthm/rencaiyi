// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.document;

import hr.wozai.service.user.server.model.document.Document;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-07
 */
@Repository("documentDao")
public class DocumentDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.document.DocumentMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertDocument(Document document) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertDocument", document);
    return document.getDocumentId();
  }

  public Document findDocumentByPrimaryKey(long orgId, long documentId) {
    Map<String, Object> params = new HashMap<>();
    params.put("documentId", documentId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findDocumentByPrimaryKey", params);
  }

  public List<Document> listDocumentByOrgIdForOnboarding(long orgId) {
    List<Document> documents = sqlSessionTemplate.selectList(BASE_PACKAGE + "listDocumentByOrgIdForOnboarding", orgId);
    if (CollectionUtils.isEmpty(documents)) {
      documents = Collections.EMPTY_LIST;
    }
    return documents;
  }

  public long updateDocumentByPrimaryKey(Document document) {
    Document cleanDocument = new Document();
    cleanDocument.setDocumentId(document.getDocumentId());
    cleanDocument.setOrgId(document.getOrgId());
    cleanDocument.setDocumentName(document.getDocumentName());
    cleanDocument.setStorageStatus(document.getStorageStatus());
    cleanDocument.setLastModifiedUserId(document.getLastModifiedUserId());
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateDocumentByPrimaryKeySelective", cleanDocument);
  }

  public long deleteDocumentByPrimaryKey(long orgId, long documentId, long actorUserId) {
    Document cleanDocument = new Document();
    cleanDocument.setDocumentId(documentId);
    cleanDocument.setOrgId(orgId);
    cleanDocument.setLastModifiedUserId(actorUserId);
    cleanDocument.setIsDeleted(1);
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateDocumentByPrimaryKeySelective", cleanDocument);
  }


}
