// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.test.dao;

import hr.wozai.service.feed.server.dao.FeedDao;
import hr.wozai.service.feed.server.test.base.TestBase;
import hr.wozai.service.feed.server.model.Feed;
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
public class FeedDaoTest extends TestBase {
  private static Logger LOGGER = LoggerFactory.getLogger(FeedDaoTest.class);

  @Autowired
  FeedDao feedDao;

  @Test
  public void testAll() {

    long orgId = 222L;
    long teamId = 55L;
    long userId = 5;

    int pageNumber = 1;
    int pageSize = 5;

    String content = "Hello world!";
    List<Long> atUsers = new ArrayList<>();
    atUsers.add(888L);
    atUsers.add(999L);

    List<String> atUsersStr = new ArrayList<>();
    atUsersStr.add("888");
    atUsersStr.add("999");

    List<String> images = new ArrayList<>();
    images.add("ali");
    images.add("aws");

    Feed feed = new Feed();

    feed.setOrgId(orgId);
    feed.setUserId(userId);
    feed.setTeamId(teamId);

    feed.setContent(content);
    //feed.setAtUsers(atUsers);
    feed.setAtUsers(atUsersStr);
    feed.setImages(images);

    feed.setLikeNumber(0L);
    feed.setCommentNumber(0L);
    feed.setLastModifiedUserId(userId);

    long currentTime = System.currentTimeMillis();
    feed.setCreatedTime(currentTime);

    Long feedId = feedDao.insertFeed(feed);
    Assert.assertEquals(feedId, feed.getFeedId());

    // verify
    LOGGER.info("feedId=" + feedId);
    Feed insertedFeed = feedDao.findFeed(orgId, feed.getFeedId());
    LOGGER.info(insertedFeed.toString());

    Assert.assertEquals(insertedFeed.getContent(), content);

    for(long i=1; i<10; i++) {
        feed.setUserId(i);
        feedDao.insertFeed(feed);
    }

    long amount = feedDao.countFeedOfOrgAndTeam(orgId, teamId);
    Assert.assertEquals(amount, 10l);

    List<Feed> feedList;

    feedList = feedDao.listPageFeedOfOrgAndTeam(orgId, teamId, pageNumber, pageSize);
    LOGGER.info("org and team feed page number " + feedList.size());

    feedList = feedDao.listPageFeedOfTeam(orgId, teamId, pageNumber, pageSize);
    LOGGER.info("team feed page number " + feedList.size());

    List<Long> feedIds = new ArrayList<>();
    for(Feed temp: feedList) {
      feedIds.add(temp.getFeedId());
    }
    feedList = feedDao.listFeedByFeedIds(orgId, feedIds);
    LOGGER.info("list feed by feed ids " + feedList.size());

    int rel;
    rel = feedDao.deleteFeed(orgId, feedId, userId);
    LOGGER.info("delete rel " + rel);
    Feed deletedComment = feedDao.findFeed(orgId, feedId);
    Assert.assertEquals(deletedComment, null);

    amount = feedDao.countFeedOfOrgAndTeam(orgId, teamId);
    Assert.assertEquals(amount, 9l);

    amount = feedDao.countFeedOfTeam(orgId, teamId);
    Assert.assertEquals(amount, 9l);

    long commentNumber = 3l;
    rel = feedDao.updateFeedCommentNumber(orgId, feedId, commentNumber, userId);
    LOGGER.info("update comment number " + rel);

    long likeNumber = 3l;
    rel = feedDao.updateFeedLikeNumber(orgId, feedId, likeNumber, userId);
    LOGGER.info("update like number " + rel);

  }
}

