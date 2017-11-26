package hr.wozai.service.thirdparty.server.test.test.dao;

import hr.wozai.service.thirdparty.server.dao.TrustedMobilePhoneDao;
import hr.wozai.service.thirdparty.server.model.TrustedMobilePhone;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * Created by wangbin on 16/5/10.
 */
public class TrustedMobilePhoneDaoTest extends BaseTest {

    @Autowired
    TrustedMobilePhoneDao trustedMobilePhoneDao;

    @Test
    public void testTMPInsert() {
        TrustedMobilePhone trustedMobilePhone = new TrustedMobilePhone();
        trustedMobilePhone.setMobilePhone("110");
        trustedMobilePhone.setName("xijinping");
        trustedMobilePhone.setEmailAddress("xijinping@sqian.com");
        trustedMobilePhone.setIsDeleted(0);
        Long result = trustedMobilePhoneDao.insertTrustedMobilePhone(trustedMobilePhone);

        TrustedMobilePhone trustedMobilePhone1 = trustedMobilePhoneDao.findTrustedMobilePhoneByPrimaryKey(result);
        Assert.assertEquals("110", trustedMobilePhone1.getMobilePhone());

        trustedMobilePhone = trustedMobilePhoneDao.findTrustedMobilePhoneByMobilePhone("110");
        Assert.assertEquals("xijinping",trustedMobilePhone.getName());

        List<TrustedMobilePhone> listByName = trustedMobilePhoneDao.listTrustedMobilePhoneByName("xijinping");
        Assert.assertEquals(1,listByName.size());

        List<TrustedMobilePhone> listByEmail = trustedMobilePhoneDao.listTrustedMobilePhoneByEmail("xijinping@sqian.com");
        Assert.assertEquals(1,listByEmail.size());

   /*     ;*/

        trustedMobilePhone1.setName("wangbin");
        trustedMobilePhone1.setMobilePhone("120");
        trustedMobilePhone1.setEmailAddress("bill@sqian.com");
        int updateResult = trustedMobilePhoneDao.updateTrustedMobilePhone(trustedMobilePhone1);
        Assert.assertEquals(1,updateResult);

        int resultDel = trustedMobilePhoneDao.deleteTrustedMobilePhone(trustedMobilePhone1);
        Assert.assertEquals(1,resultDel);

        List<TrustedMobilePhone> trustedMobilePhones = trustedMobilePhoneDao.listTrustedMobilePhoneByName("109");
        Assert.assertEquals(Collections.EMPTY_LIST, trustedMobilePhones);
        trustedMobilePhones = trustedMobilePhoneDao.listTrustedMobilePhoneByEmail("qq.com");
        Assert.assertEquals(Collections.EMPTY_LIST, trustedMobilePhones);
    }
}
