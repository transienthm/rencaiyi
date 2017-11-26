// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.api.controller.thirdparty;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.BooleanVO;
import hr.wozai.service.api.vo.trustedmobilephone.TrustedMobilePhoneVO;

import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-16
 */
@Controller("smsController")
public class SmsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsController.class);
    
    @Autowired
    FacadeFactory facadeFactory;

    @RequestMapping(value = "/auths/send_sms", method = RequestMethod.GET)
    @ResponseBody
    @LogAround

    public Result<BooleanVO> sendSmsMessage(
            @RequestParam("mobilePhone") String mobilePhone,
            HttpServletRequest request
    ) {

        LOGGER.info("sendSmsMessage()-request: mobilePhone=" + mobilePhone);

        Result<BooleanVO> result = new Result<>();

        try {
            BooleanDTO remoteResult = facadeFactory.getSmsFacade().sendSmsMessage(mobilePhone);
            BooleanVO booleanVO = new BooleanVO();
            BeanUtils.copyProperties(remoteResult, booleanVO);
            result.setData(booleanVO);
            result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
        } catch (Exception e) {
            LOGGER.info("sendSmsMessage(): fail, mobilePhone=" + mobilePhone + "exception is:" + e.getMessage());
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }

        LOGGER.info("sendSmsMessage()-response: result=" + result);
        return result;
    }


    @RequestMapping(
            value = "/auths/sms-code/trusted-mobile-phones",
            method = RequestMethod.GET,
            produces = "application/json")
    @ResponseBody
    @LogAround

    public Result<TrustedMobilePhoneVO> sendSmsCodeToTrustedMobilePhone(@RequestParam("mobilePhone") String mobilePhone) {
        Result<TrustedMobilePhoneVO> result = new Result<>();
        try {
            BooleanDTO booleanDTO = facadeFactory.getSmsFacade().sendSmsCodeToTrustedMobilePhone(mobilePhone);
            ServiceStatus rpcStatus = ServiceStatus.getEnumByMsg(booleanDTO.getServiceStatusDTO().getMsg());
            if (rpcStatus.equals(ServiceStatus.TP_MOBILE_NOT_FOUND)) {
                result.setCodeAndMsg(ServiceStatus.TP_MOBILE_NOT_FOUND);
            }
        } catch (Exception e) {
            LOGGER.error("sendSmsCodeToTrustedMobilePhone()-error", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
        return result;
    }
}
