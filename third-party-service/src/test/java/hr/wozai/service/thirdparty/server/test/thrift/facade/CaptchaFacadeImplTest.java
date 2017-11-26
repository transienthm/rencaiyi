package hr.wozai.service.thirdparty.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.thirdparty.client.facade.CaptchaFacade;
import hr.wozai.service.thirdparty.server.service.CaptchaService;
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

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 16/7/11.
 */
public class CaptchaFacadeImplTest extends BaseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    CaptchaFacade captchaFacade;

    @Mock
    CaptchaService spyCaptchaService;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(captchaFacade), "captchaService", spyCaptchaService);

    }

    @Test
    public void getCaptcha() throws Exception {
        captchaFacade.getCaptcha();


        Mockito.doReturn(true).when(spyCaptchaService).verifyCaptcha(Mockito.anyLong(), Mockito.anyString());
        BooleanDTO result = captchaFacade.verifyCaptcha(110l, "110");
        Assert.assertTrue(result.getData());


    }

    @Test
    public void testException1() throws Exception {
        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR)).when(spyCaptchaService).getCaptcha();

        captchaFacade.getCaptcha();

    }

    @Test
    public void testException2() throws Exception {

        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR)).when(spyCaptchaService).verifyCaptcha(
                Mockito.anyLong(), Mockito.anyString()
        );

        captchaFacade.verifyCaptcha(110l, "110");
    }

}