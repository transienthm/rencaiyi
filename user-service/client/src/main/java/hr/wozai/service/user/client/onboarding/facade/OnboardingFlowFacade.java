// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.onboarding.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;

import hr.wozai.service.servicecommons.thrift.dto.LongListDTO;
import hr.wozai.service.user.client.document.dto.S3DocumentRequestDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingRequestDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingTemplateDTO;
import hr.wozai.service.user.client.onboarding.dto.SuperAdminDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.onboarding.dto.OrgAccountRequestDTO;
import hr.wozai.service.user.client.userorg.dto.UserProfileDTO;
import hr.wozai.service.servicecommons.thrift.dto.IntegerDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.List;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-09
 */
@ThriftService
public interface OnboardingFlowFacade {

  /********************** Create org account and launch staff from three entrances **********************/

  /**
   * Create org and the fist admin user
   *
   * @param orgAccountRequestDTO
   * @return
   */
  @ThriftMethod
  CoreUserProfileDTO addOrgAndSuperAdminAndFirstUser(OrgAccountRequestDTO orgAccountRequestDTO);

//  /**
//   * TODO: delete after @LPJ setup for existed orgs
//   */
//  @ThriftMethod
//  VoidDTO addSuperAdminForExistedOrg(SuperAdminDTO superAdminDTO);

  /**
   * Batch import staff by orgAdmin from CSV file
   *
   * @param orgId
   * @param rawFieldLists
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  IntegerDTO batchImportStaffByOrgAdmin(
      long orgId, List<List<String>> rawFieldLists, long actorUserId, long adminUserId);

  @ThriftMethod
  LongDTO individuallyImportStaff(
      long orgId, String fullName, String emailAddress, String mobilePhone, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO grantManualOperationOfCSVFile(long orgId, long documentId, long actorUserId, long adminUserId);

  /**
   * Launch the onboarding flow for individual new staff
   *
   * @param orgId
   * @param onboardingRequestDTO
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  LongDTO launchOnboardingFlowOfIndivudualStaff(
      long orgId, OnboardingRequestDTO onboardingRequestDTO, long actorUserId, long adminUserId);

  /**
   * Resend email to init password
   *
   * @param emailAddress
   * @return
   */
  @ThriftMethod
  VoidDTO resendInitPasswordEmail(String emailAddress);

  /************************ Manage onboarding flows launched individually  by HR ************************/

  @ThriftMethod
  LongListDTO countTodoNumbersOfOnboardingAndImporting(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  CoreUserProfileListDTO listOnboardingStaffByHR(
      long orgId, int hasApproved, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO resendInvitationUrlToOnboardingStaffByHR(long orgId, long staffUserId, long actorUserId, long adminUserId);

  /**
   * 'isActivated` = 0 when userStatus = 2;
   * 'isActivated` = 1 when userStatus = 3;
   *
   * @param orgId
   * @param isActivated
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  CoreUserProfileListDTO listImportedStaffByHR(
      long orgId, int isActivated, int pageNumber, int pageSize, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO resendInitPasswordUrlToImportedStaffByHR(long orgId, long staffUserId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO approveOnboardingByHR(long orgId, long userId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO rejectOnboardingSubmisisonByHR(long orgId, long userId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO cancelOnboardingByHR(long orgId, long userId, long actorUserId, long adminUserId);

  /************************ Go thru onboarding flows launched individually by Staff ************************/

  @ThriftMethod
  UserProfileDTO getUserProfileByStaff(long orgId, long userId);

  @ThriftMethod
  VoidDTO updateUserProfileFieldByStaff(long orgId, long userId, Map<String, String> fieldValues);

  @ThriftMethod
  VoidDTO submitOnboardingRequestByStaff(long orgId, long userId);

  @ThriftMethod
  OnboardingTemplateDTO getOnboardingTemplateByStaff(long orgId, long userId);

  @ThriftMethod
  S3DocumentRequestDTO downloadOnboardingDocumentByStaff(long orgId, long userId, long documentId);


}
