package hr.wozai.service.feed.server.service.Impl;

import hr.wozai.service.feed.server.constant.FeedThumbupEnum;
import hr.wozai.service.feed.server.dao.CommentDao;
import hr.wozai.service.feed.server.dao.RewardDao;
import hr.wozai.service.feed.server.dao.ThumbupDao;
import hr.wozai.service.feed.server.model.Comment;
import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.feed.server.model.Thumbup;
import hr.wozai.service.feed.server.service.RewardService;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangbin on 2016/11/17.
 */
@Service("rewardService")
public class RewardServiceImpl implements RewardService {

    @Autowired
    private RewardDao rewardDao;
/*
    @Override
    @LogAround
    public void createReward(List<Reward> rewards) {
         rewardDao.insertReward(rewards);
    }

    @Override
    @LogAround
    public void deleteRewardByFeedId(Reward reward) {
        rewardDao.deleteRewardByFeedId(reward);
    }*/

    @Override
    @LogAround
    public List<Reward> listRewardsByFeedIds(long orgId, List<Long> feedIds) {
        if (CollectionUtils.isEmpty(feedIds)) {
            return new ArrayList<>();
        }
        List<Reward> rewards = rewardDao.listRewardsByFeedIds(orgId, feedIds);
        if (CollectionUtils.isEmpty(rewards)) {
            rewards = Collections.EMPTY_LIST;
        }
        return rewards;
    }
/*    @Override
    @LogAround
    public void deleteReward(Reward reward){
        rewardDao.deleteReward(reward);
    }*/



/*    @Override
    @LogAround
    public Reward findReward(long orgId, long rewardId) {
        Reward reward = rewardDao.findReward(orgId, rewardId);
        if (null == reward) {
            throw new ServiceStatusException(ServiceStatus.FD_REWARD_NOT_FOUND);
        }
        return reward;
    }*/

    @Override
    @LogAround
    public List<Reward> listRewardOfOrg(long orgId) {
        List<Reward> rewards = rewardDao.listRewardOfOrg(orgId);
        return rewards;
    }

/*    @Override
    @LogAround
    public List<Reward> listRewardByRewardIds(long orgId, List<Long> rewardIds) {
        if (CollectionUtils.isEmpty(rewardIds)) {
            return Collections.EMPTY_LIST;
        }
        List<Reward> rewards = rewardDao.listRewardByRewardIds(orgId, rewardIds);
        return rewards;
    }

    @Override
    @LogAround
    public Reward findRewardByFeedId(long orgId, long feedId) {
        Reward reward = rewardDao.findReward(orgId, feedId);
        if (null == reward) {
            throw new ServiceStatusException(ServiceStatus.FD_REWARD_NOT_FOUND);
        }
        return reward;
    }*/

    @Override
    @LogAround
    public List<Reward> listRewardByRewardeeId(long orgId, long rewardeeId) {
        List<Reward> result = rewardDao.listRewardByRewardeeId(orgId, rewardeeId);
        if (CollectionUtils.isEmpty(result)) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    @Override
    @LogAround
    public List<Reward> listRewardByUserId(long orgId, long userId) {
        List<Reward> result = rewardDao.listRewardByUserId(orgId, userId);
        if (CollectionUtils.isEmpty(result)) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }
}
