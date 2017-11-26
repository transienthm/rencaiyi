package hr.wozai.service.api.controller.okr;

import hr.wozai.service.api.helper.CoreUserProfileDTOHelper;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.component.PermissionChecker;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.okr.*;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import hr.wozai.service.user.client.okr.dto.*;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.client.okr.enums.PeriodTimeSpan;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.TeamMemberDTO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
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
import java.math.BigDecimal;
import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/2/8
 */
@Controller("okrController")
public class OkrController {
  private static final Logger LOGGER = LoggerFactory.getLogger(OkrController.class);

  @Autowired
  PermissionUtil permissionUtil;

  @Autowired
  PermissionChecker permissionChecker;

  @Autowired
  FacadeFactory facadeFactory;

  @LogAround
  @RequestMapping(value = "/okrs/objective-periods", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> createObjectivePeriod(@RequestBody ObjectivePeriodVO objectivePeriodVO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    if (objectivePeriodVO.getType() == OkrType.ORG.getCode()) {
      objectivePeriodVO.setOwnerId(orgId);
    }

    //setNameAndOwnerIdInObjectivePeriod(orgId, objectivePeriodVO, actorUserId, adminUserId);
    permissionChecker.permissionCheck(orgId, actorUserId, objectivePeriodVO.getOwnerId(),
            ResourceCode.OKR_PERIOD.getResourceCode(),
            getResourceTypeFromOkrType(objectivePeriodVO.getType()), ActionCode.CREATE.getCode());

    ObjectivePeriodDTO objectivePeriodDTO = new ObjectivePeriodDTO();
    BeanUtils.copyProperties(objectivePeriodVO, objectivePeriodDTO);

    objectivePeriodDTO.setOrgId(orgId);
    objectivePeriodDTO.setCreatedUserId(actorUserId);
    objectivePeriodDTO.setLastModifiedUserId(actorUserId);
    LOGGER.info("createObjectivePeriod()-request: objectivePeriodDTO={}",
            objectivePeriodDTO);

    LongDTO remoteResult = facadeFactory.getOkrFacade().createObjectivePeriod(objectivePeriodDTO, actorUserId, adminUserId);
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

  @LogAround
  private void setNameAndOwnerIdInObjectivePeriod(long orgId, ObjectivePeriodVO objectivePeriodVO,
                                                  long actorUserId, long adminUserId) {

    objectivePeriodVO.setName(PeriodTimeSpan.getNameByYearAndPeriodTimeSpan(objectivePeriodVO.getYear(),
            PeriodTimeSpan.getEnumByCode(objectivePeriodVO.getPeriodTimeSpanId())));

    int type = objectivePeriodVO.getType();
    if (objectivePeriodVO.getOwnerId() == null) {
      if (type == OkrType.ORG.getCode()) {
        objectivePeriodVO.setOwnerId(orgId);
      } else if (type == OkrType.TEAM.getCode()) {
        TeamMemberDTO teamMemberDTO = facadeFactory.getUserFacade().getTeamMemberByUserId(
                orgId, actorUserId, actorUserId, adminUserId);
        facadeFactory.checkServiceStatus(teamMemberDTO.getServiceStatusDTO(), ServiceStatus.COMMON_OK);
        objectivePeriodVO.setOwnerId(teamMemberDTO.getTeamId());
      } else if (type == OkrType.PERSON.getCode()) {
        objectivePeriodVO.setOwnerId(actorUserId);
      } else if (type == OkrType.PROJECT_TEAM.getCode()) {
        // ownerId本身就是projectTeamId
        ;
      } else {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM, "objectiveperiod type is wrong");
      }
    }
  }

  @LogAround

  @RequestMapping(value = "/okrs/objective-periods/{periodId}", method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> deleteObjectivePeriod(@PathVariable("periodId") String encryptPeriodId) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptPeriodId = getDecryptValueFromString(encryptPeriodId);

    ObjectivePeriodDTO periodDTO = facadeFactory.getOkrFacade().getObjectivePeriod(orgId, decryptPeriodId, actorUserId, adminUserId);
    facadeFactory.checkServiceStatus(periodDTO.getServiceStatusDTO(), ServiceStatus.COMMON_OK);

    permissionChecker.permissionCheck(orgId, actorUserId, periodDTO.getOwnerId(),
            ResourceCode.OKR_PERIOD.getResourceCode(),
            getResourceTypeFromOkrType(periodDTO.getType()), ActionCode.DELETE.getCode());

    VoidDTO remoteResult = facadeFactory.getOkrFacade().deleteObjectivePeriod(orgId, decryptPeriodId, actorUserId, adminUserId);
    facadeFactory.checkServiceStatus(remoteResult.getServiceStatusDTO(), ServiceStatus.COMMON_OK);

    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    return result;
  }

  @LogAround
  @RequestMapping(value = "/okrs/objective-periods/{periodId}", method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> updateObjectivePeriod(@PathVariable("periodId") String encryptPeriodId,
                                              @RequestBody ObjectivePeriodDTO objectivePeriodDTO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptPeriodId = getDecryptValueFromString(encryptPeriodId);

    ObjectivePeriodDTO periodDTO = facadeFactory.getOkrFacade().getObjectivePeriod(orgId, decryptPeriodId, actorUserId, adminUserId);
    facadeFactory.checkServiceStatus(periodDTO.getServiceStatusDTO(), ServiceStatus.COMMON_OK);

    permissionChecker.permissionCheck(orgId, actorUserId, periodDTO.getOwnerId(),
            ResourceCode.OKR_PERIOD.getResourceCode(),
            getResourceTypeFromOkrType(periodDTO.getType()), ActionCode.EDIT.getCode());

    objectivePeriodDTO.setObjectivePeriodId(decryptPeriodId);
    objectivePeriodDTO.setOrgId(orgId);
    objectivePeriodDTO.setLastModifiedUserId(actorUserId);

    VoidDTO remoteResult = facadeFactory.getOkrFacade().updateObjectivePeriod(objectivePeriodDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objective-periods", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listObjectivePeriod(@RequestParam("type") int type,
                                            @RequestParam(value = "ownerId", required = false, defaultValue = "") String encryptOwnerId) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long ownerId;
    if (type == OkrType.ORG.getCode()) {
      ownerId = orgId;
    } else {
      ownerId = getDecryptValueFromString(encryptOwnerId);
    }

    boolean creatable;
    boolean deletable;
    int resourceType = getResourceTypeFromOkrType(type);

    creatable = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, 0L,
            ownerId, ResourceCode.OKR_PERIOD.getResourceCode(), resourceType, ActionCode.CREATE.getCode());
    deletable = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, 0L,
            ownerId, ResourceCode.OKR_PERIOD.getResourceCode(), resourceType, ActionCode.DELETE.getCode());


    ObjectivePeriodListDTO remoteResult = facadeFactory.getOkrFacade().listObjectivePeriod(orgId, type, ownerId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<ObjectivePeriodVO> objectivePeriodVOs = new ArrayList<>();
    for (ObjectivePeriodDTO objectivePeriodDTO : remoteResult.getPeriodDTOList()) {
      ObjectivePeriodVO objectivePeriodVO = new ObjectivePeriodVO();
      BeanUtils.copyProperties(objectivePeriodDTO, objectivePeriodVO);
      objectivePeriodVOs.add(objectivePeriodVO);
    }

    Map<String, Object> map = new HashMap<>();
    map.put("objectivePeriods", objectivePeriodVOs);
    map.put("creatable", creatable);
    map.put("deletable", deletable);

    result.setCodeAndMsg(serviceStatus);
    result.setData(map);
    return result;
  }

  @LogAround
  @RequestMapping(value = "/okrs/objectives",
          method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<Object> createObjective(
          @RequestBody @Valid ObjectiveVO objectiveVO,
          BindingResult bindingResult) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    ObjectivePeriodDTO periodDTO = facadeFactory.getOkrFacade().getObjectivePeriod(orgId, objectiveVO.getObjectivePeriodId(),
            actorUserId, adminUserId);
    facadeFactory.checkServiceStatus(periodDTO.getServiceStatusDTO(), ServiceStatus.COMMON_OK);

    permissionChecker.permissionCheck(orgId, actorUserId, periodDTO.getOwnerId(),
            ResourceCode.OKR.getResourceCode(),
            getResourceTypeFromOkrType(periodDTO.getType()), ActionCode.CREATE.getCode());


    ObjectiveDTO objectiveDTO = new ObjectiveDTO();
    copyObjectiveVOToDTO(objectiveVO, orgId, actorUserId, objectiveDTO);
    LOGGER.info("createObjective()-request: objectiveDTO={}", objectiveDTO);
    if (objectiveDTO.getParentObjectiveId() == null) {
      objectiveDTO.setParentObjectiveId(0L);
    }

    LongDTO remoteResult = facadeFactory.getOkrFacade().createObjective(objectiveDTO, actorUserId, adminUserId);
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

  @LogAround

  @RequestMapping(value = "/okrs/objectives/{objectiveId}",
          method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> deleteObjective(@PathVariable("objectiveId") String encryptObjectiveId) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptObjectiveId = getDecryptValueFromString(encryptObjectiveId);

    ObjectivePeriodDTO periodDTO = facadeFactory.getOkrFacade().getObjectivePeriodWithObjectiveId(orgId, decryptObjectiveId,
            actorUserId, adminUserId);
    facadeFactory.checkServiceStatus(periodDTO.getServiceStatusDTO(), ServiceStatus.COMMON_OK);

    permissionChecker.permissionCheck(orgId, actorUserId, periodDTO.getOwnerId(),
            ResourceCode.OKR.getResourceCode(),
            getResourceTypeFromOkrType(periodDTO.getType()), ActionCode.DELETE.getCode());


    VoidDTO remoteResult = facadeFactory.getOkrFacade().deleteObjective(orgId, decryptObjectiveId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  @LogAround
  @BindingResultMonitor
  @RequestMapping(value = "/okrs/objectives/{objectiveId}",
          method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> updateObjective(
          @PathVariable("objectiveId") String encryptObjectiveId,
          @RequestBody @Valid ObjectiveVO objectiveVO,
          BindingResult bindingResult) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptObjectiveId = getDecryptValueFromString(encryptObjectiveId);

    ObjectivePeriodDTO periodDTO = facadeFactory.getOkrFacade().getObjectivePeriodWithObjectiveId(orgId, decryptObjectiveId,
            actorUserId, adminUserId);
    facadeFactory.checkServiceStatus(periodDTO.getServiceStatusDTO(), ServiceStatus.COMMON_OK);

    permissionChecker.permissionCheck(orgId, actorUserId, periodDTO.getOwnerId(),
            ResourceCode.OKR.getResourceCode(),
            getResourceTypeFromOkrType(periodDTO.getType()), ActionCode.EDIT.getCode());

    ObjectiveDTO objectiveDTO = new ObjectiveDTO();
    copyObjectiveVOToDTO(objectiveVO, orgId, actorUserId, objectiveDTO);
    objectiveDTO.setObjectiveId(decryptObjectiveId);
    if (objectiveDTO.getComment() == null) {
      objectiveDTO.setComment("");
    }

    if (null != objectiveVO.getDirectorList()) {
      List<DirectorDTO> directorDTOs = new ArrayList<>();
      copyDirectorVOToDTO(objectiveVO.getDirectorList(), directorDTOs, orgId, actorUserId);
      objectiveDTO.setDirectorDTOList(directorDTOs);
    } else {
      objectiveDTO.setDirectorDTOList(null);
    }
    LOGGER.info("updateObjective()-request: objectiveDTO={}", objectiveDTO);

    VoidDTO remoteResult = facadeFactory.getOkrFacade().updateObjective(objectiveDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objectives/{objectiveId}",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getObjective(@PathVariable("objectiveId") String encryptObjectiveId) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptObjectiveId = getDecryptValueFromString(encryptObjectiveId);

    ObjectiveDTO remoteResult = facadeFactory.getOkrFacade().getObjective(orgId, decryptObjectiveId, actorUserId, adminUserId);

    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    ObjectiveVO objectiveVO = new ObjectiveVO();
    copyObjectiveDTOToVO(remoteResult, objectiveVO);

    int type = remoteResult.getType();
    long ownerId = remoteResult.getOwnerId();
    long objId = remoteResult.getObjectiveId();
    int resourceType = getResourceTypeFromOkrType(type);

    boolean editable = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, objId, ownerId,
            ResourceCode.OKR.getResourceCode(),
            resourceType, ActionCode.EDIT.getCode());
    objectiveVO.setEditable(editable);

    result.setCodeAndMsg(serviceStatus);
    result.setData(objectiveVO);
    return result;
  }

  @LogAround
  @RequestMapping(value = "/okrs/objectives",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listObjective(
          @RequestParam("periodId") String encryptPeriodId,
          @RequestParam(value = "aboutMe", defaultValue = "false", required = false) boolean aboutMe,
          @RequestParam(value = "progressStatus", defaultValue = "1", required = false) Integer progressStatus,
          @RequestParam(value = "orderBy", defaultValue = "1", required = false) Integer orderBy) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    boolean objectiveCreatle;
    boolean objectiveDeletable;
    boolean objectiveEditable;

    long decryptPeriodId = getDecryptValueFromString(encryptPeriodId);
    ObjectivePeriodDTO periodDTO = facadeFactory.getOkrFacade().getObjectivePeriod(orgId, decryptPeriodId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(periodDTO.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }
    int type = periodDTO.getType();

    int resourceType = getResourceTypeFromOkrType(type);


    ObjectiveListDTO remoteResult = facadeFactory.getOkrFacade().listObjective(orgId, type, periodDTO.getOwnerId(),
            decryptPeriodId, aboutMe, progressStatus, orderBy, actorUserId, adminUserId);
    serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<ObjectiveVO> objectiveVOs = new ArrayList<>();
    for (ObjectiveDTO objectiveDTO : remoteResult.getObjectiveDTOList()) {
      ObjectiveVO objectiveVO = new ObjectiveVO();
      copyObjectiveDTOToVO(objectiveDTO, objectiveVO);

      CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
      BeanUtils.copyProperties(objectiveDTO.getLastModifiedUserProfile(), coreUserProfileVO);
      objectiveVO.setLastModifiedUserProfile(coreUserProfileVO);
      objectiveVOs.add(objectiveVO);
    }

    objectiveDeletable = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, 0L, periodDTO.getOwnerId(),
            ResourceCode.OKR.getResourceCode(),
            resourceType, ActionCode.DELETE.getCode());

    objectiveCreatle = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, 0L, periodDTO.getOwnerId(),
            ResourceCode.OKR.getResourceCode(),
            resourceType, ActionCode.CREATE.getCode());

    objectiveEditable = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, 0L, periodDTO.getOwnerId(),
            ResourceCode.OKR.getResourceCode(),
            resourceType, ActionCode.EDIT.getCode());

    Map map = new HashMap<>();
    map.put("objectiveVOList", objectiveVOs);
    map.put("totalProgress", new BigDecimal(remoteResult.getTotalProgress()).stripTrailingZeros().toPlainString());
    map.put("beginTimestamp", remoteResult.getBeginTimestamp());
    map.put("endTimestamp", remoteResult.getEndTimestamp());
    map.put("objectiveCreatable", objectiveCreatle);
    map.put("objectiveDeletable", objectiveDeletable);
    map.put("objectiveEditable", objectiveEditable);

    result.setCodeAndMsg(serviceStatus);
    result.setData(map);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objectives/{objectiveId}/move",
          method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> moveObjective(
          @PathVariable("objectiveId") String encryptObjectiveId,
          @RequestBody ObjectiveVO objectiveVO) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long objectiveId = getDecryptValueFromString(encryptObjectiveId);
    int targetOrderIndex = objectiveVO.getOrderIndex();

    ObjectivePeriodDTO periodDTO = facadeFactory.getOkrFacade().getObjectivePeriodWithObjectiveId(orgId, objectiveId,
            actorUserId, adminUserId);
    facadeFactory.checkServiceStatus(periodDTO.getServiceStatusDTO(), ServiceStatus.COMMON_OK);

    permissionChecker.permissionCheck(orgId, actorUserId, periodDTO.getOwnerId(),
            ResourceCode.OKR.getResourceCode(),
            getResourceTypeFromOkrType(periodDTO.getType()), ActionCode.EDIT.getCode());

    VoidDTO remoteResult = facadeFactory.getOkrFacade().moveObjective(orgId, objectiveId, targetOrderIndex, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround
  @RequestMapping(value = "/okrs/filter-objectives",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> filterObjectives(
          @RequestParam(value = "priority", defaultValue = "-1", required = false) Integer priority,
          @RequestParam(value = "progressStatus", defaultValue = "1", required = false) Integer progressStatus,
          @RequestParam(value = "aboutMe", defaultValue = "false", required = false) boolean aboutMe,
          @RequestParam(value = "orderItem", defaultValue = "1", required = false) Integer orderItem,
          @RequestParam(value = "pageNumber", defaultValue = "1", required = false) Integer pageNumber,
          @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if(!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    ObjectiveListDTO remoteResult = facadeFactory.getOkrFacade().filterObjectives(
            orgId, priority, progressStatus, aboutMe, orderItem, pageNumber, pageSize, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<ObjectiveVO> objectiveVOs = new ArrayList<>();
    for (ObjectiveDTO objectiveDTO : remoteResult.getObjectiveDTOList()) {
      ObjectiveVO objectiveVO = new ObjectiveVO();
      copyObjectiveDTOToVO(objectiveDTO, objectiveVO);
      objectiveVOs.add(objectiveVO);
    }

    Map map = new HashMap<>();
    map.put("objectiveVOList", objectiveVOs);
    map.put("totalNumber", remoteResult.getTotalNumber());
    result.setCodeAndMsg(serviceStatus);
    result.setData(map);

    return result;
  }

  @LogAround

  @BindingResultMonitor
  @RequestMapping(value = "/okrs/key-results",
          method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> createKeyResult(
          @RequestBody @Valid KeyResultVO keyResultVO,
          BindingResult bindingResult) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    ObjectivePeriodDTO periodDTO = facadeFactory.getOkrFacade().getObjectivePeriodWithObjectiveId(orgId, keyResultVO.getObjectiveId(),
            actorUserId, adminUserId);
    facadeFactory.checkServiceStatus(periodDTO.getServiceStatusDTO(), ServiceStatus.COMMON_OK);

    permissionChecker.permissionCheck(orgId, actorUserId, periodDTO.getOwnerId(),
            ResourceCode.OKR.getResourceCode(),
            getResourceTypeFromOkrType(periodDTO.getType()), ActionCode.CREATE.getCode());


    KeyResultDTO keyResultDTO = new KeyResultDTO();
    copyKeyResultVOToDTO(keyResultVO, orgId, actorUserId, keyResultDTO);
    LOGGER.info("createKeyResult()-request: keyResultDTO={}", keyResultDTO);

    LongDTO remoteResult = facadeFactory.getOkrFacade().createKeyResult(keyResultDTO, actorUserId, adminUserId);
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

  @LogAround
  @RequestMapping(value = "/okrs/key-results/{keyResultId}",
          method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> deletekeyResult(@PathVariable("keyResultId") String encryptKeyResultId) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptKeyResultId = getDecryptValueFromString(encryptKeyResultId);

    ObjectivePeriodDTO periodDTO = facadeFactory.getOkrFacade().getObjectivePeriodWithKeyResultId(orgId, decryptKeyResultId,
            actorUserId, adminUserId);
    facadeFactory.checkServiceStatus(periodDTO.getServiceStatusDTO(), ServiceStatus.COMMON_OK);

    permissionChecker.permissionCheck(orgId, actorUserId, periodDTO.getOwnerId(),
            ResourceCode.OKR.getResourceCode(),
            getResourceTypeFromOkrType(periodDTO.getType()), ActionCode.DELETE.getCode());


    VoidDTO remoteResult = facadeFactory.getOkrFacade().deleteKeyResult(orgId, decryptKeyResultId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  @LogAround

  @BindingResultMonitor
  @RequestMapping(value = "/okrs/key-results/{keyResultId}",
          method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  public Result<Object> updateKeyResult(
          @PathVariable("keyResultId") String encryptKeyResultId,
          @RequestBody @Valid KeyResultVO keyResultVO,
          BindingResult bindingResult) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptKeyResultId = getDecryptValueFromString(encryptKeyResultId);

    ObjectivePeriodDTO periodDTO = facadeFactory.getOkrFacade().getObjectivePeriodWithKeyResultId(orgId, decryptKeyResultId,
            actorUserId, adminUserId);
    facadeFactory.checkServiceStatus(periodDTO.getServiceStatusDTO(), ServiceStatus.COMMON_OK);

    permissionChecker.permissionCheck(orgId, actorUserId, periodDTO.getOwnerId(),
            ResourceCode.OKR.getResourceCode(),
            getResourceTypeFromOkrType(periodDTO.getType()), ActionCode.EDIT.getCode());


    KeyResultDTO keyResultDTO = new KeyResultDTO();
    copyKeyResultVOToDTO(keyResultVO, orgId, actorUserId, keyResultDTO);
    keyResultDTO.setKeyResultId(decryptKeyResultId);
    if (keyResultDTO.getComment() == null) {
      keyResultDTO.setComment("");
    }

    if (keyResultVO.getDirectorList() != null) {
      List<DirectorDTO> directorDTOs = new ArrayList<>();
      copyDirectorVOToDTO(keyResultVO.getDirectorList(), directorDTOs, orgId, actorUserId);
      keyResultDTO.setDirectorDTOList(directorDTOs);
    } else {
      keyResultDTO.setDirectorDTOList(null);
    }
    LOGGER.info("updateKeyResult()-request: keyResultDTO={}", keyResultDTO);

    VoidDTO remoteResult = facadeFactory.getOkrFacade().updadteKeyResult(keyResultDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(serviceStatus);
    return result;
  }

  @LogAround
  @RequestMapping(value = "/okrs/objectives/search",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> searchObjective(
          @RequestParam(value = "objectiveId", required = false, defaultValue = "") String encryptObjectiveId,
          @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
          @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
          @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize) {
    Result<Object> result = new Result<>();

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if (!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptObjectiveId;
    if (encryptObjectiveId.isEmpty()) {
      decryptObjectiveId = 0L;
    } else {
      decryptObjectiveId = getDecryptValueFromString(encryptObjectiveId);
    }

    ObjectiveListDTO remoteResult = facadeFactory.getOkrFacade().searchObjectiveByKeywordInOrder(
            orgId, decryptObjectiveId, keyword, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<ObjectiveVO> objectiveVOs = new ArrayList<>();
    for (ObjectiveDTO objectiveDTO : remoteResult.getObjectiveDTOList()) {
      ObjectiveVO objectiveVO = new ObjectiveVO();
      BeanUtils.copyProperties(objectiveDTO, objectiveVO);
      objectiveVOs.add(objectiveVO);
    }

    result.setCodeAndMsg(serviceStatus);
    result.setData(objectiveVOs);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objectives/{objectiveId}/ancester",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listObjectiveAncester(@PathVariable("objectiveId") String encryptObjectiveId) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptObjectiveId = getDecryptValueFromString(encryptObjectiveId);

    ObjectiveListDTO remoteResult = facadeFactory.getOkrFacade().listAncesterObjectives(orgId, decryptObjectiveId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<ObjectiveVO> objectiveVOs = new ArrayList<>();
    for (ObjectiveDTO objectiveDTO : remoteResult.getObjectiveDTOList()) {
      ObjectiveVO objectiveVO = new ObjectiveVO();
      copyObjectiveDTOToVO(objectiveDTO, objectiveVO);
      objectiveVOs.add(objectiveVO);
    }

    result.setCodeAndMsg(serviceStatus);
    result.setData(objectiveVOs);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objectives/{objectiveId}/subordinate",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listObjectiveSubordinate(@PathVariable("objectiveId") String encryptObjectiveId) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptObjectiveId = getDecryptValueFromString(encryptObjectiveId);

    ObjectiveListDTO remoteResult = facadeFactory.getOkrFacade().listFirstLevelSubordinateObjectives(
            orgId, decryptObjectiveId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<ObjectiveVO> objectiveVOs = new ArrayList<>();
    for (ObjectiveDTO objectiveDTO : remoteResult.getObjectiveDTOList()) {
      ObjectiveVO objectiveVO = new ObjectiveVO();
      copyObjectiveDTOToVO(objectiveDTO, objectiveVO);
      objectiveVOs.add(objectiveVO);
    }

    result.setCodeAndMsg(serviceStatus);
    result.setData(objectiveVOs);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objectives/{objectiveId}/bird-view",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getAerialViewByObjectiveId(@PathVariable("objectiveId") String encryptObjectiveId) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptObjectiveId = getDecryptValueFromString(encryptObjectiveId);

    ObjectiveTreeDTO remoteResult = facadeFactory.getOkrFacade().getBirdViewByObjectiveId(
            orgId, decryptObjectiveId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    List<List<SimpleObjectiveVO>> objectiveTreeVOs = new ArrayList<>();
    for (List<ObjectiveDTO> objectiveDTOs : remoteResult.getObjectiveTrees()) {
      List<SimpleObjectiveVO> objectiveVOs = new ArrayList<>();
      for (ObjectiveDTO objectiveDTO : objectiveDTOs) {
        SimpleObjectiveVO objectiveVO = new SimpleObjectiveVO();
        BeanUtils.copyProperties(objectiveDTO, objectiveVO);
        objectiveVOs.add(objectiveVO);
      }
      objectiveTreeVOs.add(objectiveVOs);
    }

    result.setCodeAndMsg(serviceStatus);
    result.setData(objectiveTreeVOs);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objectives/okr-comments",
          method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<Object> updateOkrComment(
          @RequestBody @Valid OkrCommentVO okrCommentVO,
          BindingResult bindingResult) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    OkrCommentDTO okrCommentDTO = new OkrCommentDTO();
    BeanUtils.copyProperties(okrCommentVO, okrCommentDTO);
    LongDTO remoteResult = facadeFactory.getOkrFacade().addOkrComment(
            orgId, okrCommentDTO, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());

    result.setCodeAndMsg(serviceStatus);
    result.setData(remoteResult.getData());

    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objectives/okr-comments/{okrCommentId}",
          method = RequestMethod.PUT, produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<Object> updateOkrComment(
          @PathVariable("okrCommentId") String encryptOkrCommentId,
          @RequestBody @Valid ObjectiveVO objectiveVO,
          BindingResult bindingResult) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptOkrCommentId = getDecryptValueFromString(encryptOkrCommentId);

    VoidDTO remoteResult = facadeFactory.getOkrFacade().updateOkrComment(
            orgId, decryptOkrCommentId, objectiveVO.getComment(), actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());

    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objectives/okr-comments/{okrCommentId}",
          method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<Object> deleteOkrComment(@PathVariable("okrCommentId") String encryptOkrCommentId) {
    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptOkrCommentId = getDecryptValueFromString(encryptOkrCommentId);

    VoidDTO remoteResult = facadeFactory.getOkrFacade().deleteOkrComment(orgId, decryptOkrCommentId, actorUserId, adminUserId);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());

    result.setCodeAndMsg(serviceStatus);

    return result;
  }

  @LogAround

  @RequestMapping(value = "/okrs/objectives/{objectiveId}/okr-comments",
          method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listOkrCommentsOfObjective(
          @PathVariable("objectiveId") String encryptObjectiveId,
          @RequestParam(value = "keyResultId", required = false, defaultValue = "") String encryptedKeyResultId,
          @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
          @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
    Result<Object> result = new Result<>();

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if (!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    long decryptObjectiveId = getDecryptValueFromString(encryptObjectiveId);
    long keyResultId = 0L;
    if (!encryptedKeyResultId.isEmpty()) {
      keyResultId = getDecryptValueFromString(encryptedKeyResultId);
    }

    ObjectiveDTO objectiveDTO = facadeFactory.getOkrFacade().getObjective(orgId, decryptObjectiveId, actorUserId, adminUserId);

    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(objectiveDTO.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    //int type = objectiveDTO.getType();
    /*long ownerId = objectiveDTO.getOwnerId();
    long objId = objectiveDTO.getObjectiveId();*/
    /*int resourceType;
    if (type == OkrType.ORG.getCode()) {
      resourceType = ResourceType.ORG.getCode();
    } else if (type == OkrType.TEAM.getCode()) {
      resourceType = ResourceType.TEAM.getCode();
    } else {
      resourceType = ResourceType.PERSON.getCode();
    }*/

    /*boolean editable = permissionUtil.getPermissionForSingleObj(orgId, actorUserId, objId, ownerId,
            ResourceCode.OKR.getResourceCode(),
            resourceType, ActionCode.EDIT.getCode());*/

    OkrCommentListDTO remoteResult = facadeFactory.getOkrFacade().listOkrComment(
            orgId, decryptObjectiveId, keyResultId, pageNumber, pageSize, actorUserId, adminUserId);
    serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    OkrCommentListVO okrCommentListVO = new OkrCommentListVO();
    List<OkrCommentVO> okrCommentVOs = new ArrayList<>();
    for (OkrCommentDTO okrCommentDTO : remoteResult.getOkrCommentDTOList()) {
      OkrCommentVO okrCommentVO = new OkrCommentVO();
      BeanUtils.copyProperties(okrCommentDTO, okrCommentVO);
      okrCommentVO.setEditable(okrCommentDTO.isEditable());
      okrCommentVO.setDeletable(okrCommentDTO.isDeletable());

      okrCommentVO.setActorUserProfile(
              CoreUserProfileDTOHelper.convertCoreUserProfileDTOToVO(okrCommentDTO.getActorUserProfile()));
      List<OkrUpdateLogDTO> okrUpdateLogDTOs = okrCommentDTO.getOkrUpdateLogDTOList();
      List<OkrUpdateLogVO> okrUpdateLogVOs = new ArrayList<>();
      for (OkrUpdateLogDTO okrUpdateLogDTO : okrUpdateLogDTOs) {
        OkrUpdateLogVO okrUpdateLogVO = new OkrUpdateLogVO();
        BeanUtils.copyProperties(okrUpdateLogDTO, okrUpdateLogVO);
        okrUpdateLogVOs.add(okrUpdateLogVO);
      }
      okrCommentVO.setOkrUpdateLogVOs(okrUpdateLogVOs);
      okrCommentVOs.add(okrCommentVO);
    }
    okrCommentListVO.setOkrCommentVOList(okrCommentVOs);

    okrCommentListVO.setTotalRecordNum(remoteResult.getTotalRecordNum());

    result.setCodeAndMsg(serviceStatus);
    result.setData(okrCommentListVO);

    return result;
  }

  private void copyObjectiveVOToDTO(ObjectiveVO objectiveVO, long orgId, long userId, ObjectiveDTO objectiveDTO) {
    BeanUtils.copyProperties(objectiveVO, objectiveDTO);

    List<DirectorDTO> directorDTOs = new ArrayList<>();
    copyDirectorVOToDTO(objectiveVO.getDirectorList(), directorDTOs, orgId, userId);

    objectiveDTO.setDirectorDTOList(directorDTOs);

    // setOwnerIdInObjectiveDTO(objectiveDTO, orgId, userId);
    objectiveDTO.setOrgId(orgId);
    objectiveDTO.setCreatedUserId(userId);
    objectiveDTO.setLastModifiedUserId(userId);
  }

  private void copyObjectiveDTOToVO(ObjectiveDTO objectiveDTO, ObjectiveVO objectiveVO) {

    // copy其他普通属性
    BeanUtils.copyProperties(objectiveDTO, objectiveVO);

    // 去除小数点后面的0
    if (objectiveVO.getIsAutoCalc() == 0) {
      objectiveVO.setStartingAmount(objectiveDTO.getStartingAmount());
      objectiveVO.setGoalAmount(objectiveDTO.getGoalAmount());
      objectiveVO.setCurrentAmount(objectiveDTO.getCurrentAmount());
    }

    // copy 负责人
    List<DirectorVO> directorVOs = new ArrayList<>();
    copyDirectorDTOTOVO(objectiveDTO.getDirectorDTOList(), directorVOs);
    objectiveVO.setDirectorList(directorVOs);

    // copy KeyResult
    List<KeyResultVO> keyResultVOs = new ArrayList<>();
    for (KeyResultDTO keyResultDTO : objectiveDTO.getKeyResultDTOList()) {
      KeyResultVO keyResultVO = new KeyResultVO();
      copyKeyResultDTOToVO(keyResultDTO, keyResultVO);
      keyResultVOs.add(keyResultVO);
    }
    objectiveVO.setKeyResultList(keyResultVOs);
  }

  private void copyKeyResultVOToDTO(KeyResultVO keyResultVO, long orgId, long userId, KeyResultDTO keyResultDTO) {
    BeanUtils.copyProperties(keyResultVO, keyResultDTO);

    List<DirectorDTO> directorDTOs = new ArrayList<>();
    copyDirectorVOToDTO(keyResultVO.getDirectorList(), directorDTOs, orgId, userId);

    keyResultDTO.setDirectorDTOList(directorDTOs);

    keyResultDTO.setOrgId(orgId);
    keyResultDTO.setCreatedUserId(userId);
    keyResultDTO.setLastModifiedUserId(userId);
  }

  private void copyKeyResultDTOToVO(KeyResultDTO source, KeyResultVO target) {
    BeanUtils.copyProperties(source, target);

    // 去除小数点后面的0
    target.setStartingAmount(source.getStartingAmount());
    target.setGoalAmount(source.getGoalAmount());
    target.setCurrentAmount(source.getCurrentAmount());

    List<DirectorVO> directorVOs = new ArrayList<>();
    copyDirectorDTOTOVO(source.getDirectorDTOList(), directorVOs);
    target.setDirectorList(directorVOs);
  }

  private void copyDirectorVOToDTO(List<DirectorVO> source, List<DirectorDTO> target, long orgId, long userId) {
    if (source == null) {
      return;
    }
    for (DirectorVO directorVO : source) {
      DirectorDTO directorDTO = new DirectorDTO();
      BeanUtils.copyProperties(directorVO, directorDTO);
      directorDTO.setOrgId(orgId);
      directorDTO.setCreatedUserId(userId);
      directorDTO.setLastModifiedUserId(userId);
      target.add(directorDTO);
    }
  }

  private void copyDirectorDTOTOVO(List<DirectorDTO> source, List<DirectorVO> target) {
    if (source == null) {
      return;
    }
    for (DirectorDTO directorDTO : source) {
      DirectorVO directorVO = new DirectorVO();
      BeanUtils.copyProperties(directorDTO, directorVO);
      CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
      CoreUserProfileDTO coreUserProfileDTO = directorDTO.getCoreUserProfileDTO();
      BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
      directorVO.setCoreUserProfileVO(coreUserProfileVO);
      target.add(directorVO);
    }
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

  private int getResourceTypeFromOkrType(int okrType) {
    if (okrType == OkrType.ORG.getCode()) {
      return ResourceType.ORG.getCode();
    } else if (okrType == OkrType.TEAM.getCode()) {
      return ResourceType.TEAM.getCode();
    } else if (okrType == OkrType.PERSON.getCode()) {
      return ResourceType.PERSON.getCode();
    } else if (okrType == OkrType.PROJECT_TEAM.getCode()) {
      return ResourceType.PROJECT_TEAM.getCode();
    } else {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM, "okr type is wrong");
    }
  }
}
