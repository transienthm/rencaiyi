package hr.wozai.service.user.client.common.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.user.client.common.dto.RecentUsedObjectDTO;
import hr.wozai.service.user.client.common.dto.RemindSettingDTO;
import hr.wozai.service.user.client.common.dto.RemindSettingListDTO;
import hr.wozai.service.user.client.okr.dto.UserAndTeamListDTO;
import hr.wozai.service.user.client.userorg.dto.TeamListDTO;
import hr.wozai.service.user.client.userorg.dto.UserNameListDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/25
 */
@ThriftService
public interface CommonToolFacade {
  /**
   * list最近查看的okr的所属人和所属team
   * @param orgId
   * @param userId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  UserAndTeamListDTO listRecentCheckedOkrUserAndTeam(long orgId, long userId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO addRecentUsedObject(long orgId, RecentUsedObjectDTO recentUsedObjectDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  TeamListDTO searchUserAndTeamNamesByKeyword(long orgId, String keyword, int pageNumber, int pageSize,
                                              long actorUserId, long adminUserId);

  @ThriftMethod
  UserNameListDTO listUsersByUserNameOrPinyinOrAbbreviation(long orgId, String keyword, int pageNumber,
                                                                   int pageSize, long actorUserId, long adminUserId);

  @ThriftMethod
  UserNameListDTO searchUsersWithTeamScope(long orgId, long teamId, String keyword, int pageNumber,
                                                  int pageSize, long actorUserId, long adminUserId);

  @ThriftMethod
  UserNameListDTO searchDirectorsByKeyword(long orgId, String keyword, int pageNumber,
                                           int pageSize, long actorUserId, long adminUserId);

  @ThriftMethod
  UserNameListDTO searchUsersByKeywordAndType(long orgId, String keyword, int type, int pageNumber,
                                              int pageSize, long actorUserId, long adminUserId);


  //***************************提醒设置部分*************************************************
  @ThriftMethod
  RemindSettingListDTO listRemindSettingByUserId(long orgId, long userId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO batchUpdateRemindSetting(RemindSettingListDTO remindSettingListDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  RemindSettingDTO getRemindSettingByUserIdAndRemindType(long orgId, long userId, int remindType,
                                                         long actorUserId, long adminUserId);
}
