// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.review.client.dto.ReviewProjectDetailDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-21
 */
@ThriftService
public interface ReviewInvitationProjectFacade {

  /**
   * Insert review invitation project comment
   * @param orgId
   * @param invitationId
   * @param projectId
   * @param managerUserId
   * @param content
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public LongDTO insertInvitationProjectComment(long orgId, long invitationId, long projectId,
                                                long managerUserId,
                                                String content,
                                                long actorUserId, long adminUserId);

  /**
   * Update review invitation project comment
   * @param orgId
   * @param invitationId
   * @param commentId
   * @param managerUserId
   * @param content
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public VoidDTO updateInvitationProjectComment(long orgId, long invitationId, long commentId,
                                                long managerUserId,
                                                String content,
                                                long actorUserId, long adminUserId);

  /**
   * Delete review invitation project comment
   * @param orgId
   * @param invitationId
   * @param commentId
   * @param managerUserId
   * @param actorUserId
   * @param adminUserId
   */

  @ThriftMethod
  public VoidDTO deleteInvitationProjectComment(long orgId, long invitationId, long commentId,
                                                long managerUserId,
                                                long actorUserId, long adminUserId);

  /**
   * Get reivew invitation project detail
   * @param orgId
   * @param invitationId
   * @param projectId
   * @param managerUserId
   * @param actorUserId
   * @param adminUserId
   */
  @ThriftMethod
  public ReviewProjectDetailDTO getInvitationProjectDetail(long orgId, long invitationId, long projectId,
                                                           long managerUserId,
                                                           long actorUserId, long adminUserId);

}
