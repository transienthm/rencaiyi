// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.dao.userorg.UserEmploymentDao;
import hr.wozai.service.user.server.helper.UserEmploymentHelper;
import hr.wozai.service.user.server.model.userorg.UserEmployment;
import hr.wozai.service.user.server.service.UserEmploymentService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-10
 */
@Service("userEmploymentService")
public class UserEmploymentServiceImpl implements UserEmploymentService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserEmploymentServiceImpl.class);

  private static int NEW_STAFF_LIST_SIZE = 5;
  private static int WORKING_ANNIVERSARY_LIST_SIZE = 5;

  @Autowired
  UserEmploymentDao userEmploymentDao;

  @Override
  @LogAround
  public long addUserEmployment(UserEmployment userEmployment) {
    if (!UserEmploymentHelper.isValidAddUserEmploymentRequest(userEmployment)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    long userEmploymentId = userEmploymentDao.insertUserEmployment(userEmployment);
    return userEmploymentId;
  }

  @Override
  @LogAround
  public UserEmployment getUserEmployment(long orgId, long userId) {
    UserEmployment userEmployment = userEmploymentDao.findUserEmploymentByOrgIdAndUserId(orgId, userId);
    if (null == userEmployment) {
      throw new ServiceStatusException(ServiceStatus.UP_USER_NOT_FOUND);
    }
    return userEmployment;
  }

  @Override
  @LogAround
  public List<UserEmployment> listUserEmployment(long orgId, List<Long> userIds) {
    return userEmploymentDao.listUserEmploymentByOrgIdAndUserId(orgId, userIds);
  }

  @Override
  @LogAround
  public List<Long> listUserIdByOrgIdAndOnboardingStatus(
      long orgId, int onboardingStatus, int pageNumber, int pageSize) {
    return userEmploymentDao.listUserIdByOrgIdAndOnboardingStatus(orgId, onboardingStatus, pageNumber, pageSize);
  }

  @Override
  @LogAround
  public int countUserIdByOrgIdAndOnboardingStatus(long orgId, int onboardingStatus) {
    return userEmploymentDao.countUserIdByOrgIdAndOnboardingStatus(orgId, onboardingStatus);
  }

  @Override
  public List<Long> listUserIdByOrgIdAndOnboardingHasApproved(
      long orgId, int hasApproved, int pageNumber, int pageSize) {
    return userEmploymentDao.listUserIdByOrgIdAndOnboardingHasApproved(orgId, hasApproved, pageNumber, pageSize);
  }

  @Override
  public int countUserIdByOrgIdAndOnboardingHasApproved(long orgId, int hasApproved) {
    return userEmploymentDao.countUserIdByOrgIdAndOnboardingHasApproved(orgId, hasApproved);
  }

  @Override
  @LogAround
  public List<Long> sublistUserIdByUserStatus(long orgId, List<Long> userIds, int userStatus) {

    if (CollectionUtils.isEmpty(userIds)) {
      return Collections.EMPTY_LIST;
    }

    List<Long> subbedUserIds = userEmploymentDao
        .sublistUserIdByUserStatus(orgId, userIds, userStatus);
    Set<Long> subbedUserIdSet = new HashSet<>(subbedUserIds);
    List<Long> orderedUserIds = new ArrayList<>();
    for (Long userId: userIds) {
      if (subbedUserIdSet.contains(userId)) {
        orderedUserIds.add(userId);
      }
    }

    return orderedUserIds;
  }

  @Override
  @LogAround
  public List<Long> sublistUserIdNotResignedByEmploymentStatus(
      long orgId, List<Long> userIds, int employmentStatus) {

    if (CollectionUtils.isEmpty(userIds)) {
      return Collections.EMPTY_LIST;
    }

    List<Long> subbedUserIds = userEmploymentDao
        .sublistUserIdByEmploymentStatus(orgId, userIds, employmentStatus);
    Set<Long> subbedUserIdSet = new HashSet<>(subbedUserIds);
    List<Long> orderedUserIds = new ArrayList<>();
    for (Long userId: userIds) {
      if (subbedUserIdSet.contains(userId)) {
        orderedUserIds.add(userId);
      }
    }

    return orderedUserIds;
  }

  @Override
  @LogAround
  public List<Long> listUserIdOfNewStaffByOrgId(long orgId) {
    return userEmploymentDao.listUserIdByOrgIdAndLimitOrderByEnrollDateDesc(orgId, NEW_STAFF_LIST_SIZE);
  }

  @Override
  @LogAround
  public List<Long> listUserIdOfEnrollAnniversaryByOrgId(long orgId) {
    return userEmploymentDao
        .listUserIdByOrgIdAndLimitOrderByComingAnniversaryGapAsc(orgId, WORKING_ANNIVERSARY_LIST_SIZE);
  }

  @Override
  @LogAround
  public void updateUserEmployment(UserEmployment userEmployment) {
    if (!UserEmploymentHelper.isValidUpdateUserEmploymentRequest(userEmployment)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    userEmploymentDao.updateUserEmploymentByOrgIdAndUserIdSelective(userEmployment);
  }

  @Override
  @LogAround
  public void deleteUserEmployment(long orgId, long userId, long actorUserId) {
    userEmploymentDao.deleteUserEmploymentByOrgIdAndUserId(orgId, userId, actorUserId);
  }

}
