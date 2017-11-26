// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.service.Impl;

import hr.wozai.service.feed.server.constant.FeedThumbupEnum;
import hr.wozai.service.feed.server.dao.RewardDao;
import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.feed.server.dao.FeedDao;
import hr.wozai.service.feed.server.dao.ThumbupDao;
import hr.wozai.service.feed.server.model.Comment;
import hr.wozai.service.feed.server.model.Feed;
import hr.wozai.service.feed.server.model.Thumbup;
import hr.wozai.service.feed.server.service.IFeedService;
import hr.wozai.service.feed.server.dao.CommentDao;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-17
 */
@Service("feedService")
public class FeedServiceImpl implements IFeedService {

  @Autowired
  private FeedDao feedDao;

  @Autowired
  private RewardDao rewardDao;

  @Autowired
  private CommentDao commentDao;

  @Autowired
  private ThumbupDao thumbupDao;

  @LogAround
  @Override
  public long createFeed(Feed feed) {
    long feedId = feedDao.insertFeed(feed);
    return feedId;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long createFeedAndReward(Feed feed, List<Reward> rewards) {
    if (CollectionUtils.isEmpty(rewards)) {
      return -1;
    }
    long feedId = feedDao.insertFeed(feed);
    for (Reward reward : rewards) {
      reward.setFeedId(feedId);
    }
    rewardDao.insertReward(rewards);
    return feedId;
  }

  @LogAround
  @Override
  public void deleteFeed(long orgId, long feedId, long userId) {
    feedDao.deleteFeed(orgId, feedId, userId);
  }

  @LogAround
  @Override
  public Feed findFeed(long orgId, long feedId) {
    Feed feed = feedDao.findFeed(orgId, feedId);
    if(null == feed) {
      throw new ServiceStatusException(ServiceStatus.FD_FEED_NOT_FOUND);
    }
    return feed;
  }

  @LogAround
  @Override
  public Long countFeedOfOrgAndTeam(long orgId, long teamId) {
    Long result = feedDao.countFeedOfOrgAndTeam(orgId, teamId);
    return result;
  }

  @LogAround
  @Override
  public Long countFeedOfTeam(long orgId, long teamId) {
    Long result = feedDao.countFeedOfTeam(orgId, teamId);
    return result;
  }

  @LogAround
  @Override
  public List<Feed> listPageFeedOfOrgAndTeam(long orgId, long teamId,
                                             int pageNumber, int pageSize) {
    List<Feed> result = feedDao.listPageFeedOfOrgAndTeam(orgId, teamId, pageNumber, pageSize);
    return result;
  }

  //1.0 not used
  @LogAround
  @Override
  public List<Feed> listPageFeedOfTeam(long orgId, long teamId,
                                       int pageNumber, int pageSize) {
    List<Feed> result = feedDao.listPageFeedOfTeam(orgId, teamId, pageNumber, pageSize);
    return result;
  }

  @LogAround
  @Override
  public List<Feed> listFeedByFeedIds(long orgId, List<Long> feedIds) {
    if(null == feedIds || feedIds.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    List<Feed> result = feedDao.listFeedByFeedIds(orgId, feedIds);
    return result;
  }

  @LogAround
  @Override
  public boolean isUserIdThumbupFeedId(long orgId, long userId, long feedId) {
    boolean result = thumbupDao.isUserIdThumbupFeedId(orgId, userId, feedId);
    return result;
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void thumbupFeed(long orgId, long userId, long feedId) {
    int result;
    Feed feed = feedDao.findFeed(orgId, feedId);
    if(null == feed) {
      throw new ServiceStatusException(ServiceStatus.FD_FEED_NOT_FOUND);
    }

    long likeNumber = feed.getLikeNumber().longValue() + 1;
    feedDao.updateFeedLikeNumber(orgId, feedId, likeNumber, userId);

    int isLiked = FeedThumbupEnum.LIKE.getCode();
    thumbupDao.updateThumbup(orgId, userId, feedId, isLiked);
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void unThumbupFeed(long orgId, long userId, long feedId) {
    int result;

    Feed feed = feedDao.findFeed(orgId, feedId);
    if(null == feed) {
      throw new ServiceStatusException(ServiceStatus.FD_FEED_NOT_FOUND);
    }

    long likeNumber = feed.getLikeNumber().longValue() - 1;
    feedDao.updateFeedLikeNumber(orgId, feedId, likeNumber, userId);

    int isLiked = FeedThumbupEnum.UNLIKE.getCode();
    thumbupDao.updateThumbup(orgId, userId, feedId, isLiked);
  }

  @LogAround
  @Override
  public List<Thumbup> listThumbupUserIdsOfFeedId(long orgId, long feedId) {
    List<Thumbup> result = thumbupDao.listThumbupUserIdsOfFeedId(orgId, feedId);
    return result;
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public long createComment(Comment comment) {

    long userId = comment.getUserId();
    long feedId = comment.getFeedId();
    long orgId = comment.getOrgId();

    Feed feed = feedDao.findFeed(orgId, feedId);
    if(null == feed) {
      throw new ServiceStatusException(ServiceStatus.FD_FEED_NOT_FOUND);
    }

    long commentNumber = feed.getCommentNumber().longValue() + 1;
    feedDao.updateFeedCommentNumber(orgId, feedId, commentNumber, userId);

    long commentId = commentDao.insertComment(comment);
    return commentId;
  }

  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void deleteComment(long orgId, long commentId, long userId) {

    int result;

    Comment comment = commentDao.findComment(orgId, commentId);
    if(null == comment) {
      throw new ServiceStatusException(ServiceStatus.FD_COMMENT_NOT_FOUND);
    }
    long feedId = comment.getFeedId();
    Feed feed = feedDao.findFeed(orgId, feedId);
    if(null == feed) {
      throw new ServiceStatusException(ServiceStatus.FD_FEED_NOT_FOUND);
    }

    long commentNumber = feed.getCommentNumber().longValue() - 1;
    feedDao.updateFeedCommentNumber(orgId, feedId, commentNumber, userId);

    commentDao.deleteComment(orgId, commentId, userId);
  }

  @LogAround
  @Override
  public Comment findComment(long orgId, long commentId) {
    Comment comment = commentDao.findComment(orgId, commentId);
    return comment;
  }

  @LogAround
  @Override
  public Long countFeedComment(long orgId, long feedId) {
    Long result = commentDao.countFeedComment(orgId, feedId);
    return result;
  }

  @LogAround
  @Override
  public List<Comment> listFeedComment(long orgId, long feedId) {
    List<Comment> result = commentDao.listFeedComment(orgId, feedId);
    return result;
  }

  @LogAround
  @Override
  public List<Comment> listPageFeedComment(long orgId, long feedId,
                                           int pageNumber, int pageSize) {
    List<Comment> result = commentDao.listPageFeedComment(orgId, feedId, pageNumber, pageSize);
    return result;
  }

  @LogAround
  @Override
  public List<Long> filterUserLikedFeedIds(long orgId, long userId, List<Long> feedIds) {
    if(null == feedIds || feedIds.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    return thumbupDao.filterUserLikedFeedIds(orgId, userId, feedIds);
  }
}
