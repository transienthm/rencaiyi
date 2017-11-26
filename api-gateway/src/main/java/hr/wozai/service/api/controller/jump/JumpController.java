// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.controller.jump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hr.wozai.service.api.component.FeZkClient;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.util.ParamName;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.StringDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.dto.RoleDTO;
import hr.wozai.service.user.client.userorg.dto.RoleListDTO;
import hr.wozai.service.user.client.userorg.enums.DefaultRole;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/16
 */
@Controller("jumpController")
public class JumpController {

  private static final Logger LOGGER = LoggerFactory.getLogger(JumpController.class);

  private static final String PARAM_SIGNATURE = "signature";
  private static final String COOKIE_NAME_ACCESS_TOKEN = "X-Access-Token";
  private static final String COOKIE_NAME_REFRESH_TOKEN = "X-Refresh-Token";
  private static final String LOCATION_HEADER_NAME = "Location";
  private static final String LOCATION_HEADER_VALUE = "/u";
  private static final String LOCATION_ADMIN_HEADER_VALUE = "/admin";
  private static final String LOCATION_HOMEPAGE_HEADER_VALUE = "/";

  private static final String PAGE_AUTH = "auth";
  private static final String PAGE_APP = "app";
  private static final String PAGE_ADMIN = "admin";
  private static final String PAGE_ONBOARDING = "onboarding";
  private static final String PAGE_CHARTS = "charts";

  @Autowired
  FeZkClient feZkClient;

  @Autowired
  FacadeFactory facadeFactory;

