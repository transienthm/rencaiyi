package hr.wozai.service.api.util;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/3/3
 */
public class ParamName {
  public static final String PAYLOAD_PARAM_TEAM_NAME = "teamName";
  public static final String PAYLOAD_PARAM_PARENT_TEAM_ID = "parentTeamId";
  public static final String PAYLOAD_PARAM_USER_ID_LIST = "userIdList";
  public static final String PAYLOAD_PARAM_TO_TEAM_ID = "toTeamId";
  public static final String PAYLOAD_PARAM_REPORT_USER_ID = "reportUserId";

  public static final String PAYLOAD_PARAM_PASSWORD = "password";
  public static final String PAYLOAD_PARAM_FULLNAME = "fullName";
  public static final String PAYLOAD_PARAM_ORG_NAME = "orgName";
  public static final String PAYLOAD_PARAM_MOBILE_NUMBER = "mobilePhone";
  public static final String PAYLOAD_PARAM_LOGIN_REMEMBER_ME = "rememberMe";
  public static final String PAYLOAD_PARAM_CAPTCHA_TIMESTAMP = "timestamp";
  public static final String PAYLOAD_PARAM_CAPTCHA_TEXT = "captcha";
  public static final String HEADER_ACCESS_TOKEN = "X-Access-Token";
  public static final String HEADER_REFRESH_TOKEN = "X-Refresh-Token";
  public static final String LOGIN_FAIL_TIME = "login_fail_time";

  public static final String PAYLOAD_PARAM_VERIFICATION = "verification";
  public static final String PAYLOAD_PARAM_EMAIL = "email";

  public static final String PAYLOAD_PARAM_PASSWORD_OLD = "oldPassword";
  public static final String PAYLOAD_PARAM_PASSWORD_NEW = "newPassword";

