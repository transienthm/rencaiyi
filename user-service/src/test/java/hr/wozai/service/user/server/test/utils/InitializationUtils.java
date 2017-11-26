// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.test.utils;

import com.mysql.jdbc.TimeUtil;

import hr.wozai.service.servicecommons.commons.consts.SystemFieldConsts;
import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.EmploymentStatus;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.utils.uuid.UUIDGenerator;
import hr.wozai.service.user.server.model.userorg.*;
import hr.wozai.service.user.server.service.OnboardingFlowService;
import hr.wozai.service.user.server.service.ProfileFieldService;
import hr.wozai.service.user.server.service.ProfileTemplateService;
import hr.wozai.service.user.server.service.TeamService;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-06-10
 */
public class InitializationUtils {

  private static String orgFullName = "测试北京闪签科技有限公司";
  private static String orgShortName = "测试闪签";
  private static String orgAvatarUrl = "http://";
  private static int timeZone = 1;

  private static String emailAddress = "budaha@sqian.com";
  private static String mobilePhone = "13566677888";
  private static String fullName = "王山前";

  private static String emailAddressOfSuperAdmin = "superadminwozai@sqian.com";
  private static String passwordOfSuperAdmin = "Wozai123";


  private static long actorUserId = 199999999L;
  private static long adminUserId = 0L;


  /**
   * Steps:
   *  1) create org
   *  2) create user
   *
   * @param onboardingFlowService
   * @return
   */
  public static CoreUserProfile initDefaultOrgAndFirstUser(OnboardingFlowService onboardingFlowService) {

    Org org = new Org();
    org.setFullName(orgFullName);
    org.setShortName(orgShortName);
    org.setAvatarUrl(orgAvatarUrl);
    org.setTimeZone(timeZone);

    Map<String, String> fieldValues = new HashMap<>();
    fieldValues.put(SystemFieldConsts.EMAIL_ADDRESS_REF_NAME, emailAddress);
    fieldValues.put(SystemFieldConsts.MOBILE_PHONE_REF_NAME, mobilePhone);
    fieldValues.put(SystemFieldConsts.FULL_NAME_REF_NAME, fullName);

    UserEmployment userEmployment = new UserEmployment();
    userEmployment.setContractType(ContractType.FULLTIME.getCode());
    userEmployment.setFulltimeEnrollDate(TimeUtil.getCurrentTimeNanosOrMillis());
    userEmployment.setEmploymentStatus(EmploymentStatus.REGULAR.getCode());

    CoreUserProfile addedCUP = onboardingFlowService.createOrgAndFirstUser(
        org, fieldValues, userEmployment);

    return addedCUP;
  }

  public static TeamMember initDefaultUserAndTeam(OnboardingFlowService onboardingFlowService, TeamService teamService, long orgId) {
    Team team = new Team();
    team.setOrgId(orgId);
    team.setParentTeamId(0L);
    team.setTeamName("default team");
    team.setCreatedUserId(actorUserId);

    long teamId = teamService.addTeam(team);

    String email = getEmailAddress();
    long userId = onboardingFlowService
            .individuallyImportStaff(orgId, fullName, email, mobilePhone, actorUserId, adminUserId);

    TeamMember teamMember = new TeamMember();
    teamMember.setOrgId(orgId);
    teamMember.setUserId(userId);
    teamMember.setTeamId(teamId);
    teamMember.setCreatedUserId(actorUserId);
    teamMember.setLastModifiedUserId(actorUserId);

    teamService.deleteTeamMember(orgId, userId, actorUserId);
    teamService.addTeamMember(teamMember);

    return teamMember;
  }

  private static String getEmailAddress() {
    return UUIDGenerator.generateShortUuid()  + TimeUtils.getNowTimestmapInMillis() + "@sqian.com";
  }

  public static long initPresetProfileTemplateAndFields(
      ProfileTemplateService profileTemplateService, ProfileFieldService profileFieldService, long orgId) {
    ProfileTemplate profileTemplate = new ProfileTemplate();
    profileTemplate.setOrgId(orgId);
    profileTemplate.setDisplayName("默认员工档案模板");
    profileTemplate.setIsPreset(1);
    profileTemplate.setCreatedUserId(actorUserId);
    long profileTemplateId = profileTemplateService.addProfileTemplate(profileTemplate);
    profileFieldService.addAllPresetFieldForProfileTemplate(orgId, profileTemplateId, actorUserId);
    return profileTemplateId;
  }


}
