// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.JobTransfer;

import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("jobTransferDao")
public class JobTransferDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.JobTransferMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertJobTransfer(JobTransfer jobTransfer) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertJobTransfer", jobTransfer);
    return jobTransfer.getJobTransferId();
  }

  public JobTransfer findJobTransferByOrgIdAndPrimaryKey(long orgId, long jobTransferId) {
    Map<String, Object> params = new HashMap<>();
    params.put("jobTransferId", jobTransferId);
    params.put("orgId", orgId);
    JobTransfer jobTransfer = sqlSessionTemplate
        .selectOne(BASE_PACKAGE + "findJobTransferByPrimaryKeyAndOrgId", params);
    return jobTransfer;
  }

  public List<JobTransfer> listJobTransferByOrgIdOrderByCreatedTimeDesc(long orgId, int pageNumber, int pageSize) {
    List<JobTransfer> jobTransfers = Collections.EMPTY_LIST;
    Map<Object, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    jobTransfers = sqlSessionTemplate.selectList(
        BASE_PACKAGE + "listJobTransferByOrgIdOrderByCreatedTimeDesc", params);
    return jobTransfers;
  }

  public List<JobTransfer> listJobTransferByOrgIdAndPrimaryKeys(long orgId, List<Long> jobTransferIds) {
    List<JobTransfer> jobTransfers = Collections.EMPTY_LIST;
    if (!CollectionUtils.isEmpty(jobTransferIds)) {
      Map<Object, Object> params = new HashMap<>();
      params.put("orgId", orgId);
      params.put("jobTransferIds", jobTransferIds);
      jobTransfers = sqlSessionTemplate.selectList(BASE_PACKAGE + "listJobTransferByOrgIdAndPrimaryKeys", params);
    }
    return jobTransfers;
  }

  public int countJobTransferByOrgId(long orgId) {
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "countJobTransferByOrgId", orgId);
  }

}
