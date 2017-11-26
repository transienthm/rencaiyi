package hr.wozai.service.thirdparty.server.test.service.impl;

import com.auth0.jwt.internal.com.fasterxml.jackson.annotation.JsonTypeInfo;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.server.service.HistoryLogService;
import hr.wozai.service.thirdparty.server.model.HistoryLog;
import hr.wozai.service.thirdparty.server.service.HistoryLogService;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.util.List;

/**
 * Created by transienthm on 16/4/26.
 */
public class HistoryLogServiceTest extends BaseTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    HistoryLogService historyLogService;

    @Test(expected = ServiceStatusException.class)
    public void testALl() {
        long orgId = 13L;
        long userId = 13L;
        long actorUserId = 13l;
        int actionType = 3;
        long createdTime = 10L;
        int isDeleted = 0;
//        HistoryLogServiceImpl hls = new HistoryLogServiceImpl();
        HistoryLog hl = new HistoryLog();
        hl.setOrgId(orgId);
        hl.setUserId(userId);
        hl.setActorUserId(actorUserId);
        hl.setLogType(actionType);
        hl.setCreatedTime(createdTime);
        hl.setIsDeleted(isDeleted);
        long id = historyLogService.insertHistoryLog(hl);
        HistoryLog historyLog = historyLogService.findHistoryLogByPrimaryKey(orgId, id);
        Assert.assertEquals(3, historyLog.getLogType().intValue());

        historyLog.setCurValue("更新了");
        historyLogService.updateHistoryLog(historyLog, userId);
        Assert.assertEquals("更新了", historyLog.getCurValue());

        List<HistoryLog> historyLogs = historyLogService.listHistoryLogByUserId(orgId, userId, 1, 20);
        Assert.assertEquals(1, historyLogs.size());

        historyLogs = historyLogService.listHistoryLogByActorUserId(orgId, actorUserId, 1, 20);
        Assert.assertEquals(1, historyLogs.size());

        historyLogService.deleteHistoryLogByPrimaryKey(orgId, id);

        HistoryLog historyLog1 = historyLogService.findHistoryLogByPrimaryKey(orgId, id);
        Assert.assertNull(historyLog1);
    }

    @Test
    public void testDeleteException() {
        thrown.expect(ServiceStatusException.class);
        historyLogService.deleteHistoryLogByPrimaryKey(15l, 15l);
    }

    @Test
    public void testFindException() {
        thrown.expect(ServiceStatusException.class);
        historyLogService.findHistoryLogByPrimaryKey(15l, 15l);
    }

    @Test
    public void testListByUserId() {
        thrown.expect(ServiceStatusException.class);
        historyLogService.listHistoryLogByActorUserId(15l, 15l, 1, 20);
    }

    @Test
    public void testListByActorUserId() {
        thrown.expect(ServiceStatusException.class);
        historyLogService.listHistoryLogByUserId(15l, 15l, 1, 20);
    }
}
