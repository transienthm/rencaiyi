package hr.wozai.service.feed.server.thrift.facade;

import hr.wozai.service.feed.client.dto.*;
import hr.wozai.service.feed.client.enums.RewardMedalTemplate;
import hr.wozai.service.feed.client.facade.RewardFacade;
import hr.wozai.service.feed.server.helper.FacadeExceptionHelper;
import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.feed.server.model.RewardMedal;
import hr.wozai.service.feed.server.model.RewardQuotaSetting;
import hr.wozai.service.feed.server.service.RewardMedalService;
import hr.wozai.service.feed.server.service.RewardQuotaSettingService;
import hr.wozai.service.feed.server.service.RewardService;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Created by wangbin on 2016/11/21.
 */
@Service("rewardFacade")
public class RewardFacadeImpl implements RewardFacade {

    private final static Logger LOGGER = LoggerFactory.getLogger(RewardFacadeImpl.class);

    @Autowired
    private RewardService rewardService;

    @Autowired
    private RewardMedalService rewardMedalService;

    @Autowired
    private RewardQuotaSettingService rewardQuotaSettingService;

  /*  @Override
    @LogAround
    public VoidDTO createReward(long orgId, RewardDTO rewardDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {



            List<Long> rewardeeIds = rewardDTO.getRewardeeIds();
            List<Reward> rewards = new ArrayList<>();

            for (Long l : rewardeeIds) {
                Reward reward = new Reward();
                BeanUtils.copyProperties(rewardDTO, reward);
                reward.setRewardeeId(l);
                rewards.add(reward);
            }
            rewardService.createReward(rewards);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("createReward()-error", e);
        }
        return result;
    }*/

/*    @Override
    @LogAround
    public VoidDTO updateReward(long orgId, RewardDTO rewardDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Reward reward = new Reward();
            BeanUtils.copyProperties(rewardDTO, reward);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("updateReward()-error", e);
        }

        return null;
    }*/

/*    @Override
    @LogAround
    public VoidDTO deleteReward(long orgId, RewardDTO rewardDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Reward reward = new Reward();
            BeanUtils.copyProperties(rewardDTO, reward);
            rewardService.deleteReward(reward);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("deleteReward", e);
        }
        return result;
    }*/

    /*@Override
    public VoidDTO deleteRewardByFeedId(long orgId, long feedId, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Reward reward = new Reward();
            reward.setOrgId(orgId);
            reward.setFeedId(feedId);
            reward.setLastModifiedUserId(actorUserId);
            rewardService.deleteRewardByFeedId(reward);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("deleteReward", e);
        }
        return result;
    }*/

