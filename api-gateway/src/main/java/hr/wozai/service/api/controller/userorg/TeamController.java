package hr.wozai.service.api.controller.userorg;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import hr.wozai.service.api.helper.CoreUserProfileDTOHelper;
import hr.wozai.service.api.vo.orgteam.TeamMemberVO;
import hr.wozai.service.api.vo.user.UserEmploymentVO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.component.PermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.util.ParamName;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.orgteam.TeamListVO;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.orgteam.TransferTeamVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/15
 */
@Controller("teamController")
public class TeamController {
  private static final Logger LOGGER = LoggerFactory.getLogger(TeamController.class);

  @Autowired
  FacadeFactory facadeFactory;

  @Autowired
  PermissionUtil permissionUtil;

  @Autowired
  PermissionChecker permissionChecker;

  /**
   * add a team:teamName, parentTeamId
   *
   * @param teamVO
   * @return
   */
  @LogAround

  @BindingResultMonitor
  @RequestMapping(value = "/teams", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> addTeam(
          @RequestBody @Valid TeamVO teamVO,
          BindingResult bindingResult)
          throws Exception {

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId, ResourceCode.USER_ORG_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.CREATE.getCode());

    LOGGER.info("addTeam()-request: teamVO={}, orgId={}", teamVO, orgId);

    Result<Object> result = new Result<>();
    TeamDTO teamDTO = new TeamDTO();
    BeanUtils.copyProperties(teamVO, teamDTO);
    teamDTO.setOrgId(orgId);
    teamDTO.setCreatedUserId(actorUserId);
    teamDTO.setLastModifiedUserId(actorUserId);
    LongDTO remoteResult = facadeFactory.getUserFacade().addTeam(teamDTO, actorUserId, adminUserId);

    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_CREATED) {
      throw new ServiceStatusException(serviceStatus);
    }

    IdVO idVO = new IdVO();
    idVO.setIdValue(remoteResult.getData());

