//package hr.wozai.service.user.server.test.dao.common;
package hr.wozai.service.user.server.test.dao.common;

import hr.wozai.service.user.client.userorg.enums.ContentIndexType;

import hr.wozai.service.user.server.dao.common.ContentIndexDao;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.common.ContentIndex;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/22
 */
public class ContentIndexDaoTest extends TestBase {
  @Autowired
  ContentIndexDao contentIndexDao;

  long orgId = 10L;
  long objectId = 10L;

  @Test
  public void testBatchInsertContentIndex() throws Exception {
    ContentIndex contentIndex = new ContentIndex();
    contentIndex.setOrgId(orgId);
    contentIndex.setType(ContentIndexType.USER_NAME.getCode());
    contentIndex.setContentObjectId(objectId);
    contentIndex.setContent("乐普久");
    contentIndex.setPinyin("lepujiu");
    contentIndex.setAbbreviation("lpj");

    contentIndexDao.batchInsertContentIndex(Arrays.asList(contentIndex));

    String keyword = "lepujiu";
    List<Long> ids = contentIndexDao.listContentObjectIdsByKeywordAndType(orgId, keyword,
            ContentIndexType.USER_NAME.getCode(), 1, 20);
    Assert.assertEquals(1, ids.size());

    keyword = "lep";
    ids = contentIndexDao.listContentObjectIdsByKeywordAndType(orgId, keyword,
            ContentIndexType.USER_NAME.getCode(), 1, 20);
    Assert.assertEquals(1, ids.size());

    keyword = "pujiu";
    ids = contentIndexDao.listContentObjectIdsByKeywordAndType(orgId, keyword,
            ContentIndexType.USER_NAME.getCode(), 1, 20);
    Assert.assertEquals(1, ids.size());

    keyword = "jiu";
    ids = contentIndexDao.listContentObjectIdsByKeywordAndType(orgId, keyword,
            ContentIndexType.USER_NAME.getCode(), 1, 20);
    Assert.assertEquals(1, ids.size());

    keyword = "iu";
    ids = contentIndexDao.listContentObjectIdsByKeywordAndType(orgId, keyword,
            ContentIndexType.USER_NAME.getCode(), 1, 20);
    Assert.assertEquals(0, ids.size());

    keyword = "u";
    ids = contentIndexDao.listContentObjectIdsByKeywordAndType(orgId, keyword,
            ContentIndexType.USER_NAME.getCode(), 1, 20);
    Assert.assertEquals(0, ids.size());

    keyword = "ujiu";
    ids = contentIndexDao.listContentObjectIdsByKeywordAndType(orgId, keyword,
            ContentIndexType.USER_NAME.getCode(), 1, 20);
    Assert.assertEquals(0, ids.size());

    keyword = "epujiu";
    ids = contentIndexDao.listContentObjectIdsByKeywordAndType(orgId, keyword,
            ContentIndexType.USER_NAME.getCode(), 1, 20);
    Assert.assertEquals(0, ids.size());

    keyword = "lepujiu";
    ids = contentIndexDao.listContentObjectIdsByKeywordAndType(orgId, keyword,
            ContentIndexType.TEAM_NAME.getCode(), 1, 20);
    Assert.assertEquals(0, ids.size());

    Assert.assertEquals(1L, contentIndexDao.countItem(orgId, keyword, ContentIndexType.USER_NAME.getCode()).longValue());

    contentIndexDao.deleteContentIndexsByObjectIdAndType(orgId, objectId,ContentIndexType.USER_NAME.getCode());
    Assert.assertEquals(0L, contentIndexDao.countItem(orgId, keyword, ContentIndexType.USER_NAME.getCode()).longValue());
  }
}