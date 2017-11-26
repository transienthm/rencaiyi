package hr.wozai.service.feed.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * Created by wangbin on 2016/11/17.
 */
@ThriftStruct
public final class RewardQuotaSettingDTO extends BaseThriftObject {

    private ServiceStatusDTO serviceStatusDTO;

    private Long rewardQuotaSettingId;

    private Long orgId;

    private Integer personalQuota;

    private Integer teamQuota;

    private Long createdUserId;

    private Long createdTime;

    private Long lastModifiedUserId;

    private Long lastModifiedTime;

    private Integer isDeleted;

    @ThriftField(1)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }

    @ThriftField(2)
    public Long getRewardQuotaSettingId() {
        return rewardQuotaSettingId;
    }

    @ThriftField
    public void setRewardQuotaSettingId(Long rewardQuotaSettingId) {
        this.rewardQuotaSettingId = rewardQuotaSettingId;
    }

    @ThriftField(3)
    public Long getOrgId() {
        return orgId;
    }

    @ThriftField
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    @ThriftField(4)
    public Integer getPersonalQuota() {
        return personalQuota;
    }

    @ThriftField
    public void setPersonalQuota(Integer personalQuota) {
        this.personalQuota = personalQuota;
    }

    @ThriftField(5)
    public Integer getTeamQuota() {
        return teamQuota;
    }

    @ThriftField
    public void setTeamQuota(Integer teamQuota) {
        this.teamQuota = teamQuota;
    }

    @ThriftField(6)
    public Long getCreatedUserId() {
        return createdUserId;
    }

    @ThriftField
    public void setCreatedUserId(Long createdUserId) {
        this.createdUserId = createdUserId;
    }

    @ThriftField(7)
    public Long getCreatedTime() {
        return createdTime;
    }

    @ThriftField
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    @ThriftField(8)
    public Long getLastModifiedUserId() {
        return lastModifiedUserId;
    }

    @ThriftField
    public void setLastModifiedUserId(Long lastModifiedUserId) {
        this.lastModifiedUserId = lastModifiedUserId;
    }

    @ThriftField(9)
    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    @ThriftField
    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @ThriftField(10)
    public Integer getIsDeleted() {
        return isDeleted;
    }

    @ThriftField
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