    result.setCodeAndMsg(serviceStatus);
    result.setData(idVO);
    return result;
  }

  /**
   * delete a team
   * cannot delete if it's not empty
   *
   * @param teamId
   * @return
   */
  @LogAround

  @RequestMapping(value = "/teams/{teamId}", method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> deleteTeam(@PathVariable(value = "teamId") String teamId) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId, ResourceCode.USER_ORG_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.DELETE.getCode());

    long decryptTeamId = getDecryptValueFromString(teamId);

    LOGGER.info("deleteTeam()-request: teamId={}, orgId={}", decryptTeamId, orgId);

    Result<Object> result = new Result<>();

    BooleanDTO remoteResult = facadeFactory.getUserFacade().deleteTeam(orgId, decryptTeamId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (!remoteResult.getData()) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  /**
   * update team
   * update name,parent_team
   *
   * @param teamVO
   * @return
   */
  @LogAround

  @BindingResultMonitor
  @RequestMapping(value = "/teams/{teamId}", method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> updateTeam(
          @RequestBody @Valid TeamVO teamVO,
          @PathVariable String teamId,
          BindingResult bindingResult) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long decryptTeamId = getDecryptValueFromString(teamId);

    permissionChecker.permissionCheck(orgId, actorUserId, orgId, ResourceCode.USER_ORG_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.EDIT.getCode());

    Result<Object> result = new Result<>();

    TeamDTO teamDTO = new TeamDTO();
    BeanUtils.copyProperties(teamVO, teamDTO);
    teamDTO.setTeamId(decryptTeamId);
    teamDTO.setOrgId(orgId);
    teamDTO.setLastModifiedUserId(actorUserId);

    LongDTO remoteResult = facadeFactory.getUserFacade().updateTeam(teamDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/teams", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listAllTeam() {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    Result<Object> result = new Result<>();
    TeamListDTO remoteResult = facadeFactory.getUserFacade().listAllTeams(orgId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<TeamVO> teamVOList = new ArrayList<>();
    for (TeamDTO teamDTO : remoteResult.getTeamDTOList()) {
      TeamVO teamVO = new TeamVO();
      BeanUtils.copyProperties(teamDTO, teamVO);
      teamVOList.add(teamVO);
    }

    result.setCodeAndMsg(serviceStatus);
    result.setData(teamVOList);

    return result;
  }

  /**
   * get a team info
   *
   * @param teamId
   * @return
   */
  @LogAround

  @RequestMapping(value = "/teams/{teamId}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getTeam(@PathVariable String teamId) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long decryptTeamId = getDecryptValueFromString(teamId);

    LOGGER.info("getTeam()-request: teamId={}, orgId={}", decryptTeamId, orgId);

    Result<Object> result = new Result<>();
    TeamDTO teamDTO = facadeFactory.getUserFacade().getTeam(orgId, decryptTeamId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(teamDTO.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    if (teamDTO.getTeamName() == null) {
      throw new ServiceStatusException(ServiceStatus.UO_TEAM_NOT_FOUND);
    }

    TeamVO teamVO = new TeamVO();
    BeanUtils.copyProperties(teamDTO, teamVO);

    result.setCodeAndMsg(serviceStatus);
    result.setData(teamVO);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/teams/{teamId}/team-members",
          method = RequestMethod.GET,
          produces = "application/json")
  @ResponseBody
  public Result<Object> getTeamMembers(@PathVariable String teamId) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long decryptTeamId = getDecryptValueFromString(teamId);

    CoreUserProfileListDTO remoteResult = facadeFactory.getUserFacade().getTeamMembersByTeamId(
            orgId, decryptTeamId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }



    Result<Object> result = new Result<>();
    result.setCodeAndMsg(serviceStatus);
    result.setData(CoreUserProfileDTOHelper.convertCoreUserProfileDTOsToVOs(remoteResult.getCoreUserProfileDTOs()));
    return result;
  }

  /**
   * get a team info and its sub teams
   *
   * @param teamId
   * @return
   */
  @LogAround

  @RequestMapping(value = "/teams/{teamId}/subordinate",
          method = RequestMethod.GET,
          produces = "application/json")
  @ResponseBody
  public Result<Object> listLevelOneTeamsAndTeamMembers(@PathVariable String teamId) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long decryptTeamId = getDecryptValueFromString(teamId);

    LOGGER.info("listTeams()-request: encruptedTeamId={}, orgId={}", decryptTeamId, orgId);

    Result<Object> result = new Result<>();
    TeamListDTO remoteResult = facadeFactory.getUserFacade().listSubordinateTeamsAndMembers(orgId, decryptTeamId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    LOGGER.info("remoteResult:{}", remoteResult);

    /*if (remoteResult.getTeamDTOList() == null || remoteResult.getTeamDTOList().size() == 0) {
      result.setCodeAndMsg(serviceStatus);
      return result;
    }*/

    List<TeamVO> teamVOList = new ArrayList<>();
    for (TeamDTO teamDTO : remoteResult.getTeamDTOList()) {
      TeamVO teamVO = new TeamVO();
      BeanUtils.copyProperties(teamDTO, teamVO);
      teamVOList.add(teamVO);
    }

    List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
    for (CoreUserProfileDTO coreUserProfileDTO : remoteResult.getCoreUserProfileDTOs()) {
      CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
      BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
      coreUserProfileVOs.add(coreUserProfileVO);
    }

    TeamListVO teamListVO = new TeamListVO();
    teamListVO.setTeamVOs(teamVOList);
    teamListVO.setCoreUserProfileVOs(coreUserProfileVOs);

    result.setCodeAndMsg(serviceStatus);
    result.setData(teamListVO);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/teams/{teamId}/ancestor-children",
          method = RequestMethod.GET,
          produces = "application/json")
  @ResponseBody
  public Result<Object> listAncestorAndChildrenTeams(@PathVariable String teamId) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();
    long decryptTeamId = getDecryptValueFromString(teamId);

    /*if (decryptTeamId == 0L) {
      LongDTO remoteTeamId= facadeFactory.getUserFacade().getTeamIdByUserId(orgId, actorUserId, actorUserId, adminUserId);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteTeamId.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        throw new ServiceStatusException(serviceStatus);
      }
      decryptTeamId = remoteTeamId.getData();
    } else if (decryptTeamId == -1L) {
      TeamListDTO teamListDTO = facadeFactory.getUserFacade().listNextLevelTeams(orgId, 0L, actorUserId, adminUserId);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(teamListDTO.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK || teamListDTO.getTeamDTOList().size() != 1) {
        LOGGER.error("listNextLevelTeams-error() in getUsersFromTeam");
        throw new ServiceStatusException(serviceStatus);
      }
      decryptTeamId = teamListDTO.getTeamDTOList().get(0).getTeamId();
    }*/

    LOGGER.info("listUpLineTeamsByTeamId()-request: encruptedTeamId={}, orgId={}", decryptTeamId, orgId);

    Result<Object> result = new Result<>();
    TeamListDTO remoteResult = facadeFactory.getUserFacade().listUpTeamLineByTeamId(orgId, decryptTeamId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    LOGGER.info("remoteResult:{}", remoteResult);
    List<TeamVO> upTeamVOList = new ArrayList<>();
    for (TeamDTO teamDTO : remoteResult.getTeamDTOList()) {
      TeamVO teamVO = new TeamVO();
      BeanUtils.copyProperties(teamDTO, teamVO);
      upTeamVOList.add(teamVO);
    }

    remoteResult = facadeFactory.getUserFacade().listNextLevelTeams(orgId, decryptTeamId, actorUserId, adminUserId);
    serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    LOGGER.info("remoteResult:{}", remoteResult);
    List<TeamVO> subTeamVOList = new ArrayList<>();
    for (TeamDTO teamDTO : remoteResult.getTeamDTOList()) {
      TeamVO teamVO = new TeamVO();
      BeanUtils.copyProperties(teamDTO, teamVO);
      subTeamVOList.add(teamVO);
    }

    Map<String, List<TeamVO>> map = new HashMap<>();
    map.put("ancestorTeams", upTeamVOList);
    map.put("childrenTeams", subTeamVOList);

    result.setCodeAndMsg(serviceStatus);
    result.setData(map);
    return result;
  }

  @LogAround

  @ResponseBody
  @RequestMapping(value = "/teams/transfer", method = RequestMethod.PUT, produces = "application/json")
  public Result<Object> transferTeamAndMember(@RequestBody TransferTeamVO transferTeamVO) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId, ResourceCode.USER_ORG_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.EDIT.getCode());

    Result<Object> result = new Result<>();

    List<Long> teamIds = new ArrayList<>();
    List<Long> userIds = new ArrayList<>();
    for (IdVO id : transferTeamVO.getTeamIds()) {
      teamIds.add(id.getIdValue());
    }
    for (IdVO id : transferTeamVO.getUserIds()) {
      userIds.add(id.getIdValue());
    }
    long toTeamId = transferTeamVO.getToTeamId();
    VoidDTO remoteResult = facadeFactory.getUserFacade().transferTeamsAndTeamMembers(orgId, teamIds, userIds,
            toTeamId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  /**
   * assign a user to team
   *
   * @param teamId
   * @param payload
   * @return
   */
  @LogAround

  @RequestMapping(value = "/teams/{teamId}/users",
          method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> assignUsersToTeam(@PathVariable String teamId, @RequestBody JSONObject payload) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId, ResourceCode.USER_ORG_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.EDIT.getCode());

    long decryptTeamId = -1;
    List<Long> userIds = new ArrayList<>();
    try {
      JSONArray jsonArray = payload.getJSONArray(ParamName.PAYLOAD_PARAM_USER_ID_LIST);
      for (int i = 0; i < jsonArray.size(); i++) {
        String encryptUserId = jsonArray.getString(i);
        long userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptUserId));
        userIds.add(userId);
      }
      decryptTeamId = Long.parseLong(EncryptUtils.symmetricDecrypt(teamId));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    LOGGER.info("assignUserToTeam()-request: userIds={}, teamId={}, orgId={}", userIds, decryptTeamId, orgId);

    Result<Object> result = new Result<>();
    BooleanDTO remoteResult = facadeFactory.getUserFacade().assignUsersToTeam(orgId, userIds, decryptTeamId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/teams/team-member/set-team-admin",
          method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> setTeamAdmin(
          @RequestBody TeamMemberVO teamMemberVO) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    permissionChecker.permissionCheck(orgId, actorUserId, orgId, ResourceCode.USER_ORG_ADMIN.getResourceCode(),
            ResourceType.ORG.getCode(), ActionCode.EDIT.getCode());

    TeamMemberDTO teamMemberDTO = new TeamMemberDTO();
    BeanUtils.copyProperties(teamMemberVO, teamMemberDTO);

    Result<Object> result = new Result<>();
    VoidDTO remoteResult = facadeFactory.getUserFacade().updateTeamAdmin(
            orgId, teamMemberDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  /**
   * get users from team
   *
   * @param
   * @return
   */
  @LogAround

  @RequestMapping(value = "/teams/{teamId}/users",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getUsersFromTeam(@PathVariable String teamId,
                                         @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                         @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                         @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if(!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    long decryptTeamId = getDecryptValueFromString(teamId);

    /*if (decryptTeamId == 0L) {
      LongDTO remoteTeamId= facadeFactory.getUserFacade().getTeamIdByUserId(orgId, actorUserId, actorUserId, adminUserId);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteTeamId.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        throw new ServiceStatusException(serviceStatus);
      }
      decryptTeamId = remoteTeamId.getData();
    } else if (decryptTeamId == -1L) {
      TeamListDTO teamListDTO = facadeFactory.getUserFacade().listNextLevelTeams(orgId, 0L, actorUserId, adminUserId);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(teamListDTO.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK || teamListDTO.getTeamDTOList().size() != 1) {
        LOGGER.error("listNextLevelTeams-error() in getUsersFromTeam");
        throw new ServiceStatusException(serviceStatus);
      }
      decryptTeamId = teamListDTO.getTeamDTOList().get(0).getTeamId();
    }*/

    LOGGER.info("getUsersFromTeam()-request: teamId={}, orgId={}", decryptTeamId, orgId);

    Result<Object> result = new Result<>();
    Map<String, Object> map = new HashMap<>();

    UserNameListDTO remoteResult = facadeFactory.getUserFacade().listTeamMembers(orgId, decryptTeamId, keyword, pageNumber, pageSize,
            actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

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

    List<CoreUserProfileDTO> profileWithoutEnrollDate = new ArrayList<>();
    Iterator<CoreUserProfileDTO> it = coreUserProfileDTOs.iterator();
    while (it.hasNext()) {
      CoreUserProfileDTO coreUserProfileDTO = it.next();
      if (null == coreUserProfileDTO.getEnrollDate()) {
        profileWithoutEnrollDate.add(coreUserProfileDTO);
        it.remove();
      }
    }

    Collections.sort(coreUserProfileDTOs, (arg0, arg1) -> arg1.getEnrollDate().compareTo(arg0.getEnrollDate()));
    coreUserProfileDTOs.addAll(profileWithoutEnrollDate);

    for (CoreUserProfileDTO coreUserProfileDTO : coreUserProfileDTOs) {
      CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
      BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
      if (null != coreUserProfileDTO.getUserEmploymentDTO()) {
        UserEmploymentVO userEmploymentVO = new UserEmploymentVO();
        BeanUtils.copyProperties(coreUserProfileDTO.getUserEmploymentDTO(), userEmploymentVO);
        coreUserProfileVO.setUserEmploymentVO(userEmploymentVO);
      }
      coreUserProfileVOs.add(coreUserProfileVO);
    }

    boolean managable = false;
    RoleListDTO remoteRoles = facadeFactory.getSecurityModelFacade().getRoleListDTOByUserId(orgId, actorUserId, actorUserId, adminUserId);
    serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    for (RoleDTO roleDTO : remoteRoles.getRoleDTOList()) {
      if (roleDTO.getRoleName().equals(DefaultRole.HR.getName())) {
        managable = true;
      }
    }

    map.put("UserProfiles", coreUserProfileVOs);
    map.put("totalRecordNum", remoteResult.getTotalRecordNum());
    map.put("managable", managable);

    result.setData(map);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/teams/{teamId}/users/search",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> searchUsersFromTeam(@PathVariable String teamId,
                                            @RequestParam(value = "keyword", defaultValue = "") String keyword,
                                            @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                            @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if(!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    long decryptTeamId = getDecryptValueFromString(teamId);

    Result<Object> result = new Result<>();
    Map<String, Object> map = new HashMap<>();

    boolean permitted = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, 0L, decryptTeamId,
            ResourceCode.USER_ORG.getResourceCode(), ResourceType.TEAM.getCode(), ActionCode.READ.getCode());

    if (!permitted) {
      result.setCodeAndMsg(ServiceStatus.COMMON_PERMISSION_DENIED);
    }

    UserNameListDTO remoteResult = facadeFactory.getCommonToolFacade().searchUsersWithTeamScope(orgId, decryptTeamId, keyword,
            pageNumber, pageSize, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

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

  private long getDecryptValueFromString(String encryptValue) {
    long decryptValue = -1;
    try {
      decryptValue = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptValue));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    return decryptValue;
  }

  public static void main(String[] args) throws Exception {
    System.out.println(EncryptUtils.symmetricEncrypt("-1"));
  }
}
