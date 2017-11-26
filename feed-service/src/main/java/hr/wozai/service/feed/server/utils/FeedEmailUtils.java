// Copyright (C) 2016 Wozai
// All rights reserved

package hr.wozai.service.feed.server.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.servicecommons.commons.enums.ServiceStatus;
import hr.wozai.service.servicecommons.commons.exceptions.ServiceStatusException;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.utils.codec.EncryptUtils;

import hr.wozai.service.servicecommons.utils.logging.LogAround;
import hr.wozai.service.thirdparty.client.enums.EmailTemplate;
import hr.wozai.service.thirdparty.client.utils.EmailParamCons;
import hr.wozai.service.thirdparty.client.utils.RabbitMQProducer;
import hr.wozai.service.user.client.userorg.dto.CoreUserProfileDTO;
import hr.wozai.service.user.client.userorg.dto.OrgDTO;
import hr.wozai.service.user.client.userorg.facade.OrgFacade;
import hr.wozai.service.user.client.userorg.facade.UserProfileFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-04-15
 */
@Component
public class FeedEmailUtils {

  @Value("${url}")
  private String URL;

/*  @Autowired
  private SqsProducer sqsProducer;*/

  @Autowired
  private RabbitMQProducer rabbitMQProducer;

  @Autowired
  @Qualifier("userProfileFacadeProxy")
  private ThriftClientProxy userProfileFacadeProxy;

  private UserProfileFacade userProfileFacade;

  @Autowired
  @Qualifier("orgFacadeProxy")
  private ThriftClientProxy orgFacadeProxy;

  private OrgFacade orgFacade;

  @PostConstruct
  public void init() throws Exception {
    userProfileFacade = (UserProfileFacade)userProfileFacadeProxy.getObject();
    orgFacade = (OrgFacade)orgFacadeProxy.getObject();
  }

  @LogAround
  public void sendFeedAtEmail(long orgId, long feedId,
                              long feedUserId, long atUserId,
                              long actorUserId, long adminUserId) throws Exception {

    EmailTemplate emailTemplate = EmailTemplate.FEED_AT;
    String feedIdString;
    try {
      feedIdString = EncryptUtils.symmetricEncrypt(Long.toString(feedId));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    String url = URL + "/newsfeed/" + feedIdString;
    url = getURLWithUrlCode(url, feedIdString);

    String orgShortName = getOrgShortName(orgId, actorUserId, adminUserId);

    CoreUserProfileDTO feedUserProfile = getCoreUserProfileVO(orgId, feedUserId,
        actorUserId, adminUserId);

    CoreUserProfileDTO atUserProfile = getCoreUserProfileVO(orgId, atUserId,
            actorUserId, adminUserId);


    String atUserName = atUserProfile.getFullName();
    String feedUserName = feedUserProfile.getFullName();

    String message = EmailTemplate.getFeedAtEmailContent(emailTemplate, atUserName, feedUserName, url, orgShortName, atUserProfile.getEmailAddress());

    rabbitMQProducer.sendMessage(message);
  }

  @LogAround
  public void sendFeedCommentEmail(long orgId, long feedId,
                                   long feedUserId, long commentUserId,
                                   long actorUserId, long adminUserId) throws Exception {

    EmailTemplate emailTemplate = EmailTemplate.FEED_COMMENT;
    String feedIdString;
    try {
      feedIdString = EncryptUtils.symmetricEncrypt(Long.toString(feedId));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }
    String url = URL + "/newsfeed/" + feedIdString;

    url = getURLWithUrlCode(url, feedIdString);

    String orgShortName = getOrgShortName(orgId, actorUserId, adminUserId);

    CoreUserProfileDTO feedUserProfile = getCoreUserProfileVO(orgId, feedUserId,
            actorUserId, adminUserId);

    CoreUserProfileDTO commentUserProfile = getCoreUserProfileVO(orgId, commentUserId,
            actorUserId, adminUserId);

    String feedUserName = feedUserProfile.getFullName();
    String commentUserName = commentUserProfile.getFullName();


    String message = EmailTemplate.getFeedCommentEmailContent(emailTemplate, feedUserName, commentUserName, url, orgShortName, feedUserProfile.getEmailAddress());
    rabbitMQProducer.sendMessage(message);
  }

  @LogAround
  private CoreUserProfileDTO getCoreUserProfileVO(long orgId, long userId,
                                                  long actorUserId, long adminUserId) {

    CoreUserProfileDTO coreUserProfileDTO = userProfileFacade.getCoreUserProfile(orgId, userId,
        actorUserId, adminUserId);
    if (ServiceStatus.COMMON_OK.getCode() != coreUserProfileDTO.getServiceStatusDTO().getCode()) {
      throw new ServiceStatusException(coreUserProfileDTO.getServiceStatusDTO().getCode());
    }
    /*
    CoreUserProfileDTO coreUserProfileDTO = new CoreUserProfileDTO();
    coreUserProfileDTO.setFullName("AAA");
    coreUserProfileDTO.setEmailAddress("abc@bbc");
    */
    return coreUserProfileDTO;
  }

  @LogAround
  public void sendFeedCommentAtEmail(long orgId, long feedId,
                                     long commentUserId, long atUserId,
                                     long actorUserId, long adminUserId) throws Exception {

    EmailTemplate emailTemplate = EmailTemplate.FEED_COMMENT_AT;

    String feedIdString;
    try {
      feedIdString = EncryptUtils.symmetricEncrypt(Long.toString(feedId));
    } catch (Exception e) {
      e.printStackTrace();
      return;
    }

    String url = URL + "/newsfeed/" + feedIdString;
    url = getURLWithUrlCode(url, feedIdString);

    String orgShortName = getOrgShortName(orgId, actorUserId, adminUserId);

    CoreUserProfileDTO commentUserProfile = getCoreUserProfileVO(orgId, commentUserId,
            actorUserId, adminUserId);

    CoreUserProfileDTO atUserProfile = getCoreUserProfileVO(orgId, atUserId,
            actorUserId, adminUserId);

    String atUserName =  atUserProfile.getFullName();
    String commentUserName = commentUserProfile.getFullName();

    String message = EmailTemplate.getFeedCommentAtEmailContent(emailTemplate, atUserName, commentUserName, url, orgShortName, atUserProfile.getEmailAddress());
    rabbitMQProducer.sendMessage(message);
  }

  private JSONObject addJSONArray(JSONObject obj, String key, String value) {

    JSONArray jsonArray = new JSONArray();
    jsonArray.add(value);

    obj.put(key, jsonArray);
    return obj;
  }

  @LogAround
  private String getOrgShortName(long orgId, long actorUserId, long adminUserId) {
    OrgDTO orgDTO = orgFacade.getOrg(orgId, actorUserId, adminUserId);
    if(orgDTO.getServiceStatusDTO().getCode() != ServiceStatus.COMMON_OK.getCode()) {
      throw new ServiceStatusException(ServiceStatus.COMMON_INTERNAL_SERVER_ERROR);
    }
    return orgDTO.getShortName();
  }

  private String getURLWithUrlCode(String url,String feedIdString) throws UnsupportedEncodingException{
    String suffix = "/#/newsfeed/" + feedIdString;
    String urlCode = URLEncoder.encode(suffix, "UTF-8");
    url += "?urlCode=" + urlCode;
    return url;
  }
}
