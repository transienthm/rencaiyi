// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.user.server.helper;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import hr.wozai.service.servicecommons.commons.consts.JWTConsts;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.KeyUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

@Service("tokenHelper")
public class TokenHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(TokenHelper.class);

  private static final String ACCESS_TOKEN_KEYNAMA = "accesstoken:";
  private static final String REFRESH_TOKEN_KEYNAMA = "refreshtoken:";
  private static final String SEPERATOR = "@#";

  public static String generateAccessTokenKey(long orgId, long adminId, long actorUserId) {
    return ACCESS_TOKEN_KEYNAMA + SEPERATOR + orgId + SEPERATOR + adminId + SEPERATOR + actorUserId;
  }
  
  public static String generateRefreshTokenKey(long orgId, long adminId, long actorUserId) {
    return REFRESH_TOKEN_KEYNAMA + SEPERATOR + orgId + SEPERATOR + adminId + SEPERATOR + actorUserId;
  }
}
