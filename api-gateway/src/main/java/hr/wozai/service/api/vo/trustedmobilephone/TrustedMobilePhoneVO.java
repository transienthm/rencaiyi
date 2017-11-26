package hr.wozai.service.api.vo.trustedmobilephone;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wangbin on 16/5/11.
 */
@Data
@NoArgsConstructor
public class TrustedMobilePhoneVO {
    @JsonSerialize(using = EncodeSerializer.class)
    @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
    private Long trustedMobilePhoneId;

    private String mobilePhone;

    private String name;

    private String emailAddress;

    private boolean isTrusted;
}
