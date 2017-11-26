// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.thirdparty.client.utils.RabbitMQProducer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.wozai.service.servicecommons.commons.consts.SystemFieldConsts;
import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.enums.OnboardingStatus;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.EmailUtils;
import hr.wozai.service.servicecommons.commons.utils.PasswordUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.enums.ContentIndexType;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.enums.UserGender;
import hr.wozai.service.user.server.component.OnboardingFlowNotifier;
import hr.wozai.service.user.server.constant.TimeConst;
import hr.wozai.service.user.server.factory.UserProfileConfigFactory;
import hr.wozai.service.user.server.helper.OnboardingFlowHelper;
import hr.wozai.service.user.server.helper.ValidationCheckResult;
import hr.wozai.service.user.server.model.document.Document;
import hr.wozai.service.user.server.model.onboarding.OnboardingTemplate;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.ProfileTemplate;
import hr.wozai.service.user.server.model.userorg.Team;
import hr.wozai.service.user.server.model.userorg.TeamMember;
import hr.wozai.service.user.server.model.userorg.UserAccount;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.model.userorg.UserProfile;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;
import hr.wozai.service.user.server.service.DocumentService;
import hr.wozai.service.user.server.service.NameIndexService;
import hr.wozai.service.user.server.service.OnboardingFlowService;
import hr.wozai.service.user.server.service.OnboardingTemplateService;
import hr.wozai.service.user.server.service.OrgPickOptionService;
import hr.wozai.service.user.server.service.OrgService;
import hr.wozai.service.user.server.service.ProfileFieldService;
import hr.wozai.service.user.server.service.ProfileTemplateService;
import hr.wozai.service.user.server.service.RemindSettingService;
import hr.wozai.service.user.server.service.S3DocumentService;
import hr.wozai.service.user.server.service.SecurityModelService;
import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.service.UserEmploymentService;
import hr.wozai.service.user.server.service.UserProfileService;
import hr.wozai.service.user.server.service.UserService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-23
 */
@Service("onboardingFlowService")
public class OnboardingFlowServiceImpl implements OnboardingFlowService {

  private static Logger LOGGER = LoggerFactory.getLogger(OnboardingFlowServiceImpl.class);

  private static final String PRESET_PROFILE_TEMPLATE_DISPLAY_NAME = "员工档案模板";
  private static final String PRESET_ONBOARDING_TEMPLATE_DISPLAY_NAME = "员工入职模板";

  private static final String HTTP_ENDPOINT_PREFIX_OF_AUTH = "u?uuid=";
  private static final String HTTP_ENDPOINT_SURFIX_OF_INIT_PASSWORD = "#init-password";

  private static final String HTTP_ENDPOINT_SURFIX_OF_ONBOARDING_FLOW_REVIEW = "#/team/staffProfile/";

  private static final String HTTP_ENDPOINT_SURFIX_OF_LOGIN = "u";

  private static final String PARAM_DST_EMAIL_ADDRESS = "dstEmailAddress";
  private static final String PARAM_ORG_SHORT_NAME = "orgShortName";
  private static final String PARAM_USER_FULL_NAME = "userFullName";
  private static final String PARAM_INVITATION_URL = "invitationUrl";
  private static final String PARAM_STAFF_FULL_NAME = "staffFullName";
  private static final String PARAM_HR_FULL_NAME = "hrFullName";
  private static final String PARAM_ONBOARDING_REVIEW_URL = "onboardingReviewUrl";
  private static final String PARAM_ONBOARDING_INVITATION_URL = "onboardingInvitationUrl";
  private static final String PARAM_LOGIN_URL = "loginUrl";

  private static final String SUPER_ADMIN_FULL_NAME = "超级管理员";
  private static final String SUPER_ADMIN_MOBILE_PHONE = "00000000000";
  private static final String SUPER_ADMIN_GENDER = String.valueOf(UserGender.MALE.getCode());


  /*****
   * For email and messageCenter
   *****/

  @Value("${url.host}")
  String host;

/*  @Autowired
  SqsProducer sqsProducer;*/

  @Autowired
  RabbitMQProducer rabbitMQProducer;

  @Autowired
  ProfileTemplateService profileTemplateService;

  @Autowired
  ProfileFieldService profileFieldService;

  @Autowired
  OnboardingTemplateService onboardingTemplateService;

  @Autowired
  UserProfileService userProfileService;

  @Autowired
  UserService userService;

  @Autowired
  TeamService teamService;

  @Autowired
  OrgService orgService;

  @Autowired
  OrgPickOptionService orgPickOptionService;

  @Autowired
  SecurityModelService securityModelService;

  @Autowired
  NameIndexService nameIndexService;

  @Autowired
  UserEmploymentService userEmploymentService;

  @Autowired
  RemindSettingService remindSettingService;

  @Autowired
  DocumentService documentService;

  @Autowired
  @Qualifier("ossDocumentService")
  S3DocumentService ossDocumentService;

  @Autowired
  OnboardingFlowNotifier onboardingFlowNotifier;

  @Autowired
  UserProfileConfigFactory userProfileConfigFactory;

