// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.commons.utils.StringUtils;
import hr.wozai.service.user.server.service.OssAvatarService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Date;

import javax.annotation.PostConstruct;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
@Service("ossAvatarService")
public class OssAvatarServiceImpl implements OssAvatarService {

  private static Logger LOGGER = LoggerFactory.getLogger(OssAvatarServiceImpl.class);

  private static String HTTP_PREFIX = "http";

  private OSSClient ossClient;

  @Value("${oss.avatar.bucketName}")
  private String bucketName;

  @Value("${oss.endpoint}")
  private String endpoint;

  @Value("${oss.accessKeyId}")
  private String accessKeyId;

  @Value("${oss.accessKeySecret}")
  private String accessKeySecret;

 // @Value("${oss.cdnDomain}")
  private String cdnDomain = "prod-image2.oss-cn-beijing.aliyuncs.com";

  private static final long ONE_DAY_IN_MILLIS  = 1000 * 60 * 60 * 24;

  @PostConstruct
  public void init() {
    LOGGER.info("init(): bucketName={}, endpoint={}", bucketName, endpoint);
    ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
  }

  @Override
  @LogAround
  public String generatePresignedPutUrlFromAvatarKey(String avatarKey, long effectiveTime) {

    if (StringUtils.isNullOrEmpty(avatarKey)
        || effectiveTime <= 0) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    Date expires = new Date(new Date().getTime() + effectiveTime);
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucketName, avatarKey);
    generatePresignedUrlRequest.setExpiration(expires);
    generatePresignedUrlRequest.setMethod(HttpMethod.PUT);

    URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
    String httpUrl = url.toString();
    String httpsUrl = httpUrl.replaceFirst("http", "https");

    return httpUrl;

  }

  @Override
  @LogAround
  public String generatePresignedPutUrlFromPublicGetUrl(String publicGetUrl, long effectiveTime) {

    String avatarKey = null;
    if (!StringUtils.isNullOrEmpty(publicGetUrl)) {
      avatarKey = publicGetUrl.replace(getOssGetPublicUrlPrefixBeforeAvatarKey(), "");
    }
    if (StringUtils.isNullOrEmpty(avatarKey)
        || effectiveTime <= 0) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }

    Date expires = new Date(new Date().getTime() + effectiveTime);
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucketName, avatarKey);
    generatePresignedUrlRequest.setExpiration(expires);
    generatePresignedUrlRequest.setMethod(HttpMethod.PUT);

    URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
    String httpUrl = url.toString();
    String httpsUrl = httpUrl.replaceFirst("http", "https");

    return httpUrl;
  }

  @Override
  @LogAround
  public String generatePublicGetUrlFromAvatarKey(String avatarKey) {
    if (StringUtils.isNullOrEmpty(avatarKey)) {
      throw new ServiceStatusException(ServiceStatus.COMMON_BAD_REQUEST);
    }
    return getOssGetPublicUrlPrefixBeforeAvatarKey() + avatarKey;
  }

  private String getOssGetPublicUrlPrefixBeforeAvatarKey() {
    return HTTP_PREFIX + "://" + cdnDomain + "/";
  }
}
