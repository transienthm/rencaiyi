package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.survey.dto.*;
import hr.wozai.service.user.client.survey.facade.SurveyFacade;
import hr.wozai.service.user.client.survey.enums.SurveyItemAttribute;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.helper.SurveyHelper;
import hr.wozai.service.user.server.model.survey.SurveyActivity;
import hr.wozai.service.user.server.model.survey.SurveyConfig;
import hr.wozai.service.user.server.model.survey.SurveyItem;
import hr.wozai.service.user.server.model.survey.SurveyResponse;
import hr.wozai.service.user.server.service.SurveyService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/28
 */
@Service("surveyFacadeImpl")
public class SurveyFacadeImpl implements SurveyFacade {
  private static final Logger LOGGER = LoggerFactory.getLogger(SurveyFacadeImpl.class);

  @Autowired
  private SurveyService surveyService;

  @Override
  @LogAround
  public LongDTO addSurveyItem(long orgId, SurveyItemDTO surveyItemDTO, long actorUserId, long adminUserId) {
    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      SurveyItem surveyItem = new SurveyItem();
      BeanUtils.copyProperties(surveyItemDTO, surveyItem);
      surveyItem.setOrgId(orgId);
      surveyItem.setCreatedUserId(actorUserId);

      long itemId = surveyService.insertSurveyItem(surveyItem);
      result.setData(itemId);
    } catch (Exception e) {
      LOGGER.error("addSurveyItem-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO deleteSurveyItem(long orgId, long surveyItemId, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      surveyService.deleteSurveyItemByPrimaryKey(orgId, surveyItemId, actorUserId);
    } catch (Exception e) {
      LOGGER.error("deleteSurveyItem-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateSurveyItem(long orgId, SurveyItemDTO surveyItemDTO, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      SurveyItem surveyItem = new SurveyItem();
      BeanUtils.copyProperties(surveyItemDTO, surveyItem);
      surveyItem.setOrgId(orgId);
      surveyItem.setLastModifiedUserId(actorUserId);

      surveyService.updateSurveyItem(surveyItem);
    } catch (Exception e) {
      LOGGER.error("updateSurveyItem-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public SurveyItemListDTO listSurveyItemsByOrgId(long orgId, int pageNumber, int pageSize, long actorUserId, long adminUserId) {
    SurveyItemListDTO result = new SurveyItemListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<SurveyItem> surveyItems = surveyService.listAvailableSurveyItemsByOrgIdAndTimestamp(
              orgId, 0L, pageNumber, pageSize);
      List<SurveyItemDTO> surveyItemDTOs = new ArrayList<>();
      long curTs = System.currentTimeMillis();
      for (SurveyItem surveyItem : surveyItems) {
        SurveyItemDTO surveyItemDTO = new SurveyItemDTO();
        BeanUtils.copyProperties(surveyItem, surveyItemDTO);
        surveyItemDTO.setStatus(surveyItem.getStartTime() > curTs ? 1:surveyItem.getEndTime() >= curTs ? 2 : 3);
        surveyItemDTO.setDeletable(surveyService.countSurveyResponseBySurveyItemId(
                orgId, surveyItem.getSurveyItemId()) <= 0);
        surveyItemDTOs.add(surveyItemDTO);
      }
      result.setSurveyItemDTOs(surveyItemDTOs);
      result.setTotalNumber(surveyService.countSurveyItemsByOrgId(orgId));
    } catch (Exception e) {
      LOGGER.error("listSurveyItemsByOrgId-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public SurveyItemListDTO listSurveyItemsByStartAndEndTime(long orgId, long startTime, long endTime, long actorUserId, long adminUserId) {
    SurveyItemListDTO result = new SurveyItemListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<SurveyItem> surveyItems = surveyService.listSamePeriodSurveyItemsByOrgId(
              orgId, startTime, endTime);
      List<SurveyItemDTO> surveyItemDTOs = new ArrayList<>();
      for (SurveyItem surveyItem : surveyItems) {
        SurveyItemDTO surveyItemDTO = new SurveyItemDTO();
        BeanUtils.copyProperties(surveyItem, surveyItemDTO);
        surveyItemDTOs.add(surveyItemDTO);
      }
      result.setSurveyItemDTOs(surveyItemDTOs);
    } catch (Exception e) {
      LOGGER.error("listSurveyItemsByStartAndEndTime-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public SurveyConfigDTO getSurveyConfig(long orgId, long actorUserId, long adminUserId) {
    SurveyConfigDTO result = new SurveyConfigDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      SurveyConfig surveyConfig = surveyService.getSurveyConfig(orgId);
      BeanUtils.copyProperties(surveyConfig, result);
    } catch (Exception e) {
      LOGGER.error("getSurveyConfig-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  public LongDTO updateSurveyConfig(long orgId, SurveyConfigDTO surveyConfigDTO, long actorUserId, long adminUserId) {
    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      SurveyConfig surveyConfig = new SurveyConfig();
      BeanUtils.copyProperties(surveyConfigDTO, surveyConfig);
      surveyConfig.setOrgId(orgId);

      SurveyConfig inDb = surveyService.getSurveyConfig(orgId);
      // 数据库中没有就插入,有的话就更新
      if (inDb.getSurveyConfigId() == null) {
        surveyConfig.setCreatedUserId(actorUserId);
        long configId = surveyService.insertSurveyConfig(surveyConfig);
        result.setData(configId);
      } else {
        surveyConfig.setSurveyConfigId(inDb.getSurveyConfigId());
        surveyConfig.setLastModifiedUserId(actorUserId);
        surveyService.updateSurveyConfig(surveyConfig);
        result.setData(surveyConfig.getSurveyConfigId());
      }

    } catch (Exception e) {
      LOGGER.error("updateSurveyConfig-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public SurveyActivityDTO getInPeriodAndUnSubmitedSurveyActivity(long orgId, long actorUserId, long adminUserId) {
    SurveyActivityDTO result = new SurveyActivityDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      SurveyConfig surveyConfig = surveyService.getSurveyConfig(orgId);
      List<SurveyActivity> surveyActivities = surveyService.listSurveyActivities(orgId, 1, Integer.MAX_VALUE);
      List<SurveyResponseDTO> surveyResponseDTOs = new ArrayList<>();
      if (!CollectionUtils.isEmpty(surveyActivities)) {
        SurveyActivity surveyActivity = surveyActivities.get(0);
        if (!SurveyHelper.checkActivityIsNotInPeriod(surveyActivity, surveyConfig)) {
          BeanUtils.copyProperties(surveyActivity, result);
          List<SurveyResponse> surveyResponses = surveyService.listSurveyResponsesByOrgIdAndActivityIdAndUserId(
                  orgId, actorUserId, surveyActivity.getSurveyActivityId());
          for (SurveyResponse surveyResponse : surveyResponses) {
            if (surveyResponse.getIsSubmit() == 1) {
              break;
            }
            SurveyResponseDTO surveyResponseDTO = new SurveyResponseDTO();
            BeanUtils.copyProperties(surveyResponse, surveyResponseDTO);
            SurveyItem surveyItem = surveyService.findSurveyItemByPrimaryKey(orgId, surveyResponse.getSurveyItemId());
            SurveyItemDTO surveyItemDTO = new SurveyItemDTO();
            BeanUtils.copyProperties(surveyItem, surveyItemDTO);
            surveyResponseDTO.setSurveyItemDTO(surveyItemDTO);
            surveyResponseDTOs.add(surveyResponseDTO);
          }
        }
      }
      if (CollectionUtils.isEmpty(surveyResponseDTOs)) {
        result.setSurveyActivityId(null);
      }
      result.setSurveyResponseDTOs(surveyResponseDTOs);
    } catch (Exception e) {
      LOGGER.error("getInPeriodAndUnSubmitedSurveyActivity-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO submitSurveyActivityByUser(long orgId, SurveyActivityDTO surveyActivityDTO, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long activityId = surveyActivityDTO.getSurveyActivityId();
      List<SurveyResponseDTO> surveyResponseDTOs = surveyActivityDTO.getSurveyResponseDTOs();
      List<SurveyResponse> surveyResponses = new ArrayList<>();
      for (SurveyResponseDTO surveyResponseDTO : surveyResponseDTOs) {
        SurveyResponse surveyResponse = new SurveyResponse();
        BeanUtils.copyProperties(surveyResponseDTO, surveyResponse);
        surveyResponse.setOrgId(orgId);
        surveyResponse.setSurveyActivityId(activityId);
        surveyResponse.setIsSubmit(1);
        surveyResponse.setLastModifiedUserId(actorUserId);
        surveyResponses.add(surveyResponse);
      }
      surveyService.batchUpdateSurveyResponses(surveyResponses);
    } catch (Exception e) {
      LOGGER.error("submitSurveyActivityByUser-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public SurveyActivityListDTO listSurveyActivitiesByOrgId(
          long orgId, int pageNumber, int pageSize, long actorUserId, long adminUserId) {
    SurveyActivityListDTO result  = new SurveyActivityListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<SurveyActivity> surveyActivities = surveyService.listSurveyActivities(orgId, pageNumber, pageSize);
      List<SurveyActivityDTO> surveyActivityDTOs = new ArrayList<>();
      for (SurveyActivity surveyActivity : surveyActivities) {
        SurveyActivityDTO surveyActivityDTO = new SurveyActivityDTO();
        BeanUtils.copyProperties(surveyActivity, surveyActivityDTO);
        surveyActivityDTOs.add(surveyActivityDTO);
      }
      result.setSurveyActivityDTOs(surveyActivityDTOs);
      result.setTotalNumber(surveyService.listSurveyActivities(orgId, 1, Integer.MAX_VALUE).size());
    } catch (Exception e) {
      LOGGER.error("listSurveyActivitiesByOrgId-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public NewSurveyActivityDTO listSurveyResponsesByOrgIdAndActivityId(
          long orgId, long surveyActivityId, long actorUserId, long adminUserId) {
    NewSurveyActivityDTO result  = new NewSurveyActivityDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      SurveyActivity surveyActivity = surveyService.getSurveyActivityByOrgIdAndPrimaryKey(orgId, surveyActivityId);
      BeanUtils.copyProperties(surveyActivity, result);

      List<NewSurveyItemDTO> newSurveyItemDTOs = new ArrayList<>();

      List<SurveyResponse> surveyResponses = surveyService.listSurveyResponsesByOrgIdAndActivityIds(
              orgId, Arrays.asList(surveyActivityId));
      Map<Long, List<SurveyResponse>> itemIdAndResponsesMap = getItemIdAndResponsesMap(surveyResponses);
      List<Long> surveyItemIds = getSurveyItemIdsFromSurveyResponses(surveyResponses);
      List<SurveyItem> surveyItems = surveyService.listSurveyItemsByOrgIdAndItemIds(orgId, surveyItemIds);
      for (SurveyItem surveyItem : surveyItems) {
        NewSurveyItemDTO surveyItemDTO = new NewSurveyItemDTO();
        BeanUtils.copyProperties(surveyItem, surveyItemDTO);
        List<NewSurveyResponseDTO> surveyResponseDTOs = new ArrayList<>();
        if (!itemIdAndResponsesMap.containsKey(surveyItem.getSurveyItemId())
                || itemIdAndResponsesMap.get(surveyItem.getSurveyItemId()).size() == 0) {
          continue;
        } else {
          List<SurveyResponse> surveyResponseList = itemIdAndResponsesMap.get(surveyItem.getSurveyItemId());
          for (SurveyResponse surveyResponse : surveyResponseList) {
            NewSurveyResponseDTO surveyResponseDTO = new NewSurveyResponseDTO();
            BeanUtils.copyProperties(surveyResponse, surveyResponseDTO);
            surveyResponseDTOs.add(surveyResponseDTO);
          }
        }
        surveyItemDTO.setSurveyResponseDTOs(surveyResponseDTOs);
        newSurveyItemDTOs.add(surveyItemDTO);
      }
      result.setNewSurveyItemDTOs(newSurveyItemDTOs);
    } catch (Exception e) {
      LOGGER.error("listSurveyResponsesByOrgIdAndActivityId-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  private Map<Long, List<SurveyResponse>> getItemIdAndResponsesMap(List<SurveyResponse> surveyResponses) {
    Map<Long, List<SurveyResponse>> result = new HashMap<>();
    for (SurveyResponse surveyResponse : surveyResponses) {
      long itemId = surveyResponse.getSurveyItemId();
      if (result.containsKey(itemId)) {
        List<SurveyResponse> responses = result.get(itemId);
        responses.add(surveyResponse);
        result.put(itemId, responses);
      } else {
        List<SurveyResponse> responses = new ArrayList<>();
        responses.add(surveyResponse);
        result.put(itemId, responses);
      }
    }
    return result;
  }

  private List<Long> getSurveyItemIdsFromSurveyResponses(List<SurveyResponse> surveyResponses) {
    Set<Long> result = new HashSet<>();
    for (SurveyResponse surveyResponse : surveyResponses) {
      result.add(surveyResponse.getSurveyItemId());
    }
    return new ArrayList<>(result);
  }

  @Override
  @LogAround
  public NewSurveyActivityDTO listSurveyResponsesByOrgIdAndActivityIdAndItemId(
          long orgId, long surveyActivityId, long surveyItemId, String keyword, int pageNumber, int pageSize,
          long actorUserId, long adminUserId) {
    NewSurveyActivityDTO result  = new NewSurveyActivityDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      SurveyActivity surveyActivity = surveyService.getSurveyActivityByOrgIdAndPrimaryKey(orgId, surveyActivityId);
      BeanUtils.copyProperties(surveyActivity, result);

      List<NewSurveyItemDTO> newSurveyItemDTOs = new ArrayList<>();

      List<SurveyResponse> surveyResponses = surveyService.searchResponsesByOrgIdAndActivityIdAndItemId(
              orgId, surveyActivityId, surveyItemId, keyword, pageNumber, pageSize);
      Map<Long, List<SurveyResponse>> itemIdAndResponsesMap = getItemIdAndResponsesMap(surveyResponses);
      List<Long> surveyItemIds = getSurveyItemIdsFromSurveyResponses(surveyResponses);
      List<SurveyItem> surveyItems = surveyService.listSurveyItemsByOrgIdAndItemIds(orgId, surveyItemIds);
      for (SurveyItem surveyItem : surveyItems) {
        NewSurveyItemDTO surveyItemDTO = new NewSurveyItemDTO();
        BeanUtils.copyProperties(surveyItem, surveyItemDTO);
        List<NewSurveyResponseDTO> surveyResponseDTOs = new ArrayList<>();
        if (!itemIdAndResponsesMap.containsKey(surveyItem.getSurveyItemId())
                || itemIdAndResponsesMap.get(surveyItem.getSurveyItemId()).size() == 0) {
          continue;
        } else {
          List<SurveyResponse> surveyResponseList = itemIdAndResponsesMap.get(surveyItem.getSurveyItemId());
          for (SurveyResponse surveyResponse : surveyResponseList) {
            NewSurveyResponseDTO surveyResponseDTO = new NewSurveyResponseDTO();
            BeanUtils.copyProperties(surveyResponse, surveyResponseDTO);
            surveyResponseDTOs.add(surveyResponseDTO);
          }
        }
        surveyItemDTO.setSurveyResponseDTOs(surveyResponseDTOs);
        surveyItemDTO.setTotalNumber(surveyService.searchResponsesByOrgIdAndActivityIdAndItemId(
                orgId, surveyActivityId, surveyItemId, keyword, 1, Integer.MAX_VALUE).size());
        newSurveyItemDTOs.add(surveyItemDTO);
      }
      result.setNewSurveyItemDTOs(newSurveyItemDTOs);
    } catch (Exception e) {
      LOGGER.error("listSurveyResponsesByOrgIdAndActivityIdAndItemId-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public SurveyItemHistoryListDTO listSurveyItemHistorysByOrgIdAndTimeRange(
          long orgId, long startTime, long endTime, long actorUserId, long adminUserId) {
    SurveyItemHistoryListDTO result = new SurveyItemHistoryListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<SurveyActivity> surveyActivities = surveyService.listSurveyActivityByOrgIdAndStartTimeAndEndTime(
              orgId, startTime, endTime);
      List<Long> activityIds = getActivityIds(surveyActivities);
      List<SurveyResponse> surveyResponses = surveyService.listSurveyResponsesByOrgIdAndActivityIds(orgId, activityIds);
      List<Long> surveyItemIds = getSurveyItemIdsFromSurveyResponses(surveyResponses);
      List<SurveyItem> surveyItems = surveyService.listSurveyItemsByOrgIdAndItemIds(orgId, surveyItemIds);

      List<SurveyItemHistoryDTO> surveyItemHistoryDTOs = new ArrayList<>();
      Map<Long, Map<Long, List<SurveyResponse>>> map = getItemIdActivityIdResponsesMap(surveyResponses);
      Map<Long, SurveyActivity> activityMap = getActivityMap(surveyActivities);

      for (SurveyItem surveyItem : surveyItems) {
        SurveyItemDTO surveyItemDTO = new SurveyItemDTO();
        BeanUtils.copyProperties(surveyItem, surveyItemDTO);
        SurveyItemHistoryDTO surveyItemHistoryDTO = new SurveyItemHistoryDTO();
        surveyItemHistoryDTO.setSurveyItemDTO(surveyItemDTO);

        List<SurveyActivityDTO> surveyActivityDTOs = new ArrayList<>();
        Map<Long, List<SurveyResponse>> subMap = map.get(surveyItem.getSurveyItemId());
        for (Long key : subMap.keySet()) {
          SurveyActivity surveyActivity = activityMap.get(key);
          SurveyActivityDTO surveyActivityDTO = new SurveyActivityDTO();
          BeanUtils.copyProperties(surveyActivity, surveyActivityDTO);
          surveyActivityDTO.setTotalResponseNumber(subMap.get(key).size());
          setScoreOrPercentageInSurveyActivity(surveyActivityDTO, subMap.get(key), surveyItem.getSurveyItemType());
          surveyActivityDTOs.add(surveyActivityDTO);
        }
        surveyItemHistoryDTO.setSurveyActivityDTOs(surveyActivityDTOs);
        surveyItemHistoryDTOs.add(surveyItemHistoryDTO);
      }

      result.setSurveyItemHistoryDTOs(surveyItemHistoryDTOs);
    } catch (Exception e) {
      LOGGER.error("listSurveyItemHistorysByOrgIdAndTimeRange-error()", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  private List<Long> getActivityIds(List<SurveyActivity> surveyActivities) {
    List<Long> result = new ArrayList<>();
    for (SurveyActivity surveyActivity : surveyActivities) {
      result.add(surveyActivity.getSurveyActivityId());
    }
    return result;
  }

  private Map<Long, Map<Long, List<SurveyResponse>>> getItemIdActivityIdResponsesMap(
          List<SurveyResponse> surveyResponses) {
    Map<Long, Map<Long, List<SurveyResponse>>> map = new HashMap<>();
    for (SurveyResponse surveyResponse : surveyResponses) {
      long itemId = surveyResponse.getSurveyItemId();
      long activityId = surveyResponse.getSurveyActivityId();
      if (map.containsKey(itemId)) {
        Map<Long, List<SurveyResponse>> subMap = map.get(itemId);
        if (subMap.containsKey(activityId)) {
          List<SurveyResponse> surveyResponseList = subMap.get(activityId);
          surveyResponseList.add(surveyResponse);
          subMap.put(activityId, surveyResponseList);
        } else {
          List<SurveyResponse> surveyResponseList = new ArrayList<>();
          surveyResponseList.add(surveyResponse);
          subMap.put(activityId, surveyResponseList);
        }
      } else {
        Map<Long, List<SurveyResponse>> subMap = new HashMap<>();
        List<SurveyResponse> surveyResponseList = new ArrayList<>();
        surveyResponseList.add(surveyResponse);
        subMap.put(activityId, surveyResponseList);
        map.put(itemId, subMap);
      }
    }
    return map;
  }

  private Map<Long, SurveyActivity> getActivityMap(List<SurveyActivity> surveyActivities) {
    Map<Long, SurveyActivity> result = new HashMap<>();
    for (SurveyActivity surveyActivity : surveyActivities) {
      result.put(surveyActivity.getSurveyActivityId(), surveyActivity);
    }
    return result;
  }

  private void setScoreOrPercentageInSurveyActivity(
          SurveyActivityDTO surveyActivityDTO, List<SurveyResponse> surveyResponses, int itemType) {
    if (itemType == SurveyItemAttribute.SCALE_QUESTION.getCode()) {
      BigDecimal score = new BigDecimal("0.0000");
      BigDecimal denominator = new BigDecimal(surveyResponses.size());
      for (SurveyResponse surveyResponse : surveyResponses) {
        score = score.add(new BigDecimal(SurveyItemAttribute.getEnumByCode(surveyResponse.getResponse()).getScore()));
      }
      surveyActivityDTO.setAverageScore(
              score.divide(denominator, 2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString());
    } else if (itemType == SurveyItemAttribute.BOOLEAN_QUESTION.getCode()) {
      int percent = 0;
      BigDecimal denominator = new BigDecimal(surveyResponses.size());
      for (SurveyResponse surveyResponse : surveyResponses) {
        if (surveyResponse.getResponse().intValue() == SurveyItemAttribute.YES.getCode()) {
          percent++;
        }
      }
      String percentage = new BigDecimal(percent).multiply(
              new BigDecimal(100)).divide(
              denominator, 2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
      surveyActivityDTO.setPercentage(percentage);
    }
  }

}
