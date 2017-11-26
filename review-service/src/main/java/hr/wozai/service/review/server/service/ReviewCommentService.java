// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service;

import hr.wozai.service.review.server.model.ReviewComment;
import hr.wozai.service.review.server.model.ReviewInvitation;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-07
 */
public interface ReviewCommentService {

  /**
   * Insert comment
   * @param reviewComment
   */
  public long insertReviewComment(ReviewComment reviewComment);

  /**
   * Find comment
   * @param orgId
   * @param commentId
   */
  public ReviewComment findReviewComment(long orgId, long commentId);

  /**
   * Update comment
   * @param reviewComment
   */
  public void updateReviewComment(ReviewComment reviewComment);

  /**
   * Delete comment
   * @param orgId
   * @param commentId
   * @param lastModifiedUserId
   */
  public void deleteReviewComment(long orgId, long commentId, long lastModifiedUserId);

  /**
   * Delete comment by reviewer
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @param reviewerId
   * @param lastModifiedUserId
   */
  public void deleteReviewCommentByReviewer(long orgId, long templateId,
                                            long revieweeId, long reviewerId,
                                            long lastModifiedUserId);
  /**
   * List comment of itemType, itemId belongs to reviewerIds
   * @param orgId
   * @param templateId
   * @param itemType
   * @param itemId
   * @param revieweeId
   * @param reviewerIds
   */
  public List<ReviewComment> listReviewItemCommentOfReviewers(long orgId, long templateId,
                                                              int itemType, long itemId,
                                                              long revieweeId, List<Long> reviewerIds);

  /**
   * List all comment belongs to reviewerId
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @param reviewerId
   */
  public List<ReviewComment> listReviewAllCommentByReviewer(long orgId, long templateId,
                                                            long revieweeId, long reviewerId);

  /**
   * Count comment of all question belongs to reviewerIds
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @param reviewerId
   */
  public long countReviewQuestionByReviewer(long orgId, long templateId,
                                            long revieweeId, long reviewerId);

  /**
   * Exist comment of reviewerId about tempateId, revieweeId, itemType, itemId,
   * @param orgId
   * @param templateId
   * @param itemType
   * @param itemId
   * @param revieweeId
   * @param reviewerId
   */
  public boolean existReviewItemCommentByReviewer(long orgId, long templateId,
                                                  int itemType, long itemId,
                                                  long revieweeId, long reviewerId);

  /**
   * Count review comment by reviewer
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @param reviewerId
   * @return
   */
  public long countReviewAllCommentByReviewer(long orgId, long templateId,
                                              long revieweeId, long reviewerId);

  /**
   * List distinct activity by template in reviewerIds
   * @param orgId
   * @param templateId
   * @param reviewerIds
   * @return
   */
  public List<Long> listActivityOfTemplate(long orgId, long templateId, List<Long> reviewerIds);

  /**
   * List distinct invitation by template in reviewerIds
   * @param orgId
   * @param templateId
   * @param reviewerIds
   * @return
   */
  public List<ReviewInvitation> listInvitationOfTemplate(long orgId, long templateId, List<Long> reviewerIds);

  public void batchInsertReviewComments(List<ReviewComment> reviewComments);

}