  @Value("${url.host}")
  private String host;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  @ResponseBody
  public Object jumpAfterRootPath(
          HttpServletRequest request,
          HttpServletResponse response
  ) {

    LOGGER.info("jumpAfterRootPath()-request: reqeust=" + request.toString());

    Object result = null;
    String accessTokenString = null;
    String refreshTokenString = null;
    long userId = 0;
    try {
      Cookie[] cookies = request.getCookies();
      for (Cookie cookie : cookies) {
        if (COOKIE_NAME_ACCESS_TOKEN.equals(cookie.getName())) {
          accessTokenString = cookie.getValue();
        } else if (COOKIE_NAME_REFRESH_TOKEN.equals(cookie.getName())) {
          refreshTokenString = cookie.getValue();
        }
      }
      userId = AuthenticationInterceptor.getUserIdFromTokenPair(accessTokenString, refreshTokenString);
    } catch (Exception e) {
      LOGGER.error("jumpAfterRootPath()-fail: request=" + request.toString(), e);
    }

    if (userId > 0) {
      long orgId = AuthenticationInterceptor.getOrgIdFromTokenPair(accessTokenString, refreshTokenString);
      RoleListDTO remoteResult = facadeFactory.getSecurityModelFacade().getRoleListDTOByUserId(orgId, userId, -1L, -1L);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        throw new ServiceStatusException(serviceStatus);
      }
      if (isSuperAdminOnly(remoteResult.getRoleDTOList())) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(LOCATION_HEADER_NAME, LOCATION_ADMIN_HEADER_VALUE);
        result = new ResponseEntity<String>(null, headers, HttpStatus.FOUND);
      } else {
        String appJsVersion = feZkClient.getAppJsVersion();
        String appCssVersion = feZkClient.getAppCssVersion();
        result = getPage(PAGE_APP, appJsVersion, appCssVersion, false, null, null);
      }
    } else {
      HttpHeaders headers = new HttpHeaders();
      headers.add(LOCATION_HEADER_NAME, LOCATION_HEADER_VALUE);
      result = new ResponseEntity<String>(null, headers, HttpStatus.FOUND);
    }

    LOGGER.info("jump-userId:{}, result:{}", userId, result);
    return result;
  }

  private boolean isOrgAdmin(List<RoleDTO> roleDTOList) {
    for (RoleDTO roleDTO : roleDTOList) {
      if (DefaultRole.SUPER_ADMIN.getName().equals(roleDTO.getRoleName())) {
        return true;
      }
    }
    return false;
  }

  private boolean isSuperAdminOnly(List<RoleDTO> roleDTOList) {
    boolean isSuperAdmin = false;
    boolean isOtherRole = false;
    for (RoleDTO roleDTO : roleDTOList) {
      if (DefaultRole.SUPER_ADMIN.getName().equals(roleDTO.getRoleName())) {
        isSuperAdmin = true;
      } else {
        isOtherRole = true;
      }
    }
    return isSuperAdmin && !isOtherRole;
  }

  @RequestMapping(value = {"/admin", "/admin/"}, method = RequestMethod.GET, produces = "text/html")
  @ResponseBody
  public Object jumpForAdmin(
          HttpServletRequest request,
          HttpServletResponse response) throws Exception {

    LOGGER.info("jumpAfterAdminPath()-request: reqeust=" + request.toString());

    Object result = null;
    String accessTokenString = null;
    String refreshTokenString = null;
    long userId = 0;
    try {
      Cookie[] cookies = request.getCookies();
      for (Cookie cookie : cookies) {
        if (COOKIE_NAME_ACCESS_TOKEN.equals(cookie.getName())) {
          accessTokenString = cookie.getValue();
        } else if (COOKIE_NAME_REFRESH_TOKEN.equals(cookie.getName())) {
          refreshTokenString = cookie.getValue();
        }
      }
      userId = AuthenticationInterceptor.getUserIdFromTokenPair(accessTokenString, refreshTokenString);
    } catch (Exception e) {
      LOGGER.error("jumpAfterAdminPath()-fail: request=" + request.toString(), e);
    }

    if (userId > 0) {
      long orgId = AuthenticationInterceptor.getOrgIdFromTokenPair(accessTokenString, refreshTokenString);
      RoleListDTO remoteResult = facadeFactory.getSecurityModelFacade().getRoleListDTOByUserId(orgId, userId, -1L, -1L);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        throw new ServiceStatusException(serviceStatus);
      }
      if (isOrgAdmin(remoteResult.getRoleDTOList())) {
        String adminJsVersion = feZkClient.getAdminJsVersion();
        String adminCssVersion = feZkClient.getAdminCssVersion();

        result = getPage(PAGE_ADMIN, adminJsVersion, adminCssVersion, false, null, null);
      } else {
        result = getErrorAdminPageWithoutParams();
      }
    } else {
      HttpHeaders headers = new HttpHeaders();
      headers.add(LOCATION_HEADER_NAME, LOCATION_HEADER_VALUE);
      result = new ResponseEntity<String>(null, headers, HttpStatus.FOUND);
    }

    LOGGER.info("jump-userId:{}, result:{}", userId, result);
    return result;
  }

  @RequestMapping(value = {"/charts", "/charts/"}, method = RequestMethod.GET, produces = "text/html")
  @ResponseBody
  public Object jumpForCharts(
          HttpServletRequest request,
          HttpServletResponse response) throws Exception {

    LOGGER.info("jumpAfterChartsPath()-request: reqeust=" + request.toString());

    Object result = null;
    String accessTokenString = null;
    String refreshTokenString = null;
    long userId = 0;
    try {
      Cookie[] cookies = request.getCookies();
      for (Cookie cookie : cookies) {
        if (COOKIE_NAME_ACCESS_TOKEN.equals(cookie.getName())) {
          accessTokenString = cookie.getValue();
        } else if (COOKIE_NAME_REFRESH_TOKEN.equals(cookie.getName())) {
          refreshTokenString = cookie.getValue();
        }
      }
      userId = AuthenticationInterceptor.getUserIdFromTokenPair(accessTokenString, refreshTokenString);
    } catch (Exception e) {
      LOGGER.error("jumpAfterChartsPath()-fail: request=" + request.toString(), e);
    }

    if (userId > 0) {
      long orgId = AuthenticationInterceptor.getOrgIdFromTokenPair(accessTokenString, refreshTokenString);
      RoleListDTO remoteResult = facadeFactory.getSecurityModelFacade().getRoleListDTOByUserId(orgId, userId, -1L, -1L);
      ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
      if (serviceStatus != ServiceStatus.COMMON_OK) {
        throw new ServiceStatusException(serviceStatus);
      }
        if (isSuperAdminOnly(remoteResult.getRoleDTOList())) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(LOCATION_HEADER_NAME, LOCATION_ADMIN_HEADER_VALUE);
            result = new ResponseEntity<String>(null, headers, HttpStatus.FOUND);
        } else {
            String chartsJsVersion = feZkClient.getChartsJsVersion();
            String chartsCssVersion = feZkClient.getchartsCssVersion();
            result = getPage(PAGE_CHARTS, chartsJsVersion, chartsCssVersion, false, null, null);
        }
    } else {
      HttpHeaders headers = new HttpHeaders();
      headers.add(LOCATION_HEADER_NAME, LOCATION_HEADER_VALUE);
      result = new ResponseEntity<String>(null, headers, HttpStatus.FOUND);
    }

    LOGGER.info("jump-userId:{}, result:{}", userId, result);
    return result;
  }

  @RequestMapping(value = {"/u", "/u/"}, method = RequestMethod.GET, produces = "text/html")
  @ResponseBody
  public Object jumpForAuth(
          HttpServletRequest request,
          @RequestParam(value = "uuid", defaultValue = "") String uuid
  ) throws Exception {

    LOGGER.info("jumpForAuth()-request");

    String accessTokenString = null;
    String refreshTokenString = null;
    long userId = 0;
    try {
      Cookie[] cookies = request.getCookies();
      for (Cookie cookie : cookies) {
        if (COOKIE_NAME_ACCESS_TOKEN.equals(cookie.getName())) {
          accessTokenString = cookie.getValue();
        } else if (COOKIE_NAME_REFRESH_TOKEN.equals(cookie.getName())) {
          refreshTokenString = cookie.getValue();
        }
      }
      userId = AuthenticationInterceptor.getUserIdFromTokenPair(accessTokenString, refreshTokenString);
    } catch (Exception e) {
      LOGGER.error("jumpForAuth()-fail: request=" + request.toString(), e);
    }

    Object result = null;
    try {
      if (uuid.isEmpty()) {
        if (userId > 0) {
          long orgId = AuthenticationInterceptor.getOrgIdFromTokenPair(accessTokenString, refreshTokenString);
          RoleListDTO remoteResult = facadeFactory.getSecurityModelFacade().getRoleListDTOByUserId(orgId, userId, -1L, -1L);
          ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
          if (serviceStatus != ServiceStatus.COMMON_OK) {
            throw new ServiceStatusException(serviceStatus);
          }
          if (isSuperAdminOnly(remoteResult.getRoleDTOList())) {
            HttpHeaders headers = new HttpHeaders();
            headers.add(LOCATION_HEADER_NAME, LOCATION_ADMIN_HEADER_VALUE);
            result = new ResponseEntity<String>(null, headers, HttpStatus.FOUND);
          } else {
            /*String appJsVersion = feZkClient.getAppJsVersion();
            result = getPage(PAGE_APP, appJsVersion, false, null, null);*/
            HttpHeaders headers = new HttpHeaders();
            headers.add(LOCATION_HEADER_NAME, LOCATION_HOMEPAGE_HEADER_VALUE);
            result = new ResponseEntity<String>(null, headers, HttpStatus.FOUND);
          }
        } else {
          String authJsVersion = feZkClient.getAuthJsVersion();
          String authCssVersion = feZkClient.getAuthCssVersion();

          result = getPage(PAGE_AUTH, authJsVersion, authCssVersion, false, null, null);
        }
      } else {
        StringDTO tempToken = facadeFactory.getTokenFacade().getTemporaryTokenByUUID(uuid);
        ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(tempToken.getServiceStatusDTO().getCode());
        String authJsVersion = feZkClient.getAuthJsVersion();
        String authCssVersion = feZkClient.getAuthCssVersion();

        if (serviceStatus != ServiceStatus.COMMON_OK) {
          result = getPage(PAGE_AUTH, authJsVersion, authCssVersion, true, ServiceStatus.AS_LINK_INVALID_OR_EXPIRED, null);
        } else {
          result = getPage(PAGE_AUTH, authJsVersion, authCssVersion, true, ServiceStatus.COMMON_OK, tempToken.getData());
        }
      }
    } catch (Exception e) {
      LOGGER.error("jumpForAuth()-request: fail", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    LOGGER.info("jumpForAuth()-response" + result);
    return result;
  }

  @LogAround
  @ResponseBody
  @RequestMapping(
          value = "/onboarding-flows/staff",
          method = RequestMethod.GET,
          produces = "text/html")
  public Object jumpForOnboarding(
          @RequestParam("uuid") String uuid
  ) throws Exception {

    StringDTO tempToken = facadeFactory.getTokenFacade().getTemporaryTokenByUUID(uuid);
    // TODO: handle exceptions
    String onboardingJsVersion = feZkClient.getOnboardingJsVersion();
    String onboardingCssVersion = feZkClient.getOnboardingCssVersion();
    ServiceStatus rpcStatus = ServiceStatus.getEnumByCode(tempToken.getServiceStatusDTO().getCode());
    String html = null;
    if (rpcStatus.equals(ServiceStatus.COMMON_OK)) {
      html = getPage(PAGE_ONBOARDING, onboardingJsVersion, onboardingCssVersion, true, ServiceStatus.COMMON_OK, tempToken.getData());
    } else {
      html = getPage(
              PAGE_ONBOARDING, onboardingJsVersion, onboardingCssVersion, true, ServiceStatus.AS_LINK_INVALID_OR_EXPIRED, tempToken.getData());
    }

    return html;
  }

  private static String getErrorAdminPageWithoutParams() {
    return ParamName.ERROR_ADMIN_HTML_PAGE;
  }

  private String getPage(
          String pageName, String jsVersion,String cssVersion, boolean hasParam, ServiceStatus serviceStatus, String temporaryToken) {
    jsVersion = "." + jsVersion;
    cssVersion = "." + cssVersion;
//    jsVersion = "";
//    cssVersion = "";

    return "<!DOCTYPE html>\n"
            + "<html lang=\"en\">\n"
            + "<head>\n"
            + "    <meta charset=\"UTF-8\">\n"
            + "    <title>人才易</title>\n"
            + "    <link rel=\"icon\" href=\"/rencaiyi.ico\" sizes=\"64x64\">\n"
            + "    <link rel=\"stylesheet\" href=\"/static/" + pageName + cssVersion + ".css" + "\">\n"
            + "    \n"
            + "</head>\n"
            + ((hasParam) ? getScriptOfNonemptyGlobalParams(serviceStatus, temporaryToken)
            : getScriptOfEmptyGlobalParams())
            + "<body>\n"
            + "    <div id=\"root\"></div>\n"
            + "<script>\n" +
            "      var mainJsName = '" + pageName + "'\n" +
            "      function getPolyfill() {\n" +
            "        return !''.startsWith;\n" +
            "      }\n" +
            "      function GetIEVersion() {\n" +
            "        var sAgent = window.navigator.userAgent;\n" +
            "        var Idx = sAgent.indexOf(\"MSIE\");\n" +
            "        if (Idx > 0) \n" +
            "          return parseInt(sAgent.substring(Idx+ 5, sAgent.indexOf(\".\", Idx)));\n" +
            "        else if (!!navigator.userAgent.match(/Trident\\/7\\./)) \n" +
            "          return 11;\n" +
            "        else\n" +
            "          return 0; \n" +
            "       }\n" +
            "      function setJs(name) {\n" +
            "        var js = document.createElement('script'); \n" +
            "        js.type = 'text/javascript'; \n" +
            "        js.src = '/static/'+ name +'.js';\n" +
            "        if(js.readyState){ \n" +
            "          js.onreadystatechange = function(){ \n" +
            "            if (js.readyState == \"loaded\" || js.readyState == \"complete\"){ \n" +
            "              document.body.appendChild(js); \n" +
            "            } \n" +
            "          }; \n" +
            "        }else{ \n" +
            "          document.body.appendChild(js); \n" +
            "        } \n" +
            "        \n" +
            "      }\n" +
            "      var ieVersion = GetIEVersion();\n" +
            "      if(ieVersion > 9) {\n" +
            "        window.onload=function(){\n" +
            "          document.body.setAttribute(\"data-role\",\"ie\");\n" +
            "        }\n" +
            "      }else if(ieVersion > 0){\n" +
            "        location.href = '" + host + "outdatebrowser.html';\n" +
            "      }\n" +
            "      if(getPolyfill()){\n" +
            "        setJs('polyfill')\n" +
            "        \n" +
            "      }\n" +
            "      setJs(mainJsName" + "+\"" + jsVersion + "\")\n" +
            "    </script>"
            /*+ "    <script>\n"
            + "      var mainJsName = '" + pageName + "'\n"
            + "      function GetIEVersion() {\n"
            + "        return !''.startsWith;\n"
            + "      }\n"
            + "      function setJs(name) {\n"
            + "        var js = document.createElement('script'); \n"
            + "        js.type = 'text/javascript'; \n"
            + "        js.src = '/static/'+ name +'.js?v=" + jsVersion + "';\n"
            + "        if(js.readyState){ \n"
            + "          js.onreadystatechange = function(){ \n"
            + "            if (js.readyState == \"loaded\" || js.readyState == \"complete\"){ \n"
            + "              document.body.appendChild(js); \n"
            + "            } \n"
            + "          }; \n"
            + "        }else{ \n"
            + "          document.body.appendChild(js); \n"
            + "        } \n"
            + "        \n"
            + "      }\n"
            + "      if(GetIEVersion()){\n"
            + "        setJs('polyfill')\n"
            + "        window.onload=function(){\n"
            + "          document.body.setAttribute(\"data-role\",\"ie\");\n"
            + "        }\n"
            + "      }\n"
            + "      setJs(mainJsName)\n"
            + "    </script>\n"*/
            + "</body>\n"
            + "</html>\n";
  }

  private String getScriptOfNonemptyGlobalParams(ServiceStatus serviceStatus, String temporaryToken) {
    return
            "<script>\n"
                    + "      window.global_params = {\n"
                    + "          'code': '" + serviceStatus.getCode() + "', \n"
                    + "          'msg': '" + serviceStatus.getMsg() + "', \n"
                    + "          'temporaryToken': '" + temporaryToken + "' \n"
                    + "      }\n"
                    + "</script>\n";
  }

  private String getScriptOfEmptyGlobalParams() {
    return
            "<script>\n"
                    + "  window.global_params = {}\n"
                    + "</script>\n";
  }

}

