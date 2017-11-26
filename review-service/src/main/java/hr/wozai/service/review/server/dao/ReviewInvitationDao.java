// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.review.server.dao;

import hr.wozai.service.review.server.model.ReviewInvitation;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-03
 */
@Repository("reviewInvitationDao")
public class ReviewInvitationDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.review.server.dao.ReviewInvitationMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;


  
  public long insertReviewInvitation(ReviewInvitation reviewInvitation) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertReviewInvitation", reviewInvitation);
    return reviewInvitation.getInvitationId();
  }

  
  public void batchInsertReviewInvitations(List<ReviewInvitation> reviewInvitations) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertReviewInvitations", reviewInvitations);
  }

  
  public int deleteReviewInvitation(long orgId, long invitationId, long lastModifiedUserId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("invitationId", invitationId);
    map.put("lastModifiedUserId", lastModifiedUserId);
    int result =sqlSessionTemplate.update(BASE_PACKAGE + "deleteReviewInvitation", map);
    return result;
  }

  
  public ReviewInvitation findReviewInvitation(long orgId, long invitationId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("invitationId", invitationId);
    ReviewInvitation invitation = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "findReviewInvitation", map);
    return invitation;
  }

  
  public ReviewInvitation findManagerInvitation(long orgId, long templateId, long revieweeId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("revieweeId", revieweeId);
    ReviewInvitation invitation = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "findManagerInvitation", map);
    return invitation;
  }


  
  public ReviewInvitation findReviewInvitationByTemplate(long orgId, long templateId,
                                                         long revieweeId, long reviewerId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("revieweeId", revieweeId);
    map.put("reviewerId", reviewerId);
    ReviewInvitation result = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "findReviewInvitationByTemplate", map);
    return result;
  }

  
  public List<ReviewInvitation> listUnSubmittedReviewInvitation(long orgId, long reviewerId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("reviewerId", reviewerId);
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listUnSubmittedReviewInvitation", map);
  }

  
  public List<ReviewInvitation> listSubmittedReviewInvitation(long orgId, long reviewerId,
                                                              int pageNumber, int pageSize) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("reviewerId", reviewerId);
    map.put("pageStart", (pageNumber-1) * pageSize);
    map.put("pageSize", pageSize);
    List<ReviewInvitation> invitations = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listSubmittedReviewInvitation", map);
    return invitations;
  }

  
  public long countSubmittedReviewInvitation(long orgId, long reviewerId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("reviewerId", reviewerId);
    long result = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "countSubmittedReviewInvitation", map);
    return result;
  }

  
  public List<ReviewInvitation> listRevieweeReviewInvitation(long orgId, long revieweeId, long reviewerId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("revieweeId", revieweeId);
    map.put("reviewerId", reviewerId);
    List<ReviewInvitation> result = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listRevieweeReviewInvitation", map);
    return result;
  }

  
  public List<ReviewInvitation> listCanceledReviewInvitation(long orgId, long reviewerId,
                                                             int pageNumber, int pageSize) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("reviewerId", reviewerId);
    map.put("pageStart", (pageNumber-1) * pageSize);
    map.put("pageSize", pageSize);
    List<ReviewInvitation> invitations = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listCanceledReviewInvitation", map);
    return invitations;
  }

  
  public long countCanceledReviewInvitation(long orgId, long reviewerId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("reviewerId", reviewerId);
    long result = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "countCanceledReviewInvitation", map);
    return result;
  }

  
  public List<ReviewInvitation> listUnCanceledReviewInvitationOfTemplate(long orgId, long templateId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    List<ReviewInvitation> invitations = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listUnCanceledReviewInvitationOfTemplate", map);
    return invitations;
  }

  
  public List<ReviewInvitation> listReviewInvitationOfTemplateAsReviewee(long orgId,
                                                                         long templateId,
                                                                         long revieweeId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("revieweeId", revieweeId);
    List<ReviewInvitation> invitations = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listReviewInvitationOfTemplateAsReviewee", map);
    return invitations;
  }

  
  public boolean isReviewerInvited(long orgId, long templateId,
                                   long revieweeId, long reviewerId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("revieweeId", revieweeId);
    map.put("reviewerId", reviewerId);
    ReviewInvitation result = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "findReviewInvitationByTemplate", map);
    return null != result;
  }

  
  public int updateReviewInvitation(ReviewInvitation reviewInvitation) {
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "updateReviewInvitation", reviewInvitation);
    return result;
  }

  
  public void batchUpdateReviewInvitations(List<ReviewInvitation> reviewInvitations) {
    sqlSessionTemplate.update(BASE_PACKAGE +
        "batchUpdateReviewInvitations", reviewInvitations);
  }

  
  public int updateReviewInvitationBackupStatus(long orgId, long invitationId, int isBackuped) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("invitationId", invitationId);
    map.put("isBackuped", isBackuped);
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "updateReviewInvitationBackupStatus", map);
    return result;
  }

  
  public long countReviewInvitationOfTemplate(long orgId, long templateId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    long result = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "countReviewInvitationOfTemplate", map);
    return result;
  }

  
  public long countFinishedReviewInvitationOfTemplate(long orgId, long templateId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    long result = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "countFinishedReviewInvitationOfTemplate", map);
    return result;
  }

  
  public List<ReviewInvitation> listStaffReviewerIdOfTemplate(long orgId, long templateId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("isManager", 0);

    List<ReviewInvitation> result = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listReviewerIdOfTemplate", map);
    return result;
  }

  
  public List<ReviewInvitation> listStaffSubmittedReviewerIdOfTemplate(long orgId, long templateId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("isManager", 0);

    List<ReviewInvitation> result = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listSubmittedReviewerIdOfTemplate", map);
    return result;
  }

  
  public List<ReviewInvitation> listManagerReviewerIdOfTemplate(long orgId, long templateId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("isManager", 1);

    List<ReviewInvitation> result = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listReviewerIdOfTemplate", map);
    return result;
  }

  
  public List<ReviewInvitation> listManagerSubmittedReviewerIdOfTemplate(long orgId, long templateId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("isManager", 1);

    List<ReviewInvitation> result = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listSubmittedReviewerIdOfTemplate", map);
    return result;
  }

  
  public Map<Integer, Long> countReviewInvitationScore(long orgId, long templateId) {

    Map<Integer, Long> result = new HashMap<>();

    Map<String, Object> param = new HashMap<>();
    param.put("orgId", orgId);
    param.put("templateId", templateId);

    List<Map<String, Object>> sqlResult = sqlSessionTemplate.selectList(BASE_PACKAGE + "countReviewInvitationScore", param);

    for(Map<String, Object> scoreMap: sqlResult) {

      // type is corresponding to definition
      Object score = scoreMap.get("score");
      Object amount = scoreMap.get("amount");

      result.put((Integer)score, (Long)amount);
    }

    return result;
  }


  
  public List<ReviewInvitation> listAllReviewInvitationOfTemplate(long orgId, long templateId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);

    List<ReviewInvitation> result = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listAllReviewInvitationOfTemplate", map);
    return result;
  }

  public List<ReviewInvitation> listAllReviewInvitationByTemplateIdAndRevieweeId(
          long orgId, long templateId, long revieweeId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("revieweeId", revieweeId);
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listAllReviewInvitationByTemplateIdAndRevieweeId", map);
  }

  public List<ReviewInvitation> listAllReviewInvitationByTemplateIdAndRevieweeIdExceptManager(
      long orgId, long templateId, long revieweeId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("revieweeId", revieweeId);
    return sqlSessionTemplate.selectList(
        BASE_PACKAGE + "listAllReviewInvitationByTemplateIdAndRevieweeIdExceptManager", map);
  }

  public List<ReviewInvitation> listAllReviewInvitationByTemplateIdAndReviewerId(
      long orgId, long templateId, long reviewerId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    map.put("reviewerId", reviewerId);
    return sqlSessionTemplate.selectList(
        BASE_PACKAGE + "listAllReviewInvitationByTemplateIdAndReviewerId", map);
  }

  public List<ReviewInvitation> listAllReviewInvitationOfReviewer(long orgId, long reviewerId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("reviewerId", reviewerId);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listAllReviewInvitationOfReviewer", params);
  }

  public List<ReviewInvitation> listAllReviewInvitationsByTemplatesAndReviewer(
          long orgId, List<Long> templatesList, long reviewerId) {
    List<ReviewInvitation> result = new ArrayList<>();
    if(!templatesList.isEmpty()) {
      Map map = new HashMap();
      map.put("orgId", orgId);
      map.put("templatesList", templatesList);
      map.put("reviewerId", reviewerId);
      result = sqlSessionTemplate.selectList(
              BASE_PACKAGE + "listAllReviewInvitationsByTemplatesAndReviewer", map
      );
    }
    return result;
  }
}
