// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.onboarding.OnboardingDocument;
import hr.wozai.service.user.server.model.onboarding.OnboardingTemplate;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-07
 */
public interface OnboardingTemplateService {

  /**
   * 添加 onboardingTemplate
   *
   * @param onboardingTemplate
   * @return
   */
  long addOnboardingTemplate(OnboardingTemplate onboardingTemplate);

  /**
   * 获取单个 onboardingTemplate
   *
   * @param onboardingTemplateId
   * @param orgId
   * @return
   */
  OnboardingTemplate getOnboardingTemplate(long orgId, long onboardingTemplateId);

  /**
   * 获取org下的 onboardingTemplate 列表
   *
   * @param orgId
   * @return
   */
  List<OnboardingTemplate> listOnboardingTemplate(long orgId);

  /**
   * 更新 onboardingTemplate
   *
   * @param onboardingTemplate
   */
  void updateOnboardingTemplate(OnboardingTemplate onboardingTemplate);

  /**
   * 删除 onboardingTemplate
   *
   * @param onboardingTemplateId
   * @param orgId
   */
  void deleteOnboardingTemplate(long orgId, long onboardingTemplateId, long actorUserId);

//  /**
//   * 添加入职文档
//   *
//   * @param onboardingDocument
//   */
//  long addOnboardingDocument(OnboardingDocument onboardingDocument);
//
//  /**
//   * 获取入职文档列表
//   *
//   * @param onboardingTemplateId
//   * @param orgId
//   * @return
//   */
//  List<OnboardingDocument> listOnboardingDocument(long orgId, long onboardingTemplateId);
//
//  /**
//   * Move the onboardingDocumetn to another logicalIndex
//   *
//   * @param onboardingDocument
//   */
//  void moveOnboardingDocument(OnboardingDocument onboardingDocument);
//
//  /**
//   * 删除入职文档
//   *
//   * @param orgId
//   * @param onboardingDocumentId
//   * @param onboardingTemplateId
//   * @param actorUserId
//   */
//  void deleteOnboardingDocument(long orgId, long onboardingDocumentId, long onboardingTemplateId, long actorUserId);

}
