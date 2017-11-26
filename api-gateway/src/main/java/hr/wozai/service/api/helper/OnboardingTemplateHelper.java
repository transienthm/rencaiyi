// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.helper;

import hr.wozai.service.api.vo.user.OnboardingDocumentVO;

import hr.wozai.service.user.client.document.dto.DocumentDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingDocumentDTO;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-12
 */
public class OnboardingTemplateHelper {

  private static Logger LOGGER = LoggerFactory.getLogger(OnboardingTemplateHelper.class);

  public static List<OnboardingDocumentVO> generateOnboardingDocumentVOs(
          List<OnboardingDocumentDTO> onboardingDocumentDTOs, List<DocumentDTO> documentDTOs) {

    // TEST
    LOGGER.info("OnDocDTOs={}", onboardingDocumentDTOs);
    LOGGER.info("DocDTOS={}", documentDTOs);

    if (CollectionUtils.isEmpty(onboardingDocumentDTOs)
        || CollectionUtils.isEmpty(documentDTOs)) {
      return Collections.EMPTY_LIST;
    }

    Map<Long, DocumentDTO> documentDTOMap = new HashMap<>();
    for (DocumentDTO documentDTO: documentDTOs) {
      documentDTOMap.put(documentDTO.getDocumentId(), documentDTO);
    }

    List<OnboardingDocumentVO> onboardingDocumentVOs = new ArrayList<>();
    for (int i = 0; i < onboardingDocumentDTOs.size(); i++) {
      OnboardingDocumentDTO onboardingDocumentDTO = onboardingDocumentDTOs.get(i);
      if (documentDTOMap.containsKey(onboardingDocumentDTO.getDocumentId())) {
        DocumentDTO documentDTO = documentDTOMap.get(onboardingDocumentDTO.getDocumentId());
        OnboardingDocumentVO onboardingDocumentVO = new OnboardingDocumentVO();
        BeanUtils.copyProperties(onboardingDocumentDTO, onboardingDocumentVO);
        BeanUtils.copyProperties(documentDTO, onboardingDocumentVO);
        onboardingDocumentVOs.add(onboardingDocumentVO);
      }
    }

    return onboardingDocumentVOs;
  }

  public static List<OnboardingDocumentVO> convertFromOnboardingDocumentDTOs(
      List<OnboardingDocumentDTO> onboardingDocumentDTOs) {
    if (CollectionUtils.isEmpty(onboardingDocumentDTOs)) {
      return Collections.EMPTY_LIST;
    }
    List<OnboardingDocumentVO> onboardingDocumentVOs = new ArrayList<>();
    for (int i = 0; i < onboardingDocumentDTOs.size(); i++) {
      OnboardingDocumentVO onboardingDocumentVO = new OnboardingDocumentVO();
      BeanUtils.copyProperties(onboardingDocumentDTOs.get(i), onboardingDocumentVO);
      onboardingDocumentVOs.add(onboardingDocumentVO);
    }
    return onboardingDocumentVOs;
  }

  public static List<OnboardingDocumentDTO> convertFromOnboardingDocumentVOs(
      List<OnboardingDocumentVO> onboardingDocumentVOs) {
    if (CollectionUtils.isEmpty(onboardingDocumentVOs)) {
      return Collections.EMPTY_LIST;
    }
    List<OnboardingDocumentDTO> onboardingDocumentDTOs = new ArrayList<>();
    for (int i = 0; i < onboardingDocumentVOs.size(); i++) {
      OnboardingDocumentDTO onboardingDocumentDTO = new OnboardingDocumentDTO();
      BeanUtils.copyProperties(onboardingDocumentVOs.get(i), onboardingDocumentDTO);
      onboardingDocumentDTOs.add(onboardingDocumentDTO);
    }
    return onboardingDocumentDTOs;
  }

  public static void main(String[] args) {
    OnboardingDocumentDTO onboardingDocumentDTO = new OnboardingDocumentDTO();
    DocumentDTO documentDTO = new DocumentDTO();
    OnboardingDocumentVO onboardingDocumentVO = new OnboardingDocumentVO();

    onboardingDocumentDTO.setOnboardingTemplateId(100L);
    documentDTO.setDocumentSize(100L);
    BeanUtils.copyProperties(onboardingDocumentDTO, onboardingDocumentVO);
    BeanUtils.copyProperties(documentDTO, onboardingDocumentVO);
    System.out.printf("vo=" + onboardingDocumentVO);
  }

}
