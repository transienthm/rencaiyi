package hr.wozai.service.feed.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * Created by wangbin on 2016/11/23.
 */
@ThriftStruct
public final class RewardQuotaInfoDTO extends BaseThriftObject {
    private Integer personalQuota;

    private Integer usedPersonalQuota;

    private Integer teamQuota;

    private Integer usedTeamQuota;

    private ServiceStatusDTO serviceStatusDTO;

    @ThriftField(1)
    public Integer getPersonalQuota() {
        return personalQuota;
    }

    @ThriftField
    public void setPersonalQuota(Integer personalQuota) {
        this.personalQuota = personalQuota;
    }

    @ThriftField(2)
    public Integer getUsedPersonalQuota() {
        return usedPersonalQuota;
    }

    @ThriftField
    public void setUsedPersonalQuota(Integer usedPersonalQuota) {
        this.usedPersonalQuota = usedPersonalQuota;
    }

    @ThriftField(3)
    public Integer getTeamQuota() {
        return teamQuota;
    }

    @ThriftField
    public void setTeamQuota(Integer teamQuota) {
        this.teamQuota = teamQuota;
    }

    @ThriftField(4)
    public Integer getUsedTeamQuota() {
        return usedTeamQuota;
    }

    @ThriftField
    public void setUsedTeamQuota(Integer usedTeamQuota) {
        this.usedTeamQuota = usedTeamQuota;
    }

    @ThriftField(5)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }
}
