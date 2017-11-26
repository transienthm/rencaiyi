package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.thirdparty.client.utils.RabbitMQProducer;
import hr.wozai.service.user.client.okr.dto.*;
import hr.wozai.service.user.client.okr.enums.*;
import hr.wozai.service.user.client.okr.facade.OkrFacade;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.user.client.userorg.util.PageUtil;
import hr.wozai.service.user.server.enums.OkrLogAttribute;
import hr.wozai.service.user.server.enums.OkrRemindType;
import hr.wozai.service.user.server.factory.OkrFacadeFactory;
import hr.wozai.service.user.server.helper.CalcProgressHelper;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.model.okr.*;
import hr.wozai.service.user.server.model.userorg.CoreUserProfile;
import hr.wozai.service.user.server.model.userorg.ProjectTeam;
import hr.wozai.service.user.server.model.userorg.Team;
import hr.wozai.service.user.server.service.*;
import hr.wozai.service.thirdparty.client.utils.SqsProducer;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/8
 */
@Service("okrFacadeImpl")
public class OkrFacadeImpl implements OkrFacade {
  private static final Logger LOGGER = LoggerFactory.getLogger(OkrFacadeImpl.class);

  @Autowired
  private OkrService okrService;

  @Autowired
  private TeamService teamService;

  @Autowired
  private UserProfileService userProfileService;

  @Autowired
  private NameIndexService nameIndexService;

  @Autowired
  private OrgService orgService;

  /*  @Autowired
    SqsProducer sqsProducer;*/
  @Autowired
  private RabbitMQProducer rabbitMQProducer;

  @Autowired
  OkrFacadeFactory okrFacadeFactory;

  @Autowired
  UserProfileFacade userProfileFacade;

  @Autowired
  SecurityModelService securityModelService;

