package hr.wozai.service.api.controller.feed;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.vo.feed.RewardMedalListVO;
import hr.wozai.service.api.vo.feed.RewardMedalVO;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.feed.client.dto.RewardMedalDTO;
import hr.wozai.service.feed.client.dto.RewardMedalListDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.dto.TeamDTO;
import hr.wozai.service.user.client.userorg.dto.TeamListDTO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangbin on 2016/11/22.
 */
@Component
public class RewardUtils {

    @Autowired
    private FacadeFactory facadeFactory;

    /*public List<CoreUserProfileVO> getCoreUserProfileVOsFromRewardDTO(long orgId, RewardDTO rewardDTO,
                                                                       long actorUserId, long adminUserId) {
        List<CoreUserProfileVO> result = new ArrayList<>();

        List<Long> userIds = rewardDTO.getRewardeeIds();
        if (userIds != null) {
            result = getCoreUserProfileMapFromUserIds(userIds, orgId, actorUserId, adminUserId);
        }
        return result;
    }

    public List<TeamVO> getTeamVOsFromRewardDTO(long orgId, RewardDTO rewardDTO,
                                                 long actorUserId, long adminUserId) {
        List<TeamVO> result = new ArrayList<>();
        List<Long> teamIds = rewardDTO.getRewardeeIds();
        if (teamIds != null) {
            result = getTeamMapFromTeamIds(teamIds, orgId, actorUserId, adminUserId);
        }
        return result;
    }
*/
    @LogAround
    public Map<Long,CoreUserProfileVO> getCoreUserProfileMapFromUserIds(List<Long> idList, long orgId, long actorUserId, long adminUserId) {
        List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
        Map<Long, CoreUserProfileVO> result = new HashMap<>();
        if (!CollectionUtils.isEmpty(idList)) {
            CoreUserProfileListDTO rpcList = facadeFactory.getUserProfileFacade().listCoreUserProfile(orgId, idList, actorUserId, adminUserId);
            if (!CollectionUtils.isEmpty(rpcList.getCoreUserProfileDTOs())) {
                for (CoreUserProfileDTO coreUserProfileDTO : rpcList.getCoreUserProfileDTOs()) {
                    CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
                    BeanUtils.copyProperties(coreUserProfileDTO, coreUserProfileVO);
                    coreUserProfileVOs.add(coreUserProfileVO);
                }
            }
        }
        for (CoreUserProfileVO coreUserProfileVO : coreUserProfileVOs) {
            result.put(coreUserProfileVO.getUserId(), coreUserProfileVO);
        }
        return result;
    }

    @LogAround
    public Map<Long, TeamVO> getTeamMapFromTeamIds(List<Long> idList, long orgId, long actorUserId, long adminUserId) {
        Map<Long, TeamVO> result = new HashMap<>();
        List<TeamVO> teamVOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(idList)) {
            TeamListDTO rpcList = facadeFactory.getUserFacade().listTeamsByTeamIds(orgId, idList, actorUserId, adminUserId);
            if (!CollectionUtils.isEmpty(rpcList.getTeamDTOList())) {
                for (TeamDTO teamDTO : rpcList.getTeamDTOList()) {
                    TeamVO teamVO = new TeamVO();
                    BeanUtils.copyProperties(teamDTO, teamVO);
                    teamVOs.add(teamVO);
                }
            }
        }
        for (TeamVO teamVO : teamVOs) {
            result.put(teamVO.getTeamId(), teamVO);
        }
        return result;
    }

    @LogAround
    public Map<Long, RewardMedalListVO> getRewardMedalSettingListVOMapFromDTO(RewardMedalListDTO rewardMedalListDTO) {
        Map<Long, RewardMedalListVO> result = new HashMap<>();
        List<RewardMedalDTO> rewardMedalDTOs = rewardMedalListDTO.getRewardMedalDTOList();
        for (RewardMedalDTO rewardMedalDTO : rewardMedalDTOs) {
            List<RewardMedalVO> personalRewardMedalVOs = new ArrayList<>();
            List<RewardMedalVO> teamRewardMedalVOs = new ArrayList<>();
            RewardMedalVO rewardMedalVO = new RewardMedalVO();
            BeanUtils.copyProperties(rewardMedalDTO, rewardMedalVO);
            RewardMedalListVO rewardMedalListVO = new RewardMedalListVO();
            if (rewardMedalVO.getMedalType() == 0) {
                personalRewardMedalVOs.add(rewardMedalVO);
                rewardMedalListVO.setPersonalRewardMedalVOs(personalRewardMedalVOs);
                result.put(rewardMedalVO.getRewardMedalId(), rewardMedalListVO);
            } else if (rewardMedalVO.getMedalType() == 1) {
                teamRewardMedalVOs.add(rewardMedalVO);
                rewardMedalListVO.setTeamRewardMedalVOs(teamRewardMedalVOs);
                result.put(rewardMedalVO.getRewardMedalId(), rewardMedalListVO);
            }
        }
        return result;
    }
}
