// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.userorg;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.user.NavigationVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.dto.NavigationDTO;
import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2016-11-09
 */
@Controller("navigationController")
public class NavigationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(NavigationController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(
          value = "/navigation",
          method = RequestMethod.GET,
          produces = "application/json")
  @ResponseBody
  public Result<Object> getNavigation() {

    Result<Object> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    NavigationDTO remoteResult = facadeFactory.getNavigationFacade().getNavigation(
            authedOrgId, authedActorUserId, authedActorUserId, authedAdminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    NavigationVO navigationVO = new NavigationVO();
    BeanUtils.copyProperties(remoteResult, navigationVO);
    result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    result.setData(navigationVO);

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/navigation",
      method = RequestMethod.DELETE,
      produces = "application/json")
  @ResponseBody
  public Result<Object> deleteNavigationAndGetNewTokenPari() {

    Result<Object> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    TokenPairDTO remoteResult = facadeFactory.getNavigationFacade().deleteNaviOrgAndRedirectToTrueOrg(
            authedOrgId, authedActorUserId, authedActorUserId, authedAdminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    result.setData(remoteResult);

    return result;
  }

  @LogAround

  @RequestMapping(
      value = "/navigation",
      method = RequestMethod.PUT,
      produces = "application/json")
  @ResponseBody
  public Result<Object> updateNavigation(@RequestBody NavigationVO navigationVO) {
    Result<Object> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    NavigationDTO navigationDTO = new NavigationDTO();
    BeanUtils.copyProperties(navigationVO, navigationDTO);
    navigationDTO.setNaviOrgId(authedOrgId);
    navigationDTO.setNaviUserId(authedActorUserId);
    navigationDTO.setLastModifiedUserId(authedActorUserId);
    VoidDTO remoteResult = facadeFactory.getNavigationFacade().updateNavigation(
            navigationDTO, authedActorUserId, authedAdminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_CREATED) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);

    return result;
  }
  
}
