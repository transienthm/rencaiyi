// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.service;

import java.util.List;

import hr.wozai.service.user.server.model.conversation.ConvrRecord;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
public interface ConvrRecordService {

  long addConvrRecord(ConvrRecord convrRecord);

  ConvrRecord getConvrRecord(long orgId, long convrRecordId);

  List<ConvrRecord> listAllConvrRecordIncludingSourceUserId(
      long orgId, long sourceUserId, long targetUserId, int pageNumber, int pageSize);

  int countAllConvrRecordIncludingSourceUserId(long orgId, long sourceUserId, long targetUserId);

  List<ConvrRecord> listConvrRecordByTargetUserId(long orgId, long targetUserId, int pageNumber, int pageSize);

  int countConvrRecordByTargetUserId(long orgId, long targetUserId);

  void updateConvrRecord(ConvrRecord convrRecord);

}
