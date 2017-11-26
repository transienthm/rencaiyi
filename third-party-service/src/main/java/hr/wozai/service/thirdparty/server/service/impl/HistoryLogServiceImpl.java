package hr.wozai.service.thirdparty.server.service.impl;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.thirdparty.server.dao.HistoryLogDao;
import hr.wozai.service.thirdparty.server.model.HistoryLog;
import hr.wozai.service.thirdparty.server.service.HistoryLogService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangbin on 16/4/25.
 */
@Service("historyLogService")
public class HistoryLogServiceImpl implements HistoryLogService {

    @Autowired
    HistoryLogDao historyLogDao;

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public long insertHistoryLog(HistoryLog historyLog) {
        return historyLogDao.insertHistoryLog(historyLog);
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager" , rollbackFor = Exception.class)
    public void deleteHistoryLogByPrimaryKey(long orgId,long historyLogId) {
        HistoryLog historyLog = findHistoryLogByPrimaryKey(orgId,historyLogId);
        if(null == historyLog){
            throw new ServiceStatusException(ServiceStatus.TP_HISTORY_LOG_NOT_FOUND);
        }
        historyLogDao.deleteHistoryLogById(orgId,historyLogId);
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager" , rollbackFor = Exception.class)
    public void updateHistoryLog(HistoryLog historyLog,long actorUserId) {
        historyLogDao.updateHistoryLogByPrimaryKey(historyLog);
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager" , rollbackFor = Exception.class)
    public HistoryLog findHistoryLogByPrimaryKey(long orgId,long historyLogId){
        HistoryLog historyLog = historyLogDao.selectHistoryLogByPrimaryKey(orgId,historyLogId);
        if(null == historyLog){
            throw new ServiceStatusException(ServiceStatus.TP_HISTORY_LOG_NOT_FOUND);
        }
        return historyLog;
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager" , rollbackFor = Exception.class)
    public List<HistoryLog> listHistoryLogByUserId(long orgId,long userId,int pageNum,int pageSize) {
        List<HistoryLog> historyLogs = new ArrayList();
        historyLogs = historyLogDao.selectHistoryLogsByUserId(orgId,userId,pageNum,pageSize);
        if (CollectionUtils.isEmpty(historyLogs)) {
            throw new ServiceStatusException(ServiceStatus.TP_HISTORY_LOG_NOT_FOUND);
        }
        return historyLogs;
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager" , rollbackFor = Exception.class)
    public List<HistoryLog> listHistoryLogByActorUserId(long orgId,long actorUserId,int pageNum,int pageSize) {
        List<HistoryLog> historyLogs = new ArrayList<>();
        historyLogs = historyLogDao.selectHistoryLogsByActorUserId(orgId,actorUserId,pageNum,pageSize);
        if(historyLogs.isEmpty()){
            throw new ServiceStatusException(ServiceStatus.TP_HISTORY_LOG_NOT_FOUND);
        }
        return historyLogs;
    }
}
