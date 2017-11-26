// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import hr.wozai.service.user.server.model.userorg.OrgPickOption;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-04
 */
@Repository("orgPickOptionDao")
public class OrgPickOptionDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.OrgPickOptionMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public int batchInsertOrgPickOption(List<OrgPickOption> orgPickOptions) {
    return sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertOrgPickOption", orgPickOptions);
  }

  public List<OrgPickOption> listOrgPickOptionByConfigType(long orgId, int configType) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("configType", configType);
    List<OrgPickOption> orgPickOptions =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listOrgPickOptionByConfigType", params);
    return orgPickOptions;
  }

  public List<OrgPickOption> listOrgPickOptionByConfigTypeForUpdate(long orgId, int configType) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("configType", configType);
    params.put("forUpdate", 1);
    List<OrgPickOption> orgPickOptions =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listOrgPickOptionByConfigType", params);
    return orgPickOptions;
  }

  public List<OrgPickOption> listOrgPickOptionByOrgIdAndOrgPickOptionIds(long orgId, List<Long> orgPickOptionIds) {
    List<OrgPickOption> orgPickOptions = Collections.EMPTY_LIST;
    if (!CollectionUtils.isEmpty(orgPickOptionIds)) {
      List<Long> uniqueOrgPickOptionIds = new ArrayList<>(new HashSet<>(orgPickOptionIds));
      Map<String, Object> params = new HashMap<>();
      params.put("orgId", orgId);
      params.put("orgPickOptionIds", uniqueOrgPickOptionIds);
      orgPickOptions =
          sqlSessionTemplate.selectList(BASE_PACKAGE + "listOrgPickOptionByOrgIdAndOrgPickOptionIds", params);
    }
    return orgPickOptions;
  }

  /**
   * Editable fields:
   *  1) optionValue
   *  2) optionIndex
   *  3) isDefault
   *  4) isDeprecated
   *  5) isDeleted
   *
   * @param orgPickOptions
   * @return
   */
  public int batchUpdateOrgPickOption(List<OrgPickOption> orgPickOptions) {
    List<OrgPickOption> cleanOrgPickOptions = new ArrayList<>();
    for(OrgPickOption orgPickOption: orgPickOptions) {
      OrgPickOption cleanOrgPickOption = new OrgPickOption();
      cleanOrgPickOption.setOrgPickOptionId(orgPickOption.getOrgPickOptionId());
      cleanOrgPickOption.setOrgId(orgPickOption.getOrgId());
      cleanOrgPickOption.setOptionValue(orgPickOption.getOptionValue());
      cleanOrgPickOption.setOptionIndex(orgPickOption.getOptionIndex());
      cleanOrgPickOption.setIsDefault(orgPickOption.getIsDefault());
      cleanOrgPickOption.setLastModifiedUserId(orgPickOption.getLastModifiedUserId());
      cleanOrgPickOptions.add(cleanOrgPickOption);
    }
    return sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateOrgPickOptionByPrimaryKeySelective", orgPickOptions);
  }

  public int batchDeprecateOrgPickOptionByPrimaryKey(long orgId, List<Long> orgPickOptionIds, long actorUserId) {
    List<OrgPickOption> orgPickOptions = new ArrayList<>();
    for (Long orgPickOptionId: orgPickOptionIds) {
      OrgPickOption orgPickOption = new OrgPickOption();
      orgPickOption.setOrgPickOptionId(orgPickOptionId);
      orgPickOption.setOrgId(orgId);
      orgPickOption.setIsDeprecated(1);
      orgPickOption.setLastModifiedUserId(actorUserId);
      orgPickOptions.add(orgPickOption);
    }
    return sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateOrgPickOptionByPrimaryKeySelective", orgPickOptions);
  }

}
