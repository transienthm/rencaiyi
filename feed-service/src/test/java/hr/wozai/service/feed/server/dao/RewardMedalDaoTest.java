package hr.wozai.service.feed.server.dao;

import hr.wozai.service.feed.server.model.RewardMedal;
import hr.wozai.service.feed.server.test.base.TestBase;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wangbin on 2016/11/17.
 */
public class RewardMedalDaoTest extends TestBase {

    private final static Logger LOGGER = LoggerFactory.getLogger(RewardMedalDaoTest.class);

    @Autowired
    private RewardMedalDao rewardMedalDao;

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

        List<RewardMedal> rewardMedalList = rewardMedalDao.listRewardMedalByOrgId(orgId);
        Assert.assertEquals(0, rewardMedalList.size());

        rewardMedalDao.batchInsertRewardMedal(rewardMedals);
        rewardMedalList = rewardMedalDao.listRewardMedalByOrgId(orgId);



        Assert.assertEquals(2, rewardMedalList.size());

        rewardMedal = rewardMedalList.get(0);
        rewardMedal.setMedalName("update");
        rewardMedal.setMedalType(1);
        rewardMedal.setDescription("test");
        rewardMedalList.set(0, rewardMedal);

        rewardMedalDao.batchUpdateRewardMedal(rewardMedalList);

        rewardMedalList = rewardMedalDao.listRewardMedalByOrgId(orgId);
        Assert.assertEquals(2, rewardMedalList.size());

        rewardMedals = rewardMedalDao.listRewardMedalByRewardMedalIds(orgId, Arrays.asList(rewardMedalList.get(0).getRewardMedalId()));
        Assert.assertEquals(1, rewardMedals.size());

        rewardMedals = rewardMedalDao.listRewardMedalByRewardMedalIds(orgId, Arrays.asList(-1l));
        Assert.assertEquals(0, rewardMedals.size());

        rewardMedalDao.batchDeleteRewardMedal(rewardMedalList);
        rewardMedalList = rewardMedalDao.listRewardMedalByOrgId(orgId);
        Assert.assertEquals(0, rewardMedalList.size());
    }
}