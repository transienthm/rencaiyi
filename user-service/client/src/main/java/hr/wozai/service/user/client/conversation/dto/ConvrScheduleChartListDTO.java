package hr.wozai.service.user.client.conversation.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.facebook.swift.service.ThriftMethod;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;

import java.util.List;

/**
 * Created by wangbin on 2016/12/6.
 */
@ThriftStruct
public final class ConvrScheduleChartListDTO {
    private ServiceStatusDTO serviceStatusDTO;

    private List<ConvrScheduleChartDTO> convrScheduleChartDTOList;

    @ThriftField(1)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }

    @ThriftField(2)
    public List<ConvrScheduleChartDTO> getConvrScheduleChartDTOList() {
        return convrScheduleChartDTOList;
    }

    @ThriftField
    public void setConvrScheduleChartDTOList(List<ConvrScheduleChartDTO> convrScheduleChartDTOList) {
        this.convrScheduleChartDTOList = convrScheduleChartDTOList;
    }
}
