// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.client.userorg.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.user.client.userorg.dto.OrgDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-16
 */
@ThriftService
public interface OrgFacade {

  @ThriftMethod
  OrgDTO getOrg(long orgId, long actorUserId, long adminUserId);

  @ThriftMethod
  VoidDTO updateOrg(long orgId, OrgDTO orgDTO, long actorUserId, long adminUserId);

}
