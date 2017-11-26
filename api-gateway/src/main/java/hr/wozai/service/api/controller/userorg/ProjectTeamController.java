package hr.wozai.service.api.controller.userorg;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.CoreUserProfileDTOHelper;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.ParamName;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.orgteam.ProjectTeamVO;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.user.client.userorg.facade.UserFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/18
 */
@Controller("projectTeamController")
public class ProjectTeamController {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProjectTeamController.class);

  @Autowired
  FacadeFactory facadeFactory;

  private UserFacade userFacade;

  @PostConstruct
  public void init() throws Exception {
    userFacade = facadeFactory.getUserFacade();
  }

  @LogAround

  @BindingResultMonitor
  @RequestMapping(
          value = "/teams/{teamId}/project-teams",
          method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> addProjectTeam(
          @PathVariable String teamId,
          @RequestBody @Valid ProjectTeamVO projectTeamVO,
          BindingResult bindingResult)
          throws Exception {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptTeamId = ParamName.getDecryptValueFromString(teamId);

    ProjectTeamDTO projectTeamDTO = new ProjectTeamDTO();
    BeanUtils.copyProperties(projectTeamVO, projectTeamDTO);
    projectTeamDTO.setTeamId(decryptTeamId);

    // 检测该用户是不是团队负责人
    checkTeamAdmin(orgId, decryptTeamId, actorUserId, actorUserId, adminUserId);

    LongDTO remoteResult = userFacade.addProjectTeam(orgId, projectTeamDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_CREATED) {
      throw new ServiceStatusException(serviceStatus);
    }

    IdVO idVO = new IdVO();
    idVO.setIdValue(remoteResult.getData());

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);
    result.setData(idVO);

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/teams/{teamId}/project-teams/{projectTeamId}",
          method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> deleteProjectTeam(
          @PathVariable String teamId,
          @PathVariable String projectTeamId)
          throws Exception {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptTeamId = ParamName.getDecryptValueFromString(teamId);
    long decryptProjectTeamId = ParamName.getDecryptValueFromString(projectTeamId);

    // 检测该用户是不是团队负责人
    checkTeamAdmin(orgId, decryptTeamId, actorUserId, actorUserId, adminUserId);

    VoidDTO remoteResult = userFacade.deleteProjectTeam(orgId, decryptProjectTeamId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround

  @BindingResultMonitor
  @RequestMapping(
          value = "/teams/{teamId}/project-teams/{projectTeamId}",
          method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> updateProjectTeam(
          @PathVariable String teamId,
          @PathVariable String projectTeamId,
          @RequestBody @Valid ProjectTeamVO projectTeamVO,
          BindingResult bindingResult)
          throws Exception {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptTeamId = ParamName.getDecryptValueFromString(teamId);
    long decryptProjectTeamId = ParamName.getDecryptValueFromString(projectTeamId);

    ProjectTeamDTO projectTeamDTO = new ProjectTeamDTO();
    BeanUtils.copyProperties(projectTeamVO, projectTeamDTO);
    projectTeamDTO.setTeamId(decryptTeamId);
    projectTeamDTO.setProjectTeamId(decryptProjectTeamId);

    // 检测该用户是不是团队负责人
    checkTeamAdmin(orgId, decryptTeamId, actorUserId, actorUserId, adminUserId);

    VoidDTO remoteResult = userFacade.updateProjectTeam(orgId, projectTeamDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround

  @BindingResultMonitor
  @RequestMapping(
          value = "/teams/{teamId}/project-teams",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listProjectTeamsByTeamId(
          @PathVariable String teamId)
          throws Exception {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptTeamId = ParamName.getDecryptValueFromString(teamId);

    // 检测该用户是不是团队负责人
    // checkTeamAdmin(orgId, decryptTeamId, actorUserId, actorUserId, adminUserId);

    ProjectTeamListDTO remoteResult = userFacade.listProjectTeamsByOrgIdAndTeamId(
            orgId, decryptTeamId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<ProjectTeamVO> projectTeamVOs = new ArrayList<>();
    for (ProjectTeamDTO projectTeamDTO : remoteResult.getProjectTeamDTOs()) {
      ProjectTeamVO projectTeamVO = new ProjectTeamVO();
      BeanUtils.copyProperties(projectTeamDTO, projectTeamVO);
      projectTeamVOs.add(projectTeamVO);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);
    result.setData(projectTeamVOs);

    return result;
  }

  @LogAround

  @BindingResultMonitor
  @RequestMapping(
          value = "/teams/project-teams/{projectTeamId}",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getProjectTeam(
          @PathVariable String projectTeamId)
          throws Exception {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptProjectTeamId = ParamName.getDecryptValueFromString(projectTeamId);


    ProjectTeamDTO remoteResult = userFacade.getProjectTeamByPrimaryKeyAndOrgId(
            orgId, decryptProjectTeamId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

      ProjectTeamVO projectTeamVO = new ProjectTeamVO();
      BeanUtils.copyProperties(remoteResult, projectTeamVO);

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);
    result.setData(projectTeamVO);

    return result;
  }

  // =======================ProjectTeamMember
  @LogAround

  @RequestMapping(
          value = "/teams/{teamId}/project-teams/{projectTeamId}/members",
          method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> batchAddProjectTeamMember(
          @PathVariable String teamId,
          @PathVariable String projectTeamId,
          @RequestBody List<IdVO> idVOList)
          throws Exception {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptTeamId = ParamName.getDecryptValueFromString(teamId);
    long decryptProjectTeamId = ParamName.getDecryptValueFromString(projectTeamId);

    // 检测该用户是不是团队负责人
    checkTeamAdmin(orgId, decryptTeamId, actorUserId, actorUserId, adminUserId);

    ProjectTeamMemberListDTO projectTeamMemberListDTO = new ProjectTeamMemberListDTO();
    List<ProjectTeamMemberDTO> projectTeamMemberDTOs = new ArrayList<>();
    for (IdVO idVO : idVOList) {
      ProjectTeamMemberDTO projectTeamMemberDTO = new ProjectTeamMemberDTO();
      projectTeamMemberDTO.setUserId(idVO.getIdValue());
      projectTeamMemberDTO.setProjectTeamId(decryptProjectTeamId);
      projectTeamMemberDTOs.add(projectTeamMemberDTO);
    }
    projectTeamMemberListDTO.setProjectTeamMemberDTOs(projectTeamMemberDTOs);
    VoidDTO remoteResult = userFacade.batchInsertProjectTeamMember(
            orgId, projectTeamMemberListDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/teams/{teamId}/project-teams/{projectTeamId}/members/{userId}",
          method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> deleteProjectTeamMember(
          @PathVariable String teamId,
          @PathVariable String projectTeamId,
          @PathVariable String userId)
          throws Exception {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptTeamId = ParamName.getDecryptValueFromString(teamId);
    long decryptProjectTeamId = ParamName.getDecryptValueFromString(projectTeamId);
    long decryptUserId = ParamName.getDecryptValueFromString(userId);

    // 检测该用户是不是团队负责人
    checkTeamAdmin(orgId, decryptTeamId, actorUserId, actorUserId, adminUserId);

    ProjectTeamMemberListDTO projectTeamMemberListDTO = new ProjectTeamMemberListDTO();
    List<ProjectTeamMemberDTO> projectTeamMemberDTOs = new ArrayList<>();
    ProjectTeamMemberDTO projectTeamMemberDTO = new ProjectTeamMemberDTO();
    projectTeamMemberDTO.setUserId(decryptUserId);
    projectTeamMemberDTO.setProjectTeamId(decryptProjectTeamId);
    projectTeamMemberDTOs.add(projectTeamMemberDTO);

    projectTeamMemberListDTO.setProjectTeamMemberDTOs(projectTeamMemberDTOs);
    VoidDTO remoteResult = userFacade.batchDeleteProjectTeamMember(
            orgId, projectTeamMemberListDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround

  @RequestMapping(
          value = "/teams/{teamId}/project-teams/{projectTeamId}/members",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listProjectTeamMembersByProjectTeamId(
          @PathVariable String teamId,
          @PathVariable String projectTeamId)
          throws Exception {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptTeamId = ParamName.getDecryptValueFromString(teamId);
    long decryptProjectTeamId = ParamName.getDecryptValueFromString(projectTeamId);

    // 检测该用户是不是团队负责人
    // checkTeamAdmin(orgId, decryptTeamId, actorUserId, actorUserId, adminUserId);

    CoreUserProfileListDTO remoteResult = userFacade.listProjectTeamMembersByOrgIdAndProjectTeamId(
            orgId, decryptProjectTeamId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);
    result.setData(CoreUserProfileDTOHelper.convertCoreUserProfileDTOsToVOs(remoteResult.getCoreUserProfileDTOs()));

    return result;
  }

  private void checkTeamAdmin(long orgId, long teamId, long userId, long actorUserId, long adminUserId) {
    TeamMemberDTO teamMemberDTO = userFacade.getTeamMemberByUserId(orgId, userId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(teamMemberDTO.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    if (teamId == teamMemberDTO.getTeamId() && teamMemberDTO.getIsTeamAdmin() == 1) {

    } else {
      throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
    }
  }


}
