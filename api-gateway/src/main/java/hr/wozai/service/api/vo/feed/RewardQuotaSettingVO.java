package hr.wozai.service.api.vo.feed;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import hr.wozai.service.servicecommons.utils.codec.EncodeSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wangbin on 2016/11/17.
 */
@Data
@NoArgsConstructor
public class RewardQuotaSettingVO {
    @JsonSerialize(using = EncodeSerializer.class)
    private Long rewardQuotaSettingId;

    @JsonSerialize(using = EncodeSerializer.class)
    private Long orgId;

    private Integer personalQuota;

    private Integer teamQuota;
}
