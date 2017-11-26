// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.userorg.dto.OrgDTO;
import hr.wozai.service.user.client.userorg.dto.TeamDTO;
import hr.wozai.service.user.client.userorg.dto.TeamListDTO;
import hr.wozai.service.user.client.userorg.facade.OrgFacade;
import hr.wozai.service.user.client.userorg.facade.UserFacade;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.model.navigation.Navigation;
import hr.wozai.service.user.server.model.userorg.Org;
import hr.wozai.service.user.server.model.userorg.Team;
import hr.wozai.service.user.server.service.NavigationService;
import hr.wozai.service.user.server.service.OrgService;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.server.service.TeamService;
import hr.wozai.service.user.server.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-05-16
 */
@Service("orgFacade")
public class OrgFacadeImpl implements OrgFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrgFacadeImpl.class);

  @Autowired
  OrgService orgService;

  @Autowired
  UserFacade userFacade;

  @Autowired
  NavigationService navigationService;

  @Autowired
  TokenService tokenService;

  @Override
  @LogAround
  public OrgDTO getOrg(long orgId, long actorUserId, long adminUserId) {

    OrgDTO result = new OrgDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Org org = orgService.getOrg(orgId);
      BeanUtils.copyProperties(org, result);
      /*if (result.getIsNaviOrg() == 1) {
        Navigation navigation = navigationService.findNavigationByNaviOrgIdAndNaviUserId(orgId, actorUserId);
        if (navigation == null) {
          tokenService.deleteAllTokensByOrgIdAndUserId(orgId, actorUserId);
        }
        result.setNaviStep(navigation.getNaviStep());
      }*/
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("getOrg()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateOrg(long orgId, OrgDTO orgDTO, long actorUserId, long adminUserId) {

    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Org org = new Org();
      BeanUtils.copyProperties(orgDTO, org);
      org.setOrgId(orgId);
      org.setLastModifiedUserId(actorUserId);
      orgService.updateOrg(org);

      if (!StringUtils.isNullOrEmpty(org.getShortName())) {
        TeamListDTO teamList = userFacade.listNextLevelTeams(orgId, 0L, actorUserId, adminUserId);
        TeamDTO teamDTO = teamList.getTeamDTOList().get(0);
        teamDTO.setTeamName(org.getShortName());
        userFacade.updateTeam(teamDTO, actorUserId, adminUserId);
      }
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateOrg()-error", e);
    }

    return result;
  }
}
