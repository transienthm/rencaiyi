// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.dao.onboarding.OnboardingDocumentDao;
import hr.wozai.service.user.server.dao.onboarding.OnboardingTemplateDao;
import hr.wozai.service.user.server.dao.userorg.CoreUserProfileDao;
import hr.wozai.service.user.server.helper.OnboardingTemplateHelper;
import hr.wozai.service.user.server.model.onboarding.OnboardingDocument;
import hr.wozai.service.user.server.model.onboarding.OnboardingTemplate;
import hr.wozai.service.user.server.service.OnboardingTemplateService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-09
 */
@Service("onboardingTemplateService")
public class OnboardingTemplateServiceImpl implements OnboardingTemplateService {

  private static Logger LOGGER = LoggerFactory.getLogger(OnboardingTemplateServiceImpl.class);

  @Autowired
  private OnboardingTemplateDao onboardingTemplateDao;

  @Autowired
  private OnboardingDocumentDao onboardingDocumentDao;

  @Autowired
  private CoreUserProfileDao coreUserProfileDao;

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addOnboardingTemplate(OnboardingTemplate onboardingTemplate) {

    if (!OnboardingTemplateHelper.isAcceptableAddRequest(onboardingTemplate)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    onboardingTemplateDao.insertOnboardingTemplate(onboardingTemplate);

    if (!CollectionUtils.isEmpty(onboardingTemplate.getOnboardingDocuments())) {
      for (OnboardingDocument onboardingDocument : onboardingTemplate.getOnboardingDocuments()) {
        onboardingDocument.setOrgId(onboardingTemplate.getOrgId());
        onboardingDocument.setOnboardingTemplateId(onboardingTemplate.getOnboardingTemplateId());
        onboardingDocument.setCreatedUserId(onboardingDocument.getCreatedUserId());
      }
      handleOnboardingDocumentUponAdd(onboardingTemplate);
    }

    return onboardingTemplate.getOnboardingTemplateId();
  }

  @Override  
  @LogAround
  public OnboardingTemplate getOnboardingTemplate(long orgId, long onboardingTemplateId) {

    OnboardingTemplate onboardingTemplate =
        onboardingTemplateDao.findOnboardingTemplateByOrgIdAndPrimaryKey(orgId, onboardingTemplateId);
    if (null == onboardingTemplate) {
      throw new ServiceStatusException(ServiceStatus.OB_ONBOARDING_TEMPLATE_NOT_FOUND);
    }
    populateOnboardingDocumentUponGet(onboardingTemplate);
    return onboardingTemplate;
  }

  @Override
  @LogAround
  public List<OnboardingTemplate> listOnboardingTemplate(long orgId) {
    List<OnboardingTemplate> onboardingTemplates =
        onboardingTemplateDao.listOnboardingTemplateByOrgId(orgId);
    return onboardingTemplates;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateOnboardingTemplate(OnboardingTemplate onboardingTemplate) {

    if (!OnboardingTemplateHelper.isAcceptableUpdateRequest(onboardingTemplate)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    onboardingTemplateDao.updateOnboardingTemplateByPrimaryKey(onboardingTemplate);
    handleOnboardingDocumentUponUpdate(onboardingTemplate);
  }

  /**
   * Steps:
   *  1) delete onboardingTemplate if no one used
   *  2) delete all onboardingDocuments
   *
   * @param orgId
   * @param onboardingTemplateId
   * @param actorUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteOnboardingTemplate(long orgId, long onboardingTemplateId, long actorUserId) {

    List<Long> existedUserIds = coreUserProfileDao.listUserIdByOnboardingTemplateId(orgId, onboardingTemplateId);
    if (!CollectionUtils.isEmpty(existedUserIds)) {
      throw new ServiceStatusException(ServiceStatus.OB_ONBOARDING_TEMPLATE_IN_USE);
    }

    // 1)
    OnboardingTemplate onboardingTemplate =
        onboardingTemplateDao.findOnboardingTemplateByOrgIdAndPrimaryKey(orgId, onboardingTemplateId);
    if (null == onboardingTemplate) {
      throw new ServiceStatusException(ServiceStatus.OB_ONBOARDING_TEMPLATE_NOT_FOUND);
    }
    // 2)
    onboardingTemplateDao.deleteOnboardingTemplateByPrimaryKey(orgId, onboardingTemplateId, actorUserId);
    handleOnboardingDocumentUponDelete(onboardingTemplate);
  }

  private void handleOnboardingDocumentUponAdd(OnboardingTemplate onboardingTemplate) {

    List<OnboardingDocument> onboardingDocuments = onboardingTemplate.getOnboardingDocuments();
    if (CollectionUtils.isEmpty(onboardingDocuments)) {
      return;
    }

    for (OnboardingDocument onboardingDocument: onboardingDocuments) {
      onboardingDocument.setOrgId(onboardingTemplate.getOrgId());
      onboardingDocument.setOnboardingTemplateId(onboardingTemplate.getOnboardingTemplateId());
      onboardingDocument.setCreatedUserId(onboardingTemplate.getCreatedUserId());
    }
    if (!OnboardingTemplateHelper.isValidOnboardingDocumentSequence(onboardingDocuments)) {
      throw new ServiceStatusException(ServiceStatus.OB_INVALID_ONBOARDING_DOCUMENT_SEQUENCE);
    }

    onboardingDocumentDao.batchInsertOnboardingDocument(onboardingDocuments);
  }

  private void populateOnboardingDocumentUponGet(OnboardingTemplate onboardingTemplate) {
    List<OnboardingDocument> onboardingDocuments = onboardingDocumentDao.listOnboardingDocumentByOnboardingTemplateId(
        onboardingTemplate.getOrgId(), onboardingTemplate.getOnboardingTemplateId());
    onboardingTemplate.setOnboardingDocuments(onboardingDocuments);
  }

  /**
   * Steps:
   *  1) add
   *  2) update
   *  3) delete
   *
   * @param onboardingTemplate
   */
  private void handleOnboardingDocumentUponUpdate(OnboardingTemplate onboardingTemplate) {

    long orgId = onboardingTemplate.getOrgId();
    long onboardingTemplateId = onboardingTemplate.getOnboardingTemplateId();
    long lastModifiedUserId = onboardingTemplate.getLastModifiedUserId();

    List<OnboardingDocument> newOnboardingDocuments = onboardingTemplate.getOnboardingDocuments();
    List<OnboardingDocument> currOnboardingDocuments =
        onboardingDocumentDao.listOnboardingDocumentByOnboardingTemplateIdForUpdate(orgId, onboardingTemplateId);
    for (OnboardingDocument onboardingDocument: newOnboardingDocuments) {
      onboardingDocument.setOrgId(orgId);
      onboardingDocument.setOnboardingTemplateId(onboardingTemplateId);
      if (null != onboardingDocument.getOnboardingDocumentId()) {
        onboardingDocument.setLastModifiedUserId(lastModifiedUserId);
      } else {
        onboardingDocument.setCreatedUserId(lastModifiedUserId);
      }
    }
    for (OnboardingDocument onboardingDocument: currOnboardingDocuments) {
      onboardingDocument.setLastModifiedUserId(lastModifiedUserId);
    }
    if (!OnboardingTemplateHelper.isValidOnboardingDocumentSequence(newOnboardingDocuments)) {
      throw new ServiceStatusException(ServiceStatus.OB_INVALID_ONBOARDING_DOCUMENT_SEQUENCE);
    }

    // 1)
    List<OnboardingDocument> onboardingDocumentsToAdd = OnboardingTemplateHelper
        .listOnboardingDocumentToAddUponUpdate(currOnboardingDocuments, newOnboardingDocuments);
    if (!CollectionUtils.isEmpty(onboardingDocumentsToAdd)) {
      int addedCount = onboardingDocumentDao.batchInsertOnboardingDocument(onboardingDocumentsToAdd);
    }

    // 2)
    List<OnboardingDocument> onboardingDocumentsToUdpate = OnboardingTemplateHelper
        .listOnboardingDocumentToUpdateUponUpdate(currOnboardingDocuments, newOnboardingDocuments);
    if (!CollectionUtils.isEmpty(onboardingDocumentsToUdpate)) {
      onboardingDocumentDao.batchUpdateLogicalIndexByPrimaryKey(onboardingDocumentsToUdpate);
    }

    // 3)
    List<OnboardingDocument> onboardingDocumentsToDelete = OnboardingTemplateHelper
        .listOnboardingDocumentToDeleteUponUpdate(currOnboardingDocuments, newOnboardingDocuments);
    if (!CollectionUtils.isEmpty(onboardingDocumentsToDelete)) {
      onboardingDocumentDao.batchDeleteOnboardingDocumentsByPrimaryKey(onboardingDocumentsToDelete);
    }

  }

  private void handleOnboardingDocumentUponDelete(OnboardingTemplate onboardingTemplate) {
    List<OnboardingDocument> onboardingDocuments = onboardingDocumentDao.listOnboardingDocumentByOnboardingTemplateId(
        onboardingTemplate.getOrgId(), onboardingTemplate.getOnboardingTemplateId());
    if (!CollectionUtils.isEmpty(onboardingDocuments)) {
      onboardingDocumentDao.batchDeleteOnboardingDocumentsByPrimaryKey(onboardingDocuments);
    }
  }

}
