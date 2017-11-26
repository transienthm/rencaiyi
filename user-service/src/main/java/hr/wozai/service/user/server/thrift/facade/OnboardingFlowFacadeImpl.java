// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.facade;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.OnboardingStatus;
import hr.wozai.service.servicecommons.commons.enums.OssRequestType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.IntegerDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongListDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.servicecommons.utils.uuid.UUIDGenerator;
import hr.wozai.service.user.client.conversation.enums.PeriodType;
import hr.wozai.service.user.client.conversation.enums.RemindDay;
import hr.wozai.service.user.client.document.dto.S3DocumentRequestDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingDocumentDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingRequestDTO;
import hr.wozai.service.user.client.onboarding.dto.OnboardingTemplateDTO;
import hr.wozai.service.user.client.onboarding.dto.OrgAccountRequestDTO;
import hr.wozai.service.user.client.onboarding.facade.OnboardingFlowFacade;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.dto.ProfileFieldDTO;
import hr.wozai.service.user.client.userorg.dto.TeamMemberDTO;
import hr.wozai.service.user.client.userorg.dto.UserEmploymentDTO;
import hr.wozai.service.user.client.userorg.dto.UserProfileDTO;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.enums.UuidUsage;
import hr.wozai.service.user.server.component.OnboardingFlowNotifier;
import hr.wozai.service.user.server.constant.TimeConst;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.helper.UserEmploymentHelper;
import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import hr.wozai.service.user.server.model.document.Document;
import hr.wozai.service.user.server.model.onboarding.OnboardingDocument;
import hr.wozai.service.user.server.model.onboarding.OnboardingTemplate;
import hr.wozai.service.user.server.model.token.UuidInfo;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.PickOption;
import hr.wozai.service.user.server.model.userorg.TeamMemberInfo;
import hr.wozai.service.user.server.model.userorg.UserAccount;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.model.userorg.UserProfile;
import hr.wozai.service.user.server.service.ConvrScheduleService;
import hr.wozai.service.user.server.service.DocumentService;
import hr.wozai.service.user.server.service.OnboardingFlowService;
import hr.wozai.service.user.server.service.OnboardingTemplateService;
import hr.wozai.service.user.server.service.OrgService;
import hr.wozai.service.user.server.service.ProfileFieldService;
import hr.wozai.service.user.server.service.S3DocumentService;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.service.TokenService;
import hr.wozai.service.user.server.service.UserEmploymentService;
import hr.wozai.service.user.server.service.UserProfileService;
import hr.wozai.service.user.server.service.UserService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-16
 */
@Service("onboardingFlowFacade")
public class OnboardingFlowFacadeImpl implements OnboardingFlowFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(OnboardingFlowFacadeImpl.class);

  @Autowired
  OrgService orgService;

  @Autowired
  UserProfileService userProfileService;

  @Autowired
  UserService userService;

  @Autowired
  OnboardingFlowService onboardingFlowService;

  @Autowired
  OnboardingTemplateService onboardingTemplateService;

  @Autowired
  UserEmploymentService userEmploymentService;

  @Autowired
  DocumentService documentService;

  @Autowired
  ProfileFieldService profileFieldService;

  @Autowired
  TokenService tokenService;

  @Autowired
  @Qualifier("ossDocumentService")
  S3DocumentService ossDocumentService;

  @Autowired
  TeamService teamService;

  @Autowired
  ConvrScheduleService convrScheduleService;

  @Autowired
  OnboardingFlowNotifier onboardingFlowNotifier;

  /********************** Create org account and launch staff from three entrances **********************/

  /**
   * Steps:
   *  1) create org + superAdmin + firstHRUser
   *  2) send activation email to first HR
   *  3) send open-account email to superAdmin
   *
   * @param orgAccountRequestDTO
   * @return
   */
  @Override
  @LogAround
  public CoreUserProfileDTO addOrgAndSuperAdminAndFirstUser(OrgAccountRequestDTO orgAccountRequestDTO) {

    CoreUserProfileDTO result = new CoreUserProfileDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      // 1)
      Org org = new Org();
      org.setFullName(orgAccountRequestDTO.getOrgFullName());
      org.setShortName(orgAccountRequestDTO.getOrgShortName());
      org.setAvatarUrl(orgAccountRequestDTO.getOrgAvatarUrl());
      org.setTimeZone(1);

      OnboardingRequestDTO onboardingRequestDTO = orgAccountRequestDTO.getOnboardingRequestDTO();
      if (null == onboardingRequestDTO) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }
      Map<String, String> fieldValues = new HashMap<>();
      fieldValues.put(SystemProfileField.EMAIL_ADDRESS.getReferenceName(), onboardingRequestDTO.getEmailAddress());
      fieldValues.put(SystemProfileField.MOBILE_PHONE.getReferenceName(), onboardingRequestDTO.getMobilePhone());
      fieldValues.put(SystemProfileField.FULL_NAME.getReferenceName(), onboardingRequestDTO.getFullName());
//      fieldValues.put(SystemProfileField.PERSONAL_EMAIL.getReferenceName(), onboardingRequestDTO.getPersonalEmail());
//      fieldValues.put(SystemProfileField.GENDER.getReferenceName(), String.valueOf(UserGender.MALE.getCode()));

      UserEmployment userEmployment = new UserEmployment();
//      userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
//      userEmployment.setContractType(ContractType.FULLTIME.getCode());
//      userEmployment.setFulltimeEnrollDate(TimeUtils.getTimestampOfZeroOclockToday());

//      userEmployment.setEmploymentStatus(onboardingRequestDTO.getEmploymentStatus());
//      if (null != onboardingRequestDTO.getContractType()) {
//        UserEmploymentHelper.setContractTypeAndEnrollDateAndResignDate(
//            userEmployment, onboardingRequestDTO.getContractType(), onboardingRequestDTO.getEnrollDate(), null);
//      }

