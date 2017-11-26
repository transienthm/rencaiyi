// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.api.controller.image;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.Date;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-03-17
 */
@Component
public class ImageUtils {

  private static Logger LOGGER = LoggerFactory.getLogger(ImageController.class);

 // @Value("${oss.cdnDomain}")
 // private String customDomain;

  @Value("${oss.bucketName}")
  private String bucketName;

  @Value("${oss.put.endpoint}")
  private String putEndpoint;

  @Value("${oss.get.endpoint}")
  private String getEndpoint;

  @Value("${oss.accessKeyId}")
  private String accessKeyId;

  @Value("${oss.accessKeySecret}")
  private String accessKeySecret;

  private OSSClient ossPutClient;

  private OSSClient ossGetClient;

  private static final long ONE_DAY_IN_MILLIS  = 1000 * 60 * 60 * 24;

  @PostConstruct
  public void init() throws Exception {
    LOGGER.info("init(): customDomain={},bucketName={}, putEndpoint={}, getEndpoint={}"
        , bucketName, putEndpoint, getEndpoint);
    ossPutClient = new OSSClient(putEndpoint, accessKeyId, accessKeySecret);
    ossGetClient = new OSSClient(getEndpoint, accessKeyId, accessKeySecret);
  }

  public String generatePutPresignedUrl(String documentKey, long effectiveTime) {
    Date expires = new Date(new Date().getTime() + effectiveTime);
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucketName, documentKey);
    generatePresignedUrlRequest.setExpiration(expires);
    generatePresignedUrlRequest.setMethod(HttpMethod.PUT);

    URL url = ossPutClient.generatePresignedUrl(generatePresignedUrlRequest);
    String httpUrl = url.toString();
    //String oldURL = bucketName + "." + putEndpoint;
    //String httpsUrl = httpUrl.replaceFirst(oldURL, customDomain).replace("http", "https");
    String httpsUrl = httpUrl.replaceFirst("http", "https");
    LOGGER.info("generatePutPresignedUrl(): httpsUrl={}", httpsUrl);

    return httpUrl;
  }

  public String generateGetPresignedUrl(String documentKey, long effectiveTime) {
    Date expires = new Date(new Date().getTime() + effectiveTime);
    GeneratePresignedUrlRequest generatePresignedUrlRequest =
        new GeneratePresignedUrlRequest(bucketName, documentKey);
    generatePresignedUrlRequest.setExpiration(expires);
    generatePresignedUrlRequest.setMethod(HttpMethod.GET);

    URL url = ossGetClient.generatePresignedUrl(generatePresignedUrlRequest);
    String httpUrl = url.toString();

    String oldURL = bucketName + "." + getEndpoint;
   // String httpsUrl = httpUrl.replaceFirst(oldURL, customDomain).replaceFirst("http", "https");
            //.replaceFirst("\\?","@5h_5w_1e_1c.jpg?");
//    System.out.println("oldUrl:" + oldURL + " \nhttpsUrl:" + httpsUrl + " \ncustomDomain:" + customDomain + "\nbucketName:" + bucketName);
    LOGGER.info("generateGetPresignedUrl(): httpsUrl={}", httpUrl);

    return httpUrl;
  }

}
