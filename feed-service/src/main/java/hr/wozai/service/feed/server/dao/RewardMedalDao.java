package hr.wozai.service.feed.server.dao;

import hr.wozai.service.feed.server.model.RewardMedal;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Repository("rewardMedalDao")
public class RewardMedalDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(RewardQuotaSettingDao.class);

    private final static String BASE_PACKAGE = "hr.wozai.service.feed.server.dao.RewardMedalMapper.";

    @Autowired
    SqlSessionTemplate sqlSessionTemplate;

/*    @LogAround
    public long insertRewardMedal(RewardMedal rewardMedal) {
        sqlSessionTemplate.insert(BASE_PACKAGE + "insertRewardMedal", rewardMedal);
        return rewardMedal.getRewardMedalId();
    }*/

    @LogAround
    public int batchInsertRewardMedal(List<RewardMedal> rewardMedals) {
        return sqlSessionTemplate.insert(BASE_PACKAGE + "batchInsertRewardMedal", rewardMedals);
    }

    @LogAround
    public void batchDeleteRewardMedal(List<RewardMedal> rewardMedals){
        sqlSessionTemplate.update(BASE_PACKAGE + "batchDeleteRewardMedal", rewardMedals);
    }

    @LogAround
    public void batchUpdateRewardMedal(List<RewardMedal> rewardMedals){
        sqlSessionTemplate.update(BASE_PACKAGE + "batchUpdateRewardMedal", rewardMedals);
    }


/*
    @LogAround
    public RewardMedal findRewardMedal(long orgId, long rewardMedalId) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        map.put("rewardMedalId", rewardMedalId);
        RewardMedal rewardMedal = sqlSessionTemplate.selectOne(BASE_PACKAGE + "findRewardMedal", map);
        return rewardMedal;
    }

    @LogAround
    public int updateRewardMedal(RewardMedal rewardMedal) {
        return sqlSessionTemplate.update(BASE_PACKAGE + "updateRewardMedal", rewardMedal);
    }

    @LogAround
    public int deleteRewardMedal(long orgId, long rewardMedalId) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        map.put("rewardMedalId", rewardMedalId);
        return sqlSessionTemplate.update(BASE_PACKAGE + "deleteRewardMedal", map);
    }
*/

/*    @LogAround
    public void deleteRewardMedalByOrgId(long orgId) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        sqlSessionTemplate.update(BASE_PACKAGE + "deleteRewardMedalByOrgId", map);
    }*/

    @LogAround
    public List<RewardMedal> listRewardMedalByOrgId(long orgId) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        List<RewardMedal> result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listRewardMedalByOrgId", map);
        if (CollectionUtils.isEmpty(result)) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    @LogAround
    public List<RewardMedal> listRewardMedalByRewardMedalIds(long orgId, List<Long> rewardMedalIds) {
        Map map = new HashMap();
        map.put("orgId", orgId);
        map.put("rewardMedalIds", rewardMedalIds);
        List<RewardMedal> result = sqlSessionTemplate.selectList(BASE_PACKAGE + "listRewardMedalByRewardMedalIds", map);
        if (CollectionUtils.isEmpty(result)) {
            result = Collections.EMPTY_LIST;
        }

        return result;
    }
}
