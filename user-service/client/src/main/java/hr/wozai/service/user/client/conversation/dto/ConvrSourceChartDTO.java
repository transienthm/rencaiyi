package hr.wozai.service.user.client.conversation.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;

/**
 * Created by wangbin on 2016/12/8.
 */
@ThriftStruct
public final class ConvrSourceChartDTO {

    private ServiceStatusDTO serviceStatusDTO;

    private CoreUserProfileDTO sourceUser;

    private Integer convrTimesInThisMonth;

    private Integer convrTimesInThisQuarter;

    private Integer totalCount;

    private String lastDate;

    @ThriftField(1)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }

    @ThriftField(2)
    public CoreUserProfileDTO getSourceUser() {
        return sourceUser;
    }

    @ThriftField
    public void setSourceUser(CoreUserProfileDTO sourceUser) {
        this.sourceUser = sourceUser;
    }

    @ThriftField(3)
    public Integer getConvrTimesInThisMonth() {
        return convrTimesInThisMonth;
    }

    @ThriftField
    public void setConvrTimesInThisMonth(Integer convrTimesInThisMonth) {
        this.convrTimesInThisMonth = convrTimesInThisMonth;
    }

    @ThriftField(4)
    public Integer getConvrTimesInThisQuarter() {
        return convrTimesInThisQuarter;
    }

    @ThriftField
    public void setConvrTimesInThisQuarter(Integer convrTimesInThisQuarter) {
        this.convrTimesInThisQuarter = convrTimesInThisQuarter;
    }

    @ThriftField(5)
    public Integer getTotalCount() {
        return totalCount;
    }

    @ThriftField
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    @ThriftField(6)
    public String getLastDate() {
        return lastDate;
    }

    @ThriftField
    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }
}
