package hr.wozai.service.feed.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * Created by wangbin on 2016/11/21.
 */
@ThriftStruct
public final class RewardDTO extends BaseThriftObject {

    private ServiceStatusDTO serviceStatusDTO;

    private Long rewardId;

    private Long orgId;

    private Long userId;

    private Long feedId;

    private Integer rewardType;

    private List<Long> rewardeeIds;

    private Long rewardMedalId;

    private Long createdTime;

    private Long lastModifiedUserId;

    private Long lastModifiedTime;

    private Integer isDeleted;

    @ThriftField(1)
    public Long getRewardId() {
        return rewardId;
    }

    @ThriftField
    public void setRewardId(Long rewardId) {
        this.rewardId = rewardId;
    }

    @ThriftField(2)
    public Long getOrgId() {
        return orgId;
    }

    @ThriftField
    public void setOrgId(Long orgId) {
        this.orgId = orgId;
    }

    @ThriftField(3)
    public Long getUserId() {
        return userId;
    }

    @ThriftField
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @ThriftField(4)
    public Long getFeedId() {
        return feedId;
    }

    @ThriftField
    public void setFeedId(Long feedId) {
        this.feedId = feedId;
    }

    @ThriftField(5)
    public Integer getRewardType() {
        return rewardType;
    }

    @ThriftField
    public void setRewardType(Integer rewardType) {
        this.rewardType = rewardType;
    }

    @ThriftField(6)
    public List<Long> getRewardeeIds() {
        return rewardeeIds;
    }

    @ThriftField
    public void setRewardeeIds(List<Long> rewardeeIds) {
        this.rewardeeIds = rewardeeIds;
    }

    @ThriftField(7)
    public Long getRewardMedalId() {
        return rewardMedalId;
    }

    @ThriftField
    public void setRewardMedalId(Long rewardMedalId) {
        this.rewardMedalId = rewardMedalId;
    }

    @ThriftField(8)
    public Long getCreatedTime() {
        return createdTime;
    }

    @ThriftField
    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    @ThriftField(9)
    public Long getLastModifiedUserId() {
        return lastModifiedUserId;
    }

    @ThriftField
    public void setLastModifiedUserId(Long lastModifiedUserId) {
        this.lastModifiedUserId = lastModifiedUserId;
    }

    @ThriftField(10)
    public Long getLastModifiedTime() {
        return lastModifiedTime;
    }

    @ThriftField
    public void setLastModifiedTime(Long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    @ThriftField(11)
    public Integer getIsDeleted() {
        return isDeleted;
    }

    @ThriftField
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    @ThriftField(12)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }
}
