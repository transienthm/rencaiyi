package hr.wozai.service.thirdparty.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.thirdparty.client.facade.SmsFacade;
import hr.wozai.service.thirdparty.server.model.TrustedMobilePhone;
import hr.wozai.service.thirdparty.server.service.SmsService;
import hr.wozai.service.thirdparty.server.service.TrustedMobilePhoneService;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import hr.wozai.service.thirdparty.server.test.utils.AopTargetUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 16/7/1.
 */
public class SmsFacadeImplTest extends BaseTest {

    @Autowired
    SmsFacade smsFacade;

    @Mock
    SmsService spySmsService;

    @Mock
    TrustedMobilePhoneService spyTrustedMobilePhoneService;

    @Test
    public void testAll() {


        MockitoAnnotations.initMocks(this);
        try {
            ReflectionTestUtils.setField(AopTargetUtils.getTarget(smsFacade), "smsService", spySmsService);
            ReflectionTestUtils.setField(AopTargetUtils.getTarget(smsFacade), "trustedMobilePhoneService", spyTrustedMobilePhoneService);
        } catch (Exception e) {
            e.printStackTrace();
        }
        BooleanDTO booleanDTO = new BooleanDTO();
        booleanDTO.setData(true);
        booleanDTO.setServiceStatusDTO(new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg()));

        Mockito.when(spySmsService.sendSmsMessage(Mockito.anyString())).thenReturn(true);
        smsFacade.sendSmsMessage("10000000000");

        Mockito.when(spySmsService.verifySmsMessage(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        smsFacade.verifySmsMessage("10000000000", "10");

        Mockito.when(spySmsService.verifySmsMessage(Mockito.anyString(), Mockito.anyString())).thenReturn(true);
        smsFacade.verifySmsCodeOfTrustedMobilePhone("10000000000", "110");

        Mockito.doReturn(new TrustedMobilePhone()).when(spyTrustedMobilePhoneService).findTrustedMobilePhoneByMobilePhone(Mockito.anyString());
        smsFacade.sendSmsCodeToTrustedMobilePhone("10000000000");

        smsFacade.sendSmsMessage("110");

        Mockito.doThrow(new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR))
                .when(spySmsService).sendSmsMessage(Mockito.anyString());
        smsFacade.sendSmsMessage("10000000000");
        smsFacade.verifySmsCodeOfTrustedMobilePhone("10000000000", "110");
        smsFacade.sendSmsCodeToTrustedMobilePhone("10000000000");

        Mockito.doThrow(new ServiceStatusException(ServiceStatus.TP_EXPIRED_VERIFICATION))
                .when(spySmsService).verifySmsMessage(Mockito.anyString(), Mockito.anyString());
        smsFacade.verifySmsMessage("10000000000", "110");
        smsFacade.verifySmsCodeOfTrustedMobilePhone("10000000000", "110");

        Mockito.doThrow(new ServiceStatusException(ServiceStatus.TP_EXISTING_VERIFICATION))
                .when(spySmsService).verifySmsMessage(Mockito.anyString(), Mockito.anyString());
        smsFacade.verifySmsMessage("10000000000", "110");
        smsFacade.verifySmsCodeOfTrustedMobilePhone("10000000000", "110");

        Mockito.doThrow(new ServiceStatusException(ServiceStatus.TP_SMS_VERIFICATION_NOT_FOUND))
                .when(spySmsService).verifySmsMessage(Mockito.anyString(), Mockito.anyString());
        smsFacade.verifySmsMessage("10000000000", "110");
        smsFacade.verifySmsCodeOfTrustedMobilePhone("10000000000", "110");

        Mockito.doThrow(new ServiceStatusException(ServiceStatus.TP_MOBILE_NOT_FOUND))
                .when(spyTrustedMobilePhoneService).findTrustedMobilePhoneByMobilePhone(Mockito.anyString());
        smsFacade.sendSmsCodeToTrustedMobilePhone("10000000000");

    }


}
