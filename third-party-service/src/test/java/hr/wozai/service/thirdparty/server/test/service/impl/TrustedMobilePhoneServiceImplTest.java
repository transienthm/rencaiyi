package hr.wozai.service.thirdparty.server.test.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.server.dao.TrustedMobilePhoneDao;
import hr.wozai.service.thirdparty.server.model.TrustedMobilePhone;
import hr.wozai.service.thirdparty.server.service.TrustedMobilePhoneService;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import hr.wozai.service.thirdparty.server.test.utils.AopTargetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import javax.validation.constraints.AssertTrue;
import java.util.List;

/**
 * Created by wangbin on 16/5/11.
 */
public class TrustedMobilePhoneServiceImplTest extends BaseTest{

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    TrustedMobilePhoneService trustedMobilePhoneService;

    @Autowired
    TrustedMobilePhoneDao trustedMobilePhoneDao;

    @Mock
    TrustedMobilePhoneDao spyTrustedMobilePhoneDao;


    @Test
    public void testTrustedMobilePhoneService(){
        long result = trustedMobilePhoneService.insertTrustedMobilePhone("120", "xijinping", "xijinping@sqian.com",0);
/*        boolean flag = trustedMobilePhoneService.deleteTrustedMobilePhone("10000000000");
        Assert.assertEquals(true,flag);*/

        TrustedMobilePhone trustedMobilePhone = trustedMobilePhoneService.findTrustedMobilePhoneByPrimaryKey(result);

        Assert.assertEquals("xijinping",trustedMobilePhone.getName());

        trustedMobilePhone = trustedMobilePhoneService.findTrustedMobilePhoneByPrimaryKey(trustedMobilePhone.getTrustedMobilePhoneId());
        Assert.assertEquals("120", trustedMobilePhone.getMobilePhone());
        trustedMobilePhone = trustedMobilePhoneService.findTrustedMobilePhoneByMobilePhone("120");
        Assert.assertEquals("xijinping",trustedMobilePhone.getName());
        List<TrustedMobilePhone> trustedMobilePhones = trustedMobilePhoneService.listTrustedMobilePhoneByName(trustedMobilePhone.getName());
        Assert.assertEquals(1,trustedMobilePhones.size());
        trustedMobilePhones = trustedMobilePhoneService.listTrustedMobilePhoneByEmail(trustedMobilePhone.getEmailAddress());
        Assert.assertEquals(1,trustedMobilePhones.size());

        trustedMobilePhone.setName("likeqiang");
        boolean flag = trustedMobilePhoneService.updateTrustedMobilePhone(trustedMobilePhone);
        Assert.assertEquals(true,flag);
        boolean resultDel = trustedMobilePhoneService.deleteTrustedMobilePhone("120");
        Assert.assertTrue(resultDel);

    }

    @Test
    public void testNullExcepiton() throws Exception{
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(trustedMobilePhoneService), "trustedMobilePhoneDao", spyTrustedMobilePhoneDao);

        Mockito.doReturn(null).when(spyTrustedMobilePhoneDao).findTrustedMobilePhoneByMobilePhone(Mockito.matches("950"));



        thrown.expect(ServiceStatusException.class);
        TrustedMobilePhone trustedMobilePhone = trustedMobilePhoneService.findTrustedMobilePhoneByMobilePhone("950");

    }

    @Test
    public void testNullExcepiton1() throws Exception{

        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(trustedMobilePhoneService), "trustedMobilePhoneDao", spyTrustedMobilePhoneDao);

        Mockito.doReturn(null).when(spyTrustedMobilePhoneDao).listTrustedMobilePhoneByEmail(Mockito.matches("东方不败"));

        thrown.expect(ServiceStatusException.class);
        List<TrustedMobilePhone> trustedMobilePhones = trustedMobilePhoneService.listTrustedMobilePhoneByEmail("东方不败");

    }

    @Test
    public void testNullExcepiton2() throws Exception{

        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(trustedMobilePhoneService), "trustedMobilePhoneDao", spyTrustedMobilePhoneDao);

        Mockito.doReturn(null).when(spyTrustedMobilePhoneDao).listTrustedMobilePhoneByName(Mockito.matches("西方失败"));

        thrown.expect(ServiceStatusException.class);
        List<TrustedMobilePhone> trustedMobilePhones = trustedMobilePhoneService.listTrustedMobilePhoneByName("西方失败");

    }

    @Test
    public void testNullException3() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(trustedMobilePhoneService),"trustedMobilePhoneDao", spyTrustedMobilePhoneDao);

        Mockito.doReturn(0).when(spyTrustedMobilePhoneDao).deleteTrustedMobilePhone(Mockito.anyObject());
        trustedMobilePhoneService.deleteTrustedMobilePhone("10000000000");

        Mockito.doReturn(0).when(spyTrustedMobilePhoneDao).updateTrustedMobilePhone(Mockito.anyObject());
        trustedMobilePhoneService.updateTrustedMobilePhone(new TrustedMobilePhone());

        Mockito.doReturn(null).when(spyTrustedMobilePhoneDao).findTrustedMobilePhoneByPrimaryKey(Mockito.anyLong());
        thrown.expect(new ServiceStatusException(ServiceStatus.TP_MOBILE_NOT_FOUND).getClass());
        trustedMobilePhoneService.findTrustedMobilePhoneByPrimaryKey(111l);
    }
}
