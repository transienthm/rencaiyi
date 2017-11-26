package hr.wozai.service.api.controller.auth;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.servicecommons.thrift.dto.StringDTO;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;

import hr.wozai.service.user.client.userorg.dto.TokenPairDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/1/26
 */
@Controller("tokenController")
public class TokenController {
  private static final Logger LOGGER = LoggerFactory.getLogger(TokenController.class);

  private static final String HEADER_ACCESS_TOKEN = "X-Access-Token";
  private static final String HEADER_REFRESH_TOKEN = "X-Refresh-Token";

  @Autowired
  FacadeFactory facadeFactory;


  @RequestMapping(value = "/auths/get_access_token", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getAccessToken(@RequestParam("userId") String encryptUserId,
                                       @RequestParam("orgId") String encryptOrgId,
                                       @RequestParam(value = "rememberMe", defaultValue = "0") int rememberMe,
                                       HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {

    long userId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptUserId));
    long orgId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptOrgId));
    boolean allowedRememberMe = (rememberMe == 1) ? true : false;

    LOGGER.info("getAccessToken()-request: userId=" + userId
            + ", orgId=" + orgId
            + ", rememberMe=" + rememberMe);

    Result<Object> result = new Result<>();

    TokenPairDTO tokenPairDTO = facadeFactory.getTokenFacade().getTokenPairByUserIdAndOrgId(orgId, allowedRememberMe, userId, 0L);
    // CookieUtils.setTokenPairInResponseCookie(response, tokenPairDTO);

    result.setData(tokenPairDTO);
    result.setCodeAndMsg(ServiceStatus.COMMON_OK);

    return result;
  }


  @RequestMapping(value = "/auths/refresh_access_token", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> refreshAccessToken(
          HttpServletRequest request, HttpServletResponse response) throws Exception {

    LOGGER.info("refreshAccessToken(): request=" + request);

    Result<Object> result = new Result<>();
    String accessTokenString = request.getHeader(HEADER_ACCESS_TOKEN);
    String refreshTokenString = request.getHeader(HEADER_REFRESH_TOKEN);
    if (StringUtils.isNullOrEmpty(accessTokenString)
            || StringUtils.isNullOrEmpty(refreshTokenString)) {
      throw new ServiceStatusException(ServiceStatus.AS_INVALID_TOKEN.getMsg());
    }

    TokenPairDTO tokenPairDTO = new TokenPairDTO();
    tokenPairDTO.setAccessToken(accessTokenString);
    tokenPairDTO.setRefreshToken(refreshTokenString);
    StringDTO newAccessToken = facadeFactory.getTokenFacade().refreshAccessToken(tokenPairDTO, -1L, -1L);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(newAccessToken.getServiceStatusDTO().getCode());
    if (serviceStatus != ServiceStatus.COMMON_OK) {
      throw new ServiceStatusException(serviceStatus);
    }

    String newAccessTokenString = newAccessToken.getData();
    /*if (null != newAccessTokenString) {
      CookieUtils.setTokenPairInResponseCookie(response, newAccessTokenString, refreshTokenString);
      JSONObject jsonObject = new JSONObject();
      jsonObject.put(HEADER_ACCESS_TOKEN, newAccessTokenString);
      jsonObject.put(HEADER_REFRESH_TOKEN, refreshTokenString);
      result.setData(jsonObject);
    }*/

    result.setData(newAccessTokenString);
    result.setCodeAndMsg(ServiceStatus.COMMON_OK);

    LOGGER.info("refreshAccessToken(): result=" + result);
    return result;
  }

}
