package hr.wozai.service.feed.server.service;

import hr.wozai.service.feed.server.model.RewardMedal;

import java.util.List;
import java.util.Map;

/**
 * Created by wangbin on 2016/11/17.
 */
public interface RewardMedalService {

//    public long createRewardMedal(RewardMedal rewardMedal);

    public void batchCreateRewardMedal(long orgId, List<RewardMedal> rewardMedals);

    public void batchDeleteRewardMedal(long orgId, List<RewardMedal> rewardMedals);

    public void batchUpdateRewardMedal(long orgId, List<RewardMedal> rewardMedals);

//    public int updateRewardMedal(RewardMedal rewardMedal);

//    public RewardMedal findRewardMedal(long orgId, long rewardMedalId);

    public List<RewardMedal> listRewardMedal(long orgId);

    public List<RewardMedal> listRewardMedalByRewardMedalIds(long orgId, List<Long> rewardMedalIds);

//    public void deleteRewardMedal(long orgId, long rewardMedalId);
}
