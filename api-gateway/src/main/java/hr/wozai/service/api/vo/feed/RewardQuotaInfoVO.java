package hr.wozai.service.api.vo.feed;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wangbin on 2016/11/23.
 */
@Data
@NoArgsConstructor
public class RewardQuotaInfoVO {
    private Integer personalQuota;

    private Integer usedPersonalQuota;

    private Integer teamQuota;

    private Integer usedTeamQuota;
}
