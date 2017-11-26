// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.server.model.onboarding.OnboardingDocument;
import hr.wozai.service.user.server.model.onboarding.OnboardingTemplate;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-09
 */
public class OnboardingTemplateHelper {

  private static Logger LOGGER = org.slf4j.LoggerFactory.getLogger(OnboardingTemplateHelper.class);

  public static boolean isAcceptableAddRequest(OnboardingTemplate onboardingTemplate) {
    if (null == onboardingTemplate
        || null == onboardingTemplate.getOrgId()
        || StringUtils.isNullOrEmpty(onboardingTemplate.getDisplayName())
        || null == onboardingTemplate.getIsPreset()
        || null == onboardingTemplate.getCreatedUserId()) {
      return false;
    }
    return true;
  }


  public static boolean isAcceptableUpdateRequest(OnboardingTemplate onboardingTemplate) {
    if (null == onboardingTemplate
        || null == onboardingTemplate.getOrgId()
        || null == onboardingTemplate.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }

  /**
   * Must satisfy requirements:
   *  1) refer unique documents
   *  2) each onboardingDocument is valid add/update itself
   *  3) each logicalIndex equals to list index
   *
   * @param onboardingDocuments
   * @return
   */
  public static boolean isValidOnboardingDocumentSequence(List<OnboardingDocument> onboardingDocuments) {

    LOGGER.info("isValidOnboardingDocumentSequence: seq=" + onboardingDocuments);

    Set<Long> referredDocumentIds = new HashSet<>();
    for (int i = 0; i < onboardingDocuments.size(); i++) {
      OnboardingDocument onboardingDocument = onboardingDocuments.get(i);
      System.out.println("shoot: i=" + i);

      // 1)
      if (referredDocumentIds.contains(onboardingDocument.getDocumentId())) {
        return false;
      } else {
        referredDocumentIds.add(onboardingDocument.getDocumentId());
      }
      // 3)
      if (null != onboardingDocument.getOnboardingDocumentId()) {
        if (!isValidUpdateOnboardingDocumentRequest(onboardingDocument)) {
          System.out.println("shoot: 1");
          return false;
        }
      } else {
        if (!isValidAddOnboardingDocumentRequest(onboardingDocument)) {
          System.out.println("shoot: 2");
          return false;
        }
      }
      // 2)
      if (onboardingDocument.getLogicalIndex() != i) {
        System.out.printf("shoot: 3");
        return false;
      }
    }
    return true;
  }

  public static boolean isValidAddOnboardingDocumentRequest(OnboardingDocument onboardingDocument) {
    if (null == onboardingDocument
        || null == onboardingDocument.getOrgId()
        || null == onboardingDocument.getOnboardingTemplateId()
        || null == onboardingDocument.getDocumentId()
        || null == onboardingDocument.getLogicalIndex()
        || null == onboardingDocument.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidUpdateOnboardingDocumentRequest(OnboardingDocument onboardingDocument) {
    if (null == onboardingDocument
        || null == onboardingDocument.getOrgId()
        || null == onboardingDocument.getOnboardingTemplateId()
        || null == onboardingDocument.getLogicalIndex()
        || null == onboardingDocument.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }

  public static List<OnboardingDocument> listOnboardingDocumentToAddUponUpdate(
      List<OnboardingDocument> currOnboardingDocuments, List<OnboardingDocument> newOnboardingDocuments) {

    if (CollectionUtils.isEmpty(newOnboardingDocuments)) {
      return Collections.EMPTY_LIST;
    }
    List<OnboardingDocument> onboardingDocumentsToAdd = new ArrayList<>();

    Set<Long> currOnboardingDocumentIds = new HashSet<>();
    for (OnboardingDocument currOnboardingDocument: currOnboardingDocuments) {
      currOnboardingDocumentIds.add(currOnboardingDocument.getOnboardingDocumentId());
    }
    for (OnboardingDocument newOnboardingDocument: newOnboardingDocuments) {
      if (null == newOnboardingDocument.getOnboardingDocumentId()
          || !currOnboardingDocumentIds.contains(newOnboardingDocument.getOnboardingDocumentId())) {
        onboardingDocumentsToAdd.add(newOnboardingDocument);
      }
    }

    return onboardingDocumentsToAdd;
  }

  public static List<OnboardingDocument> listOnboardingDocumentToUpdateUponUpdate(
      List<OnboardingDocument> currOnboardingDocuments, List<OnboardingDocument> newOnboardingDocuments) {

    if (CollectionUtils.isEmpty(currOnboardingDocuments)) {
      return Collections.EMPTY_LIST;
    }
    List<OnboardingDocument> onboardingDocumentsToUpdate = new ArrayList<>();

    Set<Long> currOnboardingDocumentIds = new HashSet<>();
    for (OnboardingDocument currOnboardingDocument: currOnboardingDocuments) {
      currOnboardingDocumentIds.add(currOnboardingDocument.getOnboardingDocumentId());
    }
    for (OnboardingDocument newOnboardingDocument: newOnboardingDocuments) {
      if (currOnboardingDocumentIds.contains(newOnboardingDocument.getOnboardingDocumentId())) {
        onboardingDocumentsToUpdate.add(newOnboardingDocument);
      }
    }

    return onboardingDocumentsToUpdate;
  }

  public static List<OnboardingDocument> listOnboardingDocumentToDeleteUponUpdate(
      List<OnboardingDocument> currOnboardingDocuments, List<OnboardingDocument> newOnboardingDocuments) {

    if (CollectionUtils.isEmpty(currOnboardingDocuments)) {
      return Collections.EMPTY_LIST;
    }
    List<OnboardingDocument> onboardingDocumentsToDelete = new ArrayList<>();

    Set<Long> newOnboardingDocumentIds = new HashSet<>();
    for (OnboardingDocument newOnboardingDocument:  newOnboardingDocuments) {
      newOnboardingDocumentIds.add(newOnboardingDocument.getOnboardingDocumentId());
    }
    for (OnboardingDocument currOnboardingDocument: currOnboardingDocuments) {
      if (!newOnboardingDocumentIds.contains(currOnboardingDocument.getOnboardingDocumentId())) {
        onboardingDocumentsToDelete.add(currOnboardingDocument);
      }
    }

    return onboardingDocumentsToDelete;
  }

}
