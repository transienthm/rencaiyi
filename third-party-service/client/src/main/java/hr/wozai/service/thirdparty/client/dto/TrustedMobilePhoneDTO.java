package hr.wozai.service.thirdparty.client.dto;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftStruct;
import com.fasterxml.jackson.annotation.JsonIgnore;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.model.BaseThriftObject;

/**
 * Created by wangbin on 16/5/11.
 */
@ThriftStruct
public final class TrustedMobilePhoneDTO extends BaseThriftObject {

    @JsonIgnore
    private ServiceStatusDTO serviceStatusDTO;

    private long trustedMobilePhoneId;

    private String mobilePhone;

    private String name;

    private String emailAddress;

    @ThriftField(1)
    public ServiceStatusDTO getServiceStatusDTO() {
        return serviceStatusDTO;
    }

    @ThriftField(2)
    public long getTrustedMobilePhoneId() {
        return trustedMobilePhoneId;
    }

    @ThriftField(3)
    public String getMobilePhone() {
        return mobilePhone;
    }

    @ThriftField(4)
    public String getName() {
        return name;
    }

    @ThriftField(5)
    public String getEmailAddress() {
        return emailAddress;
    }

    @ThriftField
    public void setServiceStatusDTO(ServiceStatusDTO serviceStatusDTO) {
        this.serviceStatusDTO = serviceStatusDTO;
    }

    @ThriftField
    public void setTrustedMobilePhoneId(long trustedMobilePhoneId) {
        this.trustedMobilePhoneId = trustedMobilePhoneId;
    }

    @ThriftField
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @ThriftField
    public void setName(String name) {
        this.name = name;
    }

    @ThriftField
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
