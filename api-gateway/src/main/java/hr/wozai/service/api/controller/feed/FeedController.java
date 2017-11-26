// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.feed;

import hr.wozai.service.api.vo.feed.*;
import hr.wozai.service.api.vo.orgteam.TeamVO;
import hr.wozai.service.feed.client.consts.FeedAndCommentConsts;
import hr.wozai.service.feed.client.dto.*;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.controller.image.ImageUtils;
import hr.wozai.service.api.helper.ControllerExceptionHelper;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.util.PageUtils;
import hr.wozai.service.api.vo.IdVO;
import hr.wozai.service.api.vo.ImageVO;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.servicecommons.thrift.dto.LongDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;


import hr.wozai.service.servicecommons.utils.validator.BindingResultMonitor;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.user.client.userorg.dto.TeamDTO;
import hr.wozai.service.user.client.userorg.dto.TeamListDTO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.user.client.userorg.enums.ResourceType;
import hr.wozai.service.user.client.userorg.util.PermissionObj;
import hr.wozai.service.user.client.userorg.util.PermissionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-20
 */
@Controller("feedController")
public class FeedController {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeedController.class);

  //Note: 1.0 feed published to ALL_COMPANY no specific teamId
  private static final long ALL_COMPANY = 0;

  private static final int DELETABLE = 1;
  private static final int UNDELETABLE = 0;

  private static final int LIKABLE = 1;
  private static final int UNLIKABLE = 0;

  private static final long ONE_HOUR_IN_MILLIS = 1000 * 60 * 60;
  private static final long ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

