// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.test.dao;

import hr.wozai.service.feed.server.model.Comment;
import hr.wozai.service.feed.server.test.base.TestBase;

import hr.wozai.service.feed.server.dao.CommentDao;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-16
 */
public class CommentDaoTest extends TestBase {
  private static Logger LOGGER = LoggerFactory.getLogger(FeedDaoTest.class);

  @Autowired
  CommentDao commentDao;

  @Test
  public void testAll() {

    String content = "好";
    List<Long> atUsers = new ArrayList<>();
    atUsers.add(888L);
    atUsers.add(999L);

    List<String> atUsersStr = new ArrayList<>();
    atUsersStr.add("888");
    atUsersStr.add("999");

    long orgId = 100L;
    long feedId = 122L;
    long userId = 33L;

    Comment comment = new Comment();
    comment.setOrgId(orgId);
    comment.setFeedId(feedId);
    comment.setUserId(userId);
    comment.setContent(content);
    comment.setAtUsers(atUsersStr);
    comment.setLastModifiedUserId(userId);


    long currentTime = System.currentTimeMillis();
    comment.setCreatedTime(currentTime);

    Long commentId = commentDao.insertComment(comment);
    Assert.assertEquals(commentId, comment.getCommentId());

    // verify
    LOGGER.info("commentId=" + commentId);
    Comment insertedComment = commentDao.findComment(orgId, comment.getCommentId());
    Assert.assertEquals(insertedComment.getContent(), content);

    for(long i=1; i<10; i++) {
      comment.setUserId(i);
      commentDao.insertComment(comment);
    }

    long amount = commentDao.countFeedComment(orgId, feedId);
    Assert.assertEquals(amount, 10L);

    List<Comment> commentList;
    commentList = commentDao.listFeedComment(orgId, feedId);
    LOGGER.info("comment number " + commentList.size());

    commentList = commentDao.listFeedComment(orgId+1, feedId);
    LOGGER.info("comment number " + commentList.size());

    commentList = commentDao.listPageFeedComment(orgId, feedId, 1, 5);
    LOGGER.info("comment page number " + commentList.size());

    commentList = commentDao.listPageFeedComment(orgId+1, feedId, 1, 5);
    LOGGER.info("comment page number " + commentList.size());


    int rel = commentDao.deleteComment(orgId, commentId, userId);
    LOGGER.info("delete rel " + rel);
    Comment deletedComment = commentDao.findComment(orgId, commentId);
    Assert.assertEquals(deletedComment, null);

    amount = commentDao.countFeedComment(orgId, feedId);
    Assert.assertEquals(amount, 9L);
  }

}
