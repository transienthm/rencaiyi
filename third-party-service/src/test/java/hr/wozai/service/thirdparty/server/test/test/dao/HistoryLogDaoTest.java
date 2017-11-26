package hr.wozai.service.thirdparty.server.test.test.dao;

import hr.wozai.service.thirdparty.server.dao.HistoryLogDao;
import hr.wozai.service.thirdparty.server.model.HistoryLog;
import hr.wozai.service.thirdparty.server.test.test.base.BaseTest;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

/**
 * Created by wangbin on 16/4/26.
 */
public class HistoryLogDaoTest extends BaseTest {

    @Autowired
    HistoryLogDao historyLogDao;

    @Test
    public void testAll() {
//        long historyLogId = 115L;
        long orgId = 99L;
        long userId = 67L;
        long actorUserId = 61l;
        int actionType = 1;
        long createdUserId = 60l;
        String content = "新增成员";
        String preValue = "无";
        String curValue = "无";
        long lastModifiedUserId = 58l;
        int isDeleted = 0;
        HistoryLog hl = new HistoryLog();
//        hl.setHistoryLogId(historyLogId);
        hl.setOrgId(orgId);
        hl.setUserId(userId);
        hl.setActorUserId(actorUserId);
        hl.setLogType(actionType);
        hl.setCreatedUserId(createdUserId);
        hl.setContent(content);
        hl.setPreValue(preValue);
        hl.setCurValue(curValue);
        hl.setIsDeleted(isDeleted);
        long result =  historyLogDao.insertHistoryLog(hl);
        HistoryLog historyLog = historyLogDao.selectHistoryLogByPrimaryKey(orgId,result);
        Assert.assertEquals(67l,historyLog.getUserId().longValue());
        historyLog.setCurValue("更新了");
        historyLogDao.updateHistoryLogByPrimaryKey(historyLog);
        HistoryLog historyLog1 = historyLogDao.selectHistoryLogByPrimaryKey(orgId,result);
        Assert.assertEquals("更新了",historyLog1.getCurValue());


        List<HistoryLog> historyLogs = historyLogDao.selectHistoryLogsByUserId(orgId,67l,1,20);
        Assert.assertEquals(1, historyLogs.size());
        historyLogs = historyLogDao.selectHistoryLogsByActorUserId(orgId,61l,1,20);
        Assert.assertEquals(1,historyLogs.size());

        historyLogDao.deleteHistoryLogById(orgId,result);
        historyLog1 = historyLogDao.selectHistoryLogByPrimaryKey(orgId,result);
        Assert.assertNull(historyLog1);

        List<HistoryLog> historyLogList = historyLogDao.selectHistoryLogsByActorUserId(orgId, 100l, 1, 20);
        Assert.assertEquals(Collections.EMPTY_LIST, historyLogList);
        historyLogList = historyLogDao.selectHistoryLogsByUserId(orgId, 100l, 1, 20);
        Assert.assertEquals(Collections.EMPTY_LIST, historyLogList);

    }


}