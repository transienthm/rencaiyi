package hr.wozai.service.user.server.test.thrift.facade;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.commons.enums.ContractType;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.enums.UserStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.userorg.dto.*;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.facade.UserFacade;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.user.server.dao.userorg.UserAccountDao;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.model.userorg.*;
import hr.wozai.service.user.server.service.*;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.test.utils.AopTargetUtils;
import hr.wozai.service.user.server.test.utils.InitializationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/26
 */
public class UserFacadeImplTest extends TestBase {
  @Autowired
  UserFacade userFacade;

  @Autowired
  OnboardingFlowService onboardingFlowService;

  @Autowired
  TeamService teamService;

  @Autowired
  UserProfileService userProfileService;

  @Autowired
  OrgService orgService;

  @Autowired
  UserService userService;

  private long orgId;
  private long userId;
  private long teamId;
  private String emailAddress = "test1@shanqian.com";
  private String password = "Sq1234567@@";
  private String orgName = "sq";

  @Before
  public void setUp() throws Exception {
    CoreUserProfile adminUser = InitializationUtils.initDefaultOrgAndFirstUser(onboardingFlowService);
    orgId = adminUser.getOrgId();
    Org org = orgService.getOrg(orgId);
    orgName = org.getShortName();

    TeamMember teamMember = InitializationUtils.initDefaultUserAndTeam(onboardingFlowService, teamService, orgId);
    teamId = teamMember.getTeamId();
    userId = teamMember.getUserId();

    CoreUserProfile coreUserProfile = userProfileService.getCoreUserProfileByOrgIdAndUserId(orgId, userId);
    emailAddress = coreUserProfile.getEmailAddress();
  }

  @Test
  public void testUserAccount() throws Exception {
    VoidDTO voidDTO = userFacade.initPassword(orgId, userId, password);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    voidDTO = userFacade.changePassword(orgId, userId, password, password + "@", userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    voidDTO = userFacade.resetPasswordWhenOnboarding(orgId, userId, password);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    voidDTO = userFacade.resetPasswordWhenMissingPwd(orgId, userId, password);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    UserAccountDTO userAccountDTO = userFacade.getUserAccountByEmail(emailAddress, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), userAccountDTO.getServiceStatusDTO().getCode());
    UserAccount userAccount = new UserAccount();
    BeanUtils.copyProperties(userAccountDTO, userAccount);
    JSONObject jsonObject = userAccount.getExtend();
    if (null == jsonObject) {
      jsonObject = new JSONObject();
    }
    jsonObject.put("first_login_timestamp", TimeUtils.getNowTimestmapInMillis());
    userAccount.setExtend(jsonObject);
    userAccount.setLastModifiedUserId(userId);
    userService.updateUserAccount(userAccount);

    LongDTO longDTO = userFacade.getOrgIdByUserId(userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), longDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(orgId, longDTO.getData());

    BooleanDTO booleanDTO = userFacade.hasPassword(userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), booleanDTO.getServiceStatusDTO().getCode());
    Assert.assertTrue(booleanDTO.getData());

