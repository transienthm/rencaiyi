// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.userorg;

import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.component.OrgPermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.user.OrgVO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import hr.wozai.service.user.client.userorg.dto.OrgDTO;
import hr.wozai.service.user.client.userorg.dto.TeamListDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-15
 */
@Controller("orgController")
public class OrgController {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrgController.class);

  @Autowired
  private OrgPermissionChecker orgPermissionChecker;

  @Autowired
  private FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(
          value = "/org",
          method = RequestMethod.GET,
          produces = "application/json")
  @ResponseBody
  public Result<OrgVO> getOrg() {

    Result<OrgVO> result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!orgPermissionChecker.canRead(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      OrgDTO rpcResult = facadeFactory.getOrgFacade().getOrg(authedOrgId, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcResult.getServiceStatusDTO().getCode());
      if (!rpcStatus.equals(ServiceStatus.COMMON_OK)) {
        throw new ServiceStatusException(rpcStatus);
      }
      TeamListDTO teamListDTO = facadeFactory.getUserFacade().listNextLevelTeams(rpcResult.getOrgId(), 0L,
              authedActorUserId, authedAdminUserId);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(teamListDTO.getServiceStatusDTO().getCode());
      if (!serviceStatus.equals(ServiceStatus.COMMON_OK) || teamListDTO.getTeamDTOList().size() != 1) {
        LOGGER.error("listNextLevelTeams-error() in getUsersFromTeam");
        throw new ServiceStatusException(serviceStatus);
      }

      OrgVO orgVO = new OrgVO();
      BeanUtils.copyProperties(rpcResult, orgVO);

      TeamVO teamVO = new TeamVO();
      BeanUtils.copyProperties(teamListDTO.getTeamDTOList().get(0), teamVO);
      orgVO.setTeamVO(teamVO);

      result.setData(orgVO);
      result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    } catch (Exception e) {
      LOGGER.info("getOrg()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/org",
          method = RequestMethod.PUT,
          produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result updateOrg(
          @RequestBody @Valid OrgVO orgVO,BindingResult bindingResult,HttpServletRequest request
  ) {

    Result result = new Result<>();
    long authedActorUserId = AuthenticationInterceptor.actorUserId.get();
    long authedAdminUserId = AuthenticationInterceptor.adminUserId.get();
    long authedOrgId = AuthenticationInterceptor.orgId.get();

    if (!orgPermissionChecker.canEdit(authedOrgId, authedActorUserId)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    try {
      OrgDTO orgDTO = new OrgDTO();
      BeanUtils.copyProperties(orgVO, orgDTO);
      VoidDTO rpcResult = facadeFactory.getOrgFacade().updateOrg(authedOrgId, orgDTO, authedActorUserId, authedAdminUserId);
      ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(rpcResult.getServiceStatusDTO().getCode());
      result.setCodeAndMsg(rpcStatus);
    } catch (Exception e) {
      LOGGER.info("updateOrg()-error", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

}
