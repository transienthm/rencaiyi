// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.model.userorg.ProjectTeam;
import hr.wozai.service.user.server.model.userorg.ProjectTeamMember;
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
@Repository("projectTeamMemberDao")
public class ProjectTeamMemberDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.ProjectTeamMemberMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public int batchInsertProjectTeamMember(List<ProjectTeamMember> projectTeamMembers) {
    return sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertProjectTeamMember", projectTeamMembers);
  }

  public int batchDeleteProjectTeamMember(List<ProjectTeamMember> projectTeamMembers) {
    return sqlSessionTemplate.update(BASE_PACKAGE + "batchDeleteProjectTeamMember", projectTeamMembers);
  }

  public ProjectTeamMember getProjectTeamMember(long orgId, long projectTeamId, long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("projectTeamId", projectTeamId);
    params.put("userId", userId);

    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "getProjectTeamMember", params);
  }

  public List<ProjectTeamMember> listProjectTeamMembersByOrgIdAndUserId(long orgId, long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("userId", userId);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listProjectTeamMembersByOrgIdAndUserId", params);
  }

  public List<Long> listUserIdsByOrgIdAndProjectTeamId(long orgId, long projectTeamId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("projectTeamId", projectTeamId);
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listUserIdsByOrgIdAndProjectTeamId", params);
  }

  public List<ProjectTeam> listProjectTeamMemberInfoByUserId(long orgId, long userId) {
    Map<String, Object> params = new HashMap<>();
    params.put("userId", userId);
    params.put("orgId", orgId);

    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listProjectTeamMemberInfoByUserId", params);
  }
}
