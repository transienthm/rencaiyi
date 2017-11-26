// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.review.client.dto.ReviewInvitationDetailDTO;
import hr.wozai.service.review.client.dto.ReviewInvitedUserListDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-21
 */
@ThriftService
public interface ReviewInvitationDetailFacade {

  /**
   * Get review invitation detail
   * @param orgId
   * @param invitationId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  ReviewInvitationDetailDTO getReviewInvitationDetail(
      long orgId, long invitationId, long actorUserId, long adminUserId);

  /**
   * Get review invitation invited user
   * @param orgId
   * @param invitationId
   * @param managerUserId
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  ReviewInvitedUserListDTO getReviewActivityInvitation(
      long orgId, long invitationId, long managerUserId, long actorUserId, long adminUserId);

  /**
   * Submit review invitation
   * @param orgId
   * @param invitationId
   * @param managerUserId
   * @param score
   * @param actorUserId
   * @param amdinUserId
   */
  @ThriftMethod
  VoidDTO submitPeerReviewInvitation(
      long orgId, long invitationId, long managerUserId, int score, long actorUserId, long amdinUserId);
  
  @ThriftMethod
  VoidDTO cancelSubmissionOfPeerReviewInvitation(
      long orgId, long invitationId, long actorUserId, long amdinUserId);

  /**
   * Submit review invitation
   * @param orgId
   * @param invitationId
   * @param managerUserId
   * @param score
   * @param actorUserId
   * @param amdinUserId
   */
  @ThriftMethod
  VoidDTO submitManagerReviewInvitation(
      long orgId, long invitationId, long managerUserId, int score, long actorUserId, long amdinUserId);

  @ThriftMethod
  VoidDTO cancelSubmissionOfManagerReviewInvitation(
      long orgId, long invitationId, long actorUserId, long amdinUserId);

  /**
   * Insert review invitation question comment
   * @param orgId
   * @param invitationId
   * @param questionId
   * @param managerUserId
   * @param content
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  LongDTO insertInvitationComment(
      long orgId, long invitationId, long questionId, long managerUserId,
      String content, long actorUserId, long adminUserId);

  /**
   * Update review invitation question comment
   * @param orgId
   * @param invitationId
   * @param commentId
   * @param managerUserId
   * @param content
   * @param actorUserId
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  VoidDTO updateInvitationComment(
      long orgId, long invitationId, long commentId, long managerUserId,
      String content, long actorUserId, long adminUserId);


}
