package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.user.client.userorg.enums.ContentIndexType;
import hr.wozai.service.user.client.userorg.enums.RecentUsedObjectType;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.common.RecentUsedObject;
import hr.wozai.service.user.server.service.NameIndexService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/24
 */
public class NameIndexServiceImplTest extends TestBase {
  @Autowired
  NameIndexService nameIndexService;

  private long orgId = 199L;
  private long teamId = 199L;


  @Test
  public void testInsertContentIndex() throws Exception {
    String content = "乐普久";
    Integer type = ContentIndexType.TEAM_NAME.getCode();
    nameIndexService.addContentIndex(orgId, teamId, type, content);

    String keyword = "lepujiu";
    List<Long> ids = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
            type, 1, 20);
    Assert.assertEquals(1, ids.size());
    Assert.assertEquals(teamId, ids.get(0).longValue());

    Assert.assertEquals(1, nameIndexService.countIdNumByKeywordAndType(orgId, keyword, type));

    keyword = "pujiu";
    ids = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
            type, 1, 20);
    Assert.assertEquals(1, ids.size());

    keyword = "jiu";
    ids = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
            type, 1, 20);
    Assert.assertEquals(1, ids.size());

    keyword = "lpj";
    ids = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
            type, 1, 20);
    Assert.assertEquals(1, ids.size());

    keyword = "乐普久";
    ids = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
            type, 1, 20);
    Assert.assertEquals(1, ids.size());

    keyword = "epujiu";
    ids = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
            type, 1, 20);
    Assert.assertEquals(0, ids.size());

    keyword = "ujiu";
    ids = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
            type, 1, 20);
    Assert.assertEquals(0, ids.size());

    keyword = "iu";
    ids = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, keyword,
            type, 1, 20);
    Assert.assertEquals(0, ids.size());

    nameIndexService.deleteContentIndexByObjectIdAndType(orgId, teamId, type, -1);
    ids = nameIndexService.listObjectIdsByContentOrPinyinOrAbbreviation(orgId, "lepujiiu",
            type, 1, 20);
    Assert.assertEquals(0, ids.size());
  }
}