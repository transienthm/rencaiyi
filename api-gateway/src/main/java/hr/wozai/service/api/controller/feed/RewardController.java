package hr.wozai.service.api.controller.feed;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.helper.ControllerExceptionHelper;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.feed.RewardMedalListVO;
import hr.wozai.service.api.vo.feed.RewardMedalVO;
import hr.wozai.service.api.vo.feed.RewardQuotaInfoVO;
import hr.wozai.service.feed.client.dto.*;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
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
import java.util.List;
import java.util.Map;


/** Created by wangbin on 2016/11/21.
 */

@Controller("rewardController")
public class RewardController {
     private final static Logger LOGGER = LoggerFactory.getLogger(RewardController.class);

     private static final long ALL_COMPANY = 0;

     @Autowired
     private FacadeFactory facadeFactory;

     @Autowired
     private FeedUtils feedUtils;

     @Autowired
     private RewardUtils rewardUtils;

     @LogAround

     @RequestMapping(value = "/rewards/reward-received/{rewardeeId}", method = RequestMethod.GET, produces = "application/json")
     @ResponseBody
     public Result<Object> listPersonalReceivedRewards(
             @PathVariable(value = "rewardeeId") String encryptedRewardeeId,
             HttpServletRequest request,
             HttpServletResponse response
     ) throws Exception {
         Result<Object> result = new Result<>();

         long orgId = AuthenticationInterceptor.orgId.get();
         long actorUserId = AuthenticationInterceptor.actorUserId.get();
         long adminUserId = AuthenticationInterceptor.adminUserId.get();

         Long rewardeeId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedRewardeeId));


         try {
             RewardMedalListDTO rewardMedalListDTO = facadeFactory.getRewardFacade().listRewardMedalByRewardeeId(orgId, rewardeeId, actorUserId, adminUserId);

             RewardMedalListVO rewardMedalListVO = new RewardMedalListVO();
             List<RewardMedalVO> personalRewardMedalVOs = new ArrayList<>();
             List<RewardMedalDTO> personalRewardMedalDTOs = rewardMedalListDTO.getRewardMedalDTOList();

             if (CollectionUtils.isEmpty(personalRewardMedalDTOs)) {
                 return result;
             }

             for (RewardMedalDTO rewardMedalDTO : personalRewardMedalDTOs) {
                 RewardMedalVO rewardMedalVO = new RewardMedalVO();
                 BeanUtils.copyProperties(rewardMedalDTO, rewardMedalVO);
                 personalRewardMedalVOs.add(rewardMedalVO);
             }
             rewardMedalListVO.setPersonalRewardMedalVOs(personalRewardMedalVOs);
             rewardMedalListVO.setAmount(Long.valueOf(rewardMedalListDTO.getRewardMedalDTOList().size()));
             result.setData(rewardMedalListVO);
             result.setCodeAndMsg(ServiceStatus.getEnumByCode(rewardMedalListDTO.getServiceStatusDTO().getCode()));
         } catch (Exception e) {
             ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
             LOGGER.error("listPersonalReceivedRewards-error()", e);
         }
         return result;
     }


     @LogAround

     @RequestMapping(value = "/rewards/reward-quota-info", method = RequestMethod.GET, produces = "application/json")
     @ResponseBody
     public Result<Object> listPersonalQuotaInfo(
             HttpServletRequest request,
             HttpServletResponse response
     ) throws Exception {
         Result<Object> result = new Result<>();

         long orgId = AuthenticationInterceptor.orgId.get();
         long actorUserId = AuthenticationInterceptor.actorUserId.get();
         long adminUserId = AuthenticationInterceptor.adminUserId.get();

         try {
             facadeFactory.getRewardFacade().getRewardQuotaSettingByOrgId(orgId, actorUserId, adminUserId);
             RewardQuotaInfoDTO rewardQuotaInfoDTO = facadeFactory.getRewardFacade().findPersonalRewardQuotaInfo(orgId, actorUserId, actorUserId, adminUserId);
             RewardQuotaInfoVO rewardQuotaInfoVO = new RewardQuotaInfoVO();
             BeanUtils.copyProperties(rewardQuotaInfoDTO, rewardQuotaInfoVO);

             result.setData(rewardQuotaInfoVO);
             result.setCodeAndMsg(ServiceStatus.getEnumByCode(rewardQuotaInfoDTO.getServiceStatusDTO().getCode()));
         } catch (Exception e) {
             ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
             LOGGER.error("listPersonalQuotaInfo", e);
         }
         return result;
     }
 }
