// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import hr.wozai.service.servicecommons.commons.consts.JWTConsts;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.*;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;
import hr.wozai.service.user.client.userorg.enums.UuidUsage;
import hr.wozai.service.user.server.constant.TimeConst;
import hr.wozai.service.user.server.dao.securitymodel.RoleDao;
import hr.wozai.service.user.server.dao.token.AccessTokenDao;
import hr.wozai.service.user.server.dao.token.TokenRedis;
import hr.wozai.service.user.server.dao.token.UuidInfoDao;
import hr.wozai.service.user.server.dao.securitymodel.UserRoleDao;
import hr.wozai.service.user.server.helper.TokenHelper;
import hr.wozai.service.user.server.helper.UuidInfoHelper;
import hr.wozai.service.user.server.model.securitymodel.Role;
import hr.wozai.service.user.server.model.token.AccessToken;
import hr.wozai.service.user.server.model.token.RefreshToken;
import hr.wozai.service.user.client.userorg.dto.TokenKeyDTO;
import hr.wozai.service.user.server.model.token.UuidInfo;
import hr.wozai.service.user.server.model.securitymodel.UserRole;
import hr.wozai.service.user.server.service.TokenService;
import hr.wozai.service.user.server.dao.userorg.OrgDao;
import hr.wozai.service.user.server.dao.userorg.OrgMemberDao;
import hr.wozai.service.user.server.dao.token.RefreshTokenDao;
import hr.wozai.service.user.server.dao.userorg.UserAccountDao;
import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("tokenService")
public class TokenServiceImpl implements TokenService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

  private static final String ISSUEER = "com.shanqian";
  private static final String EXTEND_LAST_LOGIN_STATUS_KEY = "last-login-status";
  private static final String EXTEND_LAST_LOGIN_STATUS_VALUE_SUCCESS = "success";
  private static final String EXTEND_LAST_LOGIN_STATUS_VALUE_FAIL = "fail";
  private static final long NOT_REMEMBER_ME_EXPIRE_PERIOD_IN_MILLIS = TimeConst.ONE_HOUR_IN_MILLIS;
  private static final long REMEMBER_ME_EXPIRE_PERIOD_IN_MILLIS = TimeConst.ONE_DAY_IN_MILLIS * 7;

  private JWSHeader jwsHeader;

  private RSAPrivateKey rsaPrivateKey;

  private RSAPublicKey accessTokenPublicKey;

  private JWSVerifier jwsVerifier;

  private JWSSigner signer;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  UserAccountDao userAccountDao;

  @Autowired
  AccessTokenDao accessTokenDao;

  @Autowired
  RefreshTokenDao refreshTokenDao;

  @Autowired
  OrgMemberDao orgMemberDao;

  @Autowired
  OrgDao orgDao;

  @Autowired
  TokenRedis tokenRedis;

  @Autowired
  UserRoleDao userRoleDao;

  @Autowired
  RoleDao roleDao;

  @Autowired
  UuidInfoDao uuidInfoDao;

  @PostConstruct
  public void init() {
    try {
      rsaPrivateKey = KeyUtils.loadRsaPrivateKey("keypair/AccessTokenPrivateRecipeDer");
      signer = new RSASSASigner(rsaPrivateKey);
      jwsHeader = new JWSHeader(JWSAlgorithm.RS256);
      accessTokenPublicKey = KeyUtils.loadRsaPublicKey("keypair/AccessTokenPublicRecipeDer");
      jwsVerifier = new RSASSAVerifier(accessTokenPublicKey);
    } catch (Exception e) {
      LOGGER.error("Fatal: fail to init key or signer");
      LOGGER.error(e.getMessage());
      throw new RuntimeException("Fatal: fail to init key or signer");
    }
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public TokenPairDTO addAccessTokenAndRefreshToken(long orgId, boolean allowedRememberMe, long actorUserId, long adminUserId) {
    TokenPairDTO tokenPairDTO = new TokenPairDTO();
    String accessTokenKey = TokenHelper.generateAccessTokenKey(orgId, adminUserId, actorUserId);
    String accessTokenValue = createAccessToken(orgId, adminUserId, actorUserId, allowedRememberMe);
    // insert into mysql
    AccessToken accessToken = new AccessToken();
    accessToken.setAccessTokenKey(accessTokenKey);
    accessToken.setAccessTokenValue(accessTokenValue);
    accessTokenDao.insertAccessToken(accessToken);

    String refreshTokenKey = TokenHelper.generateRefreshTokenKey(orgId, adminUserId, actorUserId);
    String refreshTokenValue = tokenRedis.getRefreshTokenByKey(refreshTokenKey);
    if (refreshTokenValue == null) {
      refreshTokenValue = createRefreshToken(actorUserId);
      RefreshToken refreshToken = new RefreshToken();
      refreshToken.setRefreshTokenKey(refreshTokenKey);
      refreshToken.setRefreshTokenValue(refreshTokenValue);
      refreshTokenDao.insertRefreshToken(refreshToken);
    }

    //insert into redis
    tokenRedis.addAccessToken(accessTokenKey, accessTokenValue);
    tokenRedis.addRefreshToken(refreshTokenKey, refreshTokenValue);

    tokenPairDTO.setAccessToken(accessTokenValue);
    tokenPairDTO.setRefreshToken(refreshTokenValue);


    return tokenPairDTO;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean deleteAccessToken(String accessTokenValue, long actorUserId) {
    if (!isTokenValidAndNotExpired(accessTokenValue)) {
      LOGGER.info("deleteAccessToken(): invalid accessTokenValue");
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
    }

    TokenKeyDTO tokenKeyDTO = getTokenKeyFromAccessToken(accessTokenValue);

    String accessTokenKey = tokenKeyDTO.generateAccessTokenKey();
    // delete from mysql
    accessTokenDao.deleteAccessTokenByKeyAndValue(accessTokenKey, accessTokenValue);
    // delete from redis
    tokenRedis.deleteOneAccessToken(accessTokenKey, accessTokenValue);

    return true;
  }

  private TokenKeyDTO getTokenKeyFromAccessToken(String accessTokenValue) {
    TokenKeyDTO result = new TokenKeyDTO();
    try {
      SignedJWT accessToken = SignedJWT.parse(accessTokenValue);
      ReadOnlyJWTClaimsSet accessTokenClaimSet = accessToken.getJWTClaimsSet();
      String encryptedUserId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_USERID);
      String encryptedAdminId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ADMIN_USERID);
      String encryptedOrgId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ORGID);
      long userId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedUserId));
      long adminId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedAdminId));
      long orgId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedOrgId));
      long lifetime = accessTokenClaimSet.getExpirationTime().getTime() - accessTokenClaimSet.getIssueTime().getTime();
      boolean allowedRememberedMe = (lifetime == REMEMBER_ME_EXPIRE_PERIOD_IN_MILLIS) ? true : false;
      result.setOrgId(orgId);
      result.setAdminUserId(adminId);
      result.setUserId(userId);
      result.setAllowedRememberedMe(allowedRememberedMe);
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }
    return result;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public String refreshAccessToken(String accessTokenValue, String refreshTokenValue) {
    if (!isTokenPairValidAndNotExpiredAndMatched(accessTokenValue, refreshTokenValue)) {
      LOGGER.info("deleteAccessToken(): invalid token pair");
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN,
              "isTokenPairValidAndNotExpiredAndMatched exception");
    }
    TokenKeyDTO tokenKeyDTO = getTokenKeyFromAccessToken(accessTokenValue);

    // check if refresh-token revoked
    String refreshTokenKey = tokenKeyDTO.generateRefreshTokenKey();
    String refreshTokenInRedis = tokenRedis.getRefreshTokenByKey(refreshTokenKey);
    if (null == refreshTokenInRedis
            || !refreshTokenInRedis.equals(refreshTokenValue)
            || isTokenExpired(refreshTokenInRedis)) {
      LOGGER.info("refreshAccessToken(): invalid refresh token");
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
    }

    String accessTokenKey = tokenKeyDTO.generateAccessTokenKey();
    accessTokenDao.deleteAccessTokenByKeyAndValue(accessTokenKey, accessTokenValue);
    tokenRedis.deleteOneAccessToken(accessTokenKey, accessTokenValue);

    String newAccessTokenValue = createAccessToken(tokenKeyDTO.getOrgId(), tokenKeyDTO.getAdminUserId(),
            tokenKeyDTO.getUserId(), tokenKeyDTO.isAllowedRememberedMe());

    // insert into mysql
    AccessToken accessToken = new AccessToken();
    accessToken.setAccessTokenKey(accessTokenKey);
    accessToken.setAccessTokenValue(newAccessTokenValue);
    accessTokenDao.insertAccessToken(accessToken);
    //insert to redis
    tokenRedis.addAccessToken(accessTokenKey, newAccessTokenValue);

    return newAccessTokenValue;

  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean deleteAllTokensByOrgIdAndUserId(long orgId, long userId) {

    Role admin = roleDao.findRoleByRoleName(orgId, DefaultRole.ORG_ADMIN.getName());
    List<UserRole> orgAdmin = userRoleDao.listOrgAdmin(orgId, admin.getRoleId());
    List<Long> adminUserIds = new ArrayList<>();
    for (UserRole userRole : orgAdmin) {
      adminUserIds.add(userRole.getUserId());
    }
    adminUserIds.add(0L);

    for (long adminUserId : adminUserIds) {
      deleteAllTokensByIds(orgId, userId, adminUserId);
    }
    return true;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean deleteAllTokensByIds(long orgId, long actorUserId, long adminUserId) {
    String accessTokenKey = TokenKeyDTO.generateAccessTokenKey(orgId, adminUserId, actorUserId);
    String refreshTokenKey = TokenKeyDTO.generateRefreshTokenKey(orgId, adminUserId, actorUserId);

    // delete all from mysql
    accessTokenDao.deleteByAccessTokenKey(accessTokenKey);
    refreshTokenDao.deleteByRefreshTokenKey(refreshTokenKey);
    // delete all from redis
    tokenRedis.deleteKey(accessTokenKey);
    tokenRedis.deleteKey(refreshTokenKey);

    return true;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public String addTemporaryToken(long orgId, long userId, int uuidUsage) {
    String tokenKey = TokenKeyDTO.generateTemporaryAccessTokenKey(orgId, userId, uuidUsage);
    String tokenValue = createTemporaryToken(orgId, userId, uuidUsage);

    tokenRedis.addTemporaryToken(tokenKey, tokenValue);
    return tokenValue;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean deleteTemporaryToken(long orgId, long userId, int uuidUsage) {
    String tokenKey = TokenKeyDTO.generateTemporaryAccessTokenKey(orgId, userId, uuidUsage);
    tokenRedis.deleteKey(tokenKey);

    return true;
  }

  /**
   * Steps:
   *  1) 删除该用户该 usage 下之前所有的 UUID & TemporaryToken
   *  2) 生成一个uuid,需要提供orgId,userId,uuidUsage,expireTime
   *
   * @param uuidInfo
   * @return id
   */
  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public UuidInfo addUuidInfoAndDisablePrevious(UuidInfo uuidInfo) {

    UuidInfoHelper.checkUuidInfoInsertParams(uuidInfo);

    // 1)
    long orgId = uuidInfo.getOrgId();
    long userId = uuidInfo.getUserId();
    int uuidUsage = uuidInfo.getUuidUsage();
    deleteUuidInfoByUserIdAndUsage(orgId, userId, uuidUsage);

    // 2)
    uuidInfoDao.insertUuidInfo(uuidInfo);

    return uuidInfo;
  }

  @Override
  @LogAround
  public List<UuidInfo> listUuidInfosByUserIdAndUsage(long orgId, long userId, int uuidUsage, long expireTime) {
    return uuidInfoDao.listUuidsByOrgIdAndUserIdAndUuidUsage(orgId, userId, uuidUsage, expireTime);
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public boolean deleteUuidInfoByUserIdAndUsage(long orgId, long userId, int uuidUsage) {
    uuidInfoDao.deleteUuidInfoByOrgIdAndUserIdAndUsage(orgId, userId, uuidUsage);

    String key = TokenKeyDTO.generateTemporaryAccessTokenKey(orgId, userId, uuidUsage);
    tokenRedis.deleteKey(key);

    return true;
  }

  @Override
  @LogAround
  @Transactional(value = "transactionManager", rollbackFor = Exception.class)
  public String getTemporaryTokenByUuid(String uuid) {
    UuidInfo uuidInfo = uuidInfoDao.getUuidInfoByUuid(uuid);
    if (uuidInfo == null) {
      throw new ServiceStatusException(ServiceStatus.UO_UUID_NOT_FOUND);
    }
    long now = System.currentTimeMillis();
    if (!IntegerUtils.equals(uuidInfo.getUuidUsage(),UuidUsage.ONBOARDING.getCode())
            && uuidInfo.getExpireTime() < now) {
      throw new ServiceStatusException(ServiceStatus.UO_UUID_EXPIRE);
    }

    long orgId = uuidInfo.getOrgId();
    long userId = uuidInfo.getUserId();
    int uuidUsage = uuidInfo.getUuidUsage();

    String temporaryToken = this.addTemporaryToken(orgId, userId, uuidUsage);
    return temporaryToken;
  }

  private boolean isTokenExpired(String tokenString) {

    if (StringUtils.isNullOrEmpty(tokenString)) {
      return false;
    }

    boolean isExpired = true;
    try {
      SignedJWT signedJWT = SignedJWT.parse(tokenString);
      long now = TimeUtils.getNowTimestmapInMillis();
      long tokenExpireTime = signedJWT.getJWTClaimsSet().getExpirationTime().getTime();
      if (now < tokenExpireTime) {
        isExpired = false;
      }
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
    }

    return isExpired;
  }

  private boolean isTokenValid(String tokenString) {

    if (StringUtils.isNullOrEmpty(tokenString)) {
      return false;
    }

    boolean isValid = false;
    try {
      SignedJWT signedJWT = SignedJWT.parse(tokenString);
      isValid = signedJWT.verify(jwsVerifier);
    } catch (Exception e) {
      LOGGER.error("Fail to verify token", e);
    }

    return isValid;
  }

  private boolean isTokenValidAndNotExpired(String tokenString) {

    return isTokenValid(tokenString) && !isTokenExpired(tokenString);
  }

  private boolean isTokenPairValidAndNotExpiredAndMatched(String accessTokenValue, String refreshTokenValue) {
    if (StringUtils.isNullOrEmpty(accessTokenValue)
            || StringUtils.isNullOrEmpty(refreshTokenValue)) {
      return false;
    }

    boolean isTokenPairValidAndNotExpiredAndMatched = true;

    try {
      if (!isTokenValid(accessTokenValue)
              || !isTokenValid(refreshTokenValue)) {

        isTokenPairValidAndNotExpiredAndMatched = false;
      } else {
        SignedJWT accessToken = SignedJWT.parse(accessTokenValue);
        SignedJWT refreshToken = SignedJWT.parse(refreshTokenValue);
        if (!StringUtils.isEqual(accessToken.getJWTClaimsSet().getSubject(),
                refreshToken.getJWTClaimsSet().getSubject())) {
          isTokenPairValidAndNotExpiredAndMatched = false;
        }
      }
    } catch (Exception e) {
      LOGGER.info("isTokenPairValidAndNotExpiredAndMatched(): fail", e);
      isTokenPairValidAndNotExpiredAndMatched = false;
    }

    return isTokenPairValidAndNotExpiredAndMatched;

  }

  private String createTemporaryToken(long orgId, long userId, int uuidUsage) {

    String temporaryToken = null;
    try {
      long now = TimeUtils.getNowTimestmapInMillis();
      // encode userId
      String userIdSecret = EncryptUtils.symmetricEncrypt(String.valueOf(userId));
      String orgIdSecret = EncryptUtils.symmetricEncrypt(String.valueOf(orgId));
      String usageSecret = EncryptUtils.symmetricEncrypt(String.valueOf(uuidUsage));
      JWTClaimsSet claimsSet = new JWTClaimsSet();
      claimsSet.setSubject(userIdSecret);
      claimsSet.setClaim(JWTConsts.CLAIM_NAME_USERID, userIdSecret);
      claimsSet.setClaim(JWTConsts.CLAIM_NAME_ORGID, orgIdSecret);
      claimsSet.setClaim(JWTConsts.CLAIM_NAME_UUIDUSAGE, usageSecret);
      claimsSet.setIssuer(ISSUEER);
      claimsSet.setIssueTime(new Date(now));
      claimsSet.setExpirationTime(new Date(now + REMEMBER_ME_EXPIRE_PERIOD_IN_MILLIS));
      claimsSet.setCustomClaim("type", "temporary-token");
      SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);

      signedJWT.sign(signer);
      temporaryToken = signedJWT.serialize();
      return temporaryToken;
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

  }

  private String createAccessToken(long orgId, long adminId, long userId,
                                   boolean allowedRememberMe) {

    if (userId <= 0) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    String accessToken = null;
    try {
      long now = TimeUtils.getNowTimestmapInMillis();
      // encode userId
      String userIdSecret = EncryptUtils.symmetricEncrypt(String.valueOf(userId));
      String orgIdSecret = EncryptUtils.symmetricEncrypt(String.valueOf(orgId));
      String adminIdSecret = EncryptUtils.symmetricEncrypt(String.valueOf(adminId));
      JWTClaimsSet claimsSet = new JWTClaimsSet();
      claimsSet.setSubject(userIdSecret);
      claimsSet.setClaim(JWTConsts.CLAIM_NAME_USERID, userIdSecret);
      claimsSet.setClaim(JWTConsts.CLAIM_NAME_ADMIN_USERID, adminIdSecret);
      claimsSet.setClaim(JWTConsts.CLAIM_NAME_ORGID, orgIdSecret);
      claimsSet.setIssuer(ISSUEER);
      claimsSet.setIssueTime(new Date(now));
      claimsSet.setExpirationTime(new Date(now + (allowedRememberMe ?
              REMEMBER_ME_EXPIRE_PERIOD_IN_MILLIS :
              NOT_REMEMBER_ME_EXPIRE_PERIOD_IN_MILLIS)));
      claimsSet.setCustomClaim("type", "access-token");
      SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);

      signedJWT.sign(signer);
      accessToken = signedJWT.serialize();
      return accessToken;
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

  }

  private String createRefreshToken(long userId) {

    if (userId <= 0) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    try {
      String refreshToken = null;
      long now = TimeUtils.getNowTimestmapInMillis();
      // encode userId
      String userIdSecret = EncryptUtils.symmetricEncrypt(String.valueOf(userId));
      JWTClaimsSet claimsSet = new JWTClaimsSet();
      claimsSet.setSubject(userIdSecret);
      claimsSet.setIssuer(ISSUEER);
      claimsSet.setIssueTime(new Date(now));
      claimsSet.setExpirationTime(new Date(Long.MAX_VALUE));
      claimsSet.setCustomClaim("type", "refresh-token");
      SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);

      signedJWT.sign(signer);
      refreshToken = signedJWT.serialize();
      return refreshToken;
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

  }

}
