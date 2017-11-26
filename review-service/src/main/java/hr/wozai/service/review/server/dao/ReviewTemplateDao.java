// All rights reserved

package hr.wozai.service.review.server.dao;

import hr.wozai.service.review.server.model.ReviewTemplate;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-04
 */
@Repository("reviewTemplateDao")
public class ReviewTemplateDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.review.server.dao.ReviewTemplateMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  @LogAround
  public long insertReviewTemplate(ReviewTemplate reviewTemplate) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertReviewTemplate", reviewTemplate);
    return reviewTemplate.getTemplateId();
  }

  @LogAround
  public ReviewTemplate findReviewTemplate(long orgId, long templateId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);
    ReviewTemplate template = sqlSessionTemplate.selectOne(BASE_PACKAGE +
        "findReviewTemplate", map);
    return template;
  }

  @LogAround
  public List<ReviewTemplate> listReviewTemplate(long orgId, int pageNumber, int pageSize, List<Integer> statuses) {
    List<ReviewTemplate> templates = new ArrayList<>();
    if (!CollectionUtils.isEmpty(statuses)) {
      Map map = new HashMap();
      map.put("orgId", orgId);
      map.put("pageStart", (pageNumber - 1) * pageSize);
      map.put("pageSize", pageSize);
      map.put("statuses", statuses);
      templates = sqlSessionTemplate.selectList(BASE_PACKAGE + "listReviewTemplate", map);
    }
    return templates;
  }

  @LogAround
  public long countReviewTemplate(long orgId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    long result = sqlSessionTemplate.selectOne(BASE_PACKAGE + "countReviewTemplate", map);
    return result;
  }

  @LogAround
  public List<ReviewTemplate> listActiveReviewTemplate() {
    List<ReviewTemplate> templates = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listActiveReviewTemplate");
    return templates;
  }

  @LogAround
  public int updateReviewTemplate(ReviewTemplate reviewTemplate) {
    int result = sqlSessionTemplate.update(BASE_PACKAGE +
        "updateReviewTemplate", reviewTemplate);
    return result;
  }

  @LogAround
  public void finishReviewTemplate(long orgId, long templateId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateId", templateId);

    sqlSessionTemplate.update(BASE_PACKAGE + "finishReviewTemplate", map);
  }

  @LogAround
  public List<ReviewTemplate> listReviewTemplateByTemplateIds(long orgId, List<Long> templateIds) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("templateIds", templateIds);
    List<ReviewTemplate> templates = sqlSessionTemplate.selectList(BASE_PACKAGE +
        "listReviewTemplateByTemplateIds", map);
    return templates;
  }

  @LogAround
  public List<ReviewTemplate> listAllValidReviewTemplates(long orgId) {

    Map map = new HashMap();
    map.put("orgId", orgId);
    List<ReviewTemplate> templates = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listAllValidReviewTemplates", map);
    return templates;
  }
}
