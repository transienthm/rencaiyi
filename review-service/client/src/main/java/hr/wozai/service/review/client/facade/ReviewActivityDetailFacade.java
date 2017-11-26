// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.review.client.dto.ReviewActivityDetailDTO;
import hr.wozai.service.review.client.dto.ReviewInvitedUserListDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-19
 */
@ThriftService
public interface ReviewActivityDetailFacade {

  /**
   * Get review activity detail
   * @param orgId
   * @param activityId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewActivityDetailDTO getReviewActivityDetailDTO(
      long orgId, long activityId, long actorUserId, long adminUserId);


  /**
   * Get review activity invitation
   * @param orgId
   * @param activityId
   * @param managerUserId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewInvitedUserListDTO getReviewActivityInvitation(
      long orgId, long activityId, long managerUserId, long actorUserId, long adminUserId);

  /**
   * Set review activity invitation
   * @param orgId
   * @param activityId
   * @param managerUserId
   * @param invitedUserIds
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public VoidDTO setReviewActivityInvitation(
      long orgId, long activityId, long managerUserId, List<Long> invitedUserIds, long actorUserId, long adminUserId);


  /**
   * Submit review activity
   * @param orgId
   * @param activityId
   * @param managerUserId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  public VoidDTO submitReviewActivity(
      long orgId, long activityId, long actorUserId, long adminUserId);

  /**
   * Insert activity comment
   * @param orgId
   * @param activityId
   * @param questionId
   * @param content
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public LongDTO insertActivityComment(
      long orgId, long activityId, long questionId, String content,  long actorUserId, long adminUserId);

  /**
   * Update activity comment
   * @param orgId
   * @param activityId
   * @param commentId
   * @param content
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public VoidDTO updateActivityComment(
      long orgId, long activityId, long commentId, String content, long actorUserId, long adminUserId);

  /**
   * Get review activity detail by HR
   * @param orgId
   * @param activityId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewActivityDetailDTO getReviewActivityDetailDTOByHR(
      long orgId, long activityId, long actorUserId, long adminUserId);
}
