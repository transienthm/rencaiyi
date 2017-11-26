package hr.wozai.service.thirdparty.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.thirdparty.client.dto.HistoryLogDTO;
import hr.wozai.service.thirdparty.client.dto.HistoryLogListDTO;

/**
 * Created by wangbin on 16/4/25.
 */
@ThriftService
public interface HistoryLogFacade {

    /**
     * 添加日志纪录
     * @param orgId 公司id
     * @param userId 日志涉及用户id
     * @param actorUserId 操作者id
     * @param adminUserId 管理员id(预留)
     * @param preValue 变更前的纪录
     * @param curValue 变更后的纪录
     * @param logType 日志类型(由HistoryLogTemplate说明)
     * @return
     */
    @ThriftMethod
    public LongDTO addHistoryLog(long orgId, long userId,long actorUserId, long adminUserId,String preValue,String curValue,Integer logType) ;

    /**
     * 通过日志id 得到一条日志
     * @param orgId 公司id
     * @param historyLogId 日志id
     * @param actorUserId 操作者id
     * @param adminUserId 管理员id
     * @return
     */
    @ThriftMethod
    public HistoryLogDTO getHistoryLog(long orgId, long historyLogId,long actorUserId,long adminUserId);

    /**
     * 得到某一操作人创建的所有日志,分页显示
     * @param orgId 公司id
     * @param actorUserId 操作人id
     * @param adminUserId 管理员id(预留)
     * @param pageNum 分页显示时当前页码
     * @param pageSize 分页显示时一页的最大显示条目
     * @return
     */
    @ThriftMethod
    public HistoryLogListDTO listHistoryLogByActorUserId(long orgId,long actorUserId,long adminUserId,int pageNum,int pageSize);

    /**
     * 得到当前用户相关的所有日志,分页显示
     * @param orgId 公司id
     * @param userId 当前用户id
     * @param adminUserId 管理员id(预留)
     * @param pageNum 分页显示时当前页码
     * @param pageSize 分页显示时一页的最大显示条目
     * @return
     */
    @ThriftMethod
    public HistoryLogListDTO listHistoryLogByUserId(long orgId,long userId,long adminUserId,int pageNum,int pageSize);

    /**
     * 删除某一条日志
     * @param orgId 公司id
     * @param historyLogId 日志id
     * @param actorUserId 操作者id
     * @param adminUserId 管理员id
     * @return
     */
    @ThriftMethod
    public VoidDTO deleteHistoryLog(
            long orgId, long historyLogId, long actorUserId,long adminUserId);


}
