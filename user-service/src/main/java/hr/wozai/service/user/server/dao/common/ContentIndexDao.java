package hr.wozai.service.user.server.dao.common;

import hr.wozai.service.user.server.model.common.ContentIndex;
import hr.wozai.service.user.server.util.Pinyin4jUtil;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/21
 */
@Repository("contentIndexDao")
public class ContentIndexDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.common.ContentIndexMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public void batchInsertContentIndex(List<ContentIndex> contentIndexs) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertContentIndexs", contentIndexs);
  }

  public long deleteContentIndexsByObjectIdAndType(long orgId, long contentObjectId, int type) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("contentObjectId", contentObjectId);
    params.put("type", type);
    return sqlSessionTemplate.update(BASE_PACKAGE + "deleteContentIndexsByObjectIdAndType", params);
  }

  public List<Long> listContentObjectIdsByKeywordAndType(long orgId, String keyword, int type, int pageNumber, int pageSize) {
    String pingyinKeyword = "%" + keyword + "%";
    String firstPingyin;
    if (keyword.isEmpty()) {
      firstPingyin = keyword;
    } else {
      firstPingyin = "%" + keyword.substring(0, 1) + "%";
    }
    keyword = "%" + keyword + "%";
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("pingyinKeyword", pingyinKeyword);
    params.put("firstPingyin", firstPingyin);
    params.put("keyword", keyword);
    params.put("type", type);
    int pageStart = (pageNumber - 1) * pageSize;
    params.put("pageStart", pageStart);
    params.put("pageSize", pageSize);

    List<Long> result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listContentObjectIdsByKeywordAndType", params);
    return result;
  }

  public Long countItem(long orgId, String keyword, int type) {
    keyword = "%" + keyword + "%";
    Map<String, Object> params = new HashMap<>();
    params.put("keyword", keyword);
    params.put("orgId", orgId);
    params.put("type", type);

    Long result = sqlSessionTemplate.selectOne(BASE_PACKAGE + "countItem", params);
    return result;
  }
}
