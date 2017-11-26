// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service;

import hr.wozai.service.review.server.model.ReviewInvitation;

import java.util.List;
import java.util.Map;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-09
 */
public interface ReviewInvitationService {

  /**
   * Insert review invitation
   * @param reviewInvitation
   */
  public long insertReviewInvitation(ReviewInvitation reviewInvitation);

  /**
   * Batch insert review invitations
   * @param reviewInvitations
   */
  public void batchInsertReviewInvitations(List<ReviewInvitation> reviewInvitations);

  /**
   * Delete review invitation by reviewee
   * @param orgId
   * @param invitationId
   * @param lastModifiedUserId
   */
  public void deleteReviewInvitation(long orgId, long invitationId, long lastModifiedUserId);

  /**
   * Find review invitation
   * @param orgId
   * @param invitationId
   */
  public ReviewInvitation findReviewInvitation(long orgId, long invitationId);

  /**
   * Find manager review invitation
   * @param orgId
   * @param templateId
   * @param revieweeId
   */
  public ReviewInvitation findManagerInvitation(long orgId, long templateId, long revieweeId);

  /**
   * Find review invitation by template, revieweeId and reviewerId
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @param reviewerId
   */
  public ReviewInvitation findReviewInvitationByTemplate(long orgId, long templateId, long revieweeId, long reviewerId);

  /**
   * List unsubmitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   */
  public List<ReviewInvitation> listUnSubmittedReviewInvitation(long orgId, long reviewerId);

  /**
   * List submitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param pageNumber
   * @param pageSize
   */
  public List<ReviewInvitation> listSubmittedReviewInvitation(long orgId, long reviewerId,
                                                              int pageNumber, int pageSize);

  /**
   * Count submitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   */
  public long countSubmittedReviewInvitation(long orgId, long reviewerId);

  /**
   * List reviewee review invitation by reviewer
   * @param orgId
   * @param revieweeId
   * @param reviewerId
   */
  public List<ReviewInvitation> listRevieweeReviewInvitation(long orgId, long revieweeId, long reviewerId);

  /**
   * List canceled review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param pageNumber
   * @param pageSize
   */
  public List<ReviewInvitation> listCanceledReviewInvitation(long orgId, long reviewerId,
                                                             int pageNumber, int pageSize);

  /**
   * Count canceled review invitation as reviewer
   * @param orgId
   * @param reviewerId
   */
  public long countCanceledReviewInvitation(long orgId, long reviewerId);

  /**
   * List uncanceled review invitation of template
   * @param orgId
   * @param templateId
   */
  public List<ReviewInvitation> listUnCanceledReviewInvitationOfTemplate(long orgId, long templateId);

  /**
   * List review invitation of template as reviewee
   * Show who has been invited, used for activity detail
   * @param orgId
   * @param templateId
   * @param revieweeId
   */
  public List<ReviewInvitation> listReviewInvitationOfTemplateAsReviewee(long orgId,
                                                                         long templateId,
                                                                         long revieweeId);

  /**
   * Update review invitation
   * @param reviewInvitation
   */
  public void updateReviewInvitation(ReviewInvitation reviewInvitation);

  /**
   * Refuse review invitation
   * @param reviewInvitation
   */
  public void refuseReviewInvitation(ReviewInvitation reviewInvitation);

  /**
   * Update review invitation backup status
   * @param orgId
   * @param invitationId
   * @param isBackuped
   */
  public void updateReviewInvitationBackupStatus(long orgId, long invitationId, int isBackuped);

  /**
   * Count review invitation of template
   * @param orgId
   * @param templateId
   */
  public long countReviewInvitationOfTemplate(long orgId, long templateId);

  /**
   * Count finished review invitation of template
   * @param orgId
   * @param templateId
   */
  public long countFinishedReviewInvitationOfTemplate(long orgId, long templateId);

  /**
   * List all staff invitation reviewerId of template
   * @param orgId
   * @param templateId
   */
  public List<ReviewInvitation> listStaffReviewerIdOfTemplate(long orgId, long templateId);

  /**
   * List submitted staff invitation reviewerId of template
   * @param orgId
   * @param templateId
   */
  public List<ReviewInvitation> listStaffSubmittedReviewerIdOfTemplate(long orgId, long templateId);

  /**
   * List all manager invitation reviewerId of template
   * @param orgId
   * @param templateId
   */
  public List<ReviewInvitation> listManagerReviewerIdOfTemplate(long orgId, long templateId);

  /**
   * List submitted manager invitation reviewerId of template
   * @param orgId
   * @param templateId
   */
  public List<ReviewInvitation> listManagerSubmittedReviewerIdOfTemplate(long orgId, long templateId);

  /**
   * Count review invitation score
   * @param orgId
   * @param templateId
   */
  public Map<Integer, Long> countReviewInvitationScore(long orgId, long templateId);

  /**
   * List all review invitation of template
   * @param orgId
   * @param templateId
   */
  public List<ReviewInvitation> listAllReviewInvitationOfTemplate(long orgId, long templateId);

  List<ReviewInvitation> listAllReviewInvitationByTemplateIdAndRevieweeId(
      long orgId, long templateId, long revieweeId);

  List<ReviewInvitation> listAllReviewInvitationByTemplateIdAndRevieweeIdExceptManager(
          long orgId, long templateId, long revieweeId);

  List<ReviewInvitation> listAllReviewInvitationByTemplateIdAndReviewerIdAndIsManager(
      long orgId, long templateId, long reviewerId, int isManager);

  List<ReviewInvitation> listAllReviewInvitationOfReviewer(long orgId, long reviewerId);

  public List<ReviewInvitation> listAllReviewInvitationsByTemplatesAndReviewer(
          long orgId, List<Long> templatesList, long reviewerId);
}
