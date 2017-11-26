package hr.wozai.service.user.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;
import hr.wozai.service.user.client.userorg.enums.UuidUsage;
import hr.wozai.service.user.server.dao.securitymodel.RoleDao;
import hr.wozai.service.user.server.dao.securitymodel.UserRoleDao;
import hr.wozai.service.user.server.dao.token.TokenRedis;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.model.securitymodel.UserRole;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.user.server.model.token.UuidInfo;
import hr.wozai.service.user.server.service.TokenService;
import hr.wozai.service.user.server.test.utils.AopTargetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/29
 */
public class TokenServiceImplTest extends TestBase{
  @Rule
  public ExpectedException thrown= ExpectedException.none();

  @Autowired
  TokenService tokenService;

  @Autowired
  RoleDao roleDao;

  @Mock
  RoleDao spyRoleDao;

  @Autowired
  UserRoleDao userRoleDao;

  @Mock
  UserRoleDao spyUserRoleDao;

  @Autowired
  TokenRedis tokenRedis;

  @Mock
  TokenRedis spyTokenRedis;

  private long orgId = 199L;
  private long adminId = 0L;
  private long userId = 199L;
  private long roleId = 199L;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(tokenService), "roleDao", spyRoleDao);
    ReflectionTestUtils.setField(AopTargetUtils.getTarget(tokenService), "userRoleDao", spyUserRoleDao);
  }

  @Test
  public void testAccessTokenAndRefreshToken() throws Exception {
    TokenPairDTO result = tokenService.addAccessTokenAndRefreshToken(orgId, true, userId, adminId);
    Assert.assertNotNull(result.getAccessToken());
    Assert.assertNotNull(result.getRefreshToken());

    tokenService.deleteAccessToken(result.getAccessToken(), -1L);

    thrown.expect(ServiceStatusException.class);
    tokenService.deleteAccessToken("aa", -1L);
  }

  @Test
  public void testAccessTokenAndRefreshTokenWithInvalidUserId() throws Exception {
    thrown.expect(ServiceStatusException.class);
    tokenService.addAccessTokenAndRefreshToken(orgId, true, -100L, adminId);
  }

  @Test
  public void testRefreshAccessToken() throws Exception {
    TokenPairDTO result = tokenService.addAccessTokenAndRefreshToken(orgId, true, userId, adminId);

    ReflectionTestUtils.setField(AopTargetUtils.getTarget(tokenService), "tokenRedis", spyTokenRedis);
    Mockito.doReturn(result.getRefreshToken()).when(spyTokenRedis).getRefreshTokenByKey(Mockito.anyString());

    tokenService.refreshAccessToken(result.getAccessToken(), result.getRefreshToken());

    Mockito.doReturn(null).when(spyTokenRedis).getRefreshTokenByKey(Mockito.anyString());

    thrown.expect(ServiceStatusException.class);
    tokenService.refreshAccessToken(result.getAccessToken(), result.getRefreshToken());
  }

  @Test
  public void testRefreshAccessTokenWithInvalidToken() throws Exception {
    thrown.expect(ServiceStatusException.class);
    tokenService.refreshAccessToken("aa", "bb");
  }

  @Test
  public void testRefreshAccessTokenWithEmptyToken() throws Exception {
    thrown.expect(ServiceStatusException.class);
    tokenService.refreshAccessToken("", "");
  }

  @Test
  public void testDeleteAllTokensByOrgIdAndUserId() {
    Role orgAdmin = new Role();
    orgAdmin.setRoleId(roleId);
    Mockito.doReturn(orgAdmin).when(spyRoleDao).findRoleByRoleName(Mockito.anyLong(), Mockito.anyString());
    UserRole userRole = new UserRole();
    userRole.setUserId(userId);
    userRole.setOrgId(orgId);
    userRole.setRoleId(roleId);

    Mockito.doReturn(Arrays.asList(userRole)).when(spyUserRoleDao).listOrgAdmin(Mockito.anyLong(), Mockito.anyLong());

    tokenService.deleteAllTokensByOrgIdAndUserId(orgId, userId);
  }

  @Test
  public void testUuidAndTemporaryToken() throws Exception {
    long orgId = 199L;
    long userId = 199L;
    UuidInfo uuidInfo = new UuidInfo();
    uuidInfo.setOrgId(orgId);
    uuidInfo.setUserId(userId);
    uuidInfo.setUUIDValue();
    uuidInfo.setUuidUsage(UuidUsage.ONBOARDING.getCode());
    uuidInfo.setExpireTime(System.currentTimeMillis() + 3600 * 1000);
    uuidInfo.setCreatedUserId(userId);

    UuidInfo u1 = tokenService.addUuidInfoAndDisablePrevious(uuidInfo);
    String uuid = u1.getUuid();
    Assert.assertEquals(uuid, uuidInfo.getUuid());

    List<UuidInfo> uuidInfoList = tokenService.listUuidInfosByUserIdAndUsage(orgId, userId,
            UuidUsage.ONBOARDING.getCode(), Long.MIN_VALUE);
    Assert.assertEquals(1, uuidInfoList.size());

    String token = tokenService.getTemporaryTokenByUuid(uuid);
    Assert.assertNotNull(token);

    tokenService.deleteUuidInfoByUserIdAndUsage(orgId, userId, UuidUsage.ONBOARDING.getCode());
    uuidInfoList = tokenService.listUuidInfosByUserIdAndUsage(orgId, userId,
            UuidUsage.ONBOARDING.getCode(), Long.MIN_VALUE);
    Assert.assertEquals(0, uuidInfoList.size());


    tokenService.deleteTemporaryToken(orgId, userId, UuidUsage.ONBOARDING.getCode());
  }

  @Test
  public void testGetTemporaryTokenWithNullUUID() throws Exception {
    thrown.expect(ServiceStatusException.class);
    tokenService.getTemporaryTokenByUuid("uuid");
  }

  @Test
  public void testGetTemporaryTokenWithExpiredUUID() throws Exception {
    long orgId = 199L;
    long userId = 199L;
    UuidInfo uuidInfo = new UuidInfo();
    uuidInfo.setOrgId(orgId);
    uuidInfo.setUserId(userId);
    uuidInfo.setUUIDValue();
    uuidInfo.setUuidUsage(UuidUsage.INIT_PWD.getCode());
    uuidInfo.setExpireTime(Long.MIN_VALUE);
    uuidInfo.setCreatedUserId(userId);

    UuidInfo u1 = tokenService.addUuidInfoAndDisablePrevious(uuidInfo);
    String uuid = u1.getUuid();
    Assert.assertEquals(uuid, uuidInfo.getUuid());

    thrown.expect(ServiceStatusException.class);
    tokenService.getTemporaryTokenByUuid(uuid);
  }
}