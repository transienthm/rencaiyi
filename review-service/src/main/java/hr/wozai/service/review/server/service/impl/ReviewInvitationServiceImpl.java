// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service.impl;

import hr.wozai.service.review.server.service.ReviewInvitationService;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.server.dao.ReviewCommentDao;
import hr.wozai.service.review.server.dao.ReviewInvitationDao;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-09
 */
@Service("reviewInvitationService")
public class ReviewInvitationServiceImpl implements ReviewInvitationService {

  @Autowired
  private ReviewInvitationDao reviewInvitationDao;

  @Autowired
  private ReviewCommentDao reviewCommentDao;

  /**
   * Insert review invitation
   * @param reviewInvitation
   */
  @LogAround
  @Override
  public long insertReviewInvitation(ReviewInvitation reviewInvitation) {
    long result = reviewInvitationDao.insertReviewInvitation(reviewInvitation);
    return result;
  }

  /**
   * Batch insert review invitations
   * @param reviewInvitations
   */
  @LogAround
  @Override
  public void batchInsertReviewInvitations(List<ReviewInvitation> reviewInvitations) {
    if(null == reviewInvitations || reviewInvitations.isEmpty()) {
      return;
    }
    reviewInvitationDao.batchInsertReviewInvitations(reviewInvitations);
  }

  /**
   * Delete review invitation
   * @param orgId
   * @param invitationId
   * @param lastModifiedUserId
   */
  @LogAround
  @Override
  public void deleteReviewInvitation(long orgId, long invitationId, long lastModifiedUserId) {
    reviewInvitationDao.deleteReviewInvitation(orgId, invitationId, lastModifiedUserId);
  }

