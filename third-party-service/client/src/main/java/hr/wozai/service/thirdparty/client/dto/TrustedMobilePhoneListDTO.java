package hr.wozai.service.thirdparty.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

import java.util.List;

/**
 * Created by wangbin on 16/5/11.
 */
@ThriftStruct
public final class TrustedMobilePhoneListDTO extends BaseThriftObject{

    @JsonIgnore
    ServiceStatusDTO serviceStatusDTO;

    private List<TrustedMobilePhoneDTO> trustedMobilePhoneDTOs;

    @ThriftField(1)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField(2)
    public List<TrustedMobilePhoneDTO> getTrustedMobilePhoneDTOs() {
        return trustedMobilePhoneDTOs;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }

    @ThriftField
    public void setTrustedMobilePhoneDTOs(List<TrustedMobilePhoneDTO> trustedMobilePhoneDTOs) {
        this.trustedMobilePhoneDTOs = trustedMobilePhoneDTOs;
    }
}
