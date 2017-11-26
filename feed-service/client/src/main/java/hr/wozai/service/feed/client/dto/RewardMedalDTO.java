package hr.wozai.service.feed.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * Created by wangbin on 2016/11/17.
 */
@ThriftStruct
public final class RewardMedalDTO extends BaseThriftObject{

    private ServiceStatusDTO serviceStatusDTO;

    private Long rewardMedalId;

    private Long orgId;

    private Integer medalType;

    private String medalIcon;

    private String medalName;

    private String description;

    private Long createdUserId;

    private Long createdTime;

    private Long lastModifiedUserId;

    private Long lastModifiedTime;

    private Integer isDeleted;

    private Integer receivedCount;

    @ThriftField(1)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }

    @ThriftField(2)
    public Long getRewardMedalId() {
        return rewardMedalId;
    }

    @ThriftField
    public void setRewardMedalId(Long rewardMedalId) {
        this.rewardMedalId = rewardMedalId;
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
    public Integer getMedalType() {
        return medalType;
    }

    @ThriftField
    public void setMedalType(Integer medalType) {
        this.medalType = medalType;
    }

    @ThriftField(5)
    public String getMedalIcon() {
        return medalIcon;
    }

    @ThriftField
    public void setMedalIcon(String medalIcon) {
        this.medalIcon = medalIcon;
    }

    @ThriftField(6)
    public String getDescription() {
        return description;
    }

    @ThriftField
    public void setDescription(String description) {
        this.description = description;
    }

    @ThriftField(7)
    public Long getCreatedUserId() {
        return createdUserId;
    }

    @ThriftField
    public void setCreatedUserId(Long createdUserId) {
        this.createdUserId = createdUserId;
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
    public String getMedalName() {
        return medalName;
    }

    @ThriftField
    public void setMedalName(String medalName) {
        this.medalName = medalName;
    }

    @ThriftField(13)
    public Integer getReceivedCount() {
        return receivedCount;
    }

    @ThriftField
    public void setReceivedCount(Integer receivedCount) {
        this.receivedCount = receivedCount;
    }
}

