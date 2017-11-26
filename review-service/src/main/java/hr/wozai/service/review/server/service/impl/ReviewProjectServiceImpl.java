// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.review.server.helper.ReviewProjectHelper;
import hr.wozai.service.review.server.model.ReviewProject;
import hr.wozai.service.review.server.dao.ReviewProjectDao;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-08
 */
@Service("reviewProjectService")
public class ReviewProjectServiceImpl implements hr.wozai.service.review.server.service.ReviewProjectService {

  @Autowired
  private ReviewProjectDao reviewProjectDao;

  /**
   * Insert review project
   * @param reviewProject
   * @return
   */
  @LogAround
  @Override
  public long insertReviewProject(ReviewProject reviewProject) {

    ReviewProjectHelper.chectReviewProjectInsertParams(reviewProject);
    long result = reviewProjectDao.insertReviewProject(reviewProject);
    return result;
  }

  /**
   * Find review project
   * @param orgId
   * @param projectId
   * @return
   */
  @LogAround
  @Override
  public ReviewProject findReviewProject(long orgId, long projectId) {
    ReviewProject reviewProject = reviewProjectDao.findReviewProject(orgId, projectId);
    if(null == reviewProject) {
      throw new ServiceStatusException(ServiceStatus.REVIEW_PROJECT_NOT_FOUND);
    }
    return reviewProject;
  }

  /**
   * List revieweeId's review project of templateId
   * @param orgId
   * @param templateId
   * @param revieweeId
   * @return
   */
  @LogAround
  @Override
  public List<ReviewProject> listReviewProject(long orgId, long templateId, long revieweeId) {
    List<ReviewProject> reviewProjects = reviewProjectDao.listReviewProject(orgId, templateId, revieweeId);
    return reviewProjects;
  }

  /**
   * Update review project
   * @param reviewProject
   */
  @LogAround
  @Override
  public void updateReviewProject(ReviewProject reviewProject) {
    ReviewProjectHelper.chectReviewProjectInsertParams(reviewProject);
    reviewProjectDao.updateReviewProject(reviewProject);
  }

  /**
   * Delete review project
   * @param orgId
   * @param projectId
   * @param lastModifiedUserId
   */
  @LogAround
  @Override
  public void deleteReviewProject(long orgId, long projectId, long lastModifiedUserId) {
    reviewProjectDao.deleteReviewProject(orgId, projectId, lastModifiedUserId);
  }

  /**
   * List project revieweeId of template
   * @param orgId
   * @param templateId
   */
  @LogAround
  @Override
  public List<Long> listProjectRevieweeIdOfTemplate(long orgId, long templateId) {

    List<Long> result = reviewProjectDao.listProjectRevieweeIdOfTemplate(orgId, templateId);
    return result;
  }

  @LogAround
  @Override
  public void batchInsertReviewProjects(List<ReviewProject> reviewProjects) {
    reviewProjectDao.batchInsertReviewProjects(reviewProjects);
  }
}
