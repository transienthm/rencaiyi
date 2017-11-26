// Copyright (C) 2015 Wozai
// All rights reserved

package hr.wozai.service.thirdparty.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.thirdparty.client.dto.TrustedMobilePhoneDTO;
import hr.wozai.service.thirdparty.client.facade.SmsFacade;
import hr.wozai.service.thirdparty.server.helper.FacadeExceptionHelper;
import hr.wozai.service.thirdparty.server.model.TrustedMobilePhone;
import hr.wozai.service.thirdparty.server.service.SmsService;
import hr.wozai.service.thirdparty.server.service.TrustedMobilePhoneService;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2015-09-08
 */
@Service("smsFacade")
public class SmsFacadeImpl implements SmsFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsFacadeImpl.class);

    @Autowired
    SmsService smsService;

    @Autowired
    TrustedMobilePhoneService trustedMobilePhoneService;

    @Override
    @LogAround
    public BooleanDTO sendSmsMessage(String mobilePhone) {

        LOGGER.info("sendSmsMessage()-request: mobilePhone=" + mobilePhone);

        BooleanDTO result = new BooleanDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {
            boolean sendResult = smsService.sendSmsMessage(mobilePhone);
            result.setData(sendResult);
        } catch (Exception e) {
            LOGGER.error("sendSmsMessage()-error{}, mobilePhone=" + mobilePhone, e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        LOGGER.info("sendSmsMessage()-response: result=" + result);
        return result;
    }

    @Override
    @LogAround
    public BooleanDTO verifySmsMessage(String mobilePhone, String verificationCode) {

        LOGGER.info("verifySmsMessage()-request: mobilePhone=" + mobilePhone
                + ", verificationCode=" + verificationCode);

        BooleanDTO result = new BooleanDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {
            boolean verifyResult = smsService.verifySmsMessage(mobilePhone, verificationCode);
            result.setData(verifyResult);
        } catch (Exception e) {
            LOGGER.error("verifySmsMessage()-request: mobilePhone=" + mobilePhone
                    + ", verificationCode=" + verificationCode + " error:{}", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        LOGGER.info("verifySmsMessage()-response: result=" + result);
        return result;
    }

    @Override
    @LogAround
    public BooleanDTO verifySmsCodeOfTrustedMobilePhone(String mobilePhone, String smsCode) {
        BooleanDTO result = new BooleanDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            boolean flag = smsService.verifySmsMessage(mobilePhone, smsCode);
            result.setData(flag);
        } catch (Exception e) {
            LOGGER.error("verifySmsCodeOfTrustedMobilePhone()-error:{}", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            result.setData(false);

        }
        return result;
    }


    @LogAround
    private BooleanDTO verifyMobilePhoneTrustedOrNot(String mobilePhone) {
        LOGGER.info("verifyMobilePhone-request:mobilePhone:" + mobilePhone);
        BooleanDTO result = new BooleanDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {
            trustedMobilePhoneService.findTrustedMobilePhoneByMobilePhone(mobilePhone);
        } catch (Exception e) {
            LOGGER.error("verifyMobilePhoneTrustedOrNot()-error:{}", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            result.setData(false);
            return result;
        }
        result.setData(true);
        return result;
    }

    @Override
    @LogAround
    public BooleanDTO sendSmsCodeToTrustedMobilePhone(String mobilePhone) {
        BooleanDTO result = new BooleanDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {
            result = verifyMobilePhoneTrustedOrNot(mobilePhone);
        } catch (Exception e) {
            LOGGER.error("This mobile phone is not trusted", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            result.setData(false);
            return result;
        }

        if (result.getData()) {
            try {
                boolean flag = smsService.sendSmsMessage(mobilePhone);
                result.setData(flag);
            } catch (Exception e) {
                LOGGER.error("sendSmsCodeToTrustedMobilePhone()-error:sendSmsMessageError", e);
                FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
                result.setData(false);
            }
        }
        return result;
    }

}
