// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.test.service;

import hr.wozai.service.feed.server.model.Thumbup;
import hr.wozai.service.feed.server.test.base.TestBase;
import hr.wozai.service.feed.server.model.Comment;
import hr.wozai.service.feed.server.model.Feed;
import hr.wozai.service.feed.server.service.IFeedService;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-18
 */
public class TestFeedService extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(TestFeedService.class);

  @Autowired
  private IFeedService feedService;

  private long orgId = 100;

  private long userId = 66;
  private long userIdNew = 67;

  private long ALL_COMPANY = 0;
  private long teamId = 24;
  private long teamIdNew = 25;

  private int pageNumber = 1;
  private int pageSize = 5;

  private String feedContent = "My first feed";

  private String commentContent = "My first comment";
  private String commentContent2 = "My second comment";

  private List<String> atList = null;
  private List<String> atListNew = new ArrayList<>();

  private List<String> imageList = null;

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testAll() {

    Feed feed = new Feed();
    feed.setOrgId(orgId);
    feed.setUserId(userId);
    feed.setTeamId(teamId);
    feed.setContent(feedContent);
    feed.setLastModifiedUserId(userId);

    long feedId = feedService.createFeed(feed);
    long amount;

    feed = feedService.findFeed(orgId, feedId);
    Assert.assertNotEquals(feed, null);

    List<Feed> feedList = feedService.listPageFeedOfOrgAndTeam(orgId, teamId, pageNumber, pageSize);
    Assert.assertNotEquals(feedList, null);

    amount = feedService.countFeedOfOrgAndTeam(orgId, teamId);
    Assert.assertEquals(amount, 1);

    List<Feed> feedPageList =feedService.listPageFeedOfTeam(orgId, teamId, pageNumber, pageSize);
    Assert.assertNotEquals(feedPageList, null);

    amount = feedService.countFeedOfTeam(orgId, teamId);
    Assert.assertEquals(amount, 1);

    feedService.thumbupFeed(orgId, userId, feedId);
    feed = feedService.findFeed(orgId, feedId);
    Assert.assertEquals(feed.getLikeNumber().longValue(), 1);

    List<Thumbup> thumbups = feedService.listThumbupUserIdsOfFeedId(orgId, feedId);
    Assert.assertEquals(thumbups.size(), 1);
    Assert.assertEquals(thumbups.get(0).getUserId().longValue(), userId);

    List<Long> feedIds = feedPageList.stream().map(Feed::getFeedId).collect(Collectors.toList());
    List<Long> likedFeedIds = feedService.filterUserLikedFeedIds(orgId, userId, feedIds);
    Assert.assertEquals(likedFeedIds.size(), 1);

    feedService.unThumbupFeed(orgId, userId, feedId);
    feed = feedService.findFeed(orgId, feedId);
    Assert.assertEquals(feed.getLikeNumber().longValue(), 0);

    Comment comment = new Comment();
    comment.setFeedId(feedId);
    comment.setOrgId(orgId);
    comment.setUserId(userId);
    comment.setContent(commentContent);
    comment.setLastModifiedUserId(userId);

    long commentId = feedService.createComment(comment);
    List<Comment> commentList = feedService.listFeedComment(orgId, feedId);
    Assert.assertNotEquals(commentList, null);

    comment = feedService.findComment(orgId, commentId);
    Assert.assertEquals(comment.getContent(), commentContent);

    feed = feedService.findFeed(orgId, feedId);
    Assert.assertEquals(feed.getCommentNumber().longValue(), commentList.size());

    List<Comment> commentPageList = feedService.listPageFeedComment(orgId, feedId, pageNumber, pageSize);
    Assert.assertNotEquals(commentPageList, null);

    feedService.deleteComment(orgId, commentId, userId);
    feed = feedService.findFeed(orgId, feedId);
    Assert.assertEquals(feed.getCommentNumber().longValue(), 0);

    List<Feed> feeds = feedService.listFeedByFeedIds(orgId, Arrays.asList(feedId));
    Assert.assertEquals(1, feeds.size());

    feedService.deleteFeed(orgId, feedId, userId);

    feeds = feedService.listFeedByFeedIds(orgId, null);
    Assert.assertTrue(CollectionUtils.isEmpty(feeds));

    boolean result = feedService.isUserIdThumbupFeedId(orgId, userId, feedId);
    Assert.assertFalse(result);

    thrown.expect(ServiceStatusException.class);
    feedService.findFeed(orgId, -1);


  }

  @Test
  public void testListPageFeed() {

    Feed feed = new Feed();
    feed.setOrgId(orgId);
    feed.setContent(feedContent);
    feed.setLastModifiedUserId(userId);

    feed.setUserId(userId);
    feed.setTeamId(ALL_COMPANY);
    feedService.createFeed(feed);

    feed.setUserId(userId);
    feed.setTeamId(teamId);
    feedService.createFeed(feed);

    feed.setUserId(userIdNew);
    feed.setTeamId(ALL_COMPANY);
    feedService.createFeed(feed);

    feed.setUserId(userIdNew);
    feed.setTeamId(teamIdNew);
    feedService.createFeed(feed);

    feed.setUserId(userId);
    feed.setTeamId(ALL_COMPANY);
    feedService.createFeed(feed);

    long amount;
    List<Feed> teamFeedPageList = feedService.listPageFeedOfOrgAndTeam(orgId, teamId, pageNumber, pageSize);
    Assert.assertEquals(teamFeedPageList.size(), 4);

    List<Feed> teamFeedPageListNew = feedService.listPageFeedOfTeam(orgId, teamIdNew, pageNumber, pageSize);
    Assert.assertEquals(teamFeedPageListNew.size(), 1);

    amount = feedService.countFeedOfOrgAndTeam(orgId, teamId);
    Assert.assertEquals(amount, 4);

    amount = feedService.countFeedOfTeam(orgId, teamId);
    Assert.assertEquals(amount, 1);

  }


  @Test
  public void testListComment() {

    Feed feed = new Feed();
    feed.setOrgId(orgId);
    feed.setUserId(userId);
    feed.setTeamId(teamId);
    feed.setContent(feedContent);
    feed.setLastModifiedUserId(userId);

    long feedId = feedService.createFeed(feed);

    Comment comment = new Comment();
    comment.setFeedId(feedId);
    comment.setOrgId(orgId);
    comment.setUserId(userId);
    comment.setContent(commentContent);
    comment.setLastModifiedUserId(userId);
    long commentId = feedService.createComment(comment);
    comment.setContent(commentContent2);
    feedService.createComment(comment);

    long amount = feedService.countFeedComment(orgId, feedId);
    Assert.assertEquals(amount, 2);

    List<Comment> commentList = feedService.listFeedComment(orgId, feedId);
    Assert.assertEquals(commentList.size(), 2);

    List<Comment> commentPageList = feedService.listPageFeedComment(orgId, feedId, pageNumber, pageSize);
    Assert.assertEquals(commentPageList.size(), 2);

    feedService.deleteComment(orgId, commentId, userId);

    List<Comment> commentListDeleted = feedService.listFeedComment(orgId, feedId);
    Assert.assertEquals(commentListDeleted.size(), 1);
  }

}
