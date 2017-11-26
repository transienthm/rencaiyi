package hr.wozai.service.api.controller.userorg;

import hr.wozai.service.api.helper.CoreUserProfileDTOHelper;
import hr.wozai.service.api.vo.orgteam.ProjectTeamVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.orgteam.NameIndexVO;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/25
 */
@Controller("nameIndexController")
public class NameIndexController {
  private static final Logger LOGGER = LoggerFactory.getLogger(NameIndexController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(value = "/name-indexes/search-users-teams", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> searchUsersAndTeams(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                            @RequestParam(value = "type", required = false, defaultValue = "") String type,
                                            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                            @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    TeamListDTO remoteResult = facadeFactory.getCommonToolFacade().searchUserAndTeamNamesByKeyword(orgId, keyword, pageNumber, pageSize,
            actorUserId, adminUserId);
    LOGGER.info("remoteResult:" + remoteResult);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    NameIndexVO nameIndexVO = new NameIndexVO();
    List<TeamVO> teamVOs = new ArrayList<>();
    List<CoreUserProfileVO> coreUserProfileVOs;
    List<ProjectTeamVO> projectTeamVOs = new ArrayList<>();
    coreUserProfileVOs = CoreUserProfileDTOHelper.convertCoreUserProfileDTOsToVOs(remoteResult.getCoreUserProfileDTOs());

    for (TeamDTO teamDTO : remoteResult.getTeamDTOList()) {
      TeamVO teamVO = new TeamVO();
      BeanUtils.copyProperties(teamDTO, teamVO);
      teamVOs.add(teamVO);
    }

    for (ProjectTeamDTO projectTeamDTO : remoteResult.getProjectTeamDTOs()) {
      ProjectTeamVO projectTeamVO = new ProjectTeamVO();
      BeanUtils.copyProperties(projectTeamDTO, projectTeamVO);
      projectTeamVOs.add(projectTeamVO);
    }

    if (type.equals("1")) {
      nameIndexVO.setTeamVOs(teamVOs);
      nameIndexVO.setTotalTeamNumber(remoteResult.getTotalTeamNumber());
    } else {
      nameIndexVO.setTeamVOs(teamVOs);
      nameIndexVO.setTotalTeamNumber(remoteResult.getTotalTeamNumber());
      nameIndexVO.setCoreUserProfileVOs(coreUserProfileVOs);
      nameIndexVO.setTotalUserNumber(remoteResult.getTotalUserNumber());
      nameIndexVO.setProjectTeamVOs(projectTeamVOs);
    }

    result.setData(nameIndexVO);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/name-indexes/search-director", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> searchDirectors(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                        @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                        @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
    Result<Object> result = new Result<>();

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if(!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    UserNameListDTO remoteResult = facadeFactory.getCommonToolFacade().searchDirectorsByKeyword(orgId, keyword, pageNumber, pageSize,
            actorUserId, adminUserId);

    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Map<String, Object> map = new HashMap<>();
    List<Long> userIdList = remoteResult.getIdList();
    List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();

    if (userIdList.isEmpty()) {
      map.put("UserProfiles", coreUserProfileVOs);
      map.put("totalRecordNum", remoteResult.getTotalRecordNum());
      result.setData(map);
      return result;
    }

    CoreUserProfileListDTO coreUserProfileListDTO = facadeFactory.getUserProfileFacade().
            listCoreUserProfile(orgId, userIdList, actorUserId, adminUserId);
    List<CoreUserProfileDTO> coreUserProfileDTOs = coreUserProfileListDTO.getCoreUserProfileDTOs();

    for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
      CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
      BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
      coreUserProfileVOs.add(coreUserProfileVO);
    }

    map.put("UserProfiles", coreUserProfileVOs);
    map.put("totalRecordNum", remoteResult.getTotalRecordNum());

    result.setData(map);

    return result;
  }
}
