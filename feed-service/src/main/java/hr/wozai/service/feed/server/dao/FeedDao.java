// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.dao;

import hr.wozai.service.feed.server.model.Feed;
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
@Repository("feedDao")
public class FeedDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.feed.server.dao.FeedMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  @LogAround
  public long insertFeed(Feed feed) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertFeed", feed);
    return feed.getFeedId();
  }

  @LogAround
  public Feed findFeed(long orgId, long feedId) {
    Map map = new HashMap<>();
    map.put("orgId", orgId);
    map.put("feedId", feedId);
    Feed feed = sqlSessionTemplate.selectOne(BASE_PACKAGE + "findFeed", map);
    return feed;
  }

  @LogAround
  public Long countFeedOfOrgAndTeam(long orgId, long teamId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("teamId", teamId);
    Long amount = sqlSessionTemplate.selectOne(BASE_PACKAGE + "countFeedOfOrgAndTeam", map);
    return amount;
  }

  @LogAround
  public Long countFeedOfTeam(long orgId, long teamId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("teamId", teamId);
    Long amount = sqlSessionTemplate.selectOne(BASE_PACKAGE + "countFeedOfTeam", map);
    return amount;
  }

  @LogAround
  public List<Feed> listPageFeedOfOrgAndTeam(long orgId, long teamId, int pageNumber, int pageSize) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("teamId", teamId);
    long pageStart = (pageNumber - 1) * pageSize;
    map.put("pageStart", pageStart);
    map.put("pageSize", pageSize);

    List<Feed> feedList = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listPageFeedOfOrgAndTeam", map);
    if (null == feedList) {
        feedList = Collections.EMPTY_LIST;
    }
    return feedList;
  }

  @LogAround
  public List<Feed> listPageFeedOfTeam(long orgId, long teamId,
                                       int pageNumber, int pageSize) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("teamId", teamId);
    long pageStart = (pageNumber - 1) * pageSize;
    map.put("pageStart", pageStart);
    map.put("pageSize", pageSize);

    List<Feed> feedList = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listPageFeedOfTeam", map);
    if (null == feedList) {
        feedList = Collections.EMPTY_LIST;
    }
    return feedList;
  }

  @LogAround
  public List<Feed> listFeedByFeedIds(long orgId, List<Long> feedIds) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("feedIds", feedIds);

    List<Feed> feedList = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listFeedByFeedIds", map);
    if (null == feedList) {
      feedList = Collections.EMPTY_LIST;
    }
    return feedList;
  }

  @LogAround
  public int deleteFeed(long orgId, long feedId, long lastModifiedUserId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("feedId", feedId);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "deleteFeed", map);
    return result;
  }

  @LogAround
  public int updateFeedLikeNumber(long orgId, long feedId, long likeNumber, long lastModifiedUserId) {
    Map map = new HashMap();
    map.put("feedId", feedId);
    map.put("orgId", orgId);
    map.put("likeNumber", likeNumber);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "updateFeed", map);
    return result;
  }

  @LogAround
  public int updateFeedCommentNumber(long orgId, long feedId, long commentNumber, long lastModifiedUserId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("feedId", feedId);
    map.put("commentNumber", commentNumber);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "updateFeed", map);
    return result;
  }

}
