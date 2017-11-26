// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.ProjectTeam;
import hr.wozai.service.user.server.model.userorg.Team;
import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016/11/16
 */
@Repository("projectTeamDao")
public class ProjectTeamDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.ProjectTeamMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertProjectTeam(ProjectTeam projectTeam) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertProjectTeam", projectTeam);
    return projectTeam.getProjectTeamId();
  }

  public long deleteProjectTeamByPrimaryKeyAndOrgId(long orgId, long projectTeamId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("projectTeamId", projectTeamId);
    params.put("actorUserId", actorUserId);

    return sqlSessionTemplate.update(BASE_PACKAGE + "deleteProjectTeamByPrimaryKeyAndOrgId", params);
  }

  public long updateProjectTeam(ProjectTeam projectTeam) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateProjectTeam", projectTeam);
  }

  public ProjectTeam getProjectTeamByPrimaryKeyAndOrgId(long orgId, long projectTeamId) {
    Map<String, Object> params = new HashMap<>();
    params.put("projectTeamId", projectTeamId);
    params.put("orgId", orgId);
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "getProjectTeamByPrimaryKeyAndOrgId", params);
  }

  public List<ProjectTeam> listProjectTeamsByOrgIdAndTeamId(long orgId, long teamId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("teamId", teamId);

    List<ProjectTeam> result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listProjectTeamsByOrgIdAndTeamId", params);
    return result;
  }
}