  @Autowired
  PasswordEncoder passwordEncoder;

  /**
   * Steps:
   * 1) add org
   * 2) add role & permission
   * 3) add the default profile-template which is empty
   * 4) add preset onboarding-template
   * 5) add preset jobTitle and jobLevel
   * 6) add preset userProfileConfig
   * 7) init root team
   * 8) add first user
   * [Deprecated] 9) add super admin
   *  @param org
   * @param userProfileFieldValuesOfFirstUser
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public CoreUserProfile createOrgAndFirstUser(
      Org org, Map<String, String> userProfileFieldValuesOfFirstUser, UserEmployment userEmploymentOfFirstUser) {

    if (!OnboardingFlowHelper.isValidOrgForCreateAccount(org)
        || !OnboardingFlowHelper.isValidFieldValuesForCreateAccount(userProfileFieldValuesOfFirstUser)
        || !OnboardingFlowHelper.isValidUserEmploymentForCreateOrgAndFirstHRUser(userEmploymentOfFirstUser)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    // 1)
    long orgId = orgService.addOrg(org);

    // 2)
    securityModelService.initRoleAndRolePermission(orgId, -1L);

    // 3)
    ProfileTemplate profileTemplate = new ProfileTemplate();
    profileTemplate.setOrgId(orgId);
    profileTemplate.setDisplayName(PRESET_PROFILE_TEMPLATE_DISPLAY_NAME);
    profileTemplate.setIsPreset(1);
    profileTemplate.setCreatedUserId(-1L);
    long profileTemplateId = profileTemplateService.addProfileTemplate(profileTemplate);
    // Note: comment out below line on 2016-08-07, according to product design
    // profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, -1L);

    // 4)
    OnboardingTemplate onboardingTemplate = new OnboardingTemplate();
    onboardingTemplate.setOrgId(orgId);
    onboardingTemplate.setProfileTemplateId(profileTemplateId);
    onboardingTemplate.setDisplayName(PRESET_ONBOARDING_TEMPLATE_DISPLAY_NAME);
    onboardingTemplate.setIsPreset(1);
    onboardingTemplate.setCreatedUserId(-1L);
    long onboardingTemplateId = onboardingTemplateService.addOnboardingTemplate(onboardingTemplate);

    // 5)
    orgPickOptionService.initJobTitleAndJobLevelOfOrg(orgId);

    // 6)
    List<UserProfileConfig> presetUserProfileConfigs = userProfileConfigFactory.listPresetUserProfileConfig();
    for (UserProfileConfig userProfileConfig: presetUserProfileConfigs) {
      userProfileConfig.setOrgId(orgId);
    }
    userProfileService.initUserProfileConfigUponCreateOrg(presetUserProfileConfigs);

    // 7)
    Team rootTeam = new Team();
    rootTeam.setTeamName(org.getShortName());
    rootTeam.setParentTeamId(0L);
    rootTeam.setOrgId(orgId);
    rootTeam.setCreatedUserId(-1L);
    long rootTeamId = teamService.addTeam(rootTeam);

    // nameIndexService.addContentIndex(orgId, rootTeamId, ContentIndexType.TEAM_NAME.getCode(), org.getShortName());

    // 8)
    long userId = addFirstUserOfOrg(
        orgId, onboardingTemplateId, profileTemplateId, rootTeamId,
        userEmploymentOfFirstUser, userProfileFieldValuesOfFirstUser);
    CoreUserProfile coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);

//    // 9)
//    addSuperAdminOfOrg(orgId, onboardingTemplateId, profileTemplateId,
//                       emailAddressOfSuperAdmin, passwordPlainTextOfSuperAdmin);

    return coreUserProfile;
  }

//  @Override
//  public void addSuperAdminForExistedOrg(
//      long orgId, String emailAddressOfSuperAdmin, String passwordPlainTextOfSuperAdmin) {
//
//    if (!EmailUtils.isValidEmailAddressByRegex(emailAddressOfSuperAdmin)
//        || !PasswordUtils.isValidPassword(passwordPlainTextOfSuperAdmin)) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
//    }
//
//    long userId = 0L;
//    try {
//
//      ProfileTemplate profileTemplate = profileTemplateService.findTheOnlyProfileTemplateOfOrg(orgId);
//
//      // 1)
//      UserAccount userAccount = new UserAccount();
//      userAccount.setEmailAddress(emailAddressOfSuperAdmin);
//      userAccount.setEncryptedPassword(passwordEncoder.encode(passwordPlainTextOfSuperAdmin));
//      userAccount.setCreatedUserId(-1L);
//      userId = userService.addUserAccount(userAccount);
//
//      // 2)
//      Map<String, String> fieldValues = new HashMap<>();
//      fieldValues.put(SystemProfileField.FULL_NAME.getReferenceName(), SUPER_ADMIN_FULL_NAME);
//      fieldValues.put(SystemProfileField.EMAIL_ADDRESS.getReferenceName(), emailAddressOfSuperAdmin);
//      fieldValues.put(SystemProfileField.MOBILE_PHONE.getReferenceName(), SUPER_ADMIN_MOBILE_PHONE);
//      fieldValues.put(SystemProfileField.GENDER.getReferenceName(), SUPER_ADMIN_GENDER);
//      userProfileService.addCoreAndBasicAndMetaUserProfileForOnboarding(
//          orgId, userId, null, profileTemplate.getProfileTemplateId(), fieldValues, -1);
//
//      // 3)
//      userService.addOrgMember(orgId, userId, -1L);
//
//      // 4)
//      List<Role> roles = securityModelService.listRolesByOrgId(orgId);
//      for (Role role : roles) {
//        if (DefaultRole.SUPER_ADMIN.getName().equals(role.getRoleName())) {
//          securityModelService.assignRoleToUser(orgId, userId, role.getRoleId(), 0L, -1L);
//        }
//      }
//
//      // 5)
//      UserEmployment userEmployment = new UserEmployment();
//      userEmployment.setOrgId(orgId);
//      userEmployment.setUserId(userId);
//      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
//      userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
//      userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
//      userEmployment.setContractType(ContractType.FULLTIME.getCode());
//      userEmployment.setFulltimeEnrollDate(TimeUtils.getTimestampOfZeroOclockToday());
//      userEmployment.setFulltimeResignDate(TimeConst.ONE_DAY_IN_MILLIS * 365 * 100);
//      userEmployment.setCreatedUserId(-1L);
//      userEmploymentService.addUserEmployment(userEmployment);
//
//    } catch (ServiceStatusException e) {
//      LOGGER.error("addSuperAdminOfOrg-error", e);
//      throw e;
//    }
//
//  }

  /**
   * Steps:
   *  0) check if fieldCount of each line is valid
   *  1) validate fields line by line
   *  2) validate no dup email
   *  3) get roles
   *  4) get root team
   *  5) add staff one by one
   *
   * @param orgId
   * @param rawFieldLists
   * @param actorUserId
   * @param adminUserId
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public List<Long> batchImportStaff(
      long orgId, List<List<String>> rawFieldLists, long actorUserId, long adminUserId) {

    if (CollectionUtils.isEmpty(rawFieldLists)) {
      throw new ServiceStatusException(ServiceStatus.UP_CSV_EMPTY);
    }
    for (List<String> rawFields: rawFieldLists) {
      if (CollectionUtils.isEmpty(rawFields)) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }
    }

    StringBuilder errorMessage = new StringBuilder();

    boolean isNoError = true;

    // 0)
    if (!OnboardingFlowHelper.isValidFieldCountOfEachLine(rawFieldLists)) {
      LOGGER.error("batchImportStaff()-error");
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_CSV_FORMAT);
    }

    // 1)
    appendErrorMessageLineSeperator(errorMessage, "Excel数据检测");
    ValidationCheckResult csvCheckResult = OnboardingFlowHelper
        .validateFieldValueOfEachStaffUponBatchImport(rawFieldLists);
    LOGGER.info("batchImportStaff(): validationCheckResult: noError=" + csvCheckResult.isNoError()
                + ", errorMessage=" + csvCheckResult.getErrorMessage());
    errorMessage.append(csvCheckResult.errorMessage);
    isNoError = csvCheckResult.isNoError();
    if (!isNoError) {
      LOGGER.error("batchImportStaff(): batchImportStaff()-2: errorMessage={}", errorMessage.toString());
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_CSV_DATA, errorMessage.toString());
    }

    // 2)
    appendErrorMessageLineSeperator(errorMessage, "企业邮箱排重检测");
    List<String> emailAddresses = new ArrayList<>();
    for (List<String> fieldValues : rawFieldLists) {
      emailAddresses.add(fieldValues.get(1));
    }
    List<UserAccount> existedUserAccounts = userService.listUserAccountByEmailAddress(emailAddresses);
    ValidationCheckResult dupEmailCheckResult = OnboardingFlowHelper
        .validateIfExistedEmailAddress(rawFieldLists, existedUserAccounts);
    errorMessage.append(dupEmailCheckResult.getErrorMessage());
    if (isNoError) {
      isNoError = dupEmailCheckResult.isNoError();
    }
    if (!isNoError) {
      LOGGER.error("batchImportStaff(): batchImportStaff()-3: errorMessage={}", errorMessage.toString());
      throw new ServiceStatusException(ServiceStatus.UP_INVALID_CSV_DATA, errorMessage.toString());
    }

    // 3)
    List<Role> roles = securityModelService.listRolesByOrgId(orgId);
    long staffRoleId = 0L;
    for (Role role : roles) {
      if (DefaultRole.STAFF.getName().equals(role.getRoleName())) {
        staffRoleId = role.getRoleId();
        break;
      }
    }

    // 4)
    Team rootTeam = teamService.listNextLevelTeams(orgId, 0L).get(0);
    long rootTeamId = rootTeam.getTeamId();

    // 5)
    List<Long> userIds = new ArrayList<>();
    for (List<String> fieldValueList : rawFieldLists) {

      LOGGER.info("batchImportStaff(): fieldValueList={}", fieldValueList);

      UserEmployment userEmployment = new UserEmployment();
      userEmployment.setOrgId(orgId);
      userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
      userEmployment.setCreatedUserId(actorUserId);

      Map<String, String> fieldValueMap = new HashMap<>();
      fieldValueMap.put(SystemProfileField.FULL_NAME.getReferenceName(), fieldValueList.get(0));
      fieldValueMap.put(SystemProfileField.EMAIL_ADDRESS.getReferenceName(), fieldValueList.get(1));
      fieldValueMap.put(SystemProfileField.MOBILE_PHONE.getReferenceName(), fieldValueList.get(2));

      long userId = addOneStaffFromBatchImportByOrgAdmin(
          orgId, fieldValueMap, userEmployment, staffRoleId, rootTeamId, actorUserId, adminUserId);
      userIds.add(userId);

    }
    return userIds;
  }

  /**
   * Steps:
   *  1) validate params
   *  2) validate no dup email
   *  3) get roles
   *  4) get root team
   *  5) add staff
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
  public long individuallyImportStaff(
      long orgId, String fullName, String emailAddress, String mobilePhone, long actorUserId, long adminUserId) {

    // 1)
    if (StringUtils.isNullOrEmpty(fullName)
        || !EmailUtils.isValidEmailAddressByRegex(emailAddress)
        || StringUtils.isNullOrEmpty(mobilePhone)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    // 2)
    List<UserAccount> existedUserAccounts = userService.listUserAccountByEmailAddress(Arrays.asList(emailAddress));
    if (!CollectionUtils.isEmpty(existedUserAccounts)) {
      throw new ServiceStatusException(ServiceStatus.AS_EMAIL_EXIST);
    }

    // 3)
    List<Role> roles = securityModelService.listRolesByOrgId(orgId);
    long staffRoleId = 0L;
    for (Role role : roles) {
      if (DefaultRole.STAFF.getName().equals(role.getRoleName())) {
        staffRoleId = role.getRoleId();
        break;
      }
    }

    // 4)
    Team rootTeam = teamService.listNextLevelTeams(orgId, 0L).get(0);
    long rootTeamId = rootTeam.getTeamId();

    // 5)
    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setOrgId(orgId);
    userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
    userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
    userEmployment.setCreatedUserId(actorUserId);

    Map<String, String> fieldValueMap = new HashMap<>();
    fieldValueMap.put(SystemProfileField.FULL_NAME.getReferenceName(), fullName);
    fieldValueMap.put(SystemProfileField.EMAIL_ADDRESS.getReferenceName(), emailAddress);
    fieldValueMap.put(SystemProfileField.MOBILE_PHONE.getReferenceName(), mobilePhone);

    long userId = addOneStaffFromBatchImportByOrgAdmin(
        orgId, fieldValueMap, userEmployment, staffRoleId, rootTeamId, actorUserId, adminUserId);

    return userId;
  }

  /**
   * Steps:
   *  1) generate presignedGetUrl of csv file
   *  2) send email
   *
   * @param orgId
   * @param documentId
   * @param actorUserId
   * @param adminUserId
   */
  @Override
  public void grantManualOperationOfCSVFile(long orgId, long documentId, long actorUserId, long adminUserId) {

    // 1)
    Document csvDocument = documentService.getDocument(orgId, documentId);
    String presignedGetUrl = ossDocumentService.generatePresignedGetUrl(
        csvDocument.getDocumentKey(), csvDocument.getDocumentName(), TimeConst.ONE_DAY_IN_MILLIS * 2);

    // 2)
    CoreUserProfile actorCUP = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, actorUserId);
    onboardingFlowNotifier.sendManualOperationCSVFileEmailToActor(actorCUP.getEmailAddress(), presignedGetUrl);

  }

  /**
   * Steps:
   *  1) add UserAccount
   *  2) add CoreUserProfile & BasicUserProfile & MetaUserProfile
   *  3) add OrgMember
   *  4) add TeamMember (optional)
   *  5) add ReportLine (optional)
   *  6) add UserName
   *  7) add UserRole(disable admin role setting for ux-friendly)
   *  8) add UserEmployment
   *  9) add remindSetting
   *
   * @param orgId
   * @param onboardingTemplateId
   * @param fieldValues
   * @param userEmployment
   * @param roleIds
   * @param isTeamAdmin
   *@param actorUserId
   * @param adminUserId   @return
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long launchOnboardingFlowForIndividualStaffByHR(
          long orgId, long onboardingTemplateId, Map<String, String> fieldValues, UserEmployment userEmployment,
          List<Long> roleIds, long teamId, int isTeamAdmin, long reporterUserId, long actorUserId, long adminUserId) {

    // validate
    if (!OnboardingFlowHelper.isValidFieldValuesForLaunchOnboardingFlowRequest(fieldValues)
        || !OnboardingFlowHelper.isValidUserEmploymentForLaunchOnboardingFlowRequest(userEmployment)
        || CollectionUtils.isEmpty(roleIds)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    ProfileTemplate profileTemplate = profileTemplateService.findTheOnlyProfileTemplateOfOrg(orgId);

    long userId = 0L;
    try {

      // 1)
      UserAccount userAccount = new UserAccount();
      userAccount.setEmailAddress(fieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME));
      userAccount.setCreatedUserId(actorUserId);
      userId = userService.addUserAccount(userAccount);

      // 2)
      userProfileService.addCoreAndBasicAndMetaUserProfileForOnboarding(
          orgId, userId, onboardingTemplateId, profileTemplate.getProfileTemplateId(), fieldValues, actorUserId);

      // 3)
      userService.addOrgMember(orgId, userId, actorUserId);

      // 4)
      TeamMember teamMember = new TeamMember();
      teamMember.setOrgId(orgId);
      teamMember.setUserId(userId);
      teamMember.setCreatedUserId(actorUserId);
      teamMember.setLastModifiedUserId(actorUserId);
      teamMember.setTeamId(teamId);
      teamMember.setIsTeamAdmin(isTeamAdmin);
      teamService.addTeamMember(teamMember);

      List<Long> userIds = new ArrayList<>();
      userIds.add(userId);
      userService.batchInsertReportLine(orgId, userIds, reporterUserId, actorUserId);

      // 6)
      String fullName = fieldValues.get(SystemProfileField.FULL_NAME.getReferenceName());
      nameIndexService.addContentIndex(orgId, userId, ContentIndexType.USER_NAME.getCode(), fullName);

      // 7)
      for (Long roleId : roleIds) {
        securityModelService.assignRoleToUser(orgId, userId, roleId, 0, actorUserId);
      }

      // 8)
      userEmployment.setUserId(userId);
      userEmployment.setUserStatus(UserStatus.INVITED.getCode());
      userEmployment.setOnboardingStatus(OnboardingStatus.ONBOARDING.getCode());
      userEmployment.setEmploymentStatus(EmploymentStatus.PROBATIONARY.getCode());
      userEmploymentService.addUserEmployment(userEmployment);

      // 9)
      remindSettingService.initRemindSettingByUserId(orgId, userId, actorUserId);

    } catch (ServiceStatusException e) {
      LOGGER.error("launchOnboardingFlowForIndividualStaffByHR-error", e);
      throw e;
    }

    return userId;
  }


  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void updateOnboardingStatus(
      long orgId, long userId, int onboardingStatus, long actorUserId, long adminUserId) {

    // TODO: maybe make the first sql as forUpdate

    UserEmployment userEmployment = userEmploymentService.getUserEmployment(orgId, userId);
    userEmployment.setOnboardingStatus(onboardingStatus);
    userEmployment.setLastModifiedUserId(actorUserId);
    userEmploymentService.updateUserEmployment(userEmployment);

  }

  /**
   * 1) add UserAccount
   * 2) add CoreUserProfile & BoreUserProfile & MetaUserProfile
   * 3) add OrgMember
   * 4) add TeamMember
   * 5) add ReportLine
   * 6) assign Roles to user (disable admin role setting for ux-friendly)
   * 7) add UserEmployment
   * 8) add RemindSetting
   * 9) index user's fullName
   *
   * @param orgId
   * @param onboardingTemplateId
   * @param profileTemplateId
   * @param fieldValues
   * @return
   */
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  private long addFirstUserOfOrg(
      long orgId, long onboardingTemplateId, long profileTemplateId, long rootTeamId,
      UserEmployment userEmployment, Map<String, String> fieldValues) {

    long userId = 0L;
    try {

      // 1)
      String emailAddress = fieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME);
      UserAccount userAccount = new UserAccount();
      userAccount.setEmailAddress(emailAddress);
      userAccount.setCreatedUserId(-1L);
      userId = userService.addUserAccount(userAccount);

      // 2)
//      UserProfile userProfile = new UserProfile();
//      userProfile.setOrgId(orgId);
//      userProfile.setUserId(userId);
//      userProfile.setOnboardingTemplateId(onboardingTemplateId);
//      userProfile.setProfileTemplateId(profileTemplateId);
//      userProfile.setUserStatus(UserStatus.IMPORTED.getCode());
//      userProfile.setCreatedUserId(-1L);
      userProfileService.addCoreAndBasicAndMetaUserProfileForOnboarding(
          orgId, userId, onboardingTemplateId, profileTemplateId,  fieldValues, -1);

      // 3)
      userService.addOrgMember(orgId, userId, -1L);

      // 4)
      TeamMember teamMember = new TeamMember();
      teamMember.setOrgId(orgId);
      teamMember.setTeamId(rootTeamId);
      teamMember.setUserId(userId);
      teamMember.setCreatedUserId(-1L);
      teamMember.setLastModifiedUserId(-1L);
      teamService.addTeamMember(teamMember);

      // 5)
      userService.batchInsertReportLine(orgId, Arrays.asList(userId), 0, -1);

      // 6)
      List<Role> roles = securityModelService.listRolesByOrgId(orgId);
      for (Role role : roles) {
        securityModelService.assignRoleToUser(orgId, userId, role.getRoleId(), 0L, -1L);
      }

      // 7)
//      UserEmployment userEmployment = new UserEmployment();
      userEmployment.setOrgId(orgId);
      userEmployment.setUserId(userId);
      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
      userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
//      userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
//      userEmployment.setContractType(ContractType.FULLTIME.getCode());
//      userEmployment.setFulltimeEnrollDate(TimeUtils.getTimestampOfZeroOclockToday());
//      userEmployment.setFulltimeResignDate(TimeConst.ONE_DAY_IN_MILLIS * 365 * 100);
      userEmployment.setCreatedUserId(-1L);
      userEmploymentService.addUserEmployment(userEmployment);

      // 8)
      remindSettingService.initRemindSettingByUserId(orgId, userId, -1);

      // 9)
      String fullName = fieldValues.get(SystemFieldConsts.FULL_NAME_REF_NAME);
      nameIndexService.addContentIndex(orgId, userId, ContentIndexType.USER_NAME.getCode(), fullName);


    } catch (ServiceStatusException e) {
      LOGGER.error("addFirstUserOfOrg-error", e);
      throw e;
    }

    return userId;
  }

  /**
   * 1) add UserAccount with encrypted password
   * 2) add CoreUserProfile & BoreUserProfile & MetaUserProfile
   * 3) add OrgMember
   * 4) assign Roles to user (disable admin role setting for ux-friendly)
   * 5) add UserEmployment
   *
   * @param orgId
   * @param profileTemplateId
   * @param rootTeamId
   * @param emailAddressOfSuperAdmin
   * @param passwordPlainTextOfSuperAdmin
   * @return
   */
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  private long addSuperAdminOfOrg(
      long orgId, long profileTemplateId, long rootTeamId,
      String emailAddressOfSuperAdmin, String passwordPlainTextOfSuperAdmin) {

    if (!EmailUtils.isValidEmailAddressByRegex(emailAddressOfSuperAdmin)
        || !PasswordUtils.isValidPassword(passwordPlainTextOfSuperAdmin)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    long userId = 0L;
    try {

      // 1)
      UserAccount userAccount = new UserAccount();
      userAccount.setEmailAddress(emailAddressOfSuperAdmin);
      userAccount.setEncryptedPassword(passwordEncoder.encode(passwordPlainTextOfSuperAdmin));
      userAccount.setCreatedUserId(-1L);
      userId = userService.addUserAccount(userAccount);

      // 2)
      Map<String, String> fieldValues = new HashMap<>();
      fieldValues.put(SystemProfileField.FULL_NAME.getReferenceName(), SUPER_ADMIN_FULL_NAME);
      fieldValues.put(SystemProfileField.EMAIL_ADDRESS.getReferenceName(), emailAddressOfSuperAdmin);
      fieldValues.put(SystemProfileField.MOBILE_PHONE.getReferenceName(), SUPER_ADMIN_MOBILE_PHONE);
      fieldValues.put(SystemProfileField.GENDER.getReferenceName(), SUPER_ADMIN_GENDER);
      userProfileService.addCoreAndBasicAndMetaUserProfileForOnboarding(
          orgId, userId, null, profileTemplateId,  fieldValues, -1);

      // 3)
      userService.addOrgMember(orgId, userId, -1L);

      // 4)
      List<Role> roles = securityModelService.listRolesByOrgId(orgId);
      for (Role role : roles) {
        if (DefaultRole.SUPER_ADMIN.getName().equals(role.getRoleName())) {
          securityModelService.assignRoleToUser(orgId, userId, role.getRoleId(), 0L, -1L);
        }
      }

      // 5)
      UserEmployment userEmployment = new UserEmployment();
      userEmployment.setOrgId(orgId);
      userEmployment.setUserId(userId);
//      userEmployment.setUserStatus(UserStatus.SUPER_ADMIN.getCode());
      userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
      userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
      userEmployment.setContractType(ContractType.FULLTIME.getCode());
      userEmployment.setFulltimeEnrollDate(TimeUtils.getTimestampOfZeroOclockToday(TimeUtils.BEIJING));
      userEmployment.setFulltimeResignDate(TimeConst.ONE_DAY_IN_MILLIS * 365 * 100);
      userEmployment.setCreatedUserId(-1L);
      userEmploymentService.addUserEmployment(userEmployment);

    } catch (ServiceStatusException e) {
      LOGGER.error("addSuperAdminOfOrg-error", e);
      throw e;
    }

    return userId;
  }

//  private long addFirstUserOfOrg(
//      long orgId, long onboardingTemplateId, long profileTemplateId, long rootTeamId, Map<String, String> fieldValues) {
//
//    long userId = 0L;
//    try {
//
//      // 1)
//      UserAccount userAccount = new UserAccount();
//      String emailAddress = fieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME);
//      userAccount.setEmailAddress(emailAddress);
//      userAccount.setCreatedUserId(-1L);
//      userId = userService.addUserAccount(userAccount);
//
//      // 2)
//      UserProfile userProfile = new UserProfile();
//      userProfile.setOrgId(orgId);
//      userProfile.setUserId(userId);
//      userProfile.setOnboardingTemplateId(onboardingTemplateId);
//      userProfile.setProfileTemplateId(profileTemplateId);
//      userProfile.setUserStatus(UserStatus.IMPORTED.getCode());
//      userProfile.setCreatedUserId(-1L);
//      userProfileService.addUserProfileForOnboarding(userProfile, fieldValues);
//
//      // 3)
//      userService.addOrgMember(orgId, userId, -1L);
//
//      // 4)
//      TeamMember teamMember = new TeamMember();
//      teamMember.setOrgId(orgId);
//      teamMember.setTeamId(rootTeamId);
//      teamMember.setUserId(userId);
//      teamMember.setCreatedUserId(-1L);
//      teamMember.setLastModifiedUserId(-1L);
//      teamService.addTeamMember(teamMember);
//
//      // 5)
//      userService.batchInsertReportLine(orgId, Arrays.asList(userId), 0, -1);
//
//      // 6)
//      String fullName = fieldValues.get(SystemFieldConsts.FULL_NAME_REF_NAME);
//      nameIndexService.addContentIndex(orgId, userId, ContentIndexType.USER_NAME.getCode(), fullName);
//
//      // 7)
//      List<Role> roles = securityModelService.listRolesByOrgId(orgId);
//      for (Role role : roles) {
//        securityModelService.assignRoleToUser(orgId, userId, role.getRoleId(), 0L, -1L);
//      }
//
//      // 8)
//      UserEmployment userEmployment = new UserEmployment();
//      userEmployment.setOrgId(orgId);
//      userEmployment.setUserId(userId);
//      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
//      userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
//      userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());
//      userEmployment.setContractType(ContractType.FULLTIME.getCode());
//      userEmployment.setFulltimeEnrollDate(TimeUtils.getTimestampOfZeroOclockToday());
//      userEmployment.setFulltimeResignDate(TimeConst.ONE_DAY_IN_MILLIS * 365 * 100);
//      userEmployment.setCreatedUserId(-1L);
//      userEmploymentService.addUserEmployment(userEmployment);
//
//      // 9)
//      remindSettingService.initRemindSettingByUserId(orgId, userId, -1);
//
//    } catch (ServiceStatusException e) {
//      LOGGER.error("addFirstUserOfOrg-error", e);
//      throw e;
//    }
//
//    return userId;
//  }


  /**
   * 1) add UserAccount
   * 2) add CoreUserProfile & BoreUserProfile & MetaUserProfile
   * 3) add OrgMember
   * 4) add TeamMember
   * 5) add ReportLine
   * 6) assign Roles to user (disable admin role setting for ux-friendly)
   * 7) add UserEmployment
   * 8) add RemindSetting
   * 9) index user's fullName
   *
   * @param orgId
   * @param fieldValues
   * @param userEmployment
   * @param roleId
   * @param rootTeamId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
    public long addOneStaffFromBatchImportByOrgAdmin(
      long orgId, Map<String, String> fieldValues, UserEmployment userEmployment,
      long roleId, long rootTeamId, long actorUserId, long adminUserId) {

      long userId = 0L;

      ProfileTemplate profileTemplate = profileTemplateService.findTheOnlyProfileTemplateOfOrg(orgId);

      // 1)
      UserAccount userAccount = new UserAccount();
      String emailAddress = fieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME);
      userAccount.setEmailAddress(emailAddress);
      userAccount.setCreatedUserId(-1L);
      userId = userService.addUserAccount(userAccount);

      // 2)
      UserProfile userProfile = new UserProfile();
      userProfile.setOrgId(orgId);
      userProfile.setUserId(userId);
      userProfile.setProfileTemplateId(profileTemplate.getProfileTemplateId());
      userProfile.setCreatedUserId(actorUserId);
      userProfileService.addCoreAndBasicAndMetaUserProfileForOnboarding(
          orgId, userId, 0L, profileTemplate.getProfileTemplateId(), fieldValues, actorUserId);

      // 3)
      userService.addOrgMember(orgId, userId, actorUserId);

      // 4)
      TeamMember teamMember = new TeamMember();
      teamMember.setOrgId(orgId);
      teamMember.setTeamId(rootTeamId);
      teamMember.setUserId(userId);
      teamMember.setCreatedUserId(-1L);
      teamMember.setLastModifiedUserId(-1L);
      teamService.addTeamMember(teamMember);

      // 5)
      userService.batchInsertReportLine(orgId, Arrays.asList(userId), 0, actorUserId);

      // 6)
      securityModelService.assignRoleToUser(orgId, userId, roleId, 0L, actorUserId);

      // 7)
      userEmployment.setUserId(userId);
      userEmploymentService.addUserEmployment(userEmployment);

      // 8)
      remindSettingService.initRemindSettingByUserId(orgId, userId, actorUserId);

      // 9)
      String fullName = fieldValues.get(SystemFieldConsts.FULL_NAME_REF_NAME);
      nameIndexService.addContentIndex(orgId, userId, ContentIndexType.USER_NAME.getCode(), fullName);

      return userId;
    }



//  /**
//   * Steps:
//   *  1) add UserAccount
//   *  2) add UserProfile and CoreUserProfile
//   *  3) add OrgMember 4) add TeamMember (DO NOT)
//   *  5) add ReportLine (DO NOT)
//   *  6) add UserName
//   *  7) add UserRole(set as STAFF)
//   *  8) add UserEmployment
//   *  9) add remindSetting
//   *
//   * @param orgId
//   * @param userProfileFieldValues
//   * @param userEmployment
//   * @param actorUserId
//   * @param adminUserId
//   * @return
//   */
//  @LogAround
//  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//  public long addOneStaffFromBatchImportByOrgAdmin(
//      long orgId, Map<String, String> userProfileFieldValues, UserEmployment userEmployment,
//      long roleId, long rootTeamId, long actorUserId, long adminUserId) {
//
//    // validate
//    if (!OnboardingFlowHelper.isValidUserProfileForLaunchOnboardingFlowRequest(userProfile)
//        || !OnboardingFlowHelper.isValidFieldValuesForBatchImportRequest(userProfileFieldValues)
//        || !OnboardingFlowHelper.isValidUserEmploymentForLaunchOnboardingFlowRequest(userEmployment)) {
//      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
//    }
//
//    long userId = 0L;
//    try {
//
//      // 1)
//      UserAccount userAccount = new UserAccount();
//      userAccount.setEmailAddress(userProfileFieldValues.get(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME));
//      userAccount.setCreatedUserId(actorUserId);
//      userId = userService.addUserAccount(userAccount);
//
//      // 2)
//      BeanUtils.copyProperties(userEmployment, userProfile);
//      userProfile.setOrgId(orgId);
//      userProfile.setUserId(userId);
////      userProfile.setUserStatus(UserStatus.IMPORTED.getCode());
//      userProfile.setCreatedUserId(actorUserId);
//      userProfileService.addUserProfileForOnboarding(userProfile, userProfileFieldValues);
//
//      // 3)
//      userService.addOrgMember(orgId, userId, actorUserId);
//
//      // 4)
//      TeamMember teamMember = new TeamMember();
//      teamMember.setOrgId(orgId);
//      teamMember.setUserId(userId);
//      teamMember.setCreatedUserId(actorUserId);
//      teamMember.setLastModifiedUserId(actorUserId);
//      teamMember.setTeamId(rootTeamId);
//      teamService.addTeamMember(teamMember);
//
//      // 5)
//      userService.batchInsertReportLine(orgId, Arrays.asList(userId), 0, actorUserId);
//
//      // 6)
//      String fullName = userProfileFieldValues.get(SystemFieldConsts.FULL_NAME_REF_NAME);
//      nameIndexService.addContentIndex(orgId, userId, ContentIndexType.USER_NAME.getCode(), fullName);
//
//      // 7)
//      securityModelService.assignRoleToUser(orgId, userId, roleId, 0, actorUserId);
//
//      // 8)
//      userEmployment.setUserId(userId);
//      userEmployment.setUserStatus(UserStatus.IMPORTED.getCode());
//      userEmployment.setOnboardingStatus(OnboardingStatus.APPROVED.getCode());
//      userEmploymentService.addUserEmployment(userEmployment);
//
//      // 9)
//      remindSettingService.initRemindSettingByUserId(orgId, userId, actorUserId);
//
//    } catch (ServiceStatusException e) {
//      LOGGER.error("launchOnboardingFlowForIndividualStaffByHR-error", e);
//      throw e;
//    }
//
//    return userId;
//  }

  private String generateInvitationUrlOfOnboardingFlowReview(String encryptedUserId) {
    return host + HTTP_ENDPOINT_SURFIX_OF_ONBOARDING_FLOW_REVIEW + encryptedUserId;
  }

  private String generateLoginUrl() {
    return host + HTTP_ENDPOINT_SURFIX_OF_LOGIN;
  }

  private void appendErrorMessageLineSeperator(StringBuilder stringBuilder, String seperator) {
    stringBuilder.append("\n\n************************************\n"
                         + seperator + "\n************************************\n\n");
  }

  private void convertEmptyStringToNull(List<List<String>> rawFieldLists) {
    if (!CollectionUtils.isEmpty(rawFieldLists)) {
      for (List<String> oneLine: rawFieldLists) {
        if (!CollectionUtils.isEmpty(oneLine)) {
          for (int i = 0; i < oneLine.size(); i++) {
            if (StringUtils.isNullOrEmpty(oneLine.get(i))) {
              oneLine.set(i, null);
            }
          }
        }
      }
    }
  }

  private Map<String, String> getMetadataDrivenUserProfileFieldValues(Map<String, String> userProfileFieldValues) {
    if (!MapUtils.isEmpty(userProfileFieldValues)) {
      Map<String, String> cleanUserProfileFieldValues = new HashMap<>();
      for (Map.Entry<String, String> entry: userProfileFieldValues.entrySet()) {
        if (!entry.getKey().equals(SystemFieldConsts.TEAM_ID_REF_NAME)
            && !entry.getKey().equals(SystemFieldConsts.REPORTED_ID_REF_NAME)) {
          cleanUserProfileFieldValues.put(entry.getKey(), entry.getValue());
        }
      }
      return cleanUserProfileFieldValues;
    }
    return MapUtils.EMPTY_MAP;
  }


}
