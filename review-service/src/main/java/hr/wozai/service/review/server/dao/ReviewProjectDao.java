// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.dao;

import hr.wozai.service.review.server.model.ReviewProject;
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
@Repository("reviewProjectDao")
public class ReviewProjectDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.review.server.dao.ReviewProjectMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;


  
  public long insertReviewProject(ReviewProject reviewProject) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertReviewProject", reviewProject);
    return reviewProject.getProjectId();
  }

  
  public ReviewProject findReviewProject(long orgId, long projectId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("projectId", projectId);
    ReviewProject project = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "findReviewProject", map);
    return project;
  }

  
  public List<ReviewProject> listReviewProject(long orgId, long templateId, long revieweeId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("revieweeId", revieweeId);
    List<ReviewProject> projects = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listReviewProject", map);
    return projects;
  }

  
  public int updateReviewProject(ReviewProject reviewProject) {
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "updateReviewProject", reviewProject);
    return result;
  }

  
  public int deleteReviewProject(long orgId, long projectId, long lastModifiedUserId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("projectId", projectId);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "deleteReviewProject", map);
    return result;
  }

  
  public List<Long> listProjectRevieweeIdOfTemplate(long orgId, long templateId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    List<Long> result = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listProjectRevieweeIdOfTemplate", map);
    return result;
  }

  public void batchInsertReviewProjects(List<ReviewProject> reviewProjects) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertReviewProjects", reviewProjects);
  }
}
