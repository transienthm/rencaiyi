package hr.wozai.service.user.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.common.dto.RecentUsedObjectDTO;
import hr.wozai.service.user.client.common.dto.RemindSettingDTO;
import hr.wozai.service.user.client.common.dto.RemindSettingListDTO;
import hr.wozai.service.user.client.common.enums.RemindType;
import hr.wozai.service.user.client.common.enums.UserSearchType;
import hr.wozai.service.user.client.common.facade.CommonToolFacade;
import hr.wozai.service.user.client.conversation.dto.ConvrScheduleDTO;
import hr.wozai.service.user.client.conversation.enums.RemindDay;
import hr.wozai.service.user.client.okr.dto.UserAndTeamListDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.dto.TeamListDTO;
import hr.wozai.service.user.client.userorg.dto.UserNameListDTO;
import hr.wozai.service.user.client.userorg.enums.ContentIndexType;
import hr.wozai.service.user.client.userorg.enums.RecentUsedObjectType;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import hr.wozai.service.user.server.model.userorg.*;
import hr.wozai.service.user.server.service.*;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.test.utils.AopTargetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/6/1
 */
public class CommonToolFacadeImplTest extends TestBase{
  @Autowired
  CommonToolFacade commonToolFacade;

  @Autowired
  TeamService teamService;

  @Mock
  TeamService spyTeamService;

  @Autowired
  NameIndexService nameIndexService;

  @Mock
  NameIndexService spyNameIndexService;

  @Autowired
  UserEmploymentService userEmploymentService;

  @Mock
  UserEmploymentService spyUserEmploymentService;

  @Autowired
  UserProfileFacade userProfileFacade;

  @Mock
  UserProfileFacade spyUserProfileFacade;

  @Autowired
  RemindSettingService remindSettingService;

  private long orgId = 199L;
  private long userId = 199L;
  private long teamId = 199L;
  private long projectTeamId = 199L;
  private RecentUsedObjectDTO userOkr;
  private RecentUsedObjectDTO teamOkr;
  private RecentUsedObjectDTO projectTeamOkr;

