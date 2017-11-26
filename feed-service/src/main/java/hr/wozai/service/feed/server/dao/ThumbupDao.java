// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.dao;

import hr.wozai.service.feed.server.constant.FeedThumbupEnum;
import hr.wozai.service.feed.server.model.Thumbup;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-16
 */
@Repository("thumbupDao")
public class ThumbupDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.feed.server.dao.ThumbupMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  @LogAround
  public int updateThumbup(long orgId, long userId, long feedId, int isLiked) {
    Map map = new HashMap<>();
    map.put("orgId", orgId);
    map.put("userId", userId);
    map.put("feedId", feedId);
    map.put("isLiked", isLiked);
    map.put("lastModifiedUserId", userId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "updateThumbup", map);
    return result;
  }

  @LogAround
  public boolean isUserIdThumbupFeedId(long orgId, long userId, long feedId) {
    Map map = new HashMap<>();
    map.put("orgId", orgId);
    map.put("userId", userId);
    map.put("feedId", feedId);
    Long result = sqlSessionTemplate.selectOne(BASE_PACKAGE + "isUserIdThumbupFeedId", map);
    return result == FeedThumbupEnum.LIKE.getCode();
  }

  @LogAround
  public List<Thumbup> listThumbupUserIdsOfFeedId(long orgId, long feedId) {
    Map map = new HashMap<>();
    map.put("feedId", feedId);
    map.put("orgId", orgId);
    List<Thumbup> result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listThumbupUserIdsOfFeedId", map);
    if (null == result) {
      result = Collections.EMPTY_LIST;
    }
    return result;
  }

  @LogAround
  public List<Long> filterUserLikedFeedIds(long orgId, long userId, List<Long> feedIds) {
    Map map = new HashMap<>();
    map.put("orgId", orgId);
    map.put("userId", userId);
    map.put("feedIds", feedIds);

    List<Long> result = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "filterUserLikedFeedIds", map);
    if (null == result) {
      result = Collections.EMPTY_LIST;
    }
    return result;
  }

}
