// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.user.server.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.user.server.service.S3DocumentService;
import hr.wozai.service.servicecommons.utils.logging.LogAround;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

/**
 * @Author: Zhe Chen
 * @Version: 1.0
 * @Created: 2016-03-08
 */
@Service("s3DocumentService")
public class S3DocumentServiceImpl implements S3DocumentService {

  private static Logger LOGGER = LoggerFactory.getLogger(S3DocumentServiceImpl.class);

  private static final String S3_DOWNLOAD_CONTENT_TYPE = "application/octet-stream";
  private static final String S3_DOWNLOAD_CONTENT_DISPOSITION = "attachment";

  AmazonS3Client amazonS3Client;

  @Value("${s3.document.bucketName}")
  private String bucketName;

  @Value("${s3.accessKeyId}")
  private String accessKeyId;

  @Value("${s3.accessKeySecret}")
  private String accessKeySecret;

  @PostConstruct
  public void init() {
    LOGGER.info("init(): bucketName={}", bucketName);
    // init s3
    amazonS3Client = new AmazonS3Client(new BasicAWSCredentials(accessKeyId, accessKeySecret));
    amazonS3Client.setRegion(Region.getRegion(Regions.CN_NORTH_1));
  }

  @Override
  @LogAround
  public String generatePresignedPutUrl(String documentKey, long effectiveTime) {

    Date expires = new Date(new Date().getTime() + effectiveTime);
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucketName, documentKey);
    generatePresignedUrlRequest.setExpiration(expires);
    generatePresignedUrlRequest.setMethod(HttpMethod.PUT);

    URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
    String httpsUrl = url.toString();

    return httpsUrl;
  }

  @Override
  @LogAround
  public String generatePresignedGetUrl(String documentKey, String documentName, long effectiveTime) {
    Date expires = new Date(new Date().getTime() + effectiveTime);
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucketName, documentKey);
    generatePresignedUrlRequest.setExpiration(expires);
    generatePresignedUrlRequest.setMethod(HttpMethod.GET);

    ResponseHeaderOverrides responseHeaderOverrides = new ResponseHeaderOverrides();
    responseHeaderOverrides.setContentType(S3_DOWNLOAD_CONTENT_TYPE);
    try {
      responseHeaderOverrides.setContentDisposition(
          S3_DOWNLOAD_CONTENT_DISPOSITION + "; filename=" + new String(documentName.getBytes("utf-8"), "ISO8859-1"));
    } catch (Exception e) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }
    generatePresignedUrlRequest.setResponseHeaders(responseHeaderOverrides);

    URL url = amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest);
    String httpsUrl = url.toString();

    return httpsUrl;
  }
}
