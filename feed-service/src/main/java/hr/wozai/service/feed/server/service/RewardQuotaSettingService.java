package hr.wozai.service.feed.server.service;

import hr.wozai.service.feed.server.model.RewardQuotaSetting;

/**
 * Created by wangbin on 2016/11/17.
 */
public interface RewardQuotaSettingService {

    public long createRewardSetting(RewardQuotaSetting rewardQuotaSetting);

    public void updateRewardQuota(RewardQuotaSetting rewardQuotaSetting);

//    public RewardQuotaSetting findRewardSetting(long orgId, long rewardSettingId);

    public RewardQuotaSetting getRewardSettingByOrgId(long orgId);

//    public void deleteRewardQuota(RewardQuotaSetting rewardQuotaSetting);
}
