// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.dao;

import hr.wozai.service.review.server.model.ReviewComment;
import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-03
 */
@Repository("reviewCommentDao")
public class ReviewCommentDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.review.server.dao.ReviewCommentMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  @LogAround
  public long insertReviewComment(ReviewComment reviewComment) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertReviewComment", reviewComment);
    return reviewComment.getCommentId();
  }

  @LogAround
  public ReviewComment findReviewComment(long orgId, long commentId) {
    return sqlSessionTemplate.selectOne(
            BASE_PACKAGE + "findReviewComment",
            new HashMap() {
              {
                put("orgId", orgId);
                put("commentId", commentId);
              }
            }
    );
  }

  @LogAround
  public List<ReviewComment> listReviewItemCommentOfReviewers(
          long orgId, long templateId,
          int itemType, long itemId,
          long revieweeId, List<Long> reviewerIds) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listReviewItemComment",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
                put("itemType", itemType);
                put("itemId", itemId);
                put("revieweeId", revieweeId);
                put("reviewerIds", reviewerIds);
              }
            }
    );
  }

  @LogAround
  public List<ReviewComment> listReviewAllCommentByReviewer(
          long orgId, long templateId,
          long revieweeId, long reviewerId) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listReviewAllCommentByReviewer",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
                put("revieweeId", revieweeId);
                put("reviewerId", reviewerId);
              }
            }
    );
  }

  @LogAround
  public int updateReviewComment(ReviewComment reviewComment) {
    return sqlSessionTemplate.update(
            BASE_PACKAGE + "updateReviewComment", reviewComment
    );
  }

  @LogAround
  public int deleteReviewComment(long orgId, long commentId, long lastModifiedUserId) {
    return sqlSessionTemplate.update(
            BASE_PACKAGE + "deleteReviewCommentByPrimaryKey",
            new HashMap() {
              {
                put("orgId", orgId);
                put("commentId", commentId);
                put("lastModifiedUserId", lastModifiedUserId);
              }
            }
    );
  }

  @LogAround
  public int deleteReviewCommentByReviewer(
          long orgId,
          long templateId,
          long revieweeId,
          long reviewerId,
          long lastModifiedUserId) {
    return sqlSessionTemplate.update(
            BASE_PACKAGE + "deleteReviewCommentByReviewer",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
                put("revieweeId", revieweeId);
                put("reviewerId", reviewerId);
                put("lastModifiedUserId", lastModifiedUserId);
              }
            }
    );
  }

  @LogAround
  public long countReviewQuestionByReviewer(
          long orgId,
          long templateId,
          long revieweeId,
          long reviewerId) {
    return sqlSessionTemplate.selectOne(
            BASE_PACKAGE + "countReviewQuestionByReviewer",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
                put("revieweeId", revieweeId);
                put("reviewerId", reviewerId);
              }
            }
    );
  }

  @LogAround
  public long countReviewItemCommentByReviewer(
          long orgId, long templateId,
          int itemType, long itemId,
          long revieweeId, long reviewerId) {
    return sqlSessionTemplate.selectOne(
            BASE_PACKAGE + "countReviewItemCommentByReviewer",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
                put("itemType", itemType);
                put("itemId", itemId);
                put("revieweeId", revieweeId);
                put("reviewerId", reviewerId);
              }
            }
    );
  }

  @LogAround
  public long countReviewAllCommentByReviewer(
          long orgId, long templateId,
          long revieweeId, long reviewerId) {
    return sqlSessionTemplate.selectOne(
            BASE_PACKAGE + "countReviewAllCommentByReviewer",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
                put("revieweeId", revieweeId);
                put("reviewerId", reviewerId);
              }
            }
    );
  }

  @LogAround
  public List<Long> listActivityOfTemplate(long orgId, long templateId, List<Long> reviewerIds) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listActivityOfTemplate",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
                put("reviewerIds", reviewerIds);
              }
            }
    );
  }

  @LogAround
  public List<ReviewInvitation> listInvitationOfTemplate(long orgId, long templateId, List<Long> reviewerIds) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listInvitationOfTemplate",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
                put("reviewerIds", reviewerIds);
              }
            }
    );
  }

  @LogAround
  public void batchInsertReviewComments(List<ReviewComment> reviewComments) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertReviewComments", reviewComments);
  }
}
