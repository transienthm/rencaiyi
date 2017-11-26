package hr.wozai.service.api.controller.survey;

import hr.wozai.service.api.component.PermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.controller.okr.OkrController;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.util.ParamName;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.okr.ObjectivePeriodVO;
import hr.wozai.service.api.vo.survey.SurveyItemVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.okr.dto.ObjectivePeriodDTO;
import hr.wozai.service.user.client.survey.dto.SurveyItemDTO;
import hr.wozai.service.user.client.survey.dto.SurveyItemListDTO;
import hr.wozai.service.user.client.survey.facade.SurveyFacade;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import org.eclipse.jetty.websocket.api.SuspendToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/29
 */
@Controller("surveyItemController")
public class SurveyItemController {
  private static final Logger LOGGER = LoggerFactory.getLogger(SurveyItemController.class);

  @Autowired
  PermissionChecker permissionChecker;

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(value = "/surveys/items", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> createSurveyItem(@RequestBody SurveyItemVO surveyItemVO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.SURVEY_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.CREATE.getCode());

    SurveyItemDTO surveyItemDTO = new SurveyItemDTO();
    BeanUtils.copyProperties(surveyItemVO, surveyItemDTO);

    LongDTO remoteResult = facadeFactory.getSurveyFacade().addSurveyItem(orgId, surveyItemDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_CREATED) {
      throw new ServiceStatusException(serviceStatus);
    }

    IdVO idVO = new IdVO();
    idVO.setIdValue(remoteResult.getData());

    result.setCodeAndMsg(serviceStatus);
    result.setData(idVO);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/surveys/items/{itemId}", method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> deleteSurveyItem(
          @PathVariable("itemId") String encryptItemId) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long surveyItemId = ParamName.getDecryptValueFromString(encryptItemId);

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.SURVEY_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.DELETE.getCode());

    VoidDTO remoteResult = facadeFactory.getSurveyFacade().deleteSurveyItem(orgId, surveyItemId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/surveys/items/{itemId}", method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> updateSurveyItem(
          @PathVariable("itemId") String encryptItemId,
          @RequestBody SurveyItemVO surveyItemVO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long surveyItemId = ParamName.getDecryptValueFromString(encryptItemId);

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.SURVEY_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.EDIT.getCode());

    SurveyItemDTO surveyItemDTO = new SurveyItemDTO();
    BeanUtils.copyProperties(surveyItemVO, surveyItemDTO);
    surveyItemDTO.setSurveyItemId(surveyItemId);

    VoidDTO remoteResult = facadeFactory.getSurveyFacade().updateSurveyItem(orgId, surveyItemDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/surveys/items", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listSurveyItems(
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

    SurveyFacade surveyFacade = facadeFactory.getSurveyFacade();
    SurveyItemListDTO remoteResult = surveyFacade.listSurveyItemsByOrgId(
            orgId, pageNumber, pageSize, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<SurveyItemVO> surveyItemVOs = new ArrayList<>();
    for (SurveyItemDTO surveyItemDTO : remoteResult.getSurveyItemDTOs()) {
      SurveyItemVO surveyItemVO = new SurveyItemVO();
      BeanUtils.copyProperties(surveyItemDTO, surveyItemVO);
      surveyItemVOs.add(surveyItemVO);
    }

    Map<String, Object> map = new HashMap<>();
    map.put("surveyItemVOs", surveyItemVOs);
    map.put("totalNumber", remoteResult.getTotalNumber());

    result.setCodeAndMsg(serviceStatus);
    result.setData(map);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/surveys/items/same-period", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listSurveyItemsWithSamePeriod(
          @RequestParam(value = "startTime") long startTime,
          @RequestParam(value = "endTime") long endTime) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.SURVEY_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.READ.getCode());

    SurveyFacade surveyFacade = facadeFactory.getSurveyFacade();
    SurveyItemListDTO remoteResult = surveyFacade.listSurveyItemsByStartAndEndTime(
            orgId, startTime, endTime, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<SurveyItemVO> surveyItemVOs = new ArrayList<>();
    for (SurveyItemDTO surveyItemDTO : remoteResult.getSurveyItemDTOs()) {
      SurveyItemVO surveyItemVO = new SurveyItemVO();
      BeanUtils.copyProperties(surveyItemDTO, surveyItemVO);
      surveyItemVO.setDeletable(false);
      surveyItemVOs.add(surveyItemVO);
    }

    Map<String, Object> map = new HashMap<>();
    map.put("surveyItemVOs", surveyItemVOs);
    map.put("totalNumber", surveyItemVOs.size());

    result.setCodeAndMsg(serviceStatus);
    result.setData(map);
    return result;
  }
}
