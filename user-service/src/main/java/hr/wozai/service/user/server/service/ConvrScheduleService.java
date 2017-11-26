// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service;

import java.util.List;

import hr.wozai.service.user.server.model.conversation.ConvrSchedule;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
public interface ConvrScheduleService {

  long addConvrSchedule(ConvrSchedule convrSchedule);

  ConvrSchedule findConvrSchedule(long convrScheduleId, long orgId);

  List<ConvrSchedule> listConvrScheduleByPrimaryKey(List<Long> convrScheduleIds, long orgId);

  List<ConvrSchedule> listConvrScheduleBySourceUserId(long sourceUserId, int pageStart, int pageSize, long orgId);

  int countConvrScheduleBySourceUserId(long sourceUserId, long orgId);

  List<ConvrSchedule> listAllConvrScheduleByTargetUserId(long targetUserId, long orgId);

  List<Long> listTargetUserIdBySourceUserId(long sourceUserId, long orgId);

  void updateConvrSchedule(ConvrSchedule convrSchedule);

}
