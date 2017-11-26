package hr.wozai.service.thirdparty.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wangbin on 16/5/10.
 */
@Data
@NoArgsConstructor
public class TrustedMobilePhone {

    private long trustedMobilePhoneId;

    private String mobilePhone;

    private String name;

    private String emailAddress;

    private Integer isDeleted;

    @Override
    public String toString() {
        return "TrustedMobilePhone{" +
                "trustedMobilePhoneId=" + trustedMobilePhoneId +
                ", mobilePhone='" + mobilePhone + '\'' +
                ", name='" + name + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                '}';
    }
}
