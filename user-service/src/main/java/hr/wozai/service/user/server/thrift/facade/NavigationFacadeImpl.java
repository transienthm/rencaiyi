package hr.wozai.service.user.server.thrift.facade;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.review.client.facade.ReviewTemplateFacade;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.FastJSONUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.servicecommons.utils.uuid.UUIDGenerator;
import hr.wozai.service.user.client.okr.enums.OkrType;
import hr.wozai.service.user.client.okr.enums.PeriodTimeSpan;
import hr.wozai.service.user.client.userorg.dto.NavigationDTO;
import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;
import hr.wozai.service.user.client.userorg.enums.ContentIndexType;
import hr.wozai.service.user.client.userorg.enums.SystemProfileField;
import hr.wozai.service.user.client.userorg.facade.NavigationFacade;
import hr.wozai.service.user.client.userorg.facade.UserFacade;
import hr.wozai.service.user.server.enums.NaviModule;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.model.navigation.Navigation;
import hr.wozai.service.user.server.model.okr.Director;
import hr.wozai.service.user.server.model.okr.KeyResult;
import hr.wozai.service.user.server.model.okr.Objective;
import hr.wozai.service.user.server.model.okr.ObjectivePeriod;
import hr.wozai.service.user.server.model.userorg.*;
import hr.wozai.service.user.server.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/11/3
 */
@Service("navigationFacade")
public class NavigationFacadeImpl implements NavigationFacade {

  private static Logger LOGGER = LoggerFactory.getLogger(NavigationFacadeImpl.class);

  @Autowired
  private OnboardingFlowService onboardingFlowService;

  @Autowired
  private UserFacade userFacade;

  @Autowired
  private TeamService teamService;

  @Autowired
  private NameIndexService nameIndexService;

  @Autowired
  private OkrService okrService;

  @Autowired
  private NavigationService navigationService;

  @Autowired
  private TokenService tokenService;

  @Autowired
  private UserProfileService userProfileService;

  @Autowired
  private AuthenticationService authenticationService;

  @Autowired
  private OrgService orgService;

  @Autowired
  private UserService userService;

  @Autowired
  @Qualifier("reviewTemplateFacadeProxy")
  private ThriftClientProxy reviewTemplateFacadeProxy;

  private ReviewTemplateFacade reviewTemplateFacade;

  private static final String EMAIL_SUFFIX = "@sqian.com";
  private static final String MOBILE_PHONE = "mobilePhone";
  private static final String FULL_NAME = "fullName";
  private static final String FULL_ORG_NAME = "fullOrgName";
  private JSONObject jsonObject = null;