//      SuperAdminDTO superAdminDTO = orgAccountRequestDTO.getSuperAdminDTO();
//      if (null == superAdminDTO) {
//        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
//      }

      CoreUserProfile insertedCUP = onboardingFlowService.createOrgAndFirstUser(org, fieldValues, userEmployment);
      BeanUtils.copyProperties(insertedCUP, result);

      // 2)
      long orgId = insertedCUP.getOrgId();
      long userId = insertedCUP.getUserId();
      String invitationUUID = UUIDGenerator.generateRandomKey();
      UuidInfo uuidInfo = new UuidInfo();
      uuidInfo.setOrgId(orgId);
      uuidInfo.setUserId(userId);
      uuidInfo.setUuid(invitationUUID);
      uuidInfo.setUuidUsage(UuidUsage.INIT_PWD.getCode());
      uuidInfo.setExpireTime(TimeUtils.getNowTimestmapInMillis() + TimeConst.ONE_DAY_IN_MILLIS * 365 * 100);
      uuidInfo.setCreatedUserId(-1L);
      tokenService.addUuidInfoAndDisablePrevious(uuidInfo);
      onboardingFlowNotifier.sendOpenAccountEmailToFirstStaff(
          org.getShortName(), insertedCUP, invitationUUID);

//      // 3)
//      onboardingFlowNotifier.sendOpenAccountEmailToAdmin(org.getShortName(), superAdminDTO.getEmailAddress());

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addOrgAndSuperAdminAndFirstUser()-error", e);
    }

    return result;
  }

