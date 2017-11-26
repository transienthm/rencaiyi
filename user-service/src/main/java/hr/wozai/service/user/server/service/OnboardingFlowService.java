// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service;

import java.util.List;
import java.util.Map;

import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.UserEmployment;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-23
 */
public interface OnboardingFlowService {


  CoreUserProfile createOrgAndFirstUser(
      Org org, Map<String, String> userProfileFieldValuesOfHR, UserEmployment userEmploymentOfFirstUser);

//  /**
//   * TODO: delete after @LPJ setup
//   */
//  void addSuperAdminForExistedOrg(long orgId, String emailAddressOfSuperAdmin, String passwordPlainTextOfSuperAdmin);

  List<Long> batchImportStaff(
      long orgId, List<List<String>> rawFieldLists, long actorUserId, long adminUserId);

  long individuallyImportStaff(
      long orgId, String fullName, String emailAddress, String mobilePhone, long actorUserId, long adminUserId);

  void grantManualOperationOfCSVFile(long orgId, long documentId, long actorUserId, long adminUserId);


  long launchOnboardingFlowForIndividualStaffByHR(
          long orgId, long onboardingTemplateId, Map<String, String> userProfileFieldValues, UserEmployment userEmployment,
          List<Long> roleIds, long teamId, int isTeamAdmin, long reporterUserId, long actorUserId, long adminUserId);

  /********************** update onboardingStatus **********************/

  void updateOnboardingStatus(long orgId, long userId, int onboardingStatus, long actorUserId, long adminUserId);

}
