// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service;

import hr.wozai.service.user.server.model.userorg.Org;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-16
 */
public interface OrgService {

  long addOrg(Org org);

  Org getOrg(long orgId);

  void updateOrg(Org org);

  void deleteOrg(long orgId, long actorUserId);

}
