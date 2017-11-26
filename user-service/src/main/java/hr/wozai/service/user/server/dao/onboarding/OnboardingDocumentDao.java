// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.onboarding;

import hr.wozai.service.user.server.model.onboarding.OnboardingDocument;
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
@Repository("onboardingDocumentDao")
public class OnboardingDocumentDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.onboarding.OnboardingDocumentMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public int batchInsertOnboardingDocument(List<OnboardingDocument> onboardingDocuments) {
    if (CollectionUtils.isEmpty(onboardingDocuments)) {
      return 0;
    }

    return sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertOnboardingDocument", onboardingDocuments);
  }

  public List<OnboardingDocument> listOnboardingDocumentByOnboardingTemplateId(long orgId, long onboardingTemplateId) {
    Map<String, Object> params = new HashMap<>();
    params.put("onboardingTemplateId", onboardingTemplateId);
    params.put("orgId", orgId);
    List<OnboardingDocument> documents =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listOnboardingDocumentByPrimaryKey", params);
    if (CollectionUtils.isEmpty(documents)) {
      documents = Collections.EMPTY_LIST;
    }
    return documents;
  }

  public List<OnboardingDocument> listOnboardingDocumentByOnboardingTemplateIdForUpdate(
      long orgId, long onboardingTemplateId) {
    Map<String, Object> params = new HashMap<>();
    params.put("onboardingTemplateId", onboardingTemplateId);
    params.put("orgId", orgId);
    params.put("forUpdate", 1);
    List<OnboardingDocument> documents =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listOnboardingDocumentByPrimaryKey", params);
    if (CollectionUtils.isEmpty(documents)) {
      documents = Collections.EMPTY_LIST;
    }
    return documents;
  }

  public int batchUpdateLogicalIndexByPrimaryKey(List<OnboardingDocument> onboardingDocuments) {
    if (CollectionUtils.isEmpty(onboardingDocuments)) {
      return 0;
    }
    for (OnboardingDocument onboardingDocument: onboardingDocuments) {
      cleanOnboardingDocumentForUpdateLogicalIndexRequest(onboardingDocument);
    }
    return sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateOnboardingDocumentByPrimaryKey", onboardingDocuments);
  }

  public int batchDeleteOnboardingDocumentsByPrimaryKey(List<OnboardingDocument> onboardingDocuments) {
    if (CollectionUtils.isEmpty(onboardingDocuments)) {
      return 0;
    }
    for (OnboardingDocument onboardingDocument: onboardingDocuments) {
      cleanOnboardingDocumentForDeleteRequest(onboardingDocument);
    }
    return sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateOnboardingDocumentByPrimaryKey", onboardingDocuments);
  }

  private void cleanOnboardingDocumentForUpdateLogicalIndexRequest(OnboardingDocument onboardingDocument) {
    onboardingDocument.setIsDeleted(null);
  }

  private void cleanOnboardingDocumentForDeleteRequest(OnboardingDocument onboardingDocument) {
    onboardingDocument.setLogicalIndex(null);
    onboardingDocument.setIsDeleted(1);
  }

//  public int batchUpdateLogicalIndexByPrimaryKey(List<OnboardingDocument> onboardingDocuments) {
//    return sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateLogicalIndexByPrimaryKey", onboardingDocuments);
//  }
//
//  public int deleteOnboardingDocumentByPrimaryKey(long orgId, long onboardingDocumentId, long actorUserId) {
//    OnboardingDocument onboardingDocument = new OnboardingDocument();
//    onboardingDocument.setOnboardingDocumentId(onboardingDocumentId);
//    onboardingDocument.setOrgId(orgId);
//    onboardingDocument.setLastModifiedUserId(actorUserId);
//    onboardingDocument.setIsDeleted(1);
//    return sqlSessionTemplate.update(
//        BASE_PACKAGE + "updateOnboardingDocumentByPrimaryKeySelective", onboardingDocument);
//  }

}
