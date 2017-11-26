// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.feed;

import hr.wozai.service.feed.client.dto.FeedDTO;
import hr.wozai.service.feed.client.dto.ThumbupDTO;
import hr.wozai.service.feed.client.dto.ThumbupListDTO;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;

import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileListDTO;
import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.user.CoreUserProfileVO;
import hr.wozai.service.api.vo.feed.ThumbupVO;
import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.servicecommons.thrift.dto.BooleanDTO;
import hr.wozai.service.servicecommons.thrift.dto.VoidDTO;
import hr.wozai.service.servicecommons.utils.bean.BeanHelper;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-02-24
 */
@Controller("thumbupController")
public class ThumbupController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThumbupController.class);

  @Autowired
  private FeedUtils feedUtils;

  @Autowired
  private FacadeFactory facadeFactory;

  @LogAround

  @RequestMapping(value="/feeds/{feedId}/thumbup", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> thumbupFeed(
      @PathVariable(value = "feedId") String encryptedFeedId,
      HttpServletRequest request,
      HttpServletResponse response
  ) throws Exception {

    Result<Object> result = new Result<>();

    try {
      long orgId = AuthenticationInterceptor.orgId.get();
      long actorUserId = AuthenticationInterceptor.actorUserId.get();
      long adminUserId = AuthenticationInterceptor.adminUserId.get();

      long feedId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedFeedId));

      FeedDTO feedDTO = facadeFactory.getFeedFacade().findFeed(orgId, feedId, actorUserId, adminUserId);
      if(ServiceStatus.COMMON_OK.getCode() != feedDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.FD_FEED_NOT_FOUND);
      }

      boolean isPermitted = feedUtils.isPermitted(orgId, actorUserId, feedDTO.getFeedId(), feedDTO.getUserId(),
          ResourceCode.NEWS_FEED.getResourceCode(), ActionCode.READ.getCode());
      if(false == isPermitted) {
        throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
      }

      BooleanDTO booleanDTO = facadeFactory.getFeedFacade().isUserIdThumbupFeedId(orgId, actorUserId, feedId, actorUserId, adminUserId);
      if(ServiceStatus.COMMON_OK.getCode() != booleanDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(booleanDTO.getServiceStatusDTO().getCode()));
      }

      if(true == booleanDTO.getData()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      VoidDTO remoteResult = facadeFactory.getFeedFacade().thumbupFeed(orgId, actorUserId, feedId, actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
      }
      result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));

    } catch (Exception e) {
      LOGGER.error("thumbupFeed()-fail", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(value="/feeds/{feedId}/thumbup", method = RequestMethod.DELETE, produces = "application/json")
  @ResponseBody
  public Result<Object> unThumbupFeed(
      @PathVariable(value = "feedId") String encryptedFeedId,
      HttpServletRequest request,
      HttpServletResponse response
  ) throws Exception {

    Result<Object> result = new Result<>();

    try {

      long orgId = AuthenticationInterceptor.orgId.get();
      long actorUserId = AuthenticationInterceptor.actorUserId.get();
      long adminUserId = AuthenticationInterceptor.adminUserId.get();

      long feedId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedFeedId));

      FeedDTO feedDTO = facadeFactory.getFeedFacade().findFeed(orgId, feedId, actorUserId, adminUserId);
      if(ServiceStatus.COMMON_OK.getCode() != feedDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(feedDTO.getServiceStatusDTO().getCode()));
      }

      boolean isPermitted = feedUtils.isPermitted(orgId, actorUserId, feedDTO.getFeedId(), feedDTO.getUserId(),
          ResourceCode.NEWS_FEED.getResourceCode(), ActionCode.READ.getCode());
      if(false == isPermitted) {
        throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
      }

      BooleanDTO booleanDTO = facadeFactory.getFeedFacade().isUserIdThumbupFeedId(orgId, actorUserId, feedId, actorUserId, adminUserId);
      if(ServiceStatus.COMMON_OK.getCode() != booleanDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(booleanDTO.getServiceStatusDTO().getCode()));
      }

      if(false == booleanDTO.getData()) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }

      VoidDTO remoteResult = facadeFactory.getFeedFacade().unThumbupFeed(orgId, actorUserId, feedId, actorUserId, adminUserId);
      if (ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
      }

      result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));

    } catch (Exception e) {
      LOGGER.error("unThumbupFeed()-fail", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

  @LogAround

  @RequestMapping(value="/feeds/{feedId}/thumbupusers", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> listThumbupUserOfFeed(
      @PathVariable(value = "feedId") String encryptedFeedId,
      HttpServletRequest request,
      HttpServletResponse response
  ) throws Exception {

    Result<Object> result = new Result<>();

    try {

      long orgId = AuthenticationInterceptor.orgId.get();
      long actorUserId = AuthenticationInterceptor.actorUserId.get();
      long adminUserId = AuthenticationInterceptor.adminUserId.get();

      long feedId = Long.parseLong(EncryptUtils.symmetricDecrypt(encryptedFeedId));

      FeedDTO feedDTO = facadeFactory.getFeedFacade().findFeed(orgId, feedId, actorUserId, adminUserId);
      if(ServiceStatus.COMMON_OK.getCode() != feedDTO.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(feedDTO.getServiceStatusDTO().getCode()));
      }

      boolean isPermitted = feedUtils.isPermitted(orgId, actorUserId, feedDTO.getFeedId(), feedDTO.getUserId(),
          ResourceCode.NEWS_FEED.getResourceCode(), ActionCode.READ.getCode());
      if(false == isPermitted) {
        throw new ServiceStatusException(ServiceStatus.COMMON_PERMISSION_DENIED);
      }

      ThumbupListDTO remoteResult = facadeFactory.getFeedFacade().listThumbupUserIdsOfFeedId(orgId, feedId, actorUserId, adminUserId);
      if(ServiceStatus.COMMON_OK.getCode() != remoteResult.getServiceStatusDTO().getCode()) {
        throw new ServiceStatusException(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
      }

      // handle CUP
      Set<Long> userIdSet = new HashSet<>();
      for (ThumbupDTO thumbupDTO: remoteResult.getThumbupDTOList()) {
        userIdSet.add(thumbupDTO.getUserId());
      }
      List<Long> userIdList = new ArrayList<>(userIdSet);
      CoreUserProfileListDTO rpcUserList = facadeFactory.getUserProfileFacade()
          .listCoreUserProfile(orgId, userIdList, actorUserId, adminUserId);
      Map<Long, CoreUserProfileDTO> coreUserProfileDTOMap = new HashMap<>();
      if (!CollectionUtils.isEmpty(rpcUserList.getCoreUserProfileDTOs())) {
        for (CoreUserProfileDTO coreUserProfileDTO: rpcUserList.getCoreUserProfileDTOs()) {
          coreUserProfileDTOMap.put(coreUserProfileDTO.getUserId(), coreUserProfileDTO);
        }
      }

      List<ThumbupVO> thumbupVOs = new ArrayList<>();
      for(ThumbupDTO thumbupDTO: remoteResult.getThumbupDTOList()) {
        ThumbupVO thumbupVO = new ThumbupVO();

        BeanHelper.copyPropertiesHandlingJSON(thumbupDTO, thumbupVO);
//        Long thumbupDTOUserId = thumbupDTO.getUserId();
//        CoreUserProfileVO coreUserProfileVO = feedUtils.getCoreUserProfileVO(orgId, thumbupDTOUserId, actorUserId, adminUserId);
//        thumbupVO.setThumbupUser(coreUserProfileVO);
        if (coreUserProfileDTOMap.containsKey(thumbupDTO.getUserId())) {
          CoreUserProfileVO coreUserProfileVO = new CoreUserProfileVO();
          BeanUtils.copyProperties(coreUserProfileDTOMap.get(thumbupDTO.getUserId()), coreUserProfileVO);
          thumbupVO.setThumbupUser(coreUserProfileVO);
        }
        thumbupVOs.add(thumbupVO);
      }

      result.setData(thumbupVOs);
      result.setCodeAndMsg(ServiceStatus.getEnumByCode(remoteResult.getServiceStatusDTO().getCode()));
    } catch (Exception e) {
      LOGGER.error("listThumbupUserOfFeed()-fail", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }

    return result;
  }

}
