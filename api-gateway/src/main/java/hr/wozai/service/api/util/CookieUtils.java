// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.util;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(CookieUtils.class);

  private static final String COOKIE_NAME_ACCESS_TOKEN = "X-Access-Token";
  private static final String COOKIE_NAME_REFRESH_TOKEN = "X-Refresh-Token";
  private static final String DOMAIN_NAME = "app.rencaiyi.com";

  public static void setTokenPairInResponseCookie(HttpServletResponse response, TokenPairDTO tokenPairDTO) {

    if (tokenPairDTO.getServiceStatusDTO().getCode() != ServiceStatus.COMMON_OK.getCode()) {
      throw new ServiceStatusException(ServiceStatus.getEnumByCode(tokenPairDTO.getServiceStatusDTO().getCode()));
    }

    if (isNullOrIncompleteTokenPairDTOAndResponse(response, tokenPairDTO)) {
      LOGGER.info("isNullOrIncompleteTokenPairDTOAndResponse exception");
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    Cookie accessTokenCookie = new Cookie(COOKIE_NAME_ACCESS_TOKEN, tokenPairDTO.getAccessToken());
    accessTokenCookie.setMaxAge(Integer.MAX_VALUE);
    accessTokenCookie.setDomain(DOMAIN_NAME);
    accessTokenCookie.setPath("/");
    response.addCookie(accessTokenCookie);

    Cookie refreshTokenCookie = new Cookie(COOKIE_NAME_REFRESH_TOKEN, tokenPairDTO.getRefreshToken());
    refreshTokenCookie.setMaxAge(Integer.MAX_VALUE);
    refreshTokenCookie.setDomain(DOMAIN_NAME);
    refreshTokenCookie.setPath("/");
    response.addCookie(refreshTokenCookie);
  }

  public static void setTokenPairInResponseCookie(
      HttpServletResponse response, String accessTokenString, String refreshTokenString) {

    if (null == response
        || StringUtils.isNullOrEmpty(accessTokenString)
        || StringUtils.isNullOrEmpty(refreshTokenString)) {
      return;
    }

    response.addCookie(new Cookie(COOKIE_NAME_ACCESS_TOKEN, accessTokenString));
    response.addCookie(new Cookie(COOKIE_NAME_REFRESH_TOKEN, refreshTokenString));
  }

  private static boolean isNullOrIncompleteTokenPairDTOAndResponse(HttpServletResponse response,
                                                                   TokenPairDTO tokenPairDTO) {
    return (null == response
            || null == tokenPairDTO
            || StringUtils.isNullOrEmpty(tokenPairDTO.getAccessToken())
            || StringUtils.isNullOrEmpty(tokenPairDTO.getRefreshToken()));
  }

}
