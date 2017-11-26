// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.OrgMember;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("orgMemberDao")
public class OrgMemberDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.OrgMemberMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertOrgMember(OrgMember orgMember) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertOrgMember", orgMember);
    return orgMember.getOrgMemberId();
  }

  public List<Long> listUserIdListByOrgId(long orgId) {
    List<Long> idList = sqlSessionTemplate.selectList(BASE_PACKAGE + "listUserIdListByOrgId", orgId);
    if (idList.size() == 0) {
      idList = Collections.EMPTY_LIST;
    }
    return idList;
  }

  public Long findOrgIdByUserId(long userId) {
    Long orgId = sqlSessionTemplate.selectOne(BASE_PACKAGE + "findOrgIdByUserId", userId);
    return orgId;
  }

  public OrgMember findByUserIdAndOrgId(long orgId, long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("orgId", orgId);
    OrgMember orgMember = sqlSessionTemplate.selectOne(BASE_PACKAGE + "findByUserIdAndOrgId", params);
    return orgMember;
  }

  public void deleteOrgMemberByUserId(long userId) {
    sqlSessionTemplate.update(BASE_PACKAGE + "deleteOrgMemberByUserId", userId);
  }

  /*public long updateOrgMember(OrgMember orgMember) {
    return sqlSessionTemplate.update("updateOrgMember", orgMember);
  }*/

}
