package hr.wozai.service.feed.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;
import java.util.Map;

/**
 * Created by wangbin on 2016/11/18.
 */
@ThriftStruct
public final class RewardMedalListDTO extends BaseThriftObject {
    private ServiceStatusDTO serviceStatusDTO;

    private List<RewardMedalDTO> rewardMedalDTOList;

    @ThriftField(1)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }

    @ThriftField(2)
    public List<RewardMedalDTO> getRewardMedalDTOList() {
        return rewardMedalDTOList;
    }

    @ThriftField
    public void setRewardMedalDTOList(List<RewardMedalDTO> rewardMedalDTOList) {
        this.rewardMedalDTOList = rewardMedalDTOList;
    }
}
