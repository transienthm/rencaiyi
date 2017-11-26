package hr.wozai.service.thirdparty.server.test.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.thirdparty.client.facade.HistoryLogFacade;
import hr.wozai.service.thirdparty.server.dao.HistoryLogDao;
import hr.wozai.service.thirdparty.server.model.HistoryLog;
import hr.wozai.service.thirdparty.server.service.HistoryLogService;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.thirdparty.client.dto.HistoryLogDTO;
import hr.wozai.service.thirdparty.client.dto.HistoryLogListDTO;
import hr.wozai.service.thirdparty.server.test.utils.AopTargetUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by wangbin on 16/4/27.
 */
//@RunWith(MockitoJUnitRunner.class)
public class HistoryLogFacadeImplTest extends BaseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    HistoryLogFacade historyLogFacade;

    @Autowired
    HistoryLogService historyLogService;

    @Mock
    HistoryLogService spyHistoryLogService;

    @Test
    public void testAll() throws Exception{
        LongDTO id = historyLogFacade.addHistoryLog(10l,10l,10l,10l,"你好","我也好",1);
        HistoryLogDTO historyLogDTO = historyLogFacade.getHistoryLog(10l,id.getData(),10l,10l);
        Assert.assertEquals("你好",historyLogDTO.getPreValue());

        HistoryLogListDTO historyLogListDTO = historyLogFacade.listHistoryLogByActorUserId(10l,10l,10l,1,20);
        Assert.assertEquals(1,historyLogListDTO.getHistoryLogDTOs().size());

        historyLogListDTO = historyLogFacade.listHistoryLogByUserId(10l,10l,10l,1,20);
        Assert.assertEquals(1,historyLogListDTO.getHistoryLogDTOs().size());

        historyLogFacade.deleteHistoryLog(10l,id.getData(),5l,2l);
        historyLogDTO = historyLogFacade.getHistoryLog(10l,id.getData(),10l,10l);
        Assert.assertEquals("未找到历史日志",historyLogDTO.getServiceStatusDTO().getMsg());


        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(AopTargetUtils.getTarget(historyLogFacade), "historyLogService", spyHistoryLogService, HistoryLogService.class);
        Mockito.when(spyHistoryLogService.listHistoryLogByActorUserId(
                Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt())).thenThrow(new ServiceStatusException(ServiceStatus.TP_HISTORY_LOG_NOT_FOUND));
        Mockito.when(spyHistoryLogService.listHistoryLogByUserId(Mockito.anyLong(), Mockito.anyLong(),
                Mockito.anyInt(), Mockito.anyInt())).thenThrow(new ServiceStatusException(ServiceStatus.TP_HISTORY_LOG_NOT_FOUND));
        Mockito.doThrow(new ServiceStatusException(ServiceStatus.TP_HISTORY_LOG_NOT_FOUND)).when(spyHistoryLogService).deleteHistoryLogByPrimaryKey(Mockito.anyLong(), Mockito.anyLong());

        VoidDTO voidDTO = new VoidDTO();
        HistoryLogListDTO historyLogListDTO1 = new HistoryLogListDTO();
        HistoryLogListDTO historyLogListDTO2 = new HistoryLogListDTO();
        LongDTO resultDTO = new LongDTO();

        try {
            historyLogListDTO1 = historyLogFacade.listHistoryLogByActorUserId(10l, 10l, 10l, 1, 20);
            historyLogListDTO2 = historyLogFacade.listHistoryLogByUserId(10l, 10l, 10l, 1, 20);
            voidDTO = historyLogFacade.deleteHistoryLog(10l, 10l, 10l, 10l);
        } catch (ServiceStatusException e) {

        }
        Assert.assertEquals(Collections.EMPTY_LIST, historyLogListDTO1.getHistoryLogDTOs());
        Assert.assertEquals(Collections.EMPTY_LIST, historyLogListDTO2.getHistoryLogDTOs());
        Assert.assertEquals(ServiceStatus.TP_HISTORY_LOG_NOT_FOUND.getCode(), voidDTO.getServiceStatusDTO().getCode());

    }
}
