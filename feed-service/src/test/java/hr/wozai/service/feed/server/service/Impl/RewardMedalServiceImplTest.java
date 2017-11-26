package hr.wozai.service.feed.server.service.Impl;

import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.feed.server.model.RewardMedal;
import hr.wozai.service.feed.server.service.RewardMedalService;
import hr.wozai.service.feed.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 2016/11/21.
 */
public class RewardMedalServiceImplTest extends TestBase {

    @Autowired
    private RewardMedalService rewardMedalService;

    @Test
    public void testAll() {

        long orgId = 999l;
        long userId = 22l;
        RewardMedal rewardMedal = new RewardMedal();
        rewardMedal.setMedalIcon("123");
        rewardMedal.setMedalName("骑士");
        rewardMedal.setDescription("高手");
        rewardMedal.setOrgId(orgId);
        rewardMedal.setCreatedUserId(userId);
        rewardMedal.setLastModifiedUserId(userId);
        rewardMedal.setMedalType(0);
        rewardMedal.setIsDeleted(0);

        List<RewardMedal> rewardMedals = new ArrayList<>();
        rewardMedals.add(rewardMedal);
        rewardMedal.setMedalName("勇士");
        rewardMedals.add(rewardMedal);

        rewardMedalService.batchCreateRewardMedal(orgId, rewardMedals);

        List<RewardMedal> listResult = rewardMedalService.listRewardMedal(orgId);
        Assert.assertEquals(2, listResult.size());

        rewardMedalService.batchUpdateRewardMedal(orgId, listResult);
        listResult = rewardMedalService.listRewardMedal(orgId);
        Assert.assertEquals(2, listResult.size());

        List<Long> rewardMedalIds = new ArrayList<>();
        for (RewardMedal rewardMedal1 : listResult) {
            rewardMedalIds.add(rewardMedal1.getRewardMedalId());
        }
        listResult = rewardMedalService.listRewardMedalByRewardMedalIds(orgId, rewardMedalIds);
        Assert.assertEquals(2, listResult.size());

        rewardMedalService.batchDeleteRewardMedal(orgId, listResult);
        listResult = rewardMedalService.listRewardMedal(orgId);
        Assert.assertEquals(0, listResult.size());

        listResult = rewardMedalService.listRewardMedalByRewardMedalIds(orgId, Arrays.asList(-1l));
        Assert.assertTrue(CollectionUtils.isEmpty(listResult));

        listResult = rewardMedalService.listRewardMedal(-1l);
        Assert.assertTrue(CollectionUtils.isEmpty(listResult));



        rewardMedalService.batchCreateRewardMedal(orgId, null);
        listResult = rewardMedalService.listRewardMedal(orgId);
        Assert.assertEquals(0, listResult.size());

        rewardMedalService.batchUpdateRewardMedal(orgId, null);
        listResult = rewardMedalService.listRewardMedal(orgId);
        Assert.assertEquals(0, listResult.size());

        rewardMedalService.batchDeleteRewardMedal(orgId, null);
        listResult = rewardMedalService.listRewardMedal(orgId);
        Assert.assertEquals(0, listResult.size());

    }
}