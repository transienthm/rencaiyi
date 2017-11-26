package hr.wozai.service.feed.server.service.Impl;

import hr.wozai.service.feed.client.enums.RewardMedalTemplate;
import hr.wozai.service.feed.server.dao.RewardQuotaSettingDao;
import hr.wozai.service.feed.server.model.RewardQuotaSetting;
import hr.wozai.service.feed.server.service.RewardQuotaSettingService;
import hr.wozai.service.feed.server.utils.RewardHelper;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by wangbin on 2016/11/17.
 */
@Service("rewardSettingService")
public class RewardQuotaSettingServiceImpl implements RewardQuotaSettingService {

    private final int REWARD_DEFAULT_TEAM_QUOTA = 5;
    private final int REWARD_DEFAULT_PERSONAL_QUOTA = 20;

    @Autowired
    RewardQuotaSettingDao rewardQuotaSettingDao;

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public long createRewardSetting(RewardQuotaSetting rewardQuotaSetting) {
        RewardHelper.checkRewardQuotaParam(rewardQuotaSetting);

        return rewardQuotaSettingDao.insertRewardQuotaSetting(rewardQuotaSetting);
    }

    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateRewardQuota(RewardQuotaSetting rewardQuotaSetting){
        RewardHelper.checkRewardQuotaParam(rewardQuotaSetting);
//        RewardQuotaSetting oldSetting = rewardQuotaSettingDao.listRewardSettingByOrgId(rewardQuotaSetting.getOrgId());
//        rewardQuotaSettingDao.deleteRewardQuotaSetting(oldSetting);
        rewardQuotaSettingDao.updateRewardQuotaSetting(rewardQuotaSetting);
    }

/*    @Override
    public RewardQuotaSetting findRewardSetting(long orgId, long rewardSettingId) {
        RewardQuotaSetting rewardQuotaSetting = rewardQuotaSettingDao.findRewardSetting(orgId, rewardSettingId);
        if (null == rewardQuotaSetting) {
            throw new ServiceStatusException(ServiceStatus.FD_REWARD_SETTING_NOT_FOUND);
        }
        return rewardQuotaSetting;
    }*/

    @Override
    public RewardQuotaSetting getRewardSettingByOrgId(long orgId) {
        RewardQuotaSetting rewardQuotaSetting = rewardQuotaSettingDao.listRewardSettingByOrgId(orgId);
        if (null == rewardQuotaSetting) {
            rewardQuotaSetting = new RewardQuotaSetting();
            rewardQuotaSetting.setTeamQuota(REWARD_DEFAULT_TEAM_QUOTA);
            rewardQuotaSetting.setPersonalQuota(REWARD_DEFAULT_PERSONAL_QUOTA);
        }
        return rewardQuotaSetting;
    }

  /*  @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void deleteRewardQuota(RewardQuotaSetting rewardQuotaSetting) {
        rewardQuotaSettingDao.deleteRewardQuotaSetting(rewardQuotaSetting);
    }*/
}
