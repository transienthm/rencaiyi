// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.test.dao;

import hr.wozai.service.review.server.model.ReviewProject;
import hr.wozai.service.review.server.dao.ReviewProjectDao;
import hr.wozai.service.review.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-03
 */
public class ReviewProjectDaoTest extends TestBase {

  private static Logger LOGGER = LoggerFactory.getLogger(ReviewProjectDaoTest.class);

  @Autowired
  ReviewProjectDao reviewProjectDao;

  ReviewProject reviewProject;

  List<ReviewProject> projects;

  long orgId = 100L;

  long templateId = 1L;
  long revieweeId = 11L;

  String name = "DL study";
  String role = "staff";
  int score = 3;
  String comment = "Perfect";

  @Before
  public void setup() {

    reviewProject = new ReviewProject();
    reviewProject.setOrgId(orgId);
    reviewProject.setTemplateId(templateId);
    reviewProject.setRevieweeId(revieweeId);
    reviewProject.setName(name);
    reviewProject.setRole(role);
    reviewProject.setScore(score);
    reviewProject.setComment(comment);
    reviewProject.setLastModifiedUserId(revieweeId);

  }

  @Test
  public void testInsertReviewProject() throws Exception {

    long projectId = reviewProjectDao.insertReviewProject(reviewProject);
    Assert.assertEquals(projectId, reviewProject.getProjectId().longValue());
  }

  @Test
  public void testFindReviewProject() throws Exception {

    long projectId = reviewProjectDao.insertReviewProject(reviewProject);
    Assert.assertEquals(projectId, reviewProject.getProjectId().longValue());

    ReviewProject insertedReviewProject = reviewProjectDao.findReviewProject(orgId, reviewProject.getProjectId());
    Assert.assertEquals(insertedReviewProject.getName(), name);
    Assert.assertEquals(insertedReviewProject.getRole(), role);
    Assert.assertEquals(insertedReviewProject.getScore().intValue(), score);
  }

  @Test
  public void testListReviewProject() throws Exception {

    for(long i=1; i<4; i++) {
      reviewProject.setName("name" + i);
      reviewProjectDao.insertReviewProject(reviewProject);
    }
    projects = reviewProjectDao.listReviewProject(orgId, templateId, revieweeId);
    Assert.assertEquals(projects.size(), 3);
    LOGGER.info("project number " + projects.size());
  }

  @Test
  public void testUpdateReviewProject() throws Exception {

    long projectId = reviewProjectDao.insertReviewProject(reviewProject);
    Assert.assertEquals(projectId, reviewProject.getProjectId().longValue());

    ReviewProject reviewProjectResult;
    reviewProjectResult = reviewProjectDao.findReviewProject(orgId, projectId);

    String nameNew = "python";
    String roleNew = "manager";
    int scoreNew = 5;
    reviewProjectResult.setName(nameNew);
    reviewProjectResult.setRole(roleNew);
    reviewProjectResult.setScore(scoreNew);
    reviewProjectResult.setLastModifiedUserId(revieweeId);

    int result = reviewProjectDao.updateReviewProject(reviewProjectResult);
    Assert.assertEquals(result, 1);

    reviewProjectResult = reviewProjectDao.findReviewProject(orgId, projectId);
    Assert.assertEquals(reviewProjectResult.getName(), nameNew);
    Assert.assertEquals(reviewProjectResult.getRole(), roleNew);
    Assert.assertEquals(reviewProjectResult.getScore().intValue(), scoreNew);
    Assert.assertEquals(reviewProjectResult.getLastModifiedUserId().longValue(), revieweeId);
  }

  @Test
  public void testDeleteReviewProject() throws Exception {

    long projectId = reviewProjectDao.insertReviewProject(reviewProject);
    Assert.assertEquals(projectId, reviewProject.getProjectId().longValue());

    int result = reviewProjectDao.deleteReviewProject(orgId, projectId, revieweeId);
    Assert.assertEquals(result, 1);

    projects = reviewProjectDao.listReviewProject(orgId, templateId, revieweeId);
    Assert.assertEquals(projects.size(), 0);
    LOGGER.info("project number " + projects.size());

  }

  @Test
  public void testListProjectRevieweeIdOfTemplate() throws Exception {

    for(long i=1; i<4; i++) {
      reviewProject.setName("name" + i);
      reviewProjectDao.insertReviewProject(reviewProject);
    }
    List<Long> result = reviewProjectDao.listProjectRevieweeIdOfTemplate(orgId, templateId);
    Assert.assertEquals(result.size(), 1);
  }

}
