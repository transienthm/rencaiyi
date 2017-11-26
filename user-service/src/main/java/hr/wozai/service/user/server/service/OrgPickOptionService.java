// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.service;

import java.util.List;

import hr.wozai.service.user.server.model.userorg.OrgPickOption;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-07-26
 */
public interface OrgPickOptionService {

  void initJobTitleAndJobLevelOfOrg(long orgId);

  List<OrgPickOption> listPickOptionOfConfigType(long orgId, int configType);

  void batchUpdateOrgPickOptions(long orgId, List<OrgPickOption> orgPickOptions, long actorUserId);

}
