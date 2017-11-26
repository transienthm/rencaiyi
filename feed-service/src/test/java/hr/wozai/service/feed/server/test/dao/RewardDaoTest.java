package hr.wozai.service.feed.server.test.dao;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.feed.server.dao.FeedDao;
import hr.wozai.service.feed.server.dao.RewardDao;
import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.feed.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 2016/11/16.
 */
public class RewardDaoTest extends TestBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(RewardDaoTest.class);

    @Autowired
    RewardDao rewardDao;

    @Test
    public void testAll() throws Exception {

        long orgId = 222L;
        long userId = 5;
        long rewardeeId = 4l;
        long feedId = 5l;
        List<Long> rewardeeIds = new ArrayList<>();
        rewardeeIds.add(rewardeeId);

        Reward reward = new Reward();
        reward.setOrgId(orgId);
        reward.setUserId(userId);
        reward.setFeedId(feedId);

        reward.setRewardType(0);
        reward.setRewardMedalId(0l);
        reward.setRewardeeId(rewardeeId);

        reward.setLastModifiedUserId(userId);
        long currentTime = System.currentTimeMillis();
        reward.setCreatedTime(currentTime);
        reward.setExtend(new JSONObject());
        reward.setLastModifiedTime(currentTime);
        reward.setIsDeleted(0);

        //insertReward
        rewardDao.insertReward(Arrays.asList(reward));
        List<Reward> rewards = rewardDao.listRewardOfOrg(orgId);
        Assert.assertEquals(1, rewards.size());

        rewardDao.insertReward(new ArrayList<>());
        rewards = rewardDao.listRewardOfOrg(orgId);
        Assert.assertEquals(1, rewards.size());

        //listByUserId
        rewards = rewardDao.listRewardByUserId(orgId, userId);
        Assert.assertEquals(1, rewards.size());

        //listByFeedId
        rewards = rewardDao.listRewardsByFeedIds(orgId, Arrays.asList(5l));
        Assert.assertEquals(1, rewards.size());

        //listByRewardeeId
        rewards = rewardDao.listRewardByRewardeeId(orgId, rewardeeId);
        Assert.assertEquals(1, rewards.size());

        rewards = rewardDao.listRewardsByFeedIds(orgId, Arrays.asList(feedId + 1));
        Assert.assertEquals(0, rewards.size());

    }

}