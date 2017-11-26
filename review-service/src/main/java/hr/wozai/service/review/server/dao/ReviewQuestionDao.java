// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.dao;

import hr.wozai.service.review.server.model.ReviewQuestion;
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
 * @Created: 2016-03-04
 */
@Repository("reviewQuestionDao")
public class ReviewQuestionDao{

  private static final String BASE_PACKAGE = "hr.wozai.service.review.server.dao.ReviewQuestionMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  
  public long insertReviewQuestion(ReviewQuestion reviewQuestion) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertReviewQuestion", reviewQuestion);
    return reviewQuestion.getQuestionId();
  }

  
  public ReviewQuestion findReviewQuestion(long orgId, long questionId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("questionId", questionId);
    ReviewQuestion question = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "findReviewQuestion", map);
    return question;
  }

  
  public List<ReviewQuestion> listReviewQuestion(long orgId, long templateId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    List<ReviewQuestion> questions = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listReviewQuestion", map);
    return questions;
  }

  
  public long countReviewQuestionOfTemplate(long orgId, long templateId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    long result = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "countReviewQuestionOfTemplate", map);
    return result;
  }

  
  public int updateReviewQuestion(ReviewQuestion reviewQuestion) {
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "updateReviewQuestion", reviewQuestion);
    return result;
  }

  
  public int deleteReviewQuestion(long orgId, long questionId, long lastModifiedUserId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("questionId", questionId);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "deleteReviewQuestion", map);
    return result;
  }

  
  public int deleteReviewQuestionOfTemplate(long orgId, long templateId, long lastModifiedUserId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "deleteReviewQuestionOfTemplate", map);
    return result;
  }

}
