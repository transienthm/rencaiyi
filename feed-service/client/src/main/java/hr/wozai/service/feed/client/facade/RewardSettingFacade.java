/*
package hr.wozai.service.feed.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.feed.client.dto.RewardMedalSettingDTO;
import hr.wozai.service.feed.client.dto.RewardMedalSettingListDTO;
import hr.wozai.service.feed.client.dto.RewardQuotaInfoDTO;
import hr.wozai.service.feed.client.dto.RewardQuotaSettingDTO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.List;

*/
/**
 * Created by wangbin on 2016/11/17.
 *//*

@ThriftService
public interface RewardSettingFacade {

    @ThriftMethod
    public VoidDTO createRewardQuotaSetting(long orgId, RewardQuotaSettingDTO rewardQuotaSettingDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public RewardQuotaSettingDTO getRewardQuotaSettingByOrgId(long orgId, long actorUserId, long adminUserId);

    @ThriftMethod
    public VoidDTO deleteRewardQuotaSetting(long orgId, RewardQuotaSettingDTO rewardQuotaSettingDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public VoidDTO updateRewardQuotaSetting(long orgId, RewardQuotaSettingDTO rewardQuotaSettingDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public RewardQuotaInfoDTO findPersonalRewardQuotaInfo(long orgId, long userId, long actorUserId, long adminUserId);

    */
/*************************分割线*********************************************************//*

    @ThriftMethod
    public VoidDTO deleteRewardMedal(long orgId, long rewardMedalId, long actorUserId, long adminUserId);

    @ThriftMethod
    public LongDTO createRewardMedal(long orgId, RewardMedalDTO rewardMedalSettingDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public VoidDTO batchCreateRewardMedal(long orgId, RewardMedalListDTO rewardMedalSettingListDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public VoidDTO batchDeleteRewardMedal(long orgId, RewardMedalListDTO rewardMedalSettingListDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public VoidDTO batchUpdateRewardMedal(long orgId, RewardMedalListDTO rewardMedalSettingListDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public VoidDTO updateRewardMedal(long orgId, RewardMedalDTO rewardMedalSettingDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public RewardMedalListDTO listRewardMedalSetting(long orgId, long actorUserId, long adminUserId);

    @ThriftMethod
    public RewardMedalListDTO listRewardMedalSettingByRewardMedalIds(long orgId, List<Long> rewardMedalIds, long actorUserId, long adminUserId);

}
*/
