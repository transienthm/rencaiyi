// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import hr.wozai.service.user.client.onboarding.dto.OnboardingDocumentDTO;
import hr.wozai.service.user.server.model.onboarding.OnboardingDocument;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-09
 */
public class OnboardingDocumentHelper {

  public static boolean isValidAddOnboardingDocumentRequest(OnboardingDocument onboardingDocument) {
    if (null == onboardingDocument
        || null == onboardingDocument.getOrgId()
        || null == onboardingDocument.getOnboardingTemplateId()
        || null == onboardingDocument.getDocumentId()
        || null == onboardingDocument.getCreatedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidUpdateDescriptionofOnboardingDocumentRequest(OnboardingDocument onboardingDocument) {
    if (null == onboardingDocument
        || null == onboardingDocument.getOnboardingDocumentId()
        || null == onboardingDocument.getOrgId()
        || null == onboardingDocument.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidMoveOnboardingDocumentRequest(OnboardingDocument onboardingDocument) {
    if (null == onboardingDocument
        || null == onboardingDocument.getOnboardingDocumentId()
        || null == onboardingDocument.getOnboardingTemplateId()
        || null == onboardingDocument.getOrgId()
        || null == onboardingDocument.getLogicalIndex()
        || null == onboardingDocument.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }

  public static boolean isValidDeleteOnboardingDocumentRequest(OnboardingDocument onboardingDocument) {
    if (null == onboardingDocument
        || null == onboardingDocument.getOnboardingDocumentId()
        || null == onboardingDocument.getOnboardingTemplateId()
        || null == onboardingDocument.getOrgId()
        || null == onboardingDocument.getLastModifiedUserId()) {
      return false;
    }
    return true;
  }

  public static List<OnboardingDocument> convertFromOnboardingDocumentDTOs(
      List<OnboardingDocumentDTO> onboardingDocumentDTOs) {
    if (CollectionUtils.isEmpty(onboardingDocumentDTOs)) {
      return Collections.EMPTY_LIST;
    }
    List<OnboardingDocument> onboardingDocuments = new ArrayList<>();
    for (int i = 0; i < onboardingDocumentDTOs.size(); i++) {
      OnboardingDocument onboardingDocument = new OnboardingDocument();
      BeanUtils.copyProperties(onboardingDocumentDTOs.get(i), onboardingDocument);
      onboardingDocuments.add(onboardingDocument);
    }
    return onboardingDocuments;
  }

  public static List<OnboardingDocumentDTO> convertFromOnboardingDocuments(
      List<OnboardingDocument> onboardingDocuments) {
    if (CollectionUtils.isEmpty(onboardingDocuments)) {
      return Collections.EMPTY_LIST;
    }
    List<OnboardingDocumentDTO> onboardingDocumentDTOs = new ArrayList<>();
    for (int i = 0; i < onboardingDocuments.size(); i++) {
      OnboardingDocumentDTO onboardingDocumentDTO = new OnboardingDocumentDTO();
      BeanUtils.copyProperties(onboardingDocuments.get(i), onboardingDocumentDTO);
      onboardingDocumentDTOs.add(onboardingDocumentDTO);
    }
    return onboardingDocumentDTOs;
  }

}
