package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.common.RecentUsedObject;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/24
 */
public interface NameIndexService {
  void addContentIndex(long orgId, long contentObjectId, int type, String content);

  void deleteContentIndexByObjectIdAndType(long orgId, long contentObjectId, int type, long actorUserId);

  List<Long> listObjectIdsByContentOrPinyinOrAbbreviation(long orgId, String keyword, int type,
                                                          int pageNumber, int pageSize);

  long countIdNumByKeywordAndType(long orgId, String keyword, int type);
}