//  @Override
//  public VoidDTO addSuperAdminForExistedOrg(SuperAdminDTO superAdminDTO) {
//
//    VoidDTO result = new VoidDTO();
//    ServiceStatusDTO serviceStatusDTO =
//        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
//    result.setServiceStatusDTO(serviceStatusDTO);
//
//    try {
//      if (!"Sq1234567##".equals(superAdminDTO.getUsageSecret())) {
//        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
//      }
//      onboardingFlowService.addSuperAdminForExistedOrg(
//          superAdminDTO.getOrgId(), superAdminDTO.getEmailAddress(), superAdminDTO.getPasswordPlainText());
//    } catch (Exception e) {
//      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
//      LOGGER.error("addSuperAdminForExistedOrg()-error", e);
//    }
//
//    return result;
//  }

  @Override
  @LogAround
  public LongDTO launchOnboardingFlowOfIndivudualStaff(
      long orgId, OnboardingRequestDTO onboardingRequestDTO, long actorUserId, long adminUserId) {

    if (null == onboardingRequestDTO
        || null == onboardingRequestDTO.getTeamId()
        || null == onboardingRequestDTO.getOnboardingTemplateId()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      Map<String, String> fieldValues = new HashMap<>();
      fieldValues.put(SystemProfileField.EMAIL_ADDRESS.getReferenceName(), onboardingRequestDTO.getEmailAddress());
      fieldValues.put(SystemProfileField.MOBILE_PHONE.getReferenceName(), onboardingRequestDTO.getMobilePhone());
      fieldValues.put(SystemProfileField.FULL_NAME.getReferenceName(), onboardingRequestDTO.getFullName());
//      fieldValues.put(SystemProfileField.PERSONAL_EMAIL.getReferenceName(), onboardingRequestDTO.getPersonalEmail());
      fieldValues.put(SystemProfileField.GENDER.getReferenceName(), String.valueOf(onboardingRequestDTO.getGender()));

      if (null != onboardingRequestDTO.getJobTitle()) {
        fieldValues.put(SystemProfileField.JOB_TITLE.getReferenceName(),
                        String.valueOf(onboardingRequestDTO.getJobTitle()));
      }
      if (null != onboardingRequestDTO.getJobLevel()) {
        fieldValues.put(SystemProfileField.JOB_LEVEL.getReferenceName(),
                        String.valueOf(onboardingRequestDTO.getJobLevel()));
      }
      if (!StringUtils.isNullOrEmpty(onboardingRequestDTO.getEmployeeId())) {
        fieldValues.put(SystemProfileField.EMPLOYEE_ID.getReferenceName(),
                        onboardingRequestDTO.getEmployeeId());
      }

      long teamId = onboardingRequestDTO.getTeamId();
      int isTeamAdmin = onboardingRequestDTO.getIsTeamAdmin();
      long reporterUserId = (null != onboardingRequestDTO.getReporterUserId())
                            ? onboardingRequestDTO.getReporterUserId() : 0L;

      UserEmployment userEmployment = new UserEmployment();
      userEmployment.setOrgId(orgId);
      userEmployment.setCreatedUserId(actorUserId);
      if (null == onboardingRequestDTO.getContractType()
          || null == ContractType.getEnumByCode(onboardingRequestDTO.getContractType())) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      ContractType contractType = ContractType.getEnumByCode(onboardingRequestDTO.getContractType());
      userEmployment.setContractType(onboardingRequestDTO.getContractType());
      if (contractType.equals(ContractType.INTERNSHIP)) {
        userEmployment.setInternshipEnrollDate(onboardingRequestDTO.getEnrollDate());
        userEmployment.setInternshipResignDate(onboardingRequestDTO.getResignDate());
      } else if (contractType.equals(ContractType.FULLTIME)) {
        userEmployment.setFulltimeEnrollDate(onboardingRequestDTO.getEnrollDate());
        userEmployment.setFulltimeResignDate(onboardingRequestDTO.getResignDate());
      } else {
        userEmployment.setParttimeEnrollDate(onboardingRequestDTO.getEnrollDate());
        userEmployment.setParttimeResignDate(onboardingRequestDTO.getResignDate());
      }

      long userId = onboardingFlowService.launchOnboardingFlowForIndividualStaffByHR(
          orgId, onboardingRequestDTO.getOnboardingTemplateId(), fieldValues, userEmployment,
          onboardingRequestDTO.getRoleIds(), teamId, isTeamAdmin, reporterUserId, actorUserId, adminUserId);
      result.setData(userId);

      // add convrSchedule if has reporter
      if (reporterUserId != 0) {
        ConvrSchedule convrSchedule = new ConvrSchedule();
        convrSchedule.setOrgId(orgId);
        convrSchedule.setSourceUserId(reporterUserId);
        convrSchedule.setTargetUserId(userId);
        convrSchedule.setPeriodType(PeriodType.HALF_MONTH.getCode());
        convrSchedule.setRemindDay(RemindDay.MONDAY.getCode());
        convrSchedule.setIsActive(1);
        convrSchedule.setCreatedUserId(0L);
        convrScheduleService.addConvrSchedule(convrSchedule);
      }

      // send invitation email
      String invitationUUID = UUIDGenerator.generateRandomKey();
      UuidInfo uuidInfo = new UuidInfo();
      uuidInfo.setOrgId(orgId);
      uuidInfo.setUserId(userId);
      uuidInfo.setUuid(invitationUUID);
      uuidInfo.setUuidUsage(UuidUsage.ONBOARDING.getCode());
      uuidInfo.setExpireTime(TimeUtils.getNowTimestmapInMillis() + TimeConst.ONE_DAY_IN_MILLIS * 365 * 100);
      uuidInfo.setCreatedUserId(-1L);
      tokenService.addUuidInfoAndDisablePrevious(uuidInfo);
      Org org = orgService.getOrg(orgId);
      CoreUserProfile coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
      onboardingFlowNotifier.sendInvitationEmailToOnboardingStaff(
          org.getShortName(), coreUserProfile, invitationUUID);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("launchOnboardingFlowOfIndivudualStaff()-error", e);
    }

    return result;
  }

  /**
   *
   * @param orgId
   * @param rawFieldLists
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public IntegerDTO batchImportStaffByOrgAdmin(
      long orgId, List<List<String>> rawFieldLists, long actorUserId, long adminUserId) {

    IntegerDTO result = new IntegerDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> userIds = onboardingFlowService.batchImportStaff(orgId, rawFieldLists, actorUserId, adminUserId);
      result.setData(userIds.size());
      // send emails
      if (!CollectionUtils.isEmpty(userIds)) {
        Map<Long, String> userIdUUIDMap = new HashMap<>();
        for (Long userId : userIds) {
          String invitationUUID = UUIDGenerator.generateRandomKey();
          UuidInfo uuidInfo = new UuidInfo();
          uuidInfo.setOrgId(orgId);
          uuidInfo.setUserId(userId);
          uuidInfo.setUuid(invitationUUID);
          uuidInfo.setUuidUsage(UuidUsage.INIT_PWD.getCode());
          uuidInfo.setExpireTime(TimeUtils.getNowTimestmapInMillis() + TimeConst.ONE_DAY_IN_MILLIS * 365 * 100);
          uuidInfo.setCreatedUserId(-1L);
          tokenService.addUuidInfoAndDisablePrevious(uuidInfo);
          userIdUUIDMap.put(userId, invitationUUID);
        }
        Org org = orgService.getOrg(orgId);
        List<CoreUserProfile>
            coreUserProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId, userIds);
        onboardingFlowNotifier.batchSendInvitationEmailToImportedStaff(
            org.getShortName(), coreUserProfiles, userIdUUIDMap);
      }
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("batchImportStaffByOrgAdmin()-error", e);
    }

    return result;

  }

  /**
   * Steps:
   *  1) import staff
   *  2) send notif
   *
   * @param orgId
   * @param fullName
   * @param emailAddress
   * @param mobilePhone
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @Override
  @LogAround
  public LongDTO individuallyImportStaff(
      long orgId, String fullName, String emailAddress, String mobilePhone, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      //1)
      long userId = onboardingFlowService
          .individuallyImportStaff(orgId, fullName, emailAddress, mobilePhone, actorUserId, adminUserId);
      result.setData(userId);

      // 2)
      String invitationUUID = UUIDGenerator.generateRandomKey();
      UuidInfo uuidInfo = new UuidInfo();
      uuidInfo.setOrgId(orgId);
      uuidInfo.setUserId(userId);
      uuidInfo.setUuid(invitationUUID);
      uuidInfo.setUuidUsage(UuidUsage.INIT_PWD.getCode());
      uuidInfo.setExpireTime(TimeUtils.getNowTimestmapInMillis() + TimeConst.ONE_DAY_IN_MILLIS * 365 * 100);
      uuidInfo.setCreatedUserId(-1L);
      tokenService.addUuidInfoAndDisablePrevious(uuidInfo);
      Org org = orgService.getOrg(orgId);
      CoreUserProfile coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
      onboardingFlowNotifier
          .individuallySendInvitationEmailToImportedStaff(org.getShortName(), coreUserProfile, invitationUUID);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("individuallyImportStaff()-error", e);
    }

    return result;
  }

  @Override
  public VoidDTO grantManualOperationOfCSVFile(long orgId, long documentId, long actorUserId, long adminUserId) {


    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      onboardingFlowService.grantManualOperationOfCSVFile(orgId, documentId, actorUserId, adminUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("grantManualOperationOfCSVFile()-error", e);
    }

    return result;
  }

//  @Override
//  @LogAround
//  public LongDTO launchOnboardingFlowOfIndivudualStaff(
//      long orgId, OnboardingRequestDTO onboardingRequestDTO, long actorUserId, long adminUserId) {
//
//    LongDTO result = new LongDTO();
//    ServiceStatusDTO serviceStatusDTO =
//        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
//    result.setServiceStatusDTO(serviceStatusDTO);
//
//    try {
//
//      UserProfile userProfile = new UserProfile();
//      userProfile.setOrgId(orgId);
//      userProfile.setOnboardingTemplateId(onboardingRequestDTO.getOnboardingTemplateId());
//      userProfile.setCreatedUserId(actorUserId);
//
//      Map<String, String> fieldValues = new HashMap<>();
//      fieldValues.put(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME, onboardingRequestDTO.getEmailAddress());
//      fieldValues.put(SystemFieldConsts.MOBILE_PHONE_REF_NAME, onboardingRequestDTO.getMobilePhone());
//      fieldValues.put(SystemFieldConsts.FULL_NAME_REF_NAME, onboardingRequestDTO.getFullName());
////      fieldValues.put(SystemFieldConsts.CITIZEN_ID_REF_NAME, onboardingRequestDTO.getCitizenId());
//      if (null != onboardingRequestDTO.getJobTitle()) {
//        fieldValues.put(SystemFieldConsts.JOB_TITLE_REF_NAME, String.valueOf(onboardingRequestDTO.getJobTitle()));
//      }
//      if (null != onboardingRequestDTO.getJobLevel()) {
//        fieldValues.put(SystemFieldConsts.JOB_LELVE_REF_NAME, String.valueOf(onboardingRequestDTO.getJobLevel()));
//      }
//      if (!StringUtils.isNullOrEmpty(onboardingRequestDTO.getEmployeeId())) {
//        fieldValues.put(SystemFieldConsts.EMPLOYEE_ID_REF_NAME, onboardingRequestDTO.getEmployeeId());
//      }
//      if (null != onboardingRequestDTO.getTeamId()){
//        fieldValues.put(SystemFieldConsts.TEAM_ID_REF_NAME, String.valueOf(onboardingRequestDTO.getTeamId()));
//      }
//      if (null != onboardingRequestDTO.getReporterUserId()) {
//        fieldValues.put(
//            SystemFieldConsts.REPORTED_ID_REF_NAME, String.valueOf(onboardingRequestDTO.getReporterUserId()));
//      }
//
//      UserEmployment userEmployment = new UserEmployment();
//      userEmployment.setOrgId(orgId);
//      userEmployment.setCreatedUserId(actorUserId);
//      if (null == onboardingRequestDTO.getContractType()
//          || null == ContractType.getEnumByCode(onboardingRequestDTO.getContractType())) {
//        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
//      }
//      ContractType contractType = ContractType.getEnumByCode(onboardingRequestDTO.getContractType());
//      userEmployment.setContractType(onboardingRequestDTO.getContractType());
//      if (contractType.equals(ContractType.INTERNSHIP)) {
//        userEmployment.setInternshipEnrollDate(onboardingRequestDTO.getEnrollDate());
//        userEmployment.setInternshipResignDate(onboardingRequestDTO.getResignDate());
//      } else if (contractType.equals(ContractType.FULLTIME)) {
//        userEmployment.setFulltimeEnrollDate(onboardingRequestDTO.getEnrollDate());
//        userEmployment.setFulltimeResignDate(onboardingRequestDTO.getResignDate());
//      } else {
//        userEmployment.setParttimeEnrollDate(onboardingRequestDTO.getEnrollDate());
//        userEmployment.setParttimeResignDate(onboardingRequestDTO.getResignDate());
//      }
//
//      long userId = onboardingFlowService.launchOnboardingFlowForIndividualStaffByHR(
//          orgId, userProfile, fieldValues, userEmployment,
//          onboardingRequestDTO.getRoleIds(), actorUserId, adminUserId);
//      result.setData(userId);
//
//      // send invitation email
//      String invitationUUID = UUIDGenerator.generateRandomKey();
//      UuidInfo uuidInfo = new UuidInfo();
//      uuidInfo.setOrgId(orgId);
//      uuidInfo.setUserId(userId);
//      uuidInfo.setUuid(invitationUUID);
//      uuidInfo.setUuidUsage(UuidUsage.ONBOARDING.getCode());
//      uuidInfo.setExpireTime(TimeUtils.getNowTimestmapInMillis() + TimeConst.ONE_DAY_IN_MILLIS * 2);
//      uuidInfo.setCreatedUserId(-1L);
//      tokenService.addUuidInfoAndDisablePrevious(uuidInfo);
//      Org org = orgService.getOrg(orgId);
//      OldCoreUserProfile oldCoreUserProfile = userProfileService.getOldCoreUserProfileByOrgIdAndUserId(orgId, userId);
//      onboardingFlowNotifier.sendInvitationEmailToOnboardingStaff(
//          org.getShortName(), oldCoreUserProfile, invitationUUID);
//
//    } catch (Exception e) {
//      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
//      LOGGER.error("launchOnboardingFlowOfIndivudualStaff()-error", e);
//    }
//
//    return result;
//  }

  /**
   * Steps:
   *  1) verify can init pwd
   *  2) delete existing uuid
   *  3) send new url
   *
   * @param emailAddress
   * @return
   */
  @Override
  public VoidDTO resendInitPasswordEmail(String emailAddress) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      // 1)
      UserAccount userAccount = null;
      long orgId = 0;
      try {
        userAccount = userService.getUserAccountByEmailAddress(emailAddress);
        orgId = userService.findOrgIdByUserId(userAccount.getUserId());
      } catch (ServiceStatusException e) {
        throw new ServiceStatusException(ServiceStatus.UP_USER_NOT_FOUND_OR_CANNOT_INIT_PWD);
      }
      long userId = userAccount.getUserId();
      UserEmployment userEmployment = null;
      try {
        userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
      } catch (ServiceStatusException e) {
        throw new ServiceStatusException(ServiceStatus.UP_USER_NOT_FOUND_OR_CANNOT_INIT_PWD);
      }

      if (UserStatus.ACTIVE.getCode() == userEmployment.getUserStatus()) {
        throw new ServiceStatusException(ServiceStatus.UP_USER_ALREADY_ACTIVATED);
      } else if (UserStatus.IMPORTED.getCode() != userEmployment.getUserStatus()) {
        throw new ServiceStatusException(ServiceStatus.UP_USER_NOT_FOUND_OR_CANNOT_INIT_PWD);
      }

      // 2)
      tokenService.deleteUuidInfoByUserIdAndUsage(orgId, userId, UuidUsage.INIT_PWD.getCode());

      // 3)
      Org org = orgService.getOrg(orgId);
      CoreUserProfile coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
      String invitationUUID = UUIDGenerator.generateRandomKey();
      UuidInfo uuidInfo = new UuidInfo();
      uuidInfo.setOrgId(orgId);
      uuidInfo.setUserId(userId);
      uuidInfo.setUuid(invitationUUID);
      uuidInfo.setUuidUsage(UuidUsage.INIT_PWD.getCode());
      uuidInfo.setExpireTime(TimeUtils.getNowTimestmapInMillis() + TimeConst.ONE_DAY_IN_MILLIS);
      uuidInfo.setCreatedUserId(-1L);
      tokenService.addUuidInfoAndDisablePrevious(uuidInfo);
      onboardingFlowNotifier.resendInvitationActivationEmailToImportedStaff(
          org.getShortName(), coreUserProfile, invitationUUID);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("resendInitPasswordEmail()-error", e);
    }

    return result;

  }

  @Override
  public LongListDTO countTodoNumbersOfOnboardingAndImporting(long orgId, long actorUserId, long adminUserId) {

    LongListDTO result = new LongListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long unhandledOnboardingCount = userProfileService
          .countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, 0);
      long unhandledImportedCount = userProfileService
          .countCoreUserProfileFromImportByUserStatus(orgId, UserStatus.IMPORTED.getCode());
      List<Long> data = new ArrayList<>();
      data.add(unhandledOnboardingCount);
      data.add(unhandledImportedCount);
      result.setData(data);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("countTodoNumbersOfOnboardingAndImporting()-error", e);
    }

    return result;
  }

  /************************ Manage onboarding flows launched individually  by HR ************************/

  @Override
  @LogAround
  public CoreUserProfileListDTO listOnboardingStaffByHR(
      long orgId, int hasApproved, int pageNumber, int pageSize, long actorUserId, long adminUserId) {

    CoreUserProfileListDTO result = new CoreUserProfileListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<CoreUserProfile> coreUserProfiles = userProfileService
          .listCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, hasApproved, pageNumber, pageSize);
      List<CoreUserProfileDTO> coreUserProfileDTOs = null;
      int totalCount = 0;
      if (!CollectionUtils.isEmpty(coreUserProfiles)) {
        coreUserProfileDTOs = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < coreUserProfiles.size(); i++) {
          CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
          BeanUtils.copyProperties(coreUserProfiles.get(i), coreUserProfileDTO);
          coreUserProfileDTOs.add(coreUserProfileDTO);
          userIds.add(coreUserProfileDTO.getUserId());
        }
        // handle team
        List<TeamMemberInfo> teamMemberInfos = teamService.listTeamMemberInfoByUserIds(orgId, userIds);
        if (!CollectionUtils.isEmpty(teamMemberInfos)) {
          Map<Long, TeamMemberInfo> teamMemberInfoMap = new HashMap<>();
          for (TeamMemberInfo teamMemberInfo : teamMemberInfos) {
            teamMemberInfoMap.put(teamMemberInfo.getUserId(), teamMemberInfo);
          }
          for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
            if (teamMemberInfoMap.containsKey(coreUserProfileDTO.getUserId())) {
              TeamMemberInfo teamMemberInfo = teamMemberInfoMap.get(coreUserProfileDTO.getUserId());
              TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
              BeanUtils.copyProperties(teamMemberInfo, teamMemberDTO);
              coreUserProfileDTO.setTeamMemberDTO(teamMemberDTO);
            }
          }
        }
        // handle jobTitleNames
        batchSetJobTitleNameInDTOs(orgId, coreUserProfiles, coreUserProfileDTOs);
        // handle userEmployment
        List<UserEmployment> userEmployments = userEmploymentService.listUserEmployment(orgId, userIds);
        if (!CollectionUtils.isEmpty(userEmployments)) {
          Map<Long, UserEmployment> userEmploymentMap = new HashMap<>();
          for (UserEmployment userEmployment : userEmployments) {
            userEmploymentMap.put(userEmployment.getUserId(), userEmployment);
          }
          for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
            if (userEmploymentMap.containsKey(coreUserProfileDTO.getUserId())) {
              UserEmployment userEmployment = userEmploymentMap.get(coreUserProfileDTO.getUserId());
              UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO();
              BeanUtils.copyProperties(userEmployment, userEmploymentDTO);
              coreUserProfileDTO.setUserEmploymentDTO(userEmploymentDTO);
            }
          }
        }
