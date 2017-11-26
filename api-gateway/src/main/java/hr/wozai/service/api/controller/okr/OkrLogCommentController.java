package hr.wozai.service.api.controller.okr;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.okr.OkrLogCommentVO;
import hr.wozai.service.api.vo.okr.OkrLogVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.okr.dto.OkrLogCommentDTO;
import hr.wozai.service.user.client.okr.dto.OkrLogCommentListDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
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
@Controller("okrLogCommentController")
public class OkrLogCommentController {
  private static final Logger LOGGER = LoggerFactory.getLogger(OkrController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  OkrUtils okrUtils;

  /*
  @LogAround

  @RequestMapping(value = "/okrs/okr-logs/{okrLogId}/comments",
          method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> createOkrLogComment(@PathVariable("okrLogId") String encryptOkrLogId,
                                     @RequestBody OkrLogCommentVO okrLogCommentVO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long okrLogId = okrUtils.getDecryptValueFromString(encryptOkrLogId);

    OkrLogCommentDTO okrLogCommentDTO = new OkrLogCommentDTO();
    okrLogCommentDTO.setOrgId(orgId);
    okrLogCommentDTO.setUserId(actorUserId);
    okrLogCommentDTO.setOkrLogId(okrLogId);
    okrLogCommentDTO.setCreatedUserId(actorUserId);

    String content = okrLogCommentVO.getContent();
    if(null == content || content.length() > OkrUtils.CONTENT_MAX_LENGTH) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    okrLogCommentDTO.setContent(content);

    List<CoreUserProfileVO> atUsers = okrLogCommentVO.getAtUsers();
    List<String> usersString = new ArrayList<>();
    if (atUsers != null && atUsers.size() > 0) {
      okrUtils.getUserIdString(atUsers, usersString);
    }
    okrLogCommentDTO.setAtUsers(usersString);

    LongDTO remoteResult = facadeFactory.getOkrFacade().addOkrLogComment(okrLogCommentDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);

    result.setCodeAndMsg(serviceStatus);

    IdVO idVO = new IdVO();
    idVO.setIdValue(remoteResult.getData());

    result.setData(idVO);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/okr-logs/{okrLogId}/comments",
          method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> deleteOkrLogComment(@PathVariable("objectiveId") String encryptObjectiveId,
                                            @RequestBody OkrLogVO okrLogVO) {
    Result<Object> result = new Result<>();

    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/okr-logs/{okrLogId}/comments",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listOkrLogComment(@PathVariable("okrLogId") String encryptOkrLogId,
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
    long okrLogId = okrUtils.getDecryptValueFromString(encryptOkrLogId);

    OkrLogCommentListDTO remoteResult = facadeFactory.getOkrFacade().listOkrLogCommentByOkrLogId(orgId, okrLogId,
            pageNumber, pageSize, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    result.setCodeAndMsg(serviceStatus);

    List<OkrLogCommentVO> commentVOs = new ArrayList<>();
    for (OkrLogCommentDTO okrLogCommentDTO : remoteResult.getOkrLogCommentDTOList()) {
      OkrLogCommentVO okrLogCommentVO = new OkrLogCommentVO();
      BeanUtils.copyProperties(okrLogCommentDTO, okrLogCommentVO);
      CoreUserProfileDTO actorUser = facadeFactory.getUserProfileFacade().getCoreUserProfile(orgId,
              okrLogCommentDTO.getCreatedUserId(), actorUserId, adminUserId);
      serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        throw new ServiceStatusException(serviceStatus);
      }
      CoreUserProfileVO actorUserVO = new CoreUserProfileVO();
      BeanUtils.copyProperties(actorUser, actorUserVO);
      okrLogCommentVO.setActorUser(actorUserVO);

      List<String> atUsers = okrLogCommentDTO.getAtUsers();
      if(null != atUsers && atUsers.size() > 0) {
        LOGGER.info("at rewardedUsers:{}, size:{}", atUsers, atUsers.size());
        List<CoreUserProfileVO> userProfileVOs = okrUtils.fillAtUsers(atUsers, orgId, actorUserId, adminUserId);
        okrLogCommentVO.setAtUsers(userProfileVOs);
      }
      commentVOs.add(okrLogCommentVO);
    }

    Map<String, Object> map = new HashMap<>();
    map.put("okrLogComments", commentVOs);
    map.put("totalNumber", remoteResult.getTotalRecordNum());

    result.setData(map);
    return result;
  }*/
}
