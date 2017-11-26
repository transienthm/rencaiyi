package hr.wozai.service.thirdparty.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.thirdparty.server.dao.CaptchaVerificationDao;
import hr.wozai.service.thirdparty.server.model.CaptchaVerification;
import hr.wozai.service.thirdparty.server.service.CaptchaService;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import hr.wozai.service.thirdparty.server.test.utils.AopTargetUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.SQLException;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 16/7/8.
 */
public class CaptchaServiceChenImplTest extends BaseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    CaptchaService captchaService;


    @Autowired
    CaptchaVerificationDao captchaVerificationDao;

    @Mock
    CaptchaVerificationDao spyCaptchaVerificationDao;

    private CaptchaVerification captchaVerification = new CaptchaVerification();


    @Test
    public void testAll() throws Exception {
        Map<String, Object> captchaMap = captchaService.getCaptcha();
        System.out.println(captchaMap);
        long now = (Long) captchaMap.get("captcha_timestamp");

        thrown.expect(ServiceStatusException.class);
        captchaService.verifyCaptcha(now, "110");

        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(captchaService), "captchaVerificationDao", spyCaptchaVerificationDao);
        captchaVerification.setCreateTime(now);
        captchaVerification.setExpireTime(now + 1200);
        captchaVerification.setVerificationCode("110");
        Mockito.doReturn(captchaVerification).when(spyCaptchaVerificationDao).findByCreateTimeAndVerificationCode(Mockito.anyLong(), Mockito.anyString());

        captchaService.verifyCaptcha(now, "110");

        Mockito.doThrow(new SQLException()).when(spyCaptchaVerificationDao).insert(Mockito.anyObject());

        thrown.expect(ServiceStatusException.class);
        captchaService.getCaptcha();

    }

}