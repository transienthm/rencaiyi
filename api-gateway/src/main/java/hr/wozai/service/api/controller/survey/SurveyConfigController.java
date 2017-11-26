package hr.wozai.service.api.controller.survey;

import hr.wozai.service.api.component.PermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.controller.okr.OkrController;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.survey.SurveyConfigVO;
import hr.wozai.service.api.vo.survey.SurveyItemVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.survey.dto.SurveyConfigDTO;
import hr.wozai.service.user.client.survey.dto.SurveyItemDTO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/29
 */
@Controller("surveyConfigController")
public class SurveyConfigController {
  private static final Logger LOGGER = LoggerFactory.getLogger(SurveyConfigController.class);

  @Autowired
  PermissionChecker permissionChecker;

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(value = "/surveys/configs", method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> updateSurveyConfig(@RequestBody SurveyConfigVO surveyConfigVO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.SURVEY_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.EDIT.getCode());

    SurveyConfigDTO surveyConfigDTO = new SurveyConfigDTO();
    BeanUtils.copyProperties(surveyConfigVO, surveyConfigDTO);

    LongDTO remoteResult = facadeFactory.getSurveyFacade().updateSurveyConfig(orgId, surveyConfigDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    IdVO idVO = new IdVO();
    idVO.setIdValue(remoteResult.getData());

    result.setCodeAndMsg(serviceStatus);
    result.setData(idVO);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/surveys/configs", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getSurveyConfig() {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId,
            ResourceCode.SURVEY_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.READ.getCode());

    SurveyConfigDTO remoteResult = facadeFactory.getSurveyFacade().getSurveyConfig(orgId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    SurveyConfigVO surveyConfigVO = new SurveyConfigVO();
    BeanUtils.copyProperties(remoteResult, surveyConfigVO);

    result.setCodeAndMsg(serviceStatus);
    result.setData(surveyConfigVO);
    return result;
  }
}
