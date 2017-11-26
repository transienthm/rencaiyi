
package hr.wozai.service.review.server.dao;

import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.model.ReviewTemplate;
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
@Repository("reviewActivityDao")
public class ReviewActivityDao{

  private static final String BASE_PACKAGE = "hr.wozai.service.review.server.dao.ReviewActivityMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  @LogAround
  public long insertReviewActivity(ReviewActivity reviewActivity) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertReviewActivity", reviewActivity);
    return reviewActivity.getActivityId();
  }

  @LogAround
  public void batchInsertReviewActivities(List<ReviewActivity> reviewActivities) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertReviewActivities", reviewActivities);
  }

  @LogAround
  public ReviewActivity findReviewActivity(long orgId, long activityId) {
    return sqlSessionTemplate.selectOne(
            BASE_PACKAGE + "findReviewActivity",
            new HashMap() {
              {
                put("orgId", orgId);
                put("activityId", activityId);
              }
            }
    );
  }

  @LogAround
  public ReviewActivity findReviewActivityByRevieweeId(long orgId, long templateId, long revieweeId) {
    return sqlSessionTemplate.selectOne(
            BASE_PACKAGE + "findReviewActivityByRevieweeId",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
                put("revieweeId", revieweeId);
              }
            }
    );
  }

  @LogAround
  public List<ReviewActivity> listUnSubmittedReviewActivity(long orgId, long revieweeId) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listUnSubmittedReviewActivity",
            new HashMap() {
              {
                put("orgId", orgId);
                put("revieweeId", revieweeId);
              }
            }
    );
  }

  @LogAround
  public List<ReviewActivity> listOtherReviewActivity(
          long orgId, long revieweeId, int pageNumber, int pageSize) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listOtherReviewActivity",
            new HashMap() {
              {
                put("orgId", orgId);
                put("revieweeId", revieweeId);
                put("pageStart", (pageNumber-1) * pageSize);
                put("pageSize", pageSize);
              }
            }
    );
  }

  @LogAround
  public long countOtherReviewActivity(long orgId, long revieweeId) {
    return sqlSessionTemplate.selectOne(
            BASE_PACKAGE + "countOtherReviewActivity",
            new HashMap() {
              {
                put("orgId", orgId);
                put("revieweeId", revieweeId);
              }
            }
    );
  }

  @LogAround
  public List<ReviewActivity> listUnCanceledReviewActivityOfTemplate(long orgId, long templateId) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listUnCanceledReviewActivityOfTemplate",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
              }
            }
    );
  }

  @LogAround
  public int updateReviewActivity(ReviewActivity reviewActivity) {
    return sqlSessionTemplate.update(
            BASE_PACKAGE + "updateReviewActivity", reviewActivity
    );
  }

  @LogAround
  public void batchUpdateReviewActivities(List<ReviewActivity> reviewActivities) {
    sqlSessionTemplate.update(
            BASE_PACKAGE + "batchUpdateReviewActivities", reviewActivities
    );
  }

  @LogAround
  public long countReviewActivityOfTemplate(long orgId, long templateId) {
    return sqlSessionTemplate.selectOne(
            BASE_PACKAGE + "countReviewActivityOfTemplate",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
              }
            }
    );
  }

  @LogAround
  public List<Long> listAllRevieweeIdOfTemplate(long orgId, long templateId) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listAllRevieweeIdOfTemplate",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
              }
            }
    );
  }

  @LogAround
  public List<Long> listSubmittedRevieweeIdOfTemplate(long orgId, long templateId) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listSubmittedRevieweeIdOfTemplate",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
              }
            }
    );
  }

  @LogAround
  public List<ReviewActivity> listAllReviewActivityOfTemplate(long orgId, long templateId) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listAllReviewActivityOfTemplate",
            new HashMap() {
              {
                put("orgId", orgId);
                put("templateId", templateId);
              }
            }
    );
  }

  @LogAround
  public List<ReviewActivity> listAllReviewActivityOfRevieweeId(long orgId, long revieweeId) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listAllReviewActivityOfRevieweeId",
            new HashMap() {
              {
                put("orgId", orgId);
                put("revieweeId", revieweeId);
              }
            }
    );
  }

  @LogAround
  public List<Long> listAllValidReviewActivitiesByRevieweeAndTemplatesList(
          long orgId, List<Long> templates, long revieweeId) {
    return sqlSessionTemplate.selectList(
            BASE_PACKAGE + "listAllValidReviewActivitiesByRevieweeAndTemplatesList",
            new HashMap() {
              {
                put("orgId", orgId);
                put("revieweeId", revieweeId);
                put("templates", templates);
              }
            }
    );
  }
}
