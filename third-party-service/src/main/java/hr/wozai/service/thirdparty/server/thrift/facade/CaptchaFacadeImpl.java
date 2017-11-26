// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.thirdparty.client.dto.CaptchaDTO;
import hr.wozai.service.thirdparty.client.facade.CaptchaFacade;
import hr.wozai.service.thirdparty.server.helper.FacadeExceptionHelper;
import hr.wozai.service.thirdparty.server.service.CaptchaService;
import hr.wozai.service.thirdparty.server.service.impl.CaptchaServiceChenImpl;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-08
 */
@Service("captchaFacade")
public class CaptchaFacadeImpl implements CaptchaFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(CaptchaFacadeImpl.class);

  @Autowired
  @Qualifier("chenCaptchaService")
  CaptchaService captchaService;

  @Override
  public CaptchaDTO getCaptcha() {

    LOGGER.info("getCaptcha()-request");

    CaptchaDTO result = new CaptchaDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);
    try {
      Map<String, Object> params = captchaService.getCaptcha();
      result.setCreateTime((Long) params.get(CaptchaServiceChenImpl.PARAM_CAPTCHA_TIMESTAMP));
      result.setCaptchaImageBase64((String) params.get(CaptchaServiceChenImpl.PARAM_CAPTCHA_IMAGE));
    } catch (Exception e) {
      LOGGER.error("getCaptcha()-error:{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    LOGGER.info("getCaptcha()-response: result=" + result);
    return result;
  }

  @Override
  public BooleanDTO verifyCaptcha(long createTime, String verificationCode) {

    LOGGER.info("verifyCaptcha()-request: createTime=" + createTime + ", verificationCode=" + verificationCode);

    BooleanDTO result = new BooleanDTO();
    ServiceStatusDTO serviceStatusDTO =
            new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);
    try {
      boolean verifyResult = captchaService.verifyCaptcha(createTime, verificationCode);
      result.setData(verifyResult);
    } catch (Exception e) {
      LOGGER.error("verifyCaptcha()-fail:{}", e);
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
    }

    LOGGER.info("verifyCaptcha()-response: result=" + result);
    return result;
  }
}
