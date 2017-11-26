package hr.wozai.service.feed.server.service.Impl;

import hr.wozai.service.feed.client.enums.RewardMedalTemplate;
import hr.wozai.service.feed.server.model.RewardQuotaSetting;
import hr.wozai.service.feed.server.service.RewardQuotaSettingService;
import hr.wozai.service.feed.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 2016/11/25.
 */
public class RewardQuotaSettingServiceImplTest extends TestBase {

    @Autowired
    private RewardQuotaSettingService rewardQuotaSettingService;

    @Test
    public void testAll() {
        long orgId = 999l;
        long userId = 9l;
        long now = System.currentTimeMillis();

        RewardQuotaSetting rewardQuotaSetting = new RewardQuotaSetting();
        rewardQuotaSetting.setTeamQuota(4);
        rewardQuotaSetting.setPersonalQuota(5);
        rewardQuotaSetting.setIsDeleted(0);
        rewardQuotaSetting.setOrgId(orgId);
        rewardQuotaSetting.setCreatedTime(now);
        rewardQuotaSetting.setCreatedUserId(userId);
        rewardQuotaSetting.setLastModifiedUserId(userId);

        RewardQuotaSetting rewardQuotaSetting1 = rewardQuotaSettingService.getRewardSettingByOrgId(orgId);
        Assert.assertEquals(5, rewardQuotaSetting1.getTeamQuota().toString());

        Long id = rewardQuotaSettingService.createRewardSetting(rewardQuotaSetting);
        rewardQuotaSetting = rewardQuotaSettingService.getRewardSettingByOrgId(orgId);
        Assert.assertEquals(id, rewardQuotaSetting.getRewardQuotaSettingId());

        rewardQuotaSetting.setTeamQuota(9);
        rewardQuotaSettingService.updateRewardQuota(rewardQuotaSetting);
        rewardQuotaSetting1 = rewardQuotaSettingService.getRewardSettingByOrgId(orgId);
        Assert.assertEquals(new Integer(9), rewardQuotaSetting1.getTeamQuota());
    }
}