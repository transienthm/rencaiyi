// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.feed.client.dto.*;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-17
 */
@ThriftService
public interface FeedFacade {

  // Note: 1.0 destTeamId is not used.
  /**
   * Create feed
   * @param orgId
   * @param feedDTO
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public LongDTO createFeed(long orgId, FeedDTO feedDTO, long actorUserId, long adminUserId);

  /**
   * Delete feed by feedId, orgId
   * @param orgId
   * @param feedId
   * @param userId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public VoidDTO deleteFeed(long orgId, long feedId, long userId, long actorUserId, long adminUserId);

  /**
   * Find feed by feedId
   * @param orgId
   * @param feedId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public FeedDTO findFeed(long orgId, long feedId, long actorUserId, long adminUserId);

  /**
   * Count feed of org and team
   * @param orgId
   * @param teamId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public LongDTO countFeedOfOrgAndTeam(long orgId, long teamId, long actorUserId, long adminUserId);

  /**
   * Count feed of team
   * @param orgId
   * @param teamId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public LongDTO countFeedOfTeam(long orgId, long teamId, long actorUserId, long adminUserId);

  /**
   * List page org feed of org and team
   * Note: include both public feed and team feed
   * @param orgId
   * @param teamId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public FeedListDTO listPageFeedOfOrgAndTeam(long orgId, long teamId,
                                              int pageNumber, int pageSize,
                                              long actorUserId, long adminUserId);

  // 1.0: not used
  // Note: companyAdmin, teamAdmin, staff, hr.
  /**
   * List page org feed of team
   * Note: only include team feed
   * @param orgId
   * @param teamId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public FeedListDTO listPageFeedOfTeam(long orgId, long teamId,
                                        int pageNumber, int pageSize,
                                        long actorUserId, long adminUserId);

  /**
   * List feeds by feed ids
   * @param orgId
   * @param feedIds
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public FeedListDTO listFeedByFeedIds(long orgId, List<Long> feedIds,
                                       long actorUserId, long adminUserId);

  /**
   * Is user thumbup feed
   * @param orgId
   * @param userId
   * @param feedId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public BooleanDTO isUserIdThumbupFeedId(long orgId, long userId, long feedId,
                                          long actorUserId, long adminUserId);

  /**
   * User thumbup feed
   * @param orgId
   * @param userId
   * @param feedId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public VoidDTO thumbupFeed(long orgId, long userId, long feedId,
                             long actorUserId, long adminUserId);

  /**
   * User undo thumbup feed
   * @param orgId
   * @param userId
   * @param feedId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public VoidDTO unThumbupFeed(long orgId, long userId, long feedId,
                               long actorUserId, long adminUserId);

  /**
   * List users who thumbup feedId
   * @param orgId
   * @param feedId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ThumbupListDTO listThumbupUserIdsOfFeedId(long orgId, long feedId,
                                                   long actorUserId, long adminUserId);

  /**
   * Filter user liked feed ids
   * @param orgId
   * @param userId
   * @param feedIds
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public LongListDTO filterUserLikedFeedIds(long orgId, long userId, List<Long> feedIds,
                                            long actorUserId, long adminUserId);

  /**
   * Create comment
   * @param orgId
   * @param commentDTO
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public LongDTO createComment(long orgId, CommentDTO commentDTO,
                               long actorUserId, long adminUserId);

  /**
   * Delete comment
   * @param orgId
   * @param commentId
   * @param userId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public VoidDTO deleteComment(long orgId, long commentId, long userId,
                               long actorUserId, long adminUserId);

  /**
   * Find comment
   * @param orgId
   * @param commentId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public CommentDTO findComment(long orgId, long commentId,
                                long actorUserId, long adminUserId);

  /**
   * Count comment of feed
   * @param orgId
   * @param feedId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public LongDTO countFeedComment(long orgId, long feedId,
                                  long actorUserId, long adminUserId);

  /**
   * List feed comment
   * @param orgId
   * @param feedId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public CommentListDTO listFeedComment(long orgId, long feedId,
                                        long actorUserId, long adminUserId);

  /**
   * List page feed comment
   * @param orgId
   * @param feedId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public CommentListDTO listPageFeedComment(long orgId, long feedId,
                                            int pageNumber, int pageSize,
                                            long actorUserId, long adminUserId);

}
