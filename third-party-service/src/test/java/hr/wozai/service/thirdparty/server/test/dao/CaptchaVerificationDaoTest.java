package hr.wozai.service.thirdparty.server.test.dao;

import hr.wozai.service.thirdparty.server.dao.CaptchaVerificationDao;
import hr.wozai.service.thirdparty.server.model.CaptchaVerification;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 16/7/8.
 */
public class CaptchaVerificationDaoTest extends BaseTest{

    @Autowired
    CaptchaVerificationDao captchaVerificationDao;

    @Test
    public void testAll() throws Exception {
        CaptchaVerification captchaVerification = new CaptchaVerification();
        Long now = System.currentTimeMillis();
        captchaVerification.setCreateTime(now);
        captchaVerification.setExpireTime(now + 10000);
        captchaVerification.setVerificationCode("110110110");

        int insertResult = captchaVerificationDao.insert(captchaVerification);
        Assert.assertEquals(1, insertResult);

        CaptchaVerification captchaVerification1 = captchaVerificationDao.findByCreateTimeAndVerificationCode(now, "110110110");
        Assert.assertEquals("110110110", captchaVerification1.getVerificationCode());

        captchaVerification.setVerificationCode("120120120");
        captchaVerificationDao.updateByCreateTimeAndVerificationCodeSelective(captchaVerification);
        Assert.assertEquals("120120120",captchaVerification.getVerificationCode());

    }

}