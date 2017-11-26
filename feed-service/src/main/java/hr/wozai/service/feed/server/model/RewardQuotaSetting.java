package hr.wozai.service.feed.server.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by wangbin on 2016/11/17.
 */
@Data
@NoArgsConstructor
public class RewardQuotaSetting {

    private Long rewardQuotaSettingId;

    private Long orgId;

    private Integer personalQuota;

    private Integer teamQuota;

    private Long createdUserId;

    private Long createdTime;

    private Long lastModifiedUserId;

    private Long lastModifiedTime;

    private Integer isDeleted;
}
