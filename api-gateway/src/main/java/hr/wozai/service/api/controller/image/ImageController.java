// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.image;

import hr.wozai.service.api.controller.FacadeFactory;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.api.controller.feed.FeedUtils;
import hr.wozai.service.api.interceptor.AuthenticationInterceptor;
import hr.wozai.service.api.result.Result;
import hr.wozai.service.api.vo.ImageVO;
import hr.wozai.service.feed.client.dto.FeedDTO;

import hr.wozai.service.user.client.userorg.enums.ActionCode;
import hr.wozai.service.user.client.userorg.enums.ResourceCode;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-11
 */
@Controller("imageController")
public class ImageController {

  private static Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

  private static final long ONE_HOUR_IN_MILLIS = 1000 * 60 * 60;
  private static final long ONE_DAY_IN_MILLIS  = 1000 * 60 * 60 * 24;

  @Autowired
  private FacadeFactory facadeFactory;
  
  @Autowired
  private FeedUtils feedUtils;

  @Autowired
  private ImageUtils imageUtils;

  @Value("${oss.feedImage.dirName}")
  private  String OSS_DIR_PREFIX;

  @LogAround
  @RequestMapping(value="/feeds/images", method = RequestMethod.POST, produces = "application/json")
  @ResponseBody
  public Result<Object> putImage(
      HttpServletRequest request
  ) throws Exception {

    Result<Object> result = new Result<>();
    try {

      AuthenticationInterceptor.orgId.get();
      AuthenticationInterceptor.actorUserId.get();

      String uuid = UUID.randomUUID().toString();
      String imageKey = generateImageKey(uuid);
      String url = imageUtils.generatePutPresignedUrl(imageKey, ONE_HOUR_IN_MILLIS);

      ImageVO imageVO = new ImageVO();
      imageVO.setUuid(uuid);
      imageVO.setUrl(url);

      result.setData(imageVO);

    } catch (Exception e) {
      LOGGER.error("putImage()-fail", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }
    return result;
  }

  @LogAround

  @RequestMapping(value="/feeds/images/{uuid}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getImage(
      @PathVariable(value = "uuid") String uuid,
      HttpServletRequest request
  ) throws Exception {

    Result<Object> result = new Result<>();
    try {

      AuthenticationInterceptor.orgId.get();
      AuthenticationInterceptor.actorUserId.get();

      String imageKey = generateImageKey(uuid);
      String url = imageUtils.generateGetPresignedUrl(imageKey, ONE_DAY_IN_MILLIS);
      ImageVO imageVO = new ImageVO();
      imageVO.setUuid(uuid);
      imageVO.setUrl(url);

      result.setData(imageVO);

    } catch (Exception e) {
      LOGGER.error("getImage()-fail", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }
    return result;
  }

  @LogAround
  @RequestMapping(value="/feeds/{feedId}/images/{uuid}", method = RequestMethod.GET, produces = "application/json")
  @ResponseBody
  public Result<Object> getFeedImage(
      @PathVariable(value = "feedId") String encryptedFeedId,
      @PathVariable(value = "uuid") String uuid,
      HttpServletRequest request
  ) throws Exception {

    Result<Object> result = new Result<>();
    try {

      long orgId = AuthenticationInterceptor.orgId.get();
      long actorUserId = AuthenticationInterceptor.actorUserId.get();
      long adminUserId = AuthenticationInterceptor.actorUserId.get();

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

      String imageKey = generateImageKey(uuid);
      String url = imageUtils.generateGetPresignedUrl(imageKey, ONE_DAY_IN_MILLIS);
      ImageVO imageVO = new ImageVO();
      imageVO.setUuid(uuid);
      imageVO.setUrl(url);

      result.setData(imageVO);

    } catch (Exception e) {
      LOGGER.error("getFeedImage()-fail", e);
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }
    return result;
  }

  private String generateImageKey(String uuid) {
    String result = OSS_DIR_PREFIX + uuid;
    return result;
  }
}
