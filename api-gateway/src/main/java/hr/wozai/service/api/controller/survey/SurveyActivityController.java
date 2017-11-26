package hr.wozai.service.api.controller.survey;

import hr.wozai.service.api.component.PermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.util.ParamName;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.survey.*;
import hr.wozai.service.nlp.client.labelcloud.dto.LabelCloudDTO;
import hr.wozai.service.nlp.client.labelcloud.dto.LabelCloudListDTO;
import hr.wozai.service.nlp.client.labelcloud.dto.LabelDTO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.survey.dto.*;
import hr.wozai.service.user.client.survey.enums.SurveyItemAttribute;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/29
 */
@Controller("surveyActivityController")
public class SurveyActivityController {
  private static final Logger LOGGER = LoggerFactory.getLogger(SurveyActivityController.class);

  @Autowired
  PermissionChecker permissionChecker;

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(value = "/surveys/activities/in-period", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getSurveyActivity() {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    SurveyActivityDTO remoteResult = facadeFactory.getSurveyFacade().getInPeriodAndUnSubmitedSurveyActivity(
            orgId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    SurveyActivityVO surveyActivityVO = new SurveyActivityVO();
    BeanUtils.copyProperties(remoteResult, surveyActivityVO);

    List<SurveyResponseVO> surveyResponseVOs = new ArrayList<>();
    for (SurveyResponseDTO surveyResponseDTO : remoteResult.getSurveyResponseDTOs()) {
      SurveyResponseVO surveyResponseVO = new SurveyResponseVO();
      BeanUtils.copyProperties(surveyResponseDTO, surveyResponseVO);
      SurveyItemVO surveyItemVO = new SurveyItemVO();
      BeanUtils.copyProperties(surveyResponseDTO.getSurveyItemDTO(), surveyItemVO);
      surveyResponseVO.setSurveyItemVO(surveyItemVO);
      surveyResponseVOs.add(surveyResponseVO);
    }
    surveyActivityVO.setSurveyResponseVOs(surveyResponseVOs);

    result.setCodeAndMsg(serviceStatus);
    result.setData(surveyActivityVO);
    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/surveys/activities/submit",
          method = RequestMethod.PUT,
          produces = "application/json")
  @ResponseBody
  public Result<Object> submitSurveyActivity(@RequestBody SurveyActivityVO surveyActivityVO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    SurveyActivityDTO surveyActivityDTO = new SurveyActivityDTO();
    BeanUtils.copyProperties(surveyActivityVO, surveyActivityDTO);
    List<SurveyResponseDTO> surveyResponseDTOs = new ArrayList<>();
    for (SurveyResponseVO surveyResponseVO : surveyActivityVO.getSurveyResponseVOs()) {
      SurveyResponseDTO surveyResponseDTO = new SurveyResponseDTO();
      BeanUtils.copyProperties(surveyResponseVO, surveyResponseDTO);
      surveyResponseDTOs.add(surveyResponseDTO);
    }
    surveyActivityDTO.setSurveyResponseDTOs(surveyResponseDTOs);

    VoidDTO remoteResult = facadeFactory.getSurveyFacade().submitSurveyActivityByUser(
            orgId, surveyActivityDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/surveys/activities", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listSurveyActivitiesByOrgId(
          @RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
          @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if(!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.SURVEY_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.READ.getCode());

    SurveyActivityListDTO remoteResult = facadeFactory.getSurveyFacade().listSurveyActivitiesByOrgId(
            orgId, pageNumber, pageSize, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<SurveyActivityVO> surveyActivityVOs = new ArrayList<>();
    for (SurveyActivityDTO surveyActivityDTO : remoteResult.getSurveyActivityDTOs()) {
      SurveyActivityVO surveyActivityVO = new SurveyActivityVO();
      BeanUtils.copyProperties(surveyActivityDTO, surveyActivityVO);
      surveyActivityVOs.add(surveyActivityVO);
    }

    result.setCodeAndMsg(serviceStatus);
    result.setData(surveyActivityVOs);
    return result;
  }
/**
  @LogAround

  @RequestMapping(value = "/surveys/activities/{activityId}/items", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listSurveyItemsByActivityId(@PathVariable("activityId") String encryptActivityId) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.SURVEY_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.READ.getCode());

    long activityId = ParamName.getDecryptValueFromString(encryptActivityId);

    NewSurveyActivityDTO remoteResult = facadeFactory.getSurveyFacade().listSurveyResponsesByOrgIdAndActivityId(
            orgId, activityId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    LabelCloudListDTO labelCloudListDTO = facadeFactory.getLabelCloudFacade().listLabelCloudsByActivityId(
            orgId, remoteResult.getSurveyActivityId(), actorUserId, adminUserId);
    serviceStatus = ServiceStatus.getEnumByCode(labelCloudListDTO.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    Map<Long, List<LabelDTO>> itemLabelMap = new HashMap<>();
    for (LabelCloudDTO labelCloudDTO : labelCloudListDTO.getLabelCloudDTOs()) {
      itemLabelMap.put(labelCloudDTO.getSurveyItemId(), labelCloudDTO.getLabelClouds());
    }

    SurveyActivityVO surveyActivityVO = new SurveyActivityVO();
    BeanUtils.copyProperties(remoteResult, surveyActivityVO);
    List<SurveyItemVO> surveyItemVOs = new ArrayList<>();
    for (NewSurveyItemDTO surveyItemDTO : remoteResult.getNewSurveyItemDTOs()) {
      Map<Integer, Integer> map = new HashMap<>();
      SurveyItemVO surveyItemVO = new SurveyItemVO();
      BeanUtils.copyProperties(surveyItemDTO, surveyItemVO);
      List<SurveyResponseVO> surveyResponseVOs = new ArrayList<>();
      for (NewSurveyResponseDTO surveyResponseDTO : surveyItemDTO.getSurveyResponseDTOs()) {
        SurveyResponseVO surveyResponseVO = new SurveyResponseVO();
        BeanUtils.copyProperties(surveyResponseDTO, surveyResponseVO);
        surveyResponseVOs.add(surveyResponseVO);
        if (surveyResponseDTO.getResponse() != null) {
          if (map.containsKey(surveyResponseDTO.getResponse())) {
            map.put(surveyResponseDTO.getResponse(), map.get(surveyResponseDTO.getResponse()) + 1);
          } else {
            map.put(surveyResponseDTO.getResponse(), 1);
          }
        }
      }
      surveyItemVO.setSurveyResponseVOs(surveyResponseVOs);
      if (surveyItemDTO.getSurveyItemType() == SurveyItemAttribute.SCALE_QUESTION.getCode().intValue()) {
        List<SurveyChartVO> surveyChartVOs = new ArrayList<>();
        for (int i = SurveyItemAttribute.ONE.getCode(); i <= SurveyItemAttribute.TEN.getCode(); i++) {
          SurveyChartVO surveyChartVO = new SurveyChartVO();
          surveyChartVO.setKey(String.valueOf(i));
          surveyChartVO.setValue(map.containsKey(i)?String.valueOf(map.get(i)):"0");
          surveyChartVOs.add(surveyChartVO);
        }
        surveyItemVO.setSurveyChartVOs(surveyChartVOs);
      } else if (surveyItemDTO.getSurveyItemType() == SurveyItemAttribute.BOOLEAN_QUESTION.getCode().intValue()) {
        List<SurveyChartVO> surveyChartVOs = new ArrayList<>();
        for (int i = SurveyItemAttribute.YES.getCode(); i <= SurveyItemAttribute.NO.getCode(); i++) {
          SurveyChartVO surveyChartVO = new SurveyChartVO();
          surveyChartVO.setKey(String.valueOf(i));
          surveyChartVO.setValue(map.containsKey(i)?String.valueOf(map.get(i)):"0");
          surveyChartVOs.add(surveyChartVO);
        }
        surveyItemVO.setSurveyChartVOs(surveyChartVOs);
      }

      // todo: 加标签云
      List<SurveyTagVO> surveyTagVOs = new ArrayList<>();
      if (itemLabelMap.containsKey(surveyItemDTO.getSurveyItemId())) {
        for (LabelDTO labelDTO : itemLabelMap.get(surveyItemDTO.getSurveyItemId())) {
          SurveyTagVO surveyTagVO = new SurveyTagVO();
          surveyTagVO.setName(labelDTO.getLabel());
          surveyTagVO.setWeight(labelDTO.getWeight());
          surveyTagVOs.add(surveyTagVO);
        }
      }
      surveyItemVO.setSurveyTagVOs(surveyTagVOs);
      surveyItemVOs.add(surveyItemVO);
    }
    surveyActivityVO.setSurveyItemVOs(surveyItemVOs);

    result.setCodeAndMsg(serviceStatus);
    result.setData(surveyActivityVO);
    return result;
  }
**/
  @LogAround

  @RequestMapping(value = "/surveys/activities/{activityId}/items/{itemId}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listSurveyItemsByActivityId(
          @PathVariable("activityId") String encryptActivityId,
          @PathVariable("itemId") String encryptItemId,
          @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
          @RequestParam(value = "pageNumber", required = false, defaultValue = "1") int pageNumber,
          @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.SURVEY_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.READ.getCode());

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if(!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    long activityId = ParamName.getDecryptValueFromString(encryptActivityId);
    long itemId = ParamName.getDecryptValueFromString(encryptItemId);

    NewSurveyActivityDTO remoteResult = facadeFactory.getSurveyFacade().listSurveyResponsesByOrgIdAndActivityIdAndItemId(
            orgId, activityId, itemId, keyword, pageNumber, pageSize, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    SurveyActivityVO surveyActivityVO = new SurveyActivityVO();
    BeanUtils.copyProperties(remoteResult, surveyActivityVO);
    List<SurveyItemVO> surveyItemVOs = new ArrayList<>();
    for (NewSurveyItemDTO surveyItemDTO : remoteResult.getNewSurveyItemDTOs()) {
      SurveyItemVO surveyItemVO = new SurveyItemVO();
      BeanUtils.copyProperties(surveyItemDTO, surveyItemVO);
      List<SurveyResponseVO> surveyResponseVOs = new ArrayList<>();
      for (NewSurveyResponseDTO surveyResponseDTO : surveyItemDTO.getSurveyResponseDTOs()) {
        SurveyResponseVO surveyResponseVO = new SurveyResponseVO();
        BeanUtils.copyProperties(surveyResponseDTO, surveyResponseVO);
        surveyResponseVOs.add(surveyResponseVO);
      }
      surveyItemVO.setSurveyResponseVOs(surveyResponseVOs);
      surveyItemVOs.add(surveyItemVO);
    }
    surveyActivityVO.setSurveyItemVOs(surveyItemVOs);

    result.setCodeAndMsg(serviceStatus);
    result.setData(surveyActivityVO);
    return result;
  }
/**
  @LogAround

  @RequestMapping(value = "/surveys/activities/items/history-charts", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listSurveyItemHistorysByOrgIdAndTimeRange(
          @RequestParam(value = "startTime", required = false, defaultValue = "") String startTimeString,
          @RequestParam(value = "endTime", required = false, defaultValue = "") String endTimeString) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.SURVEY_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.READ.getCode());

    long oneDay = 3600 * 24 * 1000;
    long startTime;
    long endTime;
    if (StringUtils.isNullOrEmpty(endTimeString)) {
      endTime = System.currentTimeMillis();
      startTime = endTime - 30 * oneDay;
    } else {
      startTime = Long.parseLong(startTimeString);
      endTime = Long.parseLong(endTimeString);
    }

    SurveyItemHistoryListDTO remoteResult = facadeFactory.getSurveyFacade().listSurveyItemHistorysByOrgIdAndTimeRange(
            orgId, startTime, endTime, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<SurveyItemHistoryDTO> surveyItemHistoryDTOs = remoteResult.getSurveyItemHistoryDTOs();
    List<SurveyItemHistoryVO> surveyItemHistoryVOs = new ArrayList<>();

    List<Long> itemLabelIds = new ArrayList<>();
    for (SurveyItemHistoryDTO surveyItemHistoryDTO : surveyItemHistoryDTOs) {
      SurveyItemHistoryVO surveyItemHistoryVO = new SurveyItemHistoryVO();

      SurveyItemVO surveyItemVO = new SurveyItemVO();
      BeanUtils.copyProperties(surveyItemHistoryDTO.getSurveyItemDTO(), surveyItemVO);
      surveyItemHistoryVO.setSurveyItemVO(surveyItemVO);

      List<SurveyActivityVO> surveyActivityVOs = new ArrayList<>();
      for (SurveyActivityDTO surveyActivityDTO : surveyItemHistoryDTO.getSurveyActivityDTOs()) {
        SurveyActivityVO surveyActivityVO = new SurveyActivityVO();
        BeanUtils.copyProperties(surveyActivityDTO, surveyActivityVO);
        // 标签云,前3的热词
        if (surveyItemVO.getSurveyItemType() == SurveyItemAttribute.COMMON_QUESTION.getCode().intValue()) {
          itemLabelIds.add(surveyItemVO.getSurveyItemId());
        }
        surveyActivityVOs.add(surveyActivityVO);
      }
      surveyItemHistoryVO.setSurveyActivityVOs(surveyActivityVOs);
      surveyItemHistoryVOs.add(surveyItemHistoryVO);
    }

    Map<Long, List<LabelDTO>> activityLabelMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(itemLabelIds)) {
      LabelCloudListDTO labelCloudListDTO = facadeFactory.getLabelCloudFacade().listLabelCloudsBySurveyItemIds(
              orgId, itemLabelIds, actorUserId, adminUserId);
      serviceStatus = ServiceStatus.getEnumByCode(labelCloudListDTO.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        throw new ServiceStatusException(serviceStatus);
      }
      for (LabelCloudDTO labelCloudDTO : labelCloudListDTO.getLabelCloudDTOs()) {
        activityLabelMap.put(labelCloudDTO.getSurveyActivityId(), labelCloudDTO.getLabelClouds());
      }
    }

    for (SurveyItemHistoryVO surveyItemHistoryVO : surveyItemHistoryVOs) {
      if (surveyItemHistoryVO.getSurveyItemVO().getSurveyItemType()
              == SurveyItemAttribute.COMMON_QUESTION.getCode().intValue()) {
        for (SurveyActivityVO surveyActivityVO : surveyItemHistoryVO.getSurveyActivityVOs()) {
          List<SurveyTagVO> surveyTagVOs = new ArrayList<>();
          if (activityLabelMap.containsKey(surveyActivityVO.getSurveyActivityId())) {
            int end=3;
            if (activityLabelMap.get(surveyActivityVO.getSurveyActivityId()).size() < 3) {
              end = activityLabelMap.get(surveyActivityVO.getSurveyActivityId()).size();
            }
            for (int i=0; i < end; i++) {
              SurveyTagVO surveyTagVO = new SurveyTagVO();
              surveyTagVO.setName(activityLabelMap.get(surveyActivityVO.getSurveyActivityId()).get(i).getLabel());
              surveyTagVO.setWeight(activityLabelMap.get(surveyActivityVO.getSurveyActivityId()).get(i).getWeight());
              surveyTagVOs.add(surveyTagVO);
            }
          }
          surveyActivityVO.setSurveyTagVOs(surveyTagVOs);
        }
      }
    }

    result.setCodeAndMsg(serviceStatus);
    result.setData(surveyItemHistoryVOs);
    return result;
  } **/
}

