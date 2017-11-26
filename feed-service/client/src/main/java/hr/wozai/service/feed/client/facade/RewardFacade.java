package hr.wozai.service.feed.client.facade;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import hr.wozai.service.feed.client.dto.*;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;

import java.util.List;

/**
 * Created by wangbin on 2016/11/21.
 */
@ThriftService
public interface RewardFacade {

   /* @ThriftMethod
    public VoidDTO createReward(long orgId, RewardDTO rewardDTO, long actorUserId, long adminUserId);
*/
/*    @ThriftMethod
    public VoidDTO updateReward(long orgId, RewardDTO rewardDTO, long actorUserId, long adminUserId);*/

 /*   @ThriftMethod
    public RewardDTO findRewardByFeedId(long orgId, long feedId, long actorUserId, long adminUserId);*/

  /*  @ThriftMethod
    public RewardListDTO listRewardsByFeedIds(long orgId, List<Long> feedIds, long actorUserId, long adminUserId);
*/

    /*@ThriftMethod
    public VoidDTO deleteRewardByFeedId(long orgId, long feedId , long actorUserId, long adminUserId);
*/
/*    @ThriftMethod
    public VoidDTO deleteReward(long orgId, RewardDTO rewardDTO, long actorUserId, long adminUserId);
*/

    @ThriftMethod
    public RewardListDTO listRewardByOrgId(long orgId, long actorUserId, long adminUserId);

    /*----------------------------rewardQuota---------------------------------------*/

    @ThriftMethod
    public VoidDTO createRewardQuotaSetting(long orgId, RewardQuotaSettingDTO rewardQuotaSettingDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public RewardQuotaSettingDTO getRewardQuotaSettingByOrgId(long orgId, long actorUserId, long adminUserId);

    /*@ThriftMethod
    public VoidDTO deleteRewardQuotaSetting(long orgId, RewardQuotaSettingDTO rewardQuotaSettingDTO, long actorUserId, long adminUserId);
*/
    @ThriftMethod
    public VoidDTO updateRewardQuotaSetting(long orgId, RewardQuotaSettingDTO rewardQuotaSettingDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public RewardQuotaInfoDTO findPersonalRewardQuotaInfo(long orgId, long userId, long actorUserId, long adminUserId);

    /*************************分割线***********rewardMedal**********************************************/

    @ThriftMethod
    public RewardMedalListDTO listRewardMedalByRewardeeId(long orgId, long rewardeeId, long actorUserId, long adminUserId);

    /*@ThriftMethod
    public VoidDTO deleteRewardMedal(long orgId, long rewardMedalId, long actorUserId, long adminUserId);
*/
    /*@ThriftMethod
    public LongDTO createRewardMedal(long orgId, RewardMedalDTO rewardMedalSettingDTO, long actorUserId, long adminUserId);
*/
    @ThriftMethod
    public VoidDTO batchCreateRewardMedal(long orgId, RewardMedalListDTO rewardMedalListDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public VoidDTO batchDeleteRewardMedal(long orgId, RewardMedalListDTO rewardMedalListDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public VoidDTO batchUpdateRewardMedal(long orgId, RewardMedalListDTO rewardMedalListDTO, long actorUserId, long adminUserId);

    @ThriftMethod
    public VoidDTO initRewardMedal(long orgId, long actorUserId, long adminUserId);

    /*@ThriftMethod
    public VoidDTO updateRewardMedal(long orgId, RewardMedalDTO rewardMedalSettingDTO, long actorUserId, long adminUserId);
*/
    @ThriftMethod
    public RewardMedalListDTO listRewardMedalSetting(long orgId, long actorUserId, long adminUserId);

    @ThriftMethod
    public RewardMedalListDTO listRewardMedalSettingByRewardMedalIds(long orgId, List<Long> rewardMedalIds, long actorUserId, long adminUserId);

}
