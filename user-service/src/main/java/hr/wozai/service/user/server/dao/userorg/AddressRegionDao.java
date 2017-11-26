// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.dao.userorg;

import hr.wozai.service.user.server.model.userorg.AddressRegion;

import org.apache.commons.collections.CollectionUtils;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-14
 */
@Repository("addressRegionDao")
public class AddressRegionDao {

  private static final String BASE_PACKAGE = "hr.wozai.service.user.server.dao.userorg.AddressRegionMapper.";

  @Autowired
  SqlSessionTemplate sqlSessionTemplate;

  public List<AddressRegion> listAddressRegionByParentId(long parentId) {
    List<AddressRegion> addressRegions =
        sqlSessionTemplate.selectList(BASE_PACKAGE + "listAddressRegionByParentId", parentId);
    if (CollectionUtils.isEmpty(addressRegions)) {
      addressRegions = Collections.EMPTY_LIST;
    }
    return addressRegions;
  }

}