  @Before
  public void setUp() throws Exception {
    userOkr = new RecentUsedObjectDTO();
    userOkr.setUserId(userId);
    userOkr.setType(RecentUsedObjectType.USER_OKR.getCode());
    userOkr.setUsedObjectId(Arrays.asList(String.valueOf(userId)));
    userOkr.setCreatedUserId(userId);

    teamOkr = new RecentUsedObjectDTO();
    teamOkr.setUserId(userId);
    teamOkr.setType(RecentUsedObjectType.TEAM_OKR.getCode());
    teamOkr.setUsedObjectId(Arrays.asList(String.valueOf(teamId)));
    teamOkr.setCreatedUserId(userId);

    projectTeamOkr = new RecentUsedObjectDTO();
    projectTeamOkr.setUserId(userId);
    projectTeamOkr.setType(RecentUsedObjectType.PROJECT_TEAM_OKR.getCode());
    projectTeamOkr.setUsedObjectId(Arrays.asList(String.valueOf(projectTeamId)));
    projectTeamOkr.setCreatedUserId(userId);

    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(commonToolFacade), "teamService", spyTeamService);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(commonToolFacade), "nameIndexService", spyNameIndexService);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(commonToolFacade), "userEmploymentService",
            spyUserEmploymentService);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(commonToolFacade), "userProfileFacade", spyUserProfileFacade);
  }

  @Test
  public void testAddRecentUsedObject() throws Exception {
    VoidDTO voidDTO = commonToolFacade.addRecentUsedObject(orgId, userOkr, -1L, -1L);
    Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    voidDTO = commonToolFacade.addRecentUsedObject(orgId, teamOkr, -1L, -1L);
    Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    voidDTO = commonToolFacade.addRecentUsedObject(orgId, projectTeamOkr, -1L, -1L);
    Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    CoreUserProfileDTO coreUserProfile = new CoreUserProfileDTO();
    coreUserProfile.setUserId(userId);
    CoreUserProfileListDTO coreUserProfileListDTO = new CoreUserProfileListDTO();
    coreUserProfileListDTO.setCoreUserProfileDTOs(Arrays.asList(coreUserProfile));
    Mockito.doReturn(coreUserProfileListDTO).when(spyUserProfileFacade).listCoreUserProfile(
            Mockito.eq(orgId), Mockito.anyList(), Mockito.anyLong(), Mockito.anyLong());

    Team team = new Team();
    team.setTeamId(teamId);
    Mockito.doReturn(Arrays.asList(team)).when(spyTeamService).listTeamByOrgIdAndTeamIds(
            Mockito.eq(orgId), Mockito.anyList());

    ProjectTeam projectTeam = new ProjectTeam();
    projectTeam.setProjectTeamId(projectTeamId);
    Mockito.doReturn(projectTeam).when(spyTeamService).getProjectTeamByPrimaryKeyAndOrgId(
            Mockito.eq(orgId), Mockito.eq(projectTeamId));

    UserAndTeamListDTO result = commonToolFacade.listRecentCheckedOkrUserAndTeam(orgId, userId, -1L, -1L);
    Assert.assertEquals(1, result.getTeamDTOList().size());
    Assert.assertEquals(1, result.getCoreUserProfileDTOList().size());
    Assert.assertEquals(1, result.getProjectTeamDTOList().size());
  }

  @Test
  public void testAddRecentUsedObjectWithException() throws Exception {
    userOkr.setUserId(null);
    VoidDTO voidDTO = commonToolFacade.addRecentUsedObject(orgId, userOkr, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_INVALID_PARAM.getCode(), voidDTO.getServiceStatusDTO().getCode());
  }

  @Test
  public void testListRecentCheckedOkrUserAndTeamWithException() throws Exception {
    VoidDTO voidDTO = commonToolFacade.addRecentUsedObject(orgId, userOkr, -1L, -1L);
    Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());
    voidDTO = commonToolFacade.addRecentUsedObject(orgId, teamOkr, -1L, -1L);
    Assert.assertEquals(voidDTO.getServiceStatusDTO().getCode(), ServiceStatus.COMMON_OK.getCode());

    UserAndTeamListDTO result = commonToolFacade.listRecentCheckedOkrUserAndTeam(orgId, userId, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testSearchUserAndTeamNamesByKeyword() throws Exception {
    int pageNumber = 1;
    int pageSize = 30;
    Mockito.doReturn(Arrays.asList(userId)).when(spyNameIndexService).listObjectIdsByContentOrPinyinOrAbbreviation(
            Mockito.anyLong(), Mockito.anyString(), Mockito.eq(ContentIndexType.USER_NAME.getCode()),
            Mockito.anyInt(), Mockito.anyInt());
    Mockito.doReturn(Arrays.asList(teamId)).when(spyNameIndexService).listObjectIdsByContentOrPinyinOrAbbreviation(
            Mockito.anyLong(), Mockito.anyString(), Mockito.eq(ContentIndexType.TEAM_NAME.getCode()),
            Mockito.anyInt(), Mockito.anyInt());
    Mockito.doReturn(Arrays.asList(projectTeamId)).when(spyNameIndexService).listObjectIdsByContentOrPinyinOrAbbreviation(
            Mockito.anyLong(), Mockito.anyString(), Mockito.eq(ContentIndexType.PROJECT_TEAM_NAME.getCode()),
            Mockito.anyInt(), Mockito.anyInt());
    Mockito.doReturn(1L).when(spyNameIndexService).countIdNumByKeywordAndType(
            Mockito.anyLong(), Mockito.anyString(), Mockito.eq(ContentIndexType.TEAM_NAME.getCode()));

    CoreUserProfileDTO coreUserProfile = new CoreUserProfileDTO();
    coreUserProfile.setUserId(userId);
    CoreUserProfileListDTO coreUserProfileListDTO = new CoreUserProfileListDTO();
    coreUserProfileListDTO.setCoreUserProfileDTOs(Arrays.asList(coreUserProfile));
    Mockito.doReturn(coreUserProfileListDTO).when(spyUserProfileFacade).listCoreUserProfile(
            Mockito.eq(orgId), Mockito.anyList(), Mockito.anyLong(), Mockito.anyLong());

    Team team = new Team();
    team.setTeamId(teamId);
    Mockito.doReturn(Arrays.asList(team)).when(spyTeamService).listTeamByOrgIdAndTeamIds(
            Mockito.eq(orgId), Mockito.anyList());

    ProjectTeam projectTeam = new ProjectTeam();
    projectTeam.setProjectTeamId(projectTeamId);
    Mockito.doReturn(projectTeam).when(spyTeamService).getProjectTeamByPrimaryKeyAndOrgId(
            Mockito.eq(orgId), Mockito.eq(projectTeamId));

    TeamListDTO result = commonToolFacade.searchUserAndTeamNamesByKeyword(orgId, "keyword",
            pageNumber, pageSize, -1L, -1L);
    Assert.assertEquals(1, result.getTeamDTOList().size());
    Assert.assertEquals(1, result.getCoreUserProfileDTOs().size());
    Assert.assertEquals(1, result.getTotalTeamNumber());
    Assert.assertEquals(0, result.getTotalUserNumber());
    Assert.assertEquals(1, result.getProjectTeamDTOs().size());
  }

  @Test
  public void testSearchUserAndTeamNamesByKeywordWithException() throws Exception {
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyNameIndexService)
            .listObjectIdsByContentOrPinyinOrAbbreviation(
            Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    TeamListDTO result = commonToolFacade.searchUserAndTeamNamesByKeyword(orgId, "keyword",
            1, 30, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testListUsersByUserNameOrPinyinOrAbbreviation() throws Exception {
    Mockito.doReturn(Arrays.asList(userId)).when(spyNameIndexService).listObjectIdsByContentOrPinyinOrAbbreviation
            (Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    Mockito.doReturn(1L).when(spyNameIndexService).countIdNumByKeywordAndType(
            Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt());

    UserNameListDTO result = commonToolFacade.listUsersByUserNameOrPinyinOrAbbreviation(
            orgId, "keyword", 1, 30, -1L, -1L);
    Assert.assertEquals(1, result.getIdList().size());
  }

  @Test
  public void testListUsersByUserNameOrPinyinOrAbbreviationWithException() throws Exception {
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyNameIndexService)
            .listObjectIdsByContentOrPinyinOrAbbreviation
            (Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    UserNameListDTO result = commonToolFacade.listUsersByUserNameOrPinyinOrAbbreviation(
            orgId, "keyword", 1, 30, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testSearchUsersWithTeamScope() throws Exception {
    Mockito.doReturn(Arrays.asList(userId)).when(spyNameIndexService).listObjectIdsByContentOrPinyinOrAbbreviation
            (Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    Mockito.doReturn(Arrays.asList(userId)).when(spyTeamService).getUserIdsByOrgIdAndTeamIds(
            Mockito.anyLong(), Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt());

    UserNameListDTO result = commonToolFacade.searchUsersWithTeamScope(orgId, teamId, "keyword", 1, 30, -1L, -1L);
    Assert.assertEquals(1, result.getIdList().size());
    Assert.assertEquals(1, result.getTotalRecordNum());
  }

  @Test
  public void testSearchUsersWithTeamScopeWithException() throws Exception {
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyNameIndexService)
            .listObjectIdsByContentOrPinyinOrAbbreviation
            (Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());

    UserNameListDTO result = commonToolFacade.searchUsersWithTeamScope(orgId, teamId, "keyword", 1, 30, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testSearchDirectorsByKeyword() throws Exception {
    Mockito.doReturn(Arrays.asList(userId)).when(spyNameIndexService).listObjectIdsByContentOrPinyinOrAbbreviation
            (Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    TeamMember teamMember = new TeamMember();
    teamMember.setTeamId(teamId);
    Mockito.doReturn(teamMember).when(spyTeamService).getTeamMemberByUserIdAndOrgId(Mockito.anyLong(), Mockito.anyLong());
    Mockito.doReturn(Arrays.asList(userId)).when(spyTeamService).getUserIdsByOrgIdAndTeamIds(
            Mockito.anyLong(), Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt());

    UserNameListDTO result = commonToolFacade.searchDirectorsByKeyword(orgId, "keyword", 1, 30, -1L, -1L);
    Assert.assertEquals(1, result.getIdList().size());
    Assert.assertEquals(1, result.getTotalRecordNum());
  }

  @Test
  public void testSearchDirectorsByKeywordWithException() throws Exception {
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyNameIndexService)
            .listObjectIdsByContentOrPinyinOrAbbreviation
            (Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());

    UserNameListDTO result = commonToolFacade.searchDirectorsByKeyword(orgId, "keyword", 1, 30, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testSearchUsersByKeywordAndType() throws Exception {
    Mockito.doReturn(1L).when(spyNameIndexService)
            .countIdNumByKeywordAndType(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt());
    Mockito.doReturn(Arrays.asList(userId)).when(spyNameIndexService)
            .listObjectIdsByContentOrPinyinOrAbbreviation
            (Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());

    UserNameListDTO result = commonToolFacade.searchUsersByKeywordAndType(orgId, "keyword",
            UserSearchType.NORMAL.getCode(), 1, 30, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), result.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, result.getIdList().size());

    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spyNameIndexService)
            .countIdNumByKeywordAndType(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt());
    result = commonToolFacade.searchUsersByKeywordAndType(orgId, "keyword",
            UserSearchType.NORMAL.getCode(), 1, 30, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testSearchUsersByKeywordAndTypeWithUserStatus() throws Exception {
    Mockito.doReturn(1L).when(spyNameIndexService)
            .countIdNumByKeywordAndType(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt());
    Mockito.doReturn(Arrays.asList(userId)).when(spyNameIndexService)
            .listObjectIdsByContentOrPinyinOrAbbreviation
                    (Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt());
    Mockito.doReturn(Arrays.asList(userId)).when(spyUserEmploymentService)
            .sublistUserIdByUserStatus(Mockito.anyLong(), Mockito.anyList(), Mockito.anyInt());

    UserNameListDTO result = commonToolFacade.searchUsersByKeywordAndType(orgId, "keyword",
            UserSearchType.ACTIVE.getCode(), 1, 30, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), result.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, result.getIdList().size());

    Mockito.doReturn(Arrays.asList(userId)).when(spyUserEmploymentService)
            .sublistUserIdNotResignedByEmploymentStatus(Mockito.anyLong(), Mockito.anyList(), Mockito.anyInt());
    result = commonToolFacade.searchUsersByKeywordAndType(orgId, "keyword",
            UserSearchType.UN_REGULAR.getCode(), 1, 30, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), result.getServiceStatusDTO().getCode());
    Assert.assertEquals(1, result.getIdList().size());

    result = commonToolFacade.searchUsersByKeywordAndType(orgId, "keyword",
            UserSearchType.AT.getCode(), 1, 30, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), result.getServiceStatusDTO().getCode());

    result = commonToolFacade.searchUsersByKeywordAndType(orgId, "keyword",
            100, 1, 30, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_INVALID_PARAM.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testRemindSetting() throws Exception {
    remindSettingService.initRemindSettingByUserId(orgId, userId, -1L);

    RemindSettingListDTO result = commonToolFacade.listRemindSettingByUserId(orgId, userId, -1L, -1L);
    Assert.assertEquals(3, result.getRemindSettingDTOList().size());

    VoidDTO voidDTO = commonToolFacade.batchUpdateRemindSetting(result, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());

    RemindSettingDTO remindSettingDTO = commonToolFacade.getRemindSettingByUserIdAndRemindType(
            orgId, userId, RemindType.NEWSFEED_AT.getCode(), -1L, -1L);
    Assert.assertEquals(userId, remindSettingDTO.getUserId().longValue());
  }


}