package hr.wozai.service.feed.server.test.service;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.feed.server.model.Feed;
import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.feed.server.service.IFeedService;
import hr.wozai.service.feed.server.service.Impl.RewardServiceImpl;
import hr.wozai.service.feed.server.service.RewardService;
import hr.wozai.service.feed.server.test.base.TestBase;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by wangbin on 2016/11/17.
 */
public class RewardServiceImplTest extends TestBase {

    @Autowired
    private IFeedService feedService;

    private final static Logger LOGGER = LoggerFactory.getLogger(RewardServiceImpl.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private RewardService rewardService;


    private long orgId = 100;

    private long userId = 66;
    private long userIdNew = 67;

    private long ALL_COMPANY = 0;
    private long teamId = 24;
    private long teamIdNew = 25;

    private int pageNumber = 1;
    private int pageSize = 5;

    private String feedContent = "My first feed";

    private String commentContent = "My first comment";
    private String commentContent2 = "My second comment";

    @Test
    public void testAll() {

        long orgId = 999L;
        long userId = 5;
        Long rewardeeId = 4l;
        List<Long> rewardeeIds = new ArrayList<>();
        rewardeeIds.add(rewardeeId);

        Reward reward = new Reward();
        reward.setOrgId(orgId);
        reward.setUserId(userId);
        reward.setFeedId(110l);

        reward.setRewardType(0);
        reward.setRewardMedalId(0l);
        reward.setRewardeeId(rewardeeIds.get(0));

        reward.setLastModifiedUserId(userId);
        long currentTime = System.currentTimeMillis();
        reward.setCreatedTime(currentTime);
        reward.setExtend(new JSONObject());
        reward.setLastModifiedTime(currentTime);
        reward.setIsDeleted(0);

        Feed feed = new Feed();
        feed.setOrgId(orgId);
        feed.setUserId(userId);
        feed.setTeamId(teamId);
        feed.setContent(feedContent);
        feed.setLastModifiedUserId(userId);


        long feedId = feedService.createFeedAndReward(feed, Arrays.asList(reward));

        List<Reward> rewards = rewardService.listRewardByUserId(orgId, userId);
        Assert.assertEquals(1, rewards.size());

        rewards = rewardService.listRewardsByFeedIds(orgId, Arrays.asList(feedId));
        Assert.assertEquals(1, rewards.size());

        rewards = rewardService.listRewardByRewardeeId(orgId, rewardeeId);
        Assert.assertEquals(1, rewards.size());

        rewards = rewardService.listRewardOfOrg(orgId);
        Assert.assertEquals(1, rewards.size());

        feedId = feedService.createFeedAndReward(feed, new ArrayList<>());
        Assert.assertEquals(-1, feedId);

        rewards = rewardService.listRewardsByFeedIds(orgId, null);
        Assert.assertTrue(CollectionUtils.isEmpty(rewards));

        rewards = rewardService.listRewardsByFeedIds(orgId, Arrays.asList(-1l));
        Assert.assertTrue(CollectionUtils.isEmpty(rewards));

        rewards = rewardService.listRewardByRewardeeId(orgId, -1);
        Assert.assertTrue(CollectionUtils.isEmpty(rewards));

        rewards = rewardService.listRewardByUserId(orgId, -1);
        Assert.assertTrue(CollectionUtils.isEmpty(rewards));


        /*Long rewardId = rewardService.createReward(reward);
        LOGGER.info("rewardId=" + rewardId);
        Assert.assertEquals(rewardId, reward.getRewardId());

        Reward reward1 = rewardService.findReward(orgId, rewardId);
        System.out.println(reward1);
*/
        List<Reward> list;
/*
        list = rewardService.listRewardOfOrg(orgId, pageNumber, pageSize);
        System.out.println(list);
        LOGGER.info(list.get(0).toString());
        Assert.assertEquals(list.size(), 1);

        list = rewardService.listRewardByRewardIds(orgId, Arrays.asList(rewardId));
        System.out.println(list);
        LOGGER.info(list.get(0).toString());
        Assert.assertEquals(list.size(), 1);

        list = rewardService.listRewardByRewardIds(orgId, new ArrayList<>());
        assertEquals(Collections.EMPTY_LIST,list);



        thrown.expect(ServiceStatusException.class);
        rewardService.findReward(orgId, rewardId + 1);*/

    }

}