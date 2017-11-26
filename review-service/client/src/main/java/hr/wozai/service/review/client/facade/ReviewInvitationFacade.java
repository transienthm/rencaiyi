// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.review.client.dto.ReviewInvitationDTO;
import hr.wozai.service.review.client.dto.ReviewInvitationListDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-21
 */
@ThriftService
public interface ReviewInvitationFacade {

  /**
   * Find review invitation
   * @param orgId
   * @param invitationId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewInvitationDTO findReviewInvitation(long orgId, long invitationId,
                                                  long actorUserId, long adminUserId);
  /**
   * List unsubmitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewInvitationListDTO listUnSubmittedReviewInvitation(long orgId, long reviewerId, long actorUserId, long adminUserId);

  /**
   * List submitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewInvitationListDTO listSubmittedReviewInvitation(long orgId, long reviewerId,
                                                               int pageNumber, int pageSize, long actorUserId, long adminUserId);

  /**
   * Count submitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public LongDTO countSubmittedReviewInvitation(long orgId, long reviewerId, long actorUserId, long adminUserId);

  /**
   * List canceled review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param pageNumber
   * @param pageSize
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewInvitationListDTO listCanceledReviewInvitation(long orgId, long reviewerId,
                                                              int pageNumber, int pageSize, long actorUserId, long adminUserId);

  /**
   * Count canceled review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public LongDTO countCanceledReviewInvitation(long orgId, long reviewerId, long actorUserId, long adminUserId);

  /**
   * Refuse review invitation
   * @param orgId
   * @param invitationId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public VoidDTO refuseReviewInvitation(long orgId, long invitationId,
                                        long actorUserId, long adminUserId);


  @ThriftMethod
  public ReviewInvitationListDTO listAllReviewInvitationByTemplateIdAndRevieweeId(
      long orgId, long templateId, long revieweeId, long actorUserId, long adminUserId);

  @ThriftMethod
  public ReviewInvitationListDTO listAllReviewInvitationsByTemplatesAndReviewer(
          long orgId, List<Long> templatesList, long reviewerId, long actorUserId, long adminUserId);

  @ThriftMethod
  public ReviewInvitationListDTO listAllReviewInvitationByTemplateIdAndReviewerIdAndIsManager(
      long orgId, long templateId, long reviewerId, int isManager, long actorUserId, long adminUserId);

}
