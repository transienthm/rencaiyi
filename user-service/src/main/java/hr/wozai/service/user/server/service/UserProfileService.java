// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service;

import java.util.List;
import java.util.Map;

import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.UserProfile;
import hr.wozai.service.user.server.model.userorg.UserProfileConfig;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-13
 */
public interface UserProfileService {

  /********************* methods after refraction(2016-08-08) *********************/

  void initUserProfileConfigUponCreateOrg(List<UserProfileConfig> presetUserProfileConfigs);

  void addCoreAndBasicAndMetaUserProfileForOnboarding(
      long orgId, long userId, Long onboardingTemplateId, long profileTemplateId,
      Map<String, String> fieldValues, long actorUserId);

  CoreUserProfile getCoreUserProfileByOrgIdAndUserId(long orgId, long userId);

  List<CoreUserProfile> listCoreUserProfileByOrgIdAndUserId(long orgId, List<Long> userIds);

  UserProfile getUserProfile(long orgId, long userId);

  List<CoreUserProfile> listCoreUserProfileFromOnboardingByOrgIdAndHasApproved(
      long orgId, int hasApproved, int pageNumber, int pageSize);

  int countCoreUserProfileFromOnboardingByOrgIdAndHasApproved(
      long orgId, int hasApproved);

  List<CoreUserProfile> listCoreUserProfileFromImportByUserStatus(
      long orgId, int userStatus, int pageNUmber, int pageSize);

  int countCoreUserProfileFromImportByUserStatus(
      long orgId, int userStatus);

  List<CoreUserProfile> listFullNameAndEmailAddressWhichIsNotResigned(long orgId);

  int batchUpdateRosterData(long orgId, List<String> headers, List<List<String>> rawFieldValueList, long actorUserId);

  /********************* methods before refraction(2016-08-08) *********************/


//  long addUserProfile(UserProfile userProfile);

//  long addUserProfileForOnboarding(UserProfile userProfile,  Map<String, String> fieldValues);

  void updateUserStatus(long orgId, long userId, int userStatus, long actorUserId);

  void updateUserProfileField(long orgId, long userId, Map<String, String> fieldValues, long actorUserId);

//  OldCoreUserProfile getOldCoreUserProfileByOrgIdAndUserId(long orgId, long userId);
//
//  List<OldCoreUserProfile> listOldCoreUserProfileByOrgIdAndUserId(long orgId, List<Long> userIds);
//
//  List<OldCoreUserProfile> listCoreUserProfileByCreatedUserId(long orgId, long createdUserId);
//
//  List<OldCoreUserProfile> listCoreUserProfileByOrgId(long orgId, int pageNumber, int pageSize);
//
//  int countCoreUserProfileByOrgId(long orgId);
//
//  long getProfileTemplateId(long orgId, long userId);
//
  void deleteUserProfile(long orgId, long userId, long actorUserId);

  void wipeUserProfileDataOfField(long orgId, long profileFieldId, long actorUserId);


}