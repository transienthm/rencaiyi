package hr.wozai.service.feed.server.test.utils;


import hr.wozai.service.feed.server.model.Comment;
import hr.wozai.service.feed.server.model.Feed;
import hr.wozai.service.feed.server.service.IFeedService;
import hr.wozai.service.feed.server.test.base.TestBase;
import hr.wozai.service.feed.server.utils.FeedEmailUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-05-19
 */
public class FeedEmailUtilsTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(FeedEmailUtilsTest.class);

  long orgId = 99L;
  long teamId = 0L;

  long userId = 56L;
  long commentUserId = 57L;

  long actorUserId = 0L;
  long adminUserId = 0L;

  @Autowired
  private FeedEmailUtils feedEmailUtils;

  @Autowired
  private IFeedService feedService;

  private Feed feed;

  @Before
  public void setup() throws Exception {

    String content = "Hello world!";

    List<String> atUsersStr = new ArrayList<>();
    atUsersStr.add("59");
    atUsersStr.add("60");

    List<String> images = new ArrayList<>();
    images.add("ali");
    images.add("aws");

    feed = new Feed();

    feed.setOrgId(orgId);
    feed.setUserId(userId);
    feed.setTeamId(teamId);

    feed.setContent(content);
    feed.setAtUsers(atUsersStr);
    feed.setImages(images);

    feed.setLikeNumber(0L);
    feed.setCommentNumber(0L);
    feed.setLastModifiedUserId(userId);
  }

  @Test
  public void testSendFeedAtEmail() throws Exception {

    long feedId = feedService.createFeed(feed);

    long feedUserId = feed.getUserId();
    List<String> atUsers = feed.getAtUsers();
    for(String atUser: atUsers) {
      Long atUserId = Long.parseLong(atUser);

      try {
        feedEmailUtils.sendFeedAtEmail(orgId, feedId, feedUserId, atUserId, actorUserId, adminUserId);
      } catch (Exception e) {
        LOGGER.error(e.toString());
      }
    }
  }

  @Test
  public void testSendFeedCommentEmail() throws Exception {

    long feedId = feedService.createFeed(feed);

    Comment comment = new Comment();

    comment.setOrgId(orgId);
    comment.setFeedId(feedId);
    comment.setContent("My comment");
    comment.setUserId(commentUserId);
    comment.setLastModifiedUserId(commentUserId);

    feedService.createComment(comment);

    try {
      feedEmailUtils.sendFeedCommentEmail(orgId, feedId, userId, commentUserId, actorUserId, adminUserId);
    } catch (Exception e) {
      LOGGER.error(e.toString());
    }
  }

  @Test
  public void testSendFeedCommentAtEmail() throws Exception {

    long feedId = feedService.createFeed(feed);

    Comment comment = new Comment();

    comment.setOrgId(orgId);
    comment.setFeedId(feedId);
    comment.setContent("My comment");

    List<String> atUsersStr = new ArrayList<>();
    atUsersStr.add("58");
    comment.setAtUsers(atUsersStr);

    comment.setUserId(commentUserId);
    comment.setLastModifiedUserId(commentUserId);

    feedService.createComment(comment);

    for(String atUser: atUsersStr) {
      Long atUserId = Long.parseLong(atUser);
      try {
        feedEmailUtils.sendFeedCommentAtEmail(orgId, feedId, commentUserId, atUserId, actorUserId, adminUserId);
      } catch (Exception e) {
        LOGGER.error(e.toString());
      }
    }

  }

}