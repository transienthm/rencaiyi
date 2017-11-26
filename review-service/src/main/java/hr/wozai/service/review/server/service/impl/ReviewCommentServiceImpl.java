// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.server.model.ReviewComment;
import hr.wozai.service.review.server.dao.ReviewCommentDao;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.review.server.service.ReviewCommentService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-08
 */
@Service("reviewCommentService")
public class ReviewCommentServiceImpl implements ReviewCommentService {

  @Autowired
  private ReviewCommentDao reviewCommentDao;

  /**
   * Insert comment
   * @param reviewComment
   */
  @LogAround
  @Override
  public long insertReviewComment(ReviewComment reviewComment) {
    long result = reviewCommentDao.insertReviewComment(reviewComment);
    return result;
  }

  /**
   * Find comment
   * @param orgId
   * @param commentId
   */
  @LogAround
  @Override
  public ReviewComment findReviewComment(long orgId, long commentId) {
    ReviewComment result = reviewCommentDao.findReviewComment(orgId, commentId);
    if(null == result) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_COMMENT_NOT_FOUND);
    }
    return result;
  }

  /**
   * Update comment
   * @param reviewComment
   */
  @LogAround
  @Override
  public void updateReviewComment(ReviewComment reviewComment) {
    reviewCommentDao.updateReviewComment(reviewComment);
  }

  /**
   * Delete comment
   * @param orgId
   * @param commentId
   * @param lastModifiedUserId
   */
  @LogAround
  @Override
  public void deleteReviewComment(long orgId, long commentId, long lastModifiedUserId) {
    reviewCommentDao.deleteReviewComment(orgId, commentId, lastModifiedUserId);
  }

  /**
   * Delete comment by reviewer
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @param reviewerId
   * @param lastModifiedUserId
   */
  @LogAround
  @Override
  public void deleteReviewCommentByReviewer(long orgId, long templateId,
                                            long revieweeId, long reviewerId,
                                            long lastModifiedUserId) {
    reviewCommentDao.deleteReviewCommentByReviewer(orgId, templateId, revieweeId, reviewerId, lastModifiedUserId);
  }


  /**
   * List comment of itemType, itemId belongs to reviewerIds
   * @param orgId
   * @param templateId
   * @param itemType
   * @param itemId
   * @param revieweeId
   * @param reviewerIds
   */
  @LogAround
  @Override
  public List<ReviewComment> listReviewItemCommentOfReviewers(long orgId, long templateId,
                                                              int itemType, long itemId,
                                                              long revieweeId, List<Long> reviewerIds) {
    if(null == reviewerIds || reviewerIds.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    List<ReviewComment> result = reviewCommentDao.listReviewItemCommentOfReviewers(orgId, templateId,
        itemType, itemId, revieweeId, reviewerIds);
    return result;
  }

  /**
   * List all comment belongs to reviewerId
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @param reviewerId
   */
  @LogAround
  @Override
  public List<ReviewComment> listReviewAllCommentByReviewer(long orgId, long templateId,
                                                            long revieweeId, long reviewerId) {
    List<ReviewComment> result;
    result = reviewCommentDao.listReviewAllCommentByReviewer(orgId, templateId,
        revieweeId, reviewerId);
    return result;
  }

  /**
   * Count comment of all question belongs to reviewerIds
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @param reviewerId
   */
  @LogAround
  @Override
  public long countReviewQuestionByReviewer(long orgId, long templateId,
                                            long revieweeId, long reviewerId) {
    long result = reviewCommentDao.countReviewQuestionByReviewer(orgId, templateId, revieweeId, reviewerId);
    return result;
  }

  /**
   * Exist comment of reviewerId about tempateId, revieweeId, itemType, itemId,
   * @param orgId
   * @param templateId
   * @param itemType
   * @param itemId
   * @param revieweeId
   * @param reviewerId
   */
  @LogAround
  @Override
  public boolean existReviewItemCommentByReviewer(long orgId, long templateId,
                                                  int itemType, long itemId,
                                                  long revieweeId, long reviewerId) {
    long result = reviewCommentDao.countReviewItemCommentByReviewer(orgId, templateId,
        itemType, itemId, revieweeId, reviewerId);
    if(1 == result) {
      return true;
    }
    else if(0 == result) {
      return false;
    }
    else {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR.getCode());
    }
  }

  /**
   * Count review comment by reviewer
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @param reviewerId
   * @return
   */
  @LogAround
  @Override
  public long countReviewAllCommentByReviewer(long orgId, long templateId,
                                              long revieweeId, long reviewerId) {

    long result = reviewCommentDao.countReviewAllCommentByReviewer(orgId, templateId,
            revieweeId, reviewerId);
    return result;
  }

  /**
   * List distinct activity by template in reviewerIds
   * @param orgId
   * @param templateId
   * @param reviewerIds
   */
  @LogAround
  @Override
  public List<Long> listActivityOfTemplate(long orgId, long templateId, List<Long> reviewerIds) {
    if(null == reviewerIds || reviewerIds.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    List<Long> result = reviewCommentDao.listActivityOfTemplate(orgId, templateId, reviewerIds);
    return result;
  }

  /**
   * List distinct invitation by template in reviewerIds
   * @param orgId
   * @param templateId
   * @param reviewerIds
   */
  @LogAround
  @Override
  public List<ReviewInvitation> listInvitationOfTemplate(long orgId, long templateId, List<Long> reviewerIds) {
    if(null == reviewerIds || reviewerIds.isEmpty()) {
      return Collections.EMPTY_LIST;
    }
    List<ReviewInvitation> result = reviewCommentDao.listInvitationOfTemplate(orgId, templateId, reviewerIds);
    return result;
  }

  @LogAround
  @Override
  public void batchInsertReviewComments(List<ReviewComment> reviewComments) {
    if (!CollectionUtils.isEmpty(reviewComments)) {
      reviewCommentDao.batchInsertReviewComments(reviewComments);
    }
  }
}