  /**
   * Find review invitation
   * @param orgId
   * @param invitationId
   */
  @LogAround
  @Override
  public ReviewInvitation findReviewInvitation(long orgId, long invitationId) {
    ReviewInvitation reviewInvitation = reviewInvitationDao.findReviewInvitation(orgId, invitationId);
    if(null == reviewInvitation) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_INVITATION_NOT_FOUND);
    }
    return reviewInvitation;
  }

  /**
   * Find manager review invitation
   * @param orgId
   * @param templateId
   * @param revieweeId
   */
  @LogAround
  @Override
  public ReviewInvitation findManagerInvitation(long orgId, long templateId, long revieweeId) {
    ReviewInvitation reviewInvitation = reviewInvitationDao.findManagerInvitation(orgId, templateId, revieweeId);
    if(null == reviewInvitation) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_INVITATION_NOT_FOUND);
    }
    return reviewInvitation;
  }

  /**
   * Find review invitation by template, revieweeId and reviewerId
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @param reviewerId
   */
  @LogAround
  @Override
  public ReviewInvitation findReviewInvitationByTemplate(long orgId, long templateId, long revieweeId, long reviewerId) {
    ReviewInvitation reviewInvitation = reviewInvitationDao.findReviewInvitationByTemplate(orgId, templateId, revieweeId, reviewerId);
    if(null == reviewInvitation) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_INVITATION_NOT_FOUND);
    }
    return reviewInvitation;
  }

  /**
   * List unsubmitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listUnSubmittedReviewInvitation(long orgId, long reviewerId) {
    List<ReviewInvitation> reviewInvitations =
        reviewInvitationDao.listUnSubmittedReviewInvitation(orgId, reviewerId);
    return reviewInvitations;
  }

  /**
   * List submitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param pageNumber
   * @param pageSize
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listSubmittedReviewInvitation(long orgId, long reviewerId,
                                                              int pageNumber, int pageSize) {
    List<ReviewInvitation> reviewInvitations =
        reviewInvitationDao.listSubmittedReviewInvitation(orgId, reviewerId, pageNumber, pageSize);
    return reviewInvitations;
  }

  /**
   * Count submitted review invitation as reviewer
   * @param orgId
   * @param reviewerId
   */
  @LogAround
  @Override
  public long countSubmittedReviewInvitation(long orgId, long reviewerId) {
    long result = reviewInvitationDao.countSubmittedReviewInvitation(orgId, reviewerId);
    return result;
  }

  /**
   * List reviewee review invitation by reviewer
   * @param orgId
   * @param revieweeId
   * @param reviewerId
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listRevieweeReviewInvitation(long orgId, long revieweeId, long reviewerId) {
    List<ReviewInvitation> revieweeReviewInvitation =
        reviewInvitationDao.listRevieweeReviewInvitation(orgId, revieweeId, reviewerId);
    return revieweeReviewInvitation;
  }

  /**
   * List canceled review invitation as reviewer
   * @param orgId
   * @param reviewerId
   * @param pageNumber
   * @param pageSize
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listCanceledReviewInvitation(long orgId, long reviewerId,
                                                             int pageNumber, int pageSize) {
    List<ReviewInvitation> reviewInvitations =
        reviewInvitationDao.listCanceledReviewInvitation(orgId, reviewerId, pageNumber, pageSize);
    return reviewInvitations;
  }

  /**
   * Count canceled review invitation as reviewer
   * @param orgId
   * @param reviewerId
   */
  @LogAround
  @Override
  public long countCanceledReviewInvitation(long orgId, long reviewerId) {
    long result = reviewInvitationDao.countCanceledReviewInvitation(orgId, reviewerId);
    return result;
  }

  /**
   * List uncanceled review invitation of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listUnCanceledReviewInvitationOfTemplate(long orgId, long templateId) {
    List<ReviewInvitation> reviewInvitations =
        reviewInvitationDao.listUnCanceledReviewInvitationOfTemplate(orgId, templateId);
    return reviewInvitations;
  }

  /**
   * List review invitation of template as reviewee
   * Show who has been invited, used for activity detail
   * @param orgId
   * @param templateId
   * @param revieweeId
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listReviewInvitationOfTemplateAsReviewee(long orgId,
                                                                         long templateId,
                                                                         long revieweeId) {
    List<ReviewInvitation> reviewInvitations =
        reviewInvitationDao.listReviewInvitationOfTemplateAsReviewee(orgId, templateId, revieweeId);
    return reviewInvitations;
  }


  //TODO: update include cancel and submit & score
  /**
   * Update review invitation
   * @param reviewInvitation
   */
  @LogAround
  @Override
  public void updateReviewInvitation(ReviewInvitation reviewInvitation) {
    reviewInvitationDao.updateReviewInvitation(reviewInvitation);
  }


  /**
   * Refuse review invitation
   * @param reviewInvitation
   */
  @LogAround
  @Override
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public void refuseReviewInvitation(ReviewInvitation reviewInvitation) {

    //No matter isSubmitted, isCanceled = 1
    reviewInvitationDao.updateReviewInvitation(reviewInvitation);

    long orgId = reviewInvitation.getOrgId();
    long revieweeId = reviewInvitation.getRevieweeId();
    long reviewerId = reviewInvitation.getReviewerId();
    long templateId = reviewInvitation.getTemplateId();
    long lastModifiedUserId = reviewInvitation.getLastModifiedUserId();

    reviewCommentDao.deleteReviewCommentByReviewer(orgId, templateId,
            revieweeId, reviewerId, lastModifiedUserId);
  }


  /**
   * Update review invitation backup status
   * @param orgId
   * @param invitationId
   * @param isBackuped
   */
  @LogAround
  @Override
  public void updateReviewInvitationBackupStatus(long orgId, long invitationId, int isBackuped) {
    reviewInvitationDao.updateReviewInvitationBackupStatus(orgId, invitationId, isBackuped);
  }

  /**
   * Count review invitation of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public long countReviewInvitationOfTemplate(long orgId, long templateId) {
    long result = reviewInvitationDao.countReviewInvitationOfTemplate(orgId, templateId);
    return result;
  }

  /**
   * Count finished review invitation of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public long countFinishedReviewInvitationOfTemplate(long orgId, long templateId) {
    long result = reviewInvitationDao.countFinishedReviewInvitationOfTemplate(orgId, templateId);
    return result;
  }

  /**
   * List all staff invitation reviewerId of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listStaffReviewerIdOfTemplate(long orgId, long templateId) {
    List<ReviewInvitation> reviewInvitations = reviewInvitationDao.listStaffReviewerIdOfTemplate(orgId, templateId);
    return reviewInvitations;
  }

  /**
   * List submitted staff invitation reviewerId of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listStaffSubmittedReviewerIdOfTemplate(long orgId, long templateId) {
    List<ReviewInvitation> reviewInvitations = reviewInvitationDao.listStaffSubmittedReviewerIdOfTemplate(orgId, templateId);
    return reviewInvitations;
  }

  /**
   * List all manager invitation reviewerId of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listManagerReviewerIdOfTemplate(long orgId, long templateId) {
    List<ReviewInvitation> reviewInvitations = reviewInvitationDao.listManagerReviewerIdOfTemplate(orgId, templateId);
    return reviewInvitations;
  }

  /**
   * List submitted manager invitation reviewerId of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listManagerSubmittedReviewerIdOfTemplate(long orgId, long templateId) {
    List<ReviewInvitation> reviewInvitations = reviewInvitationDao.listManagerSubmittedReviewerIdOfTemplate(orgId, templateId);
    return reviewInvitations;
  }

  /**
   * Count review invitation score
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public Map<Integer, Long> countReviewInvitationScore(long orgId, long templateId) {
    Map<Integer, Long> result = reviewInvitationDao.countReviewInvitationScore(orgId, templateId);
    return result;
  }

  /**
   * List all review invitation of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listAllReviewInvitationOfTemplate(long orgId, long templateId) {
    List<ReviewInvitation> reviewInvitations = reviewInvitationDao.listAllReviewInvitationOfTemplate(orgId, templateId);
    return reviewInvitations;
  }

  @Override
  @LogAround
  public List<ReviewInvitation> listAllReviewInvitationByTemplateIdAndRevieweeId(
      long orgId, long templateId, long revieweeId) {
    return reviewInvitationDao.listAllReviewInvitationByTemplateIdAndRevieweeId(orgId, templateId, revieweeId);
  }

  @Override
  @LogAround
  public List<ReviewInvitation> listAllReviewInvitationsByTemplatesAndReviewer(
          long orgId, List<Long> templatesList, long reviewerId) {
    return reviewInvitationDao.listAllReviewInvitationsByTemplatesAndReviewer(orgId, templatesList, reviewerId);
  }

  @Override
  @LogAround
  public List<ReviewInvitation> listAllReviewInvitationByTemplateIdAndRevieweeIdExceptManager(
          long orgId, long templateId, long revieweeId) {
    return reviewInvitationDao.listAllReviewInvitationByTemplateIdAndRevieweeIdExceptManager(orgId, templateId, revieweeId);
  }

  @Override
  @LogAround
  public List<ReviewInvitation> listAllReviewInvitationByTemplateIdAndReviewerIdAndIsManager(
      long orgId, long templateId, long reviewerId, int isManager) {
    List<ReviewInvitation> reviewInvitations = new ArrayList<>();
    List<ReviewInvitation> allReviewInvitations =
        reviewInvitationDao.listAllReviewInvitationByTemplateIdAndReviewerId(orgId, templateId, reviewerId);
    for (ReviewInvitation reviewInvitation: allReviewInvitations) {
      if (reviewInvitation.getIsManager() == isManager) {
        reviewInvitations.add(reviewInvitation);
      }
    }
    return reviewInvitations;
  }

  @Override
  @LogAround
  public List<ReviewInvitation> listAllReviewInvitationOfReviewer(long orgId, long reviewerId) {
    return reviewInvitationDao.listAllReviewInvitationOfReviewer(orgId, reviewerId);
  }


}
