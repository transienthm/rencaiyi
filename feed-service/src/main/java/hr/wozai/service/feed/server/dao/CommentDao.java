// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.dao;

import hr.wozai.service.feed.server.model.Comment;
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
@Repository("commentDao")
public class CommentDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.feed.server.dao.CommentMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  @LogAround
  public long insertComment(Comment comment) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertComment", comment);
    return comment.getCommentId();
  }

  @LogAround
  public Comment findComment(long orgId, long commentId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("commentId", commentId);
    Comment comment = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "findComment", map);
    return comment;
  }

  @LogAround
  public Long countFeedComment(long orgId, long feedId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("feedId", feedId);
    Long amount = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "countFeedComment", map);
    return amount;
  }
  @LogAround
  public List<Comment> listFeedComment(long orgId, long feedId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("feedId", feedId);
    List<Comment> commentList = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listFeedComment", map);
    if (null == commentList) {
      commentList = Collections.EMPTY_LIST;
    }
    return commentList;
  }

  @LogAround
  public List<Comment> listPageFeedComment(long orgId, long feedId,
                                           int pageNumber, int pageSize) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("feedId", feedId);
    long pageStart = (pageNumber - 1) * pageSize;
    map.put("pageStart", pageStart);
    map.put("pageSize", pageSize);

    List<Comment> commentList = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listPageFeedComment", map);
    if (null == commentList) {
      commentList = Collections.EMPTY_LIST;
    }
    return commentList;
  }

  @LogAround
  public int deleteComment(long orgId, long commentId, long lastModifiedUserId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("commentId", commentId);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int rel = sqlSessionTemplate.update(BASE_PACKAGE +
        "deleteComment", map);
    return rel;
  }

}

