// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.onboarding.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.user.client.onboarding.dto.OnboardingDocumentDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingTemplateDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingTemplateListDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-09
 */
@ThriftService
public interface OnboardingTemplateFacade {

  /**
   * 添加 onboardingTemplate
   *
   * @param onboardingTemplateDTO
   * @return
   */
  @ThriftMethod
  LongDTO addCustomOnboardingTemplate(
      long orgId, OnboardingTemplateDTO onboardingTemplateDTO, long actorUserId, long adminUserId);

  /**
   * 获取单个 onboardingTemplate
   *
   * @param onboardingTemplateId
   * @param orgId
   * @return
   */
  @ThriftMethod
  OnboardingTemplateDTO getOnboardingTemplate(
      long orgId, long onboardingTemplateId, long actorUserId, long adminUserId);

  /**
   * 获取org下的 onboardingTemplate 列表
   *
   * @param orgId
   * @return
   */
  @ThriftMethod
  OnboardingTemplateListDTO listOnboardingTemplate(long orgId, long actorUserId, long adminUserId);

  /**
   * 更新 onboardingTemplate
   *
   * @param onboardingTemplateDTO
   */
  @ThriftMethod
  VoidDTO updateOnboardingTemplate(
      long orgId, OnboardingTemplateDTO onboardingTemplateDTO, long actorUserId, long adminUserId);

  /**
   * 删除 onboardingTemplate
   *
   * @param onboardingTemplateId
   * @param orgId
   */
  @ThriftMethod
  VoidDTO deleteOnboardingTemplate(long orgId, long onboardingTemplateId, long actorUserId, long adminUserId);

//  /**
//   * 添加入职文档
//   *
//   * @param orgId
//   * @param onboardingDocumentDTO
//   * @param actorUserId
//   * @param adminUserId
//   * @return
//   */
//  @ThriftMethod
//  LongDTO addOnboardingDocument(
//      long orgId, OnboardingDocumentDTO onboardingDocumentDTO, long actorUserId, long adminUserId);
//
//  /**
//   * 获取入职文档列表
//   *
//   * @param onboardingTemplateId
//   * @param orgId
//   * @return
//   */
//  @ThriftMethod
//  OnboardingDocumentListDTO listOnboardingDocument(
//      long orgId, long onboardingTemplateId, long actorUserId, long adminUserId);
//
//  @ThriftMethod
//  VoidDTO updateDescriptionOfOnboardingDocument(
//      long orgId, OnboardingDocumentDTO onboardingDocumentDTO, long actorUserId, long adminUserId);
//
//  @ThriftMethod
//  VoidDTO moveOnboardingDocument(
//      long orgId, OnboardingDocumentDTO onboardingDocumentDTO, long actorUserId, long adminUserId);
//
//  /**
//   * 删除入职文档
//   *
//   * @param orgId
//   * @param onboardingDocumentId
//   * @param onboardingTemplateId
//   * @param actorUserId
//   * @return
//   */
//  @ThriftMethod
//  VoidDTO deleteOnboardingDocument(
//      long orgId, long onboardingDocumentId, long onboardingTemplateId, long actorUserId, long adminUserId);

}
