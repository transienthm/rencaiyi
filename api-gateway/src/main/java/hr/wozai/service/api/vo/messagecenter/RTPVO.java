package hr.wozai.service.api.vo.messagecenter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wangbin on 16/6/9.
 */
@Data
@NoArgsConstructor
public class RTPVO {
    private String userName;
    private String date;
}
