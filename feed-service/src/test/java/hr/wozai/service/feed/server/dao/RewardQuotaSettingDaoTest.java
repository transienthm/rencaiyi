package hr.wozai.service.feed.server.dao;

import hr.wozai.service.feed.server.model.RewardQuotaSetting;
import hr.wozai.service.feed.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by wangbin on 2016/11/17.
 */
public class RewardQuotaSettingDaoTest extends TestBase {

    private final static Logger LOGGER = LoggerFactory.getLogger(RewardQuotaSettingDao.class);

    @Autowired
    private RewardQuotaSettingDao rewardQuotaSettingDao;

    @Test
    public void testAll() {

        long orgId = 222L;
        long userId = 5;
        Integer personalQuota = 21;
        Integer teamQuota = 6;


        RewardQuotaSetting rewardQuotaSetting = new RewardQuotaSetting();
        rewardQuotaSetting.setOrgId(orgId);
        rewardQuotaSetting.setCreatedUserId(userId);
        rewardQuotaSetting.setLastModifiedUserId(userId);

        long now = System.currentTimeMillis();
        rewardQuotaSetting.setCreatedTime(now);
        rewardQuotaSetting.setLastModifiedTime(now);

        rewardQuotaSetting.setPersonalQuota(personalQuota);
        rewardQuotaSetting.setTeamQuota(teamQuota);
        rewardQuotaSetting.setIsDeleted(0);

        Long rewardSettingId = rewardQuotaSettingDao.insertRewardQuotaSetting(rewardQuotaSetting);
        Assert.assertEquals(rewardSettingId, rewardQuotaSetting.getRewardQuotaSettingId());

        int result;
        rewardQuotaSetting.setPersonalQuota(personalQuota + 1);
        rewardQuotaSetting.setTeamQuota(teamQuota + 1);
        result = rewardQuotaSettingDao.updateRewardQuotaSetting(rewardQuotaSetting);
        Assert.assertEquals(1, result);

        System.out.println(rewardSettingId);

        RewardQuotaSetting rewardQuotaSetting1 = rewardQuotaSettingDao.findRewardSetting(orgId, rewardSettingId);

        Assert.assertEquals(rewardQuotaSetting1.getPersonalQuota(), new Integer(personalQuota + 1));
        Assert.assertEquals(rewardQuotaSetting1.getTeamQuota(), new Integer(teamQuota + 1));

        result = rewardQuotaSettingDao.deleteRewardQuotaSetting(rewardQuotaSetting1);
        Assert.assertEquals(1, result);

        rewardQuotaSetting1 = rewardQuotaSettingDao.listRewardSettingByOrgId(orgId);
        Assert.assertNull(rewardQuotaSetting1);

    }

}