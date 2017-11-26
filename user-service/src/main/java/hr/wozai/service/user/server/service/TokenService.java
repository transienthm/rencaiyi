package hr.wozai.service.user.server.service;

import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;
import hr.wozai.service.user.server.model.token.UuidInfo;

import java.util.List;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/21
 */
public interface TokenService {
  /**
   * 登陆或者view as时调用
   * @param orgId
   * @param allowedRememberMe
   * @param actorUserId
   * @return
   */
  TokenPairDTO addAccessTokenAndRefreshToken(long orgId, boolean allowedRememberMe, long actorUserId, long adminUserId);

  /**
   * token已过期,logout时调用
   * @param accessTokenValue
   * @param actorUserId
   * @return
   */
  boolean deleteAccessToken(String accessTokenValue, long actorUserId);

  /**
   * token快过期时调用该方法
   * @param accessTokenValue
   * @param refreshTokenValue
   * @return
   */
  String refreshAccessToken(String accessTokenValue, String refreshTokenValue);

  /**
   * 修改或充值密码时调用该方法
   *
   * @param orgId
   * @param userId
   * @return
   */
  boolean deleteAllTokensByOrgIdAndUserId(long orgId, long userId);

  /**
   * 取消某个人的admin权限时调用
   * @param orgId
   * @param adminUserId
   * @return
   */
  boolean deleteAllTokensByIds(long orgId, long actorUserId, long adminUserId);

  /**
   * 产生一个临时token
   * @param orgId
   * @param userId
   * @param uuidUsage
   * @return
   */
  String addTemporaryToken(long orgId, long userId, int uuidUsage);

  /**
   * 删除一系列token
   * @param orgId
   * @param userId
   * @param uuidUsage
   * @return
   */
  boolean deleteTemporaryToken(long orgId, long userId, int uuidUsage);

  /**
   * Steps:
   *  1) 删除该用户该 usage 下之前所有的 UUID & TemporaryToken
   *  2) 生成一个uuid,需要提供orgId,userId,uuidUsage,expireTime
   * 
   * @param uuidInfo
   * @return id
   */
  UuidInfo addUuidInfoAndDisablePrevious(UuidInfo uuidInfo);

  List<UuidInfo> listUuidInfosByUserIdAndUsage(long orgId, long userId, int uuidUsage, long expireTime);

  /**
   * 删除uuid,同时要删除对应的temporary token
   * 1. 查询所有对应的uuid并删除
   * 2. 删除所有的temporary token
   * @param orgId
   * @param userId
   * @param uuidUsage
   * @return
   */
  boolean deleteUuidInfoByUserIdAndUsage(long orgId, long userId, int uuidUsage);

  /**
   * 根据uuid获取一个token
   * 1. 检查uuid是否存在,是否过期
   * 2. 生成temporary token
   * 3. 返回token
   * @param uuid
   * @return
   */
  String getTemporaryTokenByUuid(String uuid);


}
