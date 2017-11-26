// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.controller.auth;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.EmailUtils;
import hr.wozai.service.servicecommons.commons.utils.PasswordUtils;
import hr.wozai.service.servicecommons.commons.utils.PhoneUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.ParamName;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import hr.wozai.service.user.client.userorg.dto.UserAccountDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: lepujiu
 * @Version: 1.0
 * @Created: 2015-1-5
 */
@Controller("signupController")
public class SignupController {

  private static final Logger LOGGER = LoggerFactory.getLogger(SignupController.class);
  @Autowired
  FacadeFactory facadeFactory;


  @RequestMapping(value = "/auths/signupv2", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Object signupByEmail(
          @RequestBody JSONObject payload, HttpServletRequest request, HttpServletResponse response) {
    Result<Object> result = new Result<>();

    String email = payload.getString(ParamName.PAYLOAD_PARAM_EMAIL);
    String password = payload.getString(ParamName.PAYLOAD_PARAM_PASSWORD);
    String orgName = payload.getString(ParamName.PAYLOAD_PARAM_ORG_NAME);

    LOGGER.info("signupByEmail()-request: orgName=" + orgName
            + ", email=" + email);

    // check param
    if (!PasswordUtils.isValidPassword(password)
            || !EmailUtils.isValidEmailAddressByApache(email)
            || !StringUtils.isValidVarchar100(orgName)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    // sign up: insert user profile, create an org, create org member
    BooleanDTO signupResult = facadeFactory.getUserFacade().signUpWithEmail(orgName, email, password);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(signupResult.getServiceStatusDTO().getCode());
    if (!signupResult.getData()) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(ServiceStatus.COMMON_CREATED);
    return result;
  }

  @LogAround

  @RequestMapping(value = "/auths/send_sms", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Object verifyUserInfoAndSendMobileVerification(@RequestBody JSONObject payload) {
    Result<Object> result = new Result<>();
    String mobilePhone = payload.getString(ParamName.PAYLOAD_PARAM_MOBILE_NUMBER);

    // check param
    if (!PhoneUtils.isValidMobileNumber(mobilePhone)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    // check whether user exists
    UserAccountDTO userAccountDTO = facadeFactory.getUserFacade().getUserAccountByEmail(mobilePhone, 0L, 0L);

    if (null != userAccountDTO.getUserId()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    // sms service, send sms
    BooleanDTO remoteResult = facadeFactory.getSmsFacade().sendSmsMessage(mobilePhone);
    ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
    if (!remoteResult.getData()) {
      throw new ServiceStatusException(serviceStatus);
    }

    result.setCodeAndMsg(ServiceStatus.COMMON_OK);
    result.setData(null);
    return result;
  }

  private static String getJumpHtmlTemplateWithCodeAndMessage(int code, String msg) {

    return "<!DOCTYPE html>\n"
            + "<html>\n"
            + "  <head>\n"
            + "  <meta charset=\"UTF-8\">\n"
            + "  <script>\n"
            + "    window.global_params = {\n"
            + "        'code': " + code + ", \n"
            + "        'msg': '" + msg + "' \n"
            + "    }\n"
            + "  </script>\n"
            + "  </head>\n"
            + "  <body>\n"
            + "    <div id=\"root\"></div>\n"
            + "    <script src=\"http://static.shanqian.cc/auth.js\"></script>\n"
            + "  </body>\n"
            + "</html>";
  }

}
