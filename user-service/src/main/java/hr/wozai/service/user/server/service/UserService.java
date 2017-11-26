package hr.wozai.service.user.server.service;


import hr.wozai.service.user.server.model.userorg.ReportLine;
import hr.wozai.service.user.server.model.userorg.UserAccount;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/17
 */
public interface UserService {

  /*long addFirstUserAccountOfOrg(String emailAddress, String password);*/

  long addUserAccount(UserAccount userAccount);

  boolean deleteUserAccount(long userId, long actorUserId);

  boolean updateUserAccount(UserAccount userAccount);

  UserAccount getUserAccountByUserId(long userId);

  UserAccount getUserAccountByEmailAddress(String emailAddress);

  List<UserAccount> listUserAccountByEmailAddress(List<String> emailAddress);

  long addOrgMember(long orgId, long userId, long actorUserId);

  boolean deleteOrgMember(long userId);

  List<Long> listAllUsersByOrgId(long orgId);

  long findOrgIdByUserId(long userId);

  List<Long> listReporteesByUserIdAndOrgId(long orgId, long userId);

  long getReportorByUserIdAndOrgId(long orgId, long userId);

  boolean hasReportor(long orgId, long userId);

  void batchInsertReportLine(long orgId, List<Long> userIds, long reportUserId, long actorUserId);

  void batchUpdateReportLine(long orgId, List<Long> userIds, long newReportUserId,
                             long actorUserId);

  void batchDeleteReportLine(long orgId, List<Long> userIds, long actorUserId);

  List<ReportLine> listReportLinesByUserIds(long orgId, List<Long> userIds);

  List<Long> listUpReportLineByUserId(long orgId, long userId);
}
