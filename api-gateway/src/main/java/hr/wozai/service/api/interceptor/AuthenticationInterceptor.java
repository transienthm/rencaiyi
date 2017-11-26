// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import hr.wozai.service.servicecommons.commons.consts.JWTConsts;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.KeyUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.user.client.userorg.dto.TokenKeyDTO;
import hr.wozai.service.api.util.RedisUtil;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.PrintWriter;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/16
 */
public class AuthenticationInterceptor implements HandlerInterceptor {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationInterceptor.class);

  private static final String FILE_PATH_ENCRYPTION_PUBLIC_KEY = "keypair/AccessTokenPublicRecipeDer";
  private static final String HEADER_ACCESS_TOKEN = "X-Access-Token";
  private static final String HEADER_REFRESH_TOKEN = "X-Refresh-Token";
  private static final String HEADER_TEMPORARY_TOKEN = "X-Temporary-Token";

  private static final String HEADER_TOKEN_REFRESH_NEEDED = "X-Token-Refresh-Needed";
  private static final long REFRESH_THRESHOLD_AFTER_ISSUE_IN_MILLIS = 1000 * 10;

  public static ThreadLocal<Long> actorUserId = new ThreadLocal();
  public static ThreadLocal<Long> adminUserId = new ThreadLocal<>();
  public static ThreadLocal<Long> orgId = new ThreadLocal<>();

  public static ThreadLocal<Long> tempOrgId = new ThreadLocal<>();
  public static ThreadLocal<Long> tempUserId = new ThreadLocal<>();


  private static RSAPublicKey accessTokenPublicKey;

  private static JWSVerifier jwsVerifier;

  {
    try {
      accessTokenPublicKey = KeyUtils.loadRsaPublicKey(FILE_PATH_ENCRYPTION_PUBLIC_KEY);
      jwsVerifier = new RSASSAVerifier(accessTokenPublicKey);
    } catch (Exception e) {
      LOGGER.error("init(): Fatal! Fail to load AccessTokenPublicRecepeDer", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }
  }

  public AuthenticationInterceptor() {
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
          throws Exception {

    String accessTokenString = request.getHeader(HEADER_ACCESS_TOKEN);
    String refreshTokenString = request.getHeader(HEADER_REFRESH_TOKEN);
    String temporaryTokenString = request.getHeader(HEADER_TEMPORARY_TOKEN);

    try {
      boolean isValidTokenPair = isValidTokenPair(accessTokenString, refreshTokenString);
      boolean isValidTemporaryToken = isValidTemporaryToken(temporaryTokenString);


      if (!isValidTemporaryToken && !isValidTokenPair) {

        System.out.println("tempToken=" + temporaryTokenString);

        LOGGER.error("preHandle(): fail, INVALID_TOKEN");
        writeStatusCodeAndMessageInResponse(response, ServiceStatus.AS_INVALID_TOKEN);
        return false;
      }

      if (isValidTemporaryToken) {
        LOGGER.info("###### temp token is valid, tempToken={}", temporaryTokenString);
        SignedJWT temporaryToken = SignedJWT.parse(temporaryTokenString);
        ReadOnlyJWTClaimsSet tempTokenClaimSet = temporaryToken.getJWTClaimsSet();
        String encryptedTempOrgId = (String) tempTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ORGID);
        String encryptedTempUserId = (String) tempTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_USERID);
        long tempOrgId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedTempOrgId));
        long tempUserId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedTempUserId));
        AuthenticationInterceptor.tempOrgId.set(tempOrgId);
        AuthenticationInterceptor.tempUserId.set(tempUserId);
      }
      if (isValidTokenPair) {
        setResponseHeaderIfAccessTokenNeedRefresh(response, accessTokenString);
        SignedJWT accessToken = SignedJWT.parse(accessTokenString);
        ReadOnlyJWTClaimsSet accessTokenClaimSet = accessToken.getJWTClaimsSet();
        String encryptedUserId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_USERID);
        String encryptedAdminUserId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ADMIN_USERID);
        String encryptedOrgId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ORGID);
        long userId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedUserId));
        long orgId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedOrgId));
        long adminUserId = 0L;
        if (null != encryptedAdminUserId) {
          adminUserId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedAdminUserId));
        }
        AuthenticationInterceptor.actorUserId.set(userId);
        AuthenticationInterceptor.adminUserId.set(adminUserId);
        AuthenticationInterceptor.orgId.set(orgId);
      }
    } catch (Exception e) {
      LOGGER.error("preHandle(): fail", e);
      if (!(e instanceof ServiceStatusException)) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      throw e;
    }

    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                         ModelAndView modelAndView) throws Exception {
  }

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception ex) throws Exception {
    actorUserId.set(0L);
    adminUserId.set(0L);
    orgId.set(0L);

    tempUserId.set(0L);
    tempOrgId.set(0L);
  }

  public static long getOrgIdFromTokenPair(String accessTokenString, String refreshTokenString) {
    long orgId = 0L;
    try {
      boolean isValidTokenPair = isValidTokenPair(accessTokenString, refreshTokenString);

      if (!isValidTokenPair) {
        LOGGER.error("preHandle(): fail, INVALID_TOKEN");
        throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
      }

      SignedJWT accessToken = SignedJWT.parse(accessTokenString);
      ReadOnlyJWTClaimsSet accessTokenClaimSet = accessToken.getJWTClaimsSet();
//        String encryptedUserId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_USERID);
//        String encryptedAdminUserId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ADMIN_USERID);
      String encryptedOrgId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ORGID);
      orgId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedOrgId));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
    }
    return orgId;
  }

  public static long getActorUserIdFromTokenPair(String accessTokenString, String refreshTokenString) {
    long actorUserId;
    try {
      boolean isValidTokenPair = isValidTokenPair(accessTokenString, refreshTokenString);

      if (!isValidTokenPair) {
        LOGGER.error("preHandle(): fail, INVALID_TOKEN");
        throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
      }

      SignedJWT accessToken = SignedJWT.parse(accessTokenString);
      ReadOnlyJWTClaimsSet accessTokenClaimSet = accessToken.getJWTClaimsSet();
      String encryptedUserId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_USERID);
//        String encryptedAdminUserId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ADMIN_USERID);
//      String encryptedOrgId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ORGID);
      actorUserId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedUserId));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
    }
    return actorUserId;
  }

  public static long getAdminUserIdFromTokenPair(String accessTokenString, String refreshTokenString) {
    long adminUserId;
    try {
      boolean isValidTokenPair = isValidTokenPair(accessTokenString, refreshTokenString);

      if (!isValidTokenPair) {
        LOGGER.error("preHandle(): fail, INVALID_TOKEN");
        throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
      }

      SignedJWT accessToken = SignedJWT.parse(accessTokenString);
      ReadOnlyJWTClaimsSet accessTokenClaimSet = accessToken.getJWTClaimsSet();
//      String encryptedUserId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_USERID);
      String encryptedAdminUserId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ADMIN_USERID);
      String encryptedOrgId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ORGID);
      adminUserId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedAdminUserId));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
    }
    return adminUserId;
  }

  public static boolean isValidTokenPair(String accessTokenString, String refreshTokenString) {

    if (StringUtils.isNullOrEmpty(accessTokenString)
            || StringUtils.isNullOrEmpty(refreshTokenString)) {
      return false;
    }

    // validate if token in redis
    TokenKeyDTO tokenKey = getTokenKeyFromAccessToken(accessTokenString);
    LOGGER.info("Check TokenPair when accept request, orgId:{}, adminUserId:{}, userId:{}",
            tokenKey.getOrgId(), tokenKey.getAdminUserId(), tokenKey.getUserId());

    long time1 = System.currentTimeMillis();
    if (!RedisUtil.sismember(tokenKey.generateAccessTokenKey(), accessTokenString)) {
      LOGGER.info("token not in redis:{}", accessTokenString);
      return false;
    }
    long time2 = System.currentTimeMillis();
    System.out.println("redis cost:" + (time2 - time1));

    SignedJWT accessToken = null;
    SignedJWT refreshToken = null;

    try {
      accessToken = SignedJWT.parse(accessTokenString);
      refreshToken = SignedJWT.parse(refreshTokenString);
      // validate the signature
      if (!accessToken.verify(jwsVerifier)
              || !refreshToken.verify(jwsVerifier)) {
        LOGGER.error("verify jwsVerifier fail");
        return false;
      }
      ReadOnlyJWTClaimsSet accessTokenClaimSet = accessToken.getJWTClaimsSet();
      ReadOnlyJWTClaimsSet refreshTokenClaimSet = refreshToken.getJWTClaimsSet();

      // validate the subjects
      if (null == accessTokenClaimSet.getSubject()
              || !accessTokenClaimSet.getSubject().toUpperCase()
              .equals(refreshTokenClaimSet.getSubject().toUpperCase())) {
        LOGGER.error("verify subject fail:{},{}",accessTokenClaimSet.getSubject(), refreshTokenClaimSet.getSubject());
        return false;
      }
      // validate the expire time
      long currentTimestamp = TimeUtils.getNowTimestmapInMillis();
      if (null == accessTokenClaimSet.getExpirationTime()
              || null == refreshTokenClaimSet.getExpirationTime()
              || currentTimestamp >= accessTokenClaimSet.getExpirationTime().getTime()) {
        LOGGER.error("verify expire time error:{}" + accessTokenClaimSet.getExpirationTime());
        return false;
      }
    } catch (Exception e) {
      LOGGER.info("Fail to verify accessTokenString: accessTokenString=" + accessTokenString);
      return false;
    }

    return true;
  }

  public static boolean isValidTemporaryToken(String temporaryTokenString) {

    if (StringUtils.isNullOrEmpty(temporaryTokenString)) {
      return false;
    }

    // validate if token in redis
    TokenKeyDTO tokenKey = getTokenKeyFromTemporaryToken(temporaryTokenString);
    LOGGER.info("Check temporary token when accept request, orgId:{}, userId:{}, uuidUsage:{}",
            tokenKey.getOrgId(), tokenKey.getUserId(), tokenKey.getUuidUsage());

    long time1 = System.currentTimeMillis();
    if (!RedisUtil.sismember(tokenKey.generateTemporaryAccessTokenKey(), temporaryTokenString)) {
      LOGGER.info("token not in redis:{}", temporaryTokenString);
      return false;
    }
    long time2 = System.currentTimeMillis();
    System.out.println("redis cost:" + (time2 - time1));

    SignedJWT temporaryToken = null;

    try {
      temporaryToken = SignedJWT.parse(temporaryTokenString);

      // validate the signature
      if (!temporaryToken.verify(jwsVerifier)) {
        return false;
      }
      ReadOnlyJWTClaimsSet temporaryTokenClaimSet = temporaryToken.getJWTClaimsSet();

      // validate the expire time
      long currentTimestamp = TimeUtils.getNowTimestmapInMillis();
      if (null == temporaryTokenClaimSet.getExpirationTime()
              || currentTimestamp >= temporaryTokenClaimSet.getExpirationTime().getTime()) {
        return false;
      }
    } catch (Exception e) {
      LOGGER.info("Fail to verify temporaryTokenString: temporaryTokenString=" + temporaryTokenString);
      return false;
    }

    return true;
  }

  public static long getUserIdFromTokenPair(String accessTokenString, String refreshTokenString) throws Exception {

    boolean isValidTokenPair = isValidTokenPair(accessTokenString, refreshTokenString);

    if (!isValidTokenPair) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
    }

    String encryptedUserId = getUserIdSecretFromAccessToken(accessTokenString);
    long authedUserId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedUserId));

    LOGGER.info("getUserIdFromTokenPair(): userId=" + authedUserId);
    return authedUserId;
  }

  private static String getUserIdSecretFromAccessToken(String accessTokenString) {

    String userIdSecret = null;

    try {
      SignedJWT signedJWT = SignedJWT.parse(accessTokenString);
      ReadOnlyJWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
      userIdSecret = claimsSet.getSubject();
    } catch (Exception e) {
      LOGGER.error("getUserIdSecretFromAccessToken(): fail to get, accessTokenString=" + accessTokenString);
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
    }

    return userIdSecret;
  }

  private void writeStatusCodeAndMessageInResponse(HttpServletResponse response, ServiceStatus serviceStatus) {

    if (null == response) {
      return;
    }

    if (null == serviceStatus) {
      serviceStatus = ServiceStatus.COMMON_BAD_REQUEST;
    }

    JSONObject responseBody = new JSONObject();
    responseBody.put("code", serviceStatus.getCode());
    responseBody.put("msg", serviceStatus.getMsg());
    PrintWriter pw = null;
    try {
      pw = response.getWriter();
      pw.print(responseBody);
      pw.flush();
    } catch (Exception e) {
      LOGGER.info("writeStatusCodeAndMessageInResponse(): fail to write response");
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    } finally {
      if (pw != null) {
        pw.close();
      }
    }
  }

  public static TokenKeyDTO getTokenKeyFromAccessToken(String accessTokenValue) {
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
      result.setOrgId(orgId);
      result.setAdminUserId(adminId);
      result.setUserId(userId);
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
    }
    return result;
  }

  public static TokenKeyDTO getTokenKeyFromTemporaryToken(String temporaryTokenValue) {
    TokenKeyDTO result = new TokenKeyDTO();
    try {
      SignedJWT temporaryToken = SignedJWT.parse(temporaryTokenValue);
      ReadOnlyJWTClaimsSet accessTokenClaimSet = temporaryToken.getJWTClaimsSet();
      String encryptedOrgId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_ORGID);
      String encryptedUserId = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_USERID);
      String encryptedUuidUsage = (String) accessTokenClaimSet.getClaim(JWTConsts.CLAIM_NAME_UUIDUSAGE);
      long orgId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedOrgId));
      long userId = Long.valueOf(EncryptUtils.symmetricDecrypt(encryptedUserId));
      int uuidUsage = Integer.valueOf(EncryptUtils.symmetricDecrypt(encryptedUuidUsage));
      result.setOrgId(orgId);
      result.setUserId(userId);
      result.setUuidUsage(uuidUsage);
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN);
    }
    return result;
  }

  private static void setResponseHeaderIfAccessTokenNeedRefresh(
          HttpServletResponse response, String accessTokenString) throws ParseException {

    if (null == response
            || StringUtils.isNullOrEmpty(accessTokenString)) {
      return;
    }

    SignedJWT accessToken = SignedJWT.parse(accessTokenString);
    ReadOnlyJWTClaimsSet accessTokenClaimSet = accessToken.getJWTClaimsSet();
    long issueTimestamp = accessTokenClaimSet.getIssueTime().getTime();
    long expireTimestamp = accessTokenClaimSet.getExpirationTime().getTime();
    long currentTimestamp = TimeUtils.getNowTimestmapInMillis();

    if (2 * currentTimestamp > expireTimestamp - issueTimestamp) {
      response.setHeader(HEADER_TOKEN_REFRESH_NEEDED, "true");
    } else {
      response.setHeader(HEADER_TOKEN_REFRESH_NEEDED, "false");
    }


  }

  public static void main(String[] args) {
    String token = "eyJhbGciOiJSUzI1NiJ9.eyJhZG1pblVzZXJJZCI6IjJiMWU2ZDAyNWFjMjg4NjgiLCJzdWIiOiI5MDk4ZDRmMzAwZDcxZGQ3IiwiaXNzIjoiY29tLnNoYW5xaWFuIiwidHlwZSI6ImFjY2Vzcy10b2tlbiIsImV4cCI6MTQ1OTkzMzczMSwidXNlcklkIjoiOTA5OGQ0ZjMwMGQ3MWRkNyIsImlhdCI6MTQ1OTMyODkzMSwib3JnSWQiOiJiYjcwM2Q3NWE2NjIyZGVhIn0.qozb2EBaV-eLS_IDa1jGCRzMKdVjkjYKYQFgtQERXSSU7DO2ERq2IfAoZ9LBVZHlVQvVfIBIYVzQG2G3SX0jNSNSXKXIuM8DRp6qkRABsFPgVv4m_i5nnc_h0vJo1ihY6RmDZP_kwyO3_Y8ACqbU8W5Q4CM_D7HVv_M8yVkMu264WlwKhgfOCsBQRCHBwwNhvevJi6DhFi9cW2dnEl-ochr3LHEL1YS_qsWDVYr77y-wDuZtuIBBJYhr3uDXUs9ZuuDfZZuSvNYWsH9E2Nkcpc2Di8R5W0ddU5oSDW4uTg6NHXQX6wk0qX-j6Y0Xk0q6x1T-krUcA81saG8-2Qmhuw";
    AuthenticationInterceptor.getTokenKeyFromAccessToken(token);

    String tempToken = "eyJhbGciOiJSUzI1NiJ9.eyJ1dWlkVXNhZ2UiOiI1NmIxNjNkMGI1MmY5NGQwIiwic3ViIjoiMDI1ZjJhMTlkMGJkNWZjNyIsImlzcyI6ImNvbS5zaGFucWlhbiIsInR5cGUiOiJ0ZW1wb3JhcnktdG9rZW4iLCJleHAiOjE0NjEzMjEwOTUsInVzZXJJZCI6IjAyNWYyYTE5ZDBiZDVmYzciLCJpYXQiOjE0NjA3MTYyOTUsIm9yZ0lkIjoiMDI1ZjJhMTlkMGJkNWZjNyJ9.YIB8HmVzFD4e_bWxFw9ZIeV_Ff6umgUdnpFnROVZkyh1PF_8PMeD435cSWRZBkHY3aGkdwypIVTTiSGfn0vYbg0AyfwBhZ0cENrT6aKOrB-Sk9DknqPQc-jAXO-aIboCEAy0i2v4AlT5XFZisJvt2lyztM03tSv6VM4gFtwDjXiuxx0t6KtHVZ3MqVmRZkRvoURzLcOkwOuBlwGlOH1w8y2hhFgoXYkmHSyrj1zl8zcrB7yNhTPFelxtGLFjgrnxq8mpxKhjsTgffzYEJSwkiKU0s34IFM7ZcmKcun9wO8oGAsLoewDycs_bOYUd8DeKuVpkI1-krF8xGkYdcdBe9g";
    System.out.println(AuthenticationInterceptor.getTokenKeyFromTemporaryToken(tempToken));

    ThreadLocal<Long> tempOrgId = new ThreadLocal<>();
    System.out.println(tempOrgId.get() == null);
  }
}