    @Override
    @LogAround
    public RewardListDTO listRewardByOrgId(long orgId, long actorUserId, long adminUserId) {

        RewardListDTO result = new RewardListDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        List<RewardDTO> rewardDTOs = new ArrayList<>();

        try {
            List<Reward> rewards = rewardService.listRewardOfOrg(orgId);
            for (Reward reward : rewards) {
                RewardDTO rewardDTO = new RewardDTO();
                BeanUtils.copyProperties(reward, rewardDTO);
                rewardDTOs.add(rewardDTO);
            }
            result.setRewardDTOList(rewardDTOs);
        } catch (Exception e) {
            LOGGER.error("listRewardByOrgId-error()", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

/*    @Override
    @LogAround
    public RewardDTO findRewardByFeedId(long orgId, long feedId, long actorUserId, long adminUserId) {
        RewardDTO result = new RewardDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            Reward reward = rewardService.findRewardByFeedId(orgId, feedId);
            BeanUtils.copyProperties(reward, result);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("findRewardByFeedId-error()", e);
        }

        return result;
    }*/

  /*  @Override
    @LogAround
    public RewardListDTO listRewardsByFeedIds(long orgId, List<Long> feedIds, long actorUserId, long adminUserId) {
        RewardListDTO result = new RewardListDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardDTO> rewardDTOs = new ArrayList<>();
            List<Reward> rewards = rewardService.listRewardsByFeedIds(orgId, feedIds);
            for (Reward reward : rewards) {
                RewardDTO rewardDTO = new RewardDTO();
                BeanUtils.copyProperties(reward, rewardDTO);
                rewardDTO.setRewardeeIds(Arrays.asList(reward.getRewardeeId()));
                rewardDTOs.add(rewardDTO);
            }
            result.setRewardDTOList(rewardDTOs);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("listRewardsByFeedIds-error()", e);
        }

        return result;
    }*/

    @Override
    @LogAround
    public RewardMedalListDTO listRewardMedalByRewardeeId(long orgId, long rewardeeId, long actorUserId, long adminUserId) {
        RewardMedalListDTO result = new RewardMedalListDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {
            List<Reward> rewards = rewardService.listRewardByRewardeeId(orgId, rewardeeId);
            if (CollectionUtils.isEmpty(rewards)) {
                return result;
            }
            List<Long> rewardMedalIds = new ArrayList<>();
            Map<Long, Integer> rewardMedalIdAndCountMap = new HashMap<>();
            for (Reward reward : rewards) {
                long rewardMedalId = reward.getRewardMedalId();
                rewardMedalIds.add(rewardMedalId);
                if (rewardMedalIdAndCountMap.containsKey(rewardMedalId)) {
                    int count = rewardMedalIdAndCountMap.get(rewardMedalId);
                    rewardMedalIdAndCountMap.put(rewardMedalId, ++count);
                } else {
                    rewardMedalIdAndCountMap.put(rewardMedalId, 1);
                }
            }

            List<RewardMedal> rewardMedals = rewardMedalService.listRewardMedalByRewardMedalIds(orgId, rewardMedalIds);
            List<RewardMedalDTO> rewardMedalDTOs = new ArrayList<>();
            for (RewardMedal rewardMedal : rewardMedals) {
                RewardMedalDTO rewardMedalDTO = new RewardMedalDTO();
                BeanUtils.copyProperties(rewardMedal, rewardMedalDTO);

                long rewardMedalId = rewardMedalDTO.getRewardMedalId();
                if (rewardMedalIdAndCountMap.containsKey(rewardMedalId)) {
                    rewardMedalDTO.setReceivedCount(rewardMedalIdAndCountMap.get(rewardeeId));
                } else {
                    rewardMedalDTO.setReceivedCount(0);
                }

                rewardMedalDTOs.add(rewardMedalDTO);
            }

            result.setRewardMedalDTOList(rewardMedalDTOs);
        } catch (Exception e) {
            LOGGER.error("listRewardMedalByRewardeeId-error()", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }


    /*-----------------------分割线 ---------------------rewardQuotaSetting---------------------------------------*/

    @Override
    @LogAround
    public VoidDTO createRewardQuotaSetting(long orgId, RewardQuotaSettingDTO rewardQuotaSettingDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            RewardQuotaSetting rewardQuotaSetting = new RewardQuotaSetting();
            BeanUtils.copyProperties(rewardQuotaSettingDTO, rewardQuotaSetting);
            rewardQuotaSettingService.createRewardSetting(rewardQuotaSetting);
        } catch (Exception e) {
            LOGGER.error("createRewardQuotaSetting-error()", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }


    @Override
    @LogAround
    public VoidDTO updateRewardQuotaSetting(long orgId, RewardQuotaSettingDTO rewardQuotaSettingDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {
            RewardQuotaSetting rewardQuotaSetting = new RewardQuotaSetting();
            BeanUtils.copyProperties(rewardQuotaSettingDTO, rewardQuotaSetting);
            rewardQuotaSettingService.updateRewardQuota(rewardQuotaSetting);
        } catch (Exception e) {
            LOGGER.error("updateRewardQuotaSetting-error()", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @Override
    @LogAround
    public RewardQuotaInfoDTO findPersonalRewardQuotaInfo(long orgId, long userId, long actorUserId, long adminUserId) {
        RewardQuotaInfoDTO result = new RewardQuotaInfoDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            RewardQuotaSetting rewardQuotaSetting = rewardQuotaSettingService.getRewardSettingByOrgId(orgId);
            result.setPersonalQuota(rewardQuotaSetting.getPersonalQuota());
            result.setTeamQuota(rewardQuotaSetting.getTeamQuota());

            List<Reward> rewards = rewardService.listRewardByUserId(orgId, userId);
            int usedPersonalQuota = 0;
            int usedTeamQuota = 0;
            for (Reward reward : rewards) {
                if (reward.getRewardType() == 0) {
                    usedPersonalQuota++;
                } else if (reward.getRewardType() == 1) {
                    usedTeamQuota++;
                }
            }
            result.setUsedPersonalQuota(usedPersonalQuota);
            result.setUsedTeamQuota(usedTeamQuota);

        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("findPersonalRewardQuotaInfo", e);
        }
        return result;
    }

    /*@Override
    @LogAround
    public VoidDTO deleteRewardQuotaSetting(long orgId, RewardQuotaSettingDTO rewardQuotaSettingDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            RewardQuotaSetting rewardQuotaSetting = new RewardQuotaSetting();
            BeanUtils.copyProperties(rewardQuotaSettingDTO, rewardQuotaSetting);
            rewardQuotaSettingService.deleteRewardQuota(rewardQuotaSetting);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("deleteRewardQuotaSetting", e);
        }
        return result;
    }*/

/*    @Override
    @LogAround
    public VoidDTO deleteRewardMedal(long orgId, long rewardMedalId, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {
            rewardMedalService.deleteRewardMedal(orgId, rewardMedalId);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("deleteRewardMedal-error()", e);
        }
        return result;
    }*/

    @Override
    @LogAround
    public RewardQuotaSettingDTO getRewardQuotaSettingByOrgId(long orgId, long actorUserId, long adminUserId) {
        RewardQuotaSettingDTO result = new RewardQuotaSettingDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            RewardQuotaSetting rewardQuotaSetting = rewardQuotaSettingService.getRewardSettingByOrgId(orgId);
            BeanUtils.copyProperties(rewardQuotaSetting, result);
        } catch (Exception e) {
            LOGGER.error("getRewardQuotaSettingByOrgId-error()", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }
        return result;
    }

    /*
************************************ 分割线 ******************************************************    *
     */

/*    @Override
    @LogAround
    public LongDTO createRewardMedal(long orgId, RewardMedalDTO rewardMedalSettingDTO, long actorUserId, long adminUserId) {
        LongDTO result = new LongDTO();

        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {
            RewardMedal rewardMedal = new RewardMedal();
            BeanUtils.copyProperties(rewardMedalSettingDTO, rewardMedal);

            long rewardMedalId = rewardMedalService.createRewardMedal(rewardMedal);
            result.setData(rewardMedalId);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("createRewardMedal-error()", e);
        }
        return result;
    }*/

    /*@Override
    @LogAround
    public VoidDTO updateRewardMedal(long orgId, RewardMedalDTO rewardMedalSettingDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {


            RewardMedal rewardMedal = new RewardMedal();
            BeanUtils.copyProperties(rewardMedalSettingDTO, rewardMedal);
            rewardMedalService.updateRewardMedal(rewardMedal);

            // rewardMedalService.updateRewardMedal(orgId, rewardMedalId, updatedItem, actorUserId);

        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("updateRewardMedal()-error", e);
        }
        return result;
    }*/

    @Override
    @LogAround
    public RewardMedalListDTO listRewardMedalSetting(long orgId, long actorUserId, long adminUserId) {
        RewardMedalListDTO result = new RewardMedalListDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {
            List<RewardMedal> rewardMedals = rewardMedalService.listRewardMedal(orgId);
            List<RewardMedalDTO> rewardMedalDTOs = new ArrayList<>();

            for (RewardMedal rewardMedal : rewardMedals) {
                RewardMedalDTO rewardMedalDTO = new RewardMedalDTO();
                BeanUtils.copyProperties(rewardMedal, rewardMedalDTO);
                rewardMedalDTOs.add(rewardMedalDTO);
            }

            result.setRewardMedalDTOList(rewardMedalDTOs);
        } catch (Exception e) {
            LOGGER.error("listRewardMedalSetting()-error", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }
        return result;
    }

/*    @Override
    @LogAround
    public RewardMedalListDTO listPersonalRewardMedalSetting(long orgId) {
        RewardMedalListDTO result = new RewardMedalListDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardMedal> personalRewardMedals = rewardMedalService.listRewardMedalByOrgId(orgId);

            List<RewardMedalDTO> rewardMedalSettingDTOs = new ArrayList<>();
            for (RewardMedal rewardMedal : personalRewardMedals) {
                RewardMedalDTO rewardMedalSettingDTO = new RewardMedalDTO();
                BeanUtils.copyProperties(rewardMedal, rewardMedalSettingDTO);
                rewardMedalSettingDTOs.add(rewardMedalSettingDTO);
            }

            result.setRewardMedalDTOList(rewardMedalSettingDTOs);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }
        return result;
    }

    @Override
    @LogAround
    public RewardMedalListDTO listRewardMedalSetting(long orgId) {
        RewardMedalListDTO result = new RewardMedalListDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO((ServiceStatus.COMMON_OK.getCode()), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardMedal> teamRewardMedals = rewardMedalService.listRewardMedalByOrgId(orgId);

            List<RewardMedalDTO> rewardMedalSettingDTOs = new ArrayList<>();
            for (RewardMedal rewardMedal : teamRewardMedals) {
                RewardMedalDTO rewardMedalSettingDTO = new RewardMedalDTO();
                BeanUtils.copyProperties(rewardMedal, rewardMedalSettingDTO);
                rewardMedalSettingDTOs.add(rewardMedalSettingDTO);
            }

            result.setRewardMedalDTOList(rewardMedalSettingDTOs);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }
        return result;
    }*/

    @Override
    public RewardMedalListDTO listRewardMedalSettingByRewardMedalIds(long orgId, List<Long> rewardMedalIds, long actorUserId, long adminUserId) {
        RewardMedalListDTO result = new RewardMedalListDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO((ServiceStatus.COMMON_OK.getCode()), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardMedal> rewardMedals = rewardMedalService.listRewardMedalByRewardMedalIds(orgId, rewardMedalIds);
            List<RewardMedalDTO> rewardMedalDTOs = new ArrayList<>();
            for (RewardMedal rewardMedal : rewardMedals) {
                RewardMedalDTO rewardMedalDTO = new RewardMedalDTO();
                BeanUtils.copyProperties(rewardMedal, rewardMedalDTO);
                rewardMedalDTOs.add(rewardMedalDTO);
            }
            result.setRewardMedalDTOList(rewardMedalDTOs);

        } catch (Exception e) {
            LOGGER.error("listRewardMedalSettingByRewardMedalIds", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }

    @Override
    @LogAround
    public VoidDTO batchCreateRewardMedal(long orgId, RewardMedalListDTO rewardMedalListDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(
                ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardMedalDTO> rewardMedalDTOs = rewardMedalListDTO.getRewardMedalDTOList();
            List<RewardMedal> rewardMedals = new ArrayList<>();

            if (CollectionUtils.isEmpty(rewardMedalDTOs)) {
                return result;
            }

            for (RewardMedalDTO rewardMedalDTO : rewardMedalDTOs) {
                RewardMedal rewardMedal = new RewardMedal();
                BeanUtils.copyProperties(rewardMedalDTO, rewardMedal);
                rewardMedals.add(rewardMedal);
            }

            rewardMedalService.batchCreateRewardMedal(orgId, rewardMedals);
        } catch (Exception e) {
            e.printStackTrace();
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("batchCreateRewardMedal()-error", e);
        }
        return result;
    }

    @Override
    @LogAround
    public VoidDTO batchDeleteRewardMedal(long orgId, RewardMedalListDTO rewardMedalListDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(
                ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardMedal> rewardMedals = new ArrayList<>();
            List<RewardMedalDTO> rewardMedalDTOs = rewardMedalListDTO.getRewardMedalDTOList();
            if (CollectionUtils.isEmpty(rewardMedalDTOs)) {
                return result;
            }
            for (RewardMedalDTO rewardMedalDTO : rewardMedalDTOs) {
                RewardMedal rewardMedal = new RewardMedal();
                BeanUtils.copyProperties(rewardMedalDTO, rewardMedal);
                rewardMedals.add(rewardMedal);
            }

            rewardMedalService.batchDeleteRewardMedal(orgId, rewardMedals);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("batchDeleteRewardMedal", e);
        }

        return result;
    }

    @Override
    @LogAround
    public VoidDTO batchUpdateRewardMedal(long orgId, RewardMedalListDTO rewardMedalListDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(
                ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardMedal> rewardMedals = new ArrayList<>();
            List<RewardMedalDTO> rewardMedalDTOs = rewardMedalListDTO.getRewardMedalDTOList();
            if (CollectionUtils.isEmpty(rewardMedalDTOs)) {
                return result;
            }
            for (RewardMedalDTO rewardMedalDTO : rewardMedalDTOs) {
                RewardMedal rewardMedal = new RewardMedal();
                BeanUtils.copyProperties(rewardMedalDTO, rewardMedal);
                rewardMedals.add(rewardMedal);
            }
            rewardMedalService.batchUpdateRewardMedal(orgId, rewardMedals);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);LOGGER.error("batchDeleteRewardMedal", e);
        }

        return result;
    }

    @Override
    @LogAround
    public VoidDTO initRewardMedal(long orgId,long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(
                ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardMedal> rewardMedals = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                RewardMedal rewardMedal = new RewardMedal();
                RewardMedalTemplate rewardMedalTemplate = RewardMedalTemplate.getRewardMedalTemplateByCode(i);
                rewardMedal.setRewardMedalDTOByRewardMedalTemplate(rewardMedalTemplate);
                rewardMedal.setOrgId(orgId);
                rewardMedal.setCreatedUserId(actorUserId);
                rewardMedal.setLastModifiedUserId(actorUserId);
                rewardMedals.add(rewardMedal);
            }
            rewardMedalService.batchCreateRewardMedal(orgId, rewardMedals);
        } catch (Exception e) {
            LOGGER.error("initRewardMedal()-error", e);
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
        }

        return result;
    }
}
