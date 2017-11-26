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
@Service("nameIndexService")
public class NameIndexServiceImpl implements NameIndexService {
  private static final Logger LOGGER = LoggerFactory.getLogger(NameIndexServiceImpl.class);

  @Autowired
  ContentIndexDao contentIndexDao;

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void addContentIndex(long orgId, long contentObjectId, int type, String content) {
    if (!ContentIndexType.isValidType(type)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    List<ContentIndex> contentIndexes = new ArrayList<>();
    Pingyin pingyin = Pinyin4jUtil.converterToSpell(content);
    List<String> pinyinList = pingyin.getPingyinList();
    List<String> firstSpellList = pingyin.getFirstSpellList();

    for (int i = 0; i < pinyinList.size(); i++) {
      ContentIndex obj = new ContentIndex();
      obj.setOrgId(orgId);
      obj.setType(type);
      obj.setContentObjectId(contentObjectId);
      obj.setContent(content);
      obj.setPinyin(pinyinList.get(i));
      obj.setAbbreviation(firstSpellList.get(i));
      contentIndexes.add(obj);
    }

    contentIndexDao.batchInsertContentIndex(contentIndexes);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteContentIndexByObjectIdAndType(long orgId, long contentObjectId, int type, long actorUserId) {
    contentIndexDao.deleteContentIndexsByObjectIdAndType(orgId, contentObjectId, type);
  }

  @Override
  public List<Long> listObjectIdsByContentOrPinyinOrAbbreviation(long orgId, String keyword, int type, int pageNumber, int pageSize) {
    return contentIndexDao.listContentObjectIdsByKeywordAndType(orgId, keyword, type, pageNumber, pageSize);
  }

  @Override
  public long countIdNumByKeywordAndType(long orgId, String keyword, int type) {
    return contentIndexDao.countItem(orgId, keyword, type);
  }
}