//        coreUserProfileDTOs = CoreUserProfileDTOHelper.removeSuperAdminFromList(coreUserProfileDTOs);
        // handle enrollDate
        batchSetEnrollDateInDTOs(userEmployments, coreUserProfileDTOs);
        // fill totalCount
        totalCount = userProfileService.countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(orgId, hasApproved);
      } else {
        coreUserProfileDTOs = Collections.EMPTY_LIST;
        totalCount = 0;
      }
      result.setCoreUserProfileDTOs(coreUserProfileDTOs);
      result.setTotalNumber(totalCount);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listOnboardingStaffByHR()-error", e);
    }

    return result;
  }

  @Override
  public VoidDTO resendInvitationUrlToOnboardingStaffByHR(
      long orgId, long staffUserId, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // send invitation email
      String invitationUUID = UUIDGenerator.generateRandomKey();
      UuidInfo uuidInfo = new UuidInfo();
      uuidInfo.setOrgId(orgId);
      uuidInfo.setUserId(staffUserId);
      uuidInfo.setUuid(invitationUUID);
      uuidInfo.setUuidUsage(UuidUsage.ONBOARDING.getCode());
      uuidInfo.setExpireTime(TimeUtils.getNowTimestmapInMillis() + TimeConst.ONE_DAY_IN_MILLIS * 365 * 100);
      uuidInfo.setCreatedUserId(-1L);
      tokenService.addUuidInfoAndDisablePrevious(uuidInfo);
      Org org = orgService.getOrg(orgId);
      CoreUserProfile coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, staffUserId);
      onboardingFlowNotifier.sendInvitationEmailToOnboardingStaff(
          org.getShortName(), coreUserProfile, invitationUUID);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("resendInvitationUrlToOnboardingStaffByHR()-error", e);
    }

    return result;  }

  @Override
  public CoreUserProfileListDTO listImportedStaffByHR(
      long orgId, int isActivated, int pageNumber, int pageSize, long actorUserId, long adminUserId) {

    CoreUserProfileListDTO result = new CoreUserProfileListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      int userStatus = -1;
      if (1 == isActivated) {
        userStatus = UserStatus.ACTIVE.getCode();
      } else if (0 == isActivated) {
        userStatus = UserStatus.IMPORTED.getCode();
      }

      List<CoreUserProfile> coreUserProfiles = userProfileService
          .listCoreUserProfileFromImportByUserStatus(orgId, userStatus, pageNumber, pageSize);
      List<CoreUserProfileDTO> coreUserProfileDTOs = null;
      int totalCount = 0;
      if (!CollectionUtils.isEmpty(coreUserProfiles)) {
        coreUserProfileDTOs = new ArrayList<>();
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < coreUserProfiles.size(); i++) {
          CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
          BeanUtils.copyProperties(coreUserProfiles.get(i), coreUserProfileDTO);
          coreUserProfileDTOs.add(coreUserProfileDTO);
          userIds.add(coreUserProfileDTO.getUserId());
        }
        // handle team
        List<TeamMemberInfo> teamMemberInfos = teamService.listTeamMemberInfoByUserIds(orgId, userIds);
        if (!CollectionUtils.isEmpty(teamMemberInfos)) {
          Map<Long, TeamMemberInfo> teamMemberInfoMap = new HashMap<>();
          for (TeamMemberInfo teamMemberInfo : teamMemberInfos) {
            teamMemberInfoMap.put(teamMemberInfo.getUserId(), teamMemberInfo);
          }
          for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
            if (teamMemberInfoMap.containsKey(coreUserProfileDTO.getUserId())) {
              TeamMemberInfo teamMemberInfo = teamMemberInfoMap.get(coreUserProfileDTO.getUserId());
              TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
              BeanUtils.copyProperties(teamMemberInfo, teamMemberDTO);
              coreUserProfileDTO.setTeamMemberDTO(teamMemberDTO);
            }
          }
        }
        // handle jobTitleNames
        batchSetJobTitleNameInDTOs(orgId, coreUserProfiles, coreUserProfileDTOs);
        // handle userEmployment
        List<UserEmployment> userEmployments = userEmploymentService.listUserEmployment(orgId, userIds);
        if (!CollectionUtils.isEmpty(userEmployments)) {
          Map<Long, UserEmployment> userEmploymentMap = new HashMap<>();
          for (UserEmployment userEmployment : userEmployments) {
            userEmploymentMap.put(userEmployment.getUserId(), userEmployment);
          }
          for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
            if (userEmploymentMap.containsKey(coreUserProfileDTO.getUserId())) {
              UserEmployment userEmployment = userEmploymentMap.get(coreUserProfileDTO.getUserId());
              UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO();
              BeanUtils.copyProperties(userEmployment, userEmploymentDTO);
              coreUserProfileDTO.setUserEmploymentDTO(userEmploymentDTO);
            }
          }
        }
