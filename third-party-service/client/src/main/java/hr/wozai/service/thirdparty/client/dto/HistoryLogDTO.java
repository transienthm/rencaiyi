package hr.wozai.service.thirdparty.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * Created by wangbin on 16/4/27.
 */
@ThriftStruct
public final class HistoryLogDTO extends BaseThriftObject{
    @JsonIgnore
    private ServiceStatusDTO serviceStatusDTO;

    private Long historyLogId;

    private Long orgId;

    private Long userId;

    private Long actorUserId;

    private Integer logType;

    private String content;

    private String preValue;

    private String curValue;

    private Long createdTime;

    private Long createdUserId;

    private Integer isDeleted;

    @ThriftField(1)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }
    @ThriftField(2)
    public Long getHistoryLogId() {
        return historyLogId;
    }
    @ThriftField(3)
    public Long getOrgId() {
        return orgId;
    }
    @ThriftField(4)
    public Long getUserId() {
        return userId;
    }

    @ThriftField(5)
    public Long getActorUserId() {
        return actorUserId;
    }
    @ThriftField(6)
    public Integer getLogType() {
        return logType;
    }
    @ThriftField(7)
    public String getContent() {
        return content;
    }
    @ThriftField(8)
    public String getPreValue() {
        return preValue;
    }
    @ThriftField(9)
    public String getCurValue() {
        return curValue;
    }
    @ThriftField(10)
    public Long getCreatedTime() {
        return createdTime;
    }
    @ThriftField(11)
    public Long getCreatedUserId() {
        return createdUserId;
    }
    @ThriftField(12)
    public Integer getIsDeleted() {
        return isDeleted;
    }
    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }
    @ThriftField
    public void setHistoryLogId(Long historyLogId) {
        this.historyLogId = historyLogId;
    }
    @ThriftField
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }
    @ThriftField
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    @ThriftField
    public void setActorUserId(Long actorUserId) {
        this.actorUserId = actorUserId;
    }
    @ThriftField
    public void setLogType(Integer logType) {
        this.logType = logType;
    }
    @ThriftField
    public void setContent(String content) {
        this.content = content;
    }
    @ThriftField
    public void setPreValue(String preValue) {
        this.preValue = preValue;
    }
    @ThriftField
    public void setCurValue(String curValue) {
        this.curValue = curValue;
    }
    @ThriftField
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }
    @ThriftField
    public void setCreatedUserId(Long createdUserId) {
        this.createdUserId = createdUserId;
    }
    @ThriftField
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @Override
    public String toString() {
        return "historyLogDTO:[ServiceStatusDTO:"
                +this.serviceStatusDTO+", HistoryLogId:"
                +historyLogId+", OrgId:"
                +orgId+", UserId:"
                +userId+", ActorUserId:"
                +actorUserId+", LogType:"
                +logType+", CreatedUserId:"
                +createdUserId+
                "]";
    }
}
