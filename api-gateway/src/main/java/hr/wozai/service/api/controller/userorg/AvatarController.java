// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.userorg;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.document.dto.OssAvatarPutRequestDTO;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.document.OssAvatarPutRequestVO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-04-11
 */
@Controller("avatarController")
public class AvatarController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AvatarController.class);

  private static final String PARAM_KEY_PUBLIC_GET_URL = "publicGetUrl";

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(
      value = "/avatars",
      method = RequestMethod.GET,
      produces = "application/json")
  @ResponseBody
  public Result<OssAvatarPutRequestVO> getAvatarPutRequest(
      @RequestParam("x") String x,
      @RequestParam("y") String y,
      @RequestParam("e") String e
  ) {

    Result<OssAvatarPutRequestVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    try {

      OssAvatarPutRequestDTO getResult =
          facadeFactory.getAvatarFacade().addAvatar(authedOrgId, x, y, e, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(getResult.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        OssAvatarPutRequestVO ossAvatarPutRequestVO = new OssAvatarPutRequestVO();
        BeanUtils.copyProperties(getResult, ossAvatarPutRequestVO);
        result.setData(ossAvatarPutRequestVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception ex) {
      LOGGER.info("getAvatarPutRequest()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/avatars",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result<OssAvatarPutRequestVO> updateAvatarPutRequest(
      @RequestParam("x") String x,
      @RequestParam("y") String y,
      @RequestParam("e") String e,
      @RequestBody JSONObject jsonObject
  ) {

    Result<OssAvatarPutRequestVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();
    String publicGetUrl = null;

    try {
      publicGetUrl = jsonObject.getString("publicGetUrl");
    } catch (Exception ex) {
      System.out.println("Christ! jsonObject=" + jsonObject);
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    try {
      OssAvatarPutRequestDTO updateRequest =
              facadeFactory.getAvatarFacade().updateAvatar(authedOrgId, x, y, e, publicGetUrl, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(updateRequest.getServiceStatusDTO().getCode());
      if (rpcStatus.equals(ServiceStatus.COMMON_CREATED)) {
        OssAvatarPutRequestVO ossAvatarPutRequestVO = new OssAvatarPutRequestVO();
        BeanUtils.copyProperties(updateRequest, ossAvatarPutRequestVO);
        result.setData(ossAvatarPutRequestVO);
      }
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception ex) {
      LOGGER.info("updateAvatarPutRequest()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }
  
}
