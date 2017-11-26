// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.thrift.facade;

import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.client.document.dto.OssAvatarPutRequestDTO;
import hr.wozai.service.user.client.document.facade.AvatarFacade;
import hr.wozai.service.user.server.helper.FacadeExceptionHelper;
import hr.wozai.service.user.server.service.OssAvatarService;
import hr.wozai.service.servicecommons.thrift.dto.ServiceStatusDTO;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.servicecommons.utils.uuid.UUIDGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**`
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
@Service("avatarFacade")
public class AvatarFacadeImpl implements AvatarFacade {

  private static final Logger LOGGER = LoggerFactory.getLogger(AvatarFacadeImpl.class);

  // TODO: move to config file
  @Value("${oss.avatarImage.dirName}")
  private String OSS_DIR_PREFIX;

  private static final long ONE_HOUR_IN_MILLIS = 1000 * 60 * 60;
  private static final long ONE_DAY_IN_MILLIS  = 1000 * 60 * 60 * 24;

  @Autowired
  OssAvatarService ossAvatarService;

  @Override
  @LogAround
  public OssAvatarPutRequestDTO addAvatar(
      long orgId, String x, String y, String r, long actorUserId, long adminUserId) {

    OssAvatarPutRequestDTO result = new OssAvatarPutRequestDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      String uuid = UUIDGenerator.generateRandomKey();
      String avatarKey = appendXYR(uuid, x, y, r);
      long effectiveTime = ONE_HOUR_IN_MILLIS;
      String putPresigneUrl = ossAvatarService.generatePresignedPutUrlFromAvatarKey(avatarKey, effectiveTime);
      String getPublicUrl = ossAvatarService.generatePublicGetUrlFromAvatarKey(avatarKey);
      result.setPresignedPutUrl(putPresigneUrl);
      result.setPutEffectiveTime(effectiveTime);
      result.setPublicGetUrl(getPublicUrl);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("addAvatar()-error", e);
    }

    return result;
  }

  @Override
  @LogAround
  public OssAvatarPutRequestDTO updateAvatar(
      long orgId, String x, String y, String r, String publicGetUrl, long actorUserId, long adminUserId) {

    OssAvatarPutRequestDTO result = new OssAvatarPutRequestDTO();
    ServiceStatusDTO serviceStatusDTO =
        new ServiceStatusDTO(ServiceStatus.COMMON_CREATED.getCode(), ServiceStatus.COMMON_CREATED.getMsg());
    result.setServiceStatusDTO(serviceStatusDTO);

    try {
      if (StringUtils.isNullOrEmpty(publicGetUrl)) {
        throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
      }
      String uuid = UUIDGenerator.generateRandomKey();
      String avatarKey = appendXYR(uuid, x, y, r);
      long effectiveTime = ONE_HOUR_IN_MILLIS;
      String putPresigneUrl = ossAvatarService.generatePresignedPutUrlFromAvatarKey(avatarKey, effectiveTime);
      String getPublicUrl = ossAvatarService.generatePublicGetUrlFromAvatarKey(avatarKey);
      result.setPresignedPutUrl(putPresigneUrl);
      result.setPutEffectiveTime(effectiveTime);
      result.setPublicGetUrl(getPublicUrl);
    } catch (Exception e) {
      FacadeExceptionHelper.setServiceStatusForFacadeResult(serviceStatusDTO, e);
      LOGGER.error("updateAvatar()-error", e);
    }

    return result;

  }

  private String appendXYR(String uuid, String x, String y, String r) {
    String result = OSS_DIR_PREFIX + uuid + "|" + x + "," + y + "," + r;
    return result;
  }
}
