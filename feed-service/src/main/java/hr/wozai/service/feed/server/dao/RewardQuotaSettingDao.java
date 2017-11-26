package hr.wozai.service.feed.server.dao;

import hr.wozai.service.feed.server.model.RewardQuotaSetting;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangbin on 2016/11/17.
 */
@Repository("rewardSettingDao")
public class RewardQuotaSettingDao {

    private final static String BASE_PACKAGE = "hr.wozai.service.feed.server.dao.RewardQuotaSettingMapper.";

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @LogAround
    public long insertRewardQuotaSetting(RewardQuotaSetting rewardQuotaSetting) {
        sqlSessionTemplate.insert(BASE_PACKAGE + "insertRewardQuotaSetting", rewardQuotaSetting);
        return rewardQuotaSetting.getRewardQuotaSettingId();
    }

    @LogAround
    public int updateRewardQuotaSetting(RewardQuotaSetting rewardQuotaSetting) {
        int result = sqlSessionTemplate.update(BASE_PACKAGE + "updateRewardQuotaSetting", rewardQuotaSetting);
        return result;
    }

    @LogAround
    public RewardQuotaSetting findRewardSetting(long orgId, long rewardSettingId) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        map.put("rewardQuotaSettingId", rewardSettingId);
        return sqlSessionTemplate.selectOne(BASE_PACKAGE + "findRewardQuotaSetting", map);
    }

    @LogAround
    public RewardQuotaSetting listRewardSettingByOrgId(long orgId) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        RewardQuotaSetting rewardQuotaSetting = sqlSessionTemplate.selectOne(BASE_PACKAGE + "listRewardQuotaSettingByOrgId", map);
        return rewardQuotaSetting;
    }

    @LogAround
    public int deleteRewardQuotaSetting(RewardQuotaSetting rewardQuotaSetting) {
        int result = sqlSessionTemplate.update(BASE_PACKAGE + "deleteRewardQuotaSetting", rewardQuotaSetting);
        return result;
    }
}
