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
public final class RewardListDTO extends BaseThriftObject {
    private ServiceStatusDTO serviceStatusDTO;

    private List<RewardDTO> rewardDTOList;

    @ThriftField(1)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }

    @ThriftField(2)
    public List<RewardDTO> getRewardDTOList() {
        return rewardDTOList;
    }

    @ThriftField
    public void setRewardDTOList(List<RewardDTO> rewardDTOList) {
        this.rewardDTOList = rewardDTOList;
    }
}