    booleanDTO = userFacade.verifyUserAccountWithPassword(orgId, userId, password, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), booleanDTO.getServiceStatusDTO().getCode());
    Assert.assertTrue(booleanDTO.getData());

    booleanDTO = userFacade.signUpWithEmail(orgName, "ll123@sqian.com", password);
    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), booleanDTO.getServiceStatusDTO().getCode());

    voidDTO = userFacade.loginWithEmail(emailAddress, password, true);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    UserInfoDTO userInfoDTO = userFacade.getUserInfoByEmail(emailAddress);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), userInfoDTO.getServiceStatusDTO().getCode());

  }

  @Test
  public void testTeam() throws Exception {
    TeamDTO teamDTO = new TeamDTO();
    teamDTO.setOrgId(orgId);
    teamDTO.setTeamName("teamB");
    teamDTO.setParentTeamId(teamId);
    teamDTO.setCreatedUserId(userId);
    LongDTO subTeamId = userFacade.addTeam(teamDTO, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), subTeamId.getServiceStatusDTO().getCode());

    TeamDTO subTeam = userFacade.getTeam(orgId, subTeamId.getData(), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), subTeam.getServiceStatusDTO().getCode());
    Assert.assertEquals("teamB", subTeam.getTeamName());

    TeamListDTO teamListDTO = userFacade.listNextLevelTeams(orgId, 0L, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), teamListDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(2, teamListDTO.getTeamDTOList().size());

    subTeam.setTeamName("teamC");
    LongDTO update = userFacade.updateTeam(subTeam, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), update.getServiceStatusDTO().getCode());

    teamListDTO = userFacade.listSubordinateTeamsAndMembers(orgId, teamId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), teamListDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, teamListDTO.getTeamDTOList().size());
    Assert.assertEquals(subTeam.getTeamId().longValue(), teamListDTO.getTeamDTOList().get(0).getTeamId().longValue());
    Assert.assertEquals(1, teamListDTO.getCoreUserProfileDTOs().size());

    teamListDTO = userFacade.listUpTeamLineByTeamId(orgId, subTeamId.getData(), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), teamListDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(2, teamListDTO.getTeamDTOList().size());

    VoidDTO voidDTO = userFacade.transferTeamsAndTeamMembers(
            orgId, Arrays.asList(subTeamId.getData()), new ArrayList<>(), 0L, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    teamListDTO = userFacade.listAllTeams(orgId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), teamListDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(3, teamListDTO.getTeamDTOList().size());

    teamListDTO = userFacade.listTeamsByTeamIds(orgId, Arrays.asList(teamId), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), teamListDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, teamListDTO.getTeamDTOList().size());

    TeamMemberDTO teamMemberDTO = userFacade.getTeamMemberByUserId(orgId, userId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), teamMemberDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(userId, teamMemberDTO.getUserId().longValue());
    Assert.assertEquals(teamId, teamMemberDTO.getTeamId().longValue());

    BooleanDTO booleanDTO = userFacade.assignUsersToTeam(
            orgId, Arrays.asList(userId), subTeamId.getData(), userId, userId);
    Assert.assertEquals(ServiceStatus.UO_TEAM_MEMBER_EXIST.getCode(), booleanDTO.getServiceStatusDTO().getCode());

    UserNameListDTO userNameListDTO = userFacade.listTeamMembers(orgId, subTeamId.getData(), "", 1, 20, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), userNameListDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(0, userNameListDTO.getIdList().size());

    IdListDTO idListDTO = userFacade.listUnResignedAndHasReportorTeamMembersForReview(
            orgId, Arrays.asList(teamId), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), idListDTO.getServiceStatusDTO().getCode());

    teamMemberDTO.setIsTeamAdmin(1);
    voidDTO = userFacade.updateTeamAdmin(orgId, teamMemberDTO, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    TeamMemberListDTO teamMemberListDTO = userFacade.listTeamMemberInfoByUserIds
            (orgId, Arrays.asList(userId), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), teamMemberListDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, teamMemberListDTO.getTeamMemberDTOList().size());

    CoreUserProfileListDTO cupListDTO = userFacade.getTeamMembersByTeamId(
            orgId, teamId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), cupListDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, cupListDTO.getCoreUserProfileDTOs().size());

    teamListDTO = userFacade.fetchTeamAndUserProfiles(orgId, Arrays.asList(teamId), Arrays.asList(userId));
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), teamListDTO.getServiceStatusDTO().getCode());

    booleanDTO = userFacade.deleteTeam(orgId, subTeamId.getData(), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), booleanDTO.getServiceStatusDTO().getCode());
  }

  @Test
  public void testProjectTeam() throws Exception {
    ProjectTeamDTO projectTeamDTO = new ProjectTeamDTO();
    projectTeamDTO.setOrgId(orgId);
    projectTeamDTO.setTeamId(teamId);
    projectTeamDTO.setProjectTeamName("project team");
    projectTeamDTO.setCreatedUserId(userId);

    LongDTO projectTeamId = userFacade.addProjectTeam(orgId, projectTeamDTO, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), projectTeamId.getServiceStatusDTO().getCode());

    ProjectTeamDTO inDb = userFacade.getProjectTeamByPrimaryKeyAndOrgId(orgId, projectTeamId.getData(), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), inDb.getServiceStatusDTO().getCode());
    Assert.assertEquals("project team", inDb.getProjectTeamName());

    inDb.setProjectTeamName("update");
    VoidDTO voidDTO = userFacade.updateProjectTeam(orgId, inDb, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    ProjectTeamListDTO projectTeamListDTO = userFacade.listProjectTeamsByOrgIdAndTeamId(orgId, teamId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), projectTeamListDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, projectTeamListDTO.getProjectTeamDTOs().size());

    ProjectTeamMemberDTO projectTeamMemberDTO = new ProjectTeamMemberDTO();
    projectTeamMemberDTO.setProjectTeamId(projectTeamId.getData());
    projectTeamMemberDTO.setUserId(userId);
    ProjectTeamMemberListDTO projectTeamMemberListDTO = new ProjectTeamMemberListDTO();
    projectTeamMemberListDTO.setProjectTeamMemberDTOs(Arrays.asList(projectTeamMemberDTO));

    voidDTO = userFacade.batchInsertProjectTeamMember(orgId, projectTeamMemberListDTO, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_CREATED.getCode(), voidDTO.getServiceStatusDTO().getCode());

    ProjectTeamMemberListDTO ptmDTO = userFacade.listProjectTeamMembersByOrgIdAndUserId(orgId, userId, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), ptmDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, ptmDTO.getProjectTeamMemberDTOs().size());

    CoreUserProfileListDTO cupDTP = userFacade.listProjectTeamMembersByOrgIdAndProjectTeamId(
            orgId, projectTeamId.getData(), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), cupDTP.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, cupDTP.getCoreUserProfileDTOs().size());

    voidDTO = userFacade.batchDeleteProjectTeamMember(orgId, projectTeamMemberListDTO, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    voidDTO = userFacade.deleteProjectTeam(orgId, projectTeamId.getData(), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());
  }

  @Test
  public void testReportor() throws Exception {
    long userId2 = InitializationUtils.initDefaultUserAndTeam(onboardingFlowService, teamService, orgId).getUserId();

    VoidDTO voidDTO = userFacade.batchInsertReportLine(orgId, Arrays.asList(299L), userId, userId2, userId2);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    voidDTO = userFacade.batchUpdateReportLine(orgId, Arrays.asList(userId2), userId, userId2, userId2);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    CoreUserProfileListDTO cup = userFacade.listReporteesByUserIdAndOrgId(orgId, userId, userId2, userId2);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), cup.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, cup.getCoreUserProfileDTOs().size());

    CoreUserProfileDTO coreUserProfileDTO = userFacade.getReportorByUserIdAndOrgId(orgId, userId2, userId2, userId2);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), coreUserProfileDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(userId, coreUserProfileDTO.getUserId().longValue());

    ReportLineInfoDTO reportLineInfoDTO = userFacade.getReportLineInfo(orgId, userId2, false, userId2, userId2);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), reportLineInfoDTO.getServiceStatusDTO().getCode());

    ReportLineListDTO reportLineListDTO = userFacade.listReportLineByUserIds(orgId, Arrays.asList(userId2), userId2, userId2);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), reportLineListDTO.getServiceStatusDTO().getCode());

    IdListDTO idListDTO = userFacade.listUsersWhoHasReportorByOrgId(orgId, userId2, userId2);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), idListDTO.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, idListDTO.getIdList().size());
  }
}