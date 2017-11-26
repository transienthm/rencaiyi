// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.service;

import hr.wozai.service.feed.server.model.Comment;
import hr.wozai.service.feed.server.model.Feed;
import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.feed.server.model.Thumbup;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-17
 */
public interface IFeedService {

  /**
   * Create feed
   * @param feed
   */
  public long createFeed(Feed feed);

  public long createFeedAndReward(Feed feed, List<Reward> rewards);

  /**
   * Delete feed by feedId, orgId
   * @param orgId
   * @param feedId
   * @param userId
   */
  public void deleteFeed(long orgId, long feedId, long userId);

  /**
   * Delete feed by feedId, orgId
   * @param orgId
   * @param feedId
   */
  public Feed findFeed(long orgId, long feedId);

  /**
   * Count feed of org and team
   * @param orgId
   * @param teamId
   */
  public Long countFeedOfOrgAndTeam(long orgId, long teamId);

  /**
   * Count feed of team
   * @param orgId
   * @param teamId
   */
  public Long countFeedOfTeam(long orgId, long teamId);

  /**
   * List page org feed of Org and Team
   * Note: include public feed and team feed
   * @param orgId
   * @param teamId
   * @param pageNumber
   * @param pageSize
   */
  public List<Feed> listPageFeedOfOrgAndTeam(long orgId, long teamId,
                                             int pageNumber, int pageSize);

  /**
   * List page org feed of Team
   * Note: only include team feed
   * @param orgId
   * @param teamId
   * @param pageNumber
   * @param pageSize
   */
  public List<Feed> listPageFeedOfTeam(long orgId, long teamId,
                                       int pageNumber, int pageSize);

  /**
   * List feeds by feed ids
   * @param orgId
   * @param feedIds
   */
  public List<Feed> listFeedByFeedIds(long orgId, List<Long> feedIds);


  /**
   * Is user thumbup feed
   * @param orgId
   * @param userId
   * @param feedId
   */
  public boolean isUserIdThumbupFeedId(long orgId, long userId, long feedId);

  /**
   * User thumbup feed
   * @param orgId
   * @param userId
   * @param feedId
   */
  public void thumbupFeed(long orgId, long userId, long feedId);

  /**
   * User undo thumbup feed
   * @param orgId
   * @param userId
   * @param feedId
   */
  public void unThumbupFeed(long orgId, long userId, long feedId);

  /**
   * List users who thumbup feed
   * @param orgId
   * @param feedId
   * @return
   */
  public List<Thumbup> listThumbupUserIdsOfFeedId(long orgId, long feedId);

  /**
   * Create comment
   * @param comment
   */
  public long createComment(Comment comment);

  /**
   * Delete comment
   * @param orgId
   * @param commentId
   * @param userId
   */
  public void deleteComment(long orgId, long commentId, long userId);

  /**
   * Find comment
   * @param orgId
   * @param commentId
   */
  public Comment findComment(long orgId, long commentId);

  /**
   * Count comment of feed
   * @param orgId
   * @param feedId
   */
  public Long countFeedComment(long orgId, long feedId);

  /**
   * List feed comment
   * @param orgId
   * @param feedId
   */
  public List<Comment> listFeedComment(long orgId, long feedId);

  /**
   * List page feed comment
   * @param orgId
   * @param feedId
   * @param pageNumber
   * @param pageSize
   */
  public List<Comment> listPageFeedComment(long orgId, long feedId,
                                           int pageNumber, int pageSize);


  /**
   * Filter user liked feed id list
   * @param orgId
   * @param userId
   * @param feedIds
   */
  public List<Long> filterUserLikedFeedIds(long orgId, long userId, List<Long> feedIds);

}