  public static final String ERROR_ADMIN_HTML_PAGE = "<!doctype html>\n" +
          "<html class=\"no-js\" lang=\"\">\n" +
          "<head>\n" +
          "  <meta charset=\"utf-8\">\n" +
          "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
          "  <title>404</title>\n" +
          "  <style>\n" +
          "    body{\n" +
          "      background: #f5f6f7;\n" +
          "      font-family: \"Helvetica Neue\",Helvetica,Arial,\"Hiragino Sans GB\",\"Hiragino Sans GB W3\",\"WenQuanYi Micro Hei\",sans-serif;\n" +
          "    }\n" +
          "    .box{\n" +
          "      position: absolute;\n" +
          "      top: 270px;\n" +
          "      left: 50%;\n" +
          "      margin-left: -300px;\n" +
          "      width: 600px;\n" +
          "      height: 170px;\n" +
          "      padding: 0 40px;\n" +
          "    }\n" +
          "    .circle{\n" +
          "      width:  150px;\n" +
          "      height: 150px;\n" +
          "      background-color: #e4e4e4;\n" +
          "      border-radius: 50%;\n" +
          "      color: #fff;\n" +
          "      position: relative;\n" +
          "      overflow: hidden;\n" +
          "      float: left;\n" +
          "    }\n" +
          "    .circle span{\n" +
          "      font-size: 70px;\n" +
          "      font-weight: 700;\n" +
          "      position: absolute;\n" +
          "      top: 29px;\n" +
          "      right: -9px;\n" +
          "    }\n" +
          "    .info{\n" +
          "      float: left;\n" +
          "      margin-left: 35px;\n" +
          "    }\n" +
          "    h2{\n" +
          "      color:  #777;\n" +
          "      font-size: 60px;\n" +
          "      margin: 12px 0 0;\n" +
          "    }\n" +
          "    p{\n" +
          "      color: #bbb;\n" +
          "      font-size: 24px;\n" +
          "      margin: 0;\n" +
          "    }\n" +
          "    .logo{\n" +
          "      background-image: url(data:image/svg+xml;base64,PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0idXRmLTgiPz48IURPQ1RZUEUgc3ZnIFBVQkxJQyAiLS8vVzNDLy9EVEQgU1ZHIDEuMS8vRU4iICJodHRwOi8vd3d3LnczLm9yZy9HcmFwaGljcy9TVkcvMS4xL0RURC9zdmcxMS5kdGQiPjxzdmcgdmVyc2lvbj0iMS4xIiBpZD0i5Zu+5bGCXzEiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyIgeG1sbnM6eGxpbms9Imh0dHA6Ly93d3cudzMub3JnLzE5OTkveGxpbmsiIHg9IjBweCIgeT0iMHB4IiB2aWV3Qm94PSIwIDAgNzAwIDMwMCIgZW5hYmxlLWJhY2tncm91bmQ9Im5ldyAwIDAgNzAwIDMwMCIgeG1sOnNwYWNlPSJwcmVzZXJ2ZSI+PGc+PGc+PHBhdGggZmlsbC1ydWxlPSJldmVub2RkIiBjbGlwLXJ1bGU9ImV2ZW5vZGQiIGZpbGw9IiMzYTkyZWEiIGQ9Ik0yMzAuNywyNy4yaC0zOC4yTDIzMiwxMzAuM2gtNTMuOVY4OS41aC0zOC4ydjQwLjhoLTQzdjkuNmg0M3Y2NC4ybC0zNy4xLDIyLjFsNC45LDguMmwzMi4yLTE5LjJ2NjEuMWgzOC4ydi04My45bDMyLjMtMTkuMmwtNC45LTguMmwtMjcuNCwxNi4zdi00MS41aDU3LjVsMjEuOSw1N2wtNjMuOSwzOC45bDQuOSw4LjJsNjIuNS0zOGwyNyw3MC40aDM4LjlsLTE0LjctMzcuNWwtMC4yLDAuM2wtMTEuOS0zMC45bC03LjgtMTkuN2gwLjJsLTAuNS0xLjRsMjQuOS0xNS4ybC00LjktOC4yTDI4OC41LDE3OGwtMTQuNi0zOC4yaDUzLjd2LTkuNmgtNTcuM0wyMzAuNywyNy4yeiBNMTgwLjMsNTUuOGgtNjkuMXY5LjZoNjkuMVY1NS44eiBNNTI3LjEsMTY2LjFoMzMuNHYtOS42aC0zMy40di0yOC44aC0zOC4ydjI4LjhoLTQ3Ljh2OS42aDQ3Ljh2MTAwLjZoLTYyLjFWMTU2LjZoLTE4LjdsMzctNTcuNWgxMzQuNnYtOS42SDQ1MS4zbDQwLjEtNjIuM2gtNDNsLTQwLjEsNjIuM2gtOTUuNGwtMTUuMy0zOC4zaC0zOC4ybDE5LjEsNDcuOWgxMjMuN0wzMzUuOSwyMDJsMTYuMyw0MS41bDM2LjQtNTYuNXY4OS40SDU4OXYtOS42aC02MS45VjE2Ni4xeiBNNjY5LDE3Ni40djkxaC02NnY5aDgwdi0xMDBINjY5eiIvPjwvZz48L2c+PHBhdGggZmlsbC1ydWxlPSJldmVub2RkIiBjbGlwLXJ1bGU9ImV2ZW5vZGQiIGZpbGw9IiMzYTkyZWEiIGQ9Ik0yOC41LDIzLjV2OTFoLTE0di0xMDBoODB2OUgyOC41eiIvPjwvc3ZnPg==);\n" +
          "\n" +
          "      width: 112px;\n" +
          "      height: 48px;\n" +
          "      position: absolute;\n" +
          "      top: 43px;\n" +
          "      left: 90px;\n" +
          "    }\n" +
          "  </style>\n" +
          "</head>\n" +
          "<body>\n" +
          "  <div class=\"logo\"></div>\n" +
          "  <div class=\"box\">\n" +
          "    <div class=\"circle\"><span>404</span></div>\n" +
          "    <div class=\"info\">\n" +
          "      <h2>NotFound</h2>\n" +
          "      <p>该页面不存在，或您没有访问权限</p>\n" +
          "    </div>\n" +
          "  </div>\n" +
          "</body>\n" +
          "</html>";

  public static long getDecryptValueFromString(String encryptValue) {
    long decryptValue = -1;
    try {
      decryptValue = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptValue));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    return decryptValue;
  }
}
