package hr.wozai.service.thirdparty.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.thirdparty.server.dao.SmsVerificationDao;
import hr.wozai.service.thirdparty.server.model.SmsVerification;
import hr.wozai.service.thirdparty.server.service.SmsService;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import hr.wozai.service.thirdparty.server.test.utils.AopTargetUtils;
import hr.wozai.service.thirdparty.server.utils.SmsYunpianComponent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 16/7/10.
 */
public class SmsServiceYunpianImplTest extends BaseTest{

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    SmsService smsService;

    @Autowired
    SmsVerificationDao smsVerificationDao;

    @Mock
    SmsVerificationDao spySmsVerificationDao;

    private SmsVerification smsVerification;

    @Mock
    SmsYunpianComponent spySmsComponent;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        smsVerification = new SmsVerification();
        smsVerification.setVerificationCode("110");
        long now = TimeUtils.getNowTimestmapInMillis();
        smsVerification.setCreateTime(now);
        smsVerification.setExpireTime(now + 3600);
        smsVerification.setOptionalIdentifier("无");
        smsVerification.setEntryPoint(1);
        smsVerification.setExtend("无");

        ReflectionTestUtils.setField(AopTargetUtils.getTarget(smsService), "smsVerificationDao", spySmsVerificationDao);
        Mockito.doReturn(smsVerification).when(spySmsVerificationDao).findByMobilePhoneAndVerificationCode(Mockito.anyString(),Mockito.matches("110"));

        ReflectionTestUtils.setField(AopTargetUtils.getTarget(smsService), "smsYunpianComponent", spySmsComponent);
        String response = "{\"code\":0,\"msg\":\"发送成功\",\"count\":1,\"fee\":1.0,\"unit\":\"COUNT\",\"mobile\":\"10000000000\",\"sid\":8445832868}";
        Mockito.doReturn(response).when(spySmsComponent).tplSendSms(Mockito.anyString(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyString());
        
        
    }



    @Test
    public void testAll() throws Exception {

        boolean result = smsService.sendSmsMessage("10000000000");
        Assert.assertTrue(result);

        result = smsService.sendSmsMessage("10000000000", "测试", 1399337l);
        Assert.assertTrue(result);

        smsService.verifySmsMessage("10000000000", "110");

    }

    @Test
    public void testSendMessageException() {
        thrown.expect(ServiceStatusException.class);
        smsService.sendSmsMessage("110");

    }

    @Test
    public void testCondition1() {
        thrown.expect(ServiceStatusException.class);
        smsService.sendSmsMessage("120", "test", 1399337l);
    }


    @Test
    public void testCondition2() {
        thrown.expect(ServiceStatusException.class);
        smsService.verifySmsMessage("10000000000", null);
    }

    @Test
    public void testCondition3() {
        thrown.expect(ServiceStatusException.class);
        long now = TimeUtils.getNowTimestmapInMillis();
        smsVerification.setExpireTime(now - 3600);
        smsService.verifySmsMessage("10000000000", "110");
    }

    @Test
    public void testExceptionCodeNotFound() {
        thrown.expect(ServiceStatusException.class);
        smsService.verifySmsMessage("10000000000", "120");
    }

}