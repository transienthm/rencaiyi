package hr.wozai.service.user.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;
import hr.wozai.service.user.client.userorg.dto.UuidInfoDTO;
import hr.wozai.service.user.client.userorg.dto.UuidInfoListDTO;
import hr.wozai.service.user.client.userorg.enums.UuidUsage;
import hr.wozai.service.user.client.userorg.facade.TokenFacade;
import hr.wozai.service.user.server.test.base.TestBase;
import hr.wozai.service.servicecommons.thrift.dto.StringDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/4/25
 */
public class TokenFacadeImplTest extends TestBase {
  @Autowired
  TokenFacade tokenFacade;

  private long orgId = 10L;
  private long userId = 10L;
  private int uuidUsage = UuidUsage.ONBOARDING.getCode();

  @Test
  public void testUUIDInfo() throws Exception {
    UuidInfoDTO uuidInfoDTO = new UuidInfoDTO();
    uuidInfoDTO.setOrgId(orgId);
    uuidInfoDTO.setUserId(userId);
    uuidInfoDTO.setUuidUsage(uuidUsage);
    uuidInfoDTO.setExpireTime(Long.MAX_VALUE);
    uuidInfoDTO.setCreatedUserId(userId);

    UuidInfoDTO result = tokenFacade.addUUIDInfo(uuidInfoDTO, 0L, 0L);
    Assert.assertNotNull(result.getUuid());

    UuidInfoListDTO uuidInfoListDTO = tokenFacade.listUUIDInfosByUserIdAndUsage(orgId, userId,
            uuidUsage, Long.MIN_VALUE, 0L, 0L);
    Assert.assertEquals(1, uuidInfoListDTO.getUuidInfoDTOList().size());

    StringDTO token = tokenFacade.getTemporaryTokenByUUID(result.getUuid());
    Assert.assertNotNull(token.getData());

    VoidDTO voidDTO = tokenFacade.deleteAllUUIDAndTemporaryToken(orgId, userId, uuidUsage, -1L, -1L);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), voidDTO.getServiceStatusDTO().getCode());
  }

  @Test
  public void testTokenPair() throws Exception {
    TokenPairDTO tokenPairDTO = tokenFacade.getTokenPairByUserIdAndOrgId(orgId, true, userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), tokenPairDTO.getServiceStatusDTO().getCode());

    Assert.assertNotNull(tokenPairDTO.getAccessToken());
    Assert.assertNotNull(tokenPairDTO.getRefreshToken());

    StringDTO stringDTO = tokenFacade.refreshAccessToken(tokenPairDTO, userId, userId);

    BooleanDTO booleanDTO = tokenFacade.deleteAccessTokenWhenLogout(
            orgId, tokenPairDTO.getAccessToken(), userId, userId);
    Assert.assertEquals(ServiceStatus.COMMON_OK.getCode(), booleanDTO.getServiceStatusDTO().getCode());
  }

  @Test
  public void testGetTemporaryTokenByUUID() throws Exception {

  }
}