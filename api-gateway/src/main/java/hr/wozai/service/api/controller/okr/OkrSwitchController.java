package hr.wozai.service.api.controller.okr;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.CoreUserProfileDTOHelper;
import hr.wozai.service.api.vo.orgteam.ProjectTeamVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.common.dto.RecentUsedObjectDTO;
import hr.wozai.service.user.client.okr.dto.UserAndTeamListDTO;
import hr.wozai.service.user.client.userorg.dto.ProjectTeamDTO;
import hr.wozai.service.user.client.userorg.dto.TeamDTO;
import hr.wozai.service.user.client.userorg.enums.RecentUsedObjectType;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.okr.ViewHistoryVO;
import hr.wozai.service.api.vo.orgteam.TeamListVO;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/25
 */
@Controller("okrSwitchController")
public class OkrSwitchController {
  private static final Logger LOGGER = LoggerFactory.getLogger(OkrSwitchController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(value = "/okrs/view-historys", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> searchUsersAndTeams() {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    UserAndTeamListDTO remoteResult = facadeFactory.getCommonToolFacade().listRecentCheckedOkrUserAndTeam(orgId, actorUserId,
            actorUserId, adminUserId);
    LOGGER.info("remoteResult:" + remoteResult);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    TeamListVO teamListVO = new TeamListVO();
    List<TeamVO> teamVOs = new ArrayList<>();
    List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
    List<ProjectTeamVO> projectTeamVOs = new ArrayList<>();
    coreUserProfileVOs = CoreUserProfileDTOHelper.
            convertCoreUserProfileDTOsToVOs(remoteResult.getCoreUserProfileDTOList());

    for (TeamDTO teamDTO : remoteResult.getTeamDTOList()) {
      TeamVO teamVO = new TeamVO();
      BeanUtils.copyProperties(teamDTO, teamVO);
      teamVOs.add(teamVO);
    }

    for (ProjectTeamDTO projectTeamDTO : remoteResult.getProjectTeamDTOList()) {
      ProjectTeamVO projectTeamVO = new ProjectTeamVO();
      BeanUtils.copyProperties(projectTeamDTO, projectTeamVO);
      projectTeamVOs.add(projectTeamVO);
    }
    teamListVO.setTeamVOs(teamVOs);
    teamListVO.setCoreUserProfileVOs(coreUserProfileVOs);
    teamListVO.setProjectTeamVOs(projectTeamVOs);

    result.setData(teamListVO);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/view-historys", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> addOkrViewHistorys(@RequestBody ViewHistoryVO viewHistoryVO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    int type;
    List<String> usedObjectId = new ArrayList<>();
    if (viewHistoryVO.getUserId() != null) {
      type = RecentUsedObjectType.USER_OKR.getCode();
      usedObjectId.add(String.valueOf(viewHistoryVO.getUserId()));
    } else if (viewHistoryVO.getTeamId() != null){
      type = RecentUsedObjectType.TEAM_OKR.getCode();
      usedObjectId.add(String.valueOf(viewHistoryVO.getTeamId()));
    } else if (viewHistoryVO.getProjectTeamId() != null){
      type = RecentUsedObjectType.PROJECT_TEAM_OKR.getCode();
      usedObjectId.add(String.valueOf(viewHistoryVO.getProjectTeamId()));
    } else {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    RecentUsedObjectDTO recentUsedObjectDTO = new RecentUsedObjectDTO();
    recentUsedObjectDTO.setUserId(actorUserId);
    recentUsedObjectDTO.setType(type);
    recentUsedObjectDTO.setUsedObjectId(usedObjectId);
    recentUsedObjectDTO.setCreatedUserId(actorUserId);

    VoidDTO remoteResult = facadeFactory.getCommonToolFacade().addRecentUsedObject(orgId, recentUsedObjectDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }
}
