// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.JobTransfer;
import hr.wozai.service.user.server.model.userorg.StatusUpdate;

import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("statusUpdateDao")
public class StatusUpdateDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.StatusUpdateMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertStautsUpdate(StatusUpdate statusUpdate) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertStatusUpdate", statusUpdate);
    return statusUpdate.getStatusUpdateId();
  }

  public StatusUpdate findStatusUpdateByOrgIdAndPrimaryKey(long orgId, long statusUpdateId) {
    Map<String, Object> params = new HashMap<>();
    params.put("statusUpdateId", statusUpdateId);
    params.put("orgId", orgId);
    StatusUpdate statusUpdate = sqlSessionTemplate
        .selectOne(BASE_PACKAGE + "findStatusUpdateByOrgIdAndPrimaryKey", params);
    return statusUpdate;
  }

  public List<StatusUpdate> listStatusUpdateByOrgIdOrderByCreatedTimeDesc(
      long orgId, long statusType, int pageNumber, int pageSize) {
    List<StatusUpdate> statusUpdates = Collections.EMPTY_LIST;
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("statusType", statusType);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    statusUpdates = sqlSessionTemplate
        .selectList(BASE_PACKAGE + "listStatusUpdateByOrgIdOrderByCreatedTimeDesc", params);
    return statusUpdates;
  }

  public List<StatusUpdate> listStatusUpdateByOrgIdAndPrimaryKeys(long orgId, List<Long> statusUpdateIds) {
    List<StatusUpdate> statusUpdates = Collections.EMPTY_LIST;
    if (!CollectionUtils.isEmpty(statusUpdateIds)) {
      Map<Object, Object> params = new HashMap<>();
      params.put("orgId", orgId);
      params.put("statusUpdateIds", statusUpdateIds);
      statusUpdates = sqlSessionTemplate.selectList(BASE_PACKAGE + "listStatusUpdateByOrgIdAndPrimaryKeys", params);
    }
    return statusUpdates;
  }

  public int countStatusUpdateByOrgIdAndStatusType(long orgId, int statusType) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("statusType", statusType);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "countStatusUpdateByOrgIdAndStatusType", params);
  }

  public int revokeStatusUpdateByPrimaryKeyAndOrgId(long orgId, long statusUpdateId, long actorUserId) {
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("statusUpdateId", statusUpdateId);
    params.put("lastModifiedUserId", actorUserId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "revokeStatusUpdateByPrimaryKeyAndOrgId", params);
  }


}
