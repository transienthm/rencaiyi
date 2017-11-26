package hr.wozai.service.user.server.dao.okr;

import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.model.okr.OkrComment;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/16
 */
@Repository
public class OkrCommentDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.okr.OkrCommentMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;


  @LogAround
  public long insertOkrComment(OkrComment okrComment) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertOkrComment", okrComment);
    return okrComment.getOkrCommentId();
  }

  @LogAround
  public long updateOkrComment(OkrComment okrComment) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateOkrComment", okrComment);
  }

  @LogAround
  public OkrComment findOkrComment(long okrCommentId, long orgId) {
    Map map = new HashMap();
    map.put("okrCommentId", okrCommentId);
    map.put("orgId", orgId);
    OkrComment okrComment = sqlSessionTemplate.selectOne(BASE_PACKAGE +
            "findOkrComment", map);
    return okrComment;
  }

  @LogAround
  public List<OkrComment> listOkrCommentsByObjectiveId(
          long orgId, long objectiveId, long keyResultId, int pageNumber, int pageSize) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("objectiveId", objectiveId);
    map.put("keyResultId", keyResultId);
    int pageStart = (pageNumber - 1) * pageSize;
    map.put("pageStart", pageStart);
    map.put("pageSize", pageSize);
    List<OkrComment> okrComments = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listOkrCommentsByObjectiveId", map);
    return okrComments;
  }

  @LogAround
  public Long countOkrCommentByObjectiveId(long orgId, long objectiveId, long keyResultId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("objectiveId", objectiveId);
    map.put("keyResultId", keyResultId);

    Long result = sqlSessionTemplate.selectOne(BASE_PACKAGE + "countOkrCommentByObjectiveId", map);
    return result;
  }

}
