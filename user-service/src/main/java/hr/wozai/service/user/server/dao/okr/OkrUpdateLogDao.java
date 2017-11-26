package hr.wozai.service.user.server.dao.okr;

import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.model.okr.OkrUpdateLog;
import org.apache.commons.collections.CollectionUtils;
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
public class OkrUpdateLogDao {
  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.okr.OkrUpdateLogMapper.";

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;


  @LogAround
  public long batchInsertOkrUpdateLog(List<OkrUpdateLog> okrUpdateLogs) {
    if (CollectionUtils.isEmpty(okrUpdateLogs)) {
      return 0;
    }
    return sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertOkrUpdateLog", okrUpdateLogs);
  }

  @LogAround
  public List<OkrUpdateLog> listOkrUpdateLogsByOkrCommentId(long orgId, long okrCommentId) {
    Map map = new HashMap();
    map.put("orgId", orgId);
    map.put("okrCommentId", okrCommentId);

    List<OkrUpdateLog> okrUpdateLogs = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listOkrUpdateLogsByOkrCommentId", map);
    return okrUpdateLogs;
  }

}
