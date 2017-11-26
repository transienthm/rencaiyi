package hr.wozai.service.api.controller.feed;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.ControllerExceptionHelper;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.feed.RewardMedalListVO;
import hr.wozai.service.api.vo.feed.RewardMedalVO;
import hr.wozai.service.api.vo.feed.RewardQuotaSettingVO;
import hr.wozai.service.feed.client.dto.*;
import hr.wozai.service.feed.client.enums.RewardMedalTemplate;
import hr.wozai.service.feed.client.facade.RewardFacade;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wangbin on 2016/11/17.
 */
@Controller("rewardSettingController")
public class RewardSettingController {
    private final static Logger LOGGER = LoggerFactory.getLogger(RewardSettingController.class);

    @Autowired
    private FacadeFactory facadeFactory;

    private final static Integer IS_DELETABLE = 0;

    @LogAround
    @RequestMapping(value = "/rewards/reward-quota-setting", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Result<Object> updateRewardQuotaSetting(
            @RequestBody RewardQuotaSettingVO rewardQuotaSettingVO,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        Result<Object> result = new Result<>();

        try {
            long orgId = AuthenticationInterceptor.orgId.get();
            long actorUserId = AuthenticationInterceptor.actorUserId.get();
            long adminUserId = AuthenticationInterceptor.adminUserId.get();

            RewardFacade rewardFacade = facadeFactory.getRewardFacade();

            RewardQuotaSettingDTO rewardQuotaSettingDTO = new RewardQuotaSettingDTO();
            BeanUtils.copyProperties(rewardQuotaSettingVO, rewardQuotaSettingDTO);

            rewardQuotaSettingDTO.setCreatedUserId(actorUserId);
            rewardQuotaSettingDTO.setOrgId(orgId);
            rewardQuotaSettingDTO.setLastModifiedUserId(actorUserId);

            VoidDTO voidDTO = rewardFacade.createRewardQuotaSetting(orgId,
                    rewardQuotaSettingDTO, actorUserId, adminUserId);
            result.setCodeAndMsg(ServiceStatus.getEnumByCode(voidDTO.getServiceStatusDTO().getCode()));


        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
            LOGGER.error("createFeed()-fail", e);
        }

        return result;
    }

    @LogAround

    @RequestMapping(value = "/rewards/reward-quota-setting", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Result<Object> listRewardQuotaSetting() {
        Result<Object> result = new Result<>();

        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        try {
            RewardQuotaSettingDTO rewardQuotaSettingDTO = facadeFactory.getRewardFacade()
                    .getRewardQuotaSettingByOrgId(orgId, actorUserId, adminUserId);

            ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(rewardQuotaSettingDTO.getServiceStatusDTO().getCode());
            if (serviceStatus != ServiceStatus.COMMON_OK) {
                throw new ServiceStatusException(serviceStatus);
            }

            RewardQuotaSettingVO rewardQuotaSettingVO = new RewardQuotaSettingVO();
            BeanUtils.copyProperties(rewardQuotaSettingDTO, rewardQuotaSettingVO);

            result.setData(rewardQuotaSettingVO);
            result.setCodeAndMsg(serviceStatus);

        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
            LOGGER.error("getRewardQuotaSettingByOrgId()-fail", e);
        }
        return result;
    }

/*    @LogAround

    @RequestMapping(value = "/reward/reward-personal-medal-setting", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Result<Object> listPersonalRewardMedalSetting() {
        Result<Object> result = new Result<>();

        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        RewardMedalSettingListVO rewardMedalSettingListVO = new RewardMedalSettingListVO();

        try {
            RewardMedalListDTO rewardMedalSettingListDTO = facadeFactory.getRewardSettingFacade().listPersonalRewardMedalSetting(orgId);
            ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(rewardMedalSettingListDTO.getServiceStatusDTO().getCode());
            if (serviceStatus != ServiceStatus.COMMON_OK) {
                throw new ServiceStatusException(serviceStatus);
            }

            if (CollectionUtils.isEmpty(rewardMedalSettingListDTO.getRewardMedalDTOList())) {
                RewardMedalVO rewardMedalSettingVO = new RewardMedalVO();
                rewardMedalSettingVO.setMedalIcon("123");
                rewardMedalSettingVO.setMedalName("思想者");
                rewardMedalSettingVO.setDescription("给深入思考问题，解决团队难点的同事");
                result.setData(rewardMedalSettingVO);
                result.setCodeAndMsg(serviceStatus);
                return result;
            }

            List<RewardMedalDTO> rewardMedalSettingDTOList = rewardMedalSettingListDTO.getRewardMedalDTOList();
            List<RewardMedalVO> rewardMedalSettingVOs = new ArrayList<>();
            for (RewardMedalDTO rewardMedalSettingDTO : rewardMedalSettingDTOList) {
                RewardMedalVO rewardMedalSettingVO = new RewardMedalVO();
                BeanUtils.copyProperties(rewardMedalSettingDTO, rewardMedalSettingVO);
                rewardMedalSettingVOs.add(rewardMedalSettingVO);
            }
            rewardMedalSettingListVO.setRewardMedalSettingVOs(rewardMedalSettingVOs);
            rewardMedalSettingListVO.setAmount(Long.valueOf(rewardMedalSettingListDTO.getRewardMedalDTOList().size()));

            result.setData(rewardMedalSettingListVO);
            result.setCodeAndMsg(ServiceStatus.getEnumByCode(rewardMedalSettingListDTO.getServiceStatusDTO().getCode()));
        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
        }
        return result;
    }*/

/*--------------------------------------分割线---------------------------------勋章设置-------------------------------*/
    @LogAround
    @RequestMapping(value = "/rewards/reward-medal-setting", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Result<Object> listRewardMedalSetting() {
        Result<Object> result = new Result<>();

        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        try {
            RewardMedalListVO rewardMedalListVO = new RewardMedalListVO();

            List<RewardMedalVO> teamRewardMedalVOs = new ArrayList<>();
            List<RewardMedalVO> personalRewardMedalVOs = new ArrayList<>();

            RewardMedalListDTO rewardMedalListDTO =
                    facadeFactory.getRewardFacade().listRewardMedalSetting(orgId, actorUserId, adminUserId);
            ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(rewardMedalListDTO.getServiceStatusDTO().getCode());

            if (serviceStatus != ServiceStatus.COMMON_OK) {
                throw new ServiceStatusException(serviceStatus);
            }

            /*if (CollectionUtils.isEmpty(rewardMedalListDTO.getRewardMedalDTOList())) {
                RewardMedalVO rewardMedalVO = new RewardMedalVO();
                rewardMedalVO.setMedalIcon(RewardMedalTemplate.REWARD_MEDAL_ICON.getDefaultValue());
                rewardMedalVO.setMedalName(RewardMedalTemplate.REWARD_MEDAL_NAME.getDefaultValue());
                rewardMedalVO.setDescription(RewardMedalTemplate.REWARD_MEDAL_DESCRIPTION.getDefaultValue());

                result.setData(rewardMedalVO);
                result.setCodeAndMsg(serviceStatus);
                return result;
            }*/

            //将使用过的rewardMedalId存入userRewardMedalIds
            RewardListDTO rewardListDTO = facadeFactory.getRewardFacade().listRewardByOrgId(orgId, actorUserId, adminUserId);
            Set<Long> usedRewardMedalIds = new HashSet<>();
            for (RewardDTO rewardDTO : rewardListDTO.getRewardDTOList()) {
                usedRewardMedalIds.add(rewardDTO.getRewardMedalId());
            }

            List<RewardMedalDTO> rewardMedalDTOs = rewardMedalListDTO.getRewardMedalDTOList();
            for (RewardMedalDTO rewardMedalDTO : rewardMedalDTOs) {
                RewardMedalVO rewardMedalVO = new RewardMedalVO();
                BeanUtils.copyProperties(rewardMedalDTO, rewardMedalVO);
                if (rewardMedalVO.getMedalType() == 0) {
                    personalRewardMedalVOs.add(rewardMedalVO);
                } else if (rewardMedalVO.getMedalType() == 1) {
                    teamRewardMedalVOs.add(rewardMedalVO);
                }
                //设置isDeletable
                if (usedRewardMedalIds.contains(rewardMedalVO.getRewardMedalId())) {
                    rewardMedalVO.setIsDeletable(0);
                } else {
                    rewardMedalVO.setIsDeletable(1);
                }
            }
            rewardMedalListVO.setPersonalRewardMedalVOs(personalRewardMedalVOs);
            rewardMedalListVO.setTeamRewardMedalVOs(teamRewardMedalVOs);
            rewardMedalListVO.setAmount(Long.valueOf(rewardMedalDTOs.size()));

            result.setData(rewardMedalListVO);
            result.setCodeAndMsg(ServiceStatus.getEnumByCode(rewardMedalListDTO.getServiceStatusDTO().getCode()));

        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
            LOGGER.error("listRewardMedalSetting-error()", e);
        }

        return result;
    }

    @LogAround

    @RequestMapping(value = "/rewards/reward-medal-setting-batch", method = RequestMethod.POST,produces = "application/json")
    @ResponseBody
    public Result<Object> batchCreateRewardMedal(
            @RequestBody RewardMedalListVO rewardMedalListVO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Result<Object> result = new Result<>();

        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        try {
            RewardMedalListDTO rewardMedalListDTO = getRewardMedalSettingListDTOFromRewardMedalSettingListVO(
                    orgId, rewardMedalListVO, actorUserId, adminUserId);

            VoidDTO remoteResult = facadeFactory.getRewardFacade().batchCreateRewardMedal(orgId, rewardMedalListDTO, actorUserId, adminUserId);

            ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
            if (serviceStatus != ServiceStatus.COMMON_OK) {
                throw new ServiceStatusException(serviceStatus);
            }

            result.setData(remoteResult);
            result.setCodeAndMsg(serviceStatus);

        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
            LOGGER.error("createPersonalMedal-error()", e);
        }
        return result;
    }


    @LogAround
    @RequestMapping(value = "/rewards/reward-medal-setting-batch", method = RequestMethod.DELETE,produces = "application/json")
    @ResponseBody
    public Result<Object> batchDeleteRewardMedal(
            @RequestBody RewardMedalListVO rewardMedalListVO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Result<Object> result = new Result<>();

        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        try {
            RewardMedalListDTO rewardMedalListDTO = getRewardMedalSettingListDTOFromRewardMedalSettingListVO
                    (orgId, rewardMedalListVO, actorUserId, adminUserId);

            VoidDTO remoteResult = facadeFactory.getRewardFacade().batchDeleteRewardMedal(orgId, rewardMedalListDTO, actorUserId, adminUserId);

            ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
            if (serviceStatus != ServiceStatus.COMMON_OK) {
                throw new ServiceStatusException(serviceStatus);
            }

            result.setData(remoteResult);
            result.setCodeAndMsg(serviceStatus);

        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
            LOGGER.error("createPersonalMedal-error()", e);
        }
        return result;
    }

    @LogAround

    @RequestMapping(value = "/rewards/reward-medal-setting-batch", method = RequestMethod.PUT,produces = "application/json")
    @ResponseBody
    public Result<Object> batchUpdateRewardMedal(
            @RequestBody RewardMedalListVO rewardMedalListVO,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Result<Object> result = new Result<>();

        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        try {
            RewardMedalListDTO rewardMedalListDTO = getRewardMedalSettingListDTOFromRewardMedalSettingListVO
                    (orgId, rewardMedalListVO, actorUserId, adminUserId);

            VoidDTO remoteResult = facadeFactory.getRewardFacade().batchUpdateRewardMedal(orgId, rewardMedalListDTO, actorUserId, adminUserId);

            ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode());
            if (serviceStatus != ServiceStatus.COMMON_OK) {
                throw new ServiceStatusException(serviceStatus);
            }

            result.setData(remoteResult);
            result.setCodeAndMsg(serviceStatus);

        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
            LOGGER.error("createPersonalMedal-error()", e);
        }
        return result;
    }


/*    @LogAround

    @RequestMapping(value = "/rewards/reward-medal-setting/{rewardMedalId}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public Result<Object> deleteRewardMedal(
            @PathVariable(value = "rewardMedalId") String encryptedRewardMedalId
    ) {
        Result<Object> result = new Result<>();

        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        try {
            Long rewardMedalId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedRewardMedalId));
            VoidDTO voidDTO = facadeFactory.getRewardFacade().deleteRewardMedal(orgId, rewardMedalId, actorUserId, adminUserId);
            ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(voidDTO.getServiceStatusDTO().getCode());

            if (serviceStatus != ServiceStatus.COMMON_OK) {
                throw new ServiceStatusException(serviceStatus);
            }

            result.setCodeAndMsg(serviceStatus);
            result.setData(voidDTO);
        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
            LOGGER.error("deleteRewardMedal", e);
        }

        return result;
    }*/



/*    @LogAround

    @RequestMapping(value = "/rewards/reward-medal-setting/", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public Result<Object> updateRewardMedal(
            @RequestBody RemindSettingVO rewardMedalSettingVO
    ) {
        Result<Object> result = new Result<>();

        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        try {

            RewardMedalDTO rewardMedalSettingDTO = new RewardMedalDTO();
            BeanUtils.copyProperties(rewardMedalSettingVO, rewardMedalSettingDTO);
            VoidDTO voidDTO = facadeFactory.getRewardFacade().updateRewardMedal(orgId, rewardMedalSettingDTO, actorUserId, adminUserId);
            ServiceStatus serviceStatus = ServiceStatus.getEnumByCode(voidDTO.getServiceStatusDTO().getCode());

            if (serviceStatus != ServiceStatus.COMMON_OK) {
                throw new ServiceStatusException(serviceStatus);
            }

            result.setCodeAndMsg(serviceStatus);
            result.setData(voidDTO);
        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
            LOGGER.error("updateRewardMedal-error", e);
        }

        return result;
    }*/


    private RewardMedalListDTO getRewardMedalSettingListDTOFromRewardMedalSettingListVO(
            long orgId, RewardMedalListVO rewardMedalListVO, long actorUserId, long adminUserId) {

        RewardMedalListDTO rewardMedalListDTO = new RewardMedalListDTO();
        List<RewardMedalVO> rewardMedalVOs = new ArrayList<>();
        List<RewardMedalDTO> rewardMedalDTOs = new ArrayList<>();

        rewardMedalVOs.addAll(rewardMedalListVO.getPersonalRewardMedalVOs());
        rewardMedalVOs.addAll(rewardMedalListVO.getTeamRewardMedalVOs());

        for (RewardMedalVO rewardMedalVO : rewardMedalVOs) {
            RewardMedalDTO rewardMedalDTO = new RewardMedalDTO();
            BeanUtils.copyProperties(rewardMedalVO, rewardMedalDTO);
            rewardMedalDTO.setOrgId(orgId);
            rewardMedalDTO.setCreatedUserId(actorUserId);
            rewardMedalDTO.setLastModifiedUserId(actorUserId);
            rewardMedalDTOs.add(rewardMedalDTO);
        }

        rewardMedalListDTO.setRewardMedalDTOList(rewardMedalDTOs);
        return rewardMedalListDTO;
    }

}