  @PostConstruct
  public void init() throws Exception {
    reviewTemplateFacade = (ReviewTemplateFacade) reviewTemplateFacadeProxy.getObject();
    BufferedReader br;
    try {
      InputStream resource = getClass().getResourceAsStream("/navi/navi.json");
      br = new BufferedReader(new InputStreamReader(resource));
      String line;
      StringBuilder stringBuilder = new StringBuilder();
      while (null != (line = br.readLine())) {
        stringBuilder.append(line);
      }
      jsonObject = JSONObject.parseObject(stringBuilder.toString());
    } catch (Exception e) {
      LOGGER.error("init()-error: fail to parse navi config file");
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_JSON);
    }

  }

  @Override
  @LogAround
  public VoidDTO initNaviOrg(long orgId, long userId, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // init org
      Org org = getOrgFromJsonObject();
      org.setTimeZone(1);
      org.setIsNaviOrg(1);
      Map<String, String> fieldValues = getSuperAdminFieldValues();

      CoreUserProfile superAdmin = onboardingFlowService.createOrgAndFirstUser(org, fieldValues, new UserEmployment());
      long naviOrgId = superAdmin.getOrgId();
      long naviAdminId = superAdmin.getUserId();
      Team rootTeam = teamService.listNextLevelTeams(naviOrgId, 0L).get(0);

      // init team
      HashMap<Long, Long> teamIdMap = new HashMap<>();
      teamIdMap.put(0L, rootTeam.getTeamId());
      List<Team> teams = getTeamsFromJsonObject();
      initTeams(naviOrgId, teams, teamIdMap, naviAdminId);

      // init user and team member and init navigation
      HashMap<Long, Long> userIdMap = new HashMap<>();
      userIdMap.put(0L, naviAdminId);
      initUsersAntTeamMember(orgId, userId, naviOrgId, teamIdMap, userIdMap, naviAdminId, naviAdminId);

      // init okr
      initOkrs(naviOrgId, teamIdMap, userIdMap, naviAdminId);

      // init review
      VoidDTO remoteResult = reviewTemplateFacade.initReviewGuide(
              naviOrgId, naviAdminId, jsonObject.getString("reviews"), userIdMap, teamIdMap);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        LOGGER.error("initReviewGuide()-error");
        throw new ServiceStatusException(serviceStatus);
      }
    } catch (Exception e) {
      LOGGER.error("initNaviOrg()-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  private void initOkrs(long naviOrgId, Map<Long, Long> teamIdMap, Map<Long, Long> userIdMap, long actorUserId) {
    Map<Long, Long> objectiveIdMap = new HashMap<>();
    JSONArray okRs = jsonObject.getJSONArray("okrs");
    for (Object okr : okRs) {
      JSONObject objectivePeriodJson = (JSONObject)okr;
      ObjectivePeriod objectivePeriod = getObjectivePeriodFromJson(
              naviOrgId, objectivePeriodJson, teamIdMap, userIdMap, actorUserId);
      long periodId = okrService.createObjectivePeriod(objectivePeriod);

      JSONArray objectives = objectivePeriodJson.getJSONArray("objectives");
      for (Object obj : objectives) {
        JSONObject objectiveJson = (JSONObject) obj;
        long preObjectiveId = objectiveJson.getLong("objectiveId");
        Objective objective = getObjectiveFromJson(
                naviOrgId, objectiveJson, periodId, objectiveIdMap, objectivePeriod, actorUserId);

        List<Director> directorList = getDirectorListFromJson(naviOrgId, objectiveJson, userIdMap, actorUserId);

        long objectiveId = okrService.createObjectiveAndDirector(objective, directorList);
        objectiveIdMap.put(preObjectiveId, objectiveId);

        if (objectiveJson.containsKey("keyResults")) {
          JSONArray krs = objectiveJson.getJSONArray("keyResults");
          for (Object kr : krs) {
            JSONObject keyResultJson = (JSONObject)kr;
            KeyResult keyResult = getKeyResultFromJson(naviOrgId, keyResultJson, objectiveId, actorUserId);
            directorList = getDirectorListFromJson(naviOrgId, keyResultJson, userIdMap, actorUserId);
            okrService.createKeyResultAndDirector(keyResult, directorList);
          }
        }
      }
    }
  }

  private KeyResult getKeyResultFromJson(long naviOrgId, JSONObject keyResultJson, long objectiveId, long actorUserId) {
    KeyResult keyResult = JSONObject.toJavaObject(keyResultJson, KeyResult.class);
    keyResult.setOrgId(naviOrgId);
    keyResult.setObjectiveId(objectiveId);
    keyResult.setCreatedUserId(actorUserId);
    keyResult.setLastModifiedUserId(actorUserId);
    if (keyResult.getDeadline() != null) {
      keyResult.setDeadline(TimeUtils.getNowTimestmapInMillis() + keyResult.getDeadline());
    }
    return keyResult;
  }

  private List<Director> getDirectorListFromJson(long naviOrgId, JSONObject objectJson, Map<Long, Long> userIdMap, long actorUserId) {
    List<Director> directorList = new ArrayList<>();
    if (objectJson.containsKey("directorList")) {
      JSONArray directors = objectJson.getJSONArray("directorList");
      for (Object dir : directors) {
        JSONObject directorJson = (JSONObject)dir;
        Director director = new Director();
        director.setOrgId(naviOrgId);
        director.setUserId(userIdMap.get(directorJson.getLong("userId")));
        director.setCreatedUserId(actorUserId);
        director.setLastModifiedUserId(actorUserId);
        directorList.add(director);
      }
    }
    return directorList;
  }

  private Objective getObjectiveFromJson(
          long naviOrgId, JSONObject objectiveJson, long periodId,
          Map<Long, Long> objectiveIdMap, ObjectivePeriod objectivePeriod, long actorUserId) {
    Objective objective = JSONObject.toJavaObject(objectiveJson, Objective.class);
    objective.setOrgId(naviOrgId);
    objective.setType(objectivePeriod.getType());
    objective.setOwnerId(objectivePeriod.getOwnerId());
    objective.setObjectivePeriodId(periodId);
    objective.setCreatedUserId(actorUserId);
    objective.setLastModifiedUserId(actorUserId);
    if (objective.getParentObjectiveId() != 0) {
      objective.setParentObjectiveId(objectiveIdMap.get(objective.getParentObjectiveId()));
    }
    if (objective.getDeadline() != null) {
      objective.setDeadline(TimeUtils.getNowTimestmapInMillis() + objective.getDeadline());
    }
    return objective;
  }

  private ObjectivePeriod getObjectivePeriodFromJson(long naviOrgId, JSONObject objectivePeriodJson, Map<Long, Long> teamIdMap, Map<Long, Long> userIdMap, long actorUserId) {
    ObjectivePeriod objectivePeriod = new ObjectivePeriod();
    objectivePeriod.setOrgId(naviOrgId);
    int type = objectivePeriodJson.getInteger("type");
    objectivePeriod.setType(type);
    if (type == OkrType.ORG.getCode()) {
      objectivePeriod.setOwnerId(naviOrgId);
    } else if (type == OkrType.TEAM.getCode()) {
      objectivePeriod.setOwnerId(teamIdMap.get(objectivePeriodJson.getLong("ownerId")));
    } else if (type == OkrType.PERSON.getCode()) {
      objectivePeriod.setOwnerId(userIdMap.get(objectivePeriodJson.getLong("ownerId")));
    }

    PeriodTimeSpan periodTimeSpan = PeriodTimeSpan.getEnumByName(objectivePeriodJson.getString("periodTimeSpanName"));
    objectivePeriod.setPeriodTimeSpanId(periodTimeSpan.getCode());
    int year = objectivePeriodJson.getInteger("year");
    objectivePeriod.setYear(year);
    objectivePeriod.setName(PeriodTimeSpan.getNameByYearAndPeriodTimeSpan(year, periodTimeSpan));
    objectivePeriod.setCreatedUserId(actorUserId);
    objectivePeriod.setLastModifiedUserId(actorUserId);
    return objectivePeriod;
  }

  private void initUsersAntTeamMember(
          long orgId, long userId, long naviOrgId,
          Map<Long, Long> teamIdMap, Map<Long, Long> userIdMap, long actorUserId, long adminUserId) {
    Map<Long, Long> reportlineMap = new HashMap<>();
    JSONArray users = jsonObject.getJSONArray("users");
    for (Object o : users) {
      JSONObject user = (JSONObject)o;
      long preUserId = user.getLong("userId");
      String emailAddress = getEmailAddress();
      String mobilePhone = user.getString(MOBILE_PHONE);
      String fullName = user.getString(FULL_NAME);
      long teamId = user.getLong("teamId");
      long newUserId = onboardingFlowService
              .individuallyImportStaff(naviOrgId, fullName, emailAddress, mobilePhone, actorUserId, adminUserId);

      TeamMember teamMember = new TeamMember();
      teamMember.setOrgId(naviOrgId);
      teamMember.setUserId(newUserId);
      teamMember.setTeamId(teamIdMap.get(teamId));
      teamMember.setCreatedUserId(actorUserId);
      teamMember.setLastModifiedUserId(actorUserId);

      teamService.deleteTeamMember(naviOrgId, newUserId, actorUserId);
      teamService.addTeamMember(teamMember);

      userIdMap.put(preUserId, newUserId);

      if (user.containsKey("isOwner")) {
        Navigation navigation = new Navigation();
        navigation.setOrgId(orgId);
        navigation.setUserId(userId);
        navigation.setNaviOrgId(naviOrgId);
        navigation.setNaviUserId(newUserId);
        navigation.setNaviModule(NaviModule.OBJECTIVE.getCode());
        navigation.setNaviStep(0);
        navigation.setCreatedUserId(actorUserId);
        navigationService.insertNavigation(navigation);
      }
      if (user.containsKey("reportor")) {
        long reportorUserId = user.getLong("reportor");
        reportlineMap.put(newUserId, reportorUserId);
      }

      userProfileService.updateUserStatus(naviOrgId, newUserId, UserStatus.ACTIVE.getCode(), actorUserId);
    }

    for (Long key : reportlineMap.keySet()) {
      Long reportorUserId = userIdMap.get(reportlineMap.get(key));
      userService.batchUpdateReportLine(naviOrgId, Arrays.asList(key), reportorUserId, actorUserId);
    }

  }

  private void initTeams(long naviOrgId, List<Team> teams, Map<Long, Long> teamIdMap, long actorUserId) {
    for (Team team : teams) {
      long preTeamId = team.getTeamId();
      team.setOrgId(naviOrgId);
      team.setCreatedUserId(actorUserId);
      if (!teamIdMap.containsKey(team.getParentTeamId())) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_JSON);
      }
      team.setParentTeamId(teamIdMap.get(team.getParentTeamId()));

      long teamId = teamService.addTeam(team);
      /*nameIndexService.addContentIndex(team.getOrgId(), teamId,
              ContentIndexType.TEAM_NAME.getCode(), team.getTeamName());*/
      teamIdMap.put(preTeamId, teamId);
    }
  }

  private String getEmailAddress() {
    return UUIDGenerator.generateShortUuid()  + TimeUtils.getNowTimestmapInMillis() + EMAIL_SUFFIX;
  }

  private Org getOrgFromJsonObject() {
    Org org = JSONObject.toJavaObject(jsonObject.getJSONObject("org"), Org.class);
    org.setFullName(jsonObject.getJSONObject("org").getString(FULL_ORG_NAME));
    return org;
  }

  private Map<String, String> getSuperAdminFieldValues() {
    JSONObject orgJson = jsonObject.getJSONObject("org");
    Map<String, String> fieldValues = new HashMap<>();
    fieldValues.put(SystemProfileField.EMAIL_ADDRESS.getReferenceName(), getEmailAddress());
    fieldValues.put(SystemProfileField.MOBILE_PHONE.getReferenceName(), orgJson.getString(MOBILE_PHONE));
    fieldValues.put(SystemProfileField.FULL_NAME.getReferenceName(), orgJson.getString(FULL_NAME));

    return fieldValues;
  }

  private List<Team> getTeamsFromJsonObject() {
    JSONArray teamArray = jsonObject.getJSONArray("teams");
    List<Team> result = FastJSONUtils.convertJSONArrayToObjectList(teamArray, Team.class);
    return result;
  }

  @Override
  @LogAround
  public TokenPairDTO deleteNaviOrgAndRedirectToTrueOrg(long naviOrgId, long naviUserId, long actorUserId, long adminUserId) {
    TokenPairDTO result = new TokenPairDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      // 1) delete all token
      tokenService.deleteAllTokensByOrgIdAndUserId(naviOrgId, naviUserId);

      // 2) delete navigation
      Navigation navigation = navigationService.findNavigationByNaviOrgIdAndNaviUserId(naviOrgId, naviUserId);
      navigation.setIsDeleted(1);
      navigationService.updateNavigation(navigation);

      // 3) add new token
      TokenPairDTO tokenPair = tokenService.addAccessTokenAndRefreshToken(
              navigation.getOrgId(), true, navigation.getUserId(), adminUserId);
      result.setAccessToken(tokenPair.getAccessToken());
      result.setRefreshToken(tokenPair.getRefreshToken());
    } catch (Exception e) {
      LOGGER.error("deleteNaviOrgAndRedirectToTrueOrg()-error", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    return result;
  }

  @Override
  @LogAround
  public VoidDTO updateNavigation(NavigationDTO navigationDTO, long actorUserId, long adminUserId) {
    VoidDTO result = new VoidDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Navigation navigation = new Navigation();
      BeanUtils.copyProperties(navigationDTO, navigation);

      navigationService.updateNavigation(navigation);
    } catch (Exception e) {
      LOGGER.error("updateNavigation()-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }

  @Override
  @LogAround
  public NavigationDTO getNavigation(long naviOrgId, long naviUserId, long actorUserId, long adminUserId) {
    NavigationDTO result = new NavigationDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      Navigation navigation = navigationService.findNavigationByNaviOrgIdAndNaviUserId(naviOrgId, naviUserId);
      BeanUtils.copyProperties(navigation, result);
    } catch (Exception e) {
      LOGGER.error("updateNavigation()-error:", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }
    return result;
  }
}
