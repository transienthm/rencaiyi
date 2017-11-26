// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.user.client.conversation.dto.*;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.server.model.conversation.ConvrScheduleChart;
import hr.wozai.service.user.server.model.conversation.ConvrSourceUserChart;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.service.ConvrScheduleChartService;
import hr.wozai.service.user.server.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongListDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.conversation.enums.CurrentPeriodStatus;
import hr.wozai.service.user.client.conversation.enums.PeriodType;
import hr.wozai.service.user.client.conversation.facade.ConvrFacade;
import hr.wozai.service.user.client.conversation.utils.ConvrUtils;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.model.conversation.ConvrRecord;
import hr.wozai.service.user.server.model.conversation.ConvrSchedule;
import hr.wozai.service.user.server.service.ConvrRecordService;
import hr.wozai.service.user.server.service.ConvrScheduleService;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-11-28
 */
@Service("convrFacade")
public class ConvrFacadeImpl implements ConvrFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(ConvrFacadeImpl.class);

  @Autowired
  ConvrScheduleService convrScheduleService;

  @Autowired
  ConvrRecordService convrRecordService;

  @Autowired
  ConvrScheduleChartService convrScheduleChartService;

  @Autowired
  UserProfileService userProfileService;

  @Override
  @LogAround
  public LongDTO addConvrSchedule(long orgId, ConvrScheduleDTO convrScheduleDTO, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ConvrSchedule convrSchedule = new ConvrSchedule();
      BeanUtils.copyProperties(convrScheduleDTO, convrSchedule);
      convrSchedule.setOrgId(orgId);
      convrSchedule.setSourceUserId(actorUserId);
      convrSchedule.setCreatedUserId(actorUserId);
      long convrScheduleId = convrScheduleService.addConvrSchedule(convrSchedule);
      result.setData(convrScheduleId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addConvrSchedule()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public ConvrScheduleDTO getConvrSchedule(long orgId, long convrScheduleId, long actorUserId, long adminUserId) {

    ConvrScheduleDTO result = new ConvrScheduleDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ConvrSchedule convrSchedule = convrScheduleService.findConvrSchedule(convrScheduleId, orgId);
      BeanUtils.copyProperties(convrSchedule, result);
      // TODO: remove psuedo code
      result.setConvrCount(8);
      result.setCurrentPeriodStatus(CurrentPeriodStatus.COMPLETE_BY_SELF.getCode());
      result.setLastConvrDate(TimeUtils.getTimestampOfZeroOclockTodayOfInputTimestampInBeijingTime(
          TimeUtils.getNowTimestmapInMillis() - 86400 * 1000 * 2));
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getConvrSchedule()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public ConvrScheduleListDTO listConvrScheduleBySourceUserId(
      long orgId, long sourceUserId, int pageNumber, int pageSize, long actorUserId, long adminUserId) {

    ConvrScheduleListDTO result = new ConvrScheduleListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ConvrSchedule> convrSchedules =
          convrScheduleService.listConvrScheduleBySourceUserId(sourceUserId, pageNumber, pageSize, orgId);
      List<ConvrScheduleDTO> convrScheduleDTOs = new ArrayList<>();
      for (ConvrSchedule convrSchedule : convrSchedules) {
        ConvrScheduleDTO convrScheduleDTO = new ConvrScheduleDTO();
        BeanUtils.copyProperties(convrSchedule, convrScheduleDTO);
        Long lastConvrDate = convrSchedule.getLastConvrDate();
        if (null != lastConvrDate
            && ConvrUtils.isInCurrentPeriod(PeriodType.getEnumByCode(convrScheduleDTO.getPeriodType().intValue()),
                                            TimeUtils.getNowTimestmapInMillis())) {
          convrScheduleDTO.setCurrentPeriodStatus(CurrentPeriodStatus.COMPLETE_BY_SELF.getCode());
        } else {
          convrSchedule.setLastConvrDate(null);
          convrScheduleDTO.setCurrentPeriodStatus(CurrentPeriodStatus.INCOMPLETE.getCode());
        }
        convrScheduleDTOs.add(convrScheduleDTO);
      }
      result.setConvrScheduleDTOs(convrScheduleDTOs);
      int totalNumber = convrScheduleService.countConvrScheduleBySourceUserId(sourceUserId, orgId);
      result.setTotalNumber(totalNumber);

      // TODO: fill in other fields

    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listConvrScheduleBySourceUserId()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateConvrSchedule(
      long orgId, ConvrScheduleDTO convrScheduleDTO, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ConvrSchedule convrSchedule = new ConvrSchedule();
      BeanUtils.copyProperties(convrScheduleDTO, convrSchedule);
      convrSchedule.setOrgId(orgId);
      convrSchedule.setLastModifiedUserId(actorUserId);
      convrScheduleService.updateConvrSchedule(convrSchedule);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateConvrSchedule()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public LongListDTO listTargetUserIdsOfSourceUser(long orgId, long sourceUserId, long actorUserId, long adminUserId) {

    LongListDTO result = new LongListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Long> targetUserIds = convrScheduleService.listTargetUserIdBySourceUserId(sourceUserId, orgId);
      result.setData(targetUserIds);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listTargetUserIdsOfSourceUser()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public LongDTO addConvrRecord(long orgId, ConvrRecordDTO convrRecordDTO, long actorUserId, long adminUserId) {

    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ConvrRecord convrRecord = new ConvrRecord();
      BeanUtils.copyProperties(convrRecordDTO, convrRecord);
      convrRecord.setOrgId(orgId);
      convrRecord.setCreatedUserId(actorUserId);
      long convrRecordId = convrRecordService.addConvrRecord(convrRecord);
      result.setData(convrRecordId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addConvrRecord()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public ConvrRecordDTO getConvrRecord(long orgId, long convrRecordId, long actorUserId, long amdinUserId) {

    ConvrRecordDTO result = new ConvrRecordDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ConvrRecord convrRecord = convrRecordService.getConvrRecord(orgId, convrRecordId);
      BeanUtils.copyProperties(convrRecord, result);
      ConvrSchedule convrSchedule = convrScheduleService.findConvrSchedule(convrRecord.getConvrScheduleId(), orgId);
      result.setSourceUserId(convrSchedule.getSourceUserId());
      result.setTargetUserId(convrSchedule.getTargetUserId());
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getConvrRecord()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateConvrRecord(long orgId, ConvrRecordDTO convrRecordDTO, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ConvrRecord convrRecord = new ConvrRecord();
      BeanUtils.copyProperties(convrRecordDTO, convrRecord);
      convrRecord.setOrgId(orgId);
      convrRecord.setLastModifiedUserId(actorUserId);
      convrRecordService.updateConvrRecord(convrRecord);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateConvrRecord()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public ConvrRecordListDTO listConvrRecordOfScheduleAsSourceUser(
      long orgId, long convrScheduleId, int pageNumber, int pageSize, long actorUserId, long adminUserId) {

    ConvrRecordListDTO result = new ConvrRecordListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // list all records
      ConvrSchedule convrSchedule = convrScheduleService.findConvrSchedule(convrScheduleId, orgId);
      long sourceUserId = convrSchedule.getSourceUserId();
      long targetUserId = convrSchedule.getTargetUserId();
      List<ConvrRecord> convrRecords = convrRecordService.listAllConvrRecordIncludingSourceUserId(
          orgId, sourceUserId, targetUserId, pageNumber, pageSize);
      // set sourceUserId & targetUserId
      List<ConvrRecordDTO> convrRecordDTOs = new ArrayList<>();
      if (!CollectionUtils.isEmpty(convrRecords)) {
        Set<Long> scheduleIds = new HashSet<>();
        for (ConvrRecord convrRecord: convrRecords) {
          scheduleIds.add(convrRecord.getConvrScheduleId());
          ConvrRecordDTO convrRecordDTO = new ConvrRecordDTO();
          BeanUtils.copyProperties(convrRecord, convrRecordDTO);
          convrRecordDTOs.add(convrRecordDTO);
        }
        List<ConvrSchedule> convrSchedules =
            convrScheduleService.listConvrScheduleByPrimaryKey(new ArrayList<>(scheduleIds), orgId);
        Map<Long, ConvrSchedule> convrScheduleMap = convertConvrScheduleListToMap(convrSchedules);
        for (ConvrRecordDTO convrRecordDTO: convrRecordDTOs) {
          long theConvrScheduleId = convrRecordDTO.getConvrScheduleId();
          if (convrScheduleMap.containsKey(theConvrScheduleId)) {
            ConvrSchedule theConvrSchedule = convrScheduleMap.get(theConvrScheduleId);
            convrRecordDTO.setSourceUserId(theConvrSchedule.getSourceUserId());
            convrRecordDTO.setTargetUserId(theConvrSchedule.getTargetUserId());
          }
        }
      }
      //  get totalNumber
      int totalNumber = convrRecordService.countAllConvrRecordIncludingSourceUserId(orgId, sourceUserId, targetUserId);
      result.setConvrRecordDTOs(convrRecordDTOs);
      result.setTotalNumber(totalNumber);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listConvrRecordOfScheduleAsSourceUser()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public ConvrRecordListDTO listConvrRecordByTargetUserId(
      long orgId, long targetUserId, int pageNumber, int pageSize, long actorUserId, long adminUserId) {

    ConvrRecordListDTO result = new ConvrRecordListDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ConvrRecord> convrRecords =
          convrRecordService.listConvrRecordByTargetUserId(orgId, targetUserId, pageNumber, pageSize);
      List<ConvrRecordDTO> convrRecordDTOs = new ArrayList<>();
      if (!CollectionUtils.isEmpty(convrRecords)) {
        List<ConvrSchedule> convrSchedules =
            convrScheduleService.listAllConvrScheduleByTargetUserId(targetUserId, orgId);
        Map<Long, ConvrSchedule> convrScheduleMap = new HashMap<>();
        for (ConvrSchedule convrSchedule: convrSchedules) {
          convrScheduleMap.put(convrSchedule.getConvrScheduleId(), convrSchedule);
        }
        for (ConvrRecord convrRecord: convrRecords) {
          ConvrRecordDTO convrRecordDTO = new ConvrRecordDTO();
          BeanUtils.copyProperties(convrRecord, convrRecordDTO);
          long theConvrScheduleId = convrRecordDTO.getConvrScheduleId();
          if (convrScheduleMap.containsKey(theConvrScheduleId)) {
            ConvrSchedule theConvrSchedule = convrScheduleMap.get(theConvrScheduleId);
            convrRecordDTO.setSourceUserId(theConvrSchedule.getSourceUserId());
            convrRecordDTO.setTargetUserId(theConvrSchedule.getTargetUserId());
          }
          convrRecordDTOs.add(convrRecordDTO);
        }
      }
      result.setConvrRecordDTOs(convrRecordDTOs);
      int totalNumber = convrRecordService.countConvrRecordByTargetUserId(orgId, targetUserId);
      result.setTotalNumber(totalNumber);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listConvrRecordByTargetUserId()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public ConvrScheduleChartListDTO listConvrScheduleChartByOrgId(long orgId, int period, long actorUserId, long adminUserId) {
    ConvrScheduleChartListDTO result = new ConvrScheduleChartListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ConvrScheduleChart> convrScheduleCharts = convrScheduleChartService.listConvrScheduleChartByOrgId(period, orgId);
      List<ConvrScheduleChartDTO> convrScheduleChartDTOs = new ArrayList<>();
      if (!CollectionUtils.isEmpty(convrScheduleCharts)) {
        for (ConvrScheduleChart convrScheduleChart : convrScheduleCharts) {
          ConvrScheduleChartDTO convrScheduleChartDTO = new ConvrScheduleChartDTO();
          BeanUtils.copyProperties(convrScheduleChart, convrScheduleChartDTO);
          convrScheduleChartDTOs.add(convrScheduleChartDTO);
        }
      }
      result.setConvrScheduleChartDTOList(convrScheduleChartDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listConvrScheduleChartByOrgId()-error", e);
    }
    return result;
  }

  @Override
  @LogAround
  public ConvrScheduleChartDTO getConvrScheduleChartInAMonth(long orgId, long actorUserId, long adminUserId) {
    ConvrScheduleChartDTO result = new ConvrScheduleChartDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);
    try {
      ConvrScheduleChart convrScheduleChart = convrScheduleChartService.getConvrScheduleChartInAMonth(orgId);
      BeanUtils.copyProperties(convrScheduleChart, result);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getConvrScheduleChartInAMonth-error()", e);
    }
    return result;
  }

  @Override
  @LogAround
  public ConvrSourceChartListDTO listConvrSourceChartListDTO(long orgId, int pageNumber, int pageSize, long actorUserId, long adminUserId) {
    ConvrSourceChartListDTO result = new ConvrSourceChartListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ConvrSourceUserChart> convrSourceUserCharts = convrScheduleChartService.listConvrSourceUserChart(orgId, pageNumber, pageSize);
      List<ConvrSourceChartDTO> convrSourceChartDTOs = new ArrayList<>();
      Set<Long> userIds = new HashSet<>();
      Map<Long, ConvrSourceChartDTO> userIdAndChartMap = new HashMap<>();

      for (ConvrSourceUserChart convrSourceUserChart : convrSourceUserCharts) {
        ConvrSourceChartDTO convrSourceChartDTO = new ConvrSourceChartDTO();
        BeanUtils.copyProperties(convrSourceUserChart, convrSourceChartDTO);
        long userId = convrSourceUserChart.getSourceUserId();
        userIds.add(userId);
        userIdAndChartMap.put(userId, convrSourceChartDTO);
        convrSourceChartDTOs.add(convrSourceChartDTO);
      }

      List<CoreUserProfile> coreUserProfiles = userProfileService.listCoreUserProfileByOrgIdAndUserId(orgId, new ArrayList<>(userIds));
      for (CoreUserProfile coreUserProfile : coreUserProfiles) {
        CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
        BeanUtils.copyProperties(coreUserProfile, coreUserProfileDTO);
        long userId = coreUserProfile.getUserId();
        if (userIdAndChartMap.containsKey(userId)) {
          ConvrSourceChartDTO convrSourceChartDTO = userIdAndChartMap.get(userId);
          convrSourceChartDTO.setSourceUser(coreUserProfileDTO);
        }
      }

      long totalNumber = convrSourceUserCharts.size();
      result.setTotalNumber(totalNumber);

      result.setConvrSourceChartDTOs(convrSourceChartDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("listConvrSourceChartListDTO-error()", e);
    }
    return result;
  }

  private static Map<Long, ConvrSchedule> convertConvrScheduleListToMap(List<ConvrSchedule> convrSchedules) {
    Map<Long, ConvrSchedule> convrScheduleMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(convrSchedules)) {
      for (ConvrSchedule convrSchedule: convrSchedules) {
        convrScheduleMap.put(convrSchedule.getConvrScheduleId(), convrSchedule);
      }
    }
    return convrScheduleMap;
  }
}
