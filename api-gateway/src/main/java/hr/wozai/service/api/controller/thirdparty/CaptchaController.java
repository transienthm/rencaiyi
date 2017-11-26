// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.controller.thirdparty;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.result.Result;

import hr.wozai.service.thirdparty.client.dto.CaptchaDTO;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("captchaController")
public class CaptchaController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaController.class);

  private static final String PARAM_CAPTCHA_TIMESTAMP = "timestamp";
  private static final String PARAM_CAPTCHA_TEXT = "captcha";

  @Autowired
  FacadeFactory facadeFactory;


  @RequestMapping(value = "/auths/captcha", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<CaptchaDTO> getCaptcha() {

    LOGGER.info("getCaptcha()-request");

    Result<CaptchaDTO> result = new Result<>();
    try {
      CaptchaDTO remoteResult = facadeFactory.getCaptchaFacade().getCaptcha();
      result.setData(remoteResult);
      result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    } catch (Exception e) {
      LOGGER.info("getCaptcha(): fail", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    LOGGER.info("getCaptcha()-response: result=" + result);
    return result;
  }


  @RequestMapping(value = "/auths/captcha", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<BooleanDTO> verifyCaptcha(
      @RequestBody JSONObject jsonObject
  ) throws Exception {

    LOGGER.info("verifyCaptcha()-request: jsonObject=" + jsonObject);
    Result<BooleanDTO> result = new Result<>();
    Long timestamp = null;
    String captcha = null;
    try {
      timestamp = jsonObject.getLong(PARAM_CAPTCHA_TIMESTAMP);
      captcha = jsonObject.getString(PARAM_CAPTCHA_TEXT);
    } catch (Exception e) {
      LOGGER.info("verifyCaptcha(): fail to verify", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }
    if (null == timestamp
        || null == captcha) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    try {
      BooleanDTO remoteResult = facadeFactory.getCaptchaFacade().verifyCaptcha(timestamp, captcha);
      result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    } catch (Exception e) {
      LOGGER.info("verifyCaptcha(): fail to verify", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    LOGGER.info("verifyCaptcha()-response: result=" + result);
    return result;
  }

}
