package hr.wozai.service.api.vo.feed;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.utils.codec.DecodeDeserializer;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wangbin on 2016/11/18.
 */
@Data
@NoArgsConstructor
public class RewardMedalVO {

    @JsonSerialize(using = EncodeSerializer.class)
    @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
    private Long rewardMedalId;

    @JsonSerialize(using = EncodeSerializer.class)
    @JsonDeserialize(using = DecodeDeserializer.LongDeserializer.class)
    private Long orgId;

    private Integer medalType;

    private Integer isDeletable;

    private String medalIcon;

    private String medalName;

    private String description;

    private Integer receivedCount;

}
