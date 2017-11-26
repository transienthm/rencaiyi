package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.common.RemindSetting;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/16
 */
public interface RemindSettingService {
  void initRemindSettingByUserId(long orgId, long userId, long actorUserId);

  void batchUpdateRemindSetting(List<RemindSetting> remindSettingList);

  List<RemindSetting> listRemindSettingByUserId(long orgId, long userId);

  RemindSetting getRemindSettingByUserIdAndRemindType(long orgId, long useId, int remindType);

  void deleteRemindSettingByUserId(long orgId, long userId, long actorUserId);
}
