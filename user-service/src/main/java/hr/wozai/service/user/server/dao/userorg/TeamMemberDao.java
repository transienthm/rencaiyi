// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.TeamMember;
import hr.wozai.service.user.server.model.userorg.TeamMemberInfo;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016/2/17
 */
@Repository("teamMemberDao")
public class TeamMemberDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.TeamMemberMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertTeamMember(TeamMember teamMember) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertTeamMember", teamMember);
    return teamMember.getTeamMemberId();
  }

  public void batchInsertTeamMember(List<TeamMember> teamMembers) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertTeamMember", teamMembers);
  }

  public void deleteTeamMember(long orgId, long userId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("orgId", orgId);
    params.put("actorUserId", actorUserId);
    sqlSessionTemplate.update(BASE_PACKAGE + "deleteTeamMember", params);
  }

  public void batchDeleteTeamMember(List<TeamMember> teamMembers) {
    sqlSessionTemplate.update(BASE_PACKAGE + "batchDeleteTeamMember", teamMembers);
  }

  public long updateTeamMember(TeamMember teamMember) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateTeamMember", teamMember);
  }

  @LogAround
  public int batchDeleteTeamMembersByUserIds(long orgId, List<Long> userIds, long actorUserId) {
    if (null == userIds || userIds.isEmpty()) {
      return 0;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("userIds", userIds);
    params.put("orgId", orgId);
    params.put("actorUserId", actorUserId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "batchDeleteTeamMembersByUserIds", params);
  }

  public void batchUpdateTeamMembers(long orgId, long toTeamId, List<Long> userIds, long actorUserId) {
    if (null == userIds || userIds.isEmpty()) {
      return;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("userIds", userIds);
    params.put("toTeamId", toTeamId);
    params.put("orgId", orgId);
    params.put("actorUserId", actorUserId);
    sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateTeamMembers", params);
  }

  public TeamMember findByUserIdAndOrgId(long orgId, long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findByUserIdAndOrgId", params);
  }

  public List<Long> listUserIdsByOrgIdAndTeamIds(long orgId, List<Long> teamIds, int pageNumber, int pageSize) {
    List<Long> result = new ArrayList<>();
    if (null == teamIds || teamIds.isEmpty()) {
      return result;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("teamIds", teamIds);
    int pageStart = (pageNumber - 1) * pageSize;
    params.put("pageStart", pageStart);
    params.put("pageSize", pageSize);
    result = sqlSessionTemplate.selectList(BASE_PACKAGE +
            "listUserIdsByOrgIdAndTeamIds", params);
    return result;
  }

  public Long countUserNumberByTeamId(long orgId, List<Long> teamIds) {
    if (null == teamIds || teamIds.isEmpty()) {
      return 0L;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("teamIds", teamIds);
    Long result = sqlSessionTemplate.selectOne(BASE_PACKAGE + "countUserNumberByTeamId", params);
    return result;
  }

  public List<TeamMemberInfo> listTeamMemberInfoByUserIds(long orgId, List<Long> userIds) {
    List<TeamMemberInfo> result = new ArrayList<>();
    if (null == userIds || userIds.isEmpty()) {
      return result;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("userIds", userIds);
    params.put("orgId", orgId);

    result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listTeamMemberInfoByUserIds", params);
    return result;
  }

}
