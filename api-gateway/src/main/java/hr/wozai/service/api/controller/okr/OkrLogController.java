package hr.wozai.service.api.controller.okr;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.okr.dto.OkrLogDTO;
import hr.wozai.service.user.client.okr.dto.OkrLogListDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.okr.OkrLogVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/26
 */
@Controller("okrLogController")
public class OkrLogController {
  private static final Logger LOGGER = LoggerFactory.getLogger(OkrController.class);

  @Autowired
  FacadeFactory facadeFactory;
  
  @Autowired
  OkrUtils okrUtils;

  /*
  @LogAround

  @RequestMapping(value = "/okrs/objectives/{objectiveId}/okr-logs",
          method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> createOkrLog(@PathVariable("objectiveId") String encryptObjectiveId,
                                     @RequestBody OkrLogVO okrLogVO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long objectiveId = okrUtils.getDecryptValueFromString(encryptObjectiveId);

    OkrLogDTO okrLogDTO = new OkrLogDTO();
    okrLogDTO.setOrgId(orgId);
    okrLogDTO.setObjectiveId(objectiveId);
    okrLogDTO.setCreatedUserId(actorUserId);
    String content = okrLogVO.getContent();
    if(null == content || content.length() > OkrUtils.CONTENT_MAX_LENGTH) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    okrLogDTO.setContent(content);

    List<CoreUserProfileVO> atUsers = okrLogVO.getAtUsers();
    List<String> usersString = new ArrayList<>();
    if (atUsers != null && atUsers.size() > 0) {
      okrUtils.getUserIdString(atUsers, usersString);
    }
    okrLogDTO.setAtUsers(usersString);

    LongDTO remoteResult = facadeFactory.getOkrFacade().addOkrLog(okrLogDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);

    IdVO idVO = new IdVO();
    idVO.setIdValue(remoteResult.getData());

    result.setData(idVO);

    return result;
  }



  @LogAround

  @RequestMapping(value = "/okrs/objectives/{objectiveId}/okr-logs",
          method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> deleteOkrLog(@PathVariable("objectiveId") String encryptObjectiveId) {
    Result<Object> result = new Result<>();
    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objectives/{objectiveId}/okr-logs",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listOkrLogs(@PathVariable("objectiveId") String encryptObjectiveId,
                                    @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber,
                                    @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
    Result<Object> result = new Result<>();

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if(!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long objectiveId = okrUtils.getDecryptValueFromString(encryptObjectiveId);

    OkrLogListDTO remoteResult = facadeFactory.getOkrFacade().listOkrLogByObjectiveId(orgId, objectiveId,
            pageNumber, pageSize, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    result.setCodeAndMsg(serviceStatus);

    List<OkrLogVO> okrLogVOs = new ArrayList<>();
    for (OkrLogDTO okrLogDTO : remoteResult.getOkrLogDTOList()) {
      OkrLogVO okrLogVO = new OkrLogVO();
      BeanUtils.copyProperties(okrLogDTO, okrLogVO);
      CoreUserProfileDTO actorUser = facadeFactory.getUserProfileFacade().getCoreUserProfile(orgId,
              okrLogDTO.getCreatedUserId(), actorUserId, adminUserId);
      serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        throw new ServiceStatusException(serviceStatus);
      }
      CoreUserProfileVO actorUserVO = new CoreUserProfileVO();
      BeanUtils.copyProperties(actorUser, actorUserVO);
      okrLogVO.setActorUser(actorUserVO);

      List<String> atUsers = okrLogDTO.getAtUsers();
      if(null != atUsers && atUsers.size() > 0) {
        List<CoreUserProfileVO> userProfileVOs = okrUtils.fillAtUsers(atUsers, orgId, actorUserId, adminUserId);
        okrLogVO.setAtUsers(userProfileVOs);
      }
      okrLogVOs.add(okrLogVO);
    }

    Map<String, Object> map = new HashMap<>();
    map.put("okrLogs", okrLogVOs);
    map.put("totalNumber", remoteResult.getTotalRecordNum());

    result.setData(map);
    return result;
  }*/
}
