// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.ReportLine;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016/2/17
 */
@Repository("reportLineDao")
public class ReportLineDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.ReportLineMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long batchInsertReportLines(List<ReportLine> ReportLines) {
    return sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertReportLines", ReportLines);
  }

  public ReportLine getReportLineByUserId(long orgId, long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "getReportLineByUserId", params);
  }

  public List<ReportLine> listReportLinesByUserIds(long orgId, List<Long> userIds) {
    List<ReportLine> result = new ArrayList<>();
    if (CollectionUtils.isEmpty(userIds)) {
      return result;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userIds", userIds);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listReportLinesByUserIds", params);
  }

  public List<Long> listReporteesByUserId(long orgId, long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("reportUserId", userId);
    params.put("orgId", orgId);
    params.put("forUpdate", 1);
    List<Long> userIds = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listReporteesByUserId", params);
    if (userIds.size() == 0) {
      userIds = Collections.EMPTY_LIST;
    }
    return userIds;
  }

  public long batchUpdateReportLines(long orgId, List<Long> userIds, long newReportUserId,
                                     long actorUserId) {
    if (null == userIds || userIds.isEmpty()) {
      return 0L;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("userIds", userIds);
    params.put("newReportUserId", newReportUserId);
    params.put("orgId", orgId);
    params.put("actorUserId", actorUserId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateReportLines", params);
  }

  public int batchDeleteReportLines(long orgId, List<Long> userIds, long actorUserId) {
    if (null == userIds || userIds.isEmpty()) {
      return 0;
    }

    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userIds", userIds);
    params.put("actorUserId", actorUserId);

    int result = sqlSessionTemplate.update(BASE_PACKAGE + "batchDeleteReportLines", params);
    return result;
  }
}
