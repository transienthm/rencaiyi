// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.Org;

import org.aspectj.weaver.ast.Or;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("orgDao")
public class OrgDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.OrgMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public long insertOrg(Org org) {
    sqlSessionTemplate.insert(BASE_PACKAGE + "insertOrg", org);
    return org.getOrgId();
  }

  public Org findOrgByPrimaryKey(long orgId) {
    return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findOrgByPrimaryKey", orgId);
  }

  public int updateOrgByPrimaryKeySelective(Org org) {
    cleanOrgForUpdate(org);
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateOrgByPrimaryKeySelective", org);
  }

  public int deleteOrgByPrimaryKey(Org org) {
    cleanOrgForDelete(org);
    return sqlSessionTemplate.update(BASE_PACKAGE + "updateOrgByPrimaryKeySelective", org);
  }

  public List<Org> listAllOrgs() {
    return sqlSessionTemplate.selectList(BASE_PACKAGE + "listAllOrgs");
  }

  private void cleanOrgForUpdate(Org org) {
    org.setIsDeleted(null);
  }

  private void cleanOrgForDelete(Org org) {
    org.setIsDeleted(1);
  }

}
