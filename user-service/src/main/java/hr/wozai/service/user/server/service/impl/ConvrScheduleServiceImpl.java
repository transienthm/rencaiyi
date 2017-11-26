// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.server.dao.conversation.ConvrScheduleDao;
import hr.wozai.service.user.server.dao.document.DocumentDao;
import hr.wozai.service.user.server.helper.ConvrScheduleHelper;
import hr.wozai.service.user.server.helper.DocumentHelper;
import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import hr.wozai.service.user.server.model.document.Document;
import hr.wozai.service.user.server.service.ConvrScheduleService;
import hr.wozai.service.user.server.service.DocumentService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-07
 */
@Service("convrScheduleService")
public class ConvrScheduleServiceImpl implements ConvrScheduleService {

  private static Logger LOGGER = LoggerFactory.getLogger(ConvrScheduleServiceImpl.class);

  @Autowired
  ConvrScheduleDao convrScheduleDao;

  @PostConstruct
  public void init(){}


  @Override
  @LogAround
  public long addConvrSchedule(ConvrSchedule convrSchedule) {

    if (!ConvrScheduleHelper.isValidAddRequest(convrSchedule)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    return convrScheduleDao.insertConvrSchedule(convrSchedule);
  }

  @Override
  @LogAround
  public ConvrSchedule findConvrSchedule(long convrScheduleId, long orgId) {

    ConvrSchedule convrSchedule = convrScheduleDao.findConvrScheduleByPrimaryKeyAndOrgId(convrScheduleId, orgId);
    if (null == convrSchedule) {
      throw new ServiceStatusException(ServiceStatus.COMMON_NOT_FOUND);
    }

    return convrSchedule;
  }

  @Override
  @LogAround
  public List<ConvrSchedule> listConvrScheduleByPrimaryKey(List<Long> convrScheduleIds, long orgId) {
    return convrScheduleDao.listConvrScheduleByPrimaryKeyAndOrgId(convrScheduleIds, orgId);
  }

  @Override
  @LogAround
  public List<ConvrSchedule> listConvrScheduleBySourceUserId(
      long sourceUserId, int pageNumber, int pageSize, long orgId) {
    List<ConvrSchedule> convrSchedules =
        convrScheduleDao.listConvrScheduleByOrgIdAndSourceUserId(sourceUserId, pageNumber, pageSize, orgId);
    if (!CollectionUtils.isEmpty(convrSchedules)) {
      List<Long> convrScheduleIds = new ArrayList<>();
      for (ConvrSchedule convrSchedule: convrSchedules) {
        convrScheduleIds.add(convrSchedule.getConvrScheduleId());
      }
      List<ConvrSchedule> convrScheduleStats =
          convrScheduleDao.listConvrScheduleStatByPrimaryKeyAndOrgId(convrScheduleIds, orgId);
      Map<Long, ConvrSchedule> convrScheduleMap = new HashMap<>();
      for (ConvrSchedule convrSchedule: convrScheduleStats) {
        convrScheduleMap.put(convrSchedule.getConvrScheduleId(), convrSchedule);
      }
      for (ConvrSchedule convrSchedule: convrSchedules) {
        long convrScheduleId = convrSchedule.getConvrScheduleId();
        if (convrScheduleMap.containsKey(convrScheduleId)) {
          ConvrSchedule convrScheduleStat = convrScheduleMap.get(convrScheduleId);
          convrSchedule.setConvrCount(convrScheduleStat.getConvrCount());
          convrSchedule.setLastConvrDate(convrScheduleStat.getLastConvrDate());
        } else {
          convrSchedule.setConvrCount(0);
          convrSchedule.setLastConvrDate(null);
        }
      }
    }



    return convrSchedules;
  }

  @Override
  @LogAround
  public int countConvrScheduleBySourceUserId(long sourceUserId, long orgId) {
    return convrScheduleDao.countConvrScheduleByOrgIdAndSourceUserId(sourceUserId, orgId);
  }

  @Override
  @LogAround
  public List<ConvrSchedule> listAllConvrScheduleByTargetUserId(long targetUserId, long orgId) {
    return convrScheduleDao.listConvrScheduleByTargetUserIdAndOrgId(targetUserId, orgId);
  }

  @Override
  @LogAround
  public List<Long> listTargetUserIdBySourceUserId(long sourceUserId, long orgId) {
    return convrScheduleDao.listTargetUserIdsBySourceUserIdAndOrgId(sourceUserId, orgId);
  }

  @Override
  @LogAround
  public void updateConvrSchedule(ConvrSchedule convrSchedule) {

    if (!ConvrScheduleHelper.isValidUpdateRequest(convrSchedule)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    convrScheduleDao.updateConvrScheduleByPrimaryKeyAndOrgIdSelective(convrSchedule);
  }

}
