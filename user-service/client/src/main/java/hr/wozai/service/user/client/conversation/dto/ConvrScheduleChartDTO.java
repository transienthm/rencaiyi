package hr.wozai.service.user.client.conversation.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;

/**
 * Created by wangbin on 2016/12/6.
 */
@ThriftStruct
public final class ConvrScheduleChartDTO {

    private float planedTimes;

    private float actualTimes;

    private String date;

    private ServiceStatusDTO serviceStatusDTO;

    @ThriftField(1)
    public float getPlanedTimes() {
        return planedTimes;
    }

    @ThriftField
    public void setPlanedTimes(float planedTimes) {
        this.planedTimes = planedTimes;
    }

    @ThriftField(2)
    public float getActualTimes() {
        return actualTimes;
    }

    @ThriftField
    public void setActualTimes(float actualTimes) {
        this.actualTimes = actualTimes;
    }

    @ThriftField(3)
    public String getDate() {
        return date;
    }

    @ThriftField
    public void setDate(String date) {
        this.date = date;
    }

    @ThriftField(4)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }

}