/*
    @LogAround

    @RequestMapping(value = "/rewards/reward", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Result<Object> listRewards(
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        Result<Object> result = new Result<>();

        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        RewardFacade rewardFacade = facadeFactory.getRewardFacade();
        try {
            RewardListVO rewardListVO = new RewardListVO();
            List<RewardVO> rewardVOs = new ArrayList<>();

            RewardListDTO remoteResult = rewardFacade.listRewardByOrgId(orgId, actorUserId, adminUserId, pageNumber, pageSize);

            List<RewardDTO> rewardDTOList = remoteResult.getRewardDTOList();
            for (RewardDTO rewardDTO : rewardDTOList) {
                RewardVO rewardVO = new RewardVO();
                BeanUtils.copyProperties(rewardDTO, rewardVO);
                //赞赏个人
                if (rewardVO.getRewardType() == 0) {
                    List<CoreUserProfileVO> coreUserProfileVOs = rewardUtils.getCoreUserProfileVOsFromRewardDTO(orgId,
                            rewardDTO, actorUserId, adminUserId);
                    rewardVO.setRewardedUsers(coreUserProfileVOs);
                } else if (rewardVO.getRewardType() == 1) {
                    List<TeamVO> teamVOs = rewardUtils.getTeamVOsFromRewardDTO(orgId,
                            rewardDTO, actorUserId, adminUserId);
                    rewardVO.setRewardedTeams(teamVOs);
                }
                rewardVOs.add(rewardVO);
            }
            rewardListVO.setRewardInputVOs(rewardVOs);
            result.setData(rewardListVO);
            result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));

            System.out.println(rewardListVO);

        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
            LOGGER.error("listRewards", e);
        }
        return result;
    }

    @LogAround

    @RequestMapping(value = "/rewards/reward", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Result<Object> createReward(
            @RequestBody RewardInputVO rewardInputVO,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        Result<Object> result = new Result<>();
        long orgId = AuthenticationInterceptor.orgId.get();
        long actorUserId = AuthenticationInterceptor.actorUserId.get();
        long adminUserId = AuthenticationInterceptor.adminUserId.get();

        try {
            RewardFacade rewardFacade = facadeFactory.getRewardFacade();

            RewardDTO rewardDTO = new RewardDTO();
            long feedId = handleFeedInput(orgId, rewardInputVO, actorUserId, adminUserId);
            BeanUtils.copyProperties(rewardInputVO, rewardDTO);
            rewardDTO.setFeedId(feedId);
            rewardDTO.setOrgId(orgId);
            rewardDTO.setUserId(actorUserId);
            rewardDTO.setLastModifiedUserId(actorUserId);

            LongDTO longDTO = rewardFacade.createReward(orgId, rewardDTO, actorUserId, adminUserId);
            result.setData(longDTO);
            result.setCodeAndMsg(ServiceStatus.getEnumByCode(longDTO.getServiceStatusDTO().getCode()));
        } catch (Exception e) {
            ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
            LOGGER.error("createReward()-error", e);
        }
        return result;
    }


    private long handleFeedInput(long orgId, RewardInputVO rewardInputVO, long actorUserId, long adminUserId) {

        String content = rewardInputVO.getContent();

        if (StringUtils.isNullOrEmpty(content)) {
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        } else if (content.length() > FeedAndCommentConsts.MAX_LENGTH_OF_FEED) {
            throw new ServiceStatusException(ServiceStatus.FD_CONTENT_TOO_LONG);
        }

        List<String> images = rewardInputVO.getImages();
        if (null == images) {
            images = Collections.EMPTY_LIST;
        }
        if (content.trim().isEmpty() && 0 == images.size()) {
            throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
        }

        List<IdVO> atUsersIdVO = rewardInputVO.getAtUsers();
        if (null == atUsersIdVO) {
            atUsersIdVO = Collections.EMPTY_LIST;
        }
        List<Long> atUsersId = new ArrayList<>();
        for (IdVO idVO : atUsersIdVO) {
            atUsersId.add(idVO.getIdValue());
        }
        List<String> atUsers = feedUtils.getAtUsers(content, atUsersId, orgId, actorUserId, adminUserId);

        //Note: only check when published to team
        FeedDTO feedDTO = new FeedDTO();
        feedDTO.setOrgId(orgId);
        feedDTO.setUserId(actorUserId);
        feedDTO.setTeamId(ALL_COMPANY);
        feedDTO.setContent(content);
        feedDTO.setAtUsers(atUsers);
        feedDTO.setImages(images);
        feedDTO.setLastModifiedUserId(actorUserId);

        LongDTO feedIdDTO = facadeFactory.getFeedFacade().createFeed(orgId, feedDTO, actorUserId, adminUserId);
        if (ServiceStatus.COMMON_OK.getCode() != feedIdDTO.getServiceStatusDTO().getCode()) {
            throw new ServiceStatusException(ServiceStatus.getEnumByCode(feedIdDTO.getServiceStatusDTO().getCode()));
        }

        return feedIdDTO.getData();
    }
*/

