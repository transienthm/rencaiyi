package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.EmailUtils;
import hr.wozai.service.user.server.dao.userorg.*;
import hr.wozai.service.user.server.helper.UserAccountHelper;
import hr.wozai.service.user.server.model.userorg.*;
import hr.wozai.service.user.server.service.UserService;
import hr.wozai.service.user.server.util.Pinyin4jUtil;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/18
 */
@Service("userService")
public class UserServiceImpl implements UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  private static final String LOGIN_FAIL_TIME = "login_fail_time";

  @Autowired
  UserAccountDao userAccountDao;

  @Autowired
  OrgDao orgDao;

  @Autowired
  OrgMemberDao orgMemberDao;

  @Autowired
  ReportLineDao reportLineDao;

  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addUserAccount(UserAccount userAccount) {
    String emailAddress = userAccount.getEmailAddress();
    if (emailAddress == null || !EmailUtils.isValidEmailAddressByRegex(emailAddress)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM, "email is not valid");
    }
    UserAccount existingUserAccount = userAccountDao.findByEmailAddress(emailAddress);
    if (null != existingUserAccount) {
      throw new ServiceStatusException(ServiceStatus.AS_EMAIL_EXIST);
    }

    if (userAccount.getCreatedUserId() == null) {
      userAccount.setCreatedUserId(0L);
    }

    long userId = userAccountDao.insertUserAccount(userAccount);

    return userId;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean deleteUserAccount(long userId, long actorUserId) {
    userAccountDao.deleteUserAccountByPrimaryKey(userId, actorUserId);
    return true;
  }

  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean updateUserAccount(UserAccount userAccount) {
    if (!UserAccountHelper.isAcceptableUpdateRequest(userAccount)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    userAccountDao.updateByPrimaryKeySelective(userAccount);
    return true;
  }

  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public UserAccount getUserAccountByUserId(long userId) {
    UserAccount userAccount = userAccountDao.findByPrimaryKey(userId);
    if (null == userAccount) {
      LOGGER.info("getUserAccountByUserId(): userId={}", userId);
      throw new ServiceStatusException(ServiceStatus.UO_USER_NOT_FOUND);
    }
    return userAccount;
  }

  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public UserAccount getUserAccountByEmailAddress(String emailAddress) {
    UserAccount userAccount = userAccountDao.findByEmailAddress(emailAddress);
    if (null == userAccount) {
      throw new ServiceStatusException(ServiceStatus.UO_USER_NOT_FOUND);
    }
    return userAccount;
  }

  @Override
  public List<UserAccount> listUserAccountByEmailAddress(List<String> emailAddress) {
    return userAccountDao.listUserAccountByEmailAddress(emailAddress);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addOrgMember(long orgId, long userId, long actorUserId) {
    Org existingOrg = orgDao.findOrgByPrimaryKey(orgId);
    if (existingOrg == null) {
      throw new ServiceStatusException(ServiceStatus.UO_ORG_NOT_FOUND, "org not found");
    }

    OrgMember inDb = orgMemberDao.findByUserIdAndOrgId(orgId, userId);
    if (inDb != null) {
      throw new ServiceStatusException(ServiceStatus.UO_ORG_MEMBER_EXIST, "org member existing");
    }
    OrgMember orgMember = new OrgMember();
    orgMember.setUserId(userId);
    orgMember.setOrgId(orgId);
    orgMember.setCreatedUserId(actorUserId);
    long orgMemberId = orgMemberDao.insertOrgMember(orgMember);
    return orgMemberId;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean deleteOrgMember(long userId) {
    orgMemberDao.deleteOrgMemberByUserId(userId);
    return true;
  }

  @Override
  @LogAround
  public List<Long> listAllUsersByOrgId(long orgId) {
    List<Long> result = orgMemberDao.listUserIdListByOrgId(orgId);
    return result;
  }

  @Override
  public long findOrgIdByUserId(long userId) {
    Long orgId = orgMemberDao.findOrgIdByUserId(userId);
    if (null == orgId) {
      throw new ServiceStatusException(ServiceStatus.UO_ORG_MEMBER_NOT_FOUND);
    }
    return orgId;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public List<Long> listReporteesByUserIdAndOrgId(long orgId, long userId) {
    return reportLineDao.listReporteesByUserId(orgId, userId);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long getReportorByUserIdAndOrgId(long orgId, long userId) {
    ReportLine reportLine = reportLineDao.getReportLineByUserId(orgId, userId);
    if (reportLine == null) {
      throw new ServiceStatusException(ServiceStatus.UO_REPORTLINE_NOT_FOUND);
    }
    return reportLine.getReportUserId();
  }

  @Override
  @LogAround
  public boolean hasReportor(long orgId, long userId) {
    ReportLine reportLine = reportLineDao.getReportLineByUserId(orgId, userId);
    if (reportLine == null || reportLine.getReportUserId().longValue() == 0L) {
      return false;
    }
    return true;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void batchInsertReportLine(long orgId, List<Long> userIds, long reportUserId,
                                    long actorUserId) {
    if (userIds.size() == 0) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    List<ReportLine> reportLines = new ArrayList<>();
    for (long userId : userIds) {
      if (userId == reportUserId) {
        throw new ServiceStatusException(ServiceStatus.UO_REPORTLINE_UPDATE_FAIL);
      }
      ReportLine reportLine = new ReportLine();
      reportLine.setOrgId(orgId);
      reportLine.setUserId(userId);
      reportLine.setReportUserId(reportUserId);
      reportLine.setCreatedUserId(actorUserId);
      reportLine.setLastModifiedUserId(actorUserId);

      reportLines.add(reportLine);
    }


    reportLineDao.batchInsertReportLines(reportLines);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void batchUpdateReportLine(long orgId, List<Long> userIds, long newReportUserId, long actorUserId) {
    if (!CollectionUtils.isEmpty(userIds)) {
      for (Long userId : userIds) {
        if (userId == newReportUserId) {
          throw new ServiceStatusException(ServiceStatus.UO_REPORTLINE_UPDATE_FAIL);
        }
        List<Long> children = listSubReportees(orgId, userId);
        if (children.contains(newReportUserId)) {
          throw new ServiceStatusException(ServiceStatus.UO_REPORTLINE_UPDATE_FAIL);
        }
      }
    }
    reportLineDao.batchDeleteReportLines(orgId, userIds, actorUserId);
    this.batchInsertReportLine(orgId, userIds, newReportUserId, actorUserId);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void batchDeleteReportLine(long orgId, List<Long> userIds, long actorUserId) {
    for (Long userId : userIds) {
      List<Long> children = listSubReportees(orgId, userId);
      if (children.size() != 0) {
        throw new ServiceStatusException(ServiceStatus.UO_REPORTLINE_DELETE_FAIL);
      }
    }
    reportLineDao.batchDeleteReportLines(orgId, userIds, actorUserId);
  }

  @Override
  @LogAround
  public List<ReportLine> listReportLinesByUserIds(long orgId, List<Long> userIds) {
    return reportLineDao.listReportLinesByUserIds(orgId, userIds);
  }

  @Override
  @LogAround
  public List<Long> listUpReportLineByUserId(long orgId, long userId) {
    List<Long> result = new ArrayList<>();

    while (userId != 0) {
      ReportLine reportLine = reportLineDao.getReportLineByUserId(orgId, userId);
      Long reportUserId = reportLine.getReportUserId();
      if (reportUserId != 0) {
        result.add(reportUserId);
      }
      userId = reportUserId;
    }
    return result;
  }

  public List<Long> listSubReportees(long orgId, long reportorUserId) {
    List<Long> result = new ArrayList<>();

    Queue<Long> queue = new LinkedBlockingQueue<>();
    queue.add(reportorUserId);
    while (!queue.isEmpty()) {
      Long r = queue.poll();
      List<Long> reporteeUserIds = reportLineDao.listReporteesByUserId(orgId, r);
      result.addAll(reporteeUserIds);
      queue.addAll(reporteeUserIds);
    }
    LOGGER.info("listSubTeams() success, result size is {}", result.size());
    return result;
  }
}
