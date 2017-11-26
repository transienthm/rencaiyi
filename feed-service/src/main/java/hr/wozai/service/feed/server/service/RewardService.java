package hr.wozai.service.feed.server.service;

import hr.wozai.service.feed.server.model.Comment;
import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.feed.server.model.Thumbup;

import java.util.List;
/**
 * Created by wangbin on 2016/11/16.
 */
public interface RewardService {

//    public void createReward(List<Reward> rewards);

//    public void deleteReward(Reward reward);

//    public void deleteRewardByFeedId(Reward reward);

//    public Reward findReward(long orgId, long rewardId);

//    public Reward findRewardByFeedId(long orgId, long feedId);

    public List<Reward> listRewardsByFeedIds(long orgId, List<Long> feedIds);

    public List<Reward> listRewardOfOrg(long orgId);

    public List<Reward> listRewardByRewardeeId(long orgId, long rewardeeId);

    public List<Reward> listRewardByUserId(long orgId, long userId);

  /*  public List<Reward> listRewardByRewardIds(long orgId, List<Long> rewardIds);*/

}
