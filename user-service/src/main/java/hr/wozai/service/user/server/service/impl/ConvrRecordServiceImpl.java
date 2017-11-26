// Copyright (C) 2016 Shanqian
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.dao.conversation.ConvrRecordDao;
import hr.wozai.service.user.server.dao.conversation.ConvrScheduleDao;
import hr.wozai.service.user.server.helper.ConvrRecordHelper;
import hr.wozai.service.user.server.model.conversation.ConvrRecord;
import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import hr.wozai.service.user.server.service.ConvrRecordService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
@Service("convrRecordService")
public class ConvrRecordServiceImpl implements ConvrRecordService {

  private static Logger LOGGER = LoggerFactory.getLogger(ConvrScheduleServiceImpl.class);

  @Autowired
  ConvrRecordDao convrRecordDao;

  @Autowired
  ConvrScheduleDao convrScheduleDao;

  @PostConstruct
  public void init(){}

  @Override
  @LogAround
  public long addConvrRecord(ConvrRecord convrRecord) {

    if (!ConvrRecordHelper.isValidAddRequest(convrRecord)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    return convrRecordDao.insertConvrRecord(convrRecord);
  }

  @Override
  @LogAround
  public ConvrRecord getConvrRecord(long orgId, long convrRecordId) {

    ConvrRecord convrRecord = convrRecordDao.findConvrRecordByPrimaryKeyAndOrgId(convrRecordId, orgId);
    if (null == convrRecord) {
      throw new ServiceStatusException(ServiceStatus.COMMON_NOT_FOUND);
    }

    return convrRecord;
  }

  @Override
  @LogAround
  public List<ConvrRecord> listConvrRecordByTargetUserId(long orgId, long targetUserId, int pageNumber, int pageSize) {

    List<ConvrSchedule> convrSchedules = convrScheduleDao.listConvrScheduleByTargetUserIdAndOrgId(targetUserId, orgId);
    if (CollectionUtils.isEmpty(convrSchedules)) {
      return Collections.EMPTY_LIST;
    }
    List<Long> convrScheduleIds = new ArrayList<>();
    for (ConvrSchedule convrSchedule: convrSchedules) {
      convrScheduleIds.add(convrSchedule.getConvrScheduleId());
    }
    return convrRecordDao.listConvrRecordByConvrScheduleIdAndOrgId(convrScheduleIds, pageNumber, pageSize, orgId);
  }

  @Override
  @LogAround
  public int countConvrRecordByTargetUserId(long orgId, long targetUserId) {

    List<ConvrSchedule> convrSchedules = convrScheduleDao.listConvrScheduleByTargetUserIdAndOrgId(targetUserId, orgId);
    if (CollectionUtils.isEmpty(convrSchedules)) {
      return 0;
    }
    List<Long> convrScheduleIds = new ArrayList<>();
    for (ConvrSchedule convrSchedule: convrSchedules) {
      convrScheduleIds.add(convrSchedule.getConvrScheduleId());
    }
    return convrRecordDao.countConvrRecordByConvrScheduleIdAndOrgId(convrScheduleIds, orgId);

  }

  /**
   *
   * @param orgId
   * @param sourceUserId
   * @param targetUserId
   * @param pageNumber
   * @param pageSize
   * @return
   */
  @Override
  @LogAround
  public List<ConvrRecord> listAllConvrRecordIncludingSourceUserId(
      long orgId, long sourceUserId, long targetUserId, int pageNumber, int pageSize) {

    // get both scheduleIds
    ConvrSchedule scheduleOfSourceUser =
        convrScheduleDao.findConvrScheduleBySourceUserIdAndTargetUserIdAndOrgId(sourceUserId, targetUserId, orgId);
    ConvrSchedule scheduleOfTargetUser =
        convrScheduleDao.findConvrScheduleBySourceUserIdAndTargetUserIdAndOrgId(targetUserId, sourceUserId, orgId);
    List<Long> convrScheduleIds = new ArrayList<>();
    if (null != scheduleOfSourceUser) {
      convrScheduleIds.add(scheduleOfSourceUser.getConvrScheduleId());
    }
    if (null != scheduleOfTargetUser) {
      convrScheduleIds.add(scheduleOfTargetUser.getConvrScheduleId());
    }
    if (CollectionUtils.isEmpty(convrScheduleIds)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    // list records
    return convrRecordDao.listConvrRecordByConvrScheduleIdAndOrgId(convrScheduleIds, pageNumber, pageSize, orgId);
  }

  @Override
  @LogAround
  public int countAllConvrRecordIncludingSourceUserId(long orgId, long sourceUserId, long targetUserId) {

    // get both scheduleIds
    ConvrSchedule scheduleOfSourceUser =
        convrScheduleDao.findConvrScheduleBySourceUserIdAndTargetUserIdAndOrgId(sourceUserId, targetUserId, orgId);
    ConvrSchedule scheduleOfTargetUser =
        convrScheduleDao.findConvrScheduleBySourceUserIdAndTargetUserIdAndOrgId(targetUserId, sourceUserId, orgId);
    List<Long> convrScheduleIds = new ArrayList<>();
    if (null != scheduleOfSourceUser) {
      convrScheduleIds.add(scheduleOfSourceUser.getConvrScheduleId());
    }
    if (null != scheduleOfTargetUser) {
      convrScheduleIds.add(scheduleOfTargetUser.getConvrScheduleId());
    }
    if (CollectionUtils.isEmpty(convrScheduleIds)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    // list records
    return convrRecordDao.countConvrRecordByConvrScheduleIdAndOrgId(convrScheduleIds, orgId);
  }

  @Override
  @LogAround
  public void updateConvrRecord(ConvrRecord convrRecord) {

    if (!ConvrRecordHelper.isValidUpdateRequest(convrRecord)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    convrRecordDao.updateConvrRecordByPrimaryKeyAndOrgIdSelective(convrRecord);
  }
}
