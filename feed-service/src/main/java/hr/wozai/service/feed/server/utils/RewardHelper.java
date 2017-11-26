package hr.wozai.service.feed.server.utils;

import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.feed.server.model.RewardMedal;
import hr.wozai.service.feed.server.model.RewardQuotaSetting;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;

import java.util.List;

/**
 * Created by wangbin on 2016/11/21.
 */
public class RewardHelper {
    public static void checkRewardMedalParam(RewardMedal rewardMedal) {
        if (null == rewardMedal ||
                null == rewardMedal.getOrgId() ||
                null == rewardMedal.getMedalIcon() ||
                null == rewardMedal.getMedalType() ||
                null == rewardMedal.getMedalName() ||
                null == rewardMedal.getDescription())
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    public static void checkRewardMedalListParam(List<RewardMedal> rewardMedals) {
        for (RewardMedal rewardMedal : rewardMedals) {
            checkRewardMedalParam(rewardMedal);
        }
    }

    public static void checkRewardMedalUpdateParam(RewardMedal rewardMedal) {
        if (null == rewardMedal
                || null == rewardMedal.getOrgId()
                || null == rewardMedal.getRewardMedalId() ||
                (
                null == rewardMedal.getMedalIcon() &&
                        null == rewardMedal.getMedalName() &&
                        null == rewardMedal.getDescription() &&
                        null == rewardMedal.getDescription())) {
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }
    }

    public static void checkRewardMedalDeleteParam(RewardMedal rewardMedal) {
        if (null == rewardMedal
                || null == rewardMedal.getOrgId()
                || null == rewardMedal.getRewardMedalId()) {
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }
    }

    public static void checkRewardMedalDeleteListParam(List<RewardMedal> rewardMedals) {
        for (RewardMedal rewardMedal : rewardMedals) {
            checkRewardMedalDeleteParam(rewardMedal);
        }
    }

    public static void checkRewardMedalUpdateListParam(List<RewardMedal> rewardMedals) {
        for (RewardMedal rewardMedal : rewardMedals) {
            checkRewardMedalUpdateParam(rewardMedal);
        }
    }

    public static void checkRewardQuotaParam(RewardQuotaSetting rewardQuotaSetting) {
        if (null == rewardQuotaSetting ||
                null == rewardQuotaSetting.getOrgId() ||
                null == rewardQuotaSetting.getPersonalQuota() ||
                null == rewardQuotaSetting.getTeamQuota())
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }


    public static void checkRewardParam(Reward reward) {
        if (null == reward ||
                null == reward.getOrgId() ||
                null == reward.getFeedId() ||
                null == reward.getRewardeeId()) {
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }
    }

}
