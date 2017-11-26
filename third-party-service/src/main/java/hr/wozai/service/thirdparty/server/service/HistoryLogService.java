package hr.wozai.service.thirdparty.server.service;

import com.amazonaws.services.simpleworkflow.model.History;
import hr.wozai.service.thirdparty.server.model.HistoryLog;

import java.util.List;

/**
 * Created by wangbin on 16/4/25.
 */
public interface HistoryLogService {

    public long insertHistoryLog(HistoryLog historyLog);

    public void deleteHistoryLogByPrimaryKey(long orgId,long historyLogId);

    public void updateHistoryLog(HistoryLog historyLog,long actorUserId);

    public HistoryLog findHistoryLogByPrimaryKey(long orgId,long historyLogId);

    public List<HistoryLog> listHistoryLogByUserId(long orgId,long userId,int pageNum,int pageSize);

    public List<HistoryLog> listHistoryLogByActorUserId(long OrgId,long actorUserId,int pageNum,int pageSize);
}