//  private static final String IMAGE_STYLE = "@!small";
  private static final String IMAGE_STYLE = "";

  @Value("${oss.feedImage.dirName}")
  private  String OSS_DIR_PREFIX;

  @Autowired
  private PermissionUtil permissionUtil;

  @Autowired
  private FeedUtils feedUtils;

  @Autowired
  private ImageUtils imageUtils;

  @Autowired
  private RewardUtils rewardUtils;

  @Autowired
  private FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(value = "/feeds", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  @BindingResultMonitor
  public Result<Object> createFeed(
          @RequestBody @Valid FeedInputVO feedInputVO,
          BindingResult bindingResult,
          HttpServletRequest request,
          HttpServletResponse response
  ) throws Exception {

    Result<Object> result = new Result<>();

    try {
      long orgId = AuthenticationInterceptor.orgId.get();
      long actorUserId = AuthenticationInterceptor.actorUserId.get();
      long adminUserId = AuthenticationInterceptor.adminUserId.get();

      String content = feedInputVO.getContent();

      if (StringUtils.isNullOrEmpty(content)) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      } else if (content.length() > FeedAndCommentConsts.MAX_LENGTH_OF_FEED) {
        throw new ServiceStatusException(ServiceStatus.FD_CONTENT_TOO_LONG);
      }

      List<String> images = feedInputVO.getImages();
      if (null == images) {
        images = Collections.EMPTY_LIST;
      }
      if (content.trim().isEmpty() && 0 == images.size()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
      }

      List<IdVO> atUsersIdVO = feedInputVO.getAtUsers();
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
      RewardDTO rewardDTO;
      if (feedUtils.isReward(feedInputVO)) {
        rewardDTO = feedUtils.getRewardDTOFromFeedInputVO(orgId, feedInputVO, actorUserId);
        feedDTO.setRewardDTO(rewardDTO);
      }
      LongDTO feedIdDTO = facadeFactory.getFeedFacade().createFeed(orgId, feedDTO, actorUserId, adminUserId);

      if (ServiceStatus.COMMON_OK.getCode() != feedIdDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(feedIdDTO.getServiceStatusDTO().getCode()));
      }

/*      //如果是赞赏
      if (feedInputVO.getRewardeeIds() != null || feedInputVO.getRewardType() != null
              || feedInputVO.getRewardMedalId() != null) {



        VoidDTO rewardIdDTO = facadeFactory.getRewardFacade().createReward(orgId, rewardDTO, actorUserId, adminUserId);
        if (ServiceStatus.COMMON_OK.getCode() != rewardIdDTO.getServiceStatusDTO().getCode()) {
          throw new ServiceStatusException(ServiceStatus.getEnumByCode(rewardIdDTO.getServiceStatusDTO().getCode()));
        }
      }*/

      IdVO idVO = new IdVO();
      idVO.setIdValue(feedIdDTO.getData());

      result.setCodeAndMsg(ServiceStatus.getEnumByCode(feedIdDTO.getServiceStatusDTO().getCode()));
      result.setData(idVO);

    } catch (Exception e) {
      ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
      LOGGER.error("createFeed()-fail", e);
    }

    return result;
  }

  @LogAround

  @RequestMapping(value = "/feeds/{feedId}", method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> deleteFeed(
          @PathVariable(value = "feedId") String encryptedFeedId,
          HttpServletRequest request,
          HttpServletResponse response
  ) throws Exception {

    Result<Object> result = new Result<>();

    try {

      long orgId = AuthenticationInterceptor.orgId.get();
      long actorUserId = AuthenticationInterceptor.actorUserId.get();
      long adminUserId = AuthenticationInterceptor.adminUserId.get();

      Long feedId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedFeedId));

      FeedDTO feedDTO = facadeFactory.getFeedFacade().findFeed(orgId, feedId, actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != feedDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(feedDTO.getServiceStatusDTO().getCode()));
      }
      boolean isPermitted = feedUtils.isPermitted(orgId, actorUserId, feedDTO.getFeedId(), feedDTO.getUserId(),
              ResourceCode.NEWS_FEED.getResourceCode(), ActionCode.DELETE.getCode());
      if (false == isPermitted) {
        throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
      }

      VoidDTO remoteResult = facadeFactory.getFeedFacade().deleteFeed(orgId, feedId, actorUserId, actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
      }
      result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));

    } catch (Exception e) {
      LOGGER.error("deleteFeed()-fail", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(value = "/feeds", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listPageFeed(
          @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
          @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize,
          HttpServletRequest request,
          HttpServletResponse response
  ) throws Exception {

    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    boolean isValid = PageUtils.isPageParamValid(pageNumber, pageSize);
    if (!isValid) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INVALID_PARAM);
    }

    try {
      FeedListDTO remoteResult = facadeFactory.getFeedFacade().listPageFeedOfOrgAndTeam(orgId, ALL_COMPANY, pageNumber, pageSize,
              actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
      }

      List<FeedVO> feedVOs = fillFeedStatus(orgId, remoteResult.getFeedDTOList(), actorUserId, adminUserId);

      LongDTO amount = facadeFactory.getFeedFacade().countFeedOfOrgAndTeam(orgId, ALL_COMPANY, actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != amount.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(amount.getServiceStatusDTO().getCode()));
      }

      FeedListVO feedListVO = new FeedListVO();
      feedListVO.setAmount(amount.getData());
//      feedVOs = handleRewardVO(orgId, feedVOs, actorUserId, adminUserId);

      handleReward(orgId, feedVOs, remoteResult, actorUserId, adminUserId);
      feedListVO.setFeeds(feedVOs);

      result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
      result.setData(feedListVO);

    } catch (Exception e) {
      LOGGER.error("listPageFeed()-fail", e);
      //throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
      ControllerExceptionHelper.setServiceStatusForControllerResult(result, e);
    }

    return result;
  }

  @LogAround

  @RequestMapping(value = "/feeds/{feedId}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getFeed(
          @PathVariable(value = "feedId") String encryptedFeedId,
          HttpServletRequest request,
          HttpServletResponse response
  ) throws Exception {

    Result<Object> result = new Result<>();

    long orgId = AuthenticationInterceptor.orgId.get();
    long actorUserId = AuthenticationInterceptor.actorUserId.get();
    long adminUserId = AuthenticationInterceptor.adminUserId.get();

    Long feedId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedFeedId));

    FeedDTO remoteResult = facadeFactory.getFeedFacade().findFeed(orgId, feedId, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    }

    List<FeedVO> feedVOs = fillFeedStatus(orgId, Arrays.asList(remoteResult), actorUserId, adminUserId);


    FeedListDTO feedListDTO = new FeedListDTO();
    feedListDTO.setFeedDTOList(Arrays.asList(remoteResult));
    handleReward(orgId, feedVOs, feedListDTO, actorUserId, adminUserId);
    FeedVO feedVO = feedVOs.get(0);
    result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    result.setData(feedVO);

    return result;
  }

  private void handleReward(long orgId, List<FeedVO> feedVOs, FeedListDTO feedListDTO, long actorUserId, long adminUserId) throws Exception {
    List<FeedDTO> feedDTOs = feedListDTO.getFeedDTOList();
    List<Long> userIds = new ArrayList<>();
    List<Long> teamIds = new ArrayList<>();
    List<Long> rewardMedalIds = new ArrayList<>();
    Map<Long, RewardDTO> rewardDTOMap = new HashMap<>();

    for (FeedDTO feedDTO : feedDTOs) {
      RewardDTO rewardDTO = feedDTO.getRewardDTO();
      if (null != rewardDTO) {
        //赞赏个人
        if (rewardDTO.getRewardType() == 0) {
          if (!CollectionUtils.isEmpty(rewardDTO.getRewardeeIds())) {
            userIds.addAll(rewardDTO.getRewardeeIds());
          }

          //赞赏团队
        } else if (rewardDTO.getRewardType() == 1) {
          if (!CollectionUtils.isEmpty(rewardDTO.getRewardeeIds())) {
            teamIds.addAll(rewardDTO.getRewardeeIds());
          }
        }
        rewardMedalIds.add(rewardDTO.getRewardMedalId());
        rewardDTOMap.put(feedDTO.getFeedId(), rewardDTO);
      }
    }
    Map<Long,CoreUserProfileVO> coreUserProfileVOMap = rewardUtils.getCoreUserProfileMapFromUserIds(userIds, orgId, actorUserId, adminUserId);
    Map<Long,TeamVO> teamVOMap = rewardUtils.getTeamMapFromTeamIds(teamIds, orgId, actorUserId, adminUserId);
    RewardMedalListDTO rewardMedalListDTO = facadeFactory.getRewardFacade()
            .listRewardMedalSettingByRewardMedalIds(orgId, rewardMedalIds, actorUserId, adminUserId);
    Map<Long, RewardMedalListVO> rewardMedalSettingListVOMap = rewardUtils.getRewardMedalSettingListVOMapFromDTO(rewardMedalListDTO);

    for (FeedVO feedVO : feedVOs) {
      RewardDTO rewardDTO = rewardDTOMap.get(feedVO.getFeedId());
      RewardVO rewardVO = new RewardVO();
      if (null != rewardDTO) {
        //个人赞赏，取出List<CoreUserProfiles>
        if (rewardDTO.getRewardType() == 0) {
          List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
          for (Long l : rewardDTO.getRewardeeIds()) {
            CoreUserProfileVO coreUserProfileVO = coreUserProfileVOMap.get(l);
            if (null == coreUserProfileVO) {
              coreUserProfileVO = new CoreUserProfileVO();
              coreUserProfileVO.setFullName("已删除");
            }
            coreUserProfileVOs.add(coreUserProfileVO);
          }
          rewardVO.setRewardedUsers(coreUserProfileVOs);
          RewardMedalListVO rewardMedalListVO = rewardMedalSettingListVOMap.get(rewardDTO.getRewardMedalId());
          rewardVO.setRewardMedalListVO(rewardMedalListVO);
          rewardVO.setRewardType(0);
        } else if (rewardDTO.getRewardType() == 1) {
          List<TeamVO> teamVOs = new ArrayList<>();
          for (Long l : rewardDTO.getRewardeeIds()) {
            TeamVO teamVO = teamVOMap.get(l);
            if (null == teamVO) {
              teamVO = new TeamVO();
              teamVO.setTeamName("已删除");
            }
            teamVOs.add(teamVO);
          }
          rewardVO.setRewardedTeams(teamVOs);
          RewardMedalListVO rewardMedalListVO = rewardMedalSettingListVOMap.get(rewardDTO.getRewardMedalId());
          rewardVO.setRewardMedalListVO(rewardMedalListVO);
          rewardVO.setRewardType(1);
        }
        rewardVO.setFeedId(feedVO.getFeedId());
        feedVO.setIsDeletable(0);
      } else {
        continue;
      }
      feedVO.setRewardVO(rewardVO);
    }

  }


 /* //处理赞赏，将rewardVO 放入feedVO中 list接口使用
  private List<FeedVO> handleRewardVO(long orgId, List<FeedVO> feedVOs, long actorUserId, long adminUserId) throws Exception {

    //处理赞赏
    List<Long> feedIds = new ArrayList<>();
    for (FeedVO feedVO:feedVOs){
      feedIds.add(feedVO.getFeedId());
    }

    Map<Long, RewardVO> rewardDTOMap = new HashMap<>();
    RewardListDTO rewardListDTO = facadeFactory.getRewardFacade().listRewardsByFeedIds(orgId, feedIds, actorUserId, adminUserId);
    List<RewardDTO> rewardDTOs = rewardListDTO.getRewardDTOList();
    for (RewardDTO rewardDTO : rewardDTOs) {
      RewardVO rewardVO = rewardDTOMap.get(rewardDTO.getFeedId()) == null ?
              new RewardVO() : rewardDTOMap.get(rewardDTO.getFeedId());
      rewardVO.setFeedId(rewardDTO.getFeedId());
      rewardVO.setRewardType(rewardDTO.getRewardType());

      List<CoreUserProfileVO> coreUserProfileVOs = new ArrayList<>();
      List<TeamVO> teamVOs = new ArrayList<>();
      if (rewardDTO.getRewardType() == 0) {
        coreUserProfileVOs = rewardUtils.getCoreUserProfileVOsFromRewardDTO(orgId, rewardDTO, actorUserId, adminUserId);
        teamVOs = new ArrayList<>();
      } else if (rewardDTO.getRewardType() == 1) {
        teamVOs = rewardUtils.getTeamVOsFromRewardDTO(orgId, rewardDTO, actorUserId, adminUserId);
        coreUserProfileVOs = new ArrayList<>();
      }
      if (CollectionUtils.isEmpty(rewardVO.getRewardedUsers())) {
        rewardVO.setRewardedUsers(coreUserProfileVOs);
      } else {
        List<CoreUserProfileVO> total = rewardVO.getRewardedUsers();
        total.addAll(coreUserProfileVOs);
        rewardVO.setRewardedUsers(total);
      }
      if (CollectionUtils.isEmpty(rewardVO.getRewardedTeams())) {
        rewardVO.setRewardedTeams(teamVOs);
      } else {
        List<TeamVO> total = rewardVO.getRewardedTeams();
        total.addAll(teamVOs);
        rewardVO.setRewardedTeams(total);
      }
      RewardMedalSettingListVO rewardMedalSettingListVO = new RewardMedalSettingListVO();
      RewardMedalListDTO rewardMedalSettingListDTO = facadeFactory.getRewardFacade()
              .listRewardMedalSettingByRewardMedalIds(orgId, Arrays.asList(rewardDTO.getRewardMedalId()), actorUserId, adminUserId);

      List<RewardMedalVO> teamRewardMedalVOs = new ArrayList<>();
      List<RewardMedalVO> teamRewardMedalSettingVOs = new ArrayList<>();

      List<RewardMedalDTO> rewardMedalSettingDTOs = rewardMedalSettingListDTO.getRewardMedalDTOList();
      for (RewardMedalDTO rewardMedalSettingDTO : rewardMedalSettingDTOs) {
        RewardMedalVO rewardMedalSettingVO = new RewardMedalVO();
        BeanUtils.copyProperties(rewardMedalSettingDTO, rewardMedalSettingVO);
        if (rewardMedalSettingVO.getMedalType() == 0) {
          teamRewardMedalVOs.add(rewardMedalSettingVO);
        } else if (rewardMedalSettingVO.getMedalType() == 1) {
          teamRewardMedalSettingVOs.add(rewardMedalSettingVO);
        }
      }

      rewardMedalSettingListVO.setPersonalRewardMedalVOs(teamRewardMedalVOs);
      rewardMedalSettingListVO.setTeamRewardMedalVOs(teamRewardMedalSettingVOs);

      rewardVO.setRewardMedalSettingListVO(rewardMedalSettingListVO);
      rewardDTOMap.put(rewardDTO.getFeedId(), rewardVO);
    }

    for (FeedVO feedVO : feedVOs) {
      RewardVO rewardVO = rewardDTOMap.get(feedVO.getFeedId());
      if (null != rewardVO) {
        feedVO.setRewardVO(rewardVO);
        feedVO.setIsDeletable(0);
      }
    }
    return feedVOs;
  }*/

  /**
   * Fill comment with deletable atList
   *
   * @param orgId
   * @param feedDTOs
   * @param actorUserId
   * @param adminUserId
   */
  @LogAround
  private List<FeedVO> fillFeedStatus(
          long orgId, List<FeedDTO> feedDTOs, long actorUserId, long adminUserId) throws Exception {

    List<FeedVO> feedVOs = new ArrayList<>();

    // 1. deletable
    List<PermissionObj> permissionObjs = new ArrayList<>();
    for (int i = 0; i < feedDTOs.size(); i++) {
      FeedDTO feedDTO = feedDTOs.get(i);
      PermissionObj obj = new PermissionObj();
      obj.setId(feedDTO.getFeedId());
      obj.setOwnerId(feedDTO.getUserId());
      obj.setResourceType(ResourceType.PERSON.getCode());
      permissionObjs.add(obj);
    }
    permissionUtil.assignPermissionToObjList(orgId, actorUserId, permissionObjs,
            ResourceCode.NEWS_FEED.getResourceCode(), ActionCode.DELETE.getCode());

    // 2. likable
    List<Long> feedIds = new ArrayList<>();
    for (FeedDTO feedDTO : feedDTOs) {
      feedIds.add(feedDTO.getFeedId());
    }
    LongListDTO remoteResult = facadeFactory.getFeedFacade().filterUserLikedFeedIds(orgId,
            actorUserId, feedIds, actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    }
    List<Long> userLikedFeedIds = remoteResult.getData();

    Set<Long> userIdSet = new HashSet<>();
    for (FeedDTO feedDTO : feedDTOs) {
      userIdSet.add(feedDTO.getUserId());
    }
    List<Long> userIdList = new ArrayList<>(userIdSet);
    CoreUserProfileListDTO rpcUserList = facadeFactory.getUserProfileFacade()
            .listCoreUserProfile(orgId, userIdList, actorUserId, adminUserId);
    Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap = new HashMap<>();
    if (!CollectionUtils.isEmpty(rpcUserList.getCoreUserProfileDTOs())) {
      for (CoreUserProfileDTO coreUserProfileDTO : rpcUserList.getCoreUserProfileDTOs()) {
        coreUserProfileDTOMap.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
      }
    }

    for (int i = 0; i < feedDTOs.size(); i++) {

      FeedDTO feedDTO = feedDTOs.get(i);
      FeedVO feedVO = new FeedVO();

      BeanHelper.copyPropertiesHandlingJSON(feedDTO, feedVO);

      Long feedDTOUserId = feedDTO.getUserId();
      if (coreUserProfileDTOMap.containsKey(feedDTOUserId)) {
        CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
        BeanUtils.copyProperties(coreUserProfileDTOMap.get(feedDTOUserId), coreUserProfileVO);
        feedVO.setFeedUser(coreUserProfileVO);
      }

      List<String> images = feedDTO.getImages();
      if (null == images) {
        images = Collections.EMPTY_LIST;
      }
      List<ImageVO> presignedImages = new ArrayList<>();
      for (String uuid : images) {
        String imageKey = OSS_DIR_PREFIX + uuid;
        String url = imageUtils.generateGetPresignedUrl(imageKey + IMAGE_STYLE, ONE_DAY_IN_MILLIS);
        ImageVO imageVO = new ImageVO();
        imageVO.setUuid(uuid);
        imageVO.setUrl(url);
        presignedImages.add(imageVO);
      }
      feedVO.setImages(presignedImages);

      String content = feedDTO.getContent();
      List<String> atUsers = feedDTO.getAtUsers();
      if (null != atUsers) {
        List<CoreUserProfileVO> userProfileVOs = feedUtils.fillAtUsers(content, atUsers, orgId, actorUserId, adminUserId);
        feedVO.setAtUsers(userProfileVOs);
      }

      PermissionObj obj = permissionObjs.get(i);
      int deletable = obj.isHasPermission() ? DELETABLE : UNDELETABLE;
      feedVO.setIsDeletable(deletable);

      Integer isLikable = userLikedFeedIds.contains(feedDTO.getFeedId()) ? UNLIKABLE : LIKABLE;
      feedVO.setIsLikable(isLikable);

      feedVOs.add(feedVO);
    }

    return feedVOs;
  }

}
