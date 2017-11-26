// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.Team;
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
@Repository("teamDao")
public class TeamDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.TeamMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertTeam(Team team) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertTeam", team);
    return team.getTeamId();
  }

  public Team getTeamByPrimaryKeyAndOrgId(long orgId, long teamId) {
    Map<String, Object> params = new HashMap<>();
    params.put("teamId", teamId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "getTeamByPrimaryKeyAndOrgId", params);
  }

  public List<Team> listTeamByOrgIdAndTeamIds(long orgId, List<Long> teamIds) {
    List<Team> result = new ArrayList<>();
    if (CollectionUtils.isEmpty(teamIds)) {
      return result;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("teamIds", teamIds);
    result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listTeamByOrgIdAndTeamIds", params);
    return result;
  }

  public List<Team> listNextLevelTeams(long orgId, long teamId) {
    Map<String, Object> params = new HashMap<>();
    params.put("parentTeamId", teamId);
    params.put("orgId", orgId);
    params.put("forUpdate", 1);
    List<Team> teamList = sqlSessionTemplate.selectList(BASE_PACKAGE + "listNextLevelTeams", params);
    if (teamList.size() == 0) {
      teamList = Collections.EMPTY_LIST;
    }
    return teamList;
  }

  public List<Team> listAllTeams(long orgId) {
    List<Team> teams = sqlSessionTemplate.selectList(BASE_PACKAGE + "listAllTeams", orgId);

    return teams;
  }

  public long updateTeam(Team team) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateByPrimaryKey", team);
  }

  public int batchUpdateParentTeamId(long orgId, List<Long> teamIds, long parentTeamId, long actorUserId) {
    if (teamIds.size() == 0) {
      return 0;
    }
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("parentTeamId", parentTeamId);
    params.put("teamIds", teamIds);
    params.put("actorUserId", actorUserId);
    int result = sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateParentTeamId", params);

    return result;
  }

  public long deleteTeam(long orgId, long teamId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("teamId", teamId);
    params.put("actorUserId", actorUserId);

    return sqlSessionTemplate.update(BASE_PACKAGE + "deleteByPrimaryKeyAndOrgId", params);
  }
}
