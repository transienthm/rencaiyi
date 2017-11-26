package hr.wozai.service.feed.server.utils;

import com.alibaba.fastjson.JSONObject;
import hr.wozai.service.feed.server.model.Feed;
import hr.wozai.service.feed.server.service.IFeedService;
import hr.wozai.service.thirdparty.client.dto.MessageDTO;
import hr.wozai.service.thirdparty.client.enums.MessageTemplate;
import hr.wozai.service.thirdparty.client.facade.MessageCenterFacade;
import hr.wozai.service.servicecommons.thrift.client.ThriftClientProxy;
import hr.wozai.service.servicecommons.utils.logging.LogAround;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

/**
 * @Author: Xuehui Wang
 * @Version: 1.0
 * @Created: 2016-05-19
 */
@Component
public class FeedMessageUtils {

  @Autowired
  private IFeedService feedService;

  @Autowired
  @Qualifier("messageCenterFacadeProxy")
  private ThriftClientProxy messageCenterFacadeProxy;

  private MessageCenterFacade messageCenterFacade;

  @PostConstruct
  public void init() throws Exception {
    messageCenterFacade = (MessageCenterFacade)messageCenterFacadeProxy.getObject();
  }

  @LogAround
  public void sendFeedAtMessage(long orgId, long feedId, long atUserId,
                                long actorUserId, long adminUserId) throws Exception {


    Feed feed = feedService.findFeed(orgId, feedId);
    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(Arrays.asList(feed.getUserId()));
    messageDTO.setTemplateId(MessageTemplate.NEWS_FEED_AT.getCode());
    messageDTO.setObjectId(feedId);
    messageDTO.setObjectContent(JSONObject.toJSONString(feed));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(atUserId));

  }

  @LogAround
  public void sendFeedThumbupMessage(long orgId, long userId, long feedId,
                                     long actorUserId, long adminUserId) throws Exception {


    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(Arrays.asList(userId));
    messageDTO.setTemplateId(MessageTemplate.NEWS_FEED_DIANZAN.getCode());
    messageDTO.setObjectId(feedId);
    Feed feed = feedService.findFeed(orgId, feedId);
    messageDTO.setObjectContent(JSONObject.toJSONString(feed));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(feed.getUserId()));
  }

  @LogAround
  public void sendFeedCommentMessage(long orgId, long feedId, long commentUserId,
                                     long actorUserId, long adminUserId) throws Exception {

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(Arrays.asList(commentUserId));
    messageDTO.setTemplateId(MessageTemplate.NEWS_FEED_COMMENT.getCode());
    messageDTO.setObjectId(feedId);

    Feed feed = feedService.findFeed(orgId, feedId);
    messageDTO.setObjectContent(JSONObject.toJSONString(feed));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(feed.getUserId()));
  }

  @LogAround
  public void sendFeedCommentAtMessage(long orgId, long feedId, long commentUserId, long atUserId,
                                       long actorUserId, long adminUserId) throws Exception {

    MessageDTO messageDTO = new MessageDTO();
    messageDTO.setOrgId(orgId);
    messageDTO.setSenders(Arrays.asList(commentUserId));
    messageDTO.setTemplateId(MessageTemplate.NEWS_FEED_COMMENT_AT.getCode());
    messageDTO.setObjectId(feedId);

    Feed feed = feedService.findFeed(orgId, feedId);
    messageDTO.setObjectContent(JSONObject.toJSONString(feed));

    messageCenterFacade.addPersonalMessage(messageDTO, Arrays.asList(atUserId));
  }

}
