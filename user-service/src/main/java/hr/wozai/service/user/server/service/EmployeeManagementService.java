package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.userorg.JobTransfer;
import hr.wozai.service.user.server.model.userorg.StatusUpdate;
import hr.wozai.service.user.server.model.userorg.UserSysNotification;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-29
 */
public interface EmployeeManagementService {

  /************************ 调岗 ************************/

  long addJobTransfer(JobTransfer jobTransfer);

  JobTransfer getJobTransfer(long orgId, long jobTransferId);

  List<JobTransfer> listJobTransfer(long orgId, int pageNumber, int pageSize);

  List<JobTransfer> listJobTransfer(long orgId, List<Long> jobTransferIds);

  int countJobTransferByOrgId(long orgId);

  /************************ 转正 & 离职 ************************/

  long addPassProbationStatusUpdate(StatusUpdate statusUpdate);

  long addResignStatusUpdate(StatusUpdate statusUpdate);

  StatusUpdate getStatusUpdate(long orgId, long statusUpdateId);

  List<StatusUpdate> listStatusUpdate(long orgId, int statusType, int pageNumber, int pageSize);

  List<StatusUpdate> listStatusUpdate(long orgId, List<Long> statusUpdateIds);

  int countStatusUpdate(long orgId, int statusType);

  void revokePassProbationStatusUpdate(long orgId, long statusUpdateId, long actorUserId);

  void revokeResignStatusUpdate(long orgId, long statusUpdateId, long actorUserId);

  /************************ 删档 ************************/

  void deleteUser(long orgId, long userId, long actorUserId);

  /************************ 通知 ************************/

  List<Long> listToNotifyUserIds(long orgId, long objectId, int objectType);

}
