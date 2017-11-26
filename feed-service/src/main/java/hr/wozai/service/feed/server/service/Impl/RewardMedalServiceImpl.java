package hr.wozai.service.feed.server.service.Impl;

import hr.wozai.service.feed.server.dao.RewardMedalDao;
import hr.wozai.service.feed.server.model.RewardMedal;
import hr.wozai.service.feed.server.service.RewardMedalService;
import hr.wozai.service.feed.server.utils.RewardHelper;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by wangbin on 2016/11/17.
 */
@Service("rewardMedalService")
public class RewardMedalServiceImpl implements RewardMedalService {

    @Autowired
    RewardMedalDao rewardMedalDao;

/*    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public long createRewardMedal(RewardMedal rewardMedal) {
        RewardHelper.checkRewardMedalParam(rewardMedal);
        return rewardMedalDao.insertRewardMedal(rewardMedal);
    }*/

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void batchCreateRewardMedal(long orgId, List<RewardMedal> rewardMedals) {
        if (CollectionUtils.isEmpty(rewardMedals)) {
            return;
        }
        RewardHelper.checkRewardMedalListParam(rewardMedals);
        rewardMedalDao.batchInsertRewardMedal(rewardMedals);
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void batchDeleteRewardMedal(long orgId, List<RewardMedal> rewardMedals) {
        if (CollectionUtils.isEmpty(rewardMedals)) {
            return;
        }
        RewardHelper.checkRewardMedalDeleteListParam(rewardMedals);
        rewardMedalDao.batchDeleteRewardMedal(rewardMedals);
    }

    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void batchUpdateRewardMedal(long orgId, List<RewardMedal> rewardMedals) {
        if (CollectionUtils.isEmpty(rewardMedals)) {
            return;
        }
        RewardHelper.checkRewardMedalUpdateListParam(rewardMedals);
        rewardMedalDao.batchUpdateRewardMedal(rewardMedals);
    }

/*    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public int updateRewardMedal(RewardMedal rewardMedal) {
        RewardHelper.checkRewardMedalParam(rewardMedal);

        return rewardMedalDao.updateRewardMedal(rewardMedal);
    }

    @Override
    @LogAround
    public RewardMedal findRewardMedal(long orgId, long rewardMedalId) {
        RewardMedal rewardMedal = rewardMedalDao.findRewardMedal(orgId, rewardMedalId);
        if (null == rewardMedal) {
            throw new ServiceStatusException(ServiceStatus.FD_REWARD_MEDAL_NOT_FOUND);
        }
        return rewardMedal;
    }*/

/*    @Override
    public List<RewardMedal> listRewardMedalByOrgId(long orgId) {
        List<RewardMedal> result = rewardMedalDao.listRewardMedalByOrgId(orgId);
        if (null == result) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

    @Override
    public List<RewardMedal> listRewardMedalByOrgId(long orgId) {
        List<RewardMedal> result = rewardMedalDao.listRewardMedalByOrgId(orgId);
        if (null == result) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }*/

    @Override
    @LogAround
    public List<RewardMedal> listRewardMedal(long orgId) {
        List<RewardMedal> result = rewardMedalDao.listRewardMedalByOrgId(orgId);
        if (CollectionUtils.isEmpty(result)) {
            result = Collections.EMPTY_LIST;
        }
        return result;
    }

/*    @Override
    @LogAround
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void deleteRewardMedal(long orgId, long rewardMedalId) {
        rewardMedalDao.deleteRewardMedal(orgId, rewardMedalId);
    }*/

    @Override
    @LogAround
    public List<RewardMedal> listRewardMedalByRewardMedalIds(long orgId, List<Long> rewardMedalIds) {
        if (CollectionUtils.isEmpty(rewardMedalIds)) {
            return new ArrayList<>();
        }
        List<RewardMedal> rewardMedals = rewardMedalDao.listRewardMedalByRewardMedalIds(orgId, rewardMedalIds);
        return rewardMedals;
    }
}
