package hr.wozai.service.user.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.userorg.dto.IdListDTO;
import hr.wozai.service.user.client.userorg.dto.RoleListDTO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.user.client.userorg.facade.SecurityModelFacade;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.service.SecurityModelService;
import hr.wozai.service.user.server.service.TeamService;
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

import static org.junit.Assert.*;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/5/9
 */
public class SecurityModelFacadeImplTest extends TestBase{
  @Autowired
  SecurityModelFacade securityModelFacade;

  @Autowired
  SecurityModelService securityModelService;

  @Mock
  SecurityModelService spySMS;

  @Autowired
  TeamService teamService;

  @Mock
  TeamService spyTeamService;

  private long orgId = 199L;
  private long userId = 199L;
  private String resourceCode = ResourceCode.ORG.getResourceCode();

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(securityModelFacade), "securityModelService", spySMS);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(securityModelFacade), "teamService", spyTeamService);
  }

  @Test
  public void testCheckUserPermissionOnFunctionalResource() {
    Mockito.doReturn(true).when(spySMS).checkUserPermissionOnFunctionalResource(
            Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt());
    BooleanDTO result = securityModelFacade.checkUserPermissionOnFunctionalResource(
            orgId, userId, resourceCode, ActionCode.OPERATIONAL.getCode());
    Assert.assertTrue(result.getData());
  }

  @Test
  public void testCheckUserPermissionOnFunctionalResourceWithException() {
    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spySMS)
            .checkUserPermissionOnFunctionalResource(
            Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt());
    BooleanDTO result = securityModelFacade.checkUserPermissionOnFunctionalResource(
            orgId, userId, resourceCode, ActionCode.OPERATIONAL.getCode());
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testCheckUserPermissionOnRecordResource() {
    Mockito.doReturn(true).when(spySMS)
            .checkReportLinePermission(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
    Mockito.doReturn(true).when(spySMS).checkUserPermissionOnRecordResource(
            Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyLong()
    );

    BooleanDTO result = securityModelFacade.checkUserPermissionOnRecordResource(
            orgId, userId, ResourceCode.OKR.getResourceCode(), ActionCode.READ.getCode(), ResourceType.ORG.getCode(),
            userId);
    Assert.assertTrue(result.getData());

    result = securityModelFacade.checkUserPermissionOnRecordResource(
            orgId, userId, ResourceCode.REPORT_LINE.getResourceCode(), ActionCode.READ.getCode(), ResourceType.ORG.getCode(),
            userId);
    Assert.assertTrue(result.getData());

    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spySMS)
            .checkReportLinePermission(Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());
    result = securityModelFacade.checkUserPermissionOnRecordResource(
            orgId, userId, ResourceCode.REPORT_LINE.getResourceCode(), ActionCode.READ.getCode(), ResourceType.ORG.getCode(),
            userId);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testGetRoleListDTOByUserId() {
    Role role = new Role();
    role.setRoleName(DefaultRole.HR.getName());
    Mockito.doReturn(Arrays.asList(role)).when(spySMS).getRolesByUserId(Mockito.anyLong(), Mockito.anyLong());

    RoleListDTO result = securityModelFacade.getRoleListDTOByUserId(orgId, userId, -1L, -1L);
    Assert.assertEquals(1, result.getRoleDTOList().size());

    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spySMS)
            .getRolesByUserId(Mockito.anyLong(), Mockito.anyLong());
    result = securityModelFacade.getRoleListDTOByUserId(orgId, userId, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testListRoleListDTOByOrgId() {
    Role role = new Role();
    role.setRoleName(DefaultRole.HR.getName());
    Mockito.doReturn(Arrays.asList(role)).when(spySMS).listRolesByOrgId(Mockito.anyLong());

    RoleListDTO result = securityModelFacade.listRoleListDTOByOrgId(orgId, -1L, -1L);
    Assert.assertEquals(1, result.getRoleDTOList().size());

    Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST)).when(spySMS)
            .listRolesByOrgId(Mockito.anyLong());
    result = securityModelFacade.listRoleListDTOByOrgId(orgId, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_BAD_REQUEST.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testAssignRolesToUser() {
    long roleId = 199L;
    Mockito.doNothing().when(spySMS).assignRolesToUser(
            Mockito.anyLong(), Mockito.anyLong(), Mockito.anyList(), Mockito.anyLong());

    VoidDTO result = securityModelFacade.assignRolesToUser(orgId, userId, Arrays.asList(roleId), -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), result.getServiceStatusDTO().getCode());
  }

  @Test
  public void testListOrgAdminUserIdByOrgId() {
    Mockito.doReturn(Arrays.asList(userId)).when(spySMS).listOrgAdminUserIdByOrgId(Mockito.anyLong());

    IdListDTO result = securityModelFacade.listOrgAdminUserIdByOrgId(orgId, 0L, 0L);
    Assert.assertEquals(1, result.getIdList().size());
  }

  @Test
  public void testTransferSuperAdmin() {
    Mockito.doNothing().when(spySMS).transferSuperAdminRoleBetweenUsers(
            Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyLong());

    VoidDTO voidDTO = securityModelFacade.transferSuperAdminRoleBetweenUsers(orgId, 0L, 0L, 0L, 0L);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());
  }
}