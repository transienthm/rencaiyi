// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.userorg.UserEmployment;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-19
 */
public interface UserEmploymentService {

  long addUserEmployment(UserEmployment userEmployment);

  UserEmployment getUserEmployment(long orgId, long userId);

  List<UserEmployment> listUserEmployment(long orgId, List<Long> userIds);

  List<Long> listUserIdByOrgIdAndOnboardingStatus(long orgId, int onboardingStatus, int pageNumber, int pageSize);

  int countUserIdByOrgIdAndOnboardingStatus(long orgId, int onboardingStatus);

  List<Long> listUserIdByOrgIdAndOnboardingHasApproved(long orgId, int hasApproved, int pageNumber, int pageSize);

  int countUserIdByOrgIdAndOnboardingHasApproved(long orgId, int hasApproved);

  List<Long> sublistUserIdByUserStatus(long orgId, List<Long> userIds, int userStatus);

  List<Long> sublistUserIdNotResignedByEmploymentStatus(long orgId, List<Long> userIds, int employmentStatus);

  List<Long> listUserIdOfNewStaffByOrgId(long orgId);

  List<Long> listUserIdOfEnrollAnniversaryByOrgId(long orgId);

  void updateUserEmployment(UserEmployment userEmployment);

  void deleteUserEmployment(long orgId, long userId, long actorUserId);

}
