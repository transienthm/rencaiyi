package hr.wozai.service.thirdparty.server.dao;


import hr.wozai.service.thirdparty.server.model.HistoryLog;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangbin on 16/4/25.
 */
@Repository("historyLogDao")
public class HistoryLogDao {
    private static final String BASE_PACKAGE = "hr.wozai.service.thirdparty.server.dao.HistoryLogMapper";
    private static final String INSERT = BASE_PACKAGE + "." + "insert";
    private static final String DELETE = BASE_PACKAGE + "." + "delete";
    private static final String UPDATE = BASE_PACKAGE + "." + "updateByPrimaryKey";
    private static final String SELECTBYPK = BASE_PACKAGE +"."+"selectByPrimaryKey";
    private static final String SELECTBYUSERID = BASE_PACKAGE+"."+"listHistoryLogByUserId";
    private static final String SELECTBYACTORUSERID = BASE_PACKAGE+"."+"listHistoryLogByActorUserId";

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

    public long insertHistoryLog(HistoryLog historyLog) {
        sqlSessionTemplate.insert(INSERT, historyLog);
        return historyLog.getHistoryLogId();
    }

    public int deleteHistoryLogById(long orgId,long histotyLogId) {
        Map<String,Object> params = new HashMap<>();
        params.put("orgId",orgId);
        params.put("historyLogId",histotyLogId);
        int result = sqlSessionTemplate.update(DELETE, params);
        return result;
    }

    public int updateHistoryLogByPrimaryKey(HistoryLog historyLog) {
        return sqlSessionTemplate.update(UPDATE, historyLog);

    }

    public HistoryLog selectHistoryLogByPrimaryKey(long orgId,long historyLogId){
        Map<String,Object> params = new HashMap<>();
        params.put("historyLogId",historyLogId);
        params.put("orgId",orgId);
        return sqlSessionTemplate.selectOne(SELECTBYPK,params);
    }

    public List<HistoryLog> selectHistoryLogsByUserId(long orgId,long userId,int pageNum,int pageSize){
        Map<String , Object> params = new HashMap<>();
        params.put("orgId",orgId);
        params.put("userId",userId);
        int pageStart = (pageNum - 1) * pageSize;
        params.put("pageStart",pageStart);
        params.put("pageSize",pageSize);
        List<HistoryLog> historyLogList =  sqlSessionTemplate.selectList(SELECTBYUSERID,params);
        if(CollectionUtils.isEmpty(historyLogList)){
            historyLogList = Collections.emptyList();
        }
        return historyLogList;
    }
    public List<HistoryLog> selectHistoryLogsByActorUserId(long orgId,long actorUserId,int pageNum,int pageSize){
        Map<String,Object> params = new HashMap<>();
        params.put("orgId",orgId);
        params.put("actorUserId",actorUserId);
        int pageStart = (pageNum - 1) * pageSize;
        params.put("pageStart",pageStart);
        params.put("pageSize",pageSize);
        List<HistoryLog> historyLogList =  sqlSessionTemplate.selectList(SELECTBYACTORUSERID,params);
        if (CollectionUtils.isEmpty(historyLogList)){
            historyLogList=Collections.emptyList();
        }
        return historyLogList;
    }
}
