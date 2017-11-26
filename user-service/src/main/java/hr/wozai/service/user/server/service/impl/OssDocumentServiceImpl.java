package hr.wozai.service.user.server.service.impl;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import hr.wozai.service.user.server.service.S3DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URL;
import java.util.Date;

/**
 * Created by wangbin on 2017/10/26.
 */
@Service("ossDocumentService")
public class OssDocumentServiceImpl implements S3DocumentService {

    private static Logger LOGGER = LoggerFactory.getLogger(OssDocumentServiceImpl.class);

    @Value("${oss.cdnDomain}")
    private String customDomain;

    @Value("${oss.document.bucketName}")
    private String bucketName;

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.accessKeyId}")
    private String accessKeyId;

    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    private OSSClient ossClient;


    private static final long ONE_DAY_IN_MILLIS  = 1000 * 60 * 60 * 24;

    @PostConstruct
    public void init() throws Exception {
        LOGGER.info("init(): customDomain={},bucketName={}, endpoint={} "
                , customDomain, bucketName, endpoint);
        ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public String generatePresignedPutUrl(String documentKey, long effectiveTime) {
        Date expires = new Date(new Date().getTime() + effectiveTime);
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, documentKey);
        generatePresignedUrlRequest.setExpiration(expires);
        generatePresignedUrlRequest.setMethod(HttpMethod.PUT);

        URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
        String httpUrl = url.toString();
        //String oldURL = bucketName + "." + putEndpoint;
        //String httpsUrl = httpUrl.replaceFirst(oldURL, customDomain).replace("http", "https");
        //String httpsUrl = httpUrl.replaceFirst("http", "https");
        LOGGER.info("generatePresignedPutUrl(): httpsUrl={}", httpUrl);

        return httpUrl;
    }

    @Override
    public String generatePresignedGetUrl(String documentKey, String documentName, long effectiveTime) {
        Date expires = new Date(new Date().getTime() + effectiveTime);
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucketName, documentKey);
        generatePresignedUrlRequest.setExpiration(expires);
        generatePresignedUrlRequest.setMethod(HttpMethod.GET);

        URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
        String httpUrl = url.toString();

        //String oldURL = bucketName + "." + endpoint;
        //String httpsUrl = httpUrl.replaceFirst(oldURL, customDomain).replaceFirst("http", "https");
        //.irst("\\?","@5h_5w_1e_1c.jpg?");
//    System.out.println("oldUrl:" + oldURL + " \nhttpsUrl:" + httpsUrl + " \ncustomDomain:" + customDomain + "\nbucketName:" + bucketName);
        LOGGER.info("generateGetPresignedUrl(): httpsUrl={}", httpUrl);

        return httpUrl;
    }
}
