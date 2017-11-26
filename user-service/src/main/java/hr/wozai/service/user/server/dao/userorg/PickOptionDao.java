// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.PickOption;

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
import java.util.Set;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-04
 */
@Repository("pickOptionDao")
public class PickOptionDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.PickOptionMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public int batchInsertPickOption(List<PickOption> pickOptions) {
    return sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertPickOption", pickOptions);
  }

  public List<PickOption> listPickOptionByProfileFieldId(long orgId, long profileFieldId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("profileFieldId", profileFieldId);
    List<PickOption> pickOptions =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listPickOptionByProfileFieldId", params);
    return pickOptions;
  }

  public List<PickOption> listPickOptionByProfileFieldIdForUpdate(long orgId, long profileFieldId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("profileFieldId", profileFieldId);
    params.put("forUpdate", 1);
    List<PickOption> pickOptions =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listPickOptionByProfileFieldId", params);
    return pickOptions;
  }

  public List<PickOption> listPickOptionByOrgIdAndPickOptionIds(long orgId, List<Long> pickOptionIds) {
    List<PickOption> pickOptions = Collections.EMPTY_LIST;
    if (!CollectionUtils.isEmpty(pickOptionIds)) {
      List<Long> uniquePickOptionIds = new ArrayList<>(new HashSet<>(pickOptionIds));
      Map<String, Object> params = new HashMap<>();
      params.put("orgId", orgId);
      params.put("pickOptionIds", uniquePickOptionIds);
      pickOptions = sqlSessionTemplate.selectList(BASE_PACKAGE + "listPickOptionByOrgIdAndPickOptionIds", params);
    }
    return pickOptions;
  }

  /**
   * Editable fields:
   *  1) optionValue
   *  2) optionIndex
   *  3) isDefault
   *
   * @param pickOptions
   * @return
   */
  public int batchUpdatePickOption(List<PickOption> pickOptions) {
    List<PickOption> cleanPickOptions = new ArrayList<>();
    for(PickOption pickOption: pickOptions) {
      PickOption cleanPickOption = new PickOption();
      cleanPickOption.setPickOptionId(pickOption.getPickOptionId());
      cleanPickOption.setOrgId(pickOption.getOrgId());
      cleanPickOption.setOptionValue(pickOption.getOptionValue());
      cleanPickOption.setOptionIndex(pickOption.getOptionIndex());
      cleanPickOption.setIsDefault(pickOption.getIsDefault());
      cleanPickOption.setLastModifiedUserId(pickOption.getLastModifiedUserId());
      cleanPickOptions.add(cleanPickOption);
    }
    return sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdatePickOptionByPrimaryKeySelective", cleanPickOptions);
  }

  public int batchDeprecatePickOptionByPrimaryKey(long orgId, List<Long> pickOptionIds, long actorUserId) {
    List<PickOption> pickOptions = new ArrayList<>();
    for (Long pickOptionId: pickOptionIds) {
      PickOption pickOption = new PickOption();
      pickOption.setPickOptionId(pickOptionId);
      pickOption.setOrgId(orgId);
      pickOption.setIsDeprecated(1);
      pickOption.setLastModifiedUserId(actorUserId);
      pickOptions.add(pickOption);
    }
    return sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdatePickOptionByPrimaryKeySelective", pickOptions);
  }

  public int batchDeletePickOptionByProfileFieldId(long orgId, long profileFieldId, long actorUserId) {
    Map<String, Object> params = new HashMap<>();
    params.put("orgId", orgId);
    params.put("profileFieldId", profileFieldId);
    params.put("lastModifiedUserId", actorUserId);
    return sqlSessionTemplate.update(BASE_PACKAGE + "batchDeletePickOptionByProfileFieldId", params);
  }

}
