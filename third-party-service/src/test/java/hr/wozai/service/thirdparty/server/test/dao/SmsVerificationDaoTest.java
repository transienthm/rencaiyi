package hr.wozai.service.thirdparty.server.test.dao;

import hr.wozai.service.thirdparty.server.dao.SmsVerificationDao;
import hr.wozai.service.thirdparty.server.model.SmsVerification;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 16/6/27.
 */
public class SmsVerificationDaoTest extends BaseTest{
    @Autowired
    private SmsVerificationDao smsVerificationDao;

    @Test
    public void testAll() {
        SmsVerification smsVerification = new SmsVerification();
        smsVerification.setMobilePhone("110");
        smsVerification.setVerificationCode("120");
        smsVerification.setEntryPoint(1);
        smsVerification.setOptionalIdentifier("测试");
        Date date = new Date();
        smsVerification.setCreateTime(date.getTime());
        smsVerification.setExpireTime(date.getTime()+100000l);
        smsVerification.setVerificationStatus(0);

        smsVerificationDao.insert(smsVerification);
        SmsVerification smsVerification1 = smsVerificationDao.findByMobilePhoneAndVerificationCode("110","120");
        Assert.assertEquals("110",smsVerification1.getMobilePhone());

        SmsVerification smsVerification2 = smsVerificationDao.findByMobilePhoneAndOptionalIdentifierAndVerificationCode("110", "测试", "120");
        Assert.assertEquals("测试",smsVerification2.getOptionalIdentifier());

        smsVerification2.setVerificationCode("130");
        int result = smsVerificationDao.updateByPrimaryKeySelective(smsVerification2);
        Assert.assertEquals(1, result);
    }
}