  @Override
  @LogAround
  public LongDTO createObjectivePeriod(ObjectivePeriodDTO objectivePeriodDTO, long actorUserId, long adminUserId) {
    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      checkIfOwnerIdIsValid(objectivePeriodDTO);
      ObjectivePeriod objectivePeriod = new ObjectivePeriod();
      BeanUtils.copyProperties(objectivePeriodDTO, objectivePeriod);
      long id = okrService.createObjectivePeriod(objectivePeriod);
      result.setData(id);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  private void checkIfOwnerIdIsValid(ObjectivePeriodDTO objectivePeriodDTO) {
    long type = objectivePeriodDTO.getType();
    long ownerId = objectivePeriodDTO.getOwnerId();
    long orgId = objectivePeriodDTO.getOrgId();

    if (type == OkrType.ORG.getCode()) {
      orgService.getOrg(ownerId);
    } else if (type == OkrType.TEAM.getCode()) {
      Team team = teamService.getTeamByTeamId(orgId, ownerId);
      if (team == null) {
        throw new ServiceStatusException(ServiceStatus.UO_TEAM_NOT_FOUND);
      }
    } else if (type == OkrType.PERSON.getCode()) {
      userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, ownerId);
    } else if (type == OkrType.PROJECT_TEAM.getCode()) {
      ProjectTeam projectTeam = teamService.getProjectTeamByPrimaryKeyAndOrgId(orgId, ownerId);
      if (projectTeam == null) {
        throw new ServiceStatusException(ServiceStatus.UO_PROJECT_TEAM_NOT_FOUND);
      }
    } else {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
  }

  @Override
  @LogAround
  public VoidDTO deleteObjectivePeriod(long orgId, long objectivePeriodId, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      okrService.deleteObjectivePeriod(orgId, objectivePeriodId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateObjectivePeriod(ObjectivePeriodDTO objectivePeriodDTO, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ObjectivePeriod objectivePeriod = new ObjectivePeriod();
      BeanUtils.copyProperties(objectivePeriodDTO, objectivePeriod);
      okrService.updateObjectivePeriod(objectivePeriod);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public ObjectivePeriodListDTO listObjectivePeriod(long orgId, int type, long ownerId, long actorUserId, long adminUserId) {
    ObjectivePeriodListDTO result = new ObjectivePeriodListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ObjectivePeriod> objectivePeriodList = okrService.
              listObjectivePeriodByOrgIdAndOwnerId(orgId, type, ownerId);
      List<ObjectivePeriodDTO> objectivePeriodDTOs = new ArrayList<>();
      Integer month = TimeUtils.getCurrentMonthWithTimeZone(TimeUtils.BEIJING);
      boolean hasSetCurrent = false;
      if (!CollectionUtils.isEmpty(objectivePeriodList)) {
        // Collections.reverse(objectivePeriodList);
        for (ObjectivePeriod objectivePeriod : objectivePeriodList) {
          ObjectivePeriodDTO objectivePeriodDTO = new ObjectivePeriodDTO();
          BeanUtils.copyProperties(objectivePeriod, objectivePeriodDTO);
          if (PeriodTimeSpan.isInMonthRegion(month,
                  PeriodTimeSpan.getEnumByCode(objectivePeriod.getPeriodTimeSpanId()))
                  && objectivePeriod.getYear() == TimeUtils.getCurrentYearWithTimeZone(TimeUtils.BEIJING).intValue()
                  && !hasSetCurrent) {
            objectivePeriodDTO.setDefault(true);
            hasSetCurrent = true;
          }
          objectivePeriodDTOs.add(objectivePeriodDTO);
        }
        if (!hasSetCurrent) {
          // 如果没有当前的,设置最新的一个为当前周期
          objectivePeriodDTOs.get(objectivePeriodDTOs.size() - 1).setDefault(true);
        }
      }
      // Collections.reverse(objectivePeriodDTOs);
      result.setPeriodDTOList(objectivePeriodDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public ObjectivePeriodDTO getObjectivePeriod(long orgId, long objectivePeriodId, long actorUserId, long adminUserId) {
    ObjectivePeriodDTO result = new ObjectivePeriodDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      ObjectivePeriod objectivePeriod = okrService.getObjectivePeriod(orgId, objectivePeriodId);
      BeanUtils.copyProperties(objectivePeriod, result);
    } catch (Exception e) {
      LOGGER.error("getObjectivePeriod-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public LongDTO createObjective(ObjectiveDTO objectiveDTO, long actorUserId, long adminUserId) {
    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      setOwnerIdInObjectiveDTO(objectiveDTO);
      Objective objective = new Objective();
      BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(objectiveDTO, objective);
      List<Director> directors = new ArrayList<>();
      if (!CollectionUtils.isEmpty(objectiveDTO.getDirectorDTOList())) {
        for (DirectorDTO directorDTO : objectiveDTO.getDirectorDTOList()) {
          Director director = new Director();
          BeanUtils.copyProperties(directorDTO, director);
          directors.add(director);
        }
      }
      long id = okrService.createObjectiveAndDirector(objective, directors);

      // noticifation + email
      okrFacadeFactory.sendMessageAndEmailWhenCreateObjective(objective, directors, actorUserId);

      result.setData(id);
    } catch (Exception e) {
      LOGGER.error("createObjective-error():", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  private void setOwnerIdInObjectiveDTO(ObjectiveDTO objectiveDTO) {
    ObjectivePeriod objectivePeriod = okrService.getObjectivePeriod(objectiveDTO.getOrgId(),
            objectiveDTO.getObjectivePeriodId());
    objectiveDTO.setType(objectivePeriod.getType());
    objectiveDTO.setOwnerId(objectivePeriod.getOwnerId());
  }

  @Override
  @LogAround
  public VoidDTO updateObjective(ObjectiveDTO objectiveDTO, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long orgId = objectiveDTO.getOrgId();
      long objectiveId = objectiveDTO.getObjectiveId();
      Objective inDb = okrService.getObjective(orgId, objectiveId);
      List<Director> oldDirectors = okrService.listDirector(orgId, DirectorType.OBJECTIVE.getCode(),
              objectiveId);

      Objective objective = new Objective();
      BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(objectiveDTO, objective);

      if (objectiveDTO.getDirectorDTOList() == null) {
        okrService.updateObjective(objective, objectiveDTO.getComment());
        // message center + email

        objective = okrService.getObjective(orgId, objectiveId);
        okrFacadeFactory.sendMessageAndEmailWhenUpdateObjective(inDb, objective, oldDirectors, oldDirectors, actorUserId);
      } else {
        List<Director> directors = new ArrayList<>();
        for (DirectorDTO directorDTO : objectiveDTO.getDirectorDTOList()) {
          Director director = new Director();
          BeanUtils.copyProperties(directorDTO, director);
          director.setType(DirectorType.OBJECTIVE.getCode());
          director.setObjectId(objective.getObjectiveId());
          directors.add(director);
        }
        okrService.updateObjectiveAndDirectors(objective, objectiveDTO.getComment(), directors, actorUserId);
        // message center + email
        objective = okrService.getObjective(orgId, objectiveId);
        okrFacadeFactory.sendMessageAndEmailWhenUpdateObjective(inDb, objective, oldDirectors, directors, actorUserId);
      }

    } catch (Exception e) {
      LOGGER.error("updateObjective()-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO deleteObjective(long orgId, long objectiveId, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      okrService.deleteObjective(orgId, objectiveId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public ObjectiveDTO getObjective(long orgId, long objectiveId, long actorUserId, long adminUserId) {
    ObjectiveDTO result = new ObjectiveDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Objective objective = okrService.getObjective(orgId, objectiveId);
      BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(objective, result);

      long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
      List<Long> orgAdmins = securityModelService.listOrgAdminUserIdByOrgId(orgId);

      if (!checkIfCanReadObjective(objective, teamId, orgAdmins, actorUserId)) {
        throw new ServiceStatusException(ServiceStatus.OKR_OBJECTIVE_NOT_FOUND);
      }
      fillInParentObjectiveName(result, teamId, orgAdmins, actorUserId);

      setObjectivePeriodRelated(orgId, result);

      // add director, key results
      fillInObjectiveDTO(objectiveId, orgId, result);
      LOGGER.info("getObjective()-result:{}", result);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  private void setObjectivePeriodRelated(long orgId, ObjectiveDTO result) {
    ObjectivePeriod objectivePeriod = okrService.getObjectivePeriod(orgId, result.getObjectivePeriodId());
    result.setObjectivePeriodName(objectivePeriod.getName());
    long ownerId = objectivePeriod.getOwnerId();
    if (result.getType().intValue() == OkrType.ORG.getCode()) {
      result.setObjectivePeriodOwnerName(orgService.getOrg(ownerId).getShortName());
    } else if (result.getType().intValue() == OkrType.TEAM.getCode()) {
      result.setObjectivePeriodOwnerName(
              teamService.getTeamByTeamId(orgId, ownerId).getTeamName());
    } else if (result.getType().intValue() == OkrType.PERSON.getCode()) {
      CoreUserProfileDTO coreUserProfileDTO = userProfileFacade.getCoreUserProfile(
              orgId, result.getOwnerId(), -1L, -1L);
      result.setObjectivePeriodOwnerName(coreUserProfileDTO.getFullName());
      result.setObjectivePeriodOwnerJobTitleName(coreUserProfileDTO.getJobTitleName());
    } else if (result.getType().intValue() == OkrType.PROJECT_TEAM.getCode()) {
      result.setObjectivePeriodOwnerName(
              teamService.getProjectTeamByPrimaryKeyAndOrgId(orgId, ownerId).getProjectTeamName());
    } else {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
  }

  /**
   * 1) set content
   * 2) set remaining days and total days
   * 3) filter aboutMe
   * 4) set totalProgress
   * 5) filter with progressStatus
   *
   * @param orgId
   * @param type
   * @param ownerId
   * @param objectivePeriodId
   * @param aboutMe
   * @param orderBy
   * @param actorUserId
   * @param adminUserId       @return
   */
  @Override
  @LogAround
  public ObjectiveListDTO listObjective(long orgId, int type, long ownerId, long objectivePeriodId,
                                        boolean aboutMe, int progressStatus,
                                        int orderBy, long actorUserId, long adminUserId) {
    ObjectiveListDTO result = new ObjectiveListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // 1)
      fillObjectiveListDTO(orgId, type, ownerId, objectivePeriodId, result, progressStatus, orderBy, actorUserId);
      // 2)
      setRemainingAndTotalDays(orgId, objectivePeriodId, result);
      // 3)
      if (aboutMe) {
        filterMyObjectiveInObjectiveList(result, actorUserId);
      }
      // 4)
      setTotalProgressForObjectiveList(result);
      // 5)
      filterObjectiveDTOWithProvressStatus(result, progressStatus);
    } catch (Exception e) {
      LOGGER.info("listObjective-error():", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  private void filterObjectiveDTOWithProvressStatus(ObjectiveListDTO objectiveListDTO, int progressStatus) {
    List<ObjectiveDTO> newObjectives = new ArrayList<>();
    if (progressStatus == ProgressStatus.ALL.getCode()) {
      return;
    }
    BigDecimal zero = new BigDecimal(0);
    BigDecimal zeroSix = new BigDecimal(0.6);
    if (!CollectionUtils.isEmpty(objectiveListDTO.getObjectiveDTOList())) {
      for (ObjectiveDTO objectiveDTO : objectiveListDTO.getObjectiveDTOList()) {
        BigDecimal progress = new BigDecimal(objectiveDTO.getProgress());
        if (progressStatus == ProgressStatus.NOT_BEGIN.getCode() && progress.compareTo(zero) == 0) {
          newObjectives.add(objectiveDTO);
        } else if (progressStatus == ProgressStatus.ON_GOING.getCode()
                && (progress.compareTo(zero) == 1 && progress.compareTo(zeroSix) == -1)) {
          newObjectives.add(objectiveDTO);
        } else if (progressStatus == ProgressStatus.FINISH.getCode() && progress.compareTo(zeroSix) >= 0) {
          newObjectives.add(objectiveDTO);
        }
      }
    }
    objectiveListDTO.setObjectiveDTOList(newObjectives);
  }

  private void setRemainingAndTotalDays(long orgId, long objectivePeriodId, ObjectiveListDTO objectiveListDTO) {
    ObjectivePeriod objectivePeriod = okrService.getObjectivePeriod(orgId, objectivePeriodId);
    Integer year = objectivePeriod.getYear();
    Integer periodTimeSpanId = objectivePeriod.getPeriodTimeSpanId();
    objectiveListDTO.setBeginTimestamp(TimeUtils.getFirstDayOfMonth(
            year, PeriodTimeSpan.getEnumByCode(periodTimeSpanId).getStartMonth().intValue(), TimeUtils.BEIJING));
    objectiveListDTO.setEndTimestamp(TimeUtils.getLastDayOfMonth(
            year, PeriodTimeSpan.getEnumByCode(periodTimeSpanId).getEndMonth().intValue(), TimeUtils.BEIJING));
  }

  private void filterMyObjectiveInObjectiveList(ObjectiveListDTO objectiveListDTO, long actorUserId) {
    List<ObjectiveDTO> objectiveDTOs = new ArrayList<>();
    for (ObjectiveDTO objectiveDTO : objectiveListDTO.getObjectiveDTOList()) {
      List<DirectorDTO> directorDTOs = objectiveDTO.getDirectorDTOList();
      for (DirectorDTO directorDTO : directorDTOs) {
        if (directorDTO.getUserId().longValue() == actorUserId) {
          objectiveDTOs.add(objectiveDTO);
        }
      }
    }
    objectiveListDTO.setObjectiveDTOList(objectiveDTOs);
  }

  private void fillInObjectiveDTO(long objectiveId, long orgId, ObjectiveDTO result) {
    List<Director> directors = okrService.listDirector(orgId, DirectorType.OBJECTIVE.getCode(), objectiveId);
    List<DirectorDTO> directorDTOs = new ArrayList<>();
    copyDirectorModelToDTO(directors, directorDTOs);
    result.setDirectorDTOList(directorDTOs);

    List<KeyResultDTO> keyResults = listKeyResultByObjectiveId(objectiveId, orgId);
    result.setKeyResultDTOList(keyResults);
    result.setProgress(calcProgressForObjective(result));
  }

  private List<KeyResultDTO> listKeyResultByObjectiveId(long objectiveId, long orgId) {
    List<KeyResultDTO> result = new ArrayList<>();
    List<KeyResult> keyResults = okrService.listKeyResultByOBjectiveId(orgId, objectiveId);
    for (KeyResult keyResult : keyResults) {
      List<DirectorDTO> directorDTOs = new ArrayList<>();
      List<Director> directors = okrService.listDirector(orgId, DirectorType.KEYRESULT.getCode(),
              keyResult.getKeyResultId());
      copyDirectorModelToDTO(directors, directorDTOs);
      KeyResultDTO keyResultDTO = new KeyResultDTO();
      BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(keyResult, keyResultDTO);
      keyResultDTO.setProgress(calcProgressForKeyResult(keyResultDTO));
      keyResultDTO.setDirectorDTOList(directorDTOs);
      result.add(keyResultDTO);
    }
    return result;
  }

  private String calcProgressForKeyResult(KeyResultDTO keyResultDTO) {
    return CalcProgressHelper.calcProgressByDifferentAmount(
            keyResultDTO.getStartingAmount(), keyResultDTO.getGoalAmount(), keyResultDTO.getCurrentAmount());
  }

  private String calcProgressForObjective(ObjectiveDTO objectiveDTO) {
    if (objectiveDTO.getIsAutoCalc() == 0) {
      return CalcProgressHelper.calcProgressByDifferentAmount(
              objectiveDTO.getStartingAmount(), objectiveDTO.getGoalAmount(), objectiveDTO.getCurrentAmount());
    } else {
      String result = new BigDecimal(0).toString();
      if (!CollectionUtils.isEmpty(objectiveDTO.getKeyResultDTOList())) {
        result = getTotalProgressForKeyResultList(objectiveDTO.getKeyResultDTOList());
      }
      return result;
    }
  }

  private void copyDirectorModelToDTO(List<Director> directors, List<DirectorDTO> directorDTOs) {
    if (!CollectionUtils.isEmpty(directors)) {
      Map<Long, CoreUserProfile> map = getProfileMapWithDirectors(directors);
      for (Director director : directors) {
        if (map.containsKey(director.getUserId())) {
          DirectorDTO directorDTO = new DirectorDTO();
          CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
          CoreUserProfile coreUserProfile = map.get(director.getUserId());
          BeanUtils.copyProperties(coreUserProfile, coreUserProfileDTO);
          BeanUtils.copyProperties(director, directorDTO);
          directorDTO.setCoreUserProfileDTO(coreUserProfileDTO);
          directorDTOs.add(directorDTO);
        }
      }
    }
  }

  private Map<Long, CoreUserProfile> getProfileMapWithDirectors(List<Director> directors) {
    if (!CollectionUtils.isEmpty(directors)) {
      List<Long> userIds = new ArrayList<>();
      for (Director director : directors) {
        userIds.add(director.getUserId());
      }
      List<CoreUserProfile> coreUserProfiles = userProfileService.
              listCoreUserProfileByOrgIdAndUserId(directors.get(0).getOrgId(), userIds);

      Map<Long, CoreUserProfile> result = new HashMap<>();
      for (CoreUserProfile coreUserProfile : coreUserProfiles) {
        result.put(coreUserProfile.getUserId(), coreUserProfile);
      }

      return result;
    } else {
      return new HashMap<>();
    }
  }

  private void fillObjectiveListDTO(
          long orgId, int type, long ownerId,
          long objectivePeriodId, ObjectiveListDTO result, int progressStatus, int orderBy, long actorUserId) {
    List<Objective> objectives = okrService.listObjectiveByTypeAndOwnerIdAndPeriodId(orgId, type,
            ownerId, objectivePeriodId, progressStatus, orderBy);
    List<ObjectiveDTO> objectiveDTOs = new ArrayList<>();
    long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
    List<Long> orgAdmins = securityModelService.listOrgAdminUserIdByOrgId(orgId);
    for (Objective objective : objectives) {
      if (!checkIfCanReadObjective(objective, teamId, orgAdmins, actorUserId)) {
        continue;
      }
      ObjectiveDTO objectiveDTO = new ObjectiveDTO();
      BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(objective, objectiveDTO);
      fillInParentObjectiveName(objectiveDTO, teamId, orgAdmins, actorUserId);

      CoreUserProfileDTO coreUserProfileDTO = userProfileFacade.getCoreUserProfile(
              orgId, objective.getLastModifiedUserId(), -1, -1);
      objectiveDTO.setLastModifiedUserProfile(coreUserProfileDTO);
      fillInObjectiveDTO(objective.getObjectiveId(), orgId, objectiveDTO);
      objectiveDTOs.add(objectiveDTO);
    }
    result.setObjectiveDTOList(objectiveDTOs);
  }

  public void fillInParentObjectiveName(
          ObjectiveDTO objectiveDTO, long teamId, List<Long> orgAdmins, long actorUserId) {
    long parentObjectiveId = objectiveDTO.getParentObjectiveId();
    if (parentObjectiveId == 0) {
      objectiveDTO.setParentObjectiveName(null);
    } else {
      Objective parentObjective = okrService.getObjective(objectiveDTO.getOrgId(), parentObjectiveId);
      if (checkIfCanReadObjective(parentObjective, teamId, orgAdmins, actorUserId)) {
        String name = okrService.getObjective(objectiveDTO.getOrgId(), parentObjectiveId).getContent();
        objectiveDTO.setParentObjectiveName(name);
      } else {
        objectiveDTO.setParentObjectiveName(null);
      }
    }
  }

  /** 过滤私有的objective
   * 1)个人私有目标只被本人看到
   * 2)团队私有目标只被团队内的人看到
   * 3)公司私有目标只被公司内的人看到
   * @param objective
   * @param actorUserId
   * @return
   */
  private boolean checkIfCanReadObjective(Objective objective, long teamId, List<Long> orgAdmins, long actorUserId) {
    List<Long> directors = okrService.listObjectiveAndKeyResultDirectorsByObjectiveId(
            objective.getOrgId(), objective.getObjectiveId());
    boolean isDirector = directors.contains(actorUserId);
    if (objective.getIsPrivate() == 0 || isDirector) {
      return true;
    } else {
      if (IntegerUtils.equals(objective.getType(), OkrType.PERSON.getCode())) {
        // 本人可以看到自己的个人目标
        return actorUserId == objective.getOwnerId();
      } else if (IntegerUtils.equals(objective.getType(), OkrType.TEAM.getCode())) {
        // 本团队的人可以看到私有的团队目标
        // long teamId = teamService.getTeamMemberByUserIdAndOrgId(objective.getOrgId(), actorUserId).getTeamId();
        return teamId == objective.getOwnerId();
      } else if (IntegerUtils.equals(objective.getType(), OkrType.PROJECT_TEAM.getCode())) {
        // 本项目组的人可以看到私有的项目组目标
        return teamService.getProjectTeamMember(objective.getOrgId(), objective.getOwnerId(), actorUserId) != null;
      } else {
        // 只有orgAdmin可以看到私有的公司目标
        // List<Long> orgAdmins = securityModelService.listOrgAdminUserIdByOrgId(objective.getOrgId());
        return orgAdmins.contains(actorUserId);
      }
    }
  }

  private void setTotalProgressForObjectiveList(ObjectiveListDTO objectiveListDTO) {
    BigDecimal totalProgress = new BigDecimal("0.0000");
    BigDecimal denominator = new BigDecimal("0.0");

    if (!CollectionUtils.isEmpty(objectiveListDTO.getObjectiveDTOList())) {
      for (ObjectiveDTO objectiveDTO : objectiveListDTO.getObjectiveDTOList()) {
        BigDecimal factor = new BigDecimal(ObjectivePriority.getEnumByCode(objectiveDTO.getPriority()).getDesc());
        denominator = denominator.add(factor);
        BigDecimal oldProcess = new BigDecimal(objectiveDTO.getProgress());
        BigDecimal newProcess = oldProcess.multiply(factor);
        totalProgress = totalProgress.add(newProcess);
      }
      totalProgress = totalProgress.divide(denominator, 2, BigDecimal.ROUND_HALF_UP);
    }
    objectiveListDTO.setTotalProgress(totalProgress.toString());
  }

  private String getTotalProgressForKeyResultList(List<KeyResultDTO> keyResultList) {
    BigDecimal totalProcess = new BigDecimal("0.0000");
    BigDecimal denominator = new BigDecimal("0.0");

    if (!CollectionUtils.isEmpty(keyResultList)) {
      for (KeyResultDTO keyResultDTO : keyResultList) {
        BigDecimal factor = new BigDecimal(ObjectivePriority.getEnumByCode(keyResultDTO.getPriority()).getDesc());
        denominator = denominator.add(factor);
        BigDecimal oldProcess = new BigDecimal(keyResultDTO.getProgress());
        BigDecimal newProcess = oldProcess.multiply(factor);
        totalProcess = totalProcess.add(newProcess);
      }
      totalProcess = totalProcess.divide(denominator, 2, BigDecimal.ROUND_HALF_UP);
    }
    return totalProcess.toString();
  }

  @Override
  @LogAround
  public ObjectiveListDTO listObjectivesByObjectiveIds(long orgId, List<Long> objectiveIds, long actorUserId, long adminUserId) {
    ObjectiveListDTO result = new ObjectiveListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Objective> objectives = okrService.listObjectivesByObjectiveIds(orgId, objectiveIds);
      List<ObjectiveDTO> objectiveDTOs = new ArrayList<>();
      for (Objective objective : objectives) {
        ObjectiveDTO objectiveDTO = new ObjectiveDTO();
        BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(objective, objectiveDTO);
        objectiveDTOs.add(objectiveDTO);
      }
      result.setObjectiveDTOList(objectiveDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public ObjectiveListDTO searchObjectiveByKeywordInOrder(long orgId, long objectiveId, String keyword, long actorUserId, long adminUserId) {
    ObjectiveListDTO result = new ObjectiveListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<ObjectiveDTO> objectiveDTOs = new ArrayList<>();
      List<Objective> objectives;
      if (keyword.trim().isEmpty()) {
        //  选出上级team和本team的目标
        long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
        Team team = teamService.getTeamByTeamId(orgId, teamId);
        long parentTeamId = team.getParentTeamId();

        // 如果父team没有或者父team是顶级team, 就获取公司目标
        if (parentTeamId == 0 || teamService.getTeamByTeamId(orgId, parentTeamId).getParentTeamId() == 0) {
          objectives = new ArrayList<>(okrService.searchObjectiveByKeywordInOrder(
                  orgId, objectiveId, keyword, OkrType.ORG.getCode(), orgId, actorUserId));
        } else {
          objectives = new ArrayList<>(okrService.searchObjectiveByKeywordInOrder(
                  orgId, objectiveId, keyword, OkrType.TEAM.getCode(), parentTeamId, actorUserId));
        }
        objectives.addAll(new ArrayList<>(okrService.searchObjectiveByKeywordInOrder(
                orgId, objectiveId, keyword, OkrType.TEAM.getCode(), teamId, actorUserId)));
      } else {
        objectives = okrService.searchObjectiveByKeywordInOrder(orgId, objectiveId, keyword, 2, -1L, actorUserId);
      }

      long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
      List<Long> orgAdmins = securityModelService.listOrgAdminUserIdByOrgId(orgId);
      for (Objective objective : objectives) {
        // filter private objective
        if (!checkIfCanReadObjective(objective, teamId, orgAdmins, actorUserId)) {
          continue;
        }
        ObjectiveDTO objectiveDTO = new ObjectiveDTO();
        BeanUtils.copyProperties(objective, objectiveDTO);
        objectiveDTOs.add(objectiveDTO);
      }

      List<ObjectiveDTO> objs;
      if (objectiveDTOs.size() > 10) {
        objs = objectiveDTOs.subList(0, 10);
      } else {
        objs = objectiveDTOs;
      }

      for (ObjectiveDTO objectiveDTO : objs) {
        setObjectivePeriodRelated(orgId, objectiveDTO);
      }

      result.setObjectiveDTOList(objs);
    } catch (Exception e) {
      LOGGER.error("searchObjectiveByKeywordInOrder()-error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO moveObjective(long orgId, long objectiveId, int targetOrderIndex, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      okrService.moveObjective(orgId, objectiveId, targetOrderIndex);
    } catch (Exception e) {
      LOGGER.error("moveObjective-error():", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public ObjectiveListDTO listAncesterObjectives(long orgId, long objectiveId, long actorUserId, long adminUserId) {
    ObjectiveListDTO result = new ObjectiveListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Objective> objectives = okrService.listAncesterObjectives(orgId, objectiveId);
      List<ObjectiveDTO> objectiveDTOs = new ArrayList<>();
      long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
      List<Long> orgAdmins = securityModelService.listOrgAdminUserIdByOrgId(orgId);
      for (Objective objective : objectives) {
        if (!checkIfCanReadObjective(objective, teamId, orgAdmins, actorUserId)) {
          break;
        }
        ObjectiveDTO objectiveDTO = new ObjectiveDTO();
        BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(objective, objectiveDTO);

        setObjectivePeriodRelated(orgId, objectiveDTO);

        fillInObjectiveDTO(objective.getObjectiveId(), orgId, objectiveDTO);

        objectiveDTO.setHasSubordinate(1);
        objectiveDTO.setHasParent(hasParent(orgId, objective, teamId, orgAdmins, actorUserId));
        objectiveDTOs.add(objectiveDTO);
      }

      result.setObjectiveDTOList(objectiveDTOs);
    } catch (Exception e) {
      LOGGER.error("listAncesterObjectives()-error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  private int hasParent(long orgId, Objective objective, long teamId, List<Long> orgAdmins, long actorUserId) {
    long parentObjectiveId = objective.getParentObjectiveId();
    try {
      Objective parent = okrService.getObjective(orgId, parentObjectiveId);
      if (checkIfCanReadObjective(parent, teamId, orgAdmins, actorUserId)) {
        return 1;
      } else {
        return 0;
      }
    } catch (Exception e) {
      return 0;
    }
  }

  @Override
  @LogAround
  public ObjectiveListDTO listFirstLevelSubordinateObjectives(
          long orgId, long objectiveId, long actorUserId, long adminUserId) {
    ObjectiveListDTO result = new ObjectiveListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Objective> objectives = okrService.listFirstLevelSubordinateObjectives(orgId, objectiveId);
      List<ObjectiveDTO> objectiveDTOs = new ArrayList<>();
      long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
      List<Long> orgAdmins = securityModelService.listOrgAdminUserIdByOrgId(orgId);
      for (Objective objective : objectives) {
        if (!checkIfCanReadObjective(objective, teamId, orgAdmins, actorUserId)) {
          break;
        }
        ObjectiveDTO objectiveDTO = new ObjectiveDTO();
        BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(objective, objectiveDTO);

        setObjectivePeriodRelated(orgId, objectiveDTO);

        fillInObjectiveDTO(objective.getObjectiveId(), orgId, objectiveDTO);
        objectiveDTO.setHasSubordinate(hasSubordinate(orgId, objective.getObjectiveId(), teamId, orgAdmins, actorUserId));
        objectiveDTO.setHasParent(1);
        objectiveDTOs.add(objectiveDTO);
      }
      result.setObjectiveDTOList(objectiveDTOs);
    } catch (Exception e) {
      LOGGER.error("listFirstLevelSubordinateObjectives()-error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  private int hasSubordinate(long orgId, long objectiveId, long teamId, List<Long> orgAdmins, long actorUserId) {
    List<Objective> objectives = new ArrayList<>();
    for (Objective objective : okrService.listFirstLevelSubordinateObjectives(orgId, objectiveId)) {
      if (checkIfCanReadObjective(objective, teamId, orgAdmins, actorUserId)) {
        objectives.add(objective);
      }
    }
    if (objectives.size() == 0) {
      return 0;
    } else {
      return 1;
    }
  }

  @Override
  @LogAround
  public ObjectiveTreeDTO getBirdViewByObjectiveId(long orgId, long objectiveId, long actorUserId, long adminUserId) {
    ObjectiveTreeDTO result = new ObjectiveTreeDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Objective> parents = okrService.listAncesterObjectives(orgId, objectiveId);
      Objective rootObjective = null;
      long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
      List<Long> orgAdmins = securityModelService.listOrgAdminUserIdByOrgId(orgId);
      for (Objective parent : parents) {
        if (!checkIfCanReadObjective(parent, teamId, orgAdmins, actorUserId)) {
          break;
        }
        rootObjective = parent;
      }

      List<List<ObjectiveDTO>> treeObjectives = new ArrayList<>();
      List<Objective> firstLevelSubordinate = okrService.listFirstLevelSubordinateObjectives(orgId, objectiveId);
      if (rootObjective == null) {
        // 跟节点即提供的objectiveId
        Objective objective = okrService.getObjective(orgId, objectiveId);
        List<Objective> root = new ArrayList<>();
        root.add(objective);
        treeObjectives.add(transferObjectiveToSimpleObjectiveDTO(root));
        treeObjectives.add(transferObjectiveToSimpleObjectiveDTO(firstLevelSubordinate));
      } else {
        List<Objective> preLevel = new ArrayList<>();
        preLevel.add(rootObjective);
        treeObjectives.add(transferObjectiveToSimpleObjectiveDTO(preLevel));
        boolean canBreak = false;
        while (!canBreak) {
          List<Objective> curLevel = new ArrayList<>();
          for (Objective parentObjective : preLevel) {
            List<Objective> subordinate = filterPrivateObjectives(
                    orgId,
                    okrService.listFirstLevelSubordinateObjectives(orgId, parentObjective.getObjectiveId()),
                    actorUserId);
            curLevel.addAll(subordinate);
            if (isObjectiveInList(subordinate, objectiveId)) {
              canBreak = true;
            }
          }
          treeObjectives.add(transferObjectiveToSimpleObjectiveDTO(curLevel));
          preLevel = curLevel;
        }
        treeObjectives.add(transferObjectiveToSimpleObjectiveDTO(firstLevelSubordinate));
      }

      if (!CollectionUtils.isEmpty(treeObjectives)) {
        int size = treeObjectives.size();
        if (size == 1) {
          List<ObjectiveDTO> objectiveDTOs = treeObjectives.get(0);
          for (ObjectiveDTO objectiveDTO : objectiveDTOs) {
            objectiveDTO.setHasParent(0);
            objectiveDTO.setHasSubordinate(0);
          }
        } else if (size == 2) {
          List<ObjectiveDTO> levelOne = treeObjectives.get(0);
          for (ObjectiveDTO objectiveDTO : levelOne) {
            objectiveDTO.setHasParent(0);
            objectiveDTO.setHasSubordinate(1);
          }
          List<ObjectiveDTO> levelTwo = treeObjectives.get(1);
          for (ObjectiveDTO objectiveDTO : levelTwo) {
            objectiveDTO.setHasParent(1);
            objectiveDTO.setHasSubordinate(hasSubordinate(
                    orgId, objectiveDTO.getObjectiveId(), teamId, orgAdmins, actorUserId));
          }
        } else {
          for (int i = 0 ; i < size; i++) {
            List<ObjectiveDTO> objectiveDTOs = treeObjectives.get(i);
            if (i == 0) {
              for (ObjectiveDTO objectiveDTO : objectiveDTOs) {
                objectiveDTO.setHasParent(0);
                objectiveDTO.setHasSubordinate(1);
              }
            } else if (i == size - 2 || i == size - 1) {
              for (ObjectiveDTO objectiveDTO : objectiveDTOs) {
                objectiveDTO.setHasParent(1);
                objectiveDTO.setHasSubordinate(hasSubordinate(
                        orgId, objectiveDTO.getObjectiveId(), teamId, orgAdmins, actorUserId));
              }
            } else {
              for (ObjectiveDTO objectiveDTO : objectiveDTOs) {
                objectiveDTO.setHasParent(1);
                objectiveDTO.setHasSubordinate(1);
              }
            }
          }
        }
      }

      result.setObjectiveTrees(treeObjectives);
    } catch (Exception e) {
      LOGGER.error("getBirdViewByObjectiveId()-error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  private List<ObjectiveDTO> transferObjectiveToSimpleObjectiveDTO(List<Objective> objectives) {
    List<ObjectiveDTO> result = new ArrayList<>();
    for (Objective objective : objectives) {
      ObjectiveDTO objectiveDTO = new ObjectiveDTO();
      objectiveDTO.setOrgId(objective.getOrgId());
      objectiveDTO.setObjectiveId(objective.getObjectiveId());
      objectiveDTO.setParentObjectiveId(objective.getParentObjectiveId());
      result.add(objectiveDTO);
    }
    return result;
  }

  private List<Objective> filterPrivateObjectives(long orgId, List<Objective> objectives, long actorUserId) {
    List<Objective> result = new ArrayList<>();
    long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
    List<Long> orgAdmins = securityModelService.listOrgAdminUserIdByOrgId(orgId);
    for (Objective objective : objectives) {
      if (checkIfCanReadObjective(objective, teamId, orgAdmins, actorUserId)) {
        result.add(objective);
      }
    }
    return result;
  }

  private boolean isObjectiveInList(List<Objective> objectives, long objectiveId) {
    for (Objective objective : objectives) {
      if (objective.getObjectiveId() == objectiveId) {
        return true;
      }
    }
    return false;
  }

  @Override
  @LogAround
  public ObjectiveListDTO listObjectivesByStartAndEndDeadline(long orgId, long startDeadline, long endDeadline) {
    ObjectiveListDTO result = new ObjectiveListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<Objective> objectives = okrService.listObjectivesByStartAndEndDeadline(orgId, startDeadline, endDeadline);
      List<ObjectiveDTO> objectiveDTOs = new ArrayList<>();
      for (Objective objective : objectives) {
        ObjectiveDTO objectiveDTO = new ObjectiveDTO();
        BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(objective, objectiveDTO);

        fillInObjectiveDTO(objective.getObjectiveId(), orgId, objectiveDTO);
        objectiveDTOs.add(objectiveDTO);
      }
      result.setObjectiveDTOList(objectiveDTOs);
    } catch (Exception e) {
      LOGGER.error("listObjectivesByStartAndEndDeadline()-error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public ObjectiveListDTO filterObjectives(long orgId, int priority, int progressStatus, boolean aboutMe, int orderItem,
                                           int pageNumber, int pageSize, long actorUserId, long adminUserId) {
    ObjectiveListDTO result = new ObjectiveListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // 1)优先级筛选
      List<Objective> objectives = okrService.listObjectivesByPriority(orgId, priority, orderItem);
      List<ObjectiveDTO> objectiveDTOs = new ArrayList<>();
      List<Long> objectiveIds = new ArrayList<>();
      String orgName = orgService.getOrg(orgId).getShortName();
      Map<Long, String> teamIdAndNameMap = new HashMap<>();
      Map<Long, String> userIdAndNameMap = new HashMap<>();
      Map<Long, String> projectTeamIdAndNameMap = new HashMap<>();
      long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
      List<Long> orgAdmins = securityModelService.listOrgAdminUserIdByOrgId(orgId);
      for (Objective objective : objectives) {
        // 过滤私有的目标
        if (!checkIfCanReadObjective(objective, teamId, orgAdmins, actorUserId)) {
          continue;
        }
        ObjectiveDTO objectiveDTO = new ObjectiveDTO();
        BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(objective, objectiveDTO);
        if (objectiveDTO.getDeadline() == null) {
          objectiveDTO.setDeadline(0L);
        }
        objectiveIds.add(objective.getObjectiveId());

        setObjectivePeriodOwnerName(orgId, orgName, teamIdAndNameMap, userIdAndNameMap, projectTeamIdAndNameMap, objectiveDTO);
        objectiveDTOs.add(objectiveDTO);
      }

      List<KeyResult> keyResults = okrService.listSimpleKeyResultsByObjectiveIds(orgId, objectiveIds);
      List<Director> directors = okrService.listDirectorsByObjectiveIds(orgId, DirectorType.OBJECTIVE.getCode(), objectiveIds);
      Map<Long, CoreUserProfile> directorsMap = getProfileMapWithDirectors(directors);
      Map<Long, List<KeyResultDTO>> objectiveKeyResultsMap = getOjectiveKRMap(keyResults);
      Map<Long, List<DirectorDTO>> objectiveDirectorsMap = getObjectiveDIRMap(directors, directorsMap);

      for (ObjectiveDTO objectiveDTO : objectiveDTOs) {
        long objectiveId = objectiveDTO.getObjectiveId();
        if (objectiveDirectorsMap.containsKey(objectiveId)) {
          objectiveDTO.setDirectorDTOList(objectiveDirectorsMap.get(objectiveId));
        } else {
          objectiveDTO.setDirectorDTOList(new ArrayList<>());
        }
        if (objectiveKeyResultsMap.containsKey(objectiveId)) {
          objectiveDTO.setKeyResultDTOList(objectiveKeyResultsMap.get(objectiveId));
        } else {
          objectiveDTO.setKeyResultDTOList(new ArrayList<>());
        }
        objectiveDTO.setProgress(calcProgressForObjective(objectiveDTO));
      }
      result.setObjectiveDTOList(objectiveDTOs);

      // 2)关于我的筛选
      if (aboutMe) {
        filterMyObjectiveInObjectiveList(result, actorUserId);
      }
      // 3)完成度删选
      filterObjectiveDTOWithProvressStatus(result, progressStatus);

      // 这里只处理完成度排序
      if (orderItem == ObjectiveOrderItem.PROGRESS.getCode()) {
        result.getObjectiveDTOList().
                sort((ObjectiveDTO o1, ObjectiveDTO o2) -> o1.getProgress().compareTo(o2.getProgress()));
      } else if (orderItem == ObjectiveOrderItem.PROGRESS_REVERSE.getCode()) {
        result.getObjectiveDTOList().
                sort((ObjectiveDTO o1, ObjectiveDTO o2) -> o1.getProgress().compareTo(o2.getProgress()));
        Collections.reverse(result.getObjectiveDTOList());
      }

      // 分页
      result.setTotalNumber(result.getObjectiveDTOList().size());
      result.setObjectiveDTOList(PageUtil.getPagingList(result.getObjectiveDTOList(), pageNumber, pageSize));
    } catch (Exception e) {
      LOGGER.error("filterObjectives()-error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  private Map<Long, List<KeyResultDTO>> getOjectiveKRMap(List<KeyResult> keyResults) {
    Map<Long, List<KeyResultDTO>> objectiveKeyResultsMap = new HashMap<>();
    for (KeyResult keyResult : keyResults) {
      long objectiveId = keyResult.getObjectiveId();
      List<KeyResultDTO> keyResultDTOs;
      if (objectiveKeyResultsMap.containsKey(objectiveId)) {
        keyResultDTOs = objectiveKeyResultsMap.get(objectiveId);
      } else {
        keyResultDTOs = new ArrayList<>();
      }
      KeyResultDTO keyResultDTO = new KeyResultDTO();
      BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(keyResult, keyResultDTO);
      keyResultDTO.setProgress(calcProgressForKeyResult(keyResultDTO));
      keyResultDTOs.add(keyResultDTO);
      objectiveKeyResultsMap.put(objectiveId, keyResultDTOs);
    }
    return objectiveKeyResultsMap;
  }

  private Map<Long, List<DirectorDTO>> getObjectiveDIRMap(List<Director> directors, Map<Long, CoreUserProfile> map) {
    Map<Long, List<DirectorDTO>> objectiveDirectorsMap = new HashMap<>();
    for (Director director : directors) {
      long objectiveId = director.getObjectId();
      List<DirectorDTO> directorDTOs;
      if (objectiveDirectorsMap.containsKey(objectiveId)) {
        directorDTOs = objectiveDirectorsMap.get(objectiveId);
      } else {
        directorDTOs = new ArrayList<>();
      }
      DirectorDTO directorDTO = new DirectorDTO();
      BeanUtils.copyProperties(director, directorDTO);
      CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
      if (map.containsKey(director.getUserId())) {
        BeanUtils.copyProperties(map.get(director.getUserId()), coreUserProfileDTO);
        directorDTO.setCoreUserProfileDTO(coreUserProfileDTO);
        directorDTOs.add(directorDTO);
        objectiveDirectorsMap.put(objectiveId, directorDTOs);
      }
    }
    return objectiveDirectorsMap;
  }

  private void setObjectivePeriodOwnerName(long orgId, String orgName, Map<Long, String> teamIdAndNameMap,
                                           Map<Long, String> userIdAndNameMap, Map<Long, String> projectTeamIdAndNameMap,
                                           ObjectiveDTO objectiveDTO) {
    if (objectiveDTO.getType().intValue() == OkrType.ORG.getCode()) {
      objectiveDTO.setObjectivePeriodOwnerName(orgName);
    } else if (objectiveDTO.getType().intValue() == OkrType.TEAM.getCode()) {
      long teamId = objectiveDTO.getOwnerId();
      if (teamIdAndNameMap.containsKey(teamId)) {
        objectiveDTO.setObjectivePeriodOwnerName(teamIdAndNameMap.get(teamId));
      } else {
        String teamName = teamService.getTeamByTeamId(orgId, objectiveDTO.getOwnerId()).getTeamName();
        teamIdAndNameMap.put(teamId, teamName);
        objectiveDTO.setObjectivePeriodOwnerName(teamName);
      }
    } else if (objectiveDTO.getType().intValue() == OkrType.PERSON.getCode()) {
      long userId = objectiveDTO.getOwnerId();
      if (userIdAndNameMap.containsKey(userId)) {
        objectiveDTO.setObjectivePeriodOwnerName(userIdAndNameMap.get(userId));
      } else {
        String userName = userProfileService.
                getCoreUserProfileByOrgIdAndUserId(orgId, objectiveDTO.getOwnerId()).getFullName();
        userIdAndNameMap.put(userId, userName);
        objectiveDTO.setObjectivePeriodOwnerName(userName);
      }
    } else if (objectiveDTO.getType().intValue() == OkrType.PROJECT_TEAM.getCode()) {
      long projectTeamId = objectiveDTO.getOwnerId();
      if (projectTeamIdAndNameMap.containsKey(projectTeamId)) {
        objectiveDTO.setObjectivePeriodOwnerName(projectTeamIdAndNameMap.get(projectTeamId));
      } else {
        String projectTeamName = teamService.getProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId).getProjectTeamName();
        projectTeamIdAndNameMap.put(projectTeamId, projectTeamName);
        objectiveDTO.setObjectivePeriodOwnerName(projectTeamName);
      }
    }
  }

  @Override
  @LogAround
  public LongDTO createKeyResult(KeyResultDTO keyResultDTO, long actorUserId, long adminUserId) {
    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      KeyResult keyResult = new KeyResult();
      BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(keyResultDTO, keyResult);
      List<Director> directors = new ArrayList<>();
      for (DirectorDTO directorDTO : keyResultDTO.getDirectorDTOList()) {
        Director director = new Director();
        BeanUtils.copyProperties(directorDTO, director);
        directors.add(director);
      }
      long id = okrService.createKeyResultAndDirector(keyResult, directors);

      // message + email
      okrFacadeFactory.sendMessageAndEmailWhenCreateKR(keyResult, directors, actorUserId);

      result.setData(id);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO updadteKeyResult(KeyResultDTO keyResultDTO, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      long orgId = keyResultDTO.getOrgId();
      long keyResultId = keyResultDTO.getKeyResultId();
      KeyResult oldKR = okrService.getKeyResult(orgId, keyResultId);
      List<Director> oldDirectors = okrService.listDirector(orgId, DirectorType.KEYRESULT.getCode(), keyResultId);
      KeyResult keyResult = new KeyResult();
      BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(keyResultDTO, keyResult);

      if (keyResultDTO.getDirectorDTOList() == null) {
        okrService.updateKeyResult(keyResult, keyResultDTO.getComment());

        keyResult = okrService.getKeyResult(orgId, keyResultId);
        okrFacadeFactory.sendMessageAndEmailWhenUpdateKR(oldKR, keyResult, oldDirectors, oldDirectors, actorUserId);
      } else {
        List<Director> directors = new ArrayList<>();
        for (DirectorDTO directorDTO : keyResultDTO.getDirectorDTOList()) {
          Director director = new Director();
          BeanUtils.copyProperties(directorDTO, director);
          director.setType(DirectorType.KEYRESULT.getCode());
          director.setObjectId(keyResult.getKeyResultId());
          directors.add(director);
        }

        okrService.updateKeyResultAndDirectors(keyResult, keyResultDTO.getComment(), directors, actorUserId);

        keyResult = okrService.getKeyResult(orgId, keyResultId);
        okrFacadeFactory.sendMessageAndEmailWhenUpdateKR(oldKR, keyResult, oldDirectors, directors, actorUserId);
      }
    } catch (Exception e) {
      LOGGER.error("updadteKeyResult()-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO deleteKeyResult(long orgId, long keyResultId, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      okrService.deleteKeyResult(orgId, keyResultId, actorUserId);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public KeyResultDTO getKeyResult(long orgId, long keyResultId, long actorUserId, long adminUserId) {
    KeyResultDTO result = new KeyResultDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      KeyResult keyResult = okrService.getKeyResult(orgId, keyResultId);
      BeanHelper.copyPropertiesHandlingJSONAndBigDecimal(keyResult, result);
      List<DirectorDTO> directorDTOs = new ArrayList<>();
      List<Director> directors = okrService.listDirector(orgId, DirectorType.KEYRESULT.getCode(),
              keyResult.getKeyResultId());
      copyDirectorModelToDTO(directors, directorDTOs);
      result.setDirectorDTOList(directorDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public DirectorListDTO listObjectiveOrKeyResultDirector(long orgId, int type, long objectId, long actorUserId, long adminUserId) {
    DirectorListDTO result = new DirectorListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<DirectorDTO> directorDTOs = new ArrayList<>();
      List<Director> directors = okrService.listDirector(orgId, type, objectId);
      copyDirectorModelToDTO(directors, directorDTOs);
      result.setDirectorDTOList(directorDTOs);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public ObjectivePeriodDTO getObjectivePeriodWithObjectiveId(long orgId, long objectiveId, long actorUserId, long adminUserId) {
    ObjectivePeriodDTO result = new ObjectivePeriodDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Objective objective = okrService.getObjective(orgId, objectiveId);
      ObjectivePeriod objectivePeriod = okrService.getObjectivePeriod(orgId, objective.getObjectivePeriodId());
      BeanUtils.copyProperties(objectivePeriod, result);
    } catch (Exception e) {
      LOGGER.error("getObjectivePeriod-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public ObjectivePeriodDTO getObjectivePeriodWithKeyResultId(long orgId, long keyResultId, long actorUserId, long adminUserId) {
    ObjectivePeriodDTO result = new ObjectivePeriodDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      KeyResult keyResult = okrService.getKeyResult(orgId, keyResultId);
      Objective objective = okrService.getObjective(orgId, keyResult.getObjectiveId());
      ObjectivePeriod objectivePeriod = okrService.getObjectivePeriod(orgId, objective.getObjectivePeriodId());
      BeanUtils.copyProperties(objectivePeriod, result);
    } catch (Exception e) {
      LOGGER.error("getObjectivePeriod-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public LongDTO addOkrComment(long orgId, OkrCommentDTO okrCommentDTO, long actorUserId, long adminUserId) {
    LongDTO result = new LongDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Objective objective = okrService.getObjective(orgId, okrCommentDTO.getObjectiveId());
      OkrComment okrComment = new OkrComment();
      BeanUtils.copyProperties(okrCommentDTO, okrComment);
      okrComment.setOrgId(orgId);
      okrComment.setUserId(actorUserId);
      okrComment.setCreatedUserId(actorUserId);
      if (okrComment.getKeyResultId() != null && okrComment.getKeyResultId() != 0) {
        KeyResult keyResult = okrService.getKeyResult(orgId, okrComment.getKeyResultId());
        okrComment.setKeyResultContent(keyResult.getContent());
      } else {
        okrComment.setKeyResultId(0L);
        okrComment.setKeyResultContent("");
      }

      long id = okrService.addOkrCommentAndOkrUpdateLogs(okrComment, new ArrayList<>());

      objective.setCreatedUserId(actorUserId);
      okrService.updateObjective(objective, "");
      okrFacadeFactory.sendMessageAndEmailWhenAddOkrComment(orgId, okrComment, actorUserId);

      result.setData(id);
    } catch (Exception e) {
      LOGGER.error("addOkrComment-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateOkrComment(long orgId, long okrCommentId, String content, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      OkrComment okrComment = new OkrComment();
      okrComment.setOkrCommentId(okrCommentId);
      okrComment.setOrgId(orgId);
      okrComment.setContent(content);
      okrComment.setLastModifiedUserId(actorUserId);
      okrService.updateOkrComment(okrComment);
    } catch (Exception e) {
      LOGGER.error("updateOkrComment-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO deleteOkrComment(long orgId, long okrCommentId, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      OkrComment okrComment = new OkrComment();
      okrComment.setOkrCommentId(okrCommentId);
      okrComment.setOrgId(orgId);
      okrComment.setIsDeleted(1);
      okrComment.setLastModifiedUserId(actorUserId);
      okrService.updateOkrComment(okrComment);
    } catch (Exception e) {
      LOGGER.error("deleteOkrComment-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public OkrCommentListDTO listOkrComment(
          long orgId, long objectiveId, long keyResultId,
          int pageNumber, int pageSize, long actorUserId, long adminUserId) {
    OkrCommentListDTO result = new OkrCommentListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<OkrComment> okrComments = okrService.listOkrComment(orgId, objectiveId, keyResultId, pageNumber, pageSize);

      List<Long> orgAdminIds = securityModelService.listOrgAdminUserIdByOrgId(orgId);
      boolean isOrgAdmin = orgAdminIds.contains(actorUserId);

      List<OkrCommentDTO> okrCommentDTOs = new ArrayList<>();
      for (OkrComment okrComment : okrComments) {
        OkrCommentDTO okrCommentDTO = new OkrCommentDTO();
        BeanUtils.copyProperties(okrComment, okrCommentDTO);
        okrCommentDTO.setActorUserProfile(
                userProfileFacade.getCoreUserProfile(orgId, okrComment.getUserId(), actorUserId, adminUserId));
        List<OkrUpdateLog> okrUpdateLogs = okrService.listOkrUpdateLogsByOkrCommentId(
                orgId, okrComment.getOkrCommentId());
        List<OkrUpdateLogDTO> okrUpdateLogDTOs = new ArrayList<>();
        for (OkrUpdateLog okrUpdateLog : okrUpdateLogs) {
          OkrUpdateLogDTO okrUpdateLogDTO = new OkrUpdateLogDTO();
          BeanUtils.copyProperties(okrUpdateLog, okrUpdateLogDTO);
          if (okrUpdateLog.getAttribute().equals(OkrLogAttribute.OBJ_PARENT.getDesc())) {
            setUpParentObjectiveName(orgId, okrUpdateLogDTO, actorUserId);
          }
          okrUpdateLogDTOs.add(okrUpdateLogDTO);
        }
        okrCommentDTO.setOkrUpdateLogDTOList(okrUpdateLogDTOs);
        // 只有公司管理员或者自己能删除空的comment
        if ((isOrgAdmin || actorUserId == okrComment.getUserId()) && CollectionUtils.isEmpty(okrUpdateLogDTOs)) {
          okrCommentDTO.setDeletable(true);
        } else {
          okrCommentDTO.setDeletable(false);
        }
        if (isOrgAdmin || actorUserId == okrComment.getUserId()) {
          okrCommentDTO.setEditable(true);
        } else {
          okrCommentDTO.setEditable(false);
        }
        okrCommentDTOs.add(okrCommentDTO);
      }
      long totalNumber = okrService.countOkrComment(orgId, objectiveId, keyResultId);
      result.setOkrCommentDTOList(okrCommentDTOs);
      result.setTotalRecordNum(totalNumber);
    } catch (Exception e) {
      LOGGER.error("listOkrComment-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  private void setUpParentObjectiveName(long orgId, OkrUpdateLogDTO okrUpdateLogDTO, long actorUserId) {
    long preParentObjectiveId = Long.valueOf(okrUpdateLogDTO.getBeforeValue());
    long afterParentObjectiveId = Long.valueOf(okrUpdateLogDTO.getAfterValue());

    okrUpdateLogDTO.setBeforeValue(getParentObjectiveName(orgId, preParentObjectiveId, actorUserId));
    okrUpdateLogDTO.setAfterValue(getParentObjectiveName(orgId, afterParentObjectiveId, actorUserId));
  }

  private String getParentObjectiveName(long orgId, long objectiveId, long actorUserId) {
    if (objectiveId == 0L) {
      return "无";
    }

    try {
      Objective objective = okrService.getObjective(orgId, objectiveId);
      long teamId = teamService.getTeamMemberByUserIdAndOrgId(orgId, actorUserId).getTeamId();
      List<Long> orgAdmins = securityModelService.listOrgAdminUserIdByOrgId(orgId);
      if (checkIfCanReadObjective(objective, teamId, orgAdmins, actorUserId)) {
        return objective.getContent();
      } else {
        return "「隐私目标」";
      }
    } catch (Exception e) {
      return "「已删除」";
    }
  }

  @Override
  @LogAround
  public OkrRemindSettingListDTO listOkrRemindSettingsByOrgId(long orgId, long actorUserId, long adminUserId) {
    OkrRemindSettingListDTO result = new OkrRemindSettingListDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      OkrRemindSetting objectiveRemind = okrService.getOkrRemindSettingByOrgIdAndRemindType(
              orgId, OkrRemindType.OBJECTIVE_DEADLINE.getCode());
      if (objectiveRemind == null) {
        objectiveRemind = new OkrRemindSetting();
        objectiveRemind.setRemindType(OkrRemindType.OBJECTIVE_DEADLINE.getCode());
        objectiveRemind.setFrequency(OkrRemindType.OBJECTIVE_DEADLINE.getDefaultFrequency());
      }
      OkrRemindSettingDTO objectiveRemindDTO = new OkrRemindSettingDTO();
      BeanUtils.copyProperties(objectiveRemind, objectiveRemindDTO);

      OkrRemindSetting periodRemind = okrService.getOkrRemindSettingByOrgIdAndRemindType(
              orgId, OkrRemindType.OBJECTIVE_PERIOD_DEADLINE.getCode());
      if (periodRemind == null) {
        periodRemind = new OkrRemindSetting();
        periodRemind.setRemindType(OkrRemindType.OBJECTIVE_PERIOD_DEADLINE.getCode());
        periodRemind.setFrequency(OkrRemindType.OBJECTIVE_PERIOD_DEADLINE.getDefaultFrequency());
      }
      OkrRemindSettingDTO periodRemindDTO = new OkrRemindSettingDTO();
      BeanUtils.copyProperties(periodRemind, periodRemindDTO);

      OkrRemindSetting keyResultRemind = okrService.getOkrRemindSettingByOrgIdAndRemindType(
              orgId, OkrRemindType.KEY_RESULT_DEADLINE.getCode());
      if (keyResultRemind == null) {
        keyResultRemind = new OkrRemindSetting();
        keyResultRemind.setRemindType(OkrRemindType.KEY_RESULT_DEADLINE.getCode());
        keyResultRemind.setFrequency(OkrRemindType.KEY_RESULT_DEADLINE.getDefaultFrequency());
      }
      OkrRemindSettingDTO keyResultRemindDTO = new OkrRemindSettingDTO();
      BeanUtils.copyProperties(keyResultRemind, keyResultRemindDTO);

      result.setOkrRemindSettingDTOList(Arrays.asList(objectiveRemindDTO, periodRemindDTO, keyResultRemindDTO));
    } catch (Exception e) {
      LOGGER.error("listOkrRemindSettingsByOrgId-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public VoidDTO batchUpdateOkrRemindSettings(
          long orgId, List<OkrRemindSettingDTO> okrRemindSettingDTOs, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      List<OkrRemindSetting> okrRemindSettings = new ArrayList<>();
      for (OkrRemindSettingDTO okrRemindSettingDTO : okrRemindSettingDTOs) {
        OkrRemindSetting okrRemindSetting = new OkrRemindSetting();
        BeanUtils.copyProperties(okrRemindSettingDTO, okrRemindSetting);
        okrRemindSettings.add(okrRemindSetting);
      }
      okrService.batchUpdateOkrRemindSetting(orgId, okrRemindSettings);
    } catch (Exception e) {
      LOGGER.error("batchUpdateOkrRemindSettings-error():{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }
}
