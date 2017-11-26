package hr.wozai.service.user.client.conversation.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;

import java.util.List;

/**
 * Created by wangbin on 2016/12/8.
 */
@ThriftStruct
public final class ConvrSourceChartListDTO {

    private ServiceStatusDTO serviceStatusDTO;

    private List<ConvrSourceChartDTO> convrSourceChartDTOs;

    private Long totalNumber;

    @ThriftField(1)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }

    @ThriftField(2)
    public List<ConvrSourceChartDTO> getConvrSourceChartDTOs() {
        return convrSourceChartDTOs;
    }

    @ThriftField
    public void setConvrSourceChartDTOs(List<ConvrSourceChartDTO> convrSourceChartDTOs) {
        this.convrSourceChartDTOs = convrSourceChartDTOs;
    }

    @ThriftField(3)
    public Long getTotalNumber() {
        return totalNumber;
    }

    @ThriftField
    public void setTotalNumber(Long totalNumber) {
        this.totalNumber = totalNumber;
    }
}
