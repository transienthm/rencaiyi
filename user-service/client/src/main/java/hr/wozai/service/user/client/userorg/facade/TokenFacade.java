package hr.wozai.service.user.client.userorg.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;
import hr.wozai.service.user.client.userorg.dto.UuidInfoDTO;
import hr.wozai.service.user.client.userorg.dto.UuidInfoListDTO;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.StringDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

@ThriftService
public interface TokenFacade {

  /**
   * Get access-token and refresh-token pair given valid mobile phone & password
   *
   * @param orgId
   * @param allowedRememberMe
   * @param adminUserId
   * @return
   */
  @ThriftMethod
  TokenPairDTO getTokenPairByUserIdAndOrgId(long orgId, boolean allowedRememberMe, long actorUserId, long adminUserId);

  /**
   * Renew access token given un-expired access-token and valid refresh-token
   *
   * @param tokenPairDTO
   * @param actorUserId
   *@param adminUserId @return
   */
  @ThriftMethod
  StringDTO refreshAccessToken(TokenPairDTO tokenPairDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  BooleanDTO deleteAccessTokenWhenLogout(long orgId, String accessToken, long actorUserId, long adminUserId);

  @ThriftMethod
  UuidInfoDTO addUUIDInfo(UuidInfoDTO uuidInfoDTO, long actorUserId, long adminUserId);

  @ThriftMethod
  UuidInfoListDTO listUUIDInfosByUserIdAndUsage(long orgId, long userId, int uuidUsage, long expireTime,
                                                long actorUserId, long adminUserId);

  @ThriftMethod
  StringDTO getTemporaryTokenByUUID(String uuid);

  @ThriftMethod
  VoidDTO deleteAllUUIDAndTemporaryToken(long orgId, long userId, int uuidUsage, long actorUserId, long adminUserId);
}
