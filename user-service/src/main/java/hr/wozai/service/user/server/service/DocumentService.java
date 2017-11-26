// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.document.Document;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-07
 */
public interface DocumentService {

  long addDocument(Document document);

  Document getDocument(long orgId, long documentId);

  List<Document> listDocumentForOnboarding(long orgId);

  void updateDocument(Document document);

  void deleteDocument(long orgId, long documentId, long actorUserId);

}
