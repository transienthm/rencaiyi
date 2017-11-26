
package hr.wozai.service.review.server.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.wozai.service.review.server.model.ReviewActivity;
import hr.wozai.service.review.server.model.ReviewInvitedTeam;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-04
 */
@Repository("reviewInvitedTeamDao")
public class ReviewInvitedTeamDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.review.server.dao.ReviewInvitedTeamMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  public void batchInsertReviewInvitedTeam(List<ReviewInvitedTeam> reviewInvitedTeams) {
    if (!CollectionUtils.isEmpty(reviewInvitedTeams)) {
      sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertReviewInvitedTeam", reviewInvitedTeams);
    }
  }

  public List<ReviewInvitedTeam> listInvitedTeamIdByOrgIdAndReviewTemplateId(long orgId, long reviewTemplateId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("reviewTemplateId", reviewTemplateId);
    List<ReviewInvitedTeam> reviewInvitedTeams =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listInvitedTeamIdByOrgIdAndReviewTemplateId", params);
    return reviewInvitedTeams;
  }

}
