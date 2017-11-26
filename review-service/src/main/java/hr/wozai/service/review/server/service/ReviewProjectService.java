// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service;

import hr.wozai.service.review.server.model.ReviewProject;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-08
 */
public interface ReviewProjectService {

  /**
   * Insert review project
   * @param reviewProject
   * @return
   */
  public long insertReviewProject(ReviewProject reviewProject);

  /**
   * Find review project
   * @param orgId
   * @param projectId
   * @return
   */
  public ReviewProject findReviewProject(long orgId, long projectId);

  /**
   * List revieweeId's review project of templateId
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @return
   */
  public List<ReviewProject> listReviewProject(long orgId, long templateId, long revieweeId);

  /**
   * Update review project
   * @param reviewProject
   */
  public void updateReviewProject(ReviewProject reviewProject);

  /**
   * Delete review project
   * @param orgId
   * @param projectId
   * @param lastModifiedUserId
   */
  public void deleteReviewProject(long orgId, long projectId, long lastModifiedUserId);

  /**
   * List project revieweeId of template
   * @param orgId
   * @param templateId
   */
  public List<Long> listProjectRevieweeIdOfTemplate(long orgId, long templateId);

  public void batchInsertReviewProjects(List<ReviewProject> reviewProjects);

}
