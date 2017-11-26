// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.conversation;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.wozai.service.user.server.model.conversation.ConvrSchedule;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-25
 */
@Repository("convrScheduleDao")
public class ConvrScheduleDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.ConvrScheduleMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertConvrSchedule(ConvrSchedule convrSchedule) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertConvrSchedule", convrSchedule);
    return convrSchedule.getConvrScheduleId();
  }

  public ConvrSchedule findConvrScheduleByPrimaryKeyAndOrgId(long convrScheduleId, long orgId) {
    Map<String, Object> params = new HashMap<>();
    params.put("convrScheduleId", convrScheduleId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findConvrScheduleByPrimaryKeyAndOrgId", params);
  }

  public ConvrSchedule findConvrScheduleBySourceUserIdAndTargetUserIdAndOrgId(
      long sourceUserId, long targetUserId, long orgId) {
    Map<String, Object> params = new HashMap<>();
    params.put("sourceUserId", sourceUserId);
    params.put("targetUserId", targetUserId);
    params.put("orgId", orgId);
    return
        sqlSessionTemplate.selectOne(BASE_PACKAGE + "findConvrScheduleBySourceUserIdAndTargetUserIdAndOrgId", params);
  }

  public List<ConvrSchedule> listConvrScheduleByPrimaryKeyAndOrgId(List<Long> convrScheduleIds, long orgId) {
    if (CollectionUtils.isEmpty(convrScheduleIds)) {
      return Collections.EMPTY_LIST;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("convrScheduleIds", convrScheduleIds);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listConvrScheduleByPrimaryKeyAndOrgId", params);
  }

  public List<ConvrSchedule> listConvrScheduleStatByPrimaryKeyAndOrgId(List<Long> convrScheduleIds, long orgId) {
    if (CollectionUtils.isEmpty(convrScheduleIds)) {
      return Collections.EMPTY_LIST;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("convrScheduleIds", convrScheduleIds);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listConvrScheduleStatByPrimaryKeyAndOrgId", params);
  }

  public List<ConvrSchedule> listConvrScheduleByOrgIdAndSourceUserId(
      long sourceUserId, int pageNumber, int pageSize, long orgId) {
    Map<String, Object> params = new HashMap<>();
    params.put("sourceUserId", sourceUserId);
    params.put("orgId", orgId);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listConvrScheduleByOrgIdAndSourceUserId", params);
  }

  public List<ConvrSchedule> listConvrScheduleByTargetUserIdAndOrgId(long targetUserId, long orgId) {
    Map<String, Object> params = new HashMap<>();
    params.put("targetUserId", targetUserId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listConvrScheduleByTargetUserIdAndOrgId", params);
  }

  public int countConvrScheduleByOrgIdAndSourceUserId(long sourceUserId, long orgId) {
    Map<String, Object> params = new HashMap<>();
    params.put("sourceUserId", sourceUserId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "countConvrScheduleByOrgIdAndSourceUserId", params);
  }

  public List<Long> listTargetUserIdsBySourceUserIdAndOrgId(long sourceUserId, long orgId) {
    Map<String, Object> params = new HashMap<>();
    params.put("sourceUserId", sourceUserId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listTargetUserIdsBySourceUserIdAndOrgId", params);
  }

  public int updateConvrScheduleByPrimaryKeyAndOrgIdSelective(ConvrSchedule convrSchedule) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateConvrScheduleByPrimaryKeyAndOrgIdSelective", convrSchedule);
  }

}