//        coreUserProfileDTOs = CoreUserProfileDTOHelper.removeSuperAdminFromList(coreUserProfileDTOs);
        // handle enrollDate
        batchSetEnrollDateInDTOs(userEmployments, coreUserProfileDTOs);
        // fill totalCount
        totalCount = userProfileService.countCoreUserProfileFromImportByUserStatus(orgId, userStatus);
      } else {
        coreUserProfileDTOs = Collections.EMPTY_LIST;
        totalCount = 0;
      }
      result.setCoreUserProfileDTOs(coreUserProfileDTOs);
      result.setTotalNumber(totalCount);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listImportedStaffByHR()-error", e);
    }

    return result;
  }

  @Override
  public VoidDTO resendInitPasswordUrlToImportedStaffByHR(
      long orgId, long staffUserId, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {

      // 1)
      UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, staffUserId);
      if (UserStatus.ACTIVE.getCode() == userEmployment.getUserStatus()) {
        throw new ServiceStatusException(ServiceStatus.UP_USER_ALREADY_ACTIVATED);
      } else if (UserStatus.IMPORTED.getCode() != userEmployment.getUserStatus()) {
        throw new ServiceStatusException(ServiceStatus.UP_USER_NOT_FOUND_OR_CANNOT_INIT_PWD);
      }

      // 2)
      tokenService.deleteUuidInfoByUserIdAndUsage(orgId, staffUserId, UuidUsage.INIT_PWD.getCode());

      // 3)
      Org org = orgService.getOrg(orgId);
      CoreUserProfile coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, staffUserId);
      String invitationUUID = UUIDGenerator.generateRandomKey();
      UuidInfo uuidInfo = new UuidInfo();
      uuidInfo.setOrgId(orgId);
      uuidInfo.setUserId(staffUserId);
      uuidInfo.setUuid(invitationUUID);
      uuidInfo.setUuidUsage(UuidUsage.INIT_PWD.getCode());
      uuidInfo.setExpireTime(TimeUtils.getNowTimestmapInMillis() + TimeConst.ONE_DAY_IN_MILLIS * 365 * 100);
      uuidInfo.setCreatedUserId(actorUserId);
      tokenService.addUuidInfoAndDisablePrevious(uuidInfo);
      onboardingFlowNotifier.resendInvitationActivationEmailToImportedStaff(
          org.getShortName(), coreUserProfile, invitationUUID);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("resendInitPasswordUrlToImportedStaffByHR()-error", e);
    }

    return result;  }

  @Override
  @LogAround
  public VoidDTO approveOnboardingByHR(long orgId, long userId, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      onboardingFlowService.updateOnboardingStatus(
          orgId, userId, OnboardingStatus.APPROVED.getCode(), actorUserId, adminUserId);

      // handle email and messageCenter to staff
      Org org = orgService.getOrg(orgId);
      CoreUserProfile staffCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
      CoreUserProfile hrCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, staffCUP.getCreatedUserId());
      onboardingFlowNotifier.sendEmailAndMessageToStaffAfterHrApproveOnboardingFlow(org, staffCUP, hrCUP);

      // broadcast email to everyone
      List<Long> allUserIds = userService.listAllUsersByOrgId(orgId);
      List<CoreUserProfile> coreUserProfiles = userProfileService
          .listCoreUserProfileByOrgIdAndUserId(orgId, allUserIds);
      List<UserEmployment> userEmployments = userEmploymentService.listUserEmployment(orgId, allUserIds);
      List<CoreUserProfile> dstCoreUserProfiles =
          sublistKeepingOnboardingApprovedUserIdForBroadcastNewStaff(coreUserProfiles, userEmployments, userId);
      String jobTitleName = null;
      if (null != staffCUP.getJobTitle()) {
        List<PickOption> pickOptions = profileFieldService
            .listPickOptionsByOrgIdAndPickOptionIds(orgId, Arrays.asList(staffCUP.getJobTitle()));
        if (!CollectionUtils.isEmpty(pickOptions)) {
          jobTitleName = pickOptions.get(0).getOptionValue();
        }
      }
      onboardingFlowNotifier.broadcastEmailToStaffAfterHrApproveOnboardingFlow(
          org.getShortName(), staffCUP, jobTitleName, dstCoreUserProfiles);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("approveOnboardingByHR()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO rejectOnboardingSubmisisonByHR(long orgId, long userId, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      onboardingFlowService.updateOnboardingStatus(
          orgId, userId, OnboardingStatus.ONBOARDING.getCode(), actorUserId, adminUserId);

      // send invitation email
      String invitationUUID = UUIDGenerator.generateRandomKey();
      UuidInfo uuidInfo = new UuidInfo();
      uuidInfo.setOrgId(orgId);
      uuidInfo.setUserId(userId);
      uuidInfo.setUuid(invitationUUID);
      uuidInfo.setUuidUsage(UuidUsage.ONBOARDING.getCode());
      uuidInfo.setExpireTime(TimeUtils.getNowTimestmapInMillis() + TimeConst.ONE_DAY_IN_MILLIS * 365 * 100);
      uuidInfo.setCreatedUserId(-1L);
      tokenService.addUuidInfoAndDisablePrevious(uuidInfo);

      Org org = orgService.getOrg(orgId);
      CoreUserProfile staffCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
      CoreUserProfile hrCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, staffCUP.getCreatedUserId());
      onboardingFlowNotifier
          .sendEmailAndMessageToStaffAfterHrRejectOnboardingFlow(org, staffCUP, hrCUP, invitationUUID);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("rejectOnboardingSubmisisonByHR()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO cancelOnboardingByHR(long orgId, long userId, long actorUserId, long adminUserId) {
    // TODO
    return null;
  }


  /************************ Go thru onboarding flows launched individually by Staff ************************/

  @Override
  @LogAround
  public UserProfileDTO getUserProfileByStaff(long orgId, long userId) {

    UserProfileDTO result = new UserProfileDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      UserProfile userProfile = userProfileService.getUserProfile(orgId, userId);
      BeanUtils.copyProperties(userProfile, result);
      List<ProfileFieldDTO> profileFieldDTOs = new ArrayList<>();
      for (int i = 0; i < userProfile.getProfileFields().size(); i++) {
        ProfileFieldDTO profileFieldDTO = new ProfileFieldDTO();
        BeanHelper.copyPropertiesHandlingJSON(userProfile.getProfileFields().get(i), profileFieldDTO);
        profileFieldDTOs.add(profileFieldDTO);
      }
      result.setProfileFieldDTOs(profileFieldDTOs);
      // handle UserEmployment
      UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
      UserEmploymentDTO userEmploymentDTO = new UserEmploymentDTO();
      BeanUtils.copyProperties(userEmployment, userEmploymentDTO);
      result.setUserEmploymentDTO(userEmploymentDTO);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getUserProfileByStaff()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateUserProfileFieldByStaff(long orgId, long userId, Map<String, String> fieldValues) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      userProfileService.updateUserProfileField(orgId, userId, fieldValues, userId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateUserProfileFieldByStaff()-error", e);
    }

    return result;

  }

  @Override
  @LogAround
  public VoidDTO submitOnboardingRequestByStaff(long orgId, long userId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      onboardingFlowService.updateOnboardingStatus(
          orgId, userId, OnboardingStatus.SUBMITTED.getCode(), userId, -1);

      // handle email and messageCenter
      tokenService.deleteUuidInfoByUserIdAndUsage(orgId, userId, UuidUsage.ONBOARDING.getCode());
      Org org = orgService.getOrg(orgId);
      CoreUserProfile staffCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
      CoreUserProfile hrCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, staffCUP.getCreatedUserId());
      onboardingFlowNotifier
          .sendEmailAndMessageToHrAfterStaffSubmitOnboardingFlow(org, staffCUP, hrCUP);

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("submitOnboardingRequestByStaff()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public OnboardingTemplateDTO getOnboardingTemplateByStaff(long orgId, long userId) {

    OnboardingTemplateDTO result = new OnboardingTemplateDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      UserProfile userProfile = userProfileService.getUserProfile(orgId, userId);
      long onboardingTemplateId = userProfile.getOnboardingTemplateId();
      OnboardingTemplate onboardingTemplate =
          onboardingTemplateService.getOnboardingTemplate(orgId, onboardingTemplateId);
      BeanUtils.copyProperties(onboardingTemplate, result);
      if (!CollectionUtils.isEmpty(onboardingTemplate.getOnboardingDocuments())) {
        List<OnboardingDocumentDTO> onboardingDocumentDTOs = new ArrayList<>();
        for (OnboardingDocument onboardingDocument: onboardingTemplate.getOnboardingDocuments()) {
          OnboardingDocumentDTO onboardingDocumentDTO = new OnboardingDocumentDTO();
          BeanUtils.copyProperties(onboardingDocument, onboardingDocumentDTO);
          onboardingDocumentDTOs.add(onboardingDocumentDTO);
        }
        result.setOnboardingDocumentDTOs(onboardingDocumentDTOs);
      }
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getOnboardingTemplateByStaff()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public S3DocumentRequestDTO downloadOnboardingDocumentByStaff(long orgId, long userId, long documentId) {

    S3DocumentRequestDTO result = new S3DocumentRequestDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Document document = documentService.getDocument(orgId, documentId);
      long effectiveTime = TimeConst.ONE_DAY_IN_MILLIS;
      String getUrl = ossDocumentService.generatePresignedGetUrl(
          document.getDocumentKey(), document.getDocumentName(), effectiveTime);
      result.setDocumentId(documentId);
      result.setRequestType(OssRequestType.GET.getCode());
      result.setEffectiveTime(effectiveTime);
      result.setPresignedUrl(getUrl);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("downloadOnboardingDocumentByStaff()-error", e);
    }

    return result;

  }

  private void batchSetJobTitleNameInDTOs(
      long orgId, List<CoreUserProfile> coreUserProfiles, List<CoreUserProfileDTO> coreUserProfileDTOs) {

    if (CollectionUtils.isEmpty(coreUserProfiles)
        || CollectionUtils.isEmpty(coreUserProfileDTOs)) {
      return;
    }

    Set<Long> pickOptionIdSet = new HashSet<>();
    for (CoreUserProfile coreUserProfile : coreUserProfiles) {
      if (null != coreUserProfile.getJobTitle()) {
        pickOptionIdSet.add(coreUserProfile.getJobTitle());
      }
    }
    if (CollectionUtils.isEmpty(pickOptionIdSet)) {
      return;
    }
    List<PickOption> pickOptions = profileFieldService
        .listPickOptionsByOrgIdAndPickOptionIds(orgId, new ArrayList<>(pickOptionIdSet));
    if (CollectionUtils.isEmpty(pickOptions)) {
      return;
    }
    Map<Long, PickOption> pickOptionMap = new HashMap<>();
    for (PickOption pickOption : pickOptions) {
      pickOptionMap.put(pickOption.getPickOptionId(), pickOption);
    }
    Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap = new HashMap<>();
    for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
      coreUserProfileDTOMap.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
    }
    for (CoreUserProfile coreUserProfile : coreUserProfiles) {
      if (pickOptionMap.containsKey(coreUserProfile.getJobTitle())) {
        if (coreUserProfileDTOMap.containsKey(coreUserProfile.getUserId())) {
          PickOption thePickOption = pickOptionMap.get(coreUserProfile.getJobTitle());
          coreUserProfileDTOMap.get(coreUserProfile.getUserId()).setJobTitleName(thePickOption.getOptionValue());
        }
      }
    }

  }

  private void batchSetEnrollDateInDTOs(
      List<UserEmployment> userEmployments, List<CoreUserProfileDTO> coreUserProfileDTOs) {

    Map<Long, UserEmployment> userEmploymentMap = new HashMap<>();
    for (UserEmployment userEmployment: userEmployments) {
      userEmploymentMap.put(userEmployment.getUserId(), userEmployment);
    }
    for (CoreUserProfileDTO coreUserProfileDTO: coreUserProfileDTOs) {
      if (userEmploymentMap.containsKey(coreUserProfileDTO.getUserId())) {
        UserEmployment userEmployment = userEmploymentMap.get(coreUserProfileDTO.getUserId());
        coreUserProfileDTO.setEnrollDate(UserEmploymentHelper.getEnrollDate(userEmployment));
      }
    }

  }

  private List<CoreUserProfile> sublistKeepingOnboardingApprovedUserIdForBroadcastNewStaff(
      List<CoreUserProfile> coreUserProfiles, List<UserEmployment> userEmployments, long newStaffuserId) {

    if (CollectionUtils.isEmpty(coreUserProfiles)
        || CollectionUtils.isEmpty(userEmployments)) {
      return Collections.EMPTY_LIST;
    }

    Map<Long, UserEmployment> userEmploymentMap = new HashMap<>();
    for (UserEmployment userEmployment: userEmployments) {
      userEmploymentMap.put(userEmployment.getUserId(), userEmployment);
    }
    List<CoreUserProfile> sublist = new ArrayList<>();
    if (!CollectionUtils.isEmpty(coreUserProfiles)) {
      for (CoreUserProfile coreUserProfile : coreUserProfiles) {
        if (coreUserProfile.getUserId() != newStaffuserId
            && null != userEmploymentMap.get(coreUserProfile.getUserId())
            && userEmploymentMap.get(coreUserProfile.getUserId()).getUserStatus() == UserStatus.ACTIVE.getCode()) {
          sublist.add(coreUserProfile);
        }
      }
    }
    return sublist;
  }

}
