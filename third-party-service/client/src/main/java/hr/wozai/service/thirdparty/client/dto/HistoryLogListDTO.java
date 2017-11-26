package hr.wozai.service.thirdparty.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * Created by wangbin on 16/4/27.
 */
@ThriftStruct
public final class HistoryLogListDTO extends BaseThriftObject{
    @JsonIgnore
    private ServiceStatusDTO serviceStatusDTO;

    private List<HistoryLogDTO> historyLogDTOs;

    @ThriftField(1)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }
    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }
    @ThriftField(2)
    public List<HistoryLogDTO> getHistoryLogDTOs() {
        return historyLogDTOs;
    }
    @ThriftField
    public void setHistoryLogDTOs(List<HistoryLogDTO> historyLogDTOs) {
        this.historyLogDTOs = historyLogDTOs;
    }

    @Override
    public String toString() {
        return "HistoryLogListDTO{" +
                "serviceStatusDTO=" + serviceStatusDTO +
                ", historyLogDTOs=" + historyLogDTOs +
                '}';
    }
}
