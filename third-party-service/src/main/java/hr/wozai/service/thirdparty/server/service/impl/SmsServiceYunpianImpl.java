package hr.wozai.service.thirdparty.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.IntegerUtils;
import hr.wozai.service.servicecommons.commons.utils.PhoneUtils;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.thirdparty.server.dao.SmsVerificationDao;
import hr.wozai.service.thirdparty.server.enums.EntryPoint;
import hr.wozai.service.thirdparty.server.enums.SmsTemplate;
import hr.wozai.service.thirdparty.server.enums.VerificationStatus;
import hr.wozai.service.thirdparty.server.model.SmsVerification;
import hr.wozai.service.thirdparty.server.service.SmsService;


import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.thirdparty.server.utils.SmsYunpianComponent;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by wangbin on 16/5/28.
 */
@Service("smsService")
public class SmsServiceYunpianImpl implements SmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsServiceYunpianImpl.class);

    @Autowired
    SmsVerificationDao smsVerificationDao;

    @Autowired
    private SmsYunpianComponent smsYunpianComponent;


    @Value("${sms.yunpian.apiKey}")
    private String apiKey;

    private static final long EXPIRE_PERIOD_IN_MINUTE = 10;
    private static final long EXPIRE_PERIOD_IN_MILLIS = 1000 * 60 * EXPIRE_PERIOD_IN_MINUTE;
    private static final Random rand = new Random();
    //编码格式。发送编码格式统一用UTF-8
    private static String ENCODING = "UTF-8";

    private static Properties properties;

    @Override
    @LogAround
    public boolean sendSmsMessage(String mobilePhoneNumber) {

        if (!PhoneUtils.isValidMobileNumber(mobilePhoneNumber)){
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }

        try {
            String verificationCode = createVerificationCode();
            String tplValue = URLEncoder.encode("#code#", ENCODING) + "="
                    + URLEncoder.encode(verificationCode, ENCODING);
            String responseJson = smsYunpianComponent.tplSendSms(apiKey, SmsTemplate.VERIFICATION_CODE.getTemplateId(),tplValue,mobilePhoneNumber);
            JSONObject response = JSON.parseObject(responseJson);
            Integer count = (Integer) response.get("count");
            LOGGER.info("send SmsMessage response:" + response);
            if (count != null && count > 0){
                long now = TimeUtils.getNowTimestmapInMillis();
                SmsVerification smsVerification = new SmsVerification();
                smsVerification.setMobilePhone(mobilePhoneNumber);
                smsVerification.setVerificationCode(verificationCode);
                smsVerification.setEntryPoint(EntryPoint.SIGNUP.getCode());
                smsVerification.setCreateTime(now);
                smsVerification.setExpireTime(now + EXPIRE_PERIOD_IN_MILLIS);
                smsVerification.setVerificationStatus(VerificationStatus.SENT.getCode());
                LOGGER.info(smsVerification.toString());
                LOGGER.info("dao:" + smsVerificationDao);
                smsVerificationDao.insert(smsVerification);
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("sendSmsMessage()-error", e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }

        return false;
    }

    @LogAround
    public boolean sendSmsMessage(String mobilePhoneNumber,String text,long tplId){
        if (!PhoneUtils.isValidMobileNumber(mobilePhoneNumber)){
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }

        try {
            String tplValue = URLEncoder.encode("#warning#", ENCODING) + "="
                    + URLEncoder.encode(text, ENCODING);
            String response = smsYunpianComponent.tplSendSms(apiKey, tplId, tplValue, mobilePhoneNumber);
            LOGGER.info("sms response:"+ response);
            return true;
        }catch (Exception e){
            LOGGER.error("sendSmsMessage()-error",e);
            throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @LogAround
    public boolean verifySmsMessage(String mobilePhoneNumber, String verificationCode) {
        if (!PhoneUtils.isValidMobileNumber(mobilePhoneNumber) || StringUtils.isNullOrEmpty(verificationCode)) {
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }

        boolean isVerified = false;
        SmsVerification smsVerification =
                smsVerificationDao.findByMobilePhoneAndVerificationCode(mobilePhoneNumber, verificationCode);
        long now = TimeUtils.getNowTimestmapInMillis();
        if (null != smsVerification) {
            if (smsVerification.getExpireTime() < now) {
                throw new ServiceStatusException(ServiceStatus.TP_EXPIRED_VERIFICATION);
            } else if(IntegerUtils.equals(smsVerification.getVerificationStatus(), VerificationStatus.VERIFIED.getCode())) {
                throw new ServiceStatusException(ServiceStatus.TP_EXISTING_VERIFICATION);
            } else {
                smsVerification.setVerifyTime(now);
                smsVerification.setVerificationStatus(VerificationStatus.VERIFIED.getCode());
                smsVerificationDao.updateByPrimaryKeySelective(smsVerification);
                isVerified = true;
            }
        }else {
            throw new ServiceStatusException(ServiceStatus.TP_SMS_VERIFICATION_NOT_FOUND);
        }

        return isVerified;
    }

    @LogAround
    private String createVerificationCode() {
        return Integer.toString(rand.nextInt(899999) + 100000);
    }
}


