/*
package hr.wozai.service.feed.server.thrift.facade;

import hr.wozai.service.feed.client.dto.RewardMedalSettingDTO;
import hr.wozai.service.feed.client.dto.RewardMedalSettingListDTO;
import hr.wozai.service.feed.client.dto.RewardQuotaInfoDTO;
import hr.wozai.service.feed.client.dto.RewardQuotaSettingDTO;
import hr.wozai.service.feed.client.facade.RewardSettingFacade;
import hr.wozai.service.feed.server.helper.FacadeExceptionHelper;
import hr.wozai.service.feed.server.model.Reward;
import hr.wozai.service.feed.server.model.RewardMedal;
import hr.wozai.service.feed.server.model.RewardQuotaSetting;
import hr.wozai.service.feed.server.service.RewardMedalService;
import hr.wozai.service.feed.server.service.RewardQuotaSettingService;
import hr.wozai.service.feed.server.service.RewardService;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

*/
/**
 * Created by wangbin on 2016/11/17.
 *//*

@Service("rewardSettingFacade")
public class RewardSettingFacadeImpl implements RewardSettingFacade {

    private final static Logger LOGGER = LoggerFactory.getLogger(RewardSettingFacadeImpl.class);
    @Autowired
    private RewardQuotaSettingService rewardQuotaSettingService;

    @Autowired
    private RewardMedalService rewardMedalService;

    @Autowired
    private RewardService rewardService;

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
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("createRewardQuotaSetting-error()", e);
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
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("updateRewardQuotaSetting-error()", e);
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

    @Override
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
    }

    @Override
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
    }

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
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("getRewardQuotaSettingByOrgId-error()", e);
        }
        return result;
    }

    */
/*
************************************ 分割线 ******************************************************    *
     *//*


    @Override
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
    }

    @Override
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
    }

    @Override
    @LogAround
    public RewardMedalListDTO listRewardMedalSetting(long orgId, long actorUserId, long adminUserId) {
        RewardMedalListDTO result = new RewardMedalListDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO(ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);
        try {
            List<RewardMedal> rewardMedals = rewardMedalService.listRewardMedal(orgId);
            List<RewardMedalDTO> rewardMedalSettingDTOs = new ArrayList<>();

            for (RewardMedal rewardMedal : rewardMedals) {
                RewardMedalDTO rewardMedalSettingDTO = new RewardMedalDTO();
                BeanUtils.copyProperties(rewardMedal, rewardMedalSettingDTO);
                rewardMedalSettingDTOs.add(rewardMedalSettingDTO);
            }

            result.setRewardMedalDTOList(rewardMedalSettingDTOs);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("listRewardMedalSetting()-error", e);
        }
        return result;
    }

*/
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
    }*//*


    @Override
    public RewardMedalListDTO listRewardMedalSettingByRewardMedalIds(long orgId, List<Long> rewardMedalIds, long actorUserId, long adminUserId) {
        RewardMedalListDTO result = new RewardMedalListDTO();
        ServiceStatusDTO serviceStatusDTO =
                new ServiceStatusDTO((ServiceStatus.COMMON_OK.getCode()), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardMedal> rewardMedals = rewardMedalService.listRewardMedalByRewardMedalIds(orgId, rewardMedalIds);
            List<RewardMedalDTO> rewardMedalSettingDTOs = new ArrayList<>();
            for (RewardMedal rewardMedal : rewardMedals) {
                RewardMedalDTO rewardMedalSettingDTO = new RewardMedalDTO();
                BeanUtils.copyProperties(rewardMedal, rewardMedalSettingDTO);
                rewardMedalSettingDTOs.add(rewardMedalSettingDTO);
            }
            result.setRewardMedalDTOList(rewardMedalSettingDTOs);

        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("listRewardMedalSettingByRewardMedalIds", e);
        }

        return result;
    }

    @Override
    @LogAround
    public VoidDTO batchCreateRewardMedal(long orgId, RewardMedalListDTO rewardMedalSettingListDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(
                ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardMedalDTO> rewardMedalSettingDTOs = rewardMedalSettingListDTO.getRewardMedalDTOList();
            List<RewardMedal> rewardMedals = new ArrayList<>();

            for (RewardMedalDTO rewardMedalSettingDTO : rewardMedalSettingDTOs) {
                RewardMedal rewardMedal = new RewardMedal();
                BeanUtils.copyProperties(rewardMedalSettingDTO, rewardMedal);
                rewardMedals.add(rewardMedal);
            }

            rewardMedalService.batchCreateRewardMedal(orgId, rewardMedals);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("batchCreateRewardMedal()-error", e);
        }
        return result;
    }

    @Override
    @LogAround
    public VoidDTO batchDeleteRewardMedal(long orgId, RewardMedalListDTO rewardMedalSettingListDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(
                ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardMedal> rewardMedals = new ArrayList<>();
            List<RewardMedalDTO> rewardMedalSettingDTOs = rewardMedalSettingListDTO.getRewardMedalDTOList();
            for (RewardMedalDTO rewardMedalSettingDTO : rewardMedalSettingDTOs) {
                RewardMedal rewardMedal = new RewardMedal();
                BeanUtils.copyProperties(rewardMedalSettingDTO, rewardMedal);
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
    public VoidDTO batchUpdateRewardMedal(long orgId, RewardMedalListDTO rewardMedalSettingListDTO, long actorUserId, long adminUserId) {
        VoidDTO result = new VoidDTO();
        ServiceStatusDTO serviceStatusDTO = new ServiceStatusDTO(
                ServiceStatus.COMMON_OK.getCode(), ServiceStatus.COMMON_OK.getMsg());
        result.setServiceStatusDTO(serviceStatusDTO);

        try {
            List<RewardMedal> rewardMedals = new ArrayList<>();
            List<RewardMedalDTO> rewardMedalSettingDTOs = rewardMedalSettingListDTO.getRewardMedalDTOList();
            for (RewardMedalDTO rewardMedalSettingDTO : rewardMedalSettingDTOs) {
                RewardMedal rewardMedal = new RewardMedal();
                BeanUtils.copyProperties(rewardMedalSettingDTO, rewardMedal);
                rewardMedals.add(rewardMedal);
            }

            rewardMedalService.batchUpdateRewardMedal(orgId, rewardMedals);
        } catch (Exception e) {
            FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
            LOGGER.error("batchDeleteRewardMedal", e);
        }

        return result;
    }
}
*/
