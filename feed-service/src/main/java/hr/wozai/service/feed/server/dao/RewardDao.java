package hr.wozai.service.feed.server.dao;

import hr.wozai.service.feed.server.model.Feed;
import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.feed.server.model.RewardMedal;
import hr.wozai.service.servicecommons.commons.utils.TimeUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Created by wangbin on 2016/11/16.
 */
@Repository("rewardDao")
public class RewardDao {

    private final static String BASE_PACKAGE = "hr.wozai.service.feed.server.dao.RewardMapper.";

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @LogAround
    public void insertReward(List<Reward> rewards) {
        if (CollectionUtils.isEmpty(rewards)) {
            return;
        }
        sqlSessionTemplate.insert(BASE_PACKAGE + "insertReward", rewards);
    }

/*    @LogAround
    public Reward findReward(long orgId, long rewardId) {
        Map<String,Long> map = new HashMap<>();
        map.put("rewardId", rewardId);
        map.put("orgId", orgId);
        Reward reward = sqlSessionTemplate.selectOne(BASE_PACKAGE + "findReward", map);
        return reward;
    }*/

/*    public Reward findRewardByFeedId(long orgId, long feedId) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        map.put("feedId", feedId);
        Reward reward = sqlSessionTemplate.selectOne(BASE_PACKAGE + "findRewardByFeedId", map);
        return reward;
    }*/

    public List<Reward> listRewardsByFeedIds(long orgId, List<Long> feedIds) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        map.put("feedIds", feedIds);
        List<Reward> rewards = sqlSessionTemplate.selectList(BASE_PACKAGE + "listRewardByFeedIds", map);
        if (CollectionUtils.isEmpty(rewards)) {
            rewards = Collections.EMPTY_LIST;
        }
        return rewards;
    }

    @LogAround
    public List<Reward> listRewardOfOrg(long orgId) {
        Map<String, Object> map = new HashMap<>();
        map.put("orgId", orgId);
        List<Reward> rewards = sqlSessionTemplate.selectList(BASE_PACKAGE + "listRewardOfOrg", map);
        return rewards;
    }

    @LogAround
    public List<Reward> listRewardByUserId(long orgId, long userId) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        map.put("userId", userId);
        long now = System.currentTimeMillis();
        int year = TimeUtils.getYearFromTimestamp(now, TimeUtils.BEIJING);
        int month = TimeUtils.getMonthFromTimestamp(now, TimeUtils.BEIJING);
        long firstDayOfMonth = TimeUtils.getFirstDayOfMonth(year, month, TimeUtils.BEIJING);
        long lastDayOfMonth = TimeUtils.getLastDayOfMonth(year, month, TimeUtils.BEIJING);
        map.put("firstDayOfMonth", firstDayOfMonth);
        map.put("lastDayOfMonth", lastDayOfMonth);
        List<Reward> rewards = sqlSessionTemplate.selectList(BASE_PACKAGE + "listRewardByUserId", map);
        return rewards;
    }
/*    @LogAround
    public List<Reward> listRewardByRewardIds(long orgId, List<Long> rewardIds) {
        Map<String, Object> map = new HashMap<>();
        map.put("orgId", orgId);
        map.put("rewardIds", rewardIds);

        List<Reward> rewards = sqlSessionTemplate.selectList(BASE_PACKAGE + "listRewardByRewardIds", map);
        if (null == rewards) {
            rewards = Collections.emptyList();
        }
        return rewards;
    }*/

/*    @LogAround
    public int deleteReward(Reward reward){
        int result = sqlSessionTemplate.update(BASE_PACKAGE + "deleteReward", reward);
        return result;
    }*/

/*    @LogAround
    public int deleteRewardByFeedId(Reward reward) {
        int result = sqlSessionTemplate.update(BASE_PACKAGE + "deleteRewardByFeedId", reward);
        return result;
    }*/

    @LogAround
    public List<Reward> listRewardByRewardeeId(long orgId, long rewardeeId) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        map.put("rewardeeId", rewardeeId);

        List<Reward> rewards = sqlSessionTemplate.selectList(BASE_PACKAGE + "listRewardByRewardeeId", map);
        return rewards;
    }

}
