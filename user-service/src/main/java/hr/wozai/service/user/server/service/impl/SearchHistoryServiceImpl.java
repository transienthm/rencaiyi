package hr.wozai.service.user.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.userorg.enums.ContentIndexType;
import hr.wozai.service.user.server.dao.common.ContentIndexDao;
import hr.wozai.service.user.server.dao.common.RecentUsedObjectDao;
import hr.wozai.service.user.server.helper.RecentUsedObjectHelper;
import hr.wozai.service.user.server.model.common.ContentIndex;
import hr.wozai.service.user.server.model.common.RecentUsedObject;
import hr.wozai.service.user.server.model.userorg.Pingyin;
import hr.wozai.service.user.server.service.NameIndexService;
import hr.wozai.service.user.server.service.SearchHistoryService;
import hr.wozai.service.user.server.util.Pinyin4jUtil;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/24
 */
@Service("searchHistoryService")
public class SearchHistoryServiceImpl implements SearchHistoryService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SearchHistoryServiceImpl.class);

  @Autowired
  RecentUsedObjectDao recentUsedObjectDao;

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long addRecentUsedObject(RecentUsedObject recentUsedObject) {
    RecentUsedObjectHelper.checkInsertParam(recentUsedObject);
    long result;

    long orgId = recentUsedObject.getOrgId();
    long userId = recentUsedObject.getUserId();
    int type = recentUsedObject.getType();
    RecentUsedObject inDb = recentUsedObjectDao.getRecentUsedObjectByUserIdAndType(orgId, userId, type);
    if (inDb == null) {
      result = recentUsedObjectDao.insertRecentUsedObject(recentUsedObject);
    } else {
      List<String> newList = getNewList(inDb.getUsedObjectId(), recentUsedObject.getUsedObjectId());
      recentUsedObject.setUsedObjectId(newList);
      recentUsedObjectDao.deleteRecentUsedObjectByUserIdAndType(orgId, userId, type);
      result = recentUsedObjectDao.insertRecentUsedObject(recentUsedObject);
    }

    return result;
  }

  /**
   * 获取不重复的list,长度为10
   *
   * @param oldOne
   * @param newOne
   * @return
   */
  private List<String> getNewList(List<String> oldOne, List<String> newOne) {
    List<String> result = new ArrayList<>();
    for (String s : newOne) {
      result.add(s);
    }
    for (String s : oldOne) {
      if (result.size() == 10) {
        break;
      }
      if (result.contains(s)) {
        continue;
      } else {
        result.add(s);
      }
    }
    return result;
  }

  @Override
  public RecentUsedObject gerRecentUsedObjectByUserIdAndType(long orgId, long userId, int type) {
    return recentUsedObjectDao.getRecentUsedObjectByUserIdAndType(orgId, userId, type);
  }
}
