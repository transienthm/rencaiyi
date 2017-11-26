package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.common.RecentUsedObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/24
 */
public interface SearchHistoryService {
  long addRecentUsedObject(RecentUsedObject recentUsedObject);

  RecentUsedObject gerRecentUsedObjectByUserIdAndType(long orgId, long userId, int type);
}
