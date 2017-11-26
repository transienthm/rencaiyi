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

import hr.wozai.service.user.server.model.conversation.ConvrRecord;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-25
 */
@Repository("convrRecordDao")
public class ConvrRecordDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.ConvrRecordMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertConvrRecord(ConvrRecord convrRecord) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertConvrRecord", convrRecord);
    return convrRecord.getConvrRecordId();
  }

  public ConvrRecord findConvrRecordByPrimaryKeyAndOrgId(long convrRecordId, long orgId) {
    Map<String, Object> params = new HashMap<>();
    params.put("convrRecordId", convrRecordId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findConvrRecordByPrimaryKeyAndOrgId", params);
  }

  public List<ConvrRecord> listConvrRecordByConvrScheduleIdAndOrgId(
      List<Long> convrScheduleIds, int pageNumber, int pageSize, long orgId) {
    if (CollectionUtils.isEmpty(convrScheduleIds)) {
      return Collections.EMPTY_LIST;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("convrScheduleIds", convrScheduleIds);
    params.put("pageStart", (pageNumber - 1) * pageSize);
    params.put("pageSize", pageSize);
    params.put("orgId", orgId);
    return sqlSessionTemplate
        .selectList(BASE_PACKAGE + "listConvrRecordByConvrScheduleIdAndOrgId", params);
  }

  public int countConvrRecordByConvrScheduleIdAndOrgId(List<Long> convrScheduleIds, long orgId) {
    if (CollectionUtils.isEmpty(convrScheduleIds)) {
      return 0;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("convrScheduleIds", convrScheduleIds);
    params.put("orgId", orgId);
    return sqlSessionTemplate
        .selectOne(BASE_PACKAGE + "countConvrRecordByConvrScheduleIdAndOrgId", params);
  }

//  public List<ConvrRecord> listConvrRecordByTargetUserIdAndOrgId(
//      long targetUserId, int pageNumber, int pageSize, long orgId) {
//    Map<String, Object> params = new HashMap<>();
//    params.put("targetUserId", targetUserId);
//    params.put("pageStart", (pageNumber - 1) * pageSize);
//    params.put("pageSize", pageSize);
//    params.put("orgId", orgId);
//    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listConvrRecordByTargetUserIdAndOrgId", params);
//  }
//
//  public int countConvrRecordByTargetUserIdAndOrgId(long targetUserId, long orgId) {
//    Map<String, Object> params = new HashMap<>();
//    params.put("targetUserId", targetUserId);
//    params.put("orgId", orgId);
//    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "countConvrRecordByTargetUserIdAndOrgId", params);
//  }

  public int updateConvrRecordByPrimaryKeyAndOrgIdSelective(ConvrRecord convrRecord) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateConvrRecordByPrimaryKeyAndOrgIdSelective", convrRecord);
  }

